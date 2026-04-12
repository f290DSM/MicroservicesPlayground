# Tutorial: Usando OpenFeign para Comunicação entre Microserviços

## Introdução

OpenFeign é uma biblioteca declarativa para criação de clientes HTTP em aplicações Java. No contexto de microserviços, o OpenFeign simplifica a comunicação entre serviços, permitindo que você chame APIs REST de outros serviços como se fossem métodos locais.

Neste projeto, utilizamos o OpenFeign integrado com Spring Cloud para comunicação entre o `product-composite-service` e o `product-service`.

## Pré-requisitos

- Projeto Spring Boot com Spring Cloud
- Conhecimento básico de REST APIs
- Eureka Server para service discovery (opcional, mas recomendado)

## Passo 1: Adicionar Dependências

Adicione a dependência do Spring Cloud OpenFeign no `build.gradle` do seu microserviço:

```groovy
dependencies {
    // ... outras dependências
    
    implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
    
    // ... mais dependências
}
```

**Nota:** Esta dependência já está incluída no `spring-cloud-dependencies` BOM, então certifique-se de ter a versão correta do Spring Cloud configurada.

## Passo 2: Habilitar OpenFeign na Aplicação

Adicione a anotação `@EnableFeignClients` na classe principal da aplicação:

```java
package dev.sdras.microservices.composite.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients  // Habilita o uso de clientes Feign
@EnableDiscoveryClient  // Para service discovery com Eureka
@SpringBootApplication
public class ProductCompositeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductCompositeServiceApplication.class, args);
    }
}
```

## Passo 3: Criar um Cliente Feign

Crie uma interface anotada com `@FeignClient` para cada serviço que você deseja consumir:

```java
package dev.sdras.microservices.composite.product.services.feign;

import dev.sdras.api.core.product.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("product-service")  // Nome do serviço registrado no Eureka
public interface ProductFeignClient {
    
    @GetMapping(
        value = "/product/{productId}",
        produces = "application/json"
    )
    Product getProduct(@PathVariable Integer productId);
}
```

**Explicação:**
- `@FeignClient("product-service")`: Especifica o nome do serviço no registro (Eureka)
- Os métodos na interface devem corresponder exatamente aos endpoints do serviço remoto
- Use as mesmas anotações Spring MVC (`@GetMapping`, `@PostMapping`, etc.)

## Passo 4: Injetar e Usar o Cliente Feign

Injete o cliente Feign em seus serviços e use-o como qualquer outro bean Spring:

```java
package dev.sdras.microservices.composite.product.services;

import dev.sdras.api.core.product.Product;
import dev.sdras.microservices.composite.product.services.feign.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductCompositeIntegration {

    private final ProductFeignClient productFeignClient;

    @Autowired
    public ProductCompositeIntegration(ProductFeignClient productFeignClient) {
        this.productFeignClient = productFeignClient;
    }

    public Product getProduct(Integer productId) {
        // Chamada simples como se fosse um método local
        Product product = productFeignClient.getProduct(productId);
        return product;
    }
}
```

## Passo 5: Configurar Endereços dos Serviços

No `application.yaml`, configure os endereços dos serviços (útil para desenvolvimento local sem Eureka):

```yaml
app:
  product-service:
    host: localhost
    port: 8001
  recommendation-service:
    host: localhost
    port: 8002
  review-service:
    host: localhost
    port: 8003
```

## Passo 6: Tratamento de Erros

Implemente tratamento de erros adequado para falhas de comunicação:

```java
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.HttpStatus;
import java.util.Objects;

public Product getProduct(Integer productId) {
    try {
        Product product = productFeignClient.getProduct(productId);
        return product;
    } catch (HttpClientErrorException e) {
        switch (Objects.requireNonNull(HttpStatus.resolve(e.getStatusCode().value()))) {
            case NOT_FOUND:
                throw new NotFoundException("Produto não encontrado: " + productId);
            case UNPROCESSABLE_CONTENT:
                throw new InvalidInputException("ID de produto inválido: " + productId);
            default:
                throw new RuntimeException("Erro ao chamar product-service", e);
        }
    }
}
```

## Configurações Avançadas

### Configuração Personalizada do Feign

Você pode criar uma classe de configuração para personalizar o comportamento do Feign:

```java
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import feign.Logger;

@FeignClient(name = "product-service", configuration = ProductFeignConfig.class)
public interface ProductFeignClient {
    // métodos...
}

@Configuration
public class ProductFeignConfig {
    
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;  // Log completo das requisições
    }
}
```

### Timeout e Retry

Configure timeouts no `application.yaml`:

```yaml
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 5000
        loggerLevel: full
```

### Autenticação

Para serviços que requerem autenticação, adicione interceptors usando `RequestInterceptor` (específico do OpenFeign):

```java
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductFeignConfig {
    
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Authorization", "Bearer " + getToken());
        };
    }
    
    private String getToken() {
        // Lógica para obter o token JWT
        return "your-jwt-token-here";
    }
}
```

**Diferença do RestTemplate:** Ao contrário do RestTemplate onde você adiciona headers manualmente em cada chamada (`restTemplate.exchange(url, method, entity, responseType)`), o OpenFeign usa `RequestInterceptor` para adicionar headers automaticamente a todas as requisições do cliente Feign.

## Vantagens do OpenFeign

- **Declarativo**: Define contratos de API como interfaces Java
- **Integração com Spring**: Funciona perfeitamente com injeção de dependência
- **Service Discovery**: Integração automática com Eureka
- **Load Balancing**: Suporte a Ribbon para balanceamento de carga
- **Fallbacks**: Suporte a circuit breakers com Hystrix/Resilience4j

## Exemplo Completo

Para replicar em outros serviços, siga este padrão:

1. Adicione a dependência no `build.gradle`
2. Anote a classe principal com `@EnableFeignClients`
3. Crie interfaces `@FeignClient` para cada serviço remoto
4. Injete os clientes nos seus serviços de integração
5. Implemente tratamento de erros apropriado

## Conclusão

OpenFeign simplifica significativamente a comunicação entre microserviços, tornando o código mais limpo e fácil de manter. Combinado com Spring Cloud e Eureka, fornece uma solução robusta para arquitetura de microserviços.

Para mais informações, consulte a [documentação oficial do Spring Cloud OpenFeign](https://spring.io/projects/spring-cloud-openfeign).

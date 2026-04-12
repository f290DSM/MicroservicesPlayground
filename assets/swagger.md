# Tutorial: Implementando Swagger com OpenAPI em Microserviços Spring Boot

## Introdução

Este tutorial guia você através da implementação do Swagger com OpenAPI 3.0 em um microserviço Spring Boot. O Swagger/OpenAPI permite documentar automaticamente as APIs REST, gerando uma interface interativa (Swagger UI) para testar os endpoints.

No contexto deste projeto, implementamos o OpenAPI no `product-composite-service` usando a biblioteca SpringDoc OpenAPI.

## Pré-requisitos

- Projeto Spring Boot com Gradle
- Java 17 ou superior
- Conhecimento básico de Spring Boot e REST APIs

## Passo 1: Adicionar Dependências

Adicione a dependência do SpringDoc OpenAPI no `build.gradle` do seu microserviço:

```groovy
dependencies {
    // ... outras dependências
    
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2'
    
    // ... mais dependências
}
```

Esta dependência inclui:
- `springdoc-openapi-starter-webmvc-ui`: Para geração automática da documentação OpenAPI e interface Swagger UI

## Passo 2: Configurar o application.yaml

Adicione as configurações do SpringDoc no arquivo `application.yaml` (ou `application.properties`):

```yaml
springdoc:
  swagger-ui.path: /openapi/swagger-ui.html
  api-docs.path: /openapi/v3/api-docs
  packagesToScan: dev.sdras.microservices.composite.product  # Pacote onde estão seus controllers
  pathsToMatch: /**
  swagger-ui:
    config-url: /openapi/v3/api-docs/swagger-config
    url: /openapi/v3/api-docs
    try-it-out-enabled: true
    filter: true
  api-docs:
    path: /openapi/v3/api-docs

# Informações da API (usadas na configuração do OpenAPI)
api:
  common:
    version: 1.0.0
    title: Product Composite API
    description: API de unificação de informações de produtos, recomendações e reviews.
    termsOfService: Em desenvolvimento...
    license: Em desenvolvimento...
    licenseUrl: Em desenvolvimento...
    externalDocDesc: Em desenvolvimento...
    externalDocUrl: Em desenvolvimento...
    contact:
      name: Seu Nome
      url: seu-site.com
      email: seu-email@exemplo.com

  responseCodes:
    ok.description: OK
    badRequest.description: Bad Request, formato inválido de requisição. Veja a mensagem de resposta para maiores detalhes.
    notFound.description: Not found, o id especificado não existe.
    unprocessableEntity.description: Unprocessable entity, parâmetros de entrada causaram falha no processamento. Veja a mensagem de resposta para maiores detalhes.

  product-composite:
    get-composite-product:
      description: Retorna uma visão composta do produto especificado por id.
      notes: |
        # Resposta normal
        Se o id do produto solicitado for encontrado, o método retornará informações sobre:
        1. Product information
        2. Reviews
        3. Recommendations
        4. Service Addresses\n(informações técnicas sobre os endereços dos microsserviços que criaram a resposta)
        
        # Expectativa parcial e respostas com erros.
        1. Se nenhuma informação do produto for encontrada, um erro **404 - Not Found** será retornado
        2. Se nenhuma recomendação ou review for encontrada para um produto, uma resposta parcial será retornada
```

**Explicação das configurações:**
- `swagger-ui.path`: Caminho para acessar a interface Swagger UI
- `api-docs.path`: Caminho para o JSON da documentação OpenAPI
- `packagesToScan`: Pacotes onde o SpringDoc deve procurar por controllers
- As seções `api.common.*` e `api.responseCodes.*` são usadas para internacionalização e reutilização de textos

## Passo 3: Criar a Configuração OpenAPI

Crie uma classe de configuração para definir as informações gerais da API:

```java
package dev.sdras.microservices.composite.product.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {

    // Injetar valores do application.yaml
    @Value("${api.common.version}")         String apiVersion;
    @Value("${api.common.title}")           String apiTitle;
    @Value("${api.common.description}")     String apiDescription;
    @Value("${api.common.termsOfService}")  String apiTermsOfService;
    @Value("${api.common.license}")         String apiLicense;
    @Value("${api.common.licenseUrl}")      String apiLicenseUrl;
    @Value("${api.common.externalDocDesc}") String apiExternalDocDesc;
    @Value("${api.common.externalDocUrl}")  String apiExternalDocUrl;
    @Value("${api.common.contact.name}")    String apiContactName;
    @Value("${api.common.contact.url}")     String apiContactUrl;
    @Value("${api.common.contact.email}")   String apiContactEmail;

    @Bean
    public OpenAPI getOpenApiDocumentation() {
        return new OpenAPI()
                .info(new Info().title(apiTitle)
                        .description(apiDescription)
                        .version(apiVersion)
                        .contact(new Contact()
                                .name(apiContactName)
                                .url(apiContactUrl)
                                .email(apiContactEmail))
                        .termsOfService(apiTermsOfService)
                        .license(new License()
                                .name(apiLicense)
                                .url(apiLicenseUrl)))
                .externalDocs(new ExternalDocumentation()
                        .description(apiExternalDocDesc)
                        .url(apiExternalDocUrl));
    }
}
```

Este bean configura as informações básicas da API que aparecem na documentação.

## Passo 4: Adicionar Anotações OpenAPI nos Controllers

Adicione anotações OpenAPI nas interfaces ou classes dos seus controllers. Exemplo:

```java
package dev.sdras.api.composite.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "ProductComposite", description = "REST API para composição e integração de informações de produtos.")
public interface ProductCompositeService {

    @Operation(
            summary = "${api.product-composite.get-composite-product.description}",
            description = "${api.product-composite.get-composite-product.notes}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
            @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
            @ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
            @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}")
    })
    @GetMapping(
        value = "/product-composite/{productId}",
        produces = "application/json")
    ProductAggregate getProduct(@PathVariable int productId);
}
```

**Anotações importantes:**
- `@Tag`: Agrupa endpoints relacionados
- `@Operation`: Descreve o endpoint específico
- `@ApiResponses`: Define as possíveis respostas HTTP
- Os valores `${...}` referenciam propriedades do `application.yaml` para internacionalização

## Passo 5: Executar e Testar

1. **Inicie o microserviço:**
   ```bash
   ./gradlew :microservices:product-composite-service:bootRun
   ```

2. **Acesse a documentação:**
   - **Swagger UI**: `http://localhost:8000/openapi/swagger-ui.html` (ajuste a porta conforme seu serviço)
   - **JSON OpenAPI**: `http://localhost:8000/openapi/v3/api-docs`

3. **Teste os endpoints:**
   - Use a interface Swagger UI para testar os endpoints interativamente
   - Expanda um endpoint e clique em "Try it out"

## Dicas Adicionais

### Anotações Úteis

- `@Parameter`: Descreve parâmetros de entrada
- `@Schema`: Define esquemas para objetos complexos
- `@Hidden`: Oculta endpoints da documentação

### Exemplo de Uso com Parâmetros

```java
@Operation(summary = "Buscar produto por ID")
@GetMapping("/products/{id}")
public Product getProduct(
    @Parameter(description = "ID do produto") 
    @PathVariable Long id) {
    // implementação
}
```

## Conclusão

Seguindo estes passos, você terá uma documentação OpenAPI completa e interativa para seus microserviços. A documentação será automaticamente atualizada conforme você modifica os endpoints e anotações.

Para mais informações, consulte a [documentação oficial do SpringDoc](https://springdoc.org/).

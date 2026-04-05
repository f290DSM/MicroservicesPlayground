# Desafio MicroServices - Aula Presencial

## Desafio

Implementar a comunicação entre os microserviços de `produto`, `review`, `recomendação` e `composição` utilizando o 
Eureka Server para registro e descoberta de serviços, o Edge Server para roteamento e balanceamento de carga e o Config 
Server para centralizar as confiurações do microserviços.

### Orientações Gerais

Cada equipe deverá criar um microserviço a partir da branch principal do projeto `microservices`, seguindo a estrutura do `product-service` e implementando as funcionalidades específicas de cada serviço, conforme descrito abaixo:

## Equipes

## 1. Central Configuration 

### Integrantes
- **Jhonatan**
- **Giovanni**
- **Miguel Barbieri**
- **Gabriel Barbieri**
- **Brunno Rizzo**

## 2. Product Review Service

### Integrantes
- **Matheus**
- **Felipe**
- **Chritian**
- **Gabriel Oliveira**
- **Marcelo**

## 3. Product Composite Service

### Integrantes
- **Luiz**
- **Abner**
- **Darlan**
- **Victor**
- **Marcos**

## 4. Product Composite Service

### Integrantes
- **Pedro Rufino**
- **Gabriel Schrank**
- **Kalliel**
- **Bruno Guinério**
- **Stephan**

# Product Review, Recommendation e Composite

Os 3 grupos deverão criar os microserviços de `review`, `recomendação` e `composição` seguindo a mesma estrutura do `product-service`, configurando o registro no Eureka Server, e implementando as funcionalidades específicas de cada serviço.

O Projeto `api` possui as interfaces às quais microseqviços deverão implementar, e as dependências necessárias para a comunicação entre os microserviços. O projeto `api` deve ser utilizado como uma biblioteca compartilhada entre os microserviços para garantir a consistência das interfaces e facilitar a comunicação entre eles.

Cada microserviço deve ser configurado para se registrar no Eureka Server, permitindo que eles sejam descobertos por outros serviços na arquitetura. O Edge Server irá rotear as requisições para as instâncias dos microserviços de forma balanceada, utilizando o mecanismo de round-robin do Spring Cloud Gateway.

Cada microserviço deverá implementar os repositories já configurados para acessar os dados numa base de dados da escolha da equipe; os controllers para expor as APIs REST, seguindo as interfaces definidas no projeto `api`.

A última etapa será a configuração de cada microserviço para ler as configurações do Config Server, garantindo que as configurações sejam centralizadas e gerenciadas de forma eficiente.

## Product Composite Service

Deverá implementar um endpoint que agregue as informações dos microserviços de `produto`, `review` e `recomendação` para fornecer uma visão completa do produto, incluindo as suas avaliações e recomendações.

O grupo deverá criar um end-point no `product-composite-service` que faça chamadas para os microserviços de `produto`, `review` e `recomendação` utilizando o Eureka Server para descobrir as instâncias dos serviços e o Edge Server para rotear as requisições, agregando as informações retornadas para fornecer uma resposta completa ao cliente, orquestrando os microserviços de forma eficiente e garantindo a consistência dos dados retornados.

## Central Configuration

### Config Server

O Grupo de Central Configuration deverá criar um Config Server utilizando o Spring Cloud Config, que irá centralizar as configurações dos microserviços de `produto`, `review`, `recomendação` e `composição`. O Config Server deve ser configurado para ler as configurações a partir de um repositório Git, permitindo que as configurações sejam versionadas e gerenciadas de forma centralizada.


# tokio-desafio-java

Projeto desenvolvido como parte de uma avaliacao tecnica para processo seletivo da Tokio Marine.

A aplicação permite o agendamento de transferências financeiras, calcula automáticamente a taxa aplicável de acordo com a data escolhida para a transferência e permite consultar o extrato dos agendamentos cadastrados.


## Tecnologias

- Java 11
- Spring Boot 2.7.18
- Maven
- Spring Web
- Spring Data JPA
- Bean Validation
- H2 Database
- Lombok
- JUnit 5
- Mockito

## Decisoes arquiteturais

O backend foi organizado em Layered Architecture:

```text
controller  -> endpoints REST, DTOs e mappers da API
service     -> casos de uso e regras de negocio
model       -> dominio puro da transferencia agendada
repository  -> persistencia, entidade JPA e mapper de banco
exception   -> tratamento padronizado de erros
```

Principais decisoes:

- `ScheduledTransfer` foi mantida como objeto de dominio, separado da entidade JPA.
- `ScheduledTransferEntity` representa a tabela no banco H2.
- `ScheduledTransferMapper` converte entre dominio e persistência.
- `ScheduledTransferControllerMapper` converte dominio para resposta da API.
- O cálculo da taxa foi isolado em `TransferFeeCalculator`.
- Valores financeiros usam `BigDecimal`.
- Erros da API são padronizados por `GlobalExceptionHandler`.


## Como executar o backend

Entre na pasta do backend:

```bash
cd Backend
```

Execute a aplicação:

```bash
mvn spring-boot:run
```

A API sobe em:

```text
http://localhost:8082
```

Console do H2:

```text
http://localhost:8082/h2-console
```

Dados do H2:

```text
JDBC URL: jdbc:h2:mem:tokiodb
User: sa
Password: deixe vazio
```

## Como executar os testes

Na pasta `Backend`, execute:

```bash
mvn test
```

## Endpoints

```text
POST /scheduled-transfers
GET  /scheduled-transfers
```

### Agendar transferência valida

```bash
curl -X POST http://localhost:8082/scheduled-transfers \
  -H "Content-Type: application/json" \
  -d '{
    "sourceAccount": "1234567890",
    "destinationAccount": "0987654321",
    "amount": 1000.00,
    "transferDate": "2026-06-03"
  }'
```

Resposta esperada:

```json
{
  "id": 1,
  "sourceAccount": "1234567890",
  "destinationAccount": "0987654321",
  "amount": 1000.00,
  "fee": 12.00,
  "totalAmount": 1012.00,
  "transferDate": "2026-06-03",
  "schedulingDate": "2026-05-24"
}
```

### Consultar extrato

```bash
curl http://localhost:8082/scheduled-transfers
```

Resposta esperada:

```json
[
  {
    "id": 1,
    "sourceAccount": "1234567890",
    "destinationAccount": "0987654321",
    "amount": 1000.00,
    "fee": 12.00,
    "totalAmount": 1012.00,
    "transferDate": "2026-06-03",
    "schedulingDate": "2026-05-24"
  }
]
```

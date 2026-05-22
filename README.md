# tokio-desafio-java

Projeto desenvolvido como parte de uma avaliação técnica para processo seletivo da Tokio Marine.

A aplicação tem como objetivo permitir o agendamento de transferências financeiras, calculando automaticamente a taxa aplicável de acordo com a data escolhida para a transferência. 
O sistema também permite consultar o extrato de todos os agendamentos cadastrados.

O projeto foi desenvolvido utilizando Java 11, Spring Boot e Angular, com persistência em banco de dados em memória H2.

## Requisitos funcionais

- Permitir o agendamento de transferências financeiras.
- Informar conta de origem, conta de destino, valor e data da transferência.
- Gerar automaticamente a data de agendamento.
- Calcular automaticamente a taxa conforme a diferença de dias entre agendamento e transferência.
- Bloquear agendamentos quando não houver taxa aplicável.
- Listar todos os agendamentos cadastrados.
- Persistir os dados em banco de dados em memória H2.

## Requisitos não funcionais

- Backend desenvolvido em Java 11 com Spring Boot.
- Frontend desenvolvido em Angular.
- Persistência em banco H2.
- Projeto versionado no GitHub com histórico de commits.
- README com decisões arquiteturais, ferramentas, versões e instruções de execução.
- Regras de negócio cobertas por testes unitários.
- Uso de BigDecimal para valores financeiros.
- Tratamento padronizado de erros na API.
- Organização em Layered Architecture.

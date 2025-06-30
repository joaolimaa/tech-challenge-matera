# Matera Bank API

**Desafio técnico — API RESTful** para operações bancárias como criação de contas, autenticação de usuários, lançamentos de crédito/débito e consulta de saldo.

Projeto desenvolvido com o objetivo de demonstrar domínio técnico em Java, boas práticas de arquitetura, segurança e testes — com muito entusiasmo em fazer parte do time da **Matera**!

---

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3 (Web, Security, Validation)**
- **Spring Data JPA**
- **H2 Database (em memória)**
- **Flyway** para versionamento de scripts SQL
- **JWT (JSON Web Token)** para autenticação
- **Swagger / OpenAPI 3** para documentação da API
- **JUnit 5 e Mockito** para testes unitários
- **Lombok** para redução de código repetitivo
- **SLF4J / Log4j** para logs

---

## 📁 Arquitetura do Projeto

A API segue uma estrutura limpa e organizada com separação em camadas:

```
matera-bank-api/
├── src/
│   ├── main/
│   │   ├── java/com/example/materabank/
│   │   │   ├── core/
│   │   │   │   ├── model/
│   │   │   │   └── exception/
│   │   │   ├── infra/
│   │   │   │   ├── controller/
│   │   │   │   │   ├── dto/
│   │   │   │   │   │   ├── request/
│   │   │   │   │   │   └── response/
│   │   │   │   ├── gateway/
│   │   │   │   ├── mapper/
│   │   │   │   ├── repository/
│   │   │   │   └── security/
│   │   │   └── application/
│   │   └── resources/
│   │       └── db/migration/ (scripts Flyway)
│   └── test/
│       └── java/com/example/materabank/
├── pom.xml
└── README.md
```
---

## 🧪 Testes

O projeto possui **ampla cobertura de testes unitários**, validando todos os cenários importantes:
- Criação e consulta de usuários e contas
- Processamento de transações
- Autenticação e segurança
- Exceções como saldo insuficiente e recursos não encontrados

Para ver o relatório do `JACOCO`:

```bash
mvn clean verify
```
Após a execução, abra o seguinte arquivo no navegador:
`target/site/jacoco/index.html`

Use a opção “Open in Browser” na IDE ou abra manualmente no navegador.

---

## 🗃️ Banco de Dados

- Utiliza **H2 em memória**, configurado automaticamente ao iniciar a aplicação.
- Com **Flyway**, as tabelas são criadas automaticamente e um usuário inicial é inserido no banco.

---

## 📑 Documentação com Swagger

Ao rodar a aplicação, você pode acessar a interface de documentação Swagger com todos os endpoints:
📎 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

Para facilitar, ao rodar o arquivo MateraBankApplication, aparecerá o link no console da sua aplicação.

---

## ⚙️ Funcionalidades

### UserController
- `POST /user/create` – Criar novo usuário
- `GET /user/{id}` – Buscar por ID
- `PUT /user/update/{id}` – Atualizar usuário
- `GET /user/all-users` – Listar todos
- `DELETE /user/delete/{id}` – Excluir usuário

### AuthenticationController
- `POST /auth/login` – Login
- `POST /auth/validate-token` – Valida token JWT

### AccountController
- `POST /account/create` – Criar conta
- `GET /account/{id}` – Buscar conta
- `GET /account/{id}/balance` – Consultar saldo
- `GET /account/all-accounts` – Listar contas
- `DELETE /account/delete/{id}` – Excluir conta
- `POST /account/{id}/transactions` – Realizar lançamentos (crédito/débito)

---

## ✅ Testando na Prática
Na aba /auth/login, clique em `Try it out`. O modelo já virá preenchido com um exemplo de login.

A criação de contas via /account/create também está pré-configurada para testes rápidos.

Após autenticar-se, o token gerado permitirá acesso aos endpoints seguros como:

- Lançamentos de crédito/débito

- Consulta de saldo

- Exclusão da conta

---

## Considerações Finais
Este projeto foi desenvolvido com muito carinho como parte do desafio técnico da Matera.

Tenho plena consciência de que ainda há muito a aprender, mas estou disposto a correr atrás, evoluir constantemente e colaborar com o time com dedicação, curiosidade e paixão pela tecnologia.

Agradeço pela oportunidade de apresentar um pouco do meu trabalho! 🚀


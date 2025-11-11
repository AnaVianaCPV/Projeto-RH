# 💼 CadastrosRH — API de RH (Fase 1: Candidatos)

API REST para gestão de candidatos, construída com **Java 21** e **Spring Boot 3.5.x**, com:
- Persistência em **PostgreSQL** (produção/dev) e **H2** (testes);
- Migrações **Flyway**;
- Validações **Bean Validation**;
- Paginação e filtros;
- Tratamento de erros em **RFC 7807 (Problem Details)**;
- Documentação com **OpenAPI/Swagger**;
- Camada de segurança inicial com **Spring Security + JWT (Nimbus)**, pronta para evoluir.

---

## ⚙️ Stack Tecnológica

| Componente | Tecnologia |
|-------------|-------------|
| **Linguagem/Runtime** | Java 21 |
| **Framework** | Spring Boot 3.5.x (Web, Data JPA, Validation) |
| **Banco de Dados** | PostgreSQL (dev/prod) / H2 em memória (testes) |
| **Migrações** | Flyway |
| **Segurança** | Spring Security + JWT (Nimbus) |
| **Documentação** | springdoc-openapi (Swagger UI) |
| **Utilidades** | Lombok, Jackson, BeanUtils, JPA Specifications |
| **Build** | Maven |

---

## 🐘 Como Iniciar a API com PostgreSQL

A aplicação usa o driver PostgreSQL e o arquivo `application-postgres.yml`.

### 🔧 1️⃣ Defina as variáveis de ambiente:

```bash
$env:SPRING_PROFILES_ACTIVE="postgres"
$env:SPRING_DATASOURCE_PORT="sua-porta-aqui"
$env:SPRING_DATASOURCE_USERNAME="seu-username-aqui"
$env:SPRING_DATASOURCE_PASSWORD="sua-senha-aqui"
./mvnw.cmd spring-boot:run

Atenção: Em ambientes Linux/macOS, substitua $env:NOME_DA_VARIAVEL="valor"
por export NOME_DA_VARIAVEL="valor" e use ./mvnw em vez de ./mvnw.cmd.

Como Acessar o Banco de Dados H2 (Ambiente de Testes)

A aplicação utiliza o H2 Database em memória para testes locais e automatizados.
Esse banco é inicializado automaticamente na subida da API e pode ser acessado pelo console web embutido do H2.

1️⃣ Inicie a API normalmente

Para executar a aplicação com o banco H2 (padrão), basta rodar:

./mvnw.cmd spring-boot:run

2️⃣ Acesse o Console Web do H2

Abra o navegador e entre em:

http://localhost:8080/h2-console


⚠️ Caso apareça uma janela pedindo usuário e senha HTTP, é a proteção padrão do Spring Security.
No perfil H2, o console /h2-console está liberado automaticamente.
Se continuar pedindo login, abra o link em uma aba anônima ou limpe o cache do navegador.

3️⃣ Faça login no H2 Console
Campo	Valor
JDBC URL	jdbc:h2:mem:rhdb
User Name	rhdb
Password	(deixe em branco)

Clique em Connect.

4️⃣ Estrutura esperada

Após o login, o console exibirá o schema PUBLIC com a tabela:

CANDIDATOS


Criada automaticamente via Flyway a partir do script db/h2/V1__create_table_candidatos.sql.

✅ Observações

O banco H2 é temporário (em memória) — é recriado toda vez que a aplicação reinicia.

Esse perfil é usado apenas para testes locais.

Em ambientes de desenvolvimento ou produção, utilize o perfil PostgreSQL.

#✅ To-Do Checklist – RH API (Spring Boot + PostgreSQL) – Fase 1: Candidatos

✅ Feito
## 1) Banco & Migrações (Flyway)
https://medium.com/@perez_vitor/o-que-%C3%A9-flyway-e-por-que-usa-lo-com-java-e-spring-312219ebf840
 Criar `V1__create_table_candidatos.sql` com tabela, índices e constraints
- Habilitar UUID (se usar `uuid_generate_v4()` ou usar `@GeneratedValue` com UUID)
- Constraints: `email` **UNIQUE**, `cpf` **UNIQUE**
- Índices: `nome`, `status`
- Rodar migração e validar que a tabela existe

**DoD:** Banco `rhdb` com tabela `candidatos` criada via Flyway sem erro.

---
✅ Feito
## 2) Modelo de Domínio (Entity) + Enums
- `Candidato` (JPA):  
  id (UUID), nome, cpf, email, telefone, dataNascimento,  
  areaInteresse, experienciaAnos (>=0), pretensaoSalarial (>=0),  
  status (enum), criadoEm, atualizadoEm
 Enum `StatusCandidato`: `CANDIDATO`, `TRIAGEM`, `APROVADO`, `REPROVADO`
Auditar `criadoEm/atualizadoEm` (via `@PrePersist/@PreUpdate` ou Envers/Listeners)

**DoD:** Entidade mapeada, compila, e persiste um registro de teste.

---
✅ Feito
## 3) DTOs & Validações (Bean Validation)
- `CandidatoCreateDTO` (POST) – regras: nome (3–120), cpf (formato BR), email (válido), experienciaAnos >= 0, pretensaoSalarial >= 0
- `CandidatoUpdateDTO` (PUT) – todos os campos necessários (substituição total)
- `CandidatoResponseDTO` (GET) – o que retorna ao cliente
- Mensagens de validação padronizadas em `ValidationMessages.properties`
- Validador CPF (custom ou lib) e normalização (`\D` → remover)

**DoD:** Requests inválidos recebem **400** com mensagens claras de campo.

---
✅ Feito
## 4) Mapper (DTO ↔ Entity)
Implementar mapper (MapStruct ou manual) para:
- CreateDTO → Entity
- UpdateDTO → merge em Entity
- Entity → ResponseDTO

**DoD:** Conversões corretas cobertas por testes unitários simples.

---
✅ Feito
## 5) Camada de Acesso a Dados
- `CandidatoRepository` (`JpaRepository<Candidato, UUID>`)
- Consultas de filtro (por `nome`, `email`, `status`, faixa de `experienciaAnos`) – `Example`, `@Query` ou `Specification`

**DoD:** Filtros funcionando com testes de repositório (mínimo).

---
✅ Feito
## 6) Regras de Negócio (Service)
- Salvar com validações: **CPF único**, **email único**
- Buscar por ID com **404** se não existir
- Listar **paginado** com filtros (nome/status/exp) e ordenação
- Atualizar (PUT): substituição total + validações de unicidade
- Atualizar parcial (PATCH): **JSON Merge Patch** (`application/merge-patch+json`)
- Deletar com **404** se não existir

**DoD:** Casos de conflito retornam **409 Conflict**; regras cobertas por testes de service.

---
✅ Feito
## 7) API REST (Controller) – Endpoints Obrigatórios
- `GET /candidatos` – paginação, sort, filtros
- `GET /candidatos/{id}` – 200 ou 404
- `POST /candidatos` – 201 + `Location` | 400 | 409
- `PUT /candidatos/{id}` – 200 | 404 | 409
- `PATCH /candidatos/{id}` – 200 | 404 | 409 (JSON Merge Patch)
- `DELETE /candidatos/{id}` – 204 | 404
- **CORS** liberado para testes locais

**DoD:** Todos os endpoints respondem conforme especificado; testados com cURL/Insomnia/Postman.

---
✅ Feito
## 8) Tratamento de Erros (padrão)
- `@ControllerAdvice` com **RFC 7807 (Problem Details)**  
  Campos: `type`, `title`, `status`, `detail`, `timestamp`, `fields[]`
- Mapear: `MethodArgumentNotValidException`, `ConstraintViolationException`, `EntityNotFound`, `DataIntegrityViolation` (409), genéricos (500)
- IDs inválidos/parse incorreto → **400**

**DoD:** Erros padronizados, previsíveis e documentados.

---

## 9) Documentação (OpenAPI/Swagger)
- `springdoc` configurado
- Anotações de **schema** e **exemplos** nos DTOs/endpoints
- UI disponível em **/swagger-ui.html**
- Incluir exemplos de **filtros/paginação** e **códigos de resposta**

**DoD:** Swagger completo e legível; time consegue testar por ali.

---
 @ Em Progresso
## 10) Testes
- **Unitários**: service, mapper, validações
- **(Opcional)** Integração com **Testcontainers (Postgres)**
- Cobrir cenários principais: criação válida, duplicidade (409), PUT, PATCH, delete, filtros

**DoD:** `mvn test` verde; cenários críticos cobertos.

---

## 11) Observabilidade & Qualidade (Opcional)
- Logs de entrada/saída nos pontos críticos (nível **INFO**)
- **Actuator** (health) habilitado para diagnóstico
- **Checkstyle/SpotBugs** (se tempo permitir)

---

## 12) Coleção de Testes (mínimo) – cURL
- **POST** criar
- **GET** listar (com `page`, `size`, `sort`, `status`, `minExp`)
- **GET** por ID
- **PUT** atualizar completo
- **PATCH** JSON Merge Patch
- **DELETE** por ID

**DoD:** Roteiro de testes funciona ponta a ponta.

-
## 🔒13) Camada de Segurança (Autenticação)
- 	**Item de Segurança**	   Status<br>
13.1	Configuração Spring Security	✅ Feito<br>
13.2	Modelo de Usuário	✅ Feito<br>
13.3	Serviço de Usuário	✅ Feito<br>
13.4	Regras de Acesso	✅ Feito<br>
13.5	Testes de Segurança	✅ Feito

## 14) Extras (se der tempo)
- **Filtro avançado** (faixa salarial, data de nascimento)
- **Upload de currículo** (armazenamento local/S3)
- **Autenticação (Basic Auth)** para rotas de escrita
- **Relatórios simples** (ex.: candidatos por status)


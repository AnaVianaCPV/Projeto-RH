# ✅ To-Do Checklist – RH API (Spring Boot + PostgreSQL) – Fase 1: Candidatos

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


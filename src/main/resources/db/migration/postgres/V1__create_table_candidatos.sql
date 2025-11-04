CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TYPE status_candidato_enum AS ENUM ('CANDIDATO', 'TRIAGEM', 'APROVADO', 'REPROVADO');

CREATE TABLE candidatos
(
    id                 UUID PRIMARY KEY                     DEFAULT uuid_generate_v4(),
    nome               VARCHAR(255)                NOT NULL,
    cpf                VARCHAR(11) UNIQUE          NOT NULL,
    data_nascimento    DATE,
    email              VARCHAR(255) UNIQUE         NOT NULL,
    celular            VARCHAR(20),
    area_interesse     TEXT,
    experiencia_anos   INTEGER                     NOT NULL,
    pretensao_salarial NUMERIC(12, 2),
    status             status_candidato_enum       NOT NULL,
    criado_em          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    atualizado_em      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_experiencia_anos CHECK (experiencia_anos >= 0),
    CONSTRAINT chk_pretensao_salarial CHECK (pretensao_salarial IS NULL OR pretensao_salarial >= 0)
);

CREATE INDEX idx_candidatos_nome   ON candidatos (nome);
CREATE INDEX idx_candidatos_status ON candidatos (status);
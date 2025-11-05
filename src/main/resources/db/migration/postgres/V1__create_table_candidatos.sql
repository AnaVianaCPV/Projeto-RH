
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TYPE status_candidato_enum AS ENUM ('CANDIDATO', 'TRIAGEM', 'APROVADO', 'REPROVADO');

CREATE TABLE candidatos
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    data_nascimento DATE,
    email VARCHAR(255) UNIQUE NOT NULL,

    senha_hash VARCHAR(60) NOT NULL,

    celular VARCHAR(20),
    area_interesse TEXT,
    experiencia_anos INTEGER NOT NULL,
    pretensao_salarial NUMERIC(12, 2),
    status status_candidato_enum NOT NULL,

    curriculo_url VARCHAR(500),
    curriculo_nome VARCHAR(255),
    curriculo_content_type VARCHAR(100),
    curriculo_tamanho_bytes BIGINT,
    curriculo_atualizado_em TIMESTAMP,
    curriculo_storage VARCHAR(10),

    criado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_candidatos_nome   ON candidatos (nome);
CREATE INDEX idx_candidatos_status ON candidatos (status);

CREATE TABLE IF NOT EXISTS candidatos (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    data_nascimento DATE,
    email VARCHAR(255) UNIQUE NOT NULL,
    celular VARCHAR(20),
    pretensao_salarial NUMERIC(12,2),
    status VARCHAR(20)
    );
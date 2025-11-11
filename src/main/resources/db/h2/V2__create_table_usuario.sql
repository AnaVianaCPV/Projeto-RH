CREATE TABLE IF NOT EXISTS usuario (
                                       id UUID PRIMARY KEY,
                                       nome VARCHAR(255) NOT NULL,
                                       email VARCHAR(255) UNIQUE NOT NULL,
                                       senha_hash VARCHAR(255) NOT NULL,
                                       role VARCHAR(50),
                                       criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

package com.RHgroup.CadastrosRH.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*todos do lombok, inserem automáticamente os Getters, Setters pra quando formos usar em
outras classes. Insere também Construtor vazio e Construtor
com todos argumentos sem precisar escrever*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

// mapeia a classe para o banco de dados
@Entity
@Table(name= "candidatos")

public class Candidato {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column (nullable = false)
    private String nome;

    @Column(name = "cpf", length = 11, nullable = false, unique = true)
    private String cpf;

    private LocalDate dataNascimento;

    @Column (nullable = false, unique= true)
    private String email;

    private String celular;

    private BigDecimal pretensaoSalarial;

    @Enumerated(EnumType.STRING)
    private StatusCandidato status;



}

package com.example.servidor.remedio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_alteracao")
@Getter
@Setter
public class HistoricoAlteracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "remedio_id")
    private Long remedioId;

    @Column(columnDefinition = "TEXT")
    private String estadoAntigo;

    @Column(columnDefinition = "TEXT")
    private String estadoNovo;

    private String tipoAlteracao; // Ex: "ATUALIZACAO", "CADASTRO", "DESATIVACAO", etc

    private LocalDateTime dataAlteracao;

    public HistoricoAlteracao() {}

    public HistoricoAlteracao(Long remedioId, String estadoAntigo, String estadoNovo, String tipoAlteracao) {
        this.remedioId = remedioId;
        this.estadoAntigo = estadoAntigo;
        this.estadoNovo = estadoNovo;
        this.tipoAlteracao = tipoAlteracao;
        this.dataAlteracao = LocalDateTime.now();
    }

   
}

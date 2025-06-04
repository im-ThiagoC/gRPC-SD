package com.example.servidor.remedio;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//entities

@Entity(name = "Remedio")
@Table(name = "Remedio")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Remedio {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	private String nome;
	
	
	
	@Enumerated(EnumType.STRING)
	private Via via;
	private String lote;
	private int quantidade;
	private LocalDate validade;
	
	@Enumerated(EnumType.STRING)
	private Laboratorio laboratorio;
	
	private boolean ativo;
	
	public Remedio(DadosCadastroRemedio dados) {
		this.nome = dados.nome();
		this.via = dados.via();
		this.lote = dados.lote();
		this.quantidade = dados.quantidade();
		this.validade = dados.validade();
		this.laboratorio = dados.laboratorio();
		this.ativo = true;
	}

	public void atualizarDados(@Valid DadosAtualizarRemedio dados) {
		if(dados.nome() != null) {
			this.nome = dados.nome();
		}
		
		if(dados.quantidade() > 0) {
			this.quantidade = dados.quantidade();
		}
		 
		if(dados.via() != null) {
			this.via = dados.via();
		}		
		
		if(dados.laboratorio() != null) {
			this.laboratorio = dados.laboratorio();
		}
	}

	public void desativar() {
		this.ativo = false;
	}
	public void ativar() {
		this.ativo = true;
	}
	
	
}

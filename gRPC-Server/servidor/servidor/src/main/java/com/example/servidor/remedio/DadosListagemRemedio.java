package com.example.servidor.remedio;

import java.time.LocalDate;


//DTO
public record DadosListagemRemedio(long id, String nome,	Via via, String lote, int quantidade, LocalDate validade,Laboratorio laboratorio) {
	
	 public DadosListagemRemedio(Remedio remedio) {
		this(remedio.getId(),remedio.getNome(),remedio.getVia(),remedio.getLote(),remedio.getQuantidade(),remedio.getValidade(),remedio.getLaboratorio());
	
	}
}

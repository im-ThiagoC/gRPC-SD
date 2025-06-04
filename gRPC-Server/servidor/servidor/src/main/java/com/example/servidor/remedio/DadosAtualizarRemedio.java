package com.example.servidor.remedio;

import jakarta.validation.constraints.NotNull;

public record DadosAtualizarRemedio (
		@NotNull
		Long id, 
		String nome,
		int quantidade,
		Via via, 
		Laboratorio laboratorio) {

}

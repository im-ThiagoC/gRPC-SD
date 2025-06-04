package com.example.servidor.remedio;

import java.time.LocalDate;

import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

//DTO
public record DadosCadastroRemedio(
		@NotBlank
		String nome,
		@Enumerated
		Via via, 
		@NotBlank
		String lote, 
		
		@NotNull
		int quantidade, 
		
		@Future
		LocalDate validade,
		
		@Enumerated
		Laboratorio laboratorio) {
	
}

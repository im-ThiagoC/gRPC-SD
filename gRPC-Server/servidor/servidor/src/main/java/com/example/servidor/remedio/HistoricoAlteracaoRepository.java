package com.example.servidor.remedio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoAlteracaoRepository extends JpaRepository<HistoricoAlteracao, Long> {
	 List<HistoricoAlteracao> findByRemedioId(Long remedioId);
}


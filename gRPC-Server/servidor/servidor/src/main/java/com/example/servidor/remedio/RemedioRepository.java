package com.example.servidor.remedio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
//Repository
public interface RemedioRepository extends JpaRepository<Remedio, Long>{

	List<Remedio> findAllByAtivoTrue();

}

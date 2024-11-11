package br.com.monitoramento.jobdynatrace.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.monitoramento.jobdynatrace.models.Entitie;

public interface EntitieRepository extends JpaRepository<Entitie, Long> {
	Entitie findByEntityId(String entityId);
	Optional<Entitie> findById(Long id);
	Page<Entitie> findByEntityIdContainsOrNameContains(String entityId, String name, Pageable paginacao);

}
;
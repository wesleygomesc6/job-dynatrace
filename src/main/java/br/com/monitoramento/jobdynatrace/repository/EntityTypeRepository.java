package br.com.monitoramento.jobdynatrace.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.monitoramento.jobdynatrace.models.EntityType;

public interface EntityTypeRepository extends JpaRepository<EntityType, Long> {
	Page<EntityType> findByName(String name, Pageable paginacao);
	EntityType findByName(String type);

}

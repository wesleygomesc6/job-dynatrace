package br.com.monitoramento.jobdynatrace.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.monitoramento.jobdynatrace.models.Problem;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

	Problem findByProblemId(String problemId);
	Optional<Problem> findByDisplayId(String displayId);
	List<Problem> findByStatus(String string);
	@Query( value = "SELECT * FROM problems p WHERE p.start_time BETWEEN :dateTimeInitial AND :dateTimeFinal AND p.root_cause_entity_id IS NULL", nativeQuery = true)
	List<Problem> findByFiltrarData( String dateTimeInitial, String dateTimeFinal);

	@Query( value = "SELECT p.start_time FROM problems p ORDER BY p.start_time DESC LIMIT 1;", nativeQuery = true)
	String findByLastProblems();

}

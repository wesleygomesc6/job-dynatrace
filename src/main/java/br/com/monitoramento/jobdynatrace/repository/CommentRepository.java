package br.com.monitoramento.jobdynatrace.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.monitoramento.jobdynatrace.models.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	Page<Comment> findByProblemId(Long idProblema, Pageable paginacao);
	Page<Comment> findByProblemProblemId(String problemId, Pageable paginacao);

}

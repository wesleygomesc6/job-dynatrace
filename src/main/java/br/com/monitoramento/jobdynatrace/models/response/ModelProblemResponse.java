package br.com.monitoramento.jobdynatrace.models.response;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.OneToMany;

public class ModelProblemResponse {
	
	private Long totalCount;
	private Long pageSize;
	@OneToMany
	private List<ProblemResponse> problems =  new ArrayList<>();
	
	public ModelProblemResponse () {}
	
	public ModelProblemResponse(Long totalCount, Long pageSize, List<ProblemResponse> problems) {
		this.totalCount = totalCount;
		this.pageSize = pageSize;
		this.problems = problems;
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	public Long getPageSize() {
		return pageSize;
	}

	public void setPageSize(Long pageSize) {
		this.pageSize = pageSize;
	}

	public List<ProblemResponse> getProblems() {
		return problems;
	}

	public void setProblems(List<ProblemResponse> problems) {
		this.problems = problems;
	}
	
	

}

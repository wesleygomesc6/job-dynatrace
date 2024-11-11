package br.com.monitoramento.jobdynatrace.models.response;

import java.util.List;

import javax.persistence.ManyToOne;

public class Evidence {
	private Long totalCount;
	@ManyToOne
	private List<Detail> details;
	
	public Evidence () {}
	
	public Evidence(List<Detail> details, Long totalCount) {
		this.details  = details;
		this.totalCount = totalCount;
	}
	
	

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	public List<Detail> getDetails() {
		return details;
	}

	public void setDetails(List<Detail> details) {
		this.details = details;
	}


	
	

}

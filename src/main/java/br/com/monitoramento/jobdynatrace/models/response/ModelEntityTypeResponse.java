package br.com.monitoramento.jobdynatrace.models.response;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.OneToMany;

public class ModelEntityTypeResponse {
	private Long totalCount;
	private Long pageSize;
	@OneToMany
	List<EntityTypeResponse> types = new ArrayList<>();
	
	public ModelEntityTypeResponse() {};
	
	public ModelEntityTypeResponse(Long totalCount, Long pageSize, List<EntityTypeResponse> types) {
		this.totalCount = totalCount;
		this.pageSize = pageSize;
		this.types = types;
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
	public List<EntityTypeResponse> getTypes() {
		return types;
	}
	public void setTypes(List<EntityTypeResponse> types) {
		this.types = types;
	}
	
	
}

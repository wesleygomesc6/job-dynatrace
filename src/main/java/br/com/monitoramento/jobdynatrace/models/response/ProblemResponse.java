package br.com.monitoramento.jobdynatrace.models.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import br.com.monitoramento.jobdynatrace.models.Comment;

public class ProblemResponse {
	
	private String problemId;
	private String displayId;
	private String title;
	
	private Date startTime;
	private Date endTime;
	
	private String severityLevel;
	private String impactLevel;
	private String status;
	private Boolean validated;
	@ManyToOne
	private EntitieResponse rootCauseEntity;
	
	@ManyToMany
	@JoinTable(
			name = "affected_entities",
			joinColumns = @JoinColumn(name = "problem_id"),
			inverseJoinColumns = @JoinColumn(name = "entitie_id")
		)
		private List<EntitieResponse> affectedEntities  =  new ArrayList<>();
	
	
	@ManyToMany
	@JoinTable(
			name = "impacted_entities",
			joinColumns = @JoinColumn(name = "problem_id"),
			inverseJoinColumns = @JoinColumn(name = "entitie_id")
		)
		private List<EntitieResponse> impactedEntities =  new ArrayList<>();
	
	@ManyToOne
	private Evidence evidenceDetails;
	
	public ProblemResponse () {}
	
	public ProblemResponse (String problemId, String displayId, String title, Date startTime, Date endTime,
			String severityLevel, String impactLevel, String status, Boolean validated, EntitieResponse rootCauseEntity, 
			Comment comment, List<EntitieResponse> affectedEntities, List<EntitieResponse> impactedEntities, Evidence evidenceDetails) {
		this.problemId = problemId;
		this.displayId = displayId;
		this.title = title;
		this.startTime = startTime;
		this.endTime = endTime;
		this.severityLevel = severityLevel;
		this.impactLevel = impactLevel;
		this.status = status;
		this.validated = (validated == null) ? false : validated ;
		this.rootCauseEntity = rootCauseEntity;
		this.affectedEntities = affectedEntities;
		this.impactedEntities = impactedEntities;
		this.evidenceDetails = evidenceDetails;
	}
	

	public Evidence getEvidenceDetails() {
		return evidenceDetails;
	}

	public void setEvidenceDetails(Evidence evidenceDetails) {
		this.evidenceDetails = evidenceDetails;
	}

	public String getProblemId() {
		return problemId;
	}

	public void setProblemId(String problemId) {
		this.problemId = problemId;
	}

	public String getDisplayId() {
		return displayId;
	}

	public void setDisplayId(String displayId) {
		this.displayId = displayId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getSeverityLevel() {
		return severityLevel;
	}

	public void setSeverityLevel(String severityLevel) {
		this.severityLevel = severityLevel;
	}

	public String getImpactLevel() {
		return impactLevel;
	}

	public void setImpactLevel(String impactLevel) {
		this.impactLevel = impactLevel;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getValidated() {
		return validated;
	}

	public void setValidated(Boolean validated) {
		this.validated = validated ;
	}
	

	public EntitieResponse getRootCauseEntity() {
		return rootCauseEntity;
	}

	public void setRootCauseEntity(EntitieResponse rootCauseEntity) {
		this.rootCauseEntity = rootCauseEntity;
	}

	public List<EntitieResponse> getAffectedEntities() {
		return affectedEntities;
	}

	public void setAffectedEntities(List<EntitieResponse> affectedEntities) {
		this.affectedEntities = affectedEntities;
	}

	public List<EntitieResponse> getImpactedEntities() {
		return impactedEntities;
	}

	public void setImpactedEntities(List<EntitieResponse> impactedEntities) {
		this.impactedEntities = impactedEntities;
	}

	
	
	

}

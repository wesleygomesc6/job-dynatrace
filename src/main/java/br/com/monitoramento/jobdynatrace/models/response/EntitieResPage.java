package br.com.monitoramento.jobdynatrace.models.response;

import java.util.List;

public class EntitieResPage {
	private List<EntitieResponseSelector> entities;
	
	public EntitieResPage() {}

	public List<EntitieResponseSelector> getEntities() {
		return entities;
	}

	public void setEntities(List<EntitieResponseSelector> entities) {
		this.entities = entities;
	}
	
	

}

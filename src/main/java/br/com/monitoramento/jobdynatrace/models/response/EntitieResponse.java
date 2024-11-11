package br.com.monitoramento.jobdynatrace.models.response;

import javax.persistence.Lob;
import javax.persistence.OneToOne;

public class EntitieResponse {
	
	@OneToOne
	private EntityId entityId;
	@Lob
	private String name;

	
	public EntitieResponse () {};
	
	public EntitieResponse (EntityId entityId, String name) {
		this.entityId = entityId;
		this.name = name;
	}

	public EntityId getEntityId() {
		return entityId;
	}

	public void setEntityId(EntityId entityId) {
		this.entityId = entityId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}





}

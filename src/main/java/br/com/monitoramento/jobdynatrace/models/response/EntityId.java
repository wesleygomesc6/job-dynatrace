package br.com.monitoramento.jobdynatrace.models.response;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


public class EntityId {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;
	private String id;
	private String type;
	
	public EntityId () {}
	
	public EntityId(String id, String type) {
		this.id = id;
		this.type = type;
	}


	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

}

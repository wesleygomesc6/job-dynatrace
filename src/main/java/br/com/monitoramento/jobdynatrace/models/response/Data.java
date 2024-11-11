package br.com.monitoramento.jobdynatrace.models.response;

import java.util.List;

import javax.persistence.ManyToOne;

public class Data {
	@ManyToOne
	private List<Propertie> properties;
	
	public Data () {}
	
	public Data(List<Propertie> properties) {
		this.properties = properties;
	}

	public List<Propertie> getProperties() {
		return properties;
	}

	public void setProperties(List<Propertie> properties) {
		this.properties = properties;
	}

}

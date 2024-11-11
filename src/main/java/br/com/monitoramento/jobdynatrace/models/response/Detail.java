package br.com.monitoramento.jobdynatrace.models.response;

import javax.persistence.ManyToOne;

public class Detail {
	@ManyToOne
	private Data data;
	
	public Detail () {};
	
	public Detail (Data data) {
		this.data = data;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}



}

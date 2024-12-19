package it.exercise.java.spring.mvc.responsemodel;

public class Payload <P> {

	private String message;
	
	private String code;
	
	private P payload;
	
	public Payload() {
		// TODO Auto-generated constructor stub
	}
	
	public Payload(String message, String code, P payload) {
		super();
		this.message = message;
		this.code = code;
		this.payload = payload;
	}



	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public P getPayload() {
		return payload;
	}

	public void setPayload(P payload) {
		this.payload = payload;
	}
}


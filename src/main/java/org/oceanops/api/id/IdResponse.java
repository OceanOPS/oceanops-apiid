package org.oceanops.api.id;

public class IdResponse {
	private boolean success;
	private String message;
	private String wigosRef;
	private String ref;
	private String gtsId;
	private IdInput input;
	private String batchRequestRef;
	private String model;

	public IdResponse() {
	}
	
	public IdResponse(IdInput input, boolean success, String message, String wigosRef, String ref, String gtsId, String batchRef, String model) {
		this.input = input;
		this.success = success;
		this.message = message;
		this.wigosRef = wigosRef;
		this.ref = ref;
		this.gtsId = gtsId;
		this.batchRequestRef = batchRef;
		this.model = model;
	}

	public void setModel(String model){
		this.model = model;
	}

	public String getModel(){
		return this.model;
	}

	public String getBatchRequestRef() {
		return this.batchRequestRef;
	}

	public void setBatchRequestRef(String batchRequestRef) {
		this.batchRequestRef = batchRequestRef;
	}

	public IdInput getInput() {
		return this.input;
	}

	public void setInput(IdInput input) {
		this.input = input;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getWigosRef() {
		return wigosRef;
	}
	public void setWigosRef(String wigosRef) {
		this.wigosRef = wigosRef;
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public String getGtsId() {
		return gtsId;
	}
	public void setGtsId(String gtsid) {
		this.gtsId = gtsid;
	}

	public boolean getSuccess() {
		return this.success;		
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}

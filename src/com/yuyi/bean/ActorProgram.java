package com.yuyi.bean;

public class ActorProgram {
	
	public String actor;
	public String program;
	public String getActor() {
		return actor;
	}
	public void setActor(String actor) {
		this.actor = actor;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public ActorProgram(String actor, String program) {
		super();
		this.actor = actor;
		this.program = program;
	}
	
}

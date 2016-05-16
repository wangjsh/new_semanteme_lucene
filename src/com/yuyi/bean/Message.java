package com.yuyi.bean;

import java.util.ArrayList;

//对用户的请求返回
public class Message {
	/**
	 * 标记pid
	 */
	private Object pid = null;
	/**
	 * type 电视节目状态，包括正在播放、将要播放、已经播放等等。
	 */
	private int type = 0;
	/**
	 * 相关电视台
	 */
	private String station = "";	
	/**
	 * 电视节目名字，适用于通过节目名点播的情况
	 */
	private String program = "";
	/**
	 * 电视节目播出的时间
	 */
	private String time = "";
	/**
	 * 涉及到的演员，主要用于按照演员点播的情况下
	 */
	private String actorName = "";
	
	/**
	 * 存放演员当天播出的电视节目。如果用户不是按照演员名点播节目，则为null
	 */
	private ArrayList<String> proName = null;
	
	/**
	 * @return pid
	 */
	public Object getPid() {
		return pid;
	}
	
	/**
	 * 设置pid
	 * @param pid 
	 */
	public void setPid(Object pid) {
		this.pid = pid;
	}
	/**
	 * @return 得到电视节目播出状态
	 */
	public int getType() {
		return type;
	}
	/**
	 * @param type 设置电视节目播出状态
	 */
	public void setType(int type) {
		this.type = type;
	}
	/**
	 * @return 电视节目播出频道
	 */
	public String getStation() {
		return station;
	}
	/**
	 * @param station 电视节目播出频道
	 */
	public void setStation(String station) {
		this.station = station;
	}
	/**
	 * @return 返回电视节目名
	 */
	public String getProgram() {
		return program;
	}
	/**
	 * @param program 设置电视节目名
	 */
	public void setProgram(String program) {
		this.program = program;
	}
	/**
	 * @return 返回演员名字
	 */
	public String getActorName() {
		return actorName;
	}
	/**
	 * @param actorName 演员名字
	 */
	public void setActorName(String actorName) {
		this.actorName = actorName;
	}
	/**
	 * @return 演员演出的电视节目列表
	 */
	public ArrayList<String> getProName() {
		return proName;
	}
	/**
	 * @param proName 演员当天出演的电视节目列表
	 */
	public void setProName(ArrayList<String> proName) {
		this.proName = proName;
	}
	/**
	 * @return 返回节目播出时间
	 */
	public String getTime() {
		return time;
	}
	/**
	 * @param time 节目播出时间
	 */
	public void setTime(String time) {
		this.time = time;
	}
	

}

package com.yuyi.bean;
/**
 * @author wangjsh
 * 该类的对象对应于一条电视节目记录
 */
public class OneProgramItem {
	
	/**
	 * id 一条电视节目信息
	 */
	private int id;
	/**
	 * 用户指令字符串
	 */
	private String question;
	/**
	 * 电视节目名字
	 */
	private String answer;
    /**
     * 电视节目名字和用户指令相似度的值
     */
    private double score;
	/**
	 * 电视节目播出的时间
	 */
	private String time;
	/**
	 * 电视节目结束的时间
	 */
	private String nextTime;
	/**
	 * 播出电视节目的频道
	 */
	private String station;
	/**
	 * 电视节目的拼音
	 */
	private String pinyin;
	
	/**
	 * @return 返回id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id 要设置的id
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return 返回用户指令
	 */
	public String getQuestion() {
		return question;
	}
	/**
	 * @param question 设置用户指令
	 */
	public void setQuestion(String question) {
		this.question = question;
	}
	/**
	 * @return 返回电视节目名字
	 */
	public String getAnswer() {
		return answer;
	}
	/**
	 * @param answer 要设置的电视节目名字
	 */
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	/**
	 * @return 返回相似度
	 */
	public double getScore() {
		return score;
	}
	/**
	 * @param score 设置相似度
	 */
	public void setScore(double score) {
		this.score = score;
	}
	/**
	 * @return 返回电视节目的播出开始时间
	 */
	public String getTime() {
		return time;
	}
	/**
	 * @param time 设置电视节目的时间
	 */
	public void setTime(String time) {
		this.time = time;
	}
	/**
	 * @return 返回电视节目的结束时间
	 */
	public String getNextTime() {
		return nextTime;
	}
	/**
	 * @param nextTime 设置电视节目的结束时间
	 */
	public void setNextTime(String nextTime) {
		this.nextTime = nextTime;
	}
	/**
	 * @return 返回电视节目播出频道
	 */
	public String getStation() {
		return station;
	}
	/**
	 * @param station 设置电视节目播出频道
	 */
	public void setStation(String station) {
		this.station = station;
	}
	/**
	 * @return 返回电视节目拼音
	 */
	public String getPinyin() {
		return pinyin;
	}
	/**
	 * @param pinyin 电视节目拼音
	 */
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	
	

}

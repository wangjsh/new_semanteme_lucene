package com.yuyi.bean;
//用户发出的请求
public class WebTour {
	
	private String idStr;
	private String keyword;
    private double score;
	private String pinyin;
	
	public String getIdString() {
		return idStr;
	}
	public void setIdString(String idStr) {
		this.idStr = idStr;
	}
	public String getKeyword()
	{
		return keyword;
	}
	public void setKeyword(String keyword)
	{
		this.keyword = keyword;
	}
	public double getScore()
	{
		return score;
	}
	public void setScore(double score)
	{
		this.score = score;
	}
	public String getPinyin()
	{
		return pinyin;
	}
	public void setPinyin(String pinyin)
	{
		this.pinyin = pinyin;
	}
	
	
	

}

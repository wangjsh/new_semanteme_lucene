package com.yuyi.bean;

import org.apache.lucene.document.Document;

//用户发出的请求
public class LucDoucmentScoue {
	//lucene 的所有分数
    private double score;
    //lucene的搜索文档
	private Document document;
	public double getScore()
	{
		return score;
	}
	public void setScore(double score)
	{
		this.score = score;
	}
	public Document getDocument()
	{
		return document;
	}
	public void setDocument(Document document)
	{
		this.document = document;
	}
	
}

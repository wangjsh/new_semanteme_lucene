package com.yuyi.bean;

import java.util.ArrayList;

/**
 * @author wangjsh
 *该类对象中提供两个属性：
 *value 一位演员的名字或者一个电视节目的名字
 *mark 该名字是演员还是节目的标志
 */
public class MarkAndValue {
	/**
	 *  一位演员的名字或者一个电视节目的名字
	 */
	private ArrayList<String> value = null;
	/**
	 * 该名字是演员还是节目的标志
	 */
	private String mark;
	/**
	 * @return 返回名字
	 */
	public ArrayList<String> getValue() {
		return value;
	}
	/**
	 * @param value 设置名字
	 */
	public void setValue(ArrayList<String> value) {
		this.value = value;
	}
	/**
	 * @return 返回标志，试验员为a；是节目为p
	 */
	public String getMark() {
		return mark;
	}
	/**
	 * @param mark 设置标志
	 */
	public void setMark(String mark) {
		this.mark = mark;
	}
	/**
	 * @param value 演员或者节目名
	 * @param mark 名字的标志
	 */
	public MarkAndValue(ArrayList<String> value, String mark) {
		super();
		this.value = value;
		this.mark = mark;
	}
	
}

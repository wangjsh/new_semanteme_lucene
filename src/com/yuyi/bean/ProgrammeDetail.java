package com.yuyi.bean;

/**
 * @author wangjsh
 *
 */
public class ProgrammeDetail {
	
	/**
	 * 电视节目经过交集计算之后的相似度值
	 */
	private double value;
	/**
	 * 一个电视节目记录
	 */
	private OneProgramItem opi;
	/**
	 * 电视节目播出状态
	 */
	private long ifok;
	
	/**
	 * @return 返回value
	 */
	public double getValue() {
		return value;
	}
	/**
	 * @param value 设置value的值
	 */
	public void setValue(double value) {
		this.value = value;
	}
	
	/**
	 * @return 返回电视节目播出状态
	 */
	public long getIfok() {
		return ifok;
	}
	/**
	 * @param ifok 设置电视节目的播出状态
	 */
	public void setIfok(long ifok) {
		this.ifok = ifok;
	}
	/**
	 * @return 返回一个电视节目对象
	 */
	public OneProgramItem getUserOrder() {
		return opi;
	}
	/**
	 * @param userOrder 设置电视节目对象
	 */
	public void setUserOrder(OneProgramItem userOrder) {
		this.opi = userOrder;
	}
	
}

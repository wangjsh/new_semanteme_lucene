package com.yuyi.bean;

import java.util.ArrayList;

/**
 * @author wangjsh
 *该类对象为对应用户指令的电视节目名字列表，和演员名字；如果判断出用户基于演员名字点播电视节目，则该类对象中actorName不为空（前提是该演员名字满足阈值），该对象proNames属性存放电视节目名列表为该演员当天要播出的节目名字；如果actorName属性为空，则证明按照电视节目名字点播，节目列表名字为用户想要点播的电视节目名字
 */
public class ProNamesAndActorName {
/**
 * 演员名字
 */
public String actorName = null;
/**
 * 节目名字列表
 */
public ArrayList<String> proNames = null;
public String getActorName() {
	return actorName;
}
public void setActorName(String actorName) {
	this.actorName = actorName;
}
public ArrayList<String> getProNames() {
	return proNames;
}
public void setProNames(ArrayList<String> proNames) {
	this.proNames = proNames;
}

}

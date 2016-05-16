package com.yuyi.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lacom.lzu.edu.consts.Consts;
import lacom.lzu.edu.luceneindex.CreateIndex;
import lacom.lzu.edu.vo.OtpRespondMsg;
import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;

import com.yuyi.bean.Message;
import com.yuyi.bean.ProgrammeDetail;
import com.yuyi.bean.OneProgramItem;
import com.yuyi.bean.ProNamesAndActorName;
import com.yuyi.bean.WebTour;
import com.yuyi.doxml.GetConfig;
import com.yuyi.lucenedemo.SearchLogic;
import com.yuyi.lucenedemo.SearchTVProgram;
import com.yuyi.lucenedemo.SearchWebTour;

public class Compute
{

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		//String question = "我要看新闻联播";
		//String question = "西藏新闻联播";
		//String question = "我要看焦点访谈。";
		//String question = "我要看刘德华的节目。";
		//String question = "我要看邓超的节目。";
		//String question = "我要看赵丽颖的节目。";
		//String question = "我要看奔跑吧兄弟。";
		String question = "我要看谷智鑫的节目。";
		Message msg = returnMessage(question,GetConfig.mark);
		OtpRespondMsg opm = new OtpRespondMsg(msg);
		System.out.println(opm.getmType() +"--->"+ opm.getmInfomation());
		
 	}
	
	/**
	 * 返回对用户请求的反馈
	 * @param question 用户文本指令
	 * @param mark 检索方式，用拼音，还是汉字
	 * @return 返回一个Message对象
	 */
	public static Message returnMessage(String question, int mark)
	{
		Message message = null;
		ProNamesAndActorName proNamesAndActorName = null;
		SearchTVProgram searchTVProgram = new SearchTVProgram();
		try
		{
			proNamesAndActorName = searchTVProgram.getResult(question, mark);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		if(proNamesAndActorName == null)
			return null;
		//得到节目名字列表
		ArrayList<String> proNames = proNamesAndActorName.getProNames();
		if (proNames == null || proNames.size() <= 0)
			return null;
		message = new Message();
		String actorName = proNamesAndActorName.getActorName();
		message.setActorName(actorName);
		if (proNames.size() > 1)
		{	//电视节目不仅仅有一个。存在两种情况：1.通过节目名点播时，没有弄明白用户想看哪一个节目；2.通过演员名点播时，该演员有多个节目播出
			message.setPid(new Object());
			message.setType(Consts.QUESTION);
			message.setProName(proNames);
		} else
		{
			CreateIndex createIndex = new CreateIndex();
			ArrayList<OneProgramItem> proResults = null;
			proResults = createIndex.getProResult(proNames.get(0));
			ArrayList<ProgrammeDetail> al = null;
			if (actorName == null)//此时按照节目点播
				al = new Compute().getProgramOfDetails(proResults, question, mark);
			else//按照演员点播
				al = new Compute().getProgramOfDetails(proResults, proNames.get(0), mark);
			if (al == null || al.size() == 0)
				return null;
			ProgrammeDetail v = al.get(0);
			OneProgramItem userOrder = v.getUserOrder();
			if (v.getIfok() == 0)
			{
				// 给出正在播出这个节目的信息
				message.setPid(new Object());
				message.setType(Consts.PLAYING);
				message.setProgram(userOrder.getQuestion());
				message.setStation(userOrder.getStation());
			} else if (v.getIfok() > 0)
			{
				//给出离播放这个节目最近的电视台的信息
				message.setPid(new Object());
				message.setType(Consts.COMING);
				message.setProgram(userOrder.getQuestion());
				message.setStation(userOrder.getStation());
				message.setTime(userOrder.getTime());
			} else if (v.getIfok() < 0)
			{
				// 给出已经播完这个节目的信息
				message.setPid(new Object());
				message.setType(Consts.OVER);
				message.setProgram(userOrder.getQuestion());
				message.setStation(userOrder.getStation());
			}
		}
		
		return message;
	}
	
	/**
	 * @param question 要检索的字符串；用户指令字符串
	 * @param mark 检索方式；拼音or汉字
	 * @return 返回一个Message对象
	 */
	public static Message returnWebTour(String question, int mark)
	{
		List<WebTour> result;
		SearchWebTour searchWebTour = new SearchWebTour();
		result = searchWebTour.getResult(question, mark);
		Message message = new Message();
		StringBuffer stringBuffer = new StringBuffer();
		if (result == null || result.size() == 0)
			return null;
		Iterator<WebTour> iterator = result.iterator();
		//得分
		double scord = 0;
		//将汉字转化为拼音
		String pinyinstr = PinyinHelper.convertToPinyinString(question, " ", PinyinFormat.WITHOUT_TONE);
		while (iterator.hasNext())
		{
			WebTour webTour = iterator.next();
			if (mark == 0)
				scord = getValue(webTour.getKeyword(), question, "");//计算汉字和目标的分值
			else
				scord = getValue(webTour.getPinyin(), pinyinstr);//计算拼音和目标拼音的分值
			if (scord >= GetConfig.yuzhi)
			{
				stringBuffer.append(webTour.getIdString());
				stringBuffer.append(",");
			}
		}
		if(stringBuffer.length() == 0)
			return null;
		else {
			stringBuffer.deleteCharAt(stringBuffer.length()-1);
			message.setType(Consts.TOURS);
			message.setProgram(stringBuffer.toString());
			return message;
		}

	}

	
	/**
	 * 返回字符串之间的相似度
	 * @param des 和用户指令字符串比较的目的字符串
	 * @param qus 用户的指令字符串
	 * @param str 无实际意义，为了对getValue方法的重载
	 * @return 字符串相似度数值
	 */
	public static float getValue(String des, String qus, String str)
	{
		ArrayList<String> al = new ArrayList<String>();
		int deslen = des.length();
		int quslen = qus.length();
		int num = 0;
		float result = 0;
		for (int i = 0; i < quslen; i++)
		{
			for (int j = 0; j < deslen; j++)
			{
				if (qus.charAt(i) == des.charAt(j))
				{
					int flag = 0;
					for (int k = 0; k < al.size(); k++)
					{
						if ((qus.charAt(i) + "").equals(al.get(k)))
						{
							flag = 1;
							break;
						}
					}
					if (flag == 0)
					{
						num ++;
						al.add(qus.charAt(i) + "");
					}
				}
			}
		}
		result = (float) num / deslen;
		return result;
	}

	
	/**
	 * 求字符串的相似度（交集）------转化为拼音
	 * @param des 和用户指令字符串比较的目的字符串
	 * @param qus 用户的指令字符串
	 * @return 字符串相似度数值
	 */
	public static float getValue(String des, String qus)
	{
		ArrayList<String> al = new ArrayList<String>();
		String[] dess = des.split(" ");
		String[] quss = qus.split(" ");
		int deslen = dess.length;
		int quslen = quss.length;
		int num = 0;
		float result = 0;
		for (int i = 0; i < quslen; i++)
		{
			for (int j = 0; j < deslen; j++)
			{
				if (quss[i].equals(dess[j]))
				{
					int flag = 0;
					for (int k = 0; k < al.size(); k++)
					{
						if ((quss[i]).equals(al.get(k)))
						{
							flag = 1;
							break;
						}
					}
					if (flag == 0)
					{
						num++;
						al.add(quss[i]);
					}
				}
			}
		}
		result = (float) num / deslen;
		return result;
	}

	/**
	 * 得到节目的详细信息，对得到的电视节目列表进一步处理，处理此节目播出状态
	 * @param result 电视节目信息列表
	 * @param qus 用户的文本指令
	 * @param mark 检索方式，拼音还是汉字
	 * @return 电视节目详细信息列表
	 */
	public  ArrayList<ProgrammeDetail> getProgramOfDetails(List<OneProgramItem> result, String qus, int mark)
	{
		//System.out.println("getProgramOfDetails"+qus);
		String pinyinstr = PinyinHelper.convertToPinyinString(qus, " ", PinyinFormat.WITHOUT_TONE);
		HashMap<String, ProgrammeDetail> hm = new HashMap<String, ProgrammeDetail>();
		for (int i = 0; i < result.size(); i++)
		{
			OneProgramItem r = result.get(i);
			if ("".equals(r.getNextTime()) || r.getNextTime() == null)
				r.setNextTime("23:59");
			if (hm.containsKey(r.getQuestion()) && hm.get(r.getQuestion()).getIfok() == 0)
				continue;
			ProgrammeDetail valueAndList = new ProgrammeDetail();
			valueAndList.setUserOrder(r);
			setIfok(valueAndList);
			if (hm.containsKey(r.getQuestion()))
			{
				ProgrammeDetail v = hm.get(r.getQuestion());
				double value = v.getValue();
				valueAndList.setValue(value);
				if (valueAndList.getIfok() == 0)
				{
					hm.get(r.getQuestion()).setIfok(0);
					hm.replace(r.getQuestion(), valueAndList);
				} else if (valueAndList.getIfok() > 0)
				{
					if (v.getIfok() < 0 || v.getIfok() > valueAndList.getIfok())
					{
						hm.get(r.getQuestion()).setIfok(valueAndList.getIfok());
						hm.replace(r.getQuestion(), valueAndList);
					}
				} else if (valueAndList.getIfok() < 0)
				{
					if (v.getIfok() < 0 && v.getIfok() < valueAndList.getIfok())
					{
						hm.get(r.getQuestion()).setIfok(valueAndList.getIfok());
						hm.replace(r.getQuestion(), valueAndList);
					}
				}
			} else
			{
				double value = 0;
				if (mark == 0)
				{
					value = getValue(r.getQuestion(), qus, "");
				} else if (mark == 1)
				{
					value = getValue(r.getPinyin(), pinyinstr);
				}
				valueAndList.setValue(value);
				//在这里设置阈值
				if (value >= GetConfig.yuzhi)
				{
					hm.put(r.getQuestion(), valueAndList);
				}

			}
		}
		ArrayList<ProgrammeDetail> al = new ArrayList<ProgrammeDetail>();
		Iterator<?> iter = hm.entrySet().iterator();
		while (iter.hasNext())
		{
			Map.Entry entry = (Map.Entry) iter.next();
			// Object key=entry.getKey();
			ProgrammeDetail val = (ProgrammeDetail) entry.getValue();
			al.add(val);
		}
		//对列表再次排序
		sortByJiao(al);
		return al;

	}

	/**
	 * 根据系统时间，给不同时间播放的节目按照时间填充ifok属性。节目正在播出，应经播完，还是将要播出
	 * @param v 需要处理的电视节目
	 */
	public static void setIfok(ProgrammeDetail v)
	{
		OneProgramItem r = v.getUserOrder();
		long start = SearchLogic.timeTolong(r.getTime());
		long end = SearchLogic.timeTolong(r.getNextTime());
		long now = SearchLogic.timeTolong("");
		if (start <= now && now <= end)
		{
			// 正在播出这个节目
			v.setIfok(0);
		} else if (now < start)
		{
			// 将要播放这个节目
			v.setIfok(start - now);
		} else if (now > end)
		{
			// 已经播完这个节目
			v.setIfok(end - now);
		}
	}

	/**
	 * 对结果进行取交集之后，按照相似度大小排序
	 * @param list 要排序的节目信息
	 */
	public static void sortByJiao(ArrayList<ProgrammeDetail> list)
	{
		int size = list.size();
		for (int i = 0; i < size - 1; i ++)
		{
			for (int j = i + 1; j < size; j ++)
			{
				ProgrammeDetail r = list.get(i);
				ProgrammeDetail k = list.get(j);
				if (r.getValue() < k.getValue())
				{
					list.set(i, k);
					list.set(j, r);
				}
			}
		}
	}

}

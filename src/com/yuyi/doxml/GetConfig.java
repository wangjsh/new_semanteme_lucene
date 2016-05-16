package com.yuyi.doxml;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * @author wangjsh
 * 得到配置阈值以及是否按照拼音的配置
 */
public class GetConfig {
	
	public static double yuzhi = 0;
	public static int mark = 0;

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		System.out.println(yuzhi);
		System.out.println(mark);

	}
	static
	{
		SAXBuilder builder=new SAXBuilder();
		Document document=null;
		Element root=null;
		Element yuzhiele=null;
		Element config_pinyin=null;
		try
		{
			document=builder.build("file/config.xml");
			root=document.getRootElement();
			yuzhiele=root.getChild("config_yuzhi");
			config_pinyin=root.getChild("config_pinyin");
			if(yuzhiele.getValue()==null||yuzhiele.getValue().equals(""))
			{
				yuzhi=0.0;
			}else
			{
				yuzhi= Double.parseDouble(yuzhiele.getValue());
			}
			if(config_pinyin.getValue()==null||config_pinyin.getValue().equals(""))
			{
				mark=0;
			}else
			{
				mark=Integer.parseInt(config_pinyin.getValue());
			}
			
		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}

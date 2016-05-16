package com.yuyi.doxml;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class GetRegExp {
	
	public static List<String> result=null;
	@SuppressWarnings("unchecked")
	public static List<String> createList()
	{
		if(result!=null)
		{
			return result;
		}else
		{
			result=new ArrayList<String>();
			SAXBuilder builder=new SAXBuilder();
			Document document=null;
			Element root=null;
			List<Element> list=null;
			try {
				document=builder.build("file/zhangyehuachen.xml");
				root=document.getRootElement();
				list=root.getChildren();
				for(Element e:list)
				{
					result.add(e.getAttributeValue("regExp"));
				}
				
			} catch (Exception e)
			{
				// TODO: handle exception
				e.printStackTrace();
			}
			return result;
		}
	}
	
	public static void main(String[] args)
	{
		String question="";
		System.out.println(ifMatchRegExp(question));
	}

	public static String ifMatchRegExp(String question)
	{
		String b=null;
		List<String> result=GetRegExp.createList();
		Pattern pattern=null;
		Matcher matcher=null;
		for(String s:result)
		{
			pattern=Pattern.compile(s);
			matcher=pattern.matcher(question);
			if(matcher.find())
			{
				System.out.println(s);
				b=s;
				break;
			}
			
		}
		return b;
	}

}

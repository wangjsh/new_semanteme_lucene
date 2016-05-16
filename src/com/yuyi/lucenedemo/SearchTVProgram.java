package com.yuyi.lucenedemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;
import com.yuyi.bean.MarkAndValue;
import com.yuyi.bean.OneProgramItem;
import com.yuyi.bean.ProNamesAndActorName;
import com.yuyi.doxml.GetConfig;
import com.yuyi.utils.Compute;

import lacom.lzu.edu.luceneindex.CreateIndex;

public class SearchTVProgram
{
	/**
	 * Lucene打分最高的记录数目
	 */
	private static int TOPMOST = 20;
	/**
	 * Lucene检索器
	 */
	private IndexSearcher searcher = null;
	/**
	 * 电视节目单索引文件名字
	 */
	private static String searchMenu = null;
	/**
	 * 当天演员和其对应的电视节目建立索引文件的名字
	 */
	private static String aToP = null;
	/**
	 * 当天播出的电视节目名字和当天有节目的演员的名字，建立的索引文件名
	 */
	private static String uniqueAP = null;
	
	static
	{
		Properties prop = new Properties();
		InputStream in = null;
		try
		{
			in = new FileInputStream("jdbc.properties");
			prop.load(in);
			searchMenu = prop.getProperty("indexpath").trim();
			uniqueAP = prop.getProperty("uniqueAP").trim();
			aToP = prop.getProperty("aToP").trim();
			TOPMOST  = Integer.parseInt(prop.getProperty("TOPMOST").trim());
			in.close();
		} catch (IOException e)
		{
			e.printStackTrace();
			
		} finally
		{
			try
			{
				if (in != null)
					in.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 该方法的作用是判断用户的指令是演员还是电视节目
	 * @param queryStr 用户文本指令字符串
	 * @param mark 检索的方式；通过拼音检索，还是通过汉字检索
	 * @param path 索引路径
	 * @return 返回一个MarkAndValue对象。对象包括两个属性：名字（演员或者节目）、以及名字的标志
	 * @throws Exception 索引操作可能的异常
	 */
	public MarkAndValue getResultActorOrProgram(String queryStr,int mark, String path) throws Exception
	{
		String pinyinOfqueryStr = PinyinHelper.convertToPinyinString(queryStr, " ", PinyinFormat.WITHOUT_TONE);
		MarkAndValue markAndValue = null;
		try
		{
			TopDocs topDocs = this.search(queryStr,mark, path);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			 //下面的for循环仅仅为打印信息。
			System.out.println("************");
			for(int i = 0; i < scoreDocs.length; ++ i)
			{
				int docId = scoreDocs[i].doc;
				float score=scoreDocs[i].score;
				Document doc = searcher.doc(docId);
				String question = doc.get("question");
				String aOrP = doc.get("mark");
				System.out.println("方法getResultAOrP打印："+ score+ ", "+ question +", "+ aOrP);
			}
			System.out.println("************");
			for(int i = 0; i < scoreDocs.length; ++ i)
			{
				int docId = scoreDocs[i].doc;
				Document doc = searcher.doc(docId);
				String question = doc.get("question");
				String pinyinOfQuestion = doc.get("pinyin");
				float value = 0;
				if (mark == 0)
				{
					value = Compute.getValue(question, queryStr, "");
				} else if (mark == 1)
				{
					value = Compute.getValue(pinyinOfQuestion, pinyinOfqueryStr);
				}
				scoreDocs[i].score = value;
				
			}
			//******按照分值排序
			for(int i = 0; i < scoreDocs.length - 1; ++ i)
			{
				for(int j = i + 1; j < scoreDocs.length; ++ j)
				{
					ScoreDoc ei = scoreDocs[i];
					ScoreDoc ej = scoreDocs[j];
					if(ei.score < ej.score)
					{
						scoreDocs[i] = ej;
						scoreDocs[j] = ei;
					}
				}
			}
			 //下面的for循环仅仅为打印信息。
			System.out.println("************");
			for(int i = 0; i < scoreDocs.length; ++ i)
			{
				int docId = scoreDocs[i].doc;
				float score=scoreDocs[i].score;
				Document doc = searcher.doc(docId);
				String question = doc.get("question");
				String aOrP = doc.get("mark");
				System.out.println("方法getResultAOrP打印："+ score+ ", "+ question +", "+ aOrP);
			}
			System.out.println("************");
			int docId = scoreDocs[0].doc;
			float score=scoreDocs[0].score;
			Document doc = searcher.doc(docId);
			String value = doc.get("question");
			String aOrP = doc.get("mark");
			ArrayList<String> values = new ArrayList<>();
			if(aOrP.equals("p"))
			{
				//如果是节目，看看下面若干个节目是不是打分相等；提示用户，到底想要看哪一个节目
				for(int i = 0; i < scoreDocs.length; ++ i)
				{
					int _docId = scoreDocs[i].doc;
					float _score = scoreDocs[i].score;
					Document _doc = searcher.doc(_docId);
					String _value = _doc.get("question");
					String _mark = _doc.get("mark");
					if(_score >= GetConfig.yuzhi && _score >= score && _mark.equals(aOrP))
					{
						values.add(_value);
					}
					else
					{
						break;
					}
					
				}
				
			}
			else
			{
				// 如果是演员，只考虑第一个演员即可，但是其评分需要不小于阈值
				if(score >= GetConfig.yuzhi)
					values.add(value);
			}
			if(values.size() > 0)
				markAndValue = new MarkAndValue(values, aOrP);
			else
				markAndValue = null;
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
		}
		//System.out.println("方法getResultAOrP打印：最终结果是"+ markAndValue.getMark()+ "," +markAndValue.getValue());
		return markAndValue;
	}
	/**
	 * 该方法返回一个演员当天出演的电视节目（一个或者多个）组成的字符串，此方法中，对于每一个演员,处理排名最高几个电视节目（如果评分最高的节目数目 多于 1）
	 * @param queryStr 要检索的字符串。用户的文本指令
	 * @param mark 检索方式；拼音or汉字
	 * @param path 索引文件路径
	 * @return 字符串，用户当天的所有电视节目组成的字符串
	 * @throws Exception 索引操作可能的异常
	 */
	public ArrayList<String> getProgramsOfActor(String queryStr,int mark, String path) throws Exception
	{
		ArrayList<String> proNames = new ArrayList<>();
		try
		{
			TopDocs topDocs = this.search(queryStr,mark, path);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			float flag = scoreDocs[0].score;
			for (int i = 0; i < scoreDocs.length; ++ i)
			{
				int docId = scoreDocs[i].doc;
				float score = scoreDocs[i].score;
				Document doc = searcher.doc(docId);
				String program = doc.get("program");
				//System.out.println("方法getProgramsOfActor打印内容："+ score+ ",,," +program);
				if(score >= flag)
					proNames.add(program);
					
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
		}
		//System.out.println("方法getProgramsOfActor打印信息："+ result_program);
		return proNames;
	}
	/**通过检索给出一个相似度最高的电视节目列表
	 * @param queryStr 用户指令，需要检索的字符串
	 * @param mark 检索方式，拼音还是汉字
	 * @param path 索引文件路径
	 * @return 返回相似度最高的电视节目列表
	 * @throws Exception 索引操作异常
	 */
	public List<OneProgramItem> getProResult(String queryStr,int mark, String path) throws Exception
	{
		List<OneProgramItem> result = null;
		try
		{
			TopDocs topDocs = this.search(queryStr,mark, path);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			result = new ArrayList<OneProgramItem>();
			OneProgramItem bean = null;
			for (int i = 0; i < scoreDocs.length; i++)
			{
				int docId = scoreDocs[i].doc;
				float score=scoreDocs[i].score;
				Document doc = searcher.doc(docId);
				bean = new OneProgramItem();
				bean.setId(Integer.parseInt(doc.get("id")));
				bean.setQuestion(doc.get("question"));
				bean.setScore(score);
				bean.setTime(doc.get("time"));
				bean.setNextTime(doc.get("nexttime"));
				bean.setStation(doc.get("channel"));
				bean.setPinyin(doc.get("pinyin"));
				result.add(bean);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
		}
		return result;
	}
	
	/**在索引文件中检索到评分最高的文档
	 * @param queryStr 需要检索的字符串
	 * @param mark 检所方式；拼音还是汉字
	 * @param path 索引文件路径
	 * @return 相似度最高的文档
	 * @throws Exception 索引操作异常
	 */
	private TopDocs search(String queryStr,int mark, String path) throws Exception
	{
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(path)));
		searcher = new IndexSearcher(reader);
		searcher.setSimilarity(new DefaultSimilarity());
		QueryParser parser=null;
		Query query=null;
		if(mark == 0)
		{
			parser = new QueryParser("question", new IKAnalyzer());
			query = parser.parse(queryStr);
		}else if(mark == 1)
		{
			parser = new QueryParser("pinyin", new IKAnalyzer());
			String pinyin=PinyinHelper.convertToPinyinString(queryStr, " ", PinyinFormat.WITHOUT_TONE);
			query = parser.parse(pinyin);
		}
		TopDocs topDocs = searcher.search(query, TOPMOST);
		return topDocs;
	}
	
	public static void main(String[] args)
	{
		String question = "我要看奔跑吧兄弟。";
		//String question = "我要看陆贞传奇。";
		new SearchTVProgram().getResult(question, 1);
	}
	/**该方法的作用是返回一个ProNamesAndActorName对象。对象属性包括：电视节目列表，演员名字（如果用户根据演员名字检索，负责为null）
	 * @param question 需要检索的字符串
	 * @param mark 检索方式，拼音or汉字
	 * @return 返回一个ProgramItemListAndActorName对象
	 */
	public ProNamesAndActorName getResult(String question, int mark) 
	{
		ProNamesAndActorName proNamesAndActorName = null;
		SearchTVProgram logic = new SearchTVProgram();
		MarkAndValue markAndValue = null;
		//在这里将question转化为string，是为了和以前的代码衔接.
		try
		{
			markAndValue = logic.getResultActorOrProgram(question,mark, uniqueAP);
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		if(markAndValue != null)
		{
			proNamesAndActorName = new ProNamesAndActorName();
			if(markAndValue.getMark().equals("p"))
			{
				try 
				{
					ArrayList<String> proNames = markAndValue.getValue();
					proNamesAndActorName.setProNames(proNames);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else
			{
				//演员名字取出
				String actorName = markAndValue.getValue().get(0);
				proNamesAndActorName.setActorName(actorName);
				try
				{
					ArrayList<String> proNames = logic.getProgramsOfActor(actorName, mark, aToP);
					proNamesAndActorName.setProNames(proNames);
				} catch (Exception e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return proNamesAndActorName;
	}
	
}
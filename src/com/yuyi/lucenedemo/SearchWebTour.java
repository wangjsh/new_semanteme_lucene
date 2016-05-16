package com.yuyi.lucenedemo;

import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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

import com.yuyi.bean.LucDoucmentScoue;
import com.yuyi.bean.WebTour;

//按索引查找
public class SearchWebTour
{
	private static final int TOPMOST = 20;
	private static String searchDir = "lucene_webtour";
	private static IndexSearcher searcher = null;

	// private int maxBufferedDocs = 500;

	// mark==0则直接查找，mark==1则需要转化为拼音
	public List<WebTour> getResult(String queryStr, int mark)
	{
		List<WebTour> result = null;
		try
		{
			SearchLogic searchLogic = new SearchLogic(SearchLogic.INDEXWEBTOURS);
			result = addHits2List(searchLogic.search(queryStr, mark));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	private List<WebTour> addHits2List(List<LucDoucmentScoue> list)
	{
		List<WebTour> listBean = new ArrayList<WebTour>();
		WebTour bean = null;
		Iterator<LucDoucmentScoue> iterator = list.iterator();
		Document doc = null;
		while (iterator.hasNext())
		{
			LucDoucmentScoue ds = iterator.next();
			doc = ds.getDocument();
			bean = new WebTour();
			bean.setIdString(doc.get("merc_id"));
			bean.setKeyword(doc.get("question"));
			bean.setScore(ds.getScore());
			bean.setPinyin(doc.get("pinyin"));
			listBean.add(bean);
		}
		return listBean;
	}

}
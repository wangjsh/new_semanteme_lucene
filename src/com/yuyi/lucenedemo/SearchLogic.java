package com.yuyi.lucenedemo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.yuyi.bean.OneProgramItem;

//鎸夌储寮曟煡鎵�
public class SearchLogic
{
	//private static final int TOPMOST = 20;
	private static final int TOPMOST = 60;
	private static IndexSearcher indexTVProgram = null;
	private static IndexSearcher indexWebTours = null;
	public static final String INDEXTVPROGROM = "lucene_index";
	public static final int TYPETV = 1;
	public static final String INDEXWEBTOURS = "lucene_webtours";
	public static final int TYPEWEB = 2;
	// 璇存槑鏄偅绉嶇被鍨�
	private int mType;
	private String mIndexFile;
	private IndexSearcher indexSearcher = null;
	//private int maxBufferedDocs = 500;
	public SearchLogic(String indexFile)
	{
		this.mIndexFile = indexFile;
		if (INDEXTVPROGROM.equals(mIndexFile))
			mType = TYPETV;
		else
			mType = TYPEWEB;
	}

	/**
	 * 璇ュ嚱鏁版槸鍚屾鏈哄埗閿�
	 * 
	 * @param index
	 *            绱㈠紩鍚嶇О 鍒嗕负涓ょ TYPETV 鍜� TYPEWEB
	 * @throws IOException
	 */
	public synchronized static void updataIndex(int index) throws IOException
	{
		IndexReader reader;
		if (TYPETV == index)
		{
			reader = DirectoryReader.open(FSDirectory.open(new File(INDEXTVPROGROM)));
			indexTVProgram = new IndexSearcher(reader);
		} else
		{
			reader = DirectoryReader.open(FSDirectory.open(new File(INDEXWEBTOURS)));
			indexWebTours = new IndexSearcher(reader);
		}
	}

	/**
	 * 鑾峰緱绱㈠紩鐨勫苟鏌ヨ绱㈠紩
	 * 
	 * @param queryStr
	 *            鏌ヨ瀛楃涓�
	 * @param mark
	 *            绱㈠紩閭ｄ釜鏂囦欢
	 * @return LucDoucmentScoue鐨刲ist
	 * @throws Exception
	 */
	public List<LucDoucmentScoue> search(String queryStr, int mark) throws Exception
	{
		List<LucDoucmentScoue> list = new ArrayList<LucDoucmentScoue>();
		if (indexTVProgram == null)
			updataIndex(TYPETV);
		if (indexWebTours == null)
			updataIndex(TYPEWEB);
		if (mType == TYPETV)
			indexSearcher = indexTVProgram;
		else
			indexSearcher = indexWebTours;
		indexSearcher.setSimilarity(new DefaultSimilarity());
		QueryParser parser = null;
		Query query = null;
		if (mark == 0)
		{
			parser = new QueryParser("question", new IKAnalyzer());
			query = parser.parse(queryStr);
		} else if (mark == 1)
		{
			parser = new QueryParser("pinyin", new IKAnalyzer());
			String pinyin = PinyinHelper.convertToPinyinString(queryStr, " ", PinyinFormat.WITHOUT_TONE);
			query = parser.parse(pinyin);
		}
		ScoreDoc[] scoreDocs = indexSearcher.search(query, TOPMOST).scoreDocs;
		LucDoucmentScoue doucmentScoue;
		int docId = 0;
		for (int k = 0; k < scoreDocs.length; k++)
		{
			doucmentScoue = new LucDoucmentScoue();
			docId = scoreDocs[k].doc;
			doucmentScoue.setScore(scoreDocs[k].score);
			doucmentScoue.setDocument(indexSearcher.doc(docId));
			list.add(doucmentScoue);
		}
		return list;
	}

	public static long timeTolong(String dateTime)
	{
		// String dateTime="9:00:00";
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");//这里注意抓取网站数据中，时间格式
		if (dateTime.equals(""))
		{
			dateTime = formatter.format(new Date());
		}
		Calendar c = Calendar.getInstance();
		try
		{
			c.setTime(formatter.parse(dateTime));

		} catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long time = c.getTimeInMillis();
		// System.out.println("鏃堕棿杞寲鍚庣殑姣鏁颁负锛�"+time);
		return time;
	}

}
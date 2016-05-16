package lacom.lzu.edu.luceneindex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;
import com.yuyi.bean.ActorProgram;
import com.yuyi.bean.OneProgramItem;

public class CreateIndex
{
	
	private Connection conn = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;
	private String url = null;
	private String user = null;
	private String passwd = null;
	private File indexFile = null;
	private Analyzer analyzer = null;
//	private String ip = null;
//	private String port = null;
	private String indexpath = null;
	private String aToP = null;
	private String uniqueAP = null;
	private String tvMenu = null;//电视节目单表名
	private String tvProgram = null;//电视剧节目表名
	private String tvZongyi = null;
	
	static
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close()
	{
		try
		{
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	/**读取配置文件数据
	 * @return 是否正确读取
	 */
	private boolean connnection()
	{
		Properties prop = new Properties();
		InputStream in = null;
		try
		{
			in = new FileInputStream("jdbc.properties");
			prop.load(in);
			url = prop.getProperty("url").trim();
			user = prop.getProperty("username").trim();
			passwd = prop.getProperty("password").trim();
			indexpath = prop.getProperty("indexpath").trim();
			uniqueAP = prop.getProperty("uniqueAP").trim();
			aToP = prop.getProperty("aToP").trim();
//			ip = prop.getProperty("ip").trim();
//			port = prop.getProperty("port").trim();
			tvMenu = prop.getProperty("tvMenu").trim();
			tvProgram = prop.getProperty("tvProgram").trim();
			tvZongyi = prop.getProperty("tvZongYi").trim();
			in.close();
			return true;
		} catch (IOException e)
		{
			e.printStackTrace();
			return false;
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
	public static void main(String[] args)
	{
		new CreateIndex().createIndex();
	}
	/**
	 * 创建索引文件
	 */
	public void createIndex()
	{
		boolean isConn = connnection();
		if (!isConn)
		{
			System.out.println("打开配置文档错误");
			return;
		}
		createIndexsLogic();
	}
	/**
	 * 创建索引文件方法
	 */
	private void createIndexsLogic()
	{
		//得到当天不重复的要播放的电视节目
		HashMap<String, Integer> pHm= getUniqueProgram();
		//得到当天电视节目中		演员--->电视节目
		ArrayList<com.yuyi.bean.ActorProgram> apl = getActorToPro(pHm);
		//得到当天不重复的演员
		HashMap<?, ?> aHm = getUniqueActor(apl);
		//下面是创建三个索引文件
		//创建电视节目单索引文件
		//createMenuIndex();
		//创建当天播出的节目和演员索引
		createUniqueAPIndex(aHm, pHm);
		//创建当天演员对应的播出的电视节目索引
		createAToPIndex(apl);
		System.out.println("更新索引了");
		System.out.println("apl的长度是："+ apl.size());
		for(int i = 0; i < apl.size(); ++ i)
		{
			com.yuyi.bean.ActorProgram ap = apl.get(i);
			System.out.println(ap.getActor() +"------"+ ap.getProgram());
		}
			
	}
	
	/**创建当天播出的节目和演员索引
	 * @param aHm 演员字典
	 * @param pHm 节目字典
	 */
	public void createUniqueAPIndex(HashMap<?, ?> aHm, HashMap<?, ?> pHm)
	{
		Directory directory = null;
		IndexWriter indexWriter = null;
		try
		{
			indexFile = new File(uniqueAP);
			directory = FSDirectory.open(indexFile);
			analyzer = new IKAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
			iwc.setOpenMode(OpenMode.CREATE);
			indexWriter = new IndexWriter(directory, iwc);
			// indexWriter.setMaxBufferedDocs(maxBufferedDocs);
			Document doc = null;
			Iterator<?> iter = aHm.entrySet().iterator();
			while(iter.hasNext())
 			{
 				Map.Entry entry = (Map.Entry) iter.next();
 				String key = (String) entry.getKey();
				doc = new Document();
				Field content = null;
				Field pinyin=null;
				Field mark = null;
				content = new TextField("question", key, Field.Store.YES);
				pinyin=new TextField("pinyin", PinyinHelper.convertToPinyinString(key, " ", PinyinFormat.WITHOUT_TONE), Field.Store.YES);
				mark = new StringField("mark", "a", Field.Store.YES);
				doc.add(content);
				doc.add(pinyin);
				doc.add(mark);
				indexWriter.addDocument(doc);
			}
			iter = pHm.entrySet().iterator();
			while(iter.hasNext())
 			{
 				Map.Entry entry = (Map.Entry) iter.next();
 				String key = (String) entry.getKey();
				doc = new Document();
				Field content = null;
				Field pinyin=null;
				Field mark = null;
				content = new TextField("question", key, Field.Store.YES);
				pinyin=new TextField("pinyin", PinyinHelper.convertToPinyinString(key, " ", PinyinFormat.WITHOUT_TONE), Field.Store.YES);
				mark = new StringField("mark", "p", Field.Store.YES);
				doc.add(content);
				doc.add(pinyin);
				doc.add(mark);
				indexWriter.addDocument(doc);
			}
			indexWriter.commit();
			indexWriter.close();
		} catch (Exception e)
		{
			System.out.println("createUniqueAPIndex ...... wrong");
			e.printStackTrace();
		}
	}
	/**创建当天演员对应的播出的电视节目索引
	 * @param apl 演员对应的电视节目列表
	 */
	public void createAToPIndex(ArrayList<ActorProgram> apl)
	{
		Directory directory = null;
		IndexWriter indexWriter = null;
		try
		{
			File indexFile = new File(aToP);
			directory = FSDirectory.open(indexFile);
			analyzer = new IKAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
			iwc.setOpenMode(OpenMode.CREATE);
			indexWriter = new IndexWriter(directory, iwc);
			Document doc = null;
			for(int i = 0; i < apl.size(); ++ i)
			{
				ActorProgram ap = apl.get(i);
				doc = new Document();
				Field id=null;
				Field actor = null;
				Field program = null;
				Field pinyin=null;
				id = new StringField("id", i + "", Field.Store.YES);
				actor = new TextField("question", ap.getActor(), Field.Store.YES);
				program = new TextField("program", ap.getProgram(), Field.Store.YES);
				pinyin=new TextField("pinyin", PinyinHelper.convertToPinyinString(ap.getActor(), " ", PinyinFormat.WITHOUT_TONE), Field.Store.YES);
				doc.add(id);
				doc.add(actor);
				doc.add(pinyin);
				doc.add(program);
				indexWriter.addDocument(doc);
			}
			indexWriter.commit();
			indexWriter.close();
		} catch (Exception e)
		{
			System.out.println("createAToPIndex wrong");
			e.printStackTrace();
		}
	}
	/**
	 * 创建电视节目单索引文件
	 */
	public void createMenuIndex()
	{
		try 
		{
 			conn = DriverManager.getConnection(url, user, passwd);
			String sql = "select menu.id,time,nexttime,name,channel from "+ this.tvMenu +";";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			Directory directory = null;
			IndexWriter indexWriter = null;
			File indexFile = new File(indexpath);
			directory = FSDirectory.open(indexFile);
			analyzer = new IKAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
			iwc.setOpenMode(OpenMode.CREATE);
			indexWriter = new IndexWriter(directory, iwc);
			Document doc = null;
			while (rs.next())
			{
				doc = new Document();
				Field id=null;
				Field question=null;
				Field time=null;
				Field nextTime=null;
				Field channel=null;
				Field pinyin=null;
				String str=rs.getString("name") == null ? "" : rs.getString("name");
				id = new StringField("id", rs.getInt("id")+"", Field.Store.YES);
				question = new TextField("question",str, Field.Store.YES);
				time= new StringField("time", rs.getString("Time")==null?"":rs.getString("Time"), Field.Store.YES);
				nextTime= new StringField("nexttime", rs.getString("Nexttime")==null?"":rs.getString("Nexttime"), Field.Store.YES);
				channel= new StringField("channel", rs.getString("channel"), Field.Store.YES);
				pinyin=new TextField("pinyin", PinyinHelper.convertToPinyinString(str, " ", PinyinFormat.WITHOUT_TONE), Field.Store.YES);
				doc.add(id);
				doc.add(question);
				doc.add(time);
				doc.add(nextTime);
				doc.add(channel);
				doc.add(pinyin);
				indexWriter.addDocument(doc);
			}
			indexWriter.commit();
			indexWriter.close();
		} catch (Exception e) 
		{
			// TODO Auto-generated catch block
			System.out.println("createMenuIndex ...... wrong");
			e.printStackTrace();
		}finally
		{
			this.close();
		}
	}
	/**
	 * @return 当天播出的不重复的电视节目
	 */
	public HashMap<String, Integer> getUniqueProgram()
	{
		HashMap<String, Integer>  pHm= new HashMap<>();
		try {
 			conn = DriverManager.getConnection(url, user, passwd);
			String sql = "select name from "+ this.tvMenu +";";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next())
			{
				String pName = rs.getString("name");
				if(!pHm.containsKey(pName))
				{
					pHm.put(pName, 0);
				}
			}
			String sql2 = "select name from "+ this.tvZongyi +";";
			ps = conn.prepareStatement(sql2);
			rs = ps.executeQuery();
			while(rs.next())
			{
				String pName = rs.getString("name");
				if(!pHm.containsKey(pName))
				{
					pHm.put(pName, 0);
				}
			}
		} catch (Exception e) 
		{
			// TODO Auto-generated catch block
			System.out.println("getUniqueProgram wrong");
			e.printStackTrace();
		}finally
		{
			close();
		}
		return pHm;
	}
	/**
	 * @param proName 电视节目名字
	 * @return 电视节目信息列表
	 */
	public ArrayList<OneProgramItem> getProResult(String proName)
	{
		boolean isConn = connnection();
		if (!isConn)
		{
			System.out.println("打开配置文档错误");
			return null;
		}
		ArrayList<OneProgramItem> result = null;
		try {
 			conn = DriverManager.getConnection(url, user, passwd);
			String sql = "select time, nexttime, channel from "+ this.tvMenu +" where name = '"+ proName +"';";
			//System.out.println(sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			result = new ArrayList<OneProgramItem>();
			while(rs.next())
			{
				OneProgramItem oneProgramItem = new OneProgramItem();
				oneProgramItem.setQuestion(proName);
				oneProgramItem.setTime(rs.getString("time"));
				oneProgramItem.setNextTime(rs.getString("nexttime"));
				oneProgramItem.setStation(rs.getString("channel"));
				//生成一个节目的拼音字段；数据库中没有存储该字段；在这里生成；为了可以支持按照拼音检索；当然，如果按照演员名字点播电视节目，按照拼音的方式效果并不好；目前没有用拼音
				String pinyinOfName = PinyinHelper.convertToPinyinString(proName, " ", PinyinFormat.WITHOUT_TONE);
				oneProgramItem.setPinyin(pinyinOfName);
				result.add(oneProgramItem);
			}
		} catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			close();
		}
		return result;
	}
	/**根据当天要播放的电视节目找到和节目相关的演员
	 * @param map 演员字典，key为演员名
	 * @return ActorProgram对象列表，列表中每一个元素存放演员和其对应的节目
	 */
	public ArrayList<ActorProgram> getActorToPro(HashMap<?, ?> map)
	{
		ArrayList<ActorProgram> apList = new ArrayList<>();
		try {
 			conn = DriverManager.getConnection(url, user, passwd);
 			//处理电视剧节目
 			Iterator<?> iter = map.entrySet().iterator();
 			while(iter.hasNext())
 			{
 				Map.Entry entry = (Map.Entry) iter.next();
 				String key = (String) entry.getKey();
 				//String sql = "select zhuyan from "+this.tvProgram+" where name like '%"+key+"%'; ";
 				String sql = "select zhuyan from "+this.tvProgram+" where name = '"+key+"'; ";
 				ps = conn.prepareStatement(sql);
 				rs = ps.executeQuery();
 				//电视剧节目库中节目可能重复，处理一次即可
// 				while(rs.next())
// 				{
// 					
// 				}
 				if(rs.next())
 				{
 					String actorStr = rs.getString("zhuyan");
 					String[] actors = actorStr.split(",");
 					for(int i = 0; i < actors.length; ++ i)
 					{
 						ActorProgram ap = new ActorProgram(actors[i], key);
 						apList.add(ap);
 					}
 				}
 				
 			}
 			//处理综艺节目
 			iter = map.entrySet().iterator();
 			while(iter.hasNext())
 			{
 				Map.Entry entry = (Map.Entry) iter.next();
 				String key = (String) entry.getKey();
 				String sql = "select zhuchi from "+this.tvZongyi+" where name like '%"+key+"%'; ";
 				ps = conn.prepareStatement(sql);
 				rs = ps.executeQuery();
 				//综艺节目库中节目可能重复
 				if(rs.next())
 				{
 					String actorStr = rs.getString("zhuchi");
 					String[] actors = actorStr.split(",");
 					for(int i = 0; i < actors.length; ++ i)
 					{
 						ActorProgram ap = new ActorProgram(actors[i], key);
 						apList.add(ap);
 					}
 				}
 				
 			}

		} catch (Exception e) 
		{
			// TODO Auto-generated catch block
			System.out.println(".....getActorToPro...... wrong");
			e.printStackTrace();
		}finally
		{
			close();
		}
		
		return apList;
	}
	/**
	 * @param apl 演员节目列表
	 * @return 当天有节目的演员
	 */
	public HashMap<String, Integer> getUniqueActor(ArrayList<ActorProgram> apl)
	{
		HashMap<String, Integer>  aHm= new HashMap<>();
		for(int i = 0; i < apl.size(); ++ i)
		{
			ActorProgram ap = apl.get(i);
			String actor = ap.getActor();
			if(!aHm.containsKey(actor))
			{
				aHm.put(actor, 0);
			}
		}
		return aHm;
	}

}
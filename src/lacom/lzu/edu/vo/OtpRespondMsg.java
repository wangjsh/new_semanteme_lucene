package lacom.lzu.edu.vo;

import java.util.ArrayList;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangInt;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangString;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.yuyi.bean.Message;

import lacom.lzu.edu.consts.Consts;

/**
 * 从lucene得到结果转化为服务消息
 * 
 * @author lcq
 *
 */
public class OtpRespondMsg
{
	private int mType = Consts.ERROR;
	private String mStation = "";
	private String mInfomation = "";
	
	public int getmType() {
		return mType;
	}

	public void setmType(int mType) {
		this.mType = mType;
	}

	public String getmStation() {
		return mStation;
	}

	public void setmStation(String mStation) {
		this.mStation = mStation;
	}

	public String getmInfomation() {
		return mInfomation;
	}

	public void setmInfomation(String mInfomation) {
		this.mInfomation = mInfomation;
	}

	/**
	 * 从构造函数转化 消息
	 * 
	 * @param origInfo
	 */
	public OtpRespondMsg(Message origInfo)
	{
		//传输消息为空 直接返回错误
		if (origInfo == null)
		{
			mType = Consts.ERROR;
			return;
		}
		//处理下面结果
		mType = origInfo.getType();
		mStation = origInfo.getStation();
		String name = origInfo.getActorName();
		switch (mType)
		{
		case Consts.PLAYING:
			mInfomation = origInfo.getProgram();
			break;
		case Consts.COMING:
			if (origInfo.getTime().length() != 0)
			{
				if(name != null)
					mInfomation = String.format("您好，由%s出演的电视节目%s将于%s在%s播放", name, origInfo.getProgram(), origInfo.getTime(), origInfo.getStation());
				else
					mInfomation = String.format("您好，您点播的电视节目%s将于%s在%s播放", origInfo.getProgram(), origInfo.getTime(), origInfo.getStation());
			}
			else
				mType = Consts.ERROR;
			break;
		case Consts.OVER:
			if(name != null)
				mInfomation = String.format("您好，由%s出演的电视节目%s今天已经播放完毕，看看其他明星的节目吧！", name, origInfo.getProgram());
			else
				mInfomation = String.format("您好，您点播的电视节目%s今天已经播放完毕，换个节目试一试！", origInfo.getProgram());
			break;
		case Consts.QUESTION:
			if (origInfo.getProName().size() > 0)
			{
				ArrayList<String> proNames = origInfo.getProName();
				StringBuffer pros = new StringBuffer("");
				if(name != null)
				{
					for(int i = 0; i < proNames.size(); ++ i)
					{
						pros.append(proNames.get(i) + "，");
					}
					mInfomation = String.format("您好，今天电视台播出的%s的节目有：%s请问您要点播哪一个节目？", name, pros);
				}
				else
				{
					for(int i = 0; i < proNames.size(); ++ i)
					{
						pros.append("节目"+ proNames.get(i) +"，");
						if(i != proNames.size() - 1)
						{
							pros.append("还是");
						}
					}
					mInfomation = String.format("您好，请问您是要看%s？", pros);
				}
			}
			else
				mType = Consts.ERROR;
			break;
		default:
			mType = Consts.ERROR;
		}

	}

	/**
	 * 根据type得到结果
	 * 
	 * @return type station informat 类型 电视节目 信息
	 */
	public OtpErlangTuple getErlangTuple()
	{
		OtpErlangTuple tuple = null;
		if (mType == Consts.ERROR)
			tuple = new OtpErlangTuple(new OtpErlangObject[] {new OtpErlangAtom("lucene"), new OtpErlangInt(Consts.ERROR), new OtpErlangString(""), new OtpErlangString("") });
		else
			tuple = new OtpErlangTuple(new OtpErlangObject[] {new OtpErlangAtom("lucene"), new OtpErlangInt(mType), new OtpErlangString(mStation), new OtpErlangString(mInfomation) });
		return tuple;
	}

}

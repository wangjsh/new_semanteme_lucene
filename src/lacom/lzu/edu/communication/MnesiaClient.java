package lacom.lzu.edu.communication;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lacom.lzu.edu.entity.OtpServerMsg;

import com.ericsson.otp.erlang.OtpErlangBinary;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpMbox;
import com.ericsson.otp.erlang.OtpNode;

public class MnesiaClient
{
	private OtpNode node = null;
	private OtpMbox mBox = null;
	private static final long Timeout = 2000;
	private static final String MNESIA = "MnesiaClient";
	private String client = "java_semanteme";
	private String cookie = "lacom159753";
	// 线程池
	private ThreadPoolExecutor threadPool;
	private int ThreadPoolMinNum = 20;//最少数量
	private int ThreadPoolMaxNum = 1000;//最大适量
	private int ThreadKeepAliveTime = 60; //保持一分钟
	
	// private static MnesiaClient mClient = null;
	public static MnesiaClient getMnesiaClient()
	{
		return null;
	}

	private MnesiaClient()
	{
		threadPool = new ThreadPoolExecutor(ThreadPoolMinNum, ThreadPoolMaxNum, ThreadKeepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(ThreadPoolMinNum), new ThreadPoolExecutor.DiscardOldestPolicy());
		try
		{
			// 使用java + sessionid作为客户端
			node = new OtpNode(client, cookie);
			System.out.println(node.toString());
			mBox = node.createMbox();
			mBox.registerName(client);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getMessage() throws Exception
	{
		String xunfeiString = null;
		while (true)
		{
			OtpErlangObject otpObject = mBox.receive();
			//System.out.println("收到消息" + otpObject.toString());
			if (otpObject instanceof OtpErlangTuple)
			{
				// 获得Erlang服务器发过来的消息
				OtpErlangTuple tuple = (OtpErlangTuple) otpObject;
				OtpErlangObject[] resObject = tuple.elements();
				if (resObject.length == 2 && resObject[0] instanceof OtpErlangPid)
				{
					OtpServerMsg msg = new OtpServerMsg();
					// 获取erlang服务器的PID
					msg.setPid((OtpErlangPid) resObject[0]);
					// 获取Erlang服务器
					if (resObject[1] instanceof OtpErlangBinary)
					{
						byte[] buf = ((OtpErlangBinary) resObject[1]).binaryValue();
						xunfeiString = new String(buf, "utf-8");
					} else
						xunfeiString = null;
					msg.setXunFei(xunfeiString);
					// 使用线程池来处理消息
					threadPool.execute(new EmbalmeMsg(mBox, msg));
				}
			}

		}
	}

	public static void main(String[] args)
	{// {java_semanteme,'java_semanteme@lcq-pc'}!{self(),Ub}.
		//创建tcp thread
		Thread tcpThread = new Thread(new TCPService());
		tcpThread.start();
		MnesiaClient mClient = new MnesiaClient();
		while (true)
			try
			{
				mClient.getMessage();
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}

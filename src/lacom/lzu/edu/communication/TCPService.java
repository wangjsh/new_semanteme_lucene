package lacom.lzu.edu.communication;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.yuyi.lucenedemo.SearchLogic;

import lacom.lzu.edu.consts.Consts;
import lacom.lzu.edu.luceneindex.CreateIndex;

/**
 * 该类是接受其他类的TCP服务请求 用来通知本程序检索电视节目或者是旅游信息
 * 
 * @author lcq
 *
 */
public class TCPService implements Runnable
{
	// 服务端口
	int b;
	int a = b;
	private ServerSocket mServer;
	private static final int PORT = 6670;
	private boolean isReciver;

	public TCPService()
	{
		try
		{
			mServer = new ServerSocket(PORT);
			isReciver = true;
		} catch (IOException e)
		{
			e.printStackTrace();
			mServer = null;
			isReciver = false;
		}
	}

	private void resTcpMessage()
	{
		if (mServer == null)
			return;
		try
		{
			Socket socket = null;
			while (isReciver)
			{
				socket = mServer.accept();
				new Thread(new ServiceThread(socket)).start();
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run()
	{
		resTcpMessage();
	}
}

class ServiceThread implements Runnable
{
	private Socket mSocket;
	private DataOutputStream socketOut = null;
	private DataInputStream socketIn = null;
	private boolean isConnection = false;

	public ServiceThread(Socket socket) throws IOException
	{
		try
		{
			mSocket = socket;
			socketIn = new DataInputStream(mSocket.getInputStream());
			socketOut = new DataOutputStream(mSocket.getOutputStream());
			isConnection = true;
		} catch (Exception e)
		{
			e.printStackTrace();
			closeTcp();
		}
	}

	/**
	 * 关闭所有的流
	 */
	private void closeTcp()
	{
		try
		{
			if (socketIn != null)
				socketIn.close();
			if (socketOut != null)
				socketOut.close();
			if (mSocket != null)
				mSocket.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			socketIn = null;
			socketOut = null;
			mSocket = null;
			isConnection = false;
		}
	}

	@Override
	public void run()
	{
		if (!isConnection)
			return;
		byte[] buf = new byte[2];
		try
		{
			socketIn.read(buf);
			if (buf[0] == Consts.UPDATATV)
			{
				indexTvProgram();
			} else if (buf[0] == Consts.UPDATAWEB)
			{
				SearchLogic.updataIndex(SearchLogic.TYPEWEB);
			} else
			{
				indexTvProgram();
				SearchLogic.updataIndex(SearchLogic.TYPEWEB);
			}
			socketOut.writeBytes("ok");
			System.out.println("the reserive type is" + buf[0]);
			System.out.println("the index is updata");
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			closeTcp();
		}

	}

	private void indexTvProgram() throws IOException
	{
		CreateIndex createIndex = new CreateIndex();
		createIndex.createIndex();
		SearchLogic.updataIndex(SearchLogic.TYPETV);
	}

}

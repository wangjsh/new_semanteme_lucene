package lacom.lzu.edu.communication;


import lacom.lzu.edu.entity.OtpServerMsg;
import lacom.lzu.edu.vo.OtpRespondMsg;

import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpMbox;
import com.yuyi.bean.Message;
import com.yuyi.doxml.GetConfig;
import com.yuyi.utils.Compute;

public class EmbalmeMsg implements Runnable
{
	private OtpMbox mBox;
	private OtpServerMsg msg;

	/**
	 * 消息处理线程
	 * 
	 * @param box
	 *            Erlang通信的盒子
	 * @param otpMsg
	 *            Erlang传输的消息
	 */
	public EmbalmeMsg(OtpMbox box, OtpServerMsg otpMsg)
	{
		this.msg = otpMsg;
		this.mBox = box;
	}

	@Override
	public void run()
	{
		//long old = System.currentTimeMillis();
		Message resMsg = null;
		// 回去传输的消息
		OtpErlangTuple tuple = null;
		if (null != msg.getXunFei())
		{
			// 获得lucene的处理结果
			resMsg = Compute.returnMessage(msg.getXunFei(), GetConfig.mark);
			System.out.println("讯飞返回字符串："+ msg.getXunFei() +"------"+ resMsg.getType());
			if(resMsg == null)
			{
				resMsg = Compute.returnWebTour(msg.getXunFei(), GetConfig.mark);
			}
		}
		//获得封装后的消息
		tuple = new OtpRespondMsg(resMsg).getErlangTuple();
		//通过节点发送
		mBox.send(msg.getPid(), tuple);
		//System.out.println("当前执行的时间为"+(System.currentTimeMillis()-old));
	}
}

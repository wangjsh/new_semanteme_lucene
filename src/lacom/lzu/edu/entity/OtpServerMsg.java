package lacom.lzu.edu.entity;

import com.ericsson.otp.erlang.OtpErlangPid;

public class OtpServerMsg
{
	private OtpErlangPid pid = null;
	private String xunFei = null;
	
	
	public OtpErlangPid getPid()
	{
		return pid;
	}
	public void setPid(OtpErlangPid pid)
	{
		this.pid = pid;
	}
	public String getXunFei()
	{
		return xunFei;
	}
	public void setXunFei(String xunFei)
	{
		this.xunFei = xunFei;
	}
	@Override
	public String toString()
	{
		
		return "pid = "+pid.toString()+"\txunfei = "+xunFei;
	}
	
	
	
}

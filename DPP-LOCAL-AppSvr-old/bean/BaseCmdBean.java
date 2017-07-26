package bean;

import net.TcpSvrBase;
import net.appsvr.AppDeviceDataReqBean;
import net.appsvr.RmiDeviceDataReqBean;
import util.*;

public abstract class BaseCmdBean
{
	public static long m_SessionId = (new java.util.Date().getTime()/1000);
	private String actionSource = "";
	private String Reserve = "";
	private String Status = "0000";
	private int Action = 0;	
	private int TestTime = (int)(new java.util.Date().getTime()/1000);
	private String Seq = "";
	public DBUtil m_DbUtil = null;
	
	public BaseCmdBean(int action, String seq, DBUtil dbUtil){
		Action = action;
		Seq = seq;
		m_DbUtil = dbUtil;
	}
	
	public static BaseCmdBean getBean(int Cmd, DBUtil dbUtil)
	{
		BaseCmdBean retBean = null;
		switch(Cmd)
		{
			case Cmd_Sta.CMD_SUBMIT_1001:
				retBean = new AppDeviceDataReqBean(Cmd, SessionId(), dbUtil);
				break;
			case Cmd_Sta.CMD_SUBMIT_0001:
			case Cmd_Sta.CMD_SUBMIT_0002:
				retBean = new RmiDeviceDataReqBean(Cmd, SessionId(), dbUtil);
				break;
		}
		return retBean;
	}
	public static synchronized String SessionId()
	{
		long ret = m_SessionId++;
		return CommUtil.LongToStringLeftFillZero(ret, 20);
	}
	public String GetSessionId()
	{
		return Seq;
	}
	public abstract void parseReqest(String key, String strRequest, byte[] strData);
	public abstract int execRequest(TcpSvrBase tcpSvr);

	public abstract void parseReponse(String strResponse);
	public abstract void execResponse();
	
	public abstract void noticeTimeOut();
	public String getActionSource() {
		return actionSource;
	}

	public void setActionSource(String actionSource) {
		this.actionSource = actionSource;
	}

	public String getReserve() {
		return Reserve;
	}

	public void setReserve(String reserve) {
		Reserve = reserve;
	}

	public int getAction() {
		return Action;
	}

	public void setAction(int action) {
		Action = action;
	}

	public int getTestTime() {
		return TestTime;
	}

	public void setTestTime(int testTime) {
		TestTime = testTime;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getSeq() {
		return Seq;
	}

	public void setSeq(String seq) {
		Seq = seq;
	}

	public int execRequest()
	{
		// TODO Auto-generated method stub
		return 0;
	}
}

package bean;

import net.MsgCtrl;
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
	private String BeanName = "";

	public BaseCmdBean(int action, String seq){
		Action = action;
		Seq = seq;
	}
	
	public static BaseCmdBean getBean(int Cmd, String Seq)
	{
		BaseCmdBean retBean = null;
		switch(Cmd)
		{
			case Cmd_Sta.CMD_SUBMIT_1001:
				retBean = new AppDeviceDataReqBean(Cmd, Seq);
				break;
			case Cmd_Sta.CMD_SUBMIT_0001:
				retBean = new DTURestartBean(Cmd, Seq);
				break;
			case Cmd_Sta.CMD_SUBMIT_0002:
				retBean = new DTUUpdateTimeBean(Cmd, Seq);
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
	public abstract int execRequest(MsgCtrl msgCtrl);

	public abstract void parseReponse(String strResponse);
	public abstract void execResponse(MsgCtrl msgCtrl);
	
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

	public String getBeanName() {
		return BeanName;
	}

	public void setBeanName(String beanName) {
		BeanName = beanName;
	}
}

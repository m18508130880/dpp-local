package bean;

import java.util.ArrayList;
import java.util.Hashtable;

import net.AlertCtrl;
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

	@SuppressWarnings("rawtypes")
	public Hashtable<String, ArrayList> objGJGXTable = null;

	public BaseCmdBean(int action, String seq){
		Action = action;
		Seq = seq;
	}
	
	public static BaseCmdBean getBean(int Cmd, String Seq)
	{
		BaseCmdBean retBean = null;
		switch(Cmd)
		{
			case Cmd_Sta.CMD_SUBMIT_2001:
				retBean = new UpdateDataBean(Cmd, Seq);
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
	public abstract int execRequest(AlertCtrl alertCtrl);

	public abstract void parseReponse(String strResponse);
	public abstract void execResponse();
	
	public abstract void noticeTimeOut();
	public String getActionSource() {
		return actionSource;
	}

	public Hashtable<String, ArrayList> getObjGJGXTable()
	{
		return objGJGXTable;
	}

	public void setObjGJGXTable(Hashtable<String, ArrayList> objGJGXTable)
	{
		this.objGJGXTable = objGJGXTable;
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

	public String getBeanName() {
		return BeanName;
	}

	public void setBeanName(String beanName) {
		BeanName = beanName;
	}
}

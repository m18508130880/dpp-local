package bean;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rmi.Rmi;
import rmi.RmiBean;
import util.CommUtil;
import util.CurrStatus;
import util.MsgBean;

public class MacReadTaskBean extends RmiBean 
{
	public final static long serialVersionUID = RmiBean.RMI_READ_TASK;
	public long getClassId()
	{
		return serialVersionUID;
	}
	
	public MacReadTaskBean()
	{
		super.className = "MacReadTaskBean";
	}
	
	public void ExecCmd(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		switch(currStatus.getCmd())
		{
			case 10://����
			case 11://�༭
				currStatus.setResult(MsgBean.GetResult(msgBean.getStatus()));
				msgBean = pRmi.RmiExec(0, this, 0, 25);
			case 0://��ѯ
		    	request.getSession().setAttribute("Mac_Man_" + Sid, ((Object)msgBean.getMsg()));
		    	currStatus.setJsp("Mac_Man.jsp?Sid=" + Sid);		    
		    	break;
		}
		
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
	   	response.sendRedirect(currStatus.getJsp());
	}
	
	public String getSql(int pCmd)
	{
		String Sql = "";
		switch (pCmd)
		{
			case 0://��ѯ
				Sql = " select  t.sn, t.pid, t.tid, t.read "
						+ " from mac_read_task t order by t.sn";
				break;
			case 10://����
				Sql = " insert into mac_read_task(pid, tid, read)" +
					  " values('"+ PId + "', '" + TId +"', '"+ Read +"')";
				break;
			case 11://�༭
				Sql = " update mac_read_task t set t.pid= '"+ PId +"', t.tid= '"+ TId +"', t.read= '"+ Read +"' " +
					  " where t.sn = '"+ SN +"'";
				break;
		}
		return Sql;
	}
	
	public boolean getData(ResultSet pRs)
	{
		boolean IsOK = true;
		try
		{
			setSN(pRs.getString(1));
			setPId(pRs.getString(2));
			setTId(pRs.getString(3));
			setRead(pRs.getString(4));
		}
		catch (SQLException sqlExp)
		{
			sqlExp.printStackTrace();
		}
		return IsOK;
	}
	
	public boolean getHtmlData(HttpServletRequest request)
	{
		boolean IsOK = true;
		try
		{
			setSN(CommUtil.StrToGB2312(request.getParameter("SN")));
			setPId(CommUtil.StrToGB2312(request.getParameter("PId")));
			setTId(CommUtil.StrToGB2312(request.getParameter("TId")));
			setRead(CommUtil.StrToGB2312(request.getParameter("Read")));
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}
	
	private String SN;
	private String PId;
	private String TId;
	private String Read;

	private String Sid;

	public String getSN() {
		return SN;
	}

	public void setSN(String sN) {
		SN = sN;
	}

	public String getPId() {
		return PId;
	}

	public void setPId(String pId) {
		PId = pId;
	}

	public String getTId() {
		return TId;
	}

	public void setTId(String tId) {
		TId = tId;
	}

	public String getRead() {
		return Read;
	}

	public void setRead(String read) {
		Read = read;
	}

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}
	
	
}
package bean;

import java.io.IOException;
import java.io.PrintWriter;
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
			case 10://添加
			case 11://编辑
				currStatus.setResult(MsgBean.GetResult(msgBean.getStatus()));
				msgBean = pRmi.RmiExec(0, this, 0, 25);
			case 0://查询
		    	request.getSession().setAttribute("Mac_Man_" + Sid, ((Object)msgBean.getMsg()));
		    	currStatus.setJsp("Mac_Man.jsp?Sid=" + Sid);		    
		    	break;
		}
		
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
	   	response.sendRedirect(currStatus.getJsp());
	}
	
	public void addRead(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);

		PrintWriter outprint = response.getWriter();
		String Resp = "9999";
		
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		if(msgBean.getStatus() == MsgBean.STA_SUCCESS){
			Resp = "0000";
		}
		
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}
	
	public void updateStatus(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		PrintWriter outprint = response.getWriter();
		String Resp = "9999";
		
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		if(msgBean.getStatus() == MsgBean.STA_SUCCESS){
			Resp = "0000";
			// 读取不需要发送
			//pRmi.DTUAction(1, "", "", "");
		}
		
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}
	
	public String getSql(int pCmd)
	{
		String Sql = "";
		switch (pCmd)
		{
			case 0://查询
				Sql = " select  t.sn, t.pid, t.tid, t.read_sn, t.status "
						+ " from mac_read_task t order by t.sn";
				break;
			case 10://添加
				Sql = " insert into mac_read_task(pid, tid, read_sn)" +
					  " values('"+ PId + "', '" + TId +"', '"+ Read +"')";
				break;
			case 11://编辑
				Sql = " update mac_read_task t set t.pid= '"+ PId +"', t.tid= '"+ TId +"', t.read_sn= '"+ Read +"' " +
					  " where t.sn = '"+ SN +"'";
				break;
			case 12:// 状态编辑
				Sql = " update mac_read_task t set t.status= '"+ Status +"' " +
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
			setStatus(pRs.getString(5));
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
			setStatus(CommUtil.StrToGB2312(request.getParameter("Status")));
			
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
	private String Status;

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

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}
	
	
}
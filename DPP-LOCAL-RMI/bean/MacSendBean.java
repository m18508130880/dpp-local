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

public class MacSendBean extends RmiBean 
{
	public final static long serialVersionUID = RmiBean.RMI_MAC_SEND;
	public long getClassId()
	{
		return serialVersionUID;
	}
	
	public MacSendBean()
	{
		super.className = "MacSendBean";
	}
	
	public void ExecCmd(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		switch(currStatus.getCmd())
		{
			case 10://ÃÌº”
			case 11://±‡º≠
				currStatus.setResult(MsgBean.GetResult(msgBean.getStatus()));
				msgBean = pRmi.RmiExec(0, this, 0, 25);
			case 0://≤È—Ø
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
			case 0://≤È—Ø
				Sql = " select  t.sn, t.man_sn, t.pid, t.tid, t.type, t.cname, t.send "
						+ " from mac_send t order by t.sn";
				break;
			case 10://ÃÌº”
				Sql = " insert into mac_send(man_sn, pid, tid, type, cname, send)" +
					  " values('"+ Man_SN +"', '"+ PId +"', '"+ TId +"', '"+ Type +"', '"+ CName +"', '"+ Send +"')";
				break;
			case 11://±‡º≠
				Sql = " update mac_send t set t.man_sn= '"+ Man_SN +"', t.pid= '"+ PId +"', t.tid= '"+ TId +"', t.type= '"+ Type +"' " +"', t.cname= '"+ CName +"' " +"', t.send= '"+ Send +"' " +
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
			setMan_SN(pRs.getString(2));
			setPId(pRs.getString(3));
			setTId(pRs.getString(4));
			setType(pRs.getString(5));
			setCName(pRs.getString(6));
			setSend(pRs.getString(7));
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
			setMan_SN(CommUtil.StrToGB2312(request.getParameter("Man_SN")));
			setPId(CommUtil.StrToGB2312(request.getParameter("PId")));
			setTId(CommUtil.StrToGB2312(request.getParameter("TId")));
			setType(CommUtil.StrToGB2312(request.getParameter("Type")));
			setCName(CommUtil.StrToGB2312(request.getParameter("CName")));
			setSend(CommUtil.StrToGB2312(request.getParameter("Send")));
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}
	
	private String SN;
	private String Man_SN;
	private String PId;
	private String TId;
	private String Type;
	private String CName;
	private String Send;

	private String Sid;

	public String getSN() {
		return SN;
	}

	public void setSN(String sN) {
		SN = sN;
	}

	public String getCName() {
		return CName;
	}

	public void setCName(String cName) {
		CName = cName;
	}

	public String getMan_SN() {
		return Man_SN;
	}

	public void setMan_SN(String man_SN) {
		Man_SN = man_SN;
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

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public String getSend() {
		return Send;
	}

	public void setSend(String send) {
		Send = send;
	}

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}
	
	
}
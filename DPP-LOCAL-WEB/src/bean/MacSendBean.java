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
		    	request.getSession().setAttribute("Mac_Send_" + Sid, ((Object)msgBean.getMsg()));
		    	currStatus.setJsp("Mac_Send.jsp?Sid=" + Sid);		    
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
				Sql = " select  t.sn, t.addrs, t.code, t.cname, t.send "
						+ " from mac_send t order by t.sn";
				break;
			case 10://ÃÌº”
				Sql = " insert into mac_send(addrs, code, cname, send)" +
					  " values('"+ Addrs +"', '"+ Code +"', '"+ CName +"', '"+ Send +"')";
				break;
			case 11://±‡º≠
				Sql = " update mac_send t set t.addrs= '"+ Addrs +"', t.code= '"+ Code + "', t.cname= '"+ CName + "', t.send= '"+ Send +"' " +
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
			setAddrs(pRs.getString(2));
			setCode(pRs.getString(3));
			setCName(pRs.getString(4));
			setSend(pRs.getString(5));
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
			setAddrs(CommUtil.StrToGB2312(request.getParameter("Addrs")));
			setCode(CommUtil.StrToGB2312(request.getParameter("Code")));
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
	private String Addrs;
	private String Code;
	private String CName;
	private String Send;

	private String Sid;

	public String getSN() {
		return SN;
	}

	public void setSN(String sN) {
		SN = sN;
	}

	public String getAddrs() {
		return Addrs;
	}

	public void setAddrs(String addrs) {
		Addrs = addrs;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public String getCName() {
		return CName;
	}

	public void setCName(String cName) {
		CName = cName;
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
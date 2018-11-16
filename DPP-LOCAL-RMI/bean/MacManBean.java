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

public class MacManBean extends RmiBean 
{
	public final static long serialVersionUID = RmiBean.RMI_MAC_MAN;
	public long getClassId()
	{
		return serialVersionUID;
	}
	
	public MacManBean()
	{
		super.className = "MacManBean";
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
				Sql = " select  t.sn, t.cname, t.man_name, t.man_tel, t.man_addrs "
						+ " from mac_man t order by t.id";
				break;
			case 10://ÃÌº”
				Sql = " insert into mac_man(cname, man_name, man_tel, man_addrs)" +
					  " values('"+ CName +"', '"+ Man_Name +"', '"+ Man_Tel +"', '"+ Man_Addrs +"')";
				break;
			case 11://±‡º≠
				Sql = " update mac_man t set t.cname= '"+ CName +"', t.man_name= '"+ Man_Name +"', t.man_tel= '"+ Man_Tel +"', t.man_addrs= '"+ Man_Addrs +"' " +
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
			setCName(pRs.getString(2));
			setMan_Name(pRs.getString(3));
			setMan_Tel(pRs.getString(4));
			setMan_Addrs(pRs.getString(5));
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
			setCName(CommUtil.StrToGB2312(request.getParameter("CName")));
			setMan_Name(CommUtil.StrToGB2312(request.getParameter("Man_Name")));
			setMan_Tel(CommUtil.StrToGB2312(request.getParameter("Man_Tel")));
			setMan_Addrs(CommUtil.StrToGB2312(request.getParameter("Man_Addrs")));
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}
	
	private String SN;
	private String CName;
	private String Man_Name;
	private String Man_Tel;
	private String Man_Addrs;

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

	public String getMan_Name() {
		return Man_Name;
	}

	public void setMan_Name(String man_Name) {
		Man_Name = man_Name;
	}

	public String getMan_Tel() {
		return Man_Tel;
	}

	public void setMan_Tel(String man_Tel) {
		Man_Tel = man_Tel;
	}

	public String getMan_Addrs() {
		return Man_Addrs;
	}

	public void setMan_Addrs(String man_Addrs) {
		Man_Addrs = man_Addrs;
	}

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}
	
	
}
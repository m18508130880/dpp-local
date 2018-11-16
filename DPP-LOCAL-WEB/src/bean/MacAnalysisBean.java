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

public class MacAnalysisBean extends RmiBean 
{
	public final static long serialVersionUID = RmiBean.RMI_MAC_ANALYZE;
	public long getClassId()
	{
		return serialVersionUID;
	}
	
	public MacAnalysisBean()
	{
		super.className = "MacAnalyzeBean";
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
		    	request.getSession().setAttribute("Mac_Analysis_" + Sid, ((Object)msgBean.getMsg()));
		    	currStatus.setJsp("Mac_Analysis.jsp?Sid=" + Sid);		    
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
				Sql = " select  t.sn, t.attr_id, t.attr_name, t.flow, t.type, t.addrs_s, t.addrs_e, t.unit, t.amend "
						+ " from mac_analysis t order by t.sn";
				break;
			case 10://ÃÌº”
				Sql = " insert into mac_analysis(attr_id, attr_name, flow, type, addrs_s, addrs_e, unit, amend)" +
					  " values('"+ Attr_Id +"', '"+ Attr_Name +"', '"+ Flow +"', '"+ Type +"', '"+ Addrs_S +"', '"+ Addrs_E +"', '"+ Unit +"', '"+ Amend +"')";
				break;
			case 11://±‡º≠
				Sql = " update mac_analysis t set t.attr_id= '"+ Attr_Id +"', t.attr_name= '"+ Attr_Name +"', t.flow= '"+ Flow +"', t.type= '"+ Type +"', t.addrs_s= '"+ Addrs_S +"', t.addrs_e= '"+ Addrs_E +"' "+"', t.unit= '"+ Unit +"' "+"', t.amend= '"+ Amend +"' " +
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
			setAttr_Id(pRs.getString(2));
			setAttr_Name(pRs.getString(3));
			setFlow(pRs.getString(4));
			setType(pRs.getString(5));
			setAddrs_S(pRs.getString(6));
			setAddrs_E(pRs.getString(7));
			setUnit(pRs.getString(8));
			setAmend(pRs.getString(9));
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
			setAttr_Id(CommUtil.StrToGB2312(request.getParameter("Attr_Id")));
			setAttr_Name(CommUtil.StrToGB2312(request.getParameter("Attr_Name")));
			setFlow(CommUtil.StrToGB2312(request.getParameter("Flow")));
			setType(CommUtil.StrToGB2312(request.getParameter("Type")));
			setAddrs_S(CommUtil.StrToGB2312(request.getParameter("Addrs_S")));
			setAddrs_E(CommUtil.StrToGB2312(request.getParameter("Addrs_E")));
			setUnit(CommUtil.StrToGB2312(request.getParameter("Unit")));
			setAmend(CommUtil.StrToGB2312(request.getParameter("Amend")));
			
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}
	
	private String SN;
	private String Attr_Id;
	private String Attr_Name;
	private String Flow;
	private String Type;
	private String Addrs_S;
	private String Addrs_E;
	private String Unit;
	private String Amend;

	private String Sid;

	public String getSN() {
		return SN;
	}

	public void setSN(String sN) {
		SN = sN;
	}

	public String getAttr_Id() {
		return Attr_Id;
	}

	public void setAttr_Id(String attr_Id) {
		Attr_Id = attr_Id;
	}

	public String getAttr_Name() {
		return Attr_Name;
	}

	public void setAttr_Name(String attr_Name) {
		Attr_Name = attr_Name;
	}

	public String getFlow() {
		return Flow;
	}

	public void setFlow(String flow) {
		Flow = flow;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public String getAddrs_S() {
		return Addrs_S;
	}

	public void setAddrs_S(String addrs_S) {
		Addrs_S = addrs_S;
	}

	public String getAddrs_E() {
		return Addrs_E;
	}

	public void setAddrs_E(String addrs_E) {
		Addrs_E = addrs_E;
	}

	public String getUnit() {
		return Unit;
	}

	public void setUnit(String unit) {
		Unit = unit;
	}

	public String getAmend() {
		return Amend;
	}

	public void setAmend(String amend) {
		Amend = amend;
	}

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}
	
	
}
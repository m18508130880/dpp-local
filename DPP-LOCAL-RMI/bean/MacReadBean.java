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

public class MacReadBean extends RmiBean 
{
	public final static long serialVersionUID = RmiBean.RMI_MAC_READ;
	public long getClassId()
	{
		return serialVersionUID;
	}
	
	public MacReadBean()
	{
		super.className = "MacReadBean";
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
				Sql = " select  t.sn, t.man_sn, t.pid, t.tid, t.sign, t.sign_s, t.sign_e, t.analyze "
						+ " from mac_read t order by t.sn";
				break;
			case 10://ÃÌº”
				Sql = " insert into mac_read(man_sn, pid, tid, sign, sign_s, sign_e, analyze)" +
					  " values('"+ Man_SN +"', '"+ PId +"', '"+ TId +"', '"+ Sign +"', '"+ Sign_S +"', '"+ Sign_E +"', '"+ Analyze +"')";
				break;
			case 11://±‡º≠
				Sql = " update mac_read t set t.man_sn= '"+ Man_SN +"', t.pid= '"+ PId +"', t.tid= '"+ TId +"', t.sign= '"+ Sign +"' " +"', t.sign_s= '"+ Sign_S +"' " +"', t.sign_e= '"+ Sign_E +"' " +"', t.analyze= '"+ Analyze +"' " +
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
			setSign(pRs.getString(5));
			setSign_S(pRs.getString(6));
			setSign_E(pRs.getString(7));
			setAnalyze(pRs.getString(8));
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
			setSign(CommUtil.StrToGB2312(request.getParameter("Sign")));
			setSign_S(CommUtil.StrToGB2312(request.getParameter("Sign_S")));
			setSign_E(CommUtil.StrToGB2312(request.getParameter("Sign_E")));
			setAnalyze(CommUtil.StrToGB2312(request.getParameter("Analyze")));
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
	private String Sign;
	private String Sign_S;
	private String Sign_E;
	private String Analyze;

	private String Sid;

	public String getSN() {
		return SN;
	}

	public void setSN(String sN) {
		SN = sN;
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

	public String getSign() {
		return Sign;
	}

	public void setSign(String sign) {
		Sign = sign;
	}

	public String getSign_S() {
		return Sign_S;
	}

	public void setSign_S(String sign_S) {
		Sign_S = sign_S;
	}

	public String getSign_E() {
		return Sign_E;
	}

	public void setSign_E(String sign_E) {
		Sign_E = sign_E;
	}

	public String getAnalyze() {
		return Analyze;
	}

	public void setAnalyze(String analyze) {
		Analyze = analyze;
	}

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}
	
	
}
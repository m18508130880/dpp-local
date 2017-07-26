package bean;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rmi.Rmi;
import rmi.RmiBean;
import util.*;

public class CorpInfoBean extends RmiBean 
{	
	public final static long serialVersionUID = RmiBean.RMI_CORP_INFO;
	public long getClassId()
	{
		return serialVersionUID;
	}
	
	public CorpInfoBean() 
	{ 
		super.className = "CorpInfoBean";
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
			case 11://修改
				currStatus.setResult(MsgBean.GetResult(msgBean.getStatus()));
				msgBean = pRmi.RmiExec(0, this, 0, 25);
		    case 0://查询
		    	if(null != msgBean.getMsg() && ((ArrayList<?>)msgBean.getMsg()).size() > 0)
				{
					request.getSession().setAttribute("Corp_Info_" + Sid, (CorpInfoBean)((ArrayList<?>)msgBean.getMsg()).get(0));
				}
		    	currStatus.setJsp("Corp_Info.jsp?Sid=" + Sid);
		    	break;
		    case 1://查询
		    	if(null != msgBean.getMsg() && ((ArrayList<?>)msgBean.getMsg()).size() > 0)
				{
					request.getSession().setAttribute("User_Announce_" + Sid, (CorpInfoBean)((ArrayList<?>)msgBean.getMsg()).get(0));
				}
		    	currStatus.setJsp("User_Announce.jsp?Sid=" + Sid);
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
		
			case 0://查询
			case 1://查询	
				Sql = " select t.id, t.cname, t.brief, t.contact, t.tel, t.addr, t.dept ,t.demo" +
					  " from corp_info t " +
					  " order by substr(t.id,3,4)";
				break;
			case 10://添加
				Sql = " insert into corp_info(id, cname, brief, contact, tel, addr, dept ,t.demo)" +
					  " values('"+Id+"', '"+CName+"', '"+Brief+"', '"+Contact+"', '"+Tel+"', '"+Addr+"', '"+Dept+"', '"+Demo+"' )";
				break;
			case 11://修改
				Sql = " update corp_info set id='"+ Id +"', cname = '"+ CName +"', brief = '"+ Brief +"', contact = '"+ Contact +"', tel = '"+ Tel +"', " +
					  " addr = '"+ Addr +"', dept = '"+ Dept +"' , demo = '"+ Demo +"'  ";		  
				break;
		}
		return Sql;
	}
	public boolean getData(ResultSet pRs) 
	{
		boolean IsOK = true;
		try
		{
			setId(pRs.getString(1));
			setCName(pRs.getString(2));
			setBrief(pRs.getString(3));
			setContact(pRs.getString(4));
			setTel(pRs.getString(5));
			setAddr(pRs.getString(6));
			setDept(pRs.getString(7));
			setDemo(pRs.getString(8));
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
			setId(CommUtil.StrToGB2312(request.getParameter("Id")));
			setCName(CommUtil.StrToGB2312(request.getParameter("CName")));
			setBrief(CommUtil.StrToGB2312(request.getParameter("Brief")));
			setContact(CommUtil.StrToGB2312(request.getParameter("Contact")));
			setTel(CommUtil.StrToGB2312(request.getParameter("Tel")));
			setAddr(CommUtil.StrToGB2312(request.getParameter("Addr")));
			setDept(CommUtil.StrToGB2312(request.getParameter("Dept")));
			setDemo(CommUtil.StrToGB2312(request.getParameter("Demo")));
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
		}
		catch (Exception Exp) 
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}
	
	private String Id;
	private String CName;
	private String Brief;
	private String Contact;
	private String Tel;
	private String Addr;
	private String Dept;
	private String Demo;
	
	private String Sid;
	

	public String getDemo() {
		return Demo;
	}

	public void setDemo(String demo) {
		Demo = demo;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getCName() {
		return CName;
	}

	public void setCName(String cName) {
		CName = cName;
	}

	public String getBrief() {
		return Brief;
	}

	public void setBrief(String brief) {
		Brief = brief;
	}

	public String getContact() {
		return Contact;
	}

	public void setContact(String contact) {
		Contact = contact;
	}

	public String getTel() {
		return Tel;
	}

	public void setTel(String tel) {
		Tel = tel;
	}

	public String getAddr() {
		return Addr;
	}

	public void setAddr(String addr) {
		Addr = addr;
	}

	public String getDept() {
		return Dept;
	}

	public void setDept(String dept) {
		Dept = dept;
	}


	
	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}
}

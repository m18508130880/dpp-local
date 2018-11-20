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

public class MacConfigBean extends RmiBean 
{
	public final static long serialVersionUID = RmiBean.RMI_MAC_CONFIG;
	public long getClassId()
	{
		return serialVersionUID;
	}
	
	public MacConfigBean()
	{
		super.className = "MacConfigBean";
	}
	
	public void ExecCmd(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		
		switch(currStatus.getCmd())
		{
			case 10:
			case 11:
				currStatus.setResult(MsgBean.GetResult(msgBean.getStatus()));
				msgBean = pRmi.RmiExec(0, this, 0, 25);
			case 0:
				// 设备列表
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
				request.getSession().setAttribute("Mac_Man_" + Sid, ((Object)msgBean.getMsg()));
				break;
		}
		
		// 发送列表
		MacSendBean macSendBean = new MacSendBean();
		msgBean = pRmi.RmiExec(currStatus.getCmd(), macSendBean, 0, 25);
		request.getSession().setAttribute("Mac_Send_" + Sid, ((Object)msgBean.getMsg()));
		
		// 发送任务列表
		MacSendTaskBean macSendTaskBean = new MacSendTaskBean();
		msgBean = pRmi.RmiExec(currStatus.getCmd(), macSendTaskBean, 0, 25);
		request.getSession().setAttribute("Mac_Send_Task_" + Sid, ((Object)msgBean.getMsg()));
		
		// 接收列表
		MacReadBean macReadBean = new MacReadBean();
		msgBean = pRmi.RmiExec(currStatus.getCmd(), macReadBean, 0, 25);
		request.getSession().setAttribute("Mac_Read_" + Sid, ((Object)msgBean.getMsg()));
		
		// 接收任务列表
		MacReadTaskBean macReadTaskBean = new MacReadTaskBean();
		msgBean = pRmi.RmiExec(currStatus.getCmd(), macReadTaskBean, 0, 25);
		request.getSession().setAttribute("Mac_Read_Task_" + Sid, ((Object)msgBean.getMsg()));
		
		currStatus.setJsp("Mac_Config.jsp?Sid=" + Sid);
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
	   	response.sendRedirect(currStatus.getJsp());
	}
	
	public String getSql(int pCmd)
	{
		String Sql = "";
		switch (pCmd)
		{
			case 0://查询
				Sql = " select  t.id, t.cname, t.demo, t.pwd, onoff"
						+ " from device_detail t order by t.id";
				break;
			case 10://添加
				Sql = " insert into device_detail(id, cname, demo, pwd)" +
					  " values('"+ PId +"', '"+ TId +"', '"+ CName +"', '"+ PWD +"')";
				break;
			case 11://编辑
				Sql = " update device_detail t set t.id= '"+ PId +"', t.cname= '"+ TId +"', t.demo= '"+ CName +"', t.pwd= '"+ PWD +"' " +
					  " where t.id = '" + PId +"'";
				break;
		}
		return Sql;
	}
	
	public boolean getData(ResultSet pRs)
	{
		boolean IsOK = true;
		try
		{
			setPId(pRs.getString(1));
			setTId(pRs.getString(2));
			setCName(pRs.getString(3));
			setPWD(pRs.getString(4));
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
			setPId(CommUtil.StrToGB2312(request.getParameter("PId")));
			setTId(CommUtil.StrToGB2312(request.getParameter("TId")));
			setCName(CommUtil.StrToGB2312(request.getParameter("CName")));
			setPWD(CommUtil.StrToGB2312(request.getParameter("PWD")));

			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}
	
	private String PId;
	private String TId;
	private String CName;
	private String PWD;
	private String OnOff;

	private String Sid;


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

	public String getCName() {
		return CName;
	}

	public void setCName(String cName) {
		CName = cName;
	}

	public String getPWD() {
		return PWD;
	}

	public void setPWD(String pWD) {
		PWD = pWD;
	}

	public String getOnOff() {
		return OnOff;
	}

	public void setOnOff(String onOff) {
		OnOff = onOff;
	}

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}
	
	
}
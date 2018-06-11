package bean;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rmi.Rmi;
import rmi.RmiBean;
import util.CommUtil;
import util.CurrStatus;
import util.MsgBean;


public class CheckTaskBean extends RmiBean 
{
	public final static long serialVersionUID = RmiBean.RMI_CHECK_TASK;
	public long getClassId()
	{
		return serialVersionUID;
	}
	
	public CheckTaskBean()
	{
		super.className = "CheckTaskBean";
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
		//CTime = CommUtil.getDateTime();
				End_Time = End_Time + " 23:59:59";
				currStatus.setResult(MsgBean.GetResult(msgBean.getStatus()));
				msgBean = pRmi.RmiExec(0, this, 0, 25);
			case 0://查询
		    	request.getSession().setAttribute("Project_Info_" + Sid, ((Object)msgBean.getMsg()));
		    	currStatus.setJsp("Project_Info.jsp?Sid=" + Sid);		    
		    	break;
		}
		
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
	   	response.sendRedirect(currStatus.getJsp());
	}
	

	public void ajaxGetAll(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone)
	{
		try 
		{
			getHtmlData(request);
			currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
			currStatus.getHtmlData(request, pFromZone);
			
			PrintWriter outprint = response.getWriter();
			String Resp = "3006";
			
			msgBean = pRmi.RmiExec(1, this, 0, 25);//查找是否有该用户存在
			List<Object> CData = new ArrayList<Object>();
			if(msgBean.getStatus() == MsgBean.STA_SUCCESS){
				Resp = "0000";
				ArrayList<?> list = (ArrayList<?>) msgBean.getMsg();
				CData = objToJson(list, CData);
			}
			
			request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
			//JSONObject jsonObj = (JSONObject) JSONObject.toJSON(CData);
			response.setCharacterEncoding("UTF-8");
			outprint = response.getWriter();
			//outprint.write(jsonObj.toString());
			outprint.flush();
			//outprint.write(Resp);
		}
		catch (Exception Ex)
		{
			Ex.printStackTrace();
		}
	}
	
	public List<Object> objToJson(ArrayList<?> list, List<Object> CData){
		Iterator<?> iterator = list.iterator();
		while (iterator.hasNext()) {
			CheckTaskBean RealJson = (CheckTaskBean) iterator.next();
			CheckTaskBean json = new CheckTaskBean();
			json.setSN(RealJson.getSN());
			json.setLeader(RealJson.getLeader());
			json.setStaff(RealJson.getStaff());
			json.setCTime(RealJson.getCTime());
			json.setEnd_Time(RealJson.getEnd_Time());
			json.setProject_Id(RealJson.getProject_Id());
			json.setGJ_List(RealJson.getGJ_List());
			json.setGX_List(RealJson.getGX_List());
			json.setStatus(RealJson.getStatus());
			
			CData.add(json);
		}
		return CData;
		
	}
	
	public String getSql(int pCmd)
	{
		String Sql = "";
		switch (pCmd)
		{
			case 0://查询
				Sql = " select t.sn,  t.leader, t.staff, t.ctime, t.end_time, t.project_id, t.status, t.GJ_List, t.GX_List, t.Des "
						+ " from check_task t order by t.ctime";
				break;
			case 10://添加
				Sql = " insert into check_task( leader, staff, ctime, end_time, project_id, gj_list, gx_list, des)" +
					  " values('"+ Leader +"', '"+ Staff +"', '"+ CTime +"', '"+ End_Time +"', '"+ Project_Id +"', '"+ GJ_List +"', '"+ GX_List +"', '"+ Des +"')";
				break;
			case 11://编辑
				Sql = " update check_task set staff= '"+ Staff +"', end_time= '"+ End_Time +"', gj_list= '"+ GJ_List +"', gx_list= '"+ GX_List +"' , des= '"+ Des + "' " +
					  " where sn = '"+ SN +"' and leader = '" + Leader + "' and project_id = '" + Project_Id + "'";
			case 12://删除
				Sql = " delete check_task " +
					  " where sn = '"+ SN +"' and leader = '" + Leader + "' and project_id = '" + Project_Id + "'";
		}
		return Sql;
	}
	
	public boolean getData(ResultSet pRs)
	{
		boolean IsOK = true;
		try
		{
			setSN(pRs.getString(1));
			setLeader(pRs.getString(2));
			setStaff(pRs.getString(3));
			setCTime(pRs.getString(4));
			setEnd_Time(pRs.getString(5));
			setProject_Id(pRs.getString(6));
			setStatus(pRs.getString(7));
			setGJ_List(pRs.getString(8));
			setGX_List(pRs.getString(9));
			setDes(pRs.getString(10));
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
			setSN(CommUtil.StrToGB2312(request.getParameter("Sid")));
			setLeader(CommUtil.StrToGB2312(request.getParameter("Leader")));
			setStaff(CommUtil.StrToGB2312(request.getParameter("Staff")));
			setCTime(CommUtil.StrToGB2312(request.getParameter("CTime")));
			setEnd_Time(CommUtil.StrToGB2312(request.getParameter("End_Time")));
			setProject_Id(CommUtil.StrToGB2312(request.getParameter("Project_Id")));
			setStatus(CommUtil.StrToGB2312(request.getParameter("Status")));
			setGJ_List(CommUtil.StrToGB2312(request.getParameter("GJ_List")));
			setGX_List(CommUtil.StrToGB2312(request.getParameter("GX_List")));
			setDes(CommUtil.StrToGB2312(request.getParameter("Des")));
			
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}
	

	private String SN;
	private String Leader;
	private String Staff;
	private String CTime;
	private String End_Time;
	private String Project_Id;
	private String Status;
	private String GJ_List;
	private String GX_List;
	private String Des;

	private String Sid;
	
	
	public String getSN() {
		return SN;
	}

	public void setSN(String sN) {
		SN = sN;
	}

	public String getLeader() {
		return Leader;
	}

	public void setLeader(String leader) {
		Leader = leader;
	}

	public String getStaff() {
		return Staff;
	}

	public void setStaff(String staff) {
		Staff = staff;
	}

	public String getCTime() {
		return CTime;
	}

	public void setCTime(String cTime) {
		CTime = cTime;
	}

	public String getEnd_Time() {
		return End_Time;
	}

	public void setEnd_Time(String end_Time) {
		End_Time = end_Time;
	}

	public String getProject_Id() {
		return Project_Id;
	}

	public void setProject_Id(String project_Id) {
		Project_Id = project_Id;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String Status) {
		this.Status = Status;
	}

	public String getGJ_List() {
		return GJ_List;
	}

	public void setGJ_List(String gJ_List) {
		GJ_List = gJ_List;
	}

	public String getGX_List() {
		return GX_List;
	}

	public void setGX_List(String gX_List) {
		GX_List = gX_List;
	}

	public String getDes() {
		return Des;
	}

	public void setDes(String des) {
		Des = des;
	}

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}
	
	
}
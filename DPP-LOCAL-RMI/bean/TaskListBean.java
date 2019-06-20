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

import com.alibaba.fastjson.JSONObject;

public class TaskListBean extends RmiBean 
{
	public final static long serialVersionUID = RmiBean.RMI_TASK_LIST;
	public long getClassId()
	{
		return serialVersionUID;
	}
	
	public TaskListBean()
	{
		super.className = "CheckBean";
	}
	
	public void ExecCmd(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		switch(currStatus.getCmd())
		{
			case 10://添加
			case 11://编辑
				End_Time = End_Time + " 23:59:59";
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
			case 2://根据项目查询时间区间
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		    	break;
		}
		List<Object> CData = new ArrayList<Object>();
		if(msgBean.getStatus() == MsgBean.STA_SUCCESS){
			if(msgBean.getStatus() == MsgBean.STA_SUCCESS){
				ArrayList<?> list = (ArrayList<?>) msgBean.getMsg();
				CData = objToJson(list, CData);
			}
		}
		PrintWriter outprint = response.getWriter();
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		String jsonObj = JSONObject.toJSONString(CData);
		response.setCharacterEncoding("UTF-8");
		outprint.write(jsonObj);
	}
	

	public void ajaxGetAll(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone)
	{
		try 
		{
			getHtmlData(request);
			currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
			currStatus.getHtmlData(request, pFromZone);
			
			PrintWriter outprint = response.getWriter();
			
			msgBean = pRmi.RmiExec(1, this, 0, 25);
			List<Object> CData = new ArrayList<Object>();
			if(msgBean.getStatus() == MsgBean.STA_SUCCESS){
				ArrayList<?> list = (ArrayList<?>) msgBean.getMsg();
				CData = objToJson(list, CData);
			}
			
			request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
			String jsonObj = JSONObject.toJSONString(CData);
			response.setCharacterEncoding("UTF-8");
			outprint.write(jsonObj);
		}
		catch (Exception Ex)
		{
			Ex.printStackTrace();
		}
	}
	
	public List<Object> objToJson(ArrayList<?> list, List<Object> CData){
		Iterator<?> iterator = list.iterator();
		while (iterator.hasNext()) {
			TaskListBean RealJson = (TaskListBean) iterator.next();
			TaskListBean json = new TaskListBean();
			json.setSN(RealJson.getSN());
			json.setStart_Time(RealJson.getStart_Time());
			json.setEnd_Time(RealJson.getEnd_Time());
			json.setFinish_Time(RealJson.getFinish_Time());
			json.setType(RealJson.getType());
			json.setUploader(RealJson.getUploader());
			json.setStatus(RealJson.getStatus());
			json.setGJ_List(RealJson.getGJ_List());
			json.setGX_List(RealJson.getGX_List());
			json.setProject_Id(RealJson.getProject_Id());
			json.setDes(RealJson.getDes());
			json.setFinish_Des(RealJson.getFinish_Des());
			CData.add(json);
		}
		return CData;
		
	}
	
	public String getSql(int pCmd)
	{
		String Sql = "";
		switch (pCmd)
		{
			case 0://查询全部
				Sql = " select t.sn,  t.start_time, t.end_time, t.finish_time, t.type, t.uplader, t.status, t.gj_list, t.gx_list, t.project_id, t.des, t.finish_des "
						+ " from check t order by t.ctime";
				break;
			case 1://查询项目全部
				Sql = " select t.sn,  t.start_time, t.end_time, t.finish_time, t.type, t.uplader, t.status, t.gj_list, t.gx_list, t.project_id, t.des, t.finish_des "
						+ " from check t "
						+ "where project_id = '" + Project_Id + "' order by t.ctime";
				break;
			case 2://查询项目全部，时间区间
				Sql = " select t.sn,  t.start_time, t.end_time, t.finish_time, t.type, t.uplader, t.status, t.gj_list, t.gx_list, t.project_id, t.des, t.finish_des "
						+ " from check t "
						+ " where project_id = '" + Project_Id + "'"
						+ " and t.ctime >= date_format('"+currStatus.getVecDate().get(0).toString()+"', '%Y-%m-%d %H-%i-%S')"
						+ " and t.ctime <= date_format('"+currStatus.getVecDate().get(1).toString()+"', '%Y-%m-%d %H-%i-%S')"
						+ " order by t.ctime desc";
				break;
			case 3://查询项目全部，大于某个时间
				Sql = " select t.sn,  t.start_time, t.end_time, t.finish_time, t.type, t.uplader, t.status, t.gj_list, t.gx_list, t.project_id, t.des, t.finish_des "
						+ " from check t "
						+ " where project_id = '" + Project_Id + "'"
						+ " and t.ctime >= date_format('"+currStatus.getVecDate().get(0).toString()+"', '%Y-%m-%d %H-%i-%S')"
						+ " order by t.ctime desc";
				break;
			case 4://查询项目全部，小于某个时间
				Sql = " select t.sn,  t.start_time, t.end_time, t.finish_time, t.type, t.uplader, t.status, t.gj_list, t.gx_list, t.project_id, t.des, t.finish_des "
						+ " from check t "
						+ " where project_id = '" + Project_Id + "'"
						+ " and t.ctime <= date_format('"+currStatus.getVecDate().get(1).toString()+"', '%Y-%m-%d %H-%i-%S')"
						+ " order by t.ctime desc";
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
			setStart_Time(pRs.getString(2));
			setEnd_Time(pRs.getString(3));
			setFinish_Time(pRs.getString(4));
			setType(pRs.getString(5));
			setUploader(pRs.getString(6));
			setStatus(pRs.getString(7));
			setGJ_List(pRs.getString(8));
			setGX_List(pRs.getString(9));
			setProject_Id(pRs.getString(10));
			setDes(pRs.getString(11));
			setFinish_Des(pRs.getString(12));
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
			setStart_Time(CommUtil.StrToGB2312(request.getParameter("Start_Time")));
			setEnd_Time(CommUtil.StrToGB2312(request.getParameter("End_Time")));
			setFinish_Time(CommUtil.StrToGB2312(request.getParameter("Finish_Time")));
			setType(CommUtil.StrToGB2312(request.getParameter("Type")));
			setUploader(CommUtil.StrToGB2312(request.getParameter("Uploader")));
			setStatus(CommUtil.StrToGB2312(request.getParameter("Status")));
			setGJ_List(CommUtil.StrToGB2312(request.getParameter("GJ_List")));
			setGX_List(CommUtil.StrToGB2312(request.getParameter("GX_List")));
			setProject_Id(CommUtil.StrToGB2312(request.getParameter("Project_Id")));
			setDes(CommUtil.StrToGB2312(request.getParameter("Des")));
			setFinish_Des(CommUtil.StrToGB2312(request.getParameter("Finish_Des")));
			
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}
	

	private String SN;
	private String Start_Time;
	private String End_Time;
	private String Finish_Time;
	private String Type;
	private String Uploader;
	private String Status;
	private String GJ_List;
	private String GX_List;
	private String Project_Id;
	private String Des;
	private String Finish_Des;

	private String Sid;
	public String getSN() {
		return SN;
	}

	public void setSN(String sN) {
		SN = sN;
	}

	public String getStart_Time() {
		return Start_Time;
	}

	public void setStart_Time(String start_Time) {
		Start_Time = start_Time;
	}

	public String getEnd_Time() {
		return End_Time;
	}

	public void setEnd_Time(String end_Time) {
		End_Time = end_Time;
	}

	public String getFinish_Time() {
		return Finish_Time;
	}

	public void setFinish_Time(String finish_Time) {
		Finish_Time = finish_Time;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public String getUploader() {
		return Uploader;
	}

	public void setUploader(String uploader) {
		Uploader = uploader;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
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

	public String getProject_Id() {
		return Project_Id;
	}

	public void setProject_Id(String project_Id) {
		Project_Id = project_Id;
	}

	public String getDes() {
		return Des;
	}

	public void setDes(String des) {
		Des = des;
	}

	public String getFinish_Des() {
		return Finish_Des;
	}

	public void setFinish_Des(String finish_Des) {
		Finish_Des = finish_Des;
	}

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}
	
	
	
}
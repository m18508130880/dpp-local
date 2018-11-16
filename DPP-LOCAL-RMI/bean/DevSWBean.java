package bean;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rmi.Rmi;
import rmi.RmiBean;
import util.CommUtil;
import util.CurrStatus;
import util.MsgBean;

import com.alibaba.fastjson.JSONObject;

public class DevSWBean extends RmiBean
{
	public final static long	serialVersionUID	= RmiBean.RMI_DEVSW;

	public long getClassId()
	{
		return serialVersionUID;
	}

	public DevSWBean()
	{
		super.className = "DevSWBean";
	}
	
	public DevSWBean(CurrStatus currStatus)
	{
		super.className = "DevSWBean";
		this.currStatus = currStatus;
	}

	
	public void getDataNow(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);

		ArrayList<Object> CData = new ArrayList<Object>();
		msgBean = pRmi.RmiExec(0, this, 0, 0);
		if(msgBean.getStatus() == MsgBean.STA_SUCCESS){
			ArrayList<?> gj_List = (ArrayList<?>) msgBean.getMsg();
			Iterator<?> gj_iterator = gj_List.iterator();
			while (gj_iterator.hasNext())
			{
				DevSWBean bean = (DevSWBean) gj_iterator.next();
				CData.add(bean);
			}
		}
		PrintWriter outprint = null;
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		String json = JSONObject.toJSONString(CData);
		response.setCharacterEncoding("UTF-8");
		outprint = response.getWriter();
		outprint.write(json);
		outprint.flush();
	}

	/**
	 * 获取相应sql语句
	 * 
	 */
	public String getSql(int pCmd)
	{
		String Sql = "";
		switch (pCmd)
		{
			case 0:// 查询（项目）
				Sql = " select t.id, t.project_id, t.cname, t.gj_id, t.equip_id, t.equip_height, t.top_height, t.value, t.des" + 
					  " from view_dev_sw t " + 
					  " where t.project_id = '" + Project_Id + "' " + 
					  " order by t.id";
				break;
		}
		return Sql;
	}

	/**
	 * 将数据库中 结果集的数据 封装到DevGjBean中
	 * 
	 */
	public boolean getData(ResultSet pRs)
	{
		boolean IsOK = true;
		try
		{
			setId(pRs.getString(1));
			setProject_Id(pRs.getString(2));
			setCName(pRs.getString(3));
			setGJ_Id(pRs.getString(4));
			setEquip_Id(pRs.getString(5));
			setEquip_Height(pRs.getString(6));
			setTop_Height(pRs.getString(7));
			setValue(pRs.getString(8));
			setDes(pRs.getString(9));
		}
		catch (SQLException sqlExp)
		{
			sqlExp.printStackTrace();
		}
		return IsOK;
	}

	/**
	 * 得到页面数据
	 * 
	 * @param request
	 * @return 
	 */
	public boolean getHtmlData(HttpServletRequest request)
	{
		boolean IsOK = true;
		try
		{
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
			setId(CommUtil.StrToGB2312(request.getParameter("Id")));
			setProject_Id(CommUtil.StrToGB2312(request.getParameter("Project_Id")));
			setCName(CommUtil.StrToGB2312(request.getParameter("CName")));
			setEquip_Id(CommUtil.StrToGB2312(request.getParameter("Equip_Id")));
			setEquip_Height(CommUtil.StrToGB2312(request.getParameter("Equip_Height")));
			setTop_Height(CommUtil.StrToGB2312(request.getParameter("Top_Height")));
			setGJ_Id(CommUtil.StrToGB2312(request.getParameter("GJ_Id")));
			setValue(CommUtil.StrToGB2312(request.getParameter("Value")));
			setDes(CommUtil.StrToGB2312(request.getParameter("Des")));
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}

	private String	Id;
	private String	Project_Id;
	private String	CName;
	private String  GJ_Id;
	private String	Equip_Id;
	private String	Equip_Height;
	private String	Top_Height;
	private String	Value;
	private String	Des;
	
	private String Sid;

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getProject_Id() {
		return Project_Id;
	}

	public void setProject_Id(String project_Id) {
		Project_Id = project_Id;
	}

	public String getCName() {
		return CName;
	}

	public void setCName(String cName) {
		CName = cName;
	}

	public String getGJ_Id() {
		return GJ_Id;
	}

	public void setGJ_Id(String gJ_Id) {
		GJ_Id = gJ_Id;
	}

	public String getEquip_Height() {
		return Equip_Height;
	}

	public void setEquip_Height(String equip_Height) {
		Equip_Height = equip_Height;
	}

	public String getTop_Height() {
		return Top_Height;
	}

	public void setTop_Height(String top_Height) {
		Top_Height = top_Height;
	}

	public String getEquip_Id() {
		return Equip_Id;
	}

	public void setEquip_Id(String equip_Id) {
		Equip_Id = equip_Id;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String value) {
		Value = value;
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
package bean;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rmi.Rmi;
import rmi.RmiBean;
import util.CommUtil;
import util.CurrStatus;
import util.MsgBean;

/**UserInfoBean 登录筛选\人员信息
 * @author Cui
 */
public class UserInfoBean extends RmiBean 
{
	/**
	 * 赋值serialVersionUID = 13;
	 */
	public final static long serialVersionUID =RmiBean.RMI_USER_INFO;
	
	/** 获取serialVersionUID的值
	 * @see rmi.RmiBean#getClassId()
	 */
	public long getClassId()
	{
		return serialVersionUID;
	}
	
	/**无参构造器
	 * UserInfoBean
	 */
	public UserInfoBean()
	{
		super.className = "UserInfoBean";
	}
	
	
	/** 所有用户登录处
	 * @param request
	 * @param response
	 * @param pRmi
	 */
	public void Login(HttpServletRequest request, HttpServletResponse response, Rmi pRmi)
	{
		try
		{
			getHtmlData(request);
			currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
			if(null == currStatus)
				currStatus = new CurrStatus();
			currStatus.getHtmlData(request, false);
			
			String Url = "index.jsp";
			msgBean = pRmi.RmiExec(21, this, 0, 25);
			if(msgBean.getStatus() == MsgBean.STA_SUCCESS)
			{
				if(StrMd5.substring(0,20).trim().equalsIgnoreCase("system"))
				{
					
				}
				else if(StrMd5.substring(0,20).trim().equalsIgnoreCase("admin"))
				{
					//登入信息
					msgBean = pRmi.RmiExec(0, this, 0, 25);
					request.getSession().setAttribute("UserInfo_" + Sid, (UserInfoBean)((ArrayList<?>)msgBean.getMsg()).get(0));

					//公司信息
					CorpInfoBean corpInfoBean = new CorpInfoBean();
					msgBean = pRmi.RmiExec(0, corpInfoBean, 0, 25);
					if(null != msgBean.getMsg() && ((ArrayList<?>)msgBean.getMsg()).size() > 0)
					{
						request.getSession().setAttribute("Corp_Info_" + Sid, (CorpInfoBean)((ArrayList<?>)msgBean.getMsg()).get(0));
					}
					
					//项目信息
					ProjectInfoBean projectInfoBean = new ProjectInfoBean();
					msgBean = pRmi.RmiExec(0, projectInfoBean, 0, 25); 
					request.getSession().setAttribute("Project_Info_" + Sid, (Object)msgBean.getMsg());
					
					
					//设备信息
					EquipInfoBean equipmentInfoBean = new EquipInfoBean();
					msgBean = pRmi.RmiExec(0, equipmentInfoBean, 0, 25);
					request.getSession().setAttribute("Equip_Info_" + Sid, (Object)msgBean.getMsg());
					
					//管井查询-无分页
					DevGJBean devGJBean = new DevGJBean();
					msgBean = pRmi.RmiExec(1, devGJBean, 0, 25); 
					request.getSession().setAttribute("Map_GJ_" + Sid, (Object)msgBean.getMsg());
					
					/*//管线查询
					DevGXBean GXList = new DevGXBean();
					msgBean = pRmi.RmiExec(0, GXList, currStatus.getCurrPage()); 
					request.getSession().setAttribute("GXList_" + Sid, (Object)msgBean.getMsg());
					currStatus.setTotalRecord(msgBean.getCount());*/
					
					//管理权限
					UserRoleBean roleBean = new UserRoleBean();
			    	msgBean = pRmi.RmiExec(1, roleBean, 0, 25);
			    	request.getSession().setAttribute("User_Manage_Role_" + Sid, ((Object)msgBean.getMsg()));
					
					Url = "admin/Main.jsp?Sid=" + Sid;
				}
				else //user用户
				{
					//登入信息 可得到所登录的用户信息
					msgBean = pRmi.RmiExec(0, this, 0, 25);
					request.getSession().setAttribute("UserInfo_" + Sid, (UserInfoBean)((ArrayList<?>)msgBean.getMsg()).get(0));
					
					//公司信息
					CorpInfoBean corpInfoBean = new CorpInfoBean();
					msgBean = pRmi.RmiExec(0, corpInfoBean, 0, 25);
					if(null != msgBean.getMsg() && ((ArrayList<?>)msgBean.getMsg()).size() > 0)
					{
						request.getSession().setAttribute("Corp_Info_" + Sid, (CorpInfoBean)((ArrayList<?>)msgBean.getMsg()).get(0));
					}
					
					//管井查询-无分页
					DevGJBean devGJBean = new DevGJBean();
					msgBean = pRmi.RmiExec(1, devGJBean, 0, 25); 
					request.getSession().setAttribute("User_Map_GJ_" + Sid, (Object)msgBean.getMsg());
					
					//项目信息
					ProjectInfoBean projectInfoBean = new ProjectInfoBean();
					msgBean = pRmi.RmiExec(0, projectInfoBean, 0, 25); 
					request.getSession().setAttribute("Project_Info_" + Sid, (Object)msgBean.getMsg());
					
					//历史数据
					
					/*//管线查询
					DevGXBean GXList = new DevGXBean();
					msgBean = pRmi.RmiExec(0, GXList, currStatus.getCurrPage());
					request.getSession().setAttribute("GXList_" + Sid, (Object)msgBean.getMsg());
					currStatus.setTotalRecord(msgBean.getCount());*/
					
			    	//管理权限
					UserRoleBean roleBean = new UserRoleBean();
			    	msgBean = pRmi.RmiExec(1, roleBean, 0, 25);
			    	request.getSession().setAttribute("User_Manage_Role_" + Sid, ((Object)msgBean.getMsg()));

			    	Url = "user/MapMain.jsp?Sid=" + Sid;
				}
			}
			else
			{
				currStatus.setResult(MsgBean.GetResult(msgBean.getStatus()));
			}
			
			request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
			response.sendRedirect(Url);
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
	}
	

	/** 密码修改
	 * @param request
	 * @param response
	 * @param pRmi
	 */
	public void PwdEdit(HttpServletRequest request, HttpServletResponse response, Rmi pRmi)
	{
		try
		{
			getHtmlData(request);
			currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
			currStatus.getHtmlData(request,false);
			
			PrintWriter outprint = response.getWriter();
			String Resp = "9999";
			
			int pCmd = currStatus.getCmd();
			switch(currStatus.getCmd())
			{
				case 12://user 个人信息
					pCmd = currStatus.getCmd();
					break;
				case 22://user 密码修改
					pCmd = currStatus.getCmd();
					break;
				case 23://admin 密码修改
					pCmd = 22;
					break;
				case 24://admin 密码重置
					pCmd = 22;
					break;
			}
			
			msgBean = pRmi.RmiExec(pCmd, this, 0, 25);
			switch(msgBean.getStatus())
			{
				case 0://成功
					Resp = "0000";
					break;
				case 1001://用户不存在或密码错误
					Resp = "1001";
					break;
				default://失败
					Resp = "9999";
					break;
			}
			
			switch(currStatus.getCmd())
			{
				case 12://user 个人信息
				case 22://user 密码修改
					msgBean = pRmi.RmiExec(2, this, 0, 25);
					request.getSession().setAttribute("UserInfo_" + Sid, (UserInfoBean)((ArrayList<?>)msgBean.getMsg()).get(0));
					break;
				case 23://admin 密码修改
					msgBean = pRmi.RmiExec(2, this, 0, 25);
					request.getSession().setAttribute("Admin_" + Sid, (UserInfoBean)((ArrayList<?>)msgBean.getMsg()).get(0));
					break;
				case 24://admin 密码重置
					break;
			}
			
			request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
			outprint.write(Resp);
		} 
		catch (Exception Ex)
		{
			Ex.printStackTrace();
		}
	}
	
	
	/**
	 * @param request
	 * @param response
	 * @param pRmi
	 * 		DaoImpl
	 * @param pFromZone
	 * 		是一个boolean值, true表示: 从其他页面 到 当前页面 
	 * @throws ServletException
	 * @throws IOException
	 */
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
				msgBean = pRmi.RmiExec(1, this, 0, 25);
				request.getSession().setAttribute("User_Info_" + Sid, ((Object)msgBean.getMsg()));
			    currStatus.setJsp("User_Info.jsp?Sid=" + Sid);
			    
		    case 1://公司人员
		    	request.getSession().setAttribute("User_Info_" + Sid, ((Object)msgBean.getMsg()));
		    	currStatus.setJsp("User_Info.jsp?Sid=" + Sid);
		    	break;
		  
		}
		
		//功能权限
    	UserRoleBean roleBean = new UserRoleBean();
    	msgBean = pRmi.RmiExec(0, roleBean, 0, 25);
    	request.getSession().setAttribute("FP_Role_" + Sid, ((Object)msgBean.getMsg()));
    	
    	//管理权限
    	msgBean = pRmi.RmiExec(1, roleBean, 0, 25);
    	request.getSession().setAttribute("Manage_Role_" + Sid, ((Object)msgBean.getMsg()));
    	
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
	   	response.sendRedirect(currStatus.getJsp());
	}
	
	
	/** 检测账户是否存在 
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 */
	public void IdCheck(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone)
	{
		try 
		{
			getHtmlData(request);
			currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
			currStatus.getHtmlData(request, pFromZone);
			
			PrintWriter outprint = response.getWriter();
			String Resp = "3006";
			
			msgBean = pRmi.RmiExec(2, this, 0, 25);//查找是否有该用户存在
			switch(msgBean.getStatus())
			{
				case 0://已存在
					Resp = "3006";
					break;
				default://可用
					Resp = "0000";
					break;
			}
			
			request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
			outprint.write(Resp);
		}
		catch (Exception Ex)
		{
			Ex.printStackTrace();
		}
	}
	
	/** 根据传入的int值获取相应的数据库查询语句
	 * @see rmi.RmiBean#getSql(int)
	 *  
	 */
	public String getSql(int pCmd)
	{
		String Sql = "";
		switch (pCmd)
		{
			case 0://登陆信息
				Sql = " select Id, Pwd, CName, Dept_Id, Birthday, Tel, Fp_Role, Manage_Role, Project_Id, Project_Name, Status " +
					  " from view_user_info " +
					  " where upper(Id) = '"+ StrMd5.substring(0,20).trim() +"' ";
				break;
			case 1://公司人员
				Sql = " select Id, Pwd, CName, Dept_Id, Birthday, Tel, Fp_Role, Manage_Role, Project_Id, Project_Name, Status " +
				  	  " from view_user_info " +
				  	  " where Id <> 'system' and Id <> 'admin' order by Id asc";
				break;
			case 2://帐号检测
				Sql = " select Id, Pwd, CName, Dept_Id, Birthday, Tel, Fp_Role, Manage_Role, Project_Id, Project_Name, Status " +
					  " from view_user_info " +
					  " where upper(Id) = upper('"+ Id +"') ";
				break;
			case 10://员工添加
				Sql = " insert into user_info(Id, CName, Dept_Id, Birthday, Tel, Fp_Role, Manage_Role, Project_Id , Status )" +
					  " values('"+Id+"', '"+CName+"',  '"+Dept_Id+"','"+Birthday+"', '"+Tel+"', '"+Fp_Role+"', '"+Manage_Role+"', '"+Project_Id+"', '"+Status+"')";
				break;
			case 11://员工修改
				Sql = " update user_info set cname = '"+CName+"', birthday = '"+Birthday+"',  tel = '"+Tel+"', status = '"+Status+"', " +
					  " dept_id = '"+Dept_Id+"', manage_role = '"+Manage_Role+"',  Project_Id = '"+Project_Id+"', fp_role = '"+Fp_Role+"' " +
					  " where id = '"+Id+"' ";
				break;
			case 12://个人修改
				Sql = " update user_info set cname = '"+CName+"',  birthday = '"+Birthday+"',  tel = '"+Tel+"' " +
				  	  " where id = '"+Id+"' ";
				break;
			case 21://登录验证
				Sql = "{? = call RMI_LOGIN('"+StrMd5+"', '"+currStatus.getCheckCode()+"')}";
				break;
			case 22://密码修改
				Sql = "{? = call RMI_PWD_EDIT('"+getId()+"','" + getPwd() +"', '"+getNewPwd()+"')}";
				break;
		}
		return Sql;
	}
	
	/** 获取从数据库结果集中查询到的数据封装到UserInfoBean中
	 * @see   rmi.RmiBean#getData(java.sql.ResultSet)
	 * @param ResultSet
	 * @return 返回一个boolean值   表示注入成功与否
	 */
	public boolean getData(ResultSet pRs)
	{
		boolean IsOK = true;
		try
		{
			setId(pRs.getString(1));
			setPwd(pRs.getString(2));
			setCName(pRs.getString(3));
			setDept_Id(pRs.getString(4));
			setBirthday(pRs.getString(5));
			setTel(pRs.getString(6));
			setFp_Role(pRs.getString(7));
			setManage_Role(pRs.getString(8));		
			setProject_Id(pRs.getString(9));
			setProject_Name(pRs.getString(10));
			setStatus(pRs.getString(11));
		} 
		catch (SQLException sqlExp) 
		{
			sqlExp.printStackTrace();
		}		
		return IsOK;
	}
	
	/**获取request中UserInfoBean 数据封装到bean中
	 * @param request
	 * @return 返回一个boolean值   表示注入成功与否
	 */
	public boolean getHtmlData(HttpServletRequest request) 
	{		
		boolean IsOK = true;
		try 
		{		
			setId(CommUtil.StrToGB2312(request.getParameter("Id")));
			setPwd(CommUtil.StrToGB2312(request.getParameter("Pwd")));
			setCName(CommUtil.StrToGB2312(request.getParameter("CName")));
			setDept_Id(CommUtil.StrToGB2312(request.getParameter("Dept_Id")));
			setBirthday(CommUtil.StrToGB2312(request.getParameter("Birthday")));
			setTel(CommUtil.StrToGB2312(request.getParameter("Tel")));
			setFp_Role(CommUtil.StrToGB2312(request.getParameter("Fp_Role")));
			setManage_Role(CommUtil.StrToGB2312(request.getParameter("Manage_Role")));
			setProject_Id(CommUtil.StrToGB2312(request.getParameter("Project_Id")));
			setProject_Name(CommUtil.StrToGB2312(request.getParameter("Project_Name")));
			setStatus(CommUtil.StrToGB2312(request.getParameter("Status")));
			
			setStrMd5(CommUtil.StrToGB2312(request.getParameter("StrMd5")));
			setNewPwd(CommUtil.StrToGB2312(request.getParameter("NewPwd")));
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
		} 
		catch (Exception Exp) 
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}
	
	private String Id;
	private String Pwd;
	private String CName;
	private String Dept_Id;
	private String Birthday;
	private String Tel;
	private String Fp_Role;
	private String Manage_Role;
	private String Project_Id;
	private String Project_Name;

	private String Status;
	
	private String StrMd5;
	private String NewPwd;
	private String Func_Corp_Id;
	private String Sid;
	
	
	
	public String getProject_Id() {
		return Project_Id;
	}

	public void setProject_Id(String project_Id) {
		Project_Id = project_Id;
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

	public String getBirthday() {
		return Birthday;
	}

	public void setBirthday(String birthday) {
		Birthday = birthday;
	}


	public String getTel() {
		return Tel;
	}

	public void setTel(String tel) {
		Tel = tel;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getPwd() {
		return Pwd;
	}

	public void setPwd(String pwd) {
		Pwd = pwd;
	}

	public String getDept_Id() {
		return Dept_Id;
	}

	public void setDept_Id(String deptId) {
		Dept_Id = deptId;
	}

	public String getManage_Role() {
		return Manage_Role;
	}

	public void setManage_Role(String manageRole) {
		Manage_Role = manageRole;
	}
	
	public String getFp_Role() {
		return Fp_Role;
	}

	public void setFp_Role(String fpRole) {
		Fp_Role = fpRole;
	}
	
	public String getStrMd5() {
		return StrMd5;
	}

	public void setStrMd5(String strMd5) {
		StrMd5 = strMd5;
	}

	public String getNewPwd() {
		return NewPwd;
	}

	public void setNewPwd(String newPwd) {
		NewPwd = newPwd;
	}

	public String getFunc_Corp_Id() {
		return Func_Corp_Id;
	}

	public void setFunc_Corp_Id(String funcCorpId) {
		Func_Corp_Id = funcCorpId;
	}
	public String getProject_Name() {
		return Project_Name;
	}
	
	public void setProject_Name(String project_Name) {
		Project_Name = project_Name;
	}

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}
}
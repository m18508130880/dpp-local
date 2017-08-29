package servlet;

import java.io.IOException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rmi.*;
import util.*;
import bean.*;

////0全部查询 2插入 3修改 4删除 10～19单个查询
public class MainServlet extends HttpServlet
{
	public final static long serialVersionUID = 1000;
	private Rmi m_Rmi = null;
	private String rmiUrl = null;
	private Connect connect = null;
	public ServletConfig Config;
	
	public final ServletConfig getServletConfig() 
	{
		return Config;
	}
	
	public void init(ServletConfig pConfig) throws ServletException
	{	
		Config = pConfig;
		connect = new Connect();
		connect.config = pConfig;
		connect.ReConnect();
	}		
    protected void doGet(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException
    {
        this.processRequest(request, response);
    }
    protected void doPost(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException
    {
        this.processRequest(request, response);
    }
    protected void doPut(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException
    {
        this.processRequest(request, response);
    }
    protected void doTrace(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException
    {
        this.processRequest(request, response);
    }
    

    protected void processRequest(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException
    {
    	if(connect.Test()== false)
    	{   
    		request.getSession().setAttribute("ErrMsg", CommUtil.StrToGB2312("RMI服务端未正常运行，无法登陆！"));
    		response.sendRedirect(getUrl(request) + "error.jsp");
    		return;
    	}
    	
        response.setContentType("text/html; charset=gbk");
        String strUrl = request.getRequestURI();
        System.out.println(strUrl);
        String strSid = request.getParameter("Sid");
        String[] str = strUrl.split("/");
        strUrl = str[str.length - 1];
        
        System.out.println("Sid:" + strSid);
        System.out.println("====================" + strUrl);
        
        //首页
        if(strUrl.equals("index.do"))
        {
        	CheckCode.CreateCheckCode(request, response, strSid);
        	return;
        }
        else if(strUrl.equalsIgnoreCase("AdminILogout.do"))                      //第二层:admin安全退出
        {
        	request.getSession().removeAttribute("CurrStatus_" + strSid);
        	request.getSession().removeAttribute("Admin_" + strSid);
        	request.getSession().removeAttribute("Corp_Info_" + strSid);
        	request.getSession().removeAttribute("User_Info_" + strSid);
        	request.getSession().removeAttribute("User_Stat_" + strSid);
        	request.getSession().removeAttribute("FP_Role_" + strSid);
        	request.getSession().removeAttribute("Manage_Role_" + strSid);
        	request.getSession().removeAttribute("FP_Info_" + strSid);
        	request.getSession().removeAttribute("Crm_Info_" + strSid);
        	request.getSession().removeAttribute("Ccm_Info_" + strSid);
        	//request.getSession().invalidate();
        	response.getWriter().write("<script language = javascript>window.parent.location.href='../index.jsp'</script>");
        }
        else if(strUrl.equalsIgnoreCase("ILogout.do"))                           //第二层:user安全退出
        {
        	request.getSession().removeAttribute("CurrStatus_" + strSid);
        	request.getSession().removeAttribute("UserInfo_" + strSid);
        	request.getSession().removeAttribute("User_Corp_Info_" + strSid);
        	request.getSession().removeAttribute("User_Data_Attr_" + strSid);
        	request.getSession().removeAttribute("User_User_Info_" + strSid);
        	request.getSession().removeAttribute("User_FP_Role_" + strSid);
        	request.getSession().removeAttribute("User_Manage_Role_" + strSid);
        	request.getSession().removeAttribute("Env_" + strSid);
        	request.getSession().removeAttribute("Env_His_" + strSid);
        	request.getSession().removeAttribute("Week_" + strSid);
        	request.getSession().removeAttribute("Month_" + strSid);
        	request.getSession().removeAttribute("Year_" + strSid);
        	request.getSession().removeAttribute("Graph_" + strSid);
        	request.getSession().removeAttribute("Alarm_Info_" + strSid);
        	request.getSession().removeAttribute("Alert_Info_" + strSid);    	
        	request.getSession().removeAttribute("BYear_" + strSid);
        	request.getSession().removeAttribute("BMonth_" + strSid);
        	request.getSession().removeAttribute("BWeek_" + strSid);
        	request.getSession().removeAttribute("EYear_" + strSid);
        	request.getSession().removeAttribute("EMonth_" + strSid);
        	request.getSession().removeAttribute("EWeek_" + strSid);
        	request.getSession().removeAttribute("BDate_" + strSid);
        	request.getSession().removeAttribute("EDate_" + strSid);
        	request.getSession().removeAttribute("Pro_G_" + strSid);        
        	//request.getSession().invalidate();
        	response.getWriter().write("<script language = javascript>window.parent.location.href='../index.jsp'</script>");
        }
        else if(strUrl.equalsIgnoreCase("IILogout.do"))                          //第三层:user安全退出
        {
        	request.getSession().removeAttribute("CurrStatus_" + strSid);
        	request.getSession().removeAttribute("UserInfo_" + strSid);
        	request.getSession().removeAttribute("User_Corp_Info_" + strSid);
        	request.getSession().removeAttribute("User_Device_Detail_" + strSid);
        	request.getSession().removeAttribute("User_Data_Device_" + strSid);
        	request.getSession().removeAttribute("User_Data_Attr_" + strSid);
        	request.getSession().removeAttribute("User_User_Info_" + strSid);
        	request.getSession().removeAttribute("User_FP_Role_" + strSid);
        	request.getSession().removeAttribute("User_Manage_Role_" + strSid);
        	request.getSession().removeAttribute("Env_" + strSid);
        	request.getSession().removeAttribute("Env_His_" + strSid);
        	request.getSession().removeAttribute("Week_" + strSid);
        	request.getSession().removeAttribute("Month_" + strSid);
        	request.getSession().removeAttribute("Year_" + strSid);
        	request.getSession().removeAttribute("Graph_" + strSid);
        	request.getSession().removeAttribute("Alarm_Info_" + strSid);
        	request.getSession().removeAttribute("Alert_Info_" + strSid);
        	request.getSession().removeAttribute("BYear_" + strSid);
        	request.getSession().removeAttribute("BMonth_" + strSid);
        	request.getSession().removeAttribute("BWeek_" + strSid);
        	request.getSession().removeAttribute("EYear_" + strSid);
        	request.getSession().removeAttribute("EMonth_" + strSid);
        	request.getSession().removeAttribute("EWeek_" + strSid);
        	request.getSession().removeAttribute("BDate_" + strSid);
        	request.getSession().removeAttribute("EDate_" + strSid);       
        	//request.getSession().invalidate();
        	response.getWriter().write("<script language = javascript>window.parent.location.href='../../index.jsp'</script>");
        }
        
        /**************************************公用***************************************************/
        else if (strUrl.equalsIgnoreCase("Login.do"))						         //登录
        	new UserInfoBean().Login(request, response, m_Rmi);
        else if (strUrl.equalsIgnoreCase("PwdEdit.do"))						 	     //密码修改
        	new UserInfoBean().PwdEdit(request, response, m_Rmi);
        
        /**************************************admin***************************************************/
        else if (strUrl.equalsIgnoreCase("Admin_Corp_Info.do"))				         //公司信息
        	new CorpInfoBean().ExecCmd(request, response, m_Rmi, false);             
        else if (strUrl.equalsIgnoreCase("Admin_User_Info.do"))				         //人员信息
        	new UserInfoBean().ExecCmd(request, response, m_Rmi, false);       
        else if (strUrl.equalsIgnoreCase("Admin_IdCheck.do"))						 //人员信息-帐号检测
        	new UserInfoBean().IdCheck(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_User_Role.do"))				         //功能权限
        	new UserRoleBean().ExecCmd(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_User_RoleOP.do"))				     //功能权限-编辑
        	new UserRoleBean().RoleOP(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_Manage_Role.do"))				     //管理权限
        	new UserRoleBean().ExecCmd(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_Manage_RoleOP.do"))				     //管理权限
        	new UserRoleBean().RoleOP(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_Project_Info.do"))	                 //项目信息管理
        	new ProjectInfoBean().ExecCmd(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Project_IdCheck.do"))						 //项目ID检测
        	new ProjectInfoBean().IdCheck(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_Equip_Info.do"))	                 //设备信息管理
        	new EquipInfoBean().ExecCmd(request, response, m_Rmi, false); 
        else if (strUrl.equalsIgnoreCase("Equip_IdCheck.do"))						 //设备ID检测
        	new EquipInfoBean().IdCheck(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Equip_Restart.do"))						 //设备重启指令
        	new EquipInfoBean().Restart(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Equip_Compare_Time.do"))					 //设备对时指令
        	new EquipInfoBean().Compare_Time(request, response, m_Rmi, false);

        /**************************************admin-管井**********************************************/  
        else if (strUrl.equalsIgnoreCase("Admin_ToPo_GJ.do"))					//GIS监控-管井
        	new DevGJBean().ToPo(request, response, m_Rmi, false);  
        else if (strUrl.equalsIgnoreCase("Admin_Drag_GJ.do"))					//GIS监控-管井-更新坐标
        	new DevGJBean().doDragging(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_LZGX.do"))						//GIS监控-管井-管线拉直
        	new DevGXBean().straightenGX(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_HTLZ.do"))						//GIS监控-管井-管线拉直-回退
        	new DevGXBean().unStraightenGX(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_DevGJ_Info.do"))				//管井查询
        	new DevGJBean().ExecCmd(request, response, m_Rmi, false);           
        else if (strUrl.equalsIgnoreCase("Admin_Import_GJ.do"))					//Excel表导入管井（新）
        	new DevGJBean().ImportExcel(request, response, m_Rmi, false, Config);
        else if (strUrl.equalsIgnoreCase("Admin_Import_GD.do"))					//Excel表导入管道（新）
        	new DevGXBean().ImportExcel(request, response, m_Rmi, false, Config);
        else if (strUrl.equalsIgnoreCase("Admin_Update_GJ.do"))					//Excel表更新管井（新）
        	new DevGJBean().UpdateExcel(request, response, m_Rmi, false, Config);
        else if (strUrl.equalsIgnoreCase("Admin_Update_GD.do"))					//Excel表更新管道（新）
        	new DevGXBean().UpdateExcel(request, response, m_Rmi, false, Config);
        else if (strUrl.equalsIgnoreCase("Admin_File_GJ_Export.do"))			//管井Excel表导出
        	new DevGJBean().XLQRExcel(request, response, m_Rmi, false);  
        else if (strUrl.equalsIgnoreCase("Admin_File_GX_Export.do"))			//管线Excel表导出
        	new DevGXBean().XLQRExcel(request, response, m_Rmi, false);  
   
       /***************************************admin-管线**********************************************/ 
        else if (strUrl.equalsIgnoreCase("Admin_ToPo_GX.do"))						 //GIS监控-管线
        	new DevGXBean().ToPo(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_DevGX_Info.do"))			         //管线查询
        	new DevGXBean().ExecCmd(request, response, m_Rmi, false); 
        else if (strUrl.equalsIgnoreCase("Admin_DevGX_Suggest.do"))			         //管线查询
        	new DevGXBean().GXSuggest(request, response, m_Rmi, false); 
        else if (strUrl.equalsIgnoreCase("Admin_GJ_Scene.do"))	                     //窨井图片上传
        	new DevGJBean().DetailSenceUp(request, response, m_Rmi, false, Config); 
        
        /************************************user-管井**********************************************/  
        else if (strUrl.equalsIgnoreCase("User_ToPo_GJ.do"))				        //GIS监控-管井
        	new DevGJBean().ToPo(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("User_ToPo_GX.do"))			            //GIS监控-管线
        	new DevGXBean().ToPo(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("User_downloadGIS.do"))			        //GIS监控-导出地图
        	new MapImageBean().downloadGIS(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("User_drawFrame.do"))			        	//GIS监控-画显示框
        	new MapImageBean().drawFrame(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("User_DevGJ_Info.do"))				        //管井查询
        	new DevGJBean().ExecCmd(request, response, m_Rmi, false);    
        else if (strUrl.equalsIgnoreCase("User_DevGX_Info.do"))				        //管线查询
        	new DevGXBean().ExecCmd(request, response, m_Rmi, false); 
        else if (strUrl.equalsIgnoreCase("User_Equip_Info.do"))	                    //设备查询
        	new EquipInfoBean().ExecCmd(request, response, m_Rmi, false); 
 
        else if (strUrl.equalsIgnoreCase("User_DataGJ_His.do"))				        //管井表格数据
        	new DataGJBean().HistoryData(request, response, m_Rmi, false);
        
        else if (strUrl.equalsIgnoreCase("User_DataGX_His.do"))				        //管线表格数据
        	new DataGXBean().HistoryData(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("User_Announce.do"))				        //tab数据显示
        	new CorpInfoBean().ExecCmd(request, response, m_Rmi, false);    
        
        /***********************************user-图表分析*****************************************************/
        else if (strUrl.equalsIgnoreCase("User_Graph_Cut.do"))				        //管段剖面图
        	new DevGXBean().ExecCmd(request, response, m_Rmi, false);  
        else if (strUrl.equalsIgnoreCase("User_Graph_Curve.do"))				    //管井折线图
        	new DataGJBean().GraphData(request, response, m_Rmi, false); 
        
        /************************************user-管井模拟*****************************************************/
        else if (strUrl.equalsIgnoreCase("Analog_rainfall.do"))				        //上传数据
        	new AnalogBean().ImportData(request, response, m_Rmi, false, Config);  
        else if (strUrl.equalsIgnoreCase("DeleteData.do"))				       		//删除数据
        	new DevGJBean().DeleteData(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Analog_ToPo_GJ.do"))				        //查询单个子系统
        	new DevGJBean().AnalogToPo(request, response, m_Rmi, false);  
        else if (strUrl.equalsIgnoreCase("FileName_ToPo_GJ.do"))				    //返回子系统号
        	new DevGJBean().FileToPo(request, response, m_Rmi, false);  
        else if (strUrl.equalsIgnoreCase("Analog_DevGJ_Info.do"))				    //时段水位深度
        	new DevGJBean().AnalogExecCmd(request, response, m_Rmi, false); 
        else if (strUrl.equalsIgnoreCase("Analog_Graph_Cut.do"))				    //时段水位剖面图
        	new DevGXBean().AnalogExecCmd(request, response, m_Rmi, false);  
        else if (strUrl.equalsIgnoreCase("Analog_Graph_Curve.do"))				    //管井水位折线
        	new DataGJBean().AnalogGraph(request, response, m_Rmi, false);  
        else if (strUrl.equalsIgnoreCase("Analog_WaterAcc.do"))				    	//全部时段积水深度
        	new DevGJBean().WaterAcc(request, response, m_Rmi, false);  
        
        /************************************user-管线模拟*****************************************************/
        else if (strUrl.equalsIgnoreCase("Analog_DevGX_Info.do"))				    	//管线信息
        	new DevGXBean().AnalogFlow(request, response, m_Rmi, false); 
        else if (strUrl.equalsIgnoreCase("Analog_Graph_FlowLoad.do"))				    //流量负荷
        	new DevGXBean().AnalogFlow(request, response, m_Rmi, false);  
        else if (strUrl.equalsIgnoreCase("Analog_Graph_ActualFlow.do"))				    //实际流量
        	new DevGXBean().AnalogFlow(request, response, m_Rmi, false);  
        else if (strUrl.equalsIgnoreCase("Analog_Graph_FlowRate.do"))				    //流速
        	new DevGXBean().AnalogFlow(request, response, m_Rmi, false);  
        
        /************************************user-告警*****************************************************/
        else if (strUrl.equalsIgnoreCase("Alert_Info.do"))				    			//告警信息
        	new AlertInfoBean().ExecCmd(request, response, m_Rmi, false); 
        else if (strUrl.equalsIgnoreCase("Alert_Now.do"))				    			//最新告警
        	new AlertInfoBean().AlertNow(request, response, m_Rmi, false); 
        
        /************************************user-泵站*****************************************************/
        else if (strUrl.equalsIgnoreCase("Admin_DevBZ_Info.do"))				    			
        	new DevBZBean().ExecCmd(request, response, m_Rmi, false); 
    }
    
    private class Connect extends Thread
	{
    	private ServletConfig config = null;
    	public boolean Test()
    	{
    		int i = 0;
        	boolean ok = false;
        	while(3 > i)
    		{        		
    	    	try
    			{   
    	    		if(i != 0) sleep(500);
    	    		ok = m_Rmi.Test();
    	    		i = 3;
    	    		ok = true;
    			}
    	    	catch(Exception e)
    			{    	    		
    	    		ReConnect();
    	    		i++;
    			}
    		}
    		return ok;
    	}
    	private void ReConnect()
    	{
    		try
    		{
    			rmiUrl = config.getInitParameter("rmiUrl");
    			Context context = new InitialContext();
    			m_Rmi = (Rmi) context.lookup(rmiUrl);
    		}
    		catch(Exception e)
    		{	
    			e.printStackTrace();
    		}
    	}
    }
	public final static String getUrl(HttpServletRequest request)
	{
		String url = "http://" + request.getServerName() + ":"
				+ request.getServerPort() + request.getContextPath() + "/";
		return url;
	}
	
} 
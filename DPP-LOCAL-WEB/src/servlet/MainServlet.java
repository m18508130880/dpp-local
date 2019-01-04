package servlet;

import java.io.IOException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rmi.Rmi;
import util.CheckCode;
import util.CommUtil;
import bean.AlertInfoBean;
import bean.AnalogBean;
import bean.AnalogComputeBean;
import bean.CheckTaskBean;
import bean.CheckTaskGJBean;
import bean.CheckTaskGXBean;
import bean.CorpInfoBean;
import bean.DataBean;
import bean.DataGJBean;
import bean.DataGXBean;
import bean.DataHandBean;
import bean.DataNowAddBean;
import bean.DataNowBean;
import bean.DevBZBean;
import bean.DevGJBean;
import bean.DevGXBean;
import bean.DevHandBean;
import bean.DevMapBean;
import bean.DevSWBean;
import bean.EquipAlertBean;
import bean.EquipInfoBean;
import bean.MacAnalysisBean;
import bean.MacConfigBean;
import bean.MacReadBean;
import bean.MacReadTaskBean;
import bean.MacSendBean;
import bean.MacSendTaskBean;
import bean.MapImageBean;
import bean.ProjectInfoBean;
import bean.TextLLJBean;
import bean.ThreeGJBean;
import bean.ThreeGXBean;
import bean.UserInfoBean;
import bean.UserRoleBean;
import bean.WeatherBean;

////0ȫ����ѯ 2���� 3�޸� 4ɾ�� 10��19������ѯ
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
    		request.getSession().setAttribute("ErrMsg", CommUtil.StrToGB2312("RMI�����δ�������У��޷���½��"));
    		response.sendRedirect(getUrl(request) + "error.jsp");
    		return;
    	}
    	
        response.setContentType("text/html; charset=gbk");
        String strUrl = request.getRequestURI();
        //System.out.println(strUrl);
        String strSid = request.getParameter("Sid");
        String[] str = strUrl.split("/");
        strUrl = str[str.length - 1];
        
        System.out.println("Sid:" + strSid);
        System.out.println("====================" + strUrl);
        
        //��ҳ
        if(strUrl.equals("index.do"))
        {
        	CheckCode.CreateCheckCode(request, response, strSid);
        	return;
        }
        else if (strUrl.equalsIgnoreCase("dtu.do")){						     	// ��ѯDTU����
        	new DataNowAddBean().getDTU(request, response, m_Rmi, false);
        }
        else if(strUrl.equalsIgnoreCase("AdminILogout.do"))                      //�ڶ���:admin��ȫ�˳�
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
        else if(strUrl.equalsIgnoreCase("ILogout.do"))                           //�ڶ���:user��ȫ�˳�
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
        else if(strUrl.equalsIgnoreCase("IILogout.do"))                          //������:user��ȫ�˳�
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
        
        /**************************************����***************************************************/
        else if (strUrl.equalsIgnoreCase("Login.do"))						         //��¼
        	new UserInfoBean().Login(request, response, m_Rmi);
        else if (strUrl.equalsIgnoreCase("PwdEdit.do"))						 	     //�����޸�
        	new UserInfoBean().PwdEdit(request, response, m_Rmi);
        
        /**************************************admin***************************************************/
        else if (strUrl.equalsIgnoreCase("Admin_Corp_Info.do"))				         //��˾��Ϣ
        	new CorpInfoBean().ExecCmd(request, response, m_Rmi, false);             
        else if (strUrl.equalsIgnoreCase("Admin_User_Info.do"))				         //��Ա��Ϣ
        	new UserInfoBean().ExecCmd(request, response, m_Rmi, false);       
        else if (strUrl.equalsIgnoreCase("Admin_IdCheck.do"))						 //��Ա��Ϣ-�ʺż��
        	new UserInfoBean().IdCheck(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_User_Role.do"))				         //����Ȩ��
        	new UserRoleBean().ExecCmd(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_User_RoleOP.do"))				     //����Ȩ��-�༭
        	new UserRoleBean().RoleOP(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_Manage_Role.do"))				     //����Ȩ��
        	new UserRoleBean().ExecCmd(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_Manage_RoleOP.do"))				     //����Ȩ��
        	new UserRoleBean().RoleOP(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_Project_Info.do"))	                 //��Ŀ��Ϣ����
        	new ProjectInfoBean().ExecCmd(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Project_IdCheck.do"))						 //��ĿID���
        	new ProjectInfoBean().IdCheck(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_Equip_Info.do"))	                 //�豸��Ϣ����
        	new EquipInfoBean().ExecCmd(request, response, m_Rmi, false); 
        else if (strUrl.equalsIgnoreCase("Admin_Equip_GJInfo.do"))	                 //�����豸��ȡ�ܾ����
        	new EquipInfoBean().getAllId(request, response, m_Rmi, false); 
        else if (strUrl.equalsIgnoreCase("Admin_Equip_Alert.do"))	                 //�豸��������Ϣ
        	new EquipAlertBean().ExecCmd(request, response, m_Rmi, false); 
        else if (strUrl.equalsIgnoreCase("Equip_IdCheck.do"))						 //�豸ID���
        	new EquipInfoBean().IdCheck(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Equip_Restart.do"))						 //�豸����ָ��
        	new EquipInfoBean().Restart(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Equip_Compare_Time.do"))					 //�豸��ʱָ��
        	new EquipInfoBean().Compare_Time(request, response, m_Rmi, false);

        /**************************************admin-�ܾ�**********************************************/  
        else if (strUrl.equalsIgnoreCase("Admin_ToPo_GJ.do"))					//GIS���-�ܾ�
        	new DevGJBean().ToPo(request, response, m_Rmi, false);  
        else if (strUrl.equalsIgnoreCase("Admin_Drag_GJ.do"))					//GIS���-�ܾ�-��������
        	new DevGJBean().doDragging(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("User_Rotation_GJ.do"))				//GIS���-�ܾ�-������ת�Ƕ�
        	new DevGJBean().doRotation(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_LZGX.do"))						//GIS���-�ܾ�-������ֱ
        	new DevGXBean().straightenGX(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_HTLZ.do"))						//GIS���-�ܾ�-������ֱ-����
        	new DevGXBean().unStraightenGX(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_DevGJ_Info.do"))				//�ܾ���ѯ
        	new DevGJBean().ExecCmd(request, response, m_Rmi, false);           
        else if (strUrl.equalsIgnoreCase("Admin_Import_GJ.do"))					//Excel����ܾ����£�
        	new DevGJBean().ImportExcel(request, response, m_Rmi, false, Config);
        else if (strUrl.equalsIgnoreCase("Admin_Import_GD.do"))					//Excel����ܵ����£�
        	new DevGXBean().ImportExcel(request, response, m_Rmi, false, Config);
        else if (strUrl.equalsIgnoreCase("Admin_Update_GJ.do"))					//Excel����¹ܾ����£�
        	new DevGJBean().UpdateExcel(request, response, m_Rmi, false, Config);
        else if (strUrl.equalsIgnoreCase("Admin_Update_GD.do"))					//Excel����¹ܵ����£�
        	new DevGXBean().UpdateExcel(request, response, m_Rmi, false, Config);
        else if (strUrl.equalsIgnoreCase("Admin_File_GJ_Export.do"))			//�ܾ�Excel����
        	new DevGJBean().XLQRExcel(request, response, m_Rmi, false);  
        else if (strUrl.equalsIgnoreCase("Admin_File_GX_Export.do"))			//����Excel����
        	new DevGXBean().XLQRExcel(request, response, m_Rmi, false);
   
       /***************************************admin-����**********************************************/ 
        else if (strUrl.equalsIgnoreCase("Admin_ToPo_GX.do"))						 //GIS���-����
        	new DevGXBean().ToPo(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_DevGX_Info.do"))			         //���߲�ѯ
        	new DevGXBean().ExecCmd(request, response, m_Rmi, false); 
        else if (strUrl.equalsIgnoreCase("Admin_DevGX_Suggest.do"))			         //���߲�ѯ
        	new DevGXBean().GXSuggest(request, response, m_Rmi, false); 
        else if (strUrl.equalsIgnoreCase("Admin_GJ_Scene.do"))	                     //񿾮ͼƬ�ϴ�
        	new DevGJBean().DetailSenceUp(request, response, m_Rmi, false, Config); 
        
        /************************************user-�ܾ�**********************************************/  
        else if (strUrl.equalsIgnoreCase("User_ToPo_GJ.do"))				        //GIS���-�ܾ�
        	new DevGJBean().ToPo(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("User_ToPo_GX.do"))			            //GIS���-����
        	new DevGXBean().ToPo(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("User_downloadGIS.do"))			        //GIS���-������ͼ
        	new MapImageBean().downloadGIS(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("User_drawFrame.do"))			        	//GIS���-����ʾ��
        	new MapImageBean().drawFrame(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("User_DevGJ_Info.do"))				        //�ܾ���ѯ
        	new DevGJBean().ExecCmd(request, response, m_Rmi, false);    
        else if (strUrl.equalsIgnoreCase("User_DevGX_Info.do"))				        //���߲�ѯ
        	new DevGXBean().ExecCmd(request, response, m_Rmi, false); 
        else if (strUrl.equalsIgnoreCase("User_Equip_Info.do"))	                    //�豸��ѯ
        	new EquipInfoBean().ExecCmd(request, response, m_Rmi, false); 
        else if (strUrl.equalsIgnoreCase("getDataNow.do"))				        	//��ȡʵʱ����
        	new DataGJBean().getDataNow(request, response, m_Rmi, false);
 
        else if (strUrl.equalsIgnoreCase("User_DataGJ_His.do"))				        //�ܾ��������
        	new DataGJBean().HistoryData(request, response, m_Rmi, false);
        
        else if (strUrl.equalsIgnoreCase("User_DataGX_His.do"))				        //���߱������
        	new DataGXBean().HistoryData(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("User_Announce.do"))				        //tab������ʾ
        	new CorpInfoBean().ExecCmd(request, response, m_Rmi, false);    

        else if (strUrl.equalsIgnoreCase("User_DataNow.do"))					        //��ȡʵʱ����
        	new DataNowBean().getDataNow(request, response, m_Rmi, false);  
        
        /***********************************user-ͼ�����*****************************************************/
        else if (strUrl.equalsIgnoreCase("getSumOutGJ.do"))				        	//������ͼǰȡ���ų�������
        	new DevGXBean().getSumOutGJ(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("getOutGJId.do"))				        	//������ͼ֮ǰ����ų��ڣ�ȡ�ùܾ����list
        	new DevGXBean().getOutGJId(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("getGJListAndGXList.do"))				        	//ȡ��gjLIst��gxList
        	new DevGXBean().getGJListAndGXList(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("User_Graph_Cut.do"))				        //�ܶ�����ͼ
        	new DevGXBean().ExecCmd(request, response, m_Rmi, false);  
        else if (strUrl.equalsIgnoreCase("User_Graph_Curve.do"))				    //�ܾ�����ͼ
        	new DataGJBean().GraphData(request, response, m_Rmi, false); 
        
        /************************************user-�ܾ�ģ��*****************************************************/
        else if (strUrl.equalsIgnoreCase("Analog_Compute.do"))				        //ģ��������ݴ������ݿ�
        	new AnalogComputeBean().Compute(request, response, m_Rmi, false);  
        else if (strUrl.equalsIgnoreCase("getAnalogData.do"))			        	//�����ݿ��ȡģ������
        	new AnalogComputeBean().getAnalogData(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Analog_rainfall.do"))				        //�ϴ�����
        	new AnalogBean().ImportData(request, response, m_Rmi, false, Config); 
        else if (strUrl.equalsIgnoreCase("DeleteData.do"))				       		//ɾ������
        	new DevGJBean().DeleteData(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Analog_ToPo_GJ.do"))				        //��ѯ
        	new DevGJBean().AnalogToPo(request, response, m_Rmi, false);  
        else if (strUrl.equalsIgnoreCase("FileName_ToPo_GJ.do"))				    //������ϵͳ��
        	new DevGJBean().FileToPo(request, response, m_Rmi, false);  
        else if (strUrl.equalsIgnoreCase("Analog_DevGJ_Info.do"))				    //ʱ��ˮλ���
        	new DevGJBean().AnalogExecCmd(request, response, m_Rmi, false); 
        else if (strUrl.equalsIgnoreCase("Analog_Graph_Cut.do"))				    //ʱ��ˮλ����ͼ
        	new DevGXBean().AnalogExecCmd(request, response, m_Rmi, false);  
        else if (strUrl.equalsIgnoreCase("Analog_Graph_Curve.do"))				    //�ܾ�ˮλ����
        	new DataGJBean().AnalogGraph(request, response, m_Rmi, false);  
        else if (strUrl.equalsIgnoreCase("Analog_WaterAcc.do"))				    	//ȫ��ʱ�λ�ˮ���
        	new DevGJBean().WaterAcc(request, response, m_Rmi, false);  
        
        /************************************user-����ģ��*****************************************************/
        else if (strUrl.equalsIgnoreCase("Analog_ToPo_GX.do"))				        	//��ѯ
        	new DevGXBean().AnalogToPo(request, response, m_Rmi, false);  
        else if (strUrl.equalsIgnoreCase("Analog_DevGX_Info.do"))				    	//������Ϣ
        	new DevGXBean().AnalogFlow(request, response, m_Rmi, false); 
        else if (strUrl.equalsIgnoreCase("Analog_Graph_FlowLoad.do"))				    //��������
        	new DevGXBean().AnalogFlow(request, response, m_Rmi, false);  
        else if (strUrl.equalsIgnoreCase("Analog_Graph_ActualFlow.do"))				    //ʵ������
        	new DevGXBean().AnalogFlow(request, response, m_Rmi, false);  
        else if (strUrl.equalsIgnoreCase("Analog_Graph_FlowRate.do"))				    //����
        	new DevGXBean().AnalogFlow(request, response, m_Rmi, false);  
        
        /************************************user-�澯*****************************************************/
        else if (strUrl.equalsIgnoreCase("Alert_Info.do"))				    			//�澯��Ϣ
        	new AlertInfoBean().ExecCmd(request, response, m_Rmi, false); 
        else if (strUrl.equalsIgnoreCase("Alert_Now.do"))				    			//���¸澯
        	new AlertInfoBean().AlertNow(request, response, m_Rmi, false); 
        
        /************************************user-��վ*****************************************************/
        else if (strUrl.equalsIgnoreCase("Admin_DevBZ_Info.do"))				    			
        	new DevBZBean().ExecCmd(request, response, m_Rmi, false); 
        else if (strUrl.equalsIgnoreCase("User_DevBZ_Cut.do"))				    			
        	new DevBZBean().CutData(request, response, m_Rmi, false); 
        /************************************user-����*****************************************************/
        else if (strUrl.equalsIgnoreCase("Admin_ToPo_HL.do"))				    			
        	new DevMapBean().getHLData(request, response, m_Rmi, false);				//GIS��ͼ��ȡ
        else if (strUrl.equalsIgnoreCase("Admin_Dev_HL.do"))				    			
        	new DevMapBean().updateHLData(request, response, m_Rmi, false);				//�༭
        
        /************************************user-ͳ��*****************************************************/
        else if (strUrl.equalsIgnoreCase("User_InTotal.do"))			         		//�ܾ�����ͳ��
        	new DevGXBean().InTotal(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("User_InTotal_GX.do"))			         		//����ͳ��_��ϸ
        	new DevGXBean().InTotal_GX(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("bd09ToGcj02.do"))			         			//�ٶ�����ϵת��Ѷ
            	new DevGJBean().bd09ToGcj02(request, response, m_Rmi, false);
        
        /************************************user-ͳ��*****************************************************/
        else if (strUrl.equalsIgnoreCase("Admin_ToPo_YH.do"))			         		//�˺ӵ�λ��ȡ
        	new DevHandBean().getHand(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_Dev_YH.do"))			         		//������ȡ
        	new DevHandBean().ExecCmd(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_Update_YH.do"))			         		//��������
        	new DevHandBean().updateData(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("User_DevHand_Curve.do"))			         	//������ʷ����
        	new DataHandBean().GraphData(request, response, m_Rmi, false);

        /************************************user-��άͼ*****************************************************/
        else if (strUrl.equalsIgnoreCase("doThreeOneGJ.do"))			         		//��ȡ��άͼ����-�ܾ�
        	new ThreeGJBean().getThreeOneGJ(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("getCanvasOneGJ.do"))			         		//���㾮��ͼ
        	new ThreeGJBean().getCanvasOneGJ(request, response, m_Rmi, false);
        
        else if (strUrl.equalsIgnoreCase("doThreeOneGX.do"))			         		//��ȡ��άͼ����-����
        	new ThreeGXBean().getThreeOneGX(request, response, m_Rmi, false);
        
        /************************************user-�������*****************************************************/
        else if (strUrl.equalsIgnoreCase("getUser_Info.do"))			         		//��ȡ�û��б�
        	new UserInfoBean().ajaxGetAll(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("getCheck_Task.do"))			         		//ajax��ѯ�������
        	new CheckTaskBean().ajaxGetAll(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("getCheck_GJ.do"))			         			//ajax��ѯ�ܾ�
        	new CheckTaskGJBean().getCheckGJ(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("getCheck_GX.do"))			         			//ajax��ѯ����
        	new CheckTaskGXBean().getCheckGX(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Check_Task.do"))			         			//ajax��ѯ����
        	new CheckTaskBean().ExecCmd(request, response, m_Rmi, false);

        /************************************user-���ݷ���*****************************************************/
        else if (strUrl.equalsIgnoreCase("getSysId.do"))			         			//��ȡȫ��ϵͳ
        	new DevGJBean().getSysId(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("getSysIdNow.do"))			         			//������ǰϵͳ������
        	new DevGXBean().getSysIdNow(request, response, m_Rmi, false);
        
        /************************************user-�����ӿ�*****************************************************/
//        else if (strUrl.equalsIgnoreCase("getWeatherAll.do"))			         		//��ȡȫ������
//        	new WeatherBean().getWeatherAll(request, response, m_Rmi, false);
//        else if (strUrl.equalsIgnoreCase("getWeatherNow.do"))			         		//��ȡʵʱ����
//        	new WeatherBean().getWeatherNow(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("getWeatherHistory.do"))			         		//��ȡÿ����ʷ�������
        	new WeatherBean().getWeatherHistory(request, response, m_Rmi, false);
        
        /************************************���ݷ���*****************************************************/
        else if (strUrl.equalsIgnoreCase("getAllGJ.do"))			         			//��ȡȫ���ܾ�
        	new DevGJBean().getGJAll(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("getAllGX.do"))			         			//��ȡȫ������
        	new DevGXBean().getGXAll(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("getData_GJId.do"))			         		//��ȡ��ˮλ�ƵĹܾ�
        	new DataGJBean().getGJ_Id(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Import_WaterData.do"))			         		//��ȡ��ˮλ�ƵĹܾ�
        	new DataGJBean().Import_WaterData(request, response, m_Rmi, false);
        
        /************************************����������*****************************************************/
        else if (strUrl.equalsIgnoreCase("TextLLJ.do"))			         				//��ȡʵʱ����
        	new TextLLJBean().getShow(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("TextLLJ_Export.do"))			         				//����ȫ��
        	new TextLLJBean().XLQRExcel(request, response, m_Rmi, false);
        
        /************************************�Զ�����ģ������************************************************/
        else if (strUrl.equalsIgnoreCase("getAnalogData_Auot.do"))			         		//�Զ�����ģ�����ݱ��
        	new DevGXBean().getAnalogExcel(request, response, m_Rmi, false, Config);
        
        /************************************ˮ����Ϣ************************************************/
        else if (strUrl.equalsIgnoreCase("getSWNow.do"))			         		//��ȡˮ����Ϣ
        	new DevSWBean().getDataNow(request, response, m_Rmi, false);
        
        /************************************�豸�༭************************************************/
        else if (strUrl.equalsIgnoreCase("Admin_Mac_Read.do"))			         		// 
        	new MacReadBean().ExecCmd(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_Mac_Send.do"))			         		// 
        	new MacSendBean().ExecCmd(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_Mac_Analysis.do"))			         	// 
        	new MacAnalysisBean().ExecCmd(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_Equip_Config.do"))			         	// 
        	new MacConfigBean().ExecCmd(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_Add_Send.do"))			         		// 
        	new MacSendTaskBean().addSend(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Admin_Add_Read.do"))			         		// 
        	new MacReadTaskBean().addRead(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Read_Status.do"))			         		// 
        	new MacReadTaskBean().updateStatus(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("Send_Status.do"))			         		// 
        	new MacSendTaskBean().updateStatus(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("SendNow.do"))			         		// 
        	new MacSendTaskBean().sendNow(request, response, m_Rmi, false);
        
        /************************************��ȡ�ɼ�����************************************************/
        else if (strUrl.equalsIgnoreCase("000007_HQ.do"))			         		// 
        	new DataBean().ExecCmd(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("000007_DataNow.do"))			        // 
        	new DataNowAddBean().getDataNow(request, response, m_Rmi, false);
        else if (strUrl.equalsIgnoreCase("000007_DataH.do"))			        // 
        	new DataBean().getDataNow(request, response, m_Rmi, false);
        
        
        
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
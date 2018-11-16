package bean;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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

public class AnalogComputeBean extends RmiBean
{
	public final static long	serialVersionUID	= RmiBean.RMI_COMPUTE;

	public long getClassId()
	{
		return serialVersionUID;
	}

	public AnalogComputeBean()
	{
		super.className = "AnalogComputeBean";
	}
	
	public AnalogComputeBean(CurrStatus currStatus)
	{
		super.className = "AnalogComputeBean";
		this.currStatus = currStatus;
	}

	/**
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 * @throws ServletException
	 * @throws IOException
	 */
	public void Compute(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		String Resp = "9999";
		String Defeat = "";
		String Success = "";
		
		String SysIdList = CommUtil.StrToGB2312(request.getParameter("gjList"));
		if(SysIdList != null && SysIdList.length() > 0){
			Resp = "0000";
			String [] list = SysIdList.split(",");
			for(int i = 0; i < list.length; i ++){
				Id = list[i];
				String FileName = Project_Id + "_" + Id;
				AnalogBean analog = new AnalogBean();
				if(Id.contains("YJ")){					
					analog.analog_Y6(FileName, Double.parseDouble(Simu));
				}else if(Id.contains("WJ")){
					analog.analog_W4(FileName, Double.parseDouble(Simu));
				}
				if(analog.getStatus() == 0){
					WaterAcc = analog.getWaterAccList();
					WaterLev = analog.getWaterLevList();
					WaterAccGj = analog.getWaterAccGj();
					WaterFlowLoad = analog.getWaterFlowLoad();
					WaterActualFlow = analog.getWaterActualFlow();
					WaterFlowRate = analog.getWaterFlowRate();
					msgBean = pRmi.RmiExec(12, this, 0, 25);
					msgBean = pRmi.RmiExec(10, this, 0, 25);
					if(msgBean.getStatus() == MsgBean.STA_SUCCESS){
						Success += Id + ",";
					}
				}else if(analog.getStatus() == 1){
					Defeat += Id + "(" + analog.getMsg() + ")";
				}
				analog = null;
			}
			Resp += "成功[" + Success + "]失败[" + Defeat + "]";
		}
		
		PrintWriter outprint = response.getWriter();
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
		outprint.flush();
	}
	
	public void getAnalogData(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException{
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
				HashMap<String,Object> map = new HashMap<String,Object>();
				AnalogComputeBean bean = (AnalogComputeBean) gj_iterator.next();
				map.put("Id", bean.getId());
				map.put("WaterAcc", bean.getWaterAcc());
				map.put("WaterLev", bean.getWaterLev());
				map.put("WaterAccGj", bean.getWaterAccGj());
				map.put("WaterFlowLoad", bean.getWaterFlowLoad());
				map.put("WaterActualFlow", bean.getWaterActualFlow());
				map.put("WaterFlowRate", bean.getWaterFlowRate());
				CData.add(map);
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
			case 0:// 查询（类型&项目）
				Sql = " select t.id, t.project_id, t.simu, t.waterAcc, t.waterLev, t.waterAccGj, t.waterFlowLoad, t.waterActualFlow, t.waterFlowRate" + 
					  " from analog_compute t where INSTR('" + currStatus.getFunc_Type_Id() + "', t.id) > 0" + 
					  " and t.project_id = '" + Project_Id + "' " + 
					  " and t.simu = '" + Simu + "' " + 
					  " order by t.id";
				break;
			case 10:// 添加
				Sql = " insert into analog_compute(id, project_id, simu, waterAcc, waterLev, waterAccGj, waterFlowLoad, waterActualFlow, waterFlowRate) " + 
					  " values('" + Id + "','" + Project_Id + "','" + Simu + "','" + WaterAcc + "','" + WaterLev + "','" + WaterAccGj + "','" + WaterFlowLoad + "','" + WaterActualFlow + "','" + WaterFlowRate + "')";
				break;
			case 11:// 编辑
				Sql = " update analog_compute set simu= '" + Simu + "', waterAcc = '" + WaterAcc + "' ,waterLev= '" + WaterLev + "', waterAccGj = '" + WaterAccGj + "', waterFlowLoad = '" + WaterFlowLoad + "', waterActualFlow = '" + WaterActualFlow + "', waterFlowRate = '" + WaterFlowRate + "' " + 
					  " where id = '" + Id + "' and project_id = '" + Project_Id + "'";
				break;
			case 12:// 删除
				Sql = " delete from analog_compute " +
					  " where id = '" + Id + "' and project_id = '" + Project_Id + "' and simu = '" + Simu + "'";
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
			setSimu(pRs.getString(3));
			setWaterAcc(pRs.getString(4));
			setWaterLev(pRs.getString(5));
			setWaterAccGj(pRs.getString(6));
			setWaterFlowLoad(pRs.getString(7));
			setWaterActualFlow(pRs.getString(8));
			setWaterFlowRate(pRs.getString(9));
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
			setProject_Id(CommUtil.StrToGB2312(request.getParameter("Project_Id")));
			setSimu(CommUtil.StrToGB2312(request.getParameter("Simu")));
			setWaterAcc(CommUtil.StrToGB2312(request.getParameter("WaterAcc")));
			setWaterLev(CommUtil.StrToGB2312(request.getParameter("WaterLev")));
			setWaterAccGj(CommUtil.StrToGB2312(request.getParameter("WaterAccGj")));
			setWaterFlowLoad(CommUtil.StrToGB2312(request.getParameter("WaterFlowLoad")));
			setWaterActualFlow(CommUtil.StrToGB2312(request.getParameter("WaterActualFlow")));
			setWaterFlowRate(CommUtil.StrToGB2312(request.getParameter("WaterFlowRate")));
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}

	private String	Id;
	private String	Project_Id;
	private String	Simu;
	private String	WaterAcc;
	private String	WaterLev;
	private String	WaterAccGj;
	private String	WaterFlowLoad;
	private String	WaterActualFlow;
	private String	WaterFlowRate;
	
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

	public String getSimu() {
		return Simu;
	}

	public void setSimu(String simu) {
		Simu = simu;
	}

	public String getWaterAcc() {
		return WaterAcc;
	}

	public void setWaterAcc(String waterAcc) {
		WaterAcc = waterAcc;
	}

	public String getWaterLev() {
		return WaterLev;
	}

	public void setWaterLev(String waterLev) {
		WaterLev = waterLev;
	}

	public String getWaterAccGj() {
		return WaterAccGj;
	}

	public void setWaterAccGj(String waterAccGj) {
		WaterAccGj = waterAccGj;
	}

	public String getWaterFlowLoad() {
		return WaterFlowLoad;
	}

	public void setWaterFlowLoad(String waterFlowLoad) {
		WaterFlowLoad = waterFlowLoad;
	}

	public String getWaterActualFlow() {
		return WaterActualFlow;
	}

	public void setWaterActualFlow(String waterActualFlow) {
		WaterActualFlow = waterActualFlow;
	}

	public String getWaterFlowRate() {
		return WaterFlowRate;
	}

	public void setWaterFlowRate(String waterFlowRate) {
		WaterFlowRate = waterFlowRate;
	}

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}
	
}
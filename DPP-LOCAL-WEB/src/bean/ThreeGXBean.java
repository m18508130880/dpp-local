package bean;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
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

public class ThreeGXBean extends RmiBean
{	
	public final static long serialVersionUID =RmiBean.RMI_THREE_GX;
	public long getClassId()
	{
		return serialVersionUID;
	}
	
	public ThreeGXBean()
	{
		super.className = "ThreeGXBean";
	}
	
	public ThreeGXBean(CurrStatus currStatus)
	{
		super.className = "ThreeGXBean";
		this.currStatus = currStatus;
	}
	
	public void ExecCmd(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);

		switch(currStatus.getCmd())
		{
			
		}
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
	   	response.sendRedirect(currStatus.getJsp());
	}
	
	//����ͳ��-��ϸ
	public void InTotal_GX(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		ArrayList<?> gx_List = (ArrayList<?>) msgBean.getMsg();
		Iterator<?> gx_iterator = gx_List.iterator();
		String Resp = Id;
		String GX_DP = "";
		String PD = "";
		DecimalFormat df = new DecimalFormat("#.####");
		while (gx_iterator.hasNext()) {
			ThreeGXBean devGXBean = (ThreeGXBean) gx_iterator.next();
			Id = devGXBean.getId();
			Diameter = devGXBean.getDiameter();
			Length = devGXBean.getLength();
			Start_Id = devGXBean.getStart_Id();
			End_Id = devGXBean.getEnd_Id();
			Start_Height = devGXBean.getStart_Height();
			End_Height = devGXBean.getEnd_Height();
			Material = devGXBean.getMaterial();
			Buried_Year = devGXBean.getBuried_Year();
			Flag = "";
			PD = df.format((Double.valueOf(Start_Height) - Double.valueOf(End_Height))/Double.valueOf(Length));
			if(Double.valueOf(Start_Height) < Double.valueOf(End_Height))
			{
				GX_DP += Id + "(" + PD + "),";
			}
			else
			{
				Resp += Id + "(" + PD + "),";
			}
		}
		Resp = Resp + ";" +GX_DP;
		request.getSession().setAttribute("User_InTotal_GX_" + Sid, Resp);
		currStatus.setJsp("User_InTotal_GX.jsp?Sid=" + Sid + "&Id=" + Id);
		
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		response.sendRedirect(currStatus.getJsp());
	}
	
	public void getThreeOneGX(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		Subsys_Id = Id.substring(2, 5);
		msgBean = pRmi.RmiExec(0, this, 0, 25);
		ArrayList<?> gx_List = (ArrayList<?>) msgBean.getMsg();
		
		ThreeGJBean threeGJBean = new ThreeGJBean(currStatus);
		threeGJBean.setSubsys_Id(Subsys_Id);
		msgBean = pRmi.RmiExec(0, threeGJBean, 0, 25);
		ArrayList<?> gj_List = (ArrayList<?>) msgBean.getMsg();

		ArrayList<ThreeModel> modelList = getModel(gj_List, gx_List, Id);
		currStatus.setJsp("GX_Three.jsp?Sid=" + Sid + "&Id=" + Id);
		request.getSession().setAttribute("Three_Model_" + Sid, (Object) modelList);
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		response.sendRedirect(currStatus.getJsp());
	}
	
	public ArrayList<ThreeModel> getModel(ArrayList<?> gj_List, ArrayList<?> gx_List, String Id){
		ArrayList<ThreeModel> modelList = new ArrayList<ThreeModel>();
		HashMap<String, ThreeGJBean> gjMap = new HashMap<String, ThreeGJBean>();
		for(int i = 0; i < gj_List.size(); i ++){	// listתmap
			ThreeGJBean bean = (ThreeGJBean) gj_List.get(i);
			gjMap.put(bean.getId(), bean);
		}
		HashMap<String, ThreeGXBean> gxMap = new HashMap<String, ThreeGXBean>();
		for(int i = 0; i < gx_List.size(); i ++){	// listתmap
			ThreeGXBean bean = (ThreeGXBean) gx_List.get(i);
			gxMap.put(bean.getId(), bean);
		}
		double heightD = 0;
		double cenHeight = 0;
		if(gxMap.containsKey(Id)){
			ThreeModel gxModel = new ThreeModel();
			ThreeGXBean gx= gxMap.get(Id);
			ThreeGJBean startGJ = gjMap.get(gx.getStart_Id());
			ThreeGJBean endGJ = gjMap.get(gx.getEnd_Id());
			// ���Ĺܾ��͵�ǰ�ܾ�	�ĸ߶Ȳ�
			double rotaZ = (Double.valueOf(startGJ.getBase_Height()) - Double.valueOf(endGJ.getBase_Height()))*10;
			// ����ˮƽ����
			double rotaX = Double.valueOf(gx.getLength())*10;
			// ���Ĺܾ��뵱ǰ�ܾ���γ�Ȳ�
			double pLat = (Double.valueOf(startGJ.getLatitude()) - Double.valueOf(endGJ.getLatitude()))*10000000;
			// ���Ĺܾ��뵱ǰ�ܾ��ľ��Ȳ�
			double pLng = (Double.valueOf(startGJ.getLongitude()) - Double.valueOf(endGJ.getLongitude()))*10000000;
			double angle = 0;
			if((pLat <= 0 && pLng <= 0) || (pLat > 0 && pLng > 0)){ // ��һ��������
				if(pLng == 0){
					angle = - Math.PI/2; // ����ģ����ת�Ƕ�-��Z��
				}else{
					angle = - Math.atan(Math.abs(pLat/pLng)); // ����ģ����ת�Ƕ�-��Z��
				}
			}else if((pLat <= 0 && pLng > 0) || pLat > 0 && pLng <= 0){ // �ڶ���������
				if(pLng == 0){
					angle = Math.PI/2; // ����ģ����ת�Ƕ�-��Z��
				}else{
					angle = Math.atan(Math.abs(pLat/pLng));
				}
			}
			int xx = 0;
			int yy = 0;
			if(pLat <= 0 && pLng <= 0){ // ��һ����
				xx = 1;
				yy = 3;
			}else if(pLat <= 0 && pLng > 0){ // �ڶ�����
				xx = 2;
				yy = 4;
			}else if(pLat > 0 && pLng > 0){ // ��������
				xx = 3;
				yy = 1;
			}else if(pLat > 0 && pLng <= 0){ // ��������
				xx = 4;
				yy = 2;
			}
			
			heightD = Double.valueOf(startGJ.getBase_Height()) - Double.valueOf(gx.getStart_Height());
			cenHeight = (Float.valueOf(startGJ.getTop_Height()) - Float.valueOf(startGJ.getBase_Height()))*100;
			gxModel = getGXModel(startGJ, gx, endGJ, heightD, cenHeight, angle, rotaZ, rotaX);
			modelList.add(gxModel);
			ThreeModel startGJModel = new ThreeModel();
			startGJModel = getGJModel(gx, startGJ, 0, Math.abs(angle), rotaZ, rotaX, xx);
			modelList.add(startGJModel);
			ThreeModel endGJModel = new ThreeModel();
			double h = Double.valueOf(startGJ.getBase_Height()) - Double.valueOf(endGJ.getBase_Height());
			endGJModel = getGJModel(gx, endGJ, h*10, Math.abs(angle), rotaZ, rotaX, yy);
			modelList.add(endGJModel);
		}
		return modelList;
	}
	public ThreeModel getGXModel(ThreeGJBean startGJ, ThreeGXBean gx, ThreeGJBean endGJ, double heightD, double cenHeight, double angle, double rotaZ, double rotaX){

		short radii = 0;	// ģ�Ͱ뾶
		double height = 0;	// ģ�͸߶�
		double pX = 0;		// ģ��ǰ��ƫ��
		double pY = 0;		// ģ������ƫ��
		double pZ = 0;		// ģ������ƫ��
		double rX = 0;		// ģ����Y����ת����
		double rY = 0;		// ģ����X����ת����
		double rZ = 0;		// ģ����Z����ת����
		
		ThreeModel model = new ThreeModel();	
		// �¶�
		double pd = (Double.valueOf(gx.getStart_Height()) - Double.valueOf(gx.getEnd_Height())) / Double.valueOf(gx.getLength());
		radii = (short) (Short.valueOf(gx.getDiameter())/10/2);		// ģ�͵İ뾶
		
		// ���ݹ��ɶ���������߳��ȣ���ģ�͵ĸ߶�
		height = Math.sqrt(rotaZ*rotaZ + rotaX*rotaX);
		if(pd >= 0){
			rX = Math.PI/2 - Math.atan(rotaZ/rotaX); // ����ģ����ת�Ƕ�-��Y��
		}else{
			rX = Math.PI/2 + Math.atan(rotaZ/rotaX); // ����ģ����ת�Ƕ�-��Y��
		}
		rY = 0; // ��ת-��X��
		rZ = angle;
		
		// ����ģ�������Ĺܾ�ģ�͵ĸ߶Ȳ���Ĺܾ�һ��߶�-���Ĺܾ�����ߵĸ߶Ȳ��ֵ�ض�Ϊ����
		if(heightD < 0){
			pY =  - (cenHeight/2 - radii + heightD*100);
		}else{
			pY =  - (cenHeight/2 - radii - heightD*100);
		}
		
		model.setId(gx.getId());
		model.setType("1");
		model.setColor(gx.getMaterial());
		model.setPositionX("0");
		model.setPositionY(String.valueOf(pY));
		model.setPositionZ("0");
		model.setRotationX(String.valueOf(rX));
		model.setRotationY("0");
		model.setRotationZ(String.valueOf(rZ));
		model.setRadiusTop(String.valueOf(radii));
		model.setRadiusBottom(String.valueOf(radii));
		model.setRadiusSegments("32");
		model.setHeight(String.valueOf(height));
		model.setHeightSegments("1");

		//String des = "����/�׸�|" + gj.getTop_Height() + "/" + gj.getBase_Height() + ";�ߴ�|" + gj.getSize() + ";��·|" + gj.getRoad() + ";���ڹ���|" + gj.getIn_Id() + ";���ڹ���|" + gj.getOut_Id();
		String des = "����|" + gx.getLength() + ";ֱ��|" + gx.getDiameter() + ";�¶�|" + String.format("%.4f", pd) + ";��·|" + gx.getRoad() + ";��˹ܾ�|" + gx.getStart_Id() + ";�ն˹ܾ�|" + gx.getEnd_Id();
		model.setDes(des);
		return model;
	}
	
	public ThreeModel getGJModel(ThreeGXBean gx, ThreeGJBean gj, double heightD, double angle, double rotaZ, double rotaX, int xx){
		double radii = 0;	// ģ�Ͱ뾶
		double height = 0;	// ģ�͸߶�
		double pX = 0;		// ģ��ǰ��ƫ��
		double pY = 0;		// ģ������ƫ��
		double pZ = 0;		// ģ������ƫ��
		double rX = 0;		// ģ����Y����ת����
		double rY = 0;		// ģ����X����ת����
		double rZ = 0;		// ģ����Z����ת����
		ThreeModel model = new ThreeModel();
		height = (Float.valueOf(gj.getTop_Height()) - Float.valueOf(gj.getBase_Height()))*100;
		radii = Double.valueOf(gj.getSize())/10/2;
		// ���ݹ��ɶ���������߳��ȣ���ģ�͵ĸ߶�
		height = Math.sqrt(rotaZ*rotaZ + rotaX*rotaX);

		pY = heightD;
		if(xx == 1){
			pX = Math.cos(rZ)*(height/2 + radii - 10);
			pZ = Math.sin(rZ)*(height/2 + radii - 10);
		} else if(xx == 2){
			pX = - Math.cos(rZ)*(height/2 + radii - 10);
			pZ = Math.sin(rZ)*(height/2 + radii - 10);
		} else if(xx == 3){
			pX = - Math.cos(rZ)*(height/2 + radii - 10);
			pZ = - Math.sin(rZ)*(height/2 + radii - 10);
		} else if(xx == 4){
			pX = Math.cos(rZ)*(height/2 + radii - 10);
			pZ = - Math.sin(rZ)*(height/2 + radii - 10);
		}
		model.setId(gj.getId());
		model.setType("1");
		model.setColor(gj.getMaterial());
		model.setPositionX(String.valueOf(pX));
		model.setPositionY(String.valueOf(pY));
		model.setPositionZ(String.valueOf(pZ));
		model.setRotationX(String.valueOf(rX));
		model.setRotationY(String.valueOf(rY));
		model.setRotationZ(String.valueOf(rZ));
		model.setRadiusTop(String.valueOf(radii));
		model.setRadiusBottom(String.valueOf(radii));
		model.setRadiusSegments("64");
		model.setHeight(String.valueOf(height));
		model.setHeightSegments("1");
		
		String des = "����/�׸�|" + gj.getTop_Height() + "/" + gj.getBase_Height() + ";�ߴ�|" + gj.getSize() + ";��·|" + gj.getRoad() + ";���ڹ���|" + gj.getIn_Id() + ";���ڹ���|" + gj.getOut_Id();
		model.setDes(des);
		return model;
	}
	
		
	public String getSql(int pCmd)
	{  
		String Sql = "";
		switch (pCmd)
		{
		case 0://��ѯ����Ŀ&��ϵͳ��
			Sql = " select t.id, t.diameter, t.length, t.start_id, t.end_id, t.start_height, t.end_height, t.material, t.buried_year, t.data_lev, t.project_id, t.project_name, t.equip_id, t.equip_name ,round((t.curr_data),2), t.road, t.flag" +
					" from view_dev_gx t " +	
					" where t.project_id = '" + currStatus.getFunc_Project_Id() + "'" + 
					" and t.id like '%"+ Subsys_Id +"%'" +
					" order by t.id ";
			break;
		case 1://��ѯ����Ŀ��
			Sql = " select t.id, t.diameter, t.length, t.start_id, t.end_id, t.start_height, t.end_height, t.material, t.buried_year, t.data_lev, t.project_id, t.project_name, t.equip_id, t.equip_name ,round((t.curr_data),2), t.road, t.flag" +
					" from view_dev_gx t " +	
					" where t.project_id = '" + currStatus.getFunc_Project_Id() + "'" + 
					" order by t.id ";
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
			setDiameter(pRs.getString(2));
			setLength(pRs.getString(3));
			setStart_Id(pRs.getString(4));
			setEnd_Id(pRs.getString(5));
			setStart_Height(pRs.getString(6));
			setEnd_Height(pRs.getString(7));
			setMaterial(pRs.getString(8));
			setBuried_Year(pRs.getString(9));
			setData_Lev(pRs.getString(10));
			setProject_Id(pRs.getString(11));
			setProject_Name(pRs.getString(12));
			setEquip_Id(pRs.getString(13));
			setEquip_Name(pRs.getString(14));
			setCurr_Data(pRs.getString(15));
			setRoad(pRs.getString(16));
			setFlag(pRs.getString(17));
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
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
			setId(CommUtil.StrToGB2312(request.getParameter("Id")));
			setDiameter(CommUtil.StrToGB2312(request.getParameter("Diameter")));
			setLength(CommUtil.StrToGB2312(request.getParameter("Length")));
			setStart_Id(CommUtil.StrToGB2312(request.getParameter("Start_Id")));
			setEnd_Id(CommUtil.StrToGB2312(request.getParameter("End_Id")));
			setStart_Height(CommUtil.StrToGB2312(request.getParameter("Start_Height")));
			setEnd_Height(CommUtil.StrToGB2312(request.getParameter("End_Height")));
			setMaterial(CommUtil.StrToGB2312(request.getParameter("Material")));
			setBuried_Year(CommUtil.StrToGB2312(request.getParameter("Buried_Year")));
			setData_Lev(CommUtil.StrToGB2312(request.getParameter("Data_Lev")));
			setProject_Id(CommUtil.StrToGB2312(request.getParameter("Project_Id")));
			setProject_Name(CommUtil.StrToGB2312(request.getParameter("Project_Name")));
			setEquip_Id(CommUtil.StrToGB2312(request.getParameter("Equip_Id")));
			setEquip_Name(CommUtil.StrToGB2312(request.getParameter("Equip_Name")));
			setSubsys_Id(CommUtil.StrToGB2312(request.getParameter("Subsys_Id")));
			setAfter_Project_Id(CommUtil.StrToGB2312(request.getParameter("After_Project_Id")));
			setRoad(CommUtil.StrToGB2312(request.getParameter("Road")));
			setFlag(CommUtil.StrToGB2312(request.getParameter("Flag")));
			setpSimu(CommUtil.StrToGB2312(request.getParameter("pSimu")));
			
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}
	
	private String Id;
	private String Diameter;
	private String Length;	
	private String Start_Id;
	private String End_Id;
	private String Start_Height;
	private String End_Height;
	private String Material;
	private String Buried_Year;
	private String Data_Lev;
	private String Road;
	private String Flag;
	
	private String Project_Id;	
	private String Project_Name;	
	private String Equip_Id;	
	private String Equip_Name;
	private String Curr_Data;
	private String Subsys_Id;
	
	private String Sid;	
	private String After_Project_Id;
	private String pSimu;
	
	public String getFlag()
	{
		return Flag;
	}

	public void setFlag(String flag)
	{
		Flag = flag;
	}

	public String getpSimu()
	{
		return pSimu;
	}

	public void setpSimu(String pSimu)
	{
		this.pSimu = pSimu;
	}

	public String getRoad()
	{
		return Road;
	}

	public void setRoad(String road)
	{
		Road = road;
	}

	public String getAfter_Project_Id() {
		return After_Project_Id;
	}

	public void setAfter_Project_Id(String after_Project_Id) {
		After_Project_Id = after_Project_Id;
	}	
	
	
	public String getCurr_Data() {
		return Curr_Data;
	}

	public void setCurr_Data(String curr_Data) {
		Curr_Data = curr_Data;
	}

	
	
	public String getSubsys_Id() {
		return Subsys_Id;
	}

	public void setSubsys_Id(String subsys_Id) {
		Subsys_Id = subsys_Id;
	}

	public String getStart_Height() {
		return Start_Height;
	}

	public void setStart_Height(String start_Height) {
		Start_Height = start_Height;
	}

	public String getEnd_Height() {
		return End_Height;
	}

	public void setEnd_Height(String end_Height) {
		End_Height = end_Height;
	}


	public String getBuried_Year() {
		return Buried_Year;
	}

	public void setBuried_Year(String buried_Year) {
		Buried_Year = buried_Year;
	}

	public String getData_Lev() {
		return Data_Lev;
	}

	public void setData_Lev(String data_Lev) {
		Data_Lev = data_Lev;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getDiameter() {
		return Diameter;
	}

	public void setDiameter(String diameter) {
		Diameter = diameter;
	}

	public String getLength() {
		return Length;
	}

	public void setLength(String length) {
		Length = length;
	}

	public String getStart_Id() {
		return Start_Id;
	}

	public void setStart_Id(String start_Id) {
		Start_Id = start_Id;
	}

	public String getEnd_Id() {
		return End_Id;
	}

	public void setEnd_Id(String end_Id) {
		End_Id = end_Id;
	}

	public String getMaterial() {
		return Material;
	}

	public void setMaterial(String material) {
		Material = material;
	}

	public String getProject_Id() {
		return Project_Id;
	}

	public void setProject_Id(String project_Id) {
		Project_Id = project_Id;
	}

	public String getProject_Name() {
		return Project_Name;
	}

	public void setProject_Name(String project_Name) {
		Project_Name = project_Name;
	}

	public String getEquip_Id() {
		return Equip_Id;
	}

	public void setEquip_Id(String equip_Id) {
		Equip_Id = equip_Id;
	}

	public String getEquip_Name() {
		return Equip_Name;
	}

	public void setEquip_Name(String equip_Name) {
		Equip_Name = equip_Name;
	}

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}



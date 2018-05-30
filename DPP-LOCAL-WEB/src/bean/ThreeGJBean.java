package bean;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rmi.Rmi;
import rmi.RmiBean;
import util.CommUtil;
import util.CurrStatus;

public class ThreeGJBean extends RmiBean
{
	public final static long	serialVersionUID	= RmiBean.RMI_THREE_GJ;

	public long getClassId()
	{
		return serialVersionUID;
	}

	public ThreeGJBean()
	{
		super.className = "ThreeGJBean";
	}
	
	public ThreeGJBean(CurrStatus currStatus)
	{
		super.className = "DevGJBean";
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
	public void ExecCmd(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		switch (currStatus.getCmd())
		{
			
		}
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		response.sendRedirect(currStatus.getJsp());
	}
	
	public void getThreeOneGJ(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		Subsys_Id = Id.substring(2, 5);
		msgBean = pRmi.RmiExec(0, this, 0, 25);
		ArrayList<?> gj_List = (ArrayList<?>) msgBean.getMsg();
		
		ThreeGXBean threeGXBean = new ThreeGXBean(currStatus);
		threeGXBean.setSubsys_Id(Subsys_Id);
		msgBean = pRmi.RmiExec(0, threeGXBean, 0, 25);
		ArrayList<?> gx_List = (ArrayList<?>) msgBean.getMsg();

		ArrayList<ThreeModel> modelList = getModel(gj_List, gx_List, Id);
		currStatus.setJsp("three.jsp?Sid=" + Sid);
		request.getSession().setAttribute("Three_Model_" + Sid, (Object) modelList);
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		response.sendRedirect(currStatus.getJsp());
	}
	
	public ArrayList<ThreeModel> getModel(ArrayList<?> gj_List, ArrayList<?> gx_List, String Id){
		ArrayList<ThreeModel> modelList = new ArrayList<ThreeModel>();
		HashMap<String, ThreeGJBean> gjMap = new HashMap<String, ThreeGJBean>();
		for(int i = 0; i < gj_List.size(); i ++){	// list转map
			ThreeGJBean bean = (ThreeGJBean) gj_List.get(i);
			gjMap.put(bean.getId(), bean);
		}
		HashMap<String, ThreeGXBean> gxMap = new HashMap<String, ThreeGXBean>();
		for(int i = 0; i < gx_List.size(); i ++){	// list转map
			ThreeGXBean bean = (ThreeGXBean) gx_List.get(i);
			gxMap.put(bean.getId(), bean);
		}

		double cenRadii = 0;
		double cenHeight = 0;
		double heightD = 0;
		if(gjMap.containsKey(Id)){
			ThreeModel cenModel = new ThreeModel();
			ThreeGJBean cenGJ= gjMap.get(Id);
			cenHeight = (Float.valueOf(cenGJ.getTop_Height()) - Float.valueOf(cenGJ.getBase_Height()))*100;
			cenRadii = Float.valueOf(cenGJ.getSize())/10/2;
			cenModel = getGJModel(cenGJ, cenHeight, cenRadii);
			modelList.add(cenModel);
			String [] inIdList = cenGJ.getIn_Id().split(",");
			for(int i = 0; i < inIdList.length; i ++){
				if(gxMap.containsKey(inIdList[i])){
					ThreeModel inModel = new ThreeModel();
					ThreeGXBean inGx = gxMap.get(inIdList[i]);	// 取到进口管线
					ThreeGJBean inGj = gjMap.get(inGx.getStart_Id());	// 取到进口管井
					heightD = Double.valueOf(cenGJ.getBase_Height()) - Double.valueOf(inGx.getEnd_Height());
					inModel = this.getGXModel(cenGJ, inGx, inGj, cenHeight, cenRadii, heightD, 0);
					modelList.add(inModel);
				}
			}
			String outId = cenGJ.getOut_Id();
			if(gxMap.containsKey(outId)){
				ThreeModel outModel = new ThreeModel();
				ThreeGXBean outGx = gxMap.get(outId);	// 取到出口管线
				ThreeGJBean outGj = gjMap.get(outGx.getEnd_Id());	// 取到出口管井
				heightD = Double.valueOf(cenGJ.getBase_Height()) - Double.valueOf(outGx.getStart_Height());
				outModel = this.getGXModel(cenGJ, outGx, outGj, cenHeight, cenRadii, heightD, 1);
				modelList.add(outModel);
			}
		}
		return modelList;
	}
	public ThreeModel getGJModel(ThreeGJBean cenGJ, Double cenHeight, Double cenRadii){
		ThreeModel model = new ThreeModel();
		model.setId(cenGJ.getId());
		model.setType("1");
		model.setColor("#000000");
		model.setPositionX("0");
		model.setPositionY("0");
		model.setPositionZ("0");
		model.setRotationX("0");
		model.setRotationY("0");
		model.setRotationZ("0");
		model.setRadiusTop(String.valueOf(cenRadii));
		model.setRadiusBottom(String.valueOf(cenRadii));
		model.setRadiusSegments("64");
		model.setHeight(String.valueOf(cenHeight));
		model.setHeightSegments("1");

		model.setPositionX_W("0");
		model.setPositionY_W("0");
		model.setPositionZ_W("25");
		model.setRotationX_W("0");
		model.setRotationY_W(String.valueOf(Math.PI/2));
		model.setRotationZ_W("0");
		
		model.setPositionX_Y("0");
		model.setPositionY_Y(String.valueOf(cenHeight/2));
		model.setPositionZ_Y("0");
		model.setRotationX_Y(String.valueOf(Math.PI/2));
		model.setRotationY_Y("0");
		model.setRotationZ_Y("0");
		String des = "顶高/底高|" + cenGJ.getTop_Height() + "/" + cenGJ.getBase_Height() + ";尺寸|" + cenGJ.getSize() + ";道路|" + cenGJ.getRoad() + ";进口管线|" + cenGJ.getIn_Id() + ";出口管线|" + cenGJ.getOut_Id();
		model.setDes(des);
		return model;
	}
	
	public ThreeModel getGXModel(ThreeGJBean cenGJ, ThreeGXBean gx, ThreeGJBean gj, Double cenHeight, Double cenRadii, Double heightD, int flag){
		double rotaZ = 0;	// 中心管井与当前管线的高度差
		double rotaX = 0;	// 管线的水平长度
		short radii = 0;	// 模型半径
		double height = 0;	// 模型高度
		double pX = 0;		// 模型前后偏移
		double pY = 0;		// 模型上下偏移
		double pZ = 0;		// 模型左右偏移
		double rX = 0;		// 模型沿Y轴旋转弧度
		double rY = 0;		// 模型沿X轴旋转弧度
		double rZ = 0;		// 模型沿Z轴旋转弧度

		double pX_Y = 0;		// 模型前后偏移
		double pY_Y = 0;		// 模型上下偏移
		double pZ_Y = 0;		// 模型左右偏移
		
		ThreeModel model = new ThreeModel();
		// 坡度
		double pd = (Double.valueOf(gx.getStart_Height()) - Double.valueOf(gx.getEnd_Height())) / Double.valueOf(gx.getLength());

		radii = (short) (Short.valueOf(gx.getDiameter())/10/2);		// 模型的半径
		// 中心管井和当前管井	的高度差
		rotaZ = (Double.valueOf(cenGJ.getBase_Height()) - Double.valueOf(gj.getBase_Height()))*10;
		// 管线水平长度
		rotaX = Double.valueOf(gx.getLength())*10;
		// 根据勾股定理，算出管线长度，即模型的高度
		height = Math.sqrt(rotaZ*rotaZ + rotaX*rotaX);
		// 管线模型与中心管井模型的高度差，中心管井一半高度-中心管井与管线的高度差。数值必定为负数
		if(heightD < 0){
			pY =  - (cenHeight/2 - radii + heightD*100);
		}else{
			pY =  - (cenHeight/2 - radii - heightD*100);
		}
		pY_Y = pY;
		// 中心管井与当前管井的纬度差
		double pLat = (Double.valueOf(cenGJ.getLatitude()) - Double.valueOf(gj.getLatitude()))*10000000;
		// 中心管井与当前管井的经度差
		double pLng = (Double.valueOf(cenGJ.getLongitude()) - Double.valueOf(gj.getLongitude()))*10000000;
		rY = 0; // 旋转-绕X轴
		if(pd >= 0){
			rX = Math.PI/2 - Math.atan(rotaZ/rotaX); // 管线模型旋转角度-绕Y轴
		}else{
			rX = Math.PI/2 + Math.atan(rotaZ/rotaX); // 管线模型旋转角度-绕Y轴
		}
		if(pLat <= 0 && pLng <= 0){ // 第一象限
			rZ = - Math.atan(Math.abs(pLat/pLng)); // 管线模型旋转角度-绕Z轴
			pZ = - Math.cos(rZ)*(height/2 + cenRadii - 10); // 管线模型与中心管井模型的左右差
			pX = Math.sin(rZ)*(height/2 + cenRadii - 10); // 管线模型与中心管井的前后差
			pZ_Y = - Math.cos(rZ)*(height + cenRadii - 10); // 管线模型与中心管井模型的左右差
			pX_Y = Math.sin(rZ)*(height + cenRadii - 10); // 管线模型与中心管井的前后差
		}else if(pLat <= 0 && pLng > 0){ // 第二象限
			rZ = Math.atan(Math.abs(pLat/pLng));
			pZ = Math.cos(rZ)*(height/2 + cenRadii - 10);
			pX = - Math.sin(rZ)*(height/2 + cenRadii - 10);
			pZ_Y = Math.cos(rZ)*(height + cenRadii - 10);
			pX_Y = - Math.sin(rZ)*(height + cenRadii - 10);
		}else if(pLat > 0 && pLng > 0){ // 第三象限
			rZ = - Math.atan(Math.abs(pLat/pLng));
			pZ = Math.cos(rZ)*(height/2 + cenRadii - 10);
			pX = - Math.sin(rZ)*(height/2 + cenRadii - 10);
			pZ_Y = Math.cos(rZ)*(height + cenRadii - 10);
			pX_Y = - Math.sin(rZ)*(height + cenRadii - 10);
		}else if(pLat > 0 && pLng <= 0){ // 第四象限
			rZ = Math.atan(Math.abs(pLat/pLng));
			pZ = - Math.cos(rZ)*(height/2 + cenRadii - 10);
			pX = Math.sin(rZ)*(height/2 + cenRadii - 10);
			pZ_Y = - Math.cos(rZ)*(height + cenRadii - 10);
			pX_Y = Math.sin(rZ)*(height + cenRadii - 10);
		}
		
		model.setId(gx.getId());
		model.setType("1");
		model.setColor("#000000");
		model.setPositionX(String.valueOf(pX));
		model.setPositionY(String.valueOf(pY));
		model.setPositionZ(String.valueOf(pZ));
		model.setRotationX(String.valueOf(rX));
		model.setRotationY(String.valueOf(rY));
		model.setRotationZ(String.valueOf(rZ));
		model.setRadiusTop(String.valueOf(radii));
		model.setRadiusBottom(String.valueOf(radii));
		model.setRadiusSegments("32");
		model.setHeight(String.valueOf(height));
		model.setHeightSegments("1");
		
		model.setPositionX_W(String.valueOf(pX));
		model.setPositionY_W(String.valueOf(pY));
		model.setPositionZ_W(String.valueOf(pZ));
		model.setRotationX_W(String.valueOf("0")); // 绕Z轴
		model.setRotationY_W(String.valueOf(Math.PI/2 - rZ)); // 绕Y轴
		model.setRotationZ_W(String.valueOf("0")); // 绕X轴
		
		model.setPositionX_Y(String.valueOf(pX_Y));
		model.setPositionY_Y(String.valueOf(pY_Y));
		model.setPositionZ_Y(String.valueOf(pZ_Y));
		model.setRotationX_Y("0");
		model.setRotationY_Y(String.valueOf(- rZ));
		model.setRotationZ_Y("0");
		
		String des = "长度|" + gx.getLength() + ";直径|" + gx.getDiameter() + ";坡度|" + String.format("%.4f", pd) + ";道路|" + gx.getRoad() + ";起端管井|" + gx.getStart_Id() + ";终端管井|" + gx.getEnd_Id();
		model.setDes(des);
		return model;
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
			case 0:// 查询（子系统&项目）
				Sql = " select t.id, t.Longitude, t.latitude, t.top_Height, t.base_height, t.Size, t.in_id, t.out_id, t.Material, t.Flag, t.Data_Lev, round((t.curr_data),2) , t.sign , t.project_id, t.project_name, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.In_Img, t.Out_Img, t.equip_time, t.road, t.rotation" + 
					  " from view_dev_gj t " +
					  " where t.id like '%" + Subsys_Id + "%' " + 
					  " and t.project_id = '" + currStatus.getFunc_Project_Id() + "' " + " order by t.id  ";
				break;
			case 1:// 查询（项目）
				Sql = " select t.id, t.Longitude, t.latitude, t.top_Height, t.base_height, t.Size, t.in_id, t.out_id, t.Material, t.Flag, t.Data_Lev, round((t.curr_data),2) , t.sign , t.project_id, t.project_name, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.In_Img, t.Out_Img, t.equip_time, t.road, t.rotation" + 
					  " from view_dev_gj t " +
					  " where t.project_id = '" + currStatus.getFunc_Project_Id() + "' " + " order by t.id  ";
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
			setLongitude(pRs.getString(2));
			setLatitude(pRs.getString(3));
			setTop_Height(pRs.getString(4));
			setBase_Height(pRs.getString(5));
			setSize(pRs.getString(6));
			setIn_Id(pRs.getString(7));
			setOut_Id(pRs.getString(8));
			setMaterial(pRs.getString(9));
			setFlag(pRs.getString(10));
			setData_Lev(pRs.getString(11));
			setCurr_Data(pRs.getString(12));
			setSign(pRs.getString(13));
			setProject_Id(pRs.getString(14));
			setProject_Name(pRs.getString(15));
			setEquip_Id(pRs.getString(16));
			setEquip_Name(pRs.getString(17));
			setEquip_Height(pRs.getString(18));
			setEquip_Tel(pRs.getString(19));
			setIn_Img(pRs.getString(20));
			setOut_Img(pRs.getString(21));
			setEquip_Time(pRs.getString(22));
			setRoad(pRs.getString(23));
			setRotation(pRs.getString(24));
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
			setLongitude(CommUtil.StrToGB2312(request.getParameter("Longitude")));
			setLatitude(CommUtil.StrToGB2312(request.getParameter("Latitude")));
			setTop_Height(CommUtil.StrToGB2312(request.getParameter("Top_Height")));
			setBase_Height(CommUtil.StrToGB2312(request.getParameter("Base_Height")));
			setSize(CommUtil.StrToGB2312(request.getParameter("Size")));
			setIn_Id(CommUtil.StrToGB2312(request.getParameter("In_Id")));
			setOut_Id(CommUtil.StrToGB2312(request.getParameter("Out_Id")));
			setMaterial(CommUtil.StrToGB2312(request.getParameter("Material")));
			setFlag(CommUtil.StrToGB2312(request.getParameter("Flag")));
			setData_Lev(CommUtil.StrToGB2312(request.getParameter("Data_Lev")));
			setCurr_Data(CommUtil.StrToGB2312(request.getParameter("Curr_Data")));
			setSign(CommUtil.StrToGB2312(request.getParameter("Sign")));
			setProject_Id(CommUtil.StrToGB2312(request.getParameter("Project_Id")));
			setEquip_Id(CommUtil.StrToGB2312(request.getParameter("Equip_Id")));
			setEquip_Name(CommUtil.StrToGB2312(request.getParameter("Equip_Name")));
			setEquip_Height(CommUtil.StrToGB2312(request.getParameter("Equip_Height")));
			setEquip_Tel(CommUtil.StrToGB2312(request.getParameter("Equip_Tel")));
			setIn_Img(CommUtil.StrToGB2312(request.getParameter("In_Img")));
			setOut_Img(CommUtil.StrToGB2312(request.getParameter("Out_Img")));
			setEquip_Time(CommUtil.StrToGB2312(request.getParameter("Equip_Time")));
			setRoad(CommUtil.StrToGB2312(request.getParameter("Road")));
			setRotation(CommUtil.StrToGB2312(request.getParameter("Rotation")));
			setpSimu(CommUtil.StrToGB2312(request.getParameter("pSimu")));
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}

	private String	Id;
	private String	Longitude;
	private String	Latitude;
	private String	Top_Height;
	private String	Base_Height;
	private String	Size;
	private String	In_Id;
	private String	Out_Id;
	private String	Material;
	private String	Flag;
	private String	Data_Lev;

	private String	Sign;
	private String	Project_Id;
	private String	Project_Name;
	private String	Equip_Id;
	private String	Equip_Name;
	private String	Equip_Height;
	private String	Equip_Tel;
	private String	Equip_Time;
	private String	Road;
	private String	Rotation;
	
	private String	WX_Lng;
	private String	WX_Lat;
	
	private String	pSimu; //降雨强度

	public String getWX_Lng() {
		return WX_Lng;
	}

	public void setWX_Lng(String wX_Lng) {
		WX_Lng = wX_Lng;
	}

	public String getWX_Lat() {
		return WX_Lat;
	}

	public void setWX_Lat(String wX_Lat) {
		WX_Lat = wX_Lat;
	}

	public String getpSimu()
	{
		return pSimu;
	}

	public void setpSimu(String pSimu)
	{
		this.pSimu = pSimu;
	}

	public String getRotation()
	{
		return Rotation;
	}

	public void setRotation(String rotation)
	{
		Rotation = rotation;
	}

	public String getRoad()
	{
		return Road;
	}

	public void setRoad(String road)
	{
		Road = road;
	}

	public String getEquip_Time()
	{
		return Equip_Time;
	}

	public void setEquip_Time(String equip_Time)
	{
		Equip_Time = equip_Time;
	}

	public String getEquip_Tel()
	{
		return Equip_Tel;
	}

	public void setEquip_Tel(String equip_Tel)
	{
		Equip_Tel = equip_Tel;
	}

	private String	Curr_Data;
	private String	Subsys_Id;
	private String	In_Img;
	private String	Out_Img;

	private String	Sid;

	public String getSubsys_Id()
	{
		return Subsys_Id;
	}

	public void setSubsys_Id(String subsys_Id)
	{
		Subsys_Id = subsys_Id;
	}

	public String getSize()
	{
		return Size;
	}

	public void setSize(String size)
	{
		Size = size;
	}

	public String getData_Lev()
	{
		return Data_Lev;
	}

	public void setData_Lev(String data_Lev)
	{
		Data_Lev = data_Lev;
	}

	public String getFlag()
	{
		return Flag;
	}

	public void setFlag(String flag)
	{
		Flag = flag;
	}

	public String getEquip_Id()
	{
		return Equip_Id;
	}

	public void setEquip_Id(String equip_Id)
	{
		Equip_Id = equip_Id;
	}

	public String getEquip_Name()
	{
		return Equip_Name;
	}

	public void setEquip_Name(String equip_Name)
	{
		Equip_Name = equip_Name;
	}

	public String getProject_Name()
	{
		return Project_Name;
	}

	public String getEquip_Height()
	{
		return Equip_Height;
	}

	public void setEquip_Height(String equip_Height)
	{
		Equip_Height = equip_Height;
	}

	public void setProject_Name(String project_Name)
	{
		Project_Name = project_Name;
	}

	public String getId()
	{
		return Id;
	}

	public void setId(String id)
	{
		Id = id;
	}

	public String getLongitude()
	{
		return Longitude;
	}

	public void setLongitude(String longitude)
	{
		Longitude = longitude;
	}

	public String getLatitude()
	{
		return Latitude;
	}

	public void setLatitude(String latitude)
	{
		Latitude = latitude;
	}

	public String getTop_Height()
	{
		return Top_Height;
	}

	public void setTop_Height(String top_Height)
	{
		Top_Height = top_Height;
	}

	public String getBase_Height()
	{
		return Base_Height;
	}

	public void setBase_Height(String base_Height)
	{
		Base_Height = base_Height;
	}

	public String getIn_Id()
	{
		return In_Id;
	}

	public void setIn_Id(String in_Id)
	{
		In_Id = in_Id;
	}

	public String getOut_Id()
	{
		return Out_Id;
	}

	public void setOut_Id(String out_Id)
	{
		Out_Id = out_Id;
	}

	public String getMaterial()
	{
		return Material;
	}

	public void setMaterial(String material)
	{
		Material = material;
	}

	public String getCurr_Data()
	{
		return Curr_Data;
	}

	public void setCurr_Data(String curr_Data)
	{
		Curr_Data = curr_Data;
	}

	public String getProject_Id()
	{
		return Project_Id;
	}

	public void setProject_Id(String project_Id)
	{
		Project_Id = project_Id;
	}

	public String getSid()
	{
		return Sid;
	}

	public void setSid(String sid)
	{
		Sid = sid;
	}

	public String getSign()
	{
		return Sign;
	}

	public void setSign(String sign)
	{
		Sign = sign;
	}

	public String getIn_Img()
	{
		return In_Img;
	}

	public void setIn_Img(String in_Img)
	{
		In_Img = in_Img;
	}

	public String getOut_Img()
	{
		return Out_Img;
	}

	public void setOut_Img(String out_Img)
	{
		Out_Img = out_Img;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

}
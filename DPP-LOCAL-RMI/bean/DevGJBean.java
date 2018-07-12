package bean;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import rmi.RmiBean;
import util.CommUtil;
import util.CurrStatus;

public class DevGJBean extends RmiBean
{
	public final static long	serialVersionUID	= RmiBean.RMI_DEVGJ;

	public long getClassId()
	{
		return serialVersionUID;
	}

	public DevGJBean()
	{
		super.className = "DevGJBean";
	}
	
	public DevGJBean(CurrStatus currStatus)
	{
		super.className = "DevGJBean";
		this.currStatus = currStatus;
	}

	/**
	 * ��ȡ��Ӧsql���
	 * 
	 */
	public String getSql(int pCmd)
	{
		String Sql = "";
		switch (pCmd)
		{
			case 0:// ��ѯ������&��Ŀ��
				Sql = " select t.id, t.Longitude, t.latitude, t.top_Height, t.base_height, t.Size, t.in_id, t.out_id, t.Material, t.Flag, t.Data_Lev, round((t.curr_data),2) , t.sign , t.project_id, t.project_name, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.In_Img, t.Out_Img, t.equip_time, t.road, t.rotation" + " from view_dev_gj t where t.id like '%" + currStatus.getFunc_Sub_Type_Id() + "%' " + " and t.project_id = '" + currStatus.getFunc_Project_Id() + "' " + " order by t.id  ";
				break;
			case 1:// ��ѯ��ȫ����
				Sql = " select t.id, t.Longitude, t.latitude, t.top_Height, t.base_height, t.Size, t.in_id, t.out_id, t.Material, t.Flag, t.Data_Lev, round((t.curr_data),2) , t.sign , t.project_id, t.project_name, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.In_Img, t.Out_Img, t.equip_time, t.road, t.rotation" + " from view_dev_gj t where t.project_id = '" + Project_Id + "' "  + " order by t.id  ";
				break;
			case 2:// ��ѯ������&��Ŀ��
				Sql = " select t.id, t.Longitude, t.latitude, t.top_Height, t.base_height, t.Size, t.in_id, t.out_id, t.Material, t.Flag, t.Data_Lev, round((t.curr_data),2) , t.sign , t.project_id, t.project_name, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.In_Img, t.Out_Img, t.equip_time, t.road, t.rotation" + " from view_dev_gj t where t.project_id = '" + currStatus.getFunc_Project_Id() + "' " + " order by t.id  ";
				break;

			case 3:// ��ѯ��������
			case 6:
				Sql = " select t.id, t.Longitude, t.latitude, t.top_Height, t.base_height, t.Size, t.in_id, t.out_id, t.Material, t.Flag, t.Data_Lev, round((t.curr_data),2) , t.sign , t.project_id, t.project_name, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.In_Img, t.Out_Img, t.equip_time, t.road, t.rotation" + " from view_dev_gj t " + " where t.id = '" + Id + "' and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" + " order by t.id  ";
				break;
			case 4:// ��ѯ�������
				Sql = " select t.id, t.Longitude, t.latitude, t.top_Height, t.base_height, t.Size, t.in_id, t.out_id, t.Material, t.Flag, t.Data_Lev, round((t.curr_data),2) , t.sign , t.project_id, t.project_name, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.In_Img, t.Out_Img, t.equip_time, t.road, t.rotation" + " from view_dev_gj t " + " where instr('" + Id + "', t.id) > 0 and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" + " order by t.id  ";
				break;
			case 5:// ��ѯ����Ŀ&��ϵͳ��
				Sql = " select t.id, t.Longitude, t.latitude, t.top_Height, t.base_height, t.Size, t.in_id, t.out_id, t.Material, t.Flag, t.Data_Lev, round((t.curr_data),2) , t.sign , t.project_id, t.project_name, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.In_Img, t.Out_Img, t.equip_time, t.road, t.rotation" + " from view_dev_gj t " + " where t.project_id = '" + Project_Id + "'" + " and substr(t.id, 3, 3) = '" + Subsys_Id + "'" + " order by t.id";
				break;
			case 7:// ��ѯ�����ص�ͼ��
				Sql = " select t.id, t.Longitude, t.latitude, t.top_Height, t.base_height, t.Size, t.in_id, t.out_id, t.Material, t.Flag, t.Data_Lev, round((t.curr_data),2) , t.sign , t.project_id, t.project_name, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.In_Img, t.Out_Img, t.equip_time, t.road, t.rotation" + " from view_dev_gj t " + " where t.id like '" + Id + "%' and t.project_id = '" + Project_Id + "'" + " order by t.id  ";
				break;
			case 10:// ���
				Sql = "insert into dev_gj(id, wgs84_lng, wgs84_lat, Longitude, latitude, top_Height, base_height, Size, in_id, out_id, Material, Flag, Data_Lev, project_id, road, sign) " + "values('" + Id + "','" + Wgs84_Lng + "','" + Wgs84_Lat + "','" + Longitude + "','" + Latitude + "','" + Top_Height + "','" + Base_Height + "','" + Size + "','" + In_Id + "','" + Out_Id + "','" + Material + "','" + Flag + "','" + Data_Lev + "','" + Project_Id + "','" + Road + "', " + Sign + ")";
				break;
			case 11:// �༭
				Sql = " update dev_gj t set t.in_id= '" + In_Id + "', t.out_id = '" + Out_Id + "' ,t.top_height= '" + Top_Height + "', t.base_height = '" + Base_Height + "', t.size = '" + Size + "', t.Flag = '" + Flag + "', t.Data_Lev = '" + Data_Lev + "',t.material = '" + Material + "', t.gj_name = '" + Equip_Name + "',t.equip_height = '" + Equip_Height + "',t.equip_tel = '" + Equip_Tel + "',t.road = '" + Road + "' " + " where t.id = '" + Id + "' and t.project_id = '" + currStatus.getFunc_Project_Id() + "'";
				break;
			case 19:// �༭
				Sql = " update dev_gj t set t.in_id= '" + In_Id + "', t.out_id = '" + Out_Id + "' ,t.top_height= '" + Top_Height + "', t.base_height = '" + Base_Height + "', t.size = '" + Size + "', t.Flag = '" + Flag + "', t.Data_Lev = '" + Data_Lev + "',t.material = '" + Material + "', t.gj_name = '" + Equip_Name + "',t.equip_height = '" + Equip_Height + "',t.equip_tel = '" + Equip_Tel + "',t.road = '" + Road + "' " + " where t.id = '" + Id + "' and t.project_id = '" + Project_Id + "'";
				break;
			case 12:// ɾ��
				Sql = " delete from dev_gj where id = '" + Id + "' and project_id = '" + currStatus.getFunc_Project_Id() + "'";
				break;

			case 13:// �ܾ�����
				Sql = " update dev_gj t set t.wgs84_lng = '" + Wgs84_Lng + "',t.Wgs84_Lat= '" + Wgs84_Lat + "',t.Longitude= '" + Longitude + "',t.latitude= '" + Latitude + "',t.in_id= '" + In_Id + "', t.out_id = '" + Out_Id + "' ,t.top_height= '" + Top_Height + "', t.base_height = '" + Base_Height + "', t.size = '" + Size + "', t.Flag = '" + Flag + "', t.Data_Lev = '" + Data_Lev + "',t.material = '" + Material + "',t.road = '" + Road + "',t.sign = '" + Sign + "' " + " where t.id = '" + Id + "' and t.project_id = '" + Project_Id + "'";
				break;

			case 14:// 񿾮��ͼ����
				Sql = " update dev_gj t set t.in_Img = '" + In_Img + "' where t.id = '" + Id + "' and t.project_id = '" + Project_Id + "'";
				break;
			case 18:// 񿾮��ͼ����
				Sql = " update dev_gj t set t.out_Img = '" + Out_Img + "' where t.id = '" + Id + "' and t.project_id = '" + Project_Id + "'";
				break;
			case 15:// ��ͼ��קͬ������
				Sql = " update dev_gj t set t.longitude = '" + Longitude + "', t.latitude = '" + Latitude + "' " + " where t.id = '" + Id + "' and t.project_id = '" + Project_Id + "'";
				break;
			case 16:// ɾ����ע�ӿ�
				Sql = " update dev_gj t set t.sign = '0' " + " where t.id = '" + Id + "' and t.project_id = '" + Project_Id + "'";
				break;
			case 17:// ��ӱ�ע�ӿ�
				Sql = " update dev_gj t set t.sign = '1', t.longitude = '" + Longitude + "', t.latitude = '" + Latitude + "' " + " where t.id = '" + Id + "' and t.project_id = '" + Project_Id + "'";
				break;
			case 21:// ��ȡ�ѱ�ע�ܾ�
				Sql = "{? = call Func_GJ_Get('" + Id + "', '" + Road + "')}";
				break;
			case 22:// ��ȡ�ѱ�ע�ܾ�
				Sql = "{? = call Func_GJ_Analog('" + Id + "','" + currStatus.getFunc_Sub_Type_Id() + "')}";
				break;
			case 23:// ��ȡδ��ע�ܾ�
				Sql = "{? = call Func_UnMark_GJ_Get('" + Project_Id + "')}";
				break;
			case 50:// ��ͼ��ק������ת�Ƕ�
				Sql = " update dev_gj t set t.rotation = '" + Rotation + "' " + " where t.id = '" + Id + "' and t.project_id = '" + Project_Id + "'";
				break;
			case 51:// �ٶ�����ϵת��Ѷ����ϵ
				Sql = " update dev_gj t set t.wx_lng = '" + WX_Lng + "',t.wx_lat = '" + WX_Lat + "' where t.id = '" + Id + "' and t.project_id = '" + Project_Id + "'";
				break;

		}
		return Sql;
	}

	/**
	 * �����ݿ��� ����������� ��װ��DevGjBean��
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
	 * �õ�ҳ������
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
			setWgs84_Lng(CommUtil.StrToGB2312(request.getParameter("Wgs84_Lng")));
			setWgs84_Lat(CommUtil.StrToGB2312(request.getParameter("Wgs84_Lat")));
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
	
	private String Wgs84_Lng;
	private String Wgs84_Lat;
	
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
	
	private String	pSimu; //����ǿ��

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

	public String getWgs84_Lng() {
		return Wgs84_Lng;
	}

	public void setWgs84_Lng(String wgs84_Lng) {
		Wgs84_Lng = wgs84_Lng;
	}

	public String getWgs84_Lat() {
		return Wgs84_Lat;
	}

	public void setWgs84_Lat(String wgs84_Lat) {
		Wgs84_Lat = wgs84_Lat;
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
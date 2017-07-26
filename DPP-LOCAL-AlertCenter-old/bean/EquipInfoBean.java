package bean;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EquipInfoBean
{
	private String Tid;
	private String Pid;
	private String CName;
	private String Project_Id;
	private String GJ_Id;
	private String CTime;
	private String Value;
	private String Top_Height;
	private String Base_Height;
	private String Equip_Height;
	
	public boolean getData(ResultSet pRs)
	{
		boolean IsOK = true;
		try
		{
			setTid(pRs.getString(1));
			setPid(pRs.getString(2));
			setCName(pRs.getString(3));
			setProject_Id(pRs.getString(4));
			setGJ_Id(pRs.getString(5));
			setCTime(pRs.getString(6));
			setValue(pRs.getString(7));
			setTop_Height(pRs.getString(8));
			setBase_Height(pRs.getString(9));
			setEquip_Height(pRs.getString(10));
		}
		catch (SQLException sqlExp)
		{
			sqlExp.printStackTrace();
		}
		return IsOK;
	}
	
	public String getTid()
	{
		return Tid;
	}
	public void setTid(String tid)
	{
		Tid = tid;
	}
	public String getPid()
	{
		return Pid;
	}
	public void setPid(String pid)
	{
		Pid = pid;
	}
	public String getCName()
	{
		return CName;
	}
	public void setCName(String cName)
	{
		CName = cName;
	}
	public String getProject_Id()
	{
		return Project_Id;
	}
	public void setProject_Id(String project_Id)
	{
		Project_Id = project_Id;
	}
	public String getGJ_Id()
	{
		return GJ_Id;
	}
	public void setGJ_Id(String gJ_Id)
	{
		GJ_Id = gJ_Id;
	}
	public String getCTime()
	{
		return CTime;
	}

	public void setCTime(String cTime)
	{
		CTime = cTime;
	}

	public String getValue()
	{
		return Value;
	}

	public void setValue(String value)
	{
		Value = value;
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
	public String getEquip_Height()
	{
		return Equip_Height;
	}
	public void setEquip_Height(String equip_Height)
	{
		Equip_Height = equip_Height;
	}
	

}

package bean;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DevGJAlertBean
{
	private String Id;
	private String In_Id;
	private String Out_Id;
	private String Value;
	private String Flag;
	private String Top_Height;
	private String Base_Height;
	private String Equip_Height;
	
	public boolean getData(ResultSet pRs)
	{
		boolean IsOK = true;
		try
		{
			setId(pRs.getString(1));
			setIn_Id(pRs.getString(2));
			setOut_Id(pRs.getString(3));
			setValue(pRs.getString(4));
			setFlag(pRs.getString(5));
			setTop_Height(pRs.getString(6));
			setBase_Height(pRs.getString(7));
			setEquip_Height(pRs.getString(8));
		}
		catch (SQLException sqlExp)
		{
			sqlExp.printStackTrace();
		}
		return IsOK;
	}

	public String getFlag()
	{
		return Flag;
	}

	public void setFlag(String flag)
	{
		Flag = flag;
	}

	public String getId()
	{
		return Id;
	}
	public void setId(String id)
	{
		Id = id;
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
	public String getValue()
	{
		return Value;
	}
	public void setValue(String value)
	{
		Value = value;
	}

	public String getTop_Height() {
		return Top_Height;
	}

	public void setTop_Height(String top_Height) {
		Top_Height = top_Height;
	}

	public String getBase_Height() {
		return Base_Height;
	}

	public void setBase_Height(String base_Height) {
		Base_Height = base_Height;
	}

	public String getEquip_Height() {
		return Equip_Height;
	}

	public void setEquip_Height(String equip_Height) {
		Equip_Height = equip_Height;
	}
	
	
}

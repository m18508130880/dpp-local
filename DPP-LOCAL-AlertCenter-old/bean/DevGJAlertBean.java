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
	
	
}

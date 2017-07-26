package bean;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DevGXAlertBean
{
	private String Id;
	private String Size;
	private String End_Id;
	
	public boolean getData(ResultSet pRs)
	{
		boolean IsOK = true;
		try
		{
			setId(pRs.getString(1));
			setSize(pRs.getString(2));
			setEnd_Id(pRs.getString(3));
		}
		catch (SQLException sqlExp)
		{
			sqlExp.printStackTrace();
		}
		return IsOK;
	}
	
	public String getEnd_Id()
	{
		return End_Id;
	}

	public void setEnd_Id(String end_Id)
	{
		End_Id = end_Id;
	}

	public String getId()
	{
		return Id;
	}
	public void setId(String id)
	{
		Id = id;
	}
	public String getSize()
	{
		return Size;
	}
	public void setSize(String size)
	{
		Size = size;
	}
}

package bean;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DevGXAlertBean
{
	private String Id;
	private String Size;
	private String End_Id;
	private String Start_Height;
	private String End_Height;
	private String Diameter;
	private String Depth;
	
	public boolean getData(ResultSet pRs)
	{
		boolean IsOK = true;
		try
		{
			setId(pRs.getString(1));
			setSize(pRs.getString(2));
			setEnd_Id(pRs.getString(3));
			setStart_Height(pRs.getString(4));
			setEnd_Height(pRs.getString(5));
			setDiameter(pRs.getString(6));
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

	public String getDiameter() {
		return Diameter;
	}

	public void setDiameter(String diameter) {
		Diameter = diameter;
	}

	public String getDepth() {
		return Depth;
	}

	public void setDepth(String depth) {
		Depth = depth;
	}
	
}

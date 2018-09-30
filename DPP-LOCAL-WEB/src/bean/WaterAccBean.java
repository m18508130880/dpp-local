package bean;


public class WaterAccBean
{
	public String analog_Y(String FileName, String p1)
	{
		AnalogBean analog = new AnalogBean();
		String WaterAccList = analog.AnalogWaterAcc(FileName, Double.parseDouble(p1));
		return WaterAccList;
	}
	public String analog_W(String FileName, String p1)
	{
		AnalogBean analog = new AnalogBean();
		String WaterAccList = analog.AnalogSewageAcc(FileName, Double.parseDouble(p1));
		return WaterAccList;
	}

	private String	SysId;
	private String	TimePeriod;
	private String	Water;
	private String	Status;

	public String getSysId()
	{
		return SysId;
	}

	public void setSysId(String sysId)
	{
		SysId = sysId;
	}

	public String getTimePeriod()
	{
		return TimePeriod;
	}

	public void setTimePeriod(String timePeriod)
	{
		TimePeriod = timePeriod;
	}

	public String getWater()
	{
		return Water;
	}

	public void setWater(String water)
	{
		Water = water;
	}

	public String getStatus()
	{
		return Status;
	}

	public void setStatus(String status)
	{
		Status = status;
	}
}

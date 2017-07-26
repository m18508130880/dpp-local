package bean;

import java.sql.ResultSet;
import java.util.ArrayList;

import rmi.RmiBean;
import util.CommUtil;

public class WaterAccBean extends RmiBean
{
	public final static long	serialVersionUID	= RmiBean.RMI_WATERACC;

	public long getClassId()
	{
		return serialVersionUID;
	}

	public WaterAccBean()
	{
		super.className = "WaterAccBean";

	}

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

	@Override
	public String getSql(int pCmd)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getData(ResultSet pRs)
	{
		// TODO Auto-generated method stub
		return false;
	}

}

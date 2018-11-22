package bean;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MacAnalysisBean
{
	public String getSql(int pCmd)
	{
		String Sql = "";
		switch (pCmd)
		{
			case 0://≤È—Ø
				Sql = " select  t.sn, t.attr_id, t.attr_name, t.flow, t.type, t.addrs_s, t.addrs_e, t.unit, t.amend "
						+ " from mac_analysis t "
						+ " where sn = '" + SN + "' "
						+ " order by t.sn";
				break;
		}
		return Sql;
	}
	
	public boolean getData(ResultSet pRs)
	{
		boolean IsOK = true;
		try
		{
			setSN(pRs.getString(1));
			setAttr_Id(pRs.getString(2));
			setAttr_Name(pRs.getString(3));
			setFlow(pRs.getString(4));
			setType(pRs.getString(5));
			setAddrs_S(pRs.getString(6));
			setAddrs_E(pRs.getString(7));
			setUnit(pRs.getString(8));
			setAmend(pRs.getString(9));
		}
		catch (SQLException sqlExp)
		{
			sqlExp.printStackTrace();
		}
		return IsOK;
	}
	
	private String SN;
	private String Attr_Id;
	private String Attr_Name;
	private String Flow;
	private String Type;
	private String Addrs_S;
	private String Addrs_E;
	private String Unit;
	private String Amend;

	private String Sid;

	public String getSN() {
		return SN;
	}

	public void setSN(String sN) {
		SN = sN;
	}

	public String getAttr_Id() {
		return Attr_Id;
	}

	public void setAttr_Id(String attr_Id) {
		Attr_Id = attr_Id;
	}

	public String getAttr_Name() {
		return Attr_Name;
	}

	public void setAttr_Name(String attr_Name) {
		Attr_Name = attr_Name;
	}

	public String getFlow() {
		return Flow;
	}

	public void setFlow(String flow) {
		Flow = flow;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public String getAddrs_S() {
		return Addrs_S;
	}

	public void setAddrs_S(String addrs_S) {
		Addrs_S = addrs_S;
	}

	public String getAddrs_E() {
		return Addrs_E;
	}

	public void setAddrs_E(String addrs_E) {
		Addrs_E = addrs_E;
	}

	public String getUnit() {
		return Unit;
	}

	public void setUnit(String unit) {
		Unit = unit;
	}

	public String getAmend() {
		return Amend;
	}

	public void setAmend(String amend) {
		Amend = amend;
	}

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}
	
	
}
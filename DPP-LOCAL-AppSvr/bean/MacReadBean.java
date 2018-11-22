package bean;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MacReadBean 
{
	public String getSql(int pCmd)
	{
		String Sql = "";
		switch (pCmd)
		{
			case 0://≤È—Ø
				Sql = " select  t.sn, t.cname, t.addrs, t.code, t.sign, t.analysis "
						+ " from mac_read t "
						+ " where sn ='" + SN + "' "
						+ "order by t.sn";
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
			setCName(pRs.getString(2));
			setAddrs(pRs.getString(3));
			setCode(pRs.getString(4));
			setSign(pRs.getString(5));
			setAnalysis(pRs.getString(6));
		}
		catch (SQLException sqlExp)
		{
			sqlExp.printStackTrace();
		}
		return IsOK;
	}
	
	private String SN;
	private String CName;
	private String Addrs;
	private String Code;
	private String Sign;
	private String Analysis;

	private String Sid;

	public String getSN() {
		return SN;
	}

	public void setSN(String sN) {
		SN = sN;
	}

	public String getCName() {
		return CName;
	}

	public void setCName(String cName) {
		CName = cName;
	}

	public String getAddrs() {
		return Addrs;
	}

	public void setAddrs(String addrs) {
		Addrs = addrs;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public String getSign() {
		return Sign;
	}

	public void setSign(String sign) {
		Sign = sign;
	}

	public String getAnalysis() {
		return Analysis;
	}

	public void setAnalysis(String analysis) {
		Analysis = analysis;
	}

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}
	
	
}
package bean;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MacReadTaskBean
{	
	public String getSql(int pCmd)
	{
		String Sql = "";
		switch (pCmd)
		{
			case 0://≤È—Ø
				Sql = " select  t.sn, t.pid, t.tid, t.read "
						+ " from mac_read_task t "
						+ " where pid = '" + PId + "' "
						+ " and status = '1' "
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
			setPId(pRs.getString(2));
			setTId(pRs.getString(3));
			setRead(pRs.getString(4));
		}
		catch (SQLException sqlExp)
		{
			sqlExp.printStackTrace();
		}
		return IsOK;
	}
	
	private String SN;
	private String PId;
	private String TId;
	private String Read;

	private String Sid;

	public String getSN() {
		return SN;
	}

	public void setSN(String sN) {
		SN = sN;
	}

	public String getPId() {
		return PId;
	}

	public void setPId(String pId) {
		PId = pId;
	}

	public String getTId() {
		return TId;
	}

	public void setTId(String tId) {
		TId = tId;
	}

	public String getRead() {
		return Read;
	}

	public void setRead(String read) {
		Read = read;
	}

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}
	
	
}
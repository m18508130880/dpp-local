package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.CallableStatement;//用于执行sql存储过程的接口
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.TPCClient;
import oracle.jdbc.OracleTypes;
import util.Cmd_Sta;
import util.CommUtil;
import util.DBUtil;
import util.MsgBean;
import bean.AlertInfoBean;
import bean.AnalogComputeBean;
import bean.CheckTaskBean;
import bean.CheckTaskGJBean;
import bean.CheckTaskGXBean;
import bean.CorpInfoBean;
import bean.DataBean;
import bean.DataGJBean;
import bean.DataGXBean;
import bean.DataHandBean;
import bean.DataNowAddBean;
import bean.DataNowBean;
import bean.DevBZBean;
import bean.DevGJBean;
import bean.DevGXBean;
import bean.DevHandBean;
import bean.DevMapBean;
import bean.DevSWBean;
import bean.EquipAlertBean;
import bean.EquipInfoBean;
import bean.MacAnalysisBean;
import bean.MacConfigBean;
import bean.MacReadBean;
import bean.MacReadTaskBean;
import bean.MacSendBean;
import bean.MacSendTaskBean;
import bean.MapImageBean;
import bean.ProjectInfoBean;
import bean.TaskListBean;
import bean.TextLLJBean;
import bean.ThreeGJBean;
import bean.ThreeGXBean;
import bean.UserInfoBean;
import bean.UserRoleBean;
import bean.WeatherBean;

/** RmiImpl implements Rmi 
 * @author Cui
 * 以后看到Rmi, 可以这么联想
 *  Rmi pRmi = new RmiImpl();
 *  
 * 0: 查询 
 * 1：插入,更新,删除  
 * 2：Function调用
 * 3：Package调用
 * 4: Producer调用
 */
public class RmiImpl extends UnicastRemoteObject implements Rmi
{
	public final static long serialVersionUID = 1001;
	
	private DBUtil m_DBUtil = null;	
	private TPCClient m_TPCClient = null;	
	
	/**空参构造器
	 * @throws RemoteException
	 */
	public RmiImpl() throws RemoteException {	 
		
	}
	
	/**初始化数据库连接池
	 * @param pDbUtil
	 */
	public void Init(DBUtil pDbUtil , TPCClient tPCClient ) {
		m_DBUtil    = pDbUtil;
		m_TPCClient = tPCClient;
	}
	

	/** RMI测试
	 * @see rmi.Rmi#Test()
	 */
	public boolean Test()throws RemoteException {
		return true;
	}
	
	/**根据cmd命令 , 传入的pBean , CurrPage当前页码 
	 **case 0: 查询 ;
	 * case 1: 增删改 ;
	 * case 2: Function函数调用 ;
	 * case 3: Package调用 ;   ???oracle.jdbc???
	 * case 4: Producer调用 ;
	 * @see rmi.Rmi#RmiExec(int, rmi.RmiBean, int, int)
	 */
	public MsgBean RmiExec(int pCmd, RmiBean pBean, int CurrPage, int PageSize) throws RemoteException {
		MsgBean objBean = null;
		ArrayList<?> aList = null;
		String Sql = pBean.getSql(pCmd);		
		int recordCount = 0;
		System.out.println("ClassId:["+pBean.getClassId()+"]"+" ClassName["+pBean.getClassName()+"]"+" Cmd["+ pCmd + "]");
		System.out.println("BSql["+Sql+"] CurrPage[" + CurrPage + "] PageSize[" + PageSize + "]");
		switch(pCmd/10)
		{
			case 0://查询
				if(0 < CurrPage)  
				{//每页个数限制
					//总数: 需要分页显示时,才会去计算recordCount值  查询记录总数
					recordCount = Integer.parseInt(doRecordCount("select count(*) as counts from (" + Sql + ")subselect"));
					System.out.println("RecordCount["+recordCount+"]");
					//分页: 有分页显示,后台RMI下 才会出现以下打印
					Sql = Sql + " LIMIT " + PageSize + " OFFSET " + (CurrPage-1)*PageSize;
					System.out.println("ESql["+Sql+"]");
					System.out.println("条数["+PageSize+"]");
				}				
				aList = doSelect(Sql, pBean.getClassId());
				if(aList != null && aList.size() > 0)
				{//若有数据返回  将数据封装到MsgBean中去.
					objBean = new MsgBean(MsgBean.STA_SUCCESS,  aList, recordCount);
				}
				else
				{//若无数据返回  MsgBean中的集合对象则为空.
					objBean = new MsgBean(MsgBean.STA_FAILED,  null, recordCount);
				}
				break;
				
			case 1://插入,更新,删除				
			case 5://插入,更新,删除				
				if(doUpdate(Sql))    
				{		
					objBean = new MsgBean(MsgBean.STA_SUCCESS, null, recordCount);
				}
				else
				{
					objBean = new MsgBean(MsgBean.STA_FAILED, CommUtil.IntToStringLeftFillSpace(MsgBean.STA_FAILED, 4), recordCount);
				}	
				break;
				
			case 2://Function调用
				String rst = doFunction(Sql); 
				CommUtil.PRINT(rst);
				if(rst != null && rst.substring(0, 4).equals("0000"))    
				{		
					objBean = new MsgBean(MsgBean.STA_SUCCESS, rst, recordCount);
				}
				else
				{
					objBean = new MsgBean(Integer.parseInt(rst.substring(0, 4)), rst.substring(0, 4), recordCount);
				}	
				break;
				
			case 3://Package调用
				aList = Do_Package(Sql, pBean.getClassId());
				if(aList != null && aList.size() > 0)
				{
					objBean = new MsgBean(MsgBean.STA_SUCCESS,  aList, recordCount);
				}
				else
				{
					objBean = new MsgBean(MsgBean.STA_FAILED,  null, recordCount);
				}
				break;
				
			case 4://Producer调用
				doProducer(Sql); 
				rst = "0000";
				CommUtil.PRINT(rst);
				if(rst != null && rst.substring(0, 4).equals("0000"))    
				{		
					objBean = new MsgBean(MsgBean.STA_SUCCESS, rst, recordCount);
				}
				else
				{
					objBean = new MsgBean(Integer.parseInt(rst.substring(0, 4)), rst.substring(0, 4), recordCount);
				}	
				break;
		}			
		return objBean;
	}
	

	/** 数据库查询操作
	 * @param pSql
	 * @param pClass
	 *    bean的类值
	 * @return
	 */
	public ArrayList<?> doSelect(String pSql, long pClass) //Bean Type
	{
		ArrayList<Object> alist = new ArrayList<Object>();
		RmiBean rmiBean = null;
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try 
		{
			conn = m_DBUtil.objConnPool.getConnection();
			conn.setAutoCommit(false);
			pStmt = conn.prepareStatement(pSql);
			rs = pStmt.executeQuery();
			while(rs.next())          //数据库中有多少条数据,循环加入alist集合中
			{
				switch((int)pClass)
				{
				/******************system****************/
				/******************admin*****************/
					
				    case RmiBean.RMI_CORP_INFO:
				    	rmiBean = new CorpInfoBean();
				    	break;
					case RmiBean.RMI_USER_INFO:
						rmiBean = new UserInfoBean();
						break;
					case RmiBean.RMI_USER_ROLE:
						rmiBean = new UserRoleBean();
						break;
					case RmiBean.RMI_PROJECT_INFO:
						rmiBean = new ProjectInfoBean();
						break;
					case RmiBean.RMI_EQUIP_INFO:
						rmiBean = new EquipInfoBean();
						break;
					case RmiBean.RMI_DATA_NOW:
						rmiBean = new DataNowBean();
						break;

				/******************user*****************/
					case RmiBean.RMI_DATA:
						rmiBean = new DataBean();
						break;
					case RmiBean.RMI_DATAGJ:
						rmiBean = new DataGJBean();
						break;	
					case RmiBean.RMI_DATAGX:
						rmiBean = new DataGXBean();
						break;
					case RmiBean.RMI_DEVGJ:
						rmiBean = new DevGJBean();
						break;
					case RmiBean.RMI_DEVGX:
						rmiBean = new DevGXBean();
						break;
					case RmiBean.RMI_MAP_IMAGE:
						rmiBean = new MapImageBean();
						break;
					case RmiBean.RMI_ALERT:
						rmiBean = new AlertInfoBean();
						break;
					case RmiBean.RMI_DEVBZ:
						rmiBean = new DevBZBean();
						break;
					case RmiBean.RMI_DEVMAP:
						rmiBean = new DevMapBean();
						break;
					case RmiBean.RMI_DEVHAND:
						rmiBean = new DevHandBean();
						break;
					case RmiBean.RMI_DATAHAND:
						rmiBean = new DataHandBean();
						break;
					case RmiBean.RMI_THREE_GJ:
						rmiBean = new ThreeGJBean();
						break;
					case RmiBean.RMI_THREE_GX:
						rmiBean = new ThreeGXBean();
						break;
					case RmiBean.RMI_EQUIP_ALERT:
						rmiBean = new EquipAlertBean();
						break;
					case RmiBean.RMI_CHECK_TASK:
						rmiBean = new CheckTaskBean();
						break;
					case RmiBean.RMI_CHECK_GJ:
						rmiBean = new CheckTaskGJBean();
						break;
					case RmiBean.RMI_CHECK_GX:
						rmiBean = new CheckTaskGXBean();
						break;
						
					case RmiBean.RMI_WEATHER:
						rmiBean = new WeatherBean();
						break;

					case RmiBean.RMI_TEXT_LLJ:
						rmiBean = new TextLLJBean();
						break;
						
					case RmiBean.RMI_COMPUTE:
						rmiBean = new AnalogComputeBean();
						break;
					case RmiBean.RMI_DEVSW:
						rmiBean = new DevSWBean();
						break;
					case RmiBean.RMI_DATA_ADD:
						rmiBean = new DataNowAddBean();
						break;
						
					/**************设备编辑*************/
					case RmiBean.RMI_MAC_ANALYZE:
						rmiBean = new MacAnalysisBean();
						break;
					case RmiBean.RMI_MAC_READ:
						rmiBean = new MacReadBean();
						break;
					case RmiBean.RMI_MAC_SEND:
						rmiBean = new MacSendBean();
						break;
					case RmiBean.RMI_MAC_CONFIG:
						rmiBean = new MacConfigBean();
						break;
					case RmiBean.RMI_SEND_TASK:
						rmiBean = new MacSendTaskBean();
						break;
					case RmiBean.RMI_READ_TASK:
						rmiBean = new MacReadTaskBean();
						break;
					case RmiBean.RMI_TASK_LIST:
						rmiBean = new TaskListBean();
						break;
				}
				rmiBean.getData(rs);
				alist.add(rmiBean);
			}
		} catch (SQLException sqlExp)
		{
			sqlExp.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{				
					rs.close();
					rs = null;
				}
				if(pStmt != null)
				{				
					pStmt.close();
					pStmt = null;
				}
				if(conn != null)
				{
					conn.close();	
					conn = null;
				}
			}catch(Exception ex)
			{ex.printStackTrace();}
		}
		return alist;
	}
	
	/** 数据库的 增\删\改 操作
	 * @param pSql
	 * @return
	 *     返回boolean,表示是否操作成功
	 */
	public boolean doUpdate(String pSql)
	{
		boolean IsOK = false;
		Connection conn = null;
		PreparedStatement pStmt = null;
		try 
		{
			conn = m_DBUtil.objConnPool.getConnection();
			conn.setAutoCommit(false);
			pStmt = conn.prepareStatement(pSql);
			if (pStmt.executeUpdate() > 0) 
			{
				IsOK = true;
				conn.commit();
			} 
		} 
		catch (SQLException sqlExp) 
		{
			sqlExp.printStackTrace();
		}
		finally
		{
			try
			{
				if(pStmt != null)
				{				
					pStmt.close();
					pStmt = null;
				}
				if(conn != null)
				{
					conn.close();	
					conn = null;
				}
			}catch(Exception ex)
			{ex.printStackTrace();}
		}
		return IsOK;
	}
	
	/** 数据库的Function函数调用
	 * @param pSql
	 * @return 
	 *     返回数据库函数的? 
	 */
	public String doFunction(String pSql)
	{
		String rslt = null;
		Connection conn = null;
		CallableStatement cstat = null;
		try 
		{
			conn = m_DBUtil.objConnPool.getConnection();
			conn.setAutoCommit(false);
			cstat = conn.prepareCall(pSql);
			cstat.registerOutParameter(1, java.sql.Types.VARCHAR);
			cstat.execute();
			rslt = cstat.getString(1);
			conn.commit();
			//System.out.println("rslt:" + rslt);
		}
		catch(SQLException sqlExp)
		{
			sqlExp.printStackTrace();
			return  CommUtil.IntToStringLeftFillSpace(MsgBean.STA_FAILED, 4);
		}
		finally
		{
			try
			{
				if(cstat != null)
				{				
					cstat.close();
					cstat = null;
				}
				if(conn != null)
				{
					conn.close();	
					conn = null;
				}
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return rslt;
	}
	
	/** 数据库的Producer存储过程调用
	 * @param pSql
	 * @return 
	 *     返回数据库存储过程的?返回值? 
	 */
	public String doProducer(String pSql)
	{
		String rslt = null;
		Connection conn = null;
		CallableStatement cstat = null;
		try 
		{
			conn = m_DBUtil.objConnPool.getConnection();
			conn.setAutoCommit(false);
			cstat = conn.prepareCall(pSql);
			cstat.execute();
			conn.commit();
			//System.out.println("rslt:" + rslt);
		}
		catch(SQLException sqlExp)
		{
			sqlExp.printStackTrace();
			return  CommUtil.IntToStringLeftFillSpace(MsgBean.STA_FAILED, 4);
		}
		finally
		{
			try
			{
				if(cstat != null)
				{				
					cstat.close();
					cstat = null;
				}
				if(conn != null)
				{
					conn.close();	
					conn = null;
				}
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return rslt;
	}
	
	
	/** 数据库  记录总数查询
	 * @param pSql
	 * @return String
	 *   将int值转成String类型返回
	 */
	public String doRecordCount(String pSql)
	{
		String rslt = null;
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try 
		{
			conn = m_DBUtil.objConnPool.getConnection();
			conn.setAutoCommit(false);
			pStmt = conn.prepareStatement(pSql);
			rs = pStmt.executeQuery();		
			while(rs.next())
			{
				rslt = rs.getString("counts");
			}
		} 
		catch (SQLException sqlExp) 
		{
			sqlExp.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{				
					rs.close();
					rs = null;
				}
				if(pStmt != null)
				{				
					pStmt.close();
					pStmt = null;
				}
				if(conn != null)
				{
					conn.close();	
					conn = null;
				}
			}catch(Exception ex)
			{ex.printStackTrace();}
		}
		return rslt;
	}
	
	/**oracle.jdbc
	 * @param pSql
	 * @param pClass
	 * @return
	 */
	public ArrayList<?> Do_Package(String pSql, long pClass) 
	{
		ArrayList<Object> alist = new ArrayList<Object>();
		RmiBean rmiBean = null;
		Connection conn = null;
		CallableStatement cstmt = null;
		ResultSet rs = null;
		try 
		{
			conn = m_DBUtil.objConnPool.getConnection();
			cstmt = conn.prepareCall(pSql);
			cstmt.registerOutParameter(1, OracleTypes.CURSOR);
			cstmt.execute();
			rs = (ResultSet) cstmt.getObject(1);
			while(rs.next())
			{
				switch((int)pClass)
				{
					//case RmiBean.RMI_SURROUNDINGS_INFO:
							//rmiBean = new SurroundingsInfoBean();
						//break;
				}
				rmiBean.getData(rs);						
				alist.add(rmiBean);	
			}
		} catch (SQLException sqlExp) 
		{
		sqlExp.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
				{				
					rs.close();
					rs = null;
				}
				if(cstmt != null)
				{				
					cstmt.close();
					cstmt = null;
				}
				if(conn != null)
				{
					conn.close();	
					conn = null;
				}
			}catch(Exception ex)
			{ex.printStackTrace();}
		}
		return alist;
	}
	
	public String Client(int pCmd, String pClient_Id, String pOprator)throws RemoteException
	{
		System.out.println("pCmd["+pCmd+"]\npClient_Id["+pClient_Id+"]");
		String ret = "9999";
		switch(pCmd)
		{
			case Cmd_Sta.CMD_UPDATE_DATA:
			{
				String SendData = CommUtil.StrBRightFillSpace(" ", 20)
						+ "0000"
						+ Cmd_Sta.CMD_UPDATE_DATA
						+ CommUtil.StrBRightFillSpace(pClient_Id, 10);
				System.out.println("SendData["+SendData+"]");
				if(m_TPCClient.SetSendMsg(SendData, 1))
				{
					ret = "0000";
				}
				break;
			}
		}	
		return ret;
	}
	public String DTUAction(int pCmd, String pSN, String pClient_Id, String pAction)throws RemoteException
	{
		System.out.println("pCmd["+pCmd+"]\npClient_Id["+pClient_Id+"]");
		String ret = "9999";
		switch(pCmd){
			case Cmd_Sta.CMD_DTU_ACTION:
				String SendData = CommUtil.StrBRightFillSpace(" ", 20)
						+ "0000"
						+ Cmd_Sta.CMD_DTU_ACTION
						+ CommUtil.StrBRightFillSpace(pSN, 8)
						+ CommUtil.StrBRightFillSpace(pClient_Id, 10)
						+ CommUtil.StrBRightFillSpace(pAction, 2);
				System.out.println("SendData["+SendData+"]");
				if(m_TPCClient.SetSendMsg(SendData, 1))
				{
					ret = "0000";
				}
			break;
		}
		return ret;
	}
}

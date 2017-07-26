package rmi;
import java.rmi.Remote;
import java.rmi.RemoteException;

import util.*;

/**远程方法调用接口
 * @author Cui
 *包含 public MsgBean RmiExec(int pCmd, RmiBean pBean, int CurrPage) 方法
 */
public interface Rmi extends Remote
{
	public boolean Test()throws RemoteException;
	
	/**根据cmd命令 , 传入的pBean , CurrPage当前页码 
	 **case 0: 查询 ;
	 * case 1: 增删改 ;
	 * case 2: Function函数调用 ;
	 * case 3: Package调用 ;   ?oracle.jdbc操作?
	 * case 4: Producer调用 ;
	 * @param PageSize TODO
	 * @see rmi.Rmi#RmiExec(int, rmi.RmiBean, int, int)
	 */
	public MsgBean RmiExec(int pCmd, RmiBean pBean, int CurrPage, int PageSize) throws RemoteException;
	
	public String Client(int pCmd, String pClientId, String pOprator)throws RemoteException;
}
package rmi;

import java.io.Serializable;
import java.sql.ResultSet;

import util.CurrStatus;
import util.MsgBean;


/**RmiBean ʵ���� serializable �����л��ӿ�
 * @author Cui
 * bean������඼�̳� RmiBean
 * ΪʲôҪ�����л�?
 *     ���Ҫͨ��Զ�̵ķ������ã�RMI��ȥ����һ��Զ�̶���ķ��������ڼ����A�е���
 *     ��һ̨�����B�Ķ���ķ�������ô����Ҫͨ��JNDI�����ȡ�����BĿ���������ã�
 *     �������B���͵�A������Ҫʵ�����л��ӿ�
 */
public abstract class RmiBean implements Serializable
{
	public final static String UPLOAD_PATH = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/excel/";
	
	/**************************system**********************/

	
	/**************************admin***********************/
	public static final int	RMI_DEVGJ					= 11;
	public static final int	RMI_DEVGX					= 12;
	public static final int RMI_USER_INFO			 	= 13;
	public static final int RMI_USER_ROLE		   	    = 14;	
	public static final int RMI_PROJECT_INFO			= 15;
	public static final int RMI_EQUIP_INFO			    = 16;
	
	public static final int RMI_USER_POSITION			= 18;
	public static final int RMI_CORP_INFO			    = 19;
	public static final int RMI_DATA_NOW			    = 20;
	
	/**************************user-data*******************/
	public static final int RMI_DATA			        = 30;
	public static final int	RMI_DATAGJ					= 31;
	public static final int	RMI_DATAGX					= 32;
	public static final int	RMI_WATERACC				= 33;
	public static final int	RMI_MAP_IMAGE				= 34;
	public static final int	RMI_ALERT					= 35;
	public static final int	RMI_DEVBZ					= 36;
	public static final int	RMI_DEVMAP					= 37;
	public static final int	RMI_DEVHAND					= 38;
	public static final int	RMI_DATAHAND				= 39;
	public static final int	RMI_THREE_GJ				= 40;
	public static final int	RMI_THREE_GX				= 41;

	public static final int	RMI_EQUIP_ALERT				= 42;
	
	public static final int	RMI_CHECK_TASK				= 43;
	public static final int	RMI_CHECK_GJ				= 44;
	public static final int	RMI_CHECK_GX				= 45;

	public static final int	RMI_WEATHER					= 46;

	public static final int	RMI_TEXT_LLJ					= 47;
	public static final int	RMI_COMPUTE					= 48;

	public static final int	RMI_DEVSW					= 49;
	
	/****************************�豸�༭**********************/
	
	public static final int	RMI_MAC_ANALYZE				= 60;
	public static final int	RMI_MAC_READ				= 61;
	public static final int	RMI_MAC_SEND				= 62;
	public static final int	RMI_MAC_CONFIG					= 63;
	public static final int	RMI_READ_TASK					= 64;
	public static final int	RMI_SEND_TASK					= 65;
	public MsgBean    msgBean = null;
	public String     className;
	public CurrStatus currStatus = null;
	
	public RmiBean()
	{
		msgBean = new MsgBean(); 		
	}
	
	public String getClassName()
	{
		return className;
	}
	
	public abstract long getClassId();
	public abstract String getSql(int pCmd);
	public abstract boolean getData(ResultSet pRs);
}

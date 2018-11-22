package bean;

import java.util.Hashtable;

import net.TcpSvr;
import util.Cmd_Sta;
import util.CommUtil;
import util.DBUtil;

public class DeviceTimedTaskBean {
	private String SN;
	private String Id;
	private String Cycle;
	private String Order;
	
	private DBUtil m_DBUtil;
	private TcpSvr m_TcpSvr;
	
	public Hashtable<String, Thread> objThrdTable = null;//��ʱ�����߳��б�
	
	private TimedThrd timedThrd;	// ��ʱ�����߳�
		
	public DeviceTimedTaskBean(DBUtil m_DBUtil, TcpSvr m_TcpSvr){
		this.m_DBUtil = m_DBUtil;
		this.m_TcpSvr = m_TcpSvr;
		objThrdTable = new Hashtable<String, Thread>();
	}
	
	/**
	 * �����ɼ�����
	 */
	public void collectDataNow(String SN){
		String taskList = m_DBUtil.Func("Func_Send_GetOne", SN); // ȡ����ǰthread�����ж�ʱ����
		String [] task = taskList.split("\\|");

		Id = task[1];
		//Cycle = task[2];
		Order = task[3];
		
		// ���Ͳɼ�����
		if(m_TcpSvr.DisPatch(Cmd_Sta.COMM_DELIVER, CommUtil.StrBRightFillSpace(Id, 20), Order)){
			CommUtil.PRINT("Thread [" + Id + "] Send Now [" + Order + "] SUCCESS");
		}else{
			CommUtil.PRINT("Thread [" + Id + "] Send Now [" + Order + "] ERROR");
		}
	}
	
	/**
	 * �����ɼ�ȫ������
	 */
	public void collectDataNowAll(String pClientKey){
		String taskList = m_DBUtil.Func("Func_Send_GetAll_TId", pClientKey); // ȡ����ǰclien�����ж�ʱ����
		String [] task = taskList.split(";");
		for(int i = 0; i < task.length; i ++){
			String [] obj = task[i].split("\\|");
			Id = obj[1];
			//Cycle = obj[2];
			Order = obj[3];
			// ���Ͳɼ�����
			if(m_TcpSvr.DisPatch(Cmd_Sta.COMM_DELIVER, CommUtil.StrBRightFillSpace(Id, 20), Order)){
				CommUtil.PRINT("Thread [" + Id + "] Send Now [" + Order + "] SUCCESS");
			}else{
				CommUtil.PRINT("Thread [" + Id + "] Send Now [" + Order + "] ERROR");
			}
		}
	}
	
	/**
	 * �򿪵�ǰcline�Ķ�ʱ����
	 * @param pClientKey
	 */
	public void openTimedTask(String pClientKey){
		String taskList = m_DBUtil.Func("Func_Send_GetAll", pClientKey); // ȡ����ǰclien�����ж�ʱ����
		String [] task = taskList.split(";");
		for(int i = 0; i < task.length; i ++){
			String [] obj = task[i].split("\\|");
			SN = obj[0];
			if(objThrdTable.containsKey(SN)){	// ����������Ѵ��ڣ�����һ��
				continue;
			}
			Id = obj[1];
			Cycle = obj[2];
			Order = obj[3];
			CommUtil.PRINT("SN["+SN+"]Id["+Id+"]Cycle["+Cycle+"]SN["+Order+"]");
			timedThrd = new TimedThrd(Id, Cycle, Order);	// �½��߳�
			timedThrd.start();								// �����߳�

			objThrdTable.put(SN, timedThrd);
			CommUtil.PRINT("open Thread["+SN+"]");
		}
	}
	
	/**
	 * �رյ�ǰcline�Ķ�ʱ����
	 * @param pClientKey
	 */
	public void closeTimedTask(String pClientKey){
		String taskList = m_DBUtil.Func("Func_Send_GetSN", pClientKey); // ȡ����ǰclien�����ж�ʱ����
		String [] task = taskList.split("\\|");
		for(int i = 0; i < task.length; i ++){
			SN = task[i];
			System.out.println("SN["+SN+"]");
			if(SN != null && SN.length() > 0){
				// ȡ���߳�
				timedThrd = (TimedThrd) objThrdTable.get(SN);
				// �߳�interrupted��ʹ�߳�ֹͣ
				timedThrd.interrupt();
				// �߳�tab���Ƴ��رյ��߳�
				objThrdTable.remove(SN);
				CommUtil.PRINT("close Thread["+SN+"]");
			}
		}
	}
	
	/**
	 * �򿪵�����ʱ����
	 * @param SN
	 */
	public void openOneTimedTask(String SN){
		String taskList = m_DBUtil.Func("Func_Send_GetOne", SN);
		String [] task = taskList.split("\\|");
		SN = task[0];
		if(objThrdTable.containsKey(SN)){
			return;
		}
		Id = task[1];
		Cycle = task[2];
		Order = task[3];
		
		timedThrd = new TimedThrd(Id, Cycle, Order);
		timedThrd.start();

		objThrdTable.put(SN, timedThrd);
		CommUtil.PRINT("open Thread["+SN+"]");
	}
	
	/**
	 * �رյ�����ʱ����
	 * @param SN
	 */
	public void closeOneTimedTask(String SN){
		// ȡ���߳�
		timedThrd = (TimedThrd) objThrdTable.get(SN);
		// �߳�interrupted��ʹ�߳�ֹͣ
		timedThrd.interrupt();
		// �߳�tab���Ƴ��رյ��߳�
		objThrdTable.remove(SN);
		CommUtil.PRINT("close Thread["+SN+"]");
	}
	
	private class TimedThrd extends Thread
	{
		private String taskId;
		private long taskCycle;
		private String taskOrder;
		public TimedThrd(String taskId, String taskCycle, String taskOrder){
			this.taskId = taskId;
			this.taskCycle = Long.parseLong(taskCycle);
			this.taskOrder = taskOrder;
		}
		
		public void run()
		{
			while(!isInterrupted()){
				try{
					// ���Ͳɼ�����
					if(m_TcpSvr.DisPatch(Cmd_Sta.COMM_COLLECT, CommUtil.StrBRightFillSpace(taskId, 20), taskOrder)){
						CommUtil.PRINT("Thread [" + taskId + "] Send [" + taskOrder + "] SUCCESS");
					}else{
						CommUtil.PRINT("Thread [" + taskId + "] Send [" + taskOrder + "] ERROR");
					}
					
					sleep(taskCycle * 1000);//taskCycle ����
				}catch(InterruptedException e){
	                e.printStackTrace();
	                break;//�����쳣֮��ִ��break����ѭ����
	            }catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
		}	
	}
	
}
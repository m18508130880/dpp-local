package net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.Vector;
import util.*;

public abstract class TcpSvrBase extends Thread {
	public static final int STATUS_CLIENT_ONLINE = 0;
	public static final int STATUS_CLIENT_OFFLINE = 1;
	public static final String TYPE_OPERATOR = "0";

	// TCP������
	private ServerSocket objTcpSvrSock = null;

	// ���������б�,���ڿͻ������ݽ���
	public LinkedList<Object> recvMsgList = null;
	public Byte markRecv = new Byte((byte) 1);     //�߳�ͬ����

	private int m_Seq = 0;
	private int m_iPort = 0;
	private int m_iTimeOut = 0;
	public int m_iClientKey = 0;

	// ��ȡ�����ļ�����
	public TcpSvrBase() throws Exception {
	}

	/**
	 * ��ʼ��Socket
	 * 
	 * @param iPort
	 * @param iTimeOut
	 * @return boolean
	 */
	public boolean init(int iPort, int iTimeOut) {
		try {
			m_iPort = iPort;
			m_iTimeOut = iTimeOut;
			objTcpSvrSock = new ServerSocket(m_iPort); // �����󶨵��ض��˿ڵġ����������׽��֡�
			if (null == objTcpSvrSock) {
				return false;
			}
			recvMsgList = new LinkedList<Object>();
			this.start(); // ִ��run
			return true;
		} catch (IOException ioExp) {
			ioExp.printStackTrace();
			return false;
		}
	}

	/*
	 * ����Socket���� TcpSvrBase������һ���߳� (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		while (true) {
			try {
				Socket objClient = objTcpSvrSock.accept(); // ����һ�� ��Կͻ��� socket ���жԽ�
				objClient.setSoTimeout(m_iTimeOut * 1000); // ͨ��ָ����ʱֵ����/����
														   // SO_TIMEOUT���Ժ���Ϊ��λ��

				DataInputStream RecvChannel = new DataInputStream(objClient.getInputStream());
				byte[] Buffer = new byte[1024];            // ��������������Buffer

				int RecvLen = RecvChannel.read(Buffer);    // ���ض�ȡ���Ļ�����Buffer �ֽڳ���

				CommUtil.PRINT("Send Original:");          // ��ӡ Send Original ���
				CommUtil.printMsg(Buffer, RecvLen);        // ��ӡ 24 23 2a 72 65 67 2c  ע���

				if (7 > RecvLen) {
					objClient.close();
					objClient = null;
					continue;
				}

				// ������֤
				String Pid = null;
				if (null == (Pid = CheckClient(Buffer, objClient))) {
					System.out.println(Pid);
					continue;
				}

				// ����ظ�
				DataOutputStream SendChannel = new DataOutputStream(objClient.getOutputStream());
				SendChannel.write(new String(Buffer, 0, 44).getBytes());
				SendChannel.flush();
				objClient.setSoTimeout(0);
				ClientStatusNotify(Pid, STATUS_CLIENT_ONLINE);
				continue;
			} catch (Exception ex) {
				ex.printStackTrace();
				continue;
			}
		}// while
	}

	// ������֤
	protected abstract String CheckClient(byte[] buf, Socket objClient);

	// ����յ��ر�ָ��͹ر�SOCKET���ͷ���Դ
	protected abstract void ClientStatusNotify(String strClientKey, int Status);

	protected abstract void ClientClose(String pClientKey);

	// ȡ�ý����߳������б�
	public byte[] GetRecvMsgList() {
		byte[] data = null;
		synchronized (markRecv) {
			if (!recvMsgList.isEmpty()) {
				data = (byte[]) recvMsgList.removeFirst();
			}
		}
		return data;
	}

	// ���ý����߳��б�
	public void SetRecvMsgList(Object object) {
		synchronized (markRecv) {
			recvMsgList.addLast(object);
		}
	}

	// �������к�
	public int GetSeq() {
		if (m_Seq++ == 0xffffff)
			m_Seq = 0;
		return m_Seq;
	}

	// ���ؽ����б��С
	public long GetRecvMsgListLength() {
		return recvMsgList.size();
	}

	protected abstract byte[] GetActiveTestBuf();

	protected abstract byte[] EnCode(int msgCode, String pData);

	/************************************ ClientSocket *****************************************/
	// ��ÿ���ͻ������Ӧ�ķ���ˣ�ͬ���ڿͻ���
	public class ClientSocket extends Thread {
		public  Socket   objSocket   = null;
		private RecvThrd objRecvThrd = null;
		private SendThrd objSendThrd = null;

		private LinkedList<Object> sendMsgList = null;
		private byte[]   markSend    = new byte[1];
		public  String   m_ClientKey = "";    // �ͻ��� ID ��
		private int      m_TestSta   = 0;

		// ��ʼ��SOCKET
		public boolean init(Socket objClient, String pClientKey) {
			try {
				m_ClientKey = pClientKey;
				objSocket   = objClient;
				objSocket.setSoTimeout(0);

				sendMsgList = new LinkedList<Object>();

				objRecvThrd = new RecvThrd(objSocket);
				objRecvThrd.start();

				objSendThrd = new SendThrd(objClient);
				objSendThrd.start();

				this.start();
			} catch (Exception exp) {
				exp.printStackTrace();
				return false;
			}
			return true;
		}

		public void run() {
			int testTime = (int) (new java.util.Date().getTime() / 1000);    // ����ʱ��
			int nowTime = testTime;                                          // ��ǰʱ��
			int dTime = 0;

			// Active Test
			while (true) 
			{
				try {
					sleep(2000);
					if (null == objSocket || objSocket.isClosed()) 
					{
						CommUtil.LOG("socket is closed " + m_ClientKey);
						ClientClose(m_ClientKey);
						break;
					}
					nowTime = (int) (new java.util.Date().getTime() / 1000); // getTime����Ǻ�����
					dTime = nowTime - testTime;
					if (dTime > m_iTimeOut) 
					{
						m_TestSta++;
						if (m_TestSta > CmdUtil.ACTIVE_TEST_END) 
						{
							CommUtil.LOG("m_TestSta > CmdUtil.ACTIVE_TEST_END "+ m_ClientKey);
							ClientClose(m_ClientKey);
						} 
						else
						{
							if (m_TestSta >= CmdUtil.ACTIVE_TEST_START) 
							{
								byte[] byteData = GetActiveTestBuf();
								if (null != byteData) 
								{
									SetSendMsgList(byteData); // ���뷢���б�
									CommUtil.LOG("Send Active Test..");
								}
							}
						}
						testTime = nowTime;
					}
				} catch (Exception ex) {
					CommUtil.LOG("TcpSvr/Run:Active Test Error.............\n");
					ex.printStackTrace();
					continue;
				}
			}
		}

		// ����Ϣ�͵����Ͷ���
		public void SendMsg(int msgCode, String pData) {
			SetSendMsgList(EnCode(msgCode, pData));
		}

		private void SetSendMsgList(Object object) {
			synchronized (markSend) {
				sendMsgList.addLast(object);
			}
		}

		// �ӷ��Ͷ���ȡһ����Ϣ
		private byte[] getSendMsgList() {
			byte[] data = null;
			synchronized (markSend) {
				if (null != sendMsgList && !sendMsgList.isEmpty()) {
					data = (byte[]) sendMsgList.removeFirst();
				}
			}
			return data;
		}

		/************************************ �����߳� *****************************************/
		private class RecvThrd extends Thread {
			private DataInputStream RecvChannel = null;

			public RecvThrd(Socket pSocket) throws Exception {
				RecvChannel = new DataInputStream(pSocket.getInputStream());
			}

			public void run() {
				Vector<Object> data = new Vector<Object>();
				int  nRecvLen = 0;
				int  nRcvPos  = 0;
				int  nCursor  = 0;
				byte ctRslt   = 0;
				boolean bContParse = true;
				byte[] cBuff = new byte[Cmd_Sta.CONST_MAX_BUFF_SIZE];

				while (true) {
					try {
						if (null == objSocket || objSocket.isClosed()) {
							ClientClose(m_ClientKey);
							break;
						}
						nRecvLen = RecvChannel.read(cBuff, nRcvPos,
								(Cmd_Sta.CONST_MAX_BUFF_SIZE - nRcvPos));
						if (nRecvLen <= 0) {
							ClientClose(m_ClientKey);
							CommUtil.LOG("closed the socket in TcpSvr Recvs"
									+ m_ClientKey);
							break;
						}
						m_TestSta = 0;
						nRcvPos += nRecvLen;
						nRecvLen = 0;
						nCursor = 0;
						int nLen = 0;
						bContParse = true;

						while (bContParse) {
							nLen = nRcvPos - nCursor;
							if (0 >= nLen) {
								break;
							}
							data.clear();
							data.insertElementAt(new Integer(nLen), 0);
							data.insertElementAt(new Integer(nCursor), 1);

							CommUtil.PRINT("ClientRecv Original:");
							CommUtil.printMsg(cBuff, nLen);
							//
							ctRslt = DeCode(cBuff, data);
							nLen = ((Integer) data.get(0)).intValue();
							switch (ctRslt) {
							case CmdUtil.CODEC_CMD:
								byte[] Resp = ((byte[]) data.get(1));
								if (null != Resp && Resp.length > 0) {
									SetSendMsgList(Resp);
								}

								byte[] transData = (byte[]) data.get(2);
								if (null != transData
										&& transData.length >= Cmd_Sta.CONST_MSGHDRLEN) {
									SetRecvMsgList(transData);
								}
								nCursor += nLen;
								break;
							case CmdUtil.CODEC_RESP:
								nCursor += nLen;
								break;
							case CmdUtil.CODEC_NEED_DATA:
								bContParse = false;
								break;
							case CmdUtil.CODEC_ERR:
								nRcvPos = 0;
								bContParse = false;
								break;
							default:
								break;
							}
						}// bContParse
						if (0 != nRcvPos) {
							System.arraycopy(cBuff, nCursor, cBuff, 0, nRcvPos
									- nCursor);
							nRcvPos -= nCursor;
						}
					} catch (SocketException Ex1) {
						Ex1.printStackTrace();
						ClientClose(m_ClientKey);
						break;
					} catch (Exception Ex) {
						Ex.printStackTrace();
						continue;
					}
				}// while
			}

			private byte DeCode(byte[] pMsg, Vector<Object> vectData) {
				byte RetVal = CmdUtil.CODEC_ERR;
				int nUsed = ((Integer) vectData.get(0)).intValue();// ���е����ݳ���
				int nCursor = ((Integer) vectData.get(1)).intValue();// ��ʲô�ط���ʼ
				try {
					DataInputStream DinStream = new DataInputStream(
							new ByteArrayInputStream(pMsg));
					if (nUsed < (int) CmdUtil.MSGHDRLEN) {
						return CmdUtil.CODEC_NEED_DATA;
					}
					DinStream.skip(nCursor);

					int unMsgLen = CommUtil.converseInt(DinStream.readInt());
					int unMsgCode = CommUtil.converseInt(DinStream.readInt());
					int unStatus = CommUtil.converseInt(DinStream.readInt());
					int unMsgSeq = CommUtil.converseInt(DinStream.readInt());
					int unReserve = CommUtil.converseInt(DinStream.readInt());
					// System.out.println("DeCode:" + new String(pMsg));
					// CommUtil.PRINT("Decode:" +unMsgLen + "seq:" + unMsgSeq);
					// CommUtil.printMsg(pMsg, nUsed);

					if (unMsgLen < CmdUtil.MSGHDRLEN
							|| unMsgLen > CmdUtil.RECV_BUFFER_SIZE) {
						CommUtil.LOG("unMsgLen < CmdUtil.MSGHDRLEN " + unMsgLen);
						return CmdUtil.CODEC_ERR;
					}

					if (nUsed < unMsgLen) {
						return CmdUtil.CODEC_NEED_DATA;
					}

					vectData.insertElementAt(new Integer(unMsgLen), 0);// nUsed
																		// =
																		// unMsgLen;
					if ((unMsgCode & CmdUtil.COMM_RESP) != 0)// ��Ӧ���
					{
						return CmdUtil.CODEC_RESP;
					}

					DinStream.close();

					// CommUtil.printMsg(pMsg, unMsgLen);
					ByteArrayOutputStream boutStream = new ByteArrayOutputStream();
					DataOutputStream doutStream = new DataOutputStream(
							boutStream);
					// ��Ӧ���
					doutStream
							.writeInt(CommUtil.converseInt(CmdUtil.MSGHDRLEN));
					doutStream.writeInt(CommUtil.converseInt(unMsgCode
							| CmdUtil.COMM_RESP));
					doutStream.writeInt(CommUtil.converseInt(unStatus));// Sta
					doutStream.writeInt(CommUtil.converseInt(unMsgSeq));// seq
					doutStream.writeInt(CommUtil.converseInt(unReserve));//
					vectData.insertElementAt(boutStream.toByteArray(), 1);
					boutStream.close();
					doutStream.close();

					vectData.insertElementAt(null, 2);
					switch (unMsgCode) {
					case CmdUtil.COMM_ACTIVE_TEST: // ��Ӧ���
						vectData.insertElementAt(null, 2);
						RetVal = CmdUtil.CODEC_CMD;
						break;
					case CmdUtil.COMM_SUBMMIT:
					case CmdUtil.COMM_DELIVER: {
						ByteArrayOutputStream bout = new ByteArrayOutputStream();
						DataOutputStream dout = new DataOutputStream(bout);
						dout.write(pMsg, 0, unMsgLen);
						vectData.insertElementAt(bout.toByteArray(), 2);
						dout.close();
						bout.close();
						RetVal = Cmd_Sta.CODEC_CMD;
						break;
					}
					default:
						break;
					}
				} catch (Exception exp) {
					exp.printStackTrace();
				}
				return RetVal;
			}
		}

		/************************************ �����߳� *****************************************/
		private class SendThrd extends Thread {
			private DataOutputStream SendChannel = null;

			public SendThrd(Socket pSocket) throws Exception {
				SendChannel = new DataOutputStream(pSocket.getOutputStream());
			}

			public void run() {
				while (true) {
					try {
						if (null == objSocket || objSocket.isClosed()) {
							ClientClose(m_ClientKey);
							break;
						}

						byte[] data = getSendMsgList();
						if (null == data) {
							sleep(10);
							continue;
						}
						if (data.length > 20) {
							// CommUtil.printMsg(data, data.length);
						}
						SendChannel.write(data);
						SendChannel.flush();

					} catch (SocketException Ex) {
						ClientClose(m_ClientKey);
						break;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}// SendThrd
	}// ClientSocket
}// TcpSvrCls
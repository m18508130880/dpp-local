package bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Sheet;
import jxl.Workbook;
import rmi.Rmi;
import util.CommUtil;
import util.CurrStatus;

import com.jspsmart.upload.SmartUpload;

public class AnalogBean
{

	/**
	 * ģ�����ʱ����excel���
	 * 
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 * @param pConfig
	 */
	public void ImportData(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone, ServletConfig pConfig)
	{
		SmartUpload mySmartUpload = new SmartUpload();
		try
		{
			mySmartUpload.initialize(pConfig, request, response);
			mySmartUpload.setAllowedFilesList("xls,xlsx,XLS,XLSX,");
			mySmartUpload.upload();

			this.Sid = mySmartUpload.getRequest().getParameter("Sid");
			CurrStatus currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + this.Sid);
			currStatus.getHtmlData(request, pFromZone);
			String Project_Id = mySmartUpload.getRequest().getParameter("Project_Id");
			if ((mySmartUpload.getFiles().getCount() > 0))
			{
				int count = 0;
				for(int i = 0; i < mySmartUpload.getFiles().getCount(); i ++)
				{
					if(mySmartUpload.getFiles().getFile(i).getFilePathName().trim().length() > 0)
					{
						if (mySmartUpload.getFiles().getFile(i).getSize() / 1024 <= 3072)
						{
							FileSaveRoute = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogData/";
							com.jspsmart.upload.File myFile = mySmartUpload.getFiles().getFile(i);
							File_Name = mySmartUpload.getFiles().getFile(i).getFileName();
							myFile.saveAs(FileSaveRoute + Project_Id + "_" + File_Name);
							count ++;
						}
					}
				}
				currStatus.setResult("�ĵ��ϴ��ɹ�["+count+"/"+mySmartUpload.getFiles().getCount()+"]����");
				System.out.println("�ĵ��ϴ��ɹ�["+count+"/"+mySmartUpload.getFiles().getCount()+"]����");
			}
			currStatus.setJsp("AnalogDataM.jsp?Sid=" + Sid + "&Project_Id=" + Project_Id + "&AnalogType=" + File_Name.substring(0, 2));
			request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
			response.sendRedirect(currStatus.getJsp());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * ɾ��ģ�����ݱ��
	 */
	public boolean DeleteData(String fileName)
	{
		String filePath = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogData/";
		File file = new File(filePath + fileName + ".xls");
		// ����ļ�·������Ӧ���ļ����ڣ�������һ���ļ�����ֱ��ɾ��
		if (file.exists() && file.isFile())
		{
			if (file.delete())
			{
				System.out.println("ɾ�������ļ�" + fileName + "�ɹ���");
				return true;
			}
			else
			{
				System.out.println("ɾ�������ļ�" + fileName + "ʧ�ܣ�");
				return false;
			}
		}
		else
		{
			System.out.println("ɾ�������ļ�ʧ�ܣ�" + fileName + "�����ڣ�");
			return false;
		}
	}

	/**
	 * ��ˮ ����ܾ�ʱ��ˮλ - ˮλ�۾Q�D
	 * 
	 * @param gjId
	 * @return WaterAccGj
	 */
	public String AnalogWaterAccGj(String gjId, double p1)
	{
		AnalogWaterType = "WaterAccGj";
		return analog_Y5(null, 0, gjId, null, AnalogWaterType, p1);
	}

	/**
	 * ��ˮ ����ʱ��ˮλ��� - ˮλ����D
	 * 
	 * @param subSys
	 * @param timePeriod
	 * @return WaterLev
	 */
	public String AnalogWaterLev(String subSys, int timePeriod, double p1)
	{
		AnalogWaterType = "WaterLev";
		return analog_Y5(subSys, timePeriod, null, null, AnalogWaterType, p1);
	}

	/**
	 * ��ˮ ����ʱ�λ�ˮ�� - ģ�M�؈D�cλ�eˮ��
	 * 
	 * @param fileName
	 * @param timePeriod
	 * @return WaterAcc
	 */
	public String AnalogWaterAcc(String subSys, double p1)
	{
		AnalogWaterType = "WaterAcc";
		return analog_Y5(subSys, 0, null, null, AnalogWaterType, p1);
	}

	/**
	 * ��ˮ ����ʱ���������� - ����ͼ
	 * 
	 * @param subSys
	 * @param p1
	 * @return
	 */
	public String AnalogFlowLoad(String gjId, String gxId, double p1)
	{
		AnalogWaterType = "WaterFlowLoad";
		return analog_Y5(null, 0, gjId, gxId, AnalogWaterType, p1);
	}

	/**
	 * ��ˮ ����ʱ��ʵ������ - ����ͼ
	 * 
	 * @param subSys
	 * @param p1
	 * @return
	 */
	public String AnalogActualFlow(String gjId, String gxId, double p1)
	{
		AnalogWaterType = "WaterActualFlow";
		return analog_Y5(null, 0, gjId, gxId, AnalogWaterType, p1);
	}

	/**
	 * ��ˮ ����ʱ������ - ����ͼ
	 * 
	 * @param subSys
	 * @param p1
	 * @return
	 */
	public String AnalogFlowRate(String gjId, String gxId, double p1)
	{
		AnalogWaterType = "WaterFlowRate";
		return analog_Y5(null, 0, gjId, gxId, AnalogWaterType, p1);
	}

	/**
	 * ��ˮ ����ܾ�ʱ��ˮλ - ˮλ�۾Q�D
	 * 
	 * @param gjId
	 * @return WaterAccGj
	 */
	public String AnalogSewageAccGj(String gjId, double p1)
	{
		AnalogWaterType = "SewageAccGj";
		return analog_W3(null, 0, gjId, null, AnalogWaterType, p1);
	}

	/**
	 * ��ˮ ����ʱ��ˮλ��� - ˮλ����D
	 * 
	 * @param subSys
	 * @param timePeriod
	 * @return WaterLev
	 */
	public String AnalogSewageLev(String subSys, int timePeriod, double p1)
	{
		AnalogWaterType = "SewageLev";
		return analog_W3(subSys, timePeriod, null, null, AnalogWaterType, p1);
	}

	/**
	 * ��ˮ ����ʱ�λ�ˮ�� - ģ������ˮ��
	 * 
	 * @param fileName
	 * @param timePeriod
	 * @return WaterAcc
	 */
	public String AnalogSewageAcc(String subSys, double p1)
	{
		AnalogWaterType = "SewageAcc";
		return analog_W3(subSys, 0, null, null, AnalogWaterType, p1);
	}

	/**
	 * ��ˮ ����ʱ���������� - ����ͼ
	 * 
	 * @param subSys
	 * @param p1
	 * @return
	 */
	public String SewageFlowLoad(String gjId, String gxId, double p1)
	{
		AnalogWaterType = "SewageFlowLoad";
		return analog_W3(null, 0, gjId, gxId, AnalogWaterType, p1);
	}

	/**
	 * ��ˮ ����ʱ��ʵ������ - ����ͼ
	 * 
	 * @param subSys
	 * @param p1
	 * @return
	 */
	public String SewageActualFlow(String gjId, String gxId, double p1)
	{
		AnalogWaterType = "SewageActualFlow";
		return analog_W3(null, 0, gjId, gxId, AnalogWaterType, p1);
	}

	/**
	 * ��ˮ ����ʱ������ - ����ͼ
	 * 
	 * @param subSys
	 * @param p1
	 * @return
	 */
	public String SewageFlowRate(String gjId, String gxId, double p1)
	{
		AnalogWaterType = "SewageFlowRate";
		return analog_W3(null, 0, gjId, gxId, AnalogWaterType, p1);
	}

	// ��һ�װ汾
	private String analog_Y1(String subSys, int timePeriod, String gjId, String AnalogWaterType)
	{
		WaterAcc = new String[60];
		WaterLev = new String[60];
		int SubgjId = 0;
		if (gjId != null)
		{
			SubgjId = CommUtil.StrToInt(gjId.substring(12, 15)) - 1;
		}
		try
		{
			// �����������ݣ�
			// �ܶ������ڵ������ܵ��������·�����ܶ����������������ģ��ʱ������֥�Ӹ���ʱ��λ��
			// �ܵ�·������·�����ڵ������յ�ڵ�ţ��м�������ļ�ָ��
			int NP = 9, NN = 10, Nstart = 3, Npline = 7, NT = 60, NR = 23, Nroute = 3, Nr_node = 8, Nend = 7, Iprt = 0;
			// ���깫ʽ����shanghai storm water formular:
			// (A1+C*lgP)/(t+b)**n---ln(N)=2.303log(N)---����ˮλ��m��
			// �ܶ����٣�m/s��, �ܶ��趨����vp0�����氼͹ϵ��csf
			double A1 = 17.53, C_storm = 0.95, tmin = 10, b_storm = 11.77, P_simu = 50, n_storm = 0.88, dt = 2.0, rc = 0.375, Hw_end = 4.1, vp0 = 0.8, csf = 3.0;

			// ��ϵͳ�ܶ����ݣ�
			int[] I0; // �ܶ����νڵ��I0,
			int[] J0; // ���νڵ��J0,
			double[] lp; // �ܶγ���
			double[] dpl; // �ܶ�ֱ��(m)
			double[] slp; // Ħ|��ϵ��
			double[] ZJup; // ���ιܵ׸߳�(m)
			double[] ZJdw; // ���ιܵ׸߳�(m)

			// ��ϵͳ�ڵ�����
			// ������ʼ�ڵ�ź���ʼ�ڵ�ܵ�����<m>
			double[] Aj; // �ڵ��ˮ���(ha)3.5
			double[] Acoef; // �ڵ��ˮ�������ϵ��0.6
			double[] Hj; // �ڵ�����ߣ�m��[NN=23]

			// ����·������·���ڵ��(-99��ʾ�սڵ�)
			int[][] Mroute;

			// ��ϵͳ��֧·���ܶ����ݾ��� ����pipe branches-reverse order
			int[][] Mbranch;

			this.FileSaveRoute = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogData/";
			String XlsPath = "";
			if (gjId != null)
			{
				XlsPath = FileSaveRoute + gjId.substring(0, 12) + ".xls";
				gjName = gjId.substring(0, 12);
			}
			else
			{
				XlsPath = FileSaveRoute + subSys + ".xls";
				gjName = subSys;
			}
			InputStream is = new FileInputStream(XlsPath);
			Workbook rwb = Workbook.getWorkbook(is);
			Sheet rs = rwb.getSheet(0);
			int rsRows = rs.getRows();

			/*
			 * �������ݱ����ϵͳ�� �ڵ���NN �ܶ���NP �����NStart ·���ܶ���Npline ·���ڵ���Nr_node
			 * �յ���ں�Nend ģ��ʱ��NT �ܶ�·����NrouteYJ002 10 9 3 7 8 8 60 3
			 */
			int rowCnt = 2;
			String sysName = rs.getCell(0, rowCnt).getContents().trim();
			NN = Integer.parseInt(rs.getCell(1, rowCnt).getContents().trim());
			NP = Integer.parseInt(rs.getCell(2, rowCnt).getContents().trim());
			Nstart = Integer.parseInt(rs.getCell(3, rowCnt).getContents().trim());
			Npline = Integer.parseInt(rs.getCell(4, rowCnt).getContents().trim());
			Nr_node = Integer.parseInt(rs.getCell(5, rowCnt).getContents().trim());
			Nend = Integer.parseInt(rs.getCell(6, rowCnt).getContents().trim());
			NT = Integer.parseInt(rs.getCell(7, rowCnt).getContents().trim());
			Nroute = Integer.parseInt(rs.getCell(8, rowCnt).getContents().trim());
			rowCnt += 4;

			/*
			 * ��ϵͳ�ܶ����ݱ�� Pipe.No ����I0 �յ��J0 ����LP ֱ��DP Ħ��ϵ�� ��˱�� �ն˱�� 1 0 1 28.5
			 * 0.3 0.017 3.894 3.842 2 1 2 32 0.3 0.017 3.842 3.784 3 2 3 28.6
			 * 0.3 0.017 3.784 3.733 4 3 4 25.4 0.3 0.017 3.733 3.687 5 4 5 24.7
			 * 0.3 0.017 3.687 3.643 6 5 6 23.5 0.3 0.017 3.643 3.601 7 6 7 30.4
			 * 0.3 0.017 3.601 3.546 8 8 7 15.5 0.3 0.017 3.731 3.171 9 9 6 4.3
			 * 0.3 0.017 3.886 3.7
			 */
			I0 = new int[NP];
			J0 = new int[NP];
			lp = new double[NP];
			dpl = new double[NP];
			slp = new double[NP];
			ZJup = new double[NP];
			ZJdw = new double[NP];
			for (int j = 0; j < NP; j++)
			{
				I0[j] = Integer.parseInt(rs.getCell(1, rowCnt + j).getContents().trim());
				J0[j] = Integer.parseInt(rs.getCell(2, rowCnt + j).getContents().trim());
				lp[j] = Double.parseDouble(rs.getCell(3, rowCnt + j).getContents().trim());
				dpl[j] = Double.parseDouble(rs.getCell(4, rowCnt + j).getContents().trim());
				slp[j] = Double.parseDouble(rs.getCell(5, rowCnt + j).getContents().trim());
				ZJup[j] = Double.parseDouble(rs.getCell(6, rowCnt + j).getContents().trim());
				ZJdw[j] = Double.parseDouble(rs.getCell(7, rowCnt + j).getContents().trim());
			}
			rowCnt += NP;
			rowCnt += 3;

			/*
			 * ��ϵͳ�ڵ����ݱ��ڵ�No ��ˮ���ha ����ϵ�� ������ ���ױ�� 1 3.5 0.6 5.244 ��δ�õ� 2 3.5
			 * 0.6 5.191 3 3.5 0.6 5.177 4 3.5 0.6 5.208 5 3.5 0.6 5.221 6 3.5
			 * 0.6 5.201 7 3.5 0.6 5.2 8 3.5 0.6 5.121 9 3.5 0.6 5.131 10 3.5
			 * 0.6 5.186
			 */
			Aj = new double[NN];
			Acoef = new double[NN];
			Hj = new double[NN];
			for (int j = 0; j < NN; j++)
			{
				Aj[j] = Double.parseDouble(rs.getCell(1, rowCnt + j).getContents().trim());
				Acoef[j] = Double.parseDouble(rs.getCell(2, rowCnt + j).getContents().trim());
				Hj[j] = Double.parseDouble(rs.getCell(3, rowCnt + j).getContents().trim());
			}
			rowCnt += NN;
			rowCnt += 3;

			/*
			 * ����·����&·���ڵ�Žڵ���� 1 2 3 4 5 6 7 8 1 0 1 2 3 4 5 6 7 2 8 7 -99 -99
			 * -99 -99 -99 -99 3 9 6 -99 -99 -99 -99 -99 -99
			 */
			Mroute = new int[Nstart][Nr_node];
			for (int j = 0; j < Nstart; j++)
			{
				for (int k = 0; k < Nr_node; k++)
				{
					Mroute[j][k] = Integer.parseInt(rs.getCell(k + 1, rowCnt + j).getContents().trim());
				}
			}
			rowCnt += Nstart;
			rowCnt += 3;

			/*
			 * ��ϵͳ��֧·���ܶ����ݾ��� ����pipe branches-reverse order �ڵ���� 1 2 3 4 5 6 7 1
			 * 6 5 4 3 2 1 0 2 7 -99 -99 -99 -99 -99 -99 3 8 -99 -99 -99 -99 -99
			 * -99
			 */
			Mbranch = new int[Nstart][Npline];
			for (int j = 0; j < Nstart; j++)
			{
				for (int k = 0; k < Npline; k++)
				{
					Mbranch[j][k] = Integer.parseInt(rs.getCell(k + 1, rowCnt + j).getContents().trim());
				}
			}
			// ----�ٽ�ˮ��������----
			double sita0 = 3.0, eps = 0.001, alfa = 0.5;
			double Ad0, qkpmax, Hwdwkp, yykp, sita, cons_b, sita_s = 0, sita_c, fsita, dfdsita, dfsita, ssita = 0, csita = 0, hyd_A, hafsita, shafsita = 0, chafsita, sita_p = 0;
			// �м����
			int i, j, k = 0, ik, jk, it, k1, kp, in1, in2, in3;
			double dtnt, taa, tbb, AA, XX1, XX2, hdj0;
			double[] XX = new double[NT];
			double[] qit = new double[NT];
			double[][] sumqj = new double[NT][NN];
			double[][] sumAj = new double[NT][NN];
			double[][] Tnode = new double[NN][NN];
			double[][] sumTnode = new double[NN][NN];
			double[] vp = new double[NP];
			double[] slop = new double[NP];
			double[][] qpt = new double[NT][NP];
			double[][] qqkp = new double[NT][NP];
			double[][] vpt = new double[NT][NP];
			double[][] rid = new double[NT][NP];
			double[][] slopt = new double[NT][NP];
			double[][] Hwup = new double[NT][NP];
			double[][] Hwdw = new double[NT][NP];
			double[][] hdcc0 = new double[NT][NP];
			double[][] overflow = new double[NT][NN];
			double[][] Hw_over = new double[NT][NN];
			double[][] Hwj = new double[NT][NN];

			DecimalFormat df = new DecimalFormat("##.####");
			DecimalFormat df1 = new DecimalFormat("######.##");
			// ================= ����ֵ ===============================
			for (i = 0; i < NT; i++)
			{
				for (j = 0; j < NN; j++)
					sumAj[i][j] = 0;
			}
			for (i = 0; i < NT; i++)
			{
				for (j = 0; j < NN; j++)
					sumqj[i][j] = 0;
			}
			for (i = 0; i < NN; i++)
			{
				for (j = 0; j < NN; j++)
				{
					if (i == j)
					{
						Tnode[i][j] = 0;
					}
					else
					{
						Tnode[i][j] = -99;
					}
				}
			}
			for (i = 0; i < NN; i++)
			{
				for (j = 0; j < NN; j++)
					sumTnode[i][j] = 0;
			}
			for (i = 0; i < NP; i++)
				vp[i] = vp0;
			for (kp = 0; kp < NP; kp++)
			{
				in1 = I0[kp];
				in2 = J0[kp];
				Tnode[in1][in2] = lp[kp] / vp[kp] / 60;
				slop[kp] = (ZJup[kp] - ZJdw[kp]) / lp[kp];
			}
			//
			for (i = 0; i < Nroute; i++)
			{
				for (j = 0; j < Nr_node; j++)
				{
					in1 = Mroute[i][j];
					if (in1 >= 0)
					{
						for (k = j + 1; k < Nr_node; k++)
						{
							in2 = Mroute[i][k - 1];
							in3 = Mroute[i][k];
							if (in3 >= 0)
							{
								sumTnode[in1][in3] = sumTnode[in1][in2] + Tnode[in2][in3];
							}
						}
					}
				}
			}
			// ----------------�ڵ��ˮ���(ha)�ͻ�ˮ����(m3/sec)����--------//
			// ֥�Ӹ������--rainfall intensity at every time step--
			AA = A1 + A1 * C_storm * Math.log(P_simu) / 2.303;
			for (it = 0; it < NT; it++)
			{
				if (it <= NR)
				{
					dtnt = dt * (float) (it);
					tbb = dt * (float) (NR) - dtnt;
					XX1 = AA * ((1.0 - n_storm) * tbb / rc + b_storm);
					XX2 = Math.pow((tbb / rc + b_storm), (n_storm + 1.0));
				}
				else
				{
					dtnt = dt * (float) (it);
					taa = dtnt - dt * (float) (NR);
					XX1 = AA * ((1.0 - n_storm) * taa / (1.0 - rc) + b_storm);
					XX2 = Math.pow((taa / (1.0 - rc) + b_storm), (n_storm + 1.0));
				}
				XX[it] = XX1 / XX2;
				qit[it] = 167.0 * XX[it] / 1000.0;
			}
			for (it = 0; it < NT; it++)
			{
				dtnt = dt * (float) (it);
			}
			for (it = 0; it < NT; it++)
			{
				dtnt = dt + dt * (float) (it);
				for (j = 0; j < NN; j++)
				{
					sumAj[it][j] = Aj[j];
					sumqj[it][j] = Aj[j] * qit[it] * Acoef[j];
					for (i = 0; i < NN; i++)
					{
						if (sumTnode[i][j] > 0 && sumTnode[i][j] < dtnt)
						{
							sumAj[it][j] = sumAj[it][j] + Aj[i];
							sumqj[it][j] = sumqj[it][j] + Aj[i] * qit[it] * Acoef[i];
						}
					}
				}
			}
			for (it = 0; it < NT; it++)
			{
				for (i = 0; i < NN; i++)
				{
					overflow[it][i] = 0.0;
					Hw_over[it][i] = 0.0;
				}
			}
			for (it = 0; it < NT; it++)
			{
				for (j = 0; j < NP; j++)
				{
					qpt[it][j] = -99.0;
					qqkp[it][j] = 0.0;
				}
			}
			for (it = 0; it < NT; it++)
			{
				for (j = 0; j < NN; j++)
				{
					for (k = 0; k < NP; k++)
					{
						if (I0[k] == j)
						{
							qpt[it][k] = sumqj[it][j];
						}
					}
				}
				for (ik = 0; ik < Nstart; ik++)
				{
					for (jk = 0; jk < Npline; jk++)
					{
						kp = Mbranch[ik][jk];
						if (kp >= 0)
						{
							if (J0[kp] == Nend)
							{
								Hwdw[it][kp] = Hw_end;
							}
							else
							{
								for (k1 = 0; k1 < NP; k1++)
								{
									if (I0[k1] == J0[kp]) Hwdw[it][kp] = Hwup[it][k1];
								}
							}
							Ad0 = 0.7854 * Math.pow(dpl[kp], 2.0);
							hdj0 = ZJdw[kp] + dpl[kp];
							if (Hwdw[it][kp] >= hdj0)
							{
								hdcc0[it][kp] = 1.0;
								rid[it][kp] = dpl[kp] / 4.0;
								vpt[it][kp] = qpt[it][kp] / Ad0;
								slopt[it][kp] = 10.29 * Math.pow(slp[kp], 2.0) * Math.pow(qpt[it][kp], 2.0) / Math.pow(dpl[kp], 5.333);
								Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
								if (Hwup[it][kp] >= Hj[I0[kp]])
								{
									Hwup[it][kp] = Hj[I0[kp]];
									slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
									if (slopt[it][kp] < 0.0)
									{
										slopt[it][kp] = Math.abs(slopt[it][kp]);
									}
									vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt[it][kp], 0.5) / slp[kp];
									qqkp[it][kp] = vpt[it][kp] * Ad0;
									if (qqkp[it][kp] < 0.0)
									{
										qqkp[it][kp] = Math.abs(qqkp[it][kp]);
									}
								}
							}
							else
							{
								qkpmax = 2.46 * Math.pow(dpl[kp], 2.5);
								if (qpt[it][kp] > qkpmax * 0.95)
								{
									Hwdw[it][kp] = ZJdw[kp] + dpl[kp] * 1.1;
									hdcc0[it][kp] = 1.0;
									rid[it][kp] = dpl[kp] / 4.0;
									vpt[it][kp] = qpt[it][kp] / Ad0;
									slopt[it][kp] = 10.29 * Math.pow(slp[kp], 2.0) * Math.pow(qpt[it][kp], 2.0) / Math.pow(dpl[kp], 5.333);
									Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
									if (Hwup[it][kp] >= Hj[I0[kp]])
									{
										Hwup[it][kp] = Hj[I0[kp]];
										slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
										if (slopt[it][kp] < 0.0)
										{
											slopt[it][kp] = Math.abs(slopt[it][kp]);
										}
										vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt[it][kp], 0.5) / slp[kp];
										qqkp[it][kp] = vpt[it][kp] * Ad0;
										if (qqkp[it][kp] < 0.0)
										{
											qqkp[it][kp] = Math.abs(qqkp[it][kp]);
										}
									}
								}
								else
								{
									i = 0;
									sita = sita0;
									cons_b = 0.276843 * Math.pow(dpl[kp], 2.5) / qpt[it][kp];
									while (true)
									{
										ssita = Math.sin(sita);
										csita = Math.cos(sita);
										hafsita = sita / 2.0;
										shafsita = Math.sin(hafsita);
										chafsita = Math.cos(hafsita);
										sita_s = sita - Math.sin(sita);
										sita_c = 1 - Math.cos(sita);
										sita_p = Math.pow((1.0 - chafsita), -0.5);
										fsita = cons_b * sita_s - sita_p;
										dfsita = Math.abs(fsita);
										if (dfsita < eps)
										{
											hdcc0[it][kp] = (1 - Math.cos(sita / 2)) / 2;
											rid[it][kp] = 0.25 * dpl[kp] * (sita - Math.sin(sita)) / sita;
											vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slop[kp], 0.5) / slp[kp];
											break;
										}
										else
										{
											dfdsita = cons_b * (1.0 - csita) + 0.25 * Math.pow(sita_p, -1.0) * shafsita;
											sita = sita - alfa * fsita / dfdsita;
											i = i + 1;
										}
									}
								}
								Hwdwkp = ZJdw[kp] + hdcc0[it][kp] * dpl[kp];
								if (Hwdwkp >= Hwdw[it][kp])
								{
									Hwdw[it][kp] = Hwdwkp;
								}
								if (Hwdwkp < Hwdw[it][kp])
								{
									yykp = Hwdw[it][kp] - ZJdw[kp];
									if (yykp > dpl[kp])
									{
										yykp = dpl[kp];
									}
									sita = 2.0 * Math.acos(1.0 - 2.0 * yykp / dpl[kp]);
									hdcc0[it][kp] = yykp / dpl[kp];
									rid[it][kp] = 0.25 * dpl[kp] * (sita - Math.sin(sita)) / sita;
									vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slop[kp], 0.5) / slp[kp];
								}
								Hwup[it][kp] = Hwdw[it][kp] + slop[kp] * lp[kp];
							}
						}
					}
				}
				for (i = 0; i < NP; i++)
				{
					k = J0[i];
					if (k == Nend)
					{
						Hwj[it][k] = Hwdw[it][i];
					}
					{
						j = I0[i];
						Hwj[it][j] = Hwup[it][i];
						if (Hwup[it][i] == Hj[j])
						{
							overflow[it][j] = overflow[it][j] + (qpt[it][i] - qqkp[it][i]) * dt * 60.0;
							Hw_over[it][j] = csf * overflow[it][j] / Aj[j] / 10000.0 * 1000.0;

						}
						if (Hwup[it][i] < Hj[j] && overflow[it][j] > 0.0)
						{
							overflow[it][j] = overflow[it - 1][j] * 0.90;
							Hw_over[it][j] = csf * overflow[it][j] / Aj[j] / 10000.0 * 1000.0;
						}
					}
					if (it > NR && Hw_over[it][j] <= 5.0)
					{
						overflow[it][j] = 0.0;
						Hw_over[it][j] = 0.0;
					}
				}
			}
			// ʱ�ιܾ�ˮλ����ͼ�͹ܾ�ˮλʱ������ͼ�����֯
			for (it = 0; it < NT; it++)
			{
				String WaterLevNew = "";
				for (i = 0; i < NN; i++)
				{
					if (gjId != null && i == SubgjId)
					{
						WaterAccGj += df1.format(Hwj[it][i]) + "|";
					}
					WaterLevNew += df1.format(Hwj[it][i]) + "|";
				}
				WaterLev[it] = WaterLevNew;
			}
			// �����ˮ�������֯
			for (it = 0; it < NT; it++)
			{
				String WaterAccNew = "";
				for (i = 0; i < NN; i++)
				{
					if (overflow[it][i] <= 0.0)
					{
						WaterAccNew += 0 + "|";
					}
					else
					{
						WaterAccNew += df1.format(overflow[it][i]) + "|";
					}
				}
				WaterAcc[it] = WaterAccNew;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return gjName + "," + Count;
		}
		if (AnalogWaterType.equals("WaterAccGj"))
		{
			return WaterAccGj;
		}
		else if (AnalogWaterType.equals("WaterAcc"))
		{
			String WaterAccList = "";
			for (int i = 0; i < WaterAcc.length; i++)
			{
				WaterAccList += subSys.substring(7, 12) + WaterAcc[i] + ";";
			}
			return WaterAccList;
		}
		else if (AnalogWaterType.equals("WaterLev"))
		{
			String WaterLevList = "";
			for (int i = 0; i < WaterLev.length; i++)
			{
				WaterLevList += subSys.substring(7, 12) + WaterLev[i] + ";";
			}
			return WaterLevList;
			// return WaterLev[timePeriod];
		}
		return "";
	}

	// �ڶ��װ汾
	private String analog_Y2(String subSys, int timePeriod, String gjId, String AnalogWaterType)
	{
		WaterAcc = new String[60];
		WaterLev = new String[60];
		int SubgjId = 0;
		if (gjId != null)
		{
			SubgjId = CommUtil.StrToInt(gjId.substring(12, 15)) - 1;
		}
		try
		{
			// �����������ݣ�
			// �ܶ������ڵ������ܵ��������·�����ܶ����������������ģ��ʱ������֥�Ӹ���ʱ��λ��
			// �ܵ�·������·�����ڵ������յ�ڵ�ţ��м�������ļ�ָ��
			int NP = 9, NN = 10, Nstart = 3, Npline = 7, NT = 60, NR = 23, Nroute = 3, Nr_node = 8, Nend = 7, Iprt = 0, Nprtc = 20;
			// ���깫ʽ����shanghai storm water formular:
			// (A1+C*lgP)/(t+b)**n---ln(N)=2.303log(N)---����ˮλ��m��
			// �ܶ����٣�m/s��, �ܶ��趨����vp0�����氼͹ϵ��csf
			double A1 = 17.53, C_storm = 0.95, tmin = 10, b_storm = 11.77, P_simu = 100, n_storm = 0.88, dt = 2.0, rc = 0.375, Hw_end = 4.1, vp0 = 0.8, csf = 3.0;

			// ��ϵͳ�ܶ����ݣ�
			int[] I0; // �ܶ����νڵ��I0,
			int[] J0; // ���νڵ��J0,
			double[] lp; // �ܶγ���
			double[] dpl; // �ܶ�ֱ��(m)
			double[] slp; // Ħ|��ϵ��
			double[] ZJup; // ���ιܵ׸߳�(m)
			double[] ZJdw; // ���ιܵ׸߳�(m)

			// ��ϵͳ�ڵ�����
			// ������ʼ�ڵ�ź���ʼ�ڵ�ܵ�����<m>
			double[] Aj; // �ڵ��ˮ���(ha)3.5
			double[] Acoef; // �ڵ��ˮ�������ϵ��0.6
			double[] Hj; // �ڵ�����ߣ�m��[NN=23]

			// ����·������·���ڵ��(-99��ʾ�սڵ�)
			int[][] Mroute;

			// ��ϵͳ��֧·���ܶ����ݾ��� ����pipe branches-reverse order
			int[][] Mbranch;

			this.FileSaveRoute = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogData/";
			String XlsPath = "";
			if (gjId != null)
			{
				XlsPath = FileSaveRoute + gjId.substring(0, 12) + ".xls";
				gjName = gjId.substring(0, 12);
			}
			else
			{
				XlsPath = FileSaveRoute + subSys + ".xls";
				gjName = subSys;
			}
			InputStream is = new FileInputStream(XlsPath);
			Workbook rwb = Workbook.getWorkbook(is);
			Sheet rs = rwb.getSheet(0);
			int rsRows = rs.getRows();

			/*
			 * �������ݱ����ϵͳ�� �ڵ���NN �ܶ���NP �����NStart ·���ܶ���Npline ·���ڵ���Nr_node
			 * �յ���ں�Nend ģ��ʱ��NT �ܶ�·����NrouteYJ002 10 9 3 7 8 8 60 3
			 */
			int rowCnt = 2;
			String sysName = rs.getCell(0, rowCnt).getContents().trim();
			NN = Integer.parseInt(rs.getCell(1, rowCnt).getContents().trim());
			NP = Integer.parseInt(rs.getCell(2, rowCnt).getContents().trim());
			Nstart = Integer.parseInt(rs.getCell(3, rowCnt).getContents().trim());
			Npline = Integer.parseInt(rs.getCell(4, rowCnt).getContents().trim());
			Nr_node = Integer.parseInt(rs.getCell(5, rowCnt).getContents().trim());
			Nend = Integer.parseInt(rs.getCell(6, rowCnt).getContents().trim());
			NT = Integer.parseInt(rs.getCell(7, rowCnt).getContents().trim());
			Nroute = Integer.parseInt(rs.getCell(8, rowCnt).getContents().trim());
			rowCnt += 4;

			/*
			 * ��ϵͳ�ܶ����ݱ�� Pipe.No ����I0 �յ��J0 ����LP ֱ��DP Ħ��ϵ�� ��˱�� �ն˱�� 1 0 1 28.5
			 * 0.3 0.017 3.894 3.842 2 1 2 32 0.3 0.017 3.842 3.784 3 2 3 28.6
			 * 0.3 0.017 3.784 3.733 4 3 4 25.4 0.3 0.017 3.733 3.687 5 4 5 24.7
			 * 0.3 0.017 3.687 3.643 6 5 6 23.5 0.3 0.017 3.643 3.601 7 6 7 30.4
			 * 0.3 0.017 3.601 3.546 8 8 7 15.5 0.3 0.017 3.731 3.171 9 9 6 4.3
			 * 0.3 0.017 3.886 3.7
			 */
			I0 = new int[NP];
			J0 = new int[NP];
			lp = new double[NP];
			dpl = new double[NP];
			slp = new double[NP];
			ZJup = new double[NP];
			ZJdw = new double[NP];
			for (int j = 0; j < NP; j++)
			{
				I0[j] = Integer.parseInt(rs.getCell(1, rowCnt + j).getContents().trim());
				J0[j] = Integer.parseInt(rs.getCell(2, rowCnt + j).getContents().trim());
				lp[j] = Double.parseDouble(rs.getCell(3, rowCnt + j).getContents().trim());
				dpl[j] = Double.parseDouble(rs.getCell(4, rowCnt + j).getContents().trim());
				slp[j] = Double.parseDouble(rs.getCell(5, rowCnt + j).getContents().trim());
				ZJup[j] = Double.parseDouble(rs.getCell(6, rowCnt + j).getContents().trim());
				ZJdw[j] = Double.parseDouble(rs.getCell(7, rowCnt + j).getContents().trim());
			}
			rowCnt += NP;
			rowCnt += 3;

			/*
			 * ��ϵͳ�ڵ����ݱ��ڵ�No ��ˮ���ha ����ϵ�� ������ ���ױ�� 1 3.5 0.6 5.244 ��δ�õ� 2 3.5
			 * 0.6 5.191 3 3.5 0.6 5.177 4 3.5 0.6 5.208 5 3.5 0.6 5.221 6 3.5
			 * 0.6 5.201 7 3.5 0.6 5.2 8 3.5 0.6 5.121 9 3.5 0.6 5.131 10 3.5
			 * 0.6 5.186
			 */
			Aj = new double[NN];
			Acoef = new double[NN];
			Hj = new double[NN];
			for (int j = 0; j < NN; j++)
			{
				Aj[j] = Double.parseDouble(rs.getCell(1, rowCnt + j).getContents().trim());
				Acoef[j] = Double.parseDouble(rs.getCell(2, rowCnt + j).getContents().trim());
				Hj[j] = Double.parseDouble(rs.getCell(3, rowCnt + j).getContents().trim());
			}
			rowCnt += NN;
			rowCnt += 3;

			/*
			 * ����·����&·���ڵ�Žڵ���� 1 2 3 4 5 6 7 8 1 0 1 2 3 4 5 6 7 2 8 7 -99 -99
			 * -99 -99 -99 -99 3 9 6 -99 -99 -99 -99 -99 -99
			 */
			Mroute = new int[Nstart][Nr_node];
			for (int j = 0; j < Nstart; j++)
			{
				for (int k = 0; k < Nr_node; k++)
				{
					Mroute[j][k] = Integer.parseInt(rs.getCell(k + 1, rowCnt + j).getContents().trim());
				}
			}
			rowCnt += Nstart;
			rowCnt += 3;

			/*
			 * ��ϵͳ��֧·���ܶ����ݾ��� ����pipe branches-reverse order �ڵ���� 1 2 3 4 5 6 7 1
			 * 6 5 4 3 2 1 0 2 7 -99 -99 -99 -99 -99 -99 3 8 -99 -99 -99 -99 -99
			 * -99
			 */
			Mbranch = new int[Nstart][Npline];
			for (int j = 0; j < Nstart; j++)
			{
				for (int k = 0; k < Npline; k++)
				{
					Mbranch[j][k] = Integer.parseInt(rs.getCell(k + 1, rowCnt + j).getContents().trim());
				}
			}
			// ----�ٽ�ˮ��������----
			double sita0 = 3.0, eps = 0.001, alfa = 0.5;
			double Ad0, qkpmax, Hwdwkp, yykp, sita, cons_b, sita_s = 0, sita_c, fsita, dfdsita, dfsita, ssita = 0, csita = 0, hyd_A, hafsita, shafsita = 0, chafsita, sita_p = 0;
			// �м����
			int i, j, k, ik, jk, it, k1, kp, in1, in2, in3, NR1, NR2, ii, Nprt, iprt1, iprt2;
			double H00, ycd0;
			double dtnt, taa, tbb, AA, XX1, XX2, hdj0;
			double[] XX = new double[NT];
			double[] qit = new double[NT];
			double[][] sumqj = new double[NT][NN];
			double[][] sumAj = new double[NT][NN];
			double[][] Tnode = new double[NN][NN];
			double[][] sumTnode = new double[NN][NN];
			double[] vp = new double[NP];
			double[] slop = new double[NP];
			double[][] qpt = new double[NT][NP];
			double[][] qqkp = new double[NT][NP];
			double[][] vpt = new double[NT][NP];
			double[][] rid = new double[NT][NP];
			double[][] slopt = new double[NT][NP];
			double[][] Hwup = new double[NT][NP];
			double[][] Hwdw = new double[NT][NP];
			double[][] hdcc0 = new double[NT][NP];
			double[][] overflow = new double[NT][NN];
			double[][] Hw_over = new double[NT][NN];
			double[][] Hwj = new double[NT][NN];

			// ----------------------------------------------------------------------------------------------------------
			String FileName = "";
			if (gjId != null)
			{
				FileName = gjId.substring(0, 12) + ".txt";
			}
			else
			{
				FileName = subSys + ".txt";
			}
			String FilePath = "./www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogValue/";
			FileOutputStream fs = new FileOutputStream(new File(FilePath + FileName));
			PrintStream printStream = new PrintStream(fs);
			printStream.println(FileName);

			DecimalFormat df = new DecimalFormat("##.####");
			DecimalFormat df1 = new DecimalFormat("######.##");
			// --��������ļ���ʼ---
			// ================= ����ֵ ===============================
			for (i = 0; i < NT; i++)
			{
				for (j = 0; j < NN; j++)
					sumAj[i][j] = 0;
			}
			for (i = 0; i < NT; i++)
			{
				for (j = 0; j < NN; j++)
					sumqj[i][j] = 0;
			}
			for (i = 0; i < NN; i++)
			{
				for (j = 0; j < NN; j++)
				{
					if (i == j)
					{
						Tnode[i][j] = 0;
					}
					else
					{
						Tnode[i][j] = -99;
					}
				}
			}
			for (i = 0; i < NN; i++)
			{
				for (j = 0; j < NN; j++)
					sumTnode[i][j] = 0;
			}
			// ==================Tnode-sumTnode=========================
			for (i = 0; i < NP; i++)
				vp[i] = vp0;
			for (kp = 0; kp < NP; kp++)
			{
				in1 = I0[kp];
				in2 = J0[kp];
				Tnode[in1][in2] = lp[kp] / vp[kp] / 60;
				slop[kp] = (ZJup[kp] - ZJdw[kp]) / lp[kp];
			}
			for (i = 0; i < Nroute; i++)
			{
				for (j = 0; j < Nr_node; j++)
				{
					in1 = Mroute[i][j];
					if (in1 >= 0)
					{
						for (k = j + 1; k < Nr_node; k++)
						{
							in2 = Mroute[i][k - 1];
							in3 = Mroute[i][k];
							if (in3 >= 0)
							{
								sumTnode[in1][in3] = sumTnode[in1][in2] + Tnode[in2][in3];
							}
						}
					}
				}
			}
			// System.out.println("pipe no.  I0    J0");
			for (i = 0; i < NP; i++)
			{
				// System.out.printf("%6d%6d%6d", i, I0[i], J0[i]);
				// System.out.println();
			}
			printStream.println();
			printStream.print(" ip=");
			for (i = 0; i < NP; i++)
			{
				printStream.printf("%4d", i);
			}
			printStream.println();
			printStream.print(" I0=");
			for (i = 0; i < NP; i++)
			{
				printStream.printf("%4d", I0[i]);
			}
			printStream.println();
			printStream.print(" J0=");
			for (i = 0; i < NP; i++)
			{
				printStream.printf("%4d", J0[i]);
			}
			printStream.println();
			printStream.println();
			printStream.println("===========  print Mroute[i][j]");
			for (i = 0; i < Nroute; i++)
			{
				for (j = 0; j < Nr_node; j++)
				{
					printStream.printf("%6d", Mroute[i][j]);
				}
				printStream.println();
			}
			printStream.println();
			printStream.println("===========  print Mbranch[i][j]");
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Npline; j++)
				{
					printStream.printf("%6d", Mbranch[i][j]);
				}
				printStream.println();
			}
			printStream.println("===========  print Tnode[i][j]");
			printStream.println("====j=  ");
			printStream.println("      ");
			for (j = 0; j < NN; j++)
			{
				printStream.printf("%6d", j);
			}
			printStream.println();
			for (i = 0; i < NN; i++)
			{
				if (i < 10)
				{
					printStream.print("i=" + i + "   ");
				}
				else
				{
					printStream.print("i=" + i + "  ");
				}
				for (j = 0; j < NN; j++)
				{
					if (Tnode[i][j] < 0.0)
					{
						printStream.print("      ");
					}
					else
					{
						printStream.printf("%6.2f", Tnode[i][j]);
					}
				}
				printStream.println();
			}
			printStream.println();
			printStream.println("===========  print sumTnode[i][j]");
			printStream.print("==j=  ");
			for (j = 0; j < NN; j++)
			{
				printStream.printf("%6d", j);
			}
			printStream.println();
			for (i = 0; i < NN; i++)
			{
				printStream.print("i=" + i + "   ");
				for (j = 0; j < NN; j++)
				{
					if (sumTnode[i][j] <= 0.0)
					{
						printStream.print("      ");
					}
					else
					{
						printStream.printf("%6.2f", sumTnode[i][j]);
					}
				}
				printStream.println();
			}
			// ================= ����׼��̬����ģ��============================
			// -------------------��̬ģ����������-----------------------------
			// ----------------�ڵ��ˮ���(ha)�ͻ�ˮ����(m3/sec)����--------
			printStream.println();
			printStream.println("===========  ������̬ģ�����      �����ڣ� " + P_simu + "  ��   ʱ������ " + NT + "       �յ�ˮλ�� " + Hw_end + "  m  =========");
			// ֥�Ӹ������--rainfall intensity at every time step--
			AA = A1 + A1 * C_storm * Math.log(P_simu) / 2.303;
			for (it = 0; it < NT; it++)
			{
				if (it <= NR)
				{
					dtnt = dt * (float) (it);
					tbb = dt * (float) (NR) - dtnt;
					XX1 = AA * ((1.0 - n_storm) * tbb / rc + b_storm);
					XX2 = Math.pow((tbb / rc + b_storm), (n_storm + 1.0));
				}
				else
				{
					dtnt = dt * (float) (it);
					taa = dtnt - dt * (float) (NR);
					XX1 = AA * ((1.0 - n_storm) * taa / (1.0 - rc) + b_storm);
					XX2 = Math.pow((taa / (1.0 - rc) + b_storm), (n_storm + 1.0));
				}
				XX[it] = XX1 / XX2;
				qit[it] = 167.0 * XX[it] / 1000.0;
			}
			NR1 = NR - 1;
			NR2 = NR + 1;
			qit[NR] = (qit[NR] + qit[NR - 1] + qit[NR + 1]) / 3.0;
			printStream.println();
			printStream.println("    it      dtnt      XX[it]     qit[it]");
			for (it = 0; it < NT; it++)
			{
				dtnt = dt * (float) (it);
				printStream.printf("%6d%10.2f%12.6f%12.6f", it, dtnt, XX[it], qit[it]);
				printStream.println();
			}
			printStream.println();
			for (it = 0; it < NT; it++)
			{
				dtnt = dt + dt * (float) (it);
				for (j = 0; j < NN; j++)
				{
					sumAj[it][j] = Aj[j];
					sumqj[it][j] = Aj[j] * qit[it] * Acoef[j];
					for (i = 0; i < NN; i++)
					{
						if (sumTnode[i][j] > 0 && sumTnode[i][j] < dtnt)
						{
							sumAj[it][j] = sumAj[it][j] + Aj[i];
							sumqj[it][j] = sumqj[it][j] + Aj[i] * qit[it] * Acoef[i];
						}
					}
				}
			}
			printStream.println("  sumAj[it][j]=");
			for (it = 0; it < NT; it++)
			{
				for (j = 0; j < NN; j++)
				{
					printStream.printf("%8.2f", sumAj[it][j]);
				}
				printStream.println();
			}
			printStream.println();
			printStream.println("  sumqj[it][j]=");
			for (it = 0; it < NT; it++)
			{
				for (j = 0; j < NN; j++)
				{
					printStream.printf("%8.2f", sumqj[it][j]);
				}
				printStream.println();
			}
			printStream.println();
			// ---------------------------------------------------------------
			for (it = 0; it < NT; it++)
			{
				for (i = 0; i < NN; i++)
				{
					overflow[it][i] = 0.0;
					Hw_over[it][i] = 0.0;
				}
			}
			for (it = 0; it < NT; it++)
			{
				for (j = 0; j < NP; j++)
				{
					qpt[it][j] = -99.0;
					qqkp[it][j] = 0.0;
				}
			}
			// ---------------------------------------------------------------
			for (it = 0; it < NT; it++)
			{
				printStream.print(" it=" + it + "  qpt[it][k]=");
				for (j = 0; j < NN; j++)
				{
					for (k = 0; k < NP; k++)
					{
						if (I0[k] == j)
						{
							qpt[it][k] = sumqj[it][j];
							printStream.printf("%8.2f", qpt[it][k]);
						}
					}
				}
				printStream.println();
				for (ik = 0; ik < Nstart; ik++)
				{
					for (jk = 0; jk < Npline; jk++)
					{
						kp = Mbranch[ik][jk];
						if (kp >= 0)
						{
							if (J0[kp] == Nend)
							{
								Hwdw[it][kp] = Hw_end;
								if (1 == Iprt)
								{
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  Hw_end= " + Hw_end);
								}
							}
							else
							{
								for (k1 = 0; k1 < NP; k1++)
								{
									if (I0[k1] == J0[kp]) Hwdw[it][kp] = Hwup[it][k1];
								}
							}
							Ad0 = 0.7854 * Math.pow(dpl[kp], 2.0);
							hdj0 = ZJdw[kp] + dpl[kp];
							if (Hwdw[it][kp] >= hdj0)
							{
								if (1 == Iprt)
								{
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + df.format(Hwdw[it][kp]) + "  ��û���� ");
								}
								hdcc0[it][kp] = 1.0;
								rid[it][kp] = dpl[kp] / 4.0;
								vpt[it][kp] = qpt[it][kp] / Ad0;
								slopt[it][kp] = 10.29 * Math.pow(slp[kp], 2.0) * Math.pow(qpt[it][kp], 2.0) / Math.pow(dpl[kp], 5.333);
								Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
								if (Hwup[it][kp] >= Hj[I0[kp]])
								{
									Hwup[it][kp] = Hj[I0[kp]];
									slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
									if (slopt[it][kp] < 0.0)
									{
										slopt[it][kp] = Math.abs(slopt[it][kp]);
									}
									vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt[it][kp], 0.5) / slp[kp];
									qqkp[it][kp] = vpt[it][kp] * Ad0;
									if (qqkp[it][kp] < 0.0)
									{
										qqkp[it][kp] = Math.abs(qqkp[it][kp]);
									}
								}
							}
							else
							{
								if (Iprt == 1)
								{
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdw= " + Hwdw[it][kp] + "  ����û���� ");
								}
								// --20161018�޸Ŀ�ʼ---�����ٽ�ˮ����㷨-----------------------
								qkpmax = 2.699 * Math.pow(dpl[kp], 2.5);
								if (qpt[it][kp] > qkpmax)
								{
									if (Iprt == 1)
									{
										printStream.println("   it= " + it + "   kp= " + kp + "  qkpmax= " + qkpmax + "  ����û���ܳ��� ");
									}
									vpt[it][kp] = qpt[it][kp] / Ad0;
									H00 = Math.pow(vpt[it][kp], 2.0) / 13.72;
									Hwdw[it][kp] = ZJdw[kp] + dpl[kp] + H00;
									hdcc0[it][kp] = 1.0;
									rid[it][kp] = dpl[kp] / 4.0;
									slopt[it][kp] = 10.29 * Math.pow(slp[kp], 2.0) * Math.pow(qpt[it][kp], 2.0) / Math.pow(dpl[kp], 5.333);
									Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
									if (Hwup[it][kp] >= Hj[I0[kp]])
									{
										Hwup[it][kp] = Hj[I0[kp]];
										slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
										if (slopt[it][kp] < 0.0)
										{
											slopt[it][kp] = Math.abs(slopt[it][kp]);
										}
										vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt[it][kp], 0.5) / slp[kp];
										qqkp[it][kp] = vpt[it][kp] * Ad0;
										if (qqkp[it][kp] < 0.0)
										{
											qqkp[it][kp] = Math.abs(qqkp[it][kp]);
										}
									}
								}
								else
								{
									if (Iprt == 1)
									{
										printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  ����û�����ܳ��� ");
									}
									// ==20161018�޸Ŀ�ʼ---�����ٽ�ˮ��򻯹�ʽ--------zhou-p21------
									ycd0 = qpt[it][kp] / 2.983 / Math.pow(dpl[kp], 2.5);
									hdcc0[it][kp] = Math.pow(ycd0, 0.513);
									sita = 2.0 * Math.acos(1.0 - 2.0 * hdcc0[it][kp]);
									rid[it][kp] = 0.25 * dpl[kp] * (sita - Math.sin(sita)) / sita;
									vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slop[kp], 0.5) / slp[kp];
								}
								// ---for(k=0;k<N;k++)����---20160907�޸Ľ���---�ٽ�ˮ���㷨--------------
								Hwdwkp = ZJdw[kp] + hdcc0[it][kp] * dpl[kp];
								if (Hwdwkp >= Hwdw[it][kp])
								{
									Hwdw[it][kp] = Hwdwkp;
								}
								else
								{
									yykp = Hwdw[it][kp] - ZJdw[kp];
									if (yykp > dpl[kp])
									{
										yykp = dpl[kp];
									}
									sita = 2.0 * Math.acos(1.0 - 2.0 * yykp / dpl[kp]);
									hdcc0[it][kp] = yykp / dpl[kp];
									rid[it][kp] = 0.25 * dpl[kp] * (sita - Math.sin(sita)) / sita;
									vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slop[kp], 0.5) / slp[kp];
								}
								Hwup[it][kp] = Hwdw[it][kp] + slop[kp] * lp[kp];
							}
							// ------- ���it������ ----------
							if (Iprt == 1)
							{
								printStream.println("   it= " + it + "   kp= " + kp + "   I0[kp]= " + I0[kp] + "  Hwdm= " + Hwdw[it][kp] + "  Hwup= " + Hwup[it][kp] + "  Hj= " + Hj[I0[kp]] + "  hdcc0= " + hdcc0[it][kp] + "  qpt= " + qpt[it][kp] + "  qqkp= " + qqkp[it][kp] + "  vpt= " + vpt[it][kp]);
							}
						}
					}
				}
				printStream.println();

				printStream.println("    it   �ܶκ�  I0   J0 �ܾ�dpl     �ܶ�qp   ˮ���뾶R  ������ ����(m/s)  ����ˮλ  ����ˮλ  �Ϲܵ׸�  �¹ܵ׸�  �ܶ��¶�  �ϵ����");
				for (i = 0; i < NP; i++)
				{
					printStream.printf("%6d%6d%6d%5d%8.2f%12.3f%10.3f%8.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.5f%10.3f", it, i, I0[i], J0[i], dpl[i], qpt[it][i], rid[it][i], hdcc0[it][i], vpt[it][i], Hwup[it][i], Hwdw[it][i], ZJup[i], ZJdw[i], slop[i], Hj[I0[i]]);
					printStream.println();
				}
				printStream.println();
				// -------------- ��ʼ���������ڵ� ---------------
				for (i = 0; i < NP; i++)
				{
					k = J0[i];
					if (k == Nend)
					{
						Hwj[it][k] = Hwdw[it][i];
					}
					{
						j = I0[i];
						Hwj[it][j] = Hwup[it][i];
						if (Hwup[it][i] == Hj[j])
						{
							overflow[it][j] = overflow[it][j] + (qpt[it][i] - qqkp[it][i]) * dt * 60.0;
							Hw_over[it][j] = csf * overflow[it][j] / Aj[j] / 10000.0 * 1000.0;

						}
						if (Hwup[it][i] < Hj[j] && overflow[it][j] > 0.0)
						{
							overflow[it][j] = overflow[it - 1][j] * 0.90;
							Hw_over[it][j] = csf * overflow[it][j] / Aj[j] / 10000.0 * 1000.0;
						}
					}
					if (it > NR && Hw_over[it][j] <= 5.0)
					{
						overflow[it][j] = 0.0;
						Hw_over[it][j] = 0.0;
					}
				}
				// ------------------ ���������ڵ���� ---------------
			}
			// ----------------��Ļ����������------
			// System.out.println("------ ģ�ͼ���ȫ����� ------");
			// ---------------------- ����ܶγ����ȼ����� ---------------
			printStream.println(" ======== ʱ�ιܶγ����� ========");
			Nprt = NP / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NP)
					{
						iprt2 = NP;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println();
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						printStream.printf("%8.3f", hdcc0[it][i]);
					}
					printStream.println();
				}
			}
			//
			// ------------------- ����ڵ�ˮλ������ ---------------
			printStream.println(" ======== ʱ�νڵ�ˮλ ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NN)
					{
						iprt2 = NN;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "  ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
				}
			}

			// ***********��֯���ݣ�����ҳ��������ʾ********
			for (it = 0; it < NT; it++)
			{
				String WaterLevNew = "";
				for (i = 0; i < NN; i++)
				{
					if (gjId != null && i == SubgjId)
					{
						WaterAccGj += df1.format(Hwj[it][i]) + "|";
					}
					WaterLevNew += df1.format(Hwj[it][i]) + "|";
				}
				WaterLev[it] = WaterLevNew;
			}
			// *************************************
			// ------------------ ����ڵ����������� ---------------
			printStream.println(" ======== ʱ�νڵ��ˮ��(m3) ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NN)
					{
						iprt2 = NN;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "  ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.println(" " + it + "   ");
					}
					else
					{
						printStream.println(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						if (overflow[it][i] <= 0.0)
						{
							printStream.print("        ");
						}
						else
						{
							printStream.printf("%8.2f", overflow[it][i]);
						}
					}
					printStream.println();
					String WaterAccNew = "";
					for (i = 0; i < NN; i++)
					{
						if (overflow[it][i] <= 0.0)
						{
							WaterAccNew += 0 + "|";
						}
						else
						{
							WaterAccNew += df1.format(overflow[it][i]) + "|";
						}
					}
					WaterAcc[it] = WaterAccNew;
				}
			}

			// ***********��֯���ݣ�����ҳ��������ʾ********
			for (it = 0; it < NT; it++)
			{
				String WaterAccNew = "";
				for (i = 0; i < NN; i++)
				{
					if (overflow[it][i] <= 0.0)
					{
						WaterAccNew += 0 + "|";
					}
					else
					{
						WaterAccNew += df1.format(overflow[it][i]) + "|";
					}
				}
				WaterAcc[it] = WaterAccNew;
			}
			// *********************************************
			printStream.println(" ======== ʱ�νڵ��ˮ���(mm) ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NN)
					{
						iprt2 = NN;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + i + "   ");
					}
					else
					{
						printStream.print(i + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						if (overflow[it][i] <= 0.0)
						{
							printStream.print("        ");
						}
						else
						{
							printStream.printf("%8.2f", Hw_over[it][i]);
						}
					}
					printStream.println();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return gjName + "," + Count;
		}
		if (AnalogWaterType.equals("WaterAccGj"))
		{
			return WaterAccGj;
		}
		else if (AnalogWaterType.equals("WaterAcc"))
		{
			String WaterAccList = "";
			for (int i = 0; i < WaterAcc.length; i++)
			{
				WaterAccList += subSys.substring(7, 12) + WaterAcc[i] + ";";
			}
			return WaterAccList;
		}
		else if (AnalogWaterType.equals("WaterLev"))
		{
			String WaterLevList = "";
			for (int i = 0; i < WaterLev.length; i++)
			{
				WaterLevList += subSys.substring(7, 12) + WaterLev[i] + ";";
			}
			return WaterLevList;
		}
		return "";
	}

	// �����װ汾
	// �ر�˵������һ�汾��ǰ�����汾���õı��һ��
	private String analog_Y3(String subSys, int timePeriod, String gjId, String AnalogWaterType)
	{
		WaterAcc = new String[60];
		WaterLev = new String[60];
		int SubgjId = 0;
		if (gjId != null)
		{
			SubgjId = CommUtil.StrToInt(gjId.substring(12, 15)) - 1;
		}
		try
		{
			// �����������ݣ�
			// �ܶ������ڵ������ܵ��������·�����ܶ����������������ģ��ʱ������֥�Ӹ���ʱ��λ��
			// �ܵ�·������·�����ڵ������յ�ڵ�ţ��м�������ļ�ָ��
			int NP = 9, NN = 10, Nstart = 3, Npline = 7, NT = 60, NR = 23, Nroute = 3, Nr_node = 8, Nend = 7, Iprt = 0, Nprtc = 20;
			// ���깫ʽ����shanghai storm water formular:
			// (A1+C*lgP)/(t+b)**n---ln(N)=2.303log(N)---����ˮλ��m��
			// �ܶ����٣�m/s��, �ܶ��趨����vp0�����氼͹ϵ��csf
			double A1 = 17.53, C_storm = 0.95, tmin = 10, b_storm = 11.77, P_simu = 100, n_storm = 0.88, dt = 2.0, rc = 0.375, Hw_end = 4.1, vp0 = 0.8, csf = 3.0;

			// ��ϵͳ�ܶ����ݣ�
			int[] I0; // �ܶ����νڵ��I0,
			int[] J0; // ���νڵ��J0,
			double[] lp; // �ܶγ���
			double[] dpl; // �ܶ�ֱ��(m)
			double[] slp; // Ħ|��ϵ��
			double[] ZJup; // ���ιܵ׸߳�(m)
			double[] ZJdw; // ���ιܵ׸߳�(m)

			// ��ϵͳ�ڵ�����
			// ������ʼ�ڵ�ź���ʼ�ڵ�ܵ�����<m>
			double[] Aj; // �ڵ��ˮ���(ha)3.5
			double[] Acoef; // �ڵ��ˮ�������ϵ��0.6
			double[] Hj; // �ڵ�����ߣ�m��[NN=23]

			// ����·������·���ڵ��(-99��ʾ�սڵ�)
			int[][] Mroute;

			// ����·������
			int[] Mstart;

			// ��ϵͳ��֧·���ܶ����ݾ��� ����pipe branches-reverse order
			int[][] Mbranch;

			this.FileSaveRoute = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogData/";
			String XlsPath = "";
			if (gjId != null)
			{
				XlsPath = FileSaveRoute + gjId.substring(0, 12) + ".xls";
				gjName = gjId.substring(0, 12);
			}
			else
			{
				XlsPath = FileSaveRoute + subSys + ".xls";
				gjName = subSys;
			}
			InputStream is = new FileInputStream(XlsPath);
			Workbook rwb = Workbook.getWorkbook(is);
			Sheet rs = rwb.getSheet(0);
			int rsRows = rs.getRows();

			/*
			 * �������ݱ����ϵͳ�� �ڵ���NN �ܶ���NP �����NStart ·���ܶ���Npline ·���ڵ���Nr_node
			 * �յ���ں�Nend ģ��ʱ��NT �ܶ�·����NrouteYJ002 10 9 3 7 8 8 60 3
			 */
			int rowCnt = 2;
			String sysName = rs.getCell(0, rowCnt).getContents().trim();
			NN = Integer.parseInt(rs.getCell(1, rowCnt).getContents().trim());
			NP = Integer.parseInt(rs.getCell(2, rowCnt).getContents().trim());
			Nstart = Integer.parseInt(rs.getCell(3, rowCnt).getContents().trim());
			Npline = Integer.parseInt(rs.getCell(4, rowCnt).getContents().trim());
			Nr_node = Integer.parseInt(rs.getCell(5, rowCnt).getContents().trim());
			Nend = Integer.parseInt(rs.getCell(6, rowCnt).getContents().trim());
			NT = Integer.parseInt(rs.getCell(7, rowCnt).getContents().trim());
			Nroute = Integer.parseInt(rs.getCell(8, rowCnt).getContents().trim());
			rowCnt += 4;

			/*
			 * ��ϵͳ�ܶ����ݱ�� Pipe.No ����I0 �յ��J0 ����LP ֱ��DP Ħ��ϵ�� ��˱�� �ն˱�� 1 0 1 28.5
			 * 0.3 0.017 3.894 3.842 2 1 2 32 0.3 0.017 3.842 3.784 3 2 3 28.6
			 * 0.3 0.017 3.784 3.733 4 3 4 25.4 0.3 0.017 3.733 3.687 5 4 5 24.7
			 * 0.3 0.017 3.687 3.643 6 5 6 23.5 0.3 0.017 3.643 3.601 7 6 7 30.4
			 * 0.3 0.017 3.601 3.546 8 8 7 15.5 0.3 0.017 3.731 3.171 9 9 6 4.3
			 * 0.3 0.017 3.886 3.7
			 */
			I0 = new int[NP];
			J0 = new int[NP];
			lp = new double[NP];
			dpl = new double[NP];
			slp = new double[NP];
			ZJup = new double[NP];
			ZJdw = new double[NP];
			for (int j = 0; j < NP; j++)
			{
				I0[j] = Integer.parseInt(rs.getCell(1, rowCnt + j).getContents().trim());
				J0[j] = Integer.parseInt(rs.getCell(2, rowCnt + j).getContents().trim());
				lp[j] = Double.parseDouble(rs.getCell(3, rowCnt + j).getContents().trim());
				dpl[j] = Double.parseDouble(rs.getCell(4, rowCnt + j).getContents().trim());
				slp[j] = Double.parseDouble(rs.getCell(5, rowCnt + j).getContents().trim());
				ZJup[j] = Double.parseDouble(rs.getCell(6, rowCnt + j).getContents().trim());
				ZJdw[j] = Double.parseDouble(rs.getCell(7, rowCnt + j).getContents().trim());
			}
			rowCnt += NP;
			rowCnt += 3;

			/*
			 * ��ϵͳ�ڵ����ݱ��ڵ�No ��ˮ���ha ����ϵ�� ������ ���ױ�� 1 3.5 0.6 5.244 ��δ�õ� 2 3.5
			 * 0.6 5.191 3 3.5 0.6 5.177 4 3.5 0.6 5.208 5 3.5 0.6 5.221 6 3.5
			 * 0.6 5.201 7 3.5 0.6 5.2 8 3.5 0.6 5.121 9 3.5 0.6 5.131 10 3.5
			 * 0.6 5.186
			 */
			Aj = new double[NN];
			Acoef = new double[NN];
			Hj = new double[NN];
			for (int j = 0; j < NN; j++)
			{
				Aj[j] = Double.parseDouble(rs.getCell(1, rowCnt + j).getContents().trim());
				Acoef[j] = Double.parseDouble(rs.getCell(2, rowCnt + j).getContents().trim());
				Hj[j] = Double.parseDouble(rs.getCell(3, rowCnt + j).getContents().trim());
			}
			rowCnt += NN;
			rowCnt += 3;

			// **************����һ�汾��ȥ��**********
			/**
			 * ����·����&·���ڵ�Žڵ���� 1 2 3 4 5 6 7 8 1 0 1 2 3 4 5 6 7 2 8 7 -99 -99
			 * -99 -99 -99 -99 3 9 6 -99 -99 -99 -99 -99 -99
			 */
			/**
			 * Mroute = new int[Nstart][Nr_node]; for (int j = 0; j < Nstart;
			 * j++) { for (int k = 0; k < Nr_node; k++) { Mroute[j][k] =
			 * Integer.parseInt(rs.getCell(k + 1, rowCnt +
			 * j).getContents().trim()); } } rowCnt += Nstart; rowCnt += 3;
			 */
			// *******************************
			Mroute = new int[Nstart][Nr_node];
			// *************��һ�汾���¼�������********
			/**
			 * ����·������ ��� 1 2 3 ���� 0 8 9
			 */
			Mstart = new int[Nstart];
			for (int j = 0; j < Nstart; j++)
			{
				Mstart[j] = Integer.parseInt(rs.getCell(j + 1, rowCnt).getContents().trim());
			}
			rowCnt += 1;
			rowCnt += 3;
			// ************************************

			/*
			 * ��ϵͳ��֧·���ܶ����ݾ��� ����pipe branches-reverse order �ڵ���� 1 2 3 4 5 6 7 1
			 * 6 5 4 3 2 1 0 2 7 -99 -99 -99 -99 -99 -99 3 8 -99 -99 -99 -99 -99
			 * -99
			 */
			Mbranch = new int[Nstart][Npline];
			for (int j = 0; j < Nstart; j++)
			{
				for (int k = 0; k < Npline; k++)
				{
					Mbranch[j][k] = Integer.parseInt(rs.getCell(k + 1, rowCnt + j).getContents().trim());
				}
			}
			// ----�ٽ�ˮ��������----
			double sita0 = 3.0, eps = 0.001, alfa = 0.5;
			double Ad0, qkpmax, Hwdwkp, yykp, sita, cons_b, sita_s = 0, sita_c, fsita, dfdsita, dfsita, ssita = 0, csita = 0, hyd_A, hafsita, shafsita = 0, chafsita, sita_p = 0;
			// �м����
			int i, j, k, ik, jk, it, k1, kp, in1, in2, in3, NR1, NR2, ii, Nprt, iprt1, iprt2;
			double H00, ycd0;
			double dtnt, taa, tbb, AA, XX1, XX2, hdj0;
			double[] XX = new double[NT];
			double[] qit = new double[NT];
			double[][] sumqj = new double[NT][NN];
			double[][] sumAj = new double[NT][NN];
			double[][] Tnode = new double[NN][NN];
			double[][] sumTnode = new double[NN][NN];
			double[] vp = new double[NP];
			double[] slop = new double[NP];
			double[][] qpt = new double[NT][NP];
			double[][] qqkp = new double[NT][NP];
			double[][] vpt = new double[NT][NP];
			double[][] rid = new double[NT][NP];
			double[][] slopt = new double[NT][NP];
			double[][] Hwup = new double[NT][NP];
			double[][] Hwdw = new double[NT][NP];
			double[][] hdcc0 = new double[NT][NP];
			double[][] overflow = new double[NT][NN];
			double[][] Hw_over = new double[NT][NN];
			double[][] Hwj = new double[NT][NN];

			// ----------------------------------------------------------------------------------------------------------
			String FileName = "";
			if (gjId != null)
			{
				FileName = gjId.substring(0, 12) + ".txt";
			}
			else
			{
				FileName = subSys + ".txt";
			}
			String FilePath = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogValue/";
			FileOutputStream fs = new FileOutputStream(new File(FilePath + FileName));
			PrintStream printStream = new PrintStream(fs);
			printStream.println(FileName);

			DecimalFormat df = new DecimalFormat("##.####");
			DecimalFormat df1 = new DecimalFormat("######.##");
			// --��������ļ���ʼ---
			// ================= ����ֵ ===============================
			for (i = 0; i < NT; i++)
			{
				for (j = 0; j < NN; j++)
					sumAj[i][j] = 0;
			}
			for (i = 0; i < NT; i++)
			{
				for (j = 0; j < NN; j++)
					sumqj[i][j] = 0;
			}
			for (i = 0; i < NN; i++)
			{
				for (j = 0; j < NN; j++)
				{
					if (i == j)
					{
						Tnode[i][j] = 0;
					}
					else
					{
						Tnode[i][j] = -99;
					}
				}
			}
			for (i = 0; i < NN; i++)
			{
				for (j = 0; j < NN; j++)
					sumTnode[i][j] = 0;
			}
			//
			// =====20161029===== ���ɾ��� Mroute[i][j] ====
			//
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Nr_node; j++)
					Mroute[i][j] = -99;
			}
			for (i = 0; i < Nstart; i++)
				Mroute[i][0] = Mstart[i];
			for (i = 0; i < Nstart; i++)
			{
				for (j = 1; j < Nr_node; j++)
				{
					for (k = 0; k < NP; k++)
					{
						if (I0[k] == Mroute[i][j - 1])
						{
							Mroute[i][j] = J0[k];
						}
					}
				}
			}
			// ==================Tnode-sumTnode=========================
			for (i = 0; i < NP; i++)
				vp[i] = vp0;
			for (kp = 0; kp < NP; kp++)
			{
				in1 = I0[kp];
				in2 = J0[kp];
				Tnode[in1][in2] = lp[kp] / vp[kp] / 60;
				slop[kp] = (ZJup[kp] - ZJdw[kp]) / lp[kp];
			}
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Nr_node; j++)
				{
					in1 = Mroute[i][j];
					if (in1 >= 0)
					{
						for (k = j + 1; k < Nr_node; k++)
						{
							in2 = Mroute[i][k - 1];
							in3 = Mroute[i][k];
							if (in3 >= 0)
							{
								sumTnode[in1][in3] = sumTnode[in1][in2] + Tnode[in2][in3];
							}
						}
					}
				}
			}
			// System.out.println("pipe no.  I0    J0");
			for (i = 0; i < NP; i++)
			{
				// System.out.printf("%6d%6d%6d", i, I0[i], J0[i]);
				// System.out.println();
			}
			printStream.println();
			printStream.print(" ip=");
			for (i = 0; i < NP; i++)
			{
				printStream.printf("%4d", i);
			}
			printStream.println();
			printStream.print(" I0=");
			for (i = 0; i < NP; i++)
			{
				printStream.printf("%4d", I0[i]);
			}
			printStream.println();
			printStream.print(" J0=");
			for (i = 0; i < NP; i++)
			{
				printStream.printf("%4d", J0[i]);
			}
			printStream.println();
			printStream.println();
			printStream.println("===========  print Mroute[i][j]");
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Nr_node; j++)
				{
					printStream.printf("%6d", Mroute[i][j]);
				}
				printStream.println();
			}
			printStream.println();
			printStream.println("===========  print Mbranch[i][j]");
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Npline; j++)
				{
					printStream.printf("%6d", Mbranch[i][j]);
				}
				printStream.println();
			}
			printStream.println("===========  print Tnode[i][j]");
			printStream.println("====j=  ");
			printStream.println("      ");
			for (j = 0; j < NN; j++)
			{
				printStream.printf("%6d", j);
			}
			printStream.println();
			for (i = 0; i < NN; i++)
			{
				if (i < 10)
				{
					printStream.print("i=" + i + "   ");
				}
				else
				{
					printStream.print("i=" + i + "  ");
				}
				for (j = 0; j < NN; j++)
				{
					if (Tnode[i][j] < 0.0)
					{
						printStream.print("      ");
					}
					else
					{
						printStream.printf("%6.2f", Tnode[i][j]);
					}
				}
				printStream.println();
			}
			printStream.println();
			printStream.println("===========  print sumTnode[i][j]");
			printStream.print("==j=  ");
			for (j = 0; j < NN; j++)
			{
				printStream.printf("%6d", j);
			}
			printStream.println();
			for (i = 0; i < NN; i++)
			{
				printStream.print("i=" + i + "   ");
				for (j = 0; j < NN; j++)
				{
					if (sumTnode[i][j] <= 0.0)
					{
						printStream.print("      ");
					}
					else
					{
						printStream.printf("%6.2f", sumTnode[i][j]);
					}
				}
				printStream.println();
			}
			// ================= ����׼��̬����ģ��============================
			// -------------------��̬ģ����������-----------------------------
			// ----------------�ڵ��ˮ���(ha)�ͻ�ˮ����(m3/sec)����--------
			printStream.println();
			printStream.println("===========  ������̬ģ�����      �����ڣ� " + P_simu + "  ��   ʱ������ " + NT + "       �յ�ˮλ�� " + Hw_end + "  m  =========");
			// ֥�Ӹ������--rainfall intensity at every time step--
			AA = A1 + A1 * C_storm * Math.log(P_simu) / 2.303;
			for (it = 0; it < NT; it++)
			{
				if (it <= NR)
				{
					dtnt = dt * (float) (it);
					tbb = dt * (float) (NR) - dtnt;
					XX1 = AA * ((1.0 - n_storm) * tbb / rc + b_storm);
					XX2 = Math.pow((tbb / rc + b_storm), (n_storm + 1.0));
				}
				else
				{
					dtnt = dt * (float) (it);
					taa = dtnt - dt * (float) (NR);
					XX1 = AA * ((1.0 - n_storm) * taa / (1.0 - rc) + b_storm);
					XX2 = Math.pow((taa / (1.0 - rc) + b_storm), (n_storm + 1.0));
				}
				XX[it] = XX1 / XX2;
				qit[it] = 167.0 * XX[it] / 1000.0;
			}
			NR1 = NR - 1;
			NR2 = NR + 1;
			qit[NR] = (qit[NR] + qit[NR - 1] + qit[NR + 1]) / 3.0;
			printStream.println();
			printStream.println("    it      dtnt      XX[it]     qit[it]");
			for (it = 0; it < NT; it++)
			{
				dtnt = dt * (float) (it);
				printStream.printf("%6d%10.2f%12.6f%12.6f", it, dtnt, XX[it], qit[it]);
				printStream.println();
			}
			printStream.println();
			for (it = 0; it < NT; it++)
			{
				dtnt = dt + dt * (float) (it);
				for (j = 0; j < NN; j++)
				{
					sumAj[it][j] = Aj[j];
					sumqj[it][j] = Aj[j] * qit[it] * Acoef[j];
					for (i = 0; i < NN; i++)
					{
						if (sumTnode[i][j] > 0 && sumTnode[i][j] < dtnt)
						{
							sumAj[it][j] = sumAj[it][j] + Aj[i];
							sumqj[it][j] = sumqj[it][j] + Aj[i] * qit[it] * Acoef[i];
						}
					}
				}
			}
			printStream.println("  sumAj[it][j]=");
			for (it = 0; it < NT; it++)
			{
				for (j = 0; j < NN; j++)
				{
					printStream.printf("%8.2f", sumAj[it][j]);
				}
				printStream.println();
			}
			printStream.println();
			printStream.println("  sumqj[it][j]=");
			for (it = 0; it < NT; it++)
			{
				for (j = 0; j < NN; j++)
				{
					printStream.printf("%8.2f", sumqj[it][j]);
				}
				printStream.println();
			}
			printStream.println();
			// ---------------------------------------------------------------
			for (it = 0; it < NT; it++)
			{
				for (i = 0; i < NN; i++)
				{
					overflow[it][i] = 0.0;
					Hw_over[it][i] = 0.0;
				}
			}
			for (it = 0; it < NT; it++)
			{
				for (j = 0; j < NP; j++)
				{
					qpt[it][j] = -99.0;
					qqkp[it][j] = 0.0;
				}
			}
			// ---------------------------------------------------------------
			for (it = 0; it < NT; it++)
			{
				printStream.print(" it=" + it + "  qpt[it][k]=");
				for (j = 0; j < NN; j++)
				{
					for (k = 0; k < NP; k++)
					{
						if (I0[k] == j)
						{
							qpt[it][k] = sumqj[it][j];
							printStream.printf("%8.2f", qpt[it][k]);
						}
					}
				}
				printStream.println();
				for (ik = 0; ik < Nstart; ik++)
				{
					for (jk = 0; jk < Npline; jk++)
					{
						kp = Mbranch[ik][jk];
						if (kp >= 0)
						{
							if (J0[kp] == Nend)
							{
								Hwdw[it][kp] = Hw_end;
								if (1 == Iprt)
								{
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  Hw_end= " + Hw_end);
								}
							}
							else
							{
								for (k1 = 0; k1 < NP; k1++)
								{
									if (I0[k1] == J0[kp]) Hwdw[it][kp] = Hwup[it][k1];
								}
							}
							Ad0 = 0.7854 * Math.pow(dpl[kp], 2.0);
							hdj0 = ZJdw[kp] + dpl[kp];
							if (Hwdw[it][kp] >= hdj0)
							{
								if (1 == Iprt)
								{
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + df.format(Hwdw[it][kp]) + "  ��û���� ");
								}
								hdcc0[it][kp] = 1.0;
								rid[it][kp] = dpl[kp] / 4.0;
								vpt[it][kp] = qpt[it][kp] / Ad0;
								slopt[it][kp] = 10.29 * Math.pow(slp[kp], 2.0) * Math.pow(qpt[it][kp], 2.0) / Math.pow(dpl[kp], 5.333);
								Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
								if (Hwup[it][kp] >= Hj[I0[kp]])
								{
									Hwup[it][kp] = Hj[I0[kp]];
									slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
									if (slopt[it][kp] < 0.0)
									{
										slopt[it][kp] = Math.abs(slopt[it][kp]);
									}
									vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt[it][kp], 0.5) / slp[kp];
									qqkp[it][kp] = vpt[it][kp] * Ad0;
									if (qqkp[it][kp] < 0.0)
									{
										qqkp[it][kp] = Math.abs(qqkp[it][kp]);
									}
								}
							}
							else
							{
								if (Iprt == 1)
								{
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdw= " + Hwdw[it][kp] + "  ����û���� ");
								}
								// --20161018�޸Ŀ�ʼ---�����ٽ�ˮ����㷨-----------------------
								qkpmax = 2.699 * Math.pow(dpl[kp], 2.5);
								if (qpt[it][kp] > qkpmax)
								{
									if (Iprt == 1)
									{
										printStream.println("   it= " + it + "   kp= " + kp + "  qkpmax= " + qkpmax + "  ����û���ܳ��� ");
									}
									vpt[it][kp] = qpt[it][kp] / Ad0;
									H00 = Math.pow(vpt[it][kp], 2.0) / 13.72;
									Hwdw[it][kp] = ZJdw[kp] + dpl[kp] + H00;
									hdcc0[it][kp] = 1.0;
									rid[it][kp] = dpl[kp] / 4.0;
									slopt[it][kp] = 10.29 * Math.pow(slp[kp], 2.0) * Math.pow(qpt[it][kp], 2.0) / Math.pow(dpl[kp], 5.333);
									Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
									if (Hwup[it][kp] >= Hj[I0[kp]])
									{
										Hwup[it][kp] = Hj[I0[kp]];
										slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
										if (slopt[it][kp] < 0.0)
										{
											slopt[it][kp] = Math.abs(slopt[it][kp]);
										}
										vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt[it][kp], 0.5) / slp[kp];
										qqkp[it][kp] = vpt[it][kp] * Ad0;
										if (qqkp[it][kp] < 0.0)
										{
											qqkp[it][kp] = Math.abs(qqkp[it][kp]);
										}
									}
								}
								else
								{
									if (Iprt == 1)
									{
										printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  ����û�����ܳ��� ");
									}
									// ==20161018�޸Ŀ�ʼ---�����ٽ�ˮ��򻯹�ʽ--------zhou-p21------
									ycd0 = qpt[it][kp] / 2.983 / Math.pow(dpl[kp], 2.5);
									hdcc0[it][kp] = Math.pow(ycd0, 0.513);
									sita = 2.0 * Math.acos(1.0 - 2.0 * hdcc0[it][kp]);
									rid[it][kp] = 0.25 * dpl[kp] * (sita - Math.sin(sita)) / sita;
									vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slop[kp], 0.5) / slp[kp];
								}
								// ---for(k=0;k<N;k++)����---20160907�޸Ľ���---�ٽ�ˮ���㷨--------------
								Hwdwkp = ZJdw[kp] + hdcc0[it][kp] * dpl[kp];
								if (Hwdwkp >= Hwdw[it][kp])
								{
									Hwdw[it][kp] = Hwdwkp;
								}
								else
								{
									yykp = Hwdw[it][kp] - ZJdw[kp];
									if (yykp > dpl[kp])
									{
										yykp = dpl[kp];
									}
									sita = 2.0 * Math.acos(1.0 - 2.0 * yykp / dpl[kp]);
									hdcc0[it][kp] = yykp / dpl[kp];
									rid[it][kp] = 0.25 * dpl[kp] * (sita - Math.sin(sita)) / sita;
									vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slop[kp], 0.5) / slp[kp];
								}
								Hwup[it][kp] = Hwdw[it][kp] + slop[kp] * lp[kp];
							}
							// ------- ���it������ ----------
							if (Iprt == 1)
							{
								printStream.println("   it= " + it + "   kp= " + kp + "   I0[kp]= " + I0[kp] + "  Hwdm= " + Hwdw[it][kp] + "  Hwup= " + Hwup[it][kp] + "  Hj= " + Hj[I0[kp]] + "  hdcc0= " + hdcc0[it][kp] + "  qpt= " + qpt[it][kp] + "  qqkp= " + qqkp[it][kp] + "  vpt= " + vpt[it][kp]);
							}
						}
					}
				}
				printStream.println();

				printStream.println("    it   �ܶκ�  I0   J0 �ܾ�dpl     �ܶ�qp   ˮ���뾶R  ������ ����(m/s)  ����ˮλ  ����ˮλ  �Ϲܵ׸�  �¹ܵ׸�  �ܶ��¶�  �ϵ����");
				for (i = 0; i < NP; i++)
				{
					printStream.printf("%6d%6d%6d%5d%8.2f%12.3f%10.3f%8.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.5f%10.3f", it, i, I0[i], J0[i], dpl[i], qpt[it][i], rid[it][i], hdcc0[it][i], vpt[it][i], Hwup[it][i], Hwdw[it][i], ZJup[i], ZJdw[i], slop[i], Hj[I0[i]]);
					printStream.println();
				}
				printStream.println();
				// -------------- ��ʼ���������ڵ� ---------------
				for (i = 0; i < NP; i++)
				{
					k = J0[i];
					if (k == Nend)
					{
						Hwj[it][k] = Hwdw[it][i];
					}
					{
						j = I0[i];
						Hwj[it][j] = Hwup[it][i];
						if (Hwup[it][i] == Hj[j])
						{
							overflow[it][j] = overflow[it][j] + (qpt[it][i] - qqkp[it][i]) * dt * 60.0;
							Hw_over[it][j] = csf * overflow[it][j] / Aj[j] / 10000.0 * 1000.0;

						}
						if (Hwup[it][i] < Hj[j] && overflow[it][j] > 0.0)
						{
							overflow[it][j] = overflow[it - 1][j] * 0.90;
							Hw_over[it][j] = csf * overflow[it][j] / Aj[j] / 10000.0 * 1000.0;
						}
					}
					if (it > NR && Hw_over[it][j] <= 5.0)
					{
						overflow[it][j] = 0.0;
						Hw_over[it][j] = 0.0;
					}
				}
				// ------------------ ���������ڵ���� ---------------
			}
			// ----------------��Ļ����������------
			// System.out.println("------ ģ�ͼ���ȫ����� ------");
			// ---------------------- ����ܶγ����ȼ����� ---------------
			printStream.println(" ======== ʱ�ιܶγ����� ========");
			Nprt = NP / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NP)
					{
						iprt2 = NP;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println();
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						printStream.printf("%8.3f", hdcc0[it][i]);
					}
					printStream.println();
				}
			}
			//
			// ------------------- ����ڵ�ˮλ������ ---------------
			printStream.println(" ======== ʱ�νڵ�ˮλ ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NN)
					{
						iprt2 = NN;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "  ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
				}
			}

			// ***********��֯���ݣ�����ҳ��������ʾ********
			for (it = 0; it < NT; it++)
			{
				String WaterLevNew = "";
				for (i = 0; i < NN; i++)
				{
					if (gjId != null && i == SubgjId)
					{
						WaterAccGj += df1.format(Hwj[it][i]) + "|";
					}
					WaterLevNew += df1.format(Hwj[it][i]) + "|";
				}
				WaterLev[it] = WaterLevNew;
			}
			// *************************************
			// ------------------ ����ڵ����������� ---------------
			printStream.println(" ======== ʱ�νڵ��ˮ��(m3) ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NN)
					{
						iprt2 = NN;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "  ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.println(" " + it + "   ");
					}
					else
					{
						printStream.println(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						if (overflow[it][i] <= 0.0)
						{
							printStream.print("        ");
						}
						else
						{
							printStream.printf("%8.2f", overflow[it][i]);
						}
					}
					printStream.println();
					String WaterAccNew = "";
					for (i = 0; i < NN; i++)
					{
						if (overflow[it][i] <= 0.0)
						{
							WaterAccNew += 0 + "|";
						}
						else
						{
							WaterAccNew += df1.format(overflow[it][i]) + "|";
						}
					}
					WaterAcc[it] = WaterAccNew;
				}
			}

			// ***********��֯���ݣ�����ҳ��������ʾ********
			for (it = 0; it < NT; it++)
			{
				String WaterAccNew = "";
				for (i = 0; i < NN; i++)
				{
					if (overflow[it][i] <= 0.0)
					{
						WaterAccNew += 0 + "|";
					}
					else
					{
						WaterAccNew += df1.format(overflow[it][i]) + "|";
					}
				}
				WaterAcc[it] = WaterAccNew;
			}
			// *********************************************
			printStream.println(" ======== ʱ�νڵ��ˮ���(mm) ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NN)
					{
						iprt2 = NN;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + i + "   ");
					}
					else
					{
						printStream.print(i + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						if (overflow[it][i] <= 0.0)
						{
							printStream.print("        ");
						}
						else
						{
							printStream.printf("%8.2f", Hw_over[it][i]);
						}
					}
					printStream.println();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return gjName + "," + Count;
		}
		if (AnalogWaterType.equals("WaterAccGj"))
		{
			return WaterAccGj;
		}
		else if (AnalogWaterType.equals("WaterAcc"))
		{
			String WaterAccList = "";
			for (int i = 0; i < WaterAcc.length; i++)
			{
				WaterAccList += subSys.substring(7, 12) + WaterAcc[i] + ";";
			}
			return WaterAccList;
		}
		else if (AnalogWaterType.equals("WaterLev"))
		{
			String WaterLevList = "";
			for (int i = 0; i < WaterLev.length; i++)
			{
				WaterLevList += subSys.substring(7, 12) + WaterLev[i] + ";";
			}
			return WaterLevList;
		}
		return "";
	}

	// �����װ汾
	// �ر�˵������һ�汾��ǰ�����汾���õı��һ��
	private String analog_Y4(String subSys, int timePeriod, String gjId, String AnalogWaterType)
	{
		WaterAcc = new String[60];
		WaterLev = new String[60];
		int SubgjId = 0;
		if (gjId != null)
		{
			SubgjId = CommUtil.StrToInt(gjId.substring(12, 15)) - 1;
		}
		try
		{
			// �����������ݣ�
			// �ܶ������ڵ������ܵ��������·�����ܶ����������������ģ��ʱ������֥�Ӹ���ʱ��λ��
			// �ܵ�·������·�����ڵ������յ�ڵ�ţ��м�������ļ�ָ��
			int NP = 9, NN = 10, Nstart = 3, Npline = 7, NT = 60, NR = 23, Nr_node = 8, Nend = 7, Iprt = 0, Nprtc = 20;
			int Ncol = 5;// �ڵ��������ܶ���+3���˴���С

			// ���깫ʽ����shanghai storm water formular:
			// (A1+C*lgP)/(t+b)**n---ln(N)=2.303log(N)---����ˮλ��m��
			// �ܶ����٣�m/s��, �ܶ��趨����vp0�����氼͹ϵ��csf
			double A1 = 17.53, C_storm = 0.95, b_storm = 11.77, P_simu = 10, n_storm = 0.88, dt = 2.0, rc = 0.375, Hw_end = 4.2, vp0 = 0.8, csf = 1.0;

			// ��ϵͳ�ܶ����ݣ�
			int[] I0; // �ܶ����νڵ��I0,
			int[] J0; // ���νڵ��J0,
			double[] lp; // �ܶγ���
			double[] dpl; // �ܶ�ֱ��(m)
			double[] slp; // Ħ|��ϵ��
			double[] ZJup; // ���ιܵ׸߳�(m)
			double[] ZJdw; // ���ιܵ׸߳�(m)

			// ��ϵͳ�ڵ�����
			// ������ʼ�ڵ�ź���ʼ�ڵ�ܵ�����<m>
			double[] Aj; // �ڵ��ˮ���(ha)3.5
			double[] Acoef; // �ڵ��ˮ�������ϵ��0.6
			double[] Hj; // �ڵ�����ߣ�m��[NN=23]

			// ����·������·���ڵ��(-99��ʾ�սڵ�)
			int[][] Mroute;

			// ����·������
			int[] Mstart;

			// ��ϵͳ��֧·���ܶ����ݾ��� ����pipe branches-reverse order
			int[][] Mbranch;

			this.FileSaveRoute = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogData/";
			String XlsPath = "";
			if (gjId != null)
			{
				XlsPath = FileSaveRoute + gjId.substring(0, 12) + ".xls";
				gjName = gjId.substring(0, 12);
			}
			else
			{
				XlsPath = FileSaveRoute + subSys + ".xls";
				gjName = subSys;
			}
			InputStream is = new FileInputStream(XlsPath);
			Workbook rwb = Workbook.getWorkbook(is);
			Sheet rs = rwb.getSheet(0);
			int rsRows = rs.getRows();

			/*
			 * �������ݱ����ϵͳ�� �ڵ���NN �ܶ���NP �����NStart ·���ܶ���Npline ·���ڵ���Nr_node
			 * �յ���ں�Nend ģ��ʱ��NT �ܶ�·����NrouteYJ002 10 9 3 7 8 8 60 3
			 */
			rowCnt = 2;
			String sysName = rs.getCell(0, rowCnt).getContents().trim();
			NN = Integer.parseInt(rs.getCell(1, rowCnt).getContents().trim());
			NP = Integer.parseInt(rs.getCell(2, rowCnt).getContents().trim());
			Nstart = Integer.parseInt(rs.getCell(3, rowCnt).getContents().trim());
			Npline = Integer.parseInt(rs.getCell(4, rowCnt).getContents().trim());
			Nr_node = Integer.parseInt(rs.getCell(5, rowCnt).getContents().trim());
			Nend = Integer.parseInt(rs.getCell(6, rowCnt).getContents().trim());
			NT = Integer.parseInt(rs.getCell(7, rowCnt).getContents().trim());
			Ncol = Integer.parseInt(rs.getCell(8, rowCnt).getContents().trim());
			rowCnt += 4;

			/*
			 * ��ϵͳ�ܶ����ݱ�� Pipe.No ����I0 �յ��J0 ����LP ֱ��DP Ħ��ϵ�� ��˱�� �ն˱�� 1 0 1 28.5
			 * 0.3 0.017 3.894 3.842 2 1 2 32 0.3 0.017 3.842 3.784 3 2 3 28.6
			 * 0.3 0.017 3.784 3.733 4 3 4 25.4 0.3 0.017 3.733 3.687 5 4 5 24.7
			 * 0.3 0.017 3.687 3.643 6 5 6 23.5 0.3 0.017 3.643 3.601 7 6 7 30.4
			 * 0.3 0.017 3.601 3.546 8 8 7 15.5 0.3 0.017 3.731 3.171 9 9 6 4.3
			 * 0.3 0.017 3.886 3.7
			 */
			I0 = new int[NP];
			J0 = new int[NP];
			lp = new double[NP];
			dpl = new double[NP];
			slp = new double[NP];
			ZJup = new double[NP];
			ZJdw = new double[NP];
			for (int j = 0; j < NP; j++)
			{
				I0[j] = Integer.parseInt(rs.getCell(1, rowCnt + j).getContents().trim());
				J0[j] = Integer.parseInt(rs.getCell(2, rowCnt + j).getContents().trim());
				lp[j] = Double.parseDouble(rs.getCell(3, rowCnt + j).getContents().trim());
				dpl[j] = Double.parseDouble(rs.getCell(4, rowCnt + j).getContents().trim());
				slp[j] = Double.parseDouble(rs.getCell(5, rowCnt + j).getContents().trim());
				ZJup[j] = Double.parseDouble(rs.getCell(6, rowCnt + j).getContents().trim());
				ZJdw[j] = Double.parseDouble(rs.getCell(7, rowCnt + j).getContents().trim());
			}
			rowCnt += NP;
			rowCnt += 3;

			/*
			 * ��ϵͳ�ڵ����ݱ��ڵ�No ��ˮ���ha ����ϵ�� ������ ���ױ�� 1 3.5 0.6 5.244 ��δ�õ� 2 3.5
			 * 0.6 5.191 3 3.5 0.6 5.177 4 3.5 0.6 5.208 5 3.5 0.6 5.221 6 3.5
			 * 0.6 5.201 7 3.5 0.6 5.2 8 3.5 0.6 5.121 9 3.5 0.6 5.131 10 3.5
			 * 0.6 5.186
			 */
			Aj = new double[NN];
			Acoef = new double[NN];
			Hj = new double[NN];
			for (int j = 0; j < NN; j++)
			{
				Aj[j] = Double.parseDouble(rs.getCell(1, rowCnt + j).getContents().trim());
				Acoef[j] = Double.parseDouble(rs.getCell(2, rowCnt + j).getContents().trim());
				Hj[j] = Double.parseDouble(rs.getCell(3, rowCnt + j).getContents().trim());
			}
			rowCnt += NN;
			rowCnt += 3;

			// **************��Y3�汾��ȥ��**********
			/**
			 * ����·����&·���ڵ�Žڵ���� 1 2 3 4 5 6 7 8 1 0 1 2 3 4 5 6 7 2 8 7 -99 -99
			 * -99 -99 -99 -99 3 9 6 -99 -99 -99 -99 -99 -99
			 */
			/**
			 * Mroute = new int[Nstart][Nr_node]; for (int j = 0; j < Nstart;
			 * j++) { for (int k = 0; k < Nr_node; k++) { Mroute[j][k] =
			 * Integer.parseInt(rs.getCell(k + 1, rowCnt +
			 * j).getContents().trim()); } } rowCnt += Nstart; rowCnt += 3;
			 */
			// *******************************
			// ��������-·������·���ڵ�ž���
			Mroute = new int[Nstart][Nr_node];
			Mstart = new int[Nstart];

			// *************��һ�汾ȥ����ȥ��******
			/**
			 * for (int j = 0; j < Nstart; j++) { Mstart[j] =
			 * Integer.parseInt(rs.getCell(j + 1, rowCnt).getContents().trim());
			 * } rowCnt += 1; rowCnt += 3;
			 */
			// ************************************

			/*
			 * ��ϵͳ��֧·���ܶ����ݾ��� ����pipe branches-reverse order �ڵ���� 1 2 3 4 5 6 7 1
			 * 6 5 4 3 2 1 0 2 7 -99 -99 -99 -99 -99 -99 3 8 -99 -99 -99 -99 -99
			 * -99
			 */
			Mbranch = new int[Nstart][Npline];
			// *********��һ�汾��ȥ��***********
			/**
			 * for (int j = 0; j < Nstart; j++) { for (int k = 0; k < Npline;
			 * k++) { Mbranch[j][k] = Integer.parseInt(rs.getCell(k + 1, rowCnt
			 * + j).getContents().trim()); } }
			 */
			// ----�ٽ�ˮ��������----
			double Ad0, qkpmax, Hwdwkp, yykp, sita;

			// ----�м�ָ�����----
			int i00, j00 = 0, Ni1, jj, jp0, inp = 0, jpp, NPP;

			// �����ֲ�֧�߹ܶξ���-��������
			int[] Npjun = new int[NP];
			int[][] MNP = new int[NN][Ncol];

			// �м����
			int i, j, k, ik, jk, jjj, INS, it, k1, kp, in1, in2, in3, NR1, NR2, ii, Nprt, iprt1 = 0, iprt2 = 0;
			double H00, ycd0;
			double dtnt, taa, tbb, AA, XX1, XX2, hdj0;
			double[] XX = new double[NT];
			double[] qit = new double[NT];
			double[][] sumqj = new double[NT][NN];
			double[][] sumAj = new double[NT][NN];
			double[][] Tnode = new double[NN][NN];
			double[][] sumTnode = new double[NN][NN];
			double[] vp = new double[NP];
			double[] slop = new double[NP];
			double[][] qpt = new double[NT][NP];
			double[][] qqkp = new double[NT][NP];
			double[][] vpt = new double[NT][NP];
			double[][] rid = new double[NT][NP];
			double[][] slopt = new double[NT][NP];
			double[][] Hwup = new double[NT][NP];
			double[][] Hwdw = new double[NT][NP];
			double[][] hdcc0 = new double[NT][NP];
			double[][] overflow = new double[NT][NN];
			double[][] Hw_over = new double[NT][NN];
			double[][] Hwj = new double[NT][NN];

			// ----------------------------------------------------------------------------------------------------------
			String FileName = "";
			if (gjId != null)
			{
				FileName = gjId.substring(0, 12) + ".txt";
			}
			else
			{
				FileName = subSys + ".txt";
			}
			String FilePath = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogValue/";
			FileOutputStream fs = new FileOutputStream(new File(FilePath + FileName));
			PrintStream printStream = new PrintStream(fs);
			printStream.println(FileName);

			DecimalFormat df = new DecimalFormat("##.####");
			DecimalFormat df1 = new DecimalFormat("######.##");
			// --��������ļ���ʼ---
			// ================= ����ֵ ===============================
			//
			for (i = 0; i < NT; i++)
			{
				for (j = 0; j < NN; j++)
					sumAj[i][j] = 0;
			}
			for (i = 0; i < NT; i++)
			{
				for (j = 0; j < NN; j++)
					sumqj[i][j] = 0;
			}
			for (i = 0; i < NN; i++)
			{
				for (j = 0; j < NN; j++)
				{
					if (i == j)
					{
						Tnode[i][j] = 0;
					}
					else
					{
						Tnode[i][j] = -99;
					}
				}
			}
			for (i = 0; i < NN; i++)
			{
				for (j = 0; j < NN; j++)
					sumTnode[i][j] = 0;
			}
			// ====20161106===== ���ɾ��� MNP[i][j] ====
			for (i = 0; i < NN; i++)
			{
				for (j = 0; j < Ncol; j++)
				{
					MNP[i][j] = 0;
				}
				MNP[i][0] = i;
				jj = 2;
				for (k = 0; k < NP; k++)
				{
					if (J0[k] == i)
					{
						jj = jj + 1;
						MNP[i][1] = MNP[i][1] + 1;
						MNP[i][jj] = k;
					}
					if (I0[k] == i)
					{
						MNP[i][2] = MNP[i][2] + 1;
					}
				}
			}
			// System.out.println("===========  print MNP[i][j]");
			printStream.println("===========  print MNP[i][j]");
			for (i = 0; i < NN; i++)
			{
				for (j = 0; j < Ncol; j++)
				{
					printStream.printf("%6d", MNP[i][j]);
				}
				printStream.println();
			}
			// ----- MNP[i][j] ���� ------
			// ====20161112===== ���ɾ��� Mstart[i] ====
			jj = -1;
			for (i = 0; i < NN; i++)
			{
				if (MNP[i][1] == 0)
				{
					jj = jj + 1;
					Mstart[jj] = i;
				}
			}
			printStream.println("===========  print Mstart[i]");
			for (i = 0; i < Nstart; i++)
			{
				printStream.printf("%6d", Mstart[i]);
			}
			printStream.println();
			// ====20161029===== ���ɾ��� Mroute[i][j] ====
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Nr_node; j++)
					Mroute[i][j] = -99;
			}
			for (i = 0; i < Nstart; i++)
				Mroute[i][0] = Mstart[i];
			for (i = 0; i < Nstart; i++)
			{
				for (j = 1; j < Nr_node; j++)
				{
					for (k = 0; k < NP; k++)
					{
						if (I0[k] == Mroute[i][j - 1])
						{
							Mroute[i][j] = J0[k];
						}
					}
				}
			}
			// ====20161106===== ���ɾ���Mbranch[i][j] ====
			for (i = 0; i < NP; i++)
			{
				Npjun[i] = 1;
			}
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Npline; j++)
				{
					Mbranch[i][j] = -99;
				}
			}
			i00 = -1;
			NPP = 0;
			while (true)
			{
				if (NPP < NP)
				{
					for (i = 0; i < NN; i++)
					{
						if (MNP[i][2] == 0 && MNP[i][1] > 0)
						{
							jj = 2;
							Ni1 = MNP[i][1];
							for (j = 0; j < Ni1; j++)
							{
								jj = jj + 1;
								jp0 = MNP[i][jj];
								if (Npjun[jp0] > 0)
								{
									i00 = i00 + 1;
									j00 = 0;
									Mbranch[i00][j00] = jp0;
									inp = I0[jp0];
									Npjun[jp0] = -99;
									NPP = NPP + 1;
								}

								// L100:
								while (true)
								{
									INS = 1;
									for (jjj = 0; jjj < Nstart; jjj++)
									{
										if (Mstart[jjj] == inp)
										{
											INS = 0;
											break;
										}
									}
									if (INS > 0)
									{
										for (jpp = 0; jpp < NP; jpp++)
										{
											if (J0[jpp] == inp && Npjun[jpp] > 0)
											{
												j00 = j00 + 1;
												Mbranch[i00][j00] = jpp;
												inp = I0[jpp];
												Npjun[jpp] = -99;
												NPP = NPP + 1;
												// goto L100;
												break;
											}
											else
											{
												continue;
											}
										}
									}
									else
									// --- end of if(INS>0) ---
									{
										break;
									}
								}
							} // --- end of for(j=0;j<Ni1;j++) ---
						} // --- end of if(MNP[i][2]==0 && MNP[i][1]>0) ---
						MNP[i][2] = -99;
					}
					for (i = 0; i < NN; i++)
					{
						for (j = 0; j < NP; j++)
						{
							if (I0[j] == i && Npjun[j] < 0)
							{
								MNP[i][2] = 0;
							}
						}
					}// --- end of for(i=0;i<NN;1++) ---
				}
				else
				{
					break;
				}
			}
			// === ���ɾ��� Mbranch[i][j] ����====
			// ==================Tnode-sumTnode=========================
			for (i = 0; i < NP; i++)
			{
				vp[i] = vp0;
			}
			for (kp = 0; kp < NP; kp++)
			{
				in1 = I0[kp];
				in2 = J0[kp];
				Tnode[in1][in2] = lp[kp] / vp[kp] / 60;
				slop[kp] = (ZJup[kp] - ZJdw[kp]) / lp[kp];
			}
			//
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Nr_node; j++)
				{
					in1 = Mroute[i][j];
					if (in1 >= 0)
					{
						for (k = j + 1; k < Nr_node; k++)
						{
							in2 = Mroute[i][k - 1];
							in3 = Mroute[i][k];
							if (in3 >= 0)
							{
								sumTnode[in1][in3] = sumTnode[in1][in2] + Tnode[in2][in3];
							}
						}
					}
				}
			}
			// System.out.println("pipe no.  I0    J0");
			for (i = 0; i < NP; i++)
			{
				// System.out.printf("%6d%6d%6d", i, I0[i], J0[i]);
				// System.out.println();
			}
			printStream.println();
			printStream.println("=====print pipe no.  I0    J0=====");
			printStream.print(" ip=");
			for (i = 0; i < NP; i++)
			{
				printStream.printf("%4d", i);
			}
			printStream.println();
			printStream.print(" I0=");
			for (i = 0; i < NP; i++)
			{
				printStream.printf("%4d", I0[i]);
			}
			printStream.println();
			printStream.print(" J0=");
			for (i = 0; i < NP; i++)
			{
				printStream.printf("%4d", J0[i]);
			}
			printStream.println();
			printStream.println();
			printStream.println("===========  print Mroute[i][j]");
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Nr_node; j++)
				{
					printStream.printf("%6d", Mroute[i][j]);
				}
				printStream.println();
			}
			printStream.println();
			printStream.println("===========  print Mbranch[i][j]");
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Npline; j++)
				{
					printStream.printf("%6d", Mbranch[i][j]);
				}
				printStream.println();
			}
			printStream.println("===========  print Tnode[i][j]");
			printStream.println("====j=  ");
			printStream.print("      ");
			for (j = 0; j < NN; j++)
			{
				printStream.printf("%6d", j);
			}
			printStream.println();
			for (i = 0; i < NN; i++)
			{
				if (i < 10)
				{
					printStream.print("i=" + i + "   ");
				}
				else
				{
					printStream.print("i=" + i + "  ");
				}

				for (j = 0; j < NN; j++)
				{
					if (Tnode[i][j] < 0.0)
					{
						printStream.print("      ");
					}
					else
					{
						printStream.printf("%6.2f", Tnode[i][j]);
					}
				}
				printStream.println();
			}
			printStream.println();
			printStream.println("===========  print sumTnode[i][j]");
			printStream.println("==j=  ");
			for (j = 0; j < NN; j++)
			{
				printStream.printf("%6d", j);
			}
			printStream.println();

			for (i = 0; i < NN; i++)
			{
				printStream.print("i=" + i + "   ");
				for (j = 0; j < NN; j++)
				{
					if (sumTnode[i][j] <= 0.0)
					{
						printStream.print("      ");
					}
					else
					{
						printStream.printf("%6.2f", sumTnode[i][j]);
					}
				}
				printStream.println();
			}
			// ================= ����׼��̬����ģ��============================
			//
			// -------------------��̬ģ����������-----------------------------
			// ----------------�ڵ��ˮ���(ha)�ͻ�ˮ����(m3/sec)����--------

			printStream.println();
			printStream.println("===========  ������̬ģ�����      �����ڣ� " + P_simu + "  ��   ʱ������ " + NT + "       �յ�ˮλ�� " + Hw_end + "  m  =========");
			// ֥�Ӹ������--rainfall intensity at every time step--
			AA = A1 + A1 * C_storm * Math.log(P_simu) / 2.303;
			for (it = 0; it < NT; it++)
			{
				if (it <= NR)
				{
					dtnt = dt * (float) (it);
					tbb = dt * (float) (NR) - dtnt;
					XX1 = AA * ((1.0 - n_storm) * tbb / rc + b_storm);
					XX2 = Math.pow((tbb / rc + b_storm), (n_storm + 1.0));
				}
				else
				{
					dtnt = dt * (float) (it);
					taa = dtnt - dt * (float) (NR);
					XX1 = AA * ((1.0 - n_storm) * taa / (1.0 - rc) + b_storm);
					XX2 = Math.pow((taa / (1.0 - rc) + b_storm), (n_storm + 1.0));
				}
				XX[it] = XX1 / XX2;
				qit[it] = 167.0 * XX[it] / 1000.0;
			}
			//
			NR1 = NR - 1;
			NR2 = NR + 1;
			qit[NR] = (qit[NR] + qit[NR - 1] + qit[NR + 1]) / 3.0;
			printStream.println();
			printStream.println("    it      dtnt      XX[it]     qit[it]");
			for (it = 0; it < NT; it++)
			{
				dtnt = dt * (float) (it);
				printStream.printf("%6d%10.2f%12.6f%12.6f", it, dtnt, XX[it], qit[it]);
				printStream.println();

			}
			printStream.println();
			// =====֥�Ӹ������--����=====
			// =====����ڵ㼯ˮ���sumAj[it][j]=====
			for (it = 0; it < NT; it++)
			{
				dtnt = dt + dt * (float) (it);
				for (j = 0; j < NN; j++)
				{
					sumAj[it][j] = Aj[j];
					sumqj[it][j] = Aj[j] * qit[it] * Acoef[j];
					for (i = 0; i < NN; i++)
					{
						if (sumTnode[i][j] > 0 && sumTnode[i][j] < dtnt)
						{
							sumAj[it][j] = sumAj[it][j] + Aj[i];
							sumqj[it][j] = sumqj[it][j] + Aj[i] * qit[it] * Acoef[i];
						}
					}
				}
			}
			// print sumAj[it][j] and sumqj[it][j]
			printStream.println("  sumAj[it][j]=");
			for (it = 0; it < NT; it++)
			{
				for (j = 0; j < NN; j++)
				{
					printStream.printf("%8.2f", sumAj[it][j]);
				}
				printStream.println();
			}
			printStream.println();
			printStream.println("  sumqj[it][j]=");
			for (it = 0; it < NT; it++)
			{
				for (j = 0; j < NN; j++)
				{
					printStream.printf("%8.2f", sumqj[it][j]);
				}
				printStream.println();
			}
			printStream.println();
			// -------------�ܶ�ˮ�����㿪ʼ--------------
			// ---------------------------------------------------------------
			for (it = 0; it < NT; it++)
			{
				for (i = 0; i < NN; i++)
				{
					overflow[it][i] = 0.0;
					Hw_over[it][i] = 0.0;
				}
			}
			for (it = 0; it < NT; it++)
			{
				for (j = 0; j < NP; j++)
				{
					qpt[it][j] = -99.0;
					qqkp[it][j] = 0.0;
				}
			}
			// ---------------------------------------------------------------
			for (it = 0; it < NT; it++)
			// --1--
			{
				printStream.print(" it=" + it + "  qpt[it][k]=");
				for (j = 0; j < NN; j++)
				{
					for (k = 0; k < NP; k++)
					{
						if (I0[k] == j)
						{
							qpt[it][k] = sumqj[it][j];
							// s.Format("%8.2lf",qpt[it][k]); outfile<<s;
							printStream.printf("%8.2f", qpt[it][k]);
						}
					}
				}
				printStream.println();
				// -------------------20090127-sql����------------------------
				for (ik = 0; ik < Nstart; ik++)
				// --2--
				{
					for (jk = 0; jk < Npline; jk++)
					// --3--
					{
						kp = Mbranch[ik][jk];
						if (kp >= 0)
						// --4--
						{
							if (J0[kp] == Nend)
							{
								Hwdw[it][kp] = Hw_end;
								if (Iprt == 1)
								{
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  Hw_end= " + Hw_end);
								}
							}
							else
							{
								for (k1 = 0; k1 < NP; k1++)
								{
									if (I0[k1] == J0[kp]) Hwdw[it][kp] = Hwup[it][k1];
								}
							}
							//
							Ad0 = 0.7854 * Math.pow(dpl[kp], 2.0);
							hdj0 = ZJdw[kp] + dpl[kp];
							if (Hwdw[it][kp] >= hdj0)
							{
								if (Iprt == 1)
								{
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  ��û���� ");
								}
								hdcc0[it][kp] = 1.0;
								rid[it][kp] = dpl[kp] / 4.0;
								vpt[it][kp] = qpt[it][kp] / Ad0;
								slopt[it][kp] = 10.29 * Math.pow(slp[kp], 2.0) * Math.pow(qpt[it][kp], 2.0) / Math.pow(dpl[kp], 5.333);
								Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
								if (Hwup[it][kp] >= Hj[I0[kp]])
								{
									Hwup[it][kp] = Hj[I0[kp]];
									slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
									if (slopt[it][kp] < 0.0)
									{
										slopt[it][kp] = Math.abs(slopt[it][kp]);
									}
									vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt[it][kp], 0.5) / slp[kp];
									qqkp[it][kp] = vpt[it][kp] * Ad0;
									if (qqkp[it][kp] < 0.0)
									{
										qqkp[it][kp] = Math.abs(qqkp[it][kp]);
									}
								}
							}
							else
							// --5--
							{
								if (Iprt == 1)
								{
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdw= " + Hwdw[it][kp] + "  ����û���� ");
								}
								// --20161018�޸Ŀ�ʼ---�����ٽ�ˮ����㷨-----------------------
								//
								qkpmax = 2.699 * Math.pow(dpl[kp], 2.5);
								if (qpt[it][kp] > qkpmax)
								{
									if (Iprt == 1)
									{
										printStream.println("   it= " + it + "   kp= " + kp + "  qkpmax= " + qkpmax + "  ����û���ܳ��� ");
									}
									vpt[it][kp] = qpt[it][kp] / Ad0;
									// H00=pow(vpt[it][kp],2.0)/13.72;
									// Hwdw[it][kp]=ZJdw[kp]+dpl[kp]+H00;
									Hwdw[it][kp] = ZJdw[kp] + dpl[kp];
									hdcc0[it][kp] = 1.0;
									rid[it][kp] = dpl[kp] / 4.0;
									slopt[it][kp] = 10.29 * Math.pow(slp[kp], 2.0) * Math.pow(qpt[it][kp], 2.0) / Math.pow(dpl[kp], 5.333);
									Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
									if (Hwup[it][kp] >= Hj[I0[kp]])
									{
										Hwup[it][kp] = Hj[I0[kp]];
										slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
										if (slopt[it][kp] < 0.0)
										{
											slopt[it][kp] = Math.abs(slopt[it][kp]);
										}
										vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt[it][kp], 0.5) / slp[kp];
										qqkp[it][kp] = vpt[it][kp] * Ad0;
										if (qqkp[it][kp] < 0.0)
										{
											qqkp[it][kp] = Math.abs(qqkp[it][kp]);
										}
									}
								}
								else
								{
									if (Iprt == 1)
									{
										printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  ����û�����ܳ��� ");
									}
									// ==20161115�޸�---���þ���������ˮ��򻯹�ʽ��ʼ--------
									ycd0 = 20.1538 * slp[kp] * qpt[it][kp] / Math.pow(dpl[kp], 2.6667) / Math.pow(slop[kp], 0.5);
									if (ycd0 <= 1.5)
									{
										hdcc0[it][kp] = 0.27 * Math.pow(ycd0, 0.485);
									}
									else
									{
										hdcc0[it][kp] = 0.098 * ycd0 + 0.19;
									}
									if (hdcc0[it][kp] > 1.0)
									{
										hdcc0[it][kp] = 1.0;
									}
									// ==20161115�޸�---���þ���������ˮ��򻯹�ʽ����--------
									sita = 2.0 * Math.acos(1.0 - 2.0 * hdcc0[it][kp]);
									rid[it][kp] = 0.25 * dpl[kp] * (sita - Math.sin(sita)) / sita;
									vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slop[kp], 0.5) / slp[kp];
								}
								// ---if(qpt[it][kp]>qkpmax)����---
								Hwdwkp = ZJdw[kp] + hdcc0[it][kp] * dpl[kp];
								if (Hwdwkp >= Hwdw[it][kp])
								{
									Hwdw[it][kp] = Hwdwkp;
								}
								else
								{
									yykp = Hwdw[it][kp] - ZJdw[kp];
									if (yykp > dpl[kp])
									{
										yykp = dpl[kp];
									}
									sita = 2.0 * Math.acos(1.0 - 2.0 * yykp / dpl[kp]);
									hdcc0[it][kp] = yykp / dpl[kp];
									rid[it][kp] = 0.25 * dpl[kp] * (sita - Math.sin(sita)) / sita;
									vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slop[kp], 0.5) / slp[kp];
								}
								Hwup[it][kp] = Hwdw[it][kp] + slop[kp] * lp[kp];
							} // 5--end
								// ------- ���it������ ----------
							if (Iprt == 1)
							{
								printStream.println("   it= " + it + "   kp= " + kp + "   I0[kp]= " + I0[kp] + "  Hwdm= " + Hwdw[it][kp] + "  Hwup= " + Hwup[it][kp] + "  Hj= " + Hj[I0[kp]] + "  hdcc0= " + hdcc0[it][kp] + "  qpt= " + qpt[it][kp] + "  qqkp= " + qqkp[it][kp] + "  vpt= " + vpt[it][kp]);
							}
						}// --4 if(kp>=0) end
					}// --3 ---jk end
				}// --2---ik end
				printStream.println();
				printStream.println("    it   �ܶκ�  I0   J0 �ܾ�dpl     �ܶ�qp ˮ���뾶R  ������ ����(m/s)  ����ˮλ  ����ˮλ  �Ϲܵ׸�  �¹ܵ׸�  �ܶ��¶�  �ϵ����");
				for (i = 0; i < NP; i++)
				{
					printStream.printf("%6d%6d%6d%5d%8.2f%12.3f%10.3f%8.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.5f%10.3f", it, i, I0[i], J0[i], dpl[i], qpt[it][i], rid[it][i], hdcc0[it][i], vpt[it][i], Hwup[it][i], Hwdw[it][i], ZJup[i], ZJdw[i], slop[i], Hj[I0[i]]);
					printStream.println();
				}
				printStream.println();
				// -------------- ��ʼ����ڵ�ˮλ-�ڵ��ˮ���ͻ�ˮ��� ---------------
				for (i = 0; i < NP; i++)
				{
					k = J0[i];
					if (k == Nend)
					{
						Hwj[it][k] = Hwdw[it][i];
					}
					{
						j = I0[i];
						Hwj[it][j] = Hwup[it][i];
						if (Hwup[it][i] == Hj[j])
						{
							overflow[it][j] = overflow[it - 1][j] + (qpt[it][i] - qqkp[it][i]) * dt * 60.0;
							Hw_over[it][j] = csf * overflow[it][j] / Aj[j] / 10000.0 * 1000.0;

						}
						if (Hwup[it][i] < Hj[j] && it > 0 && overflow[it - 1][j] > 0.0)
						{
							overflow[it][j] = overflow[it - 1][j] * 0.90;
							Hw_over[it][j] = csf * overflow[it][j] / Aj[j] / 10000.0 * 1000.0;
						}
					}
					if (it > NR && Hw_over[it][j] <= 5.0)
					{
						overflow[it][j] = 0.0;
						Hw_over[it][j] = 0.0;
					}

				}
				// ------------------ ���������ڵ���� ---------------
			}// 1-- it end ---
				// ----------------��Ļ����������------
			// System.out.println("------ ģ�ͼ���ȫ����� ------");
			// --------------------------------- ����ܶγ����ȼ����� ---------------
			// outfile<<" ======== ʱ�ιܶγ����� ========"<<endl;
			printStream.println(" ======== ʱ�ιܶγ����� ========");
			Nprt = NP / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NP)
					{
						iprt2 = NP;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
			}
			printStream.println("it=");
			for (it = 0; it < NT; it++)
			{
				if (it < 10)
				{
					printStream.print(" " + it + "   ");
				}
				else
				{
					printStream.print(it + "   ");
				}
				for (i = iprt1; i < iprt2; i++)
				{
					printStream.printf("%8.3f", hdcc0[it][i]);
				}
				printStream.println();
			}
			// ----------- ����ڵ�ˮλ������ ---------------
			printStream.println(" ======== ʱ�νڵ�ˮλ ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NN)
					{
						iprt2 = NN;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}

					for (i = iprt1; i < iprt2; i++)
					{
						printStream.printf("%8.2f", Hwj[it][i]);
					}
					printStream.println();
				}
			}
			// ***********��֯���ݣ�����ҳ��������ʾ********
			for (it = 0; it < NT; it++)
			{
				String WaterLevNew = "";
				for (i = 0; i < NN; i++)
				{
					if (gjId != null && i == SubgjId)
					{
						WaterAccGj += df1.format(Hwj[it][i]) + "|";
					}
					WaterLevNew += df1.format(Hwj[it][i]) + "|";
				}
				WaterLev[it] = WaterLevNew;
			}
			// *************************************

			// --------------- ����ڵ����������� ---------------

			printStream.println(" ======== ʱ�νڵ��ˮ��(m3) ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NN)
					{
						iprt2 = NN;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						if (overflow[it][i] <= 0.0)
						{
							printStream.print("        ");
						}
						else
						{
							printStream.printf("%8.2f", overflow[it][i]);
						}
					}
					printStream.println();
				}
			}
			// ***********��֯���ݣ�����ҳ��������ʾ********
			for (it = 0; it < NT; it++)
			{
				String WaterAccNew = "";
				for (i = 0; i < NN; i++)
				{
					if (overflow[it][i] <= 0.0)
					{
						WaterAccNew += 0 + "|";
					}
					else
					{
						WaterAccNew += df1.format(overflow[it][i]) + "|";
					}
				}
				WaterAcc[it] = WaterAccNew;
			}
			// *********************************************
			printStream.println(" ======== ʱ�νڵ��ˮ���(mm) ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NN)
					{
						iprt2 = NN;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						if (overflow[it][i] <= 0.0)
						{
							printStream.print("        ");
						}
						else
						{
							printStream.printf("%8.2f", Hw_over[it][i]);
						}
					}
					printStream.println();

				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return gjName + "," + rowCnt;
		}
		if (AnalogWaterType.equals("WaterAccGj"))
		{
			return WaterAccGj;
		}
		else if (AnalogWaterType.equals("WaterAcc"))
		{
			String WaterAccList = "";
			for (int i = 0; i < WaterAcc.length; i++)
			{
				WaterAccList += subSys.substring(7, 12) + WaterAcc[i] + ";";
			}
			return WaterAccList;
		}
		else if (AnalogWaterType.equals("WaterLev"))
		{
			String WaterLevList = "";
			for (int i = 0; i < WaterLev.length; i++)
			{
				WaterLevList += subSys.substring(7, 12) + WaterLev[i] + ";";
			}
			return WaterLevList;
		}
		return "";
	}

	// �����װ汾
	private String analog_Y5(String subSys, int timePeriod, String gjId, String gxId, String AnalogWaterType, double pSimu)
	{
		long startTime = System.currentTimeMillis();
		int SubgjId = 0;
		if (gjId != null)
		{
			SubgjId = CommUtil.StrToInt(gjId.substring(12, 15)) - 1;
		}
		int SubgxId = 0;
		if (gxId != null)
		{
			SubgxId = CommUtil.StrToInt(gxId.substring(5, 8)) - 1; // YJ001001
		}
		try
		{
			// �����������ݣ�
			// �ܶ������ڵ������ܵ��������·�����ܶ������м����MNP[NN][Ncol]������ģ��ʱ�����������ֵʱ��,�յ�ڵ�ţ��м������ָ�룬������ݱ�����
			int NP = 9, NN = 10, Nstart = 3, Npline = 7, Ncol = 6, NT = 60, NR = 23, Nend = 7, Iprt = 0, Nprtc = 20;
			// ���깫ʽ����shanghai storm water formular:
			// (A1+C*lgP)/(t+b)**n--ln(N)=2.303log(N)--����ˮλ��m��,���氼͹ϵ��csf��·�ظ߶�heage-mm
			double A1 = 20.12, C_storm = 0.639, b_storm = 11.945, n_storm = 0.825, dt = 2.0, Hw_end = 3.0, csf = 2.0, heage = 180;
			// P_simu=5.0,,rc=0.375
			// �ڵ��ˮ���(ha),�ڵ��ˮ������ϵ��,�ڵ�����ߣ�m��,�ڵ��������m3
			// �ڵ��ˮ���(ha)

			// ��ϵͳ�ܶ�����
			int[] I0; // ���νڵ��I0
			int[] J0; // ���νڵ��J0
			double[] lp; // �ܶγ���(m)
			double[] dpl; // �ܶ�ֱ��(m)
			double[] slp; // Ħ��ϵ��
			double[] ZJup; // ���ιܵ׸߳�ZJup[NP](m)
			double[] ZJdw; // ���ιܵ׸߳�ZJdw[NP](m)

			// ��ϵͳ�ڵ�����
			double[] Aj; // �ڵ��ˮ���(ha)
			double[] Hj; // �ڵ�����ߣ�m��
			double[] Acoef; // �ڵ��ˮ������ϵ��

			this.FileSaveRoute = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogData/";
			String XlsPath = "";
			if (gjId != null)
			{
				XlsPath = FileSaveRoute + gjId.substring(0, 12) + ".xls";
				gjName = gjId.substring(0, 12);
			}
			else
			{
				XlsPath = FileSaveRoute + subSys + ".xls";
				gjName = subSys;
			}
			InputStream is = new FileInputStream(XlsPath);
			Workbook rwb = Workbook.getWorkbook(is);
			Sheet rs = rwb.getSheet(0);
			int rsRows = rs.getRows();

			/*
			 * �������ݱ����ϵͳ�� �ڵ���NN �ܶ���NP �����NStart ·���ܶ���Npline ·���ڵ���Nr_node
			 * �յ���ں�Nend ģ��ʱ��NT �ܶ�·����NrouteYJ002 10 9 3 7 8 8 60 3
			 */
			rowCnt = 2;
			String sysName = rs.getCell(0, rowCnt).getContents().trim();
			NN = Integer.parseInt(rs.getCell(1, rowCnt).getContents().trim());
			NP = Integer.parseInt(rs.getCell(2, rowCnt).getContents().trim());
			Nstart = Integer.parseInt(rs.getCell(3, rowCnt).getContents().trim());
			Npline = Integer.parseInt(rs.getCell(4, rowCnt).getContents().trim());
			Nend = Integer.parseInt(rs.getCell(6, rowCnt).getContents().trim());
			NT = Integer.parseInt(rs.getCell(7, rowCnt).getContents().trim());
			Ncol = Integer.parseInt(rs.getCell(8, rowCnt).getContents().trim());
			Hw_end = Double.parseDouble(rs.getCell(9, rowCnt).getContents().trim());
			rowCnt += 4;

			I0 = new int[NP];
			J0 = new int[NP];
			lp = new double[NP];
			dpl = new double[NP];
			slp = new double[NP];
			ZJup = new double[NP];
			ZJdw = new double[NP];
			for (int j = 0; j < NP; j++)
			{
				I0[j] = Integer.parseInt(rs.getCell(1, rowCnt).getContents().trim());
				J0[j] = Integer.parseInt(rs.getCell(2, rowCnt).getContents().trim());
				lp[j] = Double.parseDouble(rs.getCell(3, rowCnt).getContents().trim());
				dpl[j] = Double.parseDouble(rs.getCell(4, rowCnt).getContents().trim());
				slp[j] = Double.parseDouble(rs.getCell(5, rowCnt).getContents().trim());
				ZJup[j] = Double.parseDouble(rs.getCell(6, rowCnt).getContents().trim());
				ZJdw[j] = Double.parseDouble(rs.getCell(7, rowCnt).getContents().trim());
				rowCnt++;
			}
			rowCnt += 3;

			Aj = new double[NN];
			Acoef = new double[NN];
			Hj = new double[NN];
			for (int j = 0; j < NN; j++)
			{
				Aj[j] = Double.parseDouble(rs.getCell(1, rowCnt).getContents().trim());
				Acoef[j] = Double.parseDouble(rs.getCell(2, rowCnt).getContents().trim());
				Hj[j] = Double.parseDouble(rs.getCell(3, rowCnt).getContents().trim());
				rowCnt++;
			}
			rowCnt += 3;

			// ===================
			// ----�м�ָ�����---
			int i00, j00 = 0, Ni1, jj, jp0, inp = 0, jpp, NPP;
			// ������ʼ�ڵ�ž���Mstart-�ڵ��������MNP-�м�任���� Npjun-������֧�߹ܶξ���Mbranch(��������)
			int[] Mstart = new int[Nstart];
			int[][] MNP = new int[NN][Ncol];
			int[] Npjun = new int[NP];
			int[][] Mbranch = new int[Nstart][Npline];
			// ----�м����----
			int i, ii, j, ik, it, jk, jjj, k, k1, kp, INS, in1, in2, in3, NR1, NR2, Nprt, iprt1, iprt2;
			double Ad0, Akp, qkpmax, Hwdwkp, ycd0, yykp, sita, sigh_kp, slopt0, P_simu, rc;
			double dtnt, taa, tbb, AA, XX1, XX2, TTQj, TTQout, hdj0;
			double[] XX = new double[NT];
			double[] qit = new double[NT];
			double[] Qj = new double[NN];
			double[] vp = new double[NP];
			double[] slop = new double[NP];
			double[][] qpt = new double[NT][NP];
			double[][] qqkp = new double[NT][NP];
			double[][] vpt = new double[NT][NP];
			double[][] rid = new double[NT][NP];
			double[][] slopt = new double[NT][NP];
			double[][] Hwup = new double[NT][NP];
			double[][] Hwdw = new double[NT][NP];
			double[][] hdcc0 = new double[NT][NP];
			double[][] overflow = new double[NT][NN];
			double[][] Hw_over = new double[NT][NN];
			double[][] Hwj = new double[NT][NN];
			double[] TQj = new double[NT];
			double[] Toverf = new double[NT];

			WaterAcc = new String[NT];
			WaterLev = new String[NT];
			WaterFlowLoad = "";
			WaterActualFlow = "";
			WaterFlowRate = "";

			WaterAccGj = "";
			String FileName = "";
			if (gjId != null)
			{
				FileName = gjId.substring(0, 12) + ".txt";
			}
			else
			{
				FileName = subSys + ".txt";
			}
			String FilePath = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogValue/";
			FileOutputStream fs = new FileOutputStream(new File(FilePath + FileName));
			PrintStream printStream = new PrintStream(fs);
			printStream.println(FileName);

			DecimalFormat df = new DecimalFormat("##.####");
			DecimalFormat df1 = new DecimalFormat("######.##");

			/*
			 * System.out.println("�����뽵��ǿ��������P���꣩:"); Scanner input = new
			 * Scanner(System.in); P_simu = input.nextDouble();
			 */

			// ���ý���ǿ��
			P_simu = pSimu;

			printStream.println("===  �����ڣ� " + P_simu + "  ��     ʱ������ " + NT + "     �յ�ˮλ�� " + Hw_end + "  m  ===");
			// System.out.println();
			// System.out.println("pipe no.  I0    J0      lp     dpl     slp    ZJup    ZJdw");
			for (i = 0; i < NP; i++)
			{
				// System.out.printf("%6d%6d%6d%8.2f%8.2f%8.3f%8.2f%8.2f", i,
				// I0[i], J0[i], lp[i], dpl[i], slp[i], ZJup[i], ZJdw[i]);
				// System.out.println();
			}
			// System.out.println();
			// System.out.println("===  �����ڣ� " + P_simu + "  ��     ʱ������ " + NT +
			// "     �յ�ˮλ�� " + Hw_end + "  m  ===");
			printStream.println();
			printStream.println("pipe no.  I0    J0      lp     dpl     slp    ZJup    ZJdw");
			for (i = 0; i < NP; i++)
			{
				printStream.printf("%6d%6d%6d%8.2f%8.2f%8.3f%8.2f%8.2f", i, I0[i], J0[i], lp[i], dpl[i], slp[i], ZJup[i], ZJdw[i]);
				printStream.println();
			}
			printStream.println();
			// ================= ����slop[k] ===========
			for (k = 0; k < NP; k++)
			{
				slop[k] = (ZJup[k] - ZJdw[k]) / lp[k];
			}
			// ====20161106===== ���ɾ��� MNP[i][j] ====
			for (i = 0; i < NN; i++)
			{
				for (j = 0; j < Ncol; j++)
				{
					MNP[i][j] = 0;
				}
				MNP[i][0] = i;
				jj = 2;
				for (k = 0; k < NP; k++)
				{
					if (J0[k] == i)
					{
						jj = jj + 1;
						MNP[i][1] = MNP[i][1] + 1;
						MNP[i][jj] = k;
					}
					if (I0[k] == i)
					{
						MNP[i][2] = MNP[i][2] + 1;
					}
				}
			}
			// outfile<<"===========  print MNP[i][j]"<<endl;
			printStream.println("===========  print MNP[i][j]");
			for (i = 0; i < NN; i++)
			{
				for (j = 0; j < Ncol; j++)
				{
					printStream.printf("%6d", MNP[i][j]);
				}
				printStream.println();
			}
			// ----- MNP[i][j] ���� ------
			// ====20161112===== ���ɾ��� Mstart[i] ====
			jj = -1;
			for (i = 0; i < NN; i++)
			{
				if (MNP[i][1] == 0)
				{
					jj = jj + 1;
					Mstart[jj] = i;
				}
			}
			//
			// outfile<<"===========  print Mstart[i]"<<endl;
			printStream.println("===========  print Mstart[i]");
			for (i = 0; i < Nstart; i++)
			{
				printStream.printf("%6d", Mstart[i]);
			}
			printStream.println();
			// ====20161106===== ���ɾ���Mbranch[i][j] ====
			for (i = 0; i < NP; i++)
			{
				Npjun[i] = 1;
			}
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Npline; j++)
				{
					Mbranch[i][j] = -99;
				}
			}
			i00 = -1;
			NPP = 0;
			// L200:
			while (true)
			{
				for (i = 0; i < NN; i++)
				{
					if (MNP[i][2] == 0 && MNP[i][1] > 0)
					{
						jj = 2;
						Ni1 = MNP[i][1];
						for (j = 0; j < Ni1; j++)
						{
							jj = jj + 1;
							jp0 = MNP[i][jj];
							if (Npjun[jp0] > 0)
							{
								i00 = i00 + 1;
								j00 = 0;
								Mbranch[i00][j00] = jp0;
								inp = I0[jp0];
								Npjun[jp0] = -99;
								NPP = NPP + 1;
							}
							// L100:
							while (true)
							{
								INS = 1;
								for (jjj = 0; jjj < Nstart; jjj++)
								{
									if (Mstart[jjj] == inp) INS = 0;
								}
								if (INS > 0)
								{
									for (jpp = 0; jpp < NP; jpp++)
									{
										if (J0[jpp] == inp && Npjun[jpp] > 0)
										{
											j00 = j00 + 1;
											Mbranch[i00][j00] = jpp;
											inp = I0[jpp];
											Npjun[jpp] = -99;
											NPP = NPP + 1;
											break;
											// goto L100;
										}
										else
										{
											continue;
										}
									}
								} // --- end of if(INS>0) ---
								else
								{
									break;
								}
							}
						} // --- end of for(j=0;j<Ni1;j++) ---
					} // --- end of if(MNP[i][2]==0 && MNP[i][1]>0) ---
					MNP[i][2] = -99;
				}// --- end of for(i=0;i<NN;1++) ---
				for (i = 0; i < NN; i++)
				{
					for (j = 0; j < NP; j++)
					{
						if (I0[j] == i && Npjun[j] < 0)
						{
							MNP[i][2] = 0;
						}
					}
				}
				if (NPP >= NP)
				{
					break;
					// goto L200;

				}
			}
			// === ���ɾ��� Mbranch[i][j] ����====
			printStream.println();
			printStream.println("===========  print Mbranch[i][j]");
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Npline; j++)
				{
					printStream.printf("%6d", Mbranch[i][j]);
				}
				printStream.println();
			}
			//
			// ================= ����׼��̬ˮ��ģ��============================
			//
			// ----------------�ڵ��ˮ���(ha)�ͻ�ˮ����(m3/sec)����----------
			//
			// ֥�Ӹ������--rainfall intensity at every time step--
			AA = A1 + A1 * C_storm * Math.log(P_simu) / 2.303;
			rc = (float) (NR) / (float) (NT);
			for (it = 0; it < NT; it++)
			{
				if (it <= NR)
				{
					dtnt = dt * (float) (it);
					tbb = dt * (float) (NR) - dtnt;
					XX1 = AA * ((1.0 - n_storm) * tbb / rc + b_storm);
					XX2 = Math.pow((tbb / rc + b_storm), (n_storm + 1.0));
				}
				else
				{
					dtnt = dt * (float) (it);
					taa = dtnt - dt * (float) (NR);
					XX1 = AA * ((1.0 - n_storm) * taa / (1.0 - rc) + b_storm);
					XX2 = Math.pow((taa / (1.0 - rc) + b_storm), (n_storm + 1.0));
				}
				XX[it] = XX1 / XX2;
				qit[it] = 167.0 * XX[it] / 1000.0;
			}
			//
			NR1 = NR - 1;
			NR2 = NR + 1;
			qit[NR] = (qit[NR] + qit[NR - 1] + qit[NR + 1]) / 3.0;
			// ����ƽ������ǿ��mm/min
			XX1 = 0;
			for (it = 0; it < NT; it++)
			{
				XX1 = XX1 + XX[it];
			}
			XX1 = XX1 / (float) (NT);
			// ���깫ʽ����ǿ��mm/min
			taa = dt * (float) (NT) + b_storm;
			XX2 = AA / Math.pow(taa, n_storm);
			printStream.println();
			printStream.println(" ====== ����ǿ���������ݽ�� ======   ƽ��ǿ��XX1= " + XX1 + "(mm/min)   ��ʽǿ��XX2= " + XX2 + "(mm/min)" + "    rc= " + rc);
			printStream.println("    it      dtnt XX[it](mm/min) qit[it](m3/ha-sec)");
			for (it = 0; it < NT; it++)
			{
				dtnt = dt * (float) (it);
				printStream.printf("%6d%10.2f%12.4f%15.4f", it, dtnt, XX[it], qit[it]);
				printStream.println();
			}
			printStream.println();
			// ============֥�Ӹ������--����=============
			// -------------�ܶ�ˮ�����㿪ʼ--------------
			for (it = 0; it < NT; it++)
			{
				for (i = 0; i < NN; i++)
				{
					overflow[it][i] = 0.0;
					Hw_over[it][i] = 0.0;
				}
			}
			for (it = 0; it < NT; it++)
			{
				for (j = 0; j < NP; j++)
				{
					qpt[it][j] = -99.0;
					qqkp[it][j] = 0.0;
				}
			}
			// ---------------------------------------------------------------
			for (it = 0; it < NT; it++)
			{ // --1--
				// ----------����ܶ�����------------
				if (it == 0)
				{
					for (i = 0; i < NN; i++)
					{
						Qj[i] = Aj[i] * qit[it] * Acoef[i];
					}
					for (j = 0; j < NP; j++)
					{
						for (i = 0; i < NN; i++)
						{
							if (I0[j] == i) qpt[it][j] = Qj[i];
						}
					}
				}
				else
				{
					for (i = 0; i < NN; i++)
					{
						Qj[i] = Aj[i] * qit[it] * Acoef[i];
					}
					for (j = 0; j < NP; j++)
					{
						for (i = 0; i < NN; i++)
						{
							if (I0[j] == i) qpt[it][j] = Qj[i];
						}
					}
					for (j = 0; j < NP; j++)
					{
						for (k = 0; k < NP; k++)
						{
							if (J0[k] == I0[j]) qpt[it][j] = qpt[it][j] + qqkp[it - 1][k];
							// if (J0[k] == I0[j]) qpt[it][j] = qpt[it][j] +
							// qpt[it - 1][k];
						}
					}
				}
				for (j = 0; j < NP; j++)
				{
					qqkp[it][j] = qpt[it][j];
				}
				printStream.print(" it=" + it + "  qpt[it][k]=");
				for (k = 0; k < NP; k++)
				{
					printStream.printf("%8.4f", qpt[it][k]);
				}
				printStream.println();
				// -------------------20090127-sqliu------------------------
				for (ik = 0; ik < Nstart; ik++)
				// --2--
				{
					for (jk = 0; jk < Npline; jk++)
					// --3--
					{
						kp = Mbranch[ik][jk];
						if (kp >= 0)
						// --4--
						{
							if (J0[kp] == Nend)
							{
								Hwdw[it][kp] = Hw_end;
								if (Iprt == 1)
								{
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  Hw_end= " + Hw_end);
								}
							}
							else
							{
								for (k1 = 0; k1 < NP; k1++)
								{
									if (I0[k1] == J0[kp]) Hwdw[it][kp] = Hwup[it][k1];
								}
							}
							Ad0 = 0.7854 * Math.pow(dpl[kp], 2.0);
							hdj0 = ZJdw[kp] + dpl[kp];
							if (Hwdw[it][kp] >= hdj0)
							{
								if (Iprt == 1)
								{
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  ��û���� ");
								}
								hdcc0[it][kp] = 1.0;
								rid[it][kp] = dpl[kp] / 4.0;
								vpt[it][kp] = qpt[it][kp] / Ad0;
								slopt[it][kp] = 10.29 * Math.pow(slp[kp], 2.0) * Math.pow(qpt[it][kp], 2.0) / Math.pow(dpl[kp], 5.333);
								Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
								if (Hwup[it][kp] >= Hj[I0[kp]])
								{
									Hwup[it][kp] = Hj[I0[kp]];
									slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
									sigh_kp = 1.0;
									slopt0 = slopt[it][kp];
									if (slopt[it][kp] < 0.0)
									{
										slopt0 = -slopt0;
										sigh_kp = -1.0;
									}
									vpt[it][kp] = sigh_kp * Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt0, 0.5) / slp[kp];
									qqkp[it][kp] = vpt[it][kp] * Ad0;
								}
								// -------20161213start--------
								if (it > 0 && Hwup[it][kp] < Hj[I0[kp]] && overflow[it - 1][I0[kp]] > 0.0)
								{
									// xxxxxxxxxxxxxxxxxxxxxxxxx 20170417�޸Ŀ�ʼ
									// xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
									Hwup[it][kp] = (Hwup[it][kp] + Hj[I0[kp]]) / 2.0;
									slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
									sigh_kp = 1.0;
									slopt0 = slopt[it][kp];
									if (slopt[it][kp] < 0.0)
									{
										slopt0 = -slopt0;
										sigh_kp = -1.0;
									}
									vpt[it][kp] = sigh_kp * Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt0, 0.5) / slp[kp];
									qqkp[it][kp] = vpt[it][kp] * Ad0;
								}
							}
							else
							// --5--
							{
								if (Iprt == 1)
								{
									// outfile<<"   it= "<<it<<"   kp= "<<kp<<"  Hwdw= "<<Hwdw[it][kp]<<"  ����û���� "<<endl;
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdw= " + Hwdw[it][kp] + "  ����û���� ");
								}
								//
								// --20161018---�����ٽ�ˮ��------------
								//
								qkpmax = 2.699 * Math.pow(dpl[kp], 2.5);
								if (qpt[it][kp] > qkpmax)
								{
									if (Iprt == 1)
									{
										// outfile<<"   it= "<<it<<"   kp= "<<kp<<"  qkpmax= "<<qkpmax<<"  ����û���ܳ��� "<<endl;
										printStream.println("   it= " + it + "   kp= " + kp + "  qkpmax= " + qkpmax + "  ����û���ܳ��� ");
									}
									vpt[it][kp] = qpt[it][kp] / Ad0;
									Hwdw[it][kp] = ZJdw[kp] + dpl[kp];
									hdcc0[it][kp] = 1.0;
									rid[it][kp] = dpl[kp] / 4.0;
									slopt[it][kp] = 10.29 * Math.pow(slp[kp], 2.0) * Math.pow(qpt[it][kp], 2.0) / Math.pow(dpl[kp], 5.333);
									Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
									//
									if (Hwup[it][kp] >= Hj[I0[kp]])
									{
										Hwup[it][kp] = Hj[I0[kp]];
										slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
										//
										sigh_kp = 1.0;
										slopt0 = slopt[it][kp];
										if (slopt[it][kp] < 0.0)
										{
											slopt0 = -slopt0;
											sigh_kp = -1.0;
										}
										vpt[it][kp] = sigh_kp * Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt0, 0.5) / slp[kp];
										qqkp[it][kp] = vpt[it][kp] * Ad0;
									}
									//
									if (it > 0 && Hwup[it][kp] < Hj[I0[kp]] && overflow[it - 1][I0[kp]] > 0.0)
									{
										Hwup[it][kp] = (Hwup[it][kp] + Hj[I0[kp]]) / 2.0;
										slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
										sigh_kp = 1.0;
										slopt0 = slopt[it][kp];
										if (slopt[it][kp] < 0.0)
										{
											slopt0 = -slopt0;
											sigh_kp = -1.0;
										}
										vpt[it][kp] = sigh_kp * Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt0, 0.5) / slp[kp];
										qqkp[it][kp] = vpt[it][kp] * Ad0;
									}
									//
								}
								else
								{
									if (Iprt == 1)
									{
										// outfile<<"   it= "<<it<<"   kp= "<<kp<<"  Hwdw= "<<Hwdw[it][kp]<<"  ����û�����ܳ��� "<<endl;
										printStream.println("   it= " + it + "   kp= " + kp + "  Hwdw= " + Hwdw[it][kp] + "  ����û�����ܳ��� ");
									}
									if (slop[kp] < 0.0001)
									{
										slop[kp] = 0.0001;
									}
									// ----����ˮ��----
									if (qpt[it][kp] >= 0.0)
									{
										ycd0 = 20.1538 * slp[kp] * qpt[it][kp] / Math.pow(dpl[kp], 2.6667) / Math.pow(slop[kp], 0.5);
										if (ycd0 <= 1.5)
										{
											hdcc0[it][kp] = 0.27 * Math.pow(ycd0, 0.485);
										}
										else
										{
											hdcc0[it][kp] = 0.098 * ycd0 + 0.19;
										}
										if (hdcc0[it][kp] <= 0.0001)
										{
											hdcc0[it][kp] = 0.0001;
										}
									}
									else
									{
										if (it == 0)
										{
											hdcc0[it][kp] = 0.001;
										}
										if (it > 0)
										{
											hdcc0[it][kp] = hdcc0[it - 1][kp];
										}
									}
									if (hdcc0[it][kp] > 1.0)
									{
										hdcc0[it][kp] = 1.0;
									}
									//
									hdj0 = ZJdw[kp] + hdcc0[it][kp] * dpl[kp];
									if (hdj0 < Hwdw[it][kp])
									{
										hdcc0[it][kp] = (Hwdw[it][kp] - ZJdw[kp]) / dpl[kp];
									}
									//
									sita = 2.0 * Math.acos(1.0 - 2.0 * hdcc0[it][kp]);
									rid[it][kp] = 0.25 * dpl[kp] * (sita - Math.sin(sita)) / sita;
									Akp = Math.pow(dpl[kp], 2.0) * (sita - Math.sin(sita)) / 8.0;
									vpt[it][kp] = qpt[it][kp] / Akp;
									Hwdw[it][kp] = ZJdw[kp] + hdcc0[it][kp] * dpl[kp];
									slopt[it][kp] = Math.pow(slp[kp], 2.0) * Math.pow(vpt[it][kp], 2.0) / Math.pow(rid[it][kp], 1.333);
									Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
									//
									if (Hwup[it][kp] >= Hj[I0[kp]])
									{
										Hwup[it][kp] = Hj[I0[kp]];
										slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
										sigh_kp = 1.0;
										slopt0 = slopt[it][kp];
										if (slopt[it][kp] < 0.0)
										{
											slopt0 = -slopt0;
											sigh_kp = -1.0;
										}
										vpt[it][kp] = sigh_kp * Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt0, 0.5) / slp[kp];
										qqkp[it][kp] = vpt[it][kp] * Ad0;
									}
									//
									if (it > 0 && Hwup[it][kp] < Hj[I0[kp]] && overflow[it - 1][I0[kp]] > 0.0)
									//
									{
										Hwup[it][kp] = (Hwup[it][kp] + Hj[I0[kp]]) / 2.0;
										slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
										sigh_kp = 1.0;
										slopt0 = slopt[it][kp];
										if (slopt[it][kp] < 0.0)
										{
											slopt0 = -slopt0;
											sigh_kp = -1.0;
										}
										vpt[it][kp] = sigh_kp * Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt0, 0.5) / slp[kp];
										qqkp[it][kp] = vpt[it][kp] * Ad0;
									}
								}
							}
							// 5--end
							// xxxxxxxxxxxxxxxxxxxxxxxxx 20170417�޸Ľ���
							// xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

							// ------- ���it������ ----------
							if (Iprt == 1)
							{
								printStream.println("   it= " + it + "   kp= " + kp + "   I0[kp]= " + I0[kp] + "  Hwdm= " + Hwdw[it][kp] + "  Hwup= " + Hwup[it][kp] + "  Hj= " + Hj[I0[kp]] + "  hdcc0= " + hdcc0[it][kp] + "  qpt= " + qpt[it][kp] + "  qqkp= " + qqkp[it][kp] + "  vpt= " + vpt[it][kp]);
							}
						}// --4 if(kp>=0) end
					}// --3 ---jk end
				}// --2---ik end
					// -------------- ����ڵ�ˮλ-�ڵ��ˮ���ͻ�ˮ��� ---------------
				for (i = 0; i < NP; i++)
				{
					k = J0[i];
					if (k == Nend)
					{
						Hwj[it][k] = Hw_end;
					}
					// **********20170306
					j = I0[i];
					Hwj[it][j] = Hwup[it][i];
					if (it > 0)
					{
						overflow[it][j] = overflow[it - 1][j] + (qpt[it][i] - qqkp[it][i]) * dt * 60.0;
						Hw_over[it][j] = csf * overflow[it][j] / Aj[j] / 10000.0 * 1000.0;
						if (Hw_over[it][j] > heage)
						{
							Hw_over[it][j] = heage + csf * (overflow[it][j] - Aj[j] * heage / 1000.0) / 3.0 / 10000.0 * 1000.0;
						}
						if (it > NR && Hw_over[it][j] <= 5.0)
						{
							overflow[it][j] = 0.0;
							Hw_over[it][j] = 0.0;
						}
					}
				}
				// �޸Ľ���
				printStream.println();
				printStream.println("    it   �ܶκ�  I0   J0 �ܾ�dpl    �ܶ�qpt ˮ���뾶R    ������ ����(m/s)  ����ˮλ  ����ˮλ  �Ϲܵ׸�  �¹ܵ׸�  �ܶ��¶�  �ϵ����  ˮ���¶�    qqkp");
				for (i = 0; i < NP; i++)
				{
					printStream.printf("%6d%6d%6d%5d%8.2f%12.4f%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.4f%10.4f", it, i, I0[i], J0[i], dpl[i], qpt[it][i], rid[it][i], hdcc0[it][i], vpt[it][i], Hwup[it][i], Hwdw[it][i], ZJup[i], ZJdw[i], slop[i], Hj[I0[i]], slopt[it][i], qqkp[it][i]);
					printStream.println();
				}
				printStream.println();
				// ------------ ���������ڵ���� ----
				TQj[it] = 0;
				Toverf[it] = 0;
				for (i = 0; i < NN; i++)
				{
					TQj[it] = TQj[it] + Qj[i];
					Toverf[it] = Toverf[it] + overflow[it][i];
				}
				printStream.println();
				printStream.println("  TQj[it]= " + TQj[it] + " m3/sec     Toverf[it]=  " + Toverf[it] + " m3  ");
				printStream.println();
			}
			// --------------��Ļ����������------

			// xxxxxxx20170416-ʱ������ƽ��ֵ��ʼxxxxxxxxxxxxx
			// -----20170416-ʱ������ƽ��ֵ��ʼ---------------
			for (i = 0; i < NN; i++)
			{
				for (it = 2; it < NT - 2; it++)
				{
					overflow[it][i] = (overflow[it - 2][i] + overflow[it - 1][i] + overflow[it][i] + overflow[it + 1][i] + overflow[it + 2][i]) / 5.0;
					Hw_over[it][i] = (Hw_over[it - 2][i] + Hw_over[it - 1][i] + Hw_over[it][i] + Hw_over[it + 1][i] + Hw_over[it + 2][i]) / 5.0;
					Hwj[it][i] = (Hwj[it - 2][i] + Hwj[it - 1][i] + Hwj[it][i] + Hwj[it + 1][i] + Hwj[it + 2][i]) / 5.0;
				}
				// xxxx2017-04-19 ����
				for (it = NT - 2; it < NT; it++)
				{
					if (overflow[it][i] > overflow[NT - 3][i])
					{
						overflow[it][i] = overflow[NT - 3][i];
					}
					if (Hw_over[it][i] > Hw_over[NT - 3][i])
					{
						Hw_over[it][i] = Hw_over[NT - 3][i];
					}
					if (Hwj[it][i] > Hwj[NT - 3][i])
					{
						Hwj[it][i] = Hwj[NT - 3][i];
					}
				}
				// xxxx
			}
			for (i = 0; i < NP; i++)
			{
				for (it = 2; it < NT - 2; it++)
				{
					qqkp[it][i] = (qqkp[it - 2][i] + qqkp[it - 1][i] + qqkp[it][i] + qqkp[it + 1][i] + qqkp[it + 2][i]) / 5.0;
					qpt[it][i] = (qpt[it - 2][i] + qpt[it - 1][i] + qpt[it][i] + qpt[it + 1][i] + qpt[it + 2][i]) / 5.0;
					vpt[it][i] = (vpt[it - 2][i] + vpt[it - 1][i] + vpt[it][i] + vpt[it + 1][i] + vpt[it + 2][i]) / 5.0;
					hdcc0[it][i] = (hdcc0[it - 2][i] + hdcc0[it - 1][i] + hdcc0[it][i] + hdcc0[it + 1][i] + hdcc0[it + 2][i]) / 5.0;
				}
				// xxxx2017-04-19 ����
				for (it = NT - 2; it < NT; it++)
				{
					if (qqkp[it][i] > qqkp[NT - 3][i])
					{
						qqkp[it][i] = qqkp[NT - 3][i];
					}
					if (qpt[it][i] > qpt[NT - 3][i])
					{
						qpt[it][i] = qpt[NT - 3][i];
					}
					if (vpt[it][i] > vpt[NT - 3][i])
					{
						vpt[it][i] = vpt[NT - 3][i];
					}
					if (hdcc0[it][i] > hdcc0[NT - 3][i])
					{
						hdcc0[it][i] = hdcc0[NT - 3][i];
					}
				}
				// xxxx
			}
			//
			// -----20170416-ʱ������ƽ��ֵ����---------------
			// xxxxxxx20170416-ʱ������ƽ��ֵ����xxxxxxxxxxxxx

			// --------------- ����ܶγ����ȼ����� ---------------
			printStream.println(" ======== ʱ�ιܶγ����� ========");
			Nprt = NP / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NP)
					{
						iprt2 = NP;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						printStream.printf("%8.3f", hdcc0[it][i]);
					}
					printStream.println();
				}
			}
			// ----------------- ����ڵ�ˮλ������ ---------------
			printStream.println(" ======== ʱ�νڵ�ˮλ ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NN)
					{
						iprt2 = NN;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						printStream.printf("%8.2f", Hwj[it][i]);
					}
					printStream.println();
				}
			}
			// ***********��֯���ݣ�����ҳ��������ʾ********
			for (it = 0; it < NT; it++)
			{
				String WaterLevNew = "";
				for (i = 0; i < NN; i++)
				{
					if (gjId != null && i == SubgjId)
					{
						WaterAccGj += df1.format(Hwj[it][i]) + "|";
					}
					WaterLevNew += df1.format(Hwj[it][i]) + "|";
				}
				WaterLev[it] = WaterLevNew;
			}
			// *************************************
			// -------------------- ����ڵ����������� ---------------
			printStream.println(" ======== ʱ�νڵ��ˮ��(m3) ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				iprt1 = ii * Nprtc;
				iprt2 = iprt1 + Nprtc;
				if (iprt2 > NN)
				{
					iprt2 = NN;
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						if (overflow[it][i] <= 0.1)
						{
							printStream.print("        ");
						}
						else
						{
							printStream.printf("%8.2f", overflow[it][i]);
						}
					}
					printStream.println();
				}
			}
			// ***********��֯���ݣ�����ҳ��������ʾ********
			for (it = 0; it < NT; it++)
			{
				String WaterAccNew = "";
				for (i = 0; i < NN; i++)
				{
					if (overflow[it][i] <= 0.0)
					{
						WaterAccNew += 0 + "|";
					}
					else
					{
						WaterAccNew += df1.format(overflow[it][i]) + "|";
					}
				}
				WaterAcc[it] = WaterAccNew;
			}
			// *********************************************
			printStream.println(" ======== ʱ�νڵ��ˮ���(mm) ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				iprt1 = ii * Nprtc;
				iprt2 = iprt1 + Nprtc;
				if (iprt2 > NN)
				{
					iprt2 = NN;
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println();
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						if (Hw_over[it][i] < 5.0)
						{
							printStream.print("        ");
						}
						else
						{
							printStream.printf("%8.2f", Hw_over[it][i]);
						}
					}
					printStream.println();
				}
			}
			// ***********��֯���ݣ�����ҳ��������ʾ*****20170120***
			for (it = 0; it < NT; it++)
			{
				for (i = 0; i < NP; i++)
				{
					if (gjId != null && gxId != null && i == SubgxId)
					{
						WaterFlowLoad += df1.format(qpt[it][i]) + "|";
						WaterActualFlow += df1.format(qqkp[it][i]) + "|";
						WaterFlowRate += df1.format(vpt[it][i]) + "|";
					}
				}
			}
			// *********************************************

			printStream.println("------ ģ�ͼ������ ------");
			long endTime = System.currentTimeMillis() - startTime;
			
			System.out.println("��ϵͳ["+subSys+"]["+NN+"]["+ endTime +"]");
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			CommUtil.PRINT_ERROR(e.getMessage());
			return gjName + "," + "NumberFormat" + "," + (rowCnt + 1);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
			CommUtil.PRINT_ERROR(e.getMessage());
			return gjName + "," + "ArrayIndexOut" + "," + "";
		}
		catch (Exception e)
		{
			e.printStackTrace();
			CommUtil.PRINT_ERROR(e.getMessage());
			return gjName + "," + "unknown" + "," + (rowCnt + 1);
		}
		if (AnalogWaterType.equals("WaterAccGj"))
		{
			return WaterAccGj;
		}
		else if (AnalogWaterType.equals("WaterAcc"))
		{
			String WaterAccList = "";
			for (int i = 0; i < WaterAcc.length; i++)
			{
				WaterAccList += subSys.substring(7, 12) + WaterAcc[i] + ";";
			}
			return WaterAccList;
		}
		else if (AnalogWaterType.equals("WaterLev"))
		{
			String WaterLevList = "";
			for (int i = 0; i < WaterLev.length; i++)
			{
				WaterLevList += subSys.substring(7, 12) + WaterLev[i] + ";";
			}
			return WaterLevList;
		}
		else if (AnalogWaterType.equals("WaterFlowLoad"))
		{
			return WaterFlowLoad;
		}
		else if (AnalogWaterType.equals("WaterActualFlow"))
		{
			return WaterActualFlow;
		}
		else if (AnalogWaterType.equals("WaterFlowRate"))
		{
			return WaterFlowRate;
		}
		return "";
	}

	// �����װ汾 2017-04-18����
	private String analog_Y6(String subSys, int timePeriod, String gjId, String gxId, String AnalogWaterType, double pSimu)
	{
		int SubgjId = 0;
		if (gjId != null)
		{
			SubgjId = CommUtil.StrToInt(gjId.substring(12, 15)) - 1;
		}
		int SubgxId = 0;
		if (gxId != null)
		{
			SubgxId = CommUtil.StrToInt(gxId.substring(5, 8)) - 1; // YJ001001
		}
		try
		{
			// �����������ݣ�
			// �ܶ������ڵ������ܵ��������·�����ܶ������м����MNP[NN][Ncol]������ģ��ʱ�����������ֵʱ��,�յ�ڵ�ţ��м������ָ�룬������ݱ�����
			int NP = 9, NN = 10, Nstart = 3, Npline = 7, Ncol = 6, NT = 60, NR = 23, Nend = 7, Iprt = 0, Nprtc = 20;
			// ���깫ʽ����shanghai storm water formular:
			// (A1+C*lgP)/(t+b)**n--ln(N)=2.303log(N)--����ˮλ��m��,���氼͹ϵ��csf��·�ظ߶�heage-mm
			double A1 = 20.12, C_storm = 0.639, b_storm = 11.945, n_storm = 0.825, dt = 2.0, Hw_end = 3.0, csf = 2.0, heage = 180;
			// P_simu=5.0,,rc=0.375
			// �ڵ��ˮ���(ha),�ڵ��ˮ������ϵ��,�ڵ�����ߣ�m��,�ڵ��������m3
			// �ڵ��ˮ���(ha)

			// ��ϵͳ�ܶ�����
			int[] I0; // ���νڵ��I0
			int[] J0; // ���νڵ��J0
			double[] lp; // �ܶγ���(m)
			double[] dpl; // �ܶ�ֱ��(m)
			double[] slp; // Ħ��ϵ��
			double[] ZJup; // ���ιܵ׸߳�ZJup[NP](m)
			double[] ZJdw; // ���ιܵ׸߳�ZJdw[NP](m)

			// ��ϵͳ�ڵ�����
			double[] Aj; // �ڵ��ˮ���(ha)
			double[] Hj; // �ڵ�����ߣ�m��
			double[] Acoef; // �ڵ��ˮ������ϵ��

			/*
			 * double[] Aj = new double[] { 0.15, 0.15, 0.15, 0.15, 0.15, 0.15,
			 * 0.15, 0.15, 0.15, 0.15 }; // �ڵ��ˮ������ϵ�� double[] Acoef = new
			 * double[] { 0.62, 0.62, 0.62, 0.62, 0.62, 0.62, 0.62, 0.62, 0.62,
			 * 0.62 }; // �ڵ�����ߣ�m�� double[] Hj = new double[] { 5.24, 5.19,
			 * 5.18, 5.00, 5.21, 5.20, 5.20, 5.12, 5.13, 5.18 }; //
			 * �ܶ����νڵ��I0,���νڵ��J0�����ιܵ׸߳�ZJup[NP](m)�����ιܵ׸߳�ZJdw[NP](m) int[] I0 =
			 * new int[] { 0, 1, 2, 3, 4, 5, 6, 8, 9 }; int[] J0 = new int[] {
			 * 1, 2, 3, 4, 5, 6, 7, 7, 6 }; double[] ZJup = new double[] { 3.89,
			 * 3.84, 3.78, 3.73, 3.68, 3.64, 3.60, 3.73, 3.88 }; double[] ZJdw =
			 * new double[] { 3.84, 3.78, 3.73, 3.68, 3.64, 3.60, 3.55, 3.60,
			 * 3.70 }; // �ܶγ���(m),�ܶ�ֱ��(m),Ħ��ϵ�� double[] lp = new double[] { 50,
			 * 50, 50, 50, 50, 50, 50, 50, 50 }; double[] dpl = new double[] {
			 * 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3 }; double[] slp = new
			 * double[] { 0.014, 0.014, 0.014, 0.014, 0.014, 0.014, 0.014,
			 * 0.014, 0.014 };
			 */

			this.FileSaveRoute = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogData/";
			String XlsPath = "";
			if (gjId != null)
			{
				XlsPath = FileSaveRoute + gjId.substring(0, 12) + ".xls";
				gjName = gjId.substring(0, 12);
			}
			else
			{
				XlsPath = FileSaveRoute + subSys + ".xls";
				gjName = subSys;
			}
			InputStream is = new FileInputStream(XlsPath);
			Workbook rwb = Workbook.getWorkbook(is);
			Sheet rs = rwb.getSheet(0);
			int rsRows = rs.getRows();

			/*
			 * �������ݱ����ϵͳ�� �ڵ���NN �ܶ���NP �����NStart ·���ܶ���Npline ·���ڵ���Nr_node
			 * �յ���ں�Nend ģ��ʱ��NT �ܶ�·����NrouteYJ002 10 9 3 7 8 8 60 3
			 */
			rowCnt = 2;
			String sysName = rs.getCell(0, rowCnt).getContents().trim();
			NN = Integer.parseInt(rs.getCell(1, rowCnt).getContents().trim());
			NP = Integer.parseInt(rs.getCell(2, rowCnt).getContents().trim());
			Nstart = Integer.parseInt(rs.getCell(3, rowCnt).getContents().trim());
			Npline = Integer.parseInt(rs.getCell(4, rowCnt).getContents().trim());
			Nend = Integer.parseInt(rs.getCell(6, rowCnt).getContents().trim());
			NT = Integer.parseInt(rs.getCell(7, rowCnt).getContents().trim());
			Ncol = Integer.parseInt(rs.getCell(8, rowCnt).getContents().trim());
			rowCnt += 4;

			/*
			 * ��ϵͳ�ܶ����ݱ�� Pipe.No ����I0 �յ��J0 ����LP ֱ��DP Ħ��ϵ�� ��˱�� �ն˱�� 1 0 1 28.5
			 * 0.3 0.017 3.894 3.842 2 1 2 32 0.3 0.017 3.842 3.784 3 2 3 28.6
			 * 0.3 0.017 3.784 3.733 4 3 4 25.4 0.3 0.017 3.733 3.687 5 4 5 24.7
			 * 0.3 0.017 3.687 3.643 6 5 6 23.5 0.3 0.017 3.643 3.601 7 6 7 30.4
			 * 0.3 0.017 3.601 3.546 8 8 7 15.5 0.3 0.017 3.731 3.171 9 9 6 4.3
			 * 0.3 0.017 3.886 3.7
			 */
			I0 = new int[NP];
			J0 = new int[NP];
			lp = new double[NP];
			dpl = new double[NP];
			slp = new double[NP];
			ZJup = new double[NP];
			ZJdw = new double[NP];
			for (int j = 0; j < NP; j++)
			{
				I0[j] = Integer.parseInt(rs.getCell(1, rowCnt).getContents().trim());
				J0[j] = Integer.parseInt(rs.getCell(2, rowCnt).getContents().trim());
				lp[j] = Double.parseDouble(rs.getCell(3, rowCnt).getContents().trim());
				dpl[j] = Double.parseDouble(rs.getCell(4, rowCnt).getContents().trim());
				slp[j] = Double.parseDouble(rs.getCell(5, rowCnt).getContents().trim());
				ZJup[j] = Double.parseDouble(rs.getCell(6, rowCnt).getContents().trim());
				ZJdw[j] = Double.parseDouble(rs.getCell(7, rowCnt).getContents().trim());
				rowCnt++;
			}
			rowCnt += 3;

			/*
			 * ��ϵͳ�ڵ����ݱ��ڵ�No ��ˮ���ha ����ϵ�� ������ ���ױ�� 1 3.5 0.6 5.244 ��δ�õ� 2 3.5
			 * 0.6 5.191 3 3.5 0.6 5.177 4 3.5 0.6 5.208 5 3.5 0.6 5.221 6 3.5
			 * 0.6 5.201 7 3.5 0.6 5.2 8 3.5 0.6 5.121 9 3.5 0.6 5.131 10 3.5
			 * 0.6 5.186
			 */
			Aj = new double[NN];
			Acoef = new double[NN];
			Hj = new double[NN];
			for (int j = 0; j < NN; j++)
			{
				Aj[j] = Double.parseDouble(rs.getCell(1, rowCnt).getContents().trim());
				Acoef[j] = Double.parseDouble(rs.getCell(2, rowCnt).getContents().trim());
				Hj[j] = Double.parseDouble(rs.getCell(3, rowCnt).getContents().trim());
				rowCnt++;
			}
			rowCnt += 3;

			// ===================
			// ----�м�ָ�����---
			int i00, j00 = 0, Ni1, jj, jp0, inp = 0, jpp, NPP;
			// ������ʼ�ڵ�ž���Mstart-�ڵ��������MNP-�м�任���� Npjun-������֧�߹ܶξ���Mbranch(��������)
			int[] Mstart = new int[Nstart];
			int[][] MNP = new int[NN][Ncol];
			int[] Npjun = new int[NP];
			int[][] Mbranch = new int[Nstart][Npline];
			// ----�м����----
			int i, ii, j, ik, it, jk, jjj, k, k1, kp, INS, in1, in2, in3, NR1, NR2, Nprt, iprt1, iprt2;
			double Ad0, Akp, qkpmax, Hwdwkp, ycd0, yykp, sita, sigh_kp, slopt0, P_simu, rc;
			double dtnt, taa, tbb, AA, XX1, XX2, TTQj, TTQout, hdj0;
			double[] XX = new double[NT];
			double[] qit = new double[NT];
			double[] Qj = new double[NN];
			double[] vp = new double[NP];
			double[] slop = new double[NP];
			double[][] qpt = new double[NT][NP];
			double[][] qqkp = new double[NT][NP];
			double[][] vpt = new double[NT][NP];
			double[][] rid = new double[NT][NP];
			double[][] slopt = new double[NT][NP];
			double[][] Hwup = new double[NT][NP];
			double[][] Hwdw = new double[NT][NP];
			double[][] hdcc0 = new double[NT][NP];
			double[][] overflow = new double[NT][NN];
			double[][] Hw_over = new double[NT][NN];
			double[][] Hwj = new double[NT][NN];
			double[] TQj = new double[NT];
			double[] Toverf = new double[NT];

			WaterAcc = new String[NT];
			WaterLev = new String[NT];
			WaterFlowLoad = "";
			WaterActualFlow = "";
			WaterFlowRate = "";

			WaterAccGj = "";
			String FileName = "";
			if (gjId != null)
			{
				FileName = gjId.substring(0, 12) + ".txt";
			}
			else
			{
				FileName = subSys + ".txt";
			}
			String FilePath = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogValue/";
			FileOutputStream fs = new FileOutputStream(new File(FilePath + FileName));
			PrintStream printStream = new PrintStream(fs);
			printStream.println(FileName);

			DecimalFormat df = new DecimalFormat("##.####");
			DecimalFormat df1 = new DecimalFormat("######.##");

			/*
			 * System.out.println("�����뽵��ǿ��������P���꣩:"); Scanner input = new
			 * Scanner(System.in); P_simu = input.nextDouble();
			 */

			// ���ý���ǿ��
			P_simu = pSimu;

			printStream.println("===  �����ڣ� " + P_simu + "  ��     ʱ������ " + NT + "     �յ�ˮλ�� " + Hw_end + "  m  ===");
			// System.out.println();
			// System.out.println("pipe no.  I0    J0      lp     dpl     slp    ZJup    ZJdw");
			for (i = 0; i < NP; i++)
			{
				// System.out.printf("%6d%6d%6d%8.2f%8.2f%8.3f%8.2f%8.2f", i,
				// I0[i], J0[i], lp[i], dpl[i], slp[i], ZJup[i], ZJdw[i]);
				// System.out.println();
			}
			// System.out.println();
			// System.out.println("===  �����ڣ� " + P_simu + "  ��     ʱ������ " + NT +
			// "     �յ�ˮλ�� " + Hw_end + "  m  ===");
			printStream.println();
			printStream.println("pipe no.  I0    J0      lp     dpl     slp    ZJup    ZJdw");
			for (i = 0; i < NP; i++)
			{
				printStream.printf("%6d%6d%6d%8.2f%8.2f%8.3f%8.2f%8.2f", i, I0[i], J0[i], lp[i], dpl[i], slp[i], ZJup[i], ZJdw[i]);
				printStream.println();
			}
			printStream.println();
			// ================= ����slop[k] ===========
			for (k = 0; k < NP; k++)
			{
				slop[k] = (ZJup[k] - ZJdw[k]) / lp[k];
			}
			// ====20161106===== ���ɾ��� MNP[i][j] ====
			for (i = 0; i < NN; i++)
			{
				for (j = 0; j < Ncol; j++)
				{
					MNP[i][j] = 0;
				}
				MNP[i][0] = i;
				jj = 2;
				for (k = 0; k < NP; k++)
				{
					if (J0[k] == i)
					{
						jj = jj + 1;
						MNP[i][1] = MNP[i][1] + 1;
						MNP[i][jj] = k;
					}
					if (I0[k] == i)
					{
						MNP[i][2] = MNP[i][2] + 1;
					}
				}
			}
			// outfile<<"===========  print MNP[i][j]"<<endl;
			printStream.println("===========  print MNP[i][j]");
			for (i = 0; i < NN; i++)
			{
				for (j = 0; j < Ncol; j++)
				{
					printStream.printf("%6d", MNP[i][j]);
				}
				printStream.println();
			}
			// ----- MNP[i][j] ���� ------
			// ====20161112===== ���ɾ��� Mstart[i] ====
			jj = -1;
			for (i = 0; i < NN; i++)
			{
				if (MNP[i][1] == 0)
				{
					jj = jj + 1;
					Mstart[jj] = i;
				}
			}
			//
			// outfile<<"===========  print Mstart[i]"<<endl;
			printStream.println("===========  print Mstart[i]");
			for (i = 0; i < Nstart; i++)
			{
				printStream.printf("%6d", Mstart[i]);
			}
			printStream.println();
			// ====20161106===== ���ɾ���Mbranch[i][j] ====
			for (i = 0; i < NP; i++)
			{
				Npjun[i] = 1;
			}
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Npline; j++)
				{
					Mbranch[i][j] = -99;
				}
			}
			i00 = -1;
			NPP = 0;
			// L200:
			while (true)
			{
				for (i = 0; i < NN; i++)
				{
					if (MNP[i][2] == 0 && MNP[i][1] > 0)
					{
						jj = 2;
						Ni1 = MNP[i][1];
						for (j = 0; j < Ni1; j++)
						{
							jj = jj + 1;
							jp0 = MNP[i][jj];
							if (Npjun[jp0] > 0)
							{
								i00 = i00 + 1;
								j00 = 0;
								Mbranch[i00][j00] = jp0;
								inp = I0[jp0];
								Npjun[jp0] = -99;
								NPP = NPP + 1;
							}
							// L100:
							while (true)
							{
								INS = 1;
								for (jjj = 0; jjj < Nstart; jjj++)
								{
									if (Mstart[jjj] == inp) INS = 0;
								}
								if (INS > 0)
								{
									for (jpp = 0; jpp < NP; jpp++)
									{
										if (J0[jpp] == inp && Npjun[jpp] > 0)
										{
											j00 = j00 + 1;
											Mbranch[i00][j00] = jpp;
											inp = I0[jpp];
											Npjun[jpp] = -99;
											NPP = NPP + 1;
											break;
											// goto L100;
										}
										else
										{
											continue;
										}
									}
								} // --- end of if(INS>0) ---
								else
								{
									break;
								}
							}
						} // --- end of for(j=0;j<Ni1;j++) ---
					} // --- end of if(MNP[i][2]==0 && MNP[i][1]>0) ---
					MNP[i][2] = -99;
				}// --- end of for(i=0;i<NN;1++) ---
				for (i = 0; i < NN; i++)
				{
					for (j = 0; j < NP; j++)
					{
						if (I0[j] == i && Npjun[j] < 0)
						{
							MNP[i][2] = 0;
						}
					}
				}
				if (NPP >= NP)
				{
					break;
					// goto L200;

				}
			}
			// === ���ɾ��� Mbranch[i][j] ����====
			printStream.println();
			printStream.println("===========  print Mbranch[i][j]");
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Npline; j++)
				{
					printStream.printf("%6d", Mbranch[i][j]);
				}
				printStream.println();
			}
			//
			// ================= ����׼��̬ˮ��ģ��============================
			//
			// ----------------�ڵ��ˮ���(ha)�ͻ�ˮ����(m3/sec)����----------
			//
			// ֥�Ӹ������--rainfall intensity at every time step--
			AA = A1 + A1 * C_storm * Math.log(P_simu) / 2.303;
			rc = (float) (NR) / (float) (NT);
			for (it = 0; it < NT; it++)
			{
				if (it <= NR)
				{
					dtnt = dt * (float) (it);
					tbb = dt * (float) (NR) - dtnt;
					XX1 = AA * ((1.0 - n_storm) * tbb / rc + b_storm);
					XX2 = Math.pow((tbb / rc + b_storm), (n_storm + 1.0));
				}
				else
				{
					dtnt = dt * (float) (it);
					taa = dtnt - dt * (float) (NR);
					XX1 = AA * ((1.0 - n_storm) * taa / (1.0 - rc) + b_storm);
					XX2 = Math.pow((taa / (1.0 - rc) + b_storm), (n_storm + 1.0));
				}
				XX[it] = XX1 / XX2;
				qit[it] = 167.0 * XX[it] / 1000.0;
			}
			//
			NR1 = NR - 1;
			NR2 = NR + 1;
			qit[NR] = (qit[NR] + qit[NR - 1] + qit[NR + 1]) / 3.0;
			// ����ƽ������ǿ��mm/min
			XX1 = 0;
			for (it = 0; it < NT; it++)
			{
				XX1 = XX1 + XX[it];
			}
			XX1 = XX1 / (float) (NT);
			// ���깫ʽ����ǿ��mm/min
			taa = dt * (float) (NT) + b_storm;
			XX2 = AA / Math.pow(taa, n_storm);
			printStream.println();
			printStream.println(" ====== ����ǿ���������ݽ�� ======   ƽ��ǿ��XX1= " + XX1 + "(mm/min)   ��ʽǿ��XX2= " + XX2 + "(mm/min)" + "    rc= " + rc);
			printStream.println("    it      dtnt XX[it](mm/min) qit[it](m3/ha-sec)");
			for (it = 0; it < NT; it++)
			{
				dtnt = dt * (float) (it);
				printStream.printf("%6d%10.2f%12.4f%15.4f", it, dtnt, XX[it], qit[it]);
				printStream.println();
			}
			printStream.println();
			// ============֥�Ӹ������--����=============
			// -------------�ܶ�ˮ�����㿪ʼ--------------
			for (it = 0; it < NT; it++)
			{
				for (i = 0; i < NN; i++)
				{
					overflow[it][i] = 0.0;
					Hw_over[it][i] = 0.0;
				}
			}
			for (it = 0; it < NT; it++)
			{
				for (j = 0; j < NP; j++)
				{
					qpt[it][j] = -99.0;
					qqkp[it][j] = 0.0;
				}
			}
			// ---------------------------------------------------------------
			for (it = 0; it < NT; it++)
			{ // --1--
				// ----------����ܶ�����------------
				if (it == 0)
				{
					for (i = 0; i < NN; i++)
					{
						Qj[i] = Aj[i] * qit[it] * Acoef[i];
					}
					for (j = 0; j < NP; j++)
					{
						for (i = 0; i < NN; i++)
						{
							if (I0[j] == i) qpt[it][j] = Qj[i];
						}
					}
				}
				else
				{
					for (i = 0; i < NN; i++)
					{
						Qj[i] = Aj[i] * qit[it] * Acoef[i];
					}
					for (j = 0; j < NP; j++)
					{
						for (i = 0; i < NN; i++)
						{
							if (I0[j] == i) qpt[it][j] = Qj[i];
						}
					}
					for (j = 0; j < NP; j++)
					{
						for (k = 0; k < NP; k++)
						{
							if (J0[k] == I0[j]) qpt[it][j] = qpt[it][j] + qqkp[it - 1][k];
							// if (J0[k] == I0[j]) qpt[it][j] = qpt[it][j] +
							// qpt[it - 1][k];
						}
					}
				}
				for (j = 0; j < NP; j++)
				{
					qqkp[it][j] = qpt[it][j];
				}
				printStream.print(" it=" + it + "  qpt[it][k]=");
				for (k = 0; k < NP; k++)
				{
					printStream.printf("%8.4f", qpt[it][k]);
				}
				printStream.println();
				// -------------------20090127-sqliu------------------------
				for (ik = 0; ik < Nstart; ik++)
				// --2--
				{
					for (jk = 0; jk < Npline; jk++)
					// --3--
					{
						kp = Mbranch[ik][jk];
						if (kp >= 0)
						// --4--
						{
							if (J0[kp] == Nend)
							{
								Hwdw[it][kp] = Hw_end;
								if (Iprt == 1)
								{
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  Hw_end= " + Hw_end);
								}
							}
							else
							{
								for (k1 = 0; k1 < NP; k1++)
								{
									if (I0[k1] == J0[kp]) Hwdw[it][kp] = Hwup[it][k1];
								}
							}
							Ad0 = 0.7854 * Math.pow(dpl[kp], 2.0);
							hdj0 = ZJdw[kp] + dpl[kp];
							if (Hwdw[it][kp] >= hdj0)
							{
								if (Iprt == 1)
								{
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  ��û���� ");
								}
								hdcc0[it][kp] = 1.0;
								rid[it][kp] = dpl[kp] / 4.0;
								vpt[it][kp] = qpt[it][kp] / Ad0;
								slopt[it][kp] = 10.29 * Math.pow(slp[kp], 2.0) * Math.pow(qpt[it][kp], 2.0) / Math.pow(dpl[kp], 5.333);
								Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
								if (Hwup[it][kp] >= Hj[I0[kp]])
								{
									Hwup[it][kp] = Hj[I0[kp]];
									slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
									sigh_kp = 1.0;
									slopt0 = slopt[it][kp];
									if (slopt[it][kp] < 0.0)
									{
										slopt0 = -slopt0;
										sigh_kp = -1.0;
									}
									vpt[it][kp] = sigh_kp * Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt0, 0.5) / slp[kp];
									qqkp[it][kp] = vpt[it][kp] * Ad0;
								}
								// -------20161213start--------
								if (it > 0 && Hwup[it][kp] < Hj[I0[kp]] && overflow[it - 1][I0[kp]] > 0.0)
								{
									// *******20170306
									// Hwup[it][kp] = Hj[I0[kp]];
									// slopt[it][kp] = (Hwup[it][kp] -
									// Hwdw[it][kp]) / lp[kp];
									// sigh_kp = 1.0;
									// slopt0 = slopt[it][kp];
									// if (slopt[it][kp] < 0.0)
									// {
									// slopt0 = -slopt0;
									// sigh_kp = -1.0;
									// }
									// vpt[it][kp] = sigh_kp *
									// Math.pow(rid[it][kp], 0.6667) *
									// Math.pow(slopt0, 0.5) / slp[kp];
									// qqkp[it][kp] = vpt[it][kp] * Ad0;
									qqkp[it][kp] = qpt[it][kp] + overflow[it - 1][I0[kp]] * 0.15 / dt / 60.0;
									slopt[it][kp] = 10.29 * Math.pow(slp[kp], 2.0) * Math.pow(qqkp[it][kp], 2.0) / Math.pow(dpl[kp], 5.333);
									Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
									if (Hwup[it][kp] >= Hj[I0[kp]])
									{
										Hwup[it][kp] = Hj[I0[kp]];
										slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
										sigh_kp = 1.0;
										slopt0 = slopt[it][kp];
										if (slopt[it][kp] < 0.0)
										{
											slopt0 = -slopt0;
											sigh_kp = -1.0;
										}
										vpt[it][kp] = sigh_kp * Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt0, 0.5) / slp[kp];
										qqkp[it][kp] = vpt[it][kp] * Ad0;
									}
								}
								// -------20161213end----------
							}
							else
							// --5--
							{
								if (Iprt == 1)
								{
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdw= " + Hwdw[it][kp] + "  ����û���� ");
								}
								// --20161018---�����ٽ�ˮ��------------
								qkpmax = 2.699 * Math.pow(dpl[kp], 2.5);
								if (qpt[it][kp] > qkpmax)
								{
									if (Iprt == 1)
									{
										printStream.println("   it= " + it + "   kp= " + kp + "  qkpmax= " + qkpmax + "  ����û���ܳ��� ");
									}
									vpt[it][kp] = qpt[it][kp] / Ad0;
									// H00=pow(vpt[it][kp],2.0)/13.72;
									// Hwdw[it][kp]=ZJdw[kp]+dpl[kp]+H00;
									Hwdw[it][kp] = ZJdw[kp] + dpl[kp];
									hdcc0[it][kp] = 1.0;
									rid[it][kp] = dpl[kp] / 4.0;
									slopt[it][kp] = 10.29 * Math.pow(slp[kp], 2.0) * Math.pow(qpt[it][kp], 2.0) / Math.pow(dpl[kp], 5.333);
									Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
									//
									if (Hwup[it][kp] >= Hj[I0[kp]])
									{
										Hwup[it][kp] = Hj[I0[kp]];
										slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
										sigh_kp = 1.0;
										slopt0 = slopt[it][kp];
										if (slopt[it][kp] < 0.0)
										{
											slopt0 = -slopt0;
											sigh_kp = -1.0;
										}
										vpt[it][kp] = sigh_kp * Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt0, 0.5) / slp[kp];
										qqkp[it][kp] = vpt[it][kp] * Ad0;
									}
									// -------20161213start--------
									if (Hwup[it][kp] < Hj[I0[kp]] && overflow[it - 1][I0[kp]] > 0.0 && it > 0)
									{
										// Hwup[it][kp] = Hj[I0[kp]];
										// slopt[it][kp] = (Hwup[it][kp] -
										// Hwdw[it][kp]) / lp[kp];
										// sigh_kp = 1.0;
										// slopt0 = slopt[it][kp];
										// if (slopt[it][kp] < 0.0)
										// {
										// slopt0 = -slopt0;
										// sigh_kp = -1.0;
										// }
										// vpt[it][kp] = sigh_kp *
										// Math.pow(rid[it][kp], 0.6667) *
										// Math.pow(slopt0, 0.5) / slp[kp];
										// qqkp[it][kp] = vpt[it][kp] * Ad0;
										// ***********20170306
										qqkp[it][kp] = qpt[it][kp] + overflow[it - 1][I0[kp]] * 0.15 / dt / 60.0;
										slopt[it][kp] = 10.29 * Math.pow(slp[kp], 2.0) * Math.pow(qqkp[it][kp], 2.0) / Math.pow(dpl[kp], 5.333);
										Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
										if (Hwup[it][kp] >= Hj[I0[kp]])
										{
											Hwup[it][kp] = Hj[I0[kp]];
											slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
											sigh_kp = 1.0;
											slopt0 = slopt[it][kp];
											if (slopt[it][kp] < 0.0)
											{
												slopt0 = -slopt0;
												sigh_kp = -1.0;
											}
											vpt[it][kp] = sigh_kp * Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt0, 0.5) / slp[kp];
											qqkp[it][kp] = vpt[it][kp] * Ad0;
										}
									}
									// -------20161213end----------
								}
								else
								{
									if (Iprt == 1)
									{
										printStream.println("   it= " + it + "   kp= " + kp + "  Hwdw= " + Hwdw[it][kp] + "  ����û�����ܳ��� ");
									}
									// ==20161115---����ˮ�ʼ--------
									// if (slop[kp] > 0)
									// {// ----����ˮ��----
									// if (qpt[it][kp] >= 0.0)
									// {
									// ycd0 = 20.1538 * slp[kp] * qpt[it][kp] /
									// Math.pow(dpl[kp], 2.6667) /
									// Math.pow(slop[kp], 0.5);
									// if (ycd0 <= 1.5)
									// {
									// hdcc0[it][kp] = 0.27 * Math.pow(ycd0,
									// 0.485);
									// }
									// else
									// {
									// hdcc0[it][kp] = 0.098 * ycd0 + 0.19;
									// }
									// if (hdcc0[it][kp] <= 0.0001)
									// {
									// hdcc0[it][kp] = 0.0001;
									// }
									// }
									// else
									// {
									// hdcc0[it][kp] = 1.0;
									// }
									// }
									// else
									// {// ----�ٽ�ˮ��----
									// if (qpt[it][kp] >= 0.0)
									// {
									// ycd0 = qpt[it][kp] / 2.983 /
									// Math.pow(dpl[kp], 2.5);
									// hdcc0[it][kp] = Math.pow(ycd0, 0.513);
									// if (hdcc0[it][kp] <= 0.0001)
									// {
									// hdcc0[it][kp] = 0.0001;
									// }
									// }
									// else
									// {
									// hdcc0[it][kp] = 1.0;
									// }
									// }
									// if (hdcc0[it][kp] > 1.0)
									// {
									// hdcc0[it][kp] = 1.0;
									// }
									// ----20170307�޸Ŀ�ʼ----
									if (slop[kp] < 0.0001)
									{
										slop[kp] = 0.0001;
									}
									// ----����ˮ�ʽ----
									if (qpt[it][kp] >= 0.0)
									{
										ycd0 = 20.1538 * slp[kp] * qpt[it][kp] / Math.pow(dpl[kp], 2.6667) / Math.pow(slop[kp], 0.5);
										if (ycd0 <= 1.5)
										{
											hdcc0[it][kp] = 0.27 * Math.pow(ycd0, 0.485);
										}
										else
										{
											hdcc0[it][kp] = 0.098 * ycd0 + 0.19;
										}
										if (hdcc0[it][kp] <= 0.0001)
										{
											hdcc0[it][kp] = 0.0001;
										}
									}
									else
									{
										if (it == 0)
										{
											hdcc0[it][kp] = 0.001;
										}
										if (it > 0)
										{
											hdcc0[it][kp] = hdcc0[it - 1][kp];
										}
									}
									if (hdcc0[it][kp] > 1.0)
									{
										hdcc0[it][kp] = 1.0;
									}
									// ----20170307�޸Ľ���----

									// 20170217�滻
									/*
									 * ycd0 = 20.1538 * slp[kp] * qpt[it][kp] /
									 * Math.pow(dpl[kp], 2.6667) /
									 * Math.pow(slop[kp], 0.5); if (ycd0 <= 1.5)
									 * { hdcc0[it][kp] = 0.27 * Math.pow(ycd0,
									 * 0.485); } else { hdcc0[it][kp] = 0.098 *
									 * ycd0 + 0.19; } if (hdcc0[it][kp] <=
									 * 0.0001) { hdcc0[it][kp] = 0.0001; } if
									 * (hdcc0[it][kp] > 1.0) { hdcc0[it][kp] =
									 * 1.0; }
									 */

									// ==20161115---����ˮ�����-------
									// ----20170307a--�����俪ʼ-----
									hdj0 = ZJdw[kp] + hdcc0[it][kp] * dpl[kp];
									if (hdj0 < Hwdw[it][kp])
									{
										hdcc0[it][kp] = (Hwdw[it][kp] - ZJdw[kp]) / dpl[kp];
									}
									// ----20170307a--���������-----
									sita = 2.0 * Math.acos(1.0 - 2.0 * hdcc0[it][kp]);
									rid[it][kp] = 0.25 * dpl[kp] * (sita - Math.sin(sita)) / sita;
									Akp = Math.pow(dpl[kp], 2.0) * (sita - Math.sin(sita)) / 8.0;
									vpt[it][kp] = qpt[it][kp] / Akp;
									Hwdw[it][kp] = ZJdw[kp] + hdcc0[it][kp] * dpl[kp];
									slopt[it][kp] = Math.pow(slp[kp], 2.0) * Math.pow(vpt[it][kp], 2.0) / Math.pow(rid[it][kp], 1.333);
									Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
									if (Hwup[it][kp] >= Hj[I0[kp]])
									{
										Hwup[it][kp] = Hj[I0[kp]];
										slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
										sigh_kp = 1.0;
										slopt0 = slopt[it][kp];
										if (slopt[it][kp] < 0.0)
										{
											slopt0 = -slopt0;
											sigh_kp = -1.0;
										}
										vpt[it][kp] = sigh_kp * Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt0, 0.5) / slp[kp];
										qqkp[it][kp] = vpt[it][kp] * Ad0;
									}
									if (it > 0 && Hwup[it][kp] < Hj[I0[kp]] && overflow[it - 1][I0[kp]] > 0.0)
									{
										// Hwup[it][kp] = Hj[I0[kp]];
										// slopt[it][kp] = (Hwup[it][kp] -
										// Hwdw[it][kp]) / lp[kp];
										// sigh_kp = 1.0;
										// slopt0 = slopt[it][kp];
										// if (slopt[it][kp] < 0.0)
										// {
										// slopt0 = -slopt0;
										// sigh_kp = -1.0;
										// }
										// vpt[it][kp] = sigh_kp *
										// Math.pow(rid[it][kp], 0.6667) *
										// Math.pow(slopt0, 0.5) / slp[kp];
										// qqkp[it][kp] = vpt[it][kp] * Ad0;
										// *********20170306
										qqkp[it][kp] = qpt[it][kp] + overflow[it - 1][I0[kp]] * 0.15 / dt / 60.0;
										slopt[it][kp] = 10.29 * Math.pow(slp[kp], 2.0) * Math.pow(qqkp[it][kp], 2.0) / Math.pow(dpl[kp], 5.333);
										Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
										if (Hwup[it][kp] >= Hj[I0[kp]])
										{
											Hwup[it][kp] = Hj[I0[kp]];
											slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
											sigh_kp = 1.0;
											slopt0 = slopt[it][kp];
											if (slopt[it][kp] < 0.0)
											{
												slopt0 = -slopt0;
												sigh_kp = -1.0;
											}
											vpt[it][kp] = sigh_kp * Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt0, 0.5) / slp[kp];
											qqkp[it][kp] = vpt[it][kp] * Ad0;
										}
									}
								}
							}
							// 5--end
							// ------- ���it������ ----------
							if (Iprt == 1)
							{
								printStream.println("   it= " + it + "   kp= " + kp + "   I0[kp]= " + I0[kp] + "  Hwdm= " + Hwdw[it][kp] + "  Hwup= " + Hwup[it][kp] + "  Hj= " + Hj[I0[kp]] + "  hdcc0= " + hdcc0[it][kp] + "  qpt= " + qpt[it][kp] + "  qqkp= " + qqkp[it][kp] + "  vpt= " + vpt[it][kp]);
							}
						}// --4 if(kp>=0) end
					}// --3 ---jk end
				}// --2---ik end
					// -------------- ����ڵ�ˮλ-�ڵ��ˮ���ͻ�ˮ��� ---------------
				/*
				 * for (i = 0; i < NP; i++) { k = J0[i]; if (k == Nend) {
				 * Hwj[it][k] = Hwdw[it][i]; } { j = I0[i]; Hwj[it][j] =
				 * Hwup[it][i]; if (it > 0) { overflow[it][j] = overflow[it -
				 * 1][j] + (qpt[it][i] - qqkp[it][i]) * dt * 60.0;
				 * Hw_over[it][j] = csf * overflow[it][j] / Aj[j] / 10000.0 *
				 * 1000.0; if (Hw_over[it][j] > heage) { Hw_over[it][j] = heage
				 * + csf * (overflow[it][j] - Aj[j] * heage / 1000.0) / 3.0 /
				 * 10000.0 * 1000.0; } if (it > NR && Hw_over[it][j] <= 5.0) {
				 * overflow[it][j] = 0.0; Hw_over[it][j] = 0.0; } } } }
				 */
				// 20170217�޸�
				for (i = 0; i < NP; i++)
				{
					k = J0[i];
					if (k == Nend)
					{
						Hwj[it][k] = Hw_end;
					}
					// if(it > 0){
					// j = I0[i];
					// Hwj[it][j] = Hwup[it][i];
					// if (Hwup[it][i] >= Hj[j] || overflow[it - 1][j] > 0.0)
					// // if(Hwup[it][i]>=Hj[j])
					// {
					// overflow[it][j] = overflow[it - 1][j] + (qpt[it][i] -
					// qqkp[it][i]) * dt * 60.0;
					// Hw_over[it][j] = overflow[it][j] / Aj[j] / 10000.0 *
					// 1000.0;
					// }
					// if (Hwup[it][i] < Hj[j] && overflow[it - 1][j] > 0.0)
					// {
					// overflow[it][j] = overflow[it - 1][j] * 0.90;
					// Hw_over[it][j] = overflow[it][j] / Aj[j] / 10000.0 *
					// 1000.0;
					// if (Hw_over[it][j] > heage)
					// {
					// Hw_over[it][j] = heage + (overflow[it][j] - Aj[j] * heage
					// / 1000.0) / csf / 10000.0 * 1000.0;
					// }
					// }
					// }
					// if (it > NR && Hw_over[it][j] <= 5.0)
					// {
					// overflow[it][j] = 0.0;
					// Hw_over[it][j] = 0.0;
					// }
					// **********20170306
					j = I0[i];
					Hwj[it][j] = Hwup[it][i];
					if (it > 0)
					{
						overflow[it][j] = overflow[it - 1][j] + (qpt[it][i] - qqkp[it][i]) * dt * 60.0;
						Hw_over[it][j] = csf * overflow[it][j] / Aj[j] / 10000.0 * 1000.0;
						if (Hw_over[it][j] > heage)
						{
							Hw_over[it][j] = heage + csf * (overflow[it][j] - Aj[j] * heage / 1000.0) / 3.0 / 10000.0 * 1000.0;
						}
						if (it > NR && Hw_over[it][j] <= 5.0)
						{
							overflow[it][j] = 0.0;
							Hw_over[it][j] = 0.0;
						}
					}
				}
				// �޸Ľ���
				printStream.println();
				printStream.println("    it   �ܶκ�  I0   J0 �ܾ�dpl    �ܶ�qpt ˮ���뾶R    ������ ����(m/s)  ����ˮλ  ����ˮλ  �Ϲܵ׸�  �¹ܵ׸�  �ܶ��¶�  �ϵ����  ˮ���¶�    qqkp");
				for (i = 0; i < NP; i++)
				{
					printStream.printf("%6d%6d%6d%5d%8.2f%12.4f%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.4f%10.4f", it, i, I0[i], J0[i], dpl[i], qpt[it][i], rid[it][i], hdcc0[it][i], vpt[it][i], Hwup[it][i], Hwdw[it][i], ZJup[i], ZJdw[i], slop[i], Hj[I0[i]], slopt[it][i], qqkp[it][i]);
					printStream.println();
				}
				printStream.println();
				// ------------ ���������ڵ���� ----
				TQj[it] = 0;
				Toverf[it] = 0;
				for (i = 0; i < NN; i++)
				{
					TQj[it] = TQj[it] + Qj[i];
					Toverf[it] = Toverf[it] + overflow[it][i];
				}
				printStream.println();
				printStream.println("  TQj[it]= " + TQj[it] + " m3/sec     Toverf[it]=  " + Toverf[it] + " m3  ");
				printStream.println();
			}
			// --------------��Ļ����������------

			// xxxxxxx20170416-ʱ������ƽ��ֵ��ʼxxxxxxxxxxxxx
			// -----20170416-ʱ������ƽ��ֵ��ʼ---------------
			for (i = 0; i < NN; i++)
			{
				for (it = 2; it < NT - 2; it++)
				{
					overflow[it][i] = (overflow[it - 2][i] + overflow[it - 1][i] + overflow[it][i] + overflow[it + 1][i] + overflow[it + 2][i]) / 5.0;
					Hw_over[it][i] = (Hw_over[it - 2][i] + Hw_over[it - 1][i] + Hw_over[it][i] + Hw_over[it + 1][i] + Hw_over[it + 2][i]) / 5.0;
					Hwj[it][i] = (Hwj[it - 2][i] + Hwj[it - 1][i] + Hwj[it][i] + Hwj[it + 1][i] + Hwj[it + 2][i]) / 5.0;
				}
			}
			//
			for (i = 0; i < NP; i++)
			{
				for (it = 2; it < NT - 2; it++)
				{
					qqkp[it][i] = (qqkp[it - 2][i] + qqkp[it - 1][i] + qqkp[it][i] + qqkp[it + 1][i] + qqkp[it + 2][i]) / 5.0;
					qpt[it][i] = (qpt[it - 2][i] + qpt[it - 1][i] + qpt[it][i] + qpt[it + 1][i] + qpt[it + 2][i]) / 5.0;
					vpt[it][i] = (vpt[it - 2][i] + vpt[it - 1][i] + vpt[it][i] + vpt[it + 1][i] + vpt[it + 2][i]) / 5.0;
					hdcc0[it][i] = (hdcc0[it - 2][i] + hdcc0[it - 1][i] + hdcc0[it][i] + hdcc0[it + 1][i] + hdcc0[it + 2][i]) / 5.0;
				}
			}
			//
			// -----20170416-ʱ������ƽ��ֵ����---------------
			// xxxxxxx20170416-ʱ������ƽ��ֵ����xxxxxxxxxxxxx

			// --------------- ����ܶγ����ȼ����� ---------------
			printStream.println(" ======== ʱ�ιܶγ����� ========");
			Nprt = NP / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NP)
					{
						iprt2 = NP;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						printStream.printf("%8.3f", hdcc0[it][i]);
					}
					printStream.println();
				}
			}
			// ----------------- ����ڵ�ˮλ������ ---------------
			printStream.println(" ======== ʱ�νڵ�ˮλ ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NN)
					{
						iprt2 = NN;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						printStream.printf("%8.2f", Hwj[it][i]);
					}
					printStream.println();
				}
			}
			// ***********��֯���ݣ�����ҳ��������ʾ********
			for (it = 0; it < NT; it++)
			{
				String WaterLevNew = "";
				for (i = 0; i < NN; i++)
				{
					if (gjId != null && i == SubgjId)
					{
						WaterAccGj += df1.format(Hwj[it][i]) + "|";
					}
					WaterLevNew += df1.format(Hwj[it][i]) + "|";
				}
				WaterLev[it] = WaterLevNew;
			}
			// *************************************
			// -------------------- ����ڵ����������� ---------------
			printStream.println(" ======== ʱ�νڵ��ˮ��(m3) ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				iprt1 = ii * Nprtc;
				iprt2 = iprt1 + Nprtc;
				if (iprt2 > NN)
				{
					iprt2 = NN;
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						if (overflow[it][i] <= 0.1)
						{
							printStream.print("        ");
						}
						else
						{
							printStream.printf("%8.2f", overflow[it][i]);
						}
					}
					printStream.println();
				}
			}
			// ***********��֯���ݣ�����ҳ��������ʾ********
			for (it = 0; it < NT; it++)
			{
				String WaterAccNew = "";
				for (i = 0; i < NN; i++)
				{
					if (overflow[it][i] <= 0.0)
					{
						WaterAccNew += 0 + "|";
					}
					else
					{
						WaterAccNew += df1.format(overflow[it][i]) + "|";
					}
				}
				WaterAcc[it] = WaterAccNew;
			}
			// *********************************************
			printStream.println(" ======== ʱ�νڵ��ˮ���(mm) ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				iprt1 = ii * Nprtc;
				iprt2 = iprt1 + Nprtc;
				if (iprt2 > NN)
				{
					iprt2 = NN;
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println();
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						if (Hw_over[it][i] < 5.0)
						{
							printStream.print("        ");
						}
						else
						{
							printStream.printf("%8.2f", Hw_over[it][i]);
						}
					}
					printStream.println();
				}
			}
			// ***********��֯���ݣ�����ҳ��������ʾ*****20170120***
			for (it = 0; it < NT; it++)
			{
				for (i = 0; i < NP; i++)
				{
					if (gjId != null && gxId != null && i == SubgxId)
					{
						WaterFlowLoad += df1.format(qpt[it][i]) + "|";
						WaterActualFlow += df1.format(qqkp[it][i]) + "|";
						WaterFlowRate += df1.format(vpt[it][i]) + "|";
					}
				}
			}
			// *********************************************

			printStream.println("------ ģ�ͼ������ ------");
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			return gjName + "," + "NumberFormat" + "," + (rowCnt + 1);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
			return gjName + "," + "ArrayIndexOut" + "," + "";
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return gjName + "," + "unknown" + "," + (rowCnt + 1);
		}
		if (AnalogWaterType.equals("WaterAccGj"))
		{
			return WaterAccGj;
		}
		else if (AnalogWaterType.equals("WaterAcc"))
		{
			String WaterAccList = "";
			for (int i = 0; i < WaterAcc.length; i++)
			{
				WaterAccList += subSys.substring(7, 12) + WaterAcc[i] + ";";
			}
			return WaterAccList;
		}
		else if (AnalogWaterType.equals("WaterLev"))
		{
			String WaterLevList = "";
			for (int i = 0; i < WaterLev.length; i++)
			{
				WaterLevList += subSys.substring(7, 12) + WaterLev[i] + ";";
			}
			return WaterLevList;
		}
		else if (AnalogWaterType.equals("WaterFlowLoad"))
		{
			return WaterFlowLoad;
		}
		else if (AnalogWaterType.equals("WaterActualFlow"))
		{
			return WaterActualFlow;
		}
		else if (AnalogWaterType.equals("WaterFlowRate"))
		{
			return WaterFlowRate;
		}
		return "";
	}

	// ģ�����۵�һ��
	private String analog_W1(String subSys, int timePeriod, String gjId, String AnalogWaterType)
	{
		SewageAcc = new String[24];
		SewageLev = new String[24];
		int SubgjId = 0;
		if (gjId != null)
		{
			SubgjId = CommUtil.StrToInt(gjId.substring(12, 15)) - 1;
		}
		try
		{
			// subSys = 900001_WJ001
			// �����������ݣ�
			// �ܶ������ڵ������ܵ��������·�����ܶ�����ģ��ʱ����,
			// �ܵ�·������·�����ڵ������յ�ڵ�ţ��м�������ļ�ָ�룬������ݱ�����
			int NP = 9, NN = 10, Nstart = 3, Npline = 7, NT = 24, Nr_node = 8, Nend = 7, Iprt = 0, Nprtc = 20;
			// ��ˮ��������
			// �˾�����ˮ����m/d��, �ܶ��趨����vp0��ʱ�䲽����h������ϵͳ�յ�ˮλ�����氼͹ϵ��csf
			double q1 = 0.45, vp0 = 0.8, dt = 1.0, Hw_end = 4.1, csf = 3.0;
			// �ڵ�������(ha)�� �ڵ�����ߣ�m�����ڵ�����˿�(��)
			double[] Aj;
			double[] Hj;
			double[] Rj;
			// ��ˮ���仯���ߣ�NT��
			double[] Rf;
			// ����·������·���ڵ��(��99��ʾ�սڵ�)
			int[][] Mroute;
			int[][] Mbranch;
			// �ܶ����νڵ��I0,���νڵ��J0���ܶγ���(m),Ħ��ϵ��
			int[] I0;
			int[] J0;
			double[] lp;
			double[] slp;
			// �ڵ�����
			int[] Mstart;
			// �ܶ�ֱ��(m)�����ιܵ׸߳�(m)�����ιܵ׸߳�(m)
			double[] dpl;
			double[] ZJup;
			double[] ZJdw;

			this.FileSaveRoute = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogData/";
			String XlsPath = "";
			if (gjId != null)
			{
				XlsPath = FileSaveRoute + gjId.substring(0, 12) + ".xls";
				gjName = gjId.substring(0, 12);
			}
			else
			{
				XlsPath = FileSaveRoute + subSys + ".xls";
				gjName = subSys;
			}
			InputStream is = new FileInputStream(XlsPath);
			Workbook rwb = Workbook.getWorkbook(is);
			Sheet rs = rwb.getSheet(0);
			int rsRows = rs.getRows();

			/*
			 * �������ݱ����ϵͳ�� �ڵ���NN �ܶ���NP �����NStart ·���ܶ���Npline ·���ڵ���Nr_node
			 * �յ���ں�Nend ģ��ʱ��NT �ܶ�·����NrouteYJ002 10 9 3 7 8 8 60 3
			 */
			int rowCnt = 2;
			String sysName = rs.getCell(0, rowCnt).getContents().trim();
			NN = Integer.parseInt(rs.getCell(1, rowCnt).getContents().trim());
			NP = Integer.parseInt(rs.getCell(2, rowCnt).getContents().trim());
			Nstart = Integer.parseInt(rs.getCell(3, rowCnt).getContents().trim());
			Npline = Integer.parseInt(rs.getCell(4, rowCnt).getContents().trim());
			Nr_node = Integer.parseInt(rs.getCell(5, rowCnt).getContents().trim());
			Nend = Integer.parseInt(rs.getCell(6, rowCnt).getContents().trim());
			NT = Integer.parseInt(rs.getCell(7, rowCnt).getContents().trim());
			rowCnt += 4;

			/*
			 * ��ϵͳ�ܶ����ݱ�� Pipe.No ����I0 �յ��J0 ����LP ֱ��DP Ħ��ϵ�� ��˱�� �ն˱�� 1 0 1 28.5
			 * 0.3 0.017 3.894 3.842 2 1 2 32 0.3 0.017 3.842 3.784 3 2 3 28.6
			 * 0.3 0.017 3.784 3.733 4 3 4 25.4 0.3 0.017 3.733 3.687 5 4 5 24.7
			 * 0.3 0.017 3.687 3.643 6 5 6 23.5 0.3 0.017 3.643 3.601 7 6 7 30.4
			 * 0.3 0.017 3.601 3.546 8 8 7 15.5 0.3 0.017 3.731 3.171 9 9 6 4.3
			 * 0.3 0.017 3.886 3.7
			 */
			I0 = new int[NP];
			J0 = new int[NP];
			lp = new double[NP];
			dpl = new double[NP];
			slp = new double[NP];
			ZJup = new double[NP];
			ZJdw = new double[NP];
			for (int j = 0; j < NP; j++)
			{
				I0[j] = Integer.parseInt(rs.getCell(1, rowCnt + j).getContents().trim());
				J0[j] = Integer.parseInt(rs.getCell(2, rowCnt + j).getContents().trim());
				lp[j] = Double.parseDouble(rs.getCell(3, rowCnt + j).getContents().trim());
				dpl[j] = Double.parseDouble(rs.getCell(4, rowCnt + j).getContents().trim());
				slp[j] = Double.parseDouble(rs.getCell(5, rowCnt + j).getContents().trim());
				ZJup[j] = Double.parseDouble(rs.getCell(6, rowCnt + j).getContents().trim());
				ZJdw[j] = Double.parseDouble(rs.getCell(7, rowCnt + j).getContents().trim());
			}
			rowCnt += NP;
			rowCnt += 3;

			/*
			 * ��ϵͳ�ڵ����ݱ�� �ڵ�No �������Aj ������ �ڵ�����˿� 1 0.2 5.244 80 2 0.2 5.191 80 3
			 * 0.2 5.177 80 4 0.2 5.208 80 5 0.2 5.221 80 6 0.2 5.201 80 7 0.2
			 * 5.2 80 8 0.2 5.121 80 9 0.2 5.131 80 10 0.2 5.186 80
			 */
			Aj = new double[NN];
			Hj = new double[NN];
			Rj = new double[NN];
			for (int j = 0; j < NN; j++)
			{
				Aj[j] = Double.parseDouble(rs.getCell(1, rowCnt + j).getContents().trim());
				Hj[j] = Double.parseDouble(rs.getCell(2, rowCnt + j).getContents().trim());
				Rj[j] = Double.parseDouble(rs.getCell(3, rowCnt + j).getContents().trim());
			}
			rowCnt += NN;
			rowCnt += 3;

			Mroute = new int[Nstart][Nr_node];
			/**
			 * ����·������ ��� 1 2 3 ���� 0 8 9
			 */
			Mstart = new int[Nstart];
			for (int j = 0; j < Nstart; j++)
			{
				Mstart[j] = Integer.parseInt(rs.getCell(j + 1, rowCnt).getContents().trim());
			}
			rowCnt += 1;
			rowCnt += 3;

			/*
			 * ��ϵͳ��֧·���ܶ����ݾ��� ���� �ڵ���� 1 2 3 4 5 6 7 1 6 5 4 3 2 1 0 2 7 -99 -99
			 * -99 -99 -99 -99 3 8 -99 -99 -99 -99 -99 -99
			 */
			Mbranch = new int[Nstart][Npline];
			for (int j = 0; j < Nstart; j++)
			{
				for (int k = 0; k < Npline; k++)
				{
					Mbranch[j][k] = Integer.parseInt(rs.getCell(k + 1, rowCnt + j).getContents().trim());
				}
			}
			rowCnt += Nstart;
			rowCnt += 3;

			/*
			 * ��ˮ���仯���� ʱ�� 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21
			 * 22 23 24 ���� 2.12 2.19 2.18 2.8 3.21 3.9 5.2 5.62 5.63 5.08 5.12
			 * 5.69 5.28 4.52 4.51 4.58 5.5 5.62 5.13 5.18 3.4 3.12 2.22 2.2
			 */
			Rf = new double[NT];
			for (int j = 0; j < NT; j++)
			{
				Rf[j] = Double.parseDouble(rs.getCell(j + 1, rowCnt).getContents().trim());
			}
			// ----�м����----
			int i, j, k, ik, jk, it, k1, kp, in1, in2, in3, NR1, NR2, ii, Nprt, iprt1, iprt2;
			double Ad0, qkpmax, Hwdwkp, H00, ycd0, yykp, sita;
			double dtnt;
			double[] qit = new double[NT];
			double[][] sumqj = new double[NT][NN];
			double[][] sumRj = new double[NT][NN];
			double[][] Tnode = new double[NN][NN];
			double[][] sumTnode = new double[NN][NN];
			double sumqjj, hdj0;
			// taa,tbb,AA,XX1,XX2,XX[NT],
			double[] vp = new double[NP];
			double[] slop = new double[NP];
			double[][] qpt = new double[NT][NP];
			double[][] qqkp = new double[NT][NP];
			double[][] vpt = new double[NT][NP];
			double[][] rid = new double[NT][NP];
			double[][] slopt = new double[NT][NP];
			double[][] Hwup = new double[NT][NP];
			double[][] Hwdw = new double[NT][NP];
			double[][] hdcc0 = new double[NT][NP];
			double[][] overflow = new double[NT][NN];
			double[][] Hw_over = new double[NT][NN];
			double[][] Hwj = new double[NT][NN];

			DecimalFormat df = new DecimalFormat("##.####");
			DecimalFormat df1 = new DecimalFormat("######.##");
			String FilePath = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogValue/";
			String FileName = subSys + ".txt";
			FileOutputStream fs = new FileOutputStream(new File(FilePath + FileName));
			PrintStream printStream = new PrintStream(fs);
			printStream.println("20161030-��ˮ����ģ��-���ҳ�-3.txt");
			// System.out.println("------ ��ˮ����ģ��-���ҳ� ------");
			// ================= ����ֵ ===============================
			for (i = 0; i < NT; i++)
			{
				for (j = 0; j < NN; j++)
					sumRj[i][j] = 0;
			}
			for (i = 0; i < NT; i++)
			{
				for (j = 0; j < NN; j++)
					sumqj[i][j] = 0;
			}
			for (i = 0; i < NN; i++)
			{
				for (j = 0; j < NN; j++)
				{
					if (i == j)
					{
						Tnode[i][j] = 0;
					}
					else
					{
						Tnode[i][j] = -99;
					}
				}
			}
			for (i = 0; i < NN; i++)
			{
				for (j = 0; j < NN; j++)
					sumTnode[i][j] = 0;
			}
			// =====20161029===== ���ɾ��� Mroute[i][j] ====
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Nr_node; j++)
					Mroute[i][j] = -99;
			}
			for (i = 0; i < Nstart; i++)
				Mroute[i][0] = Mstart[i];
			for (i = 0; i < Nstart; i++)
			{
				for (j = 1; j < Nr_node; j++)
				{
					for (k = 0; k < NP; k++)
					{
						if (I0[k] == Mroute[i][j - 1])
						{
							Mroute[i][j] = J0[k];
						}
					}
				}
			}
			// ==================Tnode-sumTnode=========================
			for (i = 0; i < NP; i++)
				vp[i] = vp0;
			for (kp = 0; kp < NP; kp++)
			{
				in1 = I0[kp];
				in2 = J0[kp];
				Tnode[in1][in2] = lp[kp] / vp[kp] / 3600;
				slop[kp] = (ZJup[kp] - ZJdw[kp]) / lp[kp];
			}
			//
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Nr_node; j++)
				{
					in1 = Mroute[i][j];
					if (in1 >= 0)
					{
						for (k = j + 1; k < Nr_node; k++)
						{
							in2 = Mroute[i][k - 1];
							in3 = Mroute[i][k];
							if (in3 >= 0)
							{
								sumTnode[in1][in3] = sumTnode[in1][in2] + Tnode[in2][in3];
							}
						}
					}
				}
			}
			printStream.println("pipe no.  I0    J0");
			for (i = 0; i < NP; i++)
			{
				// System.out.printf("%6d%6d%6d", i, I0[i], J0[i]);
				// System.out.println();
			}
			printStream.println();
			printStream.print(" ip=");
			for (i = 0; i < NP; i++)
			{
				printStream.printf("%4d", i);
			}
			printStream.println();
			printStream.print(" I0=");
			for (i = 0; i < NP; i++)
			{
				printStream.printf("%4d", I0[i]);
			}
			printStream.println();
			printStream.print(" J0=");
			for (i = 0; i < NP; i++)
			{
				printStream.printf("%4d", J0[i]);
			}
			printStream.println();
			printStream.println();
			printStream.println("===========  print Mroute[i][j]");
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Nr_node; j++)
				{
					printStream.printf("%6d", Mroute[i][j]);
				}
				printStream.println();
			}
			printStream.println();
			printStream.println("===========  print Mbranch[i][j]");
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Npline; j++)
				{
					printStream.printf("%6d", Mbranch[i][j]);
				}
				printStream.println();
			}
			printStream.println("===========  print Tnode[i][j]");
			printStream.println("====j=  ");
			printStream.print("      ");
			for (j = 0; j < NN; j++)
			{
				printStream.printf("%6d", j);
			}
			printStream.println();
			for (i = 0; i < NN; i++)
			{
				if (i < 10)
				{
					printStream.print("i=" + i + "   ");
				}
				else
				{
					printStream.print("i=" + i + "  ");
				}
				for (j = 0; j < NN; j++)
				{
					if (Tnode[i][j] < 0.0)
					{
						printStream.print("      ");
					}
					else
					{
						printStream.printf("%6.2f", Tnode[i][j]);
					}
				}
				printStream.println();
			}
			printStream.println();
			printStream.println("===========  print sumTnode[i][j]");
			printStream.print("==j=  ");
			for (j = 0; j < NN; j++)
			{
				printStream.printf("%6d", j);
			}
			printStream.println();
			for (i = 0; i < NN; i++)
			{
				printStream.print("i=" + i + "   ");
				for (j = 0; j < NN; j++)
				{
					if (sumTnode[i][j] <= 0.0)
					{
						printStream.print("      ");
					}
					else
					{
						printStream.printf("%6.2f", sumTnode[i][j]);
					}
				}
				printStream.println();
			}

			// ----------------���ܶ��ܷ����˿�(��)�ͻ�ˮ����(m3/sec)����------
			printStream.println();
			printStream.println("======  ��ˮ������̬ģ��   �˾�����ˮ���� " + q1 + "  m3   ʱ������ " + NT + "       �յ�ˮλ�� " + Hw_end + "  m  =====");

			// �˾���ˮ���仯����---discharge at every time step per head---
			for (it = 0; it < NT; it++)
			{
				qit[it] = q1 * Rf[it] / 100.0 / 3600;
			}
			printStream.println();
			printStream.println("    it     qit[it]");
			for (it = 0; it < NT; it++)
			{
				printStream.printf("%6d%12.6f", it, qit[it]);
				printStream.println();
			}
			printStream.println();
			for (it = 0; it < NT; it++)
			{
				dtnt = dt + dt * (float) (it);
				for (j = 0; j < NN; j++)
				{
					sumRj[it][j] = Rj[j];
					sumqj[it][j] = Rj[j] * qit[it];
					for (i = 0; i < NN; i++)
					{
						if (sumTnode[i][j] > 0 && sumTnode[i][j] < dtnt)
						{
							sumRj[it][j] = sumRj[it][j] + Rj[i];
							sumqj[it][j] = sumqj[it][j] + Rj[i] * qit[it];
						}
					}
				}
			}
			printStream.println("  sumRj[it][j]=");
			for (it = 0; it < NT; it++)
			{
				for (j = 0; j < NN; j++)
				{
					printStream.printf("%8.2f", sumRj[it][j]);
				}
				printStream.println();
			}
			printStream.println();
			printStream.println("  sumqj[it][j] x 1000 =");
			for (it = 0; it < NT; it++)
			{
				for (j = 0; j < NN; j++)
				{
					sumqjj = sumqj[it][j] * 1000.0;
					printStream.printf("%8.2f", sumqjj);
				}
				printStream.println();
			}
			printStream.println();
			for (it = 0; it < NT; it++)
			{
				for (i = 0; i < NN; i++)
				{
					overflow[it][i] = 0.0;
					Hw_over[it][i] = 0.0;
				}
			}
			for (it = 0; it < NT; it++)
			{
				for (j = 0; j < NP; j++)
				{
					qpt[it][j] = -99.0;
					qqkp[it][j] = 0.0;
				}
			}
			for (it = 0; it < NT; it++)
			{
				printStream.print(" it=" + it + "  qpt[it][k]=");
				for (j = 0; j < NN; j++)
				{
					for (k = 0; k < NP; k++)
					{
						if (I0[k] == j)
						{
							qpt[it][k] = sumqj[it][j];
							printStream.printf("%8.2f", qpt[it][k]);
						}
					}
				}
				printStream.println();
				for (ik = 0; ik < Nstart; ik++)
				{
					for (jk = 0; jk < Npline; jk++)
					{
						kp = Mbranch[ik][jk];
						if (kp >= 0)
						{
							if (J0[kp] == Nend)
							{
								Hwdw[it][kp] = Hw_end;
								if (Iprt == 1)
								{
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  Hw_end= " + Hw_end);
								}
							}
							else
							{
								for (k1 = 0; k1 < NP; k1++)
								{
									if (I0[k1] == J0[kp]) Hwdw[it][kp] = Hwup[it][k1];
								}
							}
							Ad0 = 0.7854 * Math.pow(dpl[kp], 2.0);
							hdj0 = ZJdw[kp] + dpl[kp];
							if (Hwdw[it][kp] >= hdj0)
							{
								if (Iprt == 1)
								{
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  ��û���� ");
								}
								hdcc0[it][kp] = 1.0;
								rid[it][kp] = dpl[kp] / 4.0;
								vpt[it][kp] = qpt[it][kp] / Ad0;
								slopt[it][kp] = 10.29 * Math.pow(slp[kp], 2.0) * Math.pow(qpt[it][kp], 2.0) / Math.pow(dpl[kp], 5.333);
								Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
								if (Hwup[it][kp] >= Hj[I0[kp]])
								{
									Hwup[it][kp] = Hj[I0[kp]];
									slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
									if (slopt[it][kp] < 0.0)
									{
										slopt[it][kp] = Math.abs(slopt[it][kp]);
									}
									vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt[it][kp], 0.5) / slp[kp];
									qqkp[it][kp] = vpt[it][kp] * Ad0;
									if (qqkp[it][kp] < 0.0)
									{
										qqkp[it][kp] = Math.abs(qqkp[it][kp]);
									}
								}
							}
							else
							{
								if (Iprt == 1)
								{
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdw= " + Hwdw[it][kp] + "  ����û���� ");
								}
								qkpmax = 2.699 * Math.pow(dpl[kp], 2.5);
								if (qpt[it][kp] > qkpmax)
								{
									if (Iprt == 1)
									{
										printStream.println("   it= " + it + "   kp= " + kp + "  qkpmax= " + qkpmax + "  ����û���ܳ��� ");
									}
									vpt[it][kp] = qpt[it][kp] / Ad0;
									// H00=pow(vpt[it][kp],2.0)/13.72;
									// Hwdw[it][kp]=ZJdw[kp]+dpl[kp]+H00;
									Hwdw[it][kp] = ZJdw[kp] + dpl[kp];
									hdcc0[it][kp] = 1.0;
									rid[it][kp] = dpl[kp] / 4.0;
									slopt[it][kp] = 10.29 * Math.pow(slp[kp], 2.0) * Math.pow(qpt[it][kp], 2.0) / Math.pow(dpl[kp], 5.333);
									Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
									if (Hwup[it][kp] >= Hj[I0[kp]])
									{
										Hwup[it][kp] = Hj[I0[kp]];
										slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
										if (slopt[it][kp] < 0.0)
										{
											slopt[it][kp] = Math.abs(slopt[it][kp]);
										}
										vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt[it][kp], 0.5) / slp[kp];
										qqkp[it][kp] = vpt[it][kp] * Ad0;
										if (qqkp[it][kp] < 0.0)
										{
											qqkp[it][kp] = Math.abs(qqkp[it][kp]);
										}
									}
								}
								else
								{
									if (Iprt == 1)
									{
										printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  ����û�����ܳ��� ");
									}
									// ==20161018�޸Ŀ�ʼ---�����ٽ�ˮ��򻯹�ʽ--------zhou-p21------
									ycd0 = qpt[it][kp] / 2.983 / Math.pow(dpl[kp], 2.5);
									hdcc0[it][kp] = Math.pow(ycd0, 0.513);
									sita = 2.0 * Math.acos(1.0 - 2.0 * hdcc0[it][kp]);
									rid[it][kp] = 0.25 * dpl[kp] * (sita - Math.sin(sita)) / sita;
									vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slop[kp], 0.5) / slp[kp];
								}
								Hwdwkp = ZJdw[kp] + hdcc0[it][kp] * dpl[kp];
								if (Hwdwkp >= Hwdw[it][kp])
								{
									Hwdw[it][kp] = Hwdwkp;
								}
								else
								{
									yykp = Hwdw[it][kp] - ZJdw[kp];
									if (yykp > dpl[kp])
									{
										yykp = dpl[kp];
									}
									sita = 2.0 * Math.acos(1.0 - 2.0 * yykp / dpl[kp]);
									hdcc0[it][kp] = yykp / dpl[kp];
									rid[it][kp] = 0.25 * dpl[kp] * (sita - Math.sin(sita)) / sita;
									vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slop[kp], 0.5) / slp[kp];
								}
								Hwup[it][kp] = Hwdw[it][kp] + slop[kp] * lp[kp];
							}
							if (Iprt == 1)
							{
								printStream.println("   it= " + it + "   kp= " + kp + "   I0[kp]= " + I0[kp] + "  Hwdm= " + Hwdw[it][kp] + "  Hwup= " + Hwup[it][kp] + "  Hj= " + Hj[I0[kp]] + "  hdcc0= " + hdcc0[it][kp] + "  qpt= " + qpt[it][kp] + "  qqkp= " + qqkp[it][kp] + "  vpt= " + vpt[it][kp]);
							}
						}
					}
				}
				printStream.println();
				printStream.println("    it   �ܶκ�  I0   J0 �ܾ�dpl     �ܶ�qp ˮ���뾶R  ������ ����(m/s)  ����ˮλ  ����ˮλ  �Ϲܵ׸�  �¹ܵ׸�  �ܶ��¶�  �ϵ����");
				for (i = 0; i < NP; i++)
				{
					printStream.printf("%6d%6d%6d%5d%8.2f%12.3f%10.3f%8.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.5f%10.3f", it, i, I0[i], J0[i], dpl[i], qpt[it][i], rid[it][i], hdcc0[it][i], vpt[it][i], Hwup[it][i], Hwdw[it][i], ZJup[i], ZJdw[i], slop[i], Hj[I0[i]]);
					printStream.println();
				}
				printStream.println();
				for (i = 0; i < NP; i++)
				{
					k = J0[i];
					if (k == Nend)
					{
						Hwj[it][k] = Hwdw[it][i];
					}
					{
						j = I0[i];
						Hwj[it][j] = Hwup[it][i];
						if (Hwup[it][i] == Hj[j])
						{
							overflow[it][j] = overflow[it - 1][j] + (qpt[it][i] - qqkp[it][i]) * dt * 60.0;
							Hw_over[it][j] = csf * overflow[it][j] / Aj[j] / 10000.0 * 1000.0;

						}
						if (Hwup[it][i] < Hj[j] && overflow[it][j] > 0.0)
						{
							overflow[it][j] = overflow[it - 1][j] * 0.90;
							Hw_over[it][j] = csf * overflow[it][j] / Aj[j] / 10000.0 * 1000.0;
						}
					}
					if (Hw_over[it][j] <= 5.0)
					{
						overflow[it][j] = 0.0;
						Hw_over[it][j] = 0.0;
					}
				}
			}
			// System.out.println("------ ģ�ͼ���ȫ����� ------");
			// --------------- ����ܶγ����ȼ����� ---------------
			printStream.println(" ======== ʱ�ιܶγ����� ========");
			Nprt = NP / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NP)
					{
						iprt2 = NP;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						printStream.printf("%8.3f", hdcc0[it][i]);
					}
					printStream.println();
				}
			}
			// --------------------- ����ڵ�ˮλ������ ---------------
			printStream.println(" ======== ʱ�νڵ�ˮλ ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NN)
					{
						iprt2 = NN;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						printStream.printf("%8.2f", Hwj[it][i]);
					}
					printStream.println();
				}
			}
			// ***********��֯���ݣ�����ҳ��������ʾ********
			for (it = 0; it < NT; it++)
			{
				String SewageLevNew = "";
				for (i = 0; i < NN; i++)
				{
					if (gjId != null && i == SubgjId)
					{
						SewageAccGj += df1.format(Hwj[it][i]) + "|";
					}
					SewageLevNew += df1.format(Hwj[it][i]) + "|";
				}
				SewageLev[it] = SewageLevNew;
			}
			// *************************************
			// ---------------- ����ڵ����������� ---------------
			printStream.println(" ======== ʱ�νڵ��ˮ��(m3) ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NN)
					{
						iprt2 = NN;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}

					for (i = iprt1; i < iprt2; i++)
					{
						if (overflow[it][i] <= 0.0)
						{
							printStream.print("        ");
						}
						else
						{
							printStream.printf("%8.2f", overflow[it][i]);
						}
					}
					printStream.println();
				}
			}
			for (it = 0; it < NT; it++)
			{
				String SewageAccNew = "";
				for (i = 0; i < NN; i++)
				{
					if (overflow[it][i] <= 0.0)
					{
						printStream.print("        ");
						SewageAccNew += 0 + "|";
					}
					else
					{
						printStream.printf("%8.2f", overflow[it][i]);
						SewageAccNew += df1.format(overflow[it][i]) + "|";
					}
				}
				SewageAcc[it] = SewageAccNew;
			}
			printStream.println(" ======== ʱ�νڵ��ˮ���(mm) ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NN)
					{
						iprt2 = NN;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.println(" " + it + "   ");
					}
					else
					{
						printStream.println(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						if (overflow[it][i] <= 0.0)
						{
							printStream.print("        ");
						}
						else
						{
							printStream.printf("%8.2f", Hw_over[it][i]);
						}
					}
					printStream.println();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return gjName + "," + Count;
		}
		if (AnalogWaterType.equals("SewageAccGj"))
		{
			return SewageAccGj;
		}
		else if (AnalogWaterType.equals("SewageAcc"))
		{
			String SewageAccList = "";
			for (int i = 0; i < SewageAcc.length; i++)
			{
				SewageAccList += subSys.substring(7, 12) + SewageAcc[i] + ";";
			}
			return SewageAccList;
		}
		else if (AnalogWaterType.equals("SewageLev"))
		{
			String SewageLevList = "";
			for (int i = 0; i < SewageLev.length; i++)
			{
				SewageLevList += subSys.substring(7, 12) + SewageLev[i] + ";";
			}
			return SewageLevList;
		}
		return "";
	}

	// ģ�����۵ڶ���
	// �ر�˵�����͵�һ�װ汾�����ݱ��һ��
	private String analog_W2(String subSys, int timePeriod, String gjId, String AnalogWaterType)
	{
		SewageAcc = new String[24];
		SewageLev = new String[24];
		SewageAccGj = "";
		int SubgjId = 0;
		if (gjId != null)
		{
			SubgjId = CommUtil.StrToInt(gjId.substring(12, 15)) - 1;
		}
		try
		{
			// subSys = 900001_WJ001
			// �����������ݣ�
			// �ܶ������ڵ������ܵ��������·�����ܶ�����ģ��ʱ����,
			// �ܵ�·������·�����ڵ������յ�ڵ�ţ��м�������ļ�ָ�룬������ݱ�����
			int NP = 9, NN = 10, Nstart = 3, Npline = 7, NT = 24, Ncol = 5, NR = 18, Nr_node = 8, Nend = 7, Iprt = 0, Nprtc = 20;
			// ��ˮ��������
			// �˾�����ˮ����m/d��, �ܶ��趨����vp0��ʱ�䲽����h������ϵͳ�յ�ˮλ�����氼͹ϵ��csf
			double q1 = 0.45, vp0 = 0.8, dt = 1.0, Hw_end = 3.0, csf = 1.0;
			// �ڵ�������(ha)�� �ڵ�����ߣ�m�����ڵ�����˿�(��)
			double[] Aj;
			double[] Hj;
			double[] Rj;
			// ��ˮ���仯���ߣ�NT��
			double[] Rf;
			// ����·������·���ڵ��(��99��ʾ�սڵ�)
			int[][] Mroute;
			int[][] Mbranch;
			// �ܶ����νڵ��I0,���νڵ��J0���ܶγ���(m),Ħ��ϵ��
			int[] I0;
			int[] J0;
			double[] lp;
			double[] slp;
			// �ڵ�����
			int[] Mstart;
			// �ܶ�ֱ��(m)�����ιܵ׸߳�(m)�����ιܵ׸߳�(m)
			double[] dpl;
			double[] ZJup;
			double[] ZJdw;

			// �����ֲ�֧�߹ܶξ���-��������
			int[] Npjun = new int[NP];
			int[][] MNP = new int[NN][Ncol];

			this.FileSaveRoute = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogData/";
			String XlsPath = "";
			if (gjId != null)
			{
				XlsPath = FileSaveRoute + gjId.substring(0, 12) + ".xls";
				gjName = gjId.substring(0, 12);
			}
			else
			{
				XlsPath = FileSaveRoute + subSys + ".xls";
				gjName = subSys;
			}
			InputStream is = new FileInputStream(XlsPath);
			Workbook rwb = Workbook.getWorkbook(is);
			Sheet rs = rwb.getSheet(0);
			int rsRows = rs.getRows();

			/*
			 * �������ݱ����ϵͳ�� �ڵ���NN �ܶ���NP �����NStart ·���ܶ���Npline ·���ڵ���Nr_node
			 * �յ���ں�Nend ģ��ʱ��NT �ܶ�·����NrouteYJ002 10 9 3 7 8 8 60 3
			 */
			int rowCnt = 2;
			String sysName = rs.getCell(0, rowCnt).getContents().trim();
			NN = Integer.parseInt(rs.getCell(1, rowCnt).getContents().trim());
			NP = Integer.parseInt(rs.getCell(2, rowCnt).getContents().trim());
			Nstart = Integer.parseInt(rs.getCell(3, rowCnt).getContents().trim());
			Npline = Integer.parseInt(rs.getCell(4, rowCnt).getContents().trim());
			Nr_node = Integer.parseInt(rs.getCell(5, rowCnt).getContents().trim());
			Nend = Integer.parseInt(rs.getCell(6, rowCnt).getContents().trim());
			NT = Integer.parseInt(rs.getCell(7, rowCnt).getContents().trim());
			Ncol = Integer.parseInt(rs.getCell(8, rowCnt).getContents().trim());
			NR = Integer.parseInt(rs.getCell(9, rowCnt).getContents().trim());
			rowCnt += 4;

			q1 = Double.parseDouble(rs.getCell(0, rowCnt).getContents().trim());
			vp0 = Double.parseDouble(rs.getCell(1, rowCnt).getContents().trim());
			dt = Double.parseDouble(rs.getCell(2, rowCnt).getContents().trim());
			Hw_end = Double.parseDouble(rs.getCell(3, rowCnt).getContents().trim());
			csf = Double.parseDouble(rs.getCell(4, rowCnt).getContents().trim());
			rowCnt += 4;

			/*
			 * ��ϵͳ�ܶ����ݱ�� Pipe.No ����I0 �յ��J0 ����LP ֱ��DP Ħ��ϵ�� ��˱�� �ն˱�� 1 0 1 28.5
			 * 0.3 0.017 3.894 3.842 2 1 2 32 0.3 0.017 3.842 3.784 3 2 3 28.6
			 * 0.3 0.017 3.784 3.733 4 3 4 25.4 0.3 0.017 3.733 3.687 5 4 5 24.7
			 * 0.3 0.017 3.687 3.643 6 5 6 23.5 0.3 0.017 3.643 3.601 7 6 7 30.4
			 * 0.3 0.017 3.601 3.546 8 8 7 15.5 0.3 0.017 3.731 3.171 9 9 6 4.3
			 * 0.3 0.017 3.886 3.7
			 */
			I0 = new int[NP];
			J0 = new int[NP];
			lp = new double[NP];
			dpl = new double[NP];
			slp = new double[NP];
			ZJup = new double[NP];
			ZJdw = new double[NP];
			for (int j = 0; j < NP; j++)
			{
				I0[j] = Integer.parseInt(rs.getCell(1, rowCnt + j).getContents().trim());
				J0[j] = Integer.parseInt(rs.getCell(2, rowCnt + j).getContents().trim());
				lp[j] = Double.parseDouble(rs.getCell(3, rowCnt + j).getContents().trim());
				dpl[j] = Double.parseDouble(rs.getCell(4, rowCnt + j).getContents().trim());
				slp[j] = Double.parseDouble(rs.getCell(5, rowCnt + j).getContents().trim());
				ZJup[j] = Double.parseDouble(rs.getCell(6, rowCnt + j).getContents().trim());
				ZJdw[j] = Double.parseDouble(rs.getCell(7, rowCnt + j).getContents().trim());
			}
			rowCnt += NP;
			rowCnt += 3;

			/*
			 * ��ϵͳ�ڵ����ݱ�� �ڵ�No �������Aj ������ �ڵ�����˿� 1 0.2 5.244 80 2 0.2 5.191 80 3
			 * 0.2 5.177 80 4 0.2 5.208 80 5 0.2 5.221 80 6 0.2 5.201 80 7 0.2
			 * 5.2 80 8 0.2 5.121 80 9 0.2 5.131 80 10 0.2 5.186 80
			 */
			Aj = new double[NN];
			Hj = new double[NN];
			Rj = new double[NN];
			for (int j = 0; j < NN; j++)
			{
				Aj[j] = Double.parseDouble(rs.getCell(1, rowCnt + j).getContents().trim());
				Hj[j] = Double.parseDouble(rs.getCell(2, rowCnt + j).getContents().trim());
				Rj[j] = Double.parseDouble(rs.getCell(3, rowCnt + j).getContents().trim());
			}
			rowCnt += NN;
			rowCnt += 3;

			Mroute = new int[Nstart][Nr_node];
			/**
			 * ����·������ ��� 1 2 3 ���� 0 8 9
			 */
			// ************��һ�汾��ȥ��*******
			Mstart = new int[Nstart];
			/**
			 * for (int j = 0; j < Nstart; j++) { Mstart[j] =
			 * Integer.parseInt(rs.getCell(j + 1, rowCnt).getContents().trim());
			 * } rowCnt += 1; rowCnt += 3;
			 */
			// ***************************
			/*
			 * ��ϵͳ��֧·���ܶ����ݾ��� ���� �ڵ���� 1 2 3 4 5 6 7 1 6 5 4 3 2 1 0 2 7 -99 -99
			 * -99 -99 -99 -99 3 8 -99 -99 -99 -99 -99 -99
			 */
			Mbranch = new int[Nstart][Npline];
			// *****��һ�汾��ȥ��*********
			/**
			 * for (int j = 0; j < Nstart; j++) { for (int k = 0; k < Npline;
			 * k++) { Mbranch[j][k] = Integer.parseInt(rs.getCell(k + 1, rowCnt
			 * + j).getContents().trim()); } } rowCnt += Nstart; rowCnt += 3;
			 **/
			// **********************
			/*
			 * ��ˮ���仯���� ʱ�� 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21
			 * 22 23 24 ���� 2.12 2.19 2.18 2.8 3.21 3.9 5.2 5.62 5.63 5.08 5.12
			 * 5.69 5.28 4.52 4.51 4.58 5.5 5.62 5.13 5.18 3.4 3.12 2.22 2.2
			 */
			Rf = new double[NT];
			for (int j = 0; j < NT; j++)
			{
				Rf[j] = Double.parseDouble(rs.getCell(j + 1, rowCnt).getContents().trim());
			}
			// ----�м�ָ�����----
			int i00, j00 = 0, Ni0, Ni1, jj, jp0, inp = 0, jpp, NPP;
			// ----�м����----
			int i, j, k, ik, jk, jjj, INS, it, k1, kp, in1, in2, in3, NR1, NR2, ii, Nprt, iprt1, iprt2;
			double Ad0, qkpmax, Hwdwkp, H00, ycd0, yykp, sita;
			double dtnt, taa, tbb, AA, XX1, XX2, sumqjj, hdj0;
			double[] qit = new double[NT];
			double[][] sumqj = new double[NT][NN];
			double[][] sumRj = new double[NT][NN];
			double[][] Tnode = new double[NN][NN];
			double[][] sumTnode = new double[NN][NN];
			// taa,tbb,AA,XX1,XX2,XX[NT],
			double[] vp = new double[NP];
			double[] slop = new double[NP];
			double[][] qpt = new double[NT][NP];
			double[][] qqkp = new double[NT][NP];
			double[][] vpt = new double[NT][NP];
			double[][] rid = new double[NT][NP];
			double[][] slopt = new double[NT][NP];
			double[][] Hwup = new double[NT][NP];
			double[][] Hwdw = new double[NT][NP];
			double[][] hdcc0 = new double[NT][NP];
			double[][] overflow = new double[NT][NN];
			double[][] Hw_over = new double[NT][NN];
			double[][] Hwj = new double[NT][NN];

			DecimalFormat df = new DecimalFormat("##.####");
			DecimalFormat df1 = new DecimalFormat("######.##");
			String FilePath = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogValue/";
			String FileName = subSys + ".txt";
			FileOutputStream fs = new FileOutputStream(new File(FilePath + FileName));
			PrintStream printStream = new PrintStream(fs);
			printStream.println("20161030-��ˮ����ģ��-���ҳ�-3.txt");
			// System.out.println("------ ��ˮ����ģ��-���ҳ� ------");
			// ================= ����ֵ ===============================
			for (i = 0; i < NT; i++)
			{
				for (j = 0; j < NN; j++)
					sumRj[i][j] = 0;
			}
			for (i = 0; i < NT; i++)
			{
				for (j = 0; j < NN; j++)
					sumqj[i][j] = 0;
			}
			for (i = 0; i < NN; i++)
			{
				for (j = 0; j < NN; j++)
				{
					if (i == j)
					{
						Tnode[i][j] = 0;
					}
					else
					{
						Tnode[i][j] = -99;
					}
				}
			}
			for (i = 0; i < NN; i++)
			{
				for (j = 0; j < NN; j++)
					sumTnode[i][j] = 0;
			}
			// ====20161106===== ���ɾ��� MNP[i][j] ====
			for (i = 0; i < NN; i++)
			{
				for (j = 0; j < Ncol; j++)
				{
					MNP[i][j] = 0;
				}
				MNP[i][0] = i;
				jj = 2;
				for (k = 0; k < NP; k++)
				{
					if (J0[k] == i)
					{
						jj = jj + 1;
						MNP[i][1] = MNP[i][1] + 1;
						MNP[i][jj] = k;
					}
					if (I0[k] == i)
					{
						MNP[i][2] = MNP[i][2] + 1;
					}
				}
			}
			// System.out.println("===========  print MNP[i][j]");
			printStream.println("===========  print MNP[i][j]");
			for (i = 0; i < NN; i++)
			{
				for (j = 0; j < Ncol; j++)
				{
					printStream.printf("%6d", MNP[i][j]);
				}
				printStream.println();

			}
			// ----- MNP[i][j] ���� ------
			// ====20161112===== ���ɾ��� Mstart[i] ====
			jj = -1;
			for (i = 0; i < NN; i++)
			{
				if (MNP[i][1] == 0)
				{
					jj = jj + 1;
					Mstart[jj] = i;
				}
			}
			printStream.println("===========  print Mstart[i]");
			for (i = 0; i < Nstart; i++)
			{
				printStream.printf("%6d", Mstart[i]);
			}
			printStream.println();
			//
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Nr_node; j++)
					Mroute[i][j] = -99;
			}
			for (i = 0; i < Nstart; i++)
				Mroute[i][0] = Mstart[i];
			for (i = 0; i < Nstart; i++)
			{
				for (j = 1; j < Nr_node; j++)
				{
					for (k = 0; k < NP; k++)
					{
						if (I0[k] == Mroute[i][j - 1])
						{
							Mroute[i][j] = J0[k];
						}
					}
				}
			}
			//
			// ====20161106===== ���ɾ���Mbranch[i][j] ====
			for (i = 0; i < NP; i++)
			{
				Npjun[i] = 1;
			}
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Npline; j++)
				{
					Mbranch[i][j] = -99;
				}
			}
			i00 = -1;
			NPP = 0;
			// L200:
			while (true)
			{
				for (i = 0; i < NN; i++)
				{
					if (MNP[i][2] == 0 && MNP[i][1] > 0)
					{
						jj = 2;
						Ni1 = MNP[i][1];
						for (j = 0; j < Ni1; j++)
						{
							jj = jj + 1;
							jp0 = MNP[i][jj];
							if (Npjun[jp0] > 0)
							{
								i00 = i00 + 1;
								j00 = 0;
								Mbranch[i00][j00] = jp0;
								inp = I0[jp0];
								Npjun[jp0] = -99;
								NPP = NPP + 1;
							}
							// L100:
							while (true)
							{
								INS = 1;
								for (jjj = 0; jjj < Nstart; jjj++)
								{
									if (Mstart[jjj] == inp) INS = 0;
								}
								if (INS > 0)
								{
									for (jpp = 0; jpp < NP; jpp++)
									{
										if (J0[jpp] == inp && Npjun[jpp] > 0)
										{
											j00 = j00 + 1;
											Mbranch[i00][j00] = jpp;
											inp = I0[jpp];
											Npjun[jpp] = -99;
											NPP = NPP + 1;
											// goto L100;
											break;
										}
										else
										{
											continue;
										}
									}
								} // --- end of if(INS>0) ---
								else
								{
									break;
								}
							}
						} // --- end of for(j=0;j<Ni1;j++) ---
					} // --- end of if(MNP[i][2]==0 && MNP[i][1]>0) ---
					MNP[i][2] = -99;
				}// --- end of for(i=0;i<NN;1++) ---
				for (i = 0; i < NN; i++)
				{
					for (j = 0; j < NP; j++)
					{
						if (I0[j] == i && Npjun[j] < 0)
						{
							MNP[i][2] = 0;
						}
					}
				}
				// if (NPP < NP) goto L200;
				if (NPP >= NP)
				{
					break;
				}
			}
			// === ���ɾ��� Mbranch[i][j] ����====
			// ==================Tnode-sumTnode=========================
			for (i = 0; i < NP; i++)
				vp[i] = vp0;
			for (kp = 0; kp < NP; kp++)
			{
				in1 = I0[kp];
				in2 = J0[kp];
				Tnode[in1][in2] = lp[kp] / vp[kp] / 60;
				slop[kp] = (ZJup[kp] - ZJdw[kp]) / lp[kp];
			}
			//
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Nr_node; j++)
				{
					in1 = Mroute[i][j];
					if (in1 >= 0)
					{
						for (k = j + 1; k < Nr_node; k++)
						{
							in2 = Mroute[i][k - 1];
							in3 = Mroute[i][k];
							if (in3 >= 0)
							{
								sumTnode[in1][in3] = sumTnode[in1][in2] + Tnode[in2][in3];
							}
						}
					}
				}
			}
			// =====print Mroute[i][j], Tnode, sumTnode,Mbranch[i][j]====
			// System.out.println("pipe no.  I0    J0");
			for (i = 0; i < NP; i++)
			{
				// System.out.printf("%6d%6d%6d", i, I0[i], J0[i]);
				// System.out.println();
			}
			printStream.println();
			printStream.println("=====print pipe no.  I0    J0=====");
			printStream.print(" ip=");

			for (i = 0; i < NP; i++)
			{
				printStream.printf("%4d", i);
			}
			printStream.println();
			printStream.print(" I0=");
			for (i = 0; i < NP; i++)
			{
				printStream.printf("%4d", I0[i]);
			}
			printStream.println();
			printStream.print(" J0=");
			for (i = 0; i < NP; i++)
			{
				printStream.printf("%4d", J0[i]);
			}
			printStream.println();
			printStream.println();
			printStream.println("===========  print Mroute[i][j]");
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Nr_node; j++)
				{
					printStream.printf("%6d", Mroute[i][j]);
				}
				printStream.println();
			}
			printStream.println();
			printStream.println("===========  print Mbranch[i][j]");
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Npline; j++)
				{
					printStream.printf("%6d", Mbranch[i][j]);
				}
				printStream.println();
			}
			printStream.println("===========  print Tnode[i][j]");
			printStream.println("====j=  ");
			printStream.print("      ");
			for (j = 0; j < NN; j++)
			{
				printStream.printf("%6d", j);
			}
			printStream.println();
			for (i = 0; i < NN; i++)
			{
				if (i < 10)
				{
					printStream.print("i=" + i + "   ");
				}
				else
				{
					printStream.print("i=" + i + "  ");
				}
				for (j = 0; j < NN; j++)
				{
					if (Tnode[i][j] < 0.0)
					{
						printStream.print("      ");
					}
					else
					{
						printStream.printf("%6.2f", Tnode[i][j]);
					}
				}
				printStream.println();
			}
			printStream.println();
			printStream.println("===========  print sumTnode[i][j]");
			printStream.print("==j=  ");
			for (j = 0; j < NN; j++)
			{
				printStream.printf("%6d", j);
			}
			printStream.println();
			for (i = 0; i < NN; i++)
			{
				printStream.print("i=" + i + "   ");
				for (j = 0; j < NN; j++)
				{
					if (sumTnode[i][j] <= 0.0)
					{
						printStream.print("      ");
					}
					else
					{
						printStream.printf("%6.2f", sumTnode[i][j]);
					}
				}
				printStream.println();
			}
			// ================= ����׼��̬����ģ��============================
			// -------------------��̬ģ����������-----------------------------
			// ----------------���ܶ��ܷ����˿�(��)�ͻ�ˮ����(m3/sec)����------
			printStream.println();
			printStream.println("======  ��ˮ������̬ģ��   �˾�����ˮ���� " + q1 + "  m3   ʱ������ " + NT + "       �յ�ˮλ�� " + Hw_end + "  m  =====");
			// xxxxxxx
			// �˾���ˮ���仯����---discharge at every time step per head---
			for (it = 0; it < NT; it++)
			{
				qit[it] = q1 * Rf[it] / 100.0 / 3600;
			}
			printStream.println();
			printStream.println("    it     qit[it] ��m3/cap-sec��");
			for (it = 0; it < NT; it++)
			{
				printStream.printf("%6d%12.6f", it, qit[it]);
				printStream.println();
			}
			printStream.println();
			for (it = 0; it < NT; it++)
			{
				dtnt = dt + dt * (float) (it);
				for (j = 0; j < NN; j++)
				{
					sumRj[it][j] = Rj[j];
					sumqj[it][j] = Rj[j] * qit[it];
					for (i = 0; i < NN; i++)
					{
						if (sumTnode[i][j] > 0 && sumTnode[i][j] < dtnt)
						{
							sumRj[it][j] = sumRj[it][j] + Rj[i];
							sumqj[it][j] = sumqj[it][j] + Rj[i] * qit[it];
						}
					}
				}
			}
			// print sumRj[it][j] and sumqj[it][j]
			printStream.println("  sumRj[it][j]=");
			for (it = 0; it < NT; it++)
			{
				for (j = 0; j < NN; j++)
				{
					printStream.printf("%8.2f", sumRj[it][j]);
				}
				printStream.println();
			}
			printStream.println();
			printStream.println("  sumqj[it][j] x 1000 =");
			for (it = 0; it < NT; it++)
			{
				for (j = 0; j < NN; j++)
				{
					sumqjj = sumqj[it][j] * 1000.0;
					printStream.printf("%8.2f", sumqjj);
				}
				printStream.println();
			}
			printStream.println();
			// -------------�ܶ�ˮ�����㿪ʼ--------------
			for (it = 0; it < NT; it++)
			{
				for (i = 0; i < NN; i++)
				{
					overflow[it][i] = 0.0;
					Hw_over[it][i] = 0.0;
				}
			}
			for (it = 0; it < NT; it++)
			{
				for (j = 0; j < NP; j++)
				{
					qpt[it][j] = -99.0;
					qqkp[it][j] = 0.0;
				}
			}
			// ------------------------------------------
			for (it = 0; it < NT; it++)
			// --1--
			{
				printStream.print(" it=" + it + "  qpt[it][k]=");

				for (j = 0; j < NN; j++)
				{
					for (k = 0; k < NP; k++)
					{
						if (I0[k] == j)
						{
							qpt[it][k] = sumqj[it][j];
							printStream.printf("%8.2f", qpt[it][k]);
						}
					}
				}
				printStream.println();
				// -------------------20090127-sql����------------------------
				for (ik = 0; ik < Nstart; ik++)
				// --2--
				{
					for (jk = 0; jk < Npline; jk++)
					// --3--
					{
						kp = Mbranch[ik][jk];
						if (kp >= 0)
						// --4--
						{
							if (J0[kp] == Nend)
							{
								Hwdw[it][kp] = Hw_end;
								if (Iprt == 1)
								{
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  Hw_end= " + Hw_end);
								}

							}
							else
							{
								for (k1 = 0; k1 < NP; k1++)
								{
									if (I0[k1] == J0[kp]) Hwdw[it][kp] = Hwup[it][k1];
								}
							}
							Ad0 = 0.7854 * Math.pow(dpl[kp], 2.0);
							hdj0 = ZJdw[kp] + dpl[kp];
							if (Hwdw[it][kp] >= hdj0)
							{
								if (Iprt == 1)
								{
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  ��û���� ");
								}

								hdcc0[it][kp] = 1.0;
								rid[it][kp] = dpl[kp] / 4.0;
								vpt[it][kp] = qpt[it][kp] / Ad0;
								slopt[it][kp] = 10.29 * Math.pow(slp[kp], 2.0) * Math.pow(qpt[it][kp], 2.0) / Math.pow(dpl[kp], 5.333);
								Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
								if (Hwup[it][kp] >= Hj[I0[kp]])
								{
									Hwup[it][kp] = Hj[I0[kp]];
									slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
									if (slopt[it][kp] < 0.0)
									{
										slopt[it][kp] = Math.abs(slopt[it][kp]);
									}
									vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt[it][kp], 0.5) / slp[kp];
									qqkp[it][kp] = vpt[it][kp] * Ad0;
									if (qqkp[it][kp] < 0.0)
									{
										qqkp[it][kp] = Math.abs(qqkp[it][kp]);
									}
								}
							}
							else
							// --5--
							{
								if (Iprt == 1)
								{
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdw= " + Hwdw[it][kp] + "  ����û���� ");
								}
								// --20161018�޸Ŀ�ʼ---�����ٽ�ˮ����㷨--------
								qkpmax = 2.699 * Math.pow(dpl[kp], 2.5);
								if (qpt[it][kp] > qkpmax)
								{
									if (Iprt == 1)
									{
										printStream.println("   it= " + it + "   kp= " + kp + "  qkpmax= " + qkpmax + "  ����û���ܳ��� ");
									}
									vpt[it][kp] = qpt[it][kp] / Ad0;
									// H00=pow(vpt[it][kp],2.0)/13.72;
									// Hwdw[it][kp]=ZJdw[kp]+dpl[kp]+H00;
									Hwdw[it][kp] = ZJdw[kp] + dpl[kp];
									hdcc0[it][kp] = 1.0;
									rid[it][kp] = dpl[kp] / 4.0;
									slopt[it][kp] = 10.29 * Math.pow(slp[kp], 2.0) * Math.pow(qpt[it][kp], 2.0) / Math.pow(dpl[kp], 5.333);
									Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
									if (Hwup[it][kp] >= Hj[I0[kp]])
									{
										Hwup[it][kp] = Hj[I0[kp]];
										slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
										if (slopt[it][kp] < 0.0)
										{
											slopt[it][kp] = Math.abs(slopt[it][kp]);
										}
										vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt[it][kp], 0.5) / slp[kp];
										qqkp[it][kp] = vpt[it][kp] * Ad0;
										if (qqkp[it][kp] < 0.0)
										{
											qqkp[it][kp] = Math.abs(qqkp[it][kp]);
										}
									}
								}
								else
								{
									if (Iprt == 1)
									{
										printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  ����û�����ܳ��� ");
									}
									// ==20161115�޸�---���þ���������ˮ��򻯹�ʽ��ʼ--------
									ycd0 = 20.1538 * slp[kp] * qpt[it][kp] / Math.pow(dpl[kp], 2.6667) / Math.pow(slop[kp], 0.5);
									if (ycd0 <= 1.5)
									{
										hdcc0[it][kp] = 0.27 * Math.pow(ycd0, 0.485);
									}
									else
									{
										hdcc0[it][kp] = 0.098 * ycd0 + 0.19;
									}
									if (hdcc0[it][kp] > 1.0)
									{
										hdcc0[it][kp] = 1.0;
									}
									// ==20161115�޸�---���þ���������ˮ��򻯹�ʽ����--------
									sita = 2.0 * Math.acos(1.0 - 2.0 * hdcc0[it][kp]);
									rid[it][kp] = 0.25 * dpl[kp] * (sita - Math.sin(sita)) / sita;
									vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slop[kp], 0.5) / slp[kp];
								}
								// ---if(qpt[it][kp]>qkpmax)����---
								Hwdwkp = ZJdw[kp] + hdcc0[it][kp] * dpl[kp];
								if (Hwdwkp >= Hwdw[it][kp])
								{
									Hwdw[it][kp] = Hwdwkp;
								}
								else
								{
									yykp = Hwdw[it][kp] - ZJdw[kp];
									if (yykp > dpl[kp])
									{
										yykp = dpl[kp];
									}
									sita = 2.0 * Math.acos(1.0 - 2.0 * yykp / dpl[kp]);
									hdcc0[it][kp] = yykp / dpl[kp];
									rid[it][kp] = 0.25 * dpl[kp] * (sita - Math.sin(sita)) / sita;
									vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slop[kp], 0.5) / slp[kp];
								}
								Hwup[it][kp] = Hwdw[it][kp] + slop[kp] * lp[kp];
							} // 5--end
								// ------- ���it������ ----------
							if (Iprt == 1)
							{
								printStream.println("   it= " + it + "   kp= " + kp + "   I0[kp]= " + I0[kp] + "  Hwdm= " + Hwdw[it][kp] + "  Hwup= " + Hwup[it][kp] + "  Hj= " + Hj[I0[kp]] + "  hdcc0= " + hdcc0[it][kp] + "  qpt= " + qpt[it][kp] + "  qqkp= " + qqkp[it][kp] + "  vpt= " + vpt[it][kp]);
							}

						}// --4 if(kp>=0) end
					}// --3 ---jk end
				}// --2---ik end
				printStream.println();
				printStream.println("    it   �ܶκ�  I0   J0 �ܾ�dpl     �ܶ�qp ˮ���뾶R  ������ ����(m/s)  ����ˮλ  ����ˮλ  �Ϲܵ׸�  �¹ܵ׸�  �ܶ��¶�  �ϵ����");
				for (i = 0; i < NP; i++)
				{
					printStream.printf("%6d%6d%6d%5d%8.2f%12.3f%10.3f%8.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.5f%10.3f", it, i, I0[i], J0[i], dpl[i], qpt[it][i], rid[it][i], hdcc0[it][i], vpt[it][i], Hwup[it][i], Hwdw[it][i], ZJup[i], ZJdw[i], slop[i], Hj[I0[i]]);
					printStream.println();
				}
				printStream.println();
				// -------- ����ڵ�ˮλ-�ڵ��ˮ���ͻ�ˮ��� ---------
				for (i = 0; i < NP; i++)
				{
					k = J0[i];
					if (k == Nend)
					{
						Hwj[it][k] = Hwdw[it][i];
					}
					{
						j = I0[i];
						Hwj[it][j] = Hwup[it][i];
						if (Hwup[it][i] == Hj[j])
						{
							overflow[it][j] = overflow[it - 1][j] + (qpt[it][i] - qqkp[it][i]) * dt * 60.0;
							Hw_over[it][j] = csf * overflow[it][j] / Aj[j] / 10000.0 * 1000.0;

						}
						if (Hwup[it][i] < Hj[j] && it > 0 && overflow[it - 1][j] > 0.0)
						{
							overflow[it][j] = overflow[it - 1][j] * 0.90;
							Hw_over[it][j] = csf * overflow[it][j] / Aj[j] / 10000.0 * 1000.0;
						}
					}
					if (it > NR && Hw_over[it][j] <= 5.0)
					{
						overflow[it][j] = 0.0;
						Hw_over[it][j] = 0.0;
					}
				}
				// ------------ ���������ڵ���� ----
			}// 1-- it end ---
				// --------------��Ļ����������------
				// System.out.println("------ ģ�ͼ���ȫ����� ------");
				// -------------- ����ܶγ����ȼ����� ---------------
			printStream.println(" ======== ʱ�ιܶγ����� ========");
			Nprt = NP / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NP)
					{
						iprt2 = NP;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						printStream.printf("%8.3f", hdcc0[it][i]);
					}
					printStream.println();
				}
			}
			//
			// ---------- ����ڵ�ˮλ������ -------------
			printStream.println(" ======== ʱ�νڵ�ˮλ ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NN)
					{
						iprt2 = NN;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						printStream.printf("%8.2f", Hwj[it][i]);
					}
					printStream.println();
				}
			}
			// ***********��֯���ݣ�����ҳ��������ʾ********
			for (it = 0; it < NT; it++)
			{
				String SewageLevNew = "";
				for (i = 0; i < NN; i++)
				{
					if (gjId != null && i == SubgjId)
					{
						SewageAccGj += df1.format(Hwj[it][i]) + "|";
					}
					SewageLevNew += df1.format(Hwj[it][i]) + "|";
				}
				SewageLev[it] = SewageLevNew;
			}
			// **************************************
			// ------------ ����ڵ����������� --------------
			printStream.println(" ======== ʱ�νڵ��ˮ��(m3) ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NN)
					{
						iprt2 = NN;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						if (overflow[it][i] <= 0.0)
						{
							printStream.print("        ");
						}
						else
						{
							printStream.printf("%8.2f", overflow[it][i]);
						}
					}
					printStream.println();
				}
			}
			// ***********��֯���ݣ�����ҳ��������ʾ********
			for (it = 0; it < NT; it++)
			{
				String SewageAccNew = "";
				for (i = 0; i < NN; i++)
				{
					if (overflow[it][i] <= 0.0)
					{
						printStream.print("        ");
						SewageAccNew += 0 + "|";
					}
					else
					{
						printStream.printf("%8.2f", overflow[it][i]);
						SewageAccNew += df1.format(overflow[it][i]) + "|";
					}
				}
				SewageAcc[it] = SewageAccNew;
			}
			// *********************************
			printStream.println(" ======== ʱ�νڵ��ˮ���(mm) ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NN)
					{
						iprt2 = NN;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						if (overflow[it][i] <= 0.0)
						{
							printStream.print("        ");
						}
						else
						{
							printStream.printf("%8.2f", Hw_over[it][i]);
						}
					}
					printStream.println();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return gjName + "," + Count;
		}
		if (AnalogWaterType.equals("SewageAccGj"))
		{
			return SewageAccGj;
		}
		else if (AnalogWaterType.equals("SewageAcc"))
		{
			String SewageAccList = "";
			for (int i = 0; i < SewageAcc.length; i++)
			{
				SewageAccList += subSys.substring(7, 12) + SewageAcc[i] + ";";
			}
			return SewageAccList;
		}
		else if (AnalogWaterType.equals("SewageLev"))
		{
			String SewageLevList = "";
			for (int i = 0; i < SewageLev.length; i++)
			{
				SewageLevList += subSys.substring(7, 12) + SewageLev[i] + ";";
			}
			return SewageLevList;
		}
		return "";
	}

	// ģ�����۵�����
	private String analog_W3(String subSys, int timePeriod, String gjId, String gxId, String AnalogWaterType, double p1)
	{
		int SubgjId = 0;
		if (gjId != null)
		{
			SubgjId = CommUtil.StrToInt(gjId.substring(12, 15)) - 1;
		}
		int SubgxId = 0;
		if (gxId != null)
		{
			SubgxId = CommUtil.StrToInt(gxId.substring(5, 8)) - 1; // YJ001001
		}
		try
		{
			// CString s;
			// =================
			// �����������ݣ�
			// �ܶ������ڵ������ܵ��������·�����ܶ������м����MNP[NN][Ncol]������ģ��ʱ�����������ֵʱ��,�յ�ڵ�ţ��м������ָ�룬������ݱ�����
			int NP = 9, NN = 10, Nstart = 3, Npline = 20, Ncol = 6, NT = 72, Nend = 7, Iprt = 0, Nprtc = 20;
			// ��������ˮλ��m��,���氼͹ϵ��csf��·�ظ߶�heage-mm, ʱ�䲽����h��
			double Hw_end = 3.0, csf = 2.0, heage = 180, dt = 1.0;
			// ��ˮ��������
			// �˾�����ˮ����m/d��double q1=0.45;
			// ��ˮ���仯���ߣ�NT��

			// �ڵ�������(ha)�� �ڵ�����ߣ�m�����ڵ�����˿�(��)
			double[] Aj;
			double[] Hj;
			double[] Rj;
			// ��ˮ���仯���ߣ�NT��
			double[] Rf;
			// ����·������·���ڵ��(��99��ʾ�սڵ�)
			int[][] Mroute;
			int[][] Mbranch;
			// �ܶ����νڵ��I0,���νڵ��J0���ܶγ���(m),Ħ��ϵ��
			int[] I0;
			int[] J0;
			double[] lp;
			double[] slp;
			// �ڵ�����
			int[] Mstart;
			// �ܶ�ֱ��(m)�����ιܵ׸߳�(m)�����ιܵ׸߳�(m)
			double[] dpl;
			double[] ZJup;
			double[] ZJdw;

			this.FileSaveRoute = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogData/";
			String XlsPath = "";
			if (gjId != null)
			{
				XlsPath = FileSaveRoute + gjId.substring(0, 12) + ".xls";
				gjName = gjId.substring(0, 12);
			}
			else
			{
				XlsPath = FileSaveRoute + subSys + ".xls";
				gjName = subSys;
			}
			InputStream is = new FileInputStream(XlsPath);
			Workbook rwb = Workbook.getWorkbook(is);
			Sheet rs = rwb.getSheet(0);
			int rsRows = rs.getRows();

			/*
			 * �������ݱ����ϵͳ�� �ڵ���NN �ܶ���NP �����NStart ·���ܶ���Npline ·���ڵ���Nr_node
			 * �յ���ں�Nend ģ��ʱ��NT �ܶ�·����NrouteYJ002 10 9 3 7 8 8 60 3
			 */
			rowCnt = 2;
			String sysName = rs.getCell(0, rowCnt).getContents().trim();
			NN = Integer.parseInt(rs.getCell(1, rowCnt).getContents().trim());
			NP = Integer.parseInt(rs.getCell(2, rowCnt).getContents().trim());
			Nstart = Integer.parseInt(rs.getCell(3, rowCnt).getContents().trim());
			Npline = Integer.parseInt(rs.getCell(4, rowCnt).getContents().trim());
			Nend = Integer.parseInt(rs.getCell(6, rowCnt).getContents().trim());
			NT = Integer.parseInt(rs.getCell(7, rowCnt).getContents().trim());
			Ncol = Integer.parseInt(rs.getCell(8, rowCnt).getContents().trim());
			rowCnt += 4;

			dt = Double.parseDouble(rs.getCell(2, rowCnt).getContents().trim());
			Hw_end = Double.parseDouble(rs.getCell(3, rowCnt).getContents().trim());
			csf = Double.parseDouble(rs.getCell(4, rowCnt).getContents().trim());
			rowCnt += 4;

			/*
			 * ��ϵͳ�ܶ����ݱ�� Pipe.No ����I0 �յ��J0 ����LP ֱ��DP Ħ��ϵ�� ��˱�� �ն˱�� 1 0 1 28.5
			 * 0.3 0.017 3.894 3.842 2 1 2 32 0.3 0.017 3.842 3.784 3 2 3 28.6
			 * 0.3 0.017 3.784 3.733 4 3 4 25.4 0.3 0.017 3.733 3.687 5 4 5 24.7
			 * 0.3 0.017 3.687 3.643 6 5 6 23.5 0.3 0.017 3.643 3.601 7 6 7 30.4
			 * 0.3 0.017 3.601 3.546 8 8 7 15.5 0.3 0.017 3.731 3.171 9 9 6 4.3
			 * 0.3 0.017 3.886 3.7
			 */
			I0 = new int[NP];
			J0 = new int[NP];
			lp = new double[NP];
			dpl = new double[NP];
			slp = new double[NP];
			ZJup = new double[NP];
			ZJdw = new double[NP];
			for (int j = 0; j < NP; j++)
			{
				I0[j] = Integer.parseInt(rs.getCell(1, rowCnt).getContents().trim());
				J0[j] = Integer.parseInt(rs.getCell(2, rowCnt).getContents().trim());
				lp[j] = Double.parseDouble(rs.getCell(3, rowCnt).getContents().trim());
				dpl[j] = Double.parseDouble(rs.getCell(4, rowCnt).getContents().trim());
				slp[j] = Double.parseDouble(rs.getCell(5, rowCnt).getContents().trim());
				ZJup[j] = Double.parseDouble(rs.getCell(6, rowCnt).getContents().trim());
				ZJdw[j] = Double.parseDouble(rs.getCell(7, rowCnt).getContents().trim());
				rowCnt++;
			}

			rowCnt += 3;

			/*
			 * ��ϵͳ�ڵ����ݱ�� �ڵ�No �������Aj ������ �ڵ�����˿� 1 0.2 5.244 80 2 0.2 5.191 80 3
			 * 0.2 5.177 80 4 0.2 5.208 80 5 0.2 5.221 80 6 0.2 5.201 80 7 0.2
			 * 5.2 80 8 0.2 5.121 80 9 0.2 5.131 80 10 0.2 5.186 80
			 */
			Aj = new double[NN];
			Hj = new double[NN];
			Rj = new double[NN];
			for (int j = 0; j < NN; j++)
			{
				Aj[j] = Double.parseDouble(rs.getCell(1, rowCnt).getContents().trim());
				Hj[j] = Double.parseDouble(rs.getCell(2, rowCnt).getContents().trim());
				Rj[j] = Double.parseDouble(rs.getCell(3, rowCnt).getContents().trim());
				rowCnt++;
			}

			rowCnt += 3;

			/**
			 * ����·������ ��� 1 2 3 ���� 0 8 9
			 */
			// ************��һ�汾��ȥ��*******
			Mstart = new int[Nstart];
			// ***************************
			/*
			 * ��ϵͳ��֧·���ܶ����ݾ��� ���� �ڵ���� 1 2 3 4 5 6 7 1 6 5 4 3 2 1 0 2 7 -99 -99
			 * -99 -99 -99 -99 3 8 -99 -99 -99 -99 -99 -99
			 */
			Mbranch = new int[Nstart][Npline];
			/*
			 * ��ˮ���仯���� ʱ�� 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21
			 * 22 23 24 ���� 2.12 2.19 2.18 2.8 3.21 3.9 5.2 5.62 5.63 5.08 5.12
			 * 5.69 5.28 4.52 4.51 4.58 5.5 5.62 5.13 5.18 3.4 3.12 2.22 2.2
			 */
			Rf = new double[NT];
			for (int j = 0; j < NT; j++)
			{
				Rf[j] = Double.parseDouble(rs.getCell(j + 1, rowCnt).getContents().trim());
			}

			// �����ֲ�֧�߹ܶξ���-��������
			int[] Npjun = new int[NP];
			int[][] MNP = new int[NN][Ncol];

			/*
			 * double[] Rf = new double[] { 2.12, 2.19, 2.18, 2.80, 3.21, 3.90,
			 * 5.20, 5.62, 5.63, 5.08, 5.12, 5.69, 5.28, 4.52, 4.51, 4.58, 5.50,
			 * 5.62, 5.13, 5.18, 3.40, 3.12, 2.22, 2.2 }; // �ڵ�������(ha) double[]
			 * Aj = new double[] { 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2,
			 * 0.2 }; // �ڵ�����ߣ�m�� double[] Hj = new double[] { 5.24, 5.19,
			 * 5.18, 5.00, 5.21, 5.20, 5.20, 5.12, 5.13, 5.18 }; // �ڵ�����˿�(��)
			 * double[] Rj = new double[] { 300, 100, 0, 0, 200, 0, 200, 0, 200,
			 * 200 }; // ������������ //
			 * �ܶ����νڵ��I0,���νڵ��J0�����ιܵ׸߳�ZJup[NP](m)�����ιܵ׸߳�ZJdw[NP](m) int[] I0 =
			 * new int[] { 0, 1, 2, 3, 4, 5, 6, 8, 9 }; int[] J0 = new int[] {
			 * 1, 2, 3, 4, 5, 6, 7, 7, 6 }; double[] ZJup = new double[] { 3.89,
			 * 3.84, 3.78, 3.73, 3.68, 3.64, 3.60, 3.73, 3.88 }; double[] ZJdw =
			 * new double[] { 3.84, 3.78, 3.73, 3.68, 3.64, 3.60, 3.55, 3.60,
			 * 3.70 }; // �ܶγ���(m),�ܶ�ֱ��(m),Ħ��ϵ�� double[] lp = new double[] { 50,
			 * 50, 50, 50, 50, 50, 50, 50, 50 }; double[] dpl = new double[] {
			 * 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3 }; double[] slp = new
			 * double[] { 0.014, 0.014, 0.014, 0.014, 0.014, 0.014, 0.014,
			 * 0.014, 0.014 };
			 */
			// ===================
			// ----�м�ָ�����---
			int i00, j00 = 0, Ni1, jj, jp0, inp = 0, jpp, NPP;
			// ������ʼ�ڵ�ž���Mstart-�ڵ��������MNP-�м�任���� Npjun-������֧�߹ܶξ���Mbranch(��������)
			/*
			 * int[] Mstart = new int[Nstart]; int[][] MNP = new int[NN][Ncol];
			 * int[] Npjun = new int[NP]; int[][] Mbranch = new
			 * int[Nstart][Npline];
			 */
			// ----�м����----
			int i, ii, j, ik, it, jk, jjj, k, k1, kp, INS, in1, in2, in3, NR1, NR2, Nprt, iprt1, iprt2;
			double q1, Ad0, Akp, qkpmax, Hwdwkp, ycd0, yykp, sita, sigh_kp, slopt0;
			double dtnt, taa, tbb, AA, XX1, XX2, TTQj, TTQout, hdj0;
			double[] XX = new double[NT];
			double[] qit = new double[NT];
			double[] Qj = new double[NN];
			double[] vp = new double[NP];
			double[] slop = new double[NP];
			double[][] qpt = new double[NT][NP];
			double[][] qqkp = new double[NT][NP];
			double[][] vpt = new double[NT][NP];
			double[][] rid = new double[NT][NP];
			double[][] slopt = new double[NT][NP];
			double[][] Hwup = new double[NT][NP];
			double[][] Hwdw = new double[NT][NP];
			double[][] hdcc0 = new double[NT][NP];
			double[][] overflow = new double[NT][NN];
			double[][] Hw_over = new double[NT][NN];
			double[][] Hwj = new double[NT][NN];
			double[] TQj = new double[NT];
			double[] Toverf = new double[NT];

			SewageAcc = new String[NT];
			SewageLev = new String[NT];
			SewageFlowLoad = "";
			SewageActualFlow = "";
			SewageFlowRate = "";

			SewageAccGj = "";
			// ---------------------------------------------
			// --��������ļ���ʼ---

			String FilePath = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogValue/";
			String FileName = subSys + ".txt";
			FileOutputStream fs = new FileOutputStream(new File(FilePath + FileName));
			PrintStream printStream = new PrintStream(fs);
			printStream.println("20161030-��ˮ����ģ��-���ҳ�-3.txt");
			//
			DecimalFormat df = new DecimalFormat("##.####");
			DecimalFormat df1 = new DecimalFormat("######.##");

			// System.out.println("�������˾�ÿ����ˮ����m3��:");
			// Scanner input = new Scanner(System.in);
			// q1 = input.nextDouble();
			q1 = p1;

			printStream.println("===�˾�����ˮ���� " + q1 + " ��m3/d��    ʱ������ " + NT + "     �յ�ˮλ�� " + Hw_end + "  m  ===");
			// =====print pipe no. I0 lp J0 dpl slp ZJup ZJdw=====
			// System.out.println();
			// System.out.println("pipe no.  I0    J0      lp     dpl     slp    ZJup    ZJdw");
			for (i = 0; i < NP; i++)
			{
				// System.out.printf("%6d%6d%6d%8.2f%8.2f%8.3f%8.2f%8.2f", i,
				// I0[i], J0[i], lp[i], dpl[i], slp[i], ZJup[i], ZJdw[i]);
				// System.out.println();
			}
			// System.out.println();
			// System.out.println("===�˾�����ˮ���� " + q1 + " ��m3/d��   ʱ������ " + NT +
			// "     �յ�ˮλ�� " + Hw_end + "  m  ===");
			printStream.println();
			printStream.println("pipe no.  I0    J0      lp     dpl     slp    ZJup    ZJdw");
			for (i = 0; i < NP; i++)
			{
				printStream.printf("%6d%6d%6d%8.2f%8.2f%8.3f%8.2f%8.2f\n", i, I0[i], J0[i], lp[i], dpl[i], slp[i], ZJup[i], ZJdw[i]);
				printStream.println();
			}
			printStream.println();
			// ================= ����slop[k] ===========
			for (k = 0; k < NP; k++)
			{
				slop[k] = (ZJup[k] - ZJdw[k]) / lp[k];
			}
			// ====20161106===== ���ɾ��� MNP[i][j] ====
			for (i = 0; i < NN; i++)
			{
				for (j = 0; j < Ncol; j++)
				{
					MNP[i][j] = 0;
				}
				MNP[i][0] = i;
				jj = 2;
				for (k = 0; k < NP; k++)
				{
					if (J0[k] == i)
					{
						jj = jj + 1;
						MNP[i][1] = MNP[i][1] + 1;
						MNP[i][jj] = k;
					}
					if (I0[k] == i)
					{
						MNP[i][2] = MNP[i][2] + 1;
					}
				}
			}
			printStream.println("===========  print MNP[i][j]");
			for (i = 0; i < NN; i++)
			{
				for (j = 0; j < Ncol; j++)
				{
					printStream.printf("%6d", MNP[i][j]);
				}
				printStream.println();
			}
			// ----- MNP[i][j] ���� ------
			// ====20161112===== ���ɾ��� Mstart[i] ====
			jj = -1;
			for (i = 0; i < NN; i++)
			{
				if (MNP[i][1] == 0)
				{
					jj = jj + 1;
					Mstart[jj] = i;
				}
			}
			printStream.println("===========  print Mstart[i]");
			for (i = 0; i < Nstart; i++)
			{
				printStream.printf("%6d", Mstart[i]);
			}
			printStream.println();
			// ====20161106===== ���ɾ���Mbranch[i][j] ====
			for (i = 0; i < NP; i++)
			{
				Npjun[i] = 1;
			}
			//
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Npline; j++)
				{
					Mbranch[i][j] = -99;
				}
			}
			i00 = -1;
			NPP = 0;
			//
			// L200:
			while (true)
			{
				for (i = 0; i < NN; i++)
				{
					if (MNP[i][2] == 0 && MNP[i][1] > 0)
					{
						jj = 2;
						Ni1 = MNP[i][1];
						for (j = 0; j < Ni1; j++)
						{
							jj = jj + 1;
							jp0 = MNP[i][jj];
							if (Npjun[jp0] > 0)
							{
								i00 = i00 + 1;
								j00 = 0;
								Mbranch[i00][j00] = jp0;
								inp = I0[jp0];
								Npjun[jp0] = -99;
								NPP = NPP + 1;
							}
							//
							// L100:
							while (true)
							{
								INS = 1;
								for (jjj = 0; jjj < Nstart; jjj++)
								{
									if (Mstart[jjj] == inp) INS = 0;
								}
								if (INS > 0)
								{
									for (jpp = 0; jpp < NP; jpp++)
									{
										if (J0[jpp] == inp && Npjun[jpp] > 0)
										{
											j00 = j00 + 1;
											Mbranch[i00][j00] = jpp;
											inp = I0[jpp];
											Npjun[jpp] = -99;
											NPP = NPP + 1;
											break;
											// goto L100;
										}
										else
										{
											continue;
										}
									}
								} // --- end of if(INS>0) ---
								else
								{
									break;
								}
							}
						} // --- end of for(j=0;j<Ni1;j++) ---
					} // --- end of if(MNP[i][2]==0 && MNP[i][1]>0) ---
					MNP[i][2] = -99;
				}// --- end of for(i=0;i<NN;1++) ---
				for (i = 0; i < NN; i++)
				{
					for (j = 0; j < NP; j++)
					{
						if (I0[j] == i && Npjun[j] < 0)
						{
							MNP[i][2] = 0;
						}
					}
				}
				if (NPP >= NP)
				{// goto L200;
					break;
				}
			}
			// === ���ɾ��� Mbranch[i][j] ����====
			printStream.println();
			printStream.println("===========  print Mbranch[i][j]");
			for (i = 0; i < Nstart; i++)
			{
				for (j = 0; j < Npline; j++)
				{
					printStream.printf("%6d", Mbranch[i][j]);
				}
				printStream.println();
			}
			//
			// ================= ����׼��̬ˮ��ģ��============================
			//
			// -------------------��̬ģ����������-----------------------------
			// ----------------���ܶ��ܷ����˿�(��)�ͻ�ˮ����(m3/sec)����------
			printStream.println();
			printStream.println("======  ��ˮ������̬ģ��   �˾�����ˮ���� " + q1 + "  m3   ʱ������ " + NT + "       �յ�ˮλ�� " + Hw_end + "  m  =====");
			// xxxxxxx
			// �˾���ˮ���仯����---discharge at every time step per head---
			//
			for (it = 0; it < NT; it++)
			{
				qit[it] = q1 * Rf[it] / 100.0 / 3600;
			}
			printStream.println();
			printStream.println("    it     qit[it] ��m3/cap-sec��");
			for (it = 0; it < NT; it++)
			{
				printStream.printf("%6d%12.6f", it, qit[it]);
				printStream.println();
			}
			printStream.println();
			// xxxxxxx
			// -------------�ܶ�ˮ�����㿪ʼ--------------
			for (it = 0; it < NT; it++)
			{
				for (i = 0; i < NN; i++)
				{
					overflow[it][i] = 0.0;
					Hw_over[it][i] = 0.0;
				}
			}
			for (it = 0; it < NT; it++)
			{
				for (j = 0; j < NP; j++)
				{
					qpt[it][j] = -99.0;
					qqkp[it][j] = 0.0;
				}
			}
			// -------------------------------------
			for (it = 0; it < NT; it++)
			{ // --1--
				// ----------����ܶ�����------------
				if (it == 0)
				{
					for (i = 0; i < NN; i++)
					{
						Qj[i] = Rj[i] * qit[it];
					}
					for (j = 0; j < NP; j++)
					{
						for (i = 0; i < NN; i++)
						{
							if (I0[j] == i) qpt[it][j] = Qj[i];
						}
					}
				}
				else
				{
					for (i = 0; i < NN; i++)
					{
						Qj[i] = Rj[i] * qit[it];
					}
					for (j = 0; j < NP; j++)
					{
						for (i = 0; i < NN; i++)
						{
							if (I0[j] == i) qpt[it][j] = Qj[i];
						}
					}
					for (j = 0; j < NP; j++)
					{
						for (k = 0; k < NP; k++)
						{
							if (J0[k] == I0[j]) qpt[it][j] = qpt[it][j] + qqkp[it - 1][k];
						}
					}
				}
				for (j = 0; j < NP; j++)
				{
					qqkp[it][j] = qpt[it][j];
				}
				printStream.print(" it=" + it + "  qpt[it][k]=");
				for (k = 0; k < NP; k++)
				{
					printStream.printf("%8.4f", qpt[it][k]);
				}
				printStream.println();
				// ------------20090127-sqliu------------------------
				for (ik = 0; ik < Nstart; ik++)
				// --2--
				{
					for (jk = 0; jk < Npline; jk++)
					// --3--
					{
						kp = Mbranch[ik][jk];
						if (kp >= 0)
						// --4--
						{
							if (J0[kp] == Nend)
							{
								Hwdw[it][kp] = Hw_end;
								if (Iprt == 1)
								{
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  Hw_end= " + Hw_end);
								}
							}
							else
							{
								for (k1 = 0; k1 < NP; k1++)
								{
									if (I0[k1] == J0[kp]) Hwdw[it][kp] = Hwup[it][k1];
								}
							}
							Ad0 = 0.7854 * Math.pow(dpl[kp], 2.0);
							hdj0 = ZJdw[kp] + dpl[kp];
							if (Hwdw[it][kp] >= hdj0)
							{
								if (Iprt == 1)
								{
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  ��û���� ");
								}
								hdcc0[it][kp] = 1.0;
								rid[it][kp] = dpl[kp] / 4.0;
								vpt[it][kp] = qpt[it][kp] / Ad0;
								slopt[it][kp] = 10.29 * Math.pow(slp[kp], 2.0) * Math.pow(qpt[it][kp], 2.0) / Math.pow(dpl[kp], 5.333);
								Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
								//
								if (Hwup[it][kp] >= Hj[I0[kp]])
								{
									Hwup[it][kp] = Hj[I0[kp]];
									slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
									sigh_kp = 1.0;
									slopt0 = slopt[it][kp];
									if (slopt[it][kp] < 0.0)
									{
										slopt0 = -slopt0;
										sigh_kp = -1.0;
									}
									vpt[it][kp] = sigh_kp * Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt0, 0.5) / slp[kp];
									qqkp[it][kp] = vpt[it][kp] * Ad0;
								}
								// -------20161213start--------
								if (it > 0 && Hwup[it][kp] < Hj[I0[kp]] && overflow[it - 1][I0[kp]] > 0.0)
								{
									Hwup[it][kp] = Hj[I0[kp]];
									slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
									sigh_kp = 1.0;
									slopt0 = slopt[it][kp];
									if (slopt[it][kp] < 0.0)
									{
										slopt0 = -slopt0;
										sigh_kp = -1.0;
									}
									vpt[it][kp] = sigh_kp * Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt0, 0.5) / slp[kp];
									qqkp[it][kp] = vpt[it][kp] * Ad0;
								}
								// -------20161213end----------
							}
							else
							// --5--
							{
								if (Iprt == 1)
								{
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdw= " + Hwdw[it][kp] + "  ����û���� ");
								}
								// --20161018---�����ٽ�ˮ��------------
								qkpmax = 2.699 * Math.pow(dpl[kp], 2.5);
								if (qpt[it][kp] > qkpmax)
								{
									if (Iprt == 1)
									{
										printStream.println("   it= " + it + "   kp= " + kp + "  qkpmax= " + qkpmax + "  ����û���ܳ��� ");
									}
									vpt[it][kp] = qpt[it][kp] / Ad0;
									// H00=pow(vpt[it][kp],2.0)/13.72;
									// Hwdw[it][kp]=ZJdw[kp]+dpl[kp]+H00;
									Hwdw[it][kp] = ZJdw[kp] + dpl[kp];
									hdcc0[it][kp] = 1.0;
									rid[it][kp] = dpl[kp] / 4.0;
									slopt[it][kp] = 10.29 * Math.pow(slp[kp], 2.0) * Math.pow(qpt[it][kp], 2.0) / Math.pow(dpl[kp], 5.333);
									Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
									//
									if (Hwup[it][kp] >= Hj[I0[kp]])
									{
										Hwup[it][kp] = Hj[I0[kp]];
										slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
										sigh_kp = 1.0;
										slopt0 = slopt[it][kp];
										if (slopt[it][kp] < 0.0)
										{
											slopt0 = -slopt0;
											sigh_kp = -1.0;
										}
										vpt[it][kp] = sigh_kp * Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt0, 0.5) / slp[kp];
										qqkp[it][kp] = vpt[it][kp] * Ad0;
									}
									// -------20161213start--------
									if (Hwup[it][kp] < Hj[I0[kp]] && overflow[it - 1][I0[kp]] > 0.0 && it > 0)
									{
										Hwup[it][kp] = Hj[I0[kp]];
										slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
										sigh_kp = 1.0;
										slopt0 = slopt[it][kp];
										if (slopt[it][kp] < 0.0)
										{
											slopt0 = -slopt0;
											sigh_kp = -1.0;
										}
										vpt[it][kp] = sigh_kp * Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt0, 0.5) / slp[kp];
										qqkp[it][kp] = vpt[it][kp] * Ad0;
									}
									// -------20161213end----------
								}
								else
								{
									if (Iprt == 1)
									{
										printStream.println("   it= " + it + "   kp= " + kp + "  Hwdw= " + Hwdw[it][kp] + "  ����û�����ܳ��� ");
									}
									// ==20161115---����ˮ�ʼ--------
									if (slop[kp] > 0)
									{// ----����ˮ��----
										if (qpt[it][kp] >= 0.0)
										{
											ycd0 = 20.1538 * slp[kp] * qpt[it][kp] / Math.pow(dpl[kp], 2.6667) / Math.pow(slop[kp], 0.5);
											if (ycd0 <= 1.5)
											{
												hdcc0[it][kp] = 0.27 * Math.pow(ycd0, 0.485);
											}
											else
											{
												hdcc0[it][kp] = 0.098 * ycd0 + 0.19;
											}
											if (hdcc0[it][kp] <= 0.0001)
											{
												hdcc0[it][kp] = 0.0001;
											}
										}
										else
										{
											hdcc0[it][kp] = 1.0;
										}
									}
									else
									{// ----�ٽ�ˮ��----
										if (qpt[it][kp] >= 0.0)
										{
											ycd0 = qpt[it][kp] / 2.983 / Math.pow(dpl[kp], 2.5);
											hdcc0[it][kp] = Math.pow(ycd0, 0.513);
											if (hdcc0[it][kp] <= 0.0001)
											{
												hdcc0[it][kp] = 0.0001;
											}
										}
										else
										{
											hdcc0[it][kp] = 1.0;
										}
									}
									if (hdcc0[it][kp] > 1.0)
									{
										hdcc0[it][kp] = 1.0;
									}
									// ==20161115---����ˮ�����-------
									sita = 2.0 * Math.acos(1.0 - 2.0 * hdcc0[it][kp]);
									rid[it][kp] = 0.25 * dpl[kp] * (sita - Math.sin(sita)) / sita;
									Akp = Math.pow(dpl[kp], 2.0) * (sita - Math.sin(sita)) / 8.0;
									vpt[it][kp] = qpt[it][kp] / Akp;
									Hwdw[it][kp] = ZJdw[kp] + hdcc0[it][kp] * dpl[kp];
									slopt[it][kp] = Math.pow(slp[kp], 2.0) * Math.pow(vpt[it][kp], 2.0) / Math.pow(rid[it][kp], 1.333);
									Hwup[it][kp] = Hwdw[it][kp] + slopt[it][kp] * lp[kp];
									if (Hwup[it][kp] >= Hj[I0[kp]])
									{
										Hwup[it][kp] = Hj[I0[kp]];
										slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
										sigh_kp = 1.0;
										slopt0 = slopt[it][kp];
										if (slopt[it][kp] < 0.0)
										{
											slopt0 = -slopt0;
											sigh_kp = -1.0;
										}
										vpt[it][kp] = sigh_kp * Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt0, 0.5) / slp[kp];
										qqkp[it][kp] = vpt[it][kp] * Ad0;
									}
									// -------20161213start--------
									if (it > 0 && Hwup[it][kp] < Hj[I0[kp]] && overflow[it - 1][I0[kp]] > 0.0)
									{
										Hwup[it][kp] = Hj[I0[kp]];
										slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
										sigh_kp = 1.0;
										slopt0 = slopt[it][kp];
										if (slopt[it][kp] < 0.0)
										{
											slopt0 = -slopt0;
											sigh_kp = -1.0;
										}
										vpt[it][kp] = sigh_kp * Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt0, 0.5) / slp[kp];
										qqkp[it][kp] = vpt[it][kp] * Ad0;
									}
									// -------20161213end----------
								}
							}
							// 5--end
							// ------- ���it������ ----------
							if (Iprt == 1)
							{
								printStream.println("   it= " + it + "   kp= " + kp + "   I0[kp]= " + I0[kp] + "  Hwdm= " + Hwdw[it][kp] + "  Hwup= " + Hwup[it][kp] + "  Hj= " + Hj[I0[kp]] + "  hdcc0= " + hdcc0[it][kp] + "  qpt= " + qpt[it][kp] + "  qqkp= " + qqkp[it][kp] + "  vpt= " + vpt[it][kp]);
							}
						}// --4 if(kp>=0) end
					}// --3 ---jk end
				}// --2---ik end
					// -------------- ����ڵ�ˮλ-�ڵ��ˮ���ͻ�ˮ��� ---------------
				for (i = 0; i < NP; i++)
				{
					k = J0[i];
					if (k == Nend)
					{
						Hwj[it][k] = Hwdw[it][i];
					}
					{
						j = I0[i];
						Hwj[it][j] = Hwup[it][i];
						if (it > 0)
						{
							overflow[it][j] = overflow[it - 1][j] + (qpt[it][i] - qqkp[it][i]) * dt * 60.0;
							Hw_over[it][j] = csf * overflow[it][j] / Aj[j] / 10000.0 * 1000.0;
							if (Hw_over[it][j] > heage)
							{
								Hw_over[it][j] = heage + csf * (overflow[it][j] - Aj[j] * heage / 1000.0) / 3.0 / 10000.0 * 1000.0;
							}
						}
					}
				}
				printStream.println();
				printStream.println("    it   �ܶκ�  I0   J0 �ܾ�dpl    �ܶ�qpt ˮ���뾶R    ������ ����(m/s)  ����ˮλ  ����ˮλ  �Ϲܵ׸�  �¹ܵ׸�  �ܶ��¶�  �ϵ����  ˮ���¶�    qqkp");
				for (i = 0; i < NP; i++)
				{
					printStream.printf("%6d%6d%6d%5d%8.2f%12.4f%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.4f%10.4f", it, i, I0[i], J0[i], dpl[i], qpt[it][i], rid[it][i], hdcc0[it][i], vpt[it][i], Hwup[it][i], Hwdw[it][i], ZJup[i], ZJdw[i], slop[i], Hj[I0[i]], slopt[it][i], qqkp[it][i]);
					printStream.println();
				}
				printStream.println();
				// ------------ ���������ڵ���� ----
				TQj[it] = 0;
				Toverf[it] = 0;
				for (i = 0; i < NN; i++)
				{
					TQj[it] = TQj[it] + Qj[i];
					Toverf[it] = Toverf[it] + overflow[it][i];
				}
				printStream.println();
				printStream.println("  TQj[it]= " + TQj[it] + " m3/sec     Toverf[it]=  " + Toverf[it] + " m3  ");
				printStream.println();
			}// 1-- it end ---
				// --------------��Ļ����������------
				// ----------------- ����ܶγ����ȼ����� ---------------
			printStream.println(" ======== ʱ�ιܶγ����� ========");
			Nprt = NP / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NP)
					{
						iprt2 = NP;
					}
				}
				printStream.println("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						printStream.printf("%8.3f", hdcc0[it][i]);
					}
					printStream.println();
				}
			}
			// ------------------ ����ڵ�ˮλ������ ---------------
			printStream.println(" ======== ʱ�νڵ�ˮλ ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				{
					iprt1 = ii * Nprtc;
					iprt2 = iprt1 + Nprtc;
					if (iprt2 > NN)
					{
						iprt2 = NN;
					}
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println();
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						printStream.printf("%8.2f", Hwj[it][i]);
					}
					printStream.println();
				}
			}

			// ***********��֯���ݣ�����ҳ��������ʾ********
			for (it = 0; it < NT; it++)
			{
				String SewageLevNew = "";
				for (i = 0; i < NN; i++)
				{
					if (gjId != null && i == SubgjId)
					{
						SewageAccGj += df1.format(Hwj[it][i]) + "|";
					}
					SewageLevNew += df1.format(Hwj[it][i]) + "|";
				}
				SewageLev[it] = SewageLevNew;
			}
			// **************************************
			// ---------------------- ����ڵ����������� ---------------
			printStream.println(" ======== ʱ�νڵ��ˮ��(m3) ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				iprt1 = ii * Nprtc;
				iprt2 = iprt1 + Nprtc;
				if (iprt2 > NN)
				{
					iprt2 = NN;
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						if (overflow[it][i] <= 0.1)
						{
							printStream.print("        ");
						}
						else
						{
							printStream.printf("%8.2f", overflow[it][i]);
						}
					}
					printStream.println();
				}
			}

			// ***********��֯���ݣ�����ҳ��������ʾ********
			for (it = 0; it < NT; it++)
			{
				String SewageAccNew = "";
				for (i = 0; i < NN; i++)
				{
					if (overflow[it][i] <= 0.0)
					{
						printStream.print("        ");
						SewageAccNew += 0 + "|";
					}
					else
					{
						printStream.printf("%8.2f", overflow[it][i]);
						SewageAccNew += df1.format(overflow[it][i]) + "|";
					}
				}
				SewageAcc[it] = SewageAccNew;
			}
			// *********************************
			printStream.println(" ======== ʱ�νڵ��ˮ���(mm) ========");
			Nprt = NN / Nprtc + 1;
			for (ii = 0; ii < Nprt; ii++)
			{
				iprt1 = ii * Nprtc;
				iprt2 = iprt1 + Nprtc;
				if (iprt2 > NN)
				{
					iprt2 = NN;
				}
				printStream.print("  i=    ");
				for (i = iprt1; i < iprt2; i++)
				{
					if (i < 10)
					{
						printStream.print("    " + i + "   ");
					}
					else
					{
						printStream.print("   " + i + "   ");
					}
				}
				printStream.println();
				printStream.println("it=");
				for (it = 0; it < NT; it++)
				{
					if (it < 10)
					{
						printStream.print(" " + it + "   ");
					}
					else
					{
						printStream.print(it + "   ");
					}
					for (i = iprt1; i < iprt2; i++)
					{
						if (Hw_over[it][i] < 5.0)
						{
							printStream.print("        ");
						}
						else
						{
							printStream.printf("%8.2f", Hw_over[it][i]);
						}
					}
					printStream.println();
				}
			}
			// ***********��֯���ݣ�����ҳ��������ʾ*****20170120***
			for (it = 0; it < NT; it++)
			{
				for (i = 0; i < NP; i++)
				{
					if (gjId != null && gxId != null && i == SubgxId)
					{
						SewageFlowLoad += df1.format(qpt[it][i]) + "|";
						SewageActualFlow += df1.format(qqkp[it][i]) + "|";
						SewageFlowRate += df1.format(vpt[it][i]) + "|";
					}
				}
			}
			// *********************************************

			// -----ģ�ͼ������-----
			// System.out.println("------ ģ�ͼ������ ------");
			printStream.println("------ ģ�ͼ������ ------");

		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			return gjName + "," + "NumberFormat" + "," + (rowCnt + 1);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
			return gjName + "," + "ArrayIndexOut" + "," + "";
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return gjName + "," + "unknown" + "," + (rowCnt + 1);
		}
		if (AnalogWaterType.equals("SewageAccGj"))
		{
			return SewageAccGj;
		}
		else if (AnalogWaterType.equals("SewageAcc"))
		{
			String SewageAccList = "";
			for (int i = 0; i < SewageAcc.length; i++)
			{
				SewageAccList += subSys.substring(7, 12) + SewageAcc[i] + ";";
			}
			return SewageAccList;
		}
		else if (AnalogWaterType.equals("SewageLev"))
		{
			String SewageLevList = "";
			for (int i = 0; i < SewageLev.length; i++)
			{
				SewageLevList += subSys.substring(7, 12) + SewageLev[i] + ";";
			}
			return SewageLevList;
		}
		else if (AnalogWaterType.equals("SewageFlowLoad"))
		{
			return SewageFlowLoad;
		}
		else if (AnalogWaterType.equals("SewageActualFlow"))
		{
			return SewageActualFlow;
		}
		else if (AnalogWaterType.equals("SewageFlowRate"))
		{
			return SewageFlowRate;
		}
		return "";
	}

	//
	private class DevGJData
	{
		int		sn			= 0;
		float	water		= 0;
		String	Base_Height	= "0";
		String	Top_Height	= "0";
		String	Equip_Height	= "0";

	}

	/**
	 * �Զ������豸�����⾮��ˮλ����
	 * 
	 * @param gjObj
	 * @param gxObj
	 * @param Id
	 * @return
	 */
	public ArrayList AnalogGJList(ArrayList gjObj, ArrayList gxObj, String Id)
	{
		/*** 2017.2.7 ���ó�����Сʱû�ɼ����豸��ֱΪ0 cj ***/
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date newTime;
		Date equip_time;
		long between = 0;
		long hour = 0;
		/*******/
		// gjObj ArrayListתHash
		Hashtable<String, DevGJBean> objGJTable = null;
		objGJTable = new Hashtable<String, DevGJBean>();
		Iterator iterGJ = gjObj.iterator();
		while (iterGJ.hasNext())
		{
			DevGJBean gjBean = (DevGJBean) iterGJ.next();
			String gjId = gjBean.getId();

			/*** 2017.2.7 ���ó�����Сʱû�ɼ����豸��ֵΪ0 cj ***/
			if (gjBean.getEquip_Time().length() > 1)
			{
				try
				{
					newTime = df.parse(df.format(new Date()));
					equip_time = df.parse(gjBean.getEquip_Time());
					between = (newTime.getTime() - equip_time.getTime()) / 1000;// ����1000��Ϊ��ת������
					hour = between / 3600;
				}
				catch (ParseException e)
				{
					e.printStackTrace();
				}
				if (hour > 2)
				{
					gjBean.setCurr_Data("0.00");
				}
			}
			if(Double.valueOf(gjBean.getBase_Height()) <= 0 && Double.valueOf(gjBean.getCurr_Data()) <= 0){
				gjBean.setCurr_Data(gjBean.getBase_Height());
			}
			/*******/
			HashPut(objGJTable, gjId, gjBean);
		}
		// gxObj ArrayListתHash
		Hashtable<String, DevGXBean> objGXTable = null;
		objGXTable = new Hashtable<String, DevGXBean>();
		Iterator iterGX = gxObj.iterator();
		while (iterGX.hasNext())
		{
			DevGXBean gxBean = (DevGXBean) iterGX.next();
			String gxId = gxBean.getId();
			HashPut(objGXTable, gxId, gxBean);
		}

		// �����������Ⱥ��ϵ,ת��ArrayList
		DevGJBean nextGJ = (DevGJBean) HashGet(objGJTable, Id);
		ArrayList<Object> gjList = new ArrayList<Object>(); // �ܾ�ArrayList
		gjList.add(nextGJ); // �����һ���ܾ�
		ArrayList<Object> devList = new ArrayList<Object>(); // �豸�ܾ�ArrayList
		DevGXBean nextGX = new DevGXBean();

		int sn = 0;
		int option = 0;
		do
		{
			try{
				if (nextGJ.getFlag().equals("2") || nextGJ.getFlag().equals("6") || sn > 1000)
				{
					option = 1;
				}
				String outGXId = "";
				if (Double.valueOf(nextGJ.getCurr_Data()) > 0)
				{
					DevGJData devGJ = new DevGJData();
					devGJ.sn = sn;
					devGJ.Base_Height = nextGJ.getBase_Height();
					devGJ.Top_Height = nextGJ.getTop_Height();
					devGJ.Equip_Height = nextGJ.getEquip_Height();
					devGJ.water = CommUtil.StrToFloat(nextGJ.getTop_Height()) - CommUtil.StrToFloat(nextGJ.getEquip_Height()) + CommUtil.StrToFloat(nextGJ.getCurr_Data());
					if(devGJ.water - CommUtil.StrToFloat(devGJ.Base_Height) >= 0.06) {
						devList.add(devGJ);
					}
				}
				outGXId = nextGJ.getOut_Id();
				nextGX = (DevGXBean) HashGet(objGXTable, outGXId);
				if(null != nextGX)
				{
					String outGJId = nextGX.getEnd_Id();
					String startGJId = nextGX.getStart_Id();
					//System.out.println("outGJId["+outGJId+"]startGJId["+startGJId+"]");
					if(outGJId.substring(2,5).equals(startGJId.substring(2,5)))
					{
						nextGJ = (DevGJBean) HashGet(objGJTable, outGJId);
						sn++;
						gjList.add(nextGJ);
					}
					else
					{
						option = 1;
					}
				}
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
		while (option == 0);

		// ���û���豸����������ѡ��ܾ����յ�Ĺܾ��б�
		//System.out.println("devList.size()["+devList.size()+"]");
		if (0 == devList.size())
		{
			return gjList;
		}
		// ������豸�������¹ܾ��б������豸�ܾ���ˮλ
		int count = 0;
		DevGJData devGJData1 = ((DevGJData) devList.get(0)); // ȡ����һ�����豸�Ĺܾ�
		DevGJData devGJDataN = ((DevGJData) devList.get(devList.size() - 1)); // ȡ�����һ�����豸�Ĺܾ�
		@SuppressWarnings("rawtypes")
		Iterator it = gjList.iterator();
		while (it.hasNext())
		{
			DevGJBean gjBean = (DevGJBean) it.next();
			int flag = 0;
			for (int i = 0; i < devList.size(); i++)
			{
				if (count == ((DevGJData) devList.get(i)).sn)
				{
					flag = 1;
					break;
				}
			}
			if (1 == flag) // �豸�ܾ����� ��һ��
			{
				gjBean.setCurr_Data(String.valueOf(CommUtil.StrToFloat(gjBean.getTop_Height())
						- CommUtil.StrToFloat(gjBean.getEquip_Height())
						+ CommUtil.StrToFloat(gjBean.getCurr_Data())));// ���赱ǰ�ܾ���ˮλ�߶�
				count++;
				continue;
			}
			if (count < devGJData1.sn) // ��һ���豸֮ǰ�Ĺܾ�
			{
				float GJBaseHeight = CommUtil.StrToFloat(gjBean.getBase_Height()); // ��ǰ�ܾ��ĵ׸�
				float DevGJWater = devGJData1.water; // ��һ���豸��ˮλ
				float DevGJTopHeight = CommUtil.StrToFloat(devGJData1.Top_Height); // ��һ���豸�Ķ���
				float DevGJEquipHeight = CommUtil.StrToFloat(devGJData1.Equip_Height); // ��һ���豸���豸��װ��
				gjBean.setCurr_Data(String.valueOf(DevGJWater));// ���赱ǰ�ܾ���ˮλ�߶�
				
//				System.out.println("DevGJWater["+DevGJWater+"]/n"
//						+ "DevGJTopHeight["+DevGJTopHeight+"]/n"
//						+ "DevGJDevHeight["+DevGJEquipHeight+"]");
//				float DevGJBaseHeight = CommUtil.StrToFloat(devGJData1.Base_Height); // ��һ���豸�ĵ׸�
//				if (GJBaseHeight < DevGJWater + DevGJBaseHeight) // ����ǰ�豸�ĵ׸� <
//																	// �豸��ˮλ+�׸�
//				{
//					gjBean.setCurr_Data(String.valueOf(DevGJWater + DevGJBaseHeight - GJBaseHeight));// ���赱ǰ�ܾ���ˮλ�߶�
//				}
			}
			else if (count > devGJDataN.sn) // ���һ���豸֮��Ĺܾ�
			{
				float GJBaseHeight = CommUtil.StrToFloat(gjBean.getBase_Height());
				float DevGJWater = devGJDataN.water;
				float DevGJTopHeight = CommUtil.StrToFloat(devGJDataN.Top_Height); // ��һ���豸�Ķ���
				float DevGJEquipHeight = CommUtil.StrToFloat(devGJDataN.Equip_Height); // ��һ���豸���豸��װ��
				gjBean.setCurr_Data(String.valueOf(DevGJWater));// ���赱ǰ�ܾ���ˮλ�߶�
//				float DevGJBaseHeight = CommUtil.StrToFloat(devGJDataN.Base_Height);
//				if (GJBaseHeight < DevGJWater + DevGJBaseHeight)
//				{
//					gjBean.setCurr_Data(String.valueOf(DevGJWater + DevGJBaseHeight - GJBaseHeight));
//				}
			}
			else
			// ��һ���豸�����һ���豸֮��Ĺܾ�
			{

				for (int i = 1; i < devList.size(); i++)
				{
					DevGJData devGJDataI_1 = (DevGJData) devList.get(i - 1);
					DevGJData devGJDataI = (DevGJData) devList.get(i);

					// System.out.println("i-1sn[" + devGJDataI_1.sn +
					// "] count[" + count + "] isn[" + devGJDataI.sn + "]");

					if (devGJDataI_1.sn < count && devGJDataI.sn > count)
					{
						// ֮��� + ��֮���-֮ǰ�ģ�/���м������
						float i_1Lev = devGJDataI_1.water;
						float iLev = devGJDataI.water;
						float waterLev = i_1Lev + (iLev - i_1Lev) * (count - devGJDataI_1.sn) / (devGJDataI.sn - devGJDataI_1.sn);
						//float currData = waterLev - CommUtil.StrToFloat(gjBean.getBase_Height());
//						System.out.println("i_1Lev["+i_1Lev+"] /n"
//								+ "iLev["+iLev+"] /n"
//								+ "waterLev["+waterLev+"]");
						gjBean.setCurr_Data(String.valueOf(waterLev));
						break;
					}
				}
			}
			count++;
		}
		return gjList;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void HashPut(Hashtable hashTable, String key, Object obj)
	{
		if (hashTable.containsKey(key))
		{
			hashTable.remove(key); // �ڹ�ϣ�����Ƴ��ͻ���
		}
		hashTable.put(key, obj);
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	public Object HashGet(Hashtable hashTable, String key)
	{
		if (!hashTable.isEmpty() && hashTable.containsKey(key))
		{
			return hashTable.get(key);
		}
		return null;
	}

	private String		FileSaveRoute;
	private String		File_Name;
	private String		Sid;

	private String		gjName;
	private int			rowCnt;
	private int			Count;

	private String		AnalogWaterType;

	private String[]	WaterAcc;
	private String[]	WaterLev;
	private String		WaterAccGj;
	private String		WaterFlowLoad;
	private String		WaterActualFlow;
	private String		WaterFlowRate;

	private String[]	SewageAcc;
	private String[]	SewageLev;
	private String		SewageAccGj;
	private String		SewageFlowLoad;
	private String		SewageActualFlow;
	private String		SewageFlowRate;
}

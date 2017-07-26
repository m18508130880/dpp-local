package bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import rmi.Rmi;
import util.CommUtil;
import util.CurrStatus;

import com.jspsmart.upload.SmartUpload;

public class AnalogBean
{

	/**
	 * 模拟计算时导入excel表格
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
			//mySmartUpload.initialize(pConfig, request, response);
			mySmartUpload.setAllowedFilesList("xls,xlsx,XLS,XLSX,");
			mySmartUpload.upload();

			this.Sid = mySmartUpload.getRequest().getParameter("Sid");
			CurrStatus currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + this.Sid);
			currStatus.getHtmlData(request, pFromZone);
			String Project_Id = mySmartUpload.getRequest().getParameter("Project_Id");

			if ((mySmartUpload.getFiles().getCount() > 0) && (mySmartUpload.getFiles().getFile(0).getFilePathName().trim().length() > 0))
			{
				if (mySmartUpload.getFiles().getFile(0).getSize() / 1024 <= 3072)
				{
					FileSaveRoute = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogData/";

					com.jspsmart.upload.File myFile = mySmartUpload.getFiles().getFile(0);
					File_Name = mySmartUpload.getFiles().getFile(0).getFileName();
					myFile.saveAs(FileSaveRoute + Project_Id + "_" + File_Name);
					currStatus.setResult("文档上传成功！");
				}
				else
				{
					currStatus.setResult("文档上传失败！文档过大，必须小于3M!");
				}
			}
			currStatus.setJsp("loading.jsp?Sid=" + Sid + "&Project_Id=" + Project_Id + "&AnalogType=" + File_Name.substring(0, 2));
			request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
			response.sendRedirect(currStatus.getJsp());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 删除模拟数据表格
	 */
	public boolean DeleteData(String fileName)
	{
		String filePath = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogData/";
		File file = new File(filePath + fileName + ".xls");
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isFile())
		{
			if (file.delete())
			{
				System.out.println("删除单个文件" + fileName + "成功！");
				return true;
			}
			else
			{
				System.out.println("删除单个文件" + fileName + "失败！");
				return false;
			}
		}
		else
		{
			System.out.println("删除单个文件失败：" + fileName + "不存在！");
			return false;
		}
	}
	/**
	 * 雨水 计算管井时段水位 - 水位折綫圖
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
	 * 雨水 计算时段水位深度 - 水位剖面圖
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
	 * 雨水 计算时段积水量 - 模擬地圖點位積水量
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
	 *  雨水 计算时段流量负荷 - 折线图
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
	 *  雨水 计算时段实际流量 - 折线图
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
	 *  雨水 计算时段流速 - 折线图
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
	 * 污水 计算管井时段水位 - 水位折綫圖
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
	 * 污水 计算时段水位深度 - 水位剖面圖
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
	 * 污水 计算时段积水量 - 模拟地面积水量
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
	 *  雨水 计算时段流量负荷 - 折线图
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
	 *  雨水 计算时段实际流量 - 折线图
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
	 *  雨水 计算时段流速 - 折线图
	 * @param subSys
	 * @param p1
	 * @return
	 */
	public String SewageFlowRate(String gjId, String gxId, double p1)
	{
		AnalogWaterType = "SewageFlowRate";
		return analog_W3(null, 0, gjId, gxId, AnalogWaterType, p1);
	}


	// 第一套版本
	private String analog_Y1(String subSys, int timePeriod, String gjId, String AnalogWaterType)
	{
		WaterAcc	= new String[60];
		WaterLev	= new String[60];
		int SubgjId = 0;
		if (gjId != null)
		{
			SubgjId = CommUtil.StrToInt(gjId.substring(12, 15)) - 1;
		}
		try
		{
			// 管网基础数据：
			// 管段数，节点数，管道起点数，路径最大管段数，最大计算次数，模拟时段数，芝加哥峰点时段位置
			// 管道路径数，路径最大节点数，终点节点号，中间结果输出文件指针
			int NP = 9, NN = 10, Nstart = 3, Npline = 7, NT = 60, NR = 23, Nroute = 3, Nr_node = 8, Nend = 7, Iprt = 0;
			// 暴雨公式参数shanghai storm water formular:
			// (A1+C*lgP)/(t+b)**n---ln(N)=2.303log(N)---出口水位（m）
			// 管段流速（m/s）, 管段设定流速vp0，地面凹凸系数csf
			double A1 = 17.53, C_storm = 0.95, tmin = 10, b_storm = 11.77, P_simu = 50, n_storm = 0.88, dt = 2.0, rc = 0.375, Hw_end = 4.1, vp0 = 0.8, csf = 3.0;

			// 子系统管段数据：
			int[] I0; // 管段上游节点号I0,
			int[] J0; // 下游节点号J0,
			double[] lp; // 管段长度
			double[] dpl; // 管段直径(m)
			double[] slp; // 摩|阻系数
			double[] ZJup; // 上游管底高程(m)
			double[] ZJdw; // 下游管底高程(m)

			// 子系统节点数据
			// 管网起始节点号和起始节点管底埋深<m>
			double[] Aj; // 节点汇水面积(ha)3.5
			double[] Acoef; // 节点汇水面积径流系数0.6
			double[] Hj; // 节点地面标高（m）[NN=23]

			// 管网路径数和路径节点号(-99表示空节点)
			int[][] Mroute;

			// 子系统分支路径管段数据矩阵 倒序pipe branches-reverse order
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
			 * 基础数据表格子系统号 节点数NN 管段数NP 起点数NStart 路径管段数Npline 路径节点数Nr_node
			 * 终点出口号Nend 模拟时段NT 管段路径数NrouteYJ002 10 9 3 7 8 8 60 3
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
			 * 子系统管段数据表格 Pipe.No 起点号I0 终点号J0 长度LP 直径DP 摩阻系数 起端标高 终端标高 1 0 1 28.5
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
			 * 子系统节点数据表格节点No 汇水面积ha 径流系数 地面标高 井底标高 1 3.5 0.6 5.244 暂未用到 2 3.5
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
			 * 管网路径数&路径节点号节点序号 1 2 3 4 5 6 7 8 1 0 1 2 3 4 5 6 7 2 8 7 -99 -99
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
			 * 子系统分支路径管段数据矩阵 倒序pipe branches-reverse order 节点序号 1 2 3 4 5 6 7 1
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
			// ----临界水深计算变量----
			double sita0 = 3.0, eps = 0.001, alfa = 0.5;
			double Ad0, qkpmax, Hwdwkp, yykp, sita, cons_b, sita_s = 0, sita_c, fsita, dfdsita, dfsita, ssita = 0, csita = 0, hyd_A, hafsita, shafsita = 0, chafsita, sita_p = 0;
			// 中间变量
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
			// ================= 赋初值 ===============================
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
			// ----------------节点汇水面积(ha)和汇水流量(m3/sec)计算--------//
			// 芝加哥过程线--rainfall intensity at every time step--
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
			// 时段管井水位折线图和管井水位时段剖面图结果组织
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
			// 地面积水量结果组织
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

	// 第二套版本
	private String analog_Y2(String subSys, int timePeriod, String gjId, String AnalogWaterType)
	{
		WaterAcc	= new String[60];
		WaterLev	= new String[60];
		int SubgjId = 0;
		if (gjId != null)
		{
			SubgjId = CommUtil.StrToInt(gjId.substring(12, 15)) - 1;
		}
		try
		{
			// 管网基础数据：
			// 管段数，节点数，管道起点数，路径最大管段数，最大计算次数，模拟时段数，芝加哥峰点时段位置
			// 管道路径数，路径最大节点数，终点节点号，中间结果输出文件指针
			int NP = 9, NN = 10, Nstart = 3, Npline = 7, NT = 60, NR = 23, Nroute = 3, Nr_node = 8, Nend = 7, Iprt = 0, Nprtc = 20;
			// 暴雨公式参数shanghai storm water formular:
			// (A1+C*lgP)/(t+b)**n---ln(N)=2.303log(N)---出口水位（m）
			// 管段流速（m/s）, 管段设定流速vp0，地面凹凸系数csf
			double A1 = 17.53, C_storm = 0.95, tmin = 10, b_storm = 11.77, P_simu = 100, n_storm = 0.88, dt = 2.0, rc = 0.375, Hw_end = 4.1, vp0 = 0.8, csf = 3.0;

			// 子系统管段数据：
			int[] I0; // 管段上游节点号I0,
			int[] J0; // 下游节点号J0,
			double[] lp; // 管段长度
			double[] dpl; // 管段直径(m)
			double[] slp; // 摩|阻系数
			double[] ZJup; // 上游管底高程(m)
			double[] ZJdw; // 下游管底高程(m)

			// 子系统节点数据
			// 管网起始节点号和起始节点管底埋深<m>
			double[] Aj; // 节点汇水面积(ha)3.5
			double[] Acoef; // 节点汇水面积径流系数0.6
			double[] Hj; // 节点地面标高（m）[NN=23]

			// 管网路径数和路径节点号(-99表示空节点)
			int[][] Mroute;

			// 子系统分支路径管段数据矩阵 倒序pipe branches-reverse order
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
			 * 基础数据表格子系统号 节点数NN 管段数NP 起点数NStart 路径管段数Npline 路径节点数Nr_node
			 * 终点出口号Nend 模拟时段NT 管段路径数NrouteYJ002 10 9 3 7 8 8 60 3
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
			 * 子系统管段数据表格 Pipe.No 起点号I0 终点号J0 长度LP 直径DP 摩阻系数 起端标高 终端标高 1 0 1 28.5
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
			 * 子系统节点数据表格节点No 汇水面积ha 径流系数 地面标高 井底标高 1 3.5 0.6 5.244 暂未用到 2 3.5
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
			 * 管网路径数&路径节点号节点序号 1 2 3 4 5 6 7 8 1 0 1 2 3 4 5 6 7 2 8 7 -99 -99
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
			 * 子系统分支路径管段数据矩阵 倒序pipe branches-reverse order 节点序号 1 2 3 4 5 6 7 1
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
			// ----临界水深计算变量----
			double sita0 = 3.0, eps = 0.001, alfa = 0.5;
			double Ad0, qkpmax, Hwdwkp, yykp, sita, cons_b, sita_s = 0, sita_c, fsita, dfdsita, dfsita, ssita = 0, csita = 0, hyd_A, hafsita, shafsita = 0, chafsita, sita_p = 0;
			// 中间变量
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
			// --输出数据文件开始---
			// ================= 赋初值 ===============================
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
			// ================= 管网准稳态流动模拟============================
			// -------------------动态模拟流量计算-----------------------------
			// ----------------节点汇水面积(ha)和汇水流量(m3/sec)计算--------
			printStream.println();
			printStream.println("===========  管网动态模拟计算      重现期＝ " + P_simu + "  年   时段数＝ " + NT + "       终点水位＝ " + Hw_end + "  m  =========");
			// 芝加哥过程线--rainfall intensity at every time step--
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
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + df.format(Hwdw[it][kp]) + "  淹没出流 ");
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
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdw= " + Hwdw[it][kp] + "  非淹没出流 ");
								}
								// --20161018修改开始---采用临界水深简化算法-----------------------
								qkpmax = 2.699 * Math.pow(dpl[kp], 2.5);
								if (qpt[it][kp] > qkpmax)
								{
									if (Iprt == 1)
									{
										printStream.println("   it= " + it + "   kp= " + kp + "  qkpmax= " + qkpmax + "  非淹没满管出流 ");
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
										printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  非淹没非满管出流 ");
									}
									// ==20161018修改开始---采用临界水深简化公式--------zhou-p21------
									ycd0 = qpt[it][kp] / 2.983 / Math.pow(dpl[kp], 2.5);
									hdcc0[it][kp] = Math.pow(ycd0, 0.513);
									sita = 2.0 * Math.acos(1.0 - 2.0 * hdcc0[it][kp]);
									rid[it][kp] = 0.25 * dpl[kp] * (sita - Math.sin(sita)) / sita;
									vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slop[kp], 0.5) / slp[kp];
								}
								// ---for(k=0;k<N;k++)结束---20160907修改结束---临界水深算法--------------
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
							// ------- 输出it计算结果 ----------
							if (Iprt == 1)
							{
								printStream.println("   it= " + it + "   kp= " + kp + "   I0[kp]= " + I0[kp] + "  Hwdm= " + Hwdw[it][kp] + "  Hwup= " + Hwup[it][kp] + "  Hj= " + Hj[I0[kp]] + "  hdcc0= " + hdcc0[it][kp] + "  qpt= " + qpt[it][kp] + "  qqkp= " + qqkp[it][kp] + "  vpt= " + vpt[it][kp]);
							}
						}
					}
				}
				printStream.println();

				printStream.println("    it   管段号  I0   J0 管径dpl     管段qp   水力半径R  充满度 流速(m/s)  上游水位  下游水位  上管底高  下管底高  管段坡度  上地面高");
				for (i = 0; i < NP; i++)
				{
					printStream.printf("%6d%6d%6d%5d%8.2f%12.3f%10.3f%8.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.5f%10.3f", it, i, I0[i], J0[i], dpl[i], qpt[it][i], rid[it][i], hdcc0[it][i], vpt[it][i], Hwup[it][i], Hwdw[it][i], ZJup[i], ZJdw[i], slop[i], Hj[I0[i]]);
					printStream.println();
				}
				printStream.println();
				// -------------- 开始计算溢流节点 ---------------
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
				// ------------------ 计算溢流节点结束 ---------------
			}
			// ----------------屏幕输出计算结束------
			// System.out.println("------ 模型计算全部完成 ------");
			// ---------------------- 输出管段充满度计算结果 ---------------
			printStream.println(" ======== 时段管段充满度 ========");
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
			// ------------------- 输出节点水位计算结果 ---------------
			printStream.println(" ======== 时段节点水位 ========");
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

			// ***********组织数据，传到页面用于显示********
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
			// ------------------ 输出节点溢流计算结果 ---------------
			printStream.println(" ======== 时段节点积水量(m3) ========");
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

			// ***********组织数据，传到页面用于显示********
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
			printStream.println(" ======== 时段节点积水深度(mm) ========");
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

	// 第三套版本
	// 特别说明：这一版本和前两个版本所用的表格不一样
	private String analog_Y3(String subSys, int timePeriod, String gjId, String AnalogWaterType)
	{
		WaterAcc	= new String[60];
		WaterLev	= new String[60];
		int SubgjId = 0;
		if (gjId != null)
		{
			SubgjId = CommUtil.StrToInt(gjId.substring(12, 15)) - 1;
		}
		try
		{
			// 管网基础数据：
			// 管段数，节点数，管道起点数，路径最大管段数，最大计算次数，模拟时段数，芝加哥峰点时段位置
			// 管道路径数，路径最大节点数，终点节点号，中间结果输出文件指针
			int NP = 9, NN = 10, Nstart = 3, Npline = 7, NT = 60, NR = 23, Nroute = 3, Nr_node = 8, Nend = 7, Iprt = 0, Nprtc = 20;
			// 暴雨公式参数shanghai storm water formular:
			// (A1+C*lgP)/(t+b)**n---ln(N)=2.303log(N)---出口水位（m）
			// 管段流速（m/s）, 管段设定流速vp0，地面凹凸系数csf
			double A1 = 17.53, C_storm = 0.95, tmin = 10, b_storm = 11.77, P_simu = 100, n_storm = 0.88, dt = 2.0, rc = 0.375, Hw_end = 4.1, vp0 = 0.8, csf = 3.0;

			// 子系统管段数据：
			int[] I0; // 管段上游节点号I0,
			int[] J0; // 下游节点号J0,
			double[] lp; // 管段长度
			double[] dpl; // 管段直径(m)
			double[] slp; // 摩|阻系数
			double[] ZJup; // 上游管底高程(m)
			double[] ZJdw; // 下游管底高程(m)

			// 子系统节点数据
			// 管网起始节点号和起始节点管底埋深<m>
			double[] Aj; // 节点汇水面积(ha)3.5
			double[] Acoef; // 节点汇水面积径流系数0.6
			double[] Hj; // 节点地面标高（m）[NN=23]

			// 管网路径数和路径节点号(-99表示空节点)
			int[][] Mroute;

			// 管网路径起点号
			int[] Mstart;

			// 子系统分支路径管段数据矩阵 倒序pipe branches-reverse order
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
			 * 基础数据表格子系统号 节点数NN 管段数NP 起点数NStart 路径管段数Npline 路径节点数Nr_node
			 * 终点出口号Nend 模拟时段NT 管段路径数NrouteYJ002 10 9 3 7 8 8 60 3
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
			 * 子系统管段数据表格 Pipe.No 起点号I0 终点号J0 长度LP 直径DP 摩阻系数 起端标高 终端标高 1 0 1 28.5
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
			 * 子系统节点数据表格节点No 汇水面积ha 径流系数 地面标高 井底标高 1 3.5 0.6 5.244 暂未用到 2 3.5
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

			// **************在这一版本中去掉**********
			/**
			 * 管网路径数&路径节点号节点序号 1 2 3 4 5 6 7 8 1 0 1 2 3 4 5 6 7 2 8 7 -99 -99
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
			// *************这一版本中新加入内容********
			/**
			 * 管网路径起点号 序号 1 2 3 起点号 0 8 9
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
			 * 子系统分支路径管段数据矩阵 倒序pipe branches-reverse order 节点序号 1 2 3 4 5 6 7 1
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
			// ----临界水深计算变量----
			double sita0 = 3.0, eps = 0.001, alfa = 0.5;
			double Ad0, qkpmax, Hwdwkp, yykp, sita, cons_b, sita_s = 0, sita_c, fsita, dfdsita, dfsita, ssita = 0, csita = 0, hyd_A, hafsita, shafsita = 0, chafsita, sita_p = 0;
			// 中间变量
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
			// --输出数据文件开始---
			// ================= 赋初值 ===============================
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
			// =====20161029===== 生成矩阵 Mroute[i][j] ====
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
			// ================= 管网准稳态流动模拟============================
			// -------------------动态模拟流量计算-----------------------------
			// ----------------节点汇水面积(ha)和汇水流量(m3/sec)计算--------
			printStream.println();
			printStream.println("===========  管网动态模拟计算      重现期＝ " + P_simu + "  年   时段数＝ " + NT + "       终点水位＝ " + Hw_end + "  m  =========");
			// 芝加哥过程线--rainfall intensity at every time step--
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
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + df.format(Hwdw[it][kp]) + "  淹没出流 ");
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
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdw= " + Hwdw[it][kp] + "  非淹没出流 ");
								}
								// --20161018修改开始---采用临界水深简化算法-----------------------
								qkpmax = 2.699 * Math.pow(dpl[kp], 2.5);
								if (qpt[it][kp] > qkpmax)
								{
									if (Iprt == 1)
									{
										printStream.println("   it= " + it + "   kp= " + kp + "  qkpmax= " + qkpmax + "  非淹没满管出流 ");
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
										printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  非淹没非满管出流 ");
									}
									// ==20161018修改开始---采用临界水深简化公式--------zhou-p21------
									ycd0 = qpt[it][kp] / 2.983 / Math.pow(dpl[kp], 2.5);
									hdcc0[it][kp] = Math.pow(ycd0, 0.513);
									sita = 2.0 * Math.acos(1.0 - 2.0 * hdcc0[it][kp]);
									rid[it][kp] = 0.25 * dpl[kp] * (sita - Math.sin(sita)) / sita;
									vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slop[kp], 0.5) / slp[kp];
								}
								// ---for(k=0;k<N;k++)结束---20160907修改结束---临界水深算法--------------
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
							// ------- 输出it计算结果 ----------
							if (Iprt == 1)
							{
								printStream.println("   it= " + it + "   kp= " + kp + "   I0[kp]= " + I0[kp] + "  Hwdm= " + Hwdw[it][kp] + "  Hwup= " + Hwup[it][kp] + "  Hj= " + Hj[I0[kp]] + "  hdcc0= " + hdcc0[it][kp] + "  qpt= " + qpt[it][kp] + "  qqkp= " + qqkp[it][kp] + "  vpt= " + vpt[it][kp]);
							}
						}
					}
				}
				printStream.println();

				printStream.println("    it   管段号  I0   J0 管径dpl     管段qp   水力半径R  充满度 流速(m/s)  上游水位  下游水位  上管底高  下管底高  管段坡度  上地面高");
				for (i = 0; i < NP; i++)
				{
					printStream.printf("%6d%6d%6d%5d%8.2f%12.3f%10.3f%8.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.5f%10.3f", it, i, I0[i], J0[i], dpl[i], qpt[it][i], rid[it][i], hdcc0[it][i], vpt[it][i], Hwup[it][i], Hwdw[it][i], ZJup[i], ZJdw[i], slop[i], Hj[I0[i]]);
					printStream.println();
				}
				printStream.println();
				// -------------- 开始计算溢流节点 ---------------
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
				// ------------------ 计算溢流节点结束 ---------------
			}
			// ----------------屏幕输出计算结束------
			// System.out.println("------ 模型计算全部完成 ------");
			// ---------------------- 输出管段充满度计算结果 ---------------
			printStream.println(" ======== 时段管段充满度 ========");
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
			// ------------------- 输出节点水位计算结果 ---------------
			printStream.println(" ======== 时段节点水位 ========");
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

			// ***********组织数据，传到页面用于显示********
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
			// ------------------ 输出节点溢流计算结果 ---------------
			printStream.println(" ======== 时段节点积水量(m3) ========");
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

			// ***********组织数据，传到页面用于显示********
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
			printStream.println(" ======== 时段节点积水深度(mm) ========");
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

	// 第四套版本
	// 特别说明：这一版本和前三个版本所用的表格不一样
	private String analog_Y4(String subSys, int timePeriod, String gjId, String AnalogWaterType)
	{
		WaterAcc	= new String[60];
		WaterLev	= new String[60];
		int SubgjId = 0;
		if (gjId != null)
		{
			SubgjId = CommUtil.StrToInt(gjId.substring(12, 15)) - 1;
		}
		try
		{
			// 管网基础数据：
			// 管段数，节点数，管道起点数，路径最大管段数，最大计算次数，模拟时段数，芝加哥峰点时段位置
			// 管道路径数，路径最大节点数，终点节点号，中间结果输出文件指针
			int NP = 9, NN = 10, Nstart = 3, Npline = 7, NT = 60, NR = 23, Nr_node = 8, Nend = 7, Iprt = 0, Nprtc = 20;
			int Ncol = 5;// 节点最大流入管段数+3，宜大不宜小

			// 暴雨公式参数shanghai storm water formular:
			// (A1+C*lgP)/(t+b)**n---ln(N)=2.303log(N)---出口水位（m）
			// 管段流速（m/s）, 管段设定流速vp0，地面凹凸系数csf
			double A1 = 17.53, C_storm = 0.95, b_storm = 11.77, P_simu = 10, n_storm = 0.88, dt = 2.0, rc = 0.375, Hw_end = 4.2, vp0 = 0.8, csf = 1.0;

			// 子系统管段数据：
			int[] I0; // 管段上游节点号I0,
			int[] J0; // 下游节点号J0,
			double[] lp; // 管段长度
			double[] dpl; // 管段直径(m)
			double[] slp; // 摩|阻系数
			double[] ZJup; // 上游管底高程(m)
			double[] ZJdw; // 下游管底高程(m)

			// 子系统节点数据
			// 管网起始节点号和起始节点管底埋深<m>
			double[] Aj; // 节点汇水面积(ha)3.5
			double[] Acoef; // 节点汇水面积径流系数0.6
			double[] Hj; // 节点地面标高（m）[NN=23]

			// 管网路径数和路径节点号(-99表示空节点)
			int[][] Mroute;

			// 管网路径起点号
			int[] Mstart;

			// 子系统分支路径管段数据矩阵 倒序pipe branches-reverse order
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
			 * 基础数据表格子系统号 节点数NN 管段数NP 起点数NStart 路径管段数Npline 路径节点数Nr_node
			 * 终点出口号Nend 模拟时段NT 管段路径数NrouteYJ002 10 9 3 7 8 8 60 3
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
			 * 子系统管段数据表格 Pipe.No 起点号I0 终点号J0 长度LP 直径DP 摩阻系数 起端标高 终端标高 1 0 1 28.5
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
			 * 子系统节点数据表格节点No 汇水面积ha 径流系数 地面标高 井底标高 1 3.5 0.6 5.244 暂未用到 2 3.5
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

			// **************在Y3版本中去掉**********
			/**
			 * 管网路径数&路径节点号节点序号 1 2 3 4 5 6 7 8 1 0 1 2 3 4 5 6 7 2 8 7 -99 -99
			 * -99 -99 -99 -99 3 9 6 -99 -99 -99 -99 -99 -99
			 */
			/**
			 * Mroute = new int[Nstart][Nr_node]; for (int j = 0; j < Nstart;
			 * j++) { for (int k = 0; k < Nr_node; k++) { Mroute[j][k] =
			 * Integer.parseInt(rs.getCell(k + 1, rowCnt +
			 * j).getContents().trim()); } } rowCnt += Nstart; rowCnt += 3;
			 */
			// *******************************
			// 管网起点号-路径数和路径节点号矩阵
			Mroute = new int[Nstart][Nr_node];
			Mstart = new int[Nstart];

			// *************这一版本去掉中去掉******
			/**
			 * for (int j = 0; j < Nstart; j++) { Mstart[j] =
			 * Integer.parseInt(rs.getCell(j + 1, rowCnt).getContents().trim());
			 * } rowCnt += 1; rowCnt += 3;
			 */
			// ************************************

			/*
			 * 子系统分支路径管段数据矩阵 倒序pipe branches-reverse order 节点序号 1 2 3 4 5 6 7 1
			 * 6 5 4 3 2 1 0 2 7 -99 -99 -99 -99 -99 -99 3 8 -99 -99 -99 -99 -99
			 * -99
			 */
			Mbranch = new int[Nstart][Npline];
			// *********这一版本中去掉***********
			/**
			 * for (int j = 0; j < Nstart; j++) { for (int k = 0; k < Npline;
			 * k++) { Mbranch[j][k] = Integer.parseInt(rs.getCell(k + 1, rowCnt
			 * + j).getContents().trim()); } }
			 */
			// ----临界水深计算变量----
			double Ad0, qkpmax, Hwdwkp, yykp, sita;

			// ----中间指标变量----
			int i00, j00 = 0, Ni1, jj, jp0, inp = 0, jpp, NPP;

			// 管网分叉支线管段矩阵-倒序排列
			int[] Npjun = new int[NP];
			int[][] MNP = new int[NN][Ncol];

			// 中间变量
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
			// --输出数据文件开始---
			// ================= 赋初值 ===============================
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
			// ====20161106===== 生成矩阵 MNP[i][j] ====
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
			// ----- MNP[i][j] 结束 ------
			// ====20161112===== 生成矩阵 Mstart[i] ====
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
			// ====20161029===== 生成矩阵 Mroute[i][j] ====
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
			// ====20161106===== 生成矩阵Mbranch[i][j] ====
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
			// === 生成矩阵 Mbranch[i][j] 结束====
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
			// ================= 管网准稳态流动模拟============================
			//
			// -------------------动态模拟流量计算-----------------------------
			// ----------------节点汇水面积(ha)和汇水流量(m3/sec)计算--------

			printStream.println();
			printStream.println("===========  管网动态模拟计算      重现期＝ " + P_simu + "  年   时段数＝ " + NT + "       终点水位＝ " + Hw_end + "  m  =========");
			// 芝加哥过程线--rainfall intensity at every time step--
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
			// =====芝加哥过程线--结束=====
			// =====计算节点集水面积sumAj[it][j]=====
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
			// -------------管段水力计算开始--------------
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
				// -------------------20090127-sql代码------------------------
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
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  淹没出流 ");
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
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdw= " + Hwdw[it][kp] + "  非淹没出流 ");
								}
								// --20161018修改开始---采用临界水深简化算法-----------------------
								//
								qkpmax = 2.699 * Math.pow(dpl[kp], 2.5);
								if (qpt[it][kp] > qkpmax)
								{
									if (Iprt == 1)
									{
										printStream.println("   it= " + it + "   kp= " + kp + "  qkpmax= " + qkpmax + "  非淹没满管出流 ");
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
										printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  非淹没非满管出流 ");
									}
									// ==20161115修改---采用均匀流正常水深简化公式开始--------
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
									// ==20161115修改---采用均匀流正常水深简化公式结束--------
									sita = 2.0 * Math.acos(1.0 - 2.0 * hdcc0[it][kp]);
									rid[it][kp] = 0.25 * dpl[kp] * (sita - Math.sin(sita)) / sita;
									vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slop[kp], 0.5) / slp[kp];
								}
								// ---if(qpt[it][kp]>qkpmax)结束---
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
								// ------- 输出it计算结果 ----------
							if (Iprt == 1)
							{
								printStream.println("   it= " + it + "   kp= " + kp + "   I0[kp]= " + I0[kp] + "  Hwdm= " + Hwdw[it][kp] + "  Hwup= " + Hwup[it][kp] + "  Hj= " + Hj[I0[kp]] + "  hdcc0= " + hdcc0[it][kp] + "  qpt= " + qpt[it][kp] + "  qqkp= " + qqkp[it][kp] + "  vpt= " + vpt[it][kp]);
							}
						}// --4 if(kp>=0) end
					}// --3 ---jk end
				}// --2---ik end
				printStream.println();
				printStream.println("    it   管段号  I0   J0 管径dpl     管段qp 水力半径R  充满度 流速(m/s)  上游水位  下游水位  上管底高  下管底高  管段坡度  上地面高");
				for (i = 0; i < NP; i++)
				{
					printStream.printf("%6d%6d%6d%5d%8.2f%12.3f%10.3f%8.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.5f%10.3f", it, i, I0[i], J0[i], dpl[i], qpt[it][i], rid[it][i], hdcc0[it][i], vpt[it][i], Hwup[it][i], Hwdw[it][i], ZJup[i], ZJdw[i], slop[i], Hj[I0[i]]);
					printStream.println();
				}
				printStream.println();
				// -------------- 开始计算节点水位-节点积水量和积水深度 ---------------
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
				// ------------------ 计算溢流节点结束 ---------------
			}// 1-- it end ---
				// ----------------屏幕输出计算结束------
			// System.out.println("------ 模型计算全部完成 ------");
			// --------------------------------- 输出管段充满度计算结果 ---------------
			// outfile<<" ======== 时段管段充满度 ========"<<endl;
			printStream.println(" ======== 时段管段充满度 ========");
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
			// ----------- 输出节点水位计算结果 ---------------
			printStream.println(" ======== 时段节点水位 ========");
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
			// ***********组织数据，传到页面用于显示********
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

			// --------------- 输出节点溢流计算结果 ---------------

			printStream.println(" ======== 时段节点积水量(m3) ========");
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
			// ***********组织数据，传到页面用于显示********
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
			printStream.println(" ======== 时段节点积水深度(mm) ========");
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
	
	// 第五套版本
	private String analog_Y5(String subSys, int timePeriod, String gjId, String gxId, String AnalogWaterType, double pSimu)
	{
		int SubgjId = 0;
		if (gjId != null)
		{
			SubgjId = CommUtil.StrToInt(gjId.substring(12, 15)) - 1;
		}
		int SubgxId = 0;
		if (gxId != null)
		{
			SubgxId = CommUtil.StrToInt(gxId.substring(5, 8)) - 1; //YJ001001
		}
		try
			{
			// 管网基础数据：
			// 管段数，节点数，管道起点数，路径最大管段数，中间矩阵MNP[NN][Ncol]列数，模拟时段数，降雨峰值时段,终点节点号，中间结果输出指针，输出数据表列数
			int NP = 9, NN = 10, Nstart = 3, Npline = 7, Ncol = 6, NT = 60, NR = 23, Nend = 7, Iprt = 0, Nprtc = 20;
			// 暴雨公式参数shanghai storm water formular:
			// (A1+C*lgP)/(t+b)**n--ln(N)=2.303log(N)--出口水位（m）,地面凹凸系数csf，路沿高度heage-mm
			double A1 = 20.12, C_storm = 0.639, b_storm = 11.945, n_storm = 0.825, dt = 2.0, Hw_end = 3.0, csf = 2.0, heage = 180;
			// P_simu=5.0,,rc=0.375
			// 节点汇水面积(ha),节点汇水区径流系数,节点地面标高（m）,节点汇入流量m3
			// 节点汇水面积(ha)
			
			//子系统管段数据
			int[] I0;			// 上游节点号I0
			int[] J0;			// 下游节点号J0
			double[] lp;		// 管段长度(m)
			double[] dpl;		// 管段直径(m)
			double[] slp;		// 摩阻系数
			double[] ZJup;		// 上游管底高程ZJup[NP](m)
			double[] ZJdw;		// 下游管底高程ZJdw[NP](m)
			
			//子系统节点数据
			double[] Aj;		// 节点汇水面积(ha)
			double[] Hj;		// 节点地面标高（m）
			double[] Acoef;		// 节点汇水区径流系数
			
			/*
			double[] Aj = new double[] { 0.15, 0.15, 0.15, 0.15, 0.15, 0.15, 0.15, 0.15, 0.15, 0.15 };
			// 节点汇水区径流系数
			double[] Acoef = new double[] { 0.62, 0.62, 0.62, 0.62, 0.62, 0.62, 0.62, 0.62, 0.62, 0.62 };
			// 节点地面标高（m）
			double[] Hj = new double[] { 5.24, 5.19, 5.18, 5.00, 5.21, 5.20, 5.20, 5.12, 5.13, 5.18 };
			// 管段上游节点号I0,下游节点号J0，上游管底高程ZJup[NP](m)，下游管底高程ZJdw[NP](m)
			int[] I0 = new int[] { 0, 1, 2, 3, 4, 5, 6, 8, 9 };
			int[] J0 = new int[] { 1, 2, 3, 4, 5, 6, 7, 7, 6 };
			double[] ZJup = new double[] { 3.89, 3.84, 3.78, 3.73, 3.68, 3.64, 3.60, 3.73, 3.88 };
			double[] ZJdw = new double[] { 3.84, 3.78, 3.73, 3.68, 3.64, 3.60, 3.55, 3.60, 3.70 };
			// 管段长度(m),管段直径(m),摩阻系数
			double[] lp = new double[] { 50, 50, 50, 50, 50, 50, 50, 50, 50 };
			double[] dpl = new double[] { 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3 };
			double[] slp = new double[] { 0.014, 0.014, 0.014, 0.014, 0.014, 0.014, 0.014, 0.014, 0.014 };
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
			 * 基础数据表格子系统号 节点数NN 管段数NP 起点数NStart 路径管段数Npline 路径节点数Nr_node
			 * 终点出口号Nend 模拟时段NT 管段路径数NrouteYJ002 10 9 3 7 8 8 60 3
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
			 * 子系统管段数据表格 Pipe.No 起点号I0 终点号J0 长度LP 直径DP 摩阻系数 起端标高 终端标高 1 0 1 28.5
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
				rowCnt ++;
			}
			rowCnt += 3;

			/*
			 * 子系统节点数据表格节点No 汇水面积ha 径流系数 地面标高 井底标高 1 3.5 0.6 5.244 暂未用到 2 3.5
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
				rowCnt ++;
			}
			rowCnt += 3;
			
			// ===================
			// ----中间指标变量---
			int i00, j00 = 0, Ni1, jj, jp0, inp = 0, jpp, NPP;
			// 管网起始节点号矩阵Mstart-节点关联矩阵MNP-中间变换矩阵 Npjun-管网分支线管段矩阵Mbranch(倒序排列)
			int[] Mstart = new int[Nstart];
			int[][] MNP = new int[NN][Ncol];
			int[] Npjun = new int[NP];
			int[][] Mbranch = new int[Nstart][Npline];
			// ----中间变量----
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
			
			WaterAcc	= new String[NT];
			WaterLev	= new String[NT];
			WaterFlowLoad	= "";
			WaterActualFlow	= "";
			WaterFlowRate	= "";
			
			WaterAccGj  = "";
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
	
			/*System.out.println("请输入降雨强度重现期P（年）:");
			Scanner input = new Scanner(System.in);
			P_simu = input.nextDouble();*/
			
			//设置降雨强度
			P_simu = pSimu;
			
			printStream.println("===  重现期＝ " + P_simu + "  年     时段数＝ " + NT + "     终点水位＝ " + Hw_end + "  m  ===");
			//System.out.println();
			//System.out.println("pipe no.  I0    J0      lp     dpl     slp    ZJup    ZJdw");
			for (i = 0; i < NP; i++)
			{
				//System.out.printf("%6d%6d%6d%8.2f%8.2f%8.3f%8.2f%8.2f", i, I0[i], J0[i], lp[i], dpl[i], slp[i], ZJup[i], ZJdw[i]);
				//System.out.println();
			}
			//System.out.println();
			//System.out.println("===  重现期＝ " + P_simu + "  年     时段数＝ " + NT + "     终点水位＝ " + Hw_end + "  m  ===");
			printStream.println();
			printStream.println("pipe no.  I0    J0      lp     dpl     slp    ZJup    ZJdw");
			for (i = 0; i < NP; i++)
			{
				printStream.printf("%6d%6d%6d%8.2f%8.2f%8.3f%8.2f%8.2f", i, I0[i], J0[i], lp[i], dpl[i], slp[i], ZJup[i], ZJdw[i]);
				printStream.println();
			}
			printStream.println();
			// ================= 计算slop[k] ===========
			for (k = 0; k < NP; k++)
			{
				slop[k] = (ZJup[k] - ZJdw[k]) / lp[k];
			}
			// ====20161106===== 生成矩阵 MNP[i][j] ====
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
			// ----- MNP[i][j] 结束 ------
			// ====20161112===== 生成矩阵 Mstart[i] ====
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
			// ====20161106===== 生成矩阵Mbranch[i][j] ====
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
			// === 生成矩阵 Mbranch[i][j] 结束====
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
			// ================= 管网准稳态水力模拟============================
			//
			// ----------------节点汇水面积(ha)和汇水流量(m3/sec)计算----------
			//
			// 芝加哥过程线--rainfall intensity at every time step--
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
			// 计算平均降雨强度mm/min
			XX1 = 0;
			for (it = 0; it < NT; it++)
			{
				XX1 = XX1 + XX[it];
			}
			XX1 = XX1 / (float) (NT);
			// 暴雨公式降雨强度mm/min
			taa = dt * (float) (NT) + b_storm;
			XX2 = AA / Math.pow(taa, n_storm);
			printStream.println();
			printStream.println(" ====== 降雨强度曲线数据结果 ======   平均强度XX1= " + XX1 + "(mm/min)   公式强度XX2= " + XX2 + "(mm/min)" + "    rc= " + rc);
			printStream.println("    it      dtnt XX[it](mm/min) qit[it](m3/ha-sec)");
			for (it = 0; it < NT; it++)
			{
				dtnt = dt * (float) (it);
				printStream.printf("%6d%10.2f%12.4f%15.4f", it, dtnt, XX[it], qit[it]);
				printStream.println();
			}
			printStream.println();
			// ============芝加哥过程线--结束=============
			// -------------管段水力计算开始--------------
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
				// ----------计算管段流量------------
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
							//if (J0[k] == I0[j]) qpt[it][j] = qpt[it][j] + qpt[it - 1][k];
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
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  淹没出流 ");
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
									//*******20170306
//									Hwup[it][kp] = Hj[I0[kp]];
//									slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
//									sigh_kp = 1.0;
//									slopt0 = slopt[it][kp];
//									if (slopt[it][kp] < 0.0)
//									{
//										slopt0 = -slopt0;
//										sigh_kp = -1.0;
//									}
//									vpt[it][kp] = sigh_kp * Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt0, 0.5) / slp[kp];
//									qqkp[it][kp] = vpt[it][kp] * Ad0;
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
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdw= " + Hwdw[it][kp] + "  非淹没出流 ");
								}
								// --20161018---计算临界水深------------
								qkpmax = 2.699 * Math.pow(dpl[kp], 2.5);
								if (qpt[it][kp] > qkpmax)
								{
									if (Iprt == 1)
									{
										printStream.println("   it= " + it + "   kp= " + kp + "  qkpmax= " + qkpmax + "  非淹没满管出流 ");
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
//										Hwup[it][kp] = Hj[I0[kp]];
//										slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
//										sigh_kp = 1.0;
//										slopt0 = slopt[it][kp];
//										if (slopt[it][kp] < 0.0)
//										{
//											slopt0 = -slopt0;
//											sigh_kp = -1.0;
//										}
//										vpt[it][kp] = sigh_kp * Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt0, 0.5) / slp[kp];
//										qqkp[it][kp] = vpt[it][kp] * Ad0;
										//***********20170306
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
										printStream.println("   it= " + it + "   kp= " + kp + "  Hwdw= " + Hwdw[it][kp] + "  非淹没非满管出流 ");
									}
									// ==20161115---计算水深开始--------
//									if (slop[kp] > 0)
//									{// ----正常水深----
//										if (qpt[it][kp] >= 0.0)
//										{
//											ycd0 = 20.1538 * slp[kp] * qpt[it][kp] / Math.pow(dpl[kp], 2.6667) / Math.pow(slop[kp], 0.5);
//											if (ycd0 <= 1.5)
//											{
//												hdcc0[it][kp] = 0.27 * Math.pow(ycd0, 0.485);
//											}
//											else
//											{
//												hdcc0[it][kp] = 0.098 * ycd0 + 0.19;
//											}
//											if (hdcc0[it][kp] <= 0.0001)
//											{
//												hdcc0[it][kp] = 0.0001;
//											}
//										}
//										else
//										{
//											hdcc0[it][kp] = 1.0;
//										}
//									}
//									else
//									{// ----临界水深----
//										if (qpt[it][kp] >= 0.0)
//										{
//											ycd0 = qpt[it][kp] / 2.983 / Math.pow(dpl[kp], 2.5);
//											hdcc0[it][kp] = Math.pow(ycd0, 0.513);
//											if (hdcc0[it][kp] <= 0.0001)
//											{
//												hdcc0[it][kp] = 0.0001;
//											}
//										}
//										else
//										{
//											hdcc0[it][kp] = 1.0;
//										}
//									}
//									if (hdcc0[it][kp] > 1.0)
//									{
//										hdcc0[it][kp] = 1.0;
//									}
									// ----20170307修改开始----
									if (slop[kp] < 0.0001)
									{
										slop[kp] = 0.0001;
									}
									// ----正常水深公式----
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
									// ----20170307修改结束----

									//20170217替换
									/*ycd0 = 20.1538 * slp[kp] * qpt[it][kp] / Math.pow(dpl[kp], 2.6667) / Math.pow(slop[kp], 0.5);
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
									if (hdcc0[it][kp] > 1.0)
									{
										hdcc0[it][kp] = 1.0;
									}*/

									// ==20161115---计算水深结束-------
									// ----20170307a--添加语句开始-----
									hdj0 = ZJdw[kp] + hdcc0[it][kp] * dpl[kp];
									if (hdj0 < Hwdw[it][kp])
									{
										hdcc0[it][kp] = (Hwdw[it][kp] - ZJdw[kp]) / dpl[kp];
									}
									// ----20170307a--添加语句结束-----
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
//										Hwup[it][kp] = Hj[I0[kp]];
//										slopt[it][kp] = (Hwup[it][kp] - Hwdw[it][kp]) / lp[kp];
//										sigh_kp = 1.0;
//										slopt0 = slopt[it][kp];
//										if (slopt[it][kp] < 0.0)
//										{
//											slopt0 = -slopt0;
//											sigh_kp = -1.0;
//										}
//										vpt[it][kp] = sigh_kp * Math.pow(rid[it][kp], 0.6667) * Math.pow(slopt0, 0.5) / slp[kp];
//										qqkp[it][kp] = vpt[it][kp] * Ad0;
										//*********20170306
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
							// ------- 输出it计算结果 ----------
							if (Iprt == 1)
							{
								printStream.println("   it= " + it + "   kp= " + kp + "   I0[kp]= " + I0[kp] + "  Hwdm= " + Hwdw[it][kp] + "  Hwup= " + Hwup[it][kp] + "  Hj= " + Hj[I0[kp]] + "  hdcc0= " + hdcc0[it][kp] + "  qpt= " + qpt[it][kp] + "  qqkp= " + qqkp[it][kp] + "  vpt= " + vpt[it][kp]);
							}
						}// --4 if(kp>=0) end
					}// --3 ---jk end
				}// --2---ik end
				// -------------- 计算节点水位-节点积水量和积水深度 ---------------
				/*for (i = 0; i < NP; i++)
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
							if (it > NR && Hw_over[it][j] <= 5.0)
							{
								overflow[it][j] = 0.0;
								Hw_over[it][j] = 0.0;
							}
						}
					}
				}*/
				//20170217修改
				for (i = 0; i < NP; i++)
				{
					k = J0[i];
					if (k == Nend)
					{
						Hwj[it][k] = Hw_end;
					}
//					if(it > 0){
//						j = I0[i];
//						Hwj[it][j] = Hwup[it][i];
//						if (Hwup[it][i] >= Hj[j] || overflow[it - 1][j] > 0.0)
//						// if(Hwup[it][i]>=Hj[j])
//						{
//							overflow[it][j] = overflow[it - 1][j] + (qpt[it][i] - qqkp[it][i]) * dt * 60.0;
//							Hw_over[it][j] = overflow[it][j] / Aj[j] / 10000.0 * 1000.0;
//						}
//						if (Hwup[it][i] < Hj[j] && overflow[it - 1][j] > 0.0)
//						{
//							overflow[it][j] = overflow[it - 1][j] * 0.90;
//							Hw_over[it][j] = overflow[it][j] / Aj[j] / 10000.0 * 1000.0;
//							if (Hw_over[it][j] > heage)
//							{
//								Hw_over[it][j] = heage + (overflow[it][j] - Aj[j] * heage / 1000.0) / csf / 10000.0 * 1000.0;
//							}
//						}
//					}
//					if (it > NR && Hw_over[it][j] <= 5.0)
//					{
//						overflow[it][j] = 0.0;
//						Hw_over[it][j] = 0.0;
//					}
					//**********20170306
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
				//修改结束
				printStream.println();
				printStream.println("    it   管段号  I0   J0 管径dpl    管段qpt 水力半径R    充满度 流速(m/s)  上游水位  下游水位  上管底高  下管底高  管段坡度  上地面高  水力坡度    qqkp");
				for (i = 0; i < NP; i++)
				{
					printStream.printf("%6d%6d%6d%5d%8.2f%12.4f%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.4f%10.4f", it, i, I0[i], J0[i], dpl[i], qpt[it][i], rid[it][i], hdcc0[it][i], vpt[it][i], Hwup[it][i], Hwdw[it][i], ZJup[i], ZJdw[i], slop[i], Hj[I0[i]], slopt[it][i], qqkp[it][i]);
					printStream.println();
				}
				printStream.println();
				// ------------ 计算溢流节点结束 ----
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
				// --------------屏幕输出计算结束------
			
			// xxxxxxx20170416-时间序列平均值开始xxxxxxxxxxxxx
			// -----20170416-时间序列平均值开始---------------
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
			// -----20170416-时间序列平均值结束---------------
			// xxxxxxx20170416-时间序列平均值结束xxxxxxxxxxxxx

				// --------------- 输出管段充满度计算结果 ---------------
			printStream.println(" ======== 时段管段充满度 ========");
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
			// ----------------- 输出节点水位计算结果 ---------------
			printStream.println(" ======== 时段节点水位 ========");
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
			// ***********组织数据，传到页面用于显示********
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
			// -------------------- 输出节点溢流计算结果 ---------------
			printStream.println(" ======== 时段节点积水量(m3) ========");
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
			// ***********组织数据，传到页面用于显示********
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
			printStream.println(" ======== 时段节点积水深度(mm) ========");
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
			// ***********组织数据，传到页面用于显示*****20170120***
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
			
			printStream.println("------ 模型计算完成 ------");
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			return gjName + "," + "NumberFormat" + "," + (rowCnt + 1);
		}
		catch(ArrayIndexOutOfBoundsException e)
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

	// 模拟排污第一套
	private String analog_W1(String subSys, int timePeriod, String gjId, String AnalogWaterType)
	{
		SewageAcc	= new String[24];
		SewageLev	= new String[24];
		int SubgjId = 0;
		if (gjId != null)
		{
			SubgjId = CommUtil.StrToInt(gjId.substring(12, 15)) - 1;
		}
		try
		{
			// subSys = 900001_WJ001
			// 管网基础数据：
			// 管段数，节点数，管道起点数，路径最大管段数，模拟时段数,
			// 管道路径数，路径最大节点数，终点节点号，中间结果输出文件指针，输出数据表列数
			int NP = 9, NN = 10, Nstart = 3, Npline = 7, NT = 24, Nr_node = 8, Nend = 7, Iprt = 0, Nprtc = 20;
			// 污水流量数据
			// 人均日排水量（m/d）, 管段设定流速vp0，时间步长（h），子系统终点水位，地面凹凸系数csf
			double q1 = 0.45, vp0 = 0.8, dt = 1.0, Hw_end = 4.1, csf = 3.0;
			// 节点地面面积(ha)， 节点地面标高（m），节点服务人口(人)
			double[] Aj;
			double[] Hj;
			double[] Rj;
			// 排水量变化曲线（NT）
			double[] Rf;
			// 管网路径数和路径节点号(－99表示空节点)
			int[][] Mroute;
			int[][] Mbranch;
			// 管段上游节点号I0,下游节点号J0，管段长度(m),摩阻系数
			int[] I0;
			int[] J0;
			double[] lp;
			double[] slp;
			// 节点起点号
			int[] Mstart;
			// 管段直径(m)，上游管底高程(m)，下游管底高程(m)
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
			 * 基础数据表格子系统号 节点数NN 管段数NP 起点数NStart 路径管段数Npline 路径节点数Nr_node
			 * 终点出口号Nend 模拟时段NT 管段路径数NrouteYJ002 10 9 3 7 8 8 60 3
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
			 * 子系统管段数据表格 Pipe.No 起点号I0 终点号J0 长度LP 直径DP 摩阻系数 起端标高 终端标高 1 0 1 28.5
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
			 * 子系统节点数据表格 节点No 地面面积Aj 地面标高 节点服务人口 1 0.2 5.244 80 2 0.2 5.191 80 3
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
			 * 管网路径起点号 序号 1 2 3 起点号 0 8 9
			 */
			Mstart = new int[Nstart];
			for (int j = 0; j < Nstart; j++)
			{
				Mstart[j] = Integer.parseInt(rs.getCell(j + 1, rowCnt).getContents().trim());
			}
			rowCnt += 1;
			rowCnt += 3;

			/*
			 * 子系统分支路径管段数据矩阵 倒序 节点序号 1 2 3 4 5 6 7 1 6 5 4 3 2 1 0 2 7 -99 -99
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
			 * 排水量变化曲线 时段 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21
			 * 22 23 24 曲线 2.12 2.19 2.18 2.8 3.21 3.9 5.2 5.62 5.63 5.08 5.12
			 * 5.69 5.28 4.52 4.51 4.58 5.5 5.62 5.13 5.18 3.4 3.12 2.22 2.2
			 */
			Rf = new double[NT];
			for (int j = 0; j < NT; j++)
			{
				Rf[j] = Double.parseDouble(rs.getCell(j + 1, rowCnt).getContents().trim());
			}
			// ----中间变量----
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
			printStream.println("20161030-污水管网模拟-华家池-3.txt");
			// System.out.println("------ 污水管网模拟-华家池 ------");
			// ================= 赋初值 ===============================
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
			// =====20161029===== 生成矩阵 Mroute[i][j] ====
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

			// ----------------各管段总服务人口(人)和汇水流量(m3/sec)计算------
			printStream.println();
			printStream.println("======  污水管网动态模拟   人均日用水量＝ " + q1 + "  m3   时段数＝ " + NT + "       终点水位＝ " + Hw_end + "  m  =====");

			// 人均排水量变化曲线---discharge at every time step per head---
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
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  淹没出流 ");
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
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdw= " + Hwdw[it][kp] + "  非淹没出流 ");
								}
								qkpmax = 2.699 * Math.pow(dpl[kp], 2.5);
								if (qpt[it][kp] > qkpmax)
								{
									if (Iprt == 1)
									{
										printStream.println("   it= " + it + "   kp= " + kp + "  qkpmax= " + qkpmax + "  非淹没满管出流 ");
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
										printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  非淹没非满管出流 ");
									}
									// ==20161018修改开始---采用临界水深简化公式--------zhou-p21------
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
				printStream.println("    it   管段号  I0   J0 管径dpl     管段qp 水力半径R  充满度 流速(m/s)  上游水位  下游水位  上管底高  下管底高  管段坡度  上地面高");
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
			// System.out.println("------ 模型计算全部完成 ------");
			// --------------- 输出管段充满度计算结果 ---------------
			printStream.println(" ======== 时段管段充满度 ========");
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
			// --------------------- 输出节点水位计算结果 ---------------
			printStream.println(" ======== 时段节点水位 ========");
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
			// ***********组织数据，传到页面用于显示********
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
			// ---------------- 输出节点溢流计算结果 ---------------
			printStream.println(" ======== 时段节点积水量(m3) ========");
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
			printStream.println(" ======== 时段节点积水深度(mm) ========");
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
			return gjName + "," +Count;
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

	// 模拟排污第二套
	// 特别说明：和第一套版本的数据表格不一样
	private String analog_W2(String subSys, int timePeriod, String gjId, String AnalogWaterType)
	{
		SewageAcc	= new String[24];
		SewageLev	= new String[24];
		SewageAccGj = "";
		int SubgjId = 0;
		if (gjId != null)
		{
			SubgjId = CommUtil.StrToInt(gjId.substring(12, 15)) - 1;
		}
		try
		{
			// subSys = 900001_WJ001
			// 管网基础数据：
			// 管段数，节点数，管道起点数，路径最大管段数，模拟时段数,
			// 管道路径数，路径最大节点数，终点节点号，中间结果输出文件指针，输出数据表列数
			int NP = 9, NN = 10, Nstart = 3, Npline = 7, NT = 24, Ncol = 5, NR = 18, Nr_node = 8, Nend = 7, Iprt = 0, Nprtc = 20;
			// 污水流量数据
			// 人均日排水量（m/d）, 管段设定流速vp0，时间步长（h），子系统终点水位，地面凹凸系数csf
			double q1 = 0.45, vp0 = 0.8, dt = 1.0, Hw_end = 3.0, csf = 1.0;
			// 节点地面面积(ha)， 节点地面标高（m），节点服务人口(人)
			double[] Aj;
			double[] Hj;
			double[] Rj;
			// 排水量变化曲线（NT）
			double[] Rf;
			// 管网路径数和路径节点号(－99表示空节点)
			int[][] Mroute;
			int[][] Mbranch;
			// 管段上游节点号I0,下游节点号J0，管段长度(m),摩阻系数
			int[] I0;
			int[] J0;
			double[] lp;
			double[] slp;
			// 节点起点号
			int[] Mstart;
			// 管段直径(m)，上游管底高程(m)，下游管底高程(m)
			double[] dpl;
			double[] ZJup;
			double[] ZJdw;

			// 管网分叉支线管段矩阵-倒序排列
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
			 * 基础数据表格子系统号 节点数NN 管段数NP 起点数NStart 路径管段数Npline 路径节点数Nr_node
			 * 终点出口号Nend 模拟时段NT 管段路径数NrouteYJ002 10 9 3 7 8 8 60 3
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
			 * 子系统管段数据表格 Pipe.No 起点号I0 终点号J0 长度LP 直径DP 摩阻系数 起端标高 终端标高 1 0 1 28.5
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
			 * 子系统节点数据表格 节点No 地面面积Aj 地面标高 节点服务人口 1 0.2 5.244 80 2 0.2 5.191 80 3
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
			 * 管网路径起点号 序号 1 2 3 起点号 0 8 9
			 */
			// ************这一版本中去掉*******
			Mstart = new int[Nstart];
			/**
			 * for (int j = 0; j < Nstart; j++) { Mstart[j] =
			 * Integer.parseInt(rs.getCell(j + 1, rowCnt).getContents().trim());
			 * } rowCnt += 1; rowCnt += 3;
			 */
			// ***************************
			/*
			 * 子系统分支路径管段数据矩阵 倒序 节点序号 1 2 3 4 5 6 7 1 6 5 4 3 2 1 0 2 7 -99 -99
			 * -99 -99 -99 -99 3 8 -99 -99 -99 -99 -99 -99
			 */
			Mbranch = new int[Nstart][Npline];
			// *****这一版本中去掉*********
			/**
			 * for (int j = 0; j < Nstart; j++) { for (int k = 0; k < Npline;
			 * k++) { Mbranch[j][k] = Integer.parseInt(rs.getCell(k + 1, rowCnt
			 * + j).getContents().trim()); } } rowCnt += Nstart; rowCnt += 3;
			 **/
			// **********************
			/*
			 * 排水量变化曲线 时段 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21
			 * 22 23 24 曲线 2.12 2.19 2.18 2.8 3.21 3.9 5.2 5.62 5.63 5.08 5.12
			 * 5.69 5.28 4.52 4.51 4.58 5.5 5.62 5.13 5.18 3.4 3.12 2.22 2.2
			 */
			Rf = new double[NT];
			for (int j = 0; j < NT; j++)
			{
				Rf[j] = Double.parseDouble(rs.getCell(j + 1, rowCnt).getContents().trim());
			}
			// ----中间指标变量----
			int i00, j00 = 0, Ni0, Ni1, jj, jp0, inp = 0, jpp, NPP;
			// ----中间变量----
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
			printStream.println("20161030-污水管网模拟-华家池-3.txt");
			// System.out.println("------ 污水管网模拟-华家池 ------");
			// ================= 赋初值 ===============================
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
			// ====20161106===== 生成矩阵 MNP[i][j] ====
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
			//System.out.println("===========  print MNP[i][j]");
			printStream.println("===========  print MNP[i][j]"); 
			for (i = 0; i < NN; i++)
			{
				for (j = 0; j < Ncol; j++)
				{
					printStream.printf("%6d", MNP[i][j]);
				}
				printStream.println();

			}
			// ----- MNP[i][j] 结束 ------
			// ====20161112===== 生成矩阵 Mstart[i] ====
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
			// ====20161106===== 生成矩阵Mbranch[i][j] ====
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
				// if (NPP < NP)  goto L200;
				if (NPP >= NP)
				{
					break;
				}
			}
			// === 生成矩阵 Mbranch[i][j] 结束====
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
			//System.out.println("pipe no.  I0    J0");
			for (i = 0; i < NP; i++)
			{
				//System.out.printf("%6d%6d%6d", i, I0[i], J0[i]);
				//System.out.println();
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
			// ================= 管网准稳态流动模拟============================
			// -------------------动态模拟流量计算-----------------------------
			// ----------------各管段总服务人口(人)和汇水流量(m3/sec)计算------
			printStream.println();
			printStream.println("======  污水管网动态模拟   人均日排水量＝ " + q1 + "  m3   时段数＝ " + NT + "       终点水位＝ " + Hw_end + "  m  =====");
			// xxxxxxx
			// 人均排水量变化曲线---discharge at every time step per head---
			for (it = 0; it < NT; it++)
			{
				qit[it] = q1 * Rf[it] / 100.0 / 3600;
			}
			printStream.println();
			printStream.println("    it     qit[it] （m3/cap-sec）");
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
			// -------------管段水力计算开始--------------
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
				// -------------------20090127-sql代码------------------------
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
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  淹没出流 ");
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
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdw= " + Hwdw[it][kp] + "  非淹没出流 ");
								}
								// --20161018修改开始---采用临界水深简化算法--------
								qkpmax = 2.699 * Math.pow(dpl[kp], 2.5);
								if (qpt[it][kp] > qkpmax)
								{
									if (Iprt == 1)
									{
										printStream.println("   it= " + it + "   kp= " + kp + "  qkpmax= " + qkpmax + "  非淹没满管出流 ");
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
										printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  非淹没非满管出流 ");
									}
									// ==20161115修改---采用均匀流正常水深简化公式开始--------
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
									// ==20161115修改---采用均匀流正常水深简化公式结束--------
									sita = 2.0 * Math.acos(1.0 - 2.0 * hdcc0[it][kp]);
									rid[it][kp] = 0.25 * dpl[kp] * (sita - Math.sin(sita)) / sita;
									vpt[it][kp] = Math.pow(rid[it][kp], 0.6667) * Math.pow(slop[kp], 0.5) / slp[kp];
								}
								// ---if(qpt[it][kp]>qkpmax)结束---
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
							// ------- 输出it计算结果 ----------
							if (Iprt == 1)
							{
								printStream.println("   it= " + it + "   kp= " + kp + "   I0[kp]= " + I0[kp] + "  Hwdm= " + Hwdw[it][kp] + "  Hwup= " + Hwup[it][kp] + "  Hj= " + Hj[I0[kp]] + "  hdcc0= " + hdcc0[it][kp] + "  qpt= " + qpt[it][kp] + "  qqkp= " + qqkp[it][kp] + "  vpt= " + vpt[it][kp]);
							}

						}// --4 if(kp>=0) end
					}// --3 ---jk end
				}// --2---ik end
				printStream.println();
				printStream.println("    it   管段号  I0   J0 管径dpl     管段qp 水力半径R  充满度 流速(m/s)  上游水位  下游水位  上管底高  下管底高  管段坡度  上地面高");
				for (i = 0; i < NP; i++)
				{
					printStream.printf("%6d%6d%6d%5d%8.2f%12.3f%10.3f%8.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.5f%10.3f", it, i, I0[i], J0[i], dpl[i], qpt[it][i], rid[it][i], hdcc0[it][i], vpt[it][i], Hwup[it][i], Hwdw[it][i], ZJup[i], ZJdw[i], slop[i], Hj[I0[i]]);
					printStream.println();
				}
				printStream.println();
				// -------- 计算节点水位-节点积水量和积水深度 ---------
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
				// ------------ 计算溢流节点结束 ----
			}// 1-- it end ---
			// --------------屏幕输出计算结束------
			//System.out.println("------ 模型计算全部完成 ------");
			// -------------- 输出管段充满度计算结果 ---------------
			printStream.println(" ======== 时段管段充满度 ========");
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
			// ---------- 输出节点水位计算结果 -------------
			printStream.println(" ======== 时段节点水位 ========");
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
			// ***********组织数据，传到页面用于显示********
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
			// ------------ 输出节点溢流计算结果 --------------
			printStream.println(" ======== 时段节点积水量(m3) ========");
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
			// ***********组织数据，传到页面用于显示********
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
			printStream.println(" ======== 时段节点积水深度(mm) ========");
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
	
	// 模拟排污第三套
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
			SubgxId = CommUtil.StrToInt(gxId.substring(5, 8)) - 1; //YJ001001
		}
		try
		{
			// CString s;
			// =================
			// 管网基础数据：
			// 管段数，节点数，管道起点数，路径最大管段数，中间矩阵MNP[NN][Ncol]列数，模拟时段数，降雨峰值时段,终点节点号，中间结果输出指针，输出数据表列数
			int NP = 9, NN = 10, Nstart = 3, Npline = 20, Ncol = 6, NT = 24, Nend = 7, Iprt = 0, Nprtc = 20;
			// 管网出口水位（m）,地面凹凸系数csf，路沿高度heage-mm, 时间步长（h）
			double Hw_end = 3.0, csf = 2.0, heage = 180, dt = 1.0;
			// 污水流量数据
			// 人均日排水量（m/d）double q1=0.45;
			// 排水量变化曲线（NT）
			
			// 节点地面面积(ha)， 节点地面标高（m），节点服务人口(人)
			double[] Aj;
			double[] Hj;
			double[] Rj;
			// 排水量变化曲线（NT）
			double[] Rf;
			// 管网路径数和路径节点号(－99表示空节点)
			int[][] Mroute;
			int[][] Mbranch;
			// 管段上游节点号I0,下游节点号J0，管段长度(m),摩阻系数
			int[] I0;
			int[] J0;
			double[] lp;
			double[] slp;
			// 节点起点号
			int[] Mstart;
			// 管段直径(m)，上游管底高程(m)，下游管底高程(m)
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
			 * 基础数据表格子系统号 节点数NN 管段数NP 起点数NStart 路径管段数Npline 路径节点数Nr_node
			 * 终点出口号Nend 模拟时段NT 管段路径数NrouteYJ002 10 9 3 7 8 8 60 3
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
			 * 子系统管段数据表格 Pipe.No 起点号I0 终点号J0 长度LP 直径DP 摩阻系数 起端标高 终端标高 1 0 1 28.5
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
				rowCnt ++;
			}
			
			rowCnt += 3;

			/*
			 * 子系统节点数据表格 节点No 地面面积Aj 地面标高 节点服务人口 1 0.2 5.244 80 2 0.2 5.191 80 3
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
				rowCnt ++;
			}

			rowCnt += 3;

			/**
			 * 管网路径起点号 序号 1 2 3 起点号 0 8 9
			 */
			// ************这一版本中去掉*******
			Mstart = new int[Nstart];
			// ***************************
			/*
			 * 子系统分支路径管段数据矩阵 倒序 节点序号 1 2 3 4 5 6 7 1 6 5 4 3 2 1 0 2 7 -99 -99
			 * -99 -99 -99 -99 3 8 -99 -99 -99 -99 -99 -99
			 */
			Mbranch = new int[Nstart][Npline];
			/*
			 * 排水量变化曲线 时段 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21
			 * 22 23 24 曲线 2.12 2.19 2.18 2.8 3.21 3.9 5.2 5.62 5.63 5.08 5.12
			 * 5.69 5.28 4.52 4.51 4.58 5.5 5.62 5.13 5.18 3.4 3.12 2.22 2.2
			 */
			Rf = new double[NT];
			for (int j = 0; j < NT; j++)
			{
				Rf[j] = Double.parseDouble(rs.getCell(j + 1, rowCnt).getContents().trim());
			}
			
			// 管网分叉支线管段矩阵-倒序排列
			int[] Npjun = new int[NP];
			int[][] MNP = new int[NN][Ncol];
			
			/*double[] Rf = new double[] { 2.12, 2.19, 2.18, 2.80, 3.21, 3.90, 5.20, 5.62, 5.63, 5.08, 5.12, 5.69, 5.28, 4.52, 4.51, 4.58, 5.50, 5.62, 5.13, 5.18, 3.40, 3.12, 2.22, 2.2 };
			// 节点地面面积(ha)
			double[] Aj = new double[] { 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2 };
			// 节点地面标高（m）
			double[] Hj = new double[] { 5.24, 5.19, 5.18, 5.00, 5.21, 5.20, 5.20, 5.12, 5.13, 5.18 };
			// 节点服务人口(人)
			double[] Rj = new double[] { 300, 100, 0, 0, 200, 0, 200, 0, 200, 200 };
			// 管网构造数据
			// 管段上游节点号I0,下游节点号J0，上游管底高程ZJup[NP](m)，下游管底高程ZJdw[NP](m)
			int[] I0 = new int[] { 0, 1, 2, 3, 4, 5, 6, 8, 9 };
			int[] J0 = new int[] { 1, 2, 3, 4, 5, 6, 7, 7, 6 };
			double[] ZJup = new double[] { 3.89, 3.84, 3.78, 3.73, 3.68, 3.64, 3.60, 3.73, 3.88 };
			double[] ZJdw = new double[] { 3.84, 3.78, 3.73, 3.68, 3.64, 3.60, 3.55, 3.60, 3.70 };
			// 管段长度(m),管段直径(m),摩阻系数
			double[] lp = new double[] { 50, 50, 50, 50, 50, 50, 50, 50, 50 };
			double[] dpl = new double[] { 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3 };
			double[] slp = new double[] { 0.014, 0.014, 0.014, 0.014, 0.014, 0.014, 0.014, 0.014, 0.014 };*/
			// ===================
			// ----中间指标变量---
			int i00, j00 = 0, Ni1, jj, jp0, inp = 0, jpp, NPP;
			// 管网起始节点号矩阵Mstart-节点关联矩阵MNP-中间变换矩阵 Npjun-管网分支线管段矩阵Mbranch(倒序排列)
			/*int[] Mstart = new int[Nstart];
			int[][] MNP = new int[NN][Ncol];
			int[] Npjun = new int[NP];
			int[][] Mbranch = new int[Nstart][Npline];*/
			// ----中间变量----
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
			
			SewageAcc	= new String[NT];
			SewageLev	= new String[NT];
			SewageFlowLoad	= "";
			SewageActualFlow	= "";
			SewageFlowRate	= "";
			
			SewageAccGj = "";
			// ---------------------------------------------
			// --输出数据文件开始---

			String FilePath = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogValue/";
			String FileName = subSys + ".txt";
			FileOutputStream fs = new FileOutputStream(new File(FilePath + FileName));
			PrintStream printStream = new PrintStream(fs);
			printStream.println("20161030-污水管网模拟-华家池-3.txt");
			//
			DecimalFormat df = new DecimalFormat("##.####");
			DecimalFormat df1 = new DecimalFormat("######.##");
			
			//System.out.println("请输入人均每日排水量（m3）:");
			//Scanner input = new Scanner(System.in);
			//q1 = input.nextDouble();
			q1 = p1;
			
			printStream.println("===人均日排水量＝ " + q1 + " （m3/d）    时段数＝ " + NT + "     终点水位＝ " + Hw_end + "  m  ===");
			// =====print pipe no. I0 lp J0 dpl slp ZJup ZJdw=====
			//System.out.println();
			//System.out.println("pipe no.  I0    J0      lp     dpl     slp    ZJup    ZJdw");
			for (i = 0; i < NP; i++)
			{
				//System.out.printf("%6d%6d%6d%8.2f%8.2f%8.3f%8.2f%8.2f", i, I0[i], J0[i], lp[i], dpl[i], slp[i], ZJup[i], ZJdw[i]);
				//System.out.println();
			}
			//System.out.println();
			//System.out.println("===人均日排水量＝ " + q1 + " （m3/d）   时段数＝ " + NT + "     终点水位＝ " + Hw_end + "  m  ===");
			printStream.println();
			printStream.println("pipe no.  I0    J0      lp     dpl     slp    ZJup    ZJdw");
			for (i = 0; i < NP; i++)
			{
				printStream.printf("%6d%6d%6d%8.2f%8.2f%8.3f%8.2f%8.2f\n", i, I0[i], J0[i], lp[i], dpl[i], slp[i], ZJup[i], ZJdw[i]);
				printStream.println();
			}
			printStream.println();
			// ================= 计算slop[k] ===========
			for (k = 0; k < NP; k++)
			{
				slop[k] = (ZJup[k] - ZJdw[k]) / lp[k];
			}
			// ====20161106===== 生成矩阵 MNP[i][j] ====
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
			// ----- MNP[i][j] 结束 ------
			// ====20161112===== 生成矩阵 Mstart[i] ====
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
			// ====20161106===== 生成矩阵Mbranch[i][j] ====
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
			// === 生成矩阵 Mbranch[i][j] 结束====
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
			// ================= 管网准稳态水力模拟============================
			//
			// -------------------动态模拟流量计算-----------------------------
			// ----------------各管段总服务人口(人)和汇水流量(m3/sec)计算------
			printStream.println();
			printStream.println("======  污水管网动态模拟   人均日排水量＝ " + q1 + "  m3   时段数＝ " + NT + "       终点水位＝ " + Hw_end + "  m  =====");
			// xxxxxxx
			// 人均排水量变化曲线---discharge at every time step per head---
			//
			for (it = 0; it < NT; it++)
			{
				qit[it] = q1 * Rf[it] / 100.0 / 3600;
			}
			printStream.println();
			printStream.println("    it     qit[it] （m3/cap-sec）");
			for (it = 0; it < NT; it++)
			{
				printStream.printf("%6d%12.6f", it, qit[it]);
				printStream.println();
			}
			printStream.println();
			// xxxxxxx
			// -------------管段水力计算开始--------------
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
				// ----------计算管段流量------------
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
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdm= " + Hwdw[it][kp] + "  淹没出流 ");
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
									printStream.println("   it= " + it + "   kp= " + kp + "  Hwdw= " + Hwdw[it][kp] + "  非淹没出流 ");
								}
								// --20161018---计算临界水深------------
								qkpmax = 2.699 * Math.pow(dpl[kp], 2.5);
								if (qpt[it][kp] > qkpmax)
								{
									if (Iprt == 1)
									{
										printStream.println("   it= " + it + "   kp= " + kp + "  qkpmax= " + qkpmax + "  非淹没满管出流 ");
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
										printStream.println("   it= " + it + "   kp= " + kp + "  Hwdw= " + Hwdw[it][kp] + "  非淹没非满管出流 ");
									}
									// ==20161115---计算水深开始--------
									if (slop[kp] > 0)
									{// ----正常水深----
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
									{// ----临界水深----
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
									// ==20161115---计算水深结束-------
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
							// ------- 输出it计算结果 ----------
							if (Iprt == 1)
							{printStream.println("   it= " + it + "   kp= " + kp + "   I0[kp]= " + I0[kp] + "  Hwdm= " + Hwdw[it][kp] + "  Hwup= " + Hwup[it][kp] + "  Hj= " + Hj[I0[kp]] + "  hdcc0= " + hdcc0[it][kp] + "  qpt= " + qpt[it][kp] + "  qqkp= " + qqkp[it][kp] + "  vpt= " + vpt[it][kp]);
							}
						}// --4 if(kp>=0) end
					}// --3 ---jk end
				}// --2---ik end
					// -------------- 计算节点水位-节点积水量和积水深度 ---------------
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
				printStream.println("    it   管段号  I0   J0 管径dpl    管段qpt 水力半径R    充满度 流速(m/s)  上游水位  下游水位  上管底高  下管底高  管段坡度  上地面高  水力坡度    qqkp");
				for (i = 0; i < NP; i++)
				{
					printStream.printf("%6d%6d%6d%5d%8.2f%12.4f%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f%10.4f%10.4f", it, i, I0[i], J0[i], dpl[i], qpt[it][i], rid[it][i], hdcc0[it][i], vpt[it][i], Hwup[it][i], Hwdw[it][i], ZJup[i], ZJdw[i], slop[i], Hj[I0[i]], slopt[it][i], qqkp[it][i]);
					printStream.println();
				}
				printStream.println();
				// ------------ 计算溢流节点结束 ----
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
				// --------------屏幕输出计算结束------
				// ----------------- 输出管段充满度计算结果 ---------------
			printStream.println(" ======== 时段管段充满度 ========");
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
			// ------------------ 输出节点水位计算结果 ---------------
			printStream.println(" ======== 时段节点水位 ========");
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
			
			// ***********组织数据，传到页面用于显示********
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
			// ---------------------- 输出节点溢流计算结果 ---------------
			printStream.println(" ======== 时段节点积水量(m3) ========");
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
			
			// ***********组织数据，传到页面用于显示********
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
			printStream.println(" ======== 时段节点积水深度(mm) ========");
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
			// ***********组织数据，传到页面用于显示*****20170120***
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
			
			// -----模型计算完成-----
			//System.out.println("------ 模型计算完成 ------");
			printStream.println("------ 模型计算完成 ------");

		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			return gjName + "," + "NumberFormat" + "," + (rowCnt + 1);
		}
		catch(ArrayIndexOutOfBoundsException e)
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
		int sn = 0;
		String water = "0";
		String Base_Height = "0";
		
	}
	/**
	 * 自动填充出设备井以外井的水位数据
	 * @param gjObj
	 * @param gxObj
	 * @param Id
	 * @return
	 */
	public ArrayList AnalogGJList(ArrayList gjObj, ArrayList gxObj, String Id)
	{
		/***2017.2.7  设置超过两小时没采集的设备的直为0 cj ***/
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newTime;
        Date equip_time;
        long between = 0;
        long hour = 0;
        /*******/
		//gjObj ArrayList转Hash
		Hashtable<String, DevGJBean> objGJTable = null;
		objGJTable = new Hashtable<String, DevGJBean>(); 
		Iterator iterGJ = gjObj.iterator();
		while(iterGJ.hasNext())
		{
			DevGJBean gjBean = (DevGJBean)iterGJ.next();
			String gjId = gjBean.getId();
			
			/***2017.2.7  设置超过两小时没采集的设备的值为0 cj **
			if(gjBean.getEquip_Time().length() > 1)
			{
				try
				{
					newTime = df.parse(df.format(new Date()));
					equip_time = df.parse(gjBean.getEquip_Time());
					between = (newTime.getTime() - equip_time.getTime())/1000;//除以1000是为了转换成秒 
			        hour = between/3600;
				}
				catch (ParseException e)
				{
					e.printStackTrace();
				}
				if(hour > 2)
				{					
					gjBean.setCurr_Data("0.00");
				}
			}
			/*******/
			HashPut(objGJTable, gjId, gjBean);
		}
		//gxObj ArrayList转Hash
		Hashtable<String, DevGXBean> objGXTable = null;
		objGXTable = new Hashtable<String, DevGXBean>(); 
		Iterator iterGX = gxObj.iterator();
		while(iterGX.hasNext())
		{
			DevGXBean gxBean = (DevGXBean)iterGX.next();
			String gxId = gxBean.getId();
			HashPut(objGXTable, gxId, gxBean);
		}
		
		//遍历，梳理先后关系,转成ArrayList
		DevGJBean nextGJ = (DevGJBean)HashGet(objGJTable, Id);			
		ArrayList<Object> gjList = new ArrayList<Object>();		//管井ArrayList
		gjList.add(nextGJ);										//加入第一个管井
		ArrayList<Object> devList = new ArrayList<Object>();	//设备管井ArrayList
		DevGXBean nextGX = new DevGXBean();

		int sn = 0;	
		int option = 0;
		do
		{
			if(!nextGJ.getCurr_Data().equals("0.00"))
			{
				DevGJData devGJ = new DevGJData();
				devGJ.sn = sn;
				devGJ.water = nextGJ.getCurr_Data();
				devGJ.Base_Height = nextGJ.getBase_Height();
				devList.add(devGJ);
			}
			
			String outGXId = nextGJ.getOut_Id();
			nextGX = (DevGXBean)HashGet(objGXTable, outGXId);
			String outGJId = nextGX.getEnd_Id();
			nextGJ = (DevGJBean)HashGet(objGJTable, outGJId);
			sn ++;
			gjList.add(nextGJ);
			if(nextGJ.getFlag().equals("2"))
			{
				option = 1;
			}
		}
		while(option == 0);
		
		//如果没有设备井，返回由选择管井到终点的管井列表
		if(0 == devList.size())
		{
			return gjList;
		}
		//如果有设备井，更新管井列表中无设备管井的水位
		int count = 0;		
		DevGJData devGJData1 = ((DevGJData)devList.get(0));						//取到第一个有设备的管井
		DevGJData devGJDataN = ((DevGJData)devList.get(devList.size() - 1));	//取到最后一个有设备的管井
		@SuppressWarnings("rawtypes")
		Iterator it = gjList.iterator();
		while(it.hasNext())
		{
			DevGJBean gjBean = (DevGJBean)it.next();
			int flag = 0;
			for(int i = 0; i < devList.size(); i ++)
			{
				if(count == ((DevGJData)devList.get(i)).sn)			
				{
					flag = 1;
					break;
				}
			}
			if(1 == flag)					//设备管井本身 下一个 
			{
				count ++;
				continue;
			}
			if(count < devGJData1.sn)			//第一个设备之前的管井
			{
				float GJBaseHeight = CommUtil.StrToFloat(gjBean.getBase_Height());		//当前管井的底高
				float DevGJWater = CommUtil.StrToFloat(devGJData1.water);				//第一个设备的水位
				float DevGJBaseHeight = CommUtil.StrToFloat(devGJData1.Base_Height);	//第一个设备的底高
				if(GJBaseHeight < DevGJWater + DevGJBaseHeight)							//若当前设备的底高 < 设备的水位+底高
				{
					gjBean.setCurr_Data(String.valueOf(DevGJWater + DevGJBaseHeight - GJBaseHeight));//赋予当前管井的水位高度
				}
			}
			else if(count > devGJDataN.sn)		//最后一个设备之后的管井
			{
				float GJBaseHeight = CommUtil.StrToFloat(gjBean.getBase_Height());
				float DevGJWater = CommUtil.StrToFloat(devGJDataN.water);
				float DevGJBaseHeight = CommUtil.StrToFloat(devGJDataN.Base_Height);
				if(GJBaseHeight < DevGJWater + DevGJBaseHeight)
				{
					gjBean.setCurr_Data(String.valueOf(DevGJWater + DevGJBaseHeight - GJBaseHeight));
				}
			}
			else							//第一个设备和最后一个设备之间的管井
			{
				
				for(int i = 1; i < devList.size(); i ++)
				{
					DevGJData devGJDataI_1 = (DevGJData)devList.get(i-1);
					DevGJData devGJDataI = (DevGJData)devList.get(i);
					
//					System.out.println("i-1sn[" + devGJDataI_1.sn + "] count[" + count + "] isn[" + devGJDataI.sn + "]");
					
					if(devGJDataI_1.sn < count && devGJDataI.sn > count)
					{
						//之后的 + （之后的-之前的）/（中间差数）
						float i_1Lev = CommUtil.StrToFloat(devGJDataI_1.Base_Height) + CommUtil.StrToFloat(devGJDataI_1.water);
						float iLev   = CommUtil.StrToFloat(devGJDataI.Base_Height) + CommUtil.StrToFloat(devGJDataI.water);
						float waterLev = i_1Lev + (iLev - i_1Lev) * (count - devGJDataI_1.sn) / (devGJDataI.sn - devGJDataI_1.sn);
						float currData = waterLev - CommUtil.StrToFloat(gjBean.getBase_Height());
//						System.out.println("Id[" + gjBean.getId() + "] i-1Lev[" + i_1Lev + "] iLev[" + iLev + "] waterLev[" + waterLev
//								+ "] currData[" +  currData  + "]");
						gjBean.setCurr_Data(String.valueOf(currData));
						break;
					}
				}
			}
			count ++;
		}
		return gjList;
	}
		
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void HashPut(Hashtable hashTable, String key, Object obj)
	{
		if(hashTable.containsKey(key))
		{
			hashTable.remove(key);		//在哈希表里移除客户端
		}		
		hashTable.put(key , obj);
	}
	@SuppressWarnings({ "rawtypes", "unused" })
	public Object HashGet(Hashtable hashTable, String key)
	{
		if(!hashTable.isEmpty() && hashTable.containsKey(key))
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

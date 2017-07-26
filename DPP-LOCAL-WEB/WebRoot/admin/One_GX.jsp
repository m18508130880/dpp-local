<%@ page contentType="text/html; charset=gb2312" %>
<%@ page import="java.util.*" %>
<%@ page import="bean.*" %>
<%@ page import="util.*" %>
<%@ page import="java.lang.*" %>
<%@ page import="java.math.*" %>
<%@ page import="java.text.*" %>
<%@ taglib uri="/WEB-INF/limitvalidatetag.tld" prefix="Limit"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>管线查询</title>
<meta http-equiv="x-ua-compatible" content="ie=7"/>
<link type="text/css" href="../skin/css/style.css" rel="stylesheet"/>
<script type='text/javascript' src='../skin/js/zDrag.js'   charset='gb2312'></script>
<script type='text/javascript' src='../skin/js/zDialog.js' charset='gb2312'></script>
</head>
<%
	String Sid = CommUtil.StrToGB2312(request.getParameter("Sid"));			
  CurrStatus currStatus    = (CurrStatus)session.getAttribute("CurrStatus_" + Sid);
  ArrayList  One_GX        = (ArrayList)session.getAttribute("One_GX_" + Sid); 
  
%>
<body style=" background:#CADFFF">
<form name="Dev_GX"  action="doGx_Select.do" method="post" target="mFrame">
<div>
				<table width="100%" border=1 cellPadding=0 cellSpacing=0 bordercolor="#3491D6" borderColorDark="#ffffff">
					
		    <%
					if(null != One_GX)
					{
						Iterator deviter = One_GX.iterator();
						while(deviter.hasNext())
						{
							DevGXBean dBean = (DevGXBean)deviter.next();
				%>   		    	
					<tr  height='20'>
							<td width="35px" align='center' >编码</td>
							<td><%= dBean.getId()%>&nbsp; </td>
					</tr>
					<tr height='20'>
							<td width="35px" align='center' >直径</td>
							<td><%= dBean.getDiameter()%>&nbsp;</td>
					</tr>
					<tr height='20'>
							<td width="35px" align='center' >长度</td>
							<td><%= dBean.getLength()%> &nbsp; </td>
					</tr>					
					<tr height='20'>
							<td width="35px" align='center' >入端</td>
							<td><%= dBean.getStart_Id()%> &nbsp; </td>
					</tr>
					<tr height='20'>
							<td width="35px"  align='center'>出端</td>
							<td><%= dBean.getEnd_Id()%> &nbsp; </td>
					</tr>
					<tr height='20'>
							<td  width="35px" align='center'>材料</td>
							<td><%= dBean.getMaterial()%> &nbsp; </td>
					</tr>
															
				<%
						}
					}
		    %>
				
			</table>		
</div>
</form>
</body>
<SCRIPT LANGUAGE=javascript>


</SCRIPT>
</html>
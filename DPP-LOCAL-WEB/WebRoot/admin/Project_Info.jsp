<%@ page contentType="text/html; charset=gb2312"%>
<%@ page import="java.util.*"%>
<%@ page import="bean.*"%>
<%@ page import="util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>项目信息</title>
<meta http-equiv="x-ua-compatible" content="ie=7"/>
<link type="text/css" href="../skin/css/style.css" rel="stylesheet"/>
<script language=javascript>document.oncontextmenu=function(){window.event.returnValue=false;};</script>
</head>
<%
	
	String       Sid         = CommUtil.StrToGB2312(request.getParameter("Sid"));
	CurrStatus   currStatus  = (CurrStatus)session.getAttribute("CurrStatus_" + Sid);
	ArrayList    Project_Info   = (ArrayList)session.getAttribute("Project_Info_" + Sid);
	 
  int sn = 0;
  
%>
<body style="background:#CADFFF">
<form name="Project_Info"  action="Admin_Project_Info.do" method="post" target="mFrame">
<div id="down_bg_2">
	<div id="cap"><img src="../skin/images/project_info.gif"></div><br><br><br>
	<div id="right_table_center">
		<table width="80%" style='margin:auto;' border=0 cellPadding=0 cellSpacing=0 bordercolor="#3491D6" borderColorDark="#ffffff">
			
			<tr height='30'>
				<td colspan="2" width='70%' align='right'>
					<img src="../skin/images/mini_button_add.gif" style='cursor:hand;' onClick='doAdd()'>
				</td>
			</tr>
			
			
			<tr height='30'>
				<td width='100%' align='center' colspan=2>
					<table width="100%" border=1 cellPadding=0 cellSpacing=0 bordercolor="#3491D6" borderColorDark="#ffffff">
						<tr>
							<td width="5%"  class="table_deep_blue">序号</td>
							<td width="10%" class="table_deep_blue">项目ID</td>
							<td width="10%" class="table_deep_blue">项目名称</td>
							<td width="10%" class="table_deep_blue">经度</td>
							<td width="10%" class="table_deep_blue">纬度</td>
							<td width="10%" class="table_deep_blue">地图倍数</td>
							<td width="10%" class="table_deep_blue">展示角度</td>
							<td width="10%" class="table_deep_blue">项目描述</td>
						</tr>
					<%
						if(Project_Info != null)
						{
							Iterator iterator = Project_Info.iterator();
							while(iterator.hasNext())
							{
								ProjectInfoBean statBean = (ProjectInfoBean)iterator.next();
								String Id = statBean.getId();
								String CName = statBean.getCName();							
								String Longitude = statBean.getLongitude();	
								String Latitude	= statBean.getLatitude();	
								String MapLev	= statBean.getMapLev();	
								String MapAngle = statBean.getMapAngle();
							  String Demo = statBean.getDemo();				  						    						
								if(!"1000".equals(statBean.getId())){		    
								sn ++;
						%>
					
					  <tr <%=((sn%2)==0?"class='table_blue'":"class='table_white_l'")%>>
							<td  align=center style="cursor:hand " onmouseout="this.style.color='#000000';" onmouseover="this.style.color='#FF0000';"  title="点击查看" onClick="doEdit('<%=Id%>')"><U><%=sn%></U></td>
					    <td  align=center><%=Id%></td>
					    <td  align=center><%=CName%></td>
					    <td  align=center><%=Longitude%></td>
					    <td  align=center><%=Latitude%></td>
					    <td  align=center><%=MapLev%></td>
					    <td  align=center><%=MapAngle%></td>
					    <td  align=center><%=Demo%></td>      
						</tr>
						<% }
							}
						}
						%>
					</table>
				</td>
			</tr>
		</table>
	</div>
</div>
<input name="Cmd" type="hidden" value="0">
<input name="Sid" type="hidden" value="<%=Sid%>">
</form>
</body>
<SCRIPT LANGUAGE=javascript>
if(<%=currStatus.getResult().length()%> > 0)
   alert("<%=currStatus.getResult()%>");
<%
currStatus.setResult("");
session.setAttribute("CurrStatus_" + Sid, currStatus);
%>

function doSelect()
{
	Project_Info.submit();
}

function doAdd()
{
	location = "Project_Info_Add.jsp?Sid=<%=Sid%>";
}

function doEdit(pId)
{  
	location = "Project_Info_Edit.jsp?Sid=<%=Sid%>&Id="+pId;
}
</SCRIPT>
</html>
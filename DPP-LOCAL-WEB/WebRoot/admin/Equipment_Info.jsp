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
	
	String       Sid              = CommUtil.StrToGB2312(request.getParameter("Sid"));
	CurrStatus   currStatus       = (CurrStatus)session.getAttribute("CurrStatus_" + Sid);
	ArrayList    Equipment_Info   = (ArrayList)session.getAttribute("Equipment_Info_" + Sid);
	 
  int sn = 0;
  
%>
<body style="background:#CADFFF">
<form name="Equipment_Info"  action="Admin_Equipment_Info.do" method="post" target="mFrame">
<div id="down_bg_2">
	<div id="cap"><img src="../skin/images/equip_info.gif"></div><br><br><br>
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
							<td width="7%"  class="table_deep_blue">设备ID</td>
							<td width="10%" class="table_deep_blue">设备名称</td>
							<td width="10%" class="table_deep_blue">项目名称</td>
							<td width="10%" class="table_deep_blue">管井编码</td>
							<td width="10%" class="table_deep_blue">设备简介</td>
						</tr>
					<%
						if(Equipment_Info != null)
															{
																Iterator iterator = Equipment_Info.iterator();
																while(iterator.hasNext())
																{
																	EquipInfoBean statBean = (EquipInfoBean)iterator.next();
																	String Id = statBean.getId();
																	String CName = statBean.getCName();							
																	String Project_Name = "无";	
																	String GJ_Id = "无";
																	String Demo	= statBean.getDemo();
															    if(statBean.getProject_Name()!=null){
															       Project_Name = statBean.getProject_Name();
															    }
															    if(statBean.getGJ_Id()!=null){
															       GJ_Id = statBean.getGJ_Id();
															    }	
																	if(Demo == null){
																	   Demo = "";
																	} 
																	sn ++;
					%>
					
					  <tr <%=((sn%2)==0?"class='table_blue'":"class='table_white_l'")%>>
							<td  align=center style="cursor:hand " onmouseout="this.style.color='#000000';" onmouseover="this.style.color='#FF0000';"  title="点击查看" onClick="doEdit('<%=Id%>','<%=statBean.getProject_Id()%>','<%=GJ_Id%>')"><U><%=sn%></U></td>
					    <td  align=center><%=Id%></td>
					    <td  align=center><%=CName%></td>
					    <td  align=center><%=Project_Name%></td>
					    <td  align=center><%=GJ_Id%></td>
					    <td  align=center><%=Demo%></td>   
						</tr>
						<%
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


function doAdd()
{
	location = "Equipment_Info_Add.jsp?Sid=<%=Sid%>";
}

function doEdit(pId, pProject, pGJ_Id)
{  
	location = "Equipment_Info_Edit.jsp?Sid=<%=Sid%>&Id="+pId+"&Project="+pProject+"&GJ_Id="+pGJ_Id;
}
</SCRIPT>
</html>
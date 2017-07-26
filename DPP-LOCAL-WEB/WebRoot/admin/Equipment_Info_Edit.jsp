<%@ page contentType="text/html; charset=gb2312"%>
<%@ page import="java.util.*"%>
<%@ page import="bean.*"%>
<%@ page import="util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>编辑项目信息</title>
<meta http-equiv="x-ua-compatible" content="ie=7"/>
<link type="text/css" href="../skin/css/style.css" rel="stylesheet"/>
<script type="text/javascript" src="../skin/js/util.js"></script>
<script type="text/javascript" src="../skin/js/My97DatePicker/WdatePicker.js"></script>
<script language=javascript>document.oncontextmenu=function(){window.event.returnValue=false;};</script>
</head>
<%
	String     Sid            = CommUtil.StrToGB2312(request.getParameter("Sid"));
	CurrStatus currStatus     = (CurrStatus)session.getAttribute("CurrStatus_" + Sid);
  ArrayList  Equipment_Info = (ArrayList)session.getAttribute("Equipment_Info_" + Sid);
  ArrayList  Project_Info   = (ArrayList)session.getAttribute("Project_Info_" + Sid);
  ArrayList  Map_GJ         = (ArrayList)session.getAttribute("Map_GJ_" + Sid); 

 	String Id = request.getParameter("Id"); //设备id
 	String Project = request.getParameter("Project"); //项目id
 	String GJ_Id = request.getParameter("GJ_Id");     //项目id
  String CName = "";
  String Project_Id = "";
	String Curr_Data = "";
	String Demo = "";


	if(Equipment_Info != null)
	{
		Iterator iterator = Equipment_Info.iterator();
		while(iterator.hasNext())
		{
	EquipInfoBean statBean = (EquipInfoBean)iterator.next();
	if(statBean.getId().equals(Id))
	{
	CName = statBean.getCName();
	Demo = statBean.getDemo();				
	}
		}
 	}
%>
<body style="background:#CADFFF" onload="doProSelect()">
<form name="Equipment_Info_Edit"  action="Admin_Equipment_Info.do" method="post" target="mFrame" enctype="multipart/form-data">
<div id="down_bg_2">
	<div id="cap"><img src="../skin/images/cap_user_info.gif"></div><br><br><br>
	<div id="right_table_center">
		<table width="60%" style='margin:auto;' border=0 cellPadding=0 cellSpacing=0 bordercolor="#3491D6" borderColorDark="#ffffff">
			<tr height='30'>
				<td width='100%' align='right'>
				  <img src="../skin/images/mini_button_submit.gif"    style='cursor:hand;' onClick='doEdit()'>
					<img src="../skin/images/button10.gif"              style='cursor:hand;' onclick='doNO()'>
				</td>
			</tr>
			<tr height='30'>
				<td width='100%' align='center'>
					<table width="100%" border=1 cellPadding=0 cellSpacing=0 bordercolor="#3491D6" borderColorDark="#ffffff">
						<tr height='30'>
							<td width='20%' align='center'>设备ID号</td>
							<td width='30%' align='left'>
								<%=Id%>
							</td>
							<td width='20%' align='center'>设备名称</td>
							<td width='30%' align='left'>
								<input type='text' name='CName' style='width:96%;height:20px;' value='<%=CName%>' maxlength='6'>
							</td>
						</tr>

						<tr height='30'>
							<td width='20%' align='center'>所属项目</td>
							<td width='30%' align='left'>
								<select id="Project_Select" style="width:97%;height:20px" onChange="doProSelect()"  > 									  
									<option value="8888" <%=Project_Id.equals("8888")?"selected":""%> >请选择项目</option>
								<%	
 	                if(Project_Info != null){

		  								Iterator iterator = Project_Info.iterator();
											while(iterator.hasNext()){
											ProjectInfoBean statBean = (ProjectInfoBean)iterator.next();
											String Pro_Id = statBean.getId();
											String Pro_Name = statBean.getCName();				
							%>
								    <option value="<%=Pro_Id%>" <%=Project.equals(Pro_Id)?"selected":""%> > <%=Pro_Name%></option>
							<%
		    						 }
									}
							%>
								</select>
							</td>
							<td width='20%' align='center'>管井编码</td>
							<td width='30%' align='left'>
								<select id="GJ_Select" style="width:97%;height:20px" >
									 						
								</select>
							</td>
						</tr>	
						<tr height='30'>
							</td>
							<td width='20%' align='center'>设备简介</td>
							<td width='30%' align='left'  >
								<input type='text' name='Demo' style='width:96%;height:20px;' value='<%=Demo%>' maxlength='11'>
							</td>
						</tr>		
							
					</table>
				</td>
			</tr>
		</table>
	</div>
</div>
<input name="Id" type="hidden" value="<%=Id%>">
<input name="Sid" type="hidden" value="<%=Sid%>">

</form>
</body>


<SCRIPT LANGUAGE=javascript>

if(<%=currStatus.getResult().length()%> > 0)
   alert("<%=currStatus.getReif(null != Map_GJ)
		{
			Iterator deviter = Map_GJ.iterator();
			while(deviter.hasNext())
			{
				 DevGJBean devBean  = (DevGJBean)deviter.next();
				 Project_Id  = devBean.getProject_Id();
				 Id          = devBean.getId(); 	         + Equipment_Info_Edit.CName.value
  	         + "&GJ_Id=" + Equipment_Info_Edit.GJ_Select.value
  	         + "&Demo=" + Equipment_Info_Edit.Demo.value;
  }
}

function doNO()
{
	location = "Equipment_Info.jsp?Sid=<%=Sid%>";
}

function doProSelect()
{
	
	//先删除
	var pid = document.getElementById('Project_Select').value;
	var length = document.getElementById('GJ_Select').length;
	for(var i=0; i<length; i++)
	{
		document.getElementById('GJ_Select').remove(0);
	}
	//再添加
		<%
		if(null != Map_GJ)
		{
			Iterator deviter = Map_GJ.iterator();
			while(deviter.hasNext())
			{
				 DevGJBean devBean  = (DevGJBean)deviter.next();
				 Project_Id  = devBean.getProject_Id();
				 Id          = devBean.getId();
		%>
				if('<%=Project_Id%>' == pid)
				{
					var objOption = document.createElement('OPTION');
					objOption.value = '<%=Id%>';
					objOption.text  = '<%=Id%>';
					
					if('<%=Id%>' == '<%=GJ_Id%>')
					{
					   objOption.selected = 'selected';
				  }
					document.getElementById('GJ_Select').add(objOption);
				}
		<%
			}
		}
		%>
}


</SCRIPT>
</html>
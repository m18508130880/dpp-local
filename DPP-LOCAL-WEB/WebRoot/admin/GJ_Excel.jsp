<%@ page contentType="text/html; charset=gb2312"%>
<%@ page import="java.util.*"%>
<%@ page import="bean.*"%>
<%@ page import="util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>中海油LNG加气站公司级信息化管理平台</title>
<link type="text/css" href="../skin/css/style.css" rel="stylesheet"/>
<script type="text/javascript" src="../skin/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="../skin/js/util.js"></script>
</head>
<%
	
	String Sid = CommUtil.StrToGB2312(request.getParameter("Sid"));
  CurrStatus currStatus = (CurrStatus)session.getAttribute("CurrStatus_" + Sid);
  ArrayList    Project_Info   = (ArrayList)session.getAttribute("Project_Info_" + Sid);
	
%>
<body  style=" background:#CADFFF">
<form name="Admin_File_GJ" action="Admin_File_GJ.do" method="post" target="mFrame" enctype="multipart/form-data">
<div id="cap"><img src="../skin/images/gx_record.gif"></div><br><br><br><br>
  <div id="right_table_center">
	<table width="100%" style='margin:auto;' border=0 cellPadding=0 cellSpacing=0 bordercolor="#3491D6" borderColorDark="#ffffff">
		<tr height='100%' valign='middle'>
			   <h3>选择项目名称</h3>
				 <td width="30%" align='center'>
						<select name="Project_Id" style="width:30%;height:20px"> 
								<%
 	                if(Project_Info != null){
		  								Iterator iterator = Project_Info.iterator();
											while(iterator.hasNext()){
											ProjectInfoBean statBean = (ProjectInfoBean)iterator.next();
											String Project_Id = statBean.getId();
											String Project_Name = statBean.getCName();						
								%>
								    <option value="<%=Project_Id%>" > <%=Project_Name%></option>
								<% 
		    						 }
									}
								%>
						</select>
			   </td>
		  </tr>		
		  <tr height='25'>			
				<td align='center'>
					<input name='file' type='file' style='width:30%;height:20px;' title='数据导入'>
				</td>
		  </tr>	
		  <tr height='25'>
		     <td width='100%' align='center' >
			   		<img src="../skin/images/mini_button_submit.gif" style='cursor:hand;' onClick='doEdit()'>
		     </td>
	    </tr>
	</table> 
  </div>
	<input type="hidden" name="Sid" value="<%=Sid%>">
</form>
</body>
<script LANGUAGE="javascript">
	
function doEdit()
{	
	if(Admin_File_GJ.file.value.Trim().length > 0)
  {
  	if(Admin_File_GJ.file.value.indexOf('.xls') == -1 
  	&& Admin_File_GJ.file.value.indexOf('.XLS') == -1 
  	&& Admin_File_GJ.file.value.indexOf('.xlsx') == -1 
  	&& Admin_File_GJ.file.value.indexOf('.XLSX') == -1 )	
		{
			alert('请确认文档格式,支持xls,xlsx格式!');
			return;
		}
  }								
	if(confirm('信息无误,确定提交?'))
  {
		Admin_File_GJ.submit();
  }	
}

</script>
</html>
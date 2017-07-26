<%@ page contentType="text/html; charset=gb2312" %>
<%@ page import="java.util.*" %>
<%@ page import="bean.*" %>
<%@ page import="util.*" %>
<%@ page import="java.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>百事达排水管网信息管理平台</title>
<meta http-equiv="x-ua-compatible" content="ie=7"/>
<link type="text/css" href="../skin/css/style.css" rel="stylesheet"/>
<script language=javascript>document.oncontextmenu=function(){window.event.returnValue=false;};</script>
<%
	
	String Sid = CommUtil.StrToGB2312(request.getParameter("Sid"));
	
%>
</head>
<body>
	<div class="top">
		<div class="logo"><img src="../skin/images/logo_sub_system_manage.gif" /></div>
		<div class="home"></div>
		<div class="exit" style="cursor:hand"><img onClick="doExit()" src="../skin/images/exit.gif" /></div>
		<div class="bar">
			<div class="ren"><img onClick="doPwd()" width="18" height="15" style="cursor:hand" src="../skin/images/top_user_pwd.gif" alt="密码修改"/> <img src="../skin/images/ren.gif" width="13" height="15"/></div>
			<div class="js">	</div>
			<div class="line"><img src="../skin/images/bar_line.gif" /> </div>
			<div class="js2" id="time"></div>
			<div id="banner_r" class="js">								
			</div>
		</div>
	</div>
</body>
<SCRIPT LANGUAGE=javascript>
//setInterval("time.innerHTML= new Date().toLocaleString()+' 星期'+'日一二三四五六'.charAt(new Date().getDay());",1000);
setInterval("time.innerHTML= new Date().toLocaleString().substring(0, new Date().toLocaleString().indexOf('日')+1)",1000);

function doExit()
{
    if(confirm("确定要安全退出本系统?"))
    {
      alert("谢谢您的使用!");
			location = "AdminILogout.do?Sid=<%=Sid%>";
		}
}

function doPwd()
{
	window.parent.frames.mFrame.location = "Pwd_Edit.jsp?Sid=<%=Sid%>";
}
</SCRIPT>
</html>
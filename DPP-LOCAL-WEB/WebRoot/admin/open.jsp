<%@ page contentType="text/html; charset=gb2312" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="bean.*" %>
<%@ page import="util.*" %>
<html style="margin:0;padding:0">
<head>
<title>中海油LNG加气站公司级信息化管理平台</title>
<script language=javascript>document.oncontextmenu=function(){window.event.returnValue=false;};</script>
<script language="javascript" type="text/javascript">
var f=false;
function change()
{
		if(f==false)
		{
				top.document.getElementById('glb_frm').attributes["cols"].value="0,11,*";
				document.getElementById('tg').src="../skin/images/bar_right.gif";//你的左三角
				f=true;
		}
		else
		{
				top.document.getElementById('glb_frm').attributes["cols"].value="184,11,*";
				document.getElementById('tg').src="../skin/images/bar_left.gif";//你的右三角
				f=false;
		}
}
</script>
</head>
<body style="background-color:#0B80CC;margin:0;padding:0">
	<img id="tg" style="cursor:hand;" src="../skin/images/bar_left.gif" id="tog" style="width:11px;height;500px;top:0px;position:absolute" onclick="change()"/>
</body>
</html>

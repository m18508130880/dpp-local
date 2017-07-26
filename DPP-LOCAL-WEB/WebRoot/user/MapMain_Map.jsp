<%@ page contentType="text/html; charset=gb2312"%>
<%@ page import="java.util.*"%>
<%@ page import="bean.*"%>
<%@ page import="util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>管井查询</title>
<link type="text/css" href="../skin/css/style.css" rel="stylesheet"/>
<script type='text/javascript' src='../skin/js/util.js' charset='gb2312'></script>
<script type='text/javascript' src='../skin/js/browser.js' charset='gb2312'></script>
<script src="http://api.map.baidu.com/api?v=1.2&services=true" type="text/javascript"></script>
<!--Zdialog-->
<script type='text/javascript' src='../skin/js/zDrag.js'   charset='gb2312'></script>
<script type='text/javascript' src='../skin/js/zDialog.js' charset='gb2312'></script>

<!--EasyUI-->
<link rel="stylesheet" type="text/css" href="../easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="../easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="../easyui/demo/demo.css">
<script type="text/javascript" src="../easyui/jquery.min.js"></script>
<script type="text/javascript" src="../easyui/jquery.easyui.min.js"></script>
<!--BanRightClick-->
<script language=javascript>document.oncontextmenu=function(){window.event.returnValue=false;};</script>

<style>
	html{height:100%}
	body{height:100%; margin:0px; padding:0px}
	#container{height:100%}
  html,body{width:100%; height:100%; margin:0; padding:0;}/*必须将最外层设置一个高度*/
  .mesWindow{border:#C7C5C6 1px solid;background:#CADFFF;}
  .mesWindowTop{background:#3ea3f9;padding:5px;margin:0;font-weight:bold;text-align:left;font-size:12px; clear:both; line-height:1.5em; position:relative; clear:both;}
  .mesWindowTop span{ position:absolute; right:5px; top:3px;}
  .mesWindowContent{margin:4px;font-size:12px; clear:both;}
  .mesWindow .close{height:15px;width:28px; cursor:pointer;text-decoration:underline;background:#fff}

  #news_info
  {

  }
</style>
</head>
<%
	String Sid = CommUtil.StrToGB2312(request.getParameter("Sid"));
	String Func_Type_Id = CommUtil.StrToGB2312(request.getParameter("Func_Type_Id"));
	ArrayList User_Map_GJ  = (ArrayList)session.getAttribute("User_Map_GJ_" + Sid);
	UserInfoBean UserInfo    = (UserInfoBean)session.getAttribute("UserInfo_" + Sid);
	ArrayList Project_Info  = (ArrayList)session.getAttribute("Project_Info_" + Sid);
	CurrStatus currStatus  = (CurrStatus)session.getAttribute("CurrStatus_" + Sid);
	CorpInfoBean Corp_Info = (CorpInfoBean)session.getAttribute("Corp_Info_" + Sid);
	String Func_Name_Id = "W";
	String Func_Sub_Id = "";
	
	String Demo = "";
	if(Corp_Info != null)
	{
		Demo = Corp_Info.getDemo();
  	if(Demo == null){Demo = "";}
 	}
 	
	String IdList = "";
	String Project_Id = UserInfo.getProject_Id();

	double Longitude = 0.0;
	double Latitude  = 0.0;
	int    MapLev = 0;
	//初始化所在项目的位置
	if(null != Project_Info){
		Iterator deviter = Project_Info.iterator();
		while(deviter.hasNext()){
			ProjectInfoBean projectBean = (ProjectInfoBean)deviter.next();
			if(UserInfo.getProject_Id().equals(projectBean.getId())){
					//初始化坐标
					Longitude = Double.parseDouble(projectBean.getLongitude());
					Latitude  = Double.parseDouble(projectBean.getLatitude());
					//初始化倍数
					MapLev  = Integer.parseInt(projectBean.getMapLev());
					break;
				}
		}
	}
	int sn = 0;
%>
<body style="background:#CADFFF">
	<!-- 地图 -->
	<div id="container"></div>
	
	<!-- 地图固定选栏 -->
	<div  style='position:absolute;width:200px;height:20px;left:15px;top:0px;background-color:#FFFFFF;overflow:auto;'>
		  <form  name="gj_class" >
				<select id="GJ_Select" name="GJ_Select" style="width:97%;height:20px" onChange="doReresh()"> 									  
						<option value="J"   <%=Func_Type_Id.equals("J")?"selected":""%>   >管线总地图</option>		
					  <option value="YJ"  <%=Func_Type_Id.equals("YJ")?"selected":""%>  >雨水管线图</option>
					  <option value="WJ"  <%=Func_Type_Id.equals("WJ")?"selected":""%>  >污水管线图</option>
				</select>
		  </form>
	</div>
	
	<!-- 左边栏页面 天气和公告 -->
	<div id='news_info_left' style='position:absolute;width:200px;height:550px;left:15px;top:20px;background-color:#FFFFFF;overflow:auto;'>
			<!--
			<div  style="background-color:'gray'">	
				<B>气象预告</B> 	 
			</div>
			-->
			<div>
				<iframe name="weather_inc" src="http://i.tianqi.com/index.php?c=code&id=4" width="200" height="206" frameborder="0" marginwidth="0" marginheight="0" scrolling="no" ></iframe>
			</div>
			<br>
			<br>
			<br>
			<div align=center>	
				<B>事件公告</B> 
			</div>
			<div align=center>	
				<br/>
		  	<%=Demo%>
			</div>
	</div>
	
	<!-- 右边栏页面 -->
	<div id='news_info_right' style='position:absolute;width:430px;height:90%;right:16px;top:0px;background-color:#FFFFFF;overflow:auto;'>
		
		<select id="Func_Sub_Id"  name="Func_Sub_Id" style="width:49%;height:23px" onChange="reloadgrid()"> 									  
				<option value="User_DataGJ_His.do"   <%=Func_Sub_Id.equals("User_DataGJ_His.do")?"selected":""%>   >管井信息</option>		
			  <option value="User_DataGX_His.do"   <%=Func_Sub_Id.equals("User_DataGX_His.do")?"selected":""%>   >管线信息</option>
		</select>
		<select id="Func_Name_Id" name="Func_Name_Id" style="width:49%;height:23px" onChange="reloadgrid()"> 
			  <option value="W"   <%=Func_Name_Id.equals("W")?"selected":""%>   >污水信息</option>									  
			  <option value="Y"   <%=Func_Name_Id.equals("Y")?"selected":""%>   >雨水信息</option>
			  
		</select>
		
		<table id="dg" title="" style="width:430px;height:98%"
				data-options="rownumbers:true,singleSelect:true,pagination:true,remoteSort:false,multiSort:true,url:'User_DataGJ_His.do?Sid=<%=Sid%>&Project_Id=<%=Project_Id%>&Func_Name_Id=W&Current_Time=<%=new Date()%>',method:'get'">
			<thead>
				<tr>
					<th field="gj_id" width="80" align="center">编码</th>
					<th field="top_height" width="80" align="center" sortable="true" sorter="sort_int" formatter="formatValue">顶高</th>
					<th field="base_height" width="80" align="center" sortable="true" sorter="sort_int" formatter="formatValue">底高</th>
					<th field="material" width="80" align="center">材料</th>
					<th field="value" width="80" align="center" sortable="true" sorter="sort_int" formatter="formatValue">水位</th>
				</tr>
			</thead>
		</table>
	</div>
	
	</div>
	<div id='menu_info' style='position:absolute;width:16px;height:100%;left:0px;top:0px;filter:alpha(Opacity=80);-moz-opacity:0.5;opacity:0.5;background-color:#bbbdbb;'>
		<img id='news_img' src='../skin/images/map2close.gif' style='width:16px;height:16px;cursor:hand;' title='收起' onclick='doOpenLeft()'>
	</div>
	
	</div>
	<div id='menu_info' style='position:absolute;width:16px;height:100%;right:0px;top:0px;filter:alpha(Opacity=80);-moz-opacity:0.5;opacity:0.5;background-color:#bbbdbb;'>
		<img id='news_img' src='../skin/images/map2close.gif' style='width:16px;height:16px;cursor:hand;' title='收起' onclick='doOpenRight()'>
	</div>
</body>

<!--表格数据JS处理-->
<script language=javascript>

  function reloadgrid()
 	{
		$('#dg').datagrid('options').url = document.getElementById("Func_Sub_Id").value + "?Sid=<%=Sid%>&Project_Id=<%=Project_Id%>&Func_Name_Id=" + document.getElementById("Func_Name_Id").value + "&Current_Time=<%=new Date()%>";  
		$('#dg').datagrid('reload');
 	}
 

	$(function(){
		var pager = $('#dg').datagrid().datagrid('getPager');	// get the pager of datagrid
		pager.pagination({
			 pageSize: 10
		});
	});
	
	function formatValue(val,row){    
		return parseFloat(val).toFixed(2);
	}   
	//按数字类型排序 自定义排序
	function sort_int(a,b){
		if(parseFloat(a) > parseFloat(b)) return 1;
	  else return -1;
	}  

	function doOpenLeft()
	{
		if(document.getElementById('news_info_left').style.display == '')
		{
			document.getElementById('news_img').src = '../skin/images/map2open.gif';
			document.getElementById('news_img').title = '收起';
			document.getElementById('news_info_left').style.display = 'none';
		}
		else
		{
			document.getElementById('news_img').src = '../skin/images/map2close.gif';
			document.getElementById('news_img').title = '展开';
			document.getElementById('news_info_left').style.display = '';
		}
	}
	function doOpenRight()
	{
		if(document.getElementById('news_info_right').style.display == '')
		{
			document.getElementById('news_img').src = '../skin/images/map2open.gif';
			document.getElementById('news_img').title = '收起';
			document.getElementById('news_info_right').style.display = 'none';
		}
		else
		{
			document.getElementById('news_img').src = '../skin/images/map2close.gif';
			document.getElementById('news_img').title = '展开';
			document.getElementById('news_info_right').style.display = '';
		}
	}
</script>


<!--BaiduMap JS处理-->
<SCRIPT LANGUAGE=javascript>
//兼容性
if(1 == fBrowserRedirect() || 2 == fBrowserRedirect())//若是iphone,ipad
{
	window.addEventListener('onorientationchange' in window ? 'orientationchange' : 'resize', setHeight, false);
	setHeight();
}
function setHeight()
{
	document.getElementById('container').style.height = document.body.offsetHeight + 'px';
}

//载入地图
var map = new BMap.Map("container");                        //创建地图实例
//map.setMapType(BMAP_HYBRID_MAP);                          //默认类型为卫星、路网一体
var point = new BMap.Point(<%=Longitude%>, <%=Latitude%>);  //创建中心点坐标，默认为第一家企业
map.centerAndZoom(point, <%=MapLev%>);                      //初始化地图，设置中心点坐标和地图级别
//map.addControl(new BMap.NavigationControl());               //添加一个平移缩放控件，位置可偏移、形状可改变
map.addControl(new BMap.ScaleControl());                    //添加一个比例尺控件，位置可偏移[var opts = {offset: new BMap.Size(150, 5)};map.addControl(new BMap.ScaleControl(opts));]
map.addControl(new BMap.OverviewMapControl());              //添加一个缩略图控件，位置可偏移
//map.addControl(new BMap.MapTypeControl());                //添加地图类型变换(地图-卫星-三维)，位置可偏移
map.enableScrollWheelZoom();                                //启用滚轮放大缩小

//管线段集合
var gjArray = new Array();
	function gjGet(arrPerson,objPropery,objValue)
	{
   return $.grep(arrPerson, function(cur,i){
   				if(objValue == cur[objPropery])
          	return cur[objPropery];
       });
	}


//2.添加定义标注图标
function addGJMarker(point, pCorp_Id, pCName, pX, pY, pType)
{	
	var pIcon  = (((pCorp_Id.indexOf("WJ")) > 0)?'../skin/images/map_gj_Red.gif':'../skin/images/map_gj_blue.gif');
	var myIcon = new BMap.Icon(pIcon, new BMap.Size(pX, pY));
 	var marker = new BMap.Marker(point, {icon: myIcon});
 	var myLabel= new BMap.Label(pCName, {offset:new BMap.Size(0, pY)});
 	
 	myLabel.setStyle
 	({
 		fontSize:"11px",
 		font:"bold 10pt/12pt",
 		border:"0px",
 		color:"black",
 		textAlign:"center",
 		background:"yellow",
 		cursor:"pointer"
 	});
 	if(pType != 0)
 	{
 		marker.setLabel(myLabel);//文字标记
 	}	
 	map.addOverlay(marker);
 	
  //点击
 	marker.addEventListener("click", function()
 	{
 		doGJDefence(pCorp_Id, pType);
	});
}

function addGXMarker(polyline,gxId)
{
	 map.addOverlay(polyline);
	 polyline.addEventListener("click", function()
 	{
 		doGXDefence(gxId);
	});
} 
	
//管井状态监控
var reqRealGJ = null;
function RealGJStatus()
{
	if(window.XMLHttpRequest)
  {
    reqRealGJ = new XMLHttpRequest();
  }
	else if(window.ActiveXObject) //若IE6.0或5.5
	{
    reqRealGJ = new ActiveXObject('Microsoft.XMLHTTP');
  }
	reqRealGJ.onreadystatechange = function(){
	  var state = reqRealGJ.readyState;
	  if(state == 4)
	  {
	    if(reqRealGJ.status == 200)
	    {
	      var Resp = reqRealGJ.responseText;
	      if(null != Resp && Resp.length >= 4 && Resp.substring(0,4) == '0000')
	      {
	      	//1.删除
					map.clearOverlays();      	
	      	//2.添加
	      	var list = Resp.substring(4).split(";");
	      	for(var i=0; i<list.length && list[i].length>0; i++)
	      	{
	      		  var sublist = list[i].split("|");
	      		  if(sublist[0].indexOf(document.getElementById("GJ_Select").value)< 0){
      		  	  continue;
      		    }
	      		  var point = new BMap.Point(sublist[1], sublist[2]);  
	      		  var gjObj = new Object();

	      		  gjObj.tId = sublist[0];
	      		  gjObj.tPoint = point;
	      		  gjArray.push(gjObj);
    		  
	      			var pName = 'S: ';
						  addGJMarker(point, sublist[0], pName+sublist[4], '10', '10', sublist[3]);
							//;TSGYJ005-3|120.203581|30.276107|0|0;
	      	}
	      }
	    }
	  }
	};
	var url = "User_ToPo_GJ.do?Cmd=21&Id=<%=Project_Id%>&Sid=<%=Sid%>&currtime="+new Date();
	reqRealGJ.open('POST',url,false);
	reqRealGJ.send(null);
}

//管线状态监控
var reqRealGX = null;
function RealGXStatus()
{
	if(window.XMLHttpRequest)
  {
    reqRealGX = new XMLHttpRequest();
  }
	else if(window.ActiveXObject) //若IE6.0或5.5
	{
    reqRealGX = new ActiveXObject('Microsoft.XMLHTTP');
  }
	reqRealGX.onreadystatechange = function(){
	  var state = reqRealGX.readyState;
	  if(state == 4)
	  {
	    if(reqRealGX.status == 200)
	    {
	      var Resp = reqRealGX.responseText;
	      if(null != Resp && Resp.length >= 4 && Resp.substring(0,4) == '0000')
	      {

	      	//2.添加
	      	var list = Resp.substring(4).split(";");
	      	for(var i=0; i<list.length && list[i].length>0; i++)
	      	{
	      		var sublist = list[i].split("|");
	      		if(sublist[1].indexOf(document.getElementById("GJ_Select").value)< 0){
      		  	 continue;
      		  }
      		  var gjStartId = sublist[0];
      		  var gjStart = gjGet(gjArray, "tId", gjStartId);
      		  var gxId = "";
      		  var polyline = null;
      		  if(sublist[1].indexOf("WJ") > 0)
      		  {
      		  	var gjEndId = sublist[1];
	      		  var gjEnd   = gjGet(gjArray, "tId", gjEndId);
	      		  if(null == gjStart[0] || null == gjEnd[0]){continue;}
						  polyline = new BMap.Polyline([gjStart[0].tPoint, gjEnd[0].tPoint], {strokeColor:"red", strokeWeight:4, strokeOpacity:0.8});
						  gxId = sublist[1].replace('WJ', 'WG');
      		  }
      		  else if(sublist[1].indexOf("YJ") > 0)
      		  {
      		  	var gjEndId = sublist[1];
	      		  var gjEnd   = gjGet(gjArray, "tId", gjEndId);
	      		  if(null == gjStart[0] || null == gjEnd[0]){continue;}
							polyline = new BMap.Polyline([gjStart[0].tPoint, gjEnd[0].tPoint], {strokeColor:"blue", strokeWeight:4, strokeOpacity:0.8});
							gxId = sublist[1].replace('YJ', 'YG');
      		  }
      		  addGXMarker(polyline,gxId);
	      	}
	      }
	    }
	  }
	};
	var url = "User_ToPo_GX.do?Cmd=21&Id=<%=Project_Id%>&Sid=<%=Sid%>&currtime="+new Date();
	reqRealGX.open('POST',url,false);
	reqRealGX.send(null);
}
RealGJStatus()
RealGXStatus()

//左点击查看接口
var reqInfo = null;
function doGJDefence(pId, pType)
{
	if(pType == 0){
		  var Pdiag = new Dialog();
			Pdiag.Top = "50%";
			Pdiag.Width = 160;
			Pdiag.Height = 135;
			Pdiag.Title = "管井属性";
			Pdiag.URL = "User_DevGJ_Info.do?Cmd=3&Sid=<%=Sid%>&Id="+pId+"&Type="+pType;
			Pdiag.CancelEvent=function()
			{
				Pdiag.close();	//关闭窗口
	//			RealStatus();		//页面刷新
			};
			Pdiag.show();
	}
	else
	{
			var Pdiag = new Dialog();
			Pdiag.Top = "50%";
			Pdiag.Width = 800;
			Pdiag.Height = 450;
			Pdiag.Title = "管井编码: "+pId;
			Pdiag.URL = "User_DataGJ_Graph.do?Cmd=4&Sid=<%=Sid%>&GJ_Id="+pId+"&Type="+pType;
			Pdiag.CancelEvent=function()
			{
				Pdiag.close();	//关闭窗口
	//			RealStatus();		//页面刷新
			};
			Pdiag.show();
		
	}	
}

	//左点击查看管线接口
function doGXDefence(pId)
{
	
	var Pdiag = new Dialog();
	Pdiag.Top = "50%";
	Pdiag.Width = 160;
	Pdiag.Height = 135;
	Pdiag.Title = "管线属性";
	Pdiag.URL = "User_DevGX_Info.do?Cmd=3&Sid=<%=Sid%>&Id="+pId;
	Pdiag.CancelEvent=function()
	{
		Pdiag.close();	//关闭窗口
		//RealGJStatus();		//页面刷新
	};
	Pdiag.show();
}

function doReresh()
{
	this.location = "MapMain_Map.jsp?Func_Type_Id="+document.getElementById("GJ_Select").value+"&Sid=<%=Sid%>";
}

</SCRIPT>
</html>
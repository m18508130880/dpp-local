<%@ page contentType="text/html; charset=gb2312" %>
<%@ page import="java.util.*" %>
<%@ page import="bean.*" %>
<%@ page import="util.*" %>
<%@ page import="java.lang.*" %>
<%@ page import="java.math.*" %>
<%@ page import="java.text.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>水位折线图</title>
<link   type="text/css" href="../skin/css/style.css" rel="stylesheet"/>
<script type="text/javascript" src="../skin/js/jquery.min.js"></script>
<script type="text/javascript" src="../skin/js/highcharts.js"></script>
<script type="text/javascript" src="../skin/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="../skin/js/util.js"></script>

<%
	String Sid   = CommUtil.StrToGB2312(request.getParameter("Sid"));
  CurrStatus currStatus = (CurrStatus)session.getAttribute("CurrStatus_" + Sid);
  UserInfoBean UserInfo = (UserInfoBean)session.getAttribute("UserInfo_" + Sid);
	ArrayList User_DataGJ_Graph = (ArrayList)session.getAttribute("User_DataGJ_Graph_" + Sid);
	ArrayList  One_GJ        = (ArrayList)session.getAttribute("One_GJ_" + Sid);
  String GJ_Id = request.getParameter("GJ_Id");
  String WaterLev = "";
	String Material = "";
	String Base_Height = "";
	String Top_Height = "";
  
  if(One_GJ != null){
  	Iterator iterator = One_GJ.iterator();
		DevGJBean devGJBean = (DevGJBean)iterator.next();
		GJ_Id = devGJBean.getId();
	  WaterLev = devGJBean.getWaterLev();
		Material = devGJBean.getMaterial();
		Base_Height = devGJBean.getBase_Height();
		Top_Height = devGJBean.getTop_Height();
  }
  if(User_DataGJ_Graph != null){
  	Iterator iterator = User_DataGJ_Graph.iterator();
		DataGJBean statBean = (DataGJBean)iterator.next();
		GJ_Id = statBean.getGJ_Id();
  }
%>
</head>
<body style='background:#ffffff'>
<form name='User_DataGJ_Graph'   method='post' target='mFrame'>
<div id='down_bg_2'>
	<table style='margin:auto'    border=0 cellPadding=0 cellSpacing=0 bordercolor='#3491D6' borderColorDark='#ffffff' width='100%'>
			<tr height='30' valign='top'>
					<td width='85%' align='center'>
				      <select name='cmd' style='width:90px;height:20px' onChange='doSelect()'>
				      	<option value='4' <%=(currStatus.getCmd() == 4 ?"SELECTED":"")%>>最近24小时</option>
				        <option value='5' <%=(currStatus.getCmd() == 5 ?"SELECTED":"")%>>最近1周</option>
				        <option value='6' <%=(currStatus.getCmd() == 6 ?"SELECTED":"")%>>最近1月</option>
				      
				      </select>
				  </td>
			</tr>
			<tr height='30' valign='top'>
		    	<td width='100%' align='center' colspan=2>
		      		<div id='container' style='width:100%;height:350px;margin: 0 auto'></div>
		    	</td>
		  </tr>
		        <tr height='20'>
							<td width="35px" align='center' >编码: <%=GJ_Id%>&nbsp;
							顶高: <%=Top_Height%>&nbsp;
							底高: <%=Base_Height%>&nbsp;
							材质: <%=Material%>&nbsp;
							水位: <%=WaterLev%>&nbsp;</td>
					  </tr>				
  </table>
</div>
<input name="GJ_Id" type="hidden" value="<%=GJ_Id%>">
</form>
</body>
<SCRIPT LANGUAGE=javascript>
function doSelect()
{
	location = "User_DataGJ_Graph.do?Sid=<%=Sid%>&Cmd="
						+ User_DataGJ_Graph.cmd.value
						+ "&GJ_Id=" + User_DataGJ_Graph.GJ_Id.value;
}	

		var data_serie = [];
		var data_xAxis = [];
		<%
		if(User_DataGJ_Graph != null)
		{
			Iterator iterator = User_DataGJ_Graph.iterator();
			while(iterator.hasNext())
			{
				 DataGJBean statBean = (DataGJBean)iterator.next();
				 String CTime = statBean.getCTime();
				 String CValue = statBean.getValue();
			%>
				 data_serie.push(parseFloat('<%=CValue%>'));
				 data_xAxis.push('<%=CTime.substring(11,13)%>');
			<%
      }
  	}
		%>


		var chart;
    $(document).ready(function () {
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'container',
                defaultSeriesType: 'line',
                marginRight: 130,
                marginBottom: 25
            },
            title: {
                text: '',
                x: -20
            },
            subtitle: {
                text: '',
                x: -20
            },

            xAxis: {
                categories: data_xAxis  //设置x轴的刻度
                
            },
            yAxis: {
                title: {
                    text: '单位/m'      //设置y轴的标题
                },
                plotLines: [{
                    value: 0,
                    width: 0.5,
                    color: '#808080'
                }]
            },
            tooltip: {
                formatter: function () {
                    return '<b>' + this.series.name + '</b><br/>' +
               this.x + ': ' + this.y ;  //鼠标放在数据点的显示信息，但是当设置显示了每个节点的数据项的值时就不会再有这个显示信息
                }
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'top',
                x: 0,
                y: 100,
                borderWidth: 0
            },
            exporting: {
                enabled: true,
                url: "http://localhost:49394/highcharts_export.aspx" //导出图片的URL，默认导出是需要连到官方网站去的哦
            },
            plotOptions: {
                line: {
                    dataLabels: {
                        enabled: true //显示每条曲线每个节点的数据项的值
                    },
                    enableMouseTracking: false
                }
            },
            series: [{
                name: '水位高度',
                data: data_serie
            }]
        });

    });

</script>
</html>
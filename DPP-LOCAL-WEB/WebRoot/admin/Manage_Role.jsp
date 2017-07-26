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
<title>百事达排水管网信息管理平台</title>
<meta http-equiv="x-ua-compatible" content="ie=7"/>
<link type="text/css" href="../skin/css/style.css" rel="stylesheet"/>
<link rel="stylesheet" href="../skin/css/zTreeStyle2.css" type="text/css">
<script type="text/javascript" src="../skin/js/util.js"></script>
<script type="text/javascript" src="../skin/js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="../skin/js/jquery.ztree.core-3.4.js"></script>
<script type="text/javascript" src="../skin/js/jquery.ztree.excheck-3.4.js"></script>
<script type="text/javascript" src="../skin/js/jquery.ztree.exedit-3.4.js"></script>
<script language=javascript>document.oncontextmenu=function(){window.event.returnValue=false;};</script>
</head>
<%
	
	String Sid = CommUtil.StrToGB2312(request.getParameter("Sid"));
  CurrStatus currStatus = (CurrStatus)session.getAttribute("CurrStatus_" + Sid);
  ArrayList Manage_Role = (ArrayList)session.getAttribute("Manage_Role_" + Sid);
  ArrayList Device_Detail   = (ArrayList)session.getAttribute("Device_Detail_" + Sid);
  
%>
<body style="background:#CADFFF">
<form name="Manage_Role"  action="User_Role.do" method="post" target="mFrame">
<div id="down_bg_2">
	<div id="cap"><img src="../skin/images/manage_role.gif"></div><br><br><br>
	<div id="right_table_center">
		<table width="70%" height='100%' style='margin:auto;' border=0 cellPadding=0 cellSpacing=0>
			<tr height='30px'>
				<td width='100%' align='right'>
					<img style="cursor:hand" onClick="doSubmit()"   src="../skin/images/mini_button_submit.gif">
				</td>
			</tr>
			<tr height='30px'>
				<td width='100%' align=center>
					<table cellpadding="0" cellspacing="0" border="1" width="100%" bordercolor="#3491D6" borderColorDark="#ffffff">
						<tr valign="top" height='350px'>
							<td width="45%" align='center'>
								<div class="zTreeDemoBackground" style="width:99%;border:0px solid #0068a6"><ul id="areaTree" class="ztree"></ul></div>
							</td>
							<td width="10%" align='center'>
								<br><br><br><br><br>
								<img src="../skin/images/moveNode.png"  onfocus="this.blur();" title="移动节点 左->右" onclick="moveTreeL2R();">
								<br><br><br><br><br>
								<img src="../skin/images/moveNodeR.png" onfocus="this.blur();" title="移动节点 右->左" onclick="moveTreeR2L();">
								<br><br><br><br><br>
							</td>
							<td width="45%" align='center'>
								<div class="zTreeDemoBackground" style="width:99%;border:0px solid #0068a6"><ul id="devTree" class="ztree"></ul></div>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</div>
</div>
</form>
</body>
<SCRIPT LANGUAGE=javascript>
var setting = 
{
	view: 
	{
		addHoverDom: addHoverDom,
		removeHoverDom: removeHoverDom,
		selectedMulti: false
	},
	edit: 
	{
		enable: true,
		drag: {isMove: false,isCopy: false},
		showRenameBtn: setRenameBtn,
		showRemoveBtn: setRemoveBtn
	},
	data: 
	{
		simpleData:{enable: true}
	},
	callback: 
	{
		beforeRename: beforeRename
	}
};

var setting2 = 
{
	edit: 
	{
		enable: true,
		showRenameBtn: false,
		showRemoveBtn: false
	},
	data: 
	{
		simpleData:{enable: true}
	},
	callback: 
	{
		beforeDrop: zTreeBeforeDrop
	}
}

function setRenameBtn(treeId, treeNode)
{
	if(treeNode.tId.substring(0,7) == 'devTree')
	{
		return false;
	}
	switch(treeNode.level)
	{
		case 0:
				return false;
			break;
		case 1:
				return true;
			break;
		case 2:
				return true;
			break;
		case 3:
				return true;
			break;
		case 4:
				return false;
			break;
		default:
				return false;
			break;
	}
}

function setRemoveBtn(treeId, treeNode)
{
	if(treeNode.tId.substring(0,7) == 'devTree')
	{
		return false;
	}
	switch(treeNode.level)
	{
		case 0:
				return fabreak;
			}
			if(Point.trim().length() > 0)
			{
				String[] list = Point.split(",");
				for(int i=0; i<list.length && list[i].trim().length() > 0; i++)
				{
					if(Device_Detail != null)
					{
						Iterator iterator2 = Device_Detail.iterator();
						while(iterator2.hasNext())
						{
							EquipInfoBean Bean = (EquipInfoBean)iterator2.next();
							if(Bean.getId().equals(list[i]))
							{$('#' + treeNode.tId + 'Bean.getId()if (treeNode.eBean.getBrief()$('#addBtn_'+trBean.getId()ength>0) 
		Ideturn;
		var addStr = "<span class='button add' i}
						}
					}
				}
			}
		}
	}us='this.blur();'></span>";
		sObj.after(addStr);
		var btn = $('#addBtn_'+treeNode.id);
		if (btn) btn.bind('click', function()
		{
			var zTree = $.fn.zTree.getZTreeObj('areaTree');
			vif(Device_Detail != null)
	{
		Iterator iterator = Device_Detail.iterator();
		while(iterator.hasNext())
		{
			EquipInfoBean statBean = (EquipInfoBean)iterator.next();
			String Id = statBean.getId();
			String Brief = statBean.getBrief();
			if(null == Brief){Brief = "";}arseInt(childNodes[i].id, 10);
					}
       	}
      }
      
      var _newId = newId.toString();
			if(parseInt(_newId.substring(_newId.length-2, _newId.length)) >= 99)
			{
			  alert('当前角色或区域超99个，请精简!');
			  return;
			}
	
			var strNewId = StrLeftFillZero((newId + 1).toString(), treeNode.value.length+2);
			zTree.addNodes(treeNode, {id:strNewId, name:'fq'+strNewId, value:strNewId, pId:treeNode.id});
			return false;
		});
	}
};

function removeHoverDom(treeId, treeNode) 
{
	if(0 == treeNode.level || 1 == treeNode.level || 2 == treeNode.level)
	{
		$('#addBtn_'+treeNode.id).unbind().remove();
	}
};

function beforeRename(treeId, treeNode, newName) 
{
	var className = 'dark';
	className = (className === 'dark' ? '':'dark');
	if (newName.Trim().length == 0) 
	{
		alert('节点名称不能为空!');
		var zTree = $.fn.zTree.getZTreeObj('areaTree');
		setTimeout(function(){zTree.editName(treeNode)}, 10);
		return false;
	}
	return true;
}

//---放下前---
function zTreeBeforeDrop(treeId, treeNodes, targetNode, moveType)
{
	if(null == treeNodes || treeNodes.length == 0)
	{
		return false;
	}
	
	for(var i=0; i<treeNodes.length; i++)
  {
  	if(0 == treeNodes[i].level)
  	{
			return false;
  	}
  }
	
	if(null == targetNode || 0 == targetNode.level || 1 == targetNode.level || 2 == targetNode.level || 4 == targetNode.level)
	{
		return false;
	}
	
	if('areaTree' == treeId)
	{
		var zTree = $.fn.zTree.getZTreeObj(treeId);
		var childNodes = zTree.transformToArray(targetNode);
    for(var i=0; i<childNodes.length; i++)
    {
    	if(targetNode.value == childNodes[i].pId)
    	{
    		for(var j=0; j<treeNodes.length; j++)
			  {
	    		if(treeNodes[j].id == childNodes[i].id)
					{
						alert("个别站点已存在，请检查!");
						return false;
					}
			  }		
     	}
    }
    
    for(var i=0; i<treeNodes.length; i++)
	  {
	  	var zTree2 = $.fn.zTree.getZTreeObj('devTree');
	  	zTree2.addNodes(treeNodes[i].getParentNode(), treeNodes[i]);
	  }
	}
}

function moveTreeL2R()
{
	var zTree1 = $.fn.zTree.getZTreeObj('areaTree');
	var zTree2 = $.fn.zTree.getZTreeObj('devTree');
	moveTreeNode(zTree1, zTree2);
}

function moveTreeR2L()
{
	var zTree1 = $.fn.zTree.getZTreeObj('areaTree');
	var zTree2 = $.fn.zTree.getZTreeObj('devTree');
	moveTreeNode(zTree2, zTree1);
}

function moveTreeNode(srcTree, targetTree)
{
	var srcNode = srcTree.getSelectedNodes();
	var targetNode = targetTree.getSelectedNodes();
	if(null == srcNode || 0 == srcNode.length)
	{
		alert("请先选择需要移动的站点!");
		return false;
	}
	
	//左边数移除
	if($.fn.zTree.getZTreeObj('areaTree') == srcTree)
	{
		for(var i=0; i<srcNode.length; i++)
	  {
  		if(0 == srcNode[i].level)
			{
				alert("当前节点不可删除，请重新选择!");
				return false;
			}
	  }
	  
	  for(var i=0; i<srcNode.length; i++)
	  {
  		srcTree.removeNode(srcNode[i]);
	  }
	  
		return;
	}
	//右边树移至左边
	else
	{
		if(null == targetNode || 0 == targetNode.length)
		{
			alert("请先选择正确放置站点的防区!");
			return;
		}
		
		for(var i=0; i<srcNode.length; i++)
	  {
  		if(0 == srcNode[i].level)
			{
				return false;
			}
	  }
	  
	  for(var i=0; i<targetNode.length; i++)
	  {
  		if(0 == targetNode[i].level || 1 == targetNode[i].level || 2 == targetNode[i].level || 4 == targetNode[i].level)
			{
				alert("请先选择正确放置站点的防区!");
				return false;
			}
	  }
	  
	  for(var i=0; i<srcNode.length; i++)
    {
    	for(var j=0; j<targetNode.length; j++)
    	{
    		var childNodes = targetTree.transformToArray(targetNode[j]);
    		for(var k=0; k<childNodes.length; k++)
	      {
	      	if(targetNode[j].value == childNodes[k].pId)
	      	{
	          if(srcNode[i].id == childNodes[k].id)
						{
							alert("个别站点已存在，请检查!");
							return false;
						}
	       	}
	      }
    	}
    }
    
    for(var i=0; i<srcNode.length; i++)
    {
    	for(var j=0; j<targetNode.length; j++)
    	{
    		targetTree.addNodes(targetNode[j], srcNode[i]);
    	}
    }	
	}
}

var dataMaker = function(count)
{
	var nodes = [];
	var n;
	n = {id:'50', name:'管理权限', value:'50', pId:'-1', isParent:true, open:true};
	nodes.push(n);
	
	<%
	if(Manage_Role != null)
	{
		Iterator iterator = Manage_Role.iterator();
		while(iterator.hasNext())
		{
			UserRoleBean statBean = (UserRoleBean)iterator.next();
			String Id = statBean.getId();
			String CName = statBean.getCName();
			String Point = statBean.getPoint();
			if(null == CName){CName = "";}
			if(null == Point){Point = "";}
			switch(Id.length())
			{
				case 4:
	%>
					n = {id:'<%=Id%>', name:'<%=CName%>', value:'<%=Id%>', pId:'50', isParent:true, open:false};
					nodes.push(n);
	<%					
					break;
				case 6:
	%>
					n = {id:'<%=Id%>', name:'<%=CName%>', value:'<%=Id%>', pId:'<%=Id.substring(0,4)%>', isParent:true, open:false};
					nodes.push(n);
	<%
					break;
				case 8:
	%>
					n = {id:'<%=Id%>', name:'<%=CName%>', value:'<%=Id%>', pId:'<%=Id.substring(0,6)%>', isParent:true, open:false};
					nodes.push(n);
	<%
					break;
			}
			if(Point.trim().length() > 0)
			{
				String[] list = Point.split(",");
				for(int i=0; i<list.length && list[i].trim().length() > 0; i++)
				{
					if(Device_Detail != null)
					{
						Iterator iterator2 = Device_Detail.iterator();
						while(iterator2.hasNext())
						{
							DeviceDetailBean Bean = (DeviceDetailBean)iterator2.next();
							if(Bean.getId().equals(list[i]))
							{
	%>
								n = {id:'<%=Bean.getId()%>', name:'<%=Bean.getBrief()%>', value:'<%=Bean.getId()%>', pId:'<%=Id%>', open:false};
								nodes.push(n);    
	<%
							}
						}
					}
				}
			}
		}
	}
	%>
	
	return nodes;
}

var dataMaker2 = function(count)
{
	var nodes = [];
	var n;
	
	n = {id:'0', name:'站点列表', value:'0', pId:'-1', isParent:true, open:true};
	nodes.push(n);
	
	<%
	if(Device_Detail != null)
	{
		Iterator iterator = Device_Detail.iterator();
		while(iterator.hasNext())
		{
			DeviceDetailBean statBean = (DeviceDetailBean)iterator.next();
			String Id = statBean.getId();
			String Brief = statBean.getBrief();
			if(null == Brief){Brief = "";}
	%>
			n = {id:'<%=Id%>', name:'<%=Brief%>',  value:'<%=Id%>', pId:'0', open:false};
			nodes.push(n);
	<%	
		}
	}
	%>
	
	return nodes;
}

function createTree()
{
	var zNodes = dataMaker(500);
	$("#areaTree").empty();
	$.fn.zTree.init($("#areaTree"), setting, zNodes);
	
	var zNodes2 = dataMaker2(500);
	$("#devTree").empty();
	$.fn.zTree.init($("#devTree"), setting2, zNodes2);
}
createTree();

function doSubmit()
{
	var zTree1 = $.fn.zTree.getZTreeObj('areaTree')
	var Control_Role = "";
	
	var tempArray = zTree1.getNodesByParam('level', 1);
	for(var i in tempArray)
	{
		Control_Role += tempArray[i].value + ';' + tempArray[i].name + ';' + ' @';
	}
	
	tempArray = zTree1.getNodesByParam('level', 2);
	for(var i in tempArray)
	{
		Control_Role += tempArray[i].value + ';' + tempArray[i].name + ';' + ' @';
	}
	
	tempArray = zTree1.getNodesByParam('level', 3);
	for(var i in tempArray)
	{
		Control_Role += tempArray[i].value + ';' + tempArray[i].name + ';';
		var childNodes = zTree1.transformToArray(tempArray[i]);
		for(var j=0; j<childNodes.length; j++)
    {
    	if(tempArray[i].value == childNodes[j].pId)
    	{
    		Control_Role += childNodes[j].value + ',';
    	}
    }
		Control_Role += ' @';
	}
	
	if(Control_Role.length < 1)
	{
		alert("请先添加角色、防区及站点分布！");
		return;
	}
	
	if(Control_Role.length > 7000)
	{
		alert("权限配置超限，请精简！");
		return;
	}
	
	if(confirm("确定提交?"))
	{
		m_Add = createXHR();
		if(m_Add)
		{
			m_Add.onreadystatechange=callbackForAdd;
			var url = 'User_RoleOP.do?Cmd=13&Sid=<%=Sid%>&Id=50&RoleList='+Control_Role+'&currtime='+new Date();
			m_Add.open("post", url);
			m_Add.send(null);
		}
		else
		{
			alert("浏览器不支持，请更换浏览器！");
		}
	}
}

function callbackForAdd()
{
	if(m_Add.readyState == 4)
	{
		if(m_Add.status == 200)
		{
			var returnValue = m_Add.responseText;
			if(null != returnValue && returnValue == '0000')
			{
				alert('编辑成功!');
			}
			else
			{
				alert('编辑失败!');
			}
		}
		else
		{
			alert("页面出现异常！");
		}
	}
}

function createXHR()
{
	var xhr;
	try
	{
		xhr = new ActiveXObject("Msxml2.XMLHTTP");
	}
	catch(e)
	{
		try
		{
			xhr = new ActiveXObject("Microsoft.XMLHTTP");
		}
		catch(E)
		{
			xhr = false;
		}
	}
	if(!xhr && typeof XMLHttpRequest != 'undefined')
	{
		xhr = new XMLHttpRequest();
	}
	return xhr;
}
</SCRIPT>
</html>
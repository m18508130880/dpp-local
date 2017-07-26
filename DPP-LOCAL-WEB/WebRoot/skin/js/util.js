function getSubString(Total, Begin, End)
{
	 var reqStartPos = Total.indexOf(Begin) + Begin.length;
	 var reqEndPos = Total.indexOf(End);
	 return Total.substring(reqStartPos, reqEndPos);
}

function StrRightFillSpace(strData, len)
{
    var str = strData;
		var FillLen = len - str.length;
		for(var i=0; i < FillLen; i++)
		{
			str += " ";
		}
		return str;
}

function StrRightFillZero(strData, len)
{
    var str = strData;
		var FillLen = len - str.length;
		for(var i=0; i < FillLen; i++)
		{
			str += "0";
		}
		return str;
}

function StrLeftFillSpace(strData, len)
{
    var str = strData;
		var FillLen = len - str.length;
		for(var i=0; i < FillLen; i++)
		{
			str = " " + str;
		}
		return str;
}

function StrLeftFillZero(strData, len)
{
    var str = strData;
		var FillLen = len - str.length;
		for(var i=0; i < FillLen; i++)
		{
			str = "0" + str;
		}
		return str;
}

String.prototype.len = function()
{
	return this.replace(/[^\x00-\xff]/g, 'xx').length;
};

//去空格的正则表达式
String.prototype.Trim = function() 
{ 
	return this.replace(/(^\s*)|(\s*$)/g, ""); 	
}  
String.prototype.LTrim = function()
{ 
	return this.replace(/(^\s*)/g, ""); 
}  
String.prototype.RTrim = function()
{
	 return this.replace(/(\s*$)/g, ""); 
} 

Date.prototype.format = function(format)//author: meizz 
{
	var o = 
	{ 
		"M+" : this.getMonth()+1, //month
		"d+" : this.getDate(),    //day
		"h+" : this.getHours(),   //hour
		"m+" : this.getMinutes(), //minute
		"s+" : this.getSeconds(), //second
		"q+" : Math.floor((this.getMonth()+3)/3), //quarter 
		"S"  : this.getMilliseconds() //millisecond
	}
	if(/(y+)/.test(format))
	{
		format=format.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
	}
	for(var k in o)if(new RegExp("("+ k +")").test(format)) 
		format = format.replace(RegExp.$1, RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length)); 
	return format;
}

function get_cookie(name)
{
	var search = name + "=";
	var returnvalue = "";
	if(document.cookie.length > 0)
	{
		offset = document.cookie.indexOf(search);
		if(offset != -1)
		{
			offset += search.length;
			var end = document.cookie.indexOf(";", offset);
			if(end == -1)
			{
				end = document.cookie.length;
			}
			returnvalue = unescape(document.cookie.substring(offset, end));
		}
	}
	return  returnvalue;
}

function setcookie(name, days)
{
	var url = document.domain;
	var exp = new Date();
	exp.setTime(exp.getTime() + days*24*60*60*1000);
	document.cookie = name + "=" + get_cookie(name) + ";expires=" + exp.toGMTString();
}

function getMousePoint(ev) 
{
	//定义鼠标在视窗中的位置
	var point = 
	{
		x:0,
		y:0
	};
	
	// 如果浏览器支持 pageYOffset, 通过 pageXOffset 和 pageYOffset 获取页面和视窗之间的距离
	if(typeof window.pageYOffset != 'undefined') 
	{
		point.x = window.pageXOffset;
		point.y = window.pageYOffset;
	}
	// 如果浏览器支持 compatMode, 并且指定了 DOCTYPE, 通过 documentElement 获取滚动距离作为页面和视窗间的距离
	// IE 中, 当页面指定 DOCTYPE, compatMode 的值是 CSS1Compat, 否则 compatMode 的值是 BackCompat
	else if(typeof document.compatMode != 'undefined' && document.compatMode != 'BackCompat') 
	{
		point.x = document.documentElement.scrollLeft;
		point.y = document.documentElement.scrollTop;
	}
	// 如果浏览器支持 document.body, 可以通过 document.body 来获取滚动高度
	else if(typeof document.body != 'undefined') 
	{
		point.x = document.body.scrollLeft;
		point.y = document.body.scrollTop;
	}
	
	// 加上鼠标在视窗中的位置
	point.x += ev.clientX;
	point.y += ev.clientY;
	
	// 返回鼠标在视窗中的位置
	return point;
}

/**
* 简单时间控件： version 1.0
* 作者：李禄燊 
* 修改人：庞乃杰
* 时间：2007-10-31
* 使用说明：
* 首先把本控件包含到页面 
* <script src="XXX/setTime.js" type="text/javascript"></script>
* 控件调用函数：_SetTime(field)
* 例如 <input name="time" type="text"   onclick="_SetTime(this)"/>
*
**/
var str = "";
document.writeln("<div id='_contents' Author='_contents' style='padding:6px;background-color:#CADFFF;font-size:12px;border:1px solid #777777;position:absolute;left:?px;top:?px;width:?px;height:?px;z-index:1;visibility:hidden;'>");
str += "\u65f6<select Author='_contents' name='_hour'>";
for(h = 0; h <= 9; h++) 
{
	str += "<option Author='_contents' value=\"0" + h + "\">0" + h + "</option>";
}
for(h = 10; h <= 23; h++)
{
  str += "<option Author='_contents' value=\"" + h + "\">" + h + "</option>";
}
str += "</select> \u5206<select Author='_contents\"' name='_minute'>";
for(m = 0; m <= 59; m++) 
{
	if(m%5 == 0)
	{
		if(m < 10)
		{
		  str += "<option Author='_contents' value=\"0" + m + "\">0" + m + "</option>";
		}
		else
		{
		  str += "<option Author='_contents' value=\"" + m + "\">" + m + "</option>";
		}	  
	}
	else
	{
	   continue;
	}
}
str += "</select> <input Author='_contents' name='queding' type='button' onclick='_select()' value=\"\u786e\u5b9a\" style='font-size:12px' /></div>";
document.writeln(str);
var _fieldname;
function _SetTime(tt)
{
	_fieldname = tt;
  var ttop = tt.offsetTop;    //TT控件的定位点高
  var thei = tt.clientHeight; //TT控件本身的高
  var tleft = tt.offsetLeft;  //TT控件的定位点宽
  while(tt = tt.offsetParent)
  {
    ttop += tt.offsetTop;
    tleft += tt.offsetLeft;
  }
  document.all._contents.style.top = ttop + thei + 6;
  document.all._contents.style.left = tleft;
  document.all._contents.style.visibility = "visible";
}
function _select()
{
	_fieldname.value = document.all._hour.value + ":" + document.all._minute.value;
  document.all._contents.style.visibility = "hidden";
}

/*****************居中DIV******************/
var isIe = (document.all)?true:false;
//设置select的可见状态
function setSelectState(state)
{
  var objl=document.getElementsByTagName('select');
  for(var i=0; i<objl.length; i++)
  {
    objl[i].style.visibility = state;
  }
}

function mousePosition(ev)
{
  if(ev.pageX || ev.pageY)
  {
    return {x:ev.pageX, y:ev.pageY};
  }
  return {x:ev.clientX + document.body.scrollLeft - document.body.clientLeft,y:ev.clientY + document.body.scrollTop - document.body.clientTop};
}

//弹出方法
function showMessageBox(wTitle, content, wWidth, wHeight)
{
	closeWindow();
	var bWidth     = parseInt(document.documentElement.scrollWidth);
	var bHeight    = parseInt(document.documentElement.scrollHeight);
	var ascrollTop = parseInt(document.documentElement.scrollTop);
	var aWidth     = parseInt(document.body.clientWidth);
	var aHeight    = parseInt(document.body.clientHeight);	
	if(isIe)
	{
		setSelectState('hidden');
	}
	var back = document.createElement("div");
	back.id = "back";
	var styleStr = "top:0px;left:0px;position:absolute;background:#666;width:"+bWidth+"px;height:"+bHeight+"px;";
	styleStr += (isIe)?"filter:alpha(opacity=0);":"opacity:0;";
	back.style.cssText = styleStr;
	document.body.appendChild(back);
	showBackground(back,70);
	var mesW = document.createElement("div");
	mesW.id = "mesWindow";
	mesW.className = "mesWindow";
	mesW.innerHTML = "<div class='mesWindowTop'>"+wTitle+"<span><img src='../skin/images/dialogclose.gif' style='cursor:pointer' onclick='closeWindow();' /></span></div><div class='mesWindowContent' id='mesWindowContent'>"+content+"</div>"
	styleStr = "left:"+(aWidth-wWidth)/2+"px;top:"+(ascrollTop+(aHeight-wHeight)/2)+"px;position:absolute;width:"+wWidth+"px;height:"+wHeight+"px;";
	mesW.style.cssText = styleStr;
	document.body.appendChild(mesW);
}

//让背景渐渐变暗
function showBackground(obj, endInt)
{
	if(isIe)
	{
		obj.filters.alpha.opacity+=2;
		if(obj.filters.alpha.opacity<endInt)
		{
			setTimeout(function(){showBackground(obj,endInt)},5);
		}
	}
	else
	{
		var al = parseFloat(obj.style.opacity);
		al += 0.01;
		obj.style.opacity = al;
		if(al < (endInt/100))
		{
			setTimeout(function(){showBackground(obj,endInt)}, 5);
		}
	}
}

//关闭窗口
function closeWindow()
{
	if(document.getElementById('back')!=null)
	{
		document.getElementById('back').parentNode.removeChild(document.getElementById('back'));
	}
	if(document.getElementById('mesWindow')!=null)
	{
		document.getElementById('mesWindow').parentNode.removeChild(document.getElementById('mesWindow'));
	}
	if(isIe)
	{
		setSelectState('');
	}
}
/*****************居中DIV******************/
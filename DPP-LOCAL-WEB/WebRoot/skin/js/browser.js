//ä¯ÀÀÆ÷ÀàĞÍ
/*
retval
{
	1:ipad
	2:iphone
	3:Î´Öª
	4:pc(ie)
	5:pc(Firefox)
	6:pc(Chrome)
	7:pc(Opera)
	8:pc(Safari)
}
*/
function fGetQuery(name)
{ 
  var sUrl = window.location.search.substr(1);   
  var r = sUrl.match(new RegExp("(^|&)" + name + "=([^&]*)(&|$)"));   
  return (r == null ? null : unescape(r[2]));   
}

function fBrowserRedirect()
{
	var retval = 0;
	var bForcepc = fGetQuery("dv") == "pc";
  var sUserAgent = navigator.userAgent.toLowerCase();   
  var bIsIpad = sUserAgent.match(/ipad/i) == "ipad";     
  var bIsIphoneOs = sUserAgent.match(/iphone os/i) == "iphone os";   
  var bIsMidp = sUserAgent.match(/midp/i) == "midp";   
  var bIsUc7 = sUserAgent.match(/rv:1.2.3.4/i) == "rv:1.2.3.4";   
  var bIsUc = sUserAgent.match(/ucweb/i) == "ucweb";   
  var bIsAndroid = sUserAgent.match(/android/i) == "android";   
  var bIsCE = sUserAgent.match(/windows ce/i) == "windows ce";   
  var bIsWM = sUserAgent.match(/windows mobile/i) == "windows mobile";   
  if(bIsIpad)
  {
    if(!bForcepc)
    {
    	//alert('ipad');
      retval = 1;
    }   
  }   
  else if(bIsIphoneOs || bIsAndroid)
  {   
    if(!bForcepc)
    {   
    	//alert('iphone');
      retval = 2;
    }   
  }   
  else if(bIsMidp||bIsUc7||bIsUc||bIsCE||bIsWM)
  {
    if(!bForcepc)
    {   
    	retval = 3;
    }   
  } 
  else
  {
  	var explorer = window.navigator.userAgent;
		if(explorer.indexOf("MSIE") >= 0)
		{
			//alert("ie");
			retval = 4;
		}
		else if (explorer.indexOf("Firefox") >= 0) 
		{
			//alert("Firefox");
			retval = 5;
		}
		else if(explorer.indexOf("Chrome") >= 0)
		{
			//alert("Chrome");
			retval = 6;
		}
		else if(explorer.indexOf("Opera") >= 0)
		{
			//alert("Opera");
			retval = 7;
		}
		else if(explorer.indexOf("Safari") >= 0)
		{
			//alert("Safari");
			retval = 8;
		}
  }  
  return retval;
}
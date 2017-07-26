package util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CheckCode {
	public final static int WIDTH = 60;
    public final static int HEIGHT = 20;
    public static void  CreateCheckCode(HttpServletRequest request, HttpServletResponse response, String pSid) throws ServletException, IOException
    {
		response.setContentType("image/jpeg"); 
		ServletOutputStream sos = response.getOutputStream(); 
		// 设置浏览器不要缓存此图片 
		response.setHeader("Pragma","No-cache"); 
		response.setHeader("Cache-Control","no-cache"); 
		response.setDateHeader("Expires", 0); 
		// 创建内存图象并获得其图形上下文          
		BufferedImage image = 
			new BufferedImage(CheckCode.WIDTH, CheckCode.HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics2D  g = image.createGraphics(); 
		// 产生随机的认证码 
		char [] rands = CheckCode.generateCheckCode(); 
		// 产生图像 
		drawBackground(g); 
		drawRands(g,rands); 

     	// 结束图像 的绘制 过程， 完成图像 
     	g.dispose(); 
     	// 将图像输出到客户端 
     	ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
     	ImageIO.write(image, "JPEG", bos); 
     	byte [] buf = bos.toByteArray(); 
     	response.setContentLength(buf.length); 
     	// 下面的语句也可写成： bos.writeTo(sos); 
     	sos.write(buf); 
     	bos.close(); 
     	sos.close(); 
     	
     	// 将当前验证码存入到 Session 中 
     	CurrStatus currStatus = new CurrStatus();
     	currStatus.setCheckCode(new String(rands));
     	currStatus.setResult("");
     	request.getSession().setAttribute("CurrStatus_" + pSid, currStatus);
    } 
    
	public static char [] generateCheckCode() 
    { 
       // 定义验证码的字符表 
       String chars = "0123456789";
       char [] rands = new char[4]; 
       for(int i=0; i<4; i++) 
       { 
              int rand = (int)(Math.random() * 10); 
              if(i ==0 || i == 2)
              {
            	  rand = rand%3;
              }
              else
              {
            	  if(rands[i-1]== '0' && rand == 0)
            		  rand++;
              }
              rands[i] = chars.charAt(rand); 
       } 
       return rands; 
    } 
    public static void drawRands(Graphics g , char [] rands) 
    { 
       g.setColor(Color.darkGray); 
       g.setFont(new Font(null,Font.ITALIC|Font.BOLD,18)); 
       // 在不同的高度上输出验证码的每个字符          
       g.drawString("" + rands[0] + rands[1],1,18); 
//       g.drawString("" + rands[1],16,15); 
       g.drawString("" + rands[2]+ rands[3],32,18); 
//       g.drawString("" + rands[3],46,16); 
       //System.out.println(rands); 
    } 
    public static void drawBackground(Graphics g) 
	{ 
    	// 画背景 
    	g.setColor(new Color(0x87CEEB)); 
    	g.fillRect(0, 0, WIDTH, HEIGHT); 
    	// 随机产生 120 个干扰点 
    	for(int i=0; i<222; i++) 
    	{ 
    		int x = (int)(Math.random() * WIDTH); 
    		int y = (int)(Math.random() * HEIGHT); 
    		int red = (int)(Math.random() * 255); 
    		int green = (int)(Math.random() * 255); 
    		int blue = (int)(Math.random() * 255); 
    		g.setColor(new Color(red,green,blue));        
    		g.drawOval(x,y,2,0); 
    	} 
	} 
}

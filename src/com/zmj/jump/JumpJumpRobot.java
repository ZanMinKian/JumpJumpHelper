package com.zmj.jump;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class JumpJumpRobot {
	//跳一跳小人的颜色（最顶）大约在34353b左右
	private static final int jumperColorR=0x35;//容差10
	private static final int jumperColorG=0x35;//容差10
	private static final int jumperColorB=0x3e;//容差10
	
	//背景颜色
	private static final int backgroundColorR=0xd6;//容差8(大概)
	private static final int backgroundColorG=0xef;//容差10（大概）
	private static final int backgroundColorB=0xf0;//容差30（大概）
	
	//阴影颜色
	private static final int shadowColorR=0x93;//容差5
	private static final int shadowColorG=0xa6;//容差5
	private static final int shadowColorB=0xa1;//容差10
	
	//咖啡杯的颜色和jumper的颜色很像，要区别对待
	private static final int coffeeCapColorR=0x38;
	private static final int coffeeCapColorG=0x38;
	private static final int coffeeCapColorB=0x38;
	
	//跳一跳小人的高度为140px
	private static final int jumperHeight=140;
	
	//图片计数数字底部的Y坐标
	private static final int counterBottomY=200;
	//不查beginX之前的
	private static final int beginX=0;
	
	public static boolean isJumperColor(int r,int g,int b){
		boolean isCoffeeCapColor=Math.abs(r-coffeeCapColorR)+Math.abs(g-coffeeCapColorG)+Math.abs(b-coffeeCapColorB)<=3;
		if(isCoffeeCapColor){
			return false;
		}
		boolean isJumperColor=Math.abs(r-jumperColorR)+Math.abs(g-jumperColorG)+Math.abs(b-jumperColorB)<=1;//总容差小于30
		return isJumperColor;
	}
	public static boolean isBackgroundColor(int r,int g,int b){
		boolean isBackgroundColor=Math.abs(r-backgroundColorR)*Math.abs(g-backgroundColorG)*Math.abs(b-backgroundColorB)<=30*30*30;//总容差小于80
		return isBackgroundColor;
	}
	public static boolean isShadowColor(int r,int g,int b){
		boolean isShadowColor=Math.abs(r-shadowColorR)+Math.abs(g-shadowColorG)+Math.abs(b-shadowColorB)<=10;//总容差小于20
		return isShadowColor;
	}
	
	public static int[] getJumperLocation(File screenshot) throws IOException{
		BufferedImage bi=ImageIO.read(screenshot);
		int width=bi.getWidth();
		int height=bi.getHeight();
		
		int jumperLocationX=0;
		int jumperLocationY=0;
		
		loop:
		for(int y=counterBottomY;y<height;y++){
			int sum=0;
			for(int x=beginX;x<width;x++){
				Color color=new Color(bi.getRGB(x,y));
				int r=color.getRed();
				int g=color.getGreen();
				int b=color.getBlue();
				
				boolean isJumperColor=isJumperColor(r, g, b);
				if(isJumperColor){
					sum++;
				}else if(!isJumperColor&&sum!=0){
					jumperLocationY=y+jumperHeight;
					jumperLocationX=x-sum/2-1;
					break loop;
				}
			}
		}
		
		if(jumperLocationX==0&&jumperLocationY==0){
			return null;
		}else{
			return new int[]{jumperLocationX,jumperLocationY};
		}
	}
	
	//获取下一跳的顶点的位置(如果两个挨得很近，可能返回的是jumper的脑袋顶的位置)
	public static int[] getNextTopLocation(File screenshot) throws IOException{
		BufferedImage bi=ImageIO.read(screenshot);
		int width=bi.getWidth();
		int height=bi.getHeight();
		
		int nextTopLocationX=0;//下一跳的顶点的x坐标
		int nextTopLocationY=0;//下一跳的定点的y坐标
		
		loop:
		for(int y=counterBottomY;y<height;y++){
			for(int x=beginX;x<width;x++){
				Color color=new Color(bi.getRGB(x,y));
				int r=color.getRed();
				int g=color.getGreen();
				int b=color.getBlue();
				
				boolean isBackgroundColor=isBackgroundColor(r, g, b);
				boolean isShadowColor=isShadowColor(r, g, b);
				
				if(isBackgroundColor||isShadowColor){//如果是背景或阴影
					//do nothing
				}else{
					nextTopLocationX=x;
					nextTopLocationY=y;
					break loop;
				}
			}
		}
		
		if(nextTopLocationX==0&&nextTopLocationY==0){
			return null;
		}else{
			return new int[]{nextTopLocationX,nextTopLocationY};
		}
	}
	
	//获取下一跳的左边端点的位置（可能返回的是jumper脑袋左侧的位置）
	public static int[] getNextLeftLocation(File screenshot) throws IOException{
		BufferedImage bi=ImageIO.read(screenshot);
		int width=bi.getWidth();
		int height=bi.getHeight();
		
		int nextLeftLocationX=0;//下一跳的左边端点的x坐标
		int nextLeftLocationY=0;//下一跳的左边端点的y坐标
		
		int sum=0;//纵向的像素数
		
		loop:
		for(int y=counterBottomY;y<height;y++){
			for(int x=beginX;x<width;x++){
				Color color=new Color(bi.getRGB(x,y));
				int r=color.getRed();
				int g=color.getGreen();
				int b=color.getBlue();
				
				boolean isBackgroundColor=isBackgroundColor(r, g, b);
				boolean isShadowColor=isShadowColor(r, g, b);
				
				if(isBackgroundColor||isShadowColor){
					//do nothing
				}else{
					if((nextLeftLocationX==0&&nextLeftLocationY==0)||x<=nextLeftLocationX){
						if(nextLeftLocationX==x){
							sum++;
						}else{
							sum=0;
						}
						if(sum>=10){
							nextLeftLocationX=x;
							nextLeftLocationY=y-10;
							break loop;
						}
						
						nextLeftLocationX=x;
						nextLeftLocationY=y;
						break;//停止搜索这一行后面的的像素
					}else if(x>nextLeftLocationX){
						nextLeftLocationX=x;
						nextLeftLocationY=y;
						break loop;//停止搜索
					}
				}
			}
		}
		
		if(nextLeftLocationX==0&&nextLeftLocationY==0){
			return null;
		}else{
			return new int[]{nextLeftLocationX,nextLeftLocationY};
		}
	}
	
	public static double computeDistance(File screenshot) throws IOException{
		int[] jumperLocation=getJumperLocation(screenshot);
		int[] nextTopLocation=getNextTopLocation(screenshot);
		int[] nextLeftLcation=getNextLeftLocation(screenshot);
		if(Math.abs(jumperLocation[0]-nextTopLocation[0])<=12){//nextTopLocation是jumper的脑袋顶的位置
			return 170;//直接返回一个像素值（大概）
		}
		if(Math.abs(jumperLocation[0]-nextLeftLcation[0])<30){//nextLeftLocation是jumper的脑袋左侧的位置
			double distance=Math.sqrt((nextLeftLcation[0]-nextTopLocation[0])*(nextLeftLcation[0]-nextTopLocation[0])
					+(nextLeftLcation[1]-nextTopLocation[1])*(nextLeftLcation[1]-nextTopLocation[1]));
			return distance;
		}
		int[] nextCenterLocation=new int[]{nextTopLocation[0],nextLeftLcation[1]};
		
		double distance=Math.sqrt((jumperLocation[0]-nextCenterLocation[0])*(jumperLocation[0]-nextCenterLocation[0])
				+(jumperLocation[1]-nextCenterLocation[1])*(jumperLocation[1]-nextCenterLocation[1]));
		
		return distance;
	}
	
	public static int computeLongClickTime(File screenshot) throws IOException{
		int time=(int) Math.round(computeDistance(screenshot)*1.98);
		System.out.println("长按时间："+time+"毫秒");
		return time;
	}
	
	public static void processCmd(String cmd){
		Runtime run = Runtime.getRuntime();
		try {
			Process pr = run.exec(cmd);
			pr.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static void downloadScreenshot(){//path包含文件名
		String screenshotCmd="cmd /c C: && cd C:/Program Files (x86)/Nox/bin && adb shell screencap /sdcard/jump.png";
		String pullCmd="cmd /c C: && cd C:/Program Files (x86)/Nox/bin && adb pull /sdcard/jump.png D:/JumpJumpScreenshot/jump.png";
		
		processCmd(screenshotCmd);
		processCmd(pullCmd);
	}
	
	public static void main(String[] args) throws IOException {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("开始");
		
		while(true){
			downloadScreenshot();
			File screenshot=new File("D:/JumpJumpScreenshot/jump.png");
			int time=computeLongClickTime(screenshot);
			processCmd("cmd /c C: && cd C:/Program Files (x86)/Nox/bin && adb shell input touchscreen swipe 170 187 170 187 "+time);
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		
		
//		File screenshot=new File("D:/JumpJumpScreenshot/d.png");
//		try {
//			int[] jumperLocation=getNextLeftLocation(screenshot);
//			System.out.println(jumperLocation[0]+","+jumperLocation[1]);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}

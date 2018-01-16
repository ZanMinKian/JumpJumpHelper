package com.zmj.jump;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JFrame;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

public class JumpJumpHelper extends JFrame implements NativeMouseInputListener{
	private int clickCount=0;
	private int beginX=0;
	private int beginY=0;
	
	private static OutputStream os=null;
	
	public JumpJumpHelper() {
		this.setUndecorated(true);//一定要去除装饰，否则下面设置透明度会抛出异常
		this.setOpacity(0.3f);
        this.setSize(700,1050);
        this.setLocation(400,0);
        this.setVisible(true);
	}
	
	public float computeDistance(int beginX,int beginY,int endX,int endY){
		int x=beginX-endX;
		int y=beginY-endY;
		double distanceInDouble=Math.sqrt(x*x+y*y);
		return Math.round(distanceInDouble);
	}
	
	public int computeLongClickTime(float distance){//返回毫秒数
		return (int)(distance*2.76);
	}
	
	@Override
	public void nativeMousePressed(NativeMouseEvent e) {
		clickCount++;
		
		String str="Mouse clicked: " +clickCount+"\r\n";
		try {
			os.write(str.getBytes());
			os.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if(clickCount%2!=0){
			beginX=e.getX();
			beginY=e.getY();
		}
		else{
			final int time=computeLongClickTime(computeDistance(beginX,beginY,e.getX(),e.getY()));
			
			String cmd = "cmd /c C: && cd C:/Program Files (x86)/Nox/bin && adb shell input touchscreen swipe 170 187 170 187 "+time;

            Runtime run = Runtime.getRuntime();
            try {
                Process pr = run.exec(cmd);
                
                try {
        			os.write((cmd+"\r\n").getBytes());
        			os.flush();
        		} catch (IOException e1) {
        			e1.printStackTrace();
        		}
                
                pr.waitFor();
            } catch (Exception e1) {
                e1.printStackTrace();
                try {
        			os.write((e1.toString()+"\r\n").getBytes());
        			os.flush();
        		} catch (IOException e2) {
        			e2.printStackTrace();
        		}
            }
			
		}
	}
	public static void main(String[] args) throws Exception{
		File f=new File("D://a.txt");
		os=new FileOutputStream(f);
		
		JumpJumpHelper jjh=new JumpJumpHelper();
		
		Thread.sleep(3000);
		
		try {
			GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());

			System.exit(1);
		}

		// Construct the example object.
		//JumpJumpHelper jjh = new JumpJumpHelper();

		// Add the appropriate listeners.
		GlobalScreen.addNativeMouseListener(jjh);
		GlobalScreen.addNativeMouseMotionListener(jjh);
	}
	

	@Override
	public void nativeMouseClicked(NativeMouseEvent arg0) {}
	@Override
	public void nativeMouseReleased(NativeMouseEvent arg0) {}
	@Override
	public void nativeMouseDragged(NativeMouseEvent arg0) {}
	@Override
	public void nativeMouseMoved(NativeMouseEvent arg0) {}
}

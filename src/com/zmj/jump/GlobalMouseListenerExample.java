package com.zmj.jump;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

public class GlobalMouseListenerExample implements NativeMouseInputListener {
	private static OutputStream os=null;
	
	public void nativeMouseClicked(NativeMouseEvent e) {
		String str="Mouse Clicked: " + e.getClickCount()+"\r\n";
		try {
			os.write(str.getBytes());
			os.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void nativeMousePressed(NativeMouseEvent e) {
		String str="Mouse Pressed: " + e.getButton()+"\r\n";
		try {
			os.write(str.getBytes());
			os.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void nativeMouseReleased(NativeMouseEvent e) {
		String str="Mouse Released: " + e.getButton()+"\r\n";
		try {
			os.write(str.getBytes());
			os.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void nativeMouseMoved(NativeMouseEvent e) {
		//System.out.println("Mouse Moved: " + e.getX() + ", " + e.getY());
	}

	public void nativeMouseDragged(NativeMouseEvent e) {
//		String str="Mouse Dragged: " + e.getX() + ", " + e.getY()+"\r\n";
//		try {
//			os.write(str.getBytes());
//			os.flush();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
	}

	
	
	
	public static void main(String[] args) throws Exception {
		File f=new File("D://a.txt");
		os=new FileOutputStream(f);
		

		try {
			GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());

			System.exit(1);
		}
		
		
		// Construct the example object.
		GlobalMouseListenerExample example = new GlobalMouseListenerExample();

		// Add the appropriate listeners.
		GlobalScreen.addNativeMouseListener(example);
		GlobalScreen.addNativeMouseMotionListener(example);

	}
}


//public class GlobalMouseListenerExample{
//	public static void main(String[] args) throws Exception{
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				
//				Robot r=null;
//				try {
//					r = new Robot();
//				} catch (AWTException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				r.mouseMove(20,1060);
//				
//				r.mousePress(InputEvent.BUTTON1_MASK);
//				r.mouseRelease(InputEvent.BUTTON1_MASK);
//			}
//		}).start();
//		
//		
//	}
//}




/*
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

public class GlobalMouseListenerExample implements NativeMouseInputListener {
	public void nativeMouseClicked(NativeMouseEvent e) {
		System.out.println("Mouse Clicked: " + e.getClickCount());
	}

	public void nativeMousePressed(NativeMouseEvent e) {
		System.out.println("Mouse Pressed: " + e.getButton());
	}

	public void nativeMouseReleased(NativeMouseEvent e) {
		System.out.println("Mouse Released: " + e.getButton());
	}

	public void nativeMouseMoved(NativeMouseEvent e) {
		System.out.println("Mouse Moved: " + e.getX() + ", " + e.getY());
	}

	public void nativeMouseDragged(NativeMouseEvent e) {
		System.out.println("Mouse Dragged: " + e.getX() + ", " + e.getY());
	}

	public static void main(String[] args) {
		try {
			GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());

			System.exit(1);
		}

		// Construct the example object.
		GlobalMouseListenerExample example = new GlobalMouseListenerExample();

		// Add the appropriate listeners.
		GlobalScreen.addNativeMouseListener(example);
		GlobalScreen.addNativeMouseMotionListener(example);
	}
}
*/
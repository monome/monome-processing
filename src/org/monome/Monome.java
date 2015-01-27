package org.monome;

import java.util.HashMap;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import processing.core.*;
import oscP5.*;
import netP5.*;

public class Monome {

	protected PApplet parent;	
	private NetAddress serialoscAddress;
	private OscP5 osc;
	private String listenAddress = "127.0.0.1";
	private int listenPort = (int) Math.floor(Math.random() * 64512) + 1024;
	private String remoteAddress = "127.0.0.1";
	private int remotePort = 12002;
	private String activeId;
	private Device activeDevice;
	private Method pressMethod;
	
	private HashMap<String, Device> devices = new HashMap<String, Device>();
	
	public Monome(PApplet parent) {
		this(parent, "first_device");
	}
	
	public Monome(PApplet parent, String activeId) {
		this.parent = parent;
		this.activeId = activeId;
		serialoscAddress = new NetAddress(remoteAddress, remotePort);
		osc = new OscP5(this, listenPort);
		listenAddress = osc.ip();
		discover();
		addNotifyListener();
		Class args[] = new Class[] {int.class, int.class, int.class};
		try {
			pressMethod = parent.getClass().getDeclaredMethod("press", args);
		} catch (NoSuchMethodException e) {
			System.out.println("warning: press(int, int, int) method not defined");
		}
	}
		
	public void oscEvent(OscMessage msg) {
		if (msg.addrPattern().equals("/serialosc/device")) {
			handleDeviceMessage(msg);
		}
		if (msg.addrPattern().equals("/serialosc/add")) {
			discover();
			addNotifyListener();
		}
		if (msg.addrPattern().equals("/serialosc/remove")) {
			discover();
			addNotifyListener();
		}
	}
		
	private void handleDeviceMessage(OscMessage msg) {
		String id = msg.get(0).stringValue();
		if (activeId.equals("first_device")) {
			activeId = id;
		}
		String type = msg.get(1).stringValue();
		int port = msg.get(2).intValue();
		Device device = devices.get(id);
		if (device == null) {
			device = new Device(id, type, port);
			devices.put(id, device);
		} else {
			device.type = type;
			device.port = port;
		}
		device.getInfo();
		if (id.equals(activeId)) { 
			if (activeDevice != null) {
				activeDevice.pressListener = null;
			}
			activeDevice = device;
			activeDevice.pressListener = this;
		}
	}

	private void discover() {
		OscMessage msg = new OscMessage("/serialosc/list");
		msg.add(listenAddress);
		msg.add(listenPort);
		osc.send(msg, serialoscAddress);
	}
	
	private void addNotifyListener() {
		OscMessage msg = new OscMessage("/serialosc/notify");
		msg.add(listenAddress);
		msg.add(listenPort);
		osc.send(msg, serialoscAddress);		
	}
	
	public void refresh(int[][] led) {
		if (activeDevice == null) {
			return;
		}
		if (activeDevice.varibright) {
			activeDevice.levelMap(led);
		} else {
			activeDevice.map(led);
		}
	}
	
	public void onPress(Object[] args) {
		if (pressMethod == null) {
			return;
		}
		try {
			pressMethod.invoke(parent, args);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
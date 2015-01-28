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
	private Method keyMethod;
	private Method deltaMethod;
	
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
		if (!id.equals(activeId)) {
			return;
		}
		String type = msg.get(1).stringValue();
		int port = msg.get(2).intValue();
		Device device = devices.get(id);
		if (device == null) {
			if (type.contains("monome arc")) {
				String[] pieces = type.split(" ");
				int encoders = Integer.parseInt(pieces[2]);
				device = new Arc(id, type, port, encoders);
				device.deltaListener = this;
				addArcCallbacks();
			} else {
				device = new Grid(id, type, port);
				addGridCallbacks();
			}
			device.keyListener = this;
			devices.put(id, device);
		} else {
			device.type = type;
			device.port = port;
		}
		device.initDevice();
		activeDevice = device;
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
	
	public void refresh(int encoder, int[] led) {
		if (activeDevice == null) {
			return;
		}
		activeDevice.levelMap(encoder, led);
	}
	
	private void addGridCallbacks() {
		Class args[] = new Class[] {int.class, int.class, int.class};
		try {
			keyMethod = parent.getClass().getDeclaredMethod("key", args);
		} catch (NoSuchMethodException e) {
		}
	}
	
	private void addArcCallbacks() {
		Class args[] = new Class[] {int.class, int.class};
		try {
			keyMethod = parent.getClass().getDeclaredMethod("key", args);
		} catch (NoSuchMethodException e) {
		}
		try {
			deltaMethod = parent.getClass().getDeclaredMethod("delta", args);
		} catch (NoSuchMethodException e) {
		}
	}
	
	public void onKey(Object[] args) {
		if (keyMethod == null) {
			return;
		}
		try {
			keyMethod.invoke(parent, args);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
		
	public void onDelta(Object[] args) {
		if (deltaMethod == null) {
			return;
		}
		try {
			deltaMethod.invoke(parent, args);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
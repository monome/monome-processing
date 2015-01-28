package org.monome;

import oscP5.*;
import netP5.*;

public abstract class Device {
	public OscP5 osc;
	public NetAddress deviceAddress;
	private String listenAddress = "127.0.0.1";
	private int listenPort = (int) Math.floor(Math.random() * 64512) + 1024;
	private String remoteAddress = "127.0.0.1";

	public String id;
	public String type;
	public int port;
	public int width;
	public int height;
	public boolean varibright = false;
	
	public Monome keyListener;
	public Monome deltaListener;
	
	public Device(String id, String type, int port) {
		this.id = id;
		this.type = type;
		this.port = port;
		deviceAddress = new NetAddress(remoteAddress, port);
		osc = new OscP5(this, listenPort);
		listenAddress = osc.ip();
		initDevice();
	}
	
	public void initDevice() {
		OscMessage msg = new OscMessage("/sys/port");
		msg.add(listenPort);
		osc.send(msg, deviceAddress);
		
		msg = new OscMessage("/sys/host");
		msg.add(listenAddress);
		osc.send(msg, deviceAddress);

		msg = new OscMessage("/sys/prefix");
		msg.add("/monome");
		osc.send(msg, deviceAddress);
		
		msg = new OscMessage("/sys/info");
		osc.send(msg, deviceAddress);
	}
	
	public abstract void levelMap(int[][] led);
	
	public abstract void map(int[][] led);
	
	public abstract void levelMap(int encoder, int[] led);
		
}

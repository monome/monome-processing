package org.monome;

import oscP5.*;
import netP5.*;
import processing.core.*;

public class Device {
	private OscP5 osc;
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
	
	public Monome pressListener;
	
	public Device(String id, String type, int port) {
		this.id = id;
		this.type = type;
		this.port = port;
		deviceAddress = new NetAddress(remoteAddress, port);
		osc = new OscP5(this, listenPort);
		listenAddress = osc.ip();
		initDevice();
		getInfo();
	}
	
	public void oscEvent(OscMessage msg) {
		if (msg.addrPattern().equals("/sys/size")) {
			width = msg.get(0).intValue();
			height = msg.get(1).intValue();
		}
		if (msg.addrPattern().equals("/sys/id")) {
			String id = msg.get(0).stringValue();
			String pattern = "m\\d{7}";
			if (id.matches(pattern)) {
				varibright = true;
			}
		}
		if (msg.addrPattern().equals("/monome/grid/key")) {
			if (pressListener == null) {
				return;
			}
			Object[] args = new Object[3];
			args[0] = msg.get(0).intValue();
			args[1] = msg.get(1).intValue();
			args[2] = msg.get(2).intValue();
			pressListener.onPress(args);
		}
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
	}
	
	public void getInfo() {
		OscMessage msg = new OscMessage("/sys/info");
		osc.send(msg, deviceAddress);
	}
	
	public void map(int[][] led) {
		for (int xOffset = 0; xOffset < width / 8; xOffset++) {
			for (int yOffset = 0; yOffset < height / 8; yOffset++) {
				OscMessage msg = new OscMessage("/monome/grid/led/map");
				msg.add(xOffset * 8);
				msg.add(yOffset * 8);
				for (int y = 0; y < 8; y++) {
					int row = 0;
					int iY = yOffset * 8 + y;
					for (int x = 0; x < 8; x++) {
						int iX = xOffset * 8 + x;
						if (led[iY][iX] > 0) {
							row += 1 << x;
						}
					}
					msg.add(row);
				}
				osc.send(msg, deviceAddress);
			}
		}
	}
	
	public void levelMap(int[][] led) {
		for (int xOffset = 0; xOffset < width / 8; xOffset++) {
			for (int yOffset = 0; yOffset < height / 8; yOffset++) {
				OscMessage msg = new OscMessage("/monome/grid/led/level/map");
				msg.add(xOffset * 8);
				msg.add(yOffset * 8);
				for (int y = 0; y < 8; y++) {
					int iY = yOffset * 8 + y;
					for (int x = 0; x < 8; x++) {
						int iX = xOffset * 8 + x;
						msg.add(led[iY][iX]);						
					}
				}
				osc.send(msg, deviceAddress);
			}
		}
	}
}

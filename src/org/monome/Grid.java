package org.monome;

import oscP5.*;

public class Grid extends Device {
		
	public Grid(String id, String type, int port) {
		super(id, type, port);
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
			if (keyListener == null) {
				return;
			}
			Object[] args = new Object[3];
			args[0] = msg.get(0).intValue();
			args[1] = msg.get(1).intValue();
			args[2] = msg.get(2).intValue();
			keyListener.onKey(args);
		}
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
					if (iY >= led.length) {
						return;
					}
					for (int x = 0; x < 8; x++) {
						int iX = xOffset * 8 + x;
						if (iX >= led[iY].length) {
							return;
						}
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
				String args = "";
				for (int y = 0; y < 8; y++) {
					int iY = yOffset * 8 + y;
					for (int x = 0; x < 8; x++) {
						int iX = xOffset * 8 + x;
						if (iY >= led.length) {
							return;
						}
						if (iX >= led[iY].length) {
							return;
						}
						msg.add(led[iY][iX]);
						args += led[iY][iX];
					}
				}
//				System.out.print("/monome/grid/led/level/map ");
//				System.out.print(xOffset * 8 + " " + yOffset * 8 + " ");
//				System.out.println(args);
//				System.out.println(args.length());
				osc.send(msg, deviceAddress);
			}
		}
	}

	@Override
	public void levelMap(int encoder, int[] led) {
		System.out.println("warning: calling arc map on grid device");		
	}

}

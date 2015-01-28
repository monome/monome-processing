package org.monome;

import oscP5.OscMessage;

public class Arc extends Device {

	public int encoders = 0;
	
	public Arc(String id, String type, int port, int encoders) {
		super(id, type, port);
		this.encoders = encoders;
	}
	
	public void oscEvent(OscMessage msg) {
		if (msg.addrPattern().equals("/monome/enc/key")) {
			if (keyListener == null) {
				return;
			}
			Object[] args = new Object[2];
			args[0] = msg.get(0).intValue();
			args[1] = msg.get(1).intValue();
			keyListener.onKey(args);
		}
		if (msg.addrPattern().equals("/monome/enc/delta")) {
			if (deltaListener == null) {
				return;
			}
			Object[] args = new Object[2];
			args[0] = msg.get(0).intValue();
			args[1] = msg.get(1).intValue();
			deltaListener.onDelta(args);
		}
	}

	@Override
	public void levelMap(int[][] led) {
		System.out.println("warning: calling grid map on arc device");
	}

	@Override
	public void map(int[][] led) {
		System.out.println("warning: calling grid map on arc device");
	}

	@Override
	public void levelMap(int encoder, int[] led) {
		OscMessage msg = new OscMessage("/monome/ring/map");
		msg.add(encoder);
		for (int i = 0; i < led.length; i++) {
			msg.add(led[i]);
		}
		osc.send(msg, deviceAddress);
	}

}

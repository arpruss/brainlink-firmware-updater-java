/**
*
* Copyright (c) 2014 Alexander Pruss
* Distributed under the GNU GPL v3 or later. For full terms see the file COPYING.
*
*/

package mobi.omegacentauri.BrainlinkFirmwareUploader;

import java.util.Arrays;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

public class SerialDataLink extends DataLink {
	private SerialPort p;

	public SerialDataLink(String port) throws Exception {
		p = new SerialPort(port);
		
		int busyTries = 4;

		while (busyTries-- > 0 && ! p.isOpened()) {
			try {
				p.openPort();
			}
			catch (SerialPortException e) {
				if (busyTries <= 0 || ! e.getExceptionType().equals(SerialPortException.TYPE_PORT_BUSY)) 
					throw e;
			}
		}
		
		p.setParams(115200, 8, 1, 0);
	}

	public void start(int baud) {
		setBaud(baud);
	}

	private void setBaud(int baud) {
		try {
			p.setParams(baud,  8,  1, 0);
		} catch (SerialPortException e) {
		}
	}

	public void stop() {
		try {
			p.closePort();
		} catch (SerialPortException e) {
		}
	}

	@Override
	public byte[] receiveBytes() {
		try {
			byte[] data = p.readBytes();
			if (data != null)
				return data;
		} catch (SerialPortException e) {
		}

		return null;
	}

//	private int scaleTimeout(int timeout) {
//		return timeout * 9600 / baud;
	   
	 
//	}
	
	@Override
	public void transmit(byte... data) {
		try {
//			Butterfly.dump(data);
			p.writeBytes(data);
		} catch (SerialPortException e) {
		}
	}

	@Override
	public void clearBuffer() {
		try {
			p.readBytes();
		} catch (SerialPortException e) {
		}
	}

	@Override
	public int getFixedBaud() {
		return 0;
	}

	@Override
	public boolean readBytes(byte[] data, int timeout) {
		byte[] in;
		try {
			in = p.readBytes(data.length, timeout);
		} catch (SerialPortException | SerialPortTimeoutException e) {
			return false;
		}
		if (in.length < data.length)
			return false;
		System.arraycopy(in, 0, data, 0, data.length);
		return true;
	}
}

package mobi.omegacentauri.BrainlinkFirmwareUploader;

import java.io.FileInputStream;
import java.util.Arrays;

public class Uploader {
	public static void main(String[] args) throws Exception {
		System.out.println("Connecting");
		SerialDataLink sdl = new SerialDataLink("COM5"); // MODIFY
		sdl.start(115200);
		System.out.println("Starting");
		Butterfly butterfly = new Butterfly(sdl, 16384, 128);
		byte[] hex = butterfly.getFlashFromHex(new FileInputStream("c:/devel/brainlink/custom-fw/mainFirmware.hex")); // MODIFY
		if (hex == null)
			throw new Exception("Cannot parse hex file");
		if (! butterfly.start()) 
			throw new Exception("Cannot start");
		System.out.println("Started");
		System.out.println("Writing hex");
		if (!butterfly.writeFlash(hex)) {
			System.out.println("Error writing");
		}
		else {
			System.out.println("Verifying");
			byte[] flash = butterfly.readFlash();
			if (flash == null)
				throw new Exception("Cannot read");
			System.out.println("Verification: "+Arrays.equals(hex, flash));
		}
		butterfly.stop();
		sdl.stop();
	}
}

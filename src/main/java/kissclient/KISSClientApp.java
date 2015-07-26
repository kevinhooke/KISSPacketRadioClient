package kissclient;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class KISSClientApp {

	public static void main(String[] args) {
		new KISSClientApp().connect();
	}

	public void connect() {
		try {
			Socket echoSocket = new Socket("192.168.1.203", 8001);
			PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
					true);

			InputStream r = echoSocket.getInputStream();

			int bytesRead = 0;
			byte[] chars = new byte[1024];

			while (bytesRead != -1) {
				bytesRead = r.read(chars);
				
				//FEND
				System.out.print(this.identifyChar(chars[0]));
				//DATA
				System.out.print(this.identifyChar(chars[1]));
				
				//2-8 (7 bytes, source callsign bitshifted left 1?)
				byte[] source = new byte[7];
				System.arraycopy(chars, 2, source, 0, 7);
				System.out.print(this.decodeCallsign(source));
				
				
				for (int i=0; i < bytesRead; i++) {
					System.out.print(this.identifyChar(chars[i]));
				}
				System.out.println();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String decodeCallsign(byte[] source) {
		StringBuilder sb = new StringBuilder();
		
		return sb.toString();
	}

	String identifyChar(byte raw) {
		String result = null;

		// if char is a printable character: between " " and "~"
		if (raw >= (byte) 0x20 && raw <= (byte) 0x7E) {
			result = new String(Character.toChars(raw));
		} else {
			//KISS control codes: https://en.wikipedia.org/wiki/KISS_(TNC)
			switch (raw) {

			case ((byte) 0xC0): {
				result = " [FEND] ";
				break;
			}

			case ((byte) 0x00): {
				result = " [DATA] ";
				break;
			}

			case ((byte) 0x03): {
				result = " [UI] ";
				break;
			}
			case ((byte) 0xF0): {
				result = " [no_L3] ";
				break;
			}

			
			default: {
				result = String.format("%02X", raw) + " [?] ";
				break;
			}
			}
		}
		return result;
	}
}

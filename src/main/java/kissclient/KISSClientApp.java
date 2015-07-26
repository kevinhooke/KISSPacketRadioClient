package kissclient;

import java.io.InputStream;
import java.io.InputStreamReader;
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

//			InputStreamReader reader = new InputStreamReader(r);
//			char[] chars = new char[1024];
			byte[] chars = new byte[1024];
			int bytesRead = 0;
			
			int charsInPacket = 0;
			while (bytesRead != -1) {
				//bytesRead = reader.read(chars);
				bytesRead = r.read(chars);
				//System.out.println("raw: " + chars + "\n");
				//byte 1 expected as 0x0C = FEND
				System.out.print(this.identifyChar(chars[0]));
				//byte 2 expected as 0x00 = DATA
				System.out.print(this.identifyChar(chars[1]));
				
				//2-8 (7 bytes, source callsign bitshifted left 1, bitshift right to decode)
				//char[] source = new char[7];
				byte[] source = new byte[7];
				
				System.arraycopy(chars, 2, source, 0, 7);
				System.out.print(" Dest:" + this.decodeCallsign(source) + " ");
				
				
				for (int i=0; i < bytesRead; i++) {
					System.out.print(this.identifyChar(chars[i]));
				}
				System.out.println();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Bitshift right each hex value to get back to ASCII value for each character of callsign. 
	 * Converts each ASCII value back to character and appends to result buffer
	 * and returns as result.
	 * 
	 * Reference: http://www.tapr.org/pub_ax25.html section 2.2.13
	 * 
	 * Examples of parsing: 
	 * https://github.com/chrissnell/GoBalloon/blob/master/ax25/decoder.go
	 * 
	 * Address handling for AX.25 in this doc is very useful: http://tnc-x.com/dcc.doc
	 * 
	 * Converting unsigned byte value to unsigned in abs value in Java:
	 * http://stackoverflow.com/questions/4266756/can-we-make-unsigned-byte-in-java
	 * 
	 * @param source
	 * @return
	 */
	String decodeCallsign(byte[] source) {
		StringBuilder sb = new StringBuilder();
		
		for(byte next : source){
			int unsignedInt = next & 0xFF;
			int shifted = unsignedInt >>> 1; //unsigned right shift
			System.out.print("[byte:" + String.format("%02X", next) 
					+ "/int:" + unsignedInt + "/shifted:" 
					+ shifted); // + ",char: " + c + "]");
//			System.out.print("[source: " + next + " int: " + Integer.valueOf(next)
//					+ ", shifted: " + shifted + " int: " + Integer.valueOf(shifted) + "]");

			sb.append(new String(Character.toChars(shifted)));
		}
		
		return sb.toString();
	}

	String identifyChar(byte raw) {
		String result = null;

		// if char is a printable character: between " " and "~"
		if (raw >= 0x20 && raw <= 0x7E) {
			result = new String(Character.toChars(raw));
		} else {
			//KISS control codes: https://en.wikipedia.org/wiki/KISS_(TNC)
			switch (raw) {

			case ((byte)0xC0): {
				result = " [FEND] ";
				break;
			}

			case ((byte)0x00): {
				result = " [DATA] ";
				break;
			}

			case ((byte)0x03): {
				result = " [UI] ";
				break;
			}
			case ((byte)0xF0): {
				result = " [NO-L3] ";
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

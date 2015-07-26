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

			byte[] chars = new byte[1024];
			int bytesRead = 0;
			
			while (bytesRead != -1) {
				bytesRead = r.read(chars);
				
				//byte 1 expected as 0x0C = FEND
				System.out.print(this.identifyChar(chars[0]) + " ");

				//byte 2 expected as 0x00 = DATA
				System.out.print(this.identifyChar(chars[1]) + " ");
				
				//2-8 (7 bytes, dest callsign - bitshift right to decode)
				byte[] dest = new byte[7];
				System.arraycopy(chars, 2, dest, 0, 7);
				System.out.print(" Dest:" + this.decodeAX25Callsign(dest) + " ");

				//9-15 (7 bytes, source callsign - bitshift right to decode)
				byte[] source = new byte[7];
				System.arraycopy(chars, 9, source, 0, 7);
				System.out.print(" Source:" + this.decodeAX25Callsign(source) + " ");
				
				//
				//TODO - in progress
				//
				int currentPos = 2 + 7 + 7;
				int callsignLength = 7;
				int callsignsInRepeaterList = 0;
				byte nextChar = chars[currentPos];
				
				KISSControlCode nextControl = this.identifyChar(nextChar);
				//loop reading callsigns until UI control frame reached (indicates
				//end of digipeater callsign list)
				while(nextControl != KISSControlCode.UI){
					if(callsignsInRepeaterList == 0){
						System.out.print("Via:");
					}
					byte[] repeat = new byte[7];	
					System.arraycopy(chars, currentPos, repeat, 0, 7);
					System.out.print(this.decodeAX25Callsign(repeat));
					callsignsInRepeaterList++;
					currentPos += callsignLength;
					nextChar = chars[currentPos];
					nextControl = this.identifyChar(nextChar);
					//if next char is not a [UI] control char then we have more digipeater
					//callsigns to read
					if(nextControl != KISSControlCode.UI){
						System.out.print(",");
					}
				}
				
				for (int i=currentPos; i < bytesRead; i++) {
					if( this.identifyChar(chars[i]) == KISSControlCode.PRINTABLE){
						System.out.print(this.convertHexCodeToPrintableString(chars[i]));
					}
					else{
						System.out.print(" " + String.format("%02X", chars[i]) + "[?]");	
					}
					
				}
				System.out.println();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	String convertHexCodeToPrintableString(byte in){
		return new String(Character.toChars((char)in));
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
	 * Converting unsigned byte value to unsigned int abs value in Java (avoids issue
	 * that bytes are signed in Java):
	 * http://stackoverflow.com/questions/4266756/can-we-make-unsigned-byte-in-java
	 * 
	 * @param source
	 * @return
	 */
	String decodeAX25Callsign(byte[] source) {
		StringBuilder sb = new StringBuilder();
		
		for(byte next : source){
			int unsignedInt = next & 0xFF;
			int shifted = unsignedInt >>> 1; //unsigned right shift
//			System.out.print("[byte:" + String.format("%02X", next) 
//					+ "/int:" + unsignedInt + "/shifted:" 
//					+ shifted); // + ",char: " + c + "]");

			sb.append(new String(Character.toChars(shifted)));
		}
		
		return sb.toString();
	}

	KISSControlCode identifyChar(byte raw) {
		KISSControlCode result;

		// if char is a printable character: between " " (0x20) and "~" (0x7E)
		if (raw >= 0x20 && raw <= 0x7E) {
			result = KISSControlCode.PRINTABLE;
		} else {
			//KISS control codes: https://en.wikipedia.org/wiki/KISS_(TNC)
			switch (raw) {

			case ((byte)0xC0): {
				result = KISSControlCode.FEND;
				break;
			}

			case ((byte)0x00): {
				result = KISSControlCode.DATA;
				break;
			}

			case ((byte)0x03): {
				result = KISSControlCode.UI;
				break;
			}
			case ((byte)0xF0): {
				result = KISSControlCode.NO_L3;
				break;
			}

			
			default: {
				result = KISSControlCode.UNKNOWN;
				break;
			}
			}
		}
		return result;
	}
}

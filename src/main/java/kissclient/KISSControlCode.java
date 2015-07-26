package kissclient;

public enum KISSControlCode {

	PRINTABLE((byte)0xFF), //KH high value to represent non control char, printable char
	UNKNOWN((byte)0xFE),	//KH unknown value 
	FEND((byte)0xC0),
	DATA((byte)0x00),
	UI((byte)0x03),
	NO_L3((byte)0xF0);
	
	KISSControlCode(byte value){
		this.value = value;
	}
	
	byte value;

	public byte getValue() {
		return value;
	}

	public void setValue(byte value) {
		this.value = value;
	}
	
}

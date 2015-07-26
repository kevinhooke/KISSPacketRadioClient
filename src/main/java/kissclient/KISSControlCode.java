package kissclient;

public enum KISSControlCode {

	
	FEND((byte)0xC0),
	DATA((byte)0x00);
	
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

package kissclient;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class KISSClientAppTest {

	private KISSClientApp app = new KISSClientApp();
	
	@Test
	public void test_SP(){
		KISSControlCode result = this.app.identifyChar((byte)0x20);
		System.out.println(result);
		
		assertTrue(result.equals(" "));
	}
	
	@Test
	public void test_A(){
		KISSControlCode result = this.app.identifyChar((byte)0x41);
		System.out.println(result);
		assertTrue(result.equals(KISSControlCode.PRINTABLE));
	}

	@Test
	public void test_Z(){
		KISSControlCode result = this.app.identifyChar((byte)0x5A);
		System.out.println(result);
		assertTrue(result.equals("Z"));
	}


}

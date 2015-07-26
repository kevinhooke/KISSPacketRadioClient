package kissclient;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class KISSClientAppTest {

	private KISSClientApp app = new KISSClientApp();

	@Test
	public void test_SP(){
		String result = this.app.identifyChar((byte)0x20);
		System.out.println(result);
		assertTrue(result.equals(" "));
	}
	
	@Test
	public void test_A(){
		String result = this.app.identifyChar((byte)0x41);
		System.out.println(result);
		assertTrue(result.equals("A"));
	}

	@Test
	public void test_Z(){
		String result = this.app.identifyChar((byte)0x5A);
		System.out.println(result);
		assertTrue(result.equals("Z"));
	}


}

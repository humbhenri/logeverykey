package logeverykey;

import static org.junit.Assert.*;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.junit.Test;

public class KeyCodeTester {

	@Test
	public void insertLetter() {
		int keycode = NativeKeyEvent.VC_A;
		assertEquals("A", KeyCode.decode(keycode));
	}
	
	@Test
	public void insertNewLine() {
		int keycode = NativeKeyEvent.VC_ENTER;
		assertEquals(System.lineSeparator(), KeyCode.decode(keycode));
	}
	
	@Test
	public void insertNumber() {
		int keycode = NativeKeyEvent.VC_0;
		assertEquals("0", KeyCode.decode(keycode));
	}
	
	@Test
	public void printables() {
		int keys[] = {NativeKeyEvent.VC_SPACE, NativeKeyEvent.VC_OPEN_BRACKET, NativeKeyEvent.VC_COMMA};
		StringBuilder decode  = new StringBuilder();
		for (int k : keys) {
			decode.append(KeyCode.decode(k));
		}
		assertEquals(" [,", decode.toString());
	}
}

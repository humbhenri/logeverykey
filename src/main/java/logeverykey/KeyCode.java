package logeverykey;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jnativehook.keyboard.NativeKeyEvent.*;

public class KeyCode {

	private static final Map<Integer, String> KEYS = new HashMap<Integer, String>();
	static {
		KEYS.put(VC_ENTER, System.lineSeparator());
		KEYS.put(VC_SPACE, " ");
		KEYS.put(VC_CLOSE_BRACKET, "]");
		KEYS.put(VC_OPEN_BRACKET, "[");
		KEYS.put(VC_COMMA, ",");
	}
	
	private static final List<Integer> PRINTABLES = Arrays.asList(
		VC_BACK_SLASH
		, VC_BACKQUOTE
	);

	public static String decode(int keycode) {
		if (isPrintable(keycode)) {
			return getKeyText(keycode);
		}
		String val = KEYS.get(keycode);
		return val == null ? "" : val;
	}

	private static boolean isPrintable(int keycode) {
		return (keycode >= VC_A && keycode <= VC_Z) 
				|| (keycode >= VC_1 && keycode <= VC_0)
				|| PRINTABLES.contains(Integer.valueOf(keycode));
	}
}

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

public class BoardConstructor {
	
	public interface User32 extends StdCallLibrary {
		User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);
		
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}

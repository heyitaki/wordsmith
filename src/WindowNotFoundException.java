@SuppressWarnings("serial")
public class WindowNotFoundException extends Exception{
	public WindowNotFoundException(String className, String windowName) {
		super(className + " named " + windowName + " not found.");
	}
}
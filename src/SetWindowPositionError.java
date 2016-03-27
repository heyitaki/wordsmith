@SuppressWarnings("serial")
public class SetWindowPositionError extends Exception{
	public SetWindowPositionError(String windowName) {
		super("Could not resize/move window named " + windowName + ".");
	}
}
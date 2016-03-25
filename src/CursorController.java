import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

public class CursorController {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Robot test = new Robot();
			test.mouseMove(1400, 400);
			test.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			test.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			test.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			test.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
			Thread.sleep(1000);
			test.mouseMove(500, 400);
			test.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			test.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		} catch (AWTException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}

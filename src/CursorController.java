import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.ArrayList;

public class CursorController {
	
	public static Coordinate[][] cursorPositions;
	
	public static void initCursorPositions() {
		cursorPositions = new Coordinate[Player.BOARD_HEIGHT][Player.BOARD_WIDTH];
		Coordinate start = new Coordinate(995, 297);
		Coordinate curr;
		for(int i = 0; i < Player.BOARD_HEIGHT; i++) {
			for(int j = 0; j < Player.BOARD_WIDTH; j++) {
				curr = new Coordinate(start.x + i*BoardConstructor.TILE_OFFSET, 
						start.y + j*BoardConstructor.TILE_OFFSET);
				cursorPositions[i][j] = curr;
			}
		}
	}
	
	public static int[] swipeWord(String input) throws AWTException, InterruptedException {
		Robot robot = new Robot();
		
		double dx, dy, dt;
		double stepSize = 10;
		double swipeTime = 100;
		
		ArrayList<Coordinate> cursorPath = Player.getCursorPath(input);
		Coordinate curr = cursorPath.get(0);
		robot.mouseMove(curr.x, curr.y);
		Coordinate prev = new Coordinate(curr.x, curr.y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		for(int i = 1; i < cursorPath.size(); i++) {
			curr = cursorPath.get(i);
			
			dx = (curr.x - prev.x) / stepSize;
			dy = (curr.y - prev.y) / stepSize;
			dt = swipeTime / stepSize;
			
			for(int j = 1; j <= stepSize; j++) {
				Thread.sleep((int)dt);
				robot.mouseMove((int)(prev.x + dx*j), (int)(prev.y + dy*j));
			}
			
			robot.mouseMove(curr.x, curr.y);
			prev = new Coordinate(curr.x, curr.y);
		}
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseMove(prev.x - 40, prev.y + 40);
		Thread.sleep(75);
		Color swipeColor = robot.getPixelColor(prev.x - 40, prev.y + 40);
		return new int[]{swipeColor.getRed(), swipeColor.getGreen(), swipeColor.getBlue()};
	}
	
	/*public static int[] getSwipeColor(Coordinate pixel) throws AWTException {
		Robot robot = new Robot();
		robot.mouseMove(pixel.x - 30, pixel.y + 30);
		Color swipeColor = robot.getPixelColor(pixel.x - 30, pixel.y + 30);
		return new int[]{swipeColor.getRed(), swipeColor.getGreen(), swipeColor.getBlue()};
	}*/
}

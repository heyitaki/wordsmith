import java.awt.AWTException;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.StdCallLibrary;

public class BoardConstructor {
	public static final int DEFAULT_WINDOW_X = 900;
	public static final int DEFAULT_WINDOW_Y = 30;
	public static final int DEFAULT_WINDOW_WIDTH = 1000;
	public static final int DEFAULT_WINDOW_HEIGHT = 550;
	
	public static final int BOARD_OFFSET_X  = 30;
	public static final int BOARD_OFFSET_Y = 202;
	
	public static final int TILE_HEIGHT = 129;
	public static final int TILE_OFFSET = 149;
	
	public interface User32 extends StdCallLibrary {
		User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);
		
		HWND FindWindowA(String className, String windowName);
		
		boolean SetWindowPos(HWND hWnd, HWND hWndInsertAfter, int X, int Y, int cx, int cy, int uFlags);
	}
	
	public static void setDimensions(String className, String windowName) throws SetWindowPositionError, 
			WindowNotFoundException {
		HWND handle = User32.INSTANCE.FindWindowA(className, windowName);
		if(handle == null) {
			throw new WindowNotFoundException(className, windowName);
		}
		
		//TODO: generalize sizing presets
		boolean success = User32.INSTANCE.SetWindowPos(handle, null, DEFAULT_WINDOW_X, 
				DEFAULT_WINDOW_Y, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT, 0x0000);
		if(success == false) {
			throw new SetWindowPositionError(windowName);
		}
	}
	
	public static BufferedImage[] getTiles() throws AWTException {
		int numTiles = Player.BOARD_HEIGHT * Player.BOARD_WIDTH;
		BufferedImage[] images = new BufferedImage[numTiles];
		Rectangle tile;
		Robot robot = new Robot();
		BufferedImage image;
		int x = DEFAULT_WINDOW_X + BOARD_OFFSET_X;
		int y = DEFAULT_WINDOW_Y + BOARD_OFFSET_Y;
		for(int i = 0; i < Player.BOARD_HEIGHT; i++) {
			for(int j = 0; j < Player.BOARD_WIDTH; j++) {
				tile = new Rectangle(x + TILE_OFFSET * j, y + TILE_OFFSET * i, TILE_HEIGHT, TILE_HEIGHT);	
				image = robot.createScreenCapture(tile);
				images[i*Player.BOARD_HEIGHT+j] = image;
			}
		}
		return images;
	}
	
	public static String getGameState() throws AWTException {
		Robot robot = new Robot();
		Color pixel = robot.getPixelColor(943, 489);
		int red = pixel.getRed();
		int green = pixel.getGreen();
		int blue = pixel.getBlue();
		if(230 <= red && red <= 250 && 140 <= green && green <= 160 && 0 <= blue && blue <= 20) {
			return "ready";
		} else if(210 <= red && red <= 255 && 0 <= green && green <= 10 && 0 <= blue && blue <= 10) {
			return "guessing";
		} else if(0 <= red && red <= 10 && 70 <= green && green <= 90 && 145 <= blue && blue <= 165) {
			return "score";
		} else {
			return "none";
		}
	}
	
	@SuppressWarnings("serial")
	public static class WindowNotFoundException extends Exception{
		public WindowNotFoundException(String className, String windowName) {
			super(className + " named " + windowName + " not found.");
		}
	}
	
	@SuppressWarnings("serial")
	public static class SetWindowPositionError extends Exception{
		public SetWindowPositionError(String windowName) {
			super("Could not resize/move window named " + windowName + ".");
		}
	}
	
	public static void main(String[] args) {
		//courtesy of winspy++
		String windowName = "Wordament";
		String className = "ApplicationFrameWindow";
		try {
			BoardConstructor.setDimensions(className, windowName);
			BufferedImage[] images = BoardConstructor.getTiles();
			JFrame frame = new JFrame();
			frame.getContentPane().setLayout(new FlowLayout());
			for(int i=0; i <16;i++) {
				frame.getContentPane().add(new JLabel(new ImageIcon(images[i])));
			}
			frame.pack();
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			PointerInfo a = MouseInfo.getPointerInfo();
			Point b = a.getLocation();
			System.out.println(b.x + " " + b.y);
		} catch (SetWindowPositionError | WindowNotFoundException | AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

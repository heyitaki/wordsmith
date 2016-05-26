import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.StdCallLibrary;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;

public class BoardConstructor {
	public static final int DEFAULT_WINDOW_X = 900;
	public static final int DEFAULT_WINDOW_Y = 30;
	public static final int DEFAULT_WINDOW_WIDTH = 1000;
	public static final int DEFAULT_WINDOW_HEIGHT = 550;
	
	public static final int BOARD_OFFSET_X  = 30;
	public static final int BOARD_OFFSET_Y = 202;
	
	public static final int TILE_HEIGHT = 129;
	public static final int TILE_OFFSET = 149;
	
	//public static HashMap<Integer, Character> charRecognition ;
	
	public interface User32 extends StdCallLibrary {
		User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);
		
		HWND FindWindowA(String className, String windowName);
		
		boolean SetWindowPos(HWND hWnd, HWND hWndInsertAfter, int X, int Y, int cx, int cy, int uFlags);
	}
	
	/*public static void initCharRecognition() {
		charRecognition = new HashMap<Integer, Character>();
		charRecognition.put(989, 'a'); charRecognition.put(989, 'b'); charRecognition.put(989, 'c');
		charRecognition.put(989, 'd'); charRecognition.put(989, 'e'); charRecognition.put(989, 'f');
		charRecognition.put(989, 'g'); charRecognition.put(989, 'h'); charRecognition.put(989, 'i');
		charRecognition.put(989, 'j'); charRecognition.put(989, 'k'); charRecognition.put(989, 'l');
		charRecognition.put(989, 'm'); charRecognition.put(989, 'n'); charRecognition.put(989, 'o');
		charRecognition.put(989, 'p'); charRecognition.put(989, 'q'); charRecognition.put(989, 'r');
		charRecognition.put(816, 's'); charRecognition.put(989, 't'); charRecognition.put(989, 'u');
		charRecognition.put(989, 'v'); charRecognition.put(989, 'w'); charRecognition.put(989, 'x');
		charRecognition.put(989, 'y'); charRecognition.put(989, 'z'); 
	}*/
	
	public static void setDimensions(String className, String windowName) throws SetWindowPositionError, 
			WindowNotFoundException {
		HWND handle = User32.INSTANCE.FindWindowA(className, windowName);
		if(handle == null) {
			throw new WindowNotFoundException(className, windowName);
		}
		
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
	
	public static char[][] getConvertedBoard(BufferedImage[] tiles) throws TesseractException {
		Tesseract reader = new Tesseract();
		char[][] ret = new char[Player.BOARD_HEIGHT][Player.BOARD_WIDTH];
		BufferedImage processedImage;
		String convertedTile;
		for(int j = 0; j < Player.BOARD_HEIGHT; j++) {
			for(int k = 0; k < Player.BOARD_WIDTH; k++) {
				processedImage = ImageHelper.convertImageToGrayscale(tiles[j*Player.BOARD_HEIGHT+k]);
				convertedTile = reader.doOCR(processedImage);
				ret[j][k] = BoardConstructor.getLastAlpha(convertedTile);
			}
		}
		return ret;
	}
	
	/*public static double getAverageDistance(BufferedImage tile) throws AWTException {
		Robot robot = new Robot();
		int pixel, red, green, blue;
		double totalDistance = 0, count = 0;
		for(int i = 0; i < tile.getHeight(); i++) {
			for(int j = 0; j < tile.getWidth(); j++) {
				pixel = tile.getRGB(i, j);
				red = (pixel & 0x00FF0000) >> 16;
                green = (pixel & 0x0000FF00) >> 8;
                blue = pixel & 0x000000FF;
				if(red == 255 && green == 255 && blue == 255) {
					totalDistance += Math.sqrt(Math.pow(i, 2) + Math.pow(j, 2));
					count++;
				}
			}
		}
		return totalDistance/count;
	}*/
	
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
	
	//TODO: train Tesseract on I/1,O/0 or write my own OCR
	public static char getLastAlpha(String input) {
		for (int i = input.length() - 1; i >= 0; i--) {
	        char c = input.charAt(i);
	        //hack to get rid of numbers (from tile score), and misread tiles
	        if (Character.isLetter(c))
	            return Character.toLowerCase(c);
	        else if (c == '0')
	        	return 'o';
	        else if (c == '2')
	        	return 'i';
	    }
		return '.';
	}
	
	public static void printBoard(char[][] board) {
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[0].length; j++) {
				System.out.print(board[i][j] + " ");
			}
			System.out.println(" ");
		}
	}
}

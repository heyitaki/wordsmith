import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import net.sourceforge.tess4j.TesseractException;

public class Player {
	WordamentTrie dictionary; 
	String mode;

	//board info
	static final int BOARD_HEIGHT = 4;
	static final int BOARD_WIDTH = 4;
	char[][] board;
	boolean[][] visited;
	static HashMap<String, ArrayList<Coordinate>> foundWords;

	//alphabet info
	HashMap<Character, Integer> tileScores;
	static final int ALPHABET_SIZE = 26;
	static final int MIN_WORD_LENGTH = 3;
	
	//window info
	static final String WINDOW_NAME = "Wordament";
	static final String CLASS_NAME = "ApplicationFrameWindow";

	public Player (String dictFileName, char[][] board, String mode) throws IOException {
		dictionary = new WordamentTrie(readDict(dictFileName));	
		this.board = board;
		this.mode = mode;
		initTileScores();
		
		//default value of visited is false
		visited = new boolean[BOARD_WIDTH][BOARD_HEIGHT];
		foundWords = new HashMap<String, ArrayList<Coordinate>>();
		CursorController.initCursorPositions();
	}

	public static void main(String[] args) {
		//modes available: score, safe
		try {
			BoardConstructor.setDimensions(CLASS_NAME, WINDOW_NAME);
				System.out.println(BoardConstructor.getGameState());
			BufferedImage[] images = BoardConstructor.getTiles();
			char[][] board = BoardConstructor.getConvertedBoard(images);
				BoardConstructor.printBoard(board);
				
			Player god = new Player("./dict.txt", board, "score");
			ArrayList<String> sorted = god.solveBoard();
				System.out.println(sorted);
				
			int accepted = 0, count = 0, error = 0;
			String currWord;
			while(!BoardConstructor.getGameState().equals("score") && count < sorted.size()) {
				currWord = sorted.get(count);
				System.out.print(currWord);
				int[] swipeRGB = CursorController.swipeWord(currWord);
				count++;
				
				accepted = Player.isAcceptedWord(swipeRGB);
				if(accepted == 1) {
					error = 0;
					System.out.println(" - Accepted!");
				} else if(accepted == 0) { 
					System.out.println(" - Already entered");
				} else {
					error++;
					System.out.println(" - Incorrect");
				}
				
				if(error >= 3) {
					break;
				}
				Thread.sleep(250);
			}
		} catch (SetWindowPositionError | WindowNotFoundException | AWTException | TesseractException | IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	//TODO: confirm score for j
	private void initTileScores () {
		char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
		int[] points 	= { 2 ,  5 ,  3 ,  3 ,  1 ,  5 ,  4 ,  4 ,  2 ,  8 ,  6 ,  3 ,  4 ,  2 ,  2 ,  4 , 10 ,  2 ,  1 ,  2 ,  4 ,  6 ,  6 ,  9 ,  5 ,  8 };
		tileScores = new HashMap<Character, Integer>();
		for(int i = 0; i < ALPHABET_SIZE; i++) {
			tileScores.put(alphabet[i], points[i]);
		}
	}

	private ArrayList<String> readDict(String fileName) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		ArrayList<String> allWords = new ArrayList<String>();
		String word, processedWord;
		while((word = in.readLine()) != null) {
			processedWord = word.toLowerCase().trim();
			if(MIN_WORD_LENGTH <= processedWord.length() && processedWord.length() <= BOARD_HEIGHT * BOARD_WIDTH) {
				allWords.add(processedWord);
			}
		}
		in.close();
		return allWords;
	}
	
	//testing purposes
	@SuppressWarnings("unused")
	private void loadBoard(String fileName) {
		/*Scanner in = new Scanner(System.in);
		char nextLetter;
		board = new char[BOARD_HEIGHT][BOARD_WIDTH];
		for (int i = 0; i < BOARD_HEIGHT; i++) {
			for (int j = 0; j < BOARD_WIDTH; j++) {
				nextLetter = in.next("[a-z]").charAt(0);
				board[i][j] = nextLetter;
			}
		}
		in.close();*/
		board = new char[][] {
			{'c', 's', 'd', 'a'},
			{'h', 't', 'r', 'c'},
			{'s', 'a', 's', 'e'},
			{'r', 'p', 'p', 'k'}
		};
	}

	private void enumerateWords () {
		StringBuilder prefix;
		Coordinate start;
		ArrayList<Coordinate> cursorPath;
		for (int i = 0; i < BOARD_HEIGHT; i++) {
			for (int j = 0; j < BOARD_WIDTH; j++) {
				prefix = new StringBuilder();
				start = new Coordinate(i, j);
				cursorPath = new ArrayList<Coordinate>();
				cursorPath.add(CursorController.cursorPositions[j][i]);
				dfsTraversal(prefix, start, cursorPath);
			}
		}
	}

	private void dfsTraversal(StringBuilder prefix, Coordinate curr, ArrayList<Coordinate> cursorPath) {
 		if(visited[curr.x][curr.y]) {
			return;
		}
 		
 		prefix.append(board[curr.x][curr.y]);
 		cursorPath.add(CursorController.cursorPositions[curr.y][curr.x]);
 		visited[curr.x][curr.y] = true;	
		String pfx = prefix.toString();
 		if(dictionary.find(pfx) == -1) {
 			prefix.deleteCharAt(prefix.length() - 1);
 			cursorPath.remove(cursorPath.size() - 1);
 			visited[curr.x][curr.y] = false;
 			return;
 		} else if(dictionary.find(pfx) == 1) {
			foundWords.put(pfx, new ArrayList<Coordinate>(cursorPath));
		}

		for(Coordinate next : getNeighbors(curr)) {
			dfsTraversal(prefix, next, cursorPath);
		}
		
		prefix.deleteCharAt(prefix.length() - 1);
		cursorPath.remove(cursorPath.size() - 1);
		visited[curr.x][curr.y] = false;
	}

	private ArrayList<Coordinate> getNeighbors(Coordinate pt) {
		ArrayList<Coordinate> neighbors = new ArrayList<Coordinate>();
		Coordinate neighbor;
		for(int i = -1; i <= 1; i++) {
			for(int j = -1; j <= 1; j++) {
				neighbor = new Coordinate(pt.x + i, pt.y + j);
				if(isValidCoor(neighbor) && !(i == 0 && j == 0)) {
					neighbors.add(neighbor);
				}
			}
		}
		return neighbors;
	}

	private boolean isValidCoor(Coordinate pt) {
		if(0 <= pt.x && pt.x < BOARD_HEIGHT && 0 <= pt.y && pt.y < BOARD_WIDTH) {
			return true;
		}
		return false;
	}

	public ArrayList<String> solveBoard() {
		enumerateWords();
		ArrayList<String> sorted = new ArrayList<String>(foundWords.keySet());
		
		//sort by points or shuffle based on mode
		if (mode.equals("score")) {
			Collections.sort(sorted, new ScoreComparator());
		} else if (mode.equals("safe")) {
			Collections.shuffle(sorted);
		}
		return sorted;
	}

	//return score of the word
	private int getScore (String word) {
		char letter;
		int score = 0;
		for(int i = 0; i < word.length(); i++) {
			letter = word.charAt(i);
			score += tileScores.get(letter);
		}

		if (4 <= word.length() && word.length() <= 5) {
			score *= 1.5;
		} else if (6 <= word.length() && word.length() <= 7) {
			score *= 2;
		} else if (8 <= word.length()) {
			score *= 3;
		}

		return score;
	}

	private class ScoreComparator implements Comparator<String> {
		@Override
		public int compare(String s1, String s2) {
			return getScore(s2) - getScore(s1);
		}
	}
	
	public static ArrayList<Coordinate> getCursorPath(String input) {
		return foundWords.get(input);
	}
	
	public static int isAcceptedWord(int[] rgb) {
        if(rgb[0] == 51 && rgb[1] == 153 && rgb[2] == 51) { 
        	//accepted word
        	return 1;
        } else if(240 < rgb[0] && rgb[0] < 250 && 215 < rgb[1] && rgb[1] < 225 && 90 < rgb[2] && rgb[2] < 100) { 
        	//word already tried; this should never happen
        	return 0;
        }
        //word incorrect
        return -1;
	}

	public static void printList(ArrayList<String> list) {
		for (int i = 0; i < list.size() && i < 100; i++) {
			System.out.println(list.get(i));
		}
	}
}





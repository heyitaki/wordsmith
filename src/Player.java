import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Player {
	WordamentTrie dictionary; 
	String mode;

	//board info
	static final int BOARD_HEIGHT = 4;
	static final int BOARD_WIDTH = 4;
	char[][] board;
	boolean[][] visited;
	HashSet<String> foundWords;

	//alphabet info
	HashMap<Character, Integer> tileScores;
	static final int ALPHABET_SIZE = 26;
	static final int MIN_WORD_LENGTH = 3;

	public Player (String dictFileName, String boardFileName, String mode) throws IOException {
		dictionary = new WordamentTrie(readDict(dictFileName));
		loadBoard(boardFileName);
		this.mode = mode;
		initTileScores();
		
		//default value of visited is false
		visited = new boolean[BOARD_WIDTH][BOARD_HEIGHT];
		foundWords = new HashSet<String>();
	}

	public static void main(String[] args) throws IOException {
		//modes available: score, safe
		Player god = new Player("./dict.txt", "", "score");
		printList(god.solveBoard());
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

	private void loadBoard(String fileName) {
		Scanner in = new Scanner(System.in);
		char nextLetter;
		board = new char[BOARD_HEIGHT][BOARD_WIDTH];
		for (int i = 0; i < BOARD_HEIGHT; i++) {
			for (int j = 0; j < BOARD_WIDTH; j++) {
				nextLetter = in.next("[a-z]").charAt(0);
				board[i][j] = nextLetter;
			}
		}
		in.close();
	}

	private void enumerateWords () {
		StringBuilder prefix;
		Coordinate start;
		for (int i = 0; i < BOARD_HEIGHT; i++) {
			for (int j = 0; j < BOARD_WIDTH; j++) {
				prefix = new StringBuilder();
				start = new Coordinate(i, j);
				dfsTraversal(prefix, start);
			}
		}
	}

	private void dfsTraversal(StringBuilder prefix, Coordinate curr) {
		if(!isValidCoor(curr) || visited[curr.x][curr.y] || dictionary.find(prefix.toString()) == -1) {
			return;
		}

		visited[curr.x][curr.y] = true;	
		prefix.append(board[curr.x][curr.y]);
		String pfx = prefix.toString();
		if(dictionary.find(pfx) == 1) {
			foundWords.add(pfx);
		}

		for(Coordinate next : getNeighbors(curr)) {
			dfsTraversal(prefix, next);
		}

		visited[curr.x][curr.y] = false;
	}

	private ArrayList<Coordinate> getNeighbors(Coordinate pt) {
		ArrayList<Coordinate> neighbors = new ArrayList<Coordinate>();
		Coordinate neighbor;
		for(int i = -1; i <= 1; i++) {
			for(int j = -1; j <= 1; j++) {
				neighbor = new Coordinate(pt.x + i, pt.y + j);
				neighbors.add(neighbor);
				/*if(isValidCoor(neighbor) && (i != 0 && j != 0)) {
					neighbors.add(neighbor);
				}*/
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
		ArrayList<String> sorted = new ArrayList<String>(foundWords);
		
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
			return getScore(s1) - getScore(s2);
		}
	}

	public static void printList(ArrayList<String> list) {
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
	}
}

class Coordinate {
	int x;
	int y;
	public Coordinate (int x, int y) {
		this.x = x;
		this.y = y;
	}
}



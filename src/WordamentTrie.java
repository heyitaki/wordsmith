import java.util.ArrayList;
import java.util.HashMap;

public class WordamentTrie {
    public Node root;
    //TODO: add board, do some speedups
    public WordamentTrie(ArrayList<String> words) {
        root = new Node('\0', false);
        for(int i = 0; i < words.size(); i++) {
        	insert(words.get(i));
        }
    }

    //TODO: is symbol necessary
    public void insert(String word) {
        Node curr = root;
        Node child;
        boolean isFullWord = false;
        for(int i = 0; i < word.length(); i++) {
            char letter = word.charAt(i);
            //end of word
            if(i == word.length() - 1) {
                isFullWord = true;
            }

            //move to or insert current letter
            if(curr.children.containsKey(letter)) {
                curr = curr.children.get(letter);
                if (isFullWord == true) {
                	curr.isFullWord = isFullWord;
                }
            } else {
                child = new Node(letter, isFullWord);
                curr.children.put(letter, child);
                curr = curr.children.get(letter);
            }
        }
    }

    //returns -1 if input not in trie, 0 if input is prefix, 1 if input is full word
    public int find(String input) {
        Node curr = root;
        for(int i = 0; i < input.length(); i++) {
            //check if next letter is in trie
            char letter = input.charAt(i);
            if(curr.children.containsKey(letter)) {
                curr = curr.children.get(letter);
            } else {
                return -1;
            }

            //check to see if word exists in dict
            if(i == input.length() - 1 && curr.isFullWord) {
                return 1;
            }
        }
        //word in trie, but not in dict
        return 0;
    }
}

class Node {
    public char symbol;
    public boolean isFullWord;
    public HashMap<Character, Node> children;
    public Node(char symbol, boolean isFullWord) {
        this.symbol = symbol;
        this.isFullWord = isFullWord;
        children = new HashMap<Character, Node>();
    }
}

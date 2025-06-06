/*
 * This source file was generated by the Gradle 'init' task
 */

/*
 * Lab 7 - Aldo Lopez, Michael Clark
 * Implementing Huffman Coding Tree
 * 6/4/2025
 *
 */

package lab7;

import heap.Heap;
import avl.AVL;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.PriorityQueue;
public class Huffman {
    // node class to build tree, node has a char and a frequency if it is a leaf, if not, just a frequency will be stored.
    static class Node implements Comparable<Node> {
	char data;
	int freq;
	Node left;
	Node right;

	public Node(char data, int freq) {
	    this.data = data;
	    this.freq = freq;
	    this.left = null;
	    this.right = null;
	}
	//non leaf node - no char
	public Node(int freq, Node left, Node right) {
	    this.data = '\0';
	    this.freq = freq;
	    this.left = left;
	    this.right = right;
	}

	public int compareTo(Node other) {
	    return Integer.compare(this.freq, other.freq);
	}

	public boolean isLeaf() {
	    return this.left == null && this.right == null;
	}
    }


    
    public static HashMap<Character, Integer> countFrequencies(String input){
	HashMap<Character, Integer> result = new HashMap<Character, Integer>();
	char[] text = input.toCharArray();
	for (char k : text){
	    if(!result.containsKey(k)){
		result.put(k, 1);
	    } else {
		result.put(k, result.get(k)+1);
	    }
	}
	return result;
    }
    
    // recursive method to perform dfs.
    public static void ETable(Node node, String code, HashMap<Character, String> map){
	if(node.isLeaf()){
	    map.put(node.data, code);
	    return;
	}
	ETable(node.left, code + "0", map);
	ETable(node.right, code + "1", map);
    }
    // build encoding table - using dfs helper method to traverse through whole tree and find each character keeping track of the path/code to char.
    public static HashMap<Character, String> buildETable(Node root){
	HashMap<Character, String> eTable = new HashMap<Character, String>();
	ETable(root, "", eTable);
	return eTable;
    }
    
    // build tree (minheap)
    public static Node buildTree(HashMap<Character, Integer> freqMap) {
	PriorityQueue<Node> tree = new PriorityQueue<>();

	//adding all nodes
	for (HashMap.Entry<Character, Integer> entry : freqMap.entrySet()) {
	    tree.add(new Node(entry.getKey(), entry.getValue()));
	}

	//combining two smallest until root is left
	while(tree.size() > 1) {
	    Node left = tree.poll();
	    Node right = tree.poll();
	    Node parent = new Node(left.freq + right.freq,left, right);
	    tree.add(parent);
	}
	//should be root of minHeap
	return tree.poll();
    }
    
    // traversing through tree to find each char, keeping track of code for each char
    public static String decode(Node root, String input){
	StringBuilder code = new StringBuilder();
	Node current = root;
	int len = input.length();
	char[] cInput = input.toCharArray();
	for(char c : cInput){
	    if(c == '0'){
		current = current.left;
	    } else {
		current = current.right;
	    }
	    if(current.left == null && current.right == null){
		code.append(current.data);
		current = root;
	    }
	}
	return code.toString();
    }
    
    // grabbing code from encoding table for each character in the input string
    public static String encode(String input, HashMap<Character, String> eTable){
	StringBuilder ans = new StringBuilder();
	for (char c : input.toCharArray()) {
	    ans.append(eTable.get(c));
	}
	return ans.toString();
    }

    public static void main(String[] args) throws FileNotFoundException{
	String fileName = args[0];
	Scanner scanner = new Scanner(new File(fileName));
	HashMap<Character, Integer> charFreq = new HashMap<Character, Integer>();
	StringBuilder sb = new StringBuilder();
	HashMap<Character, String> eTable = new HashMap<Character, String>();
	while(scanner.hasNextLine()){
	    sb.append(scanner.nextLine());
	}
	String input = sb.toString();
	//System.out.println("Input string: " + input);
	charFreq = countFrequencies(input);
	Node treeRoot = buildTree(charFreq);
	eTable = buildETable(treeRoot);
	String eString = encode(input, eTable);
	//System.out.println("Encoded string: " + eString);
	String dString = decode(treeRoot, eString);
	//System.out.println("Decoded string: " + dString);
	if(input.length() < 100){
	    System.out.println("Input string: " + input);
	    System.out.println("Encoded string: " + eString);
	    System.out.println("Decoded string: " + dString);
	}
	System.out.println("Decoded equals input: " + input.equals(dString));
	System.out.println("Compression ratio: " + ((double)eString.length() / (double)input.length()/(double)8));
    }
}

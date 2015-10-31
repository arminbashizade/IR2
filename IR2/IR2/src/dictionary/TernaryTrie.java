package dictionary;

import read.StopWords;
import dictionary.TernaryTrieNode;


public class TernaryTrie
{
	public int numberOfNodes = 0;
	public int numberOfWords = 0;
	int lastID = 0;
	TernaryTrieNode root;
	public int maxFrequency = 0;
	
	public TernaryTrie()
	{
		root = null;
	}
	
	public boolean search(String s)
	{
		return search(s, root);
	}
	
	public boolean search(String s, TernaryTrieNode n)
	{
		if(n == null || s.length() == 0)
			return false;
		if(s.charAt(0) < n.character)
		{
			return search(s, n.loKid);
		}
		else if(s.charAt(0) > n.character)
		{
			return search(s, n.hiKid);
		}
		else
		{
			if(s.length() == 1 && n.isWord)
				return true;
			return search(s.substring(1), n.equalKid);
		}
	}
	
	public void add(String s, int frequency)
	{
		boolean isWord = s.length() == 1 ? true : false;
		int f = s.length() == 1 ? frequency : 0;
		int ID = s.length() == 1 ? lastID++ : -1;

		if(root == null)
		{
			numberOfNodes++;
			root = new TernaryTrieNode(ID, (byte)s.charAt(0), f, isWord);
			add(s, frequency, root);
		}
		else
			add(s, frequency, root);
	}
	
	public void add(String s, int frequency, TernaryTrieNode n)
	{
//		boolean isWord = s.length() == 1 ? true : false;
//		int f = s.length() == 1 ? frequency : 0;
//		int ID = s.length() == 1 ? lastID+1 : -1;
		
		if(s.charAt(0) < n.character)
		{
			if(n.loKid == null)
			{
				numberOfNodes++;
				n.loKid = new TernaryTrieNode(-1, (byte)s.charAt(0), 0, false);
			}
			add(s, frequency, n.loKid);
		}
		else if(s.charAt(0) > n.character)
		{
			if(n.hiKid == null)
			{
				numberOfNodes++;
				n.hiKid = new TernaryTrieNode(-1, (byte)s.charAt(0), 0, false);
			}
			add(s, frequency, n.hiKid);
		}
		else
		{
			if(s.length() == 1)
			{
				if(!n.isWord)
				{
					n.frequency = 0;
					numberOfWords++;
				}
				else
					n.frequency++;
				
				if(n.frequency > maxFrequency)
					maxFrequency = n.frequency;
				n.isWord = true;
//				n.frequency = frequency;
				n.ID = lastID;
				lastID++;
				return;
			}
			if(n.equalKid == null)
			{
				numberOfNodes++;
				n.equalKid = new TernaryTrieNode(-1, (byte)s.charAt(1), frequency, false);
			}
			add(s.substring(1), frequency, n.equalKid);
		}
	}

	public static void main(String[] args)
	{
		TernaryTrie trie = new TernaryTrie();
		trie.add("Dasf", 0);
		trie.add("Dase", 0);
		trie.add("Dasr", 0);
		System.out.println((char)trie.root.character);
		System.out.println((char)trie.root.equalKid.character);
		System.out.println((char)trie.root.equalKid.equalKid.character);
		System.out.println((char)trie.root.equalKid.equalKid.equalKid.character);
		System.out.println(trie.root.equalKid.equalKid.equalKid.isWord);
		System.out.println((char)trie.root.equalKid.equalKid.equalKid.loKid.character);
		System.out.println(trie.root.equalKid.equalKid.equalKid.loKid.isWord);
		System.out.println((char)trie.root.equalKid.equalKid.equalKid.hiKid.character);
		System.out.println(trie.root.equalKid.equalKid.equalKid.hiKid.isWord);
		
		System.out.println("**********\t"+trie.search("Dasf"));
		System.out.println("**********\t"+trie.search("dasf"));
		System.exit(0);
		
		System.out.println("------------------");
	}
}

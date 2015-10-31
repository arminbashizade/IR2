package dictionary;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileTrie
{
	static final int TOKEN_SIZE = 1+4+4+1;//+4+4+4;
	String filePath = "C:/Users/maxi/Documents/Java/IR2/trie.tr";
	static final String openMode = "rw";
	RandomAccessFile rf = null;
	boolean isEmpty = true;
	int i = 0;
	int lastID = 0;
	
	public FileTrie() throws FileNotFoundException
	{
		rf = new RandomAccessFile(filePath, openMode);
	}
	
	public FileTrie(String fileName) throws FileNotFoundException
	{
		rf = new RandomAccessFile("C:/Users/maxi/Documents/Java/IR2/"+fileName, openMode);
	}

	public boolean search(String s) throws IOException
	{
		return search(s, 0);
	}
	
	public boolean search(String s, int index) throws IOException
	{
		if(index*TOKEN_SIZE > rf.length())
			return false;
		
		rf.seek(index*TOKEN_SIZE);

		TernaryTrieNode n = new TernaryTrieNode(-1, rf.readByte(), 0, false);
		rf.skipBytes(8);
		n.isWord = rf.readBoolean();
		
		if(s.charAt(0) < n.character)
		{
			return search(s, (index+1)*3-1 - 1/*n.loKid*/);
		}
		else if(s.charAt(0) > n.character)
		{
			return search(s, (index+1)*3+1 - 1/*n.hiKid*/);
		}
		else
		{
			if(s.length() == 1 && n.isWord)
				return true;
			if(s.length() == 1 && !n.isWord)
				return false;
			return search(s.substring(1), (index+1)*3 - 1/*n.equalKid*/);
		}
	}

	public void add(String s, int frequency) throws IOException
	{
		boolean isWord = s.length() == 1 ? true : false;
		int f = s.length() == 1 ? frequency : 0;
		int ID = s.length() == 1 ? lastID++ : -1;

		rf.seek(0);
		if(isEmpty)
		{
			TernaryTrieNode root = new TernaryTrieNode(ID, (byte)s.charAt(0), f, isWord);
//			rf.writeInt(++i);				//index:			4bytes
			rf.writeByte(root.character); 	//character:		1byte
			rf.writeInt(root.ID);			//ID:				4bytes
			rf.writeInt(root.frequency);	//frequency:		4bytes
			rf.writeBoolean(root.isWord);	//isWord:			1byte
											//total:			10bytes
			isEmpty = false;
		}
		add(s, frequency, 0);
	}

	public void add(String s, int frequency, int index) throws IOException
	{
		boolean isWord = s.length() == 1 ? true : false;
		int f = s.length() == 1 ? frequency : 0;
		int ID = s.length() == 1 ? lastID+1 : -1;

		if(rf.length() < (index+1)*TOKEN_SIZE)
			rf.setLength((index+1)*TOKEN_SIZE);
		rf.seek(index*TOKEN_SIZE);

		TernaryTrieNode n = new TernaryTrieNode(-1, rf.readByte(), 0, false);
		rf.skipBytes(8);
		n.isWord = rf.readBoolean();
		
		if(s.charAt(0) < n.character)
		{
			if(rf.length() < ((index+1)*3-1 - 1 + 1)*TOKEN_SIZE)
				rf.setLength(((index+1)*3-1 - 1 + 1)*TOKEN_SIZE);
			rf.seek(((index+1)*3-1 - 1)*TOKEN_SIZE/*n.loKid*/);
			if(rf.readByte() == 0)
			{
				rf.seek(((index+1)*3-1 - 1)*TOKEN_SIZE/*loKid*/);
				rf.writeByte((byte)s.charAt(0)); 	//character:		1byte
				rf.writeInt(ID);					//ID:				4bytes
				rf.writeInt(f);						//frequency:		4bytes
				rf.writeBoolean(isWord);			//isWord:			1byte
													//total:			10bytes
			}
//			if(n.loKid == null)
//				n.loKid = new TernaryTrieNode(ID, (byte)s.charAt(0), f, isWord);
			add(s, frequency, (index+1)*3-1 - 1/*n.loKid*/);
		}
		else if(s.charAt(0) > n.character)
		{
			if(rf.length() < ((index+1)*3+1 - 1 + 1)*TOKEN_SIZE)
				rf.setLength(((index+1)*3+1 - 1 + 1)*TOKEN_SIZE);
			rf.seek(((index+1)*3+1 - 1)*TOKEN_SIZE/*n.hiKid*/);
			if(rf.readByte() == 0)
			{
				rf.seek(((index+1)*3+1 - 1)*TOKEN_SIZE/*n.hiKid*/);
				rf.writeByte((byte)s.charAt(0)); 	//character:		1byte
				rf.writeInt(ID);					//ID:				4bytes
				rf.writeInt(f);						//frequency:		4bytes
				rf.writeBoolean(isWord);			//isWord:			1byte
													//total:			10bytes
			}
//			if(n.hiKid == null)
//				n.hiKid = new TernaryTrieNode(ID, (byte)s.charAt(0), f, isWord);
			add(s, frequency, (index+1)*3+1 - 1/*n.hiKid*/);
		}
		else
		{
			if(s.length() == 1)
			{
				lastID++;
				rf.seek(index*TOKEN_SIZE);
				rf.skipBytes(1);
				rf.writeInt(ID);
				rf.writeInt(frequency);
				rf.writeBoolean(true);
//				n.ID = ID;
				return;
			}

			if(rf.length() < ((index+1)*3 - 1 + 1)*TOKEN_SIZE)
				rf.setLength(((index+1)*3 - 1 + 1)*TOKEN_SIZE);
			rf.seek(((index+1)*3 - 1)*TOKEN_SIZE/*n.equalKid*/);
//			if(n.equalKid == null)
			if(rf.readByte() == 0)
			{
				
				rf.seek(((index+1)*3 - 1)*TOKEN_SIZE/*n.equalKid*/);

				rf.writeByte((byte)s.charAt(1)); 	//character:		1byte
				rf.writeInt(ID);					//ID:				4bytes
				rf.writeInt(f);						//frequency:		4bytes
				rf.writeBoolean(isWord);			//isWord:			1byte
													//total:			10bytes
			}
			add(s.substring(1), frequency, (index+1)*3 - 1/*n.equalKid*/);
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException
	{
		FileTrie trie = new FileTrie();
		try {
			trie.add("pegah", 1404);
			trie.add("melica", 124);
			trie.add("armin", 21531);
			trie.add("arman", 1241);
			trie.add("melika", 23);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		RandomAccessFile r = new RandomAccessFile(trie.filePath, "r");
		try {
			int mn = 0;
			while(r.getFilePointer() < r.length())
			{
				mn++;
				System.out.println(mn+"\t"+(char)r.readByte()+"\t"+r.readInt()+"\t"+r.readInt()+"\t"+r.readBoolean()+"\t");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			System.out.println(trie.search("arm"));
			System.out.println(trie.search("melika"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}

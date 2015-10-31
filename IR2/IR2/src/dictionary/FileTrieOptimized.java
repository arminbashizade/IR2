package dictionary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import read.MyTokenizer;

public class FileTrieOptimized
{
	public static final int TEST = 1;
	public static final int MAIN_FILE = 2;
	public static final int CLEAR = 1;
	public static final int KEEP = 2;
	
	static final int CHARACTER_SIZE = 1;
	static final int ID_SIZE = 4;
	static final int FREQUENCY_SIZE = 4;
	static final int IS_WORD_SIZE = 1;
	static final int HI_KID_SIZE = 4;
	static final int LO_KID_SIZE = 4;
	static final int EQUAL_KID_SIZE = 4;
	public static final int TOKEN_SIZE = CHARACTER_SIZE+ID_SIZE+FREQUENCY_SIZE+IS_WORD_SIZE+HI_KID_SIZE+LO_KID_SIZE+EQUAL_KID_SIZE;
	public static final String TEST_TRIE_TABLE_ADDRESS = "C:/Users/maxi/Desktop/Dropbox/AUT/Information Storage and Retrieval/IR2/trietable.txt";
	public static final String TEST_OUTPUT_DIRECTORY = "C:/Users/maxi/Desktop/Dropbox/AUT/Information Storage and Retrieval/IR2/";
	public static final String MAIN_TRIE_TABLE_ADDRESS = "C:/Information Storage and Retrieval/trietable.txt";
	public static final String MAIN_OUTPUT_DIRECTORY = "C:/Information Storage and Retrieval/";
	String filePath = TEST_OUTPUT_DIRECTORY+"trieopt.tr";
	String testPath = TEST_TRIE_TABLE_ADDRESS;
	static final String openMode = "rw";
	RandomAccessFile searchFile = null;
	RandomAccessFile outputFile = null;
	FileChannel writer = null;
	ByteBuffer outputBuff = null;
	
	boolean isEmpty = true;
	int i = 0;
	int lastID = 0;
	int lastIndex = 0;
	private int deb = 50;
	
	public FileTrieOptimized(int mode, int del) throws IOException
	{
		if(mode != TEST)
		{
			filePath = MAIN_OUTPUT_DIRECTORY+"trieopt.tr";
			testPath = MAIN_TRIE_TABLE_ADDRESS;
		}

		if(del == CLEAR)
			new File(filePath).delete();
		outputFile = new RandomAccessFile(filePath, "rw");
		searchFile = new RandomAccessFile(filePath, openMode);
		writer = outputFile.getChannel();
		writer.position(0);
		outputBuff = ByteBuffer.allocate(MyTokenizer.BUFF_SIZE);
	}
	
	public FileTrieOptimized(String fileName, int mode, int del) throws IOException
	{
		if(mode == TEST)
		{
			filePath = TEST_OUTPUT_DIRECTORY+fileName;
			testPath = TEST_OUTPUT_DIRECTORY+"trietable.txt";
		}
		else
		{
			filePath = MAIN_OUTPUT_DIRECTORY+fileName;
			testPath = MAIN_OUTPUT_DIRECTORY+"trietable.txt";
		}

		if(del == CLEAR)
			new File(filePath).delete();
		searchFile = new RandomAccessFile(filePath, openMode);
		outputFile = new RandomAccessFile(filePath, "rw");
		writer = outputFile.getChannel();
		writer.position(0);
		outputBuff = ByteBuffer.allocate(MyTokenizer.BUFF_SIZE);
	}

	public boolean search(String s) throws IOException
	{
		return search(s, 0);
	}
	
	public boolean search(String s, int index) throws IOException
	{
		if(s.length() == 0)
			return false;
		if(index*TOKEN_SIZE > searchFile.length() || index < 0)
			return false;
		
		searchFile.seek(index*TOKEN_SIZE);

		TernaryTrieNode n = new TernaryTrieNode(-1, searchFile.readByte(), 0, false);
		searchFile.skipBytes(ID_SIZE+FREQUENCY_SIZE);
		n.isWord = searchFile.readBoolean();
		int loKidIndex = searchFile.readInt();
		int equalKidIndex = searchFile.readInt();
		int hiKidIndex = searchFile.readInt();
		
		if(s.charAt(0) < n.character)
		{
			return search(s, loKidIndex);
		}
		else if(s.charAt(0) > n.character)
		{
			return search(s, hiKidIndex);
		}
		else
		{
			if(s.length() == 1 && n.isWord)
				return true;
			if(s.length() == 1 && !n.isWord)
				return false;
			return search(s.substring(1), equalKidIndex);
		}
	}

	public void addWithBuffer(String s, int frequency) throws IOException
	{
		boolean isWord = s.length() == 1 ? true : false;
		int f = s.length() == 1 ? frequency : 0;
		int ID = s.length() == 1 ? lastID++ : -1;

		if(isEmpty)
		{
			outputBuff.clear();
			TernaryTrieNode root = new TernaryTrieNode(ID, (byte)s.charAt(0), f, isWord);
			outputBuff.position(0);
			outputBuff.put(root.character);
			outputBuff.putInt(root.ID);
			outputBuff.putInt(root.frequency);
			outputBuff.put(root.isWord?(byte)1:(byte)0);
			outputBuff.putInt(-1);
			outputBuff.putInt(-1);
			outputBuff.putInt(-1);
			lastIndex++;
			
			isEmpty = false;
		}
		
		addWithBuffer(s, frequency, 0);
	}
	
	private void addWithBuffer(String s, int frequency, int index) throws IOException
	{
		int indexInBuffer;
		int fileSize = (int)outputFile.length();		
		indexInBuffer = index - fileSize/TOKEN_SIZE;
		boolean isWord = s.length() == 1 ? true : false;
		int f = s.length() == 1 ? frequency : 0;
		int ID = s.length() == 1 ? lastID+1 : -1;
		
		if(outputBuff.remaining() < TOKEN_SIZE)
		{
			//write to file and clear the buffer
//			System.out.println("Writing to file from buffer.");
//			outputBuff.position(lastIndex-fileSize/TOKEN_SIZE);
			outputBuff.flip();
			writer.write(outputBuff);
			outputBuff.clear();
			fileSize = (int)outputFile.length();
		}
		
		TernaryTrieNode n = new TernaryTrieNode(-1, (byte)'!', 0, false);
		int loKidIndex, hiKidIndex, equalKidIndex;
		
		if(index < fileSize/TOKEN_SIZE)
		{
			//read from file
			searchFile.seek(index*TOKEN_SIZE);
			n.character = searchFile.readByte();
			searchFile.skipBytes(ID_SIZE+FREQUENCY_SIZE);
			n.isWord = searchFile.readBoolean();
			loKidIndex = searchFile.readInt();
			equalKidIndex = searchFile.readInt();
			hiKidIndex = searchFile.readInt();
		}
		else
		{
//			System.out.println(index+"\t"+indexInBuffer);
			//read from buffer
			outputBuff.position(indexInBuffer*TOKEN_SIZE);
			n.character = outputBuff.get();
			outputBuff.position(indexInBuffer*TOKEN_SIZE+CHARACTER_SIZE+ID_SIZE+FREQUENCY_SIZE);
			n.isWord = outputBuff.get() == 1 ? true : false;
			loKidIndex = outputBuff.getInt();
			equalKidIndex = outputBuff.getInt();
			hiKidIndex = outputBuff.getInt();
		}

		if(deb < 40)
		{
			System.out.println(indexInBuffer+"\t"+s+"\t"+(char)n.character);
			deb++;
		}

		if(s.charAt(0) < n.character)
		{
			if(loKidIndex == -1)
			{
				if(index < fileSize/TOKEN_SIZE)
				{
					searchFile.seek(index*TOKEN_SIZE+CHARACTER_SIZE+ID_SIZE+FREQUENCY_SIZE+IS_WORD_SIZE);
					searchFile.writeInt(lastIndex);
				}
				else
				{
					outputBuff.position(indexInBuffer*TOKEN_SIZE+CHARACTER_SIZE+ID_SIZE+FREQUENCY_SIZE+IS_WORD_SIZE);
					outputBuff.putInt(lastIndex);
				}
				
				if(lastIndex < fileSize/TOKEN_SIZE)
				{
					searchFile.seek(lastIndex*TOKEN_SIZE);
					searchFile.writeByte((byte)s.charAt(0));
					searchFile.writeInt(ID);
					searchFile.writeInt(f);
					searchFile.writeBoolean(isWord);
					searchFile.writeInt(-1);
					searchFile.writeInt(-1);
					searchFile.writeInt(-1);					
				}
				else
				{
					int tmpIndex = (lastIndex+1)*TOKEN_SIZE < outputBuff.limit()?lastIndex : lastIndex - fileSize/TOKEN_SIZE;
					outputBuff.position(tmpIndex*TOKEN_SIZE);
					outputBuff.put((byte)s.charAt(0));
					outputBuff.putInt(ID);
					outputBuff.putInt(f);
					outputBuff.put(isWord?(byte)1:(byte)0);
					outputBuff.putInt(-1);
					outputBuff.putInt(-1);
					outputBuff.putInt(-1);										
				}
				
				loKidIndex = lastIndex;
				lastIndex++;
			}
//			if(n.loKid == null)
//				n.loKid = new TernaryTrieNode(ID, (byte)s.charAt(0), f, isWord);
			if(deb < 40)
				System.out.println("loKid:\t"+loKidIndex);
//			System.out.println("loKid");
			addWithBuffer(s, frequency, loKidIndex);
		}
		else if(s.charAt(0) > n.character)
		{
			if(hiKidIndex == -1)
			{
				if(index < fileSize/TOKEN_SIZE)
				{
					searchFile.seek(index*TOKEN_SIZE+CHARACTER_SIZE+ID_SIZE+FREQUENCY_SIZE+IS_WORD_SIZE+LO_KID_SIZE+EQUAL_KID_SIZE);
					searchFile.writeInt(lastIndex);
				}
				else
				{
					outputBuff.position(indexInBuffer*TOKEN_SIZE+CHARACTER_SIZE+ID_SIZE+FREQUENCY_SIZE+IS_WORD_SIZE+LO_KID_SIZE+EQUAL_KID_SIZE);
					outputBuff.putInt(lastIndex);
				}
				
				if(lastIndex < fileSize/TOKEN_SIZE)
				{
					searchFile.seek(lastIndex*TOKEN_SIZE);
					searchFile.writeByte((byte)s.charAt(0));
					searchFile.writeInt(ID);
					searchFile.writeInt(f);
					searchFile.writeBoolean(isWord);
					searchFile.writeInt(-1);
					searchFile.writeInt(-1);
					searchFile.writeInt(-1);					
				}
				else
				{
					int tmpIndex = (lastIndex+1)*TOKEN_SIZE < outputBuff.limit()?lastIndex : lastIndex - fileSize/TOKEN_SIZE;
					outputBuff.position(tmpIndex*TOKEN_SIZE);
					outputBuff.put((byte)s.charAt(0));
					outputBuff.putInt(ID);
					outputBuff.putInt(f);
					outputBuff.put(isWord?(byte)1:(byte)0);
					outputBuff.putInt(-1);
					outputBuff.putInt(-1);
					outputBuff.putInt(-1);										
				}

				hiKidIndex = lastIndex;
				lastIndex++;
			}
//			if(n.hiKid == null)
//				n.hiKid = new TernaryTrieNode(ID, (byte)s.charAt(0), f, isWord);
			if(deb < 40)
				System.out.println("hiKid:\t"+hiKidIndex);
//			System.out.println("hiKid");
			addWithBuffer(s, frequency, hiKidIndex);
		}
		else
		{
			if(s.length() == 1)
			{
				lastID++;
				if(index < fileSize/TOKEN_SIZE)
				{
					searchFile.seek(index*TOKEN_SIZE+CHARACTER_SIZE);
					searchFile.writeInt(ID);
					searchFile.writeInt(frequency);
					searchFile.writeBoolean(true);
					int tmpLoIndex = searchFile.readInt();
					int tmpEqualIndex = searchFile.readInt();
					int tmpHiIndex = searchFile.readInt();
					searchFile.seek(index*TOKEN_SIZE+CHARACTER_SIZE+ID_SIZE+FREQUENCY_SIZE+IS_WORD_SIZE);
					searchFile.writeInt(tmpLoIndex);
					searchFile.writeInt(tmpEqualIndex);
					searchFile.writeInt(tmpHiIndex);
				}
				else
				{
					outputBuff.position(indexInBuffer*TOKEN_SIZE+CHARACTER_SIZE);
					outputBuff.putInt(ID);
					outputBuff.putInt(f);
					outputBuff.put((byte)1);
					int tmpLoIndex = outputBuff.getInt();
					int tmpEqualIndex = outputBuff.getInt();
					int tmpHiIndex = outputBuff.getInt();
					outputBuff.position(indexInBuffer*TOKEN_SIZE+CHARACTER_SIZE+ID_SIZE+FREQUENCY_SIZE+IS_WORD_SIZE);
					outputBuff.putInt(tmpLoIndex);
					outputBuff.putInt(tmpEqualIndex);
					outputBuff.putInt(tmpHiIndex);
				}
				
				return;
			}
			
			if(equalKidIndex == -1)
			{
				if(index < fileSize/TOKEN_SIZE)
				{
					searchFile.seek(index*TOKEN_SIZE+CHARACTER_SIZE+ID_SIZE+FREQUENCY_SIZE+IS_WORD_SIZE+LO_KID_SIZE);
					searchFile.writeInt(lastIndex);
				}
				else
				{
					outputBuff.position(indexInBuffer*TOKEN_SIZE+CHARACTER_SIZE+ID_SIZE+FREQUENCY_SIZE+IS_WORD_SIZE+LO_KID_SIZE);
					outputBuff.putInt(lastIndex);
				}
				
				if(lastIndex < fileSize/TOKEN_SIZE)
				{
					searchFile.seek(lastIndex*TOKEN_SIZE);
					searchFile.writeByte((byte)s.charAt(1));
					searchFile.writeInt(ID);
					searchFile.writeInt(f);
					searchFile.writeBoolean(isWord);
					searchFile.writeInt(-1);
					searchFile.writeInt(-1);
					searchFile.writeInt(-1);					
				}
				else
				{
					int tmpIndex = (lastIndex+1)*TOKEN_SIZE < outputBuff.limit()?lastIndex : lastIndex - fileSize/TOKEN_SIZE;
					outputBuff.position(tmpIndex*TOKEN_SIZE);
					outputBuff.put((byte)s.charAt(1));
					outputBuff.putInt(ID);
					outputBuff.putInt(f);
					outputBuff.put(isWord?(byte)1:(byte)0);
					outputBuff.putInt(-1);
					outputBuff.putInt(-1);
					outputBuff.putInt(-1);										
				}

				if(deb < 40)
				{
					int pos = outputBuff.position();
					outputBuff.position(lastIndex*TOKEN_SIZE);
					System.out.println("char:\t"+(char)outputBuff.get());
					outputBuff.position(pos);
				}
				
				equalKidIndex = lastIndex;
				lastIndex++;
			}
			if(deb < 40)
				System.out.println("equalKid:\t"+equalKidIndex+"\t"+s.charAt(1));
//			System.out.println("equalKid");
			addWithBuffer(s.substring(1), frequency, equalKidIndex);
		}
	}
	
	public void add(String s, int frequency) throws IOException
	{
		boolean isWord = s.length() == 1 ? true : false;
		int f = s.length() == 1 ? frequency : 0;
		int ID = s.length() == 1 ? lastID++ : -1;

		outputFile.seek(0);
		if(isEmpty)
		{
			TernaryTrieNode root = new TernaryTrieNode(ID, (byte)s.charAt(0), f, isWord);
			outputFile.writeByte(root.character);
			outputFile.writeInt(root.ID);
			outputFile.writeInt(root.frequency);
			outputFile.writeBoolean(root.isWord);
			outputFile.writeInt(-1);
			outputFile.writeInt(-1);
			outputFile.writeInt(-1);
			lastIndex++;
			
			isEmpty = false;
		}
		add(s, frequency, 0);
	}

	
	private void add(String s, int frequency, int index) throws IOException
	{
		boolean isWord = s.length() == 1 ? true : false;
		int f = s.length() == 1 ? frequency : 0;
		int ID = s.length() == 1 ? lastID+1 : -1;

		if(outputFile.length() < (index+1)*TOKEN_SIZE)
			outputFile.setLength((index+1)*TOKEN_SIZE);
		outputFile.seek(index*TOKEN_SIZE);

		TernaryTrieNode n = new TernaryTrieNode(-1, outputFile.readByte(), 0, false);
		outputFile.skipBytes(ID_SIZE+FREQUENCY_SIZE);
		n.isWord = outputFile.readBoolean();
		int loKidIndex = outputFile.readInt();
		int equalKidIndex = outputFile.readInt();
		int hiKidIndex = outputFile.readInt();
				
		if(s.charAt(0) < n.character)
		{
			if(loKidIndex == -1)
			{
				outputFile.seek(index*TOKEN_SIZE);
				outputFile.skipBytes(CHARACTER_SIZE+ID_SIZE+FREQUENCY_SIZE+IS_WORD_SIZE);
				outputFile.writeInt(lastIndex);
				
				outputFile.setLength((lastIndex+1)*TOKEN_SIZE);
				outputFile.seek(lastIndex*TOKEN_SIZE);
				outputFile.writeByte((byte)s.charAt(0));
				outputFile.writeInt(ID);
				outputFile.writeInt(f);
				outputFile.writeBoolean(isWord);
				outputFile.writeInt(-1);
				outputFile.writeInt(-1);
				outputFile.writeInt(-1);
				
				loKidIndex = lastIndex;
				lastIndex++;
			}
//			if(n.loKid == null)
//				n.loKid = new TernaryTrieNode(ID, (byte)s.charAt(0), f, isWord);
			add(s, frequency, loKidIndex);
		}
		else if(s.charAt(0) > n.character)
		{
			if(hiKidIndex == -1)
			{
				outputFile.seek(index*TOKEN_SIZE);
				outputFile.skipBytes(CHARACTER_SIZE+ID_SIZE+FREQUENCY_SIZE+IS_WORD_SIZE+LO_KID_SIZE+EQUAL_KID_SIZE);
				outputFile.writeInt(lastIndex);
				
				outputFile.setLength((lastIndex+1)*TOKEN_SIZE);
				outputFile.seek(lastIndex*TOKEN_SIZE);
				outputFile.writeByte((byte)s.charAt(0));
				outputFile.writeInt(ID);
				outputFile.writeInt(f);
				outputFile.writeBoolean(isWord);
				outputFile.writeInt(-1);
				outputFile.writeInt(-1);
				outputFile.writeInt(-1);
				
				hiKidIndex = lastIndex;
				lastIndex++;
			}
//			if(n.hiKid == null)
//				n.hiKid = new TernaryTrieNode(ID, (byte)s.charAt(0), f, isWord);
			add(s, frequency, hiKidIndex);
		}
		else
		{
			if(s.length() == 1)
			{
				lastID++;
				outputFile.seek(index*TOKEN_SIZE);
				outputFile.skipBytes(CHARACTER_SIZE);
				outputFile.writeInt(ID);
				outputFile.writeInt(frequency);
				outputFile.writeBoolean(true);
				return;
			}

			if(equalKidIndex == -1)
			{
				outputFile.seek(index*TOKEN_SIZE);
				outputFile.skipBytes(CHARACTER_SIZE+ID_SIZE+FREQUENCY_SIZE+IS_WORD_SIZE+LO_KID_SIZE);
				outputFile.writeInt(lastIndex);
				
				outputFile.setLength((lastIndex+1)*TOKEN_SIZE);
				outputFile.seek(lastIndex*TOKEN_SIZE);
				outputFile.writeByte((byte)s.charAt(1));
				outputFile.writeInt(ID);
				outputFile.writeInt(f);
				outputFile.writeBoolean(isWord);
				outputFile.writeInt(-1);
				outputFile.writeInt(-1);
				outputFile.writeInt(-1);
				
				equalKidIndex = lastIndex;
				lastIndex++;
			}
			add(s.substring(1), frequency, equalKidIndex);
		}
	}
	

	public void output() throws FileNotFoundException
	{
		new File(testPath).delete();
		RandomAccessFile o = new RandomAccessFile(testPath, "rw");
		RandomAccessFile r = new RandomAccessFile(filePath, "r");
		try {
			r.seek(0);
			int mn = 0;
			while(r.getFilePointer() < r.length())
			{
				mn++;
//				String st = new String("\n"+mn+"\t"+(char)r.readByte()+"\t"+r.readInt()+"\t"+r.readInt()+"\t"+r.readBoolean()+"\t"+r.readInt()+"\t"+r.readInt()+"\t"+r.readInt());
				o.writeBytes("\n"+mn);
				o.writeBytes("\t\t"+(char)r.readByte());
				o.writeBytes("\t\t"+r.readInt());
				o.writeBytes("\t\t"+r.readInt());
				o.writeBytes("\t\t"+r.readBoolean());
				o.writeBytes("\t\t"+r.readInt());
				o.writeBytes("\t\t"+r.readInt());
				o.writeBytes("\t\t"+r.readInt());
				
//				o.writeBytes(st);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void printBuffer()
	{
		int pos = outputBuff.position();
		outputBuff.position(0);
		for(int i = 0; i < lastIndex; i++)
			System.out.println(i+"\t"+(char)outputBuff.get()+"\t"+outputBuff.getInt()+"\t"+outputBuff.getInt()+"\t"+(outputBuff.get()==1?true:false)+"\t"+outputBuff.getInt()+"\t"+outputBuff.getInt()+"\t"+outputBuff.getInt());
		System.out.println("----------------------------------------------------");
		outputBuff.position(pos);
	}
	public void close() throws IOException
	{
		if(outputBuff.position() != 0)
		{
//			System.out.println("Writing to file from buffer.");
//			System.out.println(lastIndex);
//			outputBuff.position(lastIndex-(int)searchFile.length()/TOKEN_SIZE);
//			printBuffer();
			outputBuff.flip();
			writer.write(outputBuff);
			outputBuff.clear();
		}
		writer.close();
		outputFile.close();
	}
	public static void main(String[] args) throws IOException
	{
		FileTrieOptimized trie = new FileTrieOptimized(FileTrieOptimized.TEST, CLEAR);
		try {
//			trie.addWithBuffer("abc", 0);
			trie.addWithBuffer("melica", 1001);
			trie.addWithBuffer("pegah", 1002);
			trie.addWithBuffer("armin", 1003);
			trie.addWithBuffer("arman", 1004);
			trie.addWithBuffer("melika", 1005);
		} catch (IOException e) {
			e.printStackTrace();
		}

		trie.close();
		trie.output();
		
//		RandomAccessFile r = new RandomAccessFile(trie.filePath, "r");
//		try {
//			int mn = 0;
//			while(r.getFilePointer() < r.length() && mn < trie.lastIndex)
//			{
//				mn++;
//				System.out.println(mn+"\t"+(char)r.readByte()+"\t"+r.readInt()+"\t"+r.readInt()+"\t"+r.readBoolean()+"\t"+r.readInt()+"\t"+r.readInt()+"\t"+r.readInt());
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		try {
			System.out.println(trie.search("arm"));
			System.out.println(trie.search("armin"));
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}

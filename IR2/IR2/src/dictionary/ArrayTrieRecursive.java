package dictionary;

import read.MyTokenizer;

public class ArrayTrieRecursive
{
	static final int CHARACTER_SIZE_BIT = 8;
	static final int IS_WORD_SIZE_BIT = 8;
	static final int ID_SIZE_BIT = 32;
	static final int FREQUENCY_SIZE_BIT = 32;
	static final int LO_KID_INDEX_SIZE_BIT = 32;
	static final int HI_KID_INDEX_SIZE_BIT = 32;
	static final int EQUAL_KID_INDEX_SIZE_BIT = 32;
	
	public static final int TOKEN_SIZE_BIT = CHARACTER_SIZE_BIT+IS_WORD_SIZE_BIT+ID_SIZE_BIT+FREQUENCY_SIZE_BIT+LO_KID_INDEX_SIZE_BIT+EQUAL_KID_INDEX_SIZE_BIT+HI_KID_INDEX_SIZE_BIT;
	byte[] trie = new byte[MyTokenizer.BUFF_SIZE];
	int size = MyTokenizer.BUFF_SIZE;
	boolean isEmpty = true;
	public int numberOfNodes = 0;
	int lastID = 0;
	int lastIndex = 1;
	
	public ArrayTrieRecursive()
	{
		for(int i = 0; i < size; i++)
			trie[i] = 0;
	}
	
	private void resize()
	{
//		System.out.println("Resizing...\t"+"current:\t"+(size/1024)+" KB, new:\t"+(size*2/1024)+"KB.");
		byte[] tmp = new byte[size];
		for(int i = 0; i < size; i++)
			tmp[i] = trie[i];
		
		size *= 2;
		trie = new byte[size];
		for(int i = 0; i < size; i++)
		{
			if(i < size/2)
				trie[i] = tmp[i];
			else
				trie[i] = 0;
		}		
	}
	
	private void incrementFrequency(int index)
	{
		byte[] f = new byte[4];
		f[0] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8];
		f[1] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+1];
		f[2] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+2];
		f[3] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+3];

		int frequency = byteArrayToInt(f);
		frequency++;
		f = intToByteArray(frequency);
		trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8] = f[3];
		trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+1] = f[2];
		trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+2] = f[1];
		trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+3] = f[0];
	}
	
	private int byteArrayToInt(byte[] b)
	{
		int n = 0;
	
		int j = 0;
		for(int i = 0; i < 32; i++)
		{
			if(i%8 == 0 && i != 0)
				j++;
			if(b[j]%2 != 0)
			{
				n += Math.pow(2, i);
			}

			b[j] = shiftRight(b[j]);
		}

		return n;		
	}
	
	private byte[] intToByteArray(int value)
	{
		return new byte[]
				{(byte)(value >>> 24),
				 (byte)(value >>> 16),
				 (byte)(value >>> 8),
				 (byte)value};
	}
	
	
	public byte shiftRight(byte b)
	{
		if(b < 0)
		{
			b = (byte) (b & 127);
			b = (byte) (b >> 1);
			b = (byte) (b | 64);
		}
		else
		{
			b = (byte) (b >> 1);
		}
		return b;
	}
	
	private void setID(int index)
	{
		byte[] id = intToByteArray(lastID);
		lastID++;
		trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8] = id[3];
		trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+1] = id[2];
		trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+2] = id[1];
		trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+3] = id[0];
	}
	
	public int getID(int index)
	{
		byte[] id = new byte[4];
		id[0] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8];
		id[1] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+1];
		id[2] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+2];
		id[3] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+3];

		int ID = byteArrayToInt(id);
	
		return ID;
	}
	
	public int getFrequency(int index)
	{
		byte[] f = new byte[4];
		f[0] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8];
		f[1] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+1];
		f[2] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+2];
		f[3] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+3];

		int frequency = byteArrayToInt(f);

		return frequency;
	}
	
	private boolean isWord(int index)
	{
		return trie[index*TOKEN_SIZE_BIT/8+IS_WORD_SIZE_BIT/8] == 1 ? true : false;
	}
	
	private void setAsWord(int index)
	{
		trie[index*TOKEN_SIZE_BIT/8+IS_WORD_SIZE_BIT/8] = 1;
	}
	
	private void setChar(int index, byte c)
	{
		trie[index*TOKEN_SIZE_BIT/8] = c;
	}
	
	private byte getChar(int index)
	{
		return trie[index*TOKEN_SIZE_BIT/8];
	}

	private void setLoKidIndex(int index, int loKidIndex)
	{
		byte[] loKid = intToByteArray(loKidIndex);
		trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8] = loKid[3];
		trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+1] = loKid[2];
		trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+2] = loKid[1];
		trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+3] = loKid[0];
	}
	
	private void setHiKidIndex(int index, int hiKidIndex)
	{
		byte[] hiKid = intToByteArray(hiKidIndex);
		trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+EQUAL_KID_INDEX_SIZE_BIT/8] = hiKid[3];
		trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+EQUAL_KID_INDEX_SIZE_BIT/8+1] = hiKid[2];
		trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+EQUAL_KID_INDEX_SIZE_BIT/8+2] = hiKid[1];
		trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+EQUAL_KID_INDEX_SIZE_BIT/8+3] = hiKid[0];
	}
	
	private void setEqualKidIndex(int index, int equalKidIndex)
	{
		byte[] equalKid = intToByteArray(equalKidIndex);
		trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8] = equalKid[3];
		trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+1] = equalKid[2];
		trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+2] = equalKid[1];
		trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+3] = equalKid[0];
	}
	private int getLoKidIndex(int index)
	{
		byte[] lokid = new byte[4];
		lokid[0] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8];
		lokid[1] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+1];
		lokid[2] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+2];
		lokid[3] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+3];

		int loKidIndex = byteArrayToInt(lokid);

		return loKidIndex;
	}
	
	private int getHiKidIndex(int index)
	{
		int hiKidIndex = 0;
		byte[] hikid = new byte[4];
		hikid[0] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+EQUAL_KID_INDEX_SIZE_BIT/8];
		hikid[1] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+EQUAL_KID_INDEX_SIZE_BIT/8+1];
		hikid[2] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+EQUAL_KID_INDEX_SIZE_BIT/8+2];
		hikid[3] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+EQUAL_KID_INDEX_SIZE_BIT/8+3];

		hiKidIndex = byteArrayToInt(hikid);
		
		return hiKidIndex;
	}
	
	private int getEqualKidIndex(int index)
	{
		int equalKidIndex = 0;
		byte[] equalkid = new byte[4];
		equalkid[0] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8];
		equalkid[1] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+1];
		equalkid[2] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+2];
		equalkid[3] = trie[index*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+3];

		equalKidIndex = byteArrayToInt(equalkid);
		
		return equalKidIndex;
	}
	
	public void add(String s)
	{
		if(isEmpty)
		{
			isEmpty = false;
			numberOfNodes++;
			setChar(1, (byte)s.charAt(0));
			setLoKidIndex(1, 0);
			setEqualKidIndex(1, 0);
			setHiKidIndex(1, 0);
			if(s.length() == 1)
			{
				setAsWord(1);
				setID(1);
			}
			lastIndex++;
		}
		add(s, 1);	
	}

	private void add(String s, int index)
	{	
		if(s.charAt(0) < getChar(index))
		{
//			if(n.loKid == null)
			if(getLoKidIndex(index) == 0)
			{
				numberOfNodes++;
				setLoKidIndex(index, lastIndex);
				while((lastIndex+1)*TOKEN_SIZE_BIT/8 >= size)
					resize();
				setChar(lastIndex, (byte)s.charAt(0));
				lastIndex++;
			}
			add(s, getLoKidIndex(index));
		}
		else if(s.charAt(0) > getChar(index))
		{
//			if(n.hiKid == null)
			if(getHiKidIndex(index) == 0)
			{
				numberOfNodes++;
				setHiKidIndex(index, lastIndex);
				while((lastIndex+1)*TOKEN_SIZE_BIT/8 >= size)
					resize();
				setChar(lastIndex, (byte)s.charAt(0));
				lastIndex++;
			}
			add(s, getHiKidIndex(index));
		}
		else
		{
			if(s.length() == 1)
			{
				if(!isWord(index))
				{
					setAsWord(index);
					setID(index);
				}
				incrementFrequency(index);
				return;
			}
			
//			if(n.equalKid == null)
			if(getEqualKidIndex(index) == 0)
			{
				numberOfNodes++;
				setEqualKidIndex(index, lastIndex);
				while((lastIndex+1)*TOKEN_SIZE_BIT/8 >= size)
					resize();
				setChar(lastIndex, (byte)s.charAt(1));
				lastIndex++;
			}
			add(s.substring(1), getEqualKidIndex(index));
		}
	}
	
	public boolean search(String s)
	{
		return search(s, 1);
	}
	
	private boolean search(String s, int index)
	{
		if(getChar(index) == 0 || s.length() == 0)
			return false;
		if(s.charAt(0) < getChar(index))
		{
			return search(s, getLoKidIndex(index));
		}
		else if(s.charAt(0) > getChar(index))
		{
			return search(s, getHiKidIndex(index));
		}
		else
		{
			if(s.length() == 1 && isWord(index))
				return true;
			return search(s.substring(1), getEqualKidIndex(index));
		}

	}
	public static void main(String[] args)
	{
		System.out.println(TOKEN_SIZE_BIT/8);
		ArrayTrieRecursive at = new ArrayTrieRecursive();
		at.add("armin");
		at.add("sina");
		at.add("armin");
		System.out.println(at.getFrequency(at.getEqualKidIndex(at.getEqualKidIndex(at.getEqualKidIndex(at.getEqualKidIndex(1))))));
		System.out.println(at.getFrequency(at.getEqualKidIndex(at.getEqualKidIndex(at.getEqualKidIndex(at.getHiKidIndex(1))))));
		System.out.println(at.search("sina"));
	}
}

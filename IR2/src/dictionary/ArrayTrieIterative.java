package dictionary;

import read.MyTokenizer;

public class ArrayTrieIterative
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

	private int resizeCounterI;
	private byte[] tmpByteResize;
	private int i;
	private int argumentIntIndex;
	private byte[] byteArrayToIntInput = new byte[4];
	private int byteArrayToIntCounterI;
	private int byteArrayToIntCounterJ;
	private int byteArrayToIntOutput;
	private int intToByteArrayInput;
	private byte[] intToByteArrayOutput= new byte[4];
	private byte shiftRightArg;
	private int getIDOutput;
	private int getFrequencyOutput;
	private boolean isWordOutput;
	private int stringSize = 0;
	private byte[] stringToByte = new byte[300];
	private byte setCharInputChar;
	private byte getCharOutput;
	private int setLoKidIndexInputLoKid;
	private int setHiKidIndexInputHiKid;
	private int setEqualKidIndexInputEqualKid;
	private int getLoKidIndexOutput;
	private int getHiKidIndexOutput;
	private int getEqualKidIndexOutput;

	public ArrayTrieIterative()
	{
		for(int i = 0; i < size; i++)
			trie[i] = 0;
	}
	
	private void resize()
	{
//		System.out.println("Resizing...\t"+"current:\t"+(size/1024)+" KB, new:\t"+(size*2/1024)+"KB.");
		tmpByteResize = new byte[size];
		for(resizeCounterI = 0; resizeCounterI < size; resizeCounterI++)
			tmpByteResize[resizeCounterI] = trie[resizeCounterI];
		
		size *= 2;
		trie = new byte[size];
		for(resizeCounterI = 0; resizeCounterI < size; resizeCounterI++)
		{
			if(resizeCounterI < size/2)
				trie[resizeCounterI] = tmpByteResize[resizeCounterI];
			else
				trie[resizeCounterI] = 0;
		}
	}
	
	private void byteArrayToInt()
	{
		byteArrayToIntOutput = 0;

		byteArrayToIntCounterJ = 0;
		for(byteArrayToIntCounterI = 0; byteArrayToIntCounterI < 32; byteArrayToIntCounterI++)
		{
			if(byteArrayToIntCounterI%8 == 0 && byteArrayToIntCounterI != 0)
				byteArrayToIntCounterJ++;
			if(byteArrayToIntInput[byteArrayToIntCounterJ]%2 != 0)
			{
				byteArrayToIntOutput += Math.pow(2, byteArrayToIntCounterI);
			}

			shiftRightArg = byteArrayToIntInput[byteArrayToIntCounterJ];
			shiftRight();
			byteArrayToIntInput[byteArrayToIntCounterJ] = shiftRightArg;
		}
	}
	
	private void intToByteArray()
	{
		intToByteArrayOutput[0] = (byte) (intToByteArrayInput >>> 24);
		intToByteArrayOutput[1] = (byte) (intToByteArrayInput >>> 16);
		intToByteArrayOutput[2] = (byte) (intToByteArrayInput >>> 8);
		intToByteArrayOutput[3] = (byte) (intToByteArrayInput);
	}
	
	
	public void shiftRight()
	{
		if(shiftRightArg < 0)
		{
			shiftRightArg = (byte) (shiftRightArg & 127);
			shiftRightArg = (byte) (shiftRightArg >> 1);
			shiftRightArg = (byte) (shiftRightArg | 64);
		}
		else
		{
			shiftRightArg = (byte) (shiftRightArg >> 1);
		}
	}
	
	private void incrementFrequency(/*argumentIntIndex*/)
	{
		byteArrayToIntInput[0] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8];
		byteArrayToIntInput[1] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+1];
		byteArrayToIntInput[2] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+2];
		byteArrayToIntInput[3] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+3];
		
		byteArrayToInt(/*byteArrayToIntInput*/);
		byteArrayToIntOutput++;
		intToByteArrayInput = byteArrayToIntOutput;
		intToByteArray(/*frequency*/);
		trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8] = intToByteArrayOutput[3];
		trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+1] = intToByteArrayOutput[2];
		trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+2] = intToByteArrayOutput[1];
		trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+3] = intToByteArrayOutput[0];
	}
	
	private void setID(/*argumentIntIndex*/)
	{
		intToByteArrayInput = lastID;
		intToByteArray();
		lastID++;
		trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8] = intToByteArrayOutput[3];
		trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+1] = intToByteArrayOutput[2];
		trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+2] = intToByteArrayOutput[1];
		trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+3] = intToByteArrayOutput[0];
	}
	
	public void getID()
	{
		byteArrayToIntInput[0] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8];
		byteArrayToIntInput[1] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+1];
		byteArrayToIntInput[2] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+2];
		byteArrayToIntInput[3] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+3];

		byteArrayToInt();
	
		getIDOutput = byteArrayToIntOutput;
	}
	
	public void getFrequency()
	{
		byteArrayToIntInput[0] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8];
		byteArrayToIntInput[1] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+1];
		byteArrayToIntInput[2] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+2];
		byteArrayToIntInput[3] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+3];

		byteArrayToInt();
		
		getFrequencyOutput = byteArrayToIntOutput;
	}
	
	private void isWord()
	{
		isWordOutput = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+IS_WORD_SIZE_BIT/8] == 1 ? true : false;
	}
	
	private void setAsWord()
	{
		trie[argumentIntIndex*TOKEN_SIZE_BIT/8+IS_WORD_SIZE_BIT/8] = 1;
	}
	
	private void setChar()
	{
		trie[argumentIntIndex*TOKEN_SIZE_BIT/8] = setCharInputChar;
	}
	
	private void getChar()
	{
		getCharOutput = trie[argumentIntIndex*TOKEN_SIZE_BIT/8];
	}

	private void setLoKidIndex()
	{
		intToByteArrayInput = setLoKidIndexInputLoKid;
		intToByteArray();
		trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8] = intToByteArrayOutput[3];
		trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+1] = intToByteArrayOutput[2];
		trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+2] = intToByteArrayOutput[1];
		trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+3] = intToByteArrayOutput[0];
	}
	
	private void setHiKidIndex()
	{
		intToByteArrayInput = setHiKidIndexInputHiKid;
		intToByteArray();
		trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+EQUAL_KID_INDEX_SIZE_BIT/8] = intToByteArrayOutput[3];
		trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+EQUAL_KID_INDEX_SIZE_BIT/8+1] = intToByteArrayOutput[2];
		trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+EQUAL_KID_INDEX_SIZE_BIT/8+2] = intToByteArrayOutput[1];
		trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+EQUAL_KID_INDEX_SIZE_BIT/8+3] = intToByteArrayOutput[0];
	}
	
	private void setEqualKidIndex()
	{
		intToByteArrayInput = setEqualKidIndexInputEqualKid;
		intToByteArray();
		trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8] = intToByteArrayOutput[3];
		trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+1] = intToByteArrayOutput[2];
		trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+2] = intToByteArrayOutput[1];
		trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+3] = intToByteArrayOutput[0];
	}
	private void getLoKidIndex()
	{
		byteArrayToIntInput[0] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8];
		byteArrayToIntInput[1] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+1];
		byteArrayToIntInput[2] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+2];
		byteArrayToIntInput[3] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+3];

		byteArrayToInt();

		getLoKidIndexOutput = byteArrayToIntOutput;
	}
	
	private void getHiKidIndex()
	{
		byteArrayToIntInput[0] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+EQUAL_KID_INDEX_SIZE_BIT/8];
		byteArrayToIntInput[1] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+EQUAL_KID_INDEX_SIZE_BIT/8+1];
		byteArrayToIntInput[2] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+EQUAL_KID_INDEX_SIZE_BIT/8+2];
		byteArrayToIntInput[3] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+EQUAL_KID_INDEX_SIZE_BIT/8+3];

		byteArrayToInt();
		
		getHiKidIndexOutput = byteArrayToIntOutput;
	}
	
	private void getEqualKidIndex()
	{
		byteArrayToIntInput[0] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8];
		byteArrayToIntInput[1] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+1];
		byteArrayToIntInput[2] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+2];
		byteArrayToIntInput[3] = trie[argumentIntIndex*TOKEN_SIZE_BIT/8+CHARACTER_SIZE_BIT/8+IS_WORD_SIZE_BIT/8+ID_SIZE_BIT/8+FREQUENCY_SIZE_BIT/8+LO_KID_INDEX_SIZE_BIT/8+3];

		byteArrayToInt();
		
		getEqualKidIndexOutput = byteArrayToIntOutput;
	}
	
	public void add(String s)
	{
		if(isEmpty)
		{
			isEmpty = false;
			numberOfNodes++;
			argumentIntIndex = 1;
			setCharInputChar = (byte) s.charAt(0);
			setChar();
			
			argumentIntIndex = 1;
			setLoKidIndexInputLoKid = 0;
			setLoKidIndex();
			
			setEqualKidIndexInputEqualKid = 0;
			setEqualKidIndex();
			
			setHiKidIndexInputHiKid = 0;
			setHiKidIndex();
			if(s.length() == 1)
			{
				setAsWord();
				setID();
			}
			lastIndex++;
		}
		
		i = 0;
		stringSize = s.length();
		for(i = 0; i < stringSize; i++)
			stringToByte[i] = (byte) s.charAt(i);
		
		i = 0;
		argumentIntIndex = 1; 
		while(i < stringSize)
		{
			getChar();
			if(stringToByte[i] < getCharOutput)
			{
//				if(n.loKid == null)
				getLoKidIndex();
				if(getLoKidIndexOutput == 0)
				{
					numberOfNodes++;
					setLoKidIndexInputLoKid = lastIndex;
					setLoKidIndex();
					while((lastIndex+1)*TOKEN_SIZE_BIT/8 >= size)
						resize();
					
					argumentIntIndex = lastIndex;
					setCharInputChar = stringToByte[i];
					setChar();
					lastIndex++;
				}
				else
				{
					getLoKidIndex();
					argumentIntIndex = getLoKidIndexOutput;
				}
			}
			else if(stringToByte[i] > getCharOutput)
			{
//				if(n.hiKid == null)
				getHiKidIndex();
				if(getHiKidIndexOutput == 0)
				{
					numberOfNodes++;
					setHiKidIndexInputHiKid = lastIndex;
					setHiKidIndex();
					while((lastIndex+1)*TOKEN_SIZE_BIT/8 >= size)
						resize();
					
					argumentIntIndex = lastIndex;
					setCharInputChar = stringToByte[i];
					setChar();
					lastIndex++;
				}
				else
				{
					getHiKidIndex();
					argumentIntIndex = getHiKidIndexOutput;
				}
			}
			else
			{
				if(i == stringSize - 1)
				{
					isWord();
					if(!isWordOutput)
					{
						setAsWord();
						setID();
					}
					incrementFrequency();
					return;
				}
				
//				if(n.equalKid == null)
				getEqualKidIndex();
				if(getEqualKidIndexOutput == 0)
				{
					numberOfNodes++;
					setEqualKidIndexInputEqualKid = lastIndex;
					setEqualKidIndex();
					while((lastIndex+1)*TOKEN_SIZE_BIT/8 >= size)
						resize();
					
					argumentIntIndex = lastIndex;
					setCharInputChar = stringToByte[i+1];
					setChar();
					lastIndex++;
				}
				else
				{
					getEqualKidIndex();
					argumentIntIndex = getEqualKidIndexOutput;
				}
				i++;
			}	
		}
	}
	
	public int search(String s)
	{
		i = 0;
		stringSize = s.length();
		for(i = 0; i < stringSize; i++)
			stringToByte[i] = (byte) s.charAt(i);

		i = 0;
		argumentIntIndex = 1;
		getChar();
		while(getCharOutput != 0 && i < stringSize)
		{
			if(stringToByte[i] < getCharOutput)
			{
				getLoKidIndex();
				argumentIntIndex = getLoKidIndexOutput;
			}
			else if(stringToByte[i] > getCharOutput)
			{
				getHiKidIndex();
				argumentIntIndex = getHiKidIndexOutput;
			}
			else
			{
				isWord();
				if(i == stringSize - 1 && isWordOutput)
				{
					getFrequency();
					return getFrequencyOutput;
				}
				getEqualKidIndex();
				argumentIntIndex = getEqualKidIndexOutput;
				i++;
			}
			getChar();
		}
		return 0;
	}

	public static void main(String[] args)
	{
		ArrayTrieIterative at = new ArrayTrieIterative();
		at.add("armin");
		at.add("sina");
		at.add("armin");
		System.out.println(at.search("armin"));
	}
}

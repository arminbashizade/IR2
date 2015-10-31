package dictionary;

import read.MyTokenizer;

public class ArrayTrieIterativeOptimized
{	
	public static final int INITIAL_SIZE = 8900000;
	public static final int SIZE_QUANTOM = 64*1024;
	byte[] character = new byte[INITIAL_SIZE];
	int[] ID = new int[INITIAL_SIZE];
	int[] frequency = new int[INITIAL_SIZE];
	int[] loKidIndex = new int[INITIAL_SIZE];
	int[] hiKidIndex = new int[INITIAL_SIZE];
	int[] equalKidIndex = new int[INITIAL_SIZE];
	int size = INITIAL_SIZE;
	boolean isEmpty = true;
	public int numberOfNodes = 0;
	int lastID = 1;
	int lastIndex = 1;
	
	public ArrayTrieIterativeOptimized()
	{
		for(int i = 0; i < size; i++)
		{
			character[i] = 0;
			ID[i] = 0;
			frequency[i] = 0;
			loKidIndex[i] = 0;
			hiKidIndex[i] = 0;
			equalKidIndex[i] = 0;
		}
	}
	
	private void resize()
	{
//		System.out.println("Resizing...\t"+"current:\t"+(size/1024)+" KB, new:\t"+(size*2/1024)+"KB.");
		byte[] tmpChar = new byte[size];
		int[] tmpID = new int[size];
		int[] tmpFrequency = new int[size];
		int[] tmpLoKidIndex = new int[size];
		int[] tmpHiKidIndex = new int[size];
		int[] tmpEqualKidIndex = new int[size];
			
		for(int i = 0; i < size; i++)
		{
			tmpChar[i] = character[i];
			tmpID[i] = ID[i];
			tmpFrequency[i] = frequency[i];
			tmpLoKidIndex[i] = loKidIndex[i];
			tmpHiKidIndex[i] = hiKidIndex[i];
			tmpEqualKidIndex[i] = equalKidIndex[i];
		}
		
		size += SIZE_QUANTOM;
		try{
			character = new byte[size];
			ID = new int[size];
			frequency = new int[size];
			loKidIndex = new int[size];
			hiKidIndex = new int[size];
			equalKidIndex = new int[size];
		}catch(OutOfMemoryError e)
		{
			System.err.println(((2*size+SIZE_QUANTOM)*21)/1024+"KB.");
		}
		for(int i = 0; i < size; i++)
		{
			if(i < size-SIZE_QUANTOM)
			{
				character[i] = tmpChar[i];
				ID[i] = tmpID[i];
				frequency[i] = tmpFrequency[i];
				loKidIndex[i] = tmpLoKidIndex[i];
				hiKidIndex[i] = tmpHiKidIndex[i];
				equalKidIndex[i] = tmpEqualKidIndex[i];
			}
			else
			{
				character[i] = 0;
				ID[i] = 0;
				frequency[i] = 0;
				loKidIndex[i] = 0;
				hiKidIndex[i] = 0;
				equalKidIndex[i] = 0;
			}
		}		
	}

	public void add(String s)
	{
		if(isEmpty)
		{
			isEmpty = false;
			numberOfNodes++;
			
			character[1] = (byte) s.charAt(0);
			loKidIndex[1] = 0;
			hiKidIndex[1] = 0;
			equalKidIndex[1] = 0;
			
			if(s.length() == 1)
			{
				ID[1] = lastID;
				lastID++;
			}
			lastIndex++;
		}
		
		int i = 0;
		int stringSize = s.length();
		int index = 1;
		while(i < stringSize)
		{
			if(s.charAt(i) < character[index])
			{
//				if(n.loKid == null)
				if(loKidIndex[index] == 0)
				{
					numberOfNodes++;
					loKidIndex[index] = lastIndex;
					while((lastIndex+1) >= size)
						resize();
					
					character[lastIndex] = (byte)s.charAt(i);
					lastIndex++;
				}
				else
					index = loKidIndex[index];
			}
			else if(s.charAt(i) > character[index])
			{
//				if(n.hiKid == null)
				if(hiKidIndex[index] == 0)
				{
					numberOfNodes++;
					hiKidIndex[index] = lastIndex;
					while((lastIndex+1) >= size)
						resize();
					
					character[lastIndex] = (byte)s.charAt(i);
					lastIndex++;
				}
				else
					index = hiKidIndex[index]; 
			}
			else
			{
				if(i == stringSize - 1)
				{
					if(ID[index] == 0)
					{
						ID[index] = lastID;
						lastID++;
					}
					frequency[index]++;
					return;
				}
				
//				if(n.equalKid == null)
				if(equalKidIndex[index] == 0)
				{
					numberOfNodes++;
					equalKidIndex[index] = lastIndex;
					while((lastIndex+1) >= size)
						resize();
					
					character[lastIndex] = (byte)s.charAt(i+1);
					lastIndex++;
				}
				index = equalKidIndex[index];
				i++;
			}	
		}
	}
	
	public int search(String s)
	{
		int i = 0;
		int stringSize = s.length();
		int index = 1;
		while(character[index] != 0 && i < stringSize)
		{
			if(s.charAt(i) < character[index])
				index = loKidIndex[index];
			else if(s.charAt(i) > character[index])
				index = hiKidIndex[index];
			else
			{
				if(i == stringSize - 1 && ID[index] != 0)
					return frequency[index];

				index = equalKidIndex[index];
				i++;
			}
		}
		return 0;
	}

	public static void main(String[] args)
	{
		ArrayTrieIterativeOptimized at = new ArrayTrieIterativeOptimized();
		at.add("armin");
		at.add("sina");
		at.add("armin");
		System.out.println(at.numberOfNodes);
		System.out.println(at.search("sina"));
	}
}

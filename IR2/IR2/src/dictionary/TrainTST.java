package dictionary;

import java.util.ArrayList;

import com.sun.xml.internal.ws.api.pipe.NextAction;

public class TrainTST
{	
	public static final int WAGON_SIZE = 1000*1000;
	
	Train character = new Train(WAGON_SIZE, Train.Type.BYTE);
	Train ID = new Train(WAGON_SIZE, Train.Type.INTEGER);
	Train frequency = new Train(WAGON_SIZE, Train.Type.INTEGER);
	Train loKidIndex = new Train(WAGON_SIZE, Train.Type.INTEGER);
	Train hiKidIndex = new Train(WAGON_SIZE, Train.Type.INTEGER);
	Train equalKidIndex = new Train(WAGON_SIZE, Train.Type.INTEGER);
	Train listStarts = new Train(WAGON_SIZE, Train.Type.INTEGER);
	Train listEnds = new Train(WAGON_SIZE, Train.Type.INTEGER);
	Train listNexts = new Train(WAGON_SIZE, Train.Type.INTEGER);
	Train docLists = new Train(WAGON_SIZE, Train.Type.INTEGER);

	boolean isEmpty = true;
	public int numberOfNodes = 0;
	public int lastID = 1;
	int lastIndex = 1;
	int lastIndexOnLists = 1;
	private int listPointer;
	private boolean docExistsInPosting;
	
	public TrainTST()
	{
		for(int i = 0; i < WAGON_SIZE; i++)
		{
			character.setByte(i, (byte)0);
			ID.setInt(i, 0);
			frequency.setInt(i, 0);
			loKidIndex.setInt(i, 0);
			hiKidIndex.setInt(i, 0);
			equalKidIndex.setInt(i, 0);
			listNexts.setInt(i, 0);
		}
	}
	
	public void add(String s, int doc)
	{
		if(isEmpty)
		{
			isEmpty = false;
			numberOfNodes++;
		
			character.setByte(1, (byte) s.charAt(0));
			loKidIndex.setInt(1, 0);
			hiKidIndex.setInt(1, 0);
			equalKidIndex.setInt(1, 0);
			
			if(s.length() == 1)
			{
				ID.setInt(1, lastID);
				lastID++;
			}
			lastIndex++;
		}
		
		int i = 0;
		int stringSize = s.length();
		int index = 1;
		while(i < stringSize)
		{
			byte nodeChar = character.getByte(index);
			if(s.charAt(i) < nodeChar)
			{
//				if(n.loKid == null)
				if(loKidIndex.getInt(index) == 0)
				{
					numberOfNodes++;
					loKidIndex.setInt(index, lastIndex);
					character.setByte(lastIndex, (byte)s.charAt(i));
					lastIndex++;
				}
				else
					index = loKidIndex.getInt(index);
			}
			else if(s.charAt(i) > nodeChar)
			{
//				if(n.hiKid == null)
				if(hiKidIndex.getInt(index) == 0)
				{
					numberOfNodes++;
					hiKidIndex.setInt(index, lastIndex);
					character.setByte(lastIndex, (byte)s.charAt(i));
					lastIndex++;
				}
				else
					index = hiKidIndex.getInt(index);
			}
			else
			{
				if(i == stringSize - 1)
				{
					if(ID.getInt(index) == 0)
					{
						ID.setInt(index, lastID);
						listStarts.setInt(lastID, lastIndexOnLists);
						docLists.setInt(lastIndexOnLists, doc);
						listNexts.setInt(lastIndexOnLists, 0);
						listEnds.setInt(lastID, lastIndexOnLists);
						lastIndexOnLists++;
						lastID++;
					}
					// add doc to posting list
					docExistsInPosting = false;
					listPointer = listEnds.getInt(ID.getInt(index));
					if(docLists.getInt(listPointer) >= doc)
						docExistsInPosting = true;
//					listPointer = listStarts.getInt(ID.getInt(index));
//					while(listNexts.getInt(listPointer) != 0)
//					{
//						if(docLists.getInt(listPointer) == doc)
//						{
//							docExistsInPosting = true;
//							break;
//						}
//						listPointer = listNexts.getInt(listPointer);
//					}
//					if(docLists.getInt(listPointer) == doc)
//						docExistsInPosting = true;

					if(!docExistsInPosting)
					{
						listNexts.setInt(listPointer, lastIndexOnLists);
						docLists.setInt(lastIndexOnLists, doc);
						listNexts.setInt(lastIndexOnLists, 0);
						listEnds.setInt(ID.getInt(index), lastIndexOnLists);
						lastIndexOnLists++;
					}
					frequency.setInt(index, frequency.getInt(index)+1);
					return;
				}
				
//				if(n.equalKid == null)
				if(equalKidIndex.getInt(index) == 0)
				{
					numberOfNodes++;
					equalKidIndex.setInt(index, lastIndex);
					character.setByte(lastIndex, (byte)s.charAt(i+1));
					lastIndex++;
				}
				index = equalKidIndex.getInt(index);
				i++;
			}	
		}
	}
	
	public int getID(String s)
	{
		int i = 0;
		int stringSize = s.length();
		int index = 1;
		byte nodeChar = character.getByte(index);
		while(nodeChar != 0 && i < stringSize)
		{
			if(s.charAt(i) < nodeChar)
				index = loKidIndex.getInt(index);
			else if(s.charAt(i) > nodeChar)
				index = hiKidIndex.getInt(index);
			else
			{
				if(i == stringSize - 1 && ID.getInt(index) != 0)
					return ID.getInt(index);

				index = equalKidIndex.getInt(index);
				i++;
			}
			nodeChar = character.getByte(index);
		}
		return 0;
	}

	public int getFrequency(String s)
	{
		int i = 0;
		int stringSize = s.length();
		int index = 1;
		byte nodeChar = character.getByte(index);
		while(nodeChar != 0 && i < stringSize)
		{
			if(s.charAt(i) < nodeChar)
				index = loKidIndex.getInt(index);
			else if(s.charAt(i) > nodeChar)
				index = hiKidIndex.getInt(index);
			else
			{
				if(i == stringSize - 1 && ID.getInt(index) != 0)
					return frequency.getInt(index);

				index = equalKidIndex.getInt(index);
				i++;
			}
			nodeChar = character.getByte(index);
		}
		return 0;
	}

	private void printPosting(int id)
	{
		listPointer = listStarts.getInt(id);
		if(listPointer == 0)
			System.out.println("Empty list.");
		
		while(listPointer != 0)
		{
			System.out.print(docLists.getInt(listPointer)+"\t");
			listPointer = listNexts.getInt(listPointer);
		}
		System.out.println();
	}

	public ArrayList<Integer> merge(String s1, String s2)
	{
		ArrayList<Integer> result = new ArrayList<Integer>();
		int listPointer1, listPointer2;
		listPointer1 = listStarts.getInt(getID(s1));
		listPointer2 = listStarts.getInt(getID(s2));
		
		int doc1, doc2;
		doc1 = docLists.getInt(listPointer1);
		doc2 = docLists.getInt(listPointer2);
		
		if(doc1 == 0 || doc2 == 0)
			return result;
		
		do
		{
			doc1 = docLists.getInt(listPointer1);
			doc2 = docLists.getInt(listPointer2);

			if(listPointer1 == 0)
				listPointer2 = listNexts.getInt(listPointer2);
			
			if(listPointer2 == 0)
				listPointer1 = listNexts.getInt(listPointer1);
			
			if(doc1 > doc2)
				listPointer2 = listNexts.getInt(listPointer2);
			else if(doc1 < doc2)
				listPointer1 = listNexts.getInt(listPointer1);
			else
			{
				result.add(doc1);
				listPointer2 = listNexts.getInt(listPointer2);
				listPointer1 = listNexts.getInt(listPointer1);
			}
		}while(listPointer1 != 0 || listPointer2 != 0);
				
		return result;
	}
	
	public int getTrainSize()
	{
		return listNexts.size;
	}
	
	public static void main(String[] args)
	{
		TrainTST at = new TrainTST();
		at.add("armin", 1);
		at.add("sina", 1);
		at.add("nojan", 1);
		at.add("pooya", 1);
		
		at.add("pegah", 2);
		at.add("melica", 2);
		at.add("armin", 2);
		at.add("sina", 2);
		
		at.add("armin", 3);
		at.add("pegah", 3);
		
		at.add("armin", 4);
		at.add("melica", 4);
		
		at.add("armin", 5);
		at.add("pooya", 5);
		
		at.add("armin", 6);
		at.add("sina", 6);

		for(Integer i: at.merge("sina", "armin"))
			System.out.println(i);
	}
}

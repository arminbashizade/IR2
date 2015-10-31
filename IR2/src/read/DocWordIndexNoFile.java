package read;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class DocWordIndexNoFile
{
//	int currentDoc;
//	long currentFilePosition;
//	ArrayList<ArrayList<IDFrequency>> list;
//	
//	int size;
//	
//	public DocWordIndexNoFile(String fileName, int mode) throws IOException
//	{
//		list
//		currentFilePosition = 0;
//		size = 0;
//		currentDoc = -1;
//	}
//	
//	public void add(int doc, int wordID) throws IOException
//	{		
//		if(doc != currentDoc)
//		{
//			currentDoc = doc;
//			words.add(-1);
//			frequencies.add(-1);
//			currentFilePosition += 8;
//			
//			size++;
//		}
//		
//		if(!isDupilcate(wordID)) // updates the frequency if it is duplicate
//		{
//			words.add(wordID);
//			outputBuffer.putInt(wordID);
//			frequencies.add(1);
//			outputBuffer.putInt(1);
//			currentFilePosition += 8;
//			size++;
//		}
//	}
//	
//	private boolean isDupilcate(int wordID) throws IOException
//	{
//		long docPosInFile = docPositionInFile.get(currentDoc);
//		if(docPosInFile < docIndexFile.length())
//		{
//			//read from file
//			long filePos = docIndexFile.getFilePointer();
//			
//			long fileSize = docIndexFile.length();
//			long offset = docPositionInFile.get(currentDoc);
//			docIndexFile.seek(offset);
//			while(offset < fileSize)
//			{
//				int wordIDFromFile = docIndexFile.readInt();
//				int frequencyFromFile = docIndexFile.readInt();
//				offset += 8;
//				if(wordIDFromFile == wordID)
//				{
//					docIndexFile.seek(offset-4);
//					docIndexFile.writeInt(frequencyFromFile+1);
//					return true;
//				}
//			}
//			
//			docIndexFile.seek(filePos);
//			
//			if(currentDoc == docPositionInFile.size()-1)
//			{
//				int i = 0;
//				while(i < words.size() && words.get(i) != -1)
//				{
//					if(words.get(i) == wordID)
//					{
//						int f = frequencies.get(i);
//						int tmpPos = outputBuffer.position();
//						outputBuffer.position(i*8+4);
//						outputBuffer.putInt(f+1);
//						frequencies.set(i, f+1);
//						outputBuffer.position(tmpPos);
//						return true;
//					}
//					i++;
//				}
//			}
//		}
//		else
//		{
//			//read from ArrayList
//			int indexInArrayList = size-1;
//			while(words.get(indexInArrayList) != -1)
//				indexInArrayList--;
//
//			indexInArrayList++; // beginning of the word list
//			
//			for(int i = indexInArrayList; i < size;i++)
//			{
//				if(words.get(i) == wordID)
//				{
//					int f = frequencies.get(i);
//					int tmpPos = outputBuffer.position();
//					outputBuffer.position(i*8+4);
//					outputBuffer.putInt(f+1);
//					frequencies.set(i, f+1);
//					outputBuffer.position(tmpPos);
//					return true;
//				}
//			}
//		}
//		
//		return false;
//	}
//
//	public ArrayList<Integer> search(int docNum)
//	{
//		return null;
//	}
//	
//	public static void main(String[] args) throws IOException
//	{
//		DocWordIndexNoFile di = new DocWordIndexNoFile("docIndex", TEST_FILE);
//		di.add(1, 1);
//		di.add(1, 4);
//		di.add(1, 1);
//		di.add(1, 1);
//		di.add(1, 1);
//		di.add(1, 1);
//		di.add(2, 45);
//		di.add(3, 1);
//	}
}
class IDFrequency
{
	int ID;
	int frequency;
}
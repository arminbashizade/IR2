package read;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * @author Melica
 */
public class Streamer {
	String fileName, outPutFileName;    
	long startTime, endTime;
	int buffSize;

	public Streamer(int bufferSize, String file, String outPutFileName){
		fileName = file;
		buffSize = bufferSize;
		this.outPutFileName = outPutFileName;
	}
	long readFile() throws FileNotFoundException, IOException{

		RandomAccessFile main = new RandomAccessFile(fileName, "r");
		FileChannel reader = main.getChannel();
		ByteBuffer buff = ByteBuffer.allocate(buffSize);

		startTime = System.currentTimeMillis();
		while(reader.read(buff) > 0)
			buff.clear();

		reader.close();
		main.close();

		endTime = System.currentTimeMillis();
		long readTime = endTime - startTime;
		return readTime;
	}

	CountAndTime charCount() throws FileNotFoundException, IOException
	{
		CountAndTime countAndTime = new CountAndTime();

		startTime = System.currentTimeMillis();

		RandomAccessFile main = new RandomAccessFile(fileName, "r");
		FileChannel reader = main.getChannel();
		ByteBuffer buff = ByteBuffer.allocate(buffSize);

		int currCharCode = 0;
		int pertPCount = 0;

		buff.clear();

		while(reader.read(buff) > 0)
		{
			buff.flip();
			for(int i = 0 ; i < buff.limit() ; i++)
			{
				currCharCode = (int)((char)buff.get());
				if(currCharCode == 60)
					pertPCount++;
				else if(currCharCode > 47 && currCharCode <58)
					countAndTime.count[currCharCode - 48]++;
				else if(currCharCode > 64 && currCharCode < 91)
					countAndTime.count[currCharCode - 55]++;
				else if(currCharCode > 96 && currCharCode < 123)
					countAndTime.count[currCharCode - 87]++;
			}
			buff.clear();
		}
		countAndTime.count[25] -= pertPCount;

		reader.close();
		main.close();
		endTime = System.currentTimeMillis();
		countAndTime.time = endTime - startTime;

		return countAndTime;
	}

	long lowerCase() throws FileNotFoundException, IOException
	{
		long time;
		int tempCharCode;
		startTime = System.currentTimeMillis();

		//making output file:
		RandomAccessFile outPut = new RandomAccessFile(outPutFileName, "rw");
		FileChannel writer = outPut.getChannel();
		ByteBuffer outPutBuff = ByteBuffer.allocate(buffSize);

		//making input file
		RandomAccessFile main = new RandomAccessFile(fileName, "r");
		FileChannel reader = main.getChannel();
		ByteBuffer buff = ByteBuffer.allocate(buffSize);

		while(reader.read(buff) > 0)
		{
			buff.flip();
			for(int i = 0 ; i < buff.limit() ; i++)
			{
				tempCharCode = (int)((char)buff.get());
				if(tempCharCode > 64 && tempCharCode < 91)
					tempCharCode += 32;
				outPutBuff.put((byte)((char)tempCharCode));
			}
			outPutBuff.flip();
			writer.write(outPutBuff);
			outPutBuff.clear();
			buff.clear();
		}
		writer.close();
		reader.close();

		outPut.close();
		main.close();

		endTime = System.currentTimeMillis();
		time = endTime - startTime;
		return time;
	}


}

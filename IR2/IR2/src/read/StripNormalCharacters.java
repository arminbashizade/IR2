package read;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class StripNormalCharacters
{
	String fileName, outPutFileName;    
	long startTime, endTime;
	int buffSize;

	public StripNormalCharacters(int bufferSize, String file, String outPutFileName){
		fileName = file;
		buffSize = bufferSize;
		this.outPutFileName = outPutFileName;
	}

	void outputAbnormal() throws FileNotFoundException, IOException
	{
		int tempCharCode;
		
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
				if((tempCharCode >= (int)'A' && tempCharCode <= (int)'Z') || 
				   (tempCharCode >= (int)'a' && tempCharCode <= (int)'z') ||
				   (tempCharCode >= (int)'0' && tempCharCode <= (int)'9') ||
				    tempCharCode == (int)' ' || tempCharCode == (int)'\n' ||
				    tempCharCode == (int)'\r' || tempCharCode == (int)'\t')
					continue;
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
	}

	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		StripNormalCharacters s = new StripNormalCharacters(MyTokenizer.BUFF_SIZE, "C:/Users/maxi/Documents/Java/IR2/test.dat", "C:/Users/maxi/Documents/Java/IR2/abnormal.dat");
		s.outputAbnormal();
	}
}

package dictionary;


public class TernaryTrieNode
{
	byte character;
	int ID;
	int frequency;
	boolean isWord;
	TernaryTrieNode loKid, equalKid, hiKid;
	
	public TernaryTrieNode(int ID, byte character, int frequency, boolean isWord)
	{
		loKid = hiKid = equalKid = null;
		this.ID = ID;
		this.character = character;
		this.frequency = frequency;
		this.isWord = isWord;
	}
}

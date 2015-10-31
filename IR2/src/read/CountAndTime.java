package read;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Melica
 */
public class CountAndTime {
    public long time;
    public int[] count;
    public char[] chars;
    public CountAndTime(){
        count = new int[40];
        chars = new char[40];
        for(int i = 0 ; i < 10 ; i++)
            chars[i] = (char)(i + 48);
        for(int j = 10 ; j < 40 ; j++)
            chars[j] = (char)(j + 55);
    }
}

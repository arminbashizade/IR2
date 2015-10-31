package gui;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import com.sun.org.apache.xml.internal.serializer.utils.Utils;

import dictionary.TrainTST;
import read.MyTokenizer;
import read.Normalizer;
import read.StopWords;
import read.TokenDocType;
import stemmer.PaiceStemmer;

public class Server extends JFrame implements ActionListener, PropertyChangeListener
{
	private static final long serialVersionUID = 1L;
	JFrame frame = null;
	JPanel mainPanel = null;
	JTextField searchField = null;
	JButton processFileButton = null;
	JButton searchButton = null;
	JButton selectMainFile = null;
	JButton selectStemminRules = null;
	JTextArea resultArea = null;
	JScrollPane scrollPane = null;
	JFileChooser fileChooser = null;
	JProgressBar progressBar = null;
	Task task = null;
	boolean stemLoaded = false;
	boolean fileLoaded = false;
	String mainFilePath = null;
	String stemmingRulesPath = null;

	Runtime runtime = null;
	long startTime;
	MyTokenizer tokenizer = null;
	PaiceStemmer stemmer = null;
	StopWords stopword = null;
	TrainTST dictionary = null;
	
	static final int GAP = 10;
	static final int FIELD_WIDTH = 300;
	static final int BUTTON_HEIGHT = 25;
	static final int BUTTON_WIDTH = 100;
	static final int FRAME_HEIGHT = 500;
	static final int FRAME_WIDTH = 600;

	class Task extends SwingWorker<Void, Void> {
		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() throws IOException
		{
			startTime = System.currentTimeMillis();
			int word = 0;
			int progress = 0;
			setProgress(0);

			while(tokenizer.hasMoreTokens())
			{
				word++;
				TokenDocType td = tokenizer.getNextToken();
				String s = Normalizer.removeAccents(td.token).toLowerCase();
				if(!stopword.isStopWord(s))
				{
					s = stemmer.stripAffixes(s);
					dictionary.add(s, td.doc);
				}

				if((int) ((long)tokenizer.buffNum * (long)MyTokenizer.BUFF_SIZE * (long)100 / tokenizer.fileSize) > progress)
				{
					progress = (int) ((long)tokenizer.buffNum * (long)MyTokenizer.BUFF_SIZE * (long)100 / tokenizer.fileSize);
					setProgress(progress);
				}
			}

			setProgress(100);
			resultArea.append(word+" wrods.\n");
			resultArea.append(dictionary.lastID+" distinct words.\n");
			System.gc();
			long time = System.currentTimeMillis() - startTime;
			long allocatedMemory = runtime.totalMemory();
			long freeMemory = runtime.freeMemory();
			long usingMemory = allocatedMemory - freeMemory;
			usingMemory /= 1024*1024;
			resultArea.append("Memory usage: "+usingMemory+" MB.\n");
			resultArea.append("Process time: "+((double)time/1000.0)+" sec.\n");
			resultArea.append("Dictionary(TST) size: "+(dictionary.numberOfNodes)+" nodes.\n");
			resultArea.append("Posting lists size: "+dictionary.getTrainSize()+" nodes.\n");
			resultArea.append("\n");
			resultArea.setCaretPosition(resultArea.getText().length());

			try {
				tokenizer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		/*
		 * Executed in event dispatching thread
		 */
		@Override
		public void done()
		{
			java.awt.Toolkit.getDefaultToolkit().beep();
			processFileButton.setEnabled(true);
			searchButton.setEnabled(true);
			setCursor(null); //turn off the wait cursor
		}
	}	
	public Server() throws FileNotFoundException
	{
		runtime = Runtime.getRuntime();
		startTime = System.currentTimeMillis();

		stopword = new StopWords();
		dictionary = new TrainTST();

		Normalizer.initialize();

		//frame
		frame = this;
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setVisible(true);
		frame.setLayout(null);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setResizable(false);

		//panel
		mainPanel = new JPanel();
		mainPanel.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		mainPanel.setVisible(true);
		mainPanel.setLayout(null);

		int newButtonWidth = (FIELD_WIDTH+GAP+BUTTON_WIDTH - 2*GAP)/3; 
		//process file button
		processFileButton = new JButton();
		processFileButton.setBounds((FRAME_WIDTH-(FIELD_WIDTH+GAP+BUTTON_WIDTH))/2+GAP+newButtonWidth+GAP+newButtonWidth, 2*GAP+BUTTON_HEIGHT+GAP+FRAME_HEIGHT-(2*GAP+BUTTON_HEIGHT+GAP+GAP+BUTTON_HEIGHT+GAP+BUTTON_HEIGHT+5*GAP)+GAP, newButtonWidth, BUTTON_HEIGHT);
		processFileButton.setVisible(true);
		processFileButton.setText("Process File");
		processFileButton.addActionListener(this);
		
		//select main file button
		selectMainFile = new JButton();
		selectMainFile.setBounds((FRAME_WIDTH-(FIELD_WIDTH+GAP+BUTTON_WIDTH))/2+GAP+newButtonWidth, 2*GAP+BUTTON_HEIGHT+GAP+FRAME_HEIGHT-(2*GAP+BUTTON_HEIGHT+GAP+GAP+BUTTON_HEIGHT+GAP+BUTTON_HEIGHT+5*GAP)+GAP, newButtonWidth, BUTTON_HEIGHT);
		selectMainFile.setVisible(true);
		selectMainFile.setText("Main file...");
		selectMainFile.addActionListener(this);
		
		//select stemming rules file button
		selectStemminRules = new JButton();
		selectStemminRules.setBounds((FRAME_WIDTH-(FIELD_WIDTH+GAP+BUTTON_WIDTH))/2, 2*GAP+BUTTON_HEIGHT+GAP+FRAME_HEIGHT-(2*GAP+BUTTON_HEIGHT+GAP+GAP+BUTTON_HEIGHT+GAP+BUTTON_HEIGHT+5*GAP)+GAP, newButtonWidth, BUTTON_HEIGHT);
		selectStemminRules.setVisible(true);
		selectStemminRules.setText("Stemming rules...");
		selectStemminRules.addActionListener(this);
		
		//search button
		searchButton = new JButton();
		searchButton.setBounds((FRAME_WIDTH-(FIELD_WIDTH+GAP+BUTTON_WIDTH))/2+FIELD_WIDTH+GAP, 2*GAP, BUTTON_WIDTH, BUTTON_HEIGHT);
		searchButton.setVisible(true);
		searchButton.setText("Search");
		searchButton.addActionListener(this);
		
		//search field
		searchField = new JTextField();
		searchField.setBounds((FRAME_WIDTH-(FIELD_WIDTH+GAP+BUTTON_WIDTH))/2, 2*GAP, FIELD_WIDTH, BUTTON_HEIGHT);
		searchField.setVisible(true);
		searchField.addActionListener(this);

		//result area
		resultArea = new JTextArea(18, 49);
		resultArea.setSize(FIELD_WIDTH+GAP+BUTTON_WIDTH, FRAME_HEIGHT-(2*GAP+BUTTON_HEIGHT+GAP));
		resultArea.setVisible(true);
		resultArea.setFont(new Font("Courier New Bold", Font.BOLD, 14));
		resultArea.setEditable(false);

		//scroll pane
		scrollPane = new JScrollPane(resultArea);
		scrollPane.setBounds((FRAME_WIDTH-(FIELD_WIDTH+GAP+BUTTON_WIDTH))/2, 2*GAP+BUTTON_HEIGHT+GAP, FIELD_WIDTH+GAP+BUTTON_WIDTH, FRAME_HEIGHT-(2*GAP+BUTTON_HEIGHT+GAP+GAP+BUTTON_HEIGHT+GAP+BUTTON_HEIGHT+5*GAP));
		scrollPane.setVisible(true);

		//progress bar
		progressBar = new JProgressBar(0, 100);
		progressBar.setSize(FIELD_WIDTH+GAP+BUTTON_WIDTH, BUTTON_HEIGHT);
		progressBar.setLocation((FRAME_WIDTH-(FIELD_WIDTH+GAP+BUTTON_WIDTH))/2 , 2*GAP+BUTTON_HEIGHT+GAP+FRAME_HEIGHT-(2*GAP+BUTTON_HEIGHT+GAP+GAP+BUTTON_HEIGHT+5*GAP)+GAP);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		
		//file chooser
		javax.swing.filechooser.FileFilter fileFilter = new javax.swing.filechooser.FileFilter() {
			
			@Override
			public String getDescription()
			{
				return "Text";
			}
			
			@Override
			public boolean accept(File f)
			{
				if (f.isDirectory())
					return true;
				
				String extension = f.getName().substring(f.getName().lastIndexOf('.')+1).toLowerCase();
				if (extension != null)
				{
					if (extension.equals("dat") || extension.equals("txt"))
						return true;
				}
				else
					return false;
				
				return false;
}
		};
		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(fileFilter);
		
		
		mainPanel.add(selectMainFile);
		mainPanel.add(selectStemminRules);
		mainPanel.add(progressBar);
		mainPanel.add(searchButton);
		mainPanel.add(processFileButton);
		mainPanel.add(scrollPane);
		mainPanel.add(searchField);
		frame.add(mainPanel);
		frame.repaint();
	}

	public static void main(String[] args) throws FileNotFoundException
	{
		LookAndFeelSetter.setLookAndFeel();
		new Server();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if ("progress" == evt.getPropertyName())
		{
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		} 

	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == searchField)
		{
			searchButton.doClick();
		}
		else if(e.getSource() == searchButton && searchField.getText().length() != 0)
		{
			if(!stemLoaded)
			{
				resultArea.append("Load stemming rules first.\n");
				resultArea.setCaretPosition(resultArea.getText().length());
				return;
			}
			
			if(!fileLoaded)
			{
				resultArea.append("Select main file first.\n");
				resultArea.setCaretPosition(resultArea.getText().length());
				return;
			}
			
			StringTokenizer searchTokenizer = new StringTokenizer(searchField.getText(), "!@#$%^&*()_-=+\r \n?:{}|./,;\\'[]'\"<>");
			ArrayList<String> queryTerms = new ArrayList<String>();
			
			if(!searchTokenizer.hasMoreTokens())
			{
				resultArea.append("Type a search query.");
				resultArea.setCaretPosition(resultArea.getText().length());
				return;
			}
			
			while(searchTokenizer.hasMoreTokens())
			{
				String s = searchTokenizer.nextToken();
				resultArea.append(s);
				if(searchTokenizer.hasMoreTokens())
					resultArea.append(" ");
				if(!stopword.isStopWord(s))
				{
					s = stemmer.stripAffixes(s);
					queryTerms.add(s);
				}
			}
			
			resultArea.append(":\n");

			if(queryTerms.size() == 0)
			{
				resultArea.append("Not found.\n");
				resultArea.setCaretPosition(resultArea.getText().length());
				return;
			}
					
			
			ArrayList<Integer> searchResult = dictionary.merge(queryTerms.get(0), queryTerms.get(0));
			ArrayList<Integer> tmpResult = new ArrayList<Integer>();
			int len = queryTerms.size();
			for(int i = 1; i < len; i += 2)
			{
				if(i+1 < len)
					tmpResult = dictionary.merge(queryTerms.get(i), queryTerms.get(i+1));
				else
					tmpResult = dictionary.merge(queryTerms.get(i), queryTerms.get(i));

				searchResult.retainAll(tmpResult);
			}
			
			for(Integer i: searchResult)
			{
				resultArea.append(i+" ");
			}
			if(searchResult.size() == 0)
				resultArea.append("Not found.");
			resultArea.append("\n\n");
			resultArea.setCaretPosition(resultArea.getText().length());
		}
		else if(e.getSource() == processFileButton)
		{
			if(!stemLoaded)
			{
				resultArea.append("Load stemming rules first.\n");
				resultArea.setCaretPosition(resultArea.getText().length());
				return;
			}
			
			if(!fileLoaded)
			{
				resultArea.append("Select main file first.\n");
				resultArea.setCaretPosition(resultArea.getText().length());
				return;
			}
			
			processFileButton.setEnabled(false);
			searchButton.setEnabled(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			task = new Task();
			task.addPropertyChangeListener(this);
			task.execute();
		}
		else if(e.getSource() == selectMainFile)
		{
			if( fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
			{
				if(!fileChooser.getSelectedFile().exists())
				{
					resultArea.append("File \"" + fileChooser.getSelectedFile().getName() + "\" does not exist.\n");
					resultArea.setCaretPosition(resultArea.getText().length());				
				}
				else
				{
					mainFilePath = fileChooser.getSelectedFile().getAbsolutePath();
					try {
						tokenizer = new MyTokenizer(mainFilePath, "!@#$%^&*()_-=+\r \n?:{}|./,;\\'[]'\"<>", MyTokenizer.BUFF_SIZE);
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					resultArea.append("Main file loaded.\n");
					resultArea.setCaretPosition(resultArea.getText().length());
					fileLoaded = true;
				}
			}
		}
		else if(e.getSource() == selectStemminRules)
		{
			if( fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
			{
				if(!fileChooser.getSelectedFile().exists())
				{
					resultArea.append("File \"" + fileChooser.getSelectedFile().getName() + "\" does not exist.\n");
					resultArea.setCaretPosition(resultArea.getText().length());				
				}
				else
				{
					stemmingRulesPath = fileChooser.getSelectedFile().getAbsolutePath();
					stemmer = new PaiceStemmer(stemmingRulesPath, "");
					resultArea.append("Stemming rules loaded.\n");
					resultArea.setCaretPosition(resultArea.getText().length());
					stemLoaded = true;
				}
			}
		}

	}
}

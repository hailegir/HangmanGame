package hangmangame;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.Random;

import javax.swing.JOptionPane;

public class HmanConnectionHandler extends Thread {
public static int MAX_TRY = 10;
	
	public Socket socket = null;
    public int count = 5;
    public int clientNumber = 0;
    public String guessWord = "";
    RandomAccessFile words = null;
    public String actualWord = "";
    public String guessedWords = "";
    public int totalScore = 0;
    public boolean inProgress = false;
	
	
	public HmanConnectionHandler(Socket clientSocket, int i ) {
		socket = clientSocket;
		this.clientNumber = i;
		
		// Read the words list from the file
		try{
			words = new RandomAccessFile("random.txt","r");
			
		} catch(FileNotFoundException e){
			System.out.println("File Not Found");
			return;
		}
	}
	
	/**
	 * 
	 * @return a new word from the list
	 */
	
	/*
	public String getNewWord() {
		String newWord = new String();
		Random r = new Random();
		
		int i = Math.abs(r.nextInt()%50);
		i++;
		System.out.println("INFO : Random Int :"+ i);

		for (int j=0;j<i;j++) {
			try {
				newWord = words.readLine();	
			} catch (Exception e) {
			} 
		}		
		JOptionPane.showMessageDialog(null, "Selected word:  " + newWord.toUpperCase());
	return newWord.toUpperCase();
     
			
	}
	*/
	public String getNewWord() {
	Random r = new Random();
	int n =	r.nextInt(25143);
	System.out.println(n);  // Random number generated 
	String word = new String();
	String wordSelected = new String();
	int lines = 0;			
	try{
	       BufferedReader br = new BufferedReader(new FileReader("random.txt"));
	       
	       while((word =br.readLine()) != null){
	        lines++;
	            if(lines == n){
	            	 wordSelected=word.toUpperCase();
	            //	word=word.toLowerCase();
	            System.out.println("read from file" + wordSelected);
	            } 
	       }
	}catch(IOException e){
		    	System.out.println(e.toString());
		    }
	JOptionPane.showMessageDialog(null, "Selected word:  " + wordSelected);    
	return wordSelected;
	}
	
    // This method is called when the thread runs
    public void run() {
    	try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			PrintWriter out = new PrintWriter( socket.getOutputStream(),true );

			// read the file name from the socket connection
			String incomingMsgFromClient; 
			
			while((incomingMsgFromClient = in.readLine()).equals("#End#") != true) {
				System.out.println("Msg recieved from the client  " + clientNumber +" : "+ incomingMsgFromClient);
				//out.println("something" + incomingMsgFromClient);
				
				if (incomingMsgFromClient.equals("#START#")) {
					
					// Game was in progress and user pressed new word
					// Decrement his score
										
					// Init the game variables
					count = 5;
					actualWord = getNewWord();
					System.out.println("Actual Word : "+actualWord);
					guessWord = "";
					guessedWords = "";
					inProgress = true;
					
					out.println("#CLIENTNO#"+clientNumber);

					System.out.println("actual word length : "+ actualWord.length());
					for (int i=0;i<actualWord.length();i++ ) {
						guessWord += "_";
					}
					
					System.out.println("#GW#"+count+"#"+ guessWord);
					
					// Generate a the guess word randomly and send back to client
					out.println("#GW#"+count+"#"+ guessWord);
					
				}
				else if (incomingMsgFromClient.contains("#UI#")) {
					
					if (inProgress==false) {
						System.out.println("Click New Word to continue playing");
						continue;
					}
					
					

					// The user input is received in the msg
					String userInput = incomingMsgFromClient.substring(incomingMsgFromClient.lastIndexOf("#")+1,incomingMsgFromClient.length());

					if(!(actualWord.contains(userInput))){
						count--;
						out.println("#STATUS0#");
					}else{
						out.println("#STATUS1#");
					}
					
					guessedWords += userInput + " , ";
					
					System.out.println("INFO : User Input : " + userInput);
					
 					// Some processing and fill in the gap and 
					// make a updated guessString to send back
					
					if (userInput.length() == 1) {

						String tempGuessWord = "";
						for (int i=0;i<actualWord.length();i++) {
							if ( actualWord.charAt(i) == userInput.charAt(0) ) {
								tempGuessWord += actualWord.charAt(i);
							}
							else {
								
								tempGuessWord += guessWord.charAt(i);
								
							}
						}
						
						System.out.println(tempGuessWord);
						
						///////////
						
						
						
						guessWord = tempGuessWord;
					} 
					
					
					if (count == 0 ) {
						System.out.println("Yeaooo");
						if (guessWord.equals(actualWord) || userInput.equals(actualWord) ) {
							out.println("#MATCHED#"+actualWord);
							totalScore++;
						} else {
							out.println("#LOST#"+actualWord);
							
						}
						
						inProgress = false;
						out.println("#GUESSED#"+guessedWords);
						continue;
					}
					
					if (guessWord.equals(actualWord) || userInput.equals(actualWord) ) {
						out.println("#MATCHED#"+actualWord);
						totalScore++;
						inProgress = false;
					} else {
						out.println("#GW#"+count+"#"+guessWord);
						//JOptionPane.showMessageDialog(null, "guess word:  " + guessWord + "count:  " + count);
					}
					
					out.println("#GUESSED#"+guessedWords);						
					
				
				}
				
				// Send the total score
				out.println("#TOTALSCORE#"+totalScore);
				
				
			}
			
		System.out.println("client handle in server Terminated....");
		
		} catch (Exception e) {
		}
    }

}

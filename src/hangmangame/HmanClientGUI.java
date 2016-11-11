package hangmangame;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;


public class HmanClientGUI extends javax.swing.JFrame{
	private Socket clientSocket;
    public boolean isGameRunning = false;
   // int attempt = 10;
    
    JLabel WelcomeMsg;
    JLabel PlayerGuess;
    JLabel PlayerStatus;
    JLabel RemainingAttempt;
    JLabel CongralostMsg;
    JLabel TotalScore;
    JButton SendButton; 
    JButton NewgameButton;
    JTextField inputField;
    JLabel lblListOfGussedWords;
    JLabel PlayerNo;

    
    
    
    public HmanClientGUI(String host,int port) {
    	try {
    	      clientSocket = new Socket(host, port);
    	    } catch (UnknownHostException e) {
    	      System.err.println("Don't know about host: " + host + ".");
    	      System.exit(1);
    	    } catch (IOException e) {
    	      System.err.println("Couldn't get I/O for " +
    	        "the connection to: " + host + ".");
    	      System.exit(1);
    	    }

    	// init the gui components    
    	initComponents();
    }

    private void initComponents() {
    	JFrame mainFrame = new JFrame();
    	
    	JPanel topPanel = new JPanel();
    	JPanel centerPanel = new JPanel();
    	JPanel bottomPanel = new JPanel();
    	
    	JPanel inputPanel = new JPanel();
    	JPanel guessPanel = new JPanel();
    	JPanel attemptPanel = new JPanel();
    	
    	
    	//WelcomeMsg.setFont(new java.awt.Font("Bookman Old Style", 0, 18));
    	WelcomeMsg = new JLabel("Hangman Game", JLabel.CENTER);// Hangman Game
    	PlayerNo = new JLabel("Player: " + 1);
    	
    	PlayerGuess = new JLabel("Please enter a letter", JLabel.CENTER);// your current guess
    	PlayerStatus = new JLabel("No Status!", JLabel.CENTER);// won or lost
    	RemainingAttempt = new JLabel("Remaining Atempt:  " + 5, JLabel.CENTER);// counter value
    	CongralostMsg = new JLabel("To play new game click New Game Button");// Congratulation you won/ you have lost.
    	    	
    	JLabel inputLabel = new JLabel("User Input:  ", JLabel.CENTER);
    	JLabel guessLabel = new JLabel("Your Current Guess:  ", JLabel.CENTER);
    	TotalScore = new JLabel("Total Score :  ", JLabel.CENTER);
    	
    	//JLabel attemptLabel = new JLabel("Remaining Attempt:  ", JLabel.CENTER);
    	
    	SendButton = new JButton("Send");
    	NewgameButton = new JButton("New Game");
    	
    	inputField = new JTextField(10);
    	JLabel listletter = new JLabel("guessed letters  ", JLabel.CENTER);
    	JScrollPane jScrollPane1 = new JScrollPane();
    	lblListOfGussedWords = new JLabel();
    	
    	mainFrame.setLayout(new BorderLayout());	
		mainFrame.setSize(400,300);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//centerPanel.setLayout(new GridLayout(1,3));
		
		mainFrame.add(topPanel, BorderLayout.NORTH);
		topPanel.add(WelcomeMsg);
		topPanel.add(PlayerNo);
		centerPanel.setLayout(new BorderLayout());
		mainFrame.add(centerPanel, BorderLayout.CENTER);
		
		centerPanel.add(inputPanel, BorderLayout.NORTH);
		centerPanel.add(guessPanel, BorderLayout.CENTER);
		centerPanel.add(attemptPanel, BorderLayout.SOUTH);
		
		mainFrame.add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.add(CongralostMsg);
		bottomPanel.add(NewgameButton);
		
		
		
		
		inputPanel.add(inputLabel);
		inputPanel.add(inputField);
		inputPanel.add(SendButton);
		
		guessPanel.add(guessLabel);
		guessPanel.add(PlayerGuess);
		guessPanel.add(PlayerStatus);
		guessPanel.add(listletter);
		guessPanel.add(jScrollPane1);
		
		//attemptPanel.add(attemptLabel);
		attemptPanel.add(RemainingAttempt);
		attemptPanel.add(TotalScore);
		
		
		
		mainFrame.setVisible(true);
    	
		this.isGameRunning = true;
    	this.sendRequest("#START#");
    	
    	lblListOfGussedWords.setText(" ");
    	
    	
		NewgameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartGameActionPerformed(evt);
            }
        });

       

       // btnExit.getAccessibleContext().setAccessibleName("");



        jScrollPane1.setViewportView(lblListOfGussedWords);

       

       
        SendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUserInputActionPerformed(evt);
            }
        });


        
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void btnUserInputActionPerformed(java.awt.event.ActionEvent evt) {
    	if (isGameRunning == true ) {
    		JOptionPane.showMessageDialog(null, "guesswords:  " + lblListOfGussedWords.getText());
    		if(lblListOfGussedWords.getText().contains(inputField.getText().toUpperCase())){
    			inputField.setText("!!! "+inputField.getText() + " is entered before" );
    			PlayerStatus.setText("No Status!");
    		}else{
        	sendRequest("#UI#"+(this.inputField.getText()).toUpperCase());
    		}
    	}
    }
    
   

    private void btnStartGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartGameActionPerformed
    	
    	this.isGameRunning = true;
    	this.sendRequest("#START#");
    	PlayerStatus.setText("No Status!");
    	lblListOfGussedWords.setText(" ");
    	inputField.setText(" ");
    	CongralostMsg.setText("To play new game click New Game Button");
    	
    } //GEN-LAST:event_btnStartGameActionPerformed
    

    void sendRequest(String request) {
        try {
          PrintWriter out = new PrintWriter (
                      clientSocket.getOutputStream(),true);
          
          out.println(request);
          
        } catch (IOException e) {
          e.printStackTrace();
          System.exit(1);
        }
      }

    public Thread processResponseThread = new Thread() {
        void processResponse() {
            try {
              BufferedReader rd = new BufferedReader(new
              InputStreamReader(clientSocket.getInputStream()));
              
              String msg;
              while ((msg = rd.readLine()) != null) {
            	  System.out.println("INFO : Msg Recieved from Server : " + msg);
            	  
            	  // ----- Parse the msg recieved from server and do as appropiate ---
            	  // Msg Format : #GW#"+count+"#"+guessWord
            	  	if (msg.contains("#GW#")) {
							int count;
							String guessWord = new String();

							String str = msg;
							  
							guessWord = str.substring(str.lastIndexOf("#")+1,str.length());  
							
							String temp = str.substring(1,str.lastIndexOf("#"));
							String keyWord = temp.substring(0,temp.lastIndexOf("#"));
							String countStr = temp.substring(temp.lastIndexOf("#")+1,temp.length());
							
							////////////// counterStr need to be modified to counter
							  
						//		System.out.println("INFO : Key words : " + keyWord);
						//		System.out.println("INFO : Count : " + countStr);
						//		System.out.println("INFO : Guess word : " + guessWord);
            	  		
							//  print the  count in the gui
							RemainingAttempt.setText("Remaining Attempts :" + countStr);
							
            	  		String modifiedGuessWord = "";
            	  		for (int i =0;i<guessWord.length();i++) {
            	  			modifiedGuessWord += guessWord.charAt(i)+" ";
            	  		}
            	  		PlayerGuess.setText(modifiedGuessWord);
            	  		
            	  	}
            	  	else if (msg.contains("#CLIENTNO#")){
            	  		String temp = msg.substring(msg.lastIndexOf("#")+1,msg.length());
            	  		PlayerNo.setText("Player no. :  " +temp);
            	  	}
            	  	else if (msg.contains("#MATCHED#")) {
    					String temp = msg.substring(msg.lastIndexOf("#")+1,msg.length());
            	  		String modifiedGuessWord = "";
            	  		for (int i =0;i<temp.length();i++) {
            	  			modifiedGuessWord += temp.charAt(i)+" ";
            	  		}
            	  		PlayerGuess.setText(modifiedGuessWord);
            	  		CongralostMsg.setText("CONGRADULATION YOU WON !");
            	  		inputField.setText("Click Newgame to continue playing");
            	  		
            	  	//	lblTrys.setText("Trys [10] : 10");
            	  	}
            	  	else if (msg.contains("#GUESSED#")) {
    					String temp = msg.substring(msg.lastIndexOf("#")+1,msg.length());
            	  		lblListOfGussedWords.setText(temp);
            	  	} 
            	  	else if (msg.contains("#LOST#")) {
            	  		PlayerStatus.setText("YOU LOST !");
    					String temp = msg.substring(msg.lastIndexOf("#")+1,msg.length());
    					PlayerGuess.setText("[ " + temp +" ]");
    					RemainingAttempt.setText("Remaining Atempt:  " + 5);
            	  	}
            	  	else if (msg.contains("#TOTALSCORE#")) {
    					String temp = msg.substring(msg.lastIndexOf("#")+1,msg.length());
            	  		TotalScore.setText("Total Score : " + temp);
            	  	}
            	  	else if(msg.contains("#STATUS0#")){
            	  		PlayerStatus.setText("LOST! try again");
            	  	}
            	  	else if(msg.contains("#STATUS1#")){
            	  		PlayerStatus.setText("WON! continue");
            	  	}
            	  	
            	  // -----------------------------------------------------------------
            	  
              }
              clientSocket.close();
            
            } catch (IOException e) {
              e.printStackTrace();
              System.exit(1);
            }
          }
    	
        public void run() {
        	processResponse();
        }
    };

    
    
    
    /**
     * @param args the command line arguments
     */
      public static void main(String args[]) {
    	  HmanClientGUI engine = new HmanClientGUI("localhost",4444);
    	  //engine.setVisible(true);
          
    	  
    	  engine.processResponseThread.start();
    }
    
    
    
    
    
    
}

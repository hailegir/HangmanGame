package hangmangame;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class HangmanServer {
	
	public static void main(String[] args) throws IOException {
		boolean listening = true;
		ServerSocket serverSocket = null;
		
		try {
			serverSocket = new ServerSocket(4444);
		} catch (IOException e) {
			System.err.println("Could not listen on port: 4444.");
			System.exit(1);
		}
		
		System.out.println("Server is Listening to port   " + 4444);
		
		int i = 1;

		while(listening) {
			Socket clientSocket = serverSocket.accept();
			Thread serveClient = new HmanConnectionHandler(clientSocket , i++ );
			serveClient.start();
			
			System.out.println("Server ready");

		}

		serverSocket.close();
	}

}

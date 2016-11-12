package networking3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Handler extends Thread {

		//system info
		BufferedReader in;
		PrintWriter out;
		String input;
		Socket socket;
		String info;
		Metahandler meta;
		boolean metaListen;
		boolean open;
		//user info
		boolean loggedin;
		int ID;

		public Handler(Socket socket, Metahandler meta) {
			this.socket = socket;
			info = socket.toString();
			this.meta = meta;
			loggedin = false;
			metaListen = false;
			try{
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		public void receiveMessage(String message){
			if(loggedin){
				out.println(message);
			}
		}
		
		public void sendMessages(String message){
			if(message.trim().length() < 1) return; //no data to send!
			for(Handler h : meta.connections){
				if(h == this){ /*Do nothing if we connect to ourself*/}
				else
					h.receiveMessage("\n " + ID + ": " + message);
			}
			if(metaListen) System.out.println("\n " + ID + ": " + message);
		}

		/**
		 * Toggles server based listening
		 * @return the state of the toggle after the call
		 */
		public boolean toggleListen(){
			metaListen = !metaListen;
			return metaListen;
		}
		
		public void run() {
			try {
				if(!meta.muted) System.out.println("Connection established: " + info);
				//out.println("Hello!");
				
				ID = login();
				
				open = true;
				while (open) {
					input = in.readLine();
					if (input.toLowerCase().equals("quit!") || Thread.interrupted()) {
						open = false;
					}
					if( input.toLowerCase().equals("clear")){
						out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
						input = "";
					}
					
					sendMessages(input);
				}

				out.println("Thank you!");
				shutdown();
				meta.closeConnection(this);
			} catch (Exception e) {
				System.out.println("Client aborted");
				e.printStackTrace();
			} finally {
				System.out.println("Connection closed: " + info);
			}
		}
		
		/**
		 * Verifies authenication
		 * @return the ID of the user
		 */
		public int login(){
			
			out.println("Please enter your user ID:");
			int ID = 0;
			while(ID < 1){
				try {
					ID = Integer.parseInt(in.readLine());
					for(Handler h : meta.connections){
						if(h.ID == ID){
							out.println("This ID has been taken, please choose another");
							ID = 0;
						}
					}
				} catch (Exception e) {
					out.println("Please enter a number!");
					//e.printStackTrace();
				}
			}
			loggedin = true;
			return ID;
		}
		
		public void shutdown(){
			try {
				in.close();
				out.close();
				socket.close();
				open = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
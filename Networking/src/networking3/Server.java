package networking3;

import java.util.Scanner;

public class Server {

	public static void main(String[] args) {
		final int PORT = 9001;
		
		Metahandler meta;
		
		try {
			
			System.out.println("Server has Starting.....");
		
			meta = new Metahandler(PORT);
			
			try {
				meta.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Server started!");
			Scanner input = new Scanner(System.in);
			
			boolean going = true;
			boolean listen = false;
			while(going){
				String tmp = input.nextLine();
				if(tmp.toLowerCase().equals("stop")){
					going = false;
				}
				else if(tmp.length() > 6 && tmp.startsWith("message ")){//tmp.substring(0, 6).equals("message")){
					//send a message to users
					System.out.println("Sending message");
					meta.sendMessage(tmp.substring(7,tmp.length()));
				}
				else if(tmp.equals("hello")){
					System.out.println("Hi there!");
				}
				else if(tmp.equals("listen")){
					meta.listen();
					listen = !listen;
					if(listen){
						System.out.println("\n***Listening mode enabled***\n");
					} else {
						System.out.println("\n***Listening mode disabled***\n");
					}
				}
				else if(tmp.equals("connections")){
					if(meta.connections.size() < 1){
						System.out.println("No active connections");
					} else {
						System.out.println(meta.getConnections());
					}						
				}
				else if(tmp.startsWith("drop ") && tmp.length() > 5){
					int index = Integer.valueOf(tmp.substring(5,tmp.length()));
					if(meta.connections.size()-1 < index){
						//do nothing if we choose a non valid number 
					} else {
						System.out.println("dropping " + index);
						meta.closeConnection(meta.connections.get(index));
					}
				}
				else if("unmute".equals(tmp)){
					meta.unmute();
				}
				else if("mute".equals(tmp)){
					meta.mute();
				}
				
			}

			System.out.println("Server shutting down");
			meta.shutdown();
			// close up everything
			meta.interrupt();
			meta = null;
			input.close();
		} catch (Exception e) {
			System.out.println("System error:");
			e.printStackTrace();
		} finally {
			try{
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Server shut down complete");
			System.exit(0);
		}
	}

}





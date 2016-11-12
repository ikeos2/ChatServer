package networking3;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Metahandler extends Thread {
	int PORT;
	ServerSocket listener;
	ArrayList<Handler> connections;
	boolean listen;
	boolean muted;
	int maxConnections;
	
	public Metahandler(int p){
		PORT = p;
		connections = new ArrayList<Handler>();
		listen = false;
		muted = true;
		maxConnections = 10;
	}
	
	public boolean shutdown(){
		try{
		for(Handler h : connections){
			//close connections
			if(h != null) {
				h.shutdown();
				h.interrupt();
			}
		}
		connections = null;
		listener = null;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public String getConnections(){
		String out = "";
		int i = 0;
		for(Handler object : connections){
			out += i + ": " + object.info + "\n";
			i++;
		}
		
		return out;
	}
	
	public boolean closeConnection(Handler h){
		//h.interrupt();
		h.shutdown();
		connections.remove(h);
		return true;
	}
	
	public void sendMessage(String message){
		for(Handler h : connections){
			try {
				h.receiveMessage("\nSYSTEM: " + message);
			} catch (Exception e) {
				closeConnection(h);
			}
			
		}
	}
	
	public void listen(){
		listen = !listen;
		for(Handler h : connections){
			h.toggleListen();
		}
	}
	
	public void mute(){
		muted = true;
	}
	public void unmute(){
		muted = false;
	}
	
	public void run(){
		try{
			listener = new ServerSocket(PORT);
			
			while (true) {
				if(connections.size() < maxConnections){
					Handler tmp = new Handler(listener.accept(),this);
					tmp.start();
					connections.add(tmp);
					if(listen) tmp.toggleListen();
				} else {
					//Ignore the connection
					Socket tmp = listener.accept();
					tmp.close();
				}
			}
			//listener.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	
}	

package networking3;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * A simple Swing-based client for the chat server.  Graphically
 * it is a frame with a text field for entering messages and a
 * textarea to see the whole dialog.
 *
 * The client follows the Chat Protocol which is as follows.
 * When the server sends "SUBMITNAME" the client replies with the
 * desired screen name.  The server will keep sending "SUBMITNAME"
 * requests as long as the client submits screen names that are
 * already in use.  When the server sends a line beginning
 * with "NAMEACCEPTED" the client is now allowed to start
 * sending the server arbitrary strings to be broadcast to all
 * chatters connected to the server.  When the server sends a
 * line beginning with "MESSAGE " then all characters following
 * this string should be displayed in its message area.
 */
public class Spammer {

    JFrame frame = new JFrame("Chatter");
    JButton button = new JButton("Toggle Connections");

    /**
     * Constructs the client by laying out the GUI and registering a
     * listener with the textfield so that pressing Return in the
     * listener sends the textfield contents to the server.  Note
     * however that the textfield is initially NOT editable, and
     * only becomes editable AFTER the client receives the NAMEACCEPTED
     * message from the server.
     */
    public Spammer() {
        frame.getContentPane().add(new JScrollPane(), "Center");
        //frame.getContentPane().add(button, "toggle");
        frame.pack();
    }

    /**
     * Prompt for and return the address of the server.
     */
    private String getServerAddress() {
        return JOptionPane.showInputDialog(
            frame,
            "Enter IP Address of the Server:",
            "Welcome to the Chatter",
            JOptionPane.QUESTION_MESSAGE);
    }

    private void run() throws IOException {

        // Make connection and initialize streams
        String serverAddress = "127.0.0.1";//getServerAddress();
        ArrayList<Connection> sockets = new ArrayList<Connection>();
        
        //Socket socket = new Socket(serverAddress, 9001);

        while (true) {
        	Connection tmp = new Connection(new Socket(serverAddress, 9001));
        	sockets.add(tmp);
        	tmp.start();
        	try {
				Thread.sleep(530);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    }

    /**
     * Runs the client as an application with a closeable frame.
     */
    public static void main(String[] args) throws Exception {
        Spammer client = new Spammer();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
    
    class Connection extends Thread{
    	
    	Socket socket;
    	BufferedReader in;
    	PrintWriter out;
    	boolean going;
    	
    	public Connection(Socket g){
    		socket = g;
    		going = true;
    		try{
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    	
    	public void run(){
    		//picks a user number and spams messages every 1 seconds
    		while(going){
    			out.println(randInt(0,1000));
    			try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					out.println("Something broke!");
					try {
						socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				}
    		}
    	}
    	
    	public void closeConnection(){
    		try{
    			going = false;
    			out.println("Closing this connection!");
    			socket.close();
    			//this.interrupt();
    		} catch(Exception e) { }
    	}
    	
    	public int randInt(int min, int max) {

    	    // NOTE: Usually this should be a field rather than a method
    	    // variable so that it is not re-seeded every call.
    	    Random rand = new Random();

    	    // nextInt is normally exclusive of the top value,
    	    // so add 1 to make it inclusive
    	    int randomNum = rand.nextInt((max - min) + 1) + min;

    	    return randomNum;
    	}
    }
}



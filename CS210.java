import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;

public class CS210 extends Accounts{
	
    public static int accountNumber;

	public CS210(int accountNumber, double Aria, double Pres) {
		super(accountNumber, Aria, Pres);
		// TODO Auto-generated constructor stub
	}

	/**
     * Runs the server. When a client connects, the server spawns a new thread to do
     * the servicing.
     */
    public static void main(String[] args) throws Exception {
        try (ServerSocket listener = new ServerSocket(4242)) {
	    ExecutorService pool = Executors.newFixedThreadPool(1000);
            while (true) {
                pool.execute(new Talk(listener.accept()));
            }
        }
    }

    private static class Talk implements Runnable {
        private Socket socket;
        double Rate = 10.0;
        int accountNumber;
        String regex = "\\d+";
        String[] userInput;
        
        Talk(Socket socket) {
            this.socket = socket;
        }
        
        public void run() {
            System.out.println("Connected: " + socket);
            try {
                Scanner in = new Scanner(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                while (in.hasNextLine()) {
		    String line = in.nextLine();
		            userInput = line.split(" ");
		    		if (line.equals("State")) {
		    			
		    		}
		    		// Checks that user enters correct syntax for opening an account
		    		if (line.contains("Open") && userInput[1].matches(regex)) {
		    			//accountNumber = (Integer.parseInt(line));
		    			System.out.println("opened account " + userInput[1]);
		    		}
                }
             } catch (Exception e) {
                System.out.println("Error:" + socket);
            } finally {
                try { socket.close(); } catch (IOException e) {}
                System.out.println("Closed: " + socket);
            }
        }
    }
}

/**
 * 
 * @author Chaye Novak - Student Number 902037
 * This class defines the account objects and its elements for usage by the server and brokers.
 */
class Accounts {
	double Aria = 0.0;
	double Pres = 0.0;
    int accountNumber;
	String State;
	
	public static ArrayList<Accounts> Accounts = new ArrayList<Accounts>();
	
	
	public Accounts(int accountNumber, double Aria, double Pres) {
		this.accountNumber = accountNumber;
		this.Aria = Aria;
		this.Pres = Pres;
	}
	
	public ArrayList<Accounts> AccountList(ArrayList<Accounts> accounts) {
		Accounts = accounts; 
		return accounts;
		
	}
	
	public double getAria() {
		return Aria;
	}
	
	public double getPres() {
		return Pres;
	}
	
	 public static void getAccNum(int accountNumber) {
		CS210.accountNumber = accountNumber;
	 }
	 
	/*public ArrayList <Accounts> openAccount() {
		Accounts.add(getAccNum(accountNumber), getAria(), getPres());		
	}*/

}

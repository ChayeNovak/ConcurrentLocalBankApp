import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.Comparator;

public class CS210 extends Accounts{
	
    //public static int accountNumber;
    
    public CS210(int accountNumber, double Aria, double Pres) {
		super(accountNumber, Aria, Pres);
		// TODO Auto-generated constructor stub
	}

	Accounts account = new Accounts(accountNumber, Aria, Aria);
    
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
        
        ArrayList<Accounts> AccountList = new ArrayList<Accounts>();
        //Regular expression, used to ensure correct user input when opening an account (checks whether input contains digits).
        String regex = "\\d+";
        //Array for storing user input
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
		    		//Separates the line of user input into seperate tokens and consumes the spaces between them.
		            userInput = line.split(" ");
		    		// Checks that user enters correct syntax for opening an account
		    		if (line.contains("Open") && userInput[1].matches(regex)) {
		    			//accountNumber = (Integer.parseInt(line));
		    			out.println("Opened account " + userInput[1]);
		    			accountNumber = Integer.parseInt(userInput[1]);
		    			Accounts account = new Accounts(accountNumber, Aria, Pres);
		    			AccountList.add(account);
		    		}
		    		
		    		if (line.equals("State")) {
		    			Collections.sort(AccountList, new Comparator<Accounts>() {
		    						//Sort the list in ascending (numerical) order
		    						public int compare(Accounts a1, Accounts a2) {
										return Integer.valueOf(a1.accountNumber).compareTo(a2.accountNumber);
		    						}
		    			});
		    			
		    			//When user wants to check the state, this outputs all accounts and their currencies in the list.
		    			for(Accounts element : AccountList){
		    		        try{
		    		        	
		    		        	out.println(element.getAccNum() + ": " + "Arian " + element.getAria() + " " + "Pres " + element.getPres());
		    		        }
		    		        catch(Exception e){
		    		            System.out.println("Exception e: " + e);
		    		        }
		    		    }
		    			
		    			out.println("Rate " + Rate);
		    		}
                }
             } catch (Exception e) {
                System.out.println("Error:" + socket);
            } finally {
                try { socket.close(); } catch (IOException e) {}
                System.out.println("Closed: " + socket);
            }
        }
        
        public void setRate (double rateSet) {
        	this.Rate = rateSet;
        }
    }
    
}

/**
 * 
 * @author Chaye Novak - Student Number 902037
 * This class defines the account objects and its elements for usage by the server and brokers.
 */
class Accounts {
	static double Aria = 0.0;
	static double Pres = 0.0;
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
	
	public int getAccNum() {
		return accountNumber;
	}
	
	@Override
	public String toString(){
	    return "Arian " + Double.toString(Aria) + "Pres " + Double.toString(Pres);
	}
	
	/*public double getAccountIndex(int acc) {
		for (double s : Accounts) {
			if (s.accountNumber.equals(getAccNum(acc))) {
				
			}
		}
		return acc;
	}*/
	
	
	
	 public static void getAccNum(int accountNumber) {
		//CS210.accountNumber = accountNumber;
		accountNumber = accountNumber;
	 }
	 
	/*public ArrayList <Accounts> openAccount() {
		Accounts.add(getAccNum(accountNumber), getAria(), getPres());		
	}*/

}

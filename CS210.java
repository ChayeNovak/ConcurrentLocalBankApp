import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

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
        
        List<Accounts> AccountList = new ArrayList<Accounts>();
        
        //Regular expression, used to ensure correct user input when opening an account (checks whether input contains digits).
        String regex = "\\d+";
        //Array for storing user input
        String[] userInput;
        //Array for storing user input containing parentheses (Convert, Transfer)
        Pattern pattern = Pattern.compile("\\d+");
        String inputMatcher = "Convert 0000 (0, 0)";
        Matcher matcher = pattern.matcher(inputMatcher);
        String[] userInput2;
        
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
		            userInput2 = line.split(" |\\(|,|\\)");
		    		// Checks that user enters correct syntax for opening an account
		    		if (line.contains("Open") && userInput[1].matches(regex)) {
		    			//accountNumber = (Integer.parseInt(line));
		    			out.println("Opened account " + userInput[1]);
		    			accountNumber = Integer.parseInt(userInput[1]);
		    			Accounts account = new Accounts(accountNumber, 0, 0);
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
		    		
		    		//Checks if the user attempts to set the conversion rate to 0, if so, output an error message informing this is not possible.
		    		if (line.contains("Rate") && userInput[1].matches(regex) && Double.parseDouble(userInput[1]) <= 0) {
		    			out.println("Please enter a valid conversion rate. This cannot be set to 0.");
		    		}
		    		
		    		//Check if the user enters a valid conversion rate, if so, set Rate to that value.
		    		if (line.contains("Rate") && userInput[1].matches(regex) && Double.parseDouble(userInput[1]) != 0) {
		    			setRate(Double.parseDouble(userInput[1]));
		    		}
		    		
		    		if (line.contains("Convert") && userInput[1].matches(regex)){
			    		for(Accounts acList : AccountList) {
			    	        if(acList.getAccNum() == ((Integer.parseInt(userInput[1])))) {
			    	        	System.out.println(acList.getAccNum());
			    	        	double a;
			    	         	double p;
			    	        	double newArian;
			    	        	double newPres;
			    	        		try {
			    	        		    //System.out.print(matcher.group());
			    	        			System.out.println(userInput2[5]);
			    	        		    a = Double.parseDouble(userInput2[3]);
			    	        		    p = Double.parseDouble(userInput2[5]);
			    	        		    
			    	        		    newArian = acList.getAria() - a + (p / Rate);
			    	        		    newPres = acList.getPres() - a + (p * Rate);
			    	        		    
			    	        		   // convertCurrency(acList.getAccNum(), acList.setArian(newArian), acList.setPres(newPres));
			    	        		    acList.convertCurrency(newArian, newPres);
			    	  
			    	        		} catch (Exception e) {
			    	        				System.out.println(e);
			    	        				e.printStackTrace();
			    	        		}
			    	        		out.println("Converted");
			    	        }
			    	    }
			    		
			    		//Make arraylist Synchronised
		    			AccountList = Collections.synchronizedList(AccountList);
		    	    // NOTE - Can only have a race condition on something with a read action, not just a write action.
		    		}
		    		
		    		if (line.contains("Transfer") && userInput[1].matches(regex)) {
			    		for(Accounts acList : AccountList) {
			    	        if(acList.getAccNum() == ((Integer.parseInt(userInput[1])))) {
			    	        	double a;
			    	         	double p;
			    	        	double newArian;
			    	        	double newPres;
			    	        		try {
			    	        		    //Arian to be transferred
			    	        			System.out.println(userInput2[6]);
			    	        		    a = Double.parseDouble(userInput2[4]);
			    	        		    //Pres to be transferred
			    	        		    p = Double.parseDouble(userInput2[6]);
			    	        		    newArian = acList.getAria() - a;
			    	        		    newPres = acList.getPres() - p;
			    	        		    acList.transferBalance(newArian, newPres);
			    	  
			    	        		} catch (Exception e) {
			    	        				System.out.println(e);
			    	        				e.printStackTrace();
			    	        		}
			    	        }
			    	        
			    	        if(acList.getAccNum() == ((Integer.parseInt(userInput[2])))) {
			    	        	double a;
			    	         	double p;
			    	        	double newArian;
			    	        	double newPres;
		    	        		try {
		    	        		    //Arian to be transferred
		    	        		    a = Double.parseDouble(userInput2[4]);
		    	        		    //Pres to be transferred
		    	        		    p = Double.parseDouble(userInput2[6]);
		    	        		    System.out.println(a + " " + p);
		    	        		    newArian = acList.getAria() + a;
		    	        		    newPres = acList.getPres() + p;
		    	        		    acList.transferBalance(newArian, newPres);
		    	        		   // convertCurrency(acList.getAccNum(), acList.setArian(newArian), acList.setPres(newPres));
		    	        		    //acList.convertCurrency(newArian, newPres);
		    	  
		    	        		} catch (Exception e) {
		    	        				System.out.println(e);
		    	        				e.printStackTrace();
		    	        		}
		    	        }
			    	
			    	    }
			    		
			    		//Make arraylist Synchronised
		    			AccountList = Collections.synchronizedList(AccountList);
		    	    // NOTE - Can only have a race condition on something with a read action, not just a write action.
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
	 double Aria = 0.0;
	 double Pres = 0.0;
    int accountNumber;
    double a;
	double p;
	String State;
	
	//Create array list
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
	
	public void setArian(Double arian) {
		this.Aria = arian;
	}
	
	public void setPres(Double pres) {
		this.Pres = pres;
	}
	
	public int getAccNum() {
		return accountNumber;
	}
	
	public void convertCurrency (double a, double p) {
    	this.Aria = a;
    	this.Pres = p;
    }
	
	public void transferBalance (double a, double p) {
    	this.Aria = a;
    	this.Pres = p;
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

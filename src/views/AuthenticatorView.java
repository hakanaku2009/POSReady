package views;

import utils.MaskThread;

import java.sql.SQLException;
import java.util.Scanner; 
import org.apache.commons.codec.digest.DigestUtils;

import auth.Authenticator;
import config.POSDebugConfig;

public class AuthenticatorView extends View{

	public AuthenticatorView(String env_type) {
		super(env_type);
	}
	
	public void display_command_line() {
		System.out.println(authenticate());
		
		
		
		
	}
	
	public boolean authenticate(){
		String username = "", enc_pass = ""; 
		Scanner in = new Scanner(System.in);
		Authenticator reg = null; 
		boolean username_valid = false; 
		while (!username_valid) {
			System.out.println("Username: ");
			username = in.next(); //username does not have spaces, no need for nextline
			
			try {
				reg = new Authenticator(username, enc_pass);
				if (reg.chkUsername()){
					username_valid = true; 
				}
				else {
					System.out.println("The username is invalid. Please try again.");
				}
			}
			catch (SQLException se) {
				 System.out.println("ERROR: Could not connect to POSReady database. Please check your installation and if mariadb is running.");
				 if (POSDebugConfig.console_debug()) {
					 se.printStackTrace(); 
				 }
			}
			catch (ClassNotFoundException ce) {
				System.out.println("ERROR: Cannot start authenticator. Please check if the mariadb connector jar is present.");
				if (POSDebugConfig.console_debug()) {
					ce.printStackTrace();
				}
			}
		}
		
		boolean password_valid = false; 
		int attempts = 3;  // allow three attempts for now
		while (!password_valid) {
			System.out.println("Password: ");
			enc_pass = in.nextLine().trim(); 
			System.out.println(unEscapeString(enc_pass));
			enc_pass = DigestUtils.sha256Hex(enc_pass);
			try {
				if (reg.chkPassword()) {
					password_valid = true; 
				}
				else if (attempts == 0) {
					return false; 
				}
				else {
					System.out.println("The password is invalid. Please try again. You have " + attempts + " attempts remaining.");
					attempts--; 
				}
			}
			catch (ClassNotFoundException ce) {
				System.out.println("ERROR: Cannot start authenticator. Please check if the mariadb connector jar is present.");
				if (POSDebugConfig.console_debug()) {
					ce.printStackTrace();
				}
			}
			catch (SQLException se) {
				 System.out.println("ERROR: Could not connect to POSReady database. Please check your installation and if mariadb is running.");
				 if (POSDebugConfig.console_debug()) {
					 se.printStackTrace(); 
				 }
			}
		}
		
		return true; 
			
		
		
		
}
	
	public static String unEscapeString(String s){
	    StringBuilder sb = new StringBuilder();
	    for (int i=0; i<s.length(); i++)
	        switch (s.charAt(i)){
	            case '\n': sb.append("\\n"); break;
	            case '\t': sb.append("\\t"); break;
	            // ... rest of escape characters
	            default: sb.append(s.charAt(i));
	        }
	    return sb.toString();
	}
}
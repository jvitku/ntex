package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Reader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
			while(true){
				String str=null;
				try {
		            InputStreamReader isr = new InputStreamReader(System.in);
		            BufferedReader br = new BufferedReader(isr);
		            System.out.println("Enter the line : ");
		            str = br.readLine();
		            System.out.println("You have Enterd : " + str);
		 
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		        
		        String pp ="\\\\b\\{";
		        System.out.println(str);
		        str = str.replaceAll(pp, "\\\\textbf{");
		        System.out.println(str);
			}

	}

}

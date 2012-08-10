package main;

public class Logger {
	
	public boolean debug = false;
	
	private int warnings, errors;
	
	public final String warn = "WARNING: ";
	public final String err = "ERROR: ";
	
	public final String prefix ="nTex "; 
	
	public Logger(){
		warnings = 0;
		errors = 0;
	}
	
	public void warn(String what){
		System.err.println(prefix+warn+what);
		warnings++;
	}
	
	public void err(String what){
		System.err.println(prefix+err+what);
		errors++;
	}
	
	public void log(String what){
		if(debug)
			System.out.println(prefix+what);
	}
	
	public int getErrors(){ return this.errors; }
	public int getWarnings(){ return this.warnings; }

}

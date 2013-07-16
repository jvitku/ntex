package main;

import java.util.ArrayList;

/**
 * Just a list of alternative paths to files (e.g. app/)
 * 
 * @author Jaroslav Vitku
 *
 */
public class AltPaths {
	
	public ArrayList<String> paths;
	public final String here = "./";

	public AltPaths(String[] pths){
		this.paths = new ArrayList<String>();
		this.paths.add(here);
		
		for(int i=0; i<pths.length; i++){
			this.paths.add(pths[i]);
		}
	}

	public AltPaths(String p){
		this.paths = new ArrayList<String>();
		this.paths.add(here);
		this.paths.add(p);
	}
	
	public AltPaths(){
		this.paths = new ArrayList<String>();
		this.paths.add(here);
	}
}

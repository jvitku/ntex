package testPackage;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

import main.AltPaths;
import main.Files;
import main.Logger;

import org.junit.Test;

/**
 * Checks IO operations:
 * 	-all files to read accessible?
 *  -can we write?
 * 
 * @author Jaroslav Vitku
 */
public class IO{

	/**
	 * Read and write tests have to be in one method,
	 * if not, tests do not have to be called in correct order  
	 */
	@SuppressWarnings("unused")
	@Test
	public void generateParseConvert(){
		// first generate notes.ntex
		Logger log = new Logger();
		try {
			BufferedWriter wr = Files.getWriter("notes.ntex", log);
		} catch (FileNotFoundException e) {
			fail("Could not open file notes.tex");
		} catch (IOException e) {
			fail("Could write to file notes.tex");
		}
		
		// then try to parse it into notes.tex
		AltPaths a = new AltPaths("app/");
		try {
			BufferedReader ntex = Files.getReader("notes.ntex", a,log);
		} catch (Exception e) {
			fail("Could not find notex.ntex");
		}
		
		try {
			BufferedReader temp = Files.getReader("template.tex",a, log);
		} catch (FileNotFoundException e) {
			fail("Could not find template.tex");
			
		}
		
	}
	
	
}

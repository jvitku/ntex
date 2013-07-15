package testPackage;

import static org.junit.Assert.*;

import main.Logger;
import main.TemplateAndHelp;

import org.junit.Test;

/**
 * Testing class for Main.java
 * Should follow things what should Main.main() do..
 * 
 * @author Jaroslav Vitku
 *
 */
public class Main {

	/**
	 * Tests what commands like 'ntex generate' 
	 * will provably call
	 */
	@Test
	public void ntexTemplate() {
		main.Main.main(new String[]{"-template","y"});
	}
	
	@Test
	public void ntexSimpleTemplate(){
		main.Main.main(new String[]{"-simpletemplate","y"});
	}
	
	@Test
	public void ntexhelp(){
		main.Main.main(new String[]{"-help","y"});
	}

	/**
	 * Try to compile some ntex file into tex file
	 */
	@Test
	public void compile(){
		main.Main.main(new String[]{"-name","notes"});
		
		
	}
}

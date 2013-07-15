package testPackage;

import static org.junit.Assert.*;

import main.Logger;
import main.TemplateAndHelp;

import org.junit.Test;

/**
 * Testing class TemplateAndHelp
 * 
 * @author Jaroslav Vitku
 *
 */
public class Template {

	@Test
	public void image() {
		
		// create template
		TemplateAndHelp tah = new TemplateAndHelp(new Logger());
		assertTrue(tah.template(1));
	}

}

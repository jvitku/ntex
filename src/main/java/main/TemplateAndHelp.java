package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TemplateAndHelp {

	private final Logger log;
	public TemplateAndHelp(Logger log){
		this.log = log;
	}
	
	public void help(){
		System.out.println(separator+p0+separator+p1+
				separator+p11+separator+p2+separator+p3+separator);
	}
	
	private final String separator = "\n\n======================";

	private final String scriptName = "compile";
	private final String tempName = "notes.ntex";

	
	private final String p0 = "What is it:\n"+
			"-Tool for making fast (simply formatted) notes with picture support\n"+
			"-Write notes into the text file '"+tempName+"'\n"+
			"\n-Java application converts *.ntex file into *.tex file based on provided template files\n"+
			"-Script '"+scriptName+"' does this:\n"+
			"\t-Converts *.ntex file to *.tex file\n"+
			"\t-Generates pdf file\n"+
			"\t-Opens the pdf in pdf viewer";
	
	private final String p1 = "Basic Usage:\n"+
			"-Initialize the folder for notes, run: 'ntex init\n"+
			"-Can generate template ('"+tempName+"') by use of command:"+
			" java -jar nTexProject-x.x.jar -template y\n"+
			"-Edit the '"+tempName+"' file according to formatting rules\n"+
			"-Run the '"+scriptName+"' in order to generate and open pdf";
	
	private final String p11 = "Structure of the document:\n"+
			"-Document is structured by means of:\n"+
			"\t-LaTeX abstract\n" +
			"\t-LaTeX (sub)section commands\n"+
			"\t-Structure is created by means of tabulators and '-' marker";
	
	private final String p2 = "Basic commands:\n"+
			"-\\title{Document name}\n-\\author{Your Name}\n"+
			"-\\b{bold text}\n-\\{italic text}\n"+
			"-\\section{section name}\n-\\subsection{subsection name..}\n"+
			"-\\cite{book}           	\t citaiton of the book\n"+
			"-\\f{name,10,description}   \t add the figure specified by:\n"+
			"\t-name\n\t-width (optional)\n\t-description (optional)\n"+
			"-\\f{name}                  \t reference to already existing figure\n";
			
	private final String p3 = "Literature:\n"+
			"-based on the LaTeX formatting\n"+
			"-at the end of file add e.g. this:\n\n"+
			"\\begin{literatura}{1}\n\\bibitem{kniha} " +
			"\n\tAllman J.: \\emph{Evolving Brains}, Scientific American Library Paperback, 2000.\n\\end{literatura}\n";
	
	
	
	private final String temp1 = "\\title{Name of the document}\n\n\\author{Name of Author}\n\n"+
	"\\begin{abstract}\n\tAbstract in one Line..\n\\end{abstract}\n\n"+
	"\\section{Section}\n\nSome normal text..\n-itemized text\n-Some text in \\b{bold} and \\i{italics}.\n"+
	"\t-higher level..\n\n\\subsection{Some subsection here}\n-Here is an example of some citation: \\cite{kniha}.\n"+
	"\n\n-This item is separated by means of several enters from the above one\n"+
	"-Here we add some figure \\f{a,2,Description of figure}\n"+	
	"-Here is another reference to already placed figure \\f{a}\n"+
	"\t-Note that you don't have to specify width or description to the figure \\f{b, something..}.\n" +
	"\t-Type of figure is determined automatically,currently supported types are:\n" +
	"\t\t-eps\n\t\t-png\n\t\t-jpg\n"+
	"\n\n\\begin{literatura}{1}\n\n\\bibitem{kniha} " +
	"\n\tAllman J.: \\emph{Evolving Brains}, Scientific American Library Paperback, 2000.\n\\end{literatura}";
	
	public void template(){
		// is there folder for pictures?
		if(!this.isThere("fig/")){
			// cannot be created?
			if(!(new File("fig/")).mkdir()){
				log.err("cannot create directory fig/");
			}
		}
		// are there pictures?
		if(!this.isThere("fig/a.png"))
			this.createImage("fig/a", 0);
		if(!this.isThere("fig/b.jpg"))
			this.createImage("fig/b", 1);
		

		this.writeTemplate();
		
		System.out.println("Template generated in the file: "+tempName+"\n"+
				"\t-now edit the file '"+tempName+"' and/or\n"+
				"\t-run the srcipt called '"+scriptName+"' in order to convert " +
						"in into the LaTeX and generate pdf");
	}
	
	private void writeTemplate(){
		
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter(tempName);
			  BufferedWriter out = new BufferedWriter(fstream);
			  out.write(temp1);
			  //Close the output stream
			  out.close();
		}catch (Exception e){//Catch exception if any
			 log.err("Could not write the template to file named: "+tempName);
		}
	}
	
	/**
	 * create an image: 
	 * http://www.java2s.com/Code/Java/2D-Graphics-GUI/DrawanImageandsavetopng.htm
	 * 
	 * @param name
	 * @param type
	 * @return
	 */
	private boolean createImage(String name, int type){
		String app = ".png";
		switch(type){
		case 0:
			app = ".png";
		case 1:
			app = ".jpg";
		}
		
		try{
	     int width = 200, height = 200;
	      // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed
	      // into integer pixels
	      BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	      Graphics2D ig2 = bi.createGraphics();
	      Font font = new Font("TimesRoman", Font.BOLD, 20);
	      ig2.setFont(font);
	      String message = name+app;
	      FontMetrics fontMetrics = ig2.getFontMetrics();
	      int stringWidth = fontMetrics.stringWidth(message);
	      int stringHeight = fontMetrics.getAscent();
	      ig2.setPaint(Color.black);
	      ig2.drawString(message, (width - stringWidth) / 2, height / 2 + stringHeight / 4);

	      switch(type){
	      case 0:
	    	  ImageIO.write(bi, "PNG", new File(name+app));
	      case 1:
	    	  ImageIO.write(bi, "JPG", new File(name+app));
		      
	      }
	      /*
	      ImageIO.write(bi, "PNG", new File("c:\\yourImageName.PNG"));
	      ImageIO.write(bi, "JPEG", new File("c:\\yourImageName.JPG"));
	      ImageIO.write(bi, "gif", new File("c:\\yourImageName.GIF"));
	      ImageIO.write(bi, "BMP", new File("c:\\yourImageName.BMP"));
	      */
	    } catch (IOException ie) {
	      log.err("could not generate image named: "+name+app);
	      return false;
	    }
		return true;
	}
	
	
	private boolean isThere(String name){
		File file=new File(name);
		return file.exists();
	}
	
}

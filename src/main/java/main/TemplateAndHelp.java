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
		System.out.println("hellpig");
		
	}
	
	private final String tempName = "notes.ntex";
	private final String temp1 = "sgfdskghsfd\nkfdsk";
	
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
		if(this.isThere("fig/b.jpg"))
			this.createImage("fig/b", 1);
		

		this.writeTemplate();
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
	    	  ImageIO.write(bi, "JPEG", new File(name+app));
		      
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

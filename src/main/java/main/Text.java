package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class Text {

	private final Logger log;
	public Text(Logger log){
		this.log = log;
	}
	
	
	/**
	 * copy the first part of the template to the resulting stream
	 * @param from
	 * @param to
	 * @returns String defining the default author of the text (saved in template.tex) 
	 */
	public String copyIntro(BufferedReader from, BufferedWriter to){
		String line;
		try{
			// while we can read, copy the lines
			while(true){
				line= from.readLine();
				//System.out.println(line);
				if(line.equalsIgnoreCase("\\begin{document}")){
					to.write(line);
					to.newLine();
					to.newLine();
					break;
				}
				to.write(line);
				to.newLine();
			}
		}catch(Exception e){
			log.err("while trying to copy text from template..");
			return null;
		}
		try{
			// try to find the author, if not found, set the default one, me
			return this.getLineByLatexTag(from, "author",0);
		} catch (IOException e) {
			log.warn("could not find the \\author{??} in the template");
			return new String("\\author{Jaroslav Vitku}");
		}
	}
	
	private String getLineByLatexTag(BufferedReader from, String tag, int which) throws IOException{
		String line;
			// try to find the author, if not found, set the default one, me
			while(true){
				line= from.readLine();
				if(which==1)
					log.log(line);
				
				if(line == null){
					throw new IOException("This tag not found: "+tag);
				}
				
				//System.out.println(line);
				String[] s;
				s = line.split("\\{");
				if(s.length>1)
					log.log("-"+s[0]+"-"+s[1]+"-");
				
				if(s.length>which)
					if(s[which].equalsIgnoreCase("\\"+tag))
						return line;
			}
	}
	
	public String getTitle(String name, BufferedReader br){
		try{
			return this.getLineByLatexTag(br, "title", 0);
		}catch(IOException e){
			log.warn("\\title{} not found  in the nTex file");
			return new String("\\title{--title not found in the '"+name+ "'--}");
		}
	}

	private String findAbstract(BufferedReader from){
		String line;
		// try to find the author, if not found, set the default one, me
		try{
			while(true){
				line= from.readLine();
				
				if(line == null){
					log.err("Abstract not fould");
					return "--no abstract found in ntex file--";
				}
							
				if(line.equalsIgnoreCase("\\begin{abstract}")){
					return from.readLine();
				}
			}
		} catch (IOException e) {
			log.err("Abstract not fould");
			return "--no abstract found in ntex file--";
		}
	}
	
	public void copyAbstract(BufferedReader from, BufferedWriter to){
		System.out.println("abstraaaaaaaaaaaaaaaaaa ");
		String abs = this.findAbstract(from);
		if(abs == null){
			System.out.println("so abstract not found, does not matter.. :-)");
			abs = "\\begin{abstract}\n .. \n \\end{abstract}\n";
		}
			
		this.writeAbstract(to, abs);
	}
	
	private void writeAbstract(BufferedWriter to, String abs){
		try {
			to.write("\\begin{abstract}");
			to.newLine();
			to.write(abs);
			to.newLine();
			to.write("\\end{abstract}");
			to.newLine();
			to.newLine();
			to.write("\\IEEEpeerreviewmaketitle");
			to.newLine();
			to.newLine();
			
		} catch (IOException e1) {
			log.err("could not write abstract to stream..");
		}
	}
	
	private String findAbstractEnd(BufferedReader from){
		String line;
		// try to find the author, if not found, set the default one, me
		try{
			while(true){
				line= from.readLine();
				
				if(line == null){
					log.err("Abstract end not fould");
					return null;
				}
				if(line.equalsIgnoreCase("\\end{abstract}")){
					return this.getIt(from);
				}
			}
		} catch (IOException e) {
			return null;
		}
	}
	
	private String getIt(BufferedReader from){
		String lines = "";
		String line = null;
		try{
			while(true){
				line = from.readLine();
				if(line == null)
					return lines;
				lines = lines+"\n"+line;
			}
		}catch(IOException e){
			lines = "error found";
		}
		return lines;
	}
	
	public String getTheRest(BufferedReader from){
		return this.findAbstractEnd(from); 
	}
	

}




package main;

/**
 * Made by Jaroslav Vitku
 * 
 * stored here: https://bitbucket.org/jvitku/ntexproject/changesets
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;

/*
 * this class parse the nTex file, template.tex file and composes them into a final tex file
 * there are no arguments, the source files should be named:
 * 	-notes.ntex
 *  -template.tex
 *  
 */
public class Main {
	

	public String notes;	// source file
	public String out;		// destination
	
	public final String defName = "notes";	// default name
	public final String template = "template.tex";
	
	
	// here is the list of custom commands, convert all "FROM" custom ones "TO" LaTeX ones..
	public static final String[] from 	= { "\\\\b\\{"    , "\\\\i\\{"}; 	// nTex
	public static final String[] to 	= { "\\\\textbf{" , "\\\\textit{"};	// LaTeX
	
	
	public static final String item ="itemize";
	public static final String vspace = "\\vspace{3mm}";
	
	
	public static void main(String[] args) {
		
		Logger log = new Logger();		// init logging system
		Main m = new Main();		
		
		if(!m.parseArguments(args,log))
			return;
		
		log.log("=============================================================");
		log.log(" ");
		
		BufferedReader ntex = Files.getReader(m.notes, log);
		BufferedReader temp = Files.getReader(m.template, log);
		BufferedWriter wr = Files.getWriter(m.out, log);
		
		if(ntex == null || temp == null || wr == null)
			return;

		// parse the input ntex file, if no \{title} tak found, append also file name
		m.parse(m.notes, ntex, temp, wr, log);
		
		m.conclusion(log);
	}
	
	private boolean parseArguments(String[] args, Logger log){
		
		if((args.length % 2) != 0){
			this.printUsage(log);
			return false;
		}
		
		int poc = 0;
		if(args.length==0){
			log.debug = false;
			this.setupNames(defName);
			return true;
		}
		
		while(poc<args.length){
			if(args[poc].equalsIgnoreCase("-name")){
				this.setupNames(args[poc+1]);
			}
			else if(args[poc].equalsIgnoreCase("-debug"))
				log.debug = true;
			else if(args[poc].equalsIgnoreCase("-help")){
				TemplateAndHelp t = new TemplateAndHelp(log);
				t.help();
				return false;
			}else if(args[poc].equals("-template")){
				TemplateAndHelp t = new TemplateAndHelp(log);
				t.template(1);
				return false;
			}else if(args[poc].equals("-simpletemplate")){
				TemplateAndHelp t = new TemplateAndHelp(log);
				t.template(2);
				return false;
			}
			else{
				log.err("Unrecognized parameter: "+args[poc]+ " "+args[poc+1]);
				this.printUsage(log);
				return false;
			}
			poc = poc+2;
		}
		return true;
	}
	
	private void setupNames(String name){
		this.notes = name+".ntex";
		this.out = name+".tex";
	}
	
	private void printUsage(Logger log){
		log.err("Usage: If no argument found, I am looking for files: '"+defName+".ntex' and '"+template+
				"' and will create file '"+defName+".tex'");
		log.err("Usage: arguments are: \n\t-name name\n\t-debug y\n\t-help y\n" +
				"\t-template y 	\t [generates the template file]");
	}
	
	private void conclusion(Logger log){
		log.log(" ");
		log.log(" ");
		log.log("=============================================================");
		if(log.getErrors()>0)
			log.log("Compilation failed: "+log.getErrors()+" errors found. PDF may, or may not be created..");
		else{
			if(log.getWarnings()>0)
				log.log("Compiled with "+log.getWarnings()+"+ warnings");
			else
				log.log("Compiled successfully :-)");
		}
	}
	
	public void parse(String name, BufferedReader ntex,BufferedReader temp, 
			BufferedWriter wr, Logger log){
		
		Text t = new Text(log);
		
		String title = t.getTitle(name,ntex);
		String author = t.copyIntro(temp,wr,ntex);
		
		Files.writeLine(wr, title, log);
		Files.writeLine(wr, author, log);
		Files.writeLine(wr, "", log);
		Files.writeLine(wr, "\\maketitle\n", log);
		
		// parse the abstract
		t.copyAbstract(Files.getReader(notes, log), wr);
		
		// get entire -ntex- text
		String lines = t.getTheRest(Files.getReader(notes, log));
		
		// replace all 1by1 custom commands
		for(int i=0; i<from.length; i++)
			lines = lines.replaceAll(from[i], to[i]);
		log.log("--------basic formating converted-----------");
		
		// convert text format to LaTeX itemization
		Itemization it = new Itemization(log);
		lines = it.addItemization(lines, vspace);
		log.log("--------itemization added-------------------");
	
		Figures f = new Figures(log);
		lines = f.addFigures(lines);
		log.log("--------pictures added ---------------------");


		Files.writeLine(wr, lines, log);
		Files.writeLine(wr, "\\end{document}", log);
		Files.flushWriter(wr, log);
	}
	

}

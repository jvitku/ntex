package main;

import java.io.File;
import java.util.ArrayList;


/**
 * -split file by lines
 * -for each line:
 * 	-sequentially find the \f{ things
 *  -parse data from thing
 *  -add ref{label}
 *  -check for duplicity
 *  	-if not already in file
 *  		-add to the end of the line
 *  	-else ignore
 *  
 * @author jardavitku
 *
 */
public class Figures {

	
	// figure setup
	public final String marker = "\\\\f\\{";
	public final String placement = "ht";
	public final String folder = "fig/";
	public final String[] fileTypes = new String[]{".png",".jpg",".eps"};
	public final String defW = "6.5";
	public final String nolabel = " ";
	public final String figPrefix = "Obr.";
	
	private Figure fig;
	private ArrayList<String> pics;
	private final Logger log;
	
	public Figures(Logger log){
		this.log = log;
		fig = new Figure(marker,placement,folder,fileTypes,defW);
	}

	public class Figure{
		public String marker, placement, folder;
		public String[] fileTypes;
		private final String temp = 
			"\n--t--\\begin{figure}[--placement--]\n"+
				"--t--\t\\centering\n"+
				"--t--\t\t\\includegraphics[width=--width--cm]"+
								"{--folder----name----type--}\n"+
				"--t--\t\\caption{--caption--}\n"+
				"--t--\t\\label{--label--}\n"+
				"--t--\\end{figure}\n";
		
		public String defW;
		
		private String template;
		
		public Figure(
				String marker, 
				String placement, 
				String folder,
				String[] fileTypes,
				String defW){
			this.marker = marker;
			this.placement = placement;
			this.folder = folder;
			this.fileTypes = fileTypes;
			
			template = temp;
			template = template.replaceAll("--placement--", placement);
			template = template.replaceAll("--folder--", folder);
		}
		
		public String getTemplate(){ return this.template; }
		
	}
	
	
	public String addFigures(String lns){
		String[] lines = lns.split("\n");	// get line by line
		pics = new ArrayList<String>();		// store the names of the pictures
		for(int i=0; i<lines.length; i++){
			log.log("XXX "+lines[i]);
			lines[i] = this.convertLine(lines[i]);
		}

		// compose the output string and return
		String out = "";
		for(int i=0; i<lines.length; i++)
			out+=lines[i]+"\n";
		return out;
	}
	
	
	private String convertLine(String line){
		String completedFigs = "";
		// spit the line by figure references
		String[] parts = line.split(fig.marker);
		if(parts.length==1)
			return line;	// no reference found? ignore
			
		String [] pars;
		String[] split;
		String head, tail, name, width, label, type;
		
		for(int i=0; i<parts.length-1; i++){
			
			// get the parameters of the figure
			split = this.getDescription(parts[i+1]);	// get description and 
			head = split[0];			// description of the figure
			tail = split[1];	
			parts[i+1] = tail;
			
			pars = head.split(",");		// array containing figure parameters
			name = pars[0];				// name of the figure

			parts[i] = parts[i]+this.figPrefix+"\\ref{"+name+"}"; // place the reference
			
			// if fig already placed in the text, just put the reference 
			if(this.alreadyPlaced(name))
				continue;
				
			this.place(name);			// remember the figure
			width = this.getWidth(pars);// parse the parameters
			label = this.getLabel(pars);
			type = this.getType(name);	// try to determine the file type
			
			if(type == null){
				this.printError(name);
				continue;
			}
			
			// create the figure placement and insert the parsed data 
			String text = fig.getTemplate();
			text = text.replaceAll("--width--", width);
			text = text.replaceAll("--name--", name);
			text = text.replaceAll("--type--", type);
			text = text.replaceAll("--caption--",label);
			text = text.replaceAll("--label--",name);
			text = text.replaceAll("--t--",this.makeTabs(this.countTabs(line)));
			
			completedFigs = completedFigs+"\n"+ text;
		}
		// re-assemble the line and append the completed figures
		line = "";
		for(int i=0; i<parts.length; i++)
			line = line + parts[i];
		return line+completedFigs;		
	}
	
	private void printError(String name){
		String x= fig.fileTypes[0];
		for(int i=1; i<fig.fileTypes.length; i++)
			x = x+" "+fig.fileTypes[i]+"";
		
		log.err("File: '"+name+"{"+x+"}' not found!"+
				" \nPlease add the file into the folder '"+fig.folder+
				"' and recompile the nTex file");
	}
	
	/**
	 * determine the type of a file 
	 * @param name
	 * @return file type of null if file not found
	 */
	private String getType(String name){
		// for all supported file types, check if it is there
		for(int i=0; i<fig.fileTypes.length; i++){
			File f = new File(fig.folder+name+fileTypes[i]);
			if(f.exists()){
				return fig.fileTypes[i];
			}
		}
		return null;
	}
	
	/**
	 * find the width of the figure
	 * (can be placed in the description, if not, use the default one)
	 * @param s array in the format: [name, width, label] (only name is necessary)
	 * @return width of the fig
	 */
	private String getWidth(String[] s){
		// no width specified
		if(s.length<2)
			return this.defW;
		// try to parse the width
		try{
			double w = Double.parseDouble(s[1]);
			return Double.toString(w);
		}catch(Exception e){
			log.warn("cannot parse the width for fig named: "+s[0]);
			return this.defW;
		}
	}
	
	private String getLabel(String[] s){
		// no label specified
		if(s.length<3)
			return this.nolabel;
		// accidentally cut some commas in the label?
		if(s.length>3){
			String st = s[2];
			for(int i=1; i<s.length; i++)
				st = st+","+s[i];
			return st;
		}
		// ok situation
		return s[2];
	}
	
	/**
	 * should return 2x1 array with two Strings 
	 * @param secondPart
	 * @return {fig description, rest of the string} 
	 */
	private String[] getDescription(String secondPart){
		String[] tmp = secondPart.split("\\}");
		if(tmp.length <2){
			if(secondPart.charAt(secondPart.length()-1) == '}'){ // here is problem with line endings
				return new String[] {tmp[0], " "};
			}
			log.err("parsing fig descriptor, cannot find closing bracket here: "+secondPart+"|");
		}
		// if accidentally found more brackets, compose the rest back to the original form
		if(tmp.length>2){
			String tail = tmp[1];
			for(int i=2; i<tmp.length; i++)
				tail = tail+"}"+tmp[i];
			
			return new String[]{tmp[0], tail}; 
		}
		return tmp;
	}
	
	private void place(String name){
		pics.add(name);
	}
	
	private boolean alreadyPlaced(String name){
		for(int i=0; i<pics.size(); i++){
			if(pics.get(i).equalsIgnoreCase(name))
				return true;
		}
		return false;
	}
	
	/**
	 * just get the number of tabs at the beginning of line
	 * @param line
	 * @return
	 */
	private int countTabs(String line){
		for(int i=0; i<line.length(); i++)
			if(line.charAt(i) != '\t')
				return i;
		// all tabs
		return line.length();
	}
	
	private String makeTabs(int n){
		String out = "";
		for(int i=0; i<n; i++)
			out += "\t";
		return out;
	}
}

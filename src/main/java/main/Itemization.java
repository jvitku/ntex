package main;

public class Itemization {
	

	
	/**
	 * this converts tabulator specified hierarchical structure of the document to a LaTeX itemization
	 * 
	 * @author j
	 */
	
	
	public class Itemize{
		public String addItem = "\\begin{itemize}\n";
		public String item = "\t\\item ";
		public String endItem = "\\end{itemize}\n";
		public Itemize(){	
		}
	}
	
	private int actualItemDepth = 0;
	private boolean itemizing = false;
	
	private static final String[] itemEnders ={
		"\\\\(sub)*section\\{", "\\\\begin\\{liter"
	};
	
	private Itemize it;
	
	private final Logger log;
	public Itemization(Logger log){
		this.log = log;
		this.it = new Itemize();
	}
	
	/**
	 * here we want to parse text with tabulators and "-" to LaTeX format
	 * @param lines - text
	 * @param it - itemize marker
	 * @param space - vertical space marker
	 * @return 	converted text
	 */
	public String addItemization(String lns, String space){
		
		// split to lines
		String[] lines = lns.split("\n");	// get line by line
		Marker mark;
		
		// for all lines
		for(int i=0; i<lines.length; i++){
			// check whether to interrupt itemization
			//this.pln(i+"before: ", 6,i, lines);
			lines[i] = this.handleItemEnders(lines, i);
			//this.pln(i+"after: ", 6,i, lines);
			
			// count tabs and check for "-" symbol or a free line
			mark = countTabs(lines[i]);
			lines[i] = mark.line;	// line with removed tabs and "-"
			if(mark.comment){ // just ignore comments..
				lines[i] = mark.line;
			}else if(mark.freeLine){
				if(itemizing && !willEnd(lines,i))
					lines[i] = this.makeTabs()+"\t\t"+space;
			}else{
				// new item?
				if(mark.item){
					// the same depth?
					if(mark.nTabs == this.actualItemDepth){
						lines[i] = this.addItem(lines[i]);
					}else if(mark.nTabs > this.actualItemDepth){
//						this.pln("aaa b:", 6, i, lines);
						lines[i] = this.addDeeperItem(lines[i], mark.nTabs);

	//					this.pln("aaa a:", 6, i, lines);
					}else if(mark.nTabs < this.actualItemDepth){
						lines[i] = this.addShallowerItem(lines[i], mark.nTabs);
					}
				// no item, just new line
				}else{
					if(mark.nTabs == this.actualItemDepth){
						lines[i] = this.decreaseBy(lines[i], 1);
					}else{
						int by = this.actualItemDepth-mark.nTabs+1;
						lines[i] = this.decreaseBy(lines[i], by);
					}	
				}
			}

			lines[i] = this.checkDocumentEnd(lines, i);
		}
		
		// compose the output string and return
		String out = "";
		for(int i=0; i<lines.length; i++)
			out+=lines[i]+"\n";
		return out;
	}
	
	
	private String addShallowerItem(String line, int depth){
		String tmp  ="";
		// \t\t\t begin{itemize} until we are there
		while(this.actualItemDepth != depth){
			tmp = tmp+ this.makeTabs() + it.endItem + "\n";
			this.actualItemDepth--;
		}
		itemizing = true;
		// \t\t\t\t \item
		return tmp + this.makeTabs() + it.item + line;
	}
	
	private String addDeeperItem(String line, int depth){
		
		String tmp  ="";
		// not itemizing now?
		if(!this.itemizing && depth > this.actualItemDepth){
			// even when adding new itemization level, have to add \item  
			tmp = it.addItem+it.item+"\n";
			this.itemizing = true;
		}
		// \t\t\t begin{itemize} until we are there
		while(this.actualItemDepth != depth){
			this.actualItemDepth++;
			//tmp = tmp+"\n"+ this.makeTabs() + it.addItem;
			if(depth > this.actualItemDepth)
				tmp = tmp+this.makeTabs() + it.item+"\n"+it.addItem;
			else
				tmp = tmp+this.makeTabs() + it.addItem;
		}
		itemizing = true;
		// \t\t\t\t \item
		return tmp + this.makeTabs()+ it.item+line;
	}
	
	private String addItem(String line){
		if(itemizing)
			return this.makeTabs() + it.item + line;
		else{
			itemizing = true;
			return "\n"+this.makeTabs() + it.addItem+it.item+line;
		}
	}
	
	/**
	 * In order to not to add v-spaces at the end of chapters
	 * we want to identify whether the white line is at the end
	 * of the chapter 
	 * @param lines array with all the lines
	 * @param actual index of the actual line
	 * @return true if there are only white lines left
	 */
	private boolean willEnd(String[] lines, int actual){
		if(actual>=lines.length)
			log.err("Itemiziation: willEnd: out of bounds");
		// for all the following lines:
		// if we will find itemization breaker sooner than 
		// non-white character, we do not want white-spaces
		if(actual == lines.length-1){
			System.out.println("WE: end of document, returning T");
			return true;
		}
		
		String tmp;
		for(int i=actual+1; i<lines.length-1; i++){
			if(this.hasItemEnder(lines[i])){
				return true;
			}
			 
			tmp = lines[i].replaceAll("\\S", "x"); //replace all non-white-spaces with x
			if(!lines[i].equals(tmp)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * determine whether the actual one is the last (non-white) line in the document
	 * @param lines
	 * @param actual
	 * @return
	 */
	private boolean documentWillEnd(String[] lines, int actual){
		if(actual == lines.length){
			return true;
		}
		String tmp;
		for(int i=actual+1; i<lines.length; i++){
			tmp = lines[i].replaceAll("\\S", "x"); //replace all non-white-spaces with x
			if(!lines[i].equals(tmp)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * make tabs which correspond to the actual depth of itemize
	 * @return
	 */
	private String makeTabs(){
		if(this.actualItemDepth==0){
			return "";
		}
		
		String tabs = new String("\t");
		for(int i=1; i<this.actualItemDepth; i++)
			tabs = tabs+"\t";
		return tabs;
	}
	
	/**
	 * check whether there is some itemization breaker (e.g. \section{..)
	 * if so, close all actual itemization and return the result 
	 * @param line
	 * @return result
	 */	
	private String handleItemEnders(String[] lines, int which){
		String line = lines[which];
		
		// if there is some itemization ender (or the EOF will follow), decrease itemization to 0
		if(this.hasItemEnder(lines[which]) && itemizing ){
			line = this.decreaseBy(lines[which], this.actualItemDepth+1);	
			this.itemizing = false;			// no itemization initialized
		}
		return line;
	}
	
	private String checkDocumentEnd(String [] lines, int which){
		String line = lines[which];

		if(this.documentWillEnd(lines, which) && this.itemizing){
				line = this.decreaseByAfterLine(lines[which], this.actualItemDepth+1);
				this.itemizing = false;			// no itemization initialized
		}
		return line;

	}
	
	/**
	 * decrease the actual depth by given number of ..
	 * @param line
	 * @param by
	 * @return
	 */
	private String decreaseBy(String line, int by){
		String tmp = "";
		String tabs = this.makeTabs(); // actual beginning of the line
		if(!itemizing)
			return line;
		
		// if this, then stop itemizing also 
		if(by > this.actualItemDepth){
			this.itemizing = false;
			by = this.actualItemDepth+1;
		}
			
		// from the actual depth by "by" steps..
		for(int i=0; i<by; i++){
			tabs = this.makeTabs();
			tmp = tmp+tabs+it.endItem;
			this.actualItemDepth--;
		}
		//return tmp + this.makeTabs()+ "\t" +line;
		if(this.itemizing)
			return tmp + this.makeTabs()+ "\t" +line;
		else{
			this.actualItemDepth = 0;
			return tmp + this.makeTabs() +line;
		}
	}
	
	private String decreaseByAfterLine(String line, int by){
		String tmp = "";
		String tabs = this.makeTabs(); // actual beginning of the line
		if(!itemizing)
			return line;
		
		// if this, then adjust depth..
		if(by > this.actualItemDepth){
			this.itemizing = false;
			by = this.actualItemDepth+1;
		}
			
		// from the actual depth by "by" steps..
		for(int i=0; i<by; i++){
			tabs = this.makeTabs();
			tmp = tmp+tabs+it.endItem;
			this.actualItemDepth--;
		}
		//return tmp + this.makeTabs()+ "\t" +line;
		if(this.itemizing)
			return line +"\n"+ tmp;
		else{
			this.actualItemDepth = 0;
			return line+ "\n"+tmp;
		}
	}
	
	private boolean hasItemEnder(String line){
		String tmp;
		// for all item changers:
		for(int j=0; j<itemEnders.length; j++){
			tmp = line.replaceAll(itemEnders[j], "x");// try to change
			if(!tmp.equals(line))			// has been changed?
				return true;
		}
		return false;
	}
	
	/**
	 * get number of tabs at the beginning of a given line
	 * @param line
	 * @return how many tabs and whether there is a '-'
	 */
	private Marker countTabs(String line){
		/*
		if(!line.replace("^%*", "XX").equals(line)){
			System.out.println("Commentdetecteeeeeeeeeeed");
			return new Marker(0,false, false, true, line);
		}*/

		if(line.matches("\\s+") || line.length() ==0)
			return new Marker(0,false,true,false,line);
			
		for(int i=0; i<line.length(); i++){
			if(line.charAt(i) != '\t'){
				if(line.charAt(i) == '-'){
					// remove all tabs and '-' from the beginning
					return new Marker(i,true, false,false, line.substring(i+1));
				}
				else{
					// remove all the tabs from the beginning
					return new Marker(i,false, false,false, line.substring(i));
				}
			}
		}
		// all tabs
		return new Marker(line.length(),false, false, false, line);
	}
	
	/**
	 * store the information about the line start
	 * @author jardavitku
	 *
	 */
	private class Marker{
		public int nTabs;	// how many tabs
		public boolean item;// is it item? ("-" at the beginning)
		public boolean freeLine; // is it free line? (no visible chars)
		public String line;
		public boolean comment;	// whether it is a comment
		
		public Marker(int nTabs, boolean item, boolean free, boolean comment, String line){
			this.comment = comment;
			this.nTabs = nTabs;
			this.item = item;
			this.freeLine = free;
			this.line = line;
		}
	}
	
	/**
	 * print out given number of actual lines
	 * @param many
	 * @param lines
	 */
	public void pln(String message, int many, int actual, String [] lines){
		int start;
		if((actual-many)<0){
			start = 0;
			many = actual;
		}
		else
			start = actual-many;
		for(int i=start; i<=actual; i++){
			System.out.println(message+"|"+i+"|"+lines[i]);
		}
	}
	
}

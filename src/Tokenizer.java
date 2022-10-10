//  Author: Mads Rosedahl
//
//  charset=utf-8   
//
//  This program will separate an input file into usual simple tokens
//  in a programming language. It will skip space and line comments
//  and find longest sequences of simple regular expressions in the text
//
//  The lexical analysis is very simple and does not cover escaped characters,
//  double numbers, multi linecomments and a number of other operators.
//

import java.util.*;
import java.io.*;
import java.util.regex.*;

public class Tokenizer{
  public static void main(String[] args){
  	Tokenizer tok=new Tokenizer("Tokenizer.java");
  	while(true){
  		String s=tok.next();
  		if(s==null)break;
  	  System.out.println(s);	
  	}
  }

  //-----------------------------
  private String patterns[] ={
    "\\s+", // whitespcae characters	
    "//.*", // line comment
    "\\w(\\w|\\d)*", // identifiers
    "\\d+", // numbers
    "\\042[^\\042]*\\042", // Strings, octal 42 is the double quote
    "\\+\\+|\\=\\=|\\!\\=|\\&\\&|\\|\\||\\:\\=",  // two charcter operators
    "[\\.\\{\\}\\(\\)\\;\\=\\*\\+\\[\\]\\,\\<\\>\\!\\&\\\\:|]", //single character
  };
  private Pattern[] pats=new Pattern[patterns.length];

  private String file ="";
  private BufferedReader in=null;
  private String line="";
  public int patternNr=0;    // last recognised pattern
  public int skipPatterns=2; // pattern 0 and 1 should be skipped
  private String lookahead=null; // used for lookahead;
  
  Tokenizer(String f){
  	file=f;
  	try{
  		in=new BufferedReader(new FileReader(new File(f)));
  	}catch(Exception e){
  	  System.out.println("cannot open \'"+f+"\'");	
  	}
  	for(int i=0;i<patterns.length;i++)
  	  pats[i]=Pattern.compile(patterns[i]);
  }
  private String nextLine(){ 
  	if(line==null)return null;
  	try{
    	line=in.readLine();
    }catch(Exception e){
  	  System.out.println("cannot read from \'"+file+"\'");	    	
  	  return null;
    }
  	return line;  
  }
  //
  
  private String nextPattern(){
  	// return next matched pattern - including space and comments
  	if(line==null)return null;
  	while(line.equals("")){
  		line=nextLine();
  		if(line==null)return null;
    }
    for(int i=0;i<pats.length;i++){
      Matcher m = pats[i].matcher(line);
      if(!m.lookingAt())continue;
      int end =m.end();
      String r=line.substring(0,end);
      line=line.substring(end);
      patternNr=i;
      return r;
    }
    System.out.println("Ignore line '"+line+"'");
    line=""; return line;
  }
  public String nextProper(){
  	// return next matched pattern - excluding space and comments
    String r=nextPattern();
    while(r!=null&&patternNr<skipPatterns)r=nextPattern();
    return r;  	
  }  
  public String next(){
  	// get next token , with lookahead
  	if(lookahead!=null){
  		String lk=lookahead;lookahead=null;return lk; 
  	}
  	return nextProper();
  }
  public String lookahead(){
  	// look at next token
    if(lookahead==null)lookahead=nextProper();
    return lookahead;	
  }
}
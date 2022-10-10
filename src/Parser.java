//  Author: Mads Rosedahl
//
//  charset=utf-8   
//
// This program contains a recursive decent parser for a small Pascal-like language
// The parser does not build a parse tree but will only check that the input is
// correct according to the grammar.
//
// The conversion from context-free grammar to program is based on the following principles
// - nonterminals are made into functions
// - terminals are made into checks that the symbol is seen in input 
// - choice is done be checking first sets for symbols and follow sets for empty strings 
//  
//
import java.util.*;
import java.io.*;
import java.util.regex.*;

  /*  Grammar for micro-pascal
    program -> "program" decls block "."
    decls   -> empty
            |  decl ";" decls 
    block   -> "begin" stats "end"
    decl    -> "var" name ":" name
    stats   -> empty
            | stat ";" stats         
    stat    -> name ":=" exp
            | "if" exp "then" stat "else" stat
            | "while" exp "do" stat
            | block
     exp    -> term opexp
     term   -> name
            | number
            | "(" exp ")"
     opexp  -> empty
            | "+" exp
            | "-" exp
            | "*" exp
            | "/" exp
            | "=" exp
            
   sample input program:
   
   program 
     var x: integer;
   begin
     x:= 10;
     if x=10 then x := 3 else x := 5 ;
   end.         
  */
  
class Parser{
  //-----------------------------
  Tokenizer tok;
  Parser(Tokenizer tok){this.tok=tok;}
  String lookahead(){return tok.lookahead();}
  String next(){return tok.next();}
  boolean isTok(String s){return eq(s,lookahead());}
  boolean isId(){return isId(lookahead());}
  boolean isId(String s){return Character.isLetter(s.charAt(0));}
  boolean isNum(){return isNum(lookahead());}
  boolean isNum(String s){return Character.isDigit(s.charAt(0));}
  void skipId(){String s=next();pl("skipId "+s);if(!isId(s))err();}
  void skipNum(){String s=next();pl("skipNum "+s);if(!isNum(s))err();}
  void skipTok(String s){String t=next();pl("skipTok "+t);if(!eq(s,t))err();}
  boolean eq(String a,String b){return a.equals(b);}
  void pl(String s){System.out.println(s);}
  void err(){pl("Stop at "+lookahead()); throw new RuntimeException();}//System.exit(0);}
  //
  

  //  program -> "program" decls block "."
  void program(){ skipTok("program"); decls(); block(); skipTok("."); }

  //  decls   -> empty
  //          |  decl ";" decls 
  void decls(){
  	if(isTok("begin")) {} // FOLLOW(decls) = {"begin"}
  	else { decl(); skipTok(";"); decls(); }
  }

  //  block   -> "begin" stats "end"
  void block(){ skipTok("begin"); stats(); skipTok("end"); }

  //  decl    -> "var" name ":" name
  void decl(){ skipTok("var"); skipId(); skipTok(":"); skipId(); }

  //  stats   -> empty
  //          | stat ";" stats         
  void stats(){
    if(isTok("else")||isTok("do")||isTok("end")){} //FOLLOW(stats)
    else { stat(); skipTok(";"); stats(); }
  }  

  //  stat    -> "if" exp "then" stat "else" stat
  //          |  "while" exp "do" stat
  //          |  block
  //          |  name ":=" exp
  void stat(){
     if(isTok("if")){ skipTok("if"); exp(); skipTok("then"); stat();
     	                skipTok("else"); stat(); }	else
     if(isTok("while")){ skipTok("while"); exp(); skipTok("do"); stat();}	else
     if(isTok("begin"))block();	else  // FIRST(block) = {"begin"}
     if(isId()){  skipId(); skipTok(":="); exp();} 
     else err();
     pl("Stat");
  }

  //   exp    -> term opexp
  void exp(){  term(); opexp(); pl("Exp");}

  //   term   -> name
  //          | number
  //          | "(" exp ")"
  void term(){
  	if(isId()) skipId(); else
  	if(isNum()) skipNum(); 
    else  { skipTok("("); exp(); skipTok(")"); } 
    pl("Term"); 	
  }

  //   opexp  -> empty
  //          | "+" exp
  //          | "-" exp
  //          | "*" exp
  //          | "/" exp
  //          | "=" exp
  void opexp(){
    if(isTok("+")){skipTok("+"); exp(); } else	
    if(isTok("-")){skipTok("-"); exp(); } else	
    if(isTok("*")){skipTok("*"); exp(); } else	
    if(isTok("/")){skipTok("/"); exp(); } else	
    if(isTok("=")){skipTok("="); exp(); } 
    else return; // empty
  }

}
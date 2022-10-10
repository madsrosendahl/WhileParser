//  Author: Mads Rosedahl
//
//  charset=utf-8   
//
// This program contains a recursive decent parser for a small Pascal-like language
// This version constructs a parse tree from the input with non-terminals as nodes
// and terminals as leafs
//
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
  
class Parser2Node{
  //-----------------------------
  Tokenizer tok;
  Parser2Node(Tokenizer tok){this.tok=tok;}
  String lookahead(){return tok.lookahead();}
  String next(){return tok.next();}
  boolean isTok(String s){return eq(s,lookahead());}
  boolean isId(){return isId(lookahead());}
  boolean isId(String s){return Character.isLetter(s.charAt(0));}
  boolean isNum(){return isNum(lookahead());}
  boolean isNum(String s){return Character.isDigit(s.charAt(0));}
  Node skipId(){String s=next();if(!isId(s))err();return Node.leaf(s);}
  Node skipNum(){String s=next();if(!isNum(s))err();return Node.leaf(s);}
  Node skipTok(String s){String t=next();if(!eq(s,t))err();return Node.leaf(s);}
  boolean eq(String a,String b){return a.equals(b);}
  void pl(String s){System.out.println(s);}
  void err(){pl("Stop at "+lookahead()); throw new RuntimeException();}//System.exit(0);}
  //
  

  //  program -> "program" decls block "."
  Node program(){
     Node t1=skipTok("program"), t2= decls(),t3= block(),t4= skipTok(".");
     return Node.node("program",t1,t2,t3,t4);}

  //  decls   -> empty
  //          |  decl ";" decls 
  Node decls(){
  	if(isTok("begin")) {return Node.node("decls");} // FOLLOW(decls) = {"begin"}
  	else { Node t1=decl(),t2= skipTok(";"),t3= decls();
           return Node.node("decls",t1,t2,t3);}
  }

  //  block   -> "begin" stats "end"
  Node block(){
    Node t1=skipTok("begin"), t2= stats(), t3= skipTok("end");
    return Node.node("block",t1,t2,t3);
  }

  //  decl    -> "var" name ":" name
  Node decl(){
    Node t1=skipTok("var"), t2= skipId(), t3= skipTok(":"),t4= skipId();
    return Node.node("decl",t1,t2,t3,t4);
  }

  //  stats   -> empty
  //          | stat ";" stats         
  Node stats(){
    if(isTok("else")||isTok("do")||isTok("end")){
      return Node.node("stats");
    } //FOLLOW(stats)
    else {
      Node t1=stat(), t2= skipTok(";"), t3= stats();
      return Node.node("stats",t1,t2,t3);
    }
  }  

  //  stat    -> "if" exp "then" stat "else" stat
  //          |  "while" exp "do" stat
  //          |  block
  //          |  name ":=" exp
  Node stat(){
     if(isTok("if")){
       Node t1=skipTok("if"), t2= exp(), t3= skipTok("then"), t4= stat(),
     	    t5= skipTok("else"), t6= stat();
       return Node.node("stat",t1,t2,t3,t4,t5,t6);
     }	else
     if(isTok("while")){
       Node t1=skipTok("while"), t2= exp(),t3= skipTok("do"), t4= stat();
       return Node.node("stat",t1,t2,t3,t4);
     }	else
     if(isTok("begin")){
       return Node.node("stat",block());
     }	else  // FIRST(block) = {"begin"}
     if(isId()){
       Node t1=skipId(), t2= skipTok(":="), t3= exp();
       return Node.node("stat",t1,t2,t3);
     }
     else {err();return null;}
  }

  //   exp    -> term opexp
  Node exp(){
    Node t1=term(), t2= opexp();
    return Node.node("exp",t1,t2);
  }

  //   term   -> name
  //          | number
  //          | "(" exp ")"
  Node term(){
  	if(isId()) return Node.node("term",skipId()); else
  	if(isNum()) return Node.node("term",skipNum());
    else  { Node t1=skipTok("("),t2= exp(),t3= skipTok(")");
            return Node.node("term",t1,t2,t3);}
  }

  //   opexp  -> empty
  //          | "+" exp
  //          | "-" exp
  //          | "*" exp
  //          | "/" exp
  //          | "=" exp
  Node opexp(){
    if(isTok("+")){return Node.node("opexp",skipTok("+"), exp()); } else
    if(isTok("-")){return Node.node("opexp",skipTok("-"), exp()); } else
    if(isTok("*")){return Node.node("opexp",skipTok("*"), exp()); } else
    if(isTok("/")){return Node.node("opexp",skipTok("/"), exp()); } else
    if(isTok("=")){return Node.node("opexp",skipTok("="), exp()); }
    else return Node.node("opexp"); // empty
  }

}
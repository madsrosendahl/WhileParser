import java.util.List;
import java.util.ArrayList;
import java.io.PrintStream;
import java.io.InputStream;

public class Node {
  // a clas for representing parse trees and abstract syntax trees
  // A node is either a leaf containing a string
  // or a node with a tag and a list of nodes
  //
  private String head; 
  private List<Node> tail;

  private Node(String h){head=h;tail=null;}
  private Node(String h,Node... t){head=h;tail=list(t);}
  private Node(String h,List<Node> t){head=h;tail=t;}

  public static Node leaf(String h){return new Node(h);}
  public static Node node(String h,Node... t){return new Node(h,t);}
  public static Node node(String h,List<Node> t){return new Node(h,t);}
  public static List<Node> list(Node... ns){
    List<Node> r=new ArrayList<>();
    for(Node n:ns)r.add(n);
    return r;	
  }
  public static List<Node> append(Node n,List<Node> ns){	
  	if(ns==null)return list(n); ns.add(0,n);return ns;  }
  public static List<Node> append(List<Node> ns,Node n){	
  	if(ns==null)return list(n); ns.add(n);return ns;  }
  public static List<Node> append(List<Node> ns,List<Node>ms){
  	for(Node m:ms)ns.add(m);return ns;  }

  public static String head(Node n){return n.head;}
  public static List<Node> tail(Node n){return n.tail;}

  // pretty-printer for Nodes
  public static void show(Node n){ n.show(System.out);}
  public static void print(String f,Node n){ PrintStream out=outfile(f);n.show(out);out.close();}


  public static InputStream infile(String f){
    try{return new java.io.FileInputStream(f);}catch(Exception e){return null;}}

  public static PrintStream outfile(String f){
    try{return new PrintStream(f);}catch(Exception e){return null;}}
  	
  private void show(PrintStream out){ show2(out,0,80); nl(out);}
  private void show1(PrintStream out){ 
  	// print on one line
  	out.print(head);
  	if(tail==null)return;
  	out.print("(");
    for(int i=0;i<tail.size();i++){
    	if(i>0)out.print(",");
    	tail.get(i).show1(out);
    }  	
    out.print(")");
  }
  private void show2(PrintStream out,int k,int m){ 
  	// print indented k places, max m per line  	
  	if(m-k<40)m=k+40; // extend lines if deep nesting
  	if(sizeMax(m-k)<m-k){out.print(indent(k));show1(out);return;}
  	out.print(indent(k)+head);
  	if(tail==null)return;
  	out.println("(");
    for(int i=0;i<tail.size();i++){
    	tail.get(i).show2(out,k+2,m);
    	if((i+1)<tail.size())out.println(",");
    }  	
    out.print("\n"+indent(k)+")");
  }
  private int sizeMax(int mx){
    // size of print Node, stop calculate at size mx
    int m = head.length();	
    if(tail==null)return m;
    m+=2;
    for(int i=0;i<tail.size();i++){
      if(i>0)m++;
      if(m>=mx)return m;
      m+= tail.get(i).sizeMax(mx-m);
    }
    return m;
  }
  private static String indent(int k){if(k<=0)return ""; else return " "+indent(k-1);}
  private static void nl(PrintStream out){out.println();}
  //
  
  public static Node readNode(String f){openIn(f);Node n=readNode();closeIn();return n;}
  private static InputStream nodeStream=null;
  private static int lookIn=0;
  private static void openIn(String f){nodeStream=infile(f);readIn();}
  private static void closeIn(){try{nodeStream.close();}catch(Exception e){}}
  private static int peekIn(){return lookIn;}
  private static int readIn(){
  	int r=lookIn;try{lookIn=nodeStream.read();}catch(Exception e){} return r;}
  private static Node readNode(){
    int c=readIn();
    if(c<0)return null;
    while(c==' '||c=='\t'||c=='\n'||c=='\r'){c=readIn(); if(c<0)break;}
    String n=""+((char) c);
    do{
      c=peekIn();	
      if(c==' '||c=='\t'||c=='\n'||c=='\r')break;
      if(c==','||c=='('||c==')')break;
      if(c<0)break;
      n=n+((char) c);
      c=readIn();
    }while(true);
    //System.out.println("Node '"+n+"'");    
    do{
      c=peekIn();	
      if(c==' '||c=='\t'||c=='\n'||c=='\r'){c=readIn();continue;}
      break;
    }while(true);
    if(c!='(')return leaf(n);
    //System.out.println("next '"+((char)c)+"'");    
    c=readIn();
    ArrayList<Node> lst=new ArrayList<>();
    Node m=readNode();
    lst.add(m);
    while(peekIn()==','){
    	c=readIn();
    	m=readNode();
    	lst.add(m);         	
    }
    //System.out.println("Next '"+((char)c)+"'");    
    if(peekIn()==')'){c=readIn();}
    
    return node(n,lst);    
  }
}



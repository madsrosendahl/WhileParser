public class Main {
    public static void main(String[] args) {
       Tokenizer tok=new Tokenizer("src/while.txt");
       //Parser parser=new Parser(tok);
       //parser.program();
        Parser2Node parser2=new Parser2Node(tok);
        Node n=parser2.program();
        Node.show(n);
    }
}
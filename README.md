# WhileParser
A simple recursive decent parser for a small While language

The front end is a Toenizer that shows how to describe the lexical analysis of a language using regular expressions

  white space:                "\s+"

    line comments:              "//.*"

    identifiers:                "\w(\w|\d)*"

    numbers:                    "\d+"

    strings:                    "\\042[^\\042]*\\042"      // octal 42 is the double quote

    two character operators:    "\+\+|\=\=|\!\=|\&\&|\|\||\:\="

    single character operators: "[\.\{\}\(\)\;\=\*\+\[\]\,\<\>\!\&\\:|]", 

Input according to the first tow regular expressions are ignored
The other are send to the parser.

The parse will recognize input based on this grammar

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

The conversion from context-free grammar to program is based on the following principles
- nonterminals are made into functions
 - terminals are made into checks that the symbol is seen in input 
 - choice is done by checking First sets for symbols and Follow sets for empty strings 
  

The simple parser just recognizes input according to the grammars

The repository also contains a parser (Parser2Node) that builds a parse tree during parsing
The tree is constructed with nonterminals as nodes and terminals as leafs
It uses a class Node where a node is either a leaf only containing a string 
or a node with a string and a list of nodes.

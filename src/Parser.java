import java.util.List;
import java.util.ArrayList;

public class Parser {
    private static List<Symbol> tokens;
    private static int index;
    private static List<Integer> rules;
    private static String errorMessage;
    private static ParseTree tree;
    private static boolean verbose;

    /**
     * Constructor of the Parser
     * @param tokens list of symbols returned by the lexical analyzer
     * @param verbose boolean indicating if the user wishes a verbose output
     */
    public Parser(List<Symbol> tokens, boolean verbose) {
        this.tokens = tokens;
        this.index = 0;
        this.rules = new ArrayList<Integer>();
        this.errorMessage = null;
        this.verbose = verbose;
    }

    /**
     * Method called to start the parsing
     * @return the list of rules used by the parser, or null if there was an error during parsing
     */
    public List<Integer> start() {
        tree = program();
        // tree = atom();
        // tree = assign();

        if (errorMessage != null) {
            System.out.println(errorMessage);
            return null;
        } else {
            return rules;
        }
    }

    /**
     * Get the parse tree
     * @return parse tree
     */
    public ParseTree getTree() {
        return tree;
    }

    /**
     * Rule 1 of the grammar, corresponding to the variable Program
     * @return ParseTree containing Program and all its children
     */
    private static ParseTree program() {
        printVerbose(1);
        addRule(1);
        List<ParseTree> children = new ArrayList<ParseTree>();
        match(new Symbol(LexicalUnit.BEGINPROG), children);
        match(new Symbol(LexicalUnit.PROGNAME), children);
        children.add(endLine());
        children.add(code());
        match(new Symbol(LexicalUnit.ENDPROG), children);
        Symbol program = new Symbol("Program");
        ParseTree programTree = new ParseTree(program, children);
        return programTree;
    }

    /**
     * Rules 2 and 3 of the grammar, corresponding to the variable Code
     * @return ParseTree containing Code and all its children
     */
    private static ParseTree code() {
        List<ParseTree> children = new ArrayList<ParseTree>();
        Symbol token = nextToken();
        switch (token.getType()) {
            case VARNAME:
            case IF:
            case WHILE:
            case PRINT:
            case READ:
                printVerbose(2);
                addRule(2);
                children.add(instruction());
                children.add(endLine());
                children.add(code());
                break;
            case ENDPROG:
            case ENDWHILE:
            case ELSE:
            case ENDIF:
                printVerbose(3);
                addRule(3);
                Symbol epsilon = new Symbol("epsilon");
                ParseTree epsilonTree = new ParseTree(epsilon);
                children.add(epsilonTree);
                break;
            default:
                // System.out.println("ERROR" + token.toString());
        }
        Symbol code = new Symbol("Code");
        ParseTree codeTree = new ParseTree(code, children);
        return codeTree;
    }

    /**
     * Rules 4, 5, 6, 7 and 8 of the grammar, corresponding to the variable Instruction
     * @return ParseTree containing Instruction and all its children
     */
    private static ParseTree instruction() {
        List<ParseTree> children = new ArrayList<ParseTree>();
        Symbol token = nextToken();
        switch (token.getType()) {
            case VARNAME:
                printVerbose(4);
                addRule(4);
                children.add(assign());
                break;
            case IF:
                printVerbose(5);
                addRule(5);
                children.add(If());
                break;
            case WHILE:
                printVerbose(6);
                addRule(6);
                children.add(While());
                break;
            case PRINT:
                printVerbose(7);
                addRule(7);
                children.add(print());
                break;
            case READ:
                printVerbose(8);
                addRule(8);
                children.add(read());
                break;
            default:
                // System.out.println("ERROR");
        }
        Symbol instruction = new Symbol("Instruction");
        ParseTree instructionTree = new ParseTree(instruction, children);
        return instructionTree;
    }

    /**
     * Rule 9 of the grammar, corresponding to the variable Assign
     * @return ParseTree containing Assign and all its children
     */
    private static ParseTree assign() {
        printVerbose(9);
        addRule(9);
        List<ParseTree> children = new ArrayList<ParseTree>();
        match(new Symbol(LexicalUnit.VARNAME), children);
        match(new Symbol(LexicalUnit.ASSIGN), children);
        children.add(exprArith());
        Symbol assign = new Symbol("Assign");
        ParseTree assignTree = new ParseTree(assign, children);
        return assignTree;
    }

    /**
     * Rule 10 of the grammar, corresponding to the variable ExprArith
     * @return ParseTree containing ExprArith and all its children
     */
    private static ParseTree exprArith() {
        printVerbose(10);
        addRule(10);
        List<ParseTree> children = new ArrayList<ParseTree>();
        children.add(prod());
        children.add(exprArithPrime());
        Symbol exprArith = new Symbol("ExprArith");
        ParseTree exprArithTree = new ParseTree(exprArith, children);
        return exprArithTree;
    }

    /**
     * Rules 11, 12 and 13 of the grammar, corresponding to the variable ExprArith'
     * @return ParseTree containing ExprArith' and all its children
     */
    private static ParseTree exprArithPrime() {
        List<ParseTree> children = new ArrayList<ParseTree>();
        Symbol token = nextToken();
        switch (token.getType()) {
            case PLUS:
                printVerbose(11);
                addRule(11);
                match(new Symbol(LexicalUnit.PLUS), children);
                children.add(prod());
                children.add(exprArithPrime());
                break;
            case MINUS:
                printVerbose(12);
                addRule(12);
                match(new Symbol(LexicalUnit.MINUS), children);
                children.add(prod());
                children.add(exprArithPrime());
                break;
            case ENDLINE:
            case RPAREN:
            case GT:
            case EQ:
                printVerbose(13);
                addRule(13);
                Symbol epsilon = new Symbol("epsilon");
                ParseTree epsilonTree = new ParseTree(epsilon);
                children.add(epsilonTree);
                break;
            default:
                break;
        }
        Symbol exprArithPrime = new Symbol("ExprArith'");
        ParseTree exprArithPrimeTree = new ParseTree(exprArithPrime, children);
        return exprArithPrimeTree;
    }

    /**
     * Rule 14 of the grammar, corresponding to the variable Prod
     * @return ParseTree containing Prod and all its children
     */
    private static ParseTree prod() {
        printVerbose(14);
        addRule(14);
        List<ParseTree> children = new ArrayList<ParseTree>();
        children.add(atom());
        children.add(prodPrime());
        Symbol prod = new Symbol("Prod");
        ParseTree prodTree = new ParseTree(prod, children);
        return prodTree;
    }

    /**
     * Rules 15, 16 and 17 of the grammar, corresponding to the variable Prod'
     * @return ParseTree containing Prod' and all its children
     */
    private static ParseTree prodPrime() {
        List<ParseTree> children = new ArrayList<ParseTree>();
        Symbol token = nextToken();
        switch (token.getType()) {
            case TIMES:
                printVerbose(15);
                addRule(15);
                match(new Symbol(LexicalUnit.TIMES), children);
                children.add(atom());
                children.add(prodPrime());
                break;
            case DIVIDE:
                printVerbose(16);
                addRule(16);
                match(new Symbol(LexicalUnit.DIVIDE), children);
                children.add(atom());
                children.add(prodPrime());
                break;
            case ENDLINE:
            case GT:
            case EQ:
            case PLUS:
            case MINUS:
            case RPAREN:
                printVerbose(17);
                addRule(17);
                Symbol epsilon = new Symbol("epsilon");
                ParseTree epsilonTree = new ParseTree(epsilon);
                children.add(epsilonTree);
                break;
        }
        Symbol prodPrime = new Symbol("Prod'");
        ParseTree prodPrimeTree = new ParseTree(prodPrime, children);
        return prodPrimeTree;
    }

    /**
     * Rules 18, 19, 20 and 21 of the grammar, corresponding to the variable Atom
     * @return ParseTree containing Atom and all its children
     */
    private static ParseTree atom() {
        List<ParseTree> children = new ArrayList<ParseTree>();
        Symbol token = nextToken();
        switch (token.getType()) {
            case MINUS:
                printVerbose(18);
                addRule(18);
                match(new Symbol(LexicalUnit.MINUS), children);
                children.add(atom());
                break;
            case LPAREN:
                printVerbose(19);
                addRule(19);
                match(new Symbol(LexicalUnit.LPAREN), children);
                children.add(exprArith());
                match(new Symbol(LexicalUnit.RPAREN), children);
                break;
            case VARNAME:
                printVerbose(20);
                addRule(20);
                match(new Symbol(LexicalUnit.VARNAME), children);
                break;
            case NUMBER:
                printVerbose(21);
                addRule(21);
                match(new Symbol(LexicalUnit.NUMBER), children);
                break;
            default:
                // System.out.println("ERROR ATOM");
        }
        Symbol atom = new Symbol("Atom");
        ParseTree tree = new ParseTree(atom, children);
        return tree;
    }

    /**
     * Rule 22 of the grammar, corresponding to the variable If
     * @return ParseTree containing If and all its children
     */
    private static ParseTree If() {
        printVerbose(22);
        addRule(22);
        List<ParseTree> children = new ArrayList<ParseTree>();
        match(new Symbol(LexicalUnit.IF), children);
        match(new Symbol(LexicalUnit.LPAREN), children);
        children.add(cond());
        match(new Symbol(LexicalUnit.RPAREN), children);
        match(new Symbol(LexicalUnit.THEN), children);
        children.add(endLine());
        children.add(code());
        children.add(ifTail());
        Symbol ifSymbol = new Symbol("If");
        ParseTree ifTree = new ParseTree(ifSymbol, children);
        return ifTree;
    }

    /**
     * Rules 23 and 24 of the grammar, corresponding to the variable IfTail
     * @return ParseTree containing IfTail and all its children
     */
    private static ParseTree ifTail() {
        List<ParseTree> children = new ArrayList<ParseTree>();
        Symbol token = nextToken();
        switch (token.getType()) {
            case ENDIF:
                printVerbose(23);
                addRule(23);
                match(new Symbol(LexicalUnit.ENDIF), children);
                break;
            case ELSE:
                printVerbose(24);
                addRule(24);
                match(new Symbol(LexicalUnit.ELSE), children);
                children.add(endLine());
                children.add(code());
                match(new Symbol(LexicalUnit.ENDIF), children);
                break;
            default:
                // System.out.println("ERROR");
        }
        Symbol ifTail = new Symbol("IfTail");
        ParseTree ifTailTree = new ParseTree(ifTail, children);
        return ifTailTree;
    }

    /**
     * Rule 25 of the grammar, corresponding to the variable Cond
     * @return ParseTree containing Cond and all its children
     */
    private static ParseTree cond() {
        printVerbose(25);
        addRule(25);
        List<ParseTree> children = new ArrayList<ParseTree>();
        children.add(exprArith());
        children.add(comp());
        children.add(exprArith());
        Symbol cond = new Symbol("Cond");
        ParseTree condTree = new ParseTree(cond, children);
        return condTree;
    }

    /**
     * Rules 26 and 27 of the grammar, corresponding to the variable Comp
     * @return ParseTree containing Comp and all its children
     */
    private static ParseTree comp() {
        List<ParseTree> children = new ArrayList<ParseTree>();
        Symbol token = nextToken();
        switch (token.getType()) {
            case EQ:
                printVerbose(26);
                addRule(26);
                match(new Symbol(LexicalUnit.EQ), children);
                break;
            case GT:
                printVerbose(27);
                addRule(27);
                match(new Symbol(LexicalUnit.GT), children);
                break;
            
            default:
                // System.out.println("ERROR");
        }
        Symbol comp = new Symbol("Comp");
        ParseTree compTree = new ParseTree(comp, children);
        return compTree;
    }

    /**
     * Rule 28 of the grammar, corresponding to the variable While
     * @return ParseTree containing While and all its children
     */
    private static ParseTree While() {
        printVerbose(28);
        addRule(28);
        List<ParseTree> children = new ArrayList<ParseTree>();
        match(new Symbol(LexicalUnit.WHILE), children);
        match(new Symbol(LexicalUnit.LPAREN), children);
        children.add(cond());
        match(new Symbol(LexicalUnit.RPAREN), children);
        match(new Symbol(LexicalUnit.DO), children);
        children.add(endLine());
        children.add(code());
        match(new Symbol(LexicalUnit.ENDWHILE), children);
        Symbol whileSymbol = new Symbol("While");
        ParseTree whileTree = new ParseTree(whileSymbol, children);
        return whileTree;
    }

    /**
     * Rule 29 of the grammar, corresponding to the variable Print
     * @return ParseTree containing Print and all its children
     */
    private static ParseTree print() {
        printVerbose(29);
        addRule(29);
        List<ParseTree> children = new ArrayList<ParseTree>();
        match(new Symbol(LexicalUnit.PRINT), children);
        match(new Symbol(LexicalUnit.LPAREN), children);
        match(new Symbol(LexicalUnit.VARNAME), children);
        match(new Symbol(LexicalUnit.RPAREN), children);
        Symbol print = new Symbol("Print");
        ParseTree printTree = new ParseTree(print, children);
        return printTree;
    }

    /**
     * Rule 30 of the grammar, corresponding to the variable Read
     * @return ParseTree containing Read and all its children
     */
    private static ParseTree read() {
        printVerbose(30);
        addRule(30);
        List<ParseTree> children = new ArrayList<ParseTree>();
        match(new Symbol(LexicalUnit.READ), children);
        match(new Symbol(LexicalUnit.LPAREN), children);
        match(new Symbol(LexicalUnit.VARNAME), children);
        match(new Symbol(LexicalUnit.RPAREN), children);
        Symbol read = new Symbol("Read");
        ParseTree readTree = new ParseTree(read, children);
        return readTree;
    }

    /**
     * Rule 31 of the grammar, corresponding to the variable EndLine
     * @return ParseTree containing EndLine and all its children
     */
    private static ParseTree endLine() {
        printVerbose(31);
        addRule(31);
        List<ParseTree> children = new ArrayList<ParseTree>();
        match(new Symbol(LexicalUnit.ENDLINE), children);
        children.add(endLinePrime());
        Symbol endLine = new Symbol("EndLine");
        ParseTree endLineTree = new ParseTree(endLine, children);
        return endLineTree;
    }

    /**
     * Rules 32 and 33 of the grammar, corresponding to the variable EndLine'
     * @return ParseTree containing EndLine' and all its children
     */
    private static ParseTree endLinePrime() {
        List<ParseTree> children = new ArrayList<ParseTree>();
        Symbol token = nextToken();
        switch (token.getType()) {
            case ENDLINE:
                printVerbose(32);
                addRule(32);
                match(new Symbol(LexicalUnit.ENDLINE), children);
                children.add(endLinePrime());
                break;
            case VARNAME:
            case IF:
            case WHILE:
            case PRINT:
            case READ:
            case ENDPROG:
            case ENDIF:
            case ELSE:
            case ENDWHILE:
                printVerbose(33);
                addRule(33);
                Symbol epsilon = new Symbol("epsilon");
                ParseTree epsilonTree = new ParseTree(epsilon);
                children.add(epsilonTree);
                break;
            default:
                // System.out.println("ERROR");
        }
        Symbol endLinePrime = new Symbol("EndLine'");
        ParseTree endLinePrimeTree = new ParseTree(endLinePrime, children);
        return endLinePrimeTree;
    }

    /**
     * Matches a symbol on the input to the expected symbol given by the grammar.
     * In case there is an error, the errorMessage variable is updated, and if the
     * user wished to have a verbose output, he is shown a detailed error message 
     * indicating the position of the erroe and the expected symbol.
     * This function also adds a symbol to the parse tree of the calling entity.
     * @param symbol expected next symbol, according to the grammar
     * @param children children of the node calling match
     */
    public static void match(Symbol symbol, List<ParseTree> children) {
        if (tokens.get(index).getType() == symbol.getType()) {
            if (verbose) {
                System.out.println("Matched " + tokens.get(index).getValue().toString() + " of type " + symbol.getType() + " at line " + tokens.get(index).getLine()
                        + ", at position " + tokens.get(index).getColumn() + ".");
            }
            children.add(new ParseTree(tokens.get(index)));
            index++;
        } else {
            if (errorMessage == null) {
                errorMessage = "Error at line " + tokens.get(index).getLine() + ", at position "
                        + tokens.get(index).getColumn() + ", expected " + symbol.getType() + " but got "
                        + tokens.get(index).getType();
            }
        }
    }

    /**
     * Returns the next token from the input
     * @return symbol corresponding to the next token
     */
    private static Symbol nextToken() {
        return tokens.get(index);
    }

    /**
     * Adds a rule to the list of rules used by the parser so far
     * @param i number of the rule
     */
    private static void addRule(int i) {
        rules.add(i);
    }

    /**
   * Allows to get the litteral form of a rule, required when in verbose mode.
   * 
   * @param i number of the rule
   * @return string corresponding to the rule
   */
  public static String getVerbose(int i) {
    switch (i) {
      case 1:
        return "<Program> -> BEGINPROG [ProgName] <EndLine> <Code> ENDPROG";
      case 2:
        return "<Code> -> <Instruction> [EndLine] <Code>";
      case 3:
        return "<Code> -> epsilon";
      case 4:
        return "<Instruction> -> <Assign>";
      case 5:
        return "<Instruction> -> <If>";
      case 6:
        return "<Instruction> -> <While>";
      case 7:
        return "<Instruction> -> <Print>";
      case 8:
        return "<Instruction> -> <Read>";
      case 9:
        return "<Assign> -> [VarName] := <ExprArith>";
      case 10:
        return "<ExprArith> -> <Prod><ExprArith'>";
      case 11:
        return "<ExprArith'> -> +<Prod><ExprArith'>";
      case 12:
        return "<ExprArith'> -> -<Prod><ExprArith'>";
      case 13:
        return "<ExprArith'> -> epsilon";
      case 14:
        return "<Prod> -> <Atom><Prod'>";
      case 15:
        return "<Prod'> -> *<Atom><Prod'>";
      case 16:
        return "<Prod'> -> /<Atom><Prod'>";
      case 17:
        return "<Prod'> -> epsilon";
      case 18:
        return "<Atom> -> -<Atom>";
      case 19:
        return "<Atom> -> (<ExprArith>)";
      case 20:
        return "<Atom> -> [VarName]";
      case 21:
        return "<Atom> -> [Number]";
      case 22:
        return "<If> -> IF (<Cond>) THEN <EndLine> <Code> <IfTail>";
      case 23:
        return "<IfTail> -> ENDIF";
      case 24:
        return "<IfTail> -> ELSE <EndLine> <Code> ENDIF";
      case 25:
        return "<Cond> -> <ExprArith> <Comp> <ExprArith>";
      case 26:
        return "<Comp> -> =";
      case 27:
        return "<Comp> -> >";
      case 28:
        return "<While> -> WHILE (<Cond>) DO <EndLine> <Code> ENDWHILE";
      case 29:
        return "<Print> -> PRINT([VarName])";
      case 30:
        return "<Read> -> READ([VarName])";
      case 31:
        return "<EndLine> -> [EndLine] <EndLine'>";
      case 32:
        return "<EndLine'> -> [EndLine] <EndLine'>";
      case 33:
        return "<EndLine'> -> epsilon";
      default:
        return null;
    }
  }

  /**
   * Prints the verbose rule as detailed in function getVerbose
   * @param i number of the rule to be printed in verbose mode
   */
  public static void printVerbose(int i) {
    if(verbose) {
        System.out.println(getVerbose(i));
    }
  }
}
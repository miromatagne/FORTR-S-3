import java.util.ArrayList;
import java.util.List;
public class AST {
    ParseTree parseTree;
    
    /**
     * Constructor, stores the ParseTree to be converted into ad AST as attribute
     * @param parseTree ParseTree coming from the Parser
     */
    public AST(ParseTree parseTree) {
        this.parseTree = parseTree;
    }

    /**
     * Function called to transform the parseTree into an AST.
     * @return AST
     */
    public ParseTree getAST() {
        ParseTree tree1 = cleanTree(parseTree);
        ParseTree tree2 = setUpOperators(tree1);
        ParseTree tree3 = removeExprArith(tree2);
        ParseTree tree4 = fixAssociativity(tree3);
        ParseTree ast = finalCleanUp(tree4);
        return ast;
    }

    /**
     * Replaces unnecessary non-leaf nodes by their children (ex : Instruction),
     * and removes a lot of unnecessary terminals
     * @param parseTree input parseTree
     * @return a cleaned up parse tree
     */
    private static ParseTree cleanTree(ParseTree parseTree) {
        List<ParseTree> children = parseTree.getChildren();
        int i = 0;
        while(i < children.size()) {
          switch (children.get(i).getLabel().getType()) {
            //Replace unnecessary intermediate nodes by thir children
            case INSTRUCTION:
            case COMP:
              children.set(i, cleanTree(children.get(i).getChildren().get(0)));
              break;
            //Remove nodes that give epsilon, as they are not useful
            case CODE:
            case EXPRARITHPRIME:
            case PRODPRIME:
              if(children.get(i).getChildren().size() == 1 && children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.EPSILON) {
                children.remove(i);
                i--;
              }
              else {
                children.set(i, cleanTree(children.get(i)));
              }
              break;
            case ATOM:
              //If the Atom simply has a VarName or a Number as a child, we replace
              //the Atom by is child
              if(children.get(i).getChildren().size() == 1) {
                children.set(i,cleanTree(children.get(i).getChildren().get(0)));
              }
              //If the Atom has 3 children, it means rule 19 is used (with the parenthesis),
              //so the parenthesis are ignored and only the second child is kept
              else if(children.get(i).getChildren().size() == 3) {
                children.set(i,cleanTree(children.get(i).getChildren().get(1)));
              }
              else {
                children.set(i, cleanTree(children.get(i)));
              }
              break;
            case PROD:
              //If a Prod gives just an Atom, that itself gives just a VarName or a Number,
              //the Prod node is removed to the profit of that VarName of Number
              if(children.get(i).getChildren().size() == 2 && children.get(i).getChildren().get(1).getChildren().size() == 1) {
                cleanTree(children.get(i));
                children.set(i,cleanTree(children.get(i).getChildren().get(0)));
              }
              else {
                children.set(i, cleanTree(children.get(i)));
              }
              break;
            //All the following unncesessary terminals are removed
            case BEGINPROG:
            case PROGNAME:
            case ENDLINE_NT:
            case ASSIGN:
            case ENDPROG:
            case READ:
            case PRINT:
            case LPAREN:
            case WHILE:
            case DO:
            case ENDWHILE:
            case RPAREN:
            case IF:
            case THEN:
            case ENDIF:
              children.remove(i);
              i--;
              break;   
            case IFTAIL:
              //If the IfTail node has only one child, it means rule 23 was used and
              //that this child is just ENDIF, so we simply remove the IfTail as it is
              //useless.
              if(children.get(i).getChildren().size() == 1) {
                children.remove(i);
                i--;
              }
              //Else, it means the IfTail contains an ELSE (rule 24), and therefore we
              //just need to keep the code corresponding to the ELSE statement, which is 
              //why we keep only the second child.
              else {
                children.set(i,cleanTree(children.get(i).getChildren().get(2)));
              }
            default:
              children.set(i, cleanTree(children.get(i)));
              break;
          }
          i++;
        }
        parseTree.setChildren(children);
        return parseTree;
      }
    
      /**
       * Puts the operators correctly on the AST. Indeed, the operators in our grammar
       * are not represented as non-terminal nodes, they simply appear as children of
       * ExprArith,Prod,...
       * @param parseTree input Parse Tree
       * @return Parse Tree with operators in place
       */
      private static ParseTree setUpOperators(ParseTree parseTree) {
        List<ParseTree> children = parseTree.getChildren();
        int i = 0;
        while(i < children.size()) {
          switch (children.get(i).getLabel().getType()) {
            case ATOM:
              //If Atom has 2 children, it means it contains an unary minus, which
              //is why we join the minus with the number associated.
              if(children.get(i).getChildren().size() == 2) {
                children.get(i).getChildren().get(1).getLabel().setValue("-" + children.get(i).getChildren().get(1).getLabel().getValue());
                children.set(i,setUpOperators(children.get(i).getChildren().get(1)));
              }
              break;
            //For every ExprArith and Prod found, we replace them by the operator
            //they correspond to.
            case EXPRARITH:
            case PROD:
              if(children.get(i).getChildren().size() != 1){
                children.set(i, setUpOperators(new ParseTree(new Symbol(children.get(i).getChildren().get(1).getChildren().get(0).getLabel().getType(),children.get(i).getChildren().get(1).getChildren().get(0).getLabel().getValue()),children.get(i).getChildren())));
              }
              else {
                children.set(i, setUpOperators(children.get(i)));
              }
              break;
            //For every ExprArith' and Prod' found, we replace them by the operator
            //they correspond to.
            case EXPRARITHPRIME:
            case PRODPRIME:
              if(children.get(i).getChildren().size() == 3) {
                children.set(i, setUpOperators(new ParseTree(new Symbol(children.get(i).getChildren().get(2).getChildren().get(0).getLabel().getType(),children.get(i).getChildren().get(2).getChildren().get(0).getLabel().getValue()),children.get(i).getChildren())));
              }
              else {
                children.set(i, setUpOperators(children.get(i)));
              }
              break;
            //The remaining terminals corresponding to the operators are removed
            case PLUS:
            case TIMES:
            case DIVIDE:
            case MINUS:
              children.remove(i);
              i--;
              break;
            default:
              children.set(i, setUpOperators(children.get(i)));
              break;
          }
          i++;
        }
        parseTree.setChildren(children);
        return parseTree;
      }
    
      /**
       * Removes all the unnecessary ExprArith,ExprArith',Prod and Prod' nodes
       * @param parseTree input ParseTree
       * @return ParseTree without unnecessary nodes
       */
      private static ParseTree removeExprArith(ParseTree parseTree) {
        List<ParseTree> children = parseTree.getChildren();
        int i = 0;
        while(i < children.size()) {
          switch (children.get(i).getLabel().getType()) {
            case PROD:
            case EXPRARITH:
            case PRODPRIME:
            case EXPRARITHPRIME:
              if(children.get(i).getChildren().size() == 1 && (children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.NUMBER || children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.VARNAME) ) {
                children.set(i,removeExprArith(children.get(i).getChildren().get(0)));
              }
              else {
                children.set(i, removeExprArith(children.get(i)));
              }
              break;
            default:
              children.set(i, removeExprArith(children.get(i)));
              break;
          }
          i++;
        }
        parseTree.setChildren(children);
        return parseTree;
      }
    
      /**
       * Fixes the associativity of the operators. Indeed, the tree is for the moment
       * build such as right associativity is performes, and this need to be changed
       * to left associativity
       * @param parseTree input ParseTree
       * @return ParseTree with all operators respecting left associativity
       */
      private static ParseTree fixAssociativity(ParseTree parseTree) {
        List<ParseTree> children = parseTree.getChildren();
        int i = 0;
        while(i < children.size()) {
          switch (children.get(i).getLabel().getType()) {
            //Whenever a DIVIDES or a TIMES is found, we call getChildParseTree,
            //which will fix the associativity of the successive multiplications
            //and divisions.
            case DIVIDE:
            case TIMES:
              children.set(i,getChildParseTree(i,children,LexicalUnit.DIVIDE,LexicalUnit.TIMES));
              break;
            //Whenever a PLUS or a MINUS is found, we call getChildParseTree,
            //which will fix the associativity of the successive additions
            //and substractions.
            case PLUS:
            case MINUS:
                if(children.get(i).getChildren().size() != 0) {
                    children.set(i,getChildParseTree(i,children,LexicalUnit.PLUS,LexicalUnit.MINUS));
                }
                else {
                    children.set(i, fixAssociativity(children.get(i)));
                }
              break;
            default:
              children.set(i, fixAssociativity(children.get(i)));
              break;
          }
          i++;
        }
        parseTree.setChildren(children);
        return parseTree;
      }
    
      /**
       * Helper function that computes a list of all successive operations and returns
       * a ParseTree with left associativity respected. This function can be called for
       * additions and substractions, of for multiplications and divisions.
       * @param i index of the child where the first operator concerned is present in the original ParseTree
       * @param children children of the input ParseTree
       * @param op1 first operator to consider
       * @param op2 second operator to consider
       * @return ParseTree with left associativity
       */
      private static ParseTree getChildParseTree(int i, List<ParseTree> children, LexicalUnit op1, LexicalUnit op2 ) {
        List<ParseTree> consecutive = new ArrayList<ParseTree>();
        ParseTree child = children.get(i).getChildren().get(1);
        consecutive.add(children.get(i));
        //Create a lost of all consecutive nodes corresponding to the operators specified
        //(division and multiplication or addition and substraction)
        while(child.getLabel().getType() == op1 || child.getLabel().getType() == op2 ) {
          consecutive.add(child);
          child = child.getChildren().get(1);
        }
        if(consecutive.size() == 1) {
          return fixAssociativity(children.get(i));
        }
        //The previous list obtained is flattened, and the numbers
        // or variables used in these operations are also added, to 
        //have all the complete successive operations on a single level
        List<ParseTree> flatten = new ArrayList<ParseTree>();
        for(int b=0;b < consecutive.size();b++) {
          flatten.add(consecutive.get(b).getChildren().get(0));
          flatten.add(consecutive.get(b));
          if(b==consecutive.size() -1){
            flatten.add(consecutive.get(b).getChildren().get(1));
          }
        }
        Symbol s = flatten.get(flatten.size() -2).getLabel();
        List<ParseTree> chdn = new ArrayList<ParseTree>();
        chdn.add(fixAssociativity(flatten.get(flatten.size() -1)));
        ParseTree newParseTree = new ParseTree(s,chdn);
        List<ParseTree> currentChildren = chdn;
        //The flattened list is parsed in a specific order to place the nodes in
        //the tree respecting the associativity rules
        for(int b=flatten.size()-4;b>=0;b=b-2) {
          Symbol s2 = flatten.get(b).getLabel();
          List<ParseTree> c = new ArrayList<ParseTree>();
          c.add(flatten.get(b+1));
          if(b-1==0) {
            c.add(0,flatten.get(b-1));
          }
          ParseTree p = fixAssociativity(new ParseTree(s2,c));
          currentChildren.add(0,p);
          currentChildren = c;
        }
        return newParseTree;
      }

      /**
       * Removes the final ExprArith, ExprArith', Prod and Prod' nodes that are left,
       * due to the parenthesis present in certain equations.
       * @param parseTree
       * @return
       */
      private static ParseTree finalCleanUp(ParseTree parseTree) {
        List<ParseTree> children = parseTree.getChildren();
        int i = 0;
        while(i < children.size()) {
          switch (children.get(i).getLabel().getType()) {
            //Reoval of the existing ExprArith, ExprArith', Prod and Prod'.
            //These were still present because of the parenthesis.
            case EXPRARITHPRIME:
            case EXPRARITH:
            case PROD:
            case PRODPRIME:
                if(children.get(i).getChildren().size() == 1) {
                    finalCleanUp(children.get(i));
                    children.set(i,finalCleanUp(children.get(i).getChildren().get(0)));
                }
                else {
                    children.set(i, finalCleanUp(children.get(i)));
                }
              break;
            default:
              children.set(i, finalCleanUp(children.get(i)));
              break;
          }
          i++;
        }
        parseTree.setChildren(children);
        return parseTree;
      }
}
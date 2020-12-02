import java.util.ArrayList;
import java.util.List;
public class AST {
    ParseTree parseTree;
    
    public AST(ParseTree parseTree) {
        this.parseTree = parseTree;
    }

    public ParseTree getAST() {
        ParseTree tree1 = cleanTree(parseTree);
        ParseTree tree2 = buildAST(tree1);
        ParseTree tree3 = removeExprArith(tree2);
        ParseTree ast = fixAssociativity(tree3);
        return ast;
    }

    private static ParseTree cleanTree(ParseTree parseTree) {
        // ParseTree ast = new ParseTree(new Symbol("Program"));
        List<ParseTree> children = parseTree.getChildren();
        int i = 0;
        while(i < children.size()) {
          //System.out.println(children.get(i).getLabel().getASTString());
          switch (children.get(i).getLabel().getASTString()) {
            case "Instruction":
              children.set(i, cleanTree(children.get(i).getChildren().get(0)));
              break;
            case "Code":
            case "ExprArith'":
            case "Prod'":
              if(children.get(i).getChildren().size() == 1 && children.get(i).getChildren().get(0).getLabel().getASTString() == "epsilon") {
                children.remove(i);
                i--;
              }
              else {
                children.set(i, cleanTree(children.get(i)));
              }
              break;
            case "Atom":
              // System.out.println(children.get(i).getChildren().get(0).getLabel().getValue());
              if(children.get(i).getChildren().size() == 1) {
                //System.out.println("OK");
                children.set(i,cleanTree(children.get(i).getChildren().get(0)));
              }
              else if(children.get(i).getChildren().size() == 3) {
                children.set(i,cleanTree(children.get(i).getChildren().get(1)));
                i -= 2;
              }
              else {
                children.set(i, cleanTree(children.get(i)));
              }
              break;
            case "Prod":
              if(children.get(i).getChildren().size() == 2 && children.get(i).getChildren().get(1).getChildren().size() == 1) {
                cleanTree(children.get(i));
                children.set(i,cleanTree(children.get(i).getChildren().get(0)));
              }
              else {
                children.set(i, cleanTree(children.get(i)));
              }
              break;
            case "BEGINPROG":
            case "PROGNAME":
            case "EndLine":
            case "ASSIGN":
            case "ENDPROG":
            case "READ":
            case "PRINT":
            case "LPAREN":
            case "RPAREN":
              children.remove(i);
              i--;
              break;   
            case "ExprArith":
              children.set(i, cleanTree(children.get(i)));
              break;
            default:
              // System.out.println("ok");
              children.set(i, cleanTree(children.get(i)));
              break;
          }
          i++;
        }
        parseTree.setChildren(children);
        return parseTree;
      }
    
      private static ParseTree buildAST(ParseTree parseTree) {
        List<ParseTree> children = parseTree.getChildren();
        int i = 0;
        while(i < children.size()) {
          switch (children.get(i).getLabel().getASTString()) {
            case "Atom":
              if(children.get(i).getChildren().size() == 2) {
                children.get(i).getChildren().get(1).getLabel().setValue("-" + children.get(i).getChildren().get(1).getLabel().getValue());
                children.set(i,buildAST(children.get(i).getChildren().get(1)));
              }
              break;
            case "ExprArith":
            case "Prod":
              if(children.get(i).getChildren().size() != 1){
                children.set(i, buildAST(new ParseTree(new Symbol(children.get(i).getChildren().get(1).getChildren().get(0).getLabel().getType(),children.get(i).getChildren().get(1).getChildren().get(0).getLabel().getValue()),children.get(i).getChildren())));
              }
              else {
                children.set(i, buildAST(children.get(i)));
              }
              break;
            case "ExprArith'":
            case "Prod'":
              if(children.get(i).getChildren().size() == 3) {
                children.set(i, buildAST(new ParseTree(new Symbol(children.get(i).getChildren().get(2).getChildren().get(0).getLabel().getType(),children.get(i).getChildren().get(2).getChildren().get(0).getLabel().getValue()),children.get(i).getChildren())));
              }
              else {
                children.set(i, buildAST(children.get(i)));
              }
              break;
            case "PLUS":
            case "TIMES":
            case "DIVIDE":
            case "MINUS":
              children.remove(i);
              i--;
              break;
            default:
              // System.out.println("ok");
              children.set(i, buildAST(children.get(i)));
              break;
          }
          i++;
        }
        parseTree.setChildren(children);
        return parseTree;
      }
    
      private static ParseTree removeExprArith(ParseTree parseTree) {
        List<ParseTree> children = parseTree.getChildren();
        int i = 0;
        while(i < children.size()) {
          switch (children.get(i).getLabel().getASTString()) {
            case "Prod":
            case "ExprArith":
            case "Prod'":
            case "ExprArith'":
              if(children.get(i).getChildren().size() == 1) {
                children.set(i,removeExprArith(children.get(i).getChildren().get(0)));
              }
              break;
            default:
              // System.out.println("ok");
              children.set(i, removeExprArith(children.get(i)));
              break;
          }
          i++;
        }
        parseTree.setChildren(children);
        return parseTree;
      }
    
      private static ParseTree fixAssociativity(ParseTree parseTree) {
        List<ParseTree> children = parseTree.getChildren();
        int i = 0;
        while(i < children.size()) {
          switch (children.get(i).getLabel().getASTString()) {
            case "DIVIDE":
            case "TIMES":
              children.set(i,getChildParseTree(i,children,"DIVIDE","TIMES"));
              break;
            case "PLUS":
            case "MINUS":
                if(children.get(i).getChildren().size() != 0) {
                    children.set(i,getChildParseTree(i,children,"PLUS","MINUS"));
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
    
      private static ParseTree getChildParseTree(int i, List<ParseTree> children, String op1, String op2 ) {
        List<ParseTree> consecutive = new ArrayList<ParseTree>();
        ParseTree child = children.get(i).getChildren().get(1);
        consecutive.add(children.get(i));
        while(child.getLabel().getASTString() == op1 || child.getLabel().getASTString() == op2 ) {
          consecutive.add(child);
          child = child.getChildren().get(1);
        }
        if(consecutive.size() == 1) {
          // System.out.println("SIZE 1" + child.getLabel().getASTString());
          return fixAssociativity(children.get(i));
        }
        System.out.println("SIZE " + consecutive.size() + " " + children.get(i).getChildren().get(1).getChildren().get(1).getLabel().getASTString());
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
}
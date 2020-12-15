import java.util.ArrayList;
import java.util.List;

public class Llvm {
    private static ParseTree tree;
    private static String llvmCode;
    private static int line = 0;
    private static int ifCounter = 0;
    private static int wCounter = 0;
    private static boolean inLoop = false;
    private static List<Object> values = new ArrayList<Object>();
    private static String read = "@.strR = private unnamed_addr constant [3 x i8] c\"%d\\00\", align 1" + "\n"
            + "define i32 @readInt() #0 {" + "\n" + " %1 = alloca i32, align 4" + "\n"
            + " %2 = call i32 (i8*, ...) @scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.strR, i32 0, i32 0), i32* %1)"
            + "\n" + " %3 = load i32, i32* %1, align 4" + "\n" + " ret i32 %3" + "}" + "\n"
            + "declare i32 @scanf(i8*, ...) #1";

    private static String print = "@.strP = private unnamed_addr constant [4 x i8] c\"%d\\0A\\00\", align 1" + "\n"
            + "\n" + "define void @println(i32 %x) {" + "\n" + " %1 = alloca i32, align 4" + "\n"
            + " store i32 %x, i32* %1, align 4" + "\n" + " %2 = load i32, i32* %1, align 4" + "\n"
            + " %3 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.strP, i32 0, i32 0), i32 %2)"
            + "\n" + " ret void" + "\n" + "}" + "\n" + "declare i32 @printf(i8*, ...) #1";

    /**
     * Constructor, stores the parseTree of the AST class to generate into LLVM IR code
     * @param tree ParseTree coming from the AST class
     */
    public Llvm(ParseTree tree) {
        this.tree = tree;
    }

    /**
     * Function called to generate the LLVM IR code from the AST 
     * @return the complete LLVM IR code corresponding to the AST 
     */
    public String getLlvm() {
        this.llvmCode = toLlvm();
        return llvmCode;
    }

    /**
     * Function called to walk the AST and generate the main part of the LLVM IR code
     * @param tree ParseTree coming from the AST class
     * @return the main part of the LLVM IR code
     */
    private String analyze(ParseTree tree) {
            StringBuilder llvmCode = new StringBuilder();
            List<ParseTree> children = tree.getChildren();
            String currentCode = null;
            int i = 0;

            while (i < children.size()) {
                switch (children.get(i).getLabel().getType()) {
                    case CODE:
                        currentCode = analyze(children.get(i));
                        llvmCode.append(currentCode);  
                        break;
                    case ASSIGN_NT : 
                        currentCode = analyze(children.get(i));
                        llvmCode.append(currentCode);  
                        //Case of a VARNAME is assigned to a VARNAME
                        if (children.get(i).getChildren().get(1).getLabel().getType() == LexicalUnit.VARNAME) {
                            //Load the value of the VARNAME if it is already initialized before
                            if (values.contains(children.get(i).getChildren().get(1).getLabel().getValue())) {
                                llvmCode.append("\t%" + line + " = load i32, i32* %" + children.get(i).getChildren().get(1).getLabel().getValue() + "\n");
                                line++;
                            }
                            //If not declared before
                            else {
                                return "Error : undefined variable";
                            }
                        }
                        llvmCode.append("\tstore i32 %" + String.valueOf((line-1)) + ", i32* %" + children.get(i).getChildren().get(0).getLabel().getValue() + "\n");
                        //Add the VARNAME in the list values if ASSIGN_NT is not in a loop
                        if (inLoop == false) {
                            values.add(children.get(i).getChildren().get(0).getLabel().getValue());
                        }  
                        //If the VARNAME is in a loop and not declared before
                        else if (inLoop == true && !values.contains(children.get(i).getChildren().get(0).getLabel().getValue())) {
                            return "Error : undefined variable";
                        }  
                        break;
                    case READ_NT : 
                        currentCode = analyze(children.get(i));
                        llvmCode.append("\t%" + line + " = call i32 @readInt()" + "\n");
                        llvmCode.append(currentCode);
                        llvmCode.append("\tstore i32 %" + line + ", i32* %" + children.get(i).getChildren().get(0).getLabel().getValue().toString() + "\n");
                        values.add(children.get(i).getChildren().get(0).getLabel().getValue());
                        children.get(i).setCounter(line);
                        line++;
                        break;
                    case PRINT_NT :
                        currentCode = analyze(children.get(i)); 
                        llvmCode.append(currentCode);
                        //If the VARNAME to be printed is not initialized
                        if (!values.contains(children.get(i).getChildren().get(0).getLabel().getValue())) {
                            return "Error : undefined variable";
                        }
                        llvmCode.append("\t%" + line + " = load i32, i32* %" + children.get(i).getChildren().get(0).getLabel().getValue()  + "\n"); 
                        llvmCode.append("\tcall void @println(i32 %" + line +")\n");  
                        line++;
                        break;
                    case TIMES :
                        //If the first or second VARNAME is not declared before
                        if ((children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.VARNAME && !values.contains(children.get(i).getChildren().get(0).getLabel().getValue())) || (children.get(i).getChildren().get(1).getLabel().getType() == LexicalUnit.VARNAME && !values.contains(children.get(i).getChildren().get(1).getLabel().getValue()))) {
                            return "Error : undefined variable";
                        }
                        //If the first child is a VARNAME and already initialized 
                        if (children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.VARNAME && values.contains(children.get(i).getChildren().get(0).getLabel().getValue())) {
                            llvmCode.append("\t%" + line + " = load i32, i32* %" + children.get(i).getChildren().get(0).getLabel().getValue() + "\n");
                            children.get(i).getChildren().get(0).setCounter(line);
                            line++;
                        }
                        //If the second child is a VARNAME and already initialized
                        if (children.get(i).getChildren().get(1).getLabel().getType() == LexicalUnit.VARNAME && values.contains(children.get(i).getChildren().get(1).getLabel().getValue())) {
                            currentCode = analyze(children.get(i));
                            llvmCode.append(currentCode);
                            llvmCode.append("\t%" + line + " = load i32, i32* %" + children.get(i).getChildren().get(1).getLabel().getValue() + "\n"); 
                            children.get(i).getChildren().get(1).setCounter(line);
                            line++;
                        }
                        else {
                            currentCode = analyze(children.get(i));
                            llvmCode.append(currentCode);
                        }

                        llvmCode.append("\t%" + line + " = mul i32 %" + children.get(i).getChildren().get(0).getCounter() + ", %" + children.get(i).getChildren().get(1).getCounter() + "\n");
                        children.get(i).setCounter(line);
                        line++;
                        break;
                    case DIVIDE :
                        //If the first or second VARNAME is not declared before
                        if ((children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.VARNAME && !values.contains(children.get(i).getChildren().get(0).getLabel().getValue())) || (children.get(i).getChildren().get(1).getLabel().getType() == LexicalUnit.VARNAME && !values.contains(children.get(i).getChildren().get(1).getLabel().getValue()))) {
                            return "Error : undefined variable";
                        }
                        //If the first child is a VARNAME and already initialized 
                        if (children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.VARNAME && values.contains(children.get(i).getChildren().get(0).getLabel().getValue())) {
                            llvmCode.append("\t%" + line + " = load i32, i32* %" + children.get(i).getChildren().get(0).getLabel().getValue() + "\n");
                            children.get(i).getChildren().get(0).setCounter(line);
                            line++;
                        }
                        //If the second child is a VARNAME and already initialized
                        if (children.get(i).getChildren().get(1).getLabel().getType() == LexicalUnit.VARNAME && values.contains(children.get(i).getChildren().get(1).getLabel().getValue())) {
                            currentCode = analyze(children.get(i));
                            llvmCode.append(currentCode);
                            llvmCode.append("\t%" + line + " = load i32, i32* %" + children.get(i).getChildren().get(1).getLabel().getValue() + "\n"); 
                            children.get(i).getChildren().get(1).setCounter(line);
                            line++;
                        }
                        else {
                            currentCode = analyze(children.get(i));
                            llvmCode.append(currentCode);
                        }

                        llvmCode.append("\t%" + line + " = sdiv i32 %" + children.get(i).getChildren().get(0).getCounter() + ", %" + children.get(i).getChildren().get(1).getCounter() + "\n");
                        children.get(i).setCounter(line);
                        line++;
                        break;
                    case PLUS :
                        //If the first or second VARNAME is not declared before
                        if ((children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.VARNAME && !values.contains(children.get(i).getChildren().get(0).getLabel().getValue())) || (children.get(i).getChildren().get(1).getLabel().getType() == LexicalUnit.VARNAME && !values.contains(children.get(i).getChildren().get(1).getLabel().getValue()))) {
                            return "Error : undefined variable";
                        }
                        //If the first child is a VARNAME and already initialized 
                        if (children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.VARNAME && values.contains(children.get(i).getChildren().get(0).getLabel().getValue())) {
                            llvmCode.append("\t%" + line + " = load i32, i32* %" + children.get(i).getChildren().get(0).getLabel().getValue() + "\n");
                            children.get(i).getChildren().get(0).setCounter(line);
                            line++;
                        }
                        //If the second child is a VARNAME and already initialized
                        if (children.get(i).getChildren().get(1).getLabel().getType() == LexicalUnit.VARNAME && values.contains(children.get(i).getChildren().get(1).getLabel().getValue())) {
                            currentCode = analyze(children.get(i));
                            llvmCode.append(currentCode);
                            llvmCode.append("\t%" + line + " = load i32, i32* %" + children.get(i).getChildren().get(1).getLabel().getValue() + "\n");
                            children.get(i).getChildren().get(1).setCounter(line);
                            line++;
                        }
                        else {
                            currentCode = analyze(children.get(i));
                            llvmCode.append(currentCode);
                        }

                        llvmCode.append("\t%" + line + " = add i32 %" + children.get(i).getChildren().get(0).getCounter() + ", %" + children.get(i).getChildren().get(1).getCounter() + "\n");
                        children.get(i).setCounter(line);
                        line++;
                        break;
                    case MINUS :
                        //If the first or second VARNAME is not declared before
                        if ((children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.VARNAME && !values.contains(children.get(i).getChildren().get(0).getLabel().getValue())) || (children.get(i).getChildren().get(1).getLabel().getType() == LexicalUnit.VARNAME && !values.contains(children.get(i).getChildren().get(1).getLabel().getValue()))) {
                            return "Error : undefined variable";
                        }
                        // If the first child of MINUS is a VARNAME and it is initialized
                        if (children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.VARNAME && values.contains(children.get(i).getChildren().get(0).getLabel().getValue())) {
                            llvmCode.append("\t%" + line + " = load i32, i32* %" + children.get(i).getChildren().get(0).getLabel().getValue() + "\n");
                            children.get(i).getChildren().get(0).setCounter(line);
                            line++;
                        }
                        //If the second child is a VARNAME and already initialized
                        if (children.get(i).getChildren().get(1).getLabel().getType() == LexicalUnit.VARNAME && values.contains(children.get(i).getChildren().get(1).getLabel().getValue())) {
                            currentCode = analyze(children.get(i));
                            llvmCode.append(currentCode);
                            llvmCode.append("\t%" + line + " = load i32, i32* %" + children.get(i).getChildren().get(1).getLabel().getValue() + "\n");
                            children.get(i).getChildren().get(1).setCounter(line);
                            line++;
                        }
                        else {
                            currentCode = analyze(children.get(i));
                            llvmCode.append(currentCode);
                        }

                        llvmCode.append("\t%" + line + " = sub i32 %" + children.get(i).getChildren().get(0).getCounter() + ", %" + children.get(i).getChildren().get(1).getCounter() + "\n");
                        children.get(i).setCounter(line);
                        line++;
                        break;  
                    case VARNAME :
                        //If VARNAME not declared before, add it in values list
                        if (!values.contains(children.get(i).getLabel().getValue())) {
                            llvmCode.append("\t%" + children.get(i).getLabel().getValue().toString() + " = alloca i32" + "\n");
                        }
                        break;
                    case NUMBER :
                        llvmCode.append("\t%" + line + " = alloca i32\n");
                        llvmCode.append("\tstore i32 " + children.get(i).getLabel().getValue() + ", i32* %" + line + "\n"); 
                        line++;
                        llvmCode.append("\t%" + line + " = load i32, i32* %" + (line-1) + "\n");
                        children.get(i).setCounter(line);
                        line++;
                        break;
                    case IF_NT :
                        inLoop = true; 
                        ifCounter++;
                        children.get(i).setIfCounter(ifCounter); 
                        //If there is an ELSE_NT
                        if (children.get(i).getChildren().get(children.get(i).getChildren().size()-1).getLabel().getType() == LexicalUnit.IFTAIL) {
                            children.get(i).getChildren().get(children.get(i).getChildren().size()-1).setIfCounter(ifCounter);;
                        }
                        children.get(i).getChildren().get(0).setIfCounter(ifCounter);  //So COND can detect if it is a child of IF_NT 
                        currentCode = analyze(children.get(i));
                        llvmCode.append(currentCode);
                        //First IF_NT
                        if (children.get(i).getIfCounter() == 1) {
                            //If there is no ELSE_NT
                            if (children.get(i).getChildren().get(children.get(i).getChildren().size() - 1).getLabel().getType() != LexicalUnit.IFTAIL) {
                                llvmCode.append("\tbr label %exit" + children.get(i).getIfCounter() + "\n");
                                llvmCode.append("  exit" + children.get(i).getIfCounter() + ":\n");
                            }
                        }
                        //The nth IF_NT
                        else if (children.get(i).getIfCounter() > 1) {
                            //If there is no ELSE_NT
                            if (children.get(children.size()-1).getLabel().getType() == LexicalUnit.CODE) {
                                llvmCode.append("\tbr label %exit" + children.get(i).getIfCounter() + "\n");
                                llvmCode.append("  exit" + children.get(i).getIfCounter() + ":\n");
                            }        
                            //If there is no CODE and no ELSE_NT
                            else if (children.get(children.size()-1).getLabel().getType() == LexicalUnit.IF_NT) {
                                llvmCode.append("\tbr label %exit" + children.get(i).getIfCounter() + "\n");
                                llvmCode.append("  exit" + children.get(i).getIfCounter() + ":\n");
                            }
                        }
                        inLoop = false;
                        break;
                    case WHILE_NT : 
                        inLoop = true;
                        wCounter++;
                        children.get(i).setWCounter(wCounter);
                        llvmCode.append("\tbr label %while" + children.get(i).getWCounter() + "\n");
                        llvmCode.append("  while" + children.get(i).getWCounter() + ":\n");
                        children.get(i).getChildren().get(0).setWCounter(wCounter);      //So COND can detect if it is a child of WHILE_NT 
                        currentCode = analyze(children.get(i));
                        llvmCode.append(currentCode);
                        //First WHILE
                        if (children.get(i).getWCounter() == 1) {
                            llvmCode.append("\tbr label %while" + children.get(i).getWCounter() + "\n");
                            llvmCode.append("  wexit" + children.get(i).getWCounter() + ":\n");
                        }
                        //The nth WHILE 
                        else if (children.get(i).getWCounter() > 1) {
                            llvmCode.append("\tbr label %while" + children.get(i).getWCounter() + "\n");
                            llvmCode.append("  wexit" + children.get(i).getWCounter() + ":\n");
                        }
                        inLoop = false;
                        break;
                    case COND :
                        //If the first child of COND is a single VARNAME
                        if (children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.VARNAME) {
                            if (!values.contains(children.get(i).getChildren().get(0).getLabel().getValue())) {
                                return "Error : undefined variable";
                            }
                            llvmCode.append("\t%" + line + " = load i32, i32* %" + children.get(i).getChildren().get(0).getLabel().getValue() + "\n");
                            children.get(i).getChildren().get(0).setCounter(line);
                            line++;
                        }
                        //If the second child is a VARNAME
                        if (children.get(i).getChildren().get(2).getLabel().getType() == LexicalUnit.VARNAME) {
                            //If the second child is not already initialized
                            if (!values.contains(children.get(i).getChildren().get(2).getLabel().getValue())) {
                                return "Error : undefined variable";
                            }
                            llvmCode.append("\t%" + line + " = load i32, i32* %" + children.get(i).getChildren().get(2).getLabel().getValue() + "\n");
                            children.get(i).getChildren().get(2).setCounter(line);
                            line++;
                        }
                        currentCode = analyze(children.get(i));
                        llvmCode.append(currentCode);
                        llvmCode.append("\t%" + line + " = icmp " + children.get(i).getChildren().get(1).getComp() + " i32 %" +  children.get(i).getChildren().get(0).getCounter() + ", %" + children.get(i).getChildren().get(2).getCounter() + "\n");
                        //If there is a ELSE statement
                        if (children.get(children.size()-1).getLabel().getType() == LexicalUnit.IFTAIL) {
                            llvmCode.append("\tbr i1 %" + line + ", label %true" + ifCounter + ", label %else" + ifCounter + "\n");
                            llvmCode.append("  true" + ifCounter + ":\n");
                        } 
                        //Detect that COND is a child of IF_NT
                        else if (children.get(i).getIfCounter() != 0) {
                            llvmCode.append("\tbr i1 %" + line + ", label %true" + ifCounter + ", label %exit" + ifCounter + "\n"); 
                            llvmCode.append("  true" + ifCounter + ":\n");
                        }
                        // Detect that COND is a child of WHILE_NT
                        else if (children.get(i).getWCounter() != 0) {
                            llvmCode.append("\tbr i1 %" + line + ", label %wtrue" + wCounter + ", label %wexit" + wCounter + "\n");
                            llvmCode.append("  wtrue" + wCounter + ":\n");
                        }

                        line++;
                        break;
                    case IFTAIL :
                        //Child of the first IF_NT
                        if (children.get(i).getIfCounter() == 1) {
                            llvmCode.append("\tbr label %exit\n");
                        }
                        //Child of the nth IF_NT
                        else if (children.get(i).getIfCounter() > 1) {
                            llvmCode.append("\tbr label %exit" + children.get(i).getIfCounter() +"\n");
                        }
                        llvmCode.append("  else" + children.get(i).getIfCounter() + ":\n");
                        currentCode = analyze(children.get(i));
                        llvmCode.append(currentCode);
                        //Child of the first IF_NT
                        if (children.get(i).getIfCounter() == 1) {
                            llvmCode.append("\tbr label %exit\n");
                            llvmCode.append("  exit:\n");
                        }
                        break;
                    case GT :
                        children.get(i).setComp("sgt");
                        break;
                    case EQ :
                        children.get(i).setComp("eq");
                        break;
                    default :
                        break;
                }
                i++;
            }
            //Return error if there is an error detected
            if (llvmCode.toString().contains("Error : undefined variable")) {
                return "Error : undefined variable" ;
            }

        return llvmCode.toString();
    }

    /**
     * Write the complete LLVM IR code from the corresponding parseTree AST  
     * @return the complete LLVM IR code
     */
    private String toLlvm() {
        String main = "define i32 @main() {" + "\n" +
        "  entry:" + "\n" + 
        analyze(this.tree) + 
        "\tret i32 0" + "\n" +
        "}";
        //Return error
        if (analyze(this.tree).contains("Error : undefined variable")) {
            return "Error : undefined variable" ;
        }

        return this.read + "\n" + this.print + "\n" + main;
    }

}

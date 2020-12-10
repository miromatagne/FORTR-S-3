import java.util.ArrayList;
import java.util.List;

public class Llvm {
    private static ParseTree tree;
    private static String llvmCode;
    private static int line = 0;
    private static int ifCounter = 0;
    private static int wCounter = 0;
    private static List<Object> values = new ArrayList<Object>();
    private static String read = "@.strR = private unnamed_addr constant [3 x i8] c\"%d\\00\", align 1" + "\n" + 
        "define i32 @readInt() #0 {" + "\n" + 
        " %1 = alloca i32, align 4" + "\n" +
        " %2 = call i32 (i8*, ...) @scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.strR, i32 0, i32 0), i32* %1)" + "\n" +
        " %3 = load i32, i32* %1, align 4" + "\n" +
        " ret i32 %3" +
        "}" + "\n" +      
        "declare i32 @scanf(i8*, ...) #1";

    private static String print = "@.strP = private unnamed_addr constant [4 x i8] c\"%d\\0A\\00\", align 1" + "\n" + "\n" +
        "define void @println(i32 %x) {" + "\n" +
        " %1 = alloca i32, align 4" + "\n" +
        " store i32 %x, i32* %1, align 4" + "\n" +
        " %2 = load i32, i32* %1, align 4" + "\n" +
        " %3 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.strP, i32 0, i32 0), i32 %2)" + "\n" +
        " ret void" + "\n" +
        "}" + "\n" +
        "declare i32 @printf(i8*, ...) #1";


    public Llvm(ParseTree tree) {
        this.tree = tree;
        this.llvmCode = toLlvm();
    }

    public String getLlvm() {
        return llvmCode;
    }

    private String analyze(ParseTree tree) {
            StringBuilder llvmCode = new StringBuilder();
            List<ParseTree> children = tree.getChildren();
            String currentCode = null;
            String value = null;
            int i = 0;

            while (i < children.size()) {
                switch (children.get(i).getLabel().getType()) {
                    case CODE:
                        currentCode = analyze(children.get(i));
                        llvmCode.append(currentCode);  // insert(int = 0, string = value) ? 
                        break;
                    case ASSIGN_NT : 
                        currentCode = analyze(children.get(i));
                        value = children.get(i).getChildren().get(0).getLabel().getValue().toString();
                        llvmCode.append(currentCode);  // insert(int = 0, string = value) ? 
                        llvmCode.append(" store i32 %" + String.valueOf((line-1)) + ", i32* %" + value + "\n");
                        break;
                    case READ_NT : 
                        currentCode = analyze(children.get(i));
                        llvmCode.append(" %" + line + " = call i32 readInt()" + "\n");
                        llvmCode.append(currentCode);
                        llvmCode.append(" store i32 %" + line + ", i32* %" + children.get(i).getChildren().get(0).getLabel().getValue().toString());
                        llvmCode.append("\n");
                        children.get(i).setCounter(line);
                        line++;
                        break;
                    case PRINT_NT :
                        currentCode = analyze(children.get(i)); 
                        llvmCode.append(currentCode);
                        llvmCode.append(" %" + line + " = load i32, i32* %" + children.get(i).getChildren().get(0).getLabel().getValue());  //.toString()
                        llvmCode.append(" call void @println(i32 %" + line +")");  
                        llvmCode.append("\n");
                        break;
                    case TIMES :
                        // Does not always work, example : c := (a*c)* 2 => does not detect that c is not initialized
                        if ((children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.VARNAME & !values.contains(children.get(i).getChildren().get(0).getLabel().getValue())) | (children.get(i).getChildren().get(1).getLabel().getType() == LexicalUnit.VARNAME & !values.contains(children.get(i).getChildren().get(1).getLabel().getValue()))) {
                            return "Error : undefined variable";
                        }
                        // Load more than once the same variable? check if it is a problem
                        if (children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.VARNAME & values.contains(children.get(i).getChildren().get(0).getLabel().getValue())) {
                            llvmCode.append(" %" + line + " = load i32, i32* %" + children.get(i).getChildren().get(0).getLabel().getValue());
                            llvmCode.append("\n");
                            children.get(i).getChildren().get(0).setCounter(line);
                            line++;
                        }
                        
                        if (children.get(i).getChildren().get(1).getLabel().getType() == LexicalUnit.VARNAME & values.contains(children.get(i).getChildren().get(1).getLabel().getValue())) {
                            currentCode = analyze(children.get(i));
                            llvmCode.append(currentCode);
                            llvmCode.append(" %" + line + " = load i32, i32* %" + children.get(i).getChildren().get(1).getLabel().getValue()); 
                            llvmCode.append("\n");
                            children.get(i).getChildren().get(1).setCounter(line);
                            line++;
                        }
                        else {
                            currentCode = analyze(children.get(i));
                            llvmCode.append(currentCode);
                        }

                        llvmCode.append(" %" + line + " = mul i32 %" + children.get(i).getChildren().get(0).getCounter() + ", %" + children.get(i).getChildren().get(1).getCounter());
                        llvmCode.append("\n");
                        children.get(i).setCounter(line);
                        line++;
                        break;
                    case DIVIDE :
                        if ((children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.VARNAME & !values.contains(children.get(i).getChildren().get(0).getLabel().getValue())) | (children.get(i).getChildren().get(1).getLabel().getType() == LexicalUnit.VARNAME & !values.contains(children.get(i).getChildren().get(1).getLabel().getValue()))) {
                            return "Error : undefined variable";
                        }

                        if (children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.VARNAME & values.contains(children.get(i).getChildren().get(0).getLabel().getValue())) {
                            llvmCode.append(" %" + line + " = load i32, i32* %" + children.get(i).getChildren().get(0).getLabel().getValue());
                            llvmCode.append("\n");
                            children.get(i).getChildren().get(0).setCounter(line);
                            line++;
                        }
                        
                        if (children.get(i).getChildren().get(1).getLabel().getType() == LexicalUnit.VARNAME & values.contains(children.get(i).getChildren().get(1).getLabel().getValue())) {
                            currentCode = analyze(children.get(i));
                            llvmCode.append(currentCode);
                            llvmCode.append(" %" + line + " = load i32, i32* %" + children.get(i).getChildren().get(1).getLabel().getValue()); 
                            llvmCode.append("\n");
                            children.get(i).getChildren().get(1).setCounter(line);
                            line++;
                        }

                        else {
                            currentCode = analyze(children.get(i));
                            llvmCode.append(currentCode);
                        }

                        llvmCode.append(" %" + line + " = sdiv i32 %" + children.get(i).getChildren().get(0).getCounter() + ", %" + children.get(i).getChildren().get(1).getCounter());
                        llvmCode.append("\n");
                        children.get(i).setCounter(line);
                        line++;
                        break;
                    case PLUS :
                        if ((children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.VARNAME & !values.contains(children.get(i).getChildren().get(0).getLabel().getValue())) | (children.get(i).getChildren().get(1).getLabel().getType() == LexicalUnit.VARNAME & !values.contains(children.get(i).getChildren().get(1).getLabel().getValue()))) {
                            return "Error : undefined variable";
                        }

                        if (children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.VARNAME & values.contains(children.get(i).getChildren().get(0).getLabel().getValue())) {
                            llvmCode.append(" %" + line + " = load i32, i32* %" + children.get(i).getChildren().get(0).getLabel().getValue());
                            llvmCode.append("\n");
                            children.get(i).getChildren().get(0).setCounter(line);
                            line++;
                        }
                        
                        if (children.get(i).getChildren().get(1).getLabel().getType() == LexicalUnit.VARNAME & values.contains(children.get(i).getChildren().get(1).getLabel().getValue())) {
                            currentCode = analyze(children.get(i));
                            llvmCode.append(currentCode);
                            llvmCode.append(" %" + line + " = load i32, i32* %" + children.get(i).getChildren().get(1).getLabel().getValue()); 
                            llvmCode.append("\n");
                            children.get(i).getChildren().get(1).setCounter(line);
                            line++;
                        }

                        else {
                            currentCode = analyze(children.get(i));
                            llvmCode.append(currentCode);
                        }

                        llvmCode.append(" %" + line + " = add i32 %" + children.get(i).getChildren().get(0).getCounter() + ", %" + children.get(i).getChildren().get(1).getCounter());
                        llvmCode.append("\n");
                        children.get(i).setCounter(line);
                        line++;
                        break;
                    case MINUS :
                        if ((children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.VARNAME & !values.contains(children.get(i).getChildren().get(0).getLabel().getValue())) | (children.get(i).getChildren().get(1).getLabel().getType() == LexicalUnit.VARNAME & !values.contains(children.get(i).getChildren().get(1).getLabel().getValue()))) {
                            return "Error : undefined variable";
                        }

                        if (children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.VARNAME & values.contains(children.get(i).getChildren().get(0).getLabel().getValue())) {
                            llvmCode.append(" %" + line + " = load i32, i32* %" + children.get(i).getChildren().get(0).getLabel().getValue());
                            llvmCode.append("\n");
                            children.get(i).getChildren().get(0).setCounter(line);
                            line++;
                        }
                        
                        if (children.get(i).getChildren().get(1).getLabel().getType() == LexicalUnit.VARNAME & values.contains(children.get(i).getChildren().get(1).getLabel().getValue())) {
                            currentCode = analyze(children.get(i));
                            llvmCode.append(currentCode);
                            llvmCode.append(" %" + line + " = load i32, i32* %" + children.get(i).getChildren().get(1).getLabel().getValue()); 
                            llvmCode.append("\n");
                            children.get(i).getChildren().get(1).setCounter(line);
                            line++;
                        }

                        else {
                            currentCode = analyze(children.get(i));
                            llvmCode.append(currentCode);
                        }

                        llvmCode.append(" %" + line + " = sub i32 %" + children.get(i).getChildren().get(0).getCounter() + ", %" + children.get(i).getChildren().get(1).getCounter());
                        llvmCode.append("\n");
                        children.get(i).setCounter(line);
                        line++;
                        break;  
                    case VARNAME :
                        if (!values.contains(children.get(i).getLabel().getValue())) {
                            llvmCode.append("\t%" + children.get(i).getLabel().getValue().toString() + " = alloca i32" + "\n");
                            values.add(children.get(i).getLabel().getValue());
                        }
                        break;
                    case NUMBER :
                        llvmCode.append("\t%" + line + " = alloca i32\n");
                        llvmCode.append("\tstore i32 " + children.get(i).getLabel().getValue() + ", i32* %" + line + "\n");  //.toString()
                        line++;
                        llvmCode.append("\t%" + line + " = load i32, i32* %" + (line-1) + "\n");
                        children.get(i).setCounter(line);
                        line++;
                        break;
                    case IF_NT :
                        ifCounter++;
                        children.get(i).setIfCounter(ifCounter); 
                        // If there is an ELSE_NT
                        if (children.get(i).getChildren().get(children.get(i).getChildren().size()-1).getLabel().getType() == LexicalUnit.IFTAIL) {
                            children.get(i).getChildren().get(children.get(i).getChildren().size()-1).setIfCounter(ifCounter);;
                        }
                        children.get(i).getChildren().get(0).setIfCounter(ifCounter);  // So cond can detect if he is a child of IF_NT 
                        currentCode = analyze(children.get(i));
                        llvmCode.append(currentCode);
                        // First IF_NT
                        if (children.get(i).getIfCounter() == 1) {
                            // If there is no ELSE_NT
                            if (children.get(i).getChildren().get(children.get(i).getChildren().size() - 1).getLabel().getType() != LexicalUnit.IFTAIL) {
                                llvmCode.append("\tbr label %exit" + children.get(i).getIfCounter() + "\n");
                                llvmCode.append("  exit" + children.get(i).getIfCounter() + ":\n");
                            }
                        }
                        // The nth IF_NT
                        else if (children.get(i).getIfCounter() > 1) {
                            // If there is no ELSE_NT
                            if (children.get(children.size()-1).getLabel().getType() == LexicalUnit.CODE) {
                                llvmCode.append("\tbr label %exit" + children.get(i).getIfCounter() + "\n");
                                llvmCode.append("  exit" + children.get(i).getIfCounter() + ":\n");
                            }        
                            // If there is no CODE and no ELSE_NT
                            else if (children.get(children.size()-1).getLabel().getType() == LexicalUnit.IF_NT) {
                                llvmCode.append("\tbr label %exit" + children.get(i).getIfCounter() + "\n");
                                llvmCode.append("  exit" + children.get(i).getIfCounter() + ":\n");
                            }
                        }
                        break;
                    case WHILE_NT : 
                        wCounter++;
                        children.get(i).setWCounter(wCounter);
                        llvmCode.append("\tbr label %while" + children.get(i).getWCounter() + "\n");
                        llvmCode.append("  while" + children.get(i).getWCounter() + ":\n");
                        children.get(i).getChildren().get(0).setWCounter(wCounter); // So cond can detect if it is a child of WHILE_NT 
                        currentCode = analyze(children.get(i));
                        llvmCode.append(currentCode);
                        // First WHILE
                        if (children.get(i).getWCounter() == 1) {
                            llvmCode.append("\tbr label %while" + children.get(i).getWCounter() + "\n");
                            llvmCode.append("  wexit" + children.get(i).getWCounter() + ":\n");
                        }
                        // The nth WHILE 
                        else if (children.get(i).getWCounter() > 1) {
                            if (children.get(children.size()-1).getLabel().getType() == LexicalUnit.WHILE_NT) {
                                llvmCode.append("\tbr label %while" + children.get(i).getWCounter() + "\n");
                                llvmCode.append("  wexit" + children.get(i).getWCounter() + ":\n");
                            }
                        }
                        break;
                    case COND :
                        // If the first child of COND is a single VARNAME
                        if (children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.VARNAME) {
                            llvmCode.append("\t%" + line + " = load i32, i32* %" + children.get(i).getChildren().get(0).getLabel().getValue() + "\n");
                            children.get(i).getChildren().get(0).setCounter(line);
                            line++;
                        }
                        currentCode = analyze(children.get(i));
                        llvmCode.append(currentCode);
                        llvmCode.append("\t%" + line + " = icmp " + children.get(i).getChildren().get(1).getComp() + " i32 %" +  children.get(i).getChildren().get(0).getCounter() + ", %" + children.get(i).getChildren().get(2).getCounter() + "\n");
                        // If there is a ELSE statement
                        if (children.get(children.size()-1).getLabel().getType() == LexicalUnit.IFTAIL) {
                            llvmCode.append("\tbr i1 %" + line + ", label %true" + ifCounter + ", label %else" + ifCounter + "\n");
                            llvmCode.append("  true" + ifCounter + ":\n");
                        } 
                        // Detect that COND is a child of IF_NT
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
                        if (children.get(i).getIfCounter() == 1) {
                            llvmCode.append("\tbr label %exit\n");
                        }
                        else if (children.get(i).getIfCounter() > 1) {
                            llvmCode.append("\tbr label %exit" + children.get(i).getIfCounter() +"\n");
                        }
                        llvmCode.append("  else" + children.get(i).getIfCounter() + ":\n");
                        currentCode = analyze(children.get(i));
                        llvmCode.append(currentCode);
                        
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
            
            if (llvmCode.toString().contains("Error : undefined variable")) {
                return "Error : undefined variable" ;
            }

            return llvmCode.toString();
    }

    private String toLlvm() {
        String main = "define i32 @main() {" + "\n" +
        "  entry:" + "\n" + 
        "\t" + analyze(this.tree) + 
        "\tret i32 0" + "\n" +
        "}";
        return /*this.read + "\n" + this.print + "\n" +*/ main;
    }

}

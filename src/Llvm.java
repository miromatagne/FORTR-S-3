import java.util.ArrayList;
import java.util.List;

public class Llvm {
    private static ParseTree tree;
    private static String llvmCode;
    private static int line = 0;
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
    }

    public void start() {
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

            //System.out.println("size : " + children.size());
            //System.out.println(children.get(i).getLabel().getType());

            while (i < children.size()) {
                switch (children.get(i).getLabel().getType()) {
                    case CODE:
                        currentCode = analyze(children.get(i));
                        //value = children.get(i).getChildren().get(0).getLabel().getValue().toString();
                        
                        llvmCode.append(currentCode);  // insert(int = 0, string = value) ? 
                        break;
                    case ASSIGN_NT : 
                        currentCode = analyze(children.get(i));
                        value = children.get(i).getChildren().get(0).getLabel().getValue().toString();
                        llvmCode.append(currentCode);  // insert(int = 0, string = value) ? 
                        llvmCode.append(" store i32 %" + String.valueOf((line-1)) + ", i32* %" + value);
                        llvmCode.append("\n");
                        break;
                    case READ_NT : 
                        currentCode = analyze(children.get(i));
                        llvmCode.append(" %" + line + " = call i32 readInt()");
                        llvmCode.append("\n");
                        llvmCode.append(currentCode);
                        llvmCode.append(" store i32 %" + line + ", i32* %" + children.get(i).getChildren().get(0).getLabel().getValue().toString());
                        llvmCode.append("\n");
                        children.get(i).setCounter(line);
                        line++;
                        break;
                    case PRINT_NT :
                        currentCode = analyze(children.get(i)); 
                        llvmCode.append(currentCode);
                        llvmCode.append(" call void @println(i32 %" + children.get(i).getChildren().get(0).getLabel().getValue().toString() +")");
                        llvmCode.append("\n");
                        break;
                    case TIMES :
                        currentCode = analyze(children.get(i));
                        llvmCode.append(currentCode);
                        llvmCode.append(" %" + line + " = mul i32 %");
                        if (children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.VARNAME) {
                            llvmCode.append(children.get(i).getChildren().get(0).getLabel().getValue());
                        }
                        else {
                            llvmCode.append(children.get(i).getChildren().get(0).getCounter()); 
                        }
                        if (children.get(i).getChildren().get(1).getLabel().getType() == LexicalUnit.VARNAME) {
                            llvmCode.append(", %" + children.get(i).getChildren().get(1).getLabel().getValue()); 
                        }
                        else {
                            llvmCode.append(", %" + children.get(i).getChildren().get(1).getCounter()); 
                        }
                        llvmCode.append("\n");
                        children.get(i).setCounter(line);
                        line++;
                        break;
                    case DIVIDE :
                        currentCode = analyze(children.get(i));
                        llvmCode.append(currentCode);
                        llvmCode.append(" %" + line + " = sdiv i32 %"); 
                        if (children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.VARNAME) {
                            llvmCode.append(children.get(i).getChildren().get(0).getLabel().getValue());
                        }
                        else {
                            llvmCode.append(children.get(i).getChildren().get(0).getCounter()); 
                        }
                        if (children.get(i).getChildren().get(1).getLabel().getType() == LexicalUnit.VARNAME) {
                            llvmCode.append(", %" + children.get(i).getChildren().get(1).getLabel().getValue()); 
                        }
                        else {
                            llvmCode.append(", %" + children.get(i).getChildren().get(1).getCounter()); 
                        }
                        llvmCode.append("\n");
                        children.get(i).setCounter(line);
                        line++;
                        break;
                    case PLUS :
                        currentCode = analyze(children.get(i));
                        llvmCode.append(currentCode);
                        llvmCode.append(" %" + line + " = add i32 %");
                        if (children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.VARNAME) {
                            llvmCode.append(children.get(i).getChildren().get(0).getLabel().getValue());
                        }
                        else {
                            llvmCode.append(children.get(i).getChildren().get(0).getCounter()); 
                        }
                        if (children.get(i).getChildren().get(1).getLabel().getType() == LexicalUnit.VARNAME) {
                            llvmCode.append(", %" + children.get(i).getChildren().get(1).getLabel().getValue()); 
                        }
                        else {
                            llvmCode.append(", %" + children.get(i).getChildren().get(1).getCounter()); 
                        }
                        llvmCode.append("\n");
                        children.get(i).setCounter(line);
                        line++;
                        break;
                    case MINUS :
                        currentCode = analyze(children.get(i));
                        llvmCode.append(currentCode);
                        llvmCode.append(" %" + line + " = sub i32 %");
                        if (children.get(i).getChildren().get(0).getLabel().getType() == LexicalUnit.VARNAME) {
                            llvmCode.append(children.get(i).getChildren().get(0).getLabel().getValue());
                        }
                        else {
                            llvmCode.append(children.get(i).getChildren().get(0).getCounter()); 
                        }
                        if (children.get(i).getChildren().get(1).getLabel().getType() == LexicalUnit.VARNAME) {
                            llvmCode.append(", %" + children.get(i).getChildren().get(1).getLabel().getValue()); 
                        }
                        else {
                            llvmCode.append(", %" + children.get(i).getChildren().get(1).getCounter()); 
                        }
                        llvmCode.append("\n");
                        children.get(i).setCounter(line);
                        line++;
                        break;  
                    case VARNAME :
                        if (!values.contains(children.get(i).getLabel().getValue())) {
                            llvmCode.append(" %" + children.get(i).getLabel().getValue().toString() + " = alloca i32");
                            llvmCode.append("\n");
                            values.add(children.get(i).getLabel().getValue());
                        }
                        break;
                    case NUMBER :
                        llvmCode.append(" %"+ line + " = constant i32 " + children.get(i).getLabel().getValue().toString());
                        llvmCode.append("\n");
                        children.get(i).setCounter(line);
                        line++;
                        break;
                    default :
                        break;
                }
                i++;
            }
            

            return llvmCode.toString();
    }

    private String toLlvm() {
        String main = "define i32 @main() {" + "\n" +
        " entry:" + "\n" + 
        analyze(this.tree) + 
        " ret i32 0" + "\n" +
        "}";
        return /*this.read + "\n" + this.print + "\n" +*/ main;
    }

}

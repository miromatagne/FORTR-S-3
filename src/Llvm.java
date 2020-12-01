import java.util.List;

public class Llvm {
    private static ParseTree tree;
    private static String llvmCode;
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
            while (i < children.size()) {
                switch (children.get(i).getLabel().getASTString()) {
                    case "Code" :
                        currentCode = analyze(children.get(i));
                        value = children.get(i).getChildren().get(0).getLabel().getValue().toString();
                        llvmCode.append(currentCode);  // insert(int = 0, string = value) ? 
                        break;
                    case "Assign" : 
                        currentCode = analyze(children.get(i));
                        value = children.get(i).getChildren().get(0).getLabel().getValue().toString();
                        llvmCode.append(currentCode);  // insert(int = 0, string = value) ? 
                        llvmCode.append(" store i32 %value, i32* %" + value);
                        llvmCode.append("\n");
                        break;
                    case "VARNAME" :
                        llvmCode.append(" %" + children.get(i).getLabel().getValue().toString() + " = alloca i32");
                        llvmCode.append("\n");
                        break;
                    case "NUMBER" :
                        llvmCode.append(" %value = constant i32 " + children.get(i).getLabel().getValue().toString());
                        llvmCode.append("\n");
                        break;
                }
                i++;
            }
            

            return llvmCode.toString();
    }

    private String toLlvm() {
        String main = "define i32 @main() {" + "\n" +
        " entry:" + "\n" + 
        analyze(this.tree) + "\n" +
        " ret i32 0" + "\n" +
        "}";
        return /*this.read + "\n" + this.print + "\n" +*/ main;
    }

}

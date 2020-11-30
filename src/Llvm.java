import java.util.List;

public class Llvm {
    private static ParseTree tree;
    private static String llvmCode;


    public Llvm(ParseTree tree) {
        this.tree = tree;

    }
    
    public String getLlvm() {
        return llvmCode;
    }

    private String analyze(ParseTree tree) {
            List<ParseTree> children = tree.getChildren();
            int i = 0;
            while (i < children.size()) {
                switch (children.get(i).getLabel().getASTString()) {
                    case :
                    analyze(children.get(0));
                }
                
            }


            return llvmCode;
    }

}

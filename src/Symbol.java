public class Symbol{
	public static final int UNDEFINED_POSITION = -1;
	public static final Object NO_VALUE = null;
	
	private LexicalUnit type;
	private Object value;
	private int line,column;

	public Symbol(LexicalUnit unit, int line, int column, Object value) {
		this.type = unit;
		this.line = line + 1;
		this.column = column;
		this.value = value;
	}

	public Symbol(LexicalUnit unit, int line, int column) {
		this(unit, line, column, NO_VALUE);
	}

	public Symbol(LexicalUnit unit, int line) {
		this(unit, line, UNDEFINED_POSITION, NO_VALUE);
	}

	public Symbol(LexicalUnit unit) {
		this(unit, UNDEFINED_POSITION, UNDEFINED_POSITION, NO_VALUE);
	}

	public Symbol(LexicalUnit unit, Object value) {
		this(unit, UNDEFINED_POSITION, UNDEFINED_POSITION, value);
	}

	public boolean isTerminal(){
		switch (this.type) {
			case BEGINPROG:
			case PROGNAME:
			case ENDLINE:
			case ENDPROG:
			case VARNAME:
			case ASSIGN:
			case NUMBER:
			case LPAREN:
			case RPAREN:
			case MINUS:
			case PLUS:
			case TIMES:
			case DIVIDE:
			case IF:
			case THEN:
			case ENDIF:
			case ELSE:
			case EQ:
			case GT:
			case WHILE:
			case DO:
			case PRINT:
			case READ:
			case EOS:
				return true;
			default:
				return false;
		}
	}
	
	public boolean isNonTerminal(){
		return this.type == null;
	}
	
	public LexicalUnit getType(){
		return this.type;
	}
	
	public Object getValue(){
		return this.value;
	}

	public void setValue(Object value){
		this.value = value;
	}
	
	public int getLine(){
		return this.line;
	}
	
	public int getColumn(){
		return this.column;
	}
	
	@Override
	public int hashCode(){
		final String value	= this.value != null? this.value.toString() : "null";
		final String type		= this.type  != null? this.type.toString()  : "null";
		return new String(value+"_"+type).hashCode();
	}
	
	@Override
	public String toString(){
		if(this.isTerminal()){
			final String value = this.value != null? this.value.toString() : "null";
			final String type = this.type  != null? this.type.toString()  : "null";
      		return String.format("token: %-15slexical unit: %s", value, type);
		}
		return "Non-terminal symbol";
	}

	/**
	 * Return the type and the value of a root
	 * @return string corresponding to the type and value of a root
	 */
	public String toTexString(){
		if(this.isTerminal()){
			final String value = this.value != null? this.value.toString() : "null";
			final String type = this.type  != null? this.type.toString()  : "null";
			if(this.type == LexicalUnit.GT){
				return String.format("%s \\textgreater", type);
			}
      		return String.format("\\textbf{%s}  %s", type, value); 
		}
		else{
			final String upperCaseName = this.type != null? this.type.toString() : "null";
			String lowerCaseName = upperCaseName.toLowerCase();
			String name = lowerCaseName.substring(0, 1).toUpperCase() + lowerCaseName.substring(1);
			int pos = name.indexOf("_");
			if(pos != -1) {
				name = name.substring(0,pos);
			}
			int pos2 = name.indexOf("prime");
			if(pos2 != -1) {
				name = name.substring(0,pos2);
				name += "'";
			}
			if(name.equals("Epsilon")){
				return String.format("$\\varepsilon$");
			}
			else{
				return String.format("\\textbf{\\textless %s\\textgreater}", name);
			}
		}
	}

	public String getASTString() {
		if(this.isTerminal()){
      		return type.toString(); 
		}
		else{
			return value.toString();
		}
	}
}

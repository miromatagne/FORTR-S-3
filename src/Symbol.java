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

	/**
	 * Constructor of the Symbol class of a non-terminal 
	 * @param value string indicating the name of a non-terminal symbol
	 */
	public Symbol(Object value) { 
		this(null, UNDEFINED_POSITION, UNDEFINED_POSITION, NO_VALUE);
		this.value = value;
	}

	public boolean isTerminal(){
		return this.type != null;
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
			final String name = this.value != null? this.value.toString() : "null";
			if(name == "epsilon"){
				return String.format("$\\varepsilon$");
			}
			else{
				return String.format("\\textbf{\\textless %s\\textgreater}", name);
			}
		}
	}
}

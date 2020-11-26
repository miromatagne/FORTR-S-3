%%// Options of the scanner

%class LexicalAnalyzer	//Name
%unicode		//Use unicode
%line         	//Use line counter (yyline variable)
%column       	//Use character counter by line (yycolumn variable)
%type Symbol  //Says that the return type is Symbol
%function nextToken

%xstate YYINITIAL, COMMENT


%{
	private int commentDepth = 0;
%}

//At the end of the program, we check if a comment has not been closed, and notify the
//user if it is the case, and then return an EOS token.
%eofval{
	if(commentDepth != 0) {
		System.out.println("COMMENT ERROR");
	}
	return new Symbol(LexicalUnit.EOS, yyline, yycolumn);
%eofval}

// Extended Regular Expressions

//Regular expressions concerning the letters and numbers, as well as 
//combinations of letters and numbers.
AlphaUpperCase 	= [A-Z]
AlphaLowerCase 	= [a-z]
Alpha          	= {AlphaUpperCase}|{AlphaLowerCase}
Numeric        	= [0-9]
AlphaNumeric   	= {Alpha}|{Numeric}
AlphaLowNumeric	= {AlphaLowerCase}|{Numeric}

//Regular expressions for a space (or tabulation) or an end of line
Space          	= "\t"|" "	
EndOfLine	    = "\r"?"\n"

//Regular expression detecting a number (not to be confused with the Numeric
//regular expression, which detects a single numeric character). This regular 
//expression ensures that a number does not begin with a 0
Number         	= (([1-9][0-9]*)|0)

//Regular expressions regarding variable names (VARNAME) or program names
//(PROGNAME).
VarName        	= {AlphaLowerCase}{AlphaLowNumeric}*
ProgName       	= {AlphaUpperCase}+{AlphaLowNumeric}+{AlphaNumeric}*

//Regular expression to detect an inline comment, starting with // and
//ending at the end of the current line.
InlineComment 	= "//".*

//Error handling. These regular expressions allow to detect various syntaxic
//errors, and separating them into 4 categories : words starting with a lower
//case and containing upper case characters, words fully written in upper case,
//a word starting with a number but containing letters, and a number starting with a 0.
VarnameError 	= {AlphaLowerCase}+{AlphaLowNumeric}*{AlphaUpperCase}+{AlphaNumeric}*
ProgNameError 	= {AlphaUpperCase}+ 
NumError		= {Numeric}+{Alpha}+{AlphaNumeric}*
ZeroError       = (0){Numeric}+

%%// Identification of tokens

//This is the initial state.
<YYINITIAL>{
	"/*"				{commentDepth++;yybegin(COMMENT);}
	{InlineComment}		{}

	{VarnameError}	{System.out.println("VAR ERROR :" + yytext());}
	{NumError}		{System.out.println("NUM ERROR :" + yytext());}
	{ZeroError}     {System.out.println("ZERO ERROR :" + yytext());}

	{ProgName}      {return new Symbol(LexicalUnit.PROGNAME,yyline, yycolumn, yytext());}
	{Number}        {return new Symbol(LexicalUnit.NUMBER,yyline, yycolumn, yytext());}
	{VarName}       {return new Symbol(LexicalUnit.VARNAME,yyline, yycolumn, yytext());}

	":="            {return new Symbol(LexicalUnit.ASSIGN,yyline, yycolumn, yytext());}

	// Check weather the program starts or ends
	"BEGINPROG"	    {return new Symbol(LexicalUnit.BEGINPROG,yyline, yycolumn,yytext());}
	"ENDPROG"		{return new Symbol(LexicalUnit.ENDPROG,yyline, yycolumn,yytext());}

	//Operations
	"+"				{return new Symbol(LexicalUnit.PLUS,yyline, yycolumn, yytext());}
	"-"				{return new Symbol(LexicalUnit.MINUS,yyline, yycolumn, yytext());}
	"*"				{return new Symbol(LexicalUnit.TIMES,yyline, yycolumn, yytext());}
	"/"				{return new Symbol(LexicalUnit.DIVIDE,yyline, yycolumn, yytext());}

	//Check for parenthesis or commas
	"("				{return new Symbol(LexicalUnit.LPAREN,yyline, yycolumn, yytext());}
	")"				{return new Symbol(LexicalUnit.RPAREN,yyline, yycolumn, yytext());}

	// Relational operators
	"=="	        { return new Symbol(LexicalUnit.EQ,yyline, yycolumn, yytext());}
	">"		        {return new Symbol(LexicalUnit.GT,yyline, yycolumn,yytext());}

	//PRINT and READ commands
	"PRINT"	        {return new Symbol(LexicalUnit.PRINT,yyline, yycolumn, yytext());}
	"READ"	        {return new Symbol(LexicalUnit.READ,yyline, yycolumn, yytext());}


	// If/Else keywords
	"IF"	        {return new Symbol(LexicalUnit.IF,yyline, yycolumn, yytext());}
	"THEN"          {return new Symbol(LexicalUnit.THEN,yyline, yycolumn, yytext());}
	"ENDIF"         {return new Symbol(LexicalUnit.ENDIF,yyline, yycolumn, yytext());}
	"ELSE"          {return new Symbol(LexicalUnit.ELSE,yyline, yycolumn, yytext());}
	"WHILE"			{return new Symbol(LexicalUnit.WHILE,yyline, yycolumn, yytext());}
	"DO"			{return new Symbol(LexicalUnit.DO,yyline, yycolumn, yytext());}
	"ENDWHILE"		{return new Symbol(LexicalUnit.ENDWHILE,yyline, yycolumn, yytext());}


	// Ignore the rest
	{ProgNameError}	{System.out.println("PROGNAME ERROR :" + yytext());}
	{Space}         {}
	.               {System.out.println("ERROR :" + yytext());}
	{EndOfLine}		{return new Symbol(LexicalUnit.ENDLINE,yyline, yycolumn, "\\n");}
}

//This is the comment state. It increments a counter everytime we move to a further nested 
//comment, and decrements it when the end of comment regular expression is shown. If the
//counter goes back to 0, we go back to the initial state. This allows to handle nested
//comments.
<COMMENT>{
	"*/"			{commentDepth--; if(commentDepth == 0) {yybegin(YYINITIAL);}}
	"/*"			{commentDepth++;}
	.				{}
	{EndOfLine}		{}	
}
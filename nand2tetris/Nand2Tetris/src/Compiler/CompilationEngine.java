// TODO Fix this shit code

package Compiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class CompilationEngine {

	JackTokenizer tokenizer;
	private File file;
	private FileOutputStream out;
	private OutputStreamWriter writer;
	private int tabCounter;

	
	CompilationEngine(String infile, String outfile) throws Exception{
		tokenizer = new JackTokenizer(infile);
		file = new File(outfile);
		out = new FileOutputStream(file);
		writer = new OutputStreamWriter(out);
		tabCounter = 0;
		tokenizer.advance();
		if(isCorrectToken(TokenType.KEYWORD) &&  isCorrectKeyword(KeyWord.CLASS)){
			compileClass();
		}
		writer.close();
	}
	
	void compileClass() throws Exception{
		write("<class>");
		
		tabCounter++;
		writeKeyword();
		
		tokenizer.advance();
		writeIdentifier();
		
		tokenizer.advance();
		writeSymbol('{');
		
		while(tokenizer.advance().matches("static|field")){
			compileClassVarDec();
		}
		
		do{
			compileSubroutineDec();
		}while(tokenizer.advance().matches("constructor|function|method"));
		
		writeSymbol('}');
		
		tabCounter=0;
		write("</class>");
	}
	
	void compileSubroutineDec() throws Exception{
		write("<subroutineDec>");
		int tmp = tabCounter++;
		
		writeConstOrFuncOrMethod();
		
		tokenizer.advance();
		writeVoidOrType();
		
		tokenizer.advance();
		writeIdentifier();
		
		tokenizer.advance();
		writeSymbol('(');
		
		tokenizer.advance();
		compileParameterList();
		
		writeSymbol(')');
				
		tokenizer.advance();
		compileSubroutineBody();
		
		tabCounter=tmp;
		write("</subroutineDec>");

	}
	
	void compileParameterList() throws Exception{
		write("<parameterList>");
		if(!(tokenizer.symbol() == ')')){
			int tmp = tabCounter++;
			writeTypeAndIdentifier();
		
			while(writeComma()){
				tokenizer.advance();
				writeTypeAndIdentifier();
				tokenizer.advance();
			}
			tabCounter = tmp;
		}
		write("</parameterList>");

	}
	
	private void writeTypeAndIdentifier() throws Exception{
		writeType();
		tokenizer.advance();
		writeIdentifier();
	}
	
	boolean writeComma() throws Exception{
			writeSymbol(',');
			return tokenizer.symbol() == ',';
	}
	
	boolean compileLet() throws Exception{
		boolean flag = tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() == KeyWord.LET;
		if(flag){
			writeKeyword();
			
			tokenizer.advance();
			writeIdentifier();
			tokenizer.advance();
			
			if(writeSymbol('[')){
				tokenizer.advance();
				compileExpression();
				writeSymbol(']');
				tokenizer.advance();
			}
			
			writeSymbol(';');
		}
		return flag;
	}
	
	boolean compileIf() throws Exception{
		boolean flag = tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() == KeyWord.IF;
		if(flag){
			writeKeyword();
			
			tokenizer.advance();
			writeSymbol('(');
			
			tokenizer.advance();
			compileExpression();
			
			tokenizer.advance();
			writeSymbol(')');
			
			tokenizer.advance();
			writeSymbol('{');
			
			tokenizer.advance();
			compileStatements();
			
			tokenizer.advance();
			writeSymbol('}');
			
			if(tokenizer.advance().equals("else")){
				writeKeyword();
				
				tokenizer.advance();
				writeSymbol('{');
				
				tokenizer.advance();
				compileStatements();
				
				tokenizer.advance();
				writeSymbol('}');
			};
		}
		
		return flag;
	}
	
	boolean compileWhile() throws Exception{
		boolean flag = tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() == KeyWord.WHILE;
		if(flag){
			writeKeyword();
			
			tokenizer.advance();
			writeSymbol('(');
			
			tokenizer.advance();
			compileExpression();
			
			tokenizer.advance();
			writeSymbol(')');
			
			tokenizer.advance();
			writeSymbol('{');
			
			tokenizer.advance();
			compileStatements();
			
			tokenizer.advance();
			writeSymbol('}');
			
		}
		return flag;

	}
	
	boolean compileDo() throws Exception{
		boolean flag = tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() == KeyWord.DO;
		if(flag){
			writeKeyword();
			tokenizer.advance();
			
			compileSubroutine();
			
			tokenizer.advance();
			writeSymbol(';');
		}
		
		return flag;
	}
	
	boolean compileReturn() throws Exception{
		boolean flag = tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() == KeyWord.RETURN;
		if(flag){
			writeKeyword();
			tokenizer.advance();

			compileExpression();
			writeSymbol(';');
		}
		return flag;
		
	}
	
	void compileSubroutine() throws Exception{
		writeIdentifier();
		
		tokenizer.advance();
		if(writeSymbol('.')){
			tokenizer.advance();
			writeIdentifier();
			tokenizer.advance();
		}
		
		writeSymbol('(');
		
		tokenizer.advance();
		compileExpressionList();
		
		writeSymbol(')');
	}
	
	
	
	void compileExpression() throws Exception{
		
		if(compileTerm()){
			tokenizer.advance();
		
			while(compileOp()){
				tokenizer.advance();
				compileTerm();
				tokenizer.advance();
			}
		}	
	}
	void compileExpressionList() throws Exception{
		compileExpression();
		while(writeComma()){
			compileExpression();
		}
	};
	
	boolean compileOp() throws Exception{
		boolean flag = tokenizer.tokenType() == TokenType.SYMBOL;
		String s = ""+tokenizer.symbol();
		if((flag = (flag && s.matches("^(+|-|\\*|/|&|\\||<|>|=|~)$"))) && writeSymbol(s.charAt(0)));
		return flag;
	}
	boolean compileTerm(){return true;}
	void compileSubroutineBody() throws Exception{
		write("<subroutineBody>");
		int tmp = tabCounter++;

		writeSymbol('{');
		tokenizer.advance();

		compileVarDec();
		
		compileStatements();
		
		tokenizer.advance();
		writeSymbol('}');
		tabCounter = tmp;
		write("</subroutineBody>");
	};
	
	void compileVarDec() throws Exception{
		write("<varDec>");
		int tmp = tabCounter++;
		if(writeVar()){
			tokenizer.advance();
			writeTypeAndIdentifier();
			while(writeComma()){
				tokenizer.advance();
				writeTypeAndIdentifier();
				tokenizer.advance();
			}
		}
		writeSymbol(';');
		tabCounter = tmp;
		write("</varDec>");
	};
	
	private boolean writeVar() throws Exception{
		boolean flag = tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() == KeyWord.VAR;
		if(flag){
			writeKeyword();
		}
		return flag;
	}
	
	private void writeType() throws IOException{
		if(isType()){
			writeKeyword();
		}
	}
	
	private boolean isType(){
		return (isCorrectKeyword(KeyWord.BOOLEAN)|| isCorrectKeyword(KeyWord.CHAR)||isCorrectKeyword(KeyWord.INT));
	}
	
	private void writeConstOrFuncOrMethod() throws IOException, Exception{
		if(isCorrectToken(TokenType.KEYWORD) && 
				(isCorrectKeyword(KeyWord.FUNCTION)||isCorrectKeyword(KeyWord.CONSTRUCTOR) ||(isCorrectKeyword(KeyWord.METHOD) ))){
			writeKeyword();
		}
	}
	
	private void writeVoidOrType() throws IOException, Exception{
		if(isCorrectToken(TokenType.KEYWORD) &&
				(isCorrectKeyword(KeyWord.VOID)||isCorrectKeyword(KeyWord.BOOLEAN)||
						isCorrectKeyword(KeyWord.CHAR)||isCorrectKeyword(KeyWord.INT))){
			writeKeyword();
		}
	}
	
	void compileClassVarDec() throws Exception{
		int tmp = tabCounter++;
		write("<classVarDec>");
		compileStatements();
		tabCounter = tmp;
		write("</classVarDec>");
	}
	
	boolean compileStatement() throws Exception{
		return compileLet() || compileIf() || compileWhile() || compileDo() || compileReturn();
	}
	
	
	void compileStatements() throws Exception{
		while(compileStatement()){
			tokenizer.advance();
		};
	};

	private void writeIntConst() throws IOException{
		write("<integerConstant>"+tokenizer.intVal()+"</integerConstant>");
	}
	
	private void writeStringConst() throws IOException{
		write("<stringConstant>"+tokenizer.intVal()+"</stringConstant>");
	}
	
	
	private void writeIdentifier() throws Exception{
		if(isCorrectToken(TokenType.IDENTIFIER)){
			write("<identifier>"+tokenizer.identifier()+"</identifier>");
		}
	}
	
	private boolean writeSymbol(char s) throws Exception{
		boolean flag = isCorrectSymbol(s);
		if(flag){
			write("<symbol>"+tokenizer.symbol()+"</symbol>");
		}
		return flag;
	}
	
	private void writeKeyword() throws IOException{
		write("<keyword>"+tokenizer.getKeyword()+"</keyword>");
	}
	
	private boolean isCorrectToken(TokenType t) throws Exception{
		return tokenizer.tokenType() == t;
	}
	
	private boolean isCorrectSymbol(char s) throws Exception{
		return (tokenizer.tokenType() == TokenType.SYMBOL) && (tokenizer.symbol() == s);
	}
	
	private boolean isCorrectKeyword(KeyWord k){
		return tokenizer.keyWord() == k;
	}
	
	
	void write(String line) throws IOException{
		for(int i=0;i<tabCounter;i++){
			writer.write("\t");
		}
		writer.write(line + "\n");
	}
	
	void close() throws IOException{
		writer.close();
	}
	
	public static void main(String[] argv) throws Exception{
		CompilationEngine c = new CompilationEngine("sample/Square/Main.jack", "sample/Square/test.xml");
	}
}




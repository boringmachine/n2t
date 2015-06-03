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
		
		if(!tokenizer.advance().equals(")")){
			compileParameterList();
		} else{
			writeSymbol(')');
		}
		
		tokenizer.advance();
		writeSymbol(')');
		
		tokenizer.advance();
		compileSubroutineBody();
		
		tabCounter=tmp;
		write("</subroutineDec>");

	}
	
	void compileParameterList() throws Exception{
		write("<parameterList>");
		int tmp = tabCounter++;
		writeTypeAndIdentifier();
		
		while(writeComma()){
			tokenizer.advance();
			writeTypeAndIdentifier();
			tokenizer.advance();
		}
		tabCounter = tmp;
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
	
	void compileSubroutineBody() throws Exception{
		write("<subroutineBody>");
		int tmp = tabCounter++;

		writeSymbol('{');
		tokenizer.advance();

		while(tokenizer.advance().equals("var")){
			compileVarDec();
		}
		
		compileStatements();
		
		tokenizer.advance();
		writeSymbol('}');
		tabCounter = tmp;
		write("</subroutineBody>");
	};
	
	void compileVarDec() throws Exception{
		write("<varDec>");
		int tmp = tabCounter++;
		writeVar();
		
		tokenizer.advance();
		writeTypeAndIdentifier();
		while(writeComma()){
			tokenizer.advance();
			writeTypeAndIdentifier();
			tokenizer.advance();
		}
		tabCounter = tmp;
		write("</varDec>");
	};
	
	private void writeVar() throws Exception{
		if(tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() == KeyWord.VAR){
			writeKeyword();
		}
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
	
	void compileStatements() throws Exception{
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
	
	private void writeSymbol(char s) throws Exception{
		if(isCorrectSymbol(s)){
			write("<symbol>"+tokenizer.symbol()+"</symbol>");
		}
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
		CompilationEngine c = new CompilationEngine("Square/Main.jack", "Square/test.xml");
	}
}




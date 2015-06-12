// TODO Fix this shit code

package Compiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class CompilationEngine {

	public static void main(String[] argv) throws Exception {
		CompilationEngine c = new CompilationEngine(argv[0], argv[1]);
	}

	private File file;
	private FileOutputStream out;
	private int tabCounter;
	JackTokenizer tokenizer;

	private OutputStreamWriter writer;

	CompilationEngine(String infile, String outfile) throws Exception {
		tokenizer = new JackTokenizer(infile);
		file = new File(outfile);
		out = new FileOutputStream(file);
		writer = new OutputStreamWriter(out);
		tabCounter = 0;
		tokenizer.advance();
		compileClass();
		writer.close();
	}

	void close() throws IOException {
		writer.close();
	}
	
	void compileClass() throws Exception{
		writeTag("class");
		int tmp = tabCounter++;
		
		writeKeyword(KeyWord.CLASS, "");
		writeClassName("before");
		warning(writeSymbol("{", "before"), "warn: {");
		while(compileClassVarDec("before"));
		while(compileSubroutineDec());
		warning(writeSymbol("}", ""), "warn: }");
		
		tabCounter = tmp;
		writeTag("/class");
	}
	
	void warning(boolean sentence, String e) throws Exception{
		if(!sentence){
			throw new Exception(e);
		}
	}
	
	boolean compileClassVarDec(String advancePoint) throws Exception{
		writeTag("classVarDec");
		int tmp = tabCounter++;
		
		if(advancePoint.equals("before")){
			tokenizer.advance();
		}
		
		boolean flag = false;
		if(tokenizer.keyWord() == KeyWord.STATIC || tokenizer.keyWord() == KeyWord.FIELD){
			warning(flag = writeKeyword(KeyWord.STATIC, "") || writeKeyword(KeyWord.FIELD, ""),
					"warn: static|field");
		
			warning(writeType("before"), "warn: type is wrong.");
			warning(writeVarName("before"), "warn: varName is wrong");
		
			while(writeSymbol(",", "before")){
				writeVarName("before");
			}
		
			writeSymbol(";", "");
		
			if(advancePoint.equals("after")){
				tokenizer.advance();
			}
		
			tabCounter = tmp;
			writeTag("/classVarDec");
		
		}
		return flag;

	}
	
	
	boolean writeType(String advancePosition) throws Exception{
		if(advancePosition.equals("before")){
			tokenizer.advance();
		}
		boolean flag = 
				(tokenizer.tokenType() == TokenType.KEYWORD && 
					(tokenizer.keyWord() == KeyWord.BOOLEAN 
						|| tokenizer.keyWord() == KeyWord.INT
						|| tokenizer.keyWord() == KeyWord.CHAR
					)
				) || (tokenizer.tokenType() == TokenType.IDENTIFIER);
		if(flag){
			writeKeyword(tokenizer.keyWord(), "");
			writeIdentifier("");
		}
		return flag;
	}
	
	boolean writeVarName(String advancePoint) throws Exception{
		return writeIdentifier(advancePoint);
	}
	
	boolean compileSubroutineDec() throws Exception{

		boolean flag = 
				   tokenizer.keyWord() == KeyWord.CONSTRUCTOR 
				|| tokenizer.keyWord() == KeyWord.FUNCTION 
				|| tokenizer.keyWord() == KeyWord.METHOD;
		if(flag){
			writeTag("subroutineDec");
			int tmp = tabCounter++;
			warning(
			   writeKeyword(KeyWord.CONSTRUCTOR, "") || 
			   writeKeyword(KeyWord.FUNCTION, "") || 
			   writeKeyword(KeyWord.METHOD, ""), 
			   "warn: missing constructor|function|method");
		
			tokenizer.advance();
			warning(writeKeyword(KeyWord.VOID, "") || writeType(""), "warn: missing void|type");
		
			warning(writeSubroutineName("before"), "warn: wrong subroutineName");
			warning(writeSymbol("(", "before"), "warn: missing (");
			compileParameterList();
			warning(writeSymbol(")", ""), "warn: missing )");
			System.out.println("ho"+tokenizer.symbol());
			compileSubroutineBody();
			
			tabCounter = tmp;
			writeTag("/subroutineDec");
		}
		
		tokenizer.advance();
		
		return flag;
	}
	
	boolean compileSubroutineBody() throws Exception{
		writeTag("subroutineBody");
		int tmp = tabCounter++;
		
		tokenizer.advance();
		warning(writeSymbol("{","after"), "warn: missing {");
		while(compileVarDec());
		compileStatements();
		warning(writeSymbol("}", ""), "warn: missing }");
		
		tabCounter = tmp;
		writeTag("/subroutineBody");
		return true;
	}
	
	boolean compileStatements() throws Exception{
		return true;
	}
	
	boolean compileVarDec() throws Exception{
		
		boolean flag = tokenizer.keyWord() == KeyWord.VAR;
		if(flag){
			writeTag("varDec");
			int tmp = tabCounter++;
			
			warning(writeKeyword(KeyWord.VAR, ""), "warn: missing var");
			warning(writeType("before"), "warn: missing type");
			warning(writeVarName("before"), "warn: missing varName");
			while(writeSymbol(",", "before")){
				writeVarName("before");
			}
			
			tabCounter = tmp;
			writeTag("/varDec");
		}
		
		return flag;
	}
	
	boolean compileParameterList() throws Exception{
		writeTag("parameterList");
		int tmp = tabCounter++;
		
		if(tokenizer.advance().equals(")")){
			tabCounter = tmp;
			writeTag("/parameterList");
			return false;
		}
	
		
		warning(writeType(""), "warn: wrong type");
		warning(writeVarName("before"), "warn: wrong varName");
		
		
		while(writeSymbol(",", "before")){
			warning(writeType("before"), "warn: wrong type");
			warning(writeVarName("before"), "warn: wrong varName");
		}
		
		tabCounter = tmp;
		writeTag("/parameterList");
		return true;
	}
	
	
	
	boolean writeKeyword(KeyWord k, String advancePoint) throws Exception{
		if(advancePoint.equals("before")){
			tokenizer.advance();
		}
		boolean flag = tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() == k;
		if(flag){
			String s = "";
			switch(tokenizer.keyWord()){
			case CLASS:
				s = "class";
				break;
			case STATIC:
				s = "static";
				break;
			case FIELD:
				s = "field";
				break;
			case INT:
				s = "int";
				break;
			case CHAR:
				s = "char";
				break;
			case BOOLEAN:
				s = "boolean";
				break;
			case CONSTRUCTOR:
				s = "constructor";
				break;
			case FUNCTION:
				s = "function";
				break;
			case METHOD:
				s = "method";
				break;
			case VOID:
				s = "void";
				break;
			case VAR:
				s = "var";
				break;
			}
			write("<keyword>"+s+"</keyword>");

		}
		
		
		if(advancePoint.equals("after")){
			tokenizer.advance();
		}
		return flag;
	}
	
	boolean writeClassName(String advancePoint) throws Exception{
		return writeIdentifier(advancePoint);
	}
	
	boolean writeSubroutineName(String advancePoint) throws Exception{
		return writeIdentifier(advancePoint);
	}
	
	boolean writeIdentifier(String advancePoint) throws Exception{
		if(advancePoint.equals("before")){
			tokenizer.advance();
		}
		boolean flag = tokenizer.tokenType() == TokenType.IDENTIFIER;
		if(flag){
			write("<identifier>"+tokenizer.identifier()+"</identifier>");
		}
		if(advancePoint.equals("after")){
			tokenizer.advance();
		}
		return flag;
	}
	
	
	boolean writeSymbol(String s, String advancePoint) throws Exception{
		if(advancePoint.equals("before")){
			tokenizer.advance();
		}
		boolean flag = tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals(s);
		if(flag){
			write("<symbol>"+s+"</symbol>");
		}
		if(advancePoint.equals("after")){
			tokenizer.advance();
		}
		return flag;
	}

	void writeTag(String s) throws Exception{
		write("<"+s+">");
	}
	
	void write(String s) throws Exception{
		for(int i=0; i<tabCounter; i++){
			writer.write("  ");
		}
		writer.write(s + "\n");
	}
}
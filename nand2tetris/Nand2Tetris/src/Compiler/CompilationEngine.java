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
		if (isCorrectToken(TokenType.KEYWORD)
				&& isCorrectKeyword(KeyWord.CLASS)) {
			compileClass();
		}
		writer.close();
	}

	void close() throws IOException {
		writer.close();
	}

	void compileClass() throws Exception {
		write("<class>");

		tabCounter++;
		writeKeyword();

		tokenizer.advance();
		writeIdentifier();

		tokenizer.advance();
		writeSymbol('{');

		while (tokenizer.advance().matches("^(static|field)$")) {
			compileClassVarDec();
		}

		do {
			compileSubroutineDec();
		} while (tokenizer.advance().matches("^(constructor|function|method)$"));

		writeSymbol('}');

		tabCounter = 0;
		write("</class>");
	}

	void compileClassVarDec() throws Exception {
		write("<classVarDec>");
		int tmp = tabCounter++;
		compileStatements();
		tabCounter = tmp;
		write("</classVarDec>");
	}

	boolean compileDo() throws Exception {
		boolean flag = tokenizer.tokenType() == TokenType.KEYWORD
				&& tokenizer.keyWord() == KeyWord.DO;
		if (flag) {
			write("<doStatement>");
			int tmp = tabCounter++;
			writeKeyword();
			tokenizer.advance();

			compileSubroutine();

			tokenizer.advance();
			writeSymbol(';');
			tabCounter = tmp;
			write("</doStatement>");
		}

		return flag;
	}

	void compileExpression() throws Exception {
		write("<expression>");
		int tmp = tabCounter++;

		compileTerm();
		tokenizer.advance();

		while (writeOp()) {
			tokenizer.advance();
			compileTerm();
			tokenizer.advance();
		}

		tabCounter = tmp;
		write("</expression>");
	}

	void compileExpressionList() throws Exception {
		write("<expressionList>");
		int tmp = tabCounter++;

		compileExpression();
		while (writeComma()) {
			compileExpression();
		}

		tabCounter = tmp;
		write("</expressionList>");
	}

	boolean compileIf() throws Exception {
		boolean flag = tokenizer.tokenType() == TokenType.KEYWORD
				&& tokenizer.keyWord() == KeyWord.IF;
		if (flag) {
			write("<ifStatement>");
			int tmp = tabCounter++;
			writeKeyword();

			tokenizer.advance();
			writeSymbol('(');

			tokenizer.advance();
			compileExpression();

			writeSymbol(')');

			tokenizer.advance();
			writeSymbol('{');

			tokenizer.advance();
			compileStatements();

			tokenizer.advance();
			writeSymbol('}');

			if (tokenizer.advance().equals("else")) {
				writeKeyword();

				tokenizer.advance();
				writeSymbol('{');

				tokenizer.advance();
				compileStatements();

				tokenizer.advance();
				writeSymbol('}');

				tabCounter = tmp;
				write("</ifStatement>");
			}
			;
		}

		return flag;
	}

	boolean compileLet() throws Exception {
		boolean flag = tokenizer.tokenType() == TokenType.KEYWORD
				&& tokenizer.keyWord() == KeyWord.LET;
		if (flag) {
			write("<letStatement>");
			int tmp = tabCounter++;

			writeKeyword();

			tokenizer.advance();
			writeIdentifier();
			tokenizer.advance();

			if (writeSymbol('[')) {
				tokenizer.advance();
				compileExpression();
				writeSymbol(']');
				tokenizer.advance();
			}
			writeSymbol('=');

			tokenizer.advance();
			compileExpression();
			
			writeSymbol(';');

			tabCounter = tmp;
			write("</letStatement>");

		}
		return flag;
	}

	void compileParameterList() throws Exception {
		write("<parameterList>");
		if (!(tokenizer.symbol() == ')')) {
			int tmp = tabCounter++;
			writeTypeAndIdentifier();

			while (writeComma()) {
				tokenizer.advance();
				writeTypeAndIdentifier();
				tokenizer.advance();
			}
			tabCounter = tmp;
		}
		write("</parameterList>");

	}

	boolean compileReturn() throws Exception {
		boolean flag = tokenizer.tokenType() == TokenType.KEYWORD
				&& tokenizer.keyWord() == KeyWord.RETURN;
		if (flag) {
			write("<returnStatement>");
			int tmp = tabCounter++;

			writeKeyword();
			tokenizer.advance();

			if (!writeSymbol(';')) {
				compileExpression();
				writeSymbol(';');
			}
			tabCounter = tmp;
			write("</returnStatements>");
		}
		return flag;

	}

	boolean compileStatement() throws Exception {
		return compileLet() || compileIf() || compileWhile() || compileDo()
				|| compileReturn();
	}

	void compileStatements() throws Exception {
		write("<statements>");
		int tmp = tabCounter++;

		while (compileStatement()) {
			tokenizer.advance();
		}
		;

		tabCounter = tmp;
		write("</statements>");
	};

	void compileSubroutine() throws Exception {
			
		if (writeIdentifier()) {
			tokenizer.advance();
		}
		if (writeSymbol('.')) {
			tokenizer.advance();
			writeIdentifier();
			tokenizer.advance();
		} 

		if(writeSymbol('(')){
			tokenizer.advance();

			if (!writeSymbol(')')) {
				compileExpressionList();
				writeSymbol(')');
			}
		}
	}

	void compileSubroutineBody() throws Exception {
		write("<subroutineBody>");
		int tmp = tabCounter++;

		writeSymbol('{');
		tokenizer.advance();

		while (tokenizer.keyWord() == KeyWord.VAR) {
			compileVarDec();
			tokenizer.advance();
		}
		compileStatements();

		tokenizer.advance();
		writeSymbol('}');
		tabCounter = tmp;
		write("</subroutineBody>");
	}

	void compileSubroutineDec() throws Exception {
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

		tabCounter = tmp;
		write("</subroutineDec>");

	}

	void compileTerm() throws Exception {
		write("<term>");
		int tmp = tabCounter++;
		boolean flagA = false;
		boolean flagB = false;
		boolean flagC = false;
		if (writeIntConst() || writeStringConst() || writeKeywordConst()
				|| (flagA = writeIdentifier()) || (flagB =writeUnaryOp()) 
				|| (flagC = writeSymbol('('))) {
			if (flagA) {
				tokenizer.advance();
				if (writeSymbol('[')) {
					tokenizer.advance();
					compileExpression();
					writeSymbol(']');
				} else {
					compileSubroutine();
				}
			} else if(flagB){
				tokenizer.advance();
				compileTerm();
			} else if(flagC){ 
				tokenizer.advance();
				compileExpression();
				writeSymbol(')');
			}

		}
		tabCounter = tmp;
		write("</term>");
	};

	void compileVarDec() throws Exception {
		write("<varDec>");
		int tmp = tabCounter++;
		if (writeVar()) {
			tokenizer.advance();
			writeTypeAndIdentifier();
			tokenizer.advance();
			while (writeComma()) {
				tokenizer.advance();
				writeIdentifier();
				tokenizer.advance();
				if (writeSymbol(';')) {
					tabCounter = tmp;
					write("</varDec>");
					return;
				}
			}
			writeSymbol(';');

		}
		tabCounter = tmp;
		write("</varDec>");
	};

	boolean compileWhile() throws Exception {
		boolean flag = tokenizer.tokenType() == TokenType.KEYWORD
				&& tokenizer.keyWord() == KeyWord.WHILE;
		if (flag) {
			writeKeyword();

			tokenizer.advance();
			writeSymbol('(');

			tokenizer.advance();
			compileExpression();

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

	private boolean isCorrectKeyword(KeyWord k) {
		return tokenizer.keyWord() == k;
	}

	private boolean isCorrectSymbol(char s) throws Exception {
		return isCorrectToken(TokenType.SYMBOL) && (tokenizer.symbol() == s);
	}

	private boolean isCorrectToken(TokenType t) throws Exception {
		return tokenizer.tokenType() == t;
	}

	private boolean isType() throws Exception {
		return (isCorrectKeyword(KeyWord.BOOLEAN)
				|| isCorrectKeyword(KeyWord.CHAR)
				|| isCorrectKeyword(KeyWord.INT) || isCorrectToken(TokenType.IDENTIFIER));
	}

	private void write(String line) throws IOException {
		for (int i = 0; i < tabCounter; i++) {
			writer.write("  ");
		}
		writer.write(line + "\n");
	}

	private boolean writeComma() throws Exception {
		writeSymbol(',');
		return tokenizer.symbol() == ',';
	}

	private void writeConstOrFuncOrMethod() throws IOException, Exception {
		if (isCorrectKeyword(KeyWord.FUNCTION)
				|| isCorrectKeyword(KeyWord.CONSTRUCTOR)
				|| (isCorrectKeyword(KeyWord.METHOD))) {
			writeKeyword();
		}
	};

	private boolean writeIdentifier() throws Exception {
		boolean flag;
		if (flag = isCorrectToken(TokenType.IDENTIFIER)) {
			write("<identifier>" + tokenizer.identifier() + "</identifier>");
		}
		return flag;
	}

	private boolean writeIntConst() throws Exception {
		boolean flag = isCorrectToken(TokenType.INT_CONST);
		if (flag) {
			write("<integerConstant>" + tokenizer.intVal()
					+ "</integerConstant>");
		}
		return flag;
	}

	private boolean writeKeyword() throws Exception {
		boolean flag;
		if (flag = isCorrectToken(TokenType.KEYWORD)) {
			write("<keyword>" + tokenizer.getKeyword() + "</keyword>");
		}
		return flag;
	}

	private boolean writeKeywordConst() throws Exception {
		KeyWord k = tokenizer.keyWord();
		boolean flag = k == KeyWord.THIS || k == KeyWord.TRUE
				|| k == KeyWord.FALSE || k == KeyWord.NULL;
		if (flag) {
			writeKeyword();
		}
		return flag;
	}

	private boolean writeOp() throws Exception {
		boolean flag = tokenizer.tokenType() == TokenType.SYMBOL;
		String s = "" + tokenizer.symbol();
		if ((flag = (flag && s.matches("^(\\+|-|\\*|/|&|\\||<|>|=)$")))){
			writeSymbol(s.charAt(0));
		}
		return flag;
	}

	private boolean writeStringConst() throws Exception {
		boolean flag = isCorrectToken(TokenType.STRING_CONST);
		if (flag) {
			write("<stringConstant>" + tokenizer.stringVal()
					+ "</stringConstant>");
		}
		return flag;
	}

	private boolean writeSymbol(char s) throws Exception {
		boolean flag = isCorrectSymbol(s);
		if (flag) {
			write("<symbol>" + tokenizer.symbol() + "</symbol>");
		}
		return flag;
	}

	private void writeType() throws Exception {
		if (isType()) {
			if (writeKeyword() || writeIdentifier()) {
			}
			;
		}
	}

	private void writeTypeAndIdentifier() throws Exception {
		writeType();
		tokenizer.advance();
		writeIdentifier();
	}

	private boolean writeUnaryOp() throws Exception {
		boolean flag = tokenizer.tokenType() == TokenType.SYMBOL;
		String s = "" + tokenizer.symbol();
		if ((flag = (flag && s.matches("^(-|~)$"))) && writeSymbol(s.charAt(0)))
			;
		return flag;
	}

	private boolean writeVar() throws Exception {
		boolean flag = tokenizer.keyWord() == KeyWord.VAR;
		if (flag) {
			writeKeyword();
		}
		return flag;
	}

	private void writeVoidOrType() throws IOException, Exception {
		if (isCorrectKeyword(KeyWord.VOID) || isCorrectKeyword(KeyWord.BOOLEAN)
				|| isCorrectKeyword(KeyWord.CHAR)
				|| isCorrectKeyword(KeyWord.INT)) {
			writeKeyword();
		}
	}
}

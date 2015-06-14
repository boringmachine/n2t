// TODO Fix this shit code

package Compiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class CompilationEngine {

	public static void main(String[] argv) throws Exception {
		CompilationEngine c = new CompilationEngine(argv[0], argv[1]);
		// CompilationEngine c = new
		// CompilationEngine("sample/10/ArrayTest/Main.jack","sample/10/ArrayTest/Main.xml");

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

	void compileClass() throws Exception {
		writeTag("class");
		int tmp = tabCounter++;

		writeKeyword(KeyWord.CLASS, "");
		writeClassName("before");
		warning(writeSymbol("{", "before"), "warn: {");
		while (compileClassVarDec("before"))
			;
		while (compileSubroutineDec())
			;
		warning(writeSymbol("}", ""), "warn: }");

		tabCounter = tmp;
		writeTag("/class");
	}

	boolean compileClassVarDec(String advancePoint) throws Exception {

		if (advancePoint.equals("before")) {
			tokenizer.advance();
		}

		boolean flag = false;
		if (tokenizer.keyWord() == KeyWord.STATIC || tokenizer.keyWord() == KeyWord.FIELD) {
			writeTag("classVarDec");
			int tmp = tabCounter++;
			warning(flag = writeKeyword(KeyWord.STATIC, "") || writeKeyword(KeyWord.FIELD, ""), "warn: static|field");

			warning(writeType("before"), "warn: type is wrong.");
			warning(writeVarName("before"), "warn: varName is wrong");

			while (writeSymbol(",", "before")) {
				writeVarName("before");
			}

			warning(writeSymbol(";", ""), "warn: missing ;");

			if (advancePoint.equals("after")) {
				tokenizer.advance();
			}

			tabCounter = tmp;
			writeTag("/classVarDec");

		}
		return flag;

	}

	boolean compileDo() throws Exception {
		boolean flag = tokenizer.keyWord() == KeyWord.DO;
		if (flag) {
			writeTag("doStatement");
			int tmp = tabCounter++;

			warning(writeKeyword(KeyWord.DO, "after"), "warn: missing do");
			warning(compileSubroutine(), "warn: wrong subroutine");
			warning(writeSymbol(";", "after"), "warn: missing ;");

			tabCounter = tmp;
			writeTag("/doStatement");
		}
		return flag;
	}

	boolean compileExpression(String advancePoint) throws Exception {

		writeTag("expression");
		int tmp = tabCounter++;

		if (advancePoint.equals("before")) {
			tokenizer.advance();
		}

		compileTerm();
		while (writeOp()) {
			compileTerm();
		}
		tabCounter = tmp;
		writeTag("/expression");

		return true;
	}

	boolean compileExpressionList(String advancePoint) throws Exception {
		writeTag("expressionList");
		int tmp = tabCounter++;

		if (advancePoint.matches("before")) {
			tokenizer.advance();
		}
		if (!tokenizer.symbol().equals(")")) {

			compileExpression("");

			while (writeSymbol(",", "after")) {
				compileExpression("");
			}
		}
		tabCounter = tmp;
		writeTag("/expressionList");

		return true;
	}

	boolean compileIf() throws Exception {
		boolean flag = tokenizer.keyWord() == KeyWord.IF;
		if (flag) {
			writeTag("ifStatement");
			int tmp = tabCounter++;

			warning(writeKeyword(KeyWord.IF, "after"), "warn: missing if");
			warning(writeSymbol("(", "after"), "warn: missing (");
			compileExpression("");
			warning(writeSymbol(")", "after"), "warn: missing )");
			warning(writeSymbol("{", "after"), "warn: missing {");
			compileStatements("");
			warning(writeSymbol("}", "after"), "warn: missing }");

			if (tokenizer.keyWord() == KeyWord.ELSE) {
				warning(writeKeyword(KeyWord.ELSE, "after"), "warn: missing else");
				warning(writeSymbol("{", "after"), "warn: missing {");
				compileStatements("");
				warning(writeSymbol("}", "after"), "warn: missing }");
			}
			tabCounter = tmp;
			writeTag("/ifStatement");
		}

		return flag;
	}

	boolean compileLet() throws Exception {
		boolean flag = tokenizer.keyWord() == KeyWord.LET;
		if (flag) {
			writeTag("letStatement");
			int tmp = tabCounter++;

			warning(writeKeyword(KeyWord.LET, ""), "warn: missing let");
			warning(writeVarName("before"), "warn: missing varname");

			tokenizer.advance();
			if (writeSymbol("[", "after")) {
				compileExpression("");
				warning(writeSymbol("]", "after"), "warn: missing ]");
			}
			warning(writeSymbol("=", "after"), "warn: missing =");
			compileExpression("");
			warning(writeSymbol(";", "after"), "warn: missing ;");

			tabCounter = tmp;
			writeTag("/letStatement");
		}
		return flag;
	}

	boolean compileParameterList() throws Exception {
		writeTag("parameterList");
		int tmp = tabCounter++;

		if (tokenizer.advance().equals(")")) {
			tabCounter = tmp;
			writeTag("/parameterList");
			return false;
		}

		warning(writeType(""), "warn: wrong type");
		warning(writeVarName("before"), "warn: wrong varName");

		while (writeSymbol(",", "before")) {
			warning(writeType("before"), "warn: wrong type");
			warning(writeVarName("before"), "warn: wrong varName");
		}

		tabCounter = tmp;
		writeTag("/parameterList");
		return true;
	}

	boolean compileReturn() throws Exception {
		boolean flag = tokenizer.keyWord() == KeyWord.RETURN;
		if (flag) {
			writeTag("returnStatement");
			int tmp = tabCounter++;

			warning(writeKeyword(KeyWord.RETURN, "after"), "warn: missing return");

			if (!writeSymbol(";", "")) {
				compileExpression("");
				warning(writeSymbol(";", ""), "warn: missing ;");
			}

			tokenizer.advance();
			tabCounter = tmp;
			writeTag("/returnStatement");
		}
		return flag;
	}

	boolean compileStatement() throws Exception {
		return compileLet() || compileIf() || compileWhile() || compileDo() || compileReturn();
	}

	boolean compileStatements(String advancePoint) throws Exception {
		writeTag("statements");
		int tmp = tabCounter++;
		if (advancePoint.equals("before")) {
			tokenizer.advance();
		}
		while (compileStatement())
			;
		tabCounter = tmp;
		writeTag("/statements");
		return true;
	}

	boolean compileSubroutine() throws Exception {
		if (writeIdentifier("after")) {
			if (writeSymbol("(", "")) {
				compileExpressionList("before");
				warning(writeSymbol(")", "after"), "warn: missing )");
				return true;
			} else if (writeSymbol(".", "after")) {
				warning(writeIdentifier("after"), "warn: missing varName");
				if (writeSymbol("(", "")) {
					compileExpressionList("before");
					warning(writeSymbol(")", "after"), "warn: missing )");
					return true;
				}
			}
		}
		return false;
	}

	boolean compileSubroutineBody() throws Exception {
		writeTag("subroutineBody");
		int tmp = tabCounter++;

		tokenizer.advance();
		warning(writeSymbol("{", "after"), "warn: missing {");
		while (compileVarDec())
			;
		compileStatements("");
		warning(writeSymbol("}", ""), "warn: missing }");

		tabCounter = tmp;
		writeTag("/subroutineBody");
		return true;
	}

	boolean compileSubroutineDec() throws Exception {

		boolean flag = tokenizer.keyWord() == KeyWord.CONSTRUCTOR || tokenizer.keyWord() == KeyWord.FUNCTION
				|| tokenizer.keyWord() == KeyWord.METHOD;
		if (flag) {
			writeTag("subroutineDec");
			int tmp = tabCounter++;
			warning(writeKeyword(KeyWord.CONSTRUCTOR, "") || writeKeyword(KeyWord.FUNCTION, "")
					|| writeKeyword(KeyWord.METHOD, ""), "warn: missing constructor|function|method");

			tokenizer.advance();
			warning(writeKeyword(KeyWord.VOID, "") || writeType(""), "warn: missing void|type");

			warning(writeSubroutineName("before"), "warn: wrong subroutineName");
			warning(writeSymbol("(", "before"), "warn: missing (");
			compileParameterList();
			warning(writeSymbol(")", ""), "warn: missing )");
			compileSubroutineBody();

			tabCounter = tmp;
			writeTag("/subroutineDec");
		}

		tokenizer.advance();

		return flag;
	}

	boolean compileTerm() throws Exception {

		writeTag("term");
		int tmp = tabCounter++;

		boolean idenFlag = false;
		boolean leftFlag = false;
		boolean unaryFlag = false;
		boolean flag = writeIntegerConstant() || writeStringConstant() || writeKeywordConstant()
				|| (idenFlag = writeIdentifier("after")) || (leftFlag = writeSymbol("(", "")) || (unaryFlag = writeUnaryOp());
		if (flag) {

			if (flag == true && (idenFlag || leftFlag || unaryFlag) == false) {
				tokenizer.advance();
			} else if (idenFlag) {
				if (writeSymbol("[", "")) {
					compileExpression("before");
					warning(writeSymbol("]", "after"), "warn: missing ]");
				} else if (writeSymbol("(", "")) {
					compileExpressionList("before");
					warning(writeSymbol(")", "after"), "warn: missing )");
				} else if (writeSymbol(".", "after")) {
					warning(writeIdentifier("after"), "warn: missing varName");
					if (writeSymbol("(", "")) {
						compileExpressionList("before");
						warning(writeSymbol(")", "after"), "warn: missing )");
					}
				}
			} else if (leftFlag) {
				compileExpression("before");
				warning(writeSymbol(")", "after"), "warn: missing )");
			} else if (unaryFlag) {
				tokenizer.advance();
				compileTerm();
			}

		}
		tabCounter = tmp;
		writeTag("/term");
		return true;
	}

	boolean compileVarDec() throws Exception {

		boolean flag = tokenizer.keyWord() == KeyWord.VAR;
		if (flag) {
			writeTag("varDec");
			int tmp = tabCounter++;

			warning(writeKeyword(KeyWord.VAR, ""), "warn: missing var");
			warning(writeType("before"), "warn: missing type");
			warning(writeVarName("before"), "warn: missing varName");
			while (writeSymbol(",", "before")) {
				writeVarName("before");
			}

			warning(writeSymbol(";", "after"), "warn: missing ;");

			tabCounter = tmp;
			writeTag("/varDec");
		}

		return flag;
	}

	boolean compileWhile() throws Exception {
		boolean flag = tokenizer.keyWord() == KeyWord.WHILE;
		if (flag) {
			writeTag("whileStatement");
			int tmp = tabCounter++;

			warning(writeKeyword(KeyWord.WHILE, "after"), "warn: missing while");
			warning(writeSymbol("(", "after"), "warn: missing (");
			compileExpression("");
			warning(writeSymbol(")", "after"), "warn: missing )");
			warning(writeSymbol("{", "after"), "warn: missing {");
			compileStatements("");
			warning(writeSymbol("}", "after"), "warn: missing }");

			tabCounter = tmp;
			writeTag("/whileStatement");

		}
		return flag;
	}

	void warning(boolean sentence, String e) throws Exception {
		if (!sentence) {
			writer.close();
			throw new Exception(e);
		}
	}

	void write(String s) throws Exception {
		for (int i = 0; i < tabCounter; i++) {
			writer.write("  ");
		}
		writer.write(s + "\n");
	}

	boolean writeClassName(String advancePoint) throws Exception {
		return writeIdentifier(advancePoint);
	}

	boolean writeIdentifier(String advancePoint) throws Exception {
		if (advancePoint.equals("before")) {
			tokenizer.advance();
		}
		boolean flag = tokenizer.tokenType() == TokenType.IDENTIFIER;
		if (flag) {
			write("<identifier>" + tokenizer.identifier() + "</identifier>");
			if (advancePoint.equals("after")) {
				tokenizer.advance();
			}
		}

		return flag;
	}

	boolean writeIntegerConstant() throws Exception {
		boolean flag = tokenizer.tokenType() == TokenType.INT_CONST;
		if (flag) {
			write("<integerConstant>" + tokenizer.intVal() + "</integerConstant>");
		}
		return flag;
	}

	boolean writeKeyword(KeyWord k, String advancePoint) throws Exception {
		if (advancePoint.equals("before")) {
			tokenizer.advance();
		}
		boolean flag = tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() == k;
		if (flag) {
			String s = "";
			switch (tokenizer.keyWord()) {
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
			case LET:
				s = "let";
				break;
			case IF:
				s = "if";
				break;
			case ELSE:
				s = "else";
				break;
			case WHILE:
				s = "while";
				break;
			case DO:
				s = "do";
				break;
			case RETURN:
				s = "return";
				break;
			case THIS:
				s = "this";
				break;
			case TRUE:
				s = "true";
				break;
			case NULL:
				s = "null";
				break;
			case FALSE:
				s = "false";
				break;
			}
			write("<keyword>" + s + "</keyword>");
			if (advancePoint.equals("after")) {
				tokenizer.advance();
			}
		}

		return flag;
	}

	boolean writeKeywordConstant() throws Exception {
		boolean flag = tokenizer.keyWord() == KeyWord.TRUE || tokenizer.keyWord() == KeyWord.FALSE
				|| tokenizer.keyWord() == KeyWord.NULL || tokenizer.keyWord() == KeyWord.THIS;
		if (flag) {
			writeKeyword(tokenizer.keyWord(), "");
		}
		return flag;
	}

	boolean writeOp() throws Exception {
		boolean flag = tokenizer.symbol().matches("^(\\+|-|\\*|/|&amp;|\\||&lt;|&gt;|=)$");
		if (flag) {
			warning(writeSymbol(tokenizer.symbol(), ""), "warn: missing op");
			tokenizer.advance();
		}
		return flag;
	}

	boolean writeStringConstant() throws Exception {
		boolean flag = tokenizer.tokenType() == TokenType.STRING_CONST;
		if (flag) {
			write("<stringConstant>" + tokenizer.stringVal() + "</stringConstant>");
		}
		return flag;
	}

	boolean writeSubroutineName(String advancePoint) throws Exception {
		return writeIdentifier(advancePoint);
	}

	boolean writeSymbol(String s, String advancePoint) throws Exception {
		if (advancePoint.equals("before")) {
			tokenizer.advance();
		}
		boolean flag = tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol().equals(s);
		if (flag) {
			write("<symbol>" + s + "</symbol>");
			if (advancePoint.equals("after")) {
				tokenizer.advance();
			}
		}

		return flag;
	}

	void writeTag(String s) throws Exception {
		write("<" + s + ">");
	}

	boolean writeType(String advancePosition) throws Exception {
		if (advancePosition.equals("before")) {
			tokenizer.advance();
		}
		boolean flag = (tokenizer.tokenType() == TokenType.KEYWORD && (tokenizer.keyWord() == KeyWord.BOOLEAN
				|| tokenizer.keyWord() == KeyWord.INT || tokenizer.keyWord() == KeyWord.CHAR))
				|| (tokenizer.tokenType() == TokenType.IDENTIFIER);
		if (flag) {
			writeKeyword(tokenizer.keyWord(), "");
			writeIdentifier("");
		}
		return flag;
	}

	boolean writeUnaryOp() throws Exception {
		boolean flag = tokenizer.symbol().matches("^(-|~)$");
		if (flag) {
			writeSymbol(tokenizer.symbol(), "");
		}
		return flag;
	}

	boolean writeVarName(String advancePoint) throws Exception {
		return writeIdentifier(advancePoint);
	}
}
package Compiler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JackTokenizer {

	// check token
	public static void main(String[] argv) throws IOException {
		JackTokenizer a = new JackTokenizer("sample/10/ArrayTest/Main.jack");
		System.out.println(a.data);
		while (a.hasMoreTokens()) {
			System.out.println("TOKEN      :" + a.advance());
			System.out.println("SYMBOL     :" + a.symbol);
			System.out.println("IDENTIFIER :" + a.identifier);
			System.out.println("KEYWORD    :" + a.keyword);
			System.out.println("INTCONST   :" + a.integerConstant);
			System.out.println("STRINGCONST:" + a.stringConstant);
			System.out.println();
		}
	}

	private String data;
	private File file;
	private String identifier;
	private FileInputStream in;
	private String integerConstant;
	private String keyword;
	private InputStreamReader reader;
	private Scanner scan;
	private String stringConstant;

	private String symbol;

	JackTokenizer(String filename) throws IOException {
		this.file = new File(filename);
		in = new FileInputStream(file);
		reader = new InputStreamReader(in);
		char[] data = new char[(int) file.length()];
		reader.read(data);
		this.data = (new String(data)).replaceAll("(?://.*)|(/\\*(?:.|[\\n\\r])*?\\*/)", "").replaceAll("\n|\r|\n\r|\r\n", "")
				.replaceAll("(\\{|\\}|\\(|\\)|;|\\.|\\,|\\+|-|\\*|/|&|\\||<|>|=|~|\\[|\\])", " $1 ").replaceAll(" +", " ")
				.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		reader = new InputStreamReader(new ByteArrayInputStream(this.data.getBytes("UTF-8")));
		// System.out.println(this.data);
		scan = new Scanner(this.data);
	}

	String advance() throws IOException {
		String str = "";
		try {
			str = scan.next();
			symbol = "";
			keyword = "";
			identifier = "";
			integerConstant = "";
			stringConstant = "";
			if (str.matches("^(\\{|\\}|\\(|\\)|;|\\.|\\,|\\+|-|\\*|/|&amp;|\\||&gt;|&lt;|=|~|\\[|\\])$")) {
				symbol = str;
			} else if (str
					.matches("^(class|constructor|function|method|field|static|void|int|char|boolean|var|true|false|null|this|let|do|if|else|while|return)$")) {
				keyword = str;
			} else if (str.matches("\\p{Alpha}(\\p{Alnum}|_)*")) {
				identifier = str;
			} else if (str.matches("[0-9]+")) {
				integerConstant = str;
			} else if (str.matches("^\".*\"?$")) {
				while (!str.endsWith("\"")) {
					str = str + scan.next();
				}
				str = str.replaceAll("\"", "");
				stringConstant = str;
			}
		} catch (NoSuchElementException e) {
			reader.close();
			in.close();
		}
		return str;
	}

	String getKeyword() {
		return keyword;
	}

	boolean hasMoreTokens() {
		return scan.hasNext();
	}

	String identifier() {
		return identifier;
	}

	String intVal() {
		return integerConstant;
	}

	KeyWord keyWord() {
		if (keyword.equals("class")) {
			return KeyWord.CLASS;
		} else if (keyword.equals("method")) {
			return KeyWord.METHOD;
		} else if (keyword.equals("function")) {
			return KeyWord.FUNCTION;
		} else if (keyword.equals("constructor")) {
			return KeyWord.CONSTRUCTOR;
		} else if (keyword.equals("field")) {
			return KeyWord.FIELD;
		} else if (keyword.equals("static")) {
			return KeyWord.STATIC;
		} else if (keyword.equals("var")) {
			return KeyWord.VAR;
		} else if (keyword.equals("int")) {
			return KeyWord.INT;
		} else if (keyword.equals("char")) {
			return KeyWord.CHAR;
		} else if (keyword.equals("boolean")) {
			return KeyWord.BOOLEAN;
		} else if (keyword.equals("void")) {
			return KeyWord.VOID;
		} else if (keyword.equals("true")) {
			return KeyWord.TRUE;
		} else if (keyword.equals("false")) {
			return KeyWord.FALSE;
		} else if (keyword.equals("null")) {
			return KeyWord.NULL;
		} else if (keyword.equals("this")) {
			return KeyWord.THIS;
		} else if (keyword.equals("let")) {
			return KeyWord.LET;
		} else if (keyword.equals("do")) {
			return KeyWord.DO;
		} else if (keyword.equals("if")) {
			return KeyWord.IF;
		} else if (keyword.equals("else")) {
			return KeyWord.ELSE;
		} else if (keyword.equals("while")) {
			return KeyWord.WHILE;
		} else if (keyword.equals("return")) {
			return KeyWord.RETURN;
		}
		return null;
	}

	String stringVal() {
		return stringConstant;
	}

	String symbol() {
		return this.symbol;
	}

	TokenType tokenType() throws Exception {
		if (!symbol.isEmpty()) {
			return TokenType.SYMBOL;
		} else if (!keyword.isEmpty()) {
			return TokenType.KEYWORD;
		} else if (!identifier.isEmpty()) {
			return TokenType.IDENTIFIER;
		} else if (!integerConstant.isEmpty()) {
			return TokenType.INT_CONST;
		} else if (!stringConstant.isEmpty()) {
			return TokenType.STRING_CONST;
		}
		return null;
	}

}
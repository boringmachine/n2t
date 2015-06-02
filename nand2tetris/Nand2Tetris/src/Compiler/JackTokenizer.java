package Compiler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;
import java.util.Scanner;

import VMtranslator.Parser;

public class JackTokenizer {

	private File file;
	private FileInputStream in;
	private InputStreamReader reader;
	private Scanner scan;
	private String data;
	private String symbol;
	private String identifier;
	private String keyword;
	private String integerConstant;
	private String stringConstant;
	
	JackTokenizer(String filename) throws IOException{
		this.file = new File(filename);
		in = new FileInputStream(file);
		reader = new InputStreamReader(in);
		char[] data = new char[(int) file.length()];
		reader.read(data);
		this.data = (new String(data))
				.replaceAll("(?://.*)|(/\\*(?:.|[\\n\\r])*?\\*/)", "")
				.replaceAll("\n", "")
				.replaceAll(
						"(\\{|\\}|\\(|\\)|;|\\.|\\,|\\+|-|\\*|/|&|\\||<|>|=|~)",
						" $1 ");
		reader = new InputStreamReader(new ByteArrayInputStream(
				this.data.getBytes("UTF-8")));
		scan = new Scanner(this.data);
	}
	
	boolean hasMoreTokens(){
		return scan.hasNext();
	}
	
	String advance() {
		String str = scan.next();
		symbol = "";
		keyword = "";
		identifier = "";
		integerConstant = "";
		if(str.matches("\\{|\\}|\\(|\\)|;|\\.|\\,|\\+|-|\\*|/|&|\\||<|>|=|~")){
			symbol = str;
		} else if(str.matches("class|constructor|function|method|field|static|var|true|false|null|this|let|do|if|else|while|return")){
			keyword = str;
		} else if(str.matches("\\p{Alpha}(\\p{Alnum}|_)*")){
			identifier = str;
		} else if(str.matches("[0-9]+")){
			integerConstant = str;
		} else if(str.matches("\".*\"?")){
			str = str.replaceAll("\"", "");
			stringConstant = str;
		}
		return str;
	}
	
	TokenType tokenType(){
		return TokenType.KEYWORD;
	}
	
	KeyWord keyWord(){
		return KeyWord.BOOLEAN;
	}
	
	String symbol(){
		return symbol;
	}
	
	String identifier(){
		return identifier;
	}
	
	String intVal(){
		return integerConstant;
	}
	
	String stringVal(){
		return stringConstant;
	}
	
	//check token
	public static void main(String[] argv) throws IOException{
		JackTokenizer a = new JackTokenizer("Square/Main.jack");
		System.out.println(a.data);
		while (a.hasMoreTokens()) {
			System.out.println("TOKEN   :" + a.advance());
		}
	}
}

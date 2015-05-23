package VMtranslator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.NoSuchElementException;

public class CodeWriter {

	private File file;
	private FileOutputStream out;
	private OutputStreamWriter writer;
	private StringBuilder data;
	private String outfile;
	private String thisFile;
	private int labelCounter;

	CodeWriter(String thisFile, String outfile) throws IOException {
		this.thisFile = thisFile;
		this.outfile = outfile;
		file = new File(outfile);
		out = new FileOutputStream(file);
		writer = new OutputStreamWriter(out);
		data = new StringBuilder();
		int labelCounter = 0;
	}

	void setFileName(String fileName) throws IOException {
		this.outfile = fileName;
		file = new File(outfile);
		out = new FileOutputStream(file);
		writer = new OutputStreamWriter(out);
		data = new StringBuilder();
	}

	private void pop(boolean flag) throws IOException{
		if(flag){
			writer.write("@SP\n" + "AM=M-1\n" + "D=M\n");	
		} else{
			writer.write("@SP\n" + "AM=M-1\n" + "A=M\n");	
		}
	}
	
	private void push() throws IOException{
		writer.write("@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n");
	}
	
	void writeArithmetic(String command) throws Exception {
		pop(true);
		if (command.equals("add")) {
			pop(false);
			writer.write("D=A+D\n");
		} else if (command.equals("sub")) {
			pop(false);
			writer.write("D=A-D\n");
		} else if (command.equals("neg")) {
			writer.write("D=!D\n" + "D=D+1\n");
		} else if (command.equals("eq") 
				|| command.equals("gt") 
				|| command.equals("lt")) {
			pop(false);
			writer.write("D=A-D\n");
			writer.write("@TRUE" + labelCounter + "\n");
			if(command.equals("eq")){
				writer.write("D;JEQ\n");
			} else if(command.equals("gt")){
				writer.write("D;JGT\n");
			} else {
				writer.write("D;JLT\n");
			}
			writer.write("D=0\n");
			writer.write("@FALSE" + labelCounter + "\n");
			writer.write("0;JMP\n");
			writer.write("(" + "TRUE" + labelCounter + ")\n");
			writer.write("D=-1\n");
			writer.write("(" + "FALSE" + (labelCounter++) + ")\n");
		} else if (command.equals("and")) {
			pop(false);
			writer.write("D=A&D\n");
		} else if (command.equals("or")) {
			pop(false);
			writer.write("D=A|D\n");
		} else if (command.equals("not")) {
			writer.write("D=!D\n");
		} else {
			throw new Exception();
		}
		push();
	}

	void writePushPop(String command, String segment, int index)
			throws IOException {
		String reg = "";
		int add = 3;
		if (segment.equals("temp"))
			add = 5;
		if (segment.equals("local"))
			reg = "LCL";
		else if (segment.equals("argument"))
			reg = "ARG";
		else if (segment.equals("this"))
			reg = "THIS";
		else if (segment.equals("that"))
			reg = "THAT";
		if (command.equals("push")) {
			if (segment.equals("constant")) {
				writer.write("@" + index + "\n"	+ "D=A\n");
				push();
			}  else if (segment.equals("static")) {
				writer.write("@"+thisFile+"."+index+"\nD=M\n");
				push();
			}  else if (!reg.isEmpty()) {
				writer.write("@" + index + "\n" + "D=A\n");
				writer.write("@" + reg + "\nA=M+D\nD=M\n");
				push();
			} else {
				if (segment.equals("temp")) {
					writer.write("@R" + (index + add) + "\nD=M\n");
					push();
				} else {
					writer.write("@" + (index + add) + "\nD=M\n");
					push();
				}
			}
		} else if (command.equals("pop")) {
			if (segment.equals("static")) {
				pop(true);
				writer.write("@" + thisFile + "." + index + "\nM=D\n");
			} else if (!segment.equals("pointer") && !segment.equals("temp")) {
				writer.write("@" + index + "\nD=A\n");
				writer.write("@" + reg + "\nD=M+D\n@R13\nM=D\n");
				pop(true);
				writer.write("@R13\nA=M\nM=D\n");
			} else {
				pop(true);
				writer.write("@" + (index + add) + "\nM=D\n");
			}

		}
	}
	
	void writeInit(){
		
	}
	
	void writeLabel(String label) throws IOException{
		writer.write("(" + label + ")\n" );
	}
	
	void writeGoto(String label) throws IOException{
		writer.write("@"+label+"\n");
		writer.write("0;JMP\n");
	}

	void writeIf(String label) throws IOException{
		pop(true);
		writer.write("@"+label+"\n");
		writer.write("D;JNE\n");
	}
	void close() throws IOException {
		writer.close();
		out.close();
	}
}

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
	private int returnCounter;

	CodeWriter(String thisFile, String outfile) throws IOException {
		this.thisFile = thisFile;
		this.outfile = outfile;
		file = new File(outfile);
		out = new FileOutputStream(file);
		writer = new OutputStreamWriter(out);
		data = new StringBuilder();
		int labelCounter = 0;
		int returnCounter = 0;
	}

	void setFileName(String fileName) throws IOException {
		this.thisFile = fileName;
	}

	private void pop(boolean flag) throws IOException{
		if(flag){
			write("@SP");
			write("AM=M-1");
			write("D=M");
		} else{
			write("@SP");
			write("AM=M-1");
			write("A=M");
		}
	}
	
	private void push() throws IOException{
		write("@SP");
		write("AM=M+1");
		write("A=A-1");
		write("M=D");
	}
	
	private void writeInfLoop() throws IOException{
		write("(INF_LOOP)");
		write("@INF_LOOP");
		write("0;JMP");
	}
	
	void writeArithmetic(String command) throws Exception {
		pop(true);
		if (command.equals("add")) {
			pop(false);
			write("D=A+D");
		} else if (command.equals("sub")) {
			pop(false);
			write("D=A-D");
		} else if (command.equals("neg")) {
			write("D=!D");
			write("D=D+1");
		} else if (command.equals("eq") 
				|| command.equals("gt") 
				|| command.equals("lt")) {
			pop(false);
			write("D=A-D");
			write("@TRUE" + labelCounter);
			if(command.equals("eq")){
				write("D;JEQ");
			} else if(command.equals("gt")){
				write("D;JGT");
			} else {
				write("D;JLT");
			}
			write("D=0");
			write("@FALSE" + labelCounter);
			write("0;JMP");
			write("(" + "TRUE" + labelCounter + ")");
			write("D=-1");
			write("(" + "FALSE" + (labelCounter++) + ")");
		} else if (command.equals("and")) {
			pop(false);
			write("D=A&D");
		} else if (command.equals("or")) {
			pop(false);
			write("D=A|D");
		} else if (command.equals("not")) {
			write("D=!D");
		} else {
			throw new Exception();
		}
		push();
	}

	void writePushPop(String command, String segment, int index)
			throws IOException {
		String reg = "";
		thisFile = thisFile.replaceAll(".*/", "");
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
				write("@" + index);
				write("D=A\n");
				push();
			}  else if (segment.equals("static")) {
				write("@"+thisFile+"."+index);
				write("D=M");
				push();
			}  else if (!reg.isEmpty()) {
				write("@" + index);
				write("D=A");
				write("@" + reg);
				write("A=M+D");
				write("D=M");
				push();
			} else {
				if (segment.equals("temp")) {
					write("@R" + (index + add));
					write("D=M");
					push();
				} else {
					write("@" + (index + add));
					write("D=M");
					push();
				}
			}
		} else if (command.equals("pop")) {
			if (segment.equals("static")) {
				write("@" + thisFile + "." + index);
				write("D=A");
				write("@R15");
				write("M=D");
				pop(true);
				write("@R15");
				write("A=M");
				write("M=D");

			} else if (!segment.equals("pointer") && !segment.equals("temp")) {
				write("@" + index);
				write("D=A");
				write("@" + reg);
				write("D=M+D");
				write("@R13");
				write("M=D");
				pop(true);
				write("@R13");
				write("A=M");
				write("M=D");
			} else {
				pop(true);
				write("@" + (index + add));
				write("M=D");
			}

		}
	}
	
	void writeInit() throws IOException{
		write("@256");
		write("D=A");
		write("@SP");
		write("M=D");
		writeCall("Sys.init", 0);
		writeInfLoop();
	}
	
	void writeLabel(String label) throws IOException{
		write("("+label+")");
	}
	
	void writeGoto(String label) throws IOException{
		write("@"+label);
		write("0;JMP");
	}

	void writeIf(String label) throws IOException{
		pop(true);
		write("@"+label);
		write("D;JNE");
	}
	
	void writeCall(String functionName, int numArgs) throws IOException{
		write("@return-address"+returnCounter);
		write("D=A");
		push();
		pushReg();
		write("@"+(5+numArgs));
		write("D=A");
		write("@SP");
		write("D=M-D");
		write("@ARG");
		write("M=D");
		write("@SP");
		write("D=M");
		write("@LCL");
		write("M=D");
		writeGoto(functionName);
		writeLabel("return-address"+returnCounter);
		returnCounter++;
	}
	
	void writeReturn() throws IOException{
		write("@LCL");
		write("D=M");
		write("@R14");
		write("M=D");
		write("@5");
		write("D=A");
		write("@R14");
		write("A=M-D");
		write("D=M");
		write("@R13");
		write("M=D");
		pop(true);
		write("@ARG");
		write("A=M");
		write("M=D");
		write("@ARG");
		write("D=M+1");
		write("@SP");
		write("M=D");
		popR14("THAT");
		popR14("THIS");
		popR14("ARG");
		popR14("LCL");
		write("@R13");
		write("A=M");
		write("0;JMP");
	}
	
	void pushReg() throws IOException{
		write("@LCL");
		write("D=M");
		push();
		write("@ARG");
		write("D=M");
		push();
		write("@THIS");
		write("D=M");
		push();
		write("@THAT");
		write("D=M");
		push();
	}
	
	void popR14(String name) throws IOException{
		write("@R14");
		write("AM=M-1");
		write("D=M");
		write("@"+name);
		write("M=D");
	}
	
	void writeFunction(String functionName, int numLocals) throws IOException{
		writeLabel(functionName);
		for(int i=0; i< numLocals; i++){
			write("@SP");
			write("AM=M+1");
			write("A=A-1");
			write("M=0");
		}
	}
	
	private void write(String line) throws IOException{
			writer.write(line+"\n");
	}
	
	void close() throws IOException {
		writer.close();
		out.close();
	}
}

package VMtranslator;

import java.io.IOException;

public class VMtranslator {

	//debug
	public static void main(String args[]) throws Exception{
		Parser parser = new Parser("VM/StackTest.vm");
		CodeWriter writer = new CodeWriter("Asm/StackTest.asm");
		while(parser.hasMoreCommands()){
		    parser.advance();
		    if(parser.commandType() == Parser.C_ARITHMETIC){
		    	writer.writeArithmetic(parser.type);
		    	System.out.println(parser.type);
		    } else{
		    	writer.writePushPop(parser.type, parser.arg1(), Integer.parseInt(parser.arg2()));
		    	System.out.println(parser.type);
		    }
		    
		}
		writer.close();
	}	
	
}

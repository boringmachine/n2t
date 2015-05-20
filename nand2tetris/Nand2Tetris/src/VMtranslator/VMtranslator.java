package VMtranslator;

import java.io.IOException;

public class VMtranslator {

	//debug
	public static void main(String args[]) throws Exception{
		Parser parser1 = new Parser("VM/SimpleAdd.vm");
//		Parser parser2 = new Parser("VM/SimpleAdd.vm");

		CodeWriter writer = new CodeWriter("Asm/SimpleAdd.asm");
		while(parser1.hasMoreCommands()){
		    parser1.advance();
		    if(parser1.commandType() == Parser.C_ARITHMETIC){
		    	writer.writeArithmetic(parser1.type);
		    	System.out.println(parser1.type);
		    } else{
		    	writer.writePushPop(parser1.type, parser1.arg1(), Integer.parseInt(parser1.arg2()));
		    	System.out.println(parser1.type);
		    }
		    
		}
		writer.close();
	}	
	
}

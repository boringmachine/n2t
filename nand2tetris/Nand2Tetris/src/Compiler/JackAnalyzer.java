package Compiler;

import java.io.File;

public class JackAnalyzer {
	public static String[] listFiles(final File folder) {
		StringBuilder files = new StringBuilder();
		for (final File fileEntry : folder.listFiles()) {
			files.append(fileEntry.getName() + ":");
		}
		return files.toString().split(":");
	}

	public static void main(String[] argv) throws Exception {
		File file = new File(argv[0]);
		String infile = argv[0];
		if (file.isFile()) {
			String outfile = argv[0].replaceAll("^(.*)\\.jack$", "$1.xml");
			new CompilationEngine(infile, outfile);
		} else if (file.isDirectory()) {
			String[] files = listFiles(file);
			for (String inFile : files) {
				if (inFile.endsWith("jack")) {
					inFile = file.toString() + "/" + inFile;
					String outfile = inFile.replaceAll("^(.*)\\.jack$", "$1.xml");
					new CompilationEngine(inFile, outfile);
				}
			}
		}
	}

}

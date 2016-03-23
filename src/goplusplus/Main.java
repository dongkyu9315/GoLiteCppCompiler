package goplusplus;

import java.io.*;

import goplusplus.node.Start;
import goplusplus.parser.Parser;
//import goplusplus.interpret.Interpreter;

public class Main {
	public static void main(String[] args) {
		if (args.length > 0) {
			try {
				
				/* ------------------ */
				/* Scanner and Parser */
				/* ------------------ */
				/* Form our AST */
				GoLexer lexer = new GoLexer (new PushbackReader(
						new FileReader(args[0]), 1024));
				Parser parser = new Parser(lexer);
				Start ast = parser.parse();
				
				Position p = new Position();
				ast.apply(p);
				Weeder wd = new Weeder(p);
				ast.apply(wd);

//				/* Get our Interpreter going. */
//				Interpreter interp = new Interpreter();
//				ast.apply(interp);
//				System.out.println("VALID\n");
				
				/* ---------------- */
				/* filename extract */
				/* ---------------- */
				String filenameNoExt = args[0].replaceFirst("[.][^.]+$", "");
				
				/* -------------- */
				/* Pretty Printer */
				/* -------------- */
				/* Pretty Print the AST */
				String ppFile = filenameNoExt + ".pretty.go";
				PrettyPrinter prettyPrinter = new PrettyPrinter(ppFile, p);
				prettyPrinter.print(ast);
				
				/* ------------ */
				/* Type Checker */
				/* ------------ */
//				System.out.println("Type Checker ...");
				Typechecker typechecker = new Typechecker(p, true);
				typechecker.check(ast);
				typechecker.printSymbolTable();
//				System.out.println("VALID\n");
				
				/* ---------------- */
				/* C Code Generator */
				/* ---------------- */
//				System.out.println("C++ Code Generator ...");
//				String pathCGen = filename + ".cpp";
//				File fileCGen = new File(pathCGen);
//				fileCGen.createNewFile();
//				FileWriter writerCGen = new FileWriter(fileCGen, false);
//				CCodeGenerator.print(ast2, writerCGen, symbolTable);
//				writerCGen.flush();
//				writerCGen.close();
//				System.out.println("DONE\n");
				
			} catch (Exception e) {
				System.out.println("INVALID\n");
				System.out.println(e);
			}
		} else {
			System.err.println("No input file");
			System.exit(1);
		}
	}
}

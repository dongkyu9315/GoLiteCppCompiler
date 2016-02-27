package goplusplus;

import java.io.*;
import java.util.HashMap;

import goplusplus.lexer.Lexer;
import goplusplus.node.Start;
import goplusplus.parser.Parser;
import goplusplus.interpret.Interpreter;

public class Main {
	public static void main(String[] args) {
		if (args.length > 0) {
			try {
				/* ----------------- */
				/* Scanner and Parser */
				/* ----------------- */
				System.out.println("Scanner ...");
				System.out.println("Parser ...");
				/* Form our AST */
				GoLexer lexer = new GoLexer (new PushbackReader(
						new FileReader(args[0]), 1024));
				Parser parser = new Parser(lexer);
				Start ast = parser.parse();

//				/* Get our Interpreter going. */
//				Interpreter interp = new Interpreter();
//				ast.apply(interp);
				System.out.println("VALID\n");
				
				/* ---------------- */
				/* filename extract */
				/* ---------------- */
				String original = args[0];
				String[] splitString = original.split("\\.");
				String filename = splitString[0];
				
				/* -------------- */
				/* Pretty Printer */
				/* -------------- */
				System.out.println("Pretty Printer ...");
				/* Pretty Print the AST */
				String pathAST = filename + ".pretty.go";
				PrettyPrinter.print(ast, pathAST);
				System.out.println("DONE\n");
				
				/* ------------ */
				/* Type Checker */
				/* ------------ */
//				System.out.println("Type Checker ...");
//				String pathSymbol = filename + ".symbol";
//				File fileSymbol = new File(pathSymbol);
//				fileSymbol.createNewFile();
//				FileWriter writerSymbol = new FileWriter(fileSymbol, false);
//				HashMap<String, String> symbolTable = new HashMap<String, String>();
//				symbolTable = SymbolTypeChecker.print(ast, writerSymbol);
//				writerSymbol.flush();
//				writerSymbol.close();
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
			System.err.println("usage: java goplusplus inputFile");
			System.exit(1);
		}
	}
}

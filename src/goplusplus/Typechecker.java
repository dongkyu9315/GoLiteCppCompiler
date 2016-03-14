package goplusplus;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Iterator;

import goplusplus.analysis.DepthFirstAdapter;
import goplusplus.node.*;

public class Typechecker extends DepthFirstAdapter{
	public Typechecker() {
		symbolTable = new LinkedList<HashMap<String, String> >();
	}
	
	public static void check(Node node) {
		node.apply(new Typechecker());
		printSymbolTable();
	}
	
	private void print(String s) {
		try {
			mFileWriter.append(s+" ");
			mFileWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// print the symbol table to the console
	public static void printSymbolTable() {
		for (int i = 0; i < symbolTable.size(); i++) {
			HashMap<String, String> temp = symbolTable.get(i);
			System.out.println("Layer " + i + ":");
			for (HashMap.Entry<String, String> entry : temp.entrySet()) {
				System.out.println("Key = " + entry.getKey() + ", Type = " + entry.getValue());
			}
		}
	}
	
	private static LinkedList<HashMap<String, String> > symbolTable;
	FileWriter mFileWriter;
	
	// ast_program		---------------------------------------------------
	@Override
	public void caseAAstProgram(AAstProgram node) {
		symbolTable.add(new HashMap<String, String>());
		LinkedList decl = node.getDecl();
		if (!decl.isEmpty()) {
			for (Iterator iterator = decl.iterator(); iterator.hasNext();) {
				PAstDecl d = (PAstDecl) iterator.next();
				d.apply(this);
			}
		}
	}
	
	// ast_decl			---------------------------------------------------
	@Override
	public void caseAVarDecAstDecl(AVarDecAstDecl node) {
		LinkedList decl = node.getAstVarDecl();
		if (!decl.isEmpty()) {
			for (Iterator iterator = decl.iterator(); iterator.hasNext();) {
				PAstVarDecl d = (PAstVarDecl) iterator.next();
				d.apply(this);
			}
		}
	}
	
	@Override
	public void caseATypeDecAstDecl(ATypeDecAstDecl node) {
		LinkedList decl = node.getAstTypeDecl();
		if (!decl.isEmpty()) {
			for (Iterator iterator = decl.iterator(); iterator.hasNext();) {
				PAstTypeDecl d = (PAstTypeDecl) iterator.next();
				d.apply(this);
			}
		}
	}
	
	@Override
	public void caseAFuncDecAstDecl(AFuncDecAstDecl node) {
		node.getAstFuncDecl().apply(this);
	}
	
	// ast_var_decl		---------------------------------------------------
	@Override
	public void caseATypeAstVarDecl(ATypeAstVarDecl node) {
		LinkedList<TId> idlist = node.getId();
		String varType = forPAstTypeExp(node.getAstTypeExp());
		for (Iterator iterator = idlist.iterator(); iterator.hasNext();) {
			TId d = (TId) iterator.next();
			if (!symbolTable.getFirst().containsKey(d.getText().trim())) {
				symbolTable.getFirst().put(d.getText().trim(), varType);
			} else {
				printSymbolTable();
				String errorMsg = "Declaration Error at line " + d.getLine();
				throw new TypeException(errorMsg);
			}
		}
	}
	
	@Override
	public void caseAExpAstVarDecl(AExpAstVarDecl node) {
		LinkedList<TId> idlist = node.getId();
		LinkedList<PAstExp> exps = node.getAstExp();
		if (idlist.size() != exps.size()) {
			printSymbolTable();
			String errorMsg = "Declaration Error at line " + idlist.getFirst().getLine();
			throw new TypeException(errorMsg);
		}
		
		for (int i = 0; i < idlist.size(); i++) {
			TId d = idlist.get(i);
			String varType = forPAstExp(exps.get(i));
			if (!symbolTable.getFirst().containsKey(d.getText().trim())) {
				symbolTable.getFirst().put(d.getText().trim(), varType);
			} else {
				printSymbolTable();
				String errorMsg = "Declaration Error at line " + d.getLine();
				throw new TypeException(errorMsg);
			}
		}
	}
	
	@Override
	public void caseATypeExpAstVarDecl(ATypeExpAstVarDecl node) {
		LinkedList<TId> idlist = node.getId();
		String typeExp = forPAstTypeExp(node.getAstTypeExp());
		LinkedList<PAstExp> exps = node.getAstExp();
		
		if (idlist.size() != exps.size()) {
			printSymbolTable();
			String errorMsg = "Declaration Error at line " + idlist.getFirst().getLine();
			throw new TypeException(errorMsg);
		}
		
		for (int i = 0; i < idlist.size(); i++) {
			TId d = idlist.get(i);
			String varType = forPAstExp(exps.get(i));
			if (!varType.equals(typeExp)) {
				printSymbolTable();
				String errorMsg = "Declaration Error at line " + d.getLine();
				throw new TypeException(errorMsg);
			}
			if (!symbolTable.getFirst().containsKey(d.getText().trim())) {
				symbolTable.getFirst().put(d.getText().trim(), varType);
			} else {
				printSymbolTable();
				String errorMsg = "Declaration Error at line " + d.getLine();
				throw new TypeException(errorMsg);
			}
		}
	}
	
	// ast_type_decl	---------------------------------------------------
	@Override
	public void caseAAstTypeDecl(AAstTypeDecl node) {
		LinkedList<TId> idlist = node.getId();
		String varType = forPAstTypeExp(node.getAstTypeExp());
		for (Iterator iterator = idlist.iterator(); iterator.hasNext();) {
			TId d = (TId) iterator.next();
			if (!symbolTable.getFirst().containsKey(d.getText().trim())) {
				symbolTable.getFirst().put(d.getText().trim(), varType);
			} else {
				printSymbolTable();
				String errorMsg = "Declaration Error at line " + d.getLine();
				throw new TypeException(errorMsg);
			}
		}
	}
	
//	// ast_func_decl	---------------------------------------------------
//	@Override
//	public void caseAAstFuncDecl(AAstFuncDecl node) {
//		print("func");
//		print(node.getId().getText().trim());
//		print("(");
//		
//		LinkedList params = node.getAstFuncParam();
//		for (Iterator iterator = params.iterator(); iterator.hasNext();) {
//			PAstFuncParam param = (PAstFuncParam) iterator.next();
//			param.apply(this);
//			if (iterator.hasNext())
//				print(",");
//		}
//	
//		print(")");
//		
//		if(node.getAstTypeExp() != null) {
//			node.getAstTypeExp().apply(this);
//		}
//		
//		print("{\n");
//		
//		mIndentStack.push(mIndentStack.size()+1);
//		
//		LinkedList stmts = node.getAstStm();
//		for (Iterator iterator = stmts.iterator(); iterator.hasNext();) {
//			for (int i = 0; i < mIndentStack.size(); i++)
//				print("\t");
//			PAstStm stm = (PAstStm) iterator.next();
//			stm.apply(this);
//			print("\n");
//		}
//		
//		mIndentStack.pop();
//		print("}\n");
//	}
	
	
	// ast_func_param	---------------------------------------------------
	@Override
	public void caseAAstFuncParam(AAstFuncParam node) {
		LinkedList<TId> idlist = node.getId();
		String varType = forPAstTypeExp(node.getAstTypeExp());
		for (Iterator iterator = idlist.iterator(); iterator.hasNext();) {
			TId d = (TId) iterator.next();
			if (!symbolTable.getFirst().containsKey(d.getText().trim())) {
				symbolTable.getFirst().put(d.getText().trim(), varType);
			} else {
				printSymbolTable();
				String errorMsg = "Declaration Error at line " + d.getLine();
				throw new TypeException(errorMsg);
			}
		}
	}
	
	// ast_type_exp 	---------------------------------------------------
	// returns the type of PAstTypeExp
	public String forPAstTypeExp(PAstTypeExp node) {
		if (node.getClass().isInstance(new ABasicAstTypeExp())) {
			ABasicAstTypeExp temp = (ABasicAstTypeExp) node;
			return temp.getBasicTypes().toString().trim();
		} else if (node.getClass().isInstance(new ASliceAstTypeExp())) {
			ASliceAstTypeExp temp = (ASliceAstTypeExp) node;
			return forPAstTypeExp(temp.getAstTypeExp());
		} else if (node.getClass().isInstance(new AArrayAstTypeExp())) {
			AArrayAstTypeExp temp = (AArrayAstTypeExp) node;
			return forPAstTypeExp(temp.getAstTypeExp());
		} else if (node.getClass().isInstance(new AStructAstTypeExp())) {
			AStructAstTypeExp temp = (AStructAstTypeExp) node;
			LinkedList<PAstStructField> structList = temp.getAstStructField();
			for (Iterator iterator = structList.iterator(); iterator.hasNext();) {
				AAstStructField field = (AAstStructField) iterator.next();
				field.apply(this);
			}
			return "struct";
		} else if (node.getClass().isInstance(new AAliasAstTypeExp())) {
			AAliasAstTypeExp temp = (AAliasAstTypeExp) node;
			for (int i = 0; i < symbolTable.size(); i++) {
				if (symbolTable.get(i).containsKey(temp.getId())) {
					return symbolTable.get(i).get(temp.getId());
				}
			}
			printSymbolTable();
			System.out.println("In forPAstTypeExp");
			String errorMsg = "Declaration Error at line " + temp.getId().getLine();
			throw new TypeException(errorMsg);
		}
		return null;
	}
	
//	@Override
//	public void caseABasicAstTypeExp(ABasicAstTypeExp node) {
//		print(node.getBasicTypes().getText().trim());
//	}
//	
//	@Override
//	public void caseASliceAstTypeExp(ASliceAstTypeExp node) {
//		print("[]");
//		node.getAstTypeExp().apply(this);
//	}
//	
//	@Override
//	public void caseAArrayAstTypeExp(AArrayAstTypeExp node) {
//		print("["+node.getSize().getText().trim()+"]");
//		node.getAstTypeExp().apply(this);
//	}
//	
//	@Override
//	public void caseAStructAstTypeExp(AStructAstTypeExp node) {
//		LinkedList fields = node.getAstStructField();
//		for (Iterator iterator = fields.iterator(); iterator.hasNext();) {
//			PAstStructField field = (PAstStructField) iterator.next();
//			field.apply(this);
//		}
//	}
//	
//	@Override
//	public void caseAAstStructField(AAstStructField node) {
//		print_idlist(node.getId());
//		node.getAstTypeExp().apply(this);
//	}
//	
//	@Override
//	public void caseAAliasAstTypeExp(AAliasAstTypeExp node) {
//		print(node.getId().getText().trim());
//	}
//	
	
	// ast_struct_field ---------------------------------------------------
	@Override
	public void caseAAstStructField(AAstStructField node) {
		LinkedList<TId> idlist = node.getId();
		String varType = forPAstTypeExp(node.getAstTypeExp());
		for (Iterator iterator = idlist.iterator(); iterator.hasNext();) {
			TId d = (TId) iterator.next();
			if (!symbolTable.getFirst().containsKey(d.getText().trim())) {
				symbolTable.getFirst().put(d.getText().trim(), varType);
			} else {
				printSymbolTable();
				String errorMsg = "Declaration Error at line " + d.getLine();
				throw new TypeException(errorMsg);
			}
		}
	}
	
	// ast_stm			---------------------------------------------------
	// return the type of PAstStm
	public String forPAstStm(PAstStm node) {
		if (node.getClass().isInstance(new AEmptyAstStm())) {
			
		} else if (node.getClass().isInstance(new AExpAstStm())) {
			
		} else if (node.getClass().isInstance(new AAssignAstStm())) {
			
		} else if (node.getClass().isInstance(new AOpAssignAstStm())) {
			
		} else if (node.getClass().isInstance(new AVarDeclAstStm())) {
			
		} else if (node.getClass().isInstance(new AShortDeclAstStm())) {
			
		} else if (node.getClass().isInstance(new ATypeDeclAstStm())) {
			
		} else if (node.getClass().isInstance(new AIncDecAstStm())) {
			
		} else if (node.getClass().isInstance(new APrintAstStm())) {
			
		} else if (node.getClass().isInstance(new APrintlnAstStm())) {
			
		} else if (node.getClass().isInstance(new AReturnAstStm())) {
			
		} else if (node.getClass().isInstance(new AShortifAstStm())) {
			
		} else if (node.getClass().isInstance(new ALongifAstStm())) {
			
		} else if (node.getClass().isInstance(new ASwitchAstStm())) {
			
		} else if (node.getClass().isInstance(new AForAstStm())) {
			
		} else if (node.getClass().isInstance(new ABlockAstStm())) {
			
		} else if (node.getClass().isInstance(new ABreakAstStm())) {
			
		} else if (node.getClass().isInstance(new AContinueAstStm())) {
			
		}
		return null;
	}
	
//	@Override
//	public void caseAEmptyAstStm(AEmptyAstStm node) {
//		//do nothing
//	}
//	
//	@Override
//	public void caseAExpAstStm(AExpAstStm node) {
//		node.getAstExp().apply(this);
//	}
//	
//	@Override
//	public void caseAAssignAstStm(AAssignAstStm node) {
//		LinkedList lvals = node.getLval();
//		for (Iterator iterator = lvals.iterator(); iterator.hasNext();) {
//			PAstExp exp = (PAstExp) iterator.next();
//			exp.apply(this);
//			if (iterator.hasNext())
//				print(",");
//		}
//		
//		print("=");
//		
//		LinkedList rvals = node.getRval();
//		for (Iterator iterator = rvals.iterator(); iterator.hasNext();) {
//			PAstExp exp = (PAstExp) iterator.next();
//			exp.apply(this);
//			if (iterator.hasNext())
//				print(",");
//		}
//	}
//	
//	@Override
//	public void caseAOpAssignAstStm(AOpAssignAstStm node) {
//		print(node.getL().getText().trim());
//		node.getAstOpAssign().apply(this);
//		node.getR().apply(this);
//	}
//	
//	@Override
//	public void caseAVarDeclAstStm(AVarDeclAstStm node) {
//		LinkedList decls = node.getAstVarDecl();
//		for (Iterator iterator = decls.iterator(); iterator.hasNext();) {
//			PAstVarDecl exp = (PAstVarDecl) iterator.next();
//			exp.apply(this);
//			if (iterator.hasNext()){
//				print("\n");
//				for(int i = 0; i < mIndentStack.size(); i++) {
//					print("\t");
//				}
//			}
//		}
//	}
//	
//	@Override
//	public void caseAShortDeclAstStm(AShortDeclAstStm node) {
//		LinkedList lvals = node.getIds();
//		for (Iterator iterator = lvals.iterator(); iterator.hasNext();) {
//			PAstExp exp = (PAstExp) iterator.next();
//			exp.apply(this);
//			if (iterator.hasNext())
//				print(",");
//		}
//		
//		print(":=");
//		
//		LinkedList rvals = node.getAstExp();
//		for (Iterator iterator = rvals.iterator(); iterator.hasNext();) {
//			PAstExp exp = (PAstExp) iterator.next();
//			exp.apply(this);
//			if (iterator.hasNext())
//				print(",");
//		}
//	}
//	
//	@Override
//	public void caseATypeDeclAstStm(ATypeDeclAstStm node) {
//		LinkedList decls = node.getAstTypeDecl();
//		for (Iterator iterator = decls.iterator(); iterator.hasNext();) {
//			PAstTypeDecl exp = (PAstTypeDecl) iterator.next();
//			exp.apply(this);
//			if (iterator.hasNext()){
//				print("\n");
//				for(int i = 0; i < mIndentStack.size(); i++) {
//					print("\t");
//				}
//			}
//		}
//	}
//	
//	@Override
//	public void caseAIncDecAstStm(AIncDecAstStm node) {
//		node.getAstExp().apply(this);
//		node.getPostOp().apply(this);
//	}
//	
//	@Override
//	public void caseAPrintAstStm(APrintAstStm node) {
//		print("print(");
//		LinkedList exps = node.getAstExp();
//		for (Iterator iterator = exps.iterator(); iterator.hasNext();) {
//			PAstExp exp = (PAstExp) iterator.next();
//			exp.apply(this);
//			if (iterator.hasNext())
//				print(",");
//		}
//		print(")");
//	}
//	
//	@Override
//	public void caseAPrintlnAstStm(APrintlnAstStm node) {
//		print("println(");
//		LinkedList exps = node.getAstExp();
//		for (Iterator iterator = exps.iterator(); iterator.hasNext();) {
//			PAstExp exp = (PAstExp) iterator.next();
//			exp.apply(this);
//			if (iterator.hasNext())
//				print(",");
//		}
//		print(")");
//	}
//	
//	@Override
//	public void caseAReturnAstStm(AReturnAstStm node) {
//		print("return");
//		if (node.getAstExp() != null)
//			node.getAstExp().apply(this);
//	}
//	
//	@Override
//	public void caseAShortifAstStm(AShortifAstStm node) {
//		print("if");
//		if (node.getInit() != null) {
//			node.getInit().apply(this);
//			print(";");
//		}
//		node.getCondition().apply(this);
//		
//		print("{\n");
//		
//		mIndentStack.push(mIndentStack.size()+1);
//		
//		LinkedList stmts = node.getAstStm();
//		for (Iterator iterator = stmts.iterator(); iterator.hasNext();) {
//			for (int i = 0; i < mIndentStack.size(); i++)
//				print("\t");
//			PAstStm stm = (PAstStm) iterator.next();
//			stm.apply(this);
//			print("\n");
//		}
//		
//		mIndentStack.pop();
//
//		for (int i = 0; i < mIndentStack.size(); i++)
//				print("\t");
//		print("}\n");
//	}
//	
//	@Override
//	public void caseALongifAstStm(ALongifAstStm node) {
//		print("if");
//		if (node.getInit() != null) {
//			node.getInit().apply(this);
//			print(";");
//		}
//		node.getCondition().apply(this);
//		
//		print("{\n");
//		
//		mIndentStack.push(mIndentStack.size()+1);
//		
//		LinkedList if_stmts = node.getIfStms();
//		for (Iterator iterator = if_stmts.iterator(); iterator.hasNext();) {
//			for (int i = 0; i < mIndentStack.size(); i++)
//				print("\t");
//			PAstStm stm = (PAstStm) iterator.next();
//			stm.apply(this);
//			print("\n");
//		}
//		
//		mIndentStack.pop();
//		for (int i = 0; i < mIndentStack.size(); i++)
//				print("\t");
//		print("}\n");
//		
//		print("else {\n");
//		mIndentStack.push(mIndentStack.size()+1);
//		LinkedList else_stmts = node.getElseStms();
//		for (Iterator iterator = else_stmts.iterator(); iterator.hasNext();) {
//			for (int i = 0; i < mIndentStack.size(); i++)
//				print("\t");
//			PAstStm stm = (PAstStm) iterator.next();
//			stm.apply(this);
//			print("\n");
//		}
//		
//		mIndentStack.pop();
//		for (int i = 0; i < mIndentStack.size(); i++)
//				print("\t");
//		print("}\n");
//	}
//	
//	@Override
//	public void caseASwitchAstStm(ASwitchAstStm node) {
//		print("switch");
//		node.getAstStm().apply(this);
//		node.getAstExp().apply(this);
//		print(" {\n");
//		LinkedList stms = node.getAstSwitchStm();
//		for (Iterator iterator = stms.iterator(); iterator.hasNext();) {
//			PAstSwitchStm stm = (PAstSwitchStm) iterator.next();
//			stm.apply(this);
//			if (iterator.hasNext())
//				print(",");
//		}
//		print("}\n");
//	}
//	
//	@Override
//	public void caseAForAstStm(AForAstStm node) {
//		print("for");
//		if (node.getInit() != null) {
//			node.getInit().apply(this);
//			print(";");
//		}
//		if (node.getCondition() != null) {
//			node.getCondition().apply(this);
//		}
//		if (node.getPost() != null) {
//			print(";");
//			node.getPost().apply(this);
//		}
//		
//		print("{\n");
//		
//		mIndentStack.push(mIndentStack.size()+1);
//		
//		LinkedList stmts = node.getBody();
//		for (Iterator iterator = stmts.iterator(); iterator.hasNext();) {
//			for (int i = 0; i < mIndentStack.size(); i++)
//				print("\t");
//			PAstStm stm = (PAstStm) iterator.next();
//			stm.apply(this);
//			print("\n");
//		}
//		
//		mIndentStack.pop();
//		for (int i = 0; i < mIndentStack.size(); i++)
//				print("\t");
//		print("}\n");
//	}
//	
//	@Override
//	public void caseABlockAstStm(ABlockAstStm node) {
//		print("{\n");
//		
//		mIndentStack.push(mIndentStack.size()+1);
//		
//		LinkedList stmts = node.getAstStm();
//		for (Iterator iterator = stmts.iterator(); iterator.hasNext();) {
//			for (int i = 0; i < mIndentStack.size(); i++)
//				print("\t");
//			PAstStm stm = (PAstStm) iterator.next();
//			stm.apply(this);
//			print("\n");
//		}
//		
//		mIndentStack.pop();
//		print("}\n");
//	}
//	
//	@Override
//	public void caseABreakAstStm(ABreakAstStm node) {
//		print("break");
//	}
//	
//	@Override
//	public void caseAContinueAstStm(AContinueAstStm node) {
//		print("continue");
//	}
	
	// ast_exp			---------------------------------------------------
	// returns the type of PAstExp
	public String forPAstExp(PAstExp node) {
		if (node.getClass().isInstance(new AParenAstExp())) {
			AParenAstExp temp = (AParenAstExp) node;
			return forPAstExp(temp.getAstExp());
		} else if (node.getClass().isInstance(new AIdAstExp())) {
			AIdAstExp temp = (AIdAstExp) node;
			for (int i = 0; i < symbolTable.size(); i++) {
				if (symbolTable.get(i).containsKey(temp.getId())) {
					return symbolTable.get(i).get(temp.getId());
				}
			}
			printSymbolTable();
			System.out.println("In forPAstTypeExp");
			String errorMsg = "Declaration Error at line " + temp.getId().getLine();
			throw new TypeException(errorMsg);
		} else if (node.getClass().isInstance(new ALitAstExp())) {
			ALitAstExp temp = (ALitAstExp) node;
			return forPAstLiteral(temp.getAstLiteral());
		} else if (node.getClass().isInstance(new AUnaryOpAstExp())) {
			AUnaryOpAstExp temp = (AUnaryOpAstExp) node;
			String unop = forPAstUnaryOp(temp.getAstUnaryOp());
			if (unop.equals("+")) {
				String check = forPAstExp(temp.getAstExp());
				if (!(check.equals("int") || check.equals("float64") || check.equals("rune"))) {
					System.out.println("In forPAstTypeExp");
					String errorMsg = "Unary Operator Error";
					throw new TypeException(errorMsg);
				}
				return check; 
			} else if (unop.equals("-")) {
				String check = forPAstExp(temp.getAstExp());
				if (!(check.equals("int") || check.equals("float64") || check.equals("rune"))) {
					System.out.println("In forPAstTypeExp");
					String errorMsg = "Unary Operator Error";
					throw new TypeException(errorMsg);
				}
				return check; 
			} else if (unop.equals("!")) {
				String check = forPAstExp(temp.getAstExp());
				if (!(check.equals("bool"))) {
					System.out.println("In forPAstTypeExp");
					String errorMsg = "Unary Operator Error";
					throw new TypeException(errorMsg);
				}
				return check; 
			} else if (unop.equals("^")) {
				String check = forPAstExp(temp.getAstExp());
				if (!(check.equals("int") || check.equals("rune"))) {
					System.out.println("In forPAstTypeExp");
					String errorMsg = "Unary Operator Error";
					throw new TypeException(errorMsg);
				}
				return check; 
			} else {
				System.out.println("In forPAstTypeExp");
				String errorMsg = "Unary Operator Error";
				throw new TypeException(errorMsg);
			}
		} else if (node.getClass().isInstance(new ABinaryOpAstExp())) {
			ABinaryOpAstExp temp = (ABinaryOpAstExp) node;
			String leftType = forPAstExp(temp.getLeft());
			String binOp = forPAstBinaryOp(temp.getAstBinaryOp());
			String rightType = forPAstExp(temp.getRight());
			
			if (leftType.equals(rightType) && leftType.equals("bool")) {
				if (binOp.equals("||")) {
					return "bool";
				} else if (binOp.equals("&&")) {
					return "bool";
				}
				System.out.println("In forPAstTypeExp");
				String errorMsg = "Binary Operator Error";
				throw new TypeException(errorMsg);
			} else if (leftType.equals(rightType) && leftType.equals("string")) {
				if (binOp.equals("==")) {
					return "bool";
				} else if (binOp.equals("!=")) {
					return "bool";
				} else if (binOp.equals("<")) {
					return "bool";
				} else if (binOp.equals("<=")) {
					return "bool";
				} else if (binOp.equals(">")) {
					return "bool";
				} else if (binOp.equals(">=")) {
					return "bool";
				} else if (binOp.equals("+")) {
					return "string";
				}
				System.out.println("In forPAstTypeExp");
				String errorMsg = "Binary Operator Error";
				throw new TypeException(errorMsg);
			} else if (leftType.equals(rightType) && leftType.equals("float64")) {
				if (binOp.equals("==")) {
					return "bool";
				} else if (binOp.equals("!=")) {
					return "bool";
				} else if (binOp.equals("<")) {
					return "bool";
				} else if (binOp.equals("<=")) {
					return "bool";
				} else if (binOp.equals(">")) {
					return "bool";
				} else if (binOp.equals(">=")) {
					return "bool";
				} else if (binOp.equals("+")) {
					return leftType;
				} else if (binOp.equals("-")) {
					return leftType;
				} else if (binOp.equals("*")) {
					return leftType;
				} else if (binOp.equals("/")) {
					return leftType;
				} else if (binOp.equals("%")) {
					return leftType;
				}
				System.out.println("In forPAstTypeExp");
				String errorMsg = "Binary Operator Error";
				throw new TypeException(errorMsg);
			} else if (leftType.equals(rightType) && leftType.equals("int")) {
				if (binOp.equals("==")) {
					return "bool";
				} else if (binOp.equals("!=")) {
					return "bool";
				} else if (binOp.equals("<")) {
					return "bool";
				} else if (binOp.equals("<=")) {
					return "bool";
				} else if (binOp.equals(">")) {
					return "bool";
				} else if (binOp.equals(">=")) {
					return "bool";
				} else if (binOp.equals("+")) {
					return leftType;
				} else if (binOp.equals("-")) {
					return leftType;
				} else if (binOp.equals("*")) {
					return leftType;
				} else if (binOp.equals("/")) {
					return leftType;
				} else if (binOp.equals("%")) {
					return leftType;
				} else if (binOp.equals("|")) {
					return leftType;
				} else if (binOp.equals("&")) {
					return leftType;
				} else if (binOp.equals("<<")) {
					return leftType;
				} else if (binOp.equals(">>")) {
					return leftType;
				} else if (binOp.equals("&^")) {
					return leftType;
				} else if (binOp.equals("^")) {
					return leftType;
				}
				System.out.println("In forPAstTypeExp");
				String errorMsg = "Binary Operator Error";
				throw new TypeException(errorMsg);
			}
			
		} else if (node.getClass().isInstance(new AFuncCallAstExp())) {
			
		} else if (node.getClass().isInstance(new AAppendAstExp())) {
			
		} else if (node.getClass().isInstance(new ABasicCastAstExp())) {
			
		} else if (node.getClass().isInstance(new AArrayAccessAstExp())) {
			
		} else if (node.getClass().isInstance(new AFieldAccessAstExp())) {
			
		}
		return null;
	}
	
//	@Override
//	public void caseAParenAstExp(AParenAstExp node) {
//		print("(");
//		node.getAstExp().apply(this);
//		print(")");
//	}
//	
//	@Override
//	public void caseAIdAstExp(AIdAstExp node) {
//		print(node.getId().getText().trim());
//	}
//	
//	@Override
//	public void caseALitAstExp(ALitAstExp node) {
//		node.getAstLiteral().apply(this);
//	}
//	
//	@Override
//	public void caseAUnaryOpAstExp(AUnaryOpAstExp node) {
//		node.getAstUnaryOp().apply(this);
//		node.getAstExp().apply(this);
//	}
//	
//	@Override
//	public void caseABinaryOpAstExp(ABinaryOpAstExp node) {
//		node.getLeft().apply(this);
//		node.getAstBinaryOp().apply(this);
//		node.getRight().apply(this);
//	}
//	
//	@Override
//	public void caseAFuncCallAstExp(AFuncCallAstExp node) {
//		node.getName().apply(this);
//		print("(");
//		LinkedList exps = node.getArgs();
//		for (Iterator iterator = exps.iterator(); iterator.hasNext();) {
//			PAstExp exp = (PAstExp) iterator.next();
//			exp.apply(this);
//			if (iterator.hasNext())
//				print(",");
//		}
//		print(")");
//	}
//	
//	@Override
//	public void caseAAppendAstExp(AAppendAstExp node) {
//		print("append(");
//		print(node.getId().getText().trim());
//		print(",");
//		node.getAstExp().apply(this);
//		print(")");
//	}
//	
//	@Override
//	public void caseABasicCastAstExp(ABasicCastAstExp node) {
//		print(node.getBasicTypes().toString().trim());
//		print("(");
//		node.getAstExp().apply(this);
//		print(")");
//	}
//	
//	@Override
//	public void caseAArrayAccessAstExp(AArrayAccessAstExp node) {
//		node.getArray().apply(this);
//		print("[");
//		node.getIndex().apply(this);
//		print("]");
//	}
//	
//	@Override
//	public void caseAFieldAccessAstExp(AFieldAccessAstExp node) {
//		node.getStruct().apply(this);
//		print(".");
//		print(node.getField().toString().trim());
//	}
//	
//	// ast_switch_stm	---------------------------------------------------
//	@Override
//	public void caseAAstSwitchStm(AAstSwitchStm node) {
//		node.getAstSwitchCase().apply(this);
//		
//		LinkedList stms = node.getAstStm();
//		if (!stms.isEmpty()) {
//			for (Iterator iterator = stms.iterator(); iterator.hasNext();) {
//				PAstStm d = (PAstStm) iterator.next();
//				d.apply(this);
//				print("\n");
//			}
//		}
//		
//		node.getAstFallthroughStm().apply(this);
//		print("\n");
//	}
//	
//	// ast_switch_case	---------------------------------------------------
//	@Override
//	public void caseADefaultAstSwitchCase(ADefaultAstSwitchCase node) {
//		print("default: ");
//	}
//	
//	@Override
//	public void caseACaseAstSwitchCase(ACaseAstSwitchCase node) {
//		print("case ");
//		LinkedList exps = node.getAstExp();
//		if (!exps.isEmpty()) {
//			for (Iterator iterator = exps.iterator(); iterator.hasNext();) {
//				PAstExp d = (PAstExp) iterator.next();
//				d.apply(this);
//				print(", ");
//			}
//		}
//		print(": ");
//	}
	
	// ast_literal	---------------------------------------------------
	// returns the type of PAstLiteral
	public String forPAstLiteral(PAstLiteral node) {
		if (node.getClass().isInstance(new AIntAstLiteral())) {
			return "int";
		} else if (node.getClass().isInstance(new AFloatAstLiteral())) {
			return "float64";
		} else if (node.getClass().isInstance(new ARuneAstLiteral())) {
			return "rune";
		} else if (node.getClass().isInstance(new AStringAstLiteral())) {
			return "string";
		}
		return null;
	}
	
//	@Override
//	public void caseAIntAstLiteral(AIntAstLiteral node) {
//		print(node.getIntLit().toString().trim());
//	}
//	
//	@Override
//	public void caseAFloatAstLiteral(AFloatAstLiteral node) {
//		print(node.getFloatLit().toString().trim());
//	}
//	
//	@Override
//	public void caseARuneAstLiteral(ARuneAstLiteral node) {
//		print(node.getRuneLit().toString().trim());
//	}
//	
//	@Override
//	public void caseAStringAstLiteral(AStringAstLiteral node) {
//		print(node.getStringLit().toString().trim());
//	}
//	
	// ast_binary_op	---------------------------------------------------
	// return the binary_op of PAstBinaryOp
	public String forPAstBinaryOp(PAstBinaryOp node) {
		if (node.getClass().isInstance(new AAddAstBinaryOp())) {
			return "+";
		} else if (node.getClass().isInstance(new ASubAstBinaryOp())) {
			return "-";
		} else if (node.getClass().isInstance(new AMulAstBinaryOp())) {
			return "*";
		} else if (node.getClass().isInstance(new ADivAstBinaryOp())) {
			return "/";
		} else if (node.getClass().isInstance(new AModAstBinaryOp())) {
			return "%";
		} else if (node.getClass().isInstance(new ABitorAstBinaryOp())) {
			return "|";
		} else if (node.getClass().isInstance(new ABitandAstBinaryOp())) {
			return "&";
		} else if (node.getClass().isInstance(new AEqAstBinaryOp())) {
			return "==";
		} else if (node.getClass().isInstance(new ANoteqAstBinaryOp())) {
			return "!+";
		} else if (node.getClass().isInstance(new ALtAstBinaryOp())) {
			return "<";
		} else if (node.getClass().isInstance(new ALeqAstBinaryOp())) {
			return "<=";
		} else if (node.getClass().isInstance(new AGtAstBinaryOp())) {
			return ">";
		} else if (node.getClass().isInstance(new AGeqAstBinaryOp())) {
			return ">=";
		} else if (node.getClass().isInstance(new ACaretAstBinaryOp())) {
			return "^";
		} else if (node.getClass().isInstance(new ALshiftAstBinaryOp())) {
			return "<<";
		} else if (node.getClass().isInstance(new ARshiftAstBinaryOp())) {
			return ">>";
		} else if (node.getClass().isInstance(new ABitclearAstBinaryOp())) {
			return "&^";
		} else if (node.getClass().isInstance(new AOrAstBinaryOp())) {
			return "||";
		} else if (node.getClass().isInstance(new AAndAstBinaryOp())) {
			return "&&";
		}
		return null;
	}
	
//	@Override
//	public void caseAAddAstBinaryOp(AAddAstBinaryOp node) {
//		print("+");
//	}
//	
//	@Override
//	public void caseASubAstBinaryOp(ASubAstBinaryOp node) {
//		print("-");
//	}
//	
//	@Override
//	public void caseAMulAstBinaryOp(AMulAstBinaryOp node) {
//		print("*");
//	}
//	
//	@Override
//	public void caseADivAstBinaryOp(ADivAstBinaryOp node) {
//		print("/");
//	}
//	
//	@Override
//	public void caseAModAstBinaryOp(AModAstBinaryOp node) {
//		print("%");
//	}
//	
//	@Override
//	public void caseABitorAstBinaryOp(ABitorAstBinaryOp node) {
//		print("|");
//	}
//	
//	@Override
//	public void caseABitandAstBinaryOp(ABitandAstBinaryOp node) {
//		print("&");
//	}
//	
//	@Override
//	public void caseAEqAstBinaryOp(AEqAstBinaryOp node) {
//		print("==");
//	}
//	
//	@Override
//	public void caseANoteqAstBinaryOp(ANoteqAstBinaryOp node) {
//		print("!=");
//	}
//	
//	@Override
//	public void caseALtAstBinaryOp(ALtAstBinaryOp node) {
//		print("<");
//	}
//	
//	@Override
//	public void caseALeqAstBinaryOp(ALeqAstBinaryOp node) {
//		print("<=");
//	}
//	
//	@Override
//	public void caseAGtAstBinaryOp(AGtAstBinaryOp node) {
//		print(">");
//	}
//	
//	@Override
//	public void caseAGeqAstBinaryOp(AGeqAstBinaryOp node) {
//		print(">=");
//	}
//	
//	@Override
//	public void caseACaretAstBinaryOp(ACaretAstBinaryOp node) {
//		print("^");
//	}
//	
//	@Override
//	public void caseALshiftAstBinaryOp(ALshiftAstBinaryOp node) {
//		print("<<");
//	}
//	
//	@Override
//	public void caseARshiftAstBinaryOp(ARshiftAstBinaryOp node) {
//		print(">>");
//	}
//	
//	@Override
//	public void caseABitclearAstBinaryOp(ABitclearAstBinaryOp node) {
//		print("&^");
//	}
//	
//	@Override
//	public void caseAOrAstBinaryOp(AOrAstBinaryOp node) {
//		print("||");
//	}
//	
//	@Override
//	public void caseAAndAstBinaryOp(AAndAstBinaryOp node) {
//		print("&&");
//	}
//	
	// ast_op_assign	---------------------------------------------------
	// return op_assign of PAstOpAssign
	public String forPAstOpAssign(PAstOpAssign node) {
		if (node.getClass().isInstance(new AAddEqAstOpAssign())) {
			return "+=";
		} else if (node.getClass().isInstance(new ASubEqAstOpAssign())) {
			return "-=";
		} else if (node.getClass().isInstance(new AMulEqAstOpAssign())) {
			return "*=";
		} else if (node.getClass().isInstance(new ADivEqAstOpAssign())) {
			return "/=";
		} else if (node.getClass().isInstance(new AModEqAstOpAssign())) {
			return "%=";
		} else if (node.getClass().isInstance(new ABitorEqAstOpAssign())) {
			return "|=";
		} else if (node.getClass().isInstance(new ABitandEqAstOpAssign())) {
			return "^=";
		} else if (node.getClass().isInstance(new ACaretEqAstOpAssign())) {
			return "^=";
		} else if (node.getClass().isInstance(new ALshiftEqAstOpAssign())) {
			return "<<=";
		} else if (node.getClass().isInstance(new ARshiftEqAstOpAssign())) {
			return ">>=";
		} else if (node.getClass().isInstance(new ABitclearEqAstOpAssign())) {
			return "&^=";
		}
		return null;
	}
	
//	@Override
//	public void caseAAddEqAstOpAssign(AAddEqAstOpAssign node) {
//		print("+=");
//	}
//	
//	@Override
//	public void caseASubEqAstOpAssign(ASubEqAstOpAssign node) {
//		print("-=");
//	}
//	
//	@Override
//	public void caseAMulEqAstOpAssign(AMulEqAstOpAssign node) {
//		print("*=");
//	}
//	
//	@Override
//	public void caseADivEqAstOpAssign(ADivEqAstOpAssign node) {
//		print("/=");
//	}
//	
//	@Override
//	public void caseAModEqAstOpAssign(AModEqAstOpAssign node) {
//		print("%=");
//	}
//	
//	@Override
//	public void caseABitorEqAstOpAssign(ABitorEqAstOpAssign node) {
//		print("|=");
//	}
//	
//	@Override
//	public void caseABitandEqAstOpAssign(ABitandEqAstOpAssign node) {
//		print("&=");
//	}
//	
//	@Override
//	public void caseACaretEqAstOpAssign(ACaretEqAstOpAssign node) {
//		print("^=");
//	}
//	
//	@Override
//	public void caseALshiftEqAstOpAssign(ALshiftEqAstOpAssign node) {
//		print("<<=");
//	}
//	
//	@Override
//	public void caseARshiftEqAstOpAssign(ARshiftEqAstOpAssign node) {
//		print(">>=");
//	}
//	
//	@Override
//	public void caseABitclearEqAstOpAssign(ABitclearEqAstOpAssign node) {
//		print("&^=");
//	}
//	
	// ast_unary_op		---------------------------------------------------
	// return the unary_op of PAstUnaryOp
	public String forPAstUnaryOp(PAstUnaryOp node) {
		if (node.getClass().isInstance(new APlusAstUnaryOp())) {
			return "+";
		} else if (node.getClass().isInstance(new AMinusAstUnaryOp())) {
			return "-";
		} else if (node.getClass().isInstance(new ANotAstUnaryOp())) {
			return "!";
		} else if (node.getClass().isInstance(new ACaretAstUnaryOp())) {
			return "^";
		}
		return null;
	}
	
//	@Override
//	public void caseAPlusAstUnaryOp(APlusAstUnaryOp node) {
//		print("+");
//	}
//	
//	@Override
//	public void caseAMinusAstUnaryOp(AMinusAstUnaryOp node) {
//		print("-");
//	}
//	
//	@Override
//	public void caseANotAstUnaryOp(ANotAstUnaryOp node) {
//		print("!");
//	}
//	
//	@Override
//	public void caseACaretAstUnaryOp(ACaretAstUnaryOp node) {
//		print("^");
//	}
//	
	// ast_post_op	---------------------------------------------------
	// return the post_op of PAstPostOp
	public String forPAstPostOp(PAstPostOp node) {
		if (node.getClass().isInstance(new AIncAstPostOp())) {
			return "++";
		} else if (node.getClass().isInstance(new ADecAstPostOp())) {
			return "--";
		}
		return null;
	}
	
//	@Override
//	public void caseAIncAstPostOp(AIncAstPostOp node) {
//		print("++");
//	}
//	
//	@Override
//	public void caseADecAstPostOp(ADecAstPostOp node) {
//		print("--");
//	}
//	
	// ast_fallthrough_stm	---------------------------------------------------
	// return fallthrough_stm of PAstFallthroughStm
	public String forPAstFallthroughStm(PAstFallthroughStm node) {
		if (node.getClass().isInstance(new AAstFallthroughStm())) {
			return "fallthrough";
		}
		return null;
	}
	
//	@Override
//	public void caseAAstFallthroughStm(AAstFallthroughStm node) {
//		print("fallthrough");
//	}
}
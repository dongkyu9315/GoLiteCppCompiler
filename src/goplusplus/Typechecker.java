package goplusplus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Iterator;

import goplusplus.analysis.DepthFirstAdapter;
import goplusplus.node.*;
import type.*;

public class Typechecker extends DepthFirstAdapter{
	private LinkedList<HashMap<String, Type>> symbolTable;
	
	public Typechecker() {
		symbolTable = new LinkedList<HashMap<String, Type> >();
		symbolTable.addFirst(new HashMap<String, Type>());
	}
	
	public void check(Node node) {
		node.apply(this);
	}
	
	// print the symbol table to the console
	public void printSymbolTable() {
		for (int i = 0; i < symbolTable.size(); i++) {
			HashMap<String, Type> temp = symbolTable.get(i);
			System.out.println("Layer " + i + ":");
			for (HashMap.Entry<String, Type> entry : temp.entrySet()) {
				System.out.println("Key = " + entry.getKey() + ", Type = " + entry.getValue());
			}
		}
	}
	
	// ast_program		---------------------------------------------------
	@Override
	public void caseAAstProgram(AAstProgram node) {
		LinkedList<PAstDecl> decl = node.getDecl();
		if (!decl.isEmpty()) {
			for (Iterator<PAstDecl> iterator = decl.iterator(); iterator.hasNext();) {
				PAstDecl d = (PAstDecl) iterator.next();
				d.apply(this);
			}
		}
	}
	
	// ast_decl			---------------------------------------------------
	@Override
	public void caseAVarDecAstDecl(AVarDecAstDecl node) {
		LinkedList<PAstVarDecl> decl = node.getAstVarDecl();
		if (!decl.isEmpty()) {
			for (Iterator<PAstVarDecl> iterator = decl.iterator(); iterator.hasNext();) {
				PAstVarDecl d = (PAstVarDecl) iterator.next();
				d.apply(this);
			}
		}
	}
	
	@Override
	public void caseATypeDecAstDecl(ATypeDecAstDecl node) {
		LinkedList<PAstTypeDecl> decl = node.getAstTypeDecl();
		if (!decl.isEmpty()) {
			for (Iterator<PAstTypeDecl> iterator = decl.iterator(); iterator.hasNext();) {
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
		Type varType = forPAstTypeExp(node.getAstTypeExp());
		for (Iterator<TId> iterator = idlist.iterator(); iterator.hasNext();) {
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
			Type varType = forPAstExp(exps.get(i));
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
		Type typeExp = forPAstTypeExp(node.getAstTypeExp());
		LinkedList<PAstExp> exps = node.getAstExp();
		
		if (idlist.size() != exps.size()) {
			printSymbolTable();
			String errorMsg = "Declaration Error at line " + idlist.getFirst().getLine();
			throw new TypeException(errorMsg);
		}
		
		for (int i = 0; i < idlist.size(); i++) {
			TId d = idlist.get(i);
			Type varType = forPAstExp(exps.get(i));
			if (!varType.assign(typeExp)) {
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
		Type varType = forPAstTypeExp(node.getAstTypeExp());
		for (Iterator<TId> iterator = idlist.iterator(); iterator.hasNext();) {
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
	
	// ast_func_decl	---------------------------------------------------
	@Override
	public void caseAAstFuncDecl(AAstFuncDecl node) {
		if (!symbolTable.getFirst().containsKey(node.getId().getText().trim())) {
			Type varType;
			if (node.getAstFuncParam() != null) {
				varType = forPAstTypeExp(node.getAstTypeExp());
			} else {
				varType = Type.VOID;
			}
			symbolTable.getFirst().put(node.getId().getText().trim(), varType);
		} else {
			printSymbolTable();
			String errorMsg = "Declaration Error at line " + node.getId().getLine();
			throw new TypeException(errorMsg);
		}
		
		HashMap<String, Type> newScope = new HashMap<String, Type>();
		symbolTable.addFirst(newScope);
		
		LinkedList<PAstFuncParam> params = node.getAstFuncParam();
		for (Iterator<PAstFuncParam> iterator = params.iterator(); iterator.hasNext();) {
			PAstFuncParam param = (PAstFuncParam) iterator.next();
			param.apply(this);
		}
		
		LinkedList<PAstStm> stmts = node.getAstStm();
		for (Iterator<PAstStm> iterator = stmts.iterator(); iterator.hasNext();) {
			PAstStm stm = (PAstStm) iterator.next();
			stm.apply(this);
		}
		
		symbolTable.removeFirst();
	}
	
	
	// ast_func_param	---------------------------------------------------
	@Override
	public void caseAAstFuncParam(AAstFuncParam node) {
		LinkedList<TId> idlist = node.getId();
		Type varType = forPAstTypeExp(node.getAstTypeExp());
		for (Iterator<TId> iterator = idlist.iterator(); iterator.hasNext();) {
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
	public Type forPAstTypeExp(PAstTypeExp node) {
		if (node.getClass().isInstance(new ABasicAstTypeExp())) {
			ABasicAstTypeExp temp = (ABasicAstTypeExp) node;
			String t = temp.getBasicTypes().toString().trim();
			if (t.equals("int"))
				return Type.INT;
			else if (t.equals("float64"))
				return Type.FLOAT64;
			else if (t.equals("rune"))
				return Type.RUNE;
			else if (t.equals("string"))
				return Type.STRING;
			else if (t.equals("bool"))
				return Type.BOOL;
			else {
				return null;
			}
		} else if (node.getClass().isInstance(new ASliceAstTypeExp())) {
			ASliceAstTypeExp temp = (ASliceAstTypeExp) node;
			Type eleType = forPAstTypeExp(temp.getAstTypeExp());
			SliceType sliceType = new SliceType();
			sliceType.elementType = eleType;
			return sliceType;
		} else if (node.getClass().isInstance(new AArrayAstTypeExp())) {
			AArrayAstTypeExp temp = (AArrayAstTypeExp) node;
			Type eleType = forPAstTypeExp(temp.getAstTypeExp());
			ArrayType arrayType = new ArrayType();
			arrayType.elementType = eleType;
			arrayType.size = Integer.parseInt(temp.getSize().toString());
			return arrayType;
		} else if (node.getClass().isInstance(new AStructAstTypeExp())) {
			AStructAstTypeExp temp = (AStructAstTypeExp) node;
			LinkedList<PAstStructField> structList = temp.getAstStructField();
			ArrayList<Type> typesList = new ArrayList<Type>();
			for (Iterator<PAstStructField> iterator = structList.iterator(); iterator.hasNext();) {
				AAstStructField field = (AAstStructField) iterator.next();
				Type eleType = forPAstTypeExp(field.getAstTypeExp());
				typesList.add(eleType);
			}
			StructType structType = new StructType();
			structType.elementTypes = typesList;
			return structType;
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
		Type varType = forPAstTypeExp(node.getAstTypeExp());
		for (Iterator<TId> iterator = idlist.iterator(); iterator.hasNext();) {
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
	
	// TODO: probably DO NOT need this method, but keeping it just in case
//	// return the type of PAstStm
//	public Type forPAstStm(PAstStm node) {
//		if (node.getClass().isInstance(new AEmptyAstStm())) {
//			
//		} else if (node.getClass().isInstance(new AExpAstStm())) {
//			
//		} else if (node.getClass().isInstance(new AAssignAstStm())) {
//			
//		} else if (node.getClass().isInstance(new AOpAssignAstStm())) {
//			
//		} else if (node.getClass().isInstance(new AVarDeclAstStm())) {
//			
//		} else if (node.getClass().isInstance(new AShortDeclAstStm())) {
//			
//		} else if (node.getClass().isInstance(new ATypeDeclAstStm())) {
//			
//		} else if (node.getClass().isInstance(new AIncDecAstStm())) {
//			
//		} else if (node.getClass().isInstance(new APrintAstStm())) {
//			
//		} else if (node.getClass().isInstance(new APrintlnAstStm())) {
//			
//		} else if (node.getClass().isInstance(new AReturnAstStm())) {
//			
//		} else if (node.getClass().isInstance(new AShortifAstStm())) {
//			
//		} else if (node.getClass().isInstance(new ALongifAstStm())) {
//			
//		} else if (node.getClass().isInstance(new ASwitchAstStm())) {
//			
//		} else if (node.getClass().isInstance(new AForAstStm())) {
//			
//		} else if (node.getClass().isInstance(new ABlockAstStm())) {
//			
//		} else if (node.getClass().isInstance(new ABreakAstStm())) {
//			
//		} else if (node.getClass().isInstance(new AContinueAstStm())) {
//			
//		}
//		return null;
//	}
	
	@Override
	public void caseAEmptyAstStm(AEmptyAstStm node) {
		//do nothing
	}
	
	@Override
	public void caseAExpAstStm(AExpAstStm node) {
		node.getAstExp().apply(this);
	}
	
	@Override
	public void caseAAssignAstStm(AAssignAstStm node) {
		AAssignAstStm temp = (AAssignAstStm) node;
		LinkedList<PAstExp> leftList = temp.getLval();
		LinkedList<PAstExp> rightList = temp.getRval();
		
		for (int i = 0; i < leftList.size(); i++) {
			Type leftType = forPAstExp(leftList.get(i));
			Type rightType = forPAstExp(rightList.get(i));
			if (!leftType.is(rightType)) {
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(temp);
				String errorMsg = "Declaration Error at line " + pos.getLine(temp);
				throw new TypeException(errorMsg);
			}
		}
	}
	
	@Override
	public void caseAOpAssignAstStm(AOpAssignAstStm node) {
		TId d = node.getL();
		Type varType = forPAstExp(node.getR());
		if (!symbolTable.getFirst().containsKey(d.getText().trim())) {
			symbolTable.getFirst().put(d.getText().trim(), varType);
		} else {
			printSymbolTable();
			String errorMsg = "Declaration Error at line " + d.getLine();
			throw new TypeException(errorMsg);
		}
	}
	
	@Override
	public void caseAVarDeclAstStm(AVarDeclAstStm node) {
		LinkedList<PAstVarDecl> decls = node.getAstVarDecl();
		for (Iterator<PAstVarDecl> iterator = decls.iterator(); iterator.hasNext();) {
			PAstVarDecl exp = (PAstVarDecl) iterator.next();
			exp.apply(this);
		}
	}
	
	@Override
	public void caseAShortDeclAstStm(AShortDeclAstStm node) {
		AShortDeclAstStm temp = (AShortDeclAstStm) node;
		LinkedList<PAstExp> leftList = temp.getIds();
		LinkedList<PAstExp> rightList = temp.getAstExp();
		
		for (int i = 0; i < leftList.size(); i++) {
			Type leftType = forPAstExp(leftList.get(i));
			Type rightType = forPAstExp(rightList.get(i));
			if (!leftType.is(rightType)) {
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(temp);
				String errorMsg = "Declaration Error at line " + pos.getLine(temp);
				throw new TypeException(errorMsg);
			}
		}
	}
	
	@Override
	public void caseATypeDeclAstStm(ATypeDeclAstStm node) {
		LinkedList<PAstTypeDecl> decls = node.getAstTypeDecl();
		for (Iterator<PAstTypeDecl> iterator = decls.iterator(); iterator.hasNext();) {
			PAstTypeDecl exp = (PAstTypeDecl) iterator.next();
			exp.apply(this);
		}
	}
	
	// TODO: implement the method below
//	@Override
//	public void caseAIncDecAstStm(AIncDecAstStm node) {
//		String temp = forPAstPostOp(node.getAstPostOp());
//		if (temp.equals("++")) {
//			
//		} else if (temp.equals("--")) {
//			
//		} else {
//			printSymbolTable();
//			Position pos = new Position();
//			pos.defaultCase(node);
//			String errorMsg = "Declaration Error at line " + pos.getLine(node);
//			throw new TypeException(errorMsg);
//		}
//		
//		
//		node.getAstExp().apply(this);
//		node.getAstPostOp().apply(this);
//	}
	
	@Override
	public void caseAPrintAstStm(APrintAstStm node) {
		LinkedList<PAstExp> exps = node.getAstExp();
		for (Iterator<PAstExp> iterator = exps.iterator(); iterator.hasNext();) {
			PAstExp exp = (PAstExp) iterator.next();
			if (!typeCheckPAstExp(exp)) {
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(exp);
				String errorMsg = "Declaration Error at line " + pos.getLine(exp);
				throw new TypeException(errorMsg);
			}
		}
	}
	
	@Override
	public void caseAPrintlnAstStm(APrintlnAstStm node) {
		LinkedList<PAstExp> exps = node.getAstExp();
		for (Iterator<PAstExp> iterator = exps.iterator(); iterator.hasNext();) {
			PAstExp exp = (PAstExp) iterator.next();
			if (!typeCheckPAstExp(exp)) {
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(exp);
				String errorMsg = "Declaration Error at line " + pos.getLine(exp);
				throw new TypeException(errorMsg);
			}
		}
	}
	
	@Override
	public void caseAReturnAstStm(AReturnAstStm node) {
		if (node.getAstExp() != null) {
			if (!typeCheckPAstExp(node.getAstExp())) {
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(node.getAstExp());
				String errorMsg = "Declaration Error at line " + pos.getLine(node.getAstExp());
				throw new TypeException(errorMsg);
			}
		}
	}
	
	// TODO: implement the method below
//	@Override
//	public void caseAShortifAstStm(AShortifAstStm node) {
//		print("if");
//		if (node.getInit() != null) {
//			node.getInit().apply(this);
//			print(";");
//		}
//		node.getCondition().apply(this);
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
	
	// TODO: implement the method below
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
	
	// TODO: implement the method below
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
	
	// TODO: implement the method below
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
	
	@Override
	public void caseABlockAstStm(ABlockAstStm node) {
		
		HashMap<String, Type> newScope = new HashMap<String, Type>();
		symbolTable.addFirst(newScope);
		
		LinkedList<PAstStm> stmts = node.getAstStm();
		for (Iterator<PAstStm> iterator = stmts.iterator(); iterator.hasNext();) {
			PAstStm stm = (PAstStm) iterator.next();
			stm.apply(this);
		}
		
		symbolTable.removeFirst();
	}
	
	@Override
	public void caseABreakAstStm(ABreakAstStm node) {
		// do nothing
	}
	
	@Override
	public void caseAContinueAstStm(AContinueAstStm node) {
		// do nothing
	}
	
	// ast_exp			---------------------------------------------------
	public boolean typeCheckPAstExp(PAstExp node) {
		if (node.getClass().isInstance(new AParenAstExp())) {
			AParenAstExp temp = (AParenAstExp) node;
			typeCheckPAstExp(temp);
		} else if (node.getClass().isInstance(new AIdAstExp())) {
			AIdAstExp temp = (AIdAstExp) node;
			for (int i = 0; i < symbolTable.size(); i++) {
				if (symbolTable.get(i).containsKey(temp.getId().getText().trim())) {
					return true;
				}
			}
		} else if (node.getClass().isInstance(new ALitAstExp())) {
			return true;
		} else if (node.getClass().isInstance(new AUnaryOpAstExp())) {
			AUnaryOpAstExp temp = (AUnaryOpAstExp) node;
			// just to check that forPAstExp(temp) does not give an error msg
			forPAstExp(temp);
			return true;
		} else if (node.getClass().isInstance(new ABinaryOpAstExp())) {
			ABinaryOpAstExp temp = (ABinaryOpAstExp) node;
			// just to check that forPAstExp(temp) does not give an error msg
			forPAstExp(temp);
			return true;
		// TODO: implement the cases below
		} else if (node.getClass().isInstance(new AFuncCallAstExp())) {
			
		} else if (node.getClass().isInstance(new AAppendAstExp())) {
			
		} else if (node.getClass().isInstance(new ABasicCastAstExp())) {
			
		} else if (node.getClass().isInstance(new AArrayAccessAstExp())) {
			
		} else if (node.getClass().isInstance(new AFieldAccessAstExp())) {
			
		}
		return false;
	}
	
	// returns the type of PAstExp
	public Type forPAstExp(PAstExp node) {
		if (node.getClass().isInstance(new AParenAstExp())) {
			AParenAstExp temp = (AParenAstExp) node;
			return forPAstExp(temp.getAstExp());
		} else if (node.getClass().isInstance(new AIdAstExp())) {
			AIdAstExp temp = (AIdAstExp) node;
			for (int i = 0; i < symbolTable.size(); i++) {
				if (symbolTable.get(i).containsKey(temp.getId().getText().trim())) {
					return symbolTable.get(i).get(temp.getId());
				}
			}
			printSymbolTable();
			System.out.println("In forPAstExp");
			String errorMsg = "Declaration Error at line " + temp.getId().getLine();
			throw new TypeException(errorMsg);
		} else if (node.getClass().isInstance(new ALitAstExp())) {
			ALitAstExp temp = (ALitAstExp) node;
			return forPAstLiteral(temp.getAstLiteral());
		} else if (node.getClass().isInstance(new AUnaryOpAstExp())) {
			AUnaryOpAstExp temp = (AUnaryOpAstExp) node;
			String unop = forPAstUnaryOp(temp.getAstUnaryOp());
			if (unop.equals("+")) {
				Type check = forPAstExp(temp.getAstExp());
				if (!(check.is(Type.INT) || check.is(Type.FLOAT64) || check.is(Type.RUNE))) {
					printSymbolTable();
					Position pos = new Position();
					pos.defaultCase(temp);
					System.out.println("In forPAstExp");
					String errorMsg = "Unary Operator Error at line " + pos.getLine(temp);
					throw new TypeException(errorMsg);
				}
				return check; 
			} else if (unop.equals("-")) {
				Type check = forPAstExp(temp.getAstExp());
				if (!(check.is(Type.INT) || check.is(Type.FLOAT64) || check.is(Type.RUNE))) {
					printSymbolTable();
					Position pos = new Position();
					pos.defaultCase(temp);
					System.out.println("In forPAstExp");
					String errorMsg = "Unary Operator Error at line " + pos.getLine(temp);
					throw new TypeException(errorMsg);
				}
				return check; 
			} else if (unop.equals("!")) {
				Type check = forPAstExp(temp.getAstExp());
				if (!(check.is(Type.BOOL))) {
					printSymbolTable();
					Position pos = new Position();
					pos.defaultCase(temp);
					System.out.println("In forPAstExp");
					String errorMsg = "Unary Operator Error at line " + pos.getLine(temp);
					throw new TypeException(errorMsg);
				}
				return check; 
			} else if (unop.equals("^")) {
				Type check = forPAstExp(temp.getAstExp());
				if (!(check.is(Type.INT) || check.is(Type.RUNE))) {
					printSymbolTable();
					Position pos = new Position();
					pos.defaultCase(temp);
					System.out.println("In forPAstExp");
					String errorMsg = "Unary Operator Error at line " + pos.getLine(temp);
					throw new TypeException(errorMsg);
				}
				return check; 
			} else {
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(temp);
				System.out.println("In forPAstExp");
				String errorMsg = "Unary Operator Error at line " + pos.getLine(temp);
				throw new TypeException(errorMsg);
			}
		} else if (node.getClass().isInstance(new ABinaryOpAstExp())) {
			ABinaryOpAstExp temp = (ABinaryOpAstExp) node;
			Type leftType = forPAstExp(temp.getLeft());
			String binOp = forPAstBinaryOp(temp.getAstBinaryOp());
			Type rightType = forPAstExp(temp.getRight());
			
			if (leftType.is(rightType) && leftType.is(Type.BOOL)) {
				if (binOp.equals("||")) {
					return Type.BOOL;
				} else if (binOp.equals("&&")) {
					return Type.BOOL;
				}
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(temp);
				System.out.println("In forPAstExp");
				String errorMsg = "Binary Operator Error at line " + pos.getLine(temp);
				throw new TypeException(errorMsg);
			} else if (leftType.is(rightType) && leftType.is(Type.STRING)) {
				if (binOp.equals("==")) {
					return Type.BOOL;
				} else if (binOp.equals("!=")) {
					return Type.BOOL;
				} else if (binOp.equals("<")) {
					return Type.BOOL;
				} else if (binOp.equals("<=")) {
					return Type.BOOL;
				} else if (binOp.equals(">")) {
					return Type.BOOL;
				} else if (binOp.equals(">=")) {
					return Type.BOOL;
				} else if (binOp.equals("+")) {
					return Type.STRING;
				}
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(temp);
				System.out.println("In forPAstExp");
				String errorMsg = "Binary Operator Error at line " + pos.getLine(temp);
				throw new TypeException(errorMsg);
			} else if (leftType.is(rightType) && leftType.is(Type.FLOAT64)) {
				if (binOp.equals("==")) {
					return Type.BOOL;
				} else if (binOp.equals("!=")) {
					return Type.BOOL;
				} else if (binOp.equals("<")) {
					return Type.BOOL;
				} else if (binOp.equals("<=")) {
					return Type.BOOL;
				} else if (binOp.equals(">")) {
					return Type.BOOL;
				} else if (binOp.equals(">=")) {
					return Type.BOOL;
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
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(temp);
				System.out.println("In forPAstExp");
				String errorMsg = "Binary Operator Error at line " + pos.getLine(temp);
				throw new TypeException(errorMsg);
			} else if (leftType.is(rightType) && leftType.is(Type.INT)) {
				if (binOp.equals("==")) {
					return Type.BOOL;
				} else if (binOp.equals("!=")) {
					return Type.BOOL;
				} else if (binOp.equals("<")) {
					return Type.BOOL;
				} else if (binOp.equals("<=")) {
					return Type.BOOL;
				} else if (binOp.equals(">")) {
					return Type.BOOL;
				} else if (binOp.equals(">=")) {
					return Type.BOOL;
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
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(temp);
				System.out.println("In forPAstExp");
				String errorMsg = "Binary Operator Error at line " + pos.getLine(temp);
				throw new TypeException(errorMsg);
			} else {
				printSymbolTable();
				Position pos = new Position();
				pos.defaultCase(temp);
				System.out.println("In forPAstExp");
				String errorMsg = "Binary Operator Error at line " + pos.getLine(temp);
				throw new TypeException(errorMsg);
			}
		// TODO: implement the cases below
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
	public Type forPAstLiteral(PAstLiteral node) {
		if (node instanceof AIntAstLiteral) {
			return Type.INT;
		} else if (node instanceof AFloatAstLiteral) {
			return Type.FLOAT64;
		} else if (node instanceof ARuneAstLiteral) {
			return Type.RUNE;
		} else if (node instanceof AStringAstLiteral) {
			return Type.STRING;
		} else if (node instanceof ABoolAstLiteral) {
			return Type.BOOL;
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
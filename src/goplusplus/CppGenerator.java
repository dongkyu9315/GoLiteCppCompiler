package goplusplus;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import type.*;
import goplusplus.analysis.DepthFirstAdapter;
import goplusplus.node.*;

public class CppGenerator extends DepthFirstAdapter{
	public LinkedList<HashMap<String, Type>> symbolTable;
	private Position pos;
	FileWriter mFileWriter;
	
	public CppGenerator(String filename, Position p) {
		symbolTable = new LinkedList<HashMap<String, Type> >();
		symbolTable.addFirst(new HashMap<String, Type>());
		pos = p;
		try {
			mFileWriter = new FileWriter(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	// ast_program		---------------------------------------------------
	@Override
	public void caseAAstProgram(AAstProgram node) {
		LinkedList<PAstDecl> decl = node.getDecl();
		if (!decl.isEmpty()) {
			//first pass to collect root level vars and functions			
			for (PAstDecl d : decl) {
				if (d instanceof AFuncDecAstDecl) {
					addFunction((AAstFuncDecl) ((AFuncDecAstDecl)d).getAstFuncDecl());
				} else {
					d.apply(this);
				}
			}

			for (PAstDecl d : decl) {
				if (d instanceof AFuncDecAstDecl) {
					d.apply(this);
				}
			}
		}
	}


	// ast_var_decl		---------------------------------------------------
	@Override
	public void caseATypeAstVarDecl(ATypeAstVarDecl node) {
		LinkedList<TId> idlist = node.getId();
		Type varType = forPAstTypeExp(node.getAstTypeExp());
		for (TId d : idlist) {
			symbolTable.getFirst().put(d.getText().trim(), varType);
		}
	}

	@Override
	public void caseAExpAstVarDecl(AExpAstVarDecl node) {
		LinkedList<TId> idlist = node.getId();
		LinkedList<PAstExp> exps = node.getAstExp();

		for (int i = 0; i < idlist.size(); i++) {
			TId d = idlist.get(i);
			Type varType = forPAstExp(exps.get(i));
			symbolTable.getFirst().put(d.getText().trim(), varType);
		}
	}

	@Override
	public void caseATypeExpAstVarDecl(ATypeExpAstVarDecl node) {
		LinkedList<TId> idlist = node.getId();
		Type typeExp = forPAstTypeExp(node.getAstTypeExp());
		LinkedList<PAstExp> exps = node.getAstExp();

		for (int i = 0; i < idlist.size(); i++) {
			TId d = idlist.get(i);
			Type varType = forPAstExp(exps.get(i));
			symbolTable.getFirst().put(d.getText().trim(), varType);
		}
	}

	// ast_type_decl	---------------------------------------------------
	@Override
	public void caseAAstTypeDecl(AAstTypeDecl node) {
		LinkedList<TId> idlist = node.getId();
		Type varType = forPAstTypeExp(node.getAstTypeExp());
		for (TId d : idlist) {
			AliasType aType = new AliasType();
			aType.type = varType;
			symbolTable.getFirst().put(d.getText().trim(), aType);

			// for the fields of a struct
			if (varType.is(Type.STRUCT)) {
				AStructAstTypeExp tempNode = (AStructAstTypeExp) node.getAstTypeExp();
				LinkedList<PAstStructField> fieldList = tempNode.getAstStructField();
				for (PAstStructField f : fieldList) {
					AAstStructField tempField = (AAstStructField) f;
					LinkedList<TId> fieldIdList = tempField.getId();
					for (Iterator<TId> fieldIter = fieldIdList.iterator(); fieldIter.hasNext();) {
						TId field = (TId) fieldIter.next();
						String fieldName = d.getText().trim() + "." + field.toString().trim();
						Type fieldType = forPAstTypeExp(tempField.getAstTypeExp());
						symbolTable.getFirst().put(fieldName, fieldType);
					}
				}
			}
		}
	}

	
	public void inAAstFuncDecl(AAstFuncDecl node) {
		HashMap<String, Type> newScope = new HashMap<String, Type>();
		symbolTable.addFirst(newScope);
		
	}
	
	public void outAAstFuncDecl(AAstFuncDecl node) {
		symbolTable.removeFirst();
	}

	public void addFunction(AAstFuncDecl node) {
		FunctionType funcType = new FunctionType();
		Type returnType;
		if (node.getAstTypeExp() != null) {
			returnType = forPAstTypeExp(node.getAstTypeExp());
		} else {
			returnType = Type.VOID;
		}

		funcType.returnType = returnType;

		LinkedList<PAstFuncParam> params = node.getAstFuncParam();
		ArrayList<Type> paramTypes = new ArrayList<Type>();
		for (Iterator<PAstFuncParam> iterator = params.iterator(); iterator.hasNext();) {
			PAstFuncParam param = (PAstFuncParam) iterator.next();
			Type pType = forPAstFuncParam(param);
			for (int i = 0; i < ((AAstFuncParam)param).getId().size(); i++)
				paramTypes.add(pType);
		}

		funcType.paramType = paramTypes;

		symbolTable.getFirst().put(node.getId().getText().trim(), funcType);
	}


	// ast_func_param	---------------------------------------------------
	public Type forPAstFuncParam(PAstFuncParam node) {
		if (node.getClass().isInstance(new AAstFuncParam())) {
			AAstFuncParam temp = (AAstFuncParam) node;
			return forPAstTypeExp(temp.getAstTypeExp());
		}
		return null;
	}

	@Override
	public void caseAAstFuncParam(AAstFuncParam node) {
		LinkedList<TId> idlist = node.getId();
		Type varType = forPAstTypeExp(node.getAstTypeExp());
		for (Iterator<TId> iterator = idlist.iterator(); iterator.hasNext();) {
			TId d = (TId) iterator.next();
			symbolTable.getFirst().put(d.getText().trim(), varType);
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
			arrayType.size = Integer.parseInt(temp.getSize().toString().trim());
			return arrayType;
		} else if (node.getClass().isInstance(new AStructAstTypeExp())) {
			AStructAstTypeExp temp = (AStructAstTypeExp) node;
			LinkedList<PAstStructField> structList = temp.getAstStructField();
			HashMap<String, Type> typesList = new HashMap<String, Type>();
			for (Iterator<PAstStructField> iterator = structList.iterator(); iterator.hasNext();) {
				AAstStructField field = (AAstStructField) iterator.next();
				Type eleType = forPAstTypeExp(field.getAstTypeExp());
				LinkedList<TId> idList = field.getId();
				for (Iterator<TId> iter = idList.iterator(); iter.hasNext();) {
					typesList.put(iter.next().getText().trim(), eleType);
				}
			}
			StructType structType = new StructType();
			structType.attributes = typesList;
			return structType;
		} else if (node.getClass().isInstance(new AAliasAstTypeExp())) {
			AAliasAstTypeExp temp = (AAliasAstTypeExp) node;
			for (int i = 0; i < symbolTable.size(); i++) {
				if (symbolTable.get(i).containsKey(temp.getId().getText().trim())) {
					Type attType = symbolTable.get(i).get(temp.getId().getText().trim());
					if (attType.is(Type.ALIAS)) {
						return ((AliasType) attType).type;
					}
				}
			}
		}
		return null;
	}

	@Override
	public void caseAShortDeclAstStm(AShortDeclAstStm node) {
		AShortDeclAstStm temp = (AShortDeclAstStm) node;
		LinkedList<PAstExp> leftList = temp.getIds();
		LinkedList<PAstExp> rightList = temp.getAstExp();
		// TODO  add code gen stuff
	}

	// return true if the variable on the left is in the current scope
	public boolean helperForShortDecl(PAstExp node, Type rightType) {
		if (node.getClass().isInstance(new AParenAstExp())) {
			AParenAstExp temp = (AParenAstExp) node;
			return helperForShortDecl(temp.getAstExp(), rightType);
		} else if (node.getClass().isInstance(new AIdAstExp())) {
			AIdAstExp temp = (AIdAstExp) node;
			if (!symbolTable.getFirst().containsKey(temp.getId().getText().trim())) {
				symbolTable.getFirst().put(temp.getId().getText().trim(), rightType);
				return false;
			}
			return true;
		} else if (node.getClass().isInstance(new AArrayAccessAstExp())) {
			return true;
		} else if (node.getClass().isInstance(new AFieldAccessAstExp())) {
			return true;
		}
		return false;
	}


	public void inAShortifAstStm(AShortifAstStm node) {
		HashMap<String, Type> newScope = new HashMap<String, Type>();
		symbolTable.addFirst(newScope);
	}
	
	public void outAShortifAstStm(AShortifAstStm node) {
		symbolTable.removeFirst();
	}

	public void inALongifAstStm(ALongifAstStm node) {
		HashMap<String, Type> newScope = new HashMap<String, Type>();
		symbolTable.addFirst(newScope);
	}

	public void outALongifAstStm(ALongifAstStm node) {
		symbolTable.removeFirst();
	}
	

	public void inASwitchAstStm(ASwitchAstStm node) {
		HashMap<String, Type> newScope = new HashMap<String, Type>();
		symbolTable.addFirst(newScope);
	}
	
	public void outASwitchAstStm(ASwitchAstStm node) {
		symbolTable.removeFirst();
	}
	
	public void inAForAstStm(AForAstStm node) {
		HashMap<String, Type> newScope = new HashMap<String, Type>();
		symbolTable.addFirst(newScope);
	}
	
	public void outAForAstStm(AForAstStm node) {
		symbolTable.removeFirst();
	}

	
	public void inABlockAstStm(ABlockAstStm node) {
		HashMap<String, Type> newScope = new HashMap<String, Type>();
		symbolTable.addFirst(newScope);
	}
	
	public void outABlockAstStm(ABlockAstStm node) {
		symbolTable.removeFirst();
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
					return symbolTable.get(i).get(temp.getId().getText().trim());
				}
			}
		} else if (node.getClass().isInstance(new ALitAstExp())) {
			ALitAstExp temp = (ALitAstExp) node;
			return forPAstLiteral(temp.getAstLiteral());
		} else if (node.getClass().isInstance(new AUnaryOpAstExp())) {
			AUnaryOpAstExp temp = (AUnaryOpAstExp) node;
			String unop = forPAstUnaryOp(temp.getAstUnaryOp());
			if (unop.equals("+")) {
				Type check = forPAstExp(temp.getAstExp());
				return check; 
			} else if (unop.equals("-")) {
				Type check = forPAstExp(temp.getAstExp());
				return check; 
			} else if (unop.equals("!")) {
				Type check = forPAstExp(temp.getAstExp());
				return check; 
			} else if (unop.equals("^")) {
				Type check = forPAstExp(temp.getAstExp());
				return check; 
			} 
		} else if (node.getClass().isInstance(new ABinaryOpAstExp())) {
			ABinaryOpAstExp temp = (ABinaryOpAstExp) node;
			Type leftType = forPAstExp(temp.getLeft());
			String binOp = forPAstBinaryOp(temp.getAstBinaryOp());
			Type rightType = forPAstExp(temp.getRight());

			if (leftType.assign(Type.BOOL) && rightType.assign(Type.BOOL)) {
				if (binOp.equals("||")) {
					return Type.BOOL;
				} else if (binOp.equals("&&")) {
					return Type.BOOL;
				}
			} else if (leftType.assign(Type.INT) && rightType.assign(Type.INT)) {
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
			} else if (leftType.assign(Type.STRING) && rightType.assign(Type.STRING)) {
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
			} else if (leftType.assign(Type.FLOAT64) && rightType.assign(Type.FLOAT64)) {
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
			} else if (leftType.assign(Type.RUNE) && rightType.assign(Type.RUNE)) {
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
			} else {
			}
		} else if (node.getClass().isInstance(new AFuncCallAstExp())) {
			AFuncCallAstExp temp = (AFuncCallAstExp) node;
			Type type = forPAstExp(temp.getName());
			if (type.is(Type.FUNC)) {
				FunctionType funcType = (FunctionType) type;
				LinkedList<PAstExp> args = temp.getArgs();
				ArrayList<Type> params = funcType.paramType;
				boolean match = true;
				if (args.size() == params.size()) {
					Iterator<PAstExp> it = args.iterator();
					for (int i = 0; i < params.size() && it.hasNext(); i++) {
						PAstExp exp = it.next();
						Type argType = forPAstExp(exp);
						if (!params.get(i).assign(argType))	{
							match = false;
							break;
						}
					}
					if (match) {
						return funcType.returnType;
					}
				}
			} else if (temp.getName() instanceof AIdAstExp){
				String id = ((AIdAstExp) temp.getName()).getId().getText().trim();
				for (int i = 0; i < symbolTable.size(); i++) {
					if (symbolTable.get(i).containsKey(id)) {
						Type t = symbolTable.get(i).get(id);
						if (t.is(Type.ALIAS)) {
							return ((AliasType)t).type;
						} else {
						}
					}
				}
			}
		} else if (node.getClass().isInstance(new AAppendAstExp())) {
			AAppendAstExp temp = (AAppendAstExp) node;
			String id = temp.getId().getText().trim();
			for (Iterator<HashMap<String, Type>> iterator = symbolTable.iterator(); iterator.hasNext();) {
				HashMap<String, Type> table = (HashMap<String, Type>) iterator.next();
				if (table.containsKey(id)) {
					Type sliceType = table.get(id);
					Type astType = forPAstExp(temp.getAstExp());
					AppendType appendType = new AppendType();
					appendType.type = astType;
					if (sliceType.assign(appendType)) {
						return appendType;
					}
				}
			}
		} else if (node.getClass().isInstance(new ABasicCastAstExp())) {
			ABasicCastAstExp temp = (ABasicCastAstExp) node;
			Type basic = forPAstTypeExp(new ABasicAstTypeExp(temp.getBasicTypes()));
			Type other = forPAstExp(temp.getAstExp());
			if (basic.is(Type.INT)) {
				if (other.is(Type.FLOAT64) || other.is(Type.INT) || other.is(Type.RUNE)) {
					return basic;
				}
			} else if (basic.is(Type.FLOAT64)) {
				if (other.is(Type.INT) || other.is(Type.FLOAT64) || other.is(Type.RUNE)) {
					return basic;
				}
			} else if (basic.is(Type.BOOL)) {
				if (other.is(Type.BOOL)) {
					return basic;
				}
			} else if (basic.is(Type.RUNE)) {
				if (other.is(Type.RUNE) || other.is(Type.INT) || other.is(Type.FLOAT64)) {
					return basic;
				}
			}
		} else if (node.getClass().isInstance(new AArrayAccessAstExp())) {
			AArrayAccessAstExp temp = (AArrayAccessAstExp) node;
			Type arrayType = forPAstExp(temp.getArray());
			if (arrayType.is(Type.ARRAY)) {
				Type indexType = forPAstExp(temp.getIndex());
				if (indexType.is(Type.INT)) {
					ArrayType arrT = (ArrayType) arrayType;
					int maxIndex = arrT.size;
					IntType intTypeIndex = (IntType) indexType;
					int index = intTypeIndex.value;
					if (index < maxIndex) {
						return intTypeIndex;
					}
				}
			} else if (arrayType.is(Type.SLICE)) {
				Type indexType = forPAstExp(temp.getIndex());
				if (indexType.is(Type.INT)) {
					IntType intTypeIndex = (IntType) indexType;
					return intTypeIndex;
				}
			}
		} else if (node.getClass().isInstance(new AFieldAccessAstExp())) {
			AFieldAccessAstExp temp = (AFieldAccessAstExp)node;
			Type t = forPAstExp(temp.getStruct());
			if (t.is(Type.STRUCT)) {
				StructType struct = (StructType)t;
				String field = temp.getField().getText().trim();
				if (struct.attributes.containsKey(field)) {
					return struct.attributes.get(field);
				}
			}
		}
		return null;
	}


	// ast_literal	---------------------------------------------------// returns the type of PAstLiteral
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
			return "!=";
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
			return "&=";
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

	// ast_fallthrough_stm	---------------------------------------------------
	// return fallthrough_stm of PAstFallthroughStm
	public String forPAstFallthroughStm(PAstFallthroughStm node) {
		if (node.getClass().isInstance(new AAstFallthroughStm())) {
			return "fallthrough";
		}
		return null;
	}
}

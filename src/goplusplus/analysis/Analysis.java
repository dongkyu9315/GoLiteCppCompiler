/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.analysis;

import goplusplus.node.*;

public interface Analysis extends Switch
{
    Object getIn(Node node);
    void setIn(Node node, Object o);
    Object getOut(Node node);
    void setOut(Node node, Object o);

    void caseStart(Start node);
    void caseAAstProgram(AAstProgram node);
    void caseAVarDecAstDecl(AVarDecAstDecl node);
    void caseATypeDecAstDecl(ATypeDecAstDecl node);
    void caseAFuncDecAstDecl(AFuncDecAstDecl node);
    void caseATypeAstVarDecl(ATypeAstVarDecl node);
    void caseAExpAstVarDecl(AExpAstVarDecl node);
    void caseATypeExpAstVarDecl(ATypeExpAstVarDecl node);
    void caseAAstTypeDecl(AAstTypeDecl node);
    void caseAAstFuncDecl(AAstFuncDecl node);
    void caseAAstFuncParam(AAstFuncParam node);
    void caseABasicAstTypeExp(ABasicAstTypeExp node);
    void caseASliceAstTypeExp(ASliceAstTypeExp node);
    void caseAArrayAstTypeExp(AArrayAstTypeExp node);
    void caseAStructAstTypeExp(AStructAstTypeExp node);
    void caseAAliasAstTypeExp(AAliasAstTypeExp node);
    void caseAAstStructField(AAstStructField node);
    void caseAEmptyAstStm(AEmptyAstStm node);
    void caseAExpAstStm(AExpAstStm node);
    void caseAAssignAstStm(AAssignAstStm node);
    void caseAOpAssignAstStm(AOpAssignAstStm node);
    void caseAVarDeclAstStm(AVarDeclAstStm node);
    void caseAShortDeclAstStm(AShortDeclAstStm node);
    void caseATypeDeclAstStm(ATypeDeclAstStm node);
    void caseAIncDecAstStm(AIncDecAstStm node);
    void caseAPrintAstStm(APrintAstStm node);
    void caseAPrintlnAstStm(APrintlnAstStm node);
    void caseAReturnAstStm(AReturnAstStm node);
    void caseAShortifAstStm(AShortifAstStm node);
    void caseALongifAstStm(ALongifAstStm node);
    void caseASwitchAstStm(ASwitchAstStm node);
    void caseAForAstStm(AForAstStm node);
    void caseABlockAstStm(ABlockAstStm node);
    void caseABreakAstStm(ABreakAstStm node);
    void caseAContinueAstStm(AContinueAstStm node);
    void caseAParenAstExp(AParenAstExp node);
    void caseAIdAstExp(AIdAstExp node);
    void caseALitAstExp(ALitAstExp node);
    void caseAUnaryOpAstExp(AUnaryOpAstExp node);
    void caseABinaryOpAstExp(ABinaryOpAstExp node);
    void caseAFuncCallAstExp(AFuncCallAstExp node);
    void caseAAppendAstExp(AAppendAstExp node);
    void caseABasicCastAstExp(ABasicCastAstExp node);
    void caseAArrayAccessAstExp(AArrayAccessAstExp node);
    void caseAFieldAccessAstExp(AFieldAccessAstExp node);
    void caseAAstSwitchStm(AAstSwitchStm node);
    void caseADefaultAstSwitchCase(ADefaultAstSwitchCase node);
    void caseACaseAstSwitchCase(ACaseAstSwitchCase node);
    void caseAIntAstLiteral(AIntAstLiteral node);
    void caseAFloatAstLiteral(AFloatAstLiteral node);
    void caseARuneAstLiteral(ARuneAstLiteral node);
    void caseAStringAstLiteral(AStringAstLiteral node);
    void caseABoolAstLiteral(ABoolAstLiteral node);
    void caseAAddAstBinaryOp(AAddAstBinaryOp node);
    void caseASubAstBinaryOp(ASubAstBinaryOp node);
    void caseAMulAstBinaryOp(AMulAstBinaryOp node);
    void caseADivAstBinaryOp(ADivAstBinaryOp node);
    void caseAModAstBinaryOp(AModAstBinaryOp node);
    void caseABitorAstBinaryOp(ABitorAstBinaryOp node);
    void caseABitandAstBinaryOp(ABitandAstBinaryOp node);
    void caseAEqAstBinaryOp(AEqAstBinaryOp node);
    void caseANoteqAstBinaryOp(ANoteqAstBinaryOp node);
    void caseALtAstBinaryOp(ALtAstBinaryOp node);
    void caseALeqAstBinaryOp(ALeqAstBinaryOp node);
    void caseAGtAstBinaryOp(AGtAstBinaryOp node);
    void caseAGeqAstBinaryOp(AGeqAstBinaryOp node);
    void caseACaretAstBinaryOp(ACaretAstBinaryOp node);
    void caseALshiftAstBinaryOp(ALshiftAstBinaryOp node);
    void caseARshiftAstBinaryOp(ARshiftAstBinaryOp node);
    void caseABitclearAstBinaryOp(ABitclearAstBinaryOp node);
    void caseAOrAstBinaryOp(AOrAstBinaryOp node);
    void caseAAndAstBinaryOp(AAndAstBinaryOp node);
    void caseAAddEqAstOpAssign(AAddEqAstOpAssign node);
    void caseASubEqAstOpAssign(ASubEqAstOpAssign node);
    void caseAMulEqAstOpAssign(AMulEqAstOpAssign node);
    void caseADivEqAstOpAssign(ADivEqAstOpAssign node);
    void caseAModEqAstOpAssign(AModEqAstOpAssign node);
    void caseABitorEqAstOpAssign(ABitorEqAstOpAssign node);
    void caseABitandEqAstOpAssign(ABitandEqAstOpAssign node);
    void caseACaretEqAstOpAssign(ACaretEqAstOpAssign node);
    void caseALshiftEqAstOpAssign(ALshiftEqAstOpAssign node);
    void caseARshiftEqAstOpAssign(ARshiftEqAstOpAssign node);
    void caseABitclearEqAstOpAssign(ABitclearEqAstOpAssign node);
    void caseAPlusAstUnaryOp(APlusAstUnaryOp node);
    void caseAMinusAstUnaryOp(AMinusAstUnaryOp node);
    void caseANotAstUnaryOp(ANotAstUnaryOp node);
    void caseACaretAstUnaryOp(ACaretAstUnaryOp node);
    void caseAIncAstPostOp(AIncAstPostOp node);
    void caseADecAstPostOp(ADecAstPostOp node);
    void caseAAstFallthroughStm(AAstFallthroughStm node);

    void caseTEol(TEol node);
    void caseTBlank(TBlank node);
    void caseTBreak(TBreak node);
    void caseTDefault(TDefault node);
    void caseTFunc(TFunc node);
    void caseTInterface(TInterface node);
    void caseTSelect(TSelect node);
    void caseTCase(TCase node);
    void caseTDefer(TDefer node);
    void caseTGo(TGo node);
    void caseTMap(TMap node);
    void caseTStruct(TStruct node);
    void caseTChan(TChan node);
    void caseTElse(TElse node);
    void caseTGoto(TGoto node);
    void caseTPackage(TPackage node);
    void caseTSwitch(TSwitch node);
    void caseTConst(TConst node);
    void caseTFallthrough(TFallthrough node);
    void caseTIf(TIf node);
    void caseTRange(TRange node);
    void caseTType(TType node);
    void caseTContinue(TContinue node);
    void caseTFor(TFor node);
    void caseTImport(TImport node);
    void caseTReturn(TReturn node);
    void caseTVar(TVar node);
    void caseTBasicTypes(TBasicTypes node);
    void caseTPrint(TPrint node);
    void caseTPrintln(TPrintln node);
    void caseTAppend(TAppend node);
    void caseTBoolLit(TBoolLit node);
    void caseTAdd(TAdd node);
    void caseTSub(TSub node);
    void caseTMul(TMul node);
    void caseTDiv(TDiv node);
    void caseTMod(TMod node);
    void caseTBitAnd(TBitAnd node);
    void caseTBitOr(TBitOr node);
    void caseTCaret(TCaret node);
    void caseTLshift(TLshift node);
    void caseTRshift(TRshift node);
    void caseTBitclear(TBitclear node);
    void caseTPluseq(TPluseq node);
    void caseTSubeq(TSubeq node);
    void caseTMuleq(TMuleq node);
    void caseTDiveq(TDiveq node);
    void caseTModeq(TModeq node);
    void caseTAndeq(TAndeq node);
    void caseTOreq(TOreq node);
    void caseTXoreq(TXoreq node);
    void caseTLshifteq(TLshifteq node);
    void caseTRshifteq(TRshifteq node);
    void caseTBitcleareq(TBitcleareq node);
    void caseTAnd(TAnd node);
    void caseTOr(TOr node);
    void caseTReceive(TReceive node);
    void caseTIncrement(TIncrement node);
    void caseTDecrement(TDecrement node);
    void caseTEq(TEq node);
    void caseTLt(TLt node);
    void caseTGt(TGt node);
    void caseTAssign(TAssign node);
    void caseTNot(TNot node);
    void caseTNoteq(TNoteq node);
    void caseTLeq(TLeq node);
    void caseTGeq(TGeq node);
    void caseTShortdecl(TShortdecl node);
    void caseTVarargs(TVarargs node);
    void caseTLeftpar(TLeftpar node);
    void caseTLeftsq(TLeftsq node);
    void caseTLeftcurl(TLeftcurl node);
    void caseTComma(TComma node);
    void caseTDot(TDot node);
    void caseTRightpar(TRightpar node);
    void caseTRightsq(TRightsq node);
    void caseTRightcurl(TRightcurl node);
    void caseTSemi(TSemi node);
    void caseTColon(TColon node);
    void caseTLineComment(TLineComment node);
    void caseTBlockComment(TBlockComment node);
    void caseTIntLit(TIntLit node);
    void caseTFloatLit(TFloatLit node);
    void caseTRuneLit(TRuneLit node);
    void caseTStringLit(TStringLit node);
    void caseTId(TId node);
    void caseEOF(EOF node);
    void caseInvalidToken(InvalidToken node);
}

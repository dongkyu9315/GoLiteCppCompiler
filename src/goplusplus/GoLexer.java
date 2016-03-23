package goplusplus;

import goplusplus.lexer.Lexer;
import goplusplus.node.*;
import goplusplus.node.Token;

public class GoLexer extends Lexer {
	private Token last_token = null;

    public GoLexer(java.io.PushbackReader in) {
            super(in);
    }

    private boolean requires_semicolon() {
            return
	    ((token instanceof TEol || token instanceof EOF) &&
	    (last_token instanceof TId
		|| last_token instanceof TIntLit
		|| last_token instanceof TFloatLit
		|| last_token instanceof TStringLit
		|| last_token instanceof TRuneLit
		|| last_token instanceof TBoolLit
		
		|| last_token instanceof TBreak
		|| last_token instanceof TContinue
		|| last_token instanceof TFallthrough
		|| last_token instanceof TReturn
		
		|| last_token instanceof TIncrement
		|| last_token instanceof TDecrement
		
		|| last_token instanceof TRightpar
		|| last_token instanceof TRightsq
		|| last_token instanceof TRightcurl
		
		|| last_token instanceof TBasicTypes));
    }

    protected void filter() {
            if (requires_semicolon()){
                    token = new TSemi();
                    token.setLine(last_token.getLine());
                    token.setPos(last_token.getPos());
            }
            if(!( token ==null
            	||token instanceof TBlank
            	|| token instanceof TBlockComment
            	|| token instanceof TLineComment
            	|| token instanceof TEol)){
            	last_token = token;
            }
    }
}

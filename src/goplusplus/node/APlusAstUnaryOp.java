/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class APlusAstUnaryOp extends PAstUnaryOp
{

    public APlusAstUnaryOp()
    {
        // Constructor
    }

    @Override
    public Object clone()
    {
        return new APlusAstUnaryOp();
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAPlusAstUnaryOp(this);
    }

    @Override
    public String toString()
    {
        return "";
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        throw new RuntimeException("Not a child.");
    }
}

/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class ACaretEqAstOpAssign extends PAstOpAssign
{

    public ACaretEqAstOpAssign()
    {
        // Constructor
    }

    @Override
    public Object clone()
    {
        return new ACaretEqAstOpAssign();
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseACaretEqAstOpAssign(this);
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

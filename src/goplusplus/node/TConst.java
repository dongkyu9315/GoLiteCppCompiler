/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class TConst extends Token
{
    public TConst()
    {
        super.setText("const");
    }

    public TConst(int line, int pos)
    {
        super.setText("const");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TConst(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTConst(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TConst text.");
    }
}

/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class TShortdecl extends Token
{
    public TShortdecl()
    {
        super.setText(":=");
    }

    public TShortdecl(int line, int pos)
    {
        super.setText(":=");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TShortdecl(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTShortdecl(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TShortdecl text.");
    }
}

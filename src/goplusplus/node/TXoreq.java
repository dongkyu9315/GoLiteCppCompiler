/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class TXoreq extends Token
{
    public TXoreq()
    {
        super.setText("^=");
    }

    public TXoreq(int line, int pos)
    {
        super.setText("^=");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TXoreq(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTXoreq(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TXoreq text.");
    }
}

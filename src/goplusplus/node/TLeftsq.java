/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class TLeftsq extends Token
{
    public TLeftsq()
    {
        super.setText("[");
    }

    public TLeftsq(int line, int pos)
    {
        super.setText("[");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TLeftsq(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTLeftsq(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TLeftsq text.");
    }
}

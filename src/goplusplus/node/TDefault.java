/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class TDefault extends Token
{
    public TDefault()
    {
        super.setText("default");
    }

    public TDefault(int line, int pos)
    {
        super.setText("default");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TDefault(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTDefault(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TDefault text.");
    }
}

/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class TBitclear extends Token
{
    public TBitclear()
    {
        super.setText("&^");
    }

    public TBitclear(int line, int pos)
    {
        super.setText("&^");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TBitclear(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTBitclear(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TBitclear text.");
    }
}

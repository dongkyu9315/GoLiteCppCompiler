/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class TBasicTypes extends Token
{
    public TBasicTypes(String text)
    {
        setText(text);
    }

    public TBasicTypes(String text, int line, int pos)
    {
        setText(text);
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TBasicTypes(getText(), getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTBasicTypes(this);
    }
}

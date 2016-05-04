/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class ARuneAstLiteral extends PAstLiteral
{
    private TRuneLit _runeLit_;

    public ARuneAstLiteral()
    {
        // Constructor
    }

    public ARuneAstLiteral(
        @SuppressWarnings("hiding") TRuneLit _runeLit_)
    {
        // Constructor
        setRuneLit(_runeLit_);

    }

    @Override
    public Object clone()
    {
        return new ARuneAstLiteral(
            cloneNode(this._runeLit_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseARuneAstLiteral(this);
    }

    public TRuneLit getRuneLit()
    {
        return this._runeLit_;
    }

    public void setRuneLit(TRuneLit node)
    {
        if(this._runeLit_ != null)
        {
            this._runeLit_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._runeLit_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._runeLit_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._runeLit_ == child)
        {
            this._runeLit_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._runeLit_ == oldChild)
        {
            setRuneLit((TRuneLit) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
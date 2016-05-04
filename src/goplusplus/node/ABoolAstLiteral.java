/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class ABoolAstLiteral extends PAstLiteral
{
    private TBoolLit _boolLit_;

    public ABoolAstLiteral()
    {
        // Constructor
    }

    public ABoolAstLiteral(
        @SuppressWarnings("hiding") TBoolLit _boolLit_)
    {
        // Constructor
        setBoolLit(_boolLit_);

    }

    @Override
    public Object clone()
    {
        return new ABoolAstLiteral(
            cloneNode(this._boolLit_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseABoolAstLiteral(this);
    }

    public TBoolLit getBoolLit()
    {
        return this._boolLit_;
    }

    public void setBoolLit(TBoolLit node)
    {
        if(this._boolLit_ != null)
        {
            this._boolLit_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._boolLit_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._boolLit_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._boolLit_ == child)
        {
            this._boolLit_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._boolLit_ == oldChild)
        {
            setBoolLit((TBoolLit) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class ASliceAstTypeExp extends PAstTypeExp
{
    private PAstTypeExp _astTypeExp_;

    public ASliceAstTypeExp()
    {
        // Constructor
    }

    public ASliceAstTypeExp(
        @SuppressWarnings("hiding") PAstTypeExp _astTypeExp_)
    {
        // Constructor
        setAstTypeExp(_astTypeExp_);

    }

    @Override
    public Object clone()
    {
        return new ASliceAstTypeExp(
            cloneNode(this._astTypeExp_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseASliceAstTypeExp(this);
    }

    public PAstTypeExp getAstTypeExp()
    {
        return this._astTypeExp_;
    }

    public void setAstTypeExp(PAstTypeExp node)
    {
        if(this._astTypeExp_ != null)
        {
            this._astTypeExp_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._astTypeExp_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._astTypeExp_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._astTypeExp_ == child)
        {
            this._astTypeExp_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._astTypeExp_ == oldChild)
        {
            setAstTypeExp((PAstTypeExp) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
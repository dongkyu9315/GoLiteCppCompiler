/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class ABasicCastAstExp extends PAstExp
{
    private TBasicTypes _basicTypes_;
    private PAstExp _astExp_;

    public ABasicCastAstExp()
    {
        // Constructor
    }

    public ABasicCastAstExp(
        @SuppressWarnings("hiding") TBasicTypes _basicTypes_,
        @SuppressWarnings("hiding") PAstExp _astExp_)
    {
        // Constructor
        setBasicTypes(_basicTypes_);

        setAstExp(_astExp_);

    }

    @Override
    public Object clone()
    {
        return new ABasicCastAstExp(
            cloneNode(this._basicTypes_),
            cloneNode(this._astExp_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseABasicCastAstExp(this);
    }

    public TBasicTypes getBasicTypes()
    {
        return this._basicTypes_;
    }

    public void setBasicTypes(TBasicTypes node)
    {
        if(this._basicTypes_ != null)
        {
            this._basicTypes_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._basicTypes_ = node;
    }

    public PAstExp getAstExp()
    {
        return this._astExp_;
    }

    public void setAstExp(PAstExp node)
    {
        if(this._astExp_ != null)
        {
            this._astExp_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._astExp_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._basicTypes_)
            + toString(this._astExp_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._basicTypes_ == child)
        {
            this._basicTypes_ = null;
            return;
        }

        if(this._astExp_ == child)
        {
            this._astExp_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._basicTypes_ == oldChild)
        {
            setBasicTypes((TBasicTypes) newChild);
            return;
        }

        if(this._astExp_ == oldChild)
        {
            setAstExp((PAstExp) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}

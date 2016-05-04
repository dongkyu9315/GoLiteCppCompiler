/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class AOpAssignAstStm extends PAstStm
{
    private TId _l_;
    private PAstOpAssign _astOpAssign_;
    private PAstExp _r_;

    public AOpAssignAstStm()
    {
        // Constructor
    }

    public AOpAssignAstStm(
        @SuppressWarnings("hiding") TId _l_,
        @SuppressWarnings("hiding") PAstOpAssign _astOpAssign_,
        @SuppressWarnings("hiding") PAstExp _r_)
    {
        // Constructor
        setL(_l_);

        setAstOpAssign(_astOpAssign_);

        setR(_r_);

    }

    @Override
    public Object clone()
    {
        return new AOpAssignAstStm(
            cloneNode(this._l_),
            cloneNode(this._astOpAssign_),
            cloneNode(this._r_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAOpAssignAstStm(this);
    }

    public TId getL()
    {
        return this._l_;
    }

    public void setL(TId node)
    {
        if(this._l_ != null)
        {
            this._l_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._l_ = node;
    }

    public PAstOpAssign getAstOpAssign()
    {
        return this._astOpAssign_;
    }

    public void setAstOpAssign(PAstOpAssign node)
    {
        if(this._astOpAssign_ != null)
        {
            this._astOpAssign_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._astOpAssign_ = node;
    }

    public PAstExp getR()
    {
        return this._r_;
    }

    public void setR(PAstExp node)
    {
        if(this._r_ != null)
        {
            this._r_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._r_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._l_)
            + toString(this._astOpAssign_)
            + toString(this._r_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._l_ == child)
        {
            this._l_ = null;
            return;
        }

        if(this._astOpAssign_ == child)
        {
            this._astOpAssign_ = null;
            return;
        }

        if(this._r_ == child)
        {
            this._r_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._l_ == oldChild)
        {
            setL((TId) newChild);
            return;
        }

        if(this._astOpAssign_ == oldChild)
        {
            setAstOpAssign((PAstOpAssign) newChild);
            return;
        }

        if(this._r_ == oldChild)
        {
            setR((PAstExp) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class AArrayAstTypeExp extends PAstTypeExp
{
    private TIntLit _size_;
    private PAstTypeExp _astTypeExp_;

    public AArrayAstTypeExp()
    {
        // Constructor
    }

    public AArrayAstTypeExp(
        @SuppressWarnings("hiding") TIntLit _size_,
        @SuppressWarnings("hiding") PAstTypeExp _astTypeExp_)
    {
        // Constructor
        setSize(_size_);

        setAstTypeExp(_astTypeExp_);

    }

    @Override
    public Object clone()
    {
        return new AArrayAstTypeExp(
            cloneNode(this._size_),
            cloneNode(this._astTypeExp_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAArrayAstTypeExp(this);
    }

    public TIntLit getSize()
    {
        return this._size_;
    }

    public void setSize(TIntLit node)
    {
        if(this._size_ != null)
        {
            this._size_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._size_ = node;
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
            + toString(this._size_)
            + toString(this._astTypeExp_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._size_ == child)
        {
            this._size_ = null;
            return;
        }

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
        if(this._size_ == oldChild)
        {
            setSize((TIntLit) newChild);
            return;
        }

        if(this._astTypeExp_ == oldChild)
        {
            setAstTypeExp((PAstTypeExp) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}

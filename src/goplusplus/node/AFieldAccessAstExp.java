/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class AFieldAccessAstExp extends PAstExp
{
    private PAstExp _struct_;
    private TId _field_;

    public AFieldAccessAstExp()
    {
        // Constructor
    }

    public AFieldAccessAstExp(
        @SuppressWarnings("hiding") PAstExp _struct_,
        @SuppressWarnings("hiding") TId _field_)
    {
        // Constructor
        setStruct(_struct_);

        setField(_field_);

    }

    @Override
    public Object clone()
    {
        return new AFieldAccessAstExp(
            cloneNode(this._struct_),
            cloneNode(this._field_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAFieldAccessAstExp(this);
    }

    public PAstExp getStruct()
    {
        return this._struct_;
    }

    public void setStruct(PAstExp node)
    {
        if(this._struct_ != null)
        {
            this._struct_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._struct_ = node;
    }

    public TId getField()
    {
        return this._field_;
    }

    public void setField(TId node)
    {
        if(this._field_ != null)
        {
            this._field_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._field_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._struct_)
            + toString(this._field_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._struct_ == child)
        {
            this._struct_ = null;
            return;
        }

        if(this._field_ == child)
        {
            this._field_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._struct_ == oldChild)
        {
            setStruct((PAstExp) newChild);
            return;
        }

        if(this._field_ == oldChild)
        {
            setField((TId) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}

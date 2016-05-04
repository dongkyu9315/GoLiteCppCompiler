/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import java.util.*;
import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class AAstStructField extends PAstStructField
{
    private final LinkedList<TId> _id_ = new LinkedList<TId>();
    private PAstTypeExp _astTypeExp_;

    public AAstStructField()
    {
        // Constructor
    }

    public AAstStructField(
        @SuppressWarnings("hiding") List<?> _id_,
        @SuppressWarnings("hiding") PAstTypeExp _astTypeExp_)
    {
        // Constructor
        setId(_id_);

        setAstTypeExp(_astTypeExp_);

    }

    @Override
    public Object clone()
    {
        return new AAstStructField(
            cloneList(this._id_),
            cloneNode(this._astTypeExp_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAAstStructField(this);
    }

    public LinkedList<TId> getId()
    {
        return this._id_;
    }

    public void setId(List<?> list)
    {
        for(TId e : this._id_)
        {
            e.parent(null);
        }
        this._id_.clear();

        for(Object obj_e : list)
        {
            TId e = (TId) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._id_.add(e);
        }
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
            + toString(this._id_)
            + toString(this._astTypeExp_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._id_.remove(child))
        {
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
        for(ListIterator<TId> i = this._id_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((TId) newChild);
                    newChild.parent(this);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        if(this._astTypeExp_ == oldChild)
        {
            setAstTypeExp((PAstTypeExp) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}

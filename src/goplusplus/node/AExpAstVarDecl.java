/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import java.util.*;
import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class AExpAstVarDecl extends PAstVarDecl
{
    private final LinkedList<TId> _id_ = new LinkedList<TId>();
    private final LinkedList<PAstExp> _astExp_ = new LinkedList<PAstExp>();

    public AExpAstVarDecl()
    {
        // Constructor
    }

    public AExpAstVarDecl(
        @SuppressWarnings("hiding") List<?> _id_,
        @SuppressWarnings("hiding") List<?> _astExp_)
    {
        // Constructor
        setId(_id_);

        setAstExp(_astExp_);

    }

    @Override
    public Object clone()
    {
        return new AExpAstVarDecl(
            cloneList(this._id_),
            cloneList(this._astExp_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAExpAstVarDecl(this);
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

    public LinkedList<PAstExp> getAstExp()
    {
        return this._astExp_;
    }

    public void setAstExp(List<?> list)
    {
        for(PAstExp e : this._astExp_)
        {
            e.parent(null);
        }
        this._astExp_.clear();

        for(Object obj_e : list)
        {
            PAstExp e = (PAstExp) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._astExp_.add(e);
        }
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._id_)
            + toString(this._astExp_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._id_.remove(child))
        {
            return;
        }

        if(this._astExp_.remove(child))
        {
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

        for(ListIterator<PAstExp> i = this._astExp_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PAstExp) newChild);
                    newChild.parent(this);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        throw new RuntimeException("Not a child.");
    }
}

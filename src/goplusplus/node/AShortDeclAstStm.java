/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import java.util.*;
import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class AShortDeclAstStm extends PAstStm
{
    private final LinkedList<PAstExp> _ids_ = new LinkedList<PAstExp>();
    private final LinkedList<PAstExp> _astExp_ = new LinkedList<PAstExp>();

    public AShortDeclAstStm()
    {
        // Constructor
    }

    public AShortDeclAstStm(
        @SuppressWarnings("hiding") List<?> _ids_,
        @SuppressWarnings("hiding") List<?> _astExp_)
    {
        // Constructor
        setIds(_ids_);

        setAstExp(_astExp_);

    }

    @Override
    public Object clone()
    {
        return new AShortDeclAstStm(
            cloneList(this._ids_),
            cloneList(this._astExp_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAShortDeclAstStm(this);
    }

    public LinkedList<PAstExp> getIds()
    {
        return this._ids_;
    }

    public void setIds(List<?> list)
    {
        for(PAstExp e : this._ids_)
        {
            e.parent(null);
        }
        this._ids_.clear();

        for(Object obj_e : list)
        {
            PAstExp e = (PAstExp) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._ids_.add(e);
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
            + toString(this._ids_)
            + toString(this._astExp_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._ids_.remove(child))
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
        for(ListIterator<PAstExp> i = this._ids_.listIterator(); i.hasNext();)
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

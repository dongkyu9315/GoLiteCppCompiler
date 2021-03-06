/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import java.util.*;
import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class AVarDecAstDecl extends PAstDecl
{
    private final LinkedList<PAstVarDecl> _astVarDecl_ = new LinkedList<PAstVarDecl>();

    public AVarDecAstDecl()
    {
        // Constructor
    }

    public AVarDecAstDecl(
        @SuppressWarnings("hiding") List<?> _astVarDecl_)
    {
        // Constructor
        setAstVarDecl(_astVarDecl_);

    }

    @Override
    public Object clone()
    {
        return new AVarDecAstDecl(
            cloneList(this._astVarDecl_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAVarDecAstDecl(this);
    }

    public LinkedList<PAstVarDecl> getAstVarDecl()
    {
        return this._astVarDecl_;
    }

    public void setAstVarDecl(List<?> list)
    {
        for(PAstVarDecl e : this._astVarDecl_)
        {
            e.parent(null);
        }
        this._astVarDecl_.clear();

        for(Object obj_e : list)
        {
            PAstVarDecl e = (PAstVarDecl) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._astVarDecl_.add(e);
        }
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._astVarDecl_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._astVarDecl_.remove(child))
        {
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        for(ListIterator<PAstVarDecl> i = this._astVarDecl_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PAstVarDecl) newChild);
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

/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import java.util.*;
import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class AAstProgram extends PAstProgram
{
    private TId _package_;
    private final LinkedList<PAstDecl> _decl_ = new LinkedList<PAstDecl>();

    public AAstProgram()
    {
        // Constructor
    }

    public AAstProgram(
        @SuppressWarnings("hiding") TId _package_,
        @SuppressWarnings("hiding") List<?> _decl_)
    {
        // Constructor
        setPackage(_package_);

        setDecl(_decl_);

    }

    @Override
    public Object clone()
    {
        return new AAstProgram(
            cloneNode(this._package_),
            cloneList(this._decl_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAAstProgram(this);
    }

    public TId getPackage()
    {
        return this._package_;
    }

    public void setPackage(TId node)
    {
        if(this._package_ != null)
        {
            this._package_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._package_ = node;
    }

    public LinkedList<PAstDecl> getDecl()
    {
        return this._decl_;
    }

    public void setDecl(List<?> list)
    {
        for(PAstDecl e : this._decl_)
        {
            e.parent(null);
        }
        this._decl_.clear();

        for(Object obj_e : list)
        {
            PAstDecl e = (PAstDecl) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._decl_.add(e);
        }
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._package_)
            + toString(this._decl_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._package_ == child)
        {
            this._package_ = null;
            return;
        }

        if(this._decl_.remove(child))
        {
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._package_ == oldChild)
        {
            setPackage((TId) newChild);
            return;
        }

        for(ListIterator<PAstDecl> i = this._decl_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PAstDecl) newChild);
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

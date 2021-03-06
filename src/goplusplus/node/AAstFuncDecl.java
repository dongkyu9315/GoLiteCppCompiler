/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import java.util.*;
import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class AAstFuncDecl extends PAstFuncDecl
{
    private TId _id_;
    private final LinkedList<PAstFuncParam> _astFuncParam_ = new LinkedList<PAstFuncParam>();
    private PAstTypeExp _astTypeExp_;
    private final LinkedList<PAstStm> _astStm_ = new LinkedList<PAstStm>();

    public AAstFuncDecl()
    {
        // Constructor
    }

    public AAstFuncDecl(
        @SuppressWarnings("hiding") TId _id_,
        @SuppressWarnings("hiding") List<?> _astFuncParam_,
        @SuppressWarnings("hiding") PAstTypeExp _astTypeExp_,
        @SuppressWarnings("hiding") List<?> _astStm_)
    {
        // Constructor
        setId(_id_);

        setAstFuncParam(_astFuncParam_);

        setAstTypeExp(_astTypeExp_);

        setAstStm(_astStm_);

    }

    @Override
    public Object clone()
    {
        return new AAstFuncDecl(
            cloneNode(this._id_),
            cloneList(this._astFuncParam_),
            cloneNode(this._astTypeExp_),
            cloneList(this._astStm_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAAstFuncDecl(this);
    }

    public TId getId()
    {
        return this._id_;
    }

    public void setId(TId node)
    {
        if(this._id_ != null)
        {
            this._id_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._id_ = node;
    }

    public LinkedList<PAstFuncParam> getAstFuncParam()
    {
        return this._astFuncParam_;
    }

    public void setAstFuncParam(List<?> list)
    {
        for(PAstFuncParam e : this._astFuncParam_)
        {
            e.parent(null);
        }
        this._astFuncParam_.clear();

        for(Object obj_e : list)
        {
            PAstFuncParam e = (PAstFuncParam) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._astFuncParam_.add(e);
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

    public LinkedList<PAstStm> getAstStm()
    {
        return this._astStm_;
    }

    public void setAstStm(List<?> list)
    {
        for(PAstStm e : this._astStm_)
        {
            e.parent(null);
        }
        this._astStm_.clear();

        for(Object obj_e : list)
        {
            PAstStm e = (PAstStm) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._astStm_.add(e);
        }
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._id_)
            + toString(this._astFuncParam_)
            + toString(this._astTypeExp_)
            + toString(this._astStm_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._id_ == child)
        {
            this._id_ = null;
            return;
        }

        if(this._astFuncParam_.remove(child))
        {
            return;
        }

        if(this._astTypeExp_ == child)
        {
            this._astTypeExp_ = null;
            return;
        }

        if(this._astStm_.remove(child))
        {
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._id_ == oldChild)
        {
            setId((TId) newChild);
            return;
        }

        for(ListIterator<PAstFuncParam> i = this._astFuncParam_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PAstFuncParam) newChild);
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

        for(ListIterator<PAstStm> i = this._astStm_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PAstStm) newChild);
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

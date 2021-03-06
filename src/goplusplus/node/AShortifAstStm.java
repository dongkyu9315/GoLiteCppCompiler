/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import java.util.*;
import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class AShortifAstStm extends PAstStm
{
    private PAstStm _init_;
    private PAstExp _condition_;
    private final LinkedList<PAstStm> _astStm_ = new LinkedList<PAstStm>();

    public AShortifAstStm()
    {
        // Constructor
    }

    public AShortifAstStm(
        @SuppressWarnings("hiding") PAstStm _init_,
        @SuppressWarnings("hiding") PAstExp _condition_,
        @SuppressWarnings("hiding") List<?> _astStm_)
    {
        // Constructor
        setInit(_init_);

        setCondition(_condition_);

        setAstStm(_astStm_);

    }

    @Override
    public Object clone()
    {
        return new AShortifAstStm(
            cloneNode(this._init_),
            cloneNode(this._condition_),
            cloneList(this._astStm_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAShortifAstStm(this);
    }

    public PAstStm getInit()
    {
        return this._init_;
    }

    public void setInit(PAstStm node)
    {
        if(this._init_ != null)
        {
            this._init_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._init_ = node;
    }

    public PAstExp getCondition()
    {
        return this._condition_;
    }

    public void setCondition(PAstExp node)
    {
        if(this._condition_ != null)
        {
            this._condition_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._condition_ = node;
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
            + toString(this._init_)
            + toString(this._condition_)
            + toString(this._astStm_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._init_ == child)
        {
            this._init_ = null;
            return;
        }

        if(this._condition_ == child)
        {
            this._condition_ = null;
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
        if(this._init_ == oldChild)
        {
            setInit((PAstStm) newChild);
            return;
        }

        if(this._condition_ == oldChild)
        {
            setCondition((PAstExp) newChild);
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

/* This file was generated by SableCC (http://www.sablecc.org/). */

package goplusplus.node;

import java.util.*;
import goplusplus.analysis.*;

@SuppressWarnings("nls")
public final class AStructAstTypeExp extends PAstTypeExp
{
    private final LinkedList<PAstStructField> _astStructField_ = new LinkedList<PAstStructField>();

    public AStructAstTypeExp()
    {
        // Constructor
    }

    public AStructAstTypeExp(
        @SuppressWarnings("hiding") List<?> _astStructField_)
    {
        // Constructor
        setAstStructField(_astStructField_);

    }

    @Override
    public Object clone()
    {
        return new AStructAstTypeExp(
            cloneList(this._astStructField_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAStructAstTypeExp(this);
    }

    public LinkedList<PAstStructField> getAstStructField()
    {
        return this._astStructField_;
    }

    public void setAstStructField(List<?> list)
    {
        for(PAstStructField e : this._astStructField_)
        {
            e.parent(null);
        }
        this._astStructField_.clear();

        for(Object obj_e : list)
        {
            PAstStructField e = (PAstStructField) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._astStructField_.add(e);
        }
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._astStructField_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._astStructField_.remove(child))
        {
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        for(ListIterator<PAstStructField> i = this._astStructField_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PAstStructField) newChild);
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

/*
 * JOOS is Copyright (C) 1997 Laurie Hendren & Michael I. Schwartzbach
 *
 * Reproduction of all or part of this software is permitted for
 * educational or research use on condition that this copyright notice is
 * included in any copy. This software comes with no warranty of any
 * kind. In no event will the authors be liable for any damages resulting from
 * use of this software.
 *
 * email: hendren@cs.mcgill.ca, mis@brics.dk
 */



/* iload x        iload x        iload x
 * ldc 0          ldc 1          ldc 2
 * imul           imul           imul
 * ------>        ------>        ------>
 * ldc 0          iload x        iload x
 *                               dup
 *                               iadd
 */

int simplify_multiplication_right(CODE **c)
{ int x,k;
  if (is_iload(*c,&x) && 
      is_ldc_int(next(*c),&k) && 
      is_imul(next(next(*c)))) {
     if (k==0) return replace(c,3,makeCODEldc_int(0,NULL));
     else if (k==1) return replace(c,3,makeCODEiload(x,NULL));
     else if (k==2) return replace(c,3,makeCODEiload(x,
                                       makeCODEdup(
                                       makeCODEiadd(NULL))));
     return 0;
  }
  return 0;
}

/* dup
 * astore x
 * pop
 * -------->
 * astore x
 */
int simplify_astore(CODE **c)
{ int x;
  if (is_dup(*c) &&
      is_astore(next(*c),&x) &&
      is_pop(next(next(*c)))) {
     return replace(c,3,makeCODEastore(x,NULL));
  }
  return 0;
}

/* iload x
 * ldc k   (0<=k<=127)
 * iadd
 * istore x
 * --------->
 * iinc x k
 */ 
int positive_increment(CODE **c)
{ int x,y,k;
  if (is_iload(*c,&x) &&
      is_ldc_int(next(*c),&k) &&
      is_iadd(next(next(*c))) &&
      is_istore(next(next(next(*c))),&y) &&
      x==y && 0<=k && k<=127) {
     return replace(c,4,makeCODEiinc(x,k,NULL));
  }
  return 0;
}

/* goto L1
 * ...
 * L1:
 * goto L2
 * ...
 * L2:
 * --------->
 * goto L2
 * ...
 * L1:    (reference count reduced by 1)
 * goto L2
 * ...
 * L2:    (reference count increased by 1)  
 */
int simplify_goto_goto(CODE **c)
{ int l1,l2;
  if (is_goto(*c,&l1) && is_goto(next(destination(l1)),&l2) && l1>l2) {
     droplabel(l1);
     copylabel(l2);
     return replace(c,1,makeCODEgoto(l2,NULL));
  }
  return 0;
}

/*
iconst x
iconst y
arithmentic operation
=> compute and load the value directly, can be recursively applied
iconst x op y
*/

int simplify_const_op_const(CODE **c){
  int x,y;
  if (is_ldc_int(*c,&x) && is_ldc_int(next(*c),&y))
  {
    int z;
    if (is_iadd(next(next(*c))))
    {
      z = x+y;
      return replace(c,3,makeCODEldc_int(z,NULL));
    }
    else if (is_isub(next(next(*c))))
    {
      z = x-y;
      return replace(c,3,makeCODEldc_int(z,NULL));
    }
    else if (is_imul(next(next(*c))))
    {
      z = x*y;
      return replace(c,3,makeCODEldc_int(z,NULL));
    }
    else if (is_idiv(next(next(*c))))
    {
      z = x/y;
      return replace(c,3,makeCODEldc_int(z,NULL));
    }
    else if (is_irem(next(next(*c))))
    {
      z = x%y;
      return replace(c,3,makeCODEldc_int(z,NULL));
    }
  }
  return 0;
}

/*
iload/aload x
iload/aload x
=>
iload/aload x
dup
*/
int simplify_load_twice(CODE **c){
  int x,y;
  if (is_iload(*c,&x) && is_iload(next(*c),&y))
  {
    if (x==y)
    {
      return replace(c,2,makeCODEiload(x,makeCODEdup(NULL)));
    }
  }
  else if (is_aload(*c,&x) && is_aload(next(*c),&y))
  {
    if (x==y)
    {
      return replace(c,2,makeCODEaload(x,makeCODEdup(NULL)));
    }
  }
  return 0;
}


/* aload x
 * getfield a
 * aload x
 * getfield a
 * =>
 * aload x
 * getfield a
 * dup
 */
int simplify_getfield_dup(CODE **c)
{ int x,y;
  char *a,*b;
  if (is_aload(*c,&x) &&
      is_getfield(next(*c),&a) &&
      is_aload(next(next(*c)),&y) &&
      is_getfield(next(next(next(*c))),&b) &&
      x==y && strcmp(a,b)==0) {
    return replace(c,4,makeCODEaload(x,makeCODEgetfield(a,makeCODEdup(NULL))));
    // check the pointer a
  }
  return 0;
}

/* istore x
 * iload x
 * => same for all store/load
 * dup
 * istore x
 */
int simplify_istore_iload(CODE **c)
{ int x,y;
  if (is_istore(*c,&x) &&
      is_iload(next(*c),&y) &&
      x==y) {
    return replace(c,2,makeCODEdup(makeCODEistore(x,NULL)));
  }
  return 0;
}

/* iconst_0 // ldc 0
 * if_icmpeq L
 * =>
 * ifeq L
 */
int simplify_icmpeq(CODE **c)
{ int k,l1;
  if (is_ldc_int(*c,&k) && // not sure if is_aconst_null is for iconst
      is_if_icmpeq(next(*c),&l1)) {
    if (k==0) return replace(c,2,makeCODEifeq(l1,NULL));
  }
  return 0;
}

/* iconst_0 // ldc 0
 * if_icmpne L
 * =>
 * ifne L
 */
int simplify_icmpne(CODE **c)
{ int k,l1;
  if (is_ldc_int(*c,&k) && // not sure if is_aconst_null is for iconst
      is_if_icmpne(next(*c),&l1)) {
    if (k==0) return replace(c,2,makeCODEifne(l1,NULL));
  }
  return 0;
}

/* iconst_0         iconst_1 // ldc 0, 1
 * ifne goto L2     ifne goto L
 * stmts            
 * =>               =>
 * stmts            goto L
 */
int simplify_ne_branch(CODE **c)
{ int k,l1;
  if (is_ldc_int(*c,&k) && // not sure if is_aconst_null is for iconst
      is_ifne(next(*c),&l1)) {
    if (k==0) 
  	{
  		droplabel(l1);
  		return replace(c,2,makeCODEnop(NULL));
  	}
    if (k==1) return replace(c,2,makeCODEgoto(l1,NULL));
  }
  return 0;
}

/* iconst_0         iconst_1 // ldc 0, 1
 * ifeq goto L      ifeq goto L
 *                  stmts
 * =>               =>
 * goto L           stmts
 */
int simplify_eq_branch(CODE **c)
{ int k,l1;
  if (is_ldc_int(*c,&k) && // not sure if is_aconst_null is for iconst
      is_ifeq(next(*c),&l1)) {
    if (k==0) return replace(c,2,makeCODEgoto(l1,NULL));
    if (k==1) 
    {
    	droplabel(l1);
    	return replace(c,2,makeCODEnop(NULL));
    }
  }
  return 0;
}


/* goto L1
 * stmts1(without label or only has dead labels)
 * L2: (the first label that's not dead)
 * stmts2
 * =>
 * goto L1
 * L2:
 * stmts2
 */
int drop_dead_code(CODE **c)
{ int l1,l2,count;
  count=0;
  if (is_goto(*c,&l1)) {
    CODE *p = next(*c);
    while (!is_label(p,&l2) || l2!=l1) {
      if (is_label(p,&l2)) 
      {
        if(!deadlabel(l2))
          break;
      }
      count++;
      p = next(p);  
    }
    if (count>0){
    	copylabel(l1);
      return replace_modified(c,count+1,makeCODEgoto(l1,NULL));
    }
    else
      return 0;
  }
  else
    return 0;
}

/* ldc 0
 * iload x
 * idiv
 * =>
 * ldc 0
 */
int zero_division(CODE **c)
{ int x,k;
  if (is_ldc_int(*c,&k) &&
      is_iload(next(*c),&x) &&
      is_idiv(next(next(*c)))) {
    if (k==0) return replace(c,3,makeCODEldc_int(k,NULL));
    return 0;
  }
  return 0;
}

/* ldc 0          ldc 1          ldc 2
 * iload x        iload x        iload x
 * imul           imul           imul
 * ------>        ------>        ------>
 * ldc 0          iload x        iload x
 *                               dup
 *                               iadd
 */
int simplify_multiplication_right2(CODE **c)
{ int x,k;
  if (is_ldc_int(*c,&k) && 
      is_iload(next(*c),&x) && 
      is_imul(next(next(*c)))) {
     if (k==0) return replace(c,3,makeCODEldc_int(0,NULL));
     else if (k==1) return replace(c,3,makeCODEiload(x,NULL));
     else if (k==2) return replace(c,3,makeCODEiload(x,
                                       makeCODEdup(
                                       makeCODEiadd(NULL))));
     return 0;
  }
  return 0;
}

/* ldc k
 * iload x
 * iadd
 * istore x
 * =>
 * iinc x k
 */
int positive_increment2(CODE **c)
{ int x,y,k;
  if (is_ldc_int(*c,&k) &&
      is_iload(next(*c),&x) &&
      is_iadd(next(next(*c))) &&
      is_istore(next(next(next(*c))), &y) &&
      x==y && 0<=k && k<=127) {
    return replace(c,4,makeCODEiinc(x,k,NULL));
  }
  return 0;
}

int redundant_label(CODE **c)
{
	int label;
	if (uses_label(*c, &label))
	{
		CODE* label_pos = destination(label);
		int label2;
		if (is_label(next(label_pos), &label2))
		{
			(*c)->val.labelC = label2;
			droplabel(label);
			copylabel(label2);
			if (deadlabel(label))
			{
				return kill_line(&label_pos);
			}
		}
	}
	return 0;
}

int redundant_goto(CODE **c)
{
	int label;
	if (is_goto(*c, &label))
	{
		int label2;
		if (is_label(next(*c), &label2))
		{
			if (label == label2)
			{
				return replace_modified(c, 1, NULL);
			}
		}
	}
	return 0;
}

int remove_nop(CODE **c)
{
	if (is_nop(*c))
	{
		if (!is_return(next(*c)))
		{
			kill_line(c);
		}
	}
	return 0;
}

int simplify_cmp(CODE **c)
{
	CODE* n = *c;
	int elselabel;
	if (uses_label(n, &elselabel))
	{
		int v1;
		n = next(n);
		if (is_ldc_int(n,&v1) && v1 == 0)
		{
			int stoplabel;
			n = next(n);
			if (is_goto(n, &stoplabel))
			{
				int elselabel2;
				n = next(n);
				if (is_label(n, &elselabel2) && elselabel == elselabel2 && uniquelabel(elselabel))
				{
					int v2;
					n = next(n);
					if (is_ldc_int(n, &v2) && v2 == 1)
					{
						int stoplabel2;
						n = next(n);
						if (is_label(n, &stoplabel2) && stoplabel2 == stoplabel && uniquelabel(stoplabel))
						{
							int finallabel;
							n = next(n);
							if (is_ifeq(n, &finallabel))
							{
								copylabel(finallabel);
								droplabel(stoplabel);
								droplabel(elselabel);
								if (is_ifeq(*c, &elselabel))
								{
									return replace_modified(c, 7, makeCODEifne(finallabel, NULL));
								}
								else if (is_ifne(*c, &elselabel))
								{
									return replace_modified(c, 7, makeCODEifeq(finallabel, NULL));
								}
								else if (is_if_acmpeq(*c, &elselabel))
								{
									return replace_modified(c, 7, makeCODEif_acmpne(finallabel, NULL));
								}
								else if (is_if_acmpne(*c, &elselabel))
								{
									return replace_modified(c, 7, makeCODEif_acmpeq(finallabel, NULL));
								}
								else if (is_if_icmpeq(*c, &elselabel))
								{
									return replace_modified(c, 7, makeCODEif_icmpne(finallabel, NULL));
								}
								else if (is_if_icmpne(*c, &elselabel))
								{
									return replace_modified(c, 7, makeCODEif_icmpeq(finallabel, NULL));
								}
								else if (is_if_icmplt(*c, &elselabel))
								{
									return replace_modified(c, 7, makeCODEif_icmpge(finallabel, NULL));
								}
								else if (is_if_icmple(*c, &elselabel))
								{
									return replace_modified(c, 7, makeCODEif_icmpgt(finallabel, NULL));
								}
								else if (is_if_icmpgt(*c, &elselabel))
								{
									return replace_modified(c, 7, makeCODEif_icmple(finallabel, NULL));
								}
								else if (is_if_icmpge(*c, &elselabel))
								{
									return replace_modified(c, 7, makeCODEif_icmplt(finallabel, NULL));
								}
								else if (is_ifnull(*c, &elselabel))
								{
									return replace_modified(c, 7, makeCODEifnonnull(finallabel, NULL));
								}
								else if (is_ifnonnull(*c, &elselabel))
								{
									return replace_modified(c, 7, makeCODEifnull(finallabel, NULL));
								}
							}
						}
					}
				}
			}
		}
	}
	return 0;
}

int remove_deadlabel(CODE **c)
{
	int l;
	if (is_label(*c, &l))
	{
		if (deadlabel(l))
		{
			kill_line(c);
		}
	}
	return 0;
}

/* iconst k
 * istore x
 * stmts
 * iload x
 * =>
 * iconst k
 * istore x
 * stmts
 * iconst 2
 */
// int iload_to_iconst(CODE **c)
// { int k,x,y,z,label;
//   if (is_ldc_int(*c,&k) &&
//       is_istore(next(*c),&x)) {
//     CODE *iter = next(next(*c));
//     int count = 0;
//     while (!uses_label(iter,&label)) {
//       if (is_istore(iter,&y) && x==y) {
//         break;
//       }
//       if (is_iload(iter,&z) && x==z) {
//         return replace(&iter,1,makeCODEldc_int(k,NULL));
//       }
//       iter = next(iter);
//     }
//   }
//   return 0;
// }

/* dup
 * istore x
 * pop
 */
int remove_dup_pop_around_istore(CODE **c)
{ int k;
  if (is_dup(*c) &&
      is_istore(next(*c),&k) &&
      is_pop(next(next(*c)))) {
    return replace(c,3,makeCODEistore(k,NULL));
  }
  return 0;
}

int init_patterns()
{
  ADD_PATTERN(simplify_cmp);
  ADD_PATTERN(simplify_multiplication_right);
  ADD_PATTERN(simplify_astore);
  ADD_PATTERN(positive_increment);
  ADD_PATTERN(simplify_goto_goto);
  ADD_PATTERN(simplify_const_op_const);
  ADD_PATTERN(simplify_load_twice);
  ADD_PATTERN(simplify_getfield_dup);
  ADD_PATTERN(simplify_istore_iload);
  ADD_PATTERN(simplify_icmpeq);
  ADD_PATTERN(simplify_icmpne);
  ADD_PATTERN(simplify_ne_branch);
  ADD_PATTERN(simplify_eq_branch);
  ADD_PATTERN(drop_dead_code);
  ADD_PATTERN(zero_division);
  ADD_PATTERN(simplify_multiplication_right2);
  ADD_PATTERN(positive_increment2);
  ADD_PATTERN(redundant_label);
  ADD_PATTERN(redundant_goto);
  ADD_PATTERN(remove_nop);
  ADD_PATTERN(remove_deadlabel);
  // ADD_PATTERN(iload_to_iconst);
  ADD_PATTERN(remove_dup_pop_around_istore);
  return 1;
}
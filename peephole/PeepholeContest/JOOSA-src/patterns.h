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
    else
      return 0;
  }
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
    if (k==0) return replace(c,2,makeCODEgoto(l1,NULL));
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
    if (k==0) return replace(c,2,makeCODEgoto(l1,NULL));
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
    if (k==0) return replace(c,2,makeCODEnop(NULL));
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
    if (k==1) return replace(c,2,makeCODEnop(NULL));
  }
  return 0;
}


/* gotoL
 * stmts1(without label or indegree=0)
 * L:
 * =>
 * stmts2
 */
// int drop_dead_code(CODE **c)
// { int l1,count;
//   if (is_goto(*c,&l1)) {
//     while () {
//       count++;
//       if () {
//         return 
//       }
//     }
//   }
// }

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
			c->labelC = label2;
			return kill_line(&pos);
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
				return replace_modified(c, 2, NULL);
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
}

int init_patterns()
{
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
  // ADD_PATTERN(drop_dead_code);
  ADD_PATTERN(zero_division);
  ADD_PATTERN(simplify_multiplication_right2);
  ADD_PATTERN(positive_increment2);

  ADD_PATTERN(redundant_label);
  ADD_PATTERN(redundant_goto);
  ADD_PATTERN(remove_nop);
  return 1;
}
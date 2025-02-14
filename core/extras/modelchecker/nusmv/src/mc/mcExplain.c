/**CFile***********************************************************************

  FileName    [mcExplain.c]

  PackageName [mc]

  Synopsis    [Witness and Debug generator for Fair CTL models.]

  Description [This file contains the code to find counterexamples
  execution trace that shows a cause of the problem. Here are
  implemented the techniques described in the CMU-CS-94-204 Technical
  Report by E. Clarke, O. Grumberg, K. McMillan and X. Zhao.]

  SeeAlso     [mcMc.c]

  Author      [Marco Roveri]

  Copyright   [
  This file is part of the ``mc'' package of NuSMV version 2. 
  Copyright (C) 1998-2001 by CMU and ITC-irst. 

  NuSMV version 2 is free software; you can redistribute it and/or 
  modify it under the terms of the GNU Lesser General Public 
  License as published by the Free Software Foundation; either 
  version 2 of the License, or (at your option) any later version.

  NuSMV version 2 is distributed in the hope that it will be useful, 
  but WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public 
  License along with this library; if not, write to the Free Software 
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA.

  For more information on NuSMV see <http://nusmv.irst.itc.it>
  or email to <nusmv-users@irst.itc.it>.
  Please report bugs to <nusmv-users@irst.itc.it>.

  To contact the NuSMV development board, email to <nusmv@irst.itc.it>. ]

******************************************************************************/

#include "mc.h"
#include "mcInt.h"

#include "utils/ustring.h"
#include "parser/symbols.h"
#include "utils/assoc.h"
#include "fsm/bdd/FairnessList.h"
#include "utils/error.h"
#include "utils/utils_io.h" /* for indent_node */


static char rcsid[] UTIL_UNUSED = "$Id: mcExplain.c,v 1.11.4.33.4.5.4.1 2006/09/28 07:46:01 nusmv Exp $";

/* Define this to enable trace explain debug */
/* #define EXPLAIN_TRACE_DEBUG */

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Static function prototypes                                                */
/*---------------------------------------------------------------------------*/
static node_ptr fairness_explain ARGS((BddFsm_ptr fsm, BddEnc_ptr enc, 
				       node_ptr pl, bdd_ptr f,
				       JusticeList_ptr justice));

static node_ptr explain_recur ARGS((BddFsm_ptr, BddEnc_ptr enc, 
				    node_ptr, node_ptr, node_ptr));

static node_ptr
Extend_trace_with_state_input_pair ARGS ((BddFsm_ptr fsm, BddEnc_ptr enc,
                                          node_ptr path,
                                          bdd_ptr starting_state,
                                          bdd_ptr next_states,
                                          const char * comment));

static node_ptr
Extend_trace_with_states_inputs_pair ARGS ((BddFsm_ptr fsm, BddEnc_ptr enc,
                                            node_ptr path,
                                            bdd_ptr starting_state,
                                            bdd_ptr next_states,
                                            const char * comment));

static void Check_TraceList_Sanity ARGS((BddEnc_ptr enc, node_ptr path,
                                         const char * varname));


/*---------------------------------------------------------------------------*/
/* Definition of exported functions                                          */
/*---------------------------------------------------------------------------*/

/**Function********************************************************************

  Synopsis           [Counterexamples and witnesses generator.]

  Description        [This function takes as input a CTL formula and
  returns a witness showing how the given formula does not hold. The
  result consists of a list of states (i.e. an execution trace) that
  leads to a state in which the given formula does not hold.]

  SideEffects        []

  SeeAlso            [explain_recur ex_explain eu_explain eg_explain
  ebg_explain ebu_explain]

******************************************************************************/
node_ptr explain(BddFsm_ptr fsm, BddEnc_ptr enc, 
		 node_ptr path, node_ptr spec_formula, 
		 node_ptr context)
{
  return explain_recur(fsm, enc, path, spec_formula, context);
}
 
/*---------------------------------------------------------------------------*/
/* Definition of internal functions                                          */
/*---------------------------------------------------------------------------*/

/**Function********************************************************************

  Synopsis           [This function computes a path that is a witness
                      for <i>EX(f)</i>.]

  Description        [This function finds a path that is a witness for
  <i>EX(f)</i>. <code>path<code> is a BDD which represents the first
  state of the path. It essentially is an initial state from which the
  example can be found.  The formula <i>EX(f)</i> holds under
  fairness constraints in a state <i>s_i</i> iff there is a
  successor state <i>s_{i+1}</i> such that <i>s_{i+1}</i>
  satisfies <i>f</i> and </i>s_{i+1}</i> is the beginning of some
  fair computation path. We look for states that can be reached from
  the state stored as first element in <code>path</code>, which are fair and
  in which <i>f</i> is satisfied. The algorithm computes more than
  one state, in order to have only one state we apply
  <code>bdd_pick_one_state</code>. The result of this application is
  then put in AND with <code>path</code> to form the witness.]

  SideEffects        []

  SeeAlso            [explain]

******************************************************************************/

node_ptr ex_explain(BddFsm_ptr fsm, BddEnc_ptr enc, node_ptr path, bdd_ptr f)
{
  bdd_ptr acc, starting_state, image;

  if (path == Nil) return(path);

  starting_state = bdd_dup((bdd_ptr) car(path));
  nusmv_assert( BddFsm_is_fair_states(fsm, starting_state) );
  image = BddFsm_get_forward_image(fsm, starting_state);

  acc = bdd_dup(f);

  if (opt_use_fair_states(options)) {
    bdd_ptr fair_states_bdd = BddFsm_get_fair_states(fsm);
    bdd_and_accumulate(dd_manager, &acc, fair_states_bdd);
    bdd_free(dd_manager, fair_states_bdd);
  }

  bdd_and_accumulate(dd_manager, &acc, image);
  bdd_free(dd_manager, image);

  if (bdd_is_zero(dd_manager, acc)) { 
    /* Failure in search */
    path = Nil;
  }
  else {
    path = Extend_trace_with_state_input_pair(fsm, enc, path, starting_state, acc, "ex_explain: (1).");
  }

  bdd_free(dd_manager, starting_state);
  bdd_free(dd_manager, acc);

  return path;
}

/**Function********************************************************************

  Synopsis           [This function finds a path that is a witness
                      for <i>E\[f U g\]</i> when g is a set of  state-inputs ]

  Description []

  SideEffects        []

  SeeAlso            [explain]

******************************************************************************/
node_ptr eu_si_explain(BddFsm_ptr fsm, BddEnc_ptr enc, 
		       node_ptr path, bdd_ptr f, bdd_ptr g_si, bdd_ptr hulk)
{
  node_ptr res;
  bdd_ptr g;

  g = BddFsm_states_inputs_to_states(fsm, g_si);

  res = eu_explain(fsm, enc, path, f, g);

  /*
    If an explanation has been found, and the fairness condition is
    on states and inputs then we see if necessary to add a transition
    to the path.
  */
  if ((res != Nil) && (g != g_si)) {
    bdd_ptr state;
    bdd_ptr si;

    state = bdd_dup((bdd_ptr) car(res));
    si = bdd_and(dd_manager, state, g_si);
    
    /* No constraint on following transition. */
    if (state != si) {
      bdd_ptr next_states;
      bdd_ptr next_state; 
      bdd_ptr inputs;
      bdd_ptr input;

      inputs = BddFsm_states_inputs_to_inputs(fsm, si);

      /* Here we pick the right input -- and at this stage we may
	 not know. */
      input = BddEnc_pick_one_input(enc, inputs);
      bdd_free(dd_manager, inputs);

      next_states = BddFsm_get_constrained_forward_image(fsm, state, input);
      
      /* May need to restrict the image to the target states. */
      bdd_and_accumulate(dd_manager, &next_states, hulk);

      next_state = BddEnc_pick_one_state(enc, next_states);
      bdd_free(dd_manager, next_states);

      /* add next state and corresponding input to witness path */
      res = cons((node_ptr) bdd_dup(next_state), 
		 cons((node_ptr) bdd_dup(input), res));

      bdd_free(dd_manager, input);
      bdd_free(dd_manager, next_state);
    }
      
    bdd_free(dd_manager, state);
    bdd_free(dd_manager, si);
  }

  bdd_free(dd_manager, g);
  return res;
} /* eu_si_explain */

/**Function********************************************************************

  Synopsis           [This function finds a path that is a witness
                      for <i>E\[f U g\]</i>]

  Description [This function finds a path that is a witness for
  <i>E\[f U g\]</i>.  The first element of <code>path</code> is a BDD
  <code>p</code> that represents the first state of the witness
  path. It is an initial state from which the example can be
  found. The procedure is to try to execute <code>eu(f,g)</code>
  again, looking for a path from <code>p</code> to a state where
  <i>g</i> is valid. At each step we generate a set of states
  <i>s_i</i> that can be reached in one step from <i>s_{i-1}</i>. We
  extract one minterm form each <i>s_i</i> and we store it in a list.]

  SideEffects        []

  SeeAlso            [explain]

******************************************************************************/
node_ptr eu_explain(BddFsm_ptr fsm, BddEnc_ptr enc, 
		    node_ptr path, bdd_ptr f, bdd_ptr g)
{
  if (path != Nil) { 
    bdd_ptr acc = bdd_dup(g);
    
    bdd_ptr Y = bdd_dup((bdd_ptr) car(path)); /* Set of states reached
						 so far - initially
						 just one state */

    bdd_ptr Z = bdd_dup(Y);   /* Set of states reached so far along a
                                 path satisfying f. If we ever use Z,
                                 it means that car(path) does not
                                 satisfy g, therefore it satisfies
                                 f */

    bdd_ptr new = bdd_dup(Y);  /* States added to Y just now */

    int n = 0;     /* Iteration counter */

    node_ptr witness_path = path;  /* initialize list with first state
				      (list is reversed) */
    
    if (opt_verbose_level_gt(options, 1)) {
      indent_node(nusmv_stderr, "searching (counter)example for ", 
		  get_the_node(), "\n");
    }
       
    if (opt_use_fair_states(options)) {
      bdd_ptr fair_states_bdd = BddFsm_get_fair_states(fsm);
      bdd_and_accumulate(dd_manager, &acc, fair_states_bdd);
      bdd_free(dd_manager, fair_states_bdd);
    }
   
    /* acc = g /\ fair_states */
    while (bdd_isnot_zero(dd_manager, new)) {
      bdd_ptr tmp_1, tmp_2; 
      bdd_ptr x;
    
      if (opt_verbose_level_gt(options, 1)) {
	fprintf(nusmv_stderr, "eu_explain: iteration %d: states = %g, BDD nodes = %d\n",
                n++, BddEnc_count_states_of_bdd(enc, Y), bdd_size(dd_manager, Y));
      }
    
      tmp_1 = bdd_and(dd_manager, new, acc);

      /* x is one state in Y & acc */
      x = BddEnc_pick_one_state(enc, tmp_1);
      bdd_free(dd_manager, tmp_1);

      /* did we reach (g /\ fair_states_bdd) ? */
      if (bdd_isnot_zero(dd_manager, x)) { 
	/* Yes. Instantiate the Y's, and return a list of states. 
	   Aux variable "m" used to perform side effects on "witness_path" */
	node_ptr m = witness_path;
	
	while (true) { 
          bdd_ptr nx, is;

	  if (opt_use_reachable_states(options)) {
	    bdd_ptr reachable_x;
            
	    bdd_ptr reachable_states_bdd = BddFsm_get_reachable_states(fsm);
	    reachable_x = bdd_and(dd_manager, x, reachable_states_bdd);
	    bdd_free(dd_manager, reachable_states_bdd);
              
	    if (bdd_is_zero(dd_manager, reachable_x)) {
              SymbTable_ptr st = BaseEnc_get_symbol_table(BASE_ENC(enc));

	      bdd_free(dd_manager, reachable_x);
	      fprintf(nusmv_stdout, "this state is not reachable :\n");
              BddEnc_print_bdd_begin(enc, 
		     NodeList_to_node_ptr(SymbTable_get_state_vars(st)),
		     false);	
	      BddEnc_print_bdd(enc, x, nusmv_stdout);
	      /* Here the free of all the variables has to be performed */
	      internal_error("eu_explain: state not reachable");
	    }
	    else bdd_free(dd_manager, reachable_x);
	  } 

	  /* substitute Y for one state of Y in the list */            
	  bdd_free(dd_manager, (bdd_ptr) car(m));
          node_bdd_setcar(m, bdd_dup(x));

	  if (m == path) { 
	    /* if we reached the first state, it's over */
	    bdd_free(dd_manager, x); 
	    bdd_free(dd_manager, Y);
	    bdd_free(dd_manager, Z);
	    bdd_free(dd_manager, new);
	    bdd_free(dd_manager, acc);
	    
	    return witness_path;
	  }

	  is = bdd_dup((bdd_ptr) car(cdr(m)));

          /* instantiate the next Y. x is a state in car(m), such that there
	     is a path from the current x to it. */
          tmp_1 = BddFsm_get_constrained_backward_image(fsm, x, is);

          bdd_free(dd_manager, is);

          /* We intersect with the next state in the path */
          bdd_and_accumulate(dd_manager, &tmp_1, (bdd_ptr) car(cdr(cdr(m))));

	  /* if l != path, car(path) may include states not satisfying f */
	  if (m == path) bdd_and_accumulate(dd_manager, &tmp_1, f);

          nx = BddEnc_pick_one_state(enc, tmp_1);
          bdd_free(dd_manager, tmp_1);


          { /* We extract a singleton input connecting x and nx */
            bdd_ptr inputs = BddFsm_states_to_states_get_inputs(fsm, nx, x);
            bdd_ptr input = BddEnc_pick_one_input(enc, inputs);

            bdd_free(dd_manager, inputs);
            /* We strore it in the path */
            bdd_free(dd_manager, (bdd_ptr)car(cdr(m)));
            node_bdd_setcar(cdr(m), bdd_dup(input));
            bdd_free(dd_manager, input);
          }

          bdd_free(dd_manager, x);
          x = nx;
          m = cdr(cdr(m));
	} /* loop */
      } /* if (x |= zero) */
      
      bdd_free(dd_manager, x);
      
      /* generate the next Y, that is, the set of states that can be reached
	 in one step from the states in Y that satisfy f */
      tmp_1 = bdd_and(dd_manager, f, new);
      tmp_2 = BddFsm_get_forward_image(fsm, tmp_1);
      bdd_free(dd_manager, tmp_1);
      bdd_free(dd_manager, Y);

      Y = bdd_or(dd_manager, Z, tmp_2);
      bdd_free(dd_manager, tmp_2);
      bdd_free(dd_manager, new);

      tmp_1 = bdd_not(dd_manager, Z);
      new = bdd_and(dd_manager, Y, tmp_1);
      bdd_free(dd_manager, tmp_1);

      /*
        In case the new Y cannot satisfy g, save its subset of states
        that satisfies f on the state list.
      */
      bdd_free(dd_manager, Z);
      Z = bdd_and(dd_manager, f, Y);
      
      witness_path = Extend_trace_with_states_inputs_pair(fsm, enc, witness_path,
                                                          (bdd_ptr) car(witness_path), Z,
                                                          "eu_explain: (1).");
    } /* while (new != zero) */


    /* reached the fixpoint and could not find it. Release the list. */
    bdd_free(dd_manager, Y);
    bdd_free(dd_manager, Z);
    bdd_free(dd_manager, new);
    bdd_free(dd_manager, acc);
    while (witness_path != path) {
      node_ptr m = witness_path;

      bdd_free(dd_manager, (bdd_ptr) car(witness_path));
      witness_path = cdr(witness_path);
      free_node(m);
    } /* while (witness_path != p) */

  } /* if (p != nil) */

  return Nil;
} /* eu_explain */


/**Function********************************************************************

  Synopsis           [This function finds a path that is an example
                      for <i>EG(g)</i>.]

  Description [This function finds a path that is an example for
  <i>EG(g)</i>. The first element <code>p</code> is the BDD that
  represents the first state of the path. It is an initial state from
  which the example can be found.<br>

  The procedure is based on the greatest fixed point characterization
  for the CTL operator <b>EG</b>. The CTL formula <i>EG(g)</i> under
  fairness constraints means that there exists a path beginning with
  current state on which <i>g</i> holds globally (invariantly) and
  each formula in the set of fairness constraints holds infinitely
  often on the path.  If we denote with <i>EG(g)</i> the set of states
  that satisfy <i>EG(g)</i> under fairness constraints, we can
  construct the witness path incrementally by giving a sequence of
  prefixes of the path of increasing length until a cycle is found. At
  each step in the construction we must ensure that the current prefix
  can be extended to a fair path along which each state satisfies
  <i>EG(g)</i>.]

  SideEffects        []

  SeeAlso            [explain]

******************************************************************************/
node_ptr eg_explain(BddFsm_ptr fsm, BddEnc_ptr enc, 
		    node_ptr witness_path, bdd_ptr arg_g)
{
  bdd_ptr g;
  bdd_ptr eg_si_g;
  bdd_ptr eg_g;
  bdd_ptr tmp_1;
  JusticeList_ptr fairness_constraints;

  if (witness_path == Nil) return witness_path;

  /* Duplicate arg_g */
  g = bdd_dup(arg_g);

  /* Compute eg_si_g, i.e. the set of fair state inputs wrt g. */
  eg_si_g = eg_si(fsm, g);

  /* Compute eg_g, i.e. the subset of g which contains fair cycle. */
  eg_g = eg(fsm, g);

  /*
    If first state in the path is not in eg_g (that is, not a chance
     to construct a fair cycle) then return with Nil.
  */
  if (bdd_entailed(dd_manager, (bdd_ptr) car(witness_path), eg_g) == 0) {
    bdd_free(dd_manager, eg_g);
    bdd_free(dd_manager, g);
    return Nil;
  } 

  /* The main loop: it implements search, due to the fact that we may
     be in a SCC that does not allow us to satisfy all the fairness
     constransts, and therefore we need to backtrack. */
  fairness_constraints = BddFsm_get_justice(fsm);
  while (true) {
    node_ptr old_witness_path;
    bdd_ptr starting_state = bdd_dup((bdd_ptr) car(witness_path) );

    /* Try to construct a path satisfying the fairness constraints. */
    witness_path = fairness_explain(fsm, enc, witness_path, eg_si_g, 
				    fairness_constraints);

    /* Go one step backward from the starting state. */
    tmp_1 = ex(fsm, starting_state);
    bdd_free(dd_manager, g);
    g = bdd_and(dd_manager, eg_g, tmp_1);
    bdd_free(dd_manager, tmp_1);

    /* Save previous path */
    old_witness_path = witness_path;

    /* Try to show that starting_state state can be reached again:
       - first reach its premiage (without exiting eg_g)
       - then reach starting state (in one step)
    */
    witness_path = eu_explain(fsm, enc, witness_path, eg_g, g);
    witness_path = ex_explain(fsm, enc, witness_path, starting_state);

    /* If starting_state state reached, the loop is closed. Return. */
    if (witness_path != Nil) {
      bdd_free(dd_manager, starting_state);
      break;
    }

    /* 
       Otherwise, try to show that eg_g can be reached on one step.
       This must be possible, otherwise we have an error.
    */
    witness_path = ex_explain(fsm, enc, old_witness_path, eg_g);
    if (witness_path == Nil) {
      bdd_free(dd_manager, eg_g);
      bdd_free(dd_manager, g);
      bdd_free(dd_manager, starting_state);
      internal_error("eg_explain: witness_path == Nil");
    }
    bdd_free(dd_manager, starting_state);
  } /* loop */

  bdd_free(dd_manager, eg_g);
  bdd_free(dd_manager, g);

  return witness_path;
}



/**Function********************************************************************

  Synopsis           [This function finds a path that is a witness
                      for <i>E\[f U g\]^{sup}_{inf}</i>.]

  Description        [This function finds a path that is a witness
  for <i>E\[f U g\]^{sup}_{inf}</i>. The first element of
  <code>path</code> is a BDD that represents the first state of the
  path. It is an initial state from which the example can be found.
  The procedure is to try to execute <code>ebu(f, g, inf, sup)</code>, looking
  for a path, with length <code>(sup - inf)<code>, from <code>p</code>
  to a state where <i>g</i> is valid using only transitions from
  states satisfying <i>f</i>.]

  SideEffects        []

  SeeAlso            [explain]

******************************************************************************/
node_ptr ebu_explain(BddFsm_ptr fsm, BddEnc_ptr enc, 
		     node_ptr path, bdd_ptr f, bdd_ptr g, 
		     int inf, int sup)
{
  int i, n;
  bdd_ptr Y, Z, tmp_1;
  node_ptr witness_path;

  if (path == Nil) return path;
  
  Y = bdd_dup((bdd_ptr) car(path));
  witness_path = path;

  n = 0;
  if (opt_verbose_level_gt(options, 1)) {
    indent_node(nusmv_stderr, "searching (counter)example for ", get_the_node(), "\n");
  }

  /* looks for a path from the first element of "path", with length
      inf, using only transitions from states satisfying "f". The sets
      of states in the list may contain states in which "f" is false.
      This is performed later, when a complete (counter)example is
      found, to avoid the need of recovering the "old" "car(path)" */

  for (i = 0; i < inf; i++) {
    if (opt_verbose_level_gt(options, 1)) {
      fprintf(nusmv_stderr, "ebu: iteration %d: states = %g, BDD nodes = %d\n",
	      n++, BddEnc_count_states_of_bdd(enc, Y), bdd_size(dd_manager, Y));
    }

    Z = bdd_dup(Y);

    tmp_1 = bdd_and(dd_manager, Y, f);
    bdd_free(dd_manager, Y);
    Y = BddFsm_get_forward_image(fsm, tmp_1);

    if (bdd_is_zero(dd_manager, Y)) {
      /* there is no valid path */
      bdd_free(dd_manager, Z);
      bdd_free(dd_manager, Y);
      
      while (witness_path != path) {
	node_ptr m = witness_path;

	bdd_free(dd_manager, (bdd_ptr) car(witness_path));
	witness_path = cdr(witness_path);
	free_node(m);
      } /* (witness_path != path) */
      return Nil;
    } 

    witness_path = Extend_trace_with_states_inputs_pair(fsm, enc, witness_path,
                                                        (bdd_ptr) car(witness_path), Y,
                                                        "ebu_explain: (1).");
    if (Z == Y) {
      /* fixpoint found - fill the list with Y to length inf. */
      while (++i < inf) {
        witness_path = Extend_trace_with_states_inputs_pair(fsm, enc, witness_path,
                                                            (bdd_ptr) car(witness_path), Y,
                                                            "ebu_explain: (2).");
      } /* loop (++i < inf) */

      /* No need for further garbage collections. */
      bdd_free(dd_manager, Y);
      bdd_free(dd_manager, Z);
      break;
    } /* (Z == Y) */
    bdd_free(dd_manager, Z);
  } /* for (i = 0; i< inf ; i++) */
  
  /* At this point, car(witness_path) is the set of states that can be
     reached in inf steps, using transitions from states where "f" is
     valid.  Now we can call eu_explain(witness_path, f, g).
     eu_explain will find a shortest extension from car(witness_path)
     to a state where "g" is valid. We then check that the length of
     this path is less than or equal to (sup-inf). */

  { /* Block 1 */
    node_ptr new_witness_path; 

    new_witness_path = eu_explain(fsm, enc, witness_path, f, g);

    if (new_witness_path != Nil) {
      node_ptr m = new_witness_path;

      /* This is needed since eu_explain returns a list of singletons */
      for (i = 0; m != Nil && m != witness_path; i++) {
	m = cdr(cdr(m)); /* Skip two steps -- also input */
      }
      if (m == Nil) {
        internal_error("ebu_explain: cannot get back to witness_path");
      }
       
      /* did we reach g in time? */
      if (i <= (sup - inf)) {
	/* Yes. Instantiate the Y's, and return a list of states */
	bdd_ptr x  = BddEnc_pick_one_state(enc, (bdd_ptr) car(witness_path));

	m = witness_path;

	while (true) {
          bdd_ptr nx, is;

	  if (opt_use_reachable_states(options)) {
	    bdd_ptr reachable_states_bdd = BddFsm_get_reachable_states(fsm);
	    bdd_ptr reachable_x = bdd_and(dd_manager, x, reachable_states_bdd);

	    bdd_free(dd_manager,reachable_states_bdd);
 
	    if (bdd_is_zero(dd_manager, reachable_x)) {
	      SymbTable_ptr st = BaseEnc_get_symbol_table(BASE_ENC(enc));
	      bdd_free(dd_manager, reachable_x);
	      fprintf(nusmv_stdout, "this state is not reachable :\n");
              BddEnc_print_bdd_begin(enc, 
                     NodeList_to_node_ptr(SymbTable_get_vars(st)),
                     false);
	      BddEnc_print_bdd(enc, x, nusmv_stdout);
              BddEnc_print_bdd_end(enc);
	      internal_error("ebu_explain: state not reachable");
	    } 
	    bdd_free(dd_manager, reachable_x);
	  } /* if (reachable_states_bdd) */

	  /* substitute Y for one state of Y in the list */
	  bdd_free(dd_manager, (bdd_ptr) car(m));
	  node_bdd_setcar(m, bdd_dup(x)); 

	  if (m == path) {  /* if we reached the first state, it's over */
	    bdd_free(dd_manager, x);
	    return new_witness_path;
	  } 

          /* We extract the inputs as to restrict the BWD image */ 
          is = bdd_dup((bdd_ptr) car(cdr(m)));

	  /* Instantiate the next Y. x is a state in car(m), such that
	     there is a path from the current x to it. */
	  tmp_1 = BddFsm_get_constrained_backward_image(fsm, x, is);

          bdd_free(dd_manager, is);

          /* We intersect with the next state in the path */
          bdd_and_accumulate(dd_manager, &tmp_1, (bdd_ptr) car(cdr(cdr(m))));

          /* We intersect with f since if witness_path != path, car(m)
	     may include states not satisfying f */
          bdd_and_accumulate(dd_manager, &tmp_1, f);

	  nx = BddEnc_pick_one_state(enc, tmp_1);
	  bdd_free(dd_manager, tmp_1);

          { /* We extract a singleton input connecting x and nx */
            bdd_ptr inputs = BddFsm_states_to_states_get_inputs(fsm, nx, x);
            bdd_ptr input = BddEnc_pick_one_input(enc, inputs);

            bdd_free(dd_manager, inputs);
            /* We strore it in the path */
            bdd_free(dd_manager, (bdd_ptr)car(cdr(m)));
            node_bdd_setcar(cdr(m), bdd_dup(input));
            bdd_free(dd_manager, input);
          }

	  bdd_free(dd_manager, x);
          x = nx;
	  m = cdr(cdr(m));
	} /* loop */
      } /* if (i <= (sup - inf)) */

      /* path from witness_path to new_witness_path is longer than
	 requested. */
      witness_path = new_witness_path;
    } /* if (new_witness_path != Nil) */

    /* Could not find an example - free all newly allocated nodes */
    while (witness_path != path) {
      node_ptr m = witness_path;
      bdd_free(dd_manager, (bdd_ptr) car(witness_path));
      witness_path = cdr(witness_path);
      free_node(m);
    } 
  } /* Block 1 */

  return Nil;
} 


/**Function********************************************************************

  Synopsis           [This function finds a path of length
                     <tt>(sup-inf)</tt> that is an example for
                     <i>EG(g)^{sup}_{inf}</i>.]

  Description        [This function finds a path of length
  <tt>(sup-inf)</tt> that is an example for <i>EG(g)^{sup}_{inf}</i>. 
  The first element of <code>p</code> is the BDD that represents the
  first state of the path. It is an initial state from which the
  example has to be found.]

  SideEffects        []

  SeeAlso            [explain]

******************************************************************************/
node_ptr ebg_explain(BddFsm_ptr fsm, BddEnc_ptr enc, 
		     node_ptr path, bdd_ptr g, 
		     int inf, int sup)
{
  int i, n;
  bdd_ptr Z, tmp_1;
  bdd_ptr Y;
  node_ptr witness_path;

  if (path == Nil) return Nil;
  
  Y = bdd_dup((bdd_ptr) car(path));
  witness_path = path;

#ifdef EXPLAIN_TRACE_DEBUG
  Check_TraceList_Sanity(enc, path, "witness_path");
#endif

  n = 0;
  if (opt_verbose_level_gt(options, 1)) {
    indent_node(nusmv_stderr, "searching (counter)example for ", get_the_node(), "\n");
  }

  /* look for a path of length inf from car(path) */
  for (i=0; i < inf; i++) {
    
    if (opt_verbose_level_gt(options, 1)) {
      fprintf(nusmv_stderr, "ebg: iteration %d: states = %g, BDD nodes = %d\n",
	      n++, BddEnc_count_states_of_bdd(enc, Y), bdd_size(dd_manager, Y));
    }
    
    Z = bdd_dup(Y);
    tmp_1 = BddFsm_get_forward_image(fsm, Y);
    bdd_free(dd_manager, Y);
    Y = tmp_1;
    
    if (bdd_is_zero(dd_manager, Y)) {
      /* there is no valid path */
      bdd_free(dd_manager, Z);
      bdd_free(dd_manager, Y);
      
      while (witness_path != path) {
	node_ptr m=witness_path;
	
	bdd_free(dd_manager, (bdd_ptr)car(witness_path));
	witness_path = cdr(witness_path);
	free_node(m);
      } /* while (witness_path != path) */
      
      return Nil;
    } /* if (Y == zero) */

    witness_path = Extend_trace_with_states_inputs_pair(fsm, enc, witness_path,
                                                        (bdd_ptr) car(witness_path), Y,
                                                        "ebg_explain: (1).");

    if (Z == Y) {
      /* fixpoint found - fill the list with Y to length inf. */
      while (++i < inf) {
        witness_path = Extend_trace_with_states_inputs_pair(fsm, enc, witness_path,
                                                            (bdd_ptr) car(witness_path), Y,
                                                            "ebg_explain: (2).");
      } /* while (++i < inf) */

      bdd_free(dd_manager, Y);
      bdd_free(dd_manager, Z);
      break;
    } /* if (Z == Y) */
    
    bdd_free(dd_manager, Z);
  } /* for (i=0; i < inf; i++) */
  bdd_free(dd_manager, Y);
  
  /* 
     At this point, car(witness_path) is the set of states that can be
     reached in inf steps.  Look for a continuation path of length
     sup-inf using transitions from states that satisfy g
  */
  
  { /* Block 2 */
    node_ptr old_witness_path; 
    bdd_ptr fair_g = bdd_dup(g);

    if (opt_use_fair_states(options)) {
      bdd_ptr fair_states_bdd = BddFsm_get_fair_states(fsm);
      bdd_and_accumulate(dd_manager, &fair_g, fair_states_bdd);
      bdd_free(dd_manager, fair_states_bdd);
    }

    old_witness_path = witness_path;

    Y = (bdd_ptr) car(witness_path);
    for (i = inf; i < sup; i++) {
      if (opt_verbose_level_gt(options, 1))
	fprintf(nusmv_stderr, "ebg: iteration %d: states = %g, BDD nodes = %d\n",
		n++, BddEnc_count_states_of_bdd(enc, Y), bdd_size(dd_manager, Y));

      Z = bdd_dup(Y);
      tmp_1 = bdd_and(dd_manager, fair_g, Y);

      Y = BddFsm_get_forward_image(fsm, tmp_1);

      /* 
         Y must satisfy fair_g otherwise the last element can be such
         that it does not satisfy fair_g
      */
      bdd_and_accumulate(dd_manager, &Y, fair_g);
      bdd_free(dd_manager, tmp_1);

      if (bdd_is_zero(dd_manager, Y)) {
	/* there is no valid path */
	bdd_free(dd_manager, Z);
	bdd_free(dd_manager, Y);
	while (witness_path != path) {
	  node_ptr m=witness_path;
	  
	  bdd_free(dd_manager, (bdd_ptr)car(witness_path));
	  witness_path = cdr(witness_path);
	  free_node(m);
	} /* while (witness_path != path) */
	return Nil;
      } /* if (Y == zero) */

      witness_path = Extend_trace_with_states_inputs_pair(fsm, enc, witness_path,
                                                          (bdd_ptr) car(witness_path), Y,
                                                          "ebg_explain: (3).");
      if (Y == Z) {
	/* fixpoint found - fill the list with Y to length sup. */
	while (++i < sup) {
          witness_path = Extend_trace_with_states_inputs_pair(fsm, enc, witness_path,
                                                              (bdd_ptr) car(witness_path), Y,
                                                              "ebg_explain: (4).");
	} /* while (++i < sup) */
	
	/* No need for further garbage collections. */
	bdd_free(dd_manager, Y);
	bdd_free(dd_manager, Z);
	break;
      } /* if (Y == Z) */
      bdd_free(dd_manager, Z);
    } /* for (i = inf; i < sup; i++) */
    bdd_free(dd_manager, Y);

#ifdef EXPLAIN_TRACE_DEBUG
    Check_TraceList_Sanity(enc, witness_path, "ebg_explain: (5)");
    fprintf(nusmv_stdout, "States in fair_g:\n");
    BddEnc_print_set_of_states(enc, fair_g, false, nusmv_stdout);
#endif

    /*
      transform witness_path from a list of sets of states into a list
       of states. 
    */

    { /* Block 3 */
      node_ptr m = witness_path;
      bdd_ptr tmp_1 = bdd_and(dd_manager, fair_g, (bdd_ptr)car(m));
      bdd_ptr x = BddEnc_pick_one_state(enc, tmp_1);
      
      /* no longer needed */
      bdd_free(dd_manager, tmp_1);

      /* g should hold in all states up to car(old_witness_path), inclusive */
      while (true) {
        bdd_ptr nx, is;

        /* We intersect with fair_g since if witness_path != path,
           car(m) may include states not satisfying fair_g */
        /* substitute Y for one state of Y in the list */
	bdd_free(dd_manager, (bdd_ptr)car(m));
	node_bdd_setcar(m, bdd_dup(x));

#ifdef EXPLAIN_TRACE_DEBUG
	Check_TraceList_Sanity(enc, witness_path, "ebg_explain: witness_path (after setcar)");
#endif

	if (m == old_witness_path) break;
	
        /* We extract the inputs */
        is = bdd_dup((bdd_ptr) car(cdr(m)));

	/* instantiate the next Y */
	tmp_1 = BddFsm_get_constrained_backward_image(fsm, x, is);
	
        bdd_free(dd_manager, is);

        /* We intersect with fair_g since if witness_path != path,
           car(m) may include states not satisfying fair_g */
        bdd_and_accumulate(dd_manager, &tmp_1, fair_g);

        /* We intersect with the next state in the path */
        bdd_and_accumulate(dd_manager, &tmp_1, (bdd_ptr)car(cdr(cdr(m))));
	
	nx = BddEnc_pick_one_state(enc, tmp_1);
        bdd_free(dd_manager, tmp_1);

        {
          /* We extract a singleton input connecting x and nx */
          bdd_ptr inputs = BddFsm_states_to_states_get_inputs(fsm, nx, x);
          bdd_ptr input = BddEnc_pick_one_input(enc, inputs);

          bdd_free(dd_manager, inputs);
          /* We strore it in the path */
          bdd_free(dd_manager, (bdd_ptr)car(cdr(m)));
          node_bdd_setcar(cdr(m), bdd_dup(input));
          bdd_free(dd_manager, input);
        }

        bdd_free(dd_manager, x);
        x = nx;
	/* go to next state */
	m = cdr(cdr(m));

      } /* while (true) */

      bdd_free(dd_manager, fair_g);

      /* Continue instantiating up to car(path), inclusive */
      while (true) {
        bdd_ptr nx, is;

        /* substitute Y for one state of Y in the list */
	bdd_free(dd_manager, (bdd_ptr)car(m));
	node_bdd_setcar(m, bdd_dup(x));
	if (m == path) break;
	
        is = bdd_dup((bdd_ptr) car(cdr(m)));

	/* instantiate the next Y */
	tmp_1 = BddFsm_get_constrained_backward_image(fsm, x, is);

        /* We intersect with the next state in the path */
        bdd_and_accumulate(dd_manager, &tmp_1, (bdd_ptr)car(cdr(cdr(m))));

	nx = BddEnc_pick_one_state(enc, tmp_1);
	bdd_free(dd_manager, tmp_1);

        {
          /* We extract a singleton input connecting x and nx */
          bdd_ptr inputs = BddFsm_states_to_states_get_inputs(fsm, nx, x);
          bdd_ptr input = BddEnc_pick_one_input(enc, inputs);

          bdd_free(dd_manager, inputs);
          /* We strore it in the path */
          bdd_free(dd_manager, (bdd_ptr)car(cdr(m)));
          node_bdd_setcar(cdr(m), bdd_dup(input));
          bdd_free(dd_manager, input);
        }

        bdd_free(dd_manager, x);
        x = nx;
	/* got to next state */
	m = cdr(cdr(m));
      } /* while (true) */

      bdd_free(dd_manager, x);

#ifdef EXPLAIN_TRACE_DEBUG
      Check_TraceList_Sanity(enc, witness_path, "witness_path: witness_path (returned)");
#endif
      return witness_path;
    } /* Block 3 */
  } /* Block 2 */
} /* ebg_explain */


/*---------------------------------------------------------------------------*/
/* Definition of static functions                                            */
/*---------------------------------------------------------------------------*/

/**Function********************************************************************

  Synopsis           [Recursively traverse the formula CTL and rewrite
                      it in order to use the base witnesses generator functions.]

  Description        [Recursively traverse the formula CTL and rewrite
  it in order to use the base witnesses generator functions.<br>
  The rewritings performed use the equivalence between CTL formulas,
  i.e. <i>A\[f U g\]</i> is equivalent to <i>!(E\[!g U (!g & !f)\] | EG !g)</i>.]

  SideEffects        []

  SeeAlso            [explain]

******************************************************************************/
static node_ptr explain_recur(BddFsm_ptr fsm, BddEnc_ptr enc, node_ptr path,
                              node_ptr formula_expr, node_ptr context)
{
  bdd_ptr a1, a2;
  node_ptr new_path;

  if (formula_expr == Nil) return Nil;

  yylineno = node_get_lineno(formula_expr);
  switch (node_get_type(formula_expr)) {
  case CONTEXT: 
    return explain_recur(fsm, enc, path, cdr(formula_expr), car(formula_expr));

  case AND:
  case OR:
  case NOT:
  case IMPLIES:
  case IFF:
    new_path = explain_recur(fsm, enc, path, car(formula_expr), context);
    if (new_path != Nil) return new_path;
    else return explain_recur(fsm, enc, path, cdr(formula_expr), context);

  case EX:
    a1 = eval_ctl_spec(fsm, enc, car(formula_expr), context);
    set_the_node(formula_expr);
    new_path = ex_explain(fsm, enc, path, a1);
    bdd_free(dd_manager, a1);
    if (new_path != Nil) {
      node_ptr q = explain_recur(fsm, enc, new_path, car(formula_expr), context);
      if (q != Nil)  return q;
    }
    return new_path;

  case AX:
    return explain_recur(fsm, enc, path, 
			 find_node(NOT, 
				   find_node(EX, 
					     find_node(NOT, car(formula_expr), Nil), 
					     Nil), 
				   Nil), 
			 context);

  case EF:
    return explain_recur(fsm, enc, path, 
			 find_node(EU, one_number, car(formula_expr)), context);

  case AG:
    return explain_recur(fsm, enc, path, 
			 find_node(NOT, find_node(EU, one_number,
						  find_node(NOT, car(formula_expr), Nil)),
				   Nil), 
			 context);
    
  case EG:
    a1 = eval_ctl_spec(fsm, enc, car(formula_expr), context);
    set_the_node(formula_expr);
    new_path = eg_explain(fsm, enc, path, a1);
    bdd_free(dd_manager, a1);
    return new_path;

  case AF:
    /* AF g and !EG !g are equivalent. */
    return explain_recur(fsm, enc, path, 
			 find_node(NOT, find_node(EG, 
						  find_node(NOT, car(formula_expr), 
							    Nil), 
						  Nil),
				   Nil), context);

  case EU:
    a1 = eval_ctl_spec(fsm, enc, car(formula_expr), context);
    a2 = eval_ctl_spec(fsm, enc, cdr(formula_expr), context);
    set_the_node(formula_expr);
    new_path = eu_explain(fsm, enc, path, a1, a2);
    bdd_free(dd_manager, a2);
    bdd_free(dd_manager, a1);
    if (new_path != Nil) {
      node_ptr q = explain_recur(fsm, enc, new_path, cdr(formula_expr), context);

      if (q != Nil) return q;
    }
    return new_path;

  case AU:
    /* A[f U g] and !(E[!g U (!g & !f)] | EG !g) are equivalent. */
    return explain_recur(fsm, enc, path, find_node
			 (NOT, find_node
			  (OR, find_node
			   (EU, find_node
			    (NOT, cdr(formula_expr), Nil), find_node
			    (AND, find_node
			     (NOT, car(formula_expr), Nil), find_node
			     (NOT, cdr(formula_expr), Nil))), find_node
			   (EG, find_node
			    (NOT, cdr(formula_expr), Nil), Nil)), Nil),
			 context);

  case EBU:
    a1 = eval_ctl_spec(fsm, enc, car(car(formula_expr)), context);
    a2 = eval_ctl_spec(fsm, enc, cdr(car(formula_expr)), context);
    {
      int inf = BddEnc_eval_num(enc, car(cdr(formula_expr)), context);
      int sup = BddEnc_eval_num(enc, cdr(cdr(formula_expr)), context);
      
      set_the_node(formula_expr);
      new_path = ebu_explain(fsm, enc, path, a1, a2, inf, sup);
    }
    bdd_free(dd_manager, a2);
    bdd_free(dd_manager, a1);
    
    if (new_path != Nil) {
      node_ptr q = explain_recur(fsm, enc, new_path, cdr(car(formula_expr)), 
				 context);

      if (q != Nil) return q;
    }
    return new_path;

  case ABU:
    /*
      A[f BU l..h g] is equivalent to
      ! ((EBF 0..(l - 1) !f)
        | EBG l..l ((EBG 0..(h - l) !g)
              | E[!g BU 0..(h - l) (!g & !f)]))
     
      f:car(car(formula_expr)) g:cdr(car(formula_expr)) l:car(cdr(l)) h:cdr(car(formula_expr))
    */
    return (explain_recur(fsm, enc, path, find_node
			  (NOT, find_node
			   (OR, find_node
			    (EBF, find_node
			     (NOT, car(car(formula_expr)), Nil), find_node
			     (TWODOTS,
			      zero_number, find_node
			      (MINUS, car(cdr(formula_expr)), one_number))), find_node
			    (EBG, find_node
			     (OR, find_node
			      (EBG, find_node
			       (NOT, cdr(car(formula_expr)), Nil), find_node
			       (TWODOTS,
				zero_number, find_node
				(MINUS,
				 cdr(cdr(formula_expr)),
				 car(cdr(formula_expr))))), find_node
			      (EBU, find_node
			       (EU, find_node
				(NOT, cdr(car(formula_expr)), Nil), find_node
				(AND, find_node
				 (NOT, car(car(formula_expr)), Nil), find_node
				 (NOT, cdr(car(formula_expr)), Nil))), find_node
			       (TWODOTS,
				zero_number, find_node
				(MINUS,
				 cdr(cdr(formula_expr)),
				 car(cdr(formula_expr)))))), find_node
			     (TWODOTS,
			      car(cdr(formula_expr)),
			      car(cdr(formula_expr))))), Nil),
			  context));
    
  case EBF:
    /* EBF range g and E[1 BU range g] are equivalent.  */
    return (explain_recur(fsm, enc, path, find_node
			  (EBU, find_node
			   (EU, one_number, car(formula_expr)),
			   cdr(formula_expr)),
			  context));
    
  case ABG:
    /* ABG range g and !EBF range !g are equivalent. */
    return (explain_recur(fsm, enc, path, find_node
			  (NOT, find_node
			   (EBF, find_node
			    (NOT, car(formula_expr), Nil),
			    cdr(formula_expr)), Nil),
			  context));

  case EBG:
    a1 = eval_ctl_spec(fsm, enc, car(formula_expr), context);
    {
      int inf = BddEnc_eval_num(enc, car(cdr(formula_expr)), context);
      int sup = BddEnc_eval_num(enc, cdr(cdr(formula_expr)), context);

      set_the_node(formula_expr);
      new_path = ebg_explain(fsm, enc, path, a1, inf, sup);
    }
    bdd_free(dd_manager, a1);
    return new_path;
    
  case ABF:
    /* ABF range g and !EBG range !g are equivalent. */
    return (explain_recur(fsm, enc, path, find_node
			  (NOT, find_node
			   (EBG, find_node
			    (NOT, car(formula_expr), Nil),
			    cdr(formula_expr)), Nil),
			  context));

  case ATOM:
    {
      SymbTable_ptr st = BaseEnc_get_symbol_table(BASE_ENC(enc));
      node_ptr name  = find_node(DOT, context, find_atom(formula_expr));
      boolean is_decl = SymbTable_is_symbol_declared(st, name);
      boolean is_const = SymbTable_is_symbol_constant(st, find_atom(formula_expr));
      node_ptr par = lookup_param_hash(name);

      if((par && is_decl) || (par && is_const) || (is_decl && is_const)) {
        rpterr("atom \"%s\" is ambiguous", 
	       str_get_text(node_get_lstring(formula_expr)));
      }
      if (par) return explain_recur(fsm, enc, path, par, context); 
      if (is_const) return Nil;
      
      /* otherwise continue below */
    } /* fall through on purpose here */

  case DOT:
  case ARRAY:
    {
      SymbTable_ptr st;
      node_ptr name; 

      st = BaseEnc_get_symbol_table(BASE_ENC(enc));
      name = CompileFlatten_resolve_name(st, formula_expr, context);
      if (!SymbTable_is_symbol_declared(st, name)) {
	error_undefined(name);
      }

      if (SymbTable_is_symbol_define(st, name)) {
	return explain_recur(fsm, enc, path, 
			     SymbTable_get_define_body(st, name), 
			     SymbTable_get_define_context(st, name));
      }
      else return Nil;
    }

  default:
    return Nil;
  }
}

/**Function********************************************************************

  Synopsis           [Auxiliary function to the computation of a
  witness of the formula <i>EG f</i>.]

  Description        [In the computation of the witness for the
  formula <i>EG f</i>, at each step we must ensure that the current
  prefix can be extended to a fair path along which each state
  satisfies <i>f</i>. This function performs the inner fixpoint
  computation for each fairness constraints in the fix point
  computation of the formula <i>EG(f)<i>. For every constraints
  <i>h</i>, we obtain an increasing sequence of approximations Q_0^h,
  Q_1^h, ..., where each Q_i^h is the set of states from which a state
  in the accumulated set can be reached in <i>i</i> or fewer steps,
  while satisfying <i>f</i>.]

  SideEffects        []

  SeeAlso            [explain, eg_explain, fair_iter, eg]

******************************************************************************/
static node_ptr fairness_explain(BddFsm_ptr fsm, BddEnc_ptr enc,
				 node_ptr witness_path, bdd_ptr hulk_si,
				 JusticeList_ptr fairness_constrainst_list)
{
  FairnessListIterator_ptr iter;
  node_ptr res;
  bdd_ptr hulk;

  /* initializatoin of local variables */
  res = witness_path;
  hulk = BddFsm_states_inputs_to_states(fsm, hulk_si);

  iter = FairnessList_begin( FAIRNESS_LIST(fairness_constrainst_list) );
  while( ! FairnessListIterator_is_end(iter) ) {
    BddStates fc_si;
    BddStatesInputs hulk_fc_si;
    BddStatesInputs fair_si;

    /* Select fairness constraint */
    fc_si = JusticeList_get_p(fairness_constrainst_list, iter);

    /* Conjunct with fair transitions */
    fair_si = BddFsm_get_fair_states_inputs(fsm);
    bdd_and_accumulate(dd_manager, &fc_si, fair_si);

    /* Conjunct it with the current set */
    hulk_fc_si = bdd_and(dd_manager, hulk_si, fc_si);

    /* 
       Construct a path to the fairness constraint without leaving the hulk
    */
    res = eu_si_explain(fsm, enc, res, hulk, hulk_fc_si, hulk); 

    bdd_free(dd_manager, fc_si); 
    bdd_free(dd_manager, fair_si);
    bdd_free(dd_manager, hulk_fc_si);

    iter = FairnessListIterator_next(iter);      
  } /* loop */
  
  bdd_free(dd_manager, hulk);
  return res;
}

/**Function********************************************************************

  Synopsis           []

  Description        []

  SideEffects        []

  SeeAlso            []

******************************************************************************/
static node_ptr Extend_trace_with_state_input_pair(BddFsm_ptr fsm,
                                                   BddEnc_ptr enc,
                                                   node_ptr path,
                                                   bdd_ptr starting_state,
                                                   bdd_ptr next_states,
                                                   const char * comment)
{
  node_ptr res;
  bdd_ptr next_state, inputs, input;
  size_t size = strlen(comment) + 10;
  char * com = ALLOC(char, size);

  snprintf(com, size, "%s: (%d)", com, 1);

#ifdef EXPLAIN_TRACE_DEBUG
  Check_TraceList_Sanity(enc, path, com);
#endif

  next_state = BddEnc_pick_one_state(enc, next_states);
  inputs = BddFsm_states_to_states_get_inputs(fsm, starting_state, next_state);
  input = BddEnc_pick_one_input(enc, inputs);

  res = cons((node_ptr) bdd_dup(next_state),
		cons((node_ptr) bdd_dup(input), path));

  snprintf(com, size, "%s: (%d)", com, 2);

#ifdef EXPLAIN_TRACE_DEBUG
  Check_TraceList_Sanity(enc, res, com);
#endif

  bdd_free(dd_manager, input);
  bdd_free(dd_manager, inputs);
  bdd_free(dd_manager, next_state);

  return res;
}


/**Function********************************************************************

  Synopsis           []

  Description        []

  SideEffects        []

  SeeAlso            []

******************************************************************************/
static node_ptr Extend_trace_with_states_inputs_pair(BddFsm_ptr fsm,
                                                     BddEnc_ptr enc,
                                                     node_ptr path,
                                                     bdd_ptr starting_states,
                                                     bdd_ptr next_states,
                                                     const char * comment)
{
  node_ptr res;
  bdd_ptr inputs;
  size_t size = strlen(comment) + 10;
  char * com = ALLOC(char, size);

  snprintf(com, size, "%s: (%d)", com, 1);

#ifdef EXPLAIN_TRACE_DEBUG
  Check_TraceList_Sanity(enc, path, com);
#endif

  inputs = BddFsm_states_to_states_get_inputs(fsm, starting_states, next_states);

  res = cons((node_ptr) bdd_dup(next_states),
		cons((node_ptr) bdd_dup(inputs), path));

  snprintf(com, size, "%s: (%d)", com, 2);

#ifdef EXPLAIN_TRACE_DEBUG
  Check_TraceList_Sanity(enc, res, com);
#endif

  bdd_free(dd_manager, inputs);

  return res;
}

/**Function********************************************************************

  Synopsis           []

  Description        []

  SideEffects        []

  SeeAlso            []

******************************************************************************/
void Check_TraceList_Sanity(BddEnc_ptr enc, node_ptr path,
                            const char * varname)
{
  int i, l;
  node_ptr scan;
  DdManager* dd = BddEnc_get_dd_manager(enc);
  boolean must_abort = 0;

  l = llength(path);
  scan = path;

  fprintf(nusmv_stderr, "Checking TraceList Sanity: %s\n", varname);
  fprintf(nusmv_stderr, "Length of list: %d\n", l);

  for (i = 0; i < l; i++) {
    bdd_ptr elem = (bdd_ptr)car(scan);

    if ( (i % 2) == 0 ) {
      fprintf(nusmv_stderr,  "STATE(%d):\n", i);
    }
    else {
      fprintf(nusmv_stderr,  "INPUT(%d):\n", i);
    }
    nusmv_assert( elem != (bdd_ptr) NULL );
    must_abort = (must_abort || bdd_is_zero(dd, elem));
    if ( must_abort ) {
      fprintf(nusmv_stderr,  "**** DOOMED TO ABORT, STEP %d\n", i);
    }
    dd_printminterm(dd, elem);

    scan = cdr(scan);
  }
  if ( must_abort ) nusmv_assert( 0 );
}

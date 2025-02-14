/**CFsaile***********************************************************************

  FileName    [SatZchaff.c]

  PackageName [SatZchaff]

  Synopsis    [Routines related to SatZchaff object.]

  Description [ This file contains the definition of \"SatZchaff\" class.
  The solver contains its own coding of varibales, so input variables may
  by in any range from 1 .. INT_MAX, with possible holes in the range.

  Group Control:
   To control groups, every group has its ID, which is an usual internal 
   variable. If a formula is added to a permanent group, then literals are just
   converted into the internal literals and the clauses are permamently 
   added to the solver.
   If a formula is added to non-permanent group, then after convertion of 
   literals, every clause in the  group will additionally obtain one 
   literal which is just group id, and then the clauses are added permanently
   to solver.
   Then if a group is turn on, then just its negated ID is added temporary to 
   the solver. If we want to turn the group off, the just its ID
   is added temporary to the solver.

  ]
		
  SeeAlso     []

  Author      [Andrei Tchaltsev]

  Copyright   [
  This file is part of the ``sat'' package of NuSMV version 2. 
  Copyright (C) 2004 by ITC-irst.

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

#include "SatZchaff_private.h"
#include "node/node.h"

static char rcsid[] UTIL_UNUSED = "$Id: SatZchaff.c,v 1.1.2.5.2.1.6.2 2007/03/20 19:30:14 nusmv Exp $";

/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Static function prototypes                                                */
/*---------------------------------------------------------------------------*/
static void sat_zchaff_finalize ARGS((Object_ptr object, void *dummy));

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Definition of external functions                                          */
/*---------------------------------------------------------------------------*/

/**Function********************************************************************

  Synopsis    [Creates a Zchaff SAT solver and initializes it.]

  Description [The first parameter is the name of the solver.]

  SideEffects []

  SeeAlso     []

******************************************************************************/
SatZchaff_ptr SatZchaff_create(const char* name)
{
  SatZchaff_ptr self = ALLOC(SatZchaff, 1);

  SAT_ZCHAFF_CHECK_INSTANCE(self);

  sat_zchaff_init(self, name);
  return self;
}

/**Function********************************************************************

  Synopsis    [Destroys a Zchaff SAT solver instence]

  Description [The first parameter is the name of the solver.]

  SideEffects []

  SeeAlso     []

******************************************************************************/
void SatZchaff_destroy (SatZchaff_ptr self)
{
  SatSolver_destroy(SAT_SOLVER(self));
}

/* ---------------------------------------------------------------------- */
/* Private  Methods                                                       */
/* ---------------------------------------------------------------------- */

/**Function********************************************************************

  Synopsis    [Convert a cnf literal into an internal literal used by zchaff]

  Description [The literal may not be 0 (because 0 cannot have sign).
  First, the function obtains the cnf variable (removes the sign),
  obtains associated internal var through hash table(creates if necessary
  an internal variable) 
  and then converts it in zchaff literal (var*2+sign, see ZChaff SAT.h).
  If necessary a new minisat variable is created.]

  SideEffects []

  SeeAlso     [sat_zchaff_zchaffLiteral2cnfLiteral]

******************************************************************************/
int sat_zchaff_cnfLiteral2zchaffLiteral(SatZchaff_ptr self, int cnfLiteral)
{
  int cnfVar = abs(cnfLiteral);
  int zchaffVar;
  
  SAT_ZCHAFF_CHECK_INSTANCE(self);
  nusmv_assert(0 < cnfVar);
  /* it is important for  sat_zchaff_zchaffLiteral2cnfLiteral */
  nusmv_assert( (int)Nil != cnfVar );
  
  zchaffVar = NODE_TO_INT(find_assoc(self->cnfVar2zchaffVar, 
				     NODE_FROM_INT(cnfVar)));
 
  if ((int)Nil == zchaffVar) { 
    /* create a new internal var and associate with cnf */
    zchaffVar = SAT_AddVariable(self->zchaffSolver);
    insert_assoc(self->cnfVar2zchaffVar, 
		 NODE_FROM_INT(cnfVar), NODE_FROM_INT(zchaffVar));

    insert_assoc(self->zchaffVar2cnfVar, 
		 NODE_FROM_INT(zchaffVar), NODE_FROM_INT(cnfVar));
  }

  return 2*zchaffVar + (cnfLiteral < 0);
};

/**Function********************************************************************

  Synopsis    [Convert an internal zchaff literal into a cnf literal]

  Description [The variable in the literal has to be created by 
  sat_zchaff_cnfLiteral2zchaffLiteral only.
  First, the function obtains the zchaff variable from the literal,
  obtains associated cnf variable (there must already be the association),
  and then converts it in cnf literal (add the sign)]

  SideEffects []

  SeeAlso     [sat_zchaff_cnfLiteral2zchaffLiteral]

******************************************************************************/
int sat_zchaff_zchaffLiteral2cnfLiteral(SatZchaff_ptr self, int zchaffLiteral)
{
  int zchaffVar = zchaffLiteral >> 1; /* 2*var+sign == literal */
  int cnfVar = NODE_TO_INT(find_assoc(self->zchaffVar2cnfVar, 
				      NODE_FROM_INT(zchaffVar)));

#if 0 
  We cannot check that cnfVar != Nil, since some internal variables 
  can be used as group id-s.
  We cannnot check that internal variable is a group id, because 
  some groups may be deleted and their id-s are removed from the list
  'existing group'.

  /* cnf var is Nill only if the corresponding internal var represents 
     a group id, otherwise is always greater then 0 */
  nusmv_assert( ((int) Nil != cnfVar) ||
		sat_solver_BelongToList(SAT_SOLVER(self)->existingGroups,
					(lsGeneric)zchaffVar) );
#endif
  
  return (zchaffLiteral & 1) ? -cnfVar : cnfVar;
};

/* ---------------------------------------------------------------------- */
/* Static Methods                                                         */
/* ---------------------------------------------------------------------- */

/**Function********************************************************************

  Synopsis    [Adds a clause to the solver database.]

  Description [converts all CNF literals into the internal literals,
  adds a group id to every clause (if group is not permament) and then add
  obtained clauses to actual ZChaff]

  SideEffects []

  SeeAlso     []

******************************************************************************/
void sat_zchaff_add(const SatSolver_ptr solver,
		    const Be_Cnf_ptr cnfProb,
		    SatSolverGroup group)
{
  SatZchaff_ptr self = SAT_ZCHAFF(solver);

  lsList clause;
  lsGen genClause, genLiteral;
  nusmv_ptrint literal;
  int literalNumber;
  /* buffer to hold zchaff's clauses. I think a usual clause will be 2-4 literal
   and in any case it will not be more then 1000 literal (there is an 
   assertion to check it) */
  static int zchaffClause[1000]; 
  /* just for efficiency */
  const int groupIsNotPermanent = 
    SatSolver_get_permanent_group(SAT_SOLVER(self)) != group;


  SAT_ZCHAFF_CHECK_INSTANCE(self);
  
  lsForEachItem( Be_Cnf_GetClausesList(cnfProb), genClause, clause) {
    literalNumber = 0;
    nusmv_assert(1000>lsLength(clause)); /* see zchaffClause above */
    lsForEachItem(clause, genLiteral, literal) {
      zchaffClause[literalNumber] = 
	sat_zchaff_cnfLiteral2zchaffLiteral(self, (int) literal);
      ++literalNumber;
    }

    if (groupIsNotPermanent) { /* add literal with group id the the clause */
	zchaffClause[literalNumber] = group*2;
	++literalNumber;
      }
    /* add to real zchaff group 0 (real permanent one) */
    SAT_AddClause(self->zchaffSolver, zchaffClause, literalNumber, 0);
  }
}

/**Function********************************************************************

  Synopsis    [Sets the polarity of the formula.]

  Description [Sets the polarity of the formula.
  Polarity 1 means the formula is considered as positive, and -1 means
  the negation of the formula will be solved.
  A unit clause of the literal (with sign equal to polarity) 
  corresponding to the given CNF formula is added to the solve.]

  SideEffects []

  SeeAlso     []

******************************************************************************/
void sat_zchaff_set_polarity(const SatSolver_ptr solver,
			     const Be_Cnf_ptr cnfProb,
			     int polarity,
			     SatSolverGroup group)
{
  SatZchaff_ptr self = SAT_ZCHAFF(solver);

  int cnfLiteral;
  int zchaffLiteral;
  int zchaffClause[5]; /* only one or two literals may be in the clause */

  SAT_ZCHAFF_CHECK_INSTANCE(self);

  cnfLiteral = polarity * Be_Cnf_GetFormulaLiteral(cnfProb);
  zchaffLiteral = sat_zchaff_cnfLiteral2zchaffLiteral ( self,
							cnfLiteral);
  zchaffClause[0] = zchaffLiteral; 
  
  if ( SatSolver_get_permanent_group(SAT_SOLVER(self)) == group ) {
    SAT_AddClause(self->zchaffSolver, zchaffClause, 1, 0);
  }
  else { /* add group id to clause to controle the cnf formula */
    zchaffClause[1] = group*2;
    SAT_AddClause(self->zchaffSolver, zchaffClause, 2, 0);
  }
}

/**Function********************************************************************

  Synopsis    [Tries to solve all added formulas]

  Description []

  SideEffects []

  SeeAlso     []

******************************************************************************/
SatSolverResult sat_zchaff_solve_all_groups(const SatSolver_ptr solver)
{
  SatZchaff_ptr self = SAT_ZCHAFF(solver);
  SAT_ZCHAFF_CHECK_INSTANCE(self);

  return sat_zchaff_solve_groups(SAT_INC_SOLVER(self), 
				 SAT_SOLVER(self)->existingGroups);
}

/**Function********************************************************************

  Synopsis    [This function creates a model (in the original cnf variables)]

  Description [The previous invocation of SAT_Solve should have been successful]

  SideEffects []

  SeeAlso     []

******************************************************************************/
lsList sat_zchaff_make_model (const SatSolver_ptr solver)
{
  SatZchaff_ptr self = SAT_ZCHAFF(solver);
  int index;
  int varNumber;
  lsList model = lsCreate();

  SAT_ZCHAFF_CHECK_INSTANCE(self);
  /* a model is created only if there is no model */
  nusmv_assert((lsList)NULL == SAT_SOLVER(self)->model);

  varNumber = SAT_NumVariables(self->zchaffSolver);
  
  for (index = 1; index <= varNumber; ++index) {
    int cnfLiteral = sat_zchaff_zchaffLiteral2cnfLiteral(self, index*2);

    if (cnfLiteral > 0) { /* it is a real variable */ 
      int value = SAT_GetVarAsgnment(self->zchaffSolver, index);

      switch (value) {
      case 0: /* negative polarity => change the CNF var's polarity */
	cnfLiteral = -cnfLiteral;
      case 1: /* polarity is  polarity => do nothing */
	/* appends the model: */
	lsNewEnd(model, PTR_FROM_INT(lsGeneric, cnfLiteral), LS_NH); 
	break;
      case UNKNOWN:
	break;
      }
    }
    else { /* just debugging */
#if 0 
  We cannot check that cnfVar != Nil, since some internal variables 
  can be used as group id-s.
  We cannnot check that internal variable is a group id, because 
  some groups may be deleted and their id-s are removed from the list
  'existing group'.
#endif
    } /* if (UNKSNOW != value) */
  } /* for() */
  return model;
};
  
/**Function********************************************************************

  Synopsis    [Creates a new group and returns its ID ]

  Description [Adds the group at the END of the existing groups list]

  SideEffects []

  SeeAlso     [SatIncSolver_destroy_group,
  SatIncSolver_move_to_permanent_and_destroy_group]

******************************************************************************/
SatSolverGroup
sat_zchaff_create_group(const SatIncSolver_ptr solver)
{
  SatZchaff_ptr self = SAT_ZCHAFF(solver);
  int newGroup;
  SAT_ZCHAFF_CHECK_INSTANCE(self);
  newGroup = SAT_AddVariable(self->zchaffSolver);
  lsNewEnd(SAT_SOLVER(self)->existingGroups, 
	   PTR_FROM_INT(lsGeneric, newGroup), 0);
  return newGroup;
};


/**Function********************************************************************

  Synopsis    [Destroy an existing group (which has been returned by
  SatIncSolver_create_group) and all formulas in it. ]

  Description [Just adds to the solver a unit clause with positive literal 
  of a variable with index  equal to group id ]

  SideEffects []

  SeeAlso     [SatIncSolver_create_group]

******************************************************************************/
void
sat_zchaff_destroy_group(const SatIncSolver_ptr solver,
			 SatSolverGroup group)
{
  SatZchaff_ptr self = SAT_ZCHAFF(solver);
  int zchaffClause[2];


  SAT_ZCHAFF_CHECK_INSTANCE(self);
  /* it should not be a permanent group */
  nusmv_assert(SatSolver_get_permanent_group(SAT_SOLVER(self)) != group);
  /* the group should exist */
  nusmv_assert(sat_solver_BelongToList(SAT_SOLVER(self)->existingGroups,
				       (lsGeneric)group));


  /* delete the group from the lists */
  sat_solver_RemoveFromList(SAT_SOLVER(self)->existingGroups,
			    (lsGeneric)group); 
  sat_solver_RemoveFromList(SAT_SOLVER(self)->unsatisfiableGroups,
			    (lsGeneric)group); 

  /* add literal corresponding to group id to the solver (to 
     make all clauses contaning it true, so useless */
  zchaffClause[0] = group*2;
  SAT_AddClause(self->zchaffSolver, zchaffClause, 1, 0);
};

/**Function********************************************************************

  Synopsis    [Moves all formulas from a group into the permanent group of
  the solver and then destroy the given group.]

  Description [just adds to zchaff a unit clause with negative literal 
  of a variable with index equal to group id]

  SideEffects []

  SeeAlso     [SatIncSolver_create_group, SatSolver_get_permanent_group,
  ]

******************************************************************************/
void
sat_zchaff_move_to_permanent_and_destroy_group(const SatIncSolver_ptr solver,
					       SatSolverGroup group)
{
  SatZchaff_ptr self = SAT_ZCHAFF(solver);
  int zchaffClause[2];
  /* shortcut */
  SatSolverGroup permamentGroup;

  SAT_ZCHAFF_CHECK_INSTANCE(self);

  permamentGroup = SatSolver_get_permanent_group(SAT_SOLVER(self));
  
  /* it should not be a permanent group */
  nusmv_assert( permamentGroup != group);
  /* the group should exist */
  nusmv_assert(sat_solver_BelongToList(SAT_SOLVER(self)->existingGroups,
				       (lsGeneric)group));

  /* if the group is unsatisfiable, make the permanent group unsatisfiable */
  if (sat_solver_BelongToList(SAT_SOLVER(self)->unsatisfiableGroups,
			      (lsGeneric)group) &&
      ! sat_solver_BelongToList(SAT_SOLVER(self)->unsatisfiableGroups,
				(lsGeneric)permamentGroup) ) {
    lsNewBegin(SAT_SOLVER(self)->unsatisfiableGroups, 
	       (lsGeneric)permamentGroup, 
	       0);
  }


  /* delete the group from the lists */
  sat_solver_RemoveFromList(SAT_SOLVER(self)->existingGroups,
			    (lsGeneric)group); 
  sat_solver_RemoveFromList(SAT_SOLVER(self)->unsatisfiableGroups,
			    (lsGeneric)group); 

  /* add negated literal corresponding to group id to the solver (to 
     remove the group id literal from all the clauses belonding to the group */
  zchaffClause[0] = group * 2 + 1;
  SAT_AddClause(self->zchaffSolver, zchaffClause, 1, 0);
};

/**Function********************************************************************

  Synopsis    [Tries to solve formulas from the groups in the list.]

  Description [The permanent group is automatically added to the list.
  Returns a flag whether the solving was successful. If it was successful only
  then SatSolver_get_model may be invoked to obtain the model ]

  SideEffects []

  SeeAlso     []

******************************************************************************/
SatSolverResult
sat_zchaff_solve_groups(const SatIncSolver_ptr solver, const lsList groups)
{
  SatZchaff_ptr self = SAT_ZCHAFF(solver);
  int zchaffClause[2];
  int zchaffGroup;
  int zchaffResult;
  SatSolverGroup permanentGroup;

  lsGen gen;
  lsGeneric data;
  SatSolverGroup aGroup;
  
  SAT_ZCHAFF_CHECK_INSTANCE(self);

  permanentGroup = SatSolver_get_permanent_group(SAT_SOLVER(self));

  /* if the permanent group is unsatisfiable => return
     We check it here becuase the input list may not contain permenent group */
  if (sat_solver_BelongToList(SAT_SOLVER(self)->unsatisfiableGroups,
			      (lsGeneric)permanentGroup) ) {
    return SAT_SOLVER_UNSATISFIABLE_PROBLEM;
  }

  /* create a new group in actual zchaff */
  zchaffGroup = SAT_AllocClauseGroupID(self->zchaffSolver);

  nusmv_assert(-1 != zchaffGroup);

  lsForEachItem (groups, gen, data) {
    aGroup = (SatSolverGroup) data;
    /* the group must exist */
    nusmv_assert(sat_solver_BelongToList(SAT_SOLVER(self)->existingGroups,
					 (lsGeneric)aGroup));
    /* the group is unsatisfiable => exit */
    if (sat_solver_BelongToList(SAT_SOLVER(self)->unsatisfiableGroups,
				(lsGeneric)aGroup)) {
      lsFinish(gen);
      SAT_DeleteClauseGroup(self->zchaffSolver, zchaffGroup);
      return SAT_SOLVER_UNSATISFIABLE_PROBLEM;
    }
        
    /* add negated literal of the group id to the solver (if this is not 
       a permanent group) */
    if (permanentGroup != aGroup) {
      zchaffClause[0] = aGroup * 2 + 1;
      SAT_AddClause(self->zchaffSolver, zchaffClause, 1, zchaffGroup);
    }
  }; /* end of lsForEachItem */

  /* try to solver */
  SAT_Reset(self->zchaffSolver);
  zchaffResult = SAT_Solve(self->zchaffSolver);

  if (SATISFIABLE == zchaffResult) {
    SatSolver_get_model(SAT_SOLVER(self)); /* build the model */
  }
  
  SAT_DeleteClauseGroup(self->zchaffSolver, zchaffGroup);

  switch(zchaffResult) {
  case UNSATISFIABLE:   return SAT_SOLVER_UNSATISFIABLE_PROBLEM;
  case SATISFIABLE:     return SAT_SOLVER_SATISFIABLE_PROBLEM;
  case TIME_OUT:
  case MEM_OUT: 
  case ABORTED:         return SAT_SOLVER_INTERNAL_ERROR;
  default: nusmv_assert(false);
  };
 
  nusmv_assert(false); /* this code cannot be achieved. */
  return SAT_SOLVER_INTERNAL_ERROR;
};


/**Function********************************************************************

  Synopsis    [Tries to solve formulas in groups belonging to the solver
  except the groups in the list.]

  Description [The permanent group must not be in the list.
  Returns a flag whether the solving was successful. If it was successful only
  then SatSolver_get_model may be invoked to obtain the model ]

  SideEffects []

  SeeAlso     [SatSolverResult,SatSolver_get_permanent_group,
  SatIncSolver_create_group, SatSolver_get_model]

******************************************************************************/
SatSolverResult
sat_zchaff_solve_without_groups(const SatIncSolver_ptr solver,
				const lsList groups)
{
  SatZchaff_ptr self = SAT_ZCHAFF(solver);
  SatSolverResult result;
  lsList includeGroups;
  lsGen gen;
  SatSolverGroup aGroup;

  SAT_ZCHAFF_CHECK_INSTANCE(self);
  /* the permanent group is not in the list */
  nusmv_assert(!sat_solver_BelongToList
               (groups,
		(lsGeneric)SatSolver_get_permanent_group(SAT_SOLVER(self))));
  
  /* create a list of all groups except the groups in the list */
  includeGroups = lsCreate();
  lsForEachItem(SAT_SOLVER(self)->existingGroups, gen, aGroup)
    {
      if(!sat_solver_BelongToList(groups, (lsGeneric)aGroup)) {
	lsNewEnd(includeGroups, (lsGeneric)aGroup, 0);
      }
    }
  result = sat_zchaff_solve_groups(solver, includeGroups);
  lsDestroy(includeGroups, 0);
  return result;
};



/*---------------------------------------------------------------------------*/
/* Initializer, De-initializer, Finalizer                                    */
/*---------------------------------------------------------------------------*/
/**Function********************************************************************

  Synopsis    [Initializes Sat Zchaff object.]

  Description []

  SideEffects []

  SeeAlso     []

******************************************************************************/
void sat_zchaff_init(SatZchaff_ptr self, const char* name) 
{
  lsStatus status;
  lsGeneric data;

  SAT_ZCHAFF_CHECK_INSTANCE(self);

  sat_inc_solver_init(SAT_INC_SOLVER(self), name);

  OVERRIDE(Object, finalize) = sat_zchaff_finalize;

  OVERRIDE(SatSolver, add) = sat_zchaff_add;
  OVERRIDE(SatSolver, set_polarity) = sat_zchaff_set_polarity;
  OVERRIDE(SatSolver, solve_all_groups) = sat_zchaff_solve_all_groups;
  OVERRIDE(SatSolver, make_model) = sat_zchaff_make_model;

  OVERRIDE(SatIncSolver, create_group) = sat_zchaff_create_group;
  OVERRIDE(SatIncSolver, destroy_group) = sat_zchaff_destroy_group;
  OVERRIDE(SatIncSolver, move_to_permanent_and_destroy_group) 
    = sat_zchaff_move_to_permanent_and_destroy_group;
  OVERRIDE(SatIncSolver, solve_groups) = sat_zchaff_solve_groups;
  OVERRIDE(SatIncSolver, solve_without_groups)
    = sat_zchaff_solve_without_groups;

  /* set a (new) proper zchaff permanent group, i.e. '0' */
  status = lsDelBegin(SAT_SOLVER(self)->existingGroups, &data);
  /* first element of the list is always the permanent group */
  nusmv_assert(LS_OK == status);
  /* In SatSolver fake permanent group was -1. Just check. */
  nusmv_assert(-1 == (nusmv_ptrint) data);
  lsNewBegin(SAT_SOLVER(self)->existingGroups, 
	     (lsGeneric)0, 
	     0);
  
  self->zchaffSolver = SAT_InitManager();
  self->cnfVar2zchaffVar = new_assoc();
  self->zchaffVar2cnfVar = new_assoc();
}

/**Function********************************************************************

  Synopsis    [Deinitializes SatZchaff object.]

  Description []

  SideEffects []

  SeeAlso     []

******************************************************************************/
void sat_zchaff_deinit(SatZchaff_ptr self)
{ 
  SAT_ZCHAFF_CHECK_INSTANCE(self);

  free_assoc(self->cnfVar2zchaffVar);
  free_assoc(self->zchaffVar2cnfVar);
  
  SAT_ReleaseManager(self->zchaffSolver);

  sat_solver_deinit(SAT_SOLVER(self));
}
/**Function********************************************************************

  Synopsis    [Finalize method of SatZchaff class.]

  Description [Pure virtual function. This must be refined by derived classes.]

  SideEffects []

  SeeAlso     []

******************************************************************************/
static void sat_zchaff_finalize(Object_ptr object, void* dummy)
{
  SatZchaff_ptr self = SAT_ZCHAFF(object);
  sat_zchaff_deinit(self);
  FREE(self);
}


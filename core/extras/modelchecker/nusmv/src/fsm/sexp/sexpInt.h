/**CHeaderFile*****************************************************************

  FileName    [sexpInt.h]

  PackageName [fsm.sexp]

  Synopsis    [Internal sexp API package interface]

  Description []

  SeeAlso     [sexp.h]

  Author      [Roberto Cavada]

  Copyright   [
  This file is part of the ``fsm.sexp'' package of NuSMV version 2. 
  Copyright (C) 2003 by ITC-irst. 

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

#ifndef __FSM_SEXP_SEXP_INT__H
#define __FSM_SEXP_SEXP_INT__H

#include "utils/utils.h"
#include "node/node.h"
#include "opt/opt.h"


EXTERN FILE* nusmv_stderr;
EXTERN FILE* nusmv_stdout;

EXTERN options_ptr options;

EXTERN int yylineno;


#endif /* __FSM_SEXP_SEXP_INT__H */

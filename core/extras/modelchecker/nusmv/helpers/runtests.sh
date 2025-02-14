#!/bin/sh 

######################################################################
# FileName    [runtests.sh]
#
# Synopsis    [Script to perform sequential regression tests]
#
# Description [runs the regression_test script for a set of smv sources]
#
# Author      [Roberto Cavada]
#
# Copyright	[Copyright (C) 1998-2001 by ITC-irst. 
#
# NuSMV version 2 is free software; you can redistribute it and/or 
# modify it under the terms of the GNU Lesser General Public 
# License as published by the Free Software Foundation; either 
# version 2 of the License, or (at your option) any later version.
#
# NuSMV version 2 is distributed in the hope that it will be useful, 
# but WITHOUT ANY WARRANTY; without even the implied warranty of 
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public 
# License along with this library; if not, write to the Free Software 
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA.
#
# For more information on NuSMV see <http://nusmv.irst.itc.it>
# or email to <nusmv-users@irst.itc.it>.
# Please report bugs to <nusmv-users@irst.itc.it>.
#
# To contact the NuSMV development board, email to <nusmv@irst.itc.it>.]
#
######################################################################

#examples="smv-dist/gigamax.smv smv-dist/short.smv smv-dist/semaphore.smv"
examples="smv-dist/gigamax.smv smv-dist/short.smv"

for smv in ${examples}; do
    helpers/regression_test . examples/$smv
done


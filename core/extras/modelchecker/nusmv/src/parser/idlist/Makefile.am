# FileName	[ Makefile ]
#
# PackageName	[ NuSMV ]
#
# Synopsis	[ Package-wide Makefile ]
#
# Description	[ Makefile template for the id list parser ]
#
# Author	[ Roberto Cavada ]
#
# Copyright	[Copyright (C) 2006 by ITC-irst ]
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
# Revision	[v 1.1.2.1 2003/11/27 09:11:47]

CUDD_NAME = $(cuddname)
CUDD_DIR = ../$(CUDD_NAME)

INCLUDES = -I. -I../.. -I$(top_srcdir)/src -I$(top_srcdir)/$(CUDD_DIR)/include

BUILT_SOURCES = idlist_grammar.h idlist_grammar.c idlist_input.c 

MOSTLYCLEANFILES = idlist_grammar.h idlist_grammar.c idlist_input.c

AM_YFLAGS = -d -p parser_idlist_
AM_LFLAGS = -l

PKG_SOURCES = idlist_grammar.y idlist_input.l idlist_int.h  \
              ParserIdList.h  ParserIdList_private.h  ParserIdList.c

parseridlistdir = $(includedir)/nusmv/parser/idlist
parseridlist_HEADERS = ParserIdList.h

# -------------------------------------------------------

noinst_LTLIBRARIES = libparseridlist.la
libparseridlist_la_SOURCES = $(PKG_SOURCES)


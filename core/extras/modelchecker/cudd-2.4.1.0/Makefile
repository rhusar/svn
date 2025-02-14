# $Id: Makefile,v 1.1.2.1.2.5 2007/05/10 17:37:13 nusmv Exp $
#
# Makefile for the CUDD distribution kit 
#---------------------------------------------------------------------------

# NuSMV: added begin
# replaced every occurence of make with $(MAKE) to make it possible to
# execute it in systems in which you have only the gmake installed.
# NuSMV: added end

# Beginning of the configuration section. These symbol definitions can
# be overridden from the command line.

# C++ compiler
CPP	= g++
#CPP	= icpc
#CPP	= ecpc
#CPP	= CC
#CPP	= /usr/local/opt/SUNWspro/bin/CC
#CPP	= cxx

# Specific options for compilation of C++ files.
CPPFLAGS =
# Stricter standard conformance for g++.
#CPPFLAGS = -std=c++98
# For Sun CC version 5, this invokes compatibility mode.
#CPPFLAGS = -compat
# On some versions of UP-UX, it is necessary to pass the option +a1
# to CC for the C++ test program to compile successfully.
#CPPFLAGS = +a1

# C compiler used for all targets except optimize_dec, which always uses cc.
#CC	= cc
#CC	= /usr/local/opt/SUNWspro/bin/cc
CC	= gcc
#CC	= icc
#CC	= ecc
#CC	= /usr/ucb/cc
#CC	= c89
#CC	= $(CPP)

# On some machines ranlib is either non-existent or redundant.
# Use the following definition if your machine has ranlib and you think
# it is needed.
RANLIB	= ranlib
# Use the following definition if your machine either does not have
# ranlib (e.g., SUN running solaris) or can do without it (e.g., DEC Alpha).
#RANLIB	= :

# Use ICFLAGS to specify machine-independent compilation flags.
# These three are typical settings for cc.
#ICFLAGS	= -g
#ICFLAGS	= -O
#ICFLAGS	=
# These four are typical settings for optimized code with gcc.  The
# last two also work with icc/ecc.
#ICFLAGS	= -g -O6 -Wall
ICFLAGS	= -g -O6 -mno-cygwin -mwindows
#ICFLAGS	= -g -O3 -Wall
#ICFLAGS	= -g -O3

# Use XCFLAGS to specify machine-dependent compilation flags.
# For some platforms no special flags are needed.
#XCFLAGS	= -DHAVE_IEEE_754 -DBSD
#
#==========================
#  Linux
#
# Gcc 2.8.1 or higher on i686.
#XCFLAGS	= -mcpu=pentiumpro -malign-double -DHAVE_IEEE_754 -DBSD
# Gcc 3.2.2 or higher on i686.
XCFLAGS	= -mcpu=pentium4 -malign-double -DHAVE_IEEE_754 -DBSD -DSIZEOF_VOID_P=4 -DSIZEOF_LONG=4 -DSIZEOF_INT=4
# Icc on i686.
#XCFLAGS	= -ansi -align -ip -DHAVE_IEEE_754 -DBSD
# Gcc on ia64.
#XCFLAGS	= -DHAVE_IEEE_754 -DBSD -DSIZEOF_VOID_P=8 -DSIZEOF_LONG=8
# Icc/ecc on ia64.
#XCFLAGS = -ansi -DBSD -DHAVE_IEEE_754 -DSIZEOF_VOID_P=8 -DSIZEOF_LONG=8
#
#==========================
#  Solaris
#
# For Solaris, BSD should not be replaced by UNIX100.
#XCFLAGS	= -DHAVE_IEEE_754 -DUNIX100 -DEPD_BIG_ENDIAN
# Gcc 2.8.1 or higher on Ultrasparc.
#XCFLAGS	= -mcpu=ultrasparc -DHAVE_IEEE_754 -DUNIX100 -DEPD_BIG_ENDIAN
# For Solaris 2.5 and higher, optimized code with /usr/bin/cc or CC.
#XCFLAGS	= -DHAVE_IEEE_754 -DUNIX100 -xO5 -native -dalign -DEPD_BIG_ENDIAN
# On IA platforms, -dalign is not supported and causes warnings.
#XCFLAGS	= -DHAVE_IEEE_754 -DUNIX100 -xO5 -native
# Recent Sun compilers won't let you use -native on old Ultras.
#XCFLAGS	= -DHAVE_IEEE_754 -DUNIX100 -xO5 -dalign -xlibmil -DEPD_BIG_ENDIAN
# For Solaris 2.4, optimized code with /usr/bin/cc.
#XCFLAGS	= -DHAVE_IEEE_754 -DUNIX100 -xO4 -dalign -DEPD_BIG_ENDIAN
# For Solaris 2.5 and higher, optimized code with /usr/ucb/cc.
#XCFLAGS	= -DHAVE_IEEE_754 -DBSD -xO5 -native -dalign -DEPD_BIG_ENDIAN
#XCFLAGS	= -DHAVE_IEEE_754 -DBSD -xO5 -dalign -xlibmil -DEPD_BIG_ENDIAN
# For Solaris 2.4, optimized code with /usr/ucb/cc.
#XCFLAGS	= -DHAVE_IEEE_754 -DBSD -xO4 -dalign -DEPD_BIG_ENDIAN
#
#==========================
#  DEC Alphas running Digital Unix
#
# For DEC Alphas either -ieee_with_inexact or -ieee_with_no_inexact is
# needed. If you use only BDDs, -ieee_with_no_inexact is enough.
# In the following, we consider three different compilers:
# - the old native compiler (the one of MIPS ancestry that produces u-code);
# - the new native compiler;
# - gcc
# On the Alphas, gcc (as of release 2.7.2) does not support 32-bit pointers
# and IEEE 754 floating point arithmetic. Therefore, for this architecture
# only, the native compilers provide a substatial advantage.
# With the native compilers, specify -xtaso for 32-bit pointers.
# Do not use -xtaso_short because explicit reference to stdout and stderr
# does not work with this option. (Among other things.)
# Notice that -taso must be included in LDFLAGS for -xtaso to work.
# Given the number of possible choices, only some typical configurations
# are proposed here.
#
# Old native compiler for the Alphas; 64-bit pointers.
#XCFLAGS	= -DBSD -DHAVE_IEEE_754 -ieee_with_no_inexact -tune host -DSIZEOF_VOID_P=8 -DSIZEOF_LONG=8
# Old native compiler for the Alphas; 32-bit pointers.
#XCFLAGS	= -DBSD -DHAVE_IEEE_754 -ieee_with_no_inexact -tune host -xtaso -DSIZEOF_LONG=8
# New native compiler for the Alphas; 64-bit pointers. 
#XCFLAGS	= -g3 -O4 -std -DBSD -DHAVE_IEEE_754 -ieee_with_no_inexact -tune host -DSIZEOF_VOID_P=8 -DSIZEOF_LONG=8
# New native compiler for the Alphas; 32-bit pointers.
#XCFLAGS	= -g3 -O4 -std -DBSD -DHAVE_IEEE_754 -ieee_with_no_inexact -tune host -xtaso -DSIZEOF_LONG=8
# gcc for the Alphas: compile without HAVE_IEEE_754.
#XCFLAGS	= -DBSD -DSIZEOF_VOID_P=8 -DSIZEOF_LONG=8
#
#==========================
#
#  IBM RS6000
#
# For the IBM RS6000 -qstrict is necessary when specifying -O3 with cc.
#XCFLAGS	= -DBSD -DHAVE_IEEE_754 -DEPD_BIG_ENDIAN -O3 -qstrict
#
#==========================
#
#  HP-UX
#
# I haven't figured out how to enable IEEE 754 on the HPs I've tried...
# For HP-UX using gcc.
#XCFLAGS	= -DUNIX100 -DEPD_BIG_ENDIAN
# For HP-UX using c89.
#XCFLAGS	= +O3 -DUNIX100 -DEPD_BIG_ENDIAN
#
#==========================
#
#  Windows95/98/NT/XP with Cygwin tools
#
# For b16 it was also necessary to change setup.sh (define CREATE
# as "cp"). This is no longer necessary since b17.1.
# The value of RLIMIT_DATA_DEFAULT should reflect the amount of
# available memory (expressed in bytes).
# Recent versions of cygwin have getrlimit, but the datasize limit
# cannot be set.
#XCFLAGS	= -mcpu=pentiumpro -malign-double -DHAVE_IEEE_754 -DHAVE_GETRLIMIT=0 -DRLIMIT_DATA_DEFAULT=67108864


# Define the level of self-checking and verbosity of the CUDD package.
#DDDEBUG = -DDD_DEBUG -DDD_VERBOSE -DDD_STATS -DDD_CACHE_PROFILE -DDD_UNIQUE_PROFILE -DDD_COUNT
DDDEBUG	=

# Define the level of self-checking and verbosity of the MTR package.
#MTRDEBUG = -DMTR_DEBUG
MTRDEBUG =

# Loader options.
LDFLAGS	=
# This may produce faster code on the DECstations.
#LDFLAGS	= -jmpopt -Olimit 1000
# This may be necessary under some old versions of Linux.
#LDFLAGS	= -static
# This normally makes the program faster on the DEC Alphas.
#LDFLAGS	= -non_shared -om
# This is for 32-bit pointers on the DEC Alphas.
#LDFLAGS	= -non_shared -om -taso
#LDFLAGS	= -non_shared -taso

# Define PURE as purify to link with purify.
# Define PURE as quantify to link with quantify.
# Remember to compile with -g if you want line-by-line info with quantify.
PURE =
#PURE	= purify
#PURE	= quantify

# Define EXE as .exe for MS-DOS and derivatives.
EXE	=
#EXE	= .exe

# End of the configuration section.
#---------------------------------------------------------------------------

MFLAG   = -DMNEMOSYNE
MNEMLIB	= ../mnemosyne/libmnem.a

DDWDIR	= .
IDIR	= $(DDWDIR)/include
INCLUDE = -I$(IDIR)

# BDIRS	= cudd dddmp mtr st util epd
BDIRS	= cudd mtr st util epd
# DIRS	= $(BDIRS) nanotrav
DIRS	= $(BDIRS)

#------------------------------------------------------------------------

.PHONY : build
.PHONY : nanotrav
.PHONY : check_leaks
.PHONY : optimize_dec
.PHONY : testcudd
.PHONY : libobj
.PHONY : testobj
.PHONY : testdddmp
.PHONY : testmtr
.PHONY : lint
.PHONY : all
.PHONY : clean
.PHONY : distclean


build:
	sh ./setup.sh
	@for dir in $(DIRS); do \
		(cd $$dir; \
		echo Making $$dir ...; \
		$(MAKE) CC=$(CC) RANLIB=$(RANLIB) MFLAG= MNEMLIB= ICFLAGS="$(ICFLAGS)" XCFLAGS="$(XCFLAGS)" DDDEBUG="$(DDDEBUG)" MTRDEBUG="$(MTRDEBUG)" LDFLAGS="$(LDFLAGS)" PURE="$(PURE)" EXE="$(EXE)" )\
	done
	sh ./setup.sh

nanotrav: build

check_leaks:
	sh ./setup.sh
	@for dir in mnemosyne $(DIRS); do \
		(cd $$dir; \
		echo Making $$dir ...; \
		$(MAKE) CC=$(CC) RANLIB=$(RANLIB) MFLAG=$(MFLAG) MNEMLIB=$(MNEMLIB) ICFLAGS="$(ICFLAGS)" XCFLAGS="$(XCFLAGS)" DDDEBUG="$(DDDEBUG)" MTRDEBUG="$(MTRDEBUG)" LDFLAGS="$(LDFLAGS)" EXE="$(EXE)" )\
	done
	sh ./setup.sh

optimize_dec:
	sh ./setup.sh
	@for dir in $(DIRS); do \
		(cd $$dir; \
		echo Making $$dir ...; \
		$(MAKE) CC=$(CC) RANLIB=$(RANLIB) XCFLAGS="$(XCFLAGS)" LDFLAGS="$(LDFLAGS)" optimize_dec )\
	done

lint:
	sh ./setup.sh
	@for dir in $(DIRS) obj; do \
		(cd $$dir; \
		echo Making lint in $$dir ...; \
		$(MAKE) CC=$(CC) lint )\
	done
	sh ./setup.sh

tags:
	sh ./setup.sh
	@for dir in $(DIRS) obj; do \
		(cd $$dir; \
		echo Making tags in $$dir ...; \
		$(MAKE) CC=$(CC) tags )\
	done
	etags -i `pwd`/cudd/TAGS -i `pwd`/mtr/TAGS -i `pwd`/epd/TAGS -i `pwd`/util/TAGS -i `pwd`/st/TAGS
	sh ./setup.sh

all:
	sh ./setup.sh
	@for dir in $(DIRS); do \
		(cd $$dir; \
		echo Making all in $$dir ...; \
		$(MAKE) CC=$(CC) RANLIB=$(RANLIB) MFLAG= MNEMLIB= ICFLAGS="$(ICFLAGS)" XCFLAGS="$(XCFLAGS)" DDDEBUG="$(DDDEBUG)" MTRDEBUG="$(MTRDEBUG)" LDFLAGS="$(LDFLAGS)" PURE="$(PURE)" EXE="$(EXE)" all )\
	done
	sh ./setup.sh

testcudd:
	sh ./setup.sh
	@for dir in util st mtr epd; do \
		(cd $$dir; \
		echo Making $$dir ...; \
		$(MAKE) CC=$(CC) RANLIB=$(RANLIB) MFLAG= MNEMLIB= ICFLAGS="$(ICFLAGS)" XCFLAGS="$(XCFLAGS)" DDDEBUG="$(DDDEBUG)" MTRDEBUG="$(MTRDEBUG)" LDFLAGS="$(LDFLAGS)" PURE="$(PURE)" EXE="$(EXE)" )\
	done
	sh ./setup.sh
	@(cd cudd; \
	echo Making testcudd ...; \
	$(MAKE) CC=$(CC) RANLIB=$(RANLIB) MFLAG= MNEMLIB= ICFLAGS="$(ICFLAGS)" XCFLAGS="$(XCFLAGS)" DDDEBUG="$(DDDEBUG)" MTRDEBUG="$(MTRDEBUG)" LDFLAGS="$(LDFLAGS)" PURE="$(PURE)" EXE="$(EXE)" testcudd$(EXE) )

objlib:
	sh ./setup.sh
	@for dir in $(BDIRS); do \
		(cd $$dir; \
		echo Making $$dir ...; \
		$(MAKE) CC=$(CC) RANLIB=$(RANLIB) MFLAG= MNEMLIB= ICFLAGS="$(ICFLAGS)" XCFLAGS="$(XCFLAGS)" DDDEBUG="$(DDDEBUG)" MTRDEBUG="$(MTRDEBUG)" LDFLAGS="$(LDFLAGS)" PURE="$(PURE)" EXE="$(EXE)" )\
	done
	sh ./setup.sh
	@(cd obj; \
	echo Making obj ...; \
	$(MAKE) CPP=$(CPP) CPPFLAGS=$(CPPFLAGS) RANLIB=$(RANLIB) MFLAG= MNEMLIB= ICFLAGS="$(ICFLAGS)" XCFLAGS="$(XCFLAGS)" DDDEBUG="$(DDDEBUG)" MTRDEBUG="$(MTRDEBUG)" LDFLAGS="$(LDFLAGS)" PURE="$(PURE)" EXE="$(EXE)" )

testobj: objlib
	@(cd obj; \
	echo Making testobj ...; \
	$(MAKE) CPP=$(CPP) CPPFLAGS=$(CPPFLAGS) RANLIB=$(RANLIB) MFLAG= MNEMLIB= ICFLAGS="$(ICFLAGS)" XCFLAGS="$(XCFLAGS)" DDDEBUG="$(DDDEBUG)" MTRDEBUG="$(MTRDEBUG)" LDFLAGS="$(LDFLAGS)" PURE="$(PURE)" EXE="$(EXE)" testobj$(EXE) )

testdddmp: build
	@(cd dddmp; \
	echo Making testdddmp ...; \
	$(MAKE) CC=$(CC) RANLIB=$(RANLIB) MFLAG= MNEMLIB= ICFLAGS="$(ICFLAGS)" XCFLAGS="$(XCFLAGS)" DDDEBUG="$(DDDEBUG)" MTRDEBUG="$(MTRDEBUG)" LDFLAGS="$(LDFLAGS)" PURE="$(PURE)" EXE="$(EXE)" testdddmp$(EXE) )

testmtr: build
	@(cd mtr; \
	echo Making testmtr ...; \
	$(MAKE) CC=$(CC) RANLIB=$(RANLIB) MFLAG= MNEMLIB= ICFLAGS="$(ICFLAGS)" XCFLAGS="$(XCFLAGS)" DDDEBUG="$(DDDEBUG)" MTRDEBUG="$(MTRDEBUG)" LDFLAGS="$(LDFLAGS)" PURE="$(PURE)" EXE="$(EXE)" testmtr$(EXE) )

clean:
	@for dir in mnemosyne $(DIRS) obj; do	\
	    (cd $$dir; 	\
	     echo Cleaning $$dir ...; \
	     $(MAKE) -s clean	) \
	done

distclean:
	@for dir in mnemosyne $(DIRS) obj; do	\
	    (cd $$dir; 	\
	     echo Cleaning $$dir ...; \
	     $(MAKE) -s EXE="$(EXE)" distclean	) \
	done
	sh ./shutdown.sh

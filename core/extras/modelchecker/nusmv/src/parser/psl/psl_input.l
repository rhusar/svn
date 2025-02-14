/***************************************************************  -*-C-*-  ***/

%{
/**CFile***********************************************************************

  FileName    [psl_input.l]

  PackageName [parser.psl]

  Synopsis    [Lexical analyzer for the PSL specification input language]

  SeeAlso     [psl_grammar.y]

  Author      [Roberto Cavada]

  Copyright   [
  This file is part of the ``parser.psl'' package of NuSMV version 2. 
  Copyright (C) 2005 by ITC-irst. 

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

#include <stdio.h>
#include <errno.h>

#include "pslExpr.h"
#include "psl_grammar.h"

static char rcsid[] UTIL_UNUSED = "$Id: psl_input.l,v 1.1.2.21.6.1 2007/01/03 13:37:53 nusmv Exp $";

static int psl_skip_comment ARGS((void));
EXTERN void psl_yyerror ARGS((char* s, ...));

#define YY_INPUT(buf, result, max_size) \
	if ( YY_CURRENT_BUFFER->yy_is_interactive ) \
		{ \
		int c = '*', n; \
		for (n=0; n<max_size && \
		     (c = getc(psl_yyin)) != EOF && \
                     c!=' ' && c!='\n' && c!='\t' && c!='\r' && c!='\f'; ++n ) \
			buf[n] = (char) c; \
		if (c==' ' || c=='\n' || c=='\t' || c=='\r' || c=='\f') buf[n++] = (char) c; \
		if (c == EOF && ferror(psl_yyin)) YY_FATAL_ERROR( "input in flex scanner failed" ); \
		result = n; \
		} \
	else \
		{ \
		errno=0; \
		while ( (result = fread(buf, 1, max_size, psl_yyin))==0 && ferror(psl_yyin)) \
			{ \
			if(errno != EINTR) \
				{ \
				YY_FATAL_ERROR( "input in flex scanner failed" ); \
				break; \
				} \
			errno=0; \
			clearerr(psl_yyin); \
			} \
		}


%}


%a	15000
%o	25000
%option noyywrap
%option yylineno
%option always-interactive

PSL_TOK_SPACE               [ \n\t\r\f]

PSL_TOK_DIGIT               [0-9]
PSL_TOK_ALPHA               [A-Za-z_]
PSL_TOK_ALPHANUM            [A-Za-z0-9_\$#-]

PSL_TOK_COMMENT             "--"
TKTRUE                      "TRUE"
TKFALSE                     "FALSE"
TKINF                       "inf"

TKINCONTEXT                 "IN"

TKFORALL                    "forall"
TKFORANY                    "forany"
TKBOOLEAN                   "boolean"

TKIN                        "in"
TKUNION                     "union"
TKOR                        "or"
TKPOSEDGE                   "posedge"
TKNEGEDGE                   "negedge"

TKPROPERTY                  "property"
TKALWAYS                    "always"
TKNEVER                     "never"
TKEVENTUALLYBANG            "eventually!"

TKWITHINBANG                "within!"
TKWITHIN                    "within"
TKWITHINBANG_               "within!_"
TKWITHIN_                   "within_"

TKWHILENOTBANG              "whilenot!"
TKWHILENOT                  "whilenot"
TKWHILENOTBANG_             "whilenot!_"
TKWHILENOT_                 "whilenot_"

TKNEXT_EVENT_ABANG          "next_event_a!"
TKNEXT_EVENT_A              "next_event_a"
TKNEXT_EVENT_EBANG          "next_event_e!"
TKNEXT_EVENT_E              "next_event_e"
TKNEXT_EVENTBANG            "next_event!"
TKNEXT_EVENT                "next_event"
TKNEXT_ABANG                "next_a!"
TKNEXT_EBANG                "next_e!"
TKNEXT_A                    "next_a"
TKNEXT_E                    "next_e"
TKNEXTBANG                  "next!"
TKNEXT                      "next"

TKBEFOREBANG                "before!"
TKBEFORE                    "before"
TKBEFOREBANG_               "before!_"
TKBEFORE_                   "before_"
TKUNTILBANG                 "until!"
TKUNTIL                     "until"
TKUNTILBANG_                "until!_"
TKUNTIL_                    "until_"
TKABORT                     "abort"

TKCASE                      "case"
TKENDCASE                   "esac"

TKPIPEMINUSGT               "|->"
TKPIPEEQGT                  "|=>"

TKPIPEPIPE                  "||"
TKAMPERSANDAMPERSAND        "&&"
TKMINUSGT                   "->"
TKLTMINUSGT                 "<->"
TKPIPE                      "|"
TKTILDEPIPE                 "~|"
TKXOR                       "xor"
TKCARET                     "^"
TKCARETTILDE                "^~"
TKTILDECARET                "~^"
TKAMPERSAND                 "&"
TKTILDEAMPERSAND            "~&"
TKEQEQ                      "=="
TKBANGEQ                    "!="
TKEQEQEQ                    "==="
TKBANGEQEQ                  "!=="
TKEQ                        "="
TKGT                        ">"
TKGE                        ">="
TKLT                        "<"
TKLE                        "<="
TKLTLT                      "<<"
TKGTGT                      ">>"
TKGTGTGT                    ">>>"
TKLTLTLT                    "<<<"
TKPLUS                      "+"
TKMINUS                     "-"
TKSPLAT                     "*"
TKSLASH                     "/"
TKPERCENT                   "mod"
TKSPLATSPLAT                "**"

TKLBSPLAT                   "[*"
TKLBEQ                      "[="
TKLBMINUSGT                 "[->"
TKLBPLUSRB                  "[+]"

TKG                         "G"
TKXBANG                     "X!"
TKX                         "X"
TKF                         "F"
TKU                         "U"
TKW                         "W"
TKEG                        "EG"
TKEX                        "EX"
TKEF                        "EF"
TKAG                        "AG"
TKAX                        "AX"
TKAF                        "AF"
TKA                         "A"
TKE                         "E"


TKLP                        "("
TKRP                        ")"
TKLC                        "{"
TKRC                        "}"
TKLB                        "["
TKRB                        "]"
TKCOMMA                     ","
TKQUESTIONMARK              "?"
TKCOLON                     ":"
TKSEMI                      ";"
TKDOT                       "."

TKBANG                      "!"
TKTILDE                     "~"



%%

{PSL_TOK_SPACE}*         ;
{PSL_TOK_COMMENT}        { psl_skip_comment(); }
{TKTRUE}                 { return TKTRUE; }
{TKFALSE}                { return TKFALSE; }

{TKINCONTEXT}            { return TKINCONTEXT; }

{TKFORALL}               { psl_yylval.operator = TKFORALL; return TKFORALL; }
{TKFORANY}               { psl_yylval.operator = TKFORANY; return TKFORANY; }
{TKBOOLEAN}              { return TKBOOLEAN; }

{TKPIPEMINUSGT}          { psl_yylval.operator = TKPIPEMINUSGT; return TKPIPEMINUSGT; }
{TKPIPEEQGT}             { psl_yylval.operator = TKPIPEEQGT; return TKPIPEEQGT; }
{TKPIPEPIPE}             { psl_yylval.operator = TKPIPEPIPE; return TKPIPEPIPE; }
{TKPIPE}                 { psl_yylval.operator = TKPIPE; return TKPIPE; }

{TKAMPERSANDAMPERSAND}   { psl_yylval.operator = TKAMPERSANDAMPERSAND; return TKAMPERSANDAMPERSAND; }
{TKAMPERSAND}            { psl_yylval.operator = TKAMPERSAND; return TKAMPERSAND; }

{TKMINUSGT}              { psl_yylval.operator = TKMINUSGT; return TKMINUSGT; }
{TKMINUS}                { psl_yylval.operator = TKMINUS; return TKMINUS; }

{TKLTMINUSGT}            { psl_yylval.operator = TKLTMINUSGT; return TKLTMINUSGT; }
{TKLTLTLT}               { psl_yylval.operator = TKLTLTLT; return TKLTLTLT; }
{TKLTLT}                 { psl_yylval.operator = TKLTLT; return TKLTLT; }
{TKLE}                   { psl_yylval.operator = TKLE; return TKLE; }
{TKLT}                   { psl_yylval.operator = TKLT; return TKLT; }

{TKGTGTGT}               { psl_yylval.operator = TKGTGTGT; return TKGTGTGT; }
{TKGTGT}                 { psl_yylval.operator = TKGTGT; return TKGTGT; }
{TKGE}                   { psl_yylval.operator = TKGE; return TKGE; }
{TKGT}                   { psl_yylval.operator = TKGT; return TKGT; }

{TKTILDEPIPE}            { psl_yylval.operator = TKTILDEPIPE; return TKTILDEPIPE; }
{TKTILDECARET}           { psl_yylval.operator = TKTILDECARET; return TKTILDECARET; }
{TKTILDEAMPERSAND}       { psl_yylval.operator = TKTILDEAMPERSAND; return TKTILDEAMPERSAND; }
{TKTILDE}                { psl_yylval.operator = TKTILDE; return TKTILDE; }

{TKCARETTILDE}           { psl_yylval.operator = TKCARETTILDE; return TKCARETTILDE; }
{TKCARET}                { psl_yylval.operator = TKCARET; return TKCARET; }
{TKXOR}                  { psl_yylval.operator = TKXOR; return TKXOR; }

{TKEQEQEQ}               { psl_yylval.operator = TKEQEQEQ; return TKEQEQEQ; }
{TKEQEQ}                 { psl_yylval.operator = TKEQEQ; return TKEQEQ; }
{TKEQ}                   { psl_yylval.operator = TKEQ; return TKEQ; }

{TKBANGEQEQ}             { psl_yylval.operator = TKBANGEQEQ; return TKBANGEQEQ; }
{TKBANGEQ}               { psl_yylval.operator = TKBANGEQ; return TKBANGEQ; }
{TKBANG}                 { psl_yylval.operator = TKBANG; return TKBANG; }

{TKSPLATSPLAT}           { psl_yylval.operator = TKSPLATSPLAT; return TKSPLATSPLAT; }
{TKSPLAT}                { psl_yylval.operator = TKSPLAT; return TKSPLAT; }

{TKPLUS}                 { psl_yylval.operator = TKPLUS; return TKPLUS; }
{TKSLASH}                { psl_yylval.operator = TKSLASH; return TKSLASH; }
{TKPERCENT}              { psl_yylval.operator = TKPERCENT; return TKPERCENT; }

{TKLBSPLAT}              { psl_yylval.operator = TKLBSPLAT; return TKLBSPLAT; }
{TKLBEQ}                 { psl_yylval.operator = TKLBEQ; return TKLBEQ; }
{TKLBMINUSGT}            { psl_yylval.operator = TKLBMINUSGT; return TKLBMINUSGT; }
{TKLBPLUSRB}             { psl_yylval.operator = TKLBPLUSRB; return TKLBPLUSRB; }
{TKLB}                   { return TKLB; }

{TKIN}                   { psl_yylval.operator = TKIN; return TKIN; }
{TKUNION}                { psl_yylval.operator = TKUNION; return TKUNION; }
{TKOR}                   { return TKOR; }
{TKPOSEDGE}              { return TKPOSEDGE; }
{TKNEGEDGE}              { return TKNEGEDGE; }

{TKXBANG}                { psl_yylval.operator = TKXBANG; return TKXBANG; }
{TKX}                    { psl_yylval.operator = TKX; return TKX; }

{TKG}                    { psl_yylval.operator = TKG; return TKG; }
{TKF}                    { psl_yylval.operator = TKF; return TKF; }
{TKU}                    { psl_yylval.operator = TKU; return TKU; }
{TKW}                    { psl_yylval.operator = TKW; return TKW; }

{TKEG}                   { psl_yylval.operator = TKEG; return TKEG; }
{TKEX}                   { psl_yylval.operator = TKEX; return TKEX; }
{TKEF}                   { psl_yylval.operator = TKEF; return TKEF; }
{TKE}                    { psl_yylval.operator = TKE; return TKE; }

{TKAG}                   { psl_yylval.operator = TKAG; return TKAG; }
{TKAX}                   { psl_yylval.operator = TKAX; return TKAX; }
{TKAF}                   { psl_yylval.operator = TKAF; return TKAF; }
{TKA}                    { psl_yylval.operator = TKA; return TKA; }

{TKPROPERTY}             { return TKPROPERTY; }

{TKALWAYS}               { psl_yylval.operator = TKALWAYS; return TKALWAYS; }
{TKNEVER}                { psl_yylval.operator = TKNEVER; return TKNEVER; }
{TKEVENTUALLYBANG}       { psl_yylval.operator = TKEVENTUALLYBANG; return TKEVENTUALLYBANG; }


{TKWITHINBANG_}          { psl_yylval.operator = TKWITHINBANG_; return TKWITHINBANG_; }
{TKWITHINBANG}           { psl_yylval.operator = TKWITHINBANG; return TKWITHINBANG; }
{TKWITHIN_}              { psl_yylval.operator = TKWITHIN_; return TKWITHIN_; }
{TKWITHIN}               { psl_yylval.operator = TKWITHIN; return TKWITHIN; }

{TKWHILENOTBANG_}        { psl_yylval.operator = TKWHILENOTBANG_; return TKWHILENOTBANG_; }
{TKWHILENOTBANG}         { psl_yylval.operator = TKWHILENOTBANG; return TKWHILENOTBANG; }
{TKWHILENOT_}            { psl_yylval.operator = TKWHILENOT_; return TKWHILENOT_; }
{TKWHILENOT}             { psl_yylval.operator = TKWHILENOT; return TKWHILENOT; }

{TKNEXT_EVENT_ABANG}     { psl_yylval.operator = TKNEXT_EVENT_ABANG; return TKNEXT_EVENT_ABANG; }
{TKNEXT_EVENT_A}         { psl_yylval.operator = TKNEXT_EVENT_A; return TKNEXT_EVENT_A; }
{TKNEXT_EVENT_EBANG}     { psl_yylval.operator = TKNEXT_EVENT_EBANG; return TKNEXT_EVENT_EBANG; }
{TKNEXT_EVENT_E}         { psl_yylval.operator = TKNEXT_EVENT_E; return TKNEXT_EVENT_E; }
{TKNEXT_EVENTBANG}       { psl_yylval.operator = TKNEXT_EVENTBANG; return TKNEXT_EVENTBANG; }
{TKNEXT_EVENT}           { psl_yylval.operator = TKNEXT_EVENT; return TKNEXT_EVENT; }
{TKNEXT_ABANG}           { psl_yylval.operator = TKNEXT_ABANG; return TKNEXT_ABANG; }
{TKNEXT_A}               { psl_yylval.operator = TKNEXT_A; return TKNEXT_A; }
{TKNEXT_EBANG}           { psl_yylval.operator = TKNEXT_EBANG; return TKNEXT_EBANG; }
{TKNEXT_E}               { psl_yylval.operator = TKNEXT_E; return TKNEXT_E; }
{TKNEXTBANG}             { psl_yylval.operator = TKNEXTBANG; return TKNEXTBANG; }
{TKNEXT}                 { psl_yylval.operator = TKNEXT; return TKNEXT; }

{TKBEFOREBANG_}          { psl_yylval.operator = TKBEFOREBANG_; return TKBEFOREBANG_; }
{TKBEFOREBANG}           { psl_yylval.operator = TKBEFOREBANG; return TKBEFOREBANG; }
{TKBEFORE_}              { psl_yylval.operator = TKBEFORE_; return TKBEFORE_; }
{TKBEFORE}               { psl_yylval.operator = TKBEFORE; return TKBEFORE; }

{TKUNTILBANG_}           { psl_yylval.operator = TKUNTILBANG_; return TKUNTILBANG_; }
{TKUNTILBANG}            { psl_yylval.operator = TKUNTILBANG; return TKUNTILBANG; }
{TKUNTIL_}               { psl_yylval.operator = TKUNTIL_; return TKUNTIL_; }
{TKUNTIL}                { psl_yylval.operator = TKUNTIL; return TKUNTIL; }

{TKABORT}                { return TKABORT; }

{TKCASE}             { return TKCASE; }
{TKENDCASE}          { return TKENDCASE; }

{TKLP}               { return TKLP; }
{TKRP}               { return TKRP; }
{TKLC}               { return TKLC; }
{TKRC}               { return TKRC; }
{TKRB}               { return TKRB; }
{TKCOMMA}            { return TKCOMMA; }
{TKQUESTIONMARK}     { return TKQUESTIONMARK; }
{TKCOLON}            { return TKCOLON; }
{TKSEMI}             { return TKSEMI; }

{PSL_TOK_DIGIT}+     { psl_yylval.ival = atoi(psl_yytext); return TKNUMBER; }

{PSL_TOK_ALPHA}({PSL_TOK_ALPHANUM})*  { psl_yylval.idname=strdup(psl_yytext); return TKATOM; }

{TKDOT}              { return TKDOT; }

<<EOF>>              { return TKEOF; }

.                    { psl_yyerror("Invalid character: %s", psl_yytext); }

%%

static int psl_skip_comment() 
{
  register int c;

  do { c = input(); } while ( c != '\n' && c != EOF );
  return 0;
}

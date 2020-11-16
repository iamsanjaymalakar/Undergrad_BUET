%option noyywrap yylineno

%{
#include<cstdio>
#include<cstdlib>
#include<string>
#include <algorithm>
#include <bitset>
#include <cctype>
#include <cmath>
#include <cstdio>
#include <cstring>
#include <iostream>
#include <map>
#include <queue>
#include <set>
#include <stack>
#include <string>
#include <vector>
#include <ctime>
#include <sstream>

using namespace std;

#define FOR(i, a, b) for (int i = a; i <= b; i++)
#define REP(i, n) for (int i = 0; i < n; i++)
#define ll long long
#define ull unsigned long long
#define inf 1e9
#define sf(a) scanf("%d",&a);
#define sf2(a, b) scanf("%d %d",&a,&b);
#define sf3(a, b, c) scanf("%d %d %d",&a,&b,&c);
#define pf printf
#define mset(a, b) memset(a,b,sizeof(a))
#define itf it->first
#define its it->second



#include "SymbolTable.h"
#include "y.tab.h"

void yyerror(char *);
extern SymbolTable *table;

%}

WHITESPACE [ \t\f\r\v]+ 
LETTER [a-zA-Z]
DIGIT [0-9]
INTEGER {DIGIT}+
FLOAT ({DIGIT}+[Ee][+-]?{DIGIT}+)|((({DIGIT}+\.{DIGIT}*)|({DIGIT}*\.{DIGIT}+))([eE][+-]?{DIGIT}+)?)
CHAR \'([^'\\\n]|\\[nt\\\'\"afrbv0])\'
INCOP \+\+
DECOP \-\-
RELOP \<\=|\>\=|\>|\<|\=\=|\!\=
BITOP \&|\||\^|\<\<|\>\>
ADDOP \+|\-
MULOP \*|\/|\%
IDENTIFIER [A-Za-z_][_A-Za-z0-9]*
STRING \"(\\[nt\\\'\"afrbv0]|[^\n\"\\]|\\\n)*\"
UNTSTRING \"(\\[nt\\\'\"afrbv0]|[^\n\"\\]|\\\n)*
COMMENT1 \/\/(\\\n|[^\n])*
COMMENT2 \/\*([^\*]|\*+[^\/\*])*\*+\/
UNFCOMMENT \/\*([^\*]|\*+[^\/\*])*
IFN ({INTEGER}[Ee]{FLOAT})|({FLOAT}[Ee]{FLOAT})
ISP [0-9]+[_0-9A-Za-z]+
MULTICHAR \'([^'\n])([^'\n])+\'
UNFCHAR (\'\\\')|(\'[^'\n]*)
UNRECCHAR \'\\[^nt\\\'\"afrbv0]\'
NEWLINE \n

%%

"int" 		{
			return INT;
		}
"float"		{
			return FLOAT;
		}
"void"		{
			return VOID;
		}
"for" 		{
			return FOR;
		}
"if"		{
			return IF;
		}
"else"		{
			return ELSE;
		}
"while"		{
			return WHILE;
		}
"println"	{
			return PRINTLN;
		}
"return"	{
			return RETURN;
		}
{INTEGER}	{
			SymbolInfo *temp= new  SymbolInfo(yytext,"CONST_INT");
			yylval.s = temp;
			return CONST_INT;
		}
{FLOAT}	  	{
			SymbolInfo *temp= new  SymbolInfo(yytext,"CONST_FLOAT");
			yylval.s = temp;
			return CONST_FLOAT;
		}
{IDENTIFIER}	{
			SymbolInfo *temp= new SymbolInfo(yytext,"ID");
			yylval.s = temp;
			return ID;
		}
{INCOP}		{
			return INCOP;
		}
{DECOP}		{
			return DECOP;
		}
{ADDOP}       	{
			SymbolInfo *temp= new SymbolInfo(yytext,"ADDOP");
			yylval.s = temp;
			return ADDOP;	
		}
{MULOP}     	{
			SymbolInfo *temp= new SymbolInfo(yytext,"MULOP");
			yylval.s = temp;
			return MULOP;
		}
{RELOP}		{
			SymbolInfo *temp= new SymbolInfo(yytext,"RELOP");
			yylval.s = temp;
			return RELOP;
		}
\&\&|\|\|	{
			SymbolInfo *temp= new SymbolInfo(yytext,"LOGICOP");
			yylval.s = temp;
			return LOGICOP;
		}
\!		{
			return NOT;
		}
\(		{
			return LPAREN;
		}
\)		{
			return RPAREN;
		}
\{		{
			return LCURL;
		}
\}		{
			return RCURL;
		}
\[		{
			return LTHIRD;
		}
\]		{
			return RTHIRD;
		}
\=		{
			return ASSIGNOP;
		}
\,		{
			return COMMA;
		}
\;		{
			return SEMICOLON;
		}
{COMMENT1}	{
		
		}
[\n]		{
			
		}

%%




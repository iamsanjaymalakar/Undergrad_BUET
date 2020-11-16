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


FILE *fp,*logOut,*errorOut;
int errorCount;

/* interfacing in lexer */
extern int yylineno;
void yyerror(char *s){
	fprintf(logOut,"\nAt line no: %d %s\n",yylineno,s);
}
int yyparse(void);
int yylex(void);
extern FILE *yyin;

SymbolTable *table = new SymbolTable();
vector<string> pType,pID;
vector<vInfo*> vID;
vector<string> :: iterator it,it2,argit;
vector<vInfo*> :: iterator vit;


%}

%union {
	SymbolInfo *s;
}

%token <s> INT ID COMMA SEMICOLON LTHIRD RTHIRD CONST_INT VOID FLOAT LPAREN RPAREN RCURL LCURL ASSIGNOP
%token <s> ADDOP CONST_FLOAT DECOP INCOP LOGICOP MULOP NOT RELOP FOR IF ELSE LOWER_THAN_ELSE WHILE PRINTLN RETURN
%type <s> start program unit var_declaration type_specifier declaration_list func_declaration parameter_list
%type <s> func_definition compound_statement statement statements expression_statement expression logic_expression 
%type <s> variable arguments argument_list factor unary_expression term rel_expression simple_expression

%define parse.error verbose

%nonassoc LOWER_THAN_ELSE
%nonassoc ELSE




%%

start	 : program
		{
			fprintf(logOut,"\nAt line no: %d start	 : program\n",yylineno-1);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
		}
	 ;

program  : program unit 
		{
			fprintf(logOut,"\nAt line no: %d program  : program unit\n",yylineno);
			fprintf(logOut,"\n%s\n%s\n",$1->name.c_str(),$2->name.c_str());
			string temp=$1->name.c_str();
			temp+="\n";
			temp+=$2->name.c_str();
			$$->name=temp.c_str();
		}
	 | unit
		{
			fprintf(logOut,"\nAt line no: %d program  : unit\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());			
		}
	 ;

unit : var_declaration
		{
			fprintf(logOut,"\nAt line no: %d unit : var_declaration\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());			
		}
		| func_declaration
		{
			fprintf(logOut,"\nAt line no: %d unit : func_declaration\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());		
		}
		| func_definition
		{
			fprintf(logOut,"\nAt line no: %d unit : func_definition\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());	
		}
		| error SEMICOLON
		{
			pID.clear();
			pType.clear();
			vID.clear();
			$$ = new SymbolInfo("ERROR");
		}
		;

func_declaration : type_specifier ID LPAREN parameter_list RPAREN SEMICOLON
		{
			fprintf(logOut,"\nAt line no: %d func_declaration : type_specifier ID LPAREN parameter_list RPAREN SEMICOLON\n",yylineno);
			fprintf(logOut,"\n%s %s(%s);\n",$1->name.c_str(),$2->name.c_str(),$4->name.c_str());
			string temp;
			temp+=$1->name.c_str();
			temp+=" ";
			temp+=$2->name.c_str();
			temp+="(";
			temp+=$4->name.c_str();
			temp+=");";
			$$ = new SymbolInfo(temp.c_str());
			//function 
			SymbolInfo *node= new SymbolInfo($2->name.c_str());
			if(table->search($2->name.c_str())==NULL)
			{
				node->function = true;
				node->info = new FunctionInfo();
				node->info->funcDec = true;
				node->info->retType = $1->name.c_str();
				node->info->paramType = pType;
				node->info->paramID = pID;
				table->insert(node,"ID");
			}
			else
			{
				fprintf(errorOut,"\nError at line %d : Multiple Declaration of %s\n",yylineno,$2->name.c_str());
				errorCount++;
			}
			pType.clear();
			pID.clear();
		}
		| type_specifier ID LPAREN RPAREN SEMICOLON
		{
			fprintf(logOut,"\nAt line no: %d func_declaration : type_specifier ID LPAREN RPAREN SEMICOLON\n",yylineno);
			fprintf(logOut,"\n%s %s();\n",$1->name.c_str(),$2->name.c_str());
			string temp;
			temp+=$1->name.c_str();
			temp+=" ";
			temp+=$2->name.c_str();
			temp+="();";
			$$ = new SymbolInfo(temp.c_str());
			//function 
			SymbolInfo *node= new SymbolInfo($2->name.c_str());
			if(table->search($2->name.c_str())==NULL)
			{
				node->function = true;
				node->info = new FunctionInfo();
				node->info->funcDec = true;
				node->info->retType = $1->name.c_str();
				node->info->paramType = pType;
				node->info->paramID = pID;
				table->insert(node,"ID");
			}
			else
			{
				fprintf(errorOut,"\nError at line %d : Multiple Declaration of %s\n",yylineno,$2->name.c_str());
				errorCount++;
			}
			pType.clear();
			pID.clear();
		}
	/*	| error SEMICOLON
		{	
			pID.clear();
			pType.clear();
			$$ = new SymbolInfo("ERROR IN FUNCTION DECLARATION");
		}   */
		;

func_definition : type_specifier ID LPAREN parameter_list RPAREN {
			// fucntion 
			SymbolInfo *tempNode=table->search($2->name.c_str());
			if(tempNode==NULL)
			{ 
				SymbolInfo *node= new SymbolInfo($2->name.c_str());
				node->function = true;
				node->info = new FunctionInfo();
				node->info->funcDef = true;
				node->info->retType = $1->name.c_str();
				node->info->paramType = pType;
				node->info->paramID = pID;
				node->lineno = $2->lineno;
				table->insert(node,"ID");
			}
			else if(tempNode->function)
			{
				if(tempNode->info->funcDec && !tempNode->info->funcDef)
				{
					bool flag=true;
					if($1->name!=tempNode->info->retType)
						flag=false;
					if(pType!=tempNode->info->paramType)
						flag=false;
					//if(pID!=tempNode->info->paramID)
					//	flag=false;	
					for(it=pID.begin(),it2=tempNode->info->paramID.begin();it!=pID.end(),it2!=tempNode->info->paramID.end();++it,++it2)
					{
						if(*it2!="un")
						{
							if(*it!=*it2)
								flag=false;
						}
					}
					if(!flag)
					{
						fprintf(errorOut,"\nError at line %d : Function declaration and definition didn't match of %s\n",yylineno,$2->name.c_str());
						errorCount++;
					}
					else 
					{
						table->remove($2->name.c_str());
						SymbolInfo *node= new SymbolInfo($2->name.c_str());
						node->function = true;
						node->info = new FunctionInfo();
						node->info->funcDef = true;
						node->info->funcDec = true;
						node->info->retType = $1->name.c_str();
						node->info->paramType = pType;
						node->info->paramID = pID;
						node->lineno = $2->lineno;
						table->insert(node,"ID");
					}
				}
				else
				{
					fprintf(errorOut,"\nError at line %d : Redefinition of function %s\n",yylineno,$2->name.c_str());
					errorCount++;
				}
			}
			else
			{
				fprintf(errorOut,"\nError at line %d : Multiple Declaration of %s\n",yylineno,$2->name.c_str());
				errorCount++;
			}
			} compound_statement /*{table->printAllTable(logOut); table->exitScope(logOut);}*/
		{
			fprintf(logOut,"\nAt line no: %d func_definition : type_specifier ID LPAREN parameter_list RPAREN compound_statement\n",yylineno);
			fprintf(logOut,"\n%s %s(%s)%s\n",$1->name.c_str(),$2->name.c_str(),$4->name.c_str(),$7->name.c_str());
			string temp;
			temp+=$1->name.c_str();
			temp+=" ";
			temp+=$2->name.c_str();
			temp+="(";
			temp+=$4->name.c_str();
			temp+=")";
			temp+=$7->name.c_str();
			$$ = new SymbolInfo(temp.c_str());
		}
		| type_specifier ID LPAREN RPAREN {
			// fucntion 
			SymbolInfo *tempNode=table->search($2->name.c_str());
			if(tempNode==NULL)
			{ 
				SymbolInfo *node= new SymbolInfo($2->name.c_str());
				node->function = true;
				node->info = new FunctionInfo();
				node->info->funcDef = true;
				node->info->retType = $1->name.c_str();
				node->info->paramType = pType;
				node->info->paramID = pID;
				node->lineno = $2->lineno;
				table->insert(node,"ID");
			}
			else if(tempNode->function)
			{
				if(tempNode->info->funcDec && !tempNode->info->funcDef)
				{
					bool flag=true;
					if($1->name!=tempNode->info->retType)
						flag=false;
					if(pType!=tempNode->info->paramType)
						flag=false;
					if(pID!=tempNode->info->paramID)
						flag=false;
					if(!flag)
					{
						fprintf(errorOut,"\nError at line %d : Function declaration and definition didn't match of %s\n",yylineno,$2->name.c_str());
						errorCount++;
					}
					else 
					{
						table->remove($2->name.c_str());
						SymbolInfo *node= new SymbolInfo($2->name.c_str());
						node->function = true;
						node->info = new FunctionInfo();
						node->info->funcDef = true;
						node->info->funcDec = true;
						node->info->retType = $1->name.c_str();
						node->info->paramType = pType;
						node->info->paramID = pID;
						node->lineno = $2->lineno;
						table->insert(node,"ID");
					}
				}
				else
				{
					fprintf(errorOut,"\nError at line %d : Redefinition of function %s\n",yylineno,$2->name.c_str());
					errorCount++;
				}
			}
			else
			{
				fprintf(errorOut,"\nError at line %d : Multiple Declaration of %s\n",yylineno,$2->name.c_str());
				errorCount++;
			}
			} compound_statement /*{table->printAllTable(logOut); table->exitScope(logOut);}*/
		{
			fprintf(logOut,"\nAt line no: %d func_definition : type_specifier ID LPAREN RPAREN compound_statement\n",yylineno);
			fprintf(logOut,"\n%s %s()%s\n",$1->name.c_str(),$2->name.c_str(),$6->name.c_str());
			string temp;
			temp+=$1->name.c_str();
			temp+=" ";
			temp+=$2->name.c_str();
			temp+="()";
			temp+=$6->name.c_str();
			$$ = new SymbolInfo(temp.c_str());
		}
		| error compound_statement
		{
			pType.clear();
			pID.clear();
			string temp = "FUNCTION DEFINATION ERROR\n";
			temp+=$2->name.c_str();
			$$ = new SymbolInfo(temp.c_str());
		}
 		;

parameter_list  : parameter_list COMMA type_specifier ID
		{
			fprintf(logOut,"\nAt line no: %d parameter_list  : parameter_list COMMA type_specifier ID\n",yylineno);
			fprintf(logOut,"\n%s,%s %s\n",$1->name.c_str(),$3->name.c_str(),$4->name.c_str());
			string temp;
			temp+=$1->name.c_str();
			temp+=",";
			temp+=$3->name.c_str();
			temp+=" ";
			temp+=$4->name.c_str();
			$$ = new SymbolInfo(temp.c_str());
			// vector			
			pType.push_back($3->name.c_str());
			pID.push_back($4->name.c_str());
		}
		| parameter_list COMMA type_specifier
		{
			fprintf(logOut,"\nAt line no: %d parameter_list  : parameter_list COMMA type_specifier\n",yylineno);
			fprintf(logOut,"\n%s,%s\n",$1->name.c_str(),$3->name.c_str());
			string temp;
			temp+=$1->name.c_str();
			temp+=",";
			temp+=$3->name.c_str();
			$$ = new SymbolInfo(temp.c_str());
			// vector
			pType.push_back($3->name.c_str());
			pID.push_back("un");
		}
 		| type_specifier ID
		{
			fprintf(logOut,"\nAt line no: %d parameter_list  : type_specifier ID\n",yylineno);
			fprintf(logOut,"\n%s %s\n",$1->name.c_str(),$2->name.c_str());
			string temp;
			temp+=$1->name.c_str();
			temp+=" ";
			temp+=$2->name.c_str();
			$$ = new SymbolInfo(temp.c_str());
			// vector
			pType.push_back($1->name.c_str());
			pID.push_back($2->name.c_str());
		}
		| type_specifier
		{
			fprintf(logOut,"\nAt line no: %d parameter_list  : type_specifier\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			// vector
			pType.push_back($1->name.c_str());
			pID.push_back("un");
		}
 		;

compound_statement : LCURL {
			table->enterScope(logOut);
			// parameters into table
			for(it=pID.begin(),it2=pType.begin();it!=pID.end(),it2!=pType.end();++it,++it2)
			{
				SymbolInfo *node = new SymbolInfo(*it);
				node->vtype = *it2;
				if(table->search(*it)==NULL)
				{
					table->insert(node,"ID");
				}
				else
				{
					fprintf(errorOut,"\nError at line %d : Multiple Declaration of %s\n",yylineno,(*it).c_str());
					errorCount++;
				}
			}
			pType.clear();
			pID.clear();
			} statements RCURL 
		{
			fprintf(logOut,"\nAt line no: %d compound_statement : LCURL statements RCURL\n",yylineno);
			fprintf(logOut,"\n{\n%s\n}\n",$3->name.c_str());
			string temp;
			temp+="\{\n";
			temp+=$3->name.c_str();
			temp+="\n\}";
			$$ = new SymbolInfo(temp.c_str());
			table->printAllTable(logOut); table->exitScope(logOut);
		}
 		| LCURL {table->enterScope(logOut);} RCURL
		{
			fprintf(logOut,"\nAt line no: %d compound_statement : LCURL RCURL\n",yylineno);
			fprintf(logOut,"\n{\n}\n");
			string temp;
			temp+="\{\n\}";
			$$ = new SymbolInfo(temp.c_str());
			pType.clear();
			pID.clear();
			table->printAllTable(logOut); table->exitScope(logOut);
		}
 		;

var_declaration : type_specifier declaration_list SEMICOLON 
		{
			fprintf(logOut,"\nAt line no: %d var_declaration : type_specifier declaration_list SEMICOLON\n",yylineno);
			fprintf(logOut,"\n%s %s;\n",$1->name.c_str(),$2->name.c_str());
			string temp=$1->name.c_str();
			temp+=" ";
			temp+=$2->name.c_str();
			temp+=";";
			$$ = new SymbolInfo(temp.c_str());
			// symboltable
			for(vit=vID.begin();vit!=vID.end();++vit)
			{
				vInfo *vinfo = *vit;
				if(table->search(vinfo->name.c_str())==NULL)
				{
					SymbolInfo *node = new SymbolInfo();
					node->name = vinfo->name.c_str();
					node->vinfo = new vInfo();
					node->vinfo->name = vinfo->name.c_str();
					node->vinfo->array = vinfo->array;					
					node->vtype = $1->name.c_str();
					table->insert(node,"ID");
				}
				else
				{
					fprintf(errorOut,"\nError at line %d : Multiple Declaration of %s\n",yylineno,vinfo->name.c_str());
					errorCount++;
				}
			}
			vID.clear();
		}
		;

type_specifier	: INT
		{
			fprintf(logOut,"\nAt line no: %d type_specifier : INT\n",yylineno);
			fprintf(logOut,"\nint\n");
			$$ = new SymbolInfo("int","INT");
		}
		| FLOAT
		{
			fprintf(logOut,"\nAt line no: %d type_specifier : FLOAT\n",yylineno);
			fprintf(logOut,"\nfloat\n");
			$$ = new SymbolInfo("float","FLOAT");
		}
		| VOID
		{
			fprintf(logOut,"\nAt line no: %d type_specifier : VOID\n",yylineno);
			fprintf(logOut,"\nvoid\n");
			$$ = new SymbolInfo("void","VOID");
		}	
		;

declaration_list: ID 
		{
			fprintf(logOut,"\nAt line no: %d declaration_list: ID\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			// vector
			vInfo *vinfo = new vInfo();
			vinfo->name = $1->name.c_str();
			vID.push_back(vinfo);
		}
		| declaration_list COMMA ID
		{
			fprintf(logOut,"\nAt line no: %d declaration_list: declaration_list COMMA ID\n",yylineno);
			fprintf(logOut,"\n%s,%s\n",$1->getName().c_str(),$3->getName().c_str());
			string temp;
			temp=$1->name.c_str();
			temp+=",";
			temp+=$3->name.c_str();
			$$ = new SymbolInfo(temp.c_str());
			// vector
			vInfo *vinfo = new vInfo();
			vinfo->name = $3->name.c_str();
			vID.push_back(vinfo);
		}
		| ID LTHIRD CONST_INT RTHIRD
		{
			fprintf(logOut,"\nAt line no: %d declaration_list: ID LTHIRD CONST_INT RTHIRD\n",yylineno);
			string temp;
			temp=$1->getName().c_str();
			temp+="[";
			temp+=$3->getName().c_str();
			temp+="]";
			fprintf(logOut,"\n%s\n",temp.c_str());
			$$ = new SymbolInfo(temp.c_str());
			// vector
			vInfo *vinfo = new vInfo();
			vinfo->name = $1->name.c_str();
			vinfo->array = true;
			vID.push_back(vinfo);			
		}
		| declaration_list COMMA ID LTHIRD CONST_INT RTHIRD
		{
			fprintf(logOut,"\nAt line no: %d declaration_list: declaration_list COMMA ID LTHIRD CONST_INT RTHIRD\n",yylineno);
			string temp;
			temp=$1->getName().c_str();
			temp+=",";
			temp+=$3->getName().c_str();
			temp+="[";
			temp+=$5->getName().c_str();
			temp+="]";
			fprintf(logOut,"\n%s\n",temp.c_str());
			$$ = new SymbolInfo(temp.c_str());
			// vector
			vInfo *vinfo = new vInfo();
			vinfo->name = $3->name.c_str();
			vinfo->array = true;
			vID.push_back(vinfo);
		}
		| ID LTHIRD CONST_FLOAT RTHIRD
		{
			fprintf(logOut,"\nAt line no: %d declaration_list: ID LTHIRD CONST_INT RTHIRD\n",yylineno);
			string temp;
			temp=$1->getName().c_str();
			temp+="[";
			temp+="error";
			temp+="]";
			fprintf(logOut,"\n%s\n",temp.c_str());
			$$ = new SymbolInfo(temp.c_str());
			// vector
			vInfo *vinfo = new vInfo();
			vinfo->name = $1->name.c_str();
			vinfo->array = true;
			vID.push_back(vinfo);
			fprintf(errorOut,"\nError at line %d : Non-integer Array Index\n",yylineno);
			errorCount++;
		}
		| declaration_list COMMA ID LTHIRD CONST_FLOAT RTHIRD
		{
			fprintf(logOut,"\nAt line no: %d declaration_list: declaration_list COMMA ID LTHIRD CONST_INT RTHIRD\n",yylineno);
			string temp;
			temp=$1->getName().c_str();
			temp+=",";
			temp+=$3->getName().c_str();
			temp+="[";
			temp+="error";
			temp+="]";
			fprintf(logOut,"\n%s\n",temp.c_str());
			$$ = new SymbolInfo(temp.c_str());
			// vector
			vInfo *vinfo = new vInfo();
			vinfo->name = $3->name.c_str();
			vinfo->array = true;
			vID.push_back(vinfo);
			fprintf(errorOut,"\nError at line %d : Non-integer Array Index\n",yylineno);
			errorCount++;
		}
		;

statements : statement
		{
			fprintf(logOut,"\nAt line no: %d statements : statement\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
		}
	        | statements statement
		{
			fprintf(logOut,"\nAt line no: %d statements : statements statement\n",yylineno);
			fprintf(logOut,"\n%s\n%s\n",$1->name.c_str(),$2->name.c_str());
			string temp;
			temp=$1->name.c_str();
			temp+="\n";
			temp+=$2->name.c_str();
			$$->name = temp.c_str();
		}
	        ;
	   
statement : var_declaration
		{
			fprintf(logOut,"\nAt line no: %d statement : var_declaration\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
		}
		| expression_statement
		{
			fprintf(logOut,"\nAt line no: %d statement : expression_statement\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
		}
		| compound_statement
		{
			fprintf(logOut,"\nAt line no: %d statement : compound_statement\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
		}
		| FOR LPAREN expression_statement expression_statement expression RPAREN statement
		{
			fprintf(logOut,"\nAt line no: %d statement : FOR LPAREN expression_statement expression_statement expression RPAREN statement\n",yylineno);
			fprintf(logOut,"\nfor(%s%s%s)\n%s\n",$3->name.c_str(),$4->name.c_str(),$5->name.c_str(),$7->name.c_str());
			string temp="for(";
			temp+=$3->name.c_str();
			temp+=$4->name.c_str();
			temp+=$5->name.c_str();
			temp+=")\n";
			temp+=$7->name.c_str();
			$$ = new SymbolInfo(temp.c_str());
		}
		| IF LPAREN expression RPAREN statement %prec LOWER_THAN_ELSE
		{
			fprintf(logOut,"\nAt line no: %d statement : IF LPAREN expression RPAREN statement\n",yylineno);
			fprintf(logOut,"\nif(%s)\n%s\n",$3->name.c_str(),$5->name.c_str());
			string temp="if(";
			temp+=$3->name.c_str();
			temp+=")\n";
			temp+=$5->name.c_str();
			$$ = new SymbolInfo(temp.c_str());
		}
	        | IF LPAREN expression RPAREN statement ELSE statement
		{
			fprintf(logOut,"\nAt line no: %d statement : IF LPAREN expression RPAREN statement ELSE statement\n",yylineno);
			fprintf(logOut,"\nif(%s)\n%s\nelse\n%s\n",$3->name.c_str(),$5->name.c_str(),$7->name.c_str());
			string temp="if(";
			temp+=$3->name.c_str();
			temp+=")\n";
			temp+=$5->name.c_str();
			temp+="\nelse\n";
			temp+=$7->name.c_str();
			$$ = new SymbolInfo(temp.c_str());
		}
		| WHILE LPAREN expression RPAREN statement
		{
			fprintf(logOut,"\nAt line no: %d statement : WHILE LPAREN expression RPAREN statement\n",yylineno);
			fprintf(logOut,"\nwhile(%s)\n%s\n",$3->name.c_str(),$5->name.c_str());
			string temp="while(";
			temp+=$3->name.c_str();
			temp+=")\n";
			temp+=$5->name.c_str();
			$$ = new SymbolInfo(temp.c_str());
		}
		| PRINTLN LPAREN ID RPAREN SEMICOLON
		{
			fprintf(logOut,"\nAt line no: %d statement : PRINTLN LPAREN ID RPAREN SEMICOLON\n",yylineno);
			fprintf(logOut,"\nprintln(%s);\n",$3->name.c_str());
			string temp="println(";
			temp+=$3->name.c_str();
			temp+=");";
			$$ = new SymbolInfo(temp.c_str());
		}
		| RETURN expression SEMICOLON
		{
			fprintf(logOut,"\nAt line no: %d statement : RETURN expression SEMICOLON\n",yylineno);
			fprintf(logOut,"\nreturn %s;\n",$2->name.c_str());
			string temp="return ";
			temp+=$2->name.c_str();
			temp+=";";
			$$ = new SymbolInfo(temp.c_str());
		}
		| error SEMICOLON
		{
			$$ = new SymbolInfo("Statement ERROR");
		}
		;

expression_statement 	: SEMICOLON
		{
			fprintf(logOut,"\nAt line no: %d expression_statement 	: SEMICOLON\n",yylineno);
			fprintf(logOut,"\n\;\n");
			string temp=";";			
			$$ = new SymbolInfo(temp.c_str());
		}			
		| expression SEMICOLON
		{
			fprintf(logOut,"\nAt line no: %d expression_statement 	: expression SEMICOLON\n",yylineno);
			fprintf(logOut,"\n%s\;\n",$1->name.c_str());
			string temp=$1->name.c_str();
			temp+=";";			
			$$ = new SymbolInfo(temp.c_str());
		} 
		;

variable : ID 		
		{
			fprintf(logOut,"\nAt line no: %d variable : ID\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			SymbolInfo *node = table->searchAll($1->name.c_str());
			if(node==NULL)
			{
				$$ = new SymbolInfo($1->name.c_str());
				$$->vtype = "void";
			}
			else
			{
				$$ = table->searchAll($1->name.c_str());
			}			
			//type check
			if(node==NULL)
			{
				fprintf(errorOut,"\nError at line %d : Undeclared variable %s\n",yylineno,$1->name.c_str());
				errorCount++;
			}
			else 
			{
				if(node->function)
				{
					fprintf(errorOut,"\nError at line %d : %s is declared as a function at line %d\n",yylineno,$1->name.c_str(),node->lineno);
					errorCount++;
				}
				else if(node->vinfo!=NULL and node->vinfo->array)
				{
					fprintf(errorOut,"\nError at line %d : %s is declared as array at line %d\n",yylineno,$1->name.c_str(),node->lineno);
					errorCount++;
				}
			}
		}
	 	| ID LTHIRD expression RTHIRD 
		{
			fprintf(logOut,"\nAt line no: %d variable : ID LTHIRD expression RTHIRD\n",yylineno);
			fprintf(logOut,"\n%s[%s]\n",$1->name.c_str(),$3->name.c_str());
			string temp;
			temp+=$1->name.c_str();
			temp+="[";
			temp+=$3->name.c_str();
			temp+="]";
			//$$ = new SymbolInfo(temp.c_str());
			// type check 
			SymbolInfo *node = table->searchAll($1->name.c_str());
			if(node==NULL)
			{
				$$ = new SymbolInfo(temp.c_str());
				$$->vtype = "void";
			}
			else
			{
				$$ = new SymbolInfo(temp.c_str());
				$$->vtype = node->vtype;
			}	
			if(node==NULL)
			{ 
				fprintf(errorOut,"\nError at line %d : Undeclared variable %s\n",yylineno,$1->name.c_str());
				errorCount++;
			}
			else
			{
				if(node->function)
				{
					fprintf(errorOut,"\nError at line %d : %s is declared as a function at line %d\n",yylineno,$1->name.c_str(),node->lineno);
					errorCount++;
				}
				else if(node->vinfo!=NULL and !node->vinfo->array)
				{
					fprintf(errorOut,"\nError at line %d : %s is not declared as array at line %d\n",yylineno,$1->name.c_str(),node->lineno);
					errorCount++;
				}
				else
				{
					if($3->vtype!="int")
					{
						fprintf(errorOut,"\nError at Line %d : Non-integer Array Index\n",yylineno);
						errorCount++;
					}
				}
			}
		}
	 	;

 expression : logic_expression
		{
			fprintf(logOut,"\nAt line no: %d expression : logic_expression\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			$$->vtype = $1->vtype;
		}	
	        | variable ASSIGNOP logic_expression
		{
			fprintf(logOut,"\nAt line no: %d expression : variable ASSIGNOP logic_expression\n",yylineno);
			fprintf(logOut,"\n%s=%s\n",$1->name.c_str(),$3->name.c_str());
			string temp=$1->name.c_str();
			temp+="=";
			temp+=$3->name.c_str();
			$$ = new SymbolInfo(temp.c_str());
			if($1->vtype!=$3->vtype && $3->vtype!="void" && $1->vtype!="void")
			{
				fprintf(errorOut,"\nError at line %d : Type mismatch\n",yylineno);
				errorCount++;
				$$->vtype = "void";
			}
			else 
			{
				$$->vtype = $1->vtype;
			}
		} 	
		;

logic_expression : rel_expression 
		{
			fprintf(logOut,"\nAt line no: %d logic_expression : rel_expression\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			$$->vtype = $1->vtype;
		}	
		| rel_expression LOGICOP rel_expression
		{
			fprintf(logOut,"\nAt line no: %d logic_expression : rel_expression LOGICOP rel_expression\n",yylineno);
			fprintf(logOut,"\n%s%s%s\n",$1->name.c_str(),$2->name.c_str(),$3->name.c_str());
			string temp=$1->name.c_str();
			temp+=$2->name.c_str();
			temp+=$3->name.c_str();
			$$ = new SymbolInfo(temp.c_str());
			if($1->vtype=="void" || $3->vtype=="void")
			{
				$$->vtype = "void";
			}
			else 
			{
				$$->vtype = "int";
			}
		} 	
		;

rel_expression	: simple_expression 
		{
			fprintf(logOut,"\nAt line no: %d rel_expression	: simple_expression\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			$$->vtype = $1->vtype;
		}
		| simple_expression RELOP simple_expression
		{
			fprintf(logOut,"\nAt line no: %d rel_expression	: simple_expression RELOP simple_expression\n",yylineno);
			fprintf(logOut,"\n%s%s%s\n",$1->name.c_str(),$2->name.c_str(),$3->name.c_str());
			string temp=$1->name.c_str();
			temp+=$2->name.c_str();
			temp+=$3->name.c_str();
			$$ = new SymbolInfo(temp.c_str());
			if($1->vtype=="void" || $3->vtype=="void")
			{
				$$->vtype = "void";
			}
			else 
			{
				$$->vtype = "int";
			}
		}
		;

simple_expression : term 
		{
			fprintf(logOut,"\nAt line no: %d simple_expression : term\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			$$->vtype = $1->vtype;
		}
		| simple_expression ADDOP term
		{
			fprintf(logOut,"\nAt line no: %d simple_expression : simple_expression ADDOP term\n",yylineno);
			fprintf(logOut,"\n%s%s%s\n",$1->name.c_str(),$2->name.c_str(),$3->name.c_str());
			string temp=$1->name.c_str();
			temp+=$2->name.c_str();
			temp+=$3->name.c_str();
			$$ = new SymbolInfo(temp.c_str());
			if($1->vtype=="int" && $3->vtype=="int")
			{
				$$->vtype = "int";
			}
			else if($1->vtype=="void" || $3->vtype=="void")
			{
				$$->vtype = "void";
			}
			else 
			{
				$$->vtype = "float";
			}
		} 
		;

term :	unary_expression
		{
			fprintf(logOut,"\nAt line no: %d term :	unary_expression\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			$$->vtype = $1->vtype;
		}
     		|  term MULOP unary_expression
		{
			fprintf(logOut,"\nAt line no: %d term :	term MULOP unary_expression\n",yylineno);
			fprintf(logOut,"\n%s%s%s\n",$1->name.c_str(),$2->name.c_str(),$3->name.c_str());
			string temp=$1->name.c_str();
			temp+=$2->name.c_str();
			temp+=$3->name.c_str();
			$$ = new SymbolInfo(temp.c_str());
			if($1->vtype=="int" && $3->vtype=="int")
			{
				$$->vtype = "int";
			}
			else if($1->vtype=="void" || $3->vtype=="void")
			{
				$$->vtype = "void";
			}
			else 
			{
				$$->vtype = "float";
			}
			// modulas
			if($2->name == "%")
			{
				if($1->vtype!="int" || $3->vtype!="int")
				{
					fprintf(errorOut,"\nError at line %d : Non-Integer operand on modulus operator\n",yylineno);
					errorCount++;
					$$->vtype = "void";
				}
			}
		}
     		;

unary_expression : ADDOP unary_expression
		{
			fprintf(logOut,"\nAt line no: %d unary_expression : ADDOP unary_expression\n",yylineno);
			fprintf(logOut,"\n%s%s\n",$1->name.c_str(),$2->name.c_str());
			string temp=$1->name.c_str();
			temp+=$2->name.c_str();
			$$ = new SymbolInfo(temp.c_str());
			$$->vtype = $2->vtype;
		}  
		| NOT unary_expression 
		{
			fprintf(logOut,"\nAt line no: %d unary_expression : NOT unary_expression\n",yylineno);
			fprintf(logOut,"\n!%s\n",$2->name.c_str());
			string temp="!";
			temp+=$2->name.c_str();
			$$ = new SymbolInfo(temp.c_str());
			$$->vtype = "int";
		}
		| factor 
		{
			fprintf(logOut,"\nAt line no: %d unary_expression : factor\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			$$->vtype = $1->vtype;
		}
		;

factor	: variable 
		{
			fprintf(logOut,"\nAt line no: %d factor	: variable\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			$$->vtype = $1->vtype;
		}
		| ID LPAREN argument_list RPAREN // oo
		{
			fprintf(logOut,"\nAt line no: %d factor	: ID LPAREN argument_list RPAREN\n",yylineno);
			fprintf(logOut,"\n%s(%s)\n",$1->name.c_str(),$3->name.c_str());
			string temp=$1->name.c_str();
			temp+="(";
			temp+=$3->name.c_str();
			temp+=")";
			$$ = new SymbolInfo(temp.c_str());
			//function
			SymbolInfo *node = table->searchAll($1->name.c_str());
			if(node==NULL)
			{
				fprintf(errorOut,"\nError at line %d : Undeclared function %s\n",yylineno,$1->name.c_str());
				errorCount++;
			}
			else if(!node->function)
			{
				fprintf(errorOut,"\nError at line %d : %s is not declared as a function at line %d\n",yylineno,$1->name.c_str(),node->lineno);
				errorCount++;
			}
			else
			{
				if(node->info->retType=="void")
				{
					fprintf(errorOut,"\nError at line %d : Return type void function %s used in expession\n",yylineno,$1->name.c_str());
					errorCount++;
				}
				if(node->info->paramType!=$3->info->paramType)
				{
					fprintf(errorOut,"\nError at line %d : Type mismatch in function parameter\n",yylineno);	
					errorCount++;
				}
			}
			if(node!=NULL)
			{
				if(node->info->retType=="int")
				{
					$$->vtype = "int";
				}
				else if(node->info->retType=="float")
				{
					$$->vtype = "float";
				}
				else 
				{
					$$->vtype = "void";
				}
			}
			else
			{
				$$->vtype = "void";
			}
		}
		| LPAREN expression RPAREN
		{
			fprintf(logOut,"\nAt line no: %d factor	: LPAREN expression RPAREN\n",yylineno);
			fprintf(logOut,"\n(%s)\n",$2->name.c_str());
			string temp="(";
			temp+=$2->name.c_str();
			temp+=")";
			$$ = new SymbolInfo(temp.c_str());
			$$->vtype = $2->vtype;
		}
		| CONST_INT
		{
			fprintf(logOut,"\nAt line no: %d factor	: CONST_INT\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			$$->vtype = "int";
		}
		| CONST_FLOAT 
		{
			fprintf(logOut,"\nAt line no: %d factor	: CONST_FLOAT\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			$$->vtype = "float";
		}
		| variable INCOP
		{
			fprintf(logOut,"\nAt line no: %d factor	: variable INCOP\n",yylineno);
			fprintf(logOut,"\n%s++\n",$1->name.c_str());
			string temp=$1->name.c_str();
			temp+="++";
			$$ = new SymbolInfo(temp.c_str());
			$$->vtype = $1->vtype;
		} 
		| variable DECOP
		{
			fprintf(logOut,"\nAt line no: %d factor	: variable DECOP\n",yylineno);
			fprintf(logOut,"\n%s--\n",$1->name.c_str());
			string temp=$1->name.c_str();
			temp+="--";
			$$ = new SymbolInfo(temp.c_str());
			$$->vtype = $1->vtype;
		}

argument_list : arguments
		{
			fprintf(logOut,"\nAt line no: %d argument_list : arguments\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			$$->info = new FunctionInfo();
			$$->info = $1->info;
		}
		| /* empty */
		{
			$$ = new SymbolInfo("");	
			$$->info = new FunctionInfo();
		}
		;

arguments : arguments COMMA logic_expression
		{
			fprintf(logOut,"\nAt line no: %d arguments : arguments COMMA logic_expression\n",yylineno);
			fprintf(logOut,"\n%s,%s\n",$1->name.c_str(),$3->name.c_str());
			string temp;
			temp+=$1->name.c_str();
			temp+=",";			
			temp+=$3->name.c_str();
			$$ = new SymbolInfo(temp.c_str());
			$$->info = new FunctionInfo();
			$$->info = $1->info;
			SymbolInfo *node = table->searchAll($3->name.c_str());
			if(node!=NULL && node->vinfo!=NULL && node->vinfo->array)
			{
				$$->info->paramType.push_back("err");	
			}
			else
			{
				$$->info->paramType.push_back($3->vtype.c_str());
			}			
		}
	        | logic_expression
		{
			fprintf(logOut,"\nAt line no: %d arguments : logic_expression\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			$$->info = new FunctionInfo();
			SymbolInfo *node = table->searchAll($1->name.c_str());
			if(node!=NULL && node->vinfo!=NULL && node->vinfo->array)
			{
				$$->info->paramType.push_back("err");	
			}
			else
			{	
				$$->info->paramType.push_back($1->vtype.c_str());
			}
		}
	        ;


%%

main(int argc,char *argv[])
{
	
	if((fp=fopen(argv[1],"r"))==NULL)
	{
		printf("Cannot Open Input File.\n");
		exit(1);
	}
    
	logOut= fopen("log.txt","w");
	errorOut = fopen("error.txt","w");	

	yyin=fp;
	yyparse();
	fprintf(logOut,"\n\n\n		symbol table: \n");
	table->printCurrentTable(logOut);
	fprintf(logOut,"\nTotal Lines: %d\n",yylineno-1);
	fprintf(logOut,"\nTotal Errors: %d\n\n",errorCount);
	fprintf(errorOut,"\nTotal Errors: %d\n",errorCount);
	fclose(fp);
	fclose(logOut);
	fclose(errorOut);

    return 0;
}

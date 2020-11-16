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


FILE *fp,*logOut,*errorOut,*asmOut;
int errorCount;

/* interfacing in lexer */
extern int yylineno;
void yyerror(char *s){
	fprintf(errorOut,"\nAt line no: %d %s\n",yylineno,s);
}
int yyparse(void);
int yylex(void);
extern FILE *yyin;

SymbolTable *table = new SymbolTable();
vector<string> pType,pID,varID;
vector<vInfo*> vID;
vector<string> :: iterator it,it2,argit;
vector<vInfo*> :: iterator vit;
string tempReturn;
string dataSegment;


int labelCount=0;
int tempCount=0;

char *newLabel()
{
	char *lb= new char[4];
	strcpy(lb,"L");
	char b[3];
	sprintf(b,"%d", labelCount);
	labelCount++;
	strcat(lb,b);
	return lb;
}

char *newTemp()
{
	char *t= new char[4];
	strcpy(t,"t");
	char b[3];
	sprintf(b,"%d", tempCount);
	tempCount++;
	strcat(t,b);
	return t;
}

string to_upper(string str)
{
    transform(str.begin(), str.end(), str.begin(), static_cast<int(*)(int)>(toupper));
    return str;
}

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
			$$ = new SymbolInfo($1->name.c_str());			
			//asm
			$$->code = $1->code;
			$$->symbol = $1->symbol;						

			fprintf(logOut,$$->code.c_str());
			//final 
			asmOut = fopen("code.asm","w");
			fprintf(asmOut,".MODEL SMALL\n\n.STACK 100h\n\n.DATA\n");
			fprintf(asmOut,"%s",dataSegment.c_str());
			char temp[3];
			for(int i=0;i<tempCount;i++)
			{
				sprintf(temp,"%d",i);
				string out = "\tt"+string(temp)+" DW ?\n";
				fprintf(asmOut,"%s",out.c_str());
			}
			fprintf(asmOut,"\ttempPOP DW ?\n");
			fprintf(asmOut,"\n.CODE\n\n");
			fprintf(asmOut,"%s\n",$$->code.c_str());
			//OUTDEC function
			char c;
			FILE *fp = fopen("OUTDEC","r");
			while ((c = fgetc(fp)) != EOF)
      				fputc(c,asmOut);	

			fprintf(asmOut,"\t\nEND MAIN\n");
		}
	 ;

program  : program unit 
		{
			fprintf(logOut,"\nAt line no: %d program  : program unit\n",yylineno);
			fprintf(logOut,"\n%s\n%s\n",$1->name.c_str(),$2->name.c_str());
			string temp=$1->name.c_str();
			temp+="\n";
			temp+=$2->name.c_str();
			$$ = new SymbolInfo(temp.c_str());			
			//asm
			$$->code = $1->code;
			$$->code += "\n\n";
			$$->code += $2->code;
			$$->symbol = $1->symbol;						

			fprintf(logOut,$$->code.c_str());
		}
	 | unit
		{
			fprintf(logOut,"\nAt line no: %d program  : unit\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());			
			//asm
			$$->code = $1->code;
			$$->symbol = $1->symbol;						

			fprintf(logOut,$$->code.c_str());			
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
			$$ = new SymbolInfo($1->name.c_str());			
			//asm
			$$->code = $1->code;
			$$->symbol = $1->symbol;						

			fprintf(logOut,$$->code.c_str());	
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
				node->info->tableID = table->getID()+1; //asm
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
						node->info->tableID = table->getID()+1; //asm
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
			} compound_statement 
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
			if($1->name!=tempReturn && $1->name!="void")
			{
				fprintf(errorOut,"\nError at line %d : Return type didn't match on function %s\n",$2->lineno,$2->name.c_str());
				errorCount++;
			}
			if($1->name=="void")
			{
				if(tempReturn!="")
				{
					fprintf(errorOut,"\nError at line %d : Return in void function %s\n",$2->lineno,$2->name.c_str());
					errorCount++;
				}
			}
			//asm
			$$->code += to_upper($2->name) + " PROC\n";
			if($2->name=="main")
			{
				$$->code += "\tMOV AX,@DATA\n\tMOV DS,AX\n\n";
				if($7->code.rfind("POP DX")!=-1)
				{
					$7->code.erase($7->code.rfind("POP DX"));
				}
			}
			//save register
			if($2->name!="main")
			{
				$$->code += "\tPUSH AX\n\tPUSH BX\n\tPUSH CX\n\tPUSH DX\n";
			}
			$$->code += $7->code;
			if(tempReturn!="int" && tempReturn!="float" && $2->name!="main")
			{
				$$->code += "\tPOP DX\n\tPOP CX\n\tPOP BX\n\tPOP AX\n";
				$$->code += "\tRET\n";
			}
			if($2->name=="main")
			{
				$$->code += "\n\tMOV AH,4Ch\n\tINT 21h\n";
			}
			$$->code += to_upper($2->name) + " ENDP\n";						

			fprintf(logOut,$$->code.c_str());
			tempReturn="";
			//new
			/*SymbolInfo *tempNode=table->search($2->name.c_str());
			if(tempNode!=NULL and tempNode->info!=NULL)
			{
				
				tempNode->info->varID = varID;
			}
			varID.clear();*/
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
				node->info->tableID = table->getID()+1; //asm
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
						node->info->tableID = table->getID()+1; //asm
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
			} compound_statement 
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
			if($1->name!=tempReturn && $1->name!="void")
			{
				fprintf(errorOut,"\nError at line %d : Return type didn't match on function %s\n",$2->lineno,$2->name.c_str());
				errorCount++;
			}
			if($1->name=="void")
			{
				
				if(tempReturn!="")
				{
					fprintf(errorOut,"\nError at line %d : Return in void function %s\n",$2->lineno,$2->name.c_str());
					errorCount++;
				}
			}
			//asm
			$$->code += to_upper($2->name) + " PROC\n";
			if($2->name=="main")
			{
				$$->code += "\tMOV AX,@DATA\n\tMOV DS,AX\n\n";
				if($6->code.rfind("POP DX")!=-1)
				{
					$6->code.erase($6->code.rfind("POP DX"));
				}
			}
			//save register
			if($2->name!="main")
			{
				$$->code += "\tPUSH AX\n\tPUSH BX\n\tPUSH CX\n\tPUSH DX\n";
			}
			$$->code += $6->code;
			if(tempReturn!="int" && tempReturn!="float" && $2->name!="main")
			{
				$$->code += "\tPOP DX\n\tPOP CX\n\tPOP BX\n\tPOP AX\n";
				$$->code += "\tRET\n";
			}
			if($2->name=="main")
			{
				$$->code += "\n\tMOV AH,4Ch\n\tINT 21h\n";
			}
			$$->code += to_upper($2->name) + " ENDP\n";						

			fprintf(logOut,$$->code.c_str());
			tempReturn="";
			//new
			/*SymbolInfo *tempNode=table->search($2->name.c_str());
			if(tempNode!=NULL and tempNode->info!=NULL)
			{
				
				tempNode->info->varID = varID;
			}
			varID.clear();*/
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
					node->vinfo = new vInfo();
					node->vinfo->array = false;					
					table->insert(node,"ID");
				}
				else
				{
					fprintf(errorOut,"\nError at line %d : Multiple Declaration of %s\n",yylineno,(*it).c_str());
					errorCount++;
				}
				//asm
				int tableID = table->searchID((*it).c_str());
				char var[30];
				sprintf(var,"%s%d",(*it).c_str(),tableID);
				dataSegment+="\t"+string(var)+" DW ?\n";
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
			///asm
			$$->code = $3->code;
			$$->symbol = $3->code;

			fprintf(logOut,$$->code.c_str());
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
			///asm

			fprintf(logOut,$$->code.c_str());
		}
 		;

//done
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
					//asm
					if(vinfo->array)
					{
						int tableID = table->searchID(vinfo->name);
						char var[30];
						sprintf(var,"%s%d",vinfo->name.c_str(),tableID);
						dataSegment+="\t"+string(var)+" DW " + vinfo->arraySize +" DUP (?)\n";
						//varID.push_back(var); //new
					}
					else
					{
						int tableID = table->searchID(vinfo->name);
						char var[30];
						sprintf(var,"%s%d",vinfo->name.c_str(),tableID);
						dataSegment+="\t"+string(var)+" DW ?\n";
						//varID.push_back(var); //new
					}
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
			vinfo->arraySize =$3->name;
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
			vinfo->arraySize = $5->name;
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
			$$ = new SymbolInfo($1->name.c_str());
			//asm
			$$->code = $1->code;
			$$->symbol = $1->symbol;

			fprintf(logOut,$$->code.c_str());
		}
	        | statements statement
		{
			fprintf(logOut,"\nAt line no: %d statements : statements statement\n",yylineno);
			fprintf(logOut,"\n%s\n%s\n",$1->name.c_str(),$2->name.c_str());
			string temp;
			temp=$1->name.c_str();
			temp+="\n";
			temp+=$2->name.c_str();
			$$ = new SymbolInfo(temp.c_str());
			//asm
			$$->code = $1->code;
			$$->code += $2->code;
			$$->symbol = $1->symbol;

			fprintf(logOut,$$->code.c_str());
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
			//asm
			$$->code = $1->code;
			$$->symbol = $1->symbol;

			fprintf(logOut,$$->code.c_str());
		}
		| compound_statement
		{
			fprintf(logOut,"\nAt line no: %d statement : compound_statement\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			//asm
			$$->code = $1->code;
			$$->symbol = $1->symbol;

			fprintf(logOut,$$->code.c_str());
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
			//asm
			char *label1 = newLabel();	
			char *label2 = newLabel();			
			$$->code += $3->code; //init 
			$$->code += string(label1) + ":\n";
			$$->code += $4->code; //condition
			$$->code += "\tMOV AX," + $4->symbol + "\n"; // check condition
			$$->code += "\tCMP AX,0\n";
			$$->code += "\tJE " + string(label2) + "\n"; // false then end 
			$$->code += $7->code; // statement 
			$$->code += $5->code; // inc/dec
			$$->code += "\tJMP " + string(label1) + "\n";
			$$->code += string(label2) + ":\n";

			fprintf(logOut,$$->code.c_str());
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
			// asm
			char *label1 = newLabel();
			$$->code += $3->code;
			$$->code += "\tMOV AX," + $3->symbol + "\n"; // check condition
			$$->code += "\tCMP AX,0\n";
			$$->code += "\tJE " + string(label1) + "\n"; // false then end 
			$$->code += $5->code;
			$$->code += string(label1) + ":\n";

			fprintf(logOut,$$->code.c_str());
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
			// asm
			char *label1 = newLabel();
			char *label2 = newLabel();
			$$->code += $3->code;
			$$->code += "\tMOV AX," + $3->symbol + "\n"; // check condition
			$$->code += "\tCMP AX,0\n";
			$$->code += "\tJE " + string(label1) + "\n"; // false 
			$$->code += $5->code;
			$$->code += "\tJMP " + string(label2) + "\n"; // jump to end
			$$->code += string(label1) + ":\n";
			$$->code += $7->code;
			$$->code += string(label2) + ":\n";

			fprintf(logOut,$$->code.c_str());
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
			//asm
			char *label1 = newLabel();
			char *label2 = newLabel();
			$$->code += string(label1) + ":\n"; //label;
			$$->code += $3->code; //condition
			$$->code += "\tMOV AX," + $3->symbol + "\n"; // check condition
			$$->code += "\tCMP AX,0\n";
			$$->code += "\tJE " + string(label2) + "\n"; // false then end 
			$$->code += $5->code; // statement 
			$$->code += "\tJMP " + string(label1) + "\n";
			$$->code += string(label2) + ":\n";	

			fprintf(logOut,$$->code.c_str());		
		}
		| PRINTLN LPAREN ID RPAREN SEMICOLON
		{
			fprintf(logOut,"\nAt line no: %d statement : PRINTLN LPAREN ID RPAREN SEMICOLON\n",yylineno);
			fprintf(logOut,"\nprintln(%s);\n",$3->name.c_str());
			string temp="println(";
			temp+=$3->name.c_str();
			temp+=");";
			$$ = new SymbolInfo(temp.c_str());
			// asm 
			int tableID = table->searchID($3->name);
			char var[30];
			sprintf(var,"%s%d",$3->name.c_str(),tableID);
			$$->code += "\tMOV AX," + string(var) + "\n";
			$$->code += "\tCALL OUTDEC\n";
			$$->code += "\tMOV AH,2\n\tMOV DL,0Ah\n\tINT 21h\n\tMOV DL,0Dh\n\tINT 21H\n";
			fprintf(logOut,$$->code.c_str());
		}
		| RETURN expression SEMICOLON
		{
			fprintf(logOut,"\nAt line no: %d statement : RETURN expression SEMICOLON\n",yylineno);
			fprintf(logOut,"\nreturn %s;\n",$2->name.c_str());
			string temp="return ";
			temp+=$2->name.c_str();
			temp+=";";
			$$ = new SymbolInfo(temp.c_str());
			tempReturn=$2->vtype;
			//asm
			$$->code += $2->code;
			$$->code += "\tPOP DX\n\tPOP CX\n\tPOP BX\n\tPOP AX\n";
			$$->code += "\tPOP tempPOP\n";
			$$->code += "\tPUSH "+$2->symbol + "\n";
			$$->code += "\tPUSH tempPOP\n";
			$$->code += "\tRET\n";
			$$->symbol = $2->symbol;
		}
		| error SEMICOLON
		{
			$$ = new SymbolInfo("Statement ERROR");
		}
		;


//done
expression_statement 	: SEMICOLON
		{
			fprintf(logOut,"\nAt line no: %d expression_statement 	: SEMICOLON\n",yylineno);
			fprintf(logOut,"\n\;\n");
			string temp=";";			
			$$ = new SymbolInfo(temp.c_str());
			// asm 
			$$->code = "";
			$$->symbol = "";

			fprintf(logOut,$$->code.c_str());
		}			
		| expression SEMICOLON
		{
			fprintf(logOut,"\nAt line no: %d expression_statement 	: expression SEMICOLON\n",yylineno);
			fprintf(logOut,"\n%s\;\n",$1->name.c_str());
			string temp=$1->name.c_str();
			temp+=";";			
			$$ = new SymbolInfo(temp.c_str());
			//asm
			$$->code = $1->code;
			$$->symbol = $1->symbol;

			fprintf(logOut,$$->code.c_str());
		} 
		;

//done
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
			// asm
			$$->code = "";
			int tableID = table->searchID($1->name);
			char var[30];
			sprintf(var,"%s%d",$1->name.c_str(),tableID);
			$$->symbol = var;
			$$->var = $1->name;
			fprintf(logOut,$$->code.c_str());
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
			//asm
			$$->code = $3->code + "\tMOV BX," + $3->symbol + "\n\tADD BX,BX\n";
			int tableID = table->searchID($1->name);
			char var[30];
			sprintf(var,"%s%d",$1->name.c_str(),tableID);
			$$->symbol = var;
			$$->var = $1->name;
			fprintf(logOut,$$->code.c_str());
		}
	 	;

 expression : logic_expression
		{
			fprintf(logOut,"\nAt line no: %d expression : logic_expression\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			$$->vtype = $1->vtype;
			//asm
			$$->code = $1->code;
			$$->symbol = $1->symbol;

			fprintf(logOut,$$->code.c_str());
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
			//asm
			char *var = newTemp();
			$$->code += $3->code;
			$$->code += $1->code;
			SymbolInfo *node = table->searchAll($1->var);
			if(node!=NULL)
			{	
				if(node->vinfo->array)
				{
					$$->code += "\tMOV AX," + $3->symbol + "\n";
					$$->code += "\tMOV " + $1->symbol + "[BX],AX\n";
				}
				else 
				{
					$$->code += "\tMOV AX," + $3->symbol + "\n";
					$$->code += "\tMOV " + $1->symbol + ",AX\n";
				}
				$$->code += "\tMOV " + string(var) + ",AX\n";
				$$->symbol = var;
			}
			
			fprintf(logOut,$$->code.c_str());
		} 	
		;

logic_expression : rel_expression 
		{
			fprintf(logOut,"\nAt line no: %d logic_expression : rel_expression\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			$$->vtype = $1->vtype;
			//asm
			$$->code = $1->code;
			$$->symbol = $1->symbol;

			fprintf(logOut,$$->code.c_str());
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
			//asm
			char *var = newTemp();
			char *label1=newLabel();
			char *label2=newLabel();
			$$->code += $1->code;
			$$->code += $3->code;
			if($2->name == "&&")
			{
				$$->code += "\tMOV AX," + $1->symbol + "\n";
				$$->code += "\tAND AX," + $3->symbol + "\n";
				$$->code += "\tMOV " + string(var) + ",AX\n";
 			}
			else 
			{
				$$->code += "\tMOV AX," + $1->symbol + "\n";
				$$->code += "\tOR AX," + $3->symbol + "\n";
				$$->code += "\tMOV " + string(var) + ",AX\n";
			}
			$$->symbol = var;

			fprintf(logOut,$$->code.c_str());
		} 	
		;

rel_expression	: simple_expression 
		{
			fprintf(logOut,"\nAt line no: %d rel_expression	: simple_expression\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			$$->vtype = $1->vtype;
			//asm
			$$->code = $1->code;
			$$->symbol = $1->symbol;

			fprintf(logOut,$$->code.c_str());
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
			//asm
			char *var = newTemp();
			char *label1=newLabel();
			char *label2=newLabel();
			$$->code += $1->code;
			$$->code += $3->code;
			$$->code += "\tMOV AX," + $1->symbol + "\n";
			$$->code += "\tCMP AX," + $3->symbol + "\n";
			if($2->name == "<")
			{
				$$->code += "\tJL " + string(label1)+"\n";
			}
			else if($2->name == "<=")
			{
				$$->code += "\tJLE " + string(label1)+"\n";
			}
			else if($2->name == ">")
			{
				$$->code += "\tJG " + string(label1)+"\n";
			}
			else if($2->name == ">=")
			{
				$$->code += "\tJGE " + string(label1)+"\n";
			}
			else if($2->name == "==")
			{
				$$->code += "\tJE " + string(label1)+"\n";
			}
			else
			{
				$$->code += "\tJNE " + string(label1)+"\n";
			}
			$$->code += "\tMOV "+string(var) +",0\n";
			$$->code += "\tJMP "+string(label2) +"\n";
			$$->code += string(label1)+":\n\tMOV "+string(var)+",1\n";
			$$->code += string(label2)+":\n";
			$$->symbol = var;
			
			fprintf(logOut,$$->code.c_str());
		}
		;

simple_expression : term 
		{
			fprintf(logOut,"\nAt line no: %d simple_expression : term\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			$$->vtype = $1->vtype;
			//asm
			$$->code = $1->code;
			$$->symbol = $1->symbol;

			fprintf(logOut,$$->code.c_str());
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
			//asm
			char *var = newTemp();
			$$->code += $1->code;
			$$->code += $3->code;
			$$->code += "\tMOV AX," + $1->symbol + "\n";
			if($2->name == "+")
			{
				$$->code +="\tADD AX," + $3->symbol + "\n";
			}
			else 
			{
				$$->code +="\tSUB AX," + $3->symbol + "\n";
			}
			$$->code += "\tMOV " + string(var) + ",AX\n";
			$$->symbol = var;

			fprintf(logOut,$$->code.c_str());
		} 
		;

term :	unary_expression
		{
			fprintf(logOut,"\nAt line no: %d term :	unary_expression\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			$$->vtype = $1->vtype;
			//asm
			$$->code = $1->code;
			$$->symbol = $1->symbol;

			fprintf(logOut,$$->code.c_str());
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
			//asm
			$$->code += $1->code;
			$$->code += $3->code;
			$$->code += "\tMOV AX," + $1->symbol + "\n";
			$$->code += "\tMOV BX," + $3->symbol +"\n";
			char *var=newTemp();
			if($2->name == "*")
			{
				$$->code += "\tMUL BX\n";
				$$->code += "\tMOV "+ string(var) + ",AX\n";
			}
			else if($2->name == "/")
			{
				$$->code += "\tXOR DX,DX\n";
				$$->code += "\tDIV BX\n";
				$$->code += "\tMOV "+ string(var) + ",AX\n";
				// clear dx, perform 'div bx' and mov ax to temp
			}
			else
			{	
				$$->code += "\tXOR DX,DX\n";
				$$->code += "\tDIV BX\n";
				$$->code += "\tMOV "+ string(var) + ",DX\n";
				// clear dx, perform 'div bx' and mov dx to temp
			}
			$$->symbol = var;
			
			fprintf(logOut,$$->code.c_str());
		}
     		;

unary_expression : ADDOP unary_expression //confused
		{
			fprintf(logOut,"\nAt line no: %d unary_expression : ADDOP unary_expression\n",yylineno);
			fprintf(logOut,"\n%s%s\n",$1->name.c_str(),$2->name.c_str());
			string temp=$1->name.c_str();
			temp+=$2->name.c_str();
			$$ = new SymbolInfo(temp.c_str());
			$$->vtype = $2->vtype;
			//asm
			if($1->name == "-")
			{
				char *temp = newTemp();
				$$->code += $2->code;
				$$->code += "\tMOV AX," + $2->symbol + "\n";
				$$->code += "\tNEG AX\n";
				$$->code += "\tMOV " + string(temp) + ",AX\n";
				$$->symbol = temp;
			}	
			else
			{	
				$$->code = $2->code;
				$$->symbol = $2->symbol;
			}

			fprintf(logOut,$$->code.c_str());
		}  
		| NOT unary_expression 
		{	
			fprintf(logOut,"\nAt line no: %d unary_expression : NOT unary_expression\n",yylineno);
			fprintf(logOut,"\n!%s\n",$2->name.c_str());
			string temp="!";
			temp+=$2->name.c_str();
			$$ = new SymbolInfo(temp.c_str());
			$$->vtype = "int";
			//asm
			char *var = newTemp();
			char *label1 = newLabel();
			char *label2 = newLabel();
			$$->code += "\tMOV AX," + $2->symbol + "\n";
			$$->code += "\tCMP AX,0\n";
			$$->code += "\tJE " + string(label1) + "\n";
			$$->code += "\tMOV AX,0\n";
			$$->code += "\tMOV " + string(var) + ",AX\n";
			$$->code += "\tJMP " + string(label2) + "\n";
			$$->code += string(label1) + ":\n";
			$$->code += "\tMOV AX,1\n";
			$$->code += "\tMOV " + string(var) + ",AX\n";
			$$->code += string(label2) + ":\n";
			$$->symbol = var;

			fprintf(logOut,$$->code.c_str());
		}
		| factor 
		{
			fprintf(logOut,"\nAt line no: %d unary_expression : factor\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			$$->vtype = $1->vtype;
			//asm
			$$->code = $1->code;
			$$->symbol = $1->symbol;
						
			fprintf(logOut,$$->code.c_str());
		}
		;

factor	: variable 
		{
			fprintf(logOut,"\nAt line no: %d factor	: variable\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			$$->vtype = $1->vtype;
			//asm			
			SymbolInfo *node = table->searchAll($1->var);
			if(node != NULL)
			{
				if(node->vinfo->array)
				{
					char *temp = newTemp();
					$$->code += $1->code;
					$$->code += "\tMOV AX," + $1->symbol + "[BX]\n";
					$$->code += "\tMOV " + string(temp) + ",AX\n";
					$$->symbol = temp;
				}
				else 
				{
					$$->code = "";
					$$->symbol = $1->symbol;
				}
			}

			fprintf(logOut,$$->code.c_str());
		}
		| ID LPAREN argument_list RPAREN 
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
					//need to fix	
					//fprintf(errorOut,"\nError at line %d : Return type void function %s used in expession\n",yylineno,$1->name.c_str());
					//errorCount++;
				}
				if(node->info->paramType!=$3->info->paramType)
				{
					fprintf(errorOut,"\nError at line %d : Type mismatch in function parameter\n",yylineno);	
					errorCount++;
				}
			}
			if(node!=NULL && node->function)
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
			//asm
			$$->code += $3->code;
			if(node!=NULL && node->function && node->info!=NULL)
			{
				for(it=node->info->paramID.begin(),it2=$3->info->paramID.begin();it!=node->info->paramID.end(),it2!=$3->info->paramID.end();++it,++it2)
					{
						int tableID = node->info->tableID;
						char var[30];
						sprintf(var,"%s%d",(*it).c_str(),tableID);
						$$->code += "\tMOV AX," + *(it2) + "\n";
						$$->code += "\tMOV " + string(var) +",AX\n";
					}
			}
			$$->code += "\tCALL "+to_upper($1->name) + "\n";
			char *var = newTemp();
			if(node->info->retType=="int" || node->info->retType=="float")
				$$->code += "\tPOP " + string(var) + "\n";
			$$->symbol = var;
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
			//asm
			$$->symbol = $2->symbol;
			$$->code = $2->code;

			fprintf(logOut,$$->code.c_str());
		}
		| CONST_INT
		{
			fprintf(logOut,"\nAt line no: %d factor	: CONST_INT\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			$$->vtype = "int";
			//asm
			$$->code = "";
			$$->symbol = $1->name;

			fprintf(logOut,$$->code.c_str());
		}
		| CONST_FLOAT 
		{
			fprintf(logOut,"\nAt line no: %d factor	: CONST_FLOAT\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			$$->vtype = "float";
			//asm
			$$->code = "";
			$$->symbol = $1->name;

			fprintf(logOut,$$->code.c_str());
		}
		| variable INCOP
		{
			fprintf(logOut,"\nAt line no: %d factor	: variable INCOP\n",yylineno);
			fprintf(logOut,"\n%s++\n",$1->name.c_str());
			string temp=$1->name.c_str();
			temp+="++";
			$$ = new SymbolInfo(temp.c_str());
			$$->vtype = $1->vtype;
			//asm
			SymbolInfo *node = table->searchAll($1->var);
			if(node!=NULL)
			{
				if(node->vinfo->array)
				{
					string temp=newTemp();
					$$->code += "\tINC "+ $1->symbol + "[BX]\n";
					$$->code += "\tMOV AX," + $1->symbol + "[BX]\n";
					$$->code += "\tMOV " + string(temp) + ",AX\n";
					$$->symbol = temp; 
				}
				else 
				{
					$$->code += "\tINC "+ $1->symbol + "\n";
					$$->symbol = $1->symbol;
				}
			}	
			fprintf(logOut,$$->code.c_str());
		} 
		| variable DECOP
		{
			fprintf(logOut,"\nAt line no: %d factor	: variable DECOP\n",yylineno);
			fprintf(logOut,"\n%s--\n",$1->name.c_str());
			string temp=$1->name.c_str();
			temp+="--";
			$$ = new SymbolInfo(temp.c_str());
			$$->vtype = $1->vtype;
			//asm
			SymbolInfo *node = table->searchAll($1->var);
			if(node!=NULL)
			{
				if(node->vinfo->array)
				{
					string temp=newTemp();
					$$->code += "\tDEC "+ $1->symbol + "[BX]\n";
					$$->code += "\tMOV AX," + $1->symbol + "[BX]\n";
					$$->code += "\tMOV " + string(temp) + ",AX\n";
					$$->symbol = temp; 
				}
				else 
				{
					$$->code += "\tDEC "+ $1->symbol + "\n";
					$$->symbol = $1->symbol;
				}
			}
			fprintf(logOut,$$->code.c_str());	
		}

argument_list : arguments
		{
			fprintf(logOut,"\nAt line no: %d argument_list : arguments\n",yylineno);
			fprintf(logOut,"\n%s\n",$1->name.c_str());
			$$ = new SymbolInfo($1->name.c_str());
			$$->info = new FunctionInfo();
			$$->info = $1->info;
			//asm
			$$->code = "\t;function parameter\n";
			$$->code += $1->code;
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
				$$->info->paramID.push_back($3->symbol);	
			}
			else
			{
				$$->info->paramType.push_back($3->vtype.c_str());
				$$->info->paramID.push_back($3->symbol);
			}
			//asm
			$$->code = $1->code;
			$$->code += $3->code;			
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
				$$->info->paramID.push_back($1->symbol);	
			}
			else
			{	
				$$->info->paramType.push_back($1->vtype.c_str());
				$$->info->paramID.push_back($1->symbol);
			}
			//asm
			$$->code = $1->code;
			$$->symbol = $1->symbol;
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
	fclose(asmOut);

    return 0;
}

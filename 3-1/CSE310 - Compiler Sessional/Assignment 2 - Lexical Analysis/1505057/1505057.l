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

using namespace std;

int errorCount,tableSize=7;

FILE *logOut;
FILE *tokenOut;

int hashFunction(string key)
{
    int hash = 7;
    REP(i, key.length())
    {
        hash = (hash * 31 + key.at(i)) % tableSize;
    }
    return hash % tableSize;
}

class SymbolInfo
{
private:
    string name, type;
public:
    SymbolInfo *next;

    SymbolInfo()
    {
        name = "";
        type = "";
        next = NULL;
    }

    SymbolInfo(string name, string type)
    {
        this->name = name;
        this->type = type;
        next = NULL;
    }

    string getName()
    {
        return name;
    }

    void setName(string name)
    {
        SymbolInfo::name = name;
    }

    string getType()
    {
        return type;
    }

    void setType(string type)
    {
        SymbolInfo::type = type;
    }
};


class linkedList
{
public:
    SymbolInfo *head, *tail;
    int count;

    linkedList()
    {
        head = NULL;
        tail = NULL;
        count = 0;
    }
};


class ScopeTable
{
public:
    linkedList *data;
    ScopeTable *parentScope;
    int ID;


    ScopeTable(int n)
    {
        data = new linkedList[n];
        parentScope = NULL;
    }

    ~ScopeTable()
    {
        delete parentScope;
        delete[] data;
    }

    bool insert(string name, string type)
    {
        int hash = hashFunction(name);
        if (data[hash].head == NULL)
        {
            SymbolInfo *newNode = new SymbolInfo(name, type);
            data[hash].head = newNode;
            data[hash].tail = newNode;
            /*cout << " Inserted in ScopeTable# " << ID << " at position " << hash << ", " << data[hash].count++ << endl
                 << endl;*/
	    printHash();
            return true;
        }
        if (search(name, false) != NULL)
        {
            /*cout << " <" << name << "," << type << ">already exists in current ScopeTable" << endl << endl;	*/
            return false;
        }
        SymbolInfo *newNode = new SymbolInfo(name, type);
        data[hash].tail->next = newNode;
        data[hash].tail = newNode;
        /*cout << " Inserted in ScopeTable# " << ID << " at position " << hash << ", " << data[hash].count++ << endl
             << endl;*/
 	printHash();
        return true;
    }

    bool remove(string name)
    {
        SymbolInfo *node, *prev;
        int hash = hashFunction(name), pos = 0;
        node = data[hash].head;
        search(name, true);
        if (node == NULL)
        {
            /*cout << name << " not found" << endl << endl;*/
            return false;
        }
        while (node != NULL)
        {
            if (node->getName() == name)
                break;
            prev = node;
            node = node->next;
            pos++;
        }
        if (node == NULL)
        {
            /*cout << name << " not found" << endl << endl;*/
            return false;
        }
        if (node == data[hash].head && node == data[hash].tail)
        {
            data[hash].head = NULL;
            data[hash].tail = NULL;
            free(node);
        }
        else if (node == data[hash].head)
        {
            data[hash].head = data[hash].head->next;
            free(node);
        }
        else if (node == data[hash].tail)
        {
            prev->next = NULL;
            data[hash].tail = prev;
            free(node);
        }
        else
        {
            prev->next = node->next;
            free(node);
        }
        data[hash].count--;
        /*cout << "Deleted entry at " << hash << ", " << pos << " from  current ScopeTable" << endl << endl;*/
        return true;
    }

    SymbolInfo *search(string name, bool pf)
    {
        int hash = hashFunction(name);
        if (data[hash].head == NULL)
        {
            /*if (pf)
                cout << " Not found" << endl << endl;*/
            return NULL;
        }
        SymbolInfo *cur = data[hash].head;
        int pos = 0;
        while (cur != NULL)
        {
            if (cur->getName() == name)
            {
                /*if (pf)
                    cout << " Found in ScopeTable# " << ID << " at position " << hash << ", " << pos << endl << endl;*/
                return cur;
            }
            cur = cur->next;
            pos++;
        }
        /*if (pf)
            cout << " Not found" << endl << endl;*/
        return NULL;
    }


    void printHash()
    {
	fprintf(logOut,"\n ScopeTable # %d\n",ID);
        SymbolInfo *cur;
        REP(i, tableSize)
        {
	    cur = data[i].head;
	    if(cur !=NULL)
		{	
		    fprintf(logOut," %d --> ",i);
		    while (cur != NULL)
		    {
			fprintf(logOut,"<%s, %s> ",cur->getName().c_str(),cur->getType().c_str());		        
			cur = cur->next;
		    }
		    fprintf(logOut,"\n");
		}
        }
    }

};

class SymbolTable
{
public:
    ScopeTable *currentTable;

    SymbolTable()
    {
        ScopeTable *temp = new ScopeTable(tableSize);
        temp->parentScope = NULL;
        temp->ID = 1;
        currentTable = temp;
    }
    
    void enterScopre()
    {
        ScopeTable *temp = new ScopeTable(tableSize);
        temp->parentScope = currentTable;
        temp->ID = temp->parentScope->ID + 1;
        currentTable = temp;
        /*cout << " New ScopeTable with id " << currentTable->ID << " created" << endl << endl;*/
    }

    void exitScope()
    {
        if (currentTable->ID != 1)
        {
            ScopeTable *temp = currentTable;
            currentTable = currentTable->parentScope;
            /*cout << " ScopeTable with id " << temp->ID << " removed" << endl << endl;*/
            delete temp;
        }
        else
        {
            /*cout << " Can not remove ScopeTable #1" << endl << endl;*/
        }
    }

    bool insert(string name, string type)
    {
        return currentTable->insert(name, type);
    }

    bool remove(string name)
    {
        return currentTable->remove(name);
    }

    SymbolInfo *search(string name)
    {
        ScopeTable *cur = currentTable;
        while (cur != NULL)
        {
            SymbolInfo *temp = cur->search(name, true);
            if (temp != NULL)
            {
		cout << "ret temp" << endl;
                return temp;
            }
            cur = cur->parentScope;
        }
        return NULL;
	cout << "ret null" << endl;
    }

    void printCurrentTable()
    {
        currentTable->printHash();
    }

    void printAllTable()
    {
        ScopeTable *cur = currentTable;
        while (cur != NULL)
        {
            cur->printHash();
            cur = cur->parentScope;
        }
    }
};

SymbolTable symbolTable;

%}

WHITESPACE [ \t\f\r\v]+ 
LETTER [a-zA-Z]
DIGIT [0-9]
INTEGER {DIGIT}+
FLOAT ({DIGIT}+[Ee][+-]?{DIGIT}+)|((({DIGIT}+\.{DIGIT}*)|({DIGIT}*\.{DIGIT}+))([eE][+-]?{DIGIT}+)?)
CHAR \'([^'\\\n]|\\[nt\\\'\"afrbv0])\'
INCOP \+\+|\-\-
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


							/* keywords */

"if" | 
"for" |
"do" |
"int" |
"float" |
"void" |
"switch" |
"default" |
"else" |
"while" |
"break" |
"char" |
"double" |
"return" |
"case" |
"continue" 	{
			char prev[15];
			char *temp=yytext;
			int i=0;
			while(temp[i])
			{
				prev[i]=temp[i];
				temp[i]=toupper(temp[i]);
				i++;
			}
			prev[i]='\0';
			fprintf(tokenOut,"<%s> ",yytext);
			fprintf(logOut,"\nLine No. %d: Token <%s> Lexeme %s found\n",yylineno,yytext,prev);
		}


							/*  constants  */
				/* integer */		
{INTEGER}		{
			fprintf(tokenOut,"<CONST_INT, %s> ",yytext);
			fprintf(logOut,"\nLine No. %d: Token <CONST_INT> Lexeme %s found\n",yylineno,yytext);
			symbolTable.insert(yytext,"CONST_INT");
		}
				/* float */

{FLOAT}	  	{
			fprintf(tokenOut,"<CONST_FLOAT, %s> ",yytext);
			fprintf(logOut,"\nLine No. %d: Token <CONST_FLOAT> Lexeme %s found\n",yylineno,yytext);
			symbolTable.insert(yytext,"CONST_INT");
		}

				/* ill formed number */
{IFN}		{ 
			errorCount++;
			fprintf(logOut,"\nError at line %d: Ill formed number %s\n",yylineno,yytext);
		}		
				/* too many decimal point error */
{FLOAT}+	{
			errorCount++;
			fprintf(logOut,"\nError at line %d: Too many decimal point %s\n",yylineno,yytext);
		}
				/* character */
{UNRECCHAR}	{
			errorCount++;
			fprintf(logOut,"\nError at line %d: Unrecognised character %s\n",yylineno,yytext);
		}
{CHAR} 		{
			char out;
			if(yytext[1]=='\\')
			{
				switch(yytext[2])
				{
					case 'n':
						out='\n';
						break;
					case 't':
						out='\t';
						break;
					case '\\':
						out='\\';
						break;
					case '\'':
						out='\'';
						break;
					case '\"':
						out='\"';
						break;
					case 'a':
						out='\a';
						break;
					case 'f':
						out='\f';
						break;
					case 'r':
						out='\r';
						break;
					case 'b':
						out='\b';
						break;
					case 'v':
						out='\v';
						break;
					default:
						out='0';
				}	
			}
			else if(yytext[1]=='\"')
			{
				out='\"';
			}
			else 
			{
				out=yytext[1];
			}
			fprintf(tokenOut,"<CONST_CHAR, %c> ",out);
			fprintf(logOut,"\nLine No. %d: Token <CONST_CHAR> Lexeme %s found\n",yylineno,yytext);
			symbolTable.insert(yytext,"CONST_CHAR");
		}
			/* empty character */
\'\'		{
			errorCount++;
			fprintf(logOut,"\nError at line %d: Empty character constant error %s\n",yylineno,yytext);	
		}
			/* multi character error */
{MULTICHAR}	{
			errorCount++;
			fprintf(logOut,"\nError at line %d: Multi character constant error %s\n",yylineno,yytext);	
		}
			/* unfinished char */
{UNFCHAR}	{
			errorCount++;
			fprintf(logOut,"\nError at line %d: Unterminated character %s\n",yylineno,yytext);
		}

		
			/* Operators and Punctuators */
{ADDOP}       	{
			fprintf(tokenOut,"<ADDOP, %s> ",yytext);
			fprintf(logOut,"\nLine No. %d: Token <ADDOP> Lexeme %s found\n",yylineno,yytext);
		}
{MULOP}     	{
			fprintf(tokenOut,"<MULOP, %s> ",yytext);
			fprintf(logOut,"\nLine No. %d: Token <MULOP> Lexeme %s found\n",yylineno,yytext);
		}
{INCOP}		{
			fprintf(tokenOut,"<INCOP, %s> ",yytext);
			fprintf(logOut,"\nLine No. %d: Token <INCOP> Lexeme %s found\n",yylineno,yytext);
		}
{RELOP}		{
			fprintf(tokenOut,"<RELOP, %s> ",yytext);
			fprintf(logOut,"\nLine No. %d: Token <RELOP> Lexeme %s found\n",yylineno,yytext);
		}
\=		{
			fprintf(tokenOut,"<ASSIGNOP, %s> ",yytext);
			fprintf(logOut,"\nLine No. %d: Token <ASSIGNOP> Lexeme %s found\n",yylineno,yytext);
		}
\&\&|\|\|	{
			fprintf(tokenOut,"<LOGICOP, %s> ",yytext);
			fprintf(logOut,"\nLine No. %d: Token <LOGICOP> Lexeme %s found\n",yylineno,yytext);
		}
{BITOP}		{
			fprintf(tokenOut,"<BITOP, %s> ",yytext);
			fprintf(logOut,"\nLine No. %d: Token <BITOP> Lexeme %s found\n",yylineno,yytext);
		}
\!		{
			fprintf(tokenOut,"<NOT, %s> ",yytext);
			fprintf(logOut,"\nLine No. %d: Token <NOT> Lexeme %s found\n",yylineno,yytext);
		}
\(		{
			fprintf(tokenOut,"<LPAREN, %s> ",yytext);
			fprintf(logOut,"\nLine No. %d: Token <LPAREN> Lexeme %s found\n",yylineno,yytext);
		}
\)		{
			fprintf(tokenOut,"<RPAREN, %s> ",yytext);
			fprintf(logOut,"\nLine No. %d: Token <RPAREN> Lexeme %s found\n",yylineno,yytext);
		}
\{		{
			fprintf(tokenOut,"<LCURL, %s> ",yytext);
			fprintf(logOut,"\nLine No. %d: Token <LCURL> Lexeme %s found\n",yylineno,yytext);
		}
\}		{
			fprintf(tokenOut,"<RCURL, %s> ",yytext);
			fprintf(logOut,"\nLine No. %d: Token <RCURL> Lexeme %s found\n",yylineno,yytext);
		}
\[		{
			fprintf(tokenOut,"<LTHIRD, %s> ",yytext);
			fprintf(logOut,"\nLine No. %d: Token <LTHIRD> Lexeme %s found\n",yylineno,yytext);
		}
\]		{
			fprintf(tokenOut,"<RTHIRD, %s> ",yytext);
			fprintf(logOut,"\nLine No. %d: Token <RTHIRD> Lexeme %s found\n",yylineno,yytext);
		}
\,		{
			fprintf(tokenOut,"<COMMA, %s> ",yytext);
			fprintf(logOut,"\nLine No. %d: Token <COMMA> Lexeme %s found\n",yylineno,yytext);
		}
\;		{
			fprintf(tokenOut,"<SEMICOLON, %s> ",yytext);
			fprintf(logOut,"\nLine No. %d: Token <SEMICOLON> Lexeme %s found\n",yylineno,yytext);
		}

		
			/* identifier */
{IDENTIFIER}	{
			fprintf(tokenOut,"<ID, %s> ",yytext);
			fprintf(logOut,"\nLine No. %d: Token <ID> Lexeme %s found\n",yylineno,yytext);
			symbolTable.insert(yytext,"ID");
		}	
			/* invalid suffix/prefix */
{ISP}		{
			errorCount++;
			fprintf(logOut,"\nError at line %d: Invalid prefix on ID or invalid suffix on Number %s\n",yylineno,yytext);
		}

			/* string */
{STRING}	{
			char temp[500];
			int i=1,j=0,lineno=yylineno;
			while(yytext[i]!='\0')
			{	
				if(yytext[i]=='\\')
				{
					if(yytext[i+1]=='n')
						temp[j++]='\n';
					else if(yytext[i+1]=='t')
						temp[j++]=9;
					else if(yytext[i+1]=='\\')
						temp[j++]='\\';
					else if(yytext[i+1]=='\'')
						temp[j++]='\'';
					else if(yytext[i+1]=='\"')
						temp[j++]='\"';
					else if(yytext[i+1]=='a')
						temp[j++]='\a';
					else if(yytext[i+1]=='f')
						temp[j++]='\f';
					else if(yytext[i+1]=='r')
						temp[j++]='\r';
					else if(yytext[i+1]=='b')
						temp[j++]='\b';
					else if(yytext[i+1]=='v')
						temp[j++]='\v';
					else if(yytext[i+1]=='\n')
						lineno--;
					else
						temp[j++]='\0';	
					i+=2;
				}
				else
				{
					temp[j++]=yytext[i++];
				}
			}
			temp[--j]='\0';
			fprintf(tokenOut,"<STRING, \"%s\"> ",temp);
			fprintf(logOut,"\nLine No. %d: Token <STRING> Lexeme %s found\n",lineno,yytext);
		}

			/* string error */
{UNTSTRING}	{
			int lineno=yylineno,i=0;
			errorCount++;
			while(yytext[i])
			{
				if(yytext[i++]=='\n')
					lineno--;				
			}
			fprintf(logOut,"\nError at line %d: Unterminated String %s\n",lineno,yytext);
		}


		 	/* comment */
{COMMENT1}	{
			int lineno=yylineno,i=0;
			while(yytext[i])
			{
				if(yytext[i++]=='\n')
					lineno--;
			}
			fprintf(logOut,"\nLine No. %d: Token <COMMENT> Lexeme %s found\n",lineno,yytext);
		}

{COMMENT2} 	{
			int lineno=yylineno,i=0;
			while(yytext[i])
			{
				if(yytext[i++]=='\n')
					lineno--;
			}
			fprintf(logOut,"\nLine No. %d: Token <COMMENT> Lexeme %s found\n",lineno,yytext);
		}
{UNFCOMMENT}	{
			errorCount++;
			int lineno=yylineno,i=0;
			while(yytext[i])
			{
				if(yytext[i++]=='\n')
					lineno--;
			}
			fprintf(logOut,"\nError at line %d: Unterminated comment %s\n",lineno,yytext);
		}


[\n]		{
			
		}


{WHITESPACE}	{
			/* ignore */
		}

.		{
			errorCount++;
			fprintf(logOut,"\nError at line %d: Unrecognised character %s\n",yylineno,yytext);
		}
			
%%


int main(int argc,char *argv[]){
	
	if(argc!=2){
		printf("Please provide input file name and try again\n");
		return 0;
	}
	
	FILE *fin=fopen(argv[1],"r");
	if(fin==NULL){
		printf("Cannot open specified file\n");
		return 0;
	}
	
	logOut= fopen("1505057_log.txt","w");
	tokenOut= fopen("1505057_token.txt","w");

	yyin= fin;
	yylex();
	symbolTable.printCurrentTable();
	fprintf(logOut,"\nTotal Lines: %d\n",yylineno-1);
	fprintf(logOut,"Total Errors: %d\n",errorCount);	
	fclose(yyin);
	fclose(tokenOut);
	fclose(logOut);
	return 0;
}

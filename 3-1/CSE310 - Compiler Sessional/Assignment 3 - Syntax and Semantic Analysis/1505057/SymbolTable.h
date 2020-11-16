#define tableSize 7
#define mset(a, b) memset(a,b,sizeof(a))
extern int yylineno;

class vInfo
{
public:
	string name;
	bool array;
	vInfo()
	{
		name = "";
		array = false;
	}
};

class FunctionInfo
{

public:
	bool funcDef,funcDec;
	vector<string> paramType,paramID;
	string retType;
	
	FunctionInfo()
	{
		funcDef=false;
		funcDec=false;
		retType="";
	}
};



class SymbolInfo
{

    
public:
	string name, type;    
	SymbolInfo *next;
	bool function;
	string vtype;
	FunctionInfo *info;
	vInfo *vinfo;
	int lineno;

    	SymbolInfo()
    	{
        	name = "";
        	type = "";
		vtype= "";
        	next = NULL;
		info = NULL;
		vinfo = NULL;
		function = false;
		lineno = yylineno;
    	}
	

    	SymbolInfo(string name, string type)
    	{
        	this->name = name;
        	this->type = type;
		vtype= "";
        	next = NULL;
		info = NULL;
		vinfo = NULL;
		function = false;
		lineno = yylineno;
    	}
	
	SymbolInfo(string name)
	{
		this->name = name;
		type = "";
		vtype= "";
		next = NULL;
		info = NULL;
		vinfo = NULL;
		function = false;
		lineno = yylineno;
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

	int hashFunction(string key)
	{
    		int hash = 7;
    		REP(i, key.length())
   		{
       			hash = (hash * 31 + key.at(i)) % tableSize;
    		}
    		return hash % tableSize;
	}

    	bool insert(string name, string type)
    	{
        	int hash = hashFunction(name);
        	if (data[hash].head == NULL)
        	{
            		SymbolInfo *newNode = new SymbolInfo(name, type);
            		data[hash].head = newNode;
            		data[hash].tail = newNode;
            		return true;
        	}
        	if (search(name, false) != NULL)
        	{
            		return false;
        	}
        	SymbolInfo *newNode = new SymbolInfo(name, type);
        	data[hash].tail->next = newNode;
        	data[hash].tail = newNode;
        	return true;
    	}

	bool insert(SymbolInfo *node, string type)
    	{
        	int hash = hashFunction(node->name);
        	if (data[hash].head == NULL)
        	{
            		node->type = type;
            		data[hash].head = node;
            		data[hash].tail = node;
            		return true;
        	}
        	if (search(node->name, false) != NULL)
        	{
            		return false;
        	}
        	node->type = type;
        	data[hash].tail->next = node;
        	data[hash].tail = node;
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
        	return true;
    	}

    	SymbolInfo *search(string name, bool pf)
    	{
        	int hash = hashFunction(name);
        	if (data[hash].head == NULL)
        	{
           	     return NULL;
        	}
        	SymbolInfo *cur = data[hash].head;
        	int pos = 0;
        	while (cur != NULL)
        	{
            		if (cur->getName() == name)
            		{
               	        	return cur;
            		}
            		cur = cur->next;
            		pos++;
        	}
        	return NULL;
    	}


    	void printHash(FILE *fp)
    	{
		fprintf(fp,"\n ScopeTable # %d\n",ID);
        	SymbolInfo *cur;
        	REP(i, tableSize)
        	{
	    		cur = data[i].head;
	    		if(cur !=NULL)
			{	
		    		fprintf(fp," %d --> ",i);
		    		while (cur != NULL)
		    		{
					fprintf(fp,"<%s, %s> ",cur->getName().c_str(),cur->getType().c_str());
					//fprintf(fp,"<%s, %s, %s> ",cur->getName().c_str(),cur->getType().c_str(),cur->vtype.c_str());		        
					cur = cur->next;
		    		}
		    		fprintf(fp,"\n");
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
    
    	void enterScope(FILE *fp)
    	{
        	ScopeTable *temp = new ScopeTable(tableSize);
        	temp->parentScope = currentTable;
        	temp->ID = temp->parentScope->ID + 1;
        	currentTable = temp;
		fprintf(fp,"\n	New ScopeTable with id %d created\n",currentTable->ID);
        }

    	void exitScope(FILE *fp)
    	{
        	if (currentTable->ID != 1)
        	{
			fprintf(fp,"\n	ScopeTable with id %d removed\n",currentTable->ID);
            		ScopeTable *temp = currentTable;
            		currentTable = currentTable->parentScope;
           		free(temp);
        	}
        	else
        	{
			printf("x");
            		/*cout << " Can not remove ScopeTable #1" << endl << endl;*/
        	}
    	}

    	bool insert(string name, string type)
    	{
        	return currentTable->insert(name, type);
    	}

	bool insert(SymbolInfo *node, string type)
	{
		return currentTable->insert(node, type);
	}

    	bool remove(string name)
    	{
        	return currentTable->remove(name);
    	}

    	SymbolInfo *search(string name)
    	{
            	SymbolInfo *temp = currentTable->search(name, true);
            	if (temp != NULL)
            	{
                	return temp;
            	}	
        	return NULL;
    	}

	SymbolInfo *searchAll(string name)
    	{
		ScopeTable *cur = currentTable;
        	while (cur != NULL)
        	{
            		SymbolInfo *temp = cur->search(name, true);
            		if (temp != NULL)
            		{
		                return temp;
            		}
            		cur = cur->parentScope;
        	}
        	return NULL;
    	}

    	void printCurrentTable(FILE *fp)
    	{
        	currentTable->printHash(fp);
    	}

    	void printAllTable(FILE *fp)
    	{
        	ScopeTable *cur = currentTable;
        	while (cur != NULL)
        	{
            		cur->printHash(fp);
            		cur = cur->parentScope;
        	}
    	}
};

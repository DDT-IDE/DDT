
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@TPLPARAMS_OPT《●(T, U : T[])》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module Module;

int Var; 
auto VarAuto;

int Var1, Var2, Var3;
int VarAuto1 = int, VarAuto2 = bar;


int Function#@TPLPARAMS_OPT (int Parameter)  { }
int Function#@TPLPARAMS_OPT ()  { }
auto FunctionAuto#@TPLPARAMS_OPT (int Parameter)  { }

class Class #@TPLPARAMS_OPT { static int member1; int member2; }
interface Interface #@TPLPARAMS_OPT { static int member1; int member2; }
struct Struct #@TPLPARAMS_OPT { static int member1; int member2; }
union Union #@TPLPARAMS_OPT { static int member1; int member2; }

enum Enum { member1, member2, }

template Template(TemplateParamType) { }

#REFSEARCH:
Module
Var
VarAuto
Var1

VarAuto1

Function
Function
FunctionAuto

Class
Interface
Struct
Union

Enum
Template

#comment(TODO):
Var2
Var3
VarAuto2

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

template Template(
	TemplateParamType, 
	int TemplateParamValue, 
	alias TemplateParamAlias,
	TemplateParamTuple...,
	this TemplateParamThis
	) 
{
	#REFSEARCH¤【
Template
TemplateParamType 
TemplateParamValue 
TemplateParamAlias
TemplateParamTuple
TemplateParamThis
	】
}

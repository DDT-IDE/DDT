Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module Module;

int Var; 
auto VarAuto = 2;

int Var1, Var2, Var3;
int VarAuto1 = int, VarAuto2 = bar;


int Function (int Param = 3, Foo Param2 ...)  { }
int Function (...)  { }
int Function (T, U : T[]) (Foo Param = { i++; })  { }
auto FunctionAuto (int Parameter)  { }
auto FunctionAuto(T, U : T[]) (int Parameter ...)  { }


class Class (T, U : T[]) { static int member1; int member2; }
interface Interface { static int member1; int member2; }
struct Struct { static int member1; int member2; }
union Union 
(T : int = bar, ambigA = ambigB, int R : 10 = 1, alias A1 : 12 + 2 = foo, alias A2 : int = 2, TUPLE ..., this V)
{ static int member1; int member2; }

enum Enum { member1, member2, }
enum { EnumMemberA, EnumMemberB }

template Template(
	TemplateParamType, 
	int TemplateParamValue, 
	alias TemplateParamAlias,
	TemplateParamTuple...,
	this TemplateParamThis
	) 
{

#REFSEARCH¤【
	
Module

Var
VarAuto
Var1
#comment(TODO)¤【
Var2
Var3
VarAuto2
】

VarAuto1

Function
Function
FunctionAuto

Class▪Interface▪Struct▪Union

Enum
EnumMemberA▪EnumMemberB

Template

TemplateParamType 
TemplateParamValue 
TemplateParamAlias
TemplateParamTuple
TemplateParamThis
】

}
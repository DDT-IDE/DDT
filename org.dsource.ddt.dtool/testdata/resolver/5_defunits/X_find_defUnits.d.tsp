Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module Module; // TODO: rest of defUnits

int Var; 
int Var1, Var2, Var3;
auto VarAuto = 2;
auto VarAuto1 = int, VarAuto2 = bar;


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

alias Alias1 = foo[], Alias2 = bar[1123 + 3]; 
alias foo[] AliasVarDecl; 
alias foo[] AliasFunctionDecl(int[2] a) nothrow;


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

Var▪Var1
VarAuto▪VarAuto1
#comment(TODO)¤【
Var2▪Var3▪VarAuto2
】

Function
Function
FunctionAuto

Class▪Interface▪Struct▪Union

Enum
EnumMemberA▪EnumMemberB

#comment(TODO)¤【Alias1▪Alias2▪】
AliasVarDecl▪AliasFunctionDecl

Template

TemplateParamType 
TemplateParamValue 
TemplateParamAlias
TemplateParamTuple
TemplateParamThis
】

}
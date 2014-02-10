Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module Module; // TODO: rest of defUnits

int Var; 
int Var1, Var2, Var3;
auto VarAuto = 1;
auto VarAuto1 = 2, VarAuto2 = "bar";

enum EnumVar;
enum EnumVar1 = 1, EnumVar2 = "bar";


int Function (int Param, Struct Param2 ...)  { return 0; }
int Function (...)  { return 0; }
void FunctionT (T, U : T[]) (Class Param = null)  { }
auto FunctionAuto (int Parameter)  { }
auto FunctionAutoT(T, U : T[]) (int Parameter ...)  { }


class Class (T, U : T[]) { static int member1; int member2; int method() { return 1;};}
interface Interface { static int member1; int method(); }
struct Struct { static int member1; int member2; }
union Union 
(T : int = bar, int R : 10 = 1, alias ALIAS : int[] = [1, 10], this V, TUPLE ...)
{ static int member1; int member2; }

enum Enum { member1, member2, }
enum { EnumMemberA, EnumMemberB }

alias Alias1 = int[], Alias2 = char[42 + 3]; 
alias char[] AliasVarDecl; 
alias char[] AliasFunctionDecl(int[2] a) nothrow;


template Template(
	TemplateParamType, 
	int TemplateParamValue, 
	alias TemplateParamAlias,
	this TemplateParamThis,
	TemplateParamTuple...
	) 
{

#REFSEARCH¤【
	
Module

Var▪Var1▪Var2▪Var3▪
VarAuto▪VarAuto1▪VarAuto2
EnumVar▪EnumVar1▪EnumVar2

Function
Function
FunctionT
FunctionAuto
FunctionAutoT

Class▪Interface▪Struct▪Union

Enum
EnumMemberA▪EnumMemberB

Alias1▪Alias2
AliasVarDecl▪AliasFunctionDecl

Template

TemplateParamType 
TemplateParamValue 
TemplateParamAlias
TemplateParamTuple
TemplateParamThis
】

}
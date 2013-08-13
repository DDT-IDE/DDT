Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@TPLDUMMYPARAMS_OPT《●(TYPE, TYPE_U : TYPE[])》
#@TPLDUMMYPARAMS_OPT_result《●▪TYPE▪TYPE_U》(TPLDUMMYPARAMS_OPT)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

int Function#@TPLPARAMS_OPT (int Param = 3, Foo Param2...)  { }
int Function#@TPLPARAMS_OPT (int foo, ...)  { }
auto FunctionAuto#@TPLPARAMS_OPT (int Parameter)  { }

#REFSEARCH:
Function▪
Function▪
FunctionAuto▪
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
int Function(int a)  { }
#REFSEARCH¤【Function▪FunctionSyntaxError】
int FunctionSyntaxError#@TPLDUMMYPARAMS_OPT (bar foo) {
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
int Function(int a)  { }
#REFSEARCH¤【Function▪FunctionSyntaxError】
int FunctionSyntaxError#@TPLDUMMYPARAMS_OPT (bar foo)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
int Function(int a)  { }
#REFSEARCH¤【Function▪FunctionSyntaxError 】
int FunctionSyntaxError#@TPLDUMMYPARAMS_OPT (bar foo,
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
int Function(int a)  { }
#REFSEARCH¤【Function▪FunctionSyntaxError】
int FunctionSyntaxError(T, U : T[], alias ALIAS)
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ From inside function

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ TPL parameters
int outside;
int Function 
(T, #REFSEARCH¤【T▪Function▪outside】 U : T[], alias ALIAS #REFSEARCH¤【T▪U▪ALIAS▪Function▪outside】)
(int Param, char, int Param2, Foo Param3...)  { 
	int a;
}
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
int outside;
int FunctionSyntaxError(T, #REFSEARCH¤【T▪FunctionSyntaxError▪outside】 U : T[], alias ALIAS)
#REFSEARCH¤【T▪U▪ALIAS▪FunctionSyntaxError▪outside】

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Function parameters
int outside;
int Function #@TPLDUMMYPARAMS_OPT 
(int Param, #REFSEARCH¤【Param▪Function▪#@TPLDUMMYPARAMS_OPT_result▪outside】 char, int Param2, Foo Param3...)  { 
	int a;
	#REFSEARCH¤【▪a▪Param▪Param2▪Param3▪#@TPLDUMMYPARAMS_OPT_result▪Function▪outside】
}
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
int outside;
int Function #@TPLDUMMYPARAMS_OPT (int Param, char, int Param2, Foo Param3...) 
	#REFSEARCH¤【Param▪Param2▪Param3▪#@TPLDUMMYPARAMS_OPT_result▪Function▪outside】 
{
	#REFSEARCH¤【Param▪Param2▪Param3▪#@TPLDUMMYPARAMS_OPT_result▪Function▪outside】
	int Var;
	#REFSEARCH¤【Var▪Param▪Param2▪Param3▪#@TPLDUMMYPARAMS_OPT_result▪Function▪outside】
}
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
int outside;
int FunctionSyntaxError#@TPLDUMMYPARAMS_OPT (bar foo)
	#REFSEARCH¤【foo▪#@TPLDUMMYPARAMS_OPT_result▪FunctionSyntaxError▪outside】
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
int outside;
int FunctionSyntaxError#@TPLDUMMYPARAMS_OPT 
(bar foo, #REFSEARCH¤【outside▪FunctionSyntaxError▪foo▪#@TPLDUMMYPARAMS_OPT_result】

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ function body 
int outside;
int Function#@TPLDUMMYPARAMS_OPT (bar foo) in {
	int inVar;
	#REFSEARCH¤【inVar  ▪foo▪#@TPLDUMMYPARAMS_OPT_result▪Function▪outside】
}
out(fnResult) { // TODO fnResult in search results
	int outVar;
	#REFSEARCH¤【outVar ▪foo▪#@TPLDUMMYPARAMS_OPT_result▪Function▪outside】
}
body {
	int bodyVar;
	#REFSEARCH¤【bodyVar▪foo▪#@TPLDUMMYPARAMS_OPT_result▪Function▪outside】
}
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
int outside;
int FunctionSyntaxError#@TPLDUMMYPARAMS_OPT (bar foo) in { 
	int Var;
	#REFSEARCH¤【Var▪foo▪#@TPLDUMMYPARAMS_OPT_result▪FunctionSyntaxError▪outside】
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
int outside;
int FunctionSyntaxError#@TPLDUMMYPARAMS_OPT (bar foo) body { 
	int Var;
	#REFSEARCH¤【Var▪foo▪#@TPLDUMMYPARAMS_OPT_result▪FunctionSyntaxError▪outside】
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
int outside;
int FunctionSyntaxError#@TPLDUMMYPARAMS_OPT (bar foo) out { 
	int Var;
	#REFSEARCH¤【Var▪foo▪#@TPLDUMMYPARAMS_OPT_result▪FunctionSyntaxError▪outside】
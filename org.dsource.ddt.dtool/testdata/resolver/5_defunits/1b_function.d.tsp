Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@TPLDUMMYPARAMS_OPT《●(_dummy, _dummy : T[])》
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
int FunctionSyntaxError#@TPLDUMMYPARAMS_OPT (bar foo) {
#REFSEARCH¤【Function▪FunctionSyntaxError】
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
int Function(int a)  { }
int FunctionSyntaxError#@TPLDUMMYPARAMS_OPT (bar foo)
#REFSEARCH¤【Function▪FunctionSyntaxError】
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
int Function(int a)  { }
int FunctionSyntaxError#@TPLDUMMYPARAMS_OPT (bar foo,
#REFSEARCH¤【Function▪FunctionSyntaxError▪foo】
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
int Function(int a)  { }
int FunctionSyntaxError(_dummy, _dummy : T[], alias _dummy)
#REFSEARCH¤【Function▪FunctionSyntaxError】
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Function parameters
int Function(int Param, char, int Param2, Foo Param3...)  { 
	int a;
	#REFSEARCH¤【a▪Param▪Param2▪Param3▪Function】
}
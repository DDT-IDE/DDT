Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

int Var; 
int Var1, Var2, Var3;

auto VarAuto;
auto VarAuto1 = Var, VarAuto2 = bar;


int /*SYNTAX-ERROR*/;
auto /*SYNTAX-ERROR*/;

#REFSEARCH¤【
Var
VarAuto
Var1

VarAuto1
】
#comment(TODO)¤【
Var2
Var3
VarAuto2
】

Var.#REFSEARCH¤【#@INTEGRAL_PROPERTIES】;
Var1.#REFSEARCH¤【#@INTEGRAL_PROPERTIES】;
VarAuto1.#REFSEARCH¤【#@INTEGRAL_PROPERTIES】;

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ TODO
Var2.#REFSEARCH¤【#@INTEGRAL_PROPERTIES】;
VarAuto2.#REFSEARCH¤【#@INTEGRAL_PROPERTIES】;
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
auto VarAuto = void;

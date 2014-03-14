Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
int func1() { }
int func2(T) (int 1) { }
Class func3(T) (int 1) { }

class Class { static int member1; int member2; }

auto _dummy = func1().#REFSEARCH¤【#@INTEGRAL_PROPERTIES】;
auto _dummy = func2().#REFSEARCH¤【#@INTEGRAL_PROPERTIES】;
auto _dummy = func3().#REFSEARCH¤【member1▪member2▪#@CLASS_PROPERTIES】;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
struct OpCallabled {
	int foo;
	
	int opCall() { }
}
OpCallabled var;
int varX;

auto _dummy = OpCallabled().#REFSEARCH¤【#@INTEGRAL_PROPERTIES】;
auto _dummy =         var().#REFSEARCH¤【#@INTEGRAL_PROPERTIES】;
auto _dummy = varX().#REFSEARCH¤【】;
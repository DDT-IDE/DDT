Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ class/interface
#@OBJ_AGGREGATE_KIND《class●interface》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ class properties
#PROJECT(rawProject)

int foo_outside;
 
class Super(TYPE, alias A = int) : SuperX { int superMember; int fooX;} 
class SuperX { int fooX2 }
interface ISuperMissingBody(TYPE, alias A = int) : NonExistant, ISuperX;
interface ISuper  { int fBar; }
interface ISuperX { int fBarX; }


#@OBJ_AGGREGATE_KIND Class : ISuperMissingBody, Super, ISuper {
	int foo;
	int member;
	
	void _dummy(int a, int foo_param) {
		int foo_local;
		f#REFSEARCH¤【foo_local▪foo_param▪foo▪fooX▪fooX2▪fBar▪fBarX▪foo_outside】;
		this.f#REFSEARCH¤【foo▪fooX▪fooX2▪fBar▪fBarX】;
		super.f#REFSEARCH¤【fooX▪fooX2】;
		
		this.#REFSEARCH¤【foo▪member▪superMember▪fooX▪fooX2▪fBar▪fBarX▪classinfo▪#@TYPE_PROPERTIES】;
		super.#REFSEARCH¤【superMember▪fooX▪fooX2▪classinfo▪#@TYPE_PROPERTIES】;
	}
}

auto _dummy = this.#REFSEARCH¤【】;
auto _dummy = super.#REFSEARCH¤【】;

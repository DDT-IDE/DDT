Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

class Foo_ignore { int member; }

void _dummy() {

	foreach(varA ; null) {
		#REFSEARCH¤【varA】;
		foreach(defA, defB, ref defC ; null) {
			#REFSEARCH¤【varA▪defA▪defB▪defC▪】;
		}
		
		foreach(defA, int defB, ref Foo_ignore defC ; null) {
			defB.#REFSEARCH¤【#@INTEGRAL_PROPERTIES】;
			defC.#REFSEARCH¤【member▪#@TYPE_PROPERTIES▪classinfo】;
			defA.#REFSEARCH¤【】; // TODO
		}
	}
	
}
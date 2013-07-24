▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ implicit module name
int mysample; #PROJECT(implicitName@)

#@__CONTEXT_START__
	#REFSEARCH¤【mysample▪implicitName】
#@__CONTEXT_END__
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importTest;

int mysample;

#@__CONTEXT_START__
	#REFSEARCH¤【importTest▪mysample】
	
	importTest#FIND¤【importTest/】;
	importTest.#REFSEARCH¤【mysample】;
	importTest.mysample#FIND¤【importTest/mysample】;
	importTest.importTest#FIND¤【】;
#@__CONTEXT_END__
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
module#@SPACE_Opt; // Syntax errors
int mysample;
#REFSEARCH¤【mysample】
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ // test package defunit
module pack.importTest;

int mysample;

#@__CONTEXT_START__
	#REFSEARCH¤【pack▪mysample】
	
	importTest#FIND¤【】;
	pack#FIND¤【pack/】;
	pack.#REFSEARCH¤【importTest】;
	
	pack.importTest#FIND¤【pack.importTest/】;
	pack.importTest.#REFSEARCH¤【mysample】;
	pack.importTest.mysample#FIND¤【pack.importTest/mysample】;
	pack.importTest.pack.importTest#FIND¤【】;
	pack.importTest.importTest#FIND¤【】;	
#@__CONTEXT_END__
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
module pack.#@SPACE_Opt; // Syntax errors
int mysample;
#REFSEARCH¤【mysample▪pack】
	pack.#REFSEARCH¤【】#FINDMISSING;
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ // test package defunit with subpackages
module pack.subpack.importTest;

int mysample;

#@__CONTEXT_START__
	importTest#FIND¤【】;
	
	pack#FIND¤【pack/】;
	pack.#REFSEARCH¤【subpack】;
	pack.subpack#FIND¤【pack.subpack/】;
	pack.subpack.#REFSEARCH¤【importTest】;
	
	pack.subpack.importTest#FIND¤【pack.subpack.importTest/】;
	pack.subpack.importTest.#REFSEARCH¤【mysample】;
	pack.subpack.importTest.mysample#FIND¤【pack.subpack.importTest/mysample】;
	pack.subpack.importTest.pack.importTest#FIND¤【】;
	pack.subpack.importTest.importTest#FIND¤【】;
	
	#REFSEARCH¤【pack▪mysample】
#@__CONTEXT_END__

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ // Syntax errors
module pack.subpack.#@SPACE_Opt;
int mysample;
#REFSEARCH¤【mysample▪pack】
	pack.subpack.#REFSEARCH¤【】#FINDMISSING;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importTest;

int mysample;

#@__CONTEXT_START__
	importTest#FIND¤【importTest/】;
	importTest.mysample#FIND¤【importTest/mysample】;
	importTest.importTest#FIND¤【】;
	
	#REFSEARCH¤【importTest▪mysample】
#@__CONTEXT_END__

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ // test package defunit
module pack.importTest;

int mysample;

#@__CONTEXT_START__
	importTest#FIND¤【】;
	pack#FIND¤【pack/】;
	pack.#REFSEARCH¤【importTest】;
	
	pack.importTest#FIND¤【pack.importTest/】;
	pack.importTest.mysample#FIND¤【pack.importTest/mysample】;
	pack.importTest.pack.importTest#FIND¤【】;
	pack.importTest.importTest#FIND¤【】;
	
	#REFSEARCH¤【pack▪mysample】
#@__CONTEXT_END__

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ // test package defunit with subpackages
module pack.subpack.importTest;

int mysample;

#@__CONTEXT_START__
	importTest#FIND¤【】;
	pack#FIND¤【pack/】;
	pack.#REFSEARCH¤【subpack】;
	pack.subpack#FIND¤【pack.subpack/】;
	pack.subpack.#REFSEARCH¤【importTest】;
	
	pack.subpack.importTest#FIND¤【importTest/】;
	pack.subpack.importTest.mysample#FIND¤【pack.importTest/mysample】;
	pack.subpack.importTest.pack.importTest#FIND¤【】;
	pack.subpack.importTest.importTest#FIND¤【】;
	
	#REFSEARCH¤【pack▪mysample】
#@__CONTEXT_END__
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importTest; #PROJECT(importsProject)

import importTest#FIND¤【】; // Check that importsProject doesn't have an importTest.d file
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importTest; #PROJECT(importTest@importsProject)

import importTest#FIND¤【importTest/】; // Import to self

int mysample;

#@__CONTEXT_START__

	#REFSEARCH¤【importTest▪mysample】
	importTest.#REFSEARCH¤【mysample】;
	
	importTest#FIND¤【importTest/】;
	importTest.mysample#FIND¤【importTest/mysample】;
	
	importTest.importTest#FIND¤【】;
	
#@__CONTEXT_END__
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module pack.importTest; #PROJECT(pack.importTest@importsProject)

import pack.importTest#FIND¤【pack.importTest/】;

int mysample;

#@__CONTEXT_START__
	#REFSEARCH¤【pack▪mysample】
	pack.importTest.#REFSEARCH¤【mysample】;
	
	pack#FIND¤【pack/】;
	pack.importTest#FIND¤【pack.importTest/】;
	pack.importTest.mysample#FIND¤【pack.importTest/mysample】;
	
	pack.importTest.importTest#FIND¤【】;
	pack.importTest.pack#FIND¤【】;
	pack.importTest.pack.importTest#FIND¤【】;
	
#@__CONTEXT_END__
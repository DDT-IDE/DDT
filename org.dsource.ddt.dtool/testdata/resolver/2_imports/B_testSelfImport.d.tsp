▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module importTest; #PROJECT(importTest@importsProject)

import importTest#FIND¤【importTest/】; // Import to self

int mysample;

#@__CONTEXT_START__
	importTest#FIND¤【testSelfImport/】;
	importTest.mysample#FIND¤【importTest/mysample】;
	importTest.importTest#FIND¤【】;
	
	#REFSEARCH¤【importTest▪mysample】
#@__CONTEXT_END__
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module pack.importTest; #PROJECT(pack.importTest@importsProject)

import pack.importTest#FIND¤【pack.importTest/】;

int mysample;

#@__CONTEXT_START__
	importTest#FIND¤【testSelfImport/】;
	importTest.mysample#FIND¤【importTest/mysample】;
	importTest.importTest#FIND¤【】;
	
	#REFSEARCH¤【importTest▪pack▪mysample】
#@__CONTEXT_END__




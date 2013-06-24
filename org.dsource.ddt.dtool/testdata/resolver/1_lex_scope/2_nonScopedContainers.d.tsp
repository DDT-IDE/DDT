Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@NONSCOPED_CONDITIONALS《version(yes)●debug(tests)●static if(true) 》
#@NONSCOPED_CONTAINERS《#@ATTRIBS__LITE●#@NONSCOPED_CONDITIONALS 》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module tests;
#@REFSEARCH_TEST【#REFSEARCH¤【f1▪f2▪nested▪tests】】

#@ATTRIBS__LITE {
	int f1;
	int f2;
	#@REFSEARCH_TEST
	
	extern(D) {
		int nested;
		#@REFSEARCH_TEST
	}
}
#@REFSEARCH_TEST
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module tests;
#@REFSEARCH_TEST【#REFSEARCH¤【f1▪f2▪nested▪tests】】

#@ATTRIBS__LITE #?ENUM_ATTRIB{#:DISCARD_CASE} :
	int f1;
	int f2;
	#@REFSEARCH_TEST
	
public :
	int nested;
	#@REFSEARCH_TEST

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
module tests; // Semantic engine could improve to prevent then and else blocks to appear toghether
#@REFSEARCH_TEST【#REFSEARCH¤【tests▪f1▪f2▪nested▪elseBlock▪elseBlock2】】

#@NONSCOPED_CONDITIONALS {
	int f1;
	int f2;
	#@REFSEARCH_TEST

public:
	int nested;
	#@REFSEARCH_TEST
} else {
	int elseBlock
	public:
	int elseBlock2;
	#@REFSEARCH_TEST
}
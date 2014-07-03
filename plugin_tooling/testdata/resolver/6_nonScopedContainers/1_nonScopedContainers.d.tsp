▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@NONSCOPED_CONTAINERS {
	int f1; int f2; char duplicate;
	
	extern(D) {
		int nested;
	}
	debug(tests /* SYNTAX ERROR */
}

#@REFSEARCH_TEST【#REFSEARCH¤【f1▪f2▪duplicate▪nested▪f3▪f4】】

#@NONSCOPED_CONTAINERS {
	#@REFSEARCH_TEST  // test ref from within container
	int f3;
	char duplicate;
}

#@NONSCOPED_CONTAINERS int f4;

debug(tests)  /* SYNTAX ERROR */

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ with else case - engine could improve to prevent then and else blocks to both be considered

#@NONSCOPED_CONDITIONALS {
	int f1; int f2; char duplicate;
	
	extern(D) {
		int nested;
	}
	debug(tests /* SYNTAX ERROR */
} else {
	int else_f1;
	
	public:
		int else_nested;
}

#@REFSEARCH_TEST【#REFSEARCH¤【f1▪f2▪duplicate▪nested▪else_f1▪else_nested▪f3▪else_f3▪f4▪else_f4】】

#@NONSCOPED_CONTAINERS {
	int f3;
} else {
	int else_f3;
	char duplicate;
	#@REFSEARCH_TEST  // test ref from within container
}

#@NONSCOPED_CONTAINERS int f4; else int else_f4;


#@NONSCOPED_CONTAINERS  {
} else /* SYNTAX ERROR */

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ the ":" body syntax

#@REFSEARCH_TEST【#REFSEARCH¤【f1▪f2▪duplicate▪nested】】

#@NONSCOPED_CONTAINERS #?ENUM_ATTRIB{#:DISCARD_CASE} :
	int f1; int f2;
	char duplicate;
	
public :
	int nested;
	char duplicate;
	#@REFSEARCH_TEST // test ref from within container
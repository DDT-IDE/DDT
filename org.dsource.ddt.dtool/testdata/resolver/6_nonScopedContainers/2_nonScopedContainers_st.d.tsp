Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Test non scoped containers in statement context (these are different nodes)
#@NONSCOPED_CONTAINERS_ST《#@NONSCOPED_CONDITIONALS》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
void _dummy() {

#@NONSCOPED_CONTAINERS_ST {
	int f1; int f2; char duplicate;
	
	debug(tests) {
		int nested;
	}
	debug(xxx) /* SYNTAX ERROR */
}

#@REFSEARCH_TEST【#REFSEARCH¤【f1▪f2▪duplicate▪nested】】

#@NONSCOPED_CONTAINERS_ST {
	#@REFSEARCH_TEST  // test ref from within container
	int f3;
	char duplicate; 
	#REFSEARCH¤【f1▪f2▪duplicate▪nested▪f3】 // f3 visible now
}
	#REFSEARCH¤【f1▪f2▪duplicate▪nested▪f3】 // f3 visible now

debug(xxx2 /* SYNTAX ERROR */

}
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ with else case - engine could improve to prevent then and else blocks to both be considered
void _dummy() {

#@NONSCOPED_CONTAINERS_ST {
	int f1; int f2; char duplicate;
	
	debug(tests) {
		int nested;
	}
	debug(xxx) /* SYNTAX ERROR */
} else {
	int else_f1;
	
	extern(D):
		int else_nested;
}

#@REFSEARCH_TEST【#REFSEARCH¤【f1▪f2▪duplicate▪nested▪else_f1▪else_nested】】

#@NONSCOPED_CONTAINERS_ST {
	int f3;
} else {
	#REFSEARCH¤【f1▪f2▪duplicate▪nested▪else_f1▪else_nested▪f3】
	int else_f3;
	char duplicate;
}

#@NONSCOPED_CONTAINERS_ST int f4; else int else_f4;

	#REFSEARCH¤【f1▪f2▪duplicate▪nested▪else_f1▪else_nested▪f3▪else_f3▪f4▪else_f4】

#@NONSCOPED_CONTAINERS_ST  {
} else /* SYNTAX ERROR */

}
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ the ":" body syntax is not available in statements 


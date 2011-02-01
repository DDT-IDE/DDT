module foo;

// ==============   DebugSpecification   ==============

debug(foo) { 
	int var;
} 
debug(12) { int var; }
debug = foo;
debug = 666;
debug(foo) { 
} else {
	int var3;
}

void func() {
	debug(foo) 
		char var3;
	else {
		int var3;
	}
}

// ==============   VersionSpecification   ==============

version(foo) { 
	int var2;
}
version(12) int var2;
version = foo;	
version = 666;
version(foo) {
	char var3;
} else {
	int var3;
}


void func() {
	version(foo) {
		char var3;
	} else 
		int var3;
	
}

// ----- Conditional ----
static if(true) { 
	String str;
} else {
	int num;
}

static if(true)  
	String str;
else 
	int num;



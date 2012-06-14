
void func() {

	foreach (int i; a) { 
		writefln("a[%d] = '%c'", i, c); 
	}

	foreach (int i, char c; a) { 
		writefln("a[%d] = '%c'", i, c); 
	}
	
	foreach (int i, char c; a) { 
		writefln("a[%d] = '%c'", i, c); 
	}
	
	foreach (e; range) { 
		//... 
	}
	
	foreach_reverse (e; range) { doStuff(); }
	
}

/+__ INVALID _____________________+/
foreach (a) { 
	doStuff(a); 
}
/+__ INVALID _____________________+/
foreach (int i; a { 
	doStuff(a); 
}
/+__ INVALID _____________________+/
foreach { 
	doStuff(a); 
}
/+__ INVALID _____________________+/
foreach () { 
	doStuff(a); 
}
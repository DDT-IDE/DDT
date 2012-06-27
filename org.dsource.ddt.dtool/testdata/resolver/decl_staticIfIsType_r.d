
/+markerOUT@+/int T;

static if(is(T_asint /+marker1@+/T : int)) {
	int foo; 
	alias /+#find(T)@marker1+/T dummy1;
	// These var,dummy2 should not appear, but semantic engine is not smart enough to figure it out:
	/+#complete(foo,dummy1,T,   var,dummy2)+/
} else {
	alias /+#find(T)@markerOUT+/T dummy2;
	int var; 
}


/+__ INVALID _____________________+/
static if(is(T_asint T == )) {


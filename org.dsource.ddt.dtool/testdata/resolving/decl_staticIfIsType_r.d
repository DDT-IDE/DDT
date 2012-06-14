
/+@markerOUT+/int T;

static if(is(T_asint /+@marker1+/T : int)) {
	int foo; 
	alias /+#find(T)@marker1+/T dummy1;
	/+#completion+/
} else {
	alias /+#find(T)@markerOUT+/T dummy2;
	int var; 
}


/+__ INVALID _____________________+/
static if(is(T_asint T == )) {


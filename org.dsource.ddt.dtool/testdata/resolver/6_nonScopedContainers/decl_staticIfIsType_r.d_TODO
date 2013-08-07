module _module;
/+markerOUT@+/int T;

static if(is(T_asint /+marker1@+/T : int)) {
	int foo; 
	alias /+#find(=)@marker1+/T dummy1;
	// These var,dummy2 should not appear, but semantic engine is not smart enough to figure it out:
	/+#complete(foo,dummy1,T,_module,   var,dummy2)+/
} else {
	alias /+#find(=)@markerOUT+/T dummy2;
	int var; 
}

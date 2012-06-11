
static if(is(T_asint T : int)) {
	int foo;
} else {
	int var;
}

static if(is(T_asint T == OTHERTYPE)) {
	int blah;
}


/+__ INVALID _____________________+/
static if(is(T_asint T == )) {

/+__ INVALID _____________________+/
static if(is()) {
}

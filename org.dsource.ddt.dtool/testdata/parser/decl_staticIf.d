
static if(true) {

} else {
	auto x = 2;
}

static if(2 + 2 == 4) {

}

static if(is(T : int)) {
	auto x = 42;
}

static if(false) int var;
else double var; 

// As statement:

void func() {

	static if(true) {
	
	} else {
		doStuff();
	}
	
	static if(2 + 2 == 4) {
	
	}
	
	static if(is(T : int)) {
		doStuff();
	}
	
	static if(false) doStuff();
	else doElseStuff(); 
}


/+__ INVALID _____________________+/
static if(2 + ) {

}
/+__ INVALID _____________________+/
static if(2 + 2 {

}

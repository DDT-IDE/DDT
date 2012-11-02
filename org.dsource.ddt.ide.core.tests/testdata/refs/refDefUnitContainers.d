/** Test entities inside DefUnit containters **/

int dummy = f1+s2+f2+f3+f4+f5+f6+f7;
                                      
extern(D) {
	int f1;
}
align (4) {
	struct s1 {}  
}
deprecated {
	int f2;
}
public {
	int f3;
} 
const {
	int f4;
}
override {
	int f5;
}
static {
	int f6;
}
auto {
	int f7;
}

void func() {
	scope int f8;
}


/*** CONDITIONALS ***/

version = Yes;
version(Yes) {
	int f3;
}

static if(true) {
	int f4t;
} else  {
	int f4f;
}

/*** OTHER (ANONYMOUS DEFUNITS) ***/
//enum { Member1 }

int dummy = f1 + f2 + f3+f4t +f4f +s1 ;
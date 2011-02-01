/** Test entities inside DefUnit containters **/

int dummy = f1+s1+f2+f3+f4+f6   ;
                                      
extern(D):
	int f1;

align (4):
	struct s1 {}  

deprecated :
	int f2;

public :
	int f3;

const :
	int f4;

static :
	int f6;

int dummy =          f1+s1+f2+f3+f4+f6   ;

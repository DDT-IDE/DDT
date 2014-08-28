 module _dummy;

int abc1;
int abc2;

struct Foo {
	int xx1;
	int xx2;
	int intOther;
	int inzzz;
}
int bar;
	
void _dummy()
{
	auto _dummy = abc/*CC1*/;
	
	Foo.xx/*CC2*/;
	
	Foo /*CC_beforeDot*/ . /*CC_afterDot*/ xx ;
	
	Foo . /*CC_afterDot2*/  ;
}


void _dummy()
{
	char intVar;
	char incredible;

	int/*CC_keywords_1*/;
	in/*CC_keywords_2*/; // Test with keyword as well
	
	Foo.int/*CC_keywords_q1*/;
	Foo.in/*CC_keywords_q2*/;
}


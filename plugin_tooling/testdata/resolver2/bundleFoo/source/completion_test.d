module _dummy;

int abc1;
int abc2;

class Foo {
	int xx1;
	int xx2;
	int other;
}
int bar;
	
void _dummy()
{
	auto _dummy = abc/*CC1*/;
	
	Foo.xx/*CC2*/;
	
	Foo /*CC_beforeDot*/ . /*CC_afterDot*/ xx ;
}
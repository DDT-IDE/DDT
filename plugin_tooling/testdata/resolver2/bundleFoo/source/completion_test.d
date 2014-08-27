module completion_test;

int xx1;
int xx2;
	
void _dummy()
{
	auto _dummy = xx/*CC1*/;
	
	completion_test.xx/*CC2*/;
}
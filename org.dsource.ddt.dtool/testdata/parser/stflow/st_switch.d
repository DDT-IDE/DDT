
void func() {
	switch(true) {
		case 1: { break; }
		case 2: doStuff();
		case "fred":
		case 5: .. case 10: doStuff();
		default:
	}
	switch(123) {
	}
	
	switch(123) {
		doStuff();
		case foo: doStuff();
	}
	
	switch (i) { 
		case 1: { 
			case 2: 
		} 
		break; 
	}
}


/+__ INVALID _________________________+/
void func() {
	switch(true) {
		doStuff();
		default
	}
}
/+__ INVALID _________________________+/
void func() {
	switch(true) {
		label1: break;
		default:
// 
}
/+__ INVALID _________________________+/
void func() {
	switch(;) {
		label2: doStuff();
		default:
	}
}
/+__ INVALID _________________________+/

void func() {
	switch(;) {
		label2: doStuff();
		case 5..: doStuff();
		default:
	}
}

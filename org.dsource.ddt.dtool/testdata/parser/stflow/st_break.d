

void main() {

	while(false) {
		break;
	}
	
	while(false) {
		break label;
	}
	
	label: ;
	
	break;
	
}


/+__ INVALID __+/

break;
    

// Typeid with exp arguments
void func() { 
	if (typeid(this) != typeid(o))
    	return false;


	if (typeid(123) != typeid("asdfsd"))
    	return false;

}

// Function Literal as a template argument
void func() { 
	(a, b){ return (*a)[1] < (*b)[1]; } (1, "asd");
	
	auto x = (a, b){ return (*a)[1] < (*b)[1]; };
	
	tpl1!(1 + 2)(idx, mid);
	
	tpl2!((a, b){ return (*a)[1] < (*b)[1]; })(idx, mid);
	
	//tpl3a!({ return 1; }) v1;
	//tpl3b!((a, b){ return 1; }) v2;
}


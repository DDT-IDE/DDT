
foo 			dummy; // single ref
foo!(12)		dummy; // tpl instance
typeof(foo)		dummy; // typeof
.foo			dummy; // module qualified
xpto.foo		dummy; // qualified ref 2 elements
xpto.foo.bar	dummy; // qualified ref 3 elements
foo[3]			dummy; // tuple ref



// tpl instance
foo!(12)				dummy;
//typeof(foo)!(12)		dummy;
.foo!(12)				dummy;
xpto.foo!(12)			dummy;
xpto.foo.bar!(12)		dummy;
//foo[3]!(12)			dummy; //Syntactically invalid

xpto.foo!(1).bar!(2)		dummy;
.xpto.foo.bar!(12)		dummy;
typeof(.xpto).foo.bar!(12)	dummy;



// typeof
typeof(foo)				dummy;
typeof(foo!(12))		dummy;
typeof(typeof(foo))		dummy;
typeof(.foo)			dummy;
typeof(xpto.foo)		dummy;
typeof(xpto.foo.bar)	dummy;
typeof(foo[3])			dummy;

typeof(typeof(.xpto).foo.bar!(12))			dummy;




// module qualified
.foo				dummy;
.foo!(12)			dummy;
//..foo!(12)		dummy; //Syntactically invalid
.xpto.foo			dummy;
.xpto.foo.bar		dummy;
.foo[3]				dummy;


// qualified ref 2 elements
foo.qref			dummy;
foo!(42).qref		dummy;
.foo!(42).qref		dummy;
typeof(foo).qref	dummy;
.foo.qref			dummy;
xpto.foo.qref		dummy;
xpto.foo.bar.qref	dummy;
//foo[2].qref			dummy; //Syntactically invalid



// tuple ref
foo[3]				dummy;
foo!(42)[3]			dummy;
typeof(foo)[3]		dummy;
.foo[3]				dummy;
xpto.foo[3]			dummy;
xpto.foo.bar[3]		dummy;
foo[3][3]			dummy;

xpto.foo!(2).bar[3]		dummy;
.xpto.foo!(1).bar!(2)[3]			dummy;
typeof(.xpto[3]).foo.bar!(12)[3]	dummy;



void func() {

// Repeat the about in an expression context: 

	foo 			++; 
	foo!(12)		++; 
	typeof(foo)		++; 
	.foo			++; 
	xpto.foo		++; 
	xpto.foo.bar	++; 
	foo[3]			++; 
	
	
	
	// tpl instance
	foo!(12)				++;
	//typeof(foo)!(12)		++;
	.foo!(12)				++;
	xpto.foo!(12)			++;
	xpto.foo.bar!(12)		++;
	//foo[3]!(12)			++; //Syntactically invalid
	
	xpto.foo!(1).bar!(2)		++;
	.xpto.foo.bar!(12)		++;
	typeof(.xpto).foo.bar!(12)	++;
	
	
	
	// typeof
	typeof(foo)				++;
	typeof(foo!(12))		++;
	typeof(typeof(foo))		++;
	typeof(.foo)			++;
	typeof(xpto.foo)		++;
	typeof(xpto.foo.bar)	++;
	typeof(foo[3])			++;
	
	typeof(typeof(.xpto).foo.bar!(12))			++;
	
	
	
	
	// module qualified
	.foo				++;
	.foo!(12)			++;
	//..foo!(12)		++; //Syntactically invalid
	.xpto.foo			++;
	.xpto.foo.bar		++;
	.foo[3]				++;
	
	
	// qualified ref 2 elements
	foo.qref			++;
	foo!(42).qref		++;
	.foo!(42).qref		++;
	typeof(foo).qref	++;
	.foo.qref			++;
	xpto.foo.qref		++;
	xpto.foo.bar.qref	++;
	//foo[2].qref			++; //Syntactically invalid
	
	
	
	// tuple ref
	foo[3]				++;
	foo!(42)[3]			++;
	typeof(foo)[3]		++;
	.foo[3]				++;
	xpto.foo[3]			++;
	xpto.foo.bar[3]		++;
	foo[3][3]			++;
	
	xpto.foo!(2).bar[3]		++;
	.xpto.foo!(1).bar!(2)[3]			++;
	typeof(.xpto[3]).foo.bar!(12)[3]	++;
}

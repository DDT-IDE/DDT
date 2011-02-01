module testCodeCompletion3;

import pack.sample : 
	/+CC1@+/SampleClass, 
	SampleClassAlias = /+CC2@+/SampleClassB;

template Tpl(T) {
}

void func(Tuple!(1, 2) a) {
	Tpl  ! (int);
	foo2  . sdf;
	
	alias Tuple!(1, 2) tuple;
	tuple[0].as;
}


import /+CC3@+/pack.mod3; // Test complete here, several offsets

import /+CC4@+/pack.; // Test complete here, syntax-recover

/// Test code completion import content and import selection contexts 
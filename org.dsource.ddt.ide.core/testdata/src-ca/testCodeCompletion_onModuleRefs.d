module testCodeCompletion_onModuleRefs;

import pack.sample : 
	/+CC1@+/SampleClass, 
	SampleClassAlias = /+CC2@+/SampleClassB;

template Tpl(T) {
}

import /+CC3@+/pack.mod3; // Test complete here, several offsets

import /+CC3x@+/pack .  mod3; // Like CC3 but with spaces

import /+CC4@+/pack.; // Test complete here, syntax-recover

/// Test code completion import content and import selection contexts 
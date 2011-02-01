/*** Selective import test                                             ***/

//port pack.mod2;
                                                                    
import   pack.mod2,   pack.sample : 
 	SampleClass, SampleClassAlias = SampleClassB, 
 	sampleVar, sampleVarAlias = sampleVarB;
import mod1alias = pack.mod1;

// XXX: The following is not suppported (aliasing and selective) :
//  import samplealias = pack.sample : SampleClassB, sampleVarB;

alias pack.sample modref; // fail, no FQN with selective

SampleClass sampleclass;
SampleClassAlias sampleclass2;
SampleClassB sampleclass3; // fail

void func() {
	SampleClass.foo++; 
	SampleClassAlias.foo++;
	SampleClassB.foo++; // fail

	foopublicImportVar++; // fail
	fooprivateImportVar++; // fail

	pack2.foopublic.foopublicImportVar++; // fail
	pack2.fooprivate.fooprivateImportVar++; // fail
	
	sampleVarB++; 
}
int sampleVarB;
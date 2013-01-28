Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 


#@SP_TYPE_REF!《
	int●
	Foo●
	.Foo●
	Bar.foo●
	Bar.foo.Foobar●
	int*●
	arrayElem[]●
	
	.Bar.foo[]*
》

#@SPSE_TYPE_REF!《
	RefPrimitive●
	RefIdentifier●
	RefModuleQualified(?)●
	RefQualified(RefIdentifier RefIdentifier)●
	RefQualified(RefQualified(RefIdentifier RefIdentifier) RefIdentifier)●
	RefTypePointer(RefPrimitive)●
	RefTypeDynArray(RefIdentifier)●
	
	RefTypePointer(RefTypeDynArray(RefQualified(RefModuleQualified(RefIdentifier) RefIdentifier)))
》
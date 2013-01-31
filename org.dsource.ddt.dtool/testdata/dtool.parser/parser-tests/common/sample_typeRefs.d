Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 


#@SP_TYPE_REF!《►
	int●
	Foo●
	.Foo●
	Bar.foo●
	Bar.foo.Foobar●
	
	int*●
	arrayElem[]●
	arrayElem[123]● //TODO Expressions
	arrayElem[int]●
	arrayElem[Boo.foo]●
	
	Bar.foo[.x[12]][Bar.foo*]●
	.Bar.foo[]*[123][][1]●
》


#@SPSE_TYPE_REF!《►
	RefPrimitive●
	RefIdentifier●
	RefModuleQualified(?)●
	RefQualified(RefIdentifier RefIdentifier)●
	RefQualified(RefQualified(RefIdentifier RefIdentifier) RefIdentifier)●
	
	RefTypePointer(RefPrimitive)●
	RefTypeDynArray(RefIdentifier)●
	RefIndexing(RefIdentifier ExpLiteralInteger)●
	RefIndexing(RefIdentifier RefPrimitive)●
	RefIndexing(RefIdentifier RefQualified(RefIdentifier RefIdentifier))●
	
	RefIndexing(
	 	RefIndexing(RefQualified(RefIdentifier RefIdentifier) RefIndexing(RefModuleQualified(?) ExpLiteralInteger)) 
		RefTypePointer(RefQualified(RefIdentifier RefIdentifier))
	)●
	RefIndexing(RefTypeDynArray(RefIndexing(
			RefTypePointer(RefTypeDynArray(RefQualified(RefModuleQualified(?) RefIdentifier)))
			ExpLiteralInteger
		))
		ExpLiteralInteger
	)●
》

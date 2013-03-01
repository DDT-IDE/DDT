Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

// These type refs can always be used as qualifiers
#@TYPE_REFS_QUALIFIER《
  ►#?AST_STRUCTURE_EXPECTED!【int●RefPrimitive】●
  ►#?AST_STRUCTURE_EXPECTED!【Foo●RefIdentifier】●
  ►#?AST_STRUCTURE_EXPECTED!【.Foo●RefModuleQualified(?)】●
  ►#?AST_STRUCTURE_EXPECTED!【Bar.foo●RefQualified(RefIdentifier RefIdentifier)】●
  ►#?AST_STRUCTURE_EXPECTED!【Bar.foo.Foobar●RefQualified(RefQualified(RefIdentifier RefIdentifier) RefIdentifier)】●
  
  ►#?AST_STRUCTURE_EXPECTED!【Bar.foo!(foo = 123, xpto, foo[foo* #error(EXP_CLOSE_BRACKET) )●
RefTemplateInstance(
  RefQualified(RefIdentifier RefIdentifier)
  ExpInfix(#@ExpIdentifier Integer)
  RefIdentifier
  RefIndexing(RefIdentifier RefTypePointer(RefIdentifier))
)】●
¤》

// These type refs can be used as qualifiers wihtout disrupting parser flow, but are not valid qualifier
#@TYPE_REFS_INVALID_QUALIFIER《
  ►#?AST_STRUCTURE_EXPECTED!【int[]●RefTypeDynArray(RefPrimitive)】●
  ►#?AST_STRUCTURE_EXPECTED!【arrayElem[]●RefTypeDynArray(RefIdentifier)】●
  ►#?AST_STRUCTURE_EXPECTED!【arrayElem[foo*]●RefIndexing(RefIdentifier RefTypePointer(RefIdentifier))】●
  ►#?AST_STRUCTURE_EXPECTED!【arrayElem[int]●RefIndexing(RefIdentifier RefPrimitive)】●
  ►#?AST_STRUCTURE_EXPECTED!【arrayElem[foo**bar]●RefIndexing(RefIdentifier ExpInfix(* ExpPrefix(ExpReference(?))) )】●
  ►#?AST_STRUCTURE_EXPECTED!【arrayElem[Boo.foo]●RefIndexing(RefIdentifier RefQualified(RefIdentifier RefIdentifier))】●
¤》

#@TYPE_REFS__TPL_SINGLE_ARG《
  ►#?AST_STRUCTURE_EXPECTED!【bar. foo!this●RefTemplateInstance(RefQualified(RefIdentifier ?) ExpThis)】●
  ►#?AST_STRUCTURE_EXPECTED!【.bar.foo! arg●RefTemplateInstance(RefQualified(RefModuleQualified(?) ?) RefIdentifier)】●
¤》
#@TYPE_REFS__CLEAN《
  ►#@TYPE_REFS_QUALIFIER●
  ►#@TYPE_REFS_INVALID_QUALIFIER●
  
  ►#?AST_STRUCTURE_EXPECTED!【int*●RefTypePointer(RefPrimitive)】●

  ►#?AST_STRUCTURE_EXPECTED!【.Bar.foo[]*[123][][1]●
RefIndexing(
  RefTypeDynArray(RefIndexing(
	RefTypePointer(RefTypeDynArray(RefQualified(RefModuleQualified(?) RefIdentifier)))
	ExpLiteralInteger))
  ExpLiteralInteger
)】●

  ►#@TYPE_REFS__LITE●
¤》

#@TYPE_REFS《#@TYPE_REFS__CLEAN●#@TYPE_REFS__TPL_SINGLE_ARG》
#@TYPE_REFS__LITE《
  ►#?AST_STRUCTURE_EXPECTED!【Bar.foo[.x[12]][Bar.foo*]#NOT_QUAL(flag)●
RefIndexing(
 	RefIndexing(RefQualified(RefIdentifier RefIdentifier) RefIndexing(RefModuleQualified(?) Integer)) 
	RefTypePointer(RefQualified(RefIdentifier RefIdentifier))
)】●
¤》


TODO

 	《const●immutable●shared●inout》( )●
 	
//---------------------------------------------------------

// Simple prefix:
#@PREFIX_S《
  ►#?AST_STRUCTURE_EXPECTED!【dai ● RefIdentifier】● 
  ►#?AST_STRUCTURE_EXPECTED!【dai* ● RefTypePointer(RefIdentifier)】 ●
  ►#?AST_STRUCTURE_EXPECTED!【dai[] ● RefTypeDynArray(RefIdentifier) 】●
¤》

// These references unambiguously parse as ref
#@REFS_UNAMBIG《
  ►#?AST_STRUCTURE_EXPECTED!【int●RefPrimitive】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo*/*REF*/]● RefIndexing(#@PREFIX_S RefTypePointer(RefIdentifier))】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[int/*REF*/]● RefIndexing(#@PREFIX_S RefPrimitive)】●

  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo*[int]/*REF*/]●RefIndexing(#@PREFIX_S RefIndexing(* *))】●
¤》

// These source snippets are ambiguous as to whether to parse as ref or exp (they can parse as both)
#@AMBIG_AS_REF《
  ►#?AST_STRUCTURE_EXPECTED!【dai ● RefIdentifier】● 
  ►#?AST_STRUCTURE_EXPECTED!【dai* #INFIX(flag)● RefTypePointer(RefIdentifier)】 ●
  ►#?AST_STRUCTURE_EXPECTED!【dai[] ● RefTypeDynArray(RefIdentifier) 】●
  
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[7 /*EXP*/]● RefIndexing(#@PREFIX_S Integer) 】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo /*AMBIG*/]● RefIndexing(#@PREFIX_S RefIdentifier)】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo*[]/*AMBIG*/]● RefIndexing(#@PREFIX_S 
    RefTypeDynArray(RefTypePointer(RefIdentifier))) 】●

  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo[1, 2]/*EXP*/]●RefIndexing(#@PREFIX_S ExpIndex(* Integer Integer))】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo*[1, 2]/*EXP*/]●RefIndexing(#@PREFIX_S ExpInfix(* ?(Integer Integer)))】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo*[2 : #@NO_EXP]/*EXP*/]●RefIndexing(#@PREFIX_S ExpInfix(* *))】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo[bar]/*AMBIG*/]●RefIndexing(#@PREFIX_S RefIndexing(* *))】●
¤》

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

#@REF_DISCARD_CASE《#?AST_STRUCTURE_EXPECTED!【float●RefPrimitive】》

// These type refs can always be used as qualifiers
#@TYPE_REFS__QUALIFIER_ANY《
  ►#?AST_STRUCTURE_EXPECTED!【Foo #TYPE_REF_CAN_PARSE_AS_ID(flag) ●RefIdentifier】●
  ►#?AST_STRUCTURE_EXPECTED!【.Foo●RefModuleQualified(?)】●
  ►#?AST_STRUCTURE_EXPECTED!【Bar.foo●RefQualified(RefIdentifier RefIdentifier)】●
  ►#?AST_STRUCTURE_EXPECTED!【Bar.foo.Foobar●RefQualified(RefQualified(RefIdentifier RefIdentifier) RefIdentifier)】●
  
  ►#?AST_STRUCTURE_EXPECTED!【typeof(Bar.foo).foo●RefQualified(RefTypeof(?(RefQualified(? ?))) RefIdentifier)】●
  ►#?AST_STRUCTURE_EXPECTED!【typeof(Bar.foo).tpl!(Bar)●RefTemplateInstance(RefQualified(RefTypeof(*) ?) ?)】●
  
  ►#?AST_STRUCTURE_EXPECTED!【Bar.foo!(foo = 123, xpto, foo[foo* #error(EXP_CLOSE_BRACKET) )●
RefTemplateInstance(
  RefQualified(RefIdentifier RefIdentifier)
  ExpInfix(#@ExpIdentifier Integer)
  RefIdentifier
  RefIndexing(RefIdentifier RefTypePointer(RefIdentifier))
)】●
¤》

#@TYPE_REFS__QUALIFIER_DOT_ONLY《
  ►#?AST_STRUCTURE_EXPECTED!【int●RefPrimitive】●
  ►#?AST_STRUCTURE_EXPECTED!【const(foo)●RefTypeModifier(RefIdentifier)】●
  ►#?AST_STRUCTURE_EXPECTED!【typeof(Bar.foo)●RefTypeof(ExpReference(RefQualified(RefIdentifier RefIdentifier)))】●
¤》

#@TYPE_REFS__TPL_SINGLE_ARG《
  ►#?AST_STRUCTURE_EXPECTED!【bar. foo!this●RefTemplateInstance(RefQualified(RefIdentifier ?) ExpThis)】●
  ►#?AST_STRUCTURE_EXPECTED!【.bar.foo! arg●RefTemplateInstance(RefQualified(RefModuleQualified(?) ?) RefIdentifier)】●
¤》

#@TYPE_REFS__QUALIFIER《#@TYPE_REFS__QUALIFIER_ANY●#@TYPE_REFS__QUALIFIER_DOT_ONLY》

// ----------------------------------------------------

// Simple prefix helper:
#@_PREFIX_S_UNARY《
  ►#?AST_STRUCTURE_EXPECTED!【dai ● RefIdentifier】● 
  ►#?AST_STRUCTURE_EXPECTED!【dai[] ● RefTypeDynArray(RefIdentifier) 】●
¤》
#@_PREFIX_S_ALL《
  ►#@_PREFIX_S_UNARY● 
  ►#?AST_STRUCTURE_EXPECTED!【dai* ● RefTypePointer(RefIdentifier)】●
¤》
#@DEFINE_PREFIX_S_UNARY《#@PREFIX_S!【#@_PREFIX_S_UNARY】》

// These source snippets are ambiguous as to whether to parse as ref or exp (they can parse as both)
=====> #@TYPEREFS_AMBIG《
  ►#@TYPE_REFS__QUALIFIER_ANY●
  ►#@TYPE_REFS__TPL_SINGLE_ARG●
  ►#@TYPEREFS_AMBIG__BASE●
  
  ►#?AST_STRUCTURE_EXPECTED!【dai* #INFIX(flag)● RefTypePointer(RefIdentifier)】●
  ►#?AST_STRUCTURE_EXPECTED!【dai *[]●RefTypeDynArray(RefTypePointer(RefIdentifier))】●
  ►#?AST_STRUCTURE_EXPECTED!【dai*[foo][bar]             ●RefIndexing(RefIndexing(RefTypePointer(?) ?) ?)】●
  ►#?AST_STRUCTURE_EXPECTED!【dai*[foo][bar]*#INFIX(flag)●RefTypePointer(RefIndexing(RefIndexing(RefTypePointer(?) ?) ?))】●
  ►#?AST_STRUCTURE_EXPECTED!【dai**[foo][bar]               ●RefIndexing(RefIndexing(RefTypePointer(RefTypePointer(?)) ?) ?)】●
  ►#?AST_STRUCTURE_EXPECTED!【dai**[foo][bar] * #INFIX(flag)●RefTypePointer(RefIndexing(RefIndexing(RefTypePointer(RefTypePointer(?)) ?) ?))】●
  ►#?AST_STRUCTURE_EXPECTED!【dai*** [bar]●RefIndexing(RefTypePointer(RefTypePointer(RefTypePointer(?))) ?)】●
  ►#?AST_STRUCTURE_EXPECTED!【dai*** #INFIX(flag)●RefTypePointer(RefTypePointer(RefTypePointer(?)))】●
¤》

#@TYPEREFS_AMBIG__CUSTOM_PREFIX《
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[]● RefTypeDynArray(#@PREFIX_S) 】●
  
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[7 /*EXP*/]● RefIndexing(#@PREFIX_S Integer) 】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo /*AMBIG*/]● RefIndexing(#@PREFIX_S RefIdentifier)】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo /*AMBIG*/][]● RefTypeDynArray(RefIndexing(#@PREFIX_S RefIdentifier))】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo*[]/*AMBIG*/]● RefIndexing(#@PREFIX_S 
    RefTypeDynArray(RefTypePointer(RefIdentifier))) 】●

  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo[1, 2]/*EXP*/]●RefIndexing(#@PREFIX_S ExpIndex(* Integer Integer))】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo*[1, 2]/*EXP*/]●RefIndexing(#@PREFIX_S ExpInfix(* ?(Integer Integer)))】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo*[2 : #@NO_EXP]/*EXP*/]●RefIndexing(#@PREFIX_S ExpInfix(* *))】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo[bar]/*AMBIG*/]●RefIndexing(#@PREFIX_S RefIndexing(* *))】●
¤》
#@TYPEREFS_AMBIG__BASE《#@PREFIX_S!《#@_PREFIX_S_ALL》#@TYPEREFS_AMBIG__CUSTOM_PREFIX》
#@TYPEREFS_AMBIG__INVALID_QUALIFIER《#@DEFINE_PREFIX_S_UNARY#@TYPEREFS_AMBIG__CUSTOM_PREFIX》


// These references unambiguously parse as ref
=====> #@TYPEREFS_UNAMBIG《
  ►#@TYPE_REFS__QUALIFIER_DOT_ONLY●
  
  ►#@TYPEREFS_UNAMBIG__BASE●
  ►#?AST_STRUCTURE_EXPECTED!【int *[]●RefTypeDynArray(RefTypePointer(RefPrimitive))】●
  ►#?AST_STRUCTURE_EXPECTED!【int *[foo]●RefIndexing(RefTypePointer(RefPrimitive) RefIdentifier)】●
  ►#?AST_STRUCTURE_EXPECTED!【int * *[foo]●RefIndexing(RefTypePointer(RefTypePointer(RefPrimitive)) RefIdentifier)】●
  ►#?AST_STRUCTURE_EXPECTED!【int *[foo][bar]●RefIndexing(RefIndexing(RefTypePointer(RefPrimitive) ?) RefIdentifier)】●

   ►#?TYPE__NO_PENDING_MISSING【#@REF_DISCARD_CASE●
  ►#?AST_STRUCTURE_EXPECTED!【int *●RefTypePointer(RefPrimitive)】】●
   ►#?TYPE__NO_PENDING_MISSING【#@REF_DISCARD_CASE●
  ►#?AST_STRUCTURE_EXPECTED!【int * *●RefTypePointer(RefTypePointer(RefPrimitive))】】●
   ►#?TYPE__NO_PENDING_MISSING【#@REF_DISCARD_CASE●
  ►#?AST_STRUCTURE_EXPECTED!【int * **●RefTypePointer(RefTypePointer(RefTypePointer(RefPrimitive)))】】●
¤》
#@TYPEREFS_UNAMBIG__NO_PENDING《#TYPE__NO_PENDING_MISSING(flag)#@TYPEREFS_UNAMBIG》

#@TYPEREFS_UNAMBIG__CUSTOM_PREFIX《
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo*/+AMBIG_TO_REF+/]● RefIndexing(#@PREFIX_S RefTypePointer(RefIdentifier))】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[int /+REF+/]● RefIndexing(#@PREFIX_S RefPrimitive)】●
  ►#?AST_STRUCTURE_EXPECTED!【#@PREFIX_S[foo*[int]/+REF+/]●RefIndexing(#@PREFIX_S RefIndexing(* *))】●
¤》
#@TYPEREFS_UNAMBIG__BASE《#@PREFIX_S!《#@_PREFIX_S_ALL》#@TYPEREFS_UNAMBIG__CUSTOM_PREFIX》
#@TYPEREFS_UNAMBIG__INVALID_QUALIFIER《#@DEFINE_PREFIX_S_UNARY#@TYPEREFS_UNAMBIG__CUSTOM_PREFIX》

//---------------------------------------------------------
// These refs cannot be used in type refs as qualifiers by neither qualified or template instance refs
#@TYPE_REFS__INVALID_QUALIFIER《
  ►#@TYPEREFS_AMBIG__INVALID_QUALIFIER●
  ►#@TYPEREFS_UNAMBIG__INVALID_QUALIFIER●
¤》

---------------------------------------------------------

We don't use TYPEREFS_AMBIG__INVALID_QUALIFIER or TYPEREFS_UNAMBIG__INVALID_QUALIFIER in TYPE_REFS
for performance reasons, to keep the number of cases low.

#@TYPE_REFS__NO_BROKEN《
  ►#@TYPE_REFS__QUALIFIER_ANY●
  ►#@TYPE_REFS__TPL_SINGLE_ARG●
  ►#@TYPE_REFS__QUALIFIER_DOT_ONLY●
  
  ►#?AST_STRUCTURE_EXPECTED!【int*●RefTypePointer(RefPrimitive)】●
  ►#?AST_STRUCTURE_EXPECTED!【int[]●RefTypeDynArray(RefPrimitive)】●
  ►#?AST_STRUCTURE_EXPECTED!【int*[]●RefTypeDynArray(RefTypePointer(RefPrimitive))】●
  ►#?AST_STRUCTURE_EXPECTED!【foo*●RefTypePointer(RefIdentifier)】●
  ►#?AST_STRUCTURE_EXPECTED!【foo*[]●RefTypeDynArray(RefTypePointer(RefIdentifier))】●
  ►#?AST_STRUCTURE_EXPECTED!【arrayElem[]●RefTypeDynArray(RefIdentifier)】●
  ►#?AST_STRUCTURE_EXPECTED!【arrayElem[foo*]●RefIndexing(RefIdentifier RefTypePointer(RefIdentifier))】●
  ►#?AST_STRUCTURE_EXPECTED!【arrayElem[int]●RefIndexing(RefIdentifier RefPrimitive)】●
  ►#?AST_STRUCTURE_EXPECTED!【arrayElem[foo**bar]●RefIndexing(RefIdentifier ExpInfix(* ExpPrefix(ExpReference(?))) )】●

  ►#?AST_STRUCTURE_EXPECTED!【dai[foo] *[foo][bar]●RefIndexing(RefIndexing(RefTypePointer(*) ?) RefIdentifier)】●  
  ►#?AST_STRUCTURE_EXPECTED!【dai[bar*] *[foo][bar]●RefIndexing(RefIndexing(RefTypePointer(*) ?) RefIdentifier)】●
  ►#?AST_STRUCTURE_EXPECTED!【dai[bar] ***●RefTypePointer(RefTypePointer(RefTypePointer(RefIndexing(? ?))))】●
  
  ►#?AST_STRUCTURE_EXPECTED!【foo**[bar* /++/] *●
   RefTypePointer(RefIndexing(RefTypePointer(RefTypePointer(RefIdentifier)) RefTypePointer(RefIdentifier) ))】●
¤》
#@TYPE_REFS__NO_BROKEN__LITE《
  ►#?AST_STRUCTURE_EXPECTED!【.Bar.foo●RefQualified(RefModuleQualified(?) RefIdentifier)】●
¤》

#@TYPE_REFS__BROKEN《
   ►#?NO_BROKEN_BRACKETS【#@REF_DISCARD_CASE●
  ►#?AST_STRUCTURE_EXPECTED!【arrayElem[foo #error(EXP_CLOSE_BRACKET)●RefIndexing(RefIdentifier RefIdentifier)】】●

   ►#?NO_BROKEN_PARENS【#@REF_DISCARD_CASE●
  ►#?AST_STRUCTURE_EXPECTED!【typeof("abc" #error(EXP_CLOSE_PARENS)●RefTypeof(String)】】●
¤》

#@TYPE_REFS《
  ►#@TYPE_REFS__NO_BROKEN●
  ►#@TYPE_REFS__BROKEN●
¤》
#@TYPE_REFS__LITE《
  ►#@TYPE_REFS__NO_BROKEN__LITE●
  ►#@TYPE_REFS__BROKEN●
¤》

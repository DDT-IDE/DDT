Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

#@TYPE_REFS__REFID《#?STRUCTURE_EXPECTED!【Foo #TYPE_REF_CAN_PARSE_AS_ID(flag) ●RefIdentifier】》

// These type refs can always be used as qualifiers
#@TYPE_REFS__QUALIFIER_ANY《
  ►#@TYPE_REFS__REFID●
  ►#?STRUCTURE_EXPECTED!【.Foo●RefModuleQualified(?)】●
  ►#?STRUCTURE_EXPECTED!【Bar.foo●RefQualified(RefIdentifier RefIdentifier)】●
  ►#?STRUCTURE_EXPECTED!【Bar.foo.Foobar●RefQualified(RefQualified(RefIdentifier RefIdentifier) RefIdentifier)】●
  
  ►#?STRUCTURE_EXPECTED!【typeof(Bar.foo).foo●RefQualified(RefTypeof(?(RefQualified(? ?))) RefIdentifier)】●
  ►#?STRUCTURE_EXPECTED!【typeof(Bar.foo).tpl!(Bar)●RefTemplateInstance(RefQualified(RefTypeof(*) ?) ?)】●
  
  ►#?STRUCTURE_EXPECTED!【Bar.foo!(foo = 123, xpto, foo[foo* #error(EXP_CLOSE_BRACKET) )●
RefTemplateInstance(
  RefQualified(RefIdentifier RefIdentifier)
  ExpInfix(#@ExpIdentifier Integer)
  RefIdentifier
  RefIndexing(RefIdentifier RefTypePointer(RefIdentifier))
)】●
¤》

#@TYPE_REFS__QUALIFIER_DOT_ONLY《
  ►#?STRUCTURE_EXPECTED!【int●RefPrimitive】●
  ►#?STRUCTURE_EXPECTED!【const(foo)●RefTypeModifier(RefIdentifier)】●
  ►#?STRUCTURE_EXPECTED!【typeof(Bar.foo)●RefTypeof(ExpReference(RefQualified(RefIdentifier RefIdentifier)))】●
¤》

#@TYPE_REFS__TPL_SINGLE_ARG《
  ►#?STRUCTURE_EXPECTED!【bar. foo!this●RefTemplateInstance(RefQualified(RefIdentifier ?) ExpThis)】●
  ►#?STRUCTURE_EXPECTED!【.bar.foo! arg●RefTemplateInstance(RefQualified(RefModuleQualified(?) ?) RefIdentifier)】●
¤》

#@TYPE_REFS__QUALIFIER《#@TYPE_REFS__QUALIFIER_ANY●#@TYPE_REFS__QUALIFIER_DOT_ONLY》

// ----------------------------------------------------

// Simple prefix helper:
#@_PREFIX_S_UNARY《
  ►#?STRUCTURE_EXPECTED!【dai ● RefIdentifier】● 
  ►#?STRUCTURE_EXPECTED!【dai[] ● RefTypeDynArray(RefIdentifier) 】●
¤》
#@_PREFIX_S_ALL《
  ►#@_PREFIX_S_UNARY● 
  ►#?STRUCTURE_EXPECTED!【dai* ● RefTypePointer(RefIdentifier)】●
¤》
#@DEFINE_PREFIX_S_UNARY《#@PREFIX_S!【#@_PREFIX_S_UNARY】》

// These source snippets are ambiguous as to whether to parse as ref or exp (they can parse as both)
=====> #@TYPEREFS_AMBIG《
  ►#@TYPE_REFS__QUALIFIER_ANY●
  ►#@TYPE_REFS__TPL_SINGLE_ARG●
  ►#@_TYPEREFS_AMBIG__BASE●
  
  ►#?STRUCTURE_EXPECTED!【dai* #INFIX(flag)● RefTypePointer(RefIdentifier)】●
  ►#?STRUCTURE_EXPECTED!【dai *[]●RefTypeDynArray(RefTypePointer(RefIdentifier))】●
  ►#?STRUCTURE_EXPECTED!【dai*[foo][bar]             ●RefIndexing(RefIndexing(RefTypePointer(?) ?) ?)】●
  ►#?STRUCTURE_EXPECTED!【dai*[foo][bar]*#INFIX(flag)●RefTypePointer(RefIndexing(RefIndexing(RefTypePointer(?) ?) ?))】●
  ►#?STRUCTURE_EXPECTED!【dai**[foo][bar]               ●RefIndexing(RefIndexing(RefTypePointer(RefTypePointer(?)) ?) ?)】●
  ►#?STRUCTURE_EXPECTED!【dai**[foo][bar] * #INFIX(flag)●RefTypePointer(RefIndexing(RefIndexing(RefTypePointer(RefTypePointer(?)) ?) ?))】●
  ►#?STRUCTURE_EXPECTED!【dai*** [bar]●RefIndexing(RefTypePointer(RefTypePointer(RefTypePointer(?))) ?)】●
  ►#?STRUCTURE_EXPECTED!【dai*** #INFIX(flag)●RefTypePointer(RefTypePointer(RefTypePointer(?)))】●
¤》

#@_TYPEREFS_AMBIG__CUSTOM_PREFIX《
  ►#?STRUCTURE_EXPECTED!【#@PREFIX_S[ ]● RefTypeDynArray(#@PREFIX_S) 】●
   ►#?TYPE__NO_PENDING【#:DISCARD_CASE●
  ►#?STRUCTURE_EXPECTED!【#@PREFIX_S[#error(EXP_CLOSE_BRACKET)● RefTypeDynArray(#@PREFIX_S) 】】#TYPE_IS_BROKEN(flag)●
  
  ►#?STRUCTURE_EXPECTED!【#@PREFIX_S[7 /*EXP*/]● RefIndexing(#@PREFIX_S Integer) 】●
  ►#?STRUCTURE_EXPECTED!【#@PREFIX_S[foo /*AMBIG*/ ]● RefIndexing(#@PREFIX_S RefIdentifier)】●
   ►#?TYPE__NO_PENDING【#:DISCARD_CASE●
  ►#?STRUCTURE_EXPECTED!【#@PREFIX_S[foo#error(EXP_CLOSE_BRACKET)● RefIndexing(#@PREFIX_S RefIdentifier)】】#TYPE_IS_BROKEN(flag)●
  ►#?STRUCTURE_EXPECTED!【#@PREFIX_S[foo /*AMBIG*/][]● RefTypeDynArray(RefIndexing(#@PREFIX_S RefIdentifier))】●
  ►#?STRUCTURE_EXPECTED!【#@PREFIX_S[foo*[]/*AMBIG*/]● RefIndexing(#@PREFIX_S 
    RefTypeDynArray(RefTypePointer(RefIdentifier))) 】●

  ►#?STRUCTURE_EXPECTED!【#@PREFIX_S[foo[1, 2]/*EXP*/]●RefIndexing(#@PREFIX_S ExpIndex(* Integer Integer))】●
  ►#?STRUCTURE_EXPECTED!【#@PREFIX_S[foo*[1, 2]/*EXP*/]●RefIndexing(#@PREFIX_S ExpInfix(* ?(Integer Integer)))】●
  ►#?STRUCTURE_EXPECTED!【#@PREFIX_S[foo*[2 : #@NO_EXP]/*EXP*/]●RefIndexing(#@PREFIX_S ExpInfix(* *))】●
  ►#?STRUCTURE_EXPECTED!【#@PREFIX_S[foo[bar]/*AMBIG*/]●RefIndexing(#@PREFIX_S RefIndexing(* *))】●
¤》
#@_TYPEREFS_AMBIG__BASE《#@PREFIX_S!《#@_PREFIX_S_ALL》#@_TYPEREFS_AMBIG__CUSTOM_PREFIX》
#@TYPEREFS_AMBIG__INVALID_QUALIFIER《#TYPE__NO_PENDING(flag)#@DEFINE_PREFIX_S_UNARY#@_TYPEREFS_AMBIG__CUSTOM_PREFIX》


// These references unambiguously parse as ref
=====> #@TYPEREFS_UNAMBIG《
  ►#@TYPE_REFS__QUALIFIER_DOT_ONLY●
  
  ►#@_TYPEREFS_UNAMBIG__BASE●
  ►#?STRUCTURE_EXPECTED!【int *[]●RefTypeDynArray(RefTypePointer(RefPrimitive))】●
  ►#?STRUCTURE_EXPECTED!【int *[foo]●RefIndexing(RefTypePointer(RefPrimitive) RefIdentifier)】●
  ►#?STRUCTURE_EXPECTED!【int * *[foo]●RefIndexing(RefTypePointer(RefTypePointer(RefPrimitive)) RefIdentifier)】●
  ►#?STRUCTURE_EXPECTED!【int *[foo][bar]●RefIndexing(RefIndexing(RefTypePointer(RefPrimitive) ?) RefIdentifier)】●

   ►#?TYPE__NO_PENDING【#:DISCARD_CASE●
  ►#?STRUCTURE_EXPECTED!【int *●RefTypePointer(RefPrimitive)】】●
   ►#?TYPE__NO_PENDING【#:DISCARD_CASE●
  ►#?STRUCTURE_EXPECTED!【int * *●RefTypePointer(RefTypePointer(RefPrimitive))】】●
   ►#?TYPE__NO_PENDING【#:DISCARD_CASE●
  ►#?STRUCTURE_EXPECTED!【int * **●RefTypePointer(RefTypePointer(RefTypePointer(RefPrimitive)))】】●

  ►#?STRUCTURE_EXPECTED!【int function(inout int blah) nothrow●RefTypeFunction(RefPrimitive FunctionParameter(? ?))】●
¤》
#@TYPEREFS_UNAMBIG__NO_PENDING《#TYPE__NO_PENDING(flag)#@TYPEREFS_UNAMBIG》

#@_TYPEREFS_UNAMBIG__CUSTOM_PREFIX《
  ►#?STRUCTURE_EXPECTED!【#@PREFIX_S[foo*/+AMBIG_TO_REF+/]● RefIndexing(#@PREFIX_S RefTypePointer(RefIdentifier))】●
  ►#?STRUCTURE_EXPECTED!【#@PREFIX_S[int /+REF+/ ]● RefIndexing(#@PREFIX_S RefPrimitive)】●
  ►#?STRUCTURE_EXPECTED!【#@PREFIX_S[foo*[int]/+REF+/]●RefIndexing(#@PREFIX_S RefIndexing(* *))】●
   ►#?TYPE__NO_PENDING【#:DISCARD_CASE●
  ►#?STRUCTURE_EXPECTED!【#@PREFIX_S[foo*[int]#error(EXP_CLOSE_BRACKET)●RefIndexing(#@PREFIX_S RefIndexing(* *))】】#TYPE_IS_BROKEN(flag)●
¤》
#@_TYPEREFS_UNAMBIG__BASE《#@PREFIX_S!《#@_PREFIX_S_ALL》#@_TYPEREFS_UNAMBIG__CUSTOM_PREFIX》
#@TYPEREFS_UNAMBIG__INVALID_QUALIFIER《#TYPE__NO_PENDING(flag)#@DEFINE_PREFIX_S_UNARY#@_TYPEREFS_UNAMBIG__CUSTOM_PREFIX》

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
  
  ►#?STRUCTURE_EXPECTED!【int*●RefTypePointer(RefPrimitive)】●
  ►#?STRUCTURE_EXPECTED!【int[]●RefTypeDynArray(RefPrimitive)】●
  ►#?STRUCTURE_EXPECTED!【int*[]●RefTypeDynArray(RefTypePointer(RefPrimitive))】●
  ►#?STRUCTURE_EXPECTED!【foo*●RefTypePointer(RefIdentifier)】●
  ►#?STRUCTURE_EXPECTED!【foo*[]●RefTypeDynArray(RefTypePointer(RefIdentifier))】●
  ►#?STRUCTURE_EXPECTED!【arrayElem[]●RefTypeDynArray(RefIdentifier)】●
  ►#?STRUCTURE_EXPECTED!【arrayElem[foo*]●RefIndexing(RefIdentifier RefTypePointer(RefIdentifier))】●
  ►#?STRUCTURE_EXPECTED!【arrayElem[int]●RefIndexing(RefIdentifier RefPrimitive)】●
  ►#?STRUCTURE_EXPECTED!【arrayElem[foo**bar]●RefIndexing(RefIdentifier ExpInfix(* ExpPrefix(ExpReference(?))) )】●

  ►#?STRUCTURE_EXPECTED!【dai[foo] *[foo][bar]●RefIndexing(RefIndexing(RefTypePointer(*) ?) RefIdentifier)】●  
  ►#?STRUCTURE_EXPECTED!【dai[bar*] *[foo][bar]●RefIndexing(RefIndexing(RefTypePointer(*) ?) RefIdentifier)】●
  ►#?STRUCTURE_EXPECTED!【dai[bar] ***●RefTypePointer(RefTypePointer(RefTypePointer(RefIndexing(? ?))))】●
  
  ►#?STRUCTURE_EXPECTED!【int function(inout int blah) nothrow●RefTypeFunction(RefPrimitive FunctionParameter(? ?))】●
¤》
#@TYPE_REFS__NO_BROKEN__LITE《
  ►#@TYPE_REFS__REFID●
  ►#?STRUCTURE_EXPECTED!【.Bar.foo*[]●RefTypeDynArray(RefTypePointer(RefQualified(RefModuleQualified(?) RefIdentifier)))】●
¤》

#@TYPE_REFS__BROKEN《
   ►#?NO_BROKEN_BRACKETS【#:DISCARD_CASE●
  ►#?STRUCTURE_EXPECTED!【arrayElem[foo #error(EXP_CLOSE_BRACKET)●RefIndexing(RefIdentifier RefIdentifier)】】●

   ►#?NO_BROKEN_PARENS【#:DISCARD_CASE●
  ►#?STRUCTURE_EXPECTED!【typeof("abc" #error(EXP_CLOSE_PARENS)●RefTypeof(String)】】●
¤》

#@TYPE_REFS《
  ►#@TYPE_REFS__NO_BROKEN●
  ►#@TYPE_REFS__BROKEN●
¤》
#@TYPE_REFS__LITE《
  ►#@TYPE_REFS__NO_BROKEN__LITE●
  ►#@TYPE_REFS__BROKEN●
¤》

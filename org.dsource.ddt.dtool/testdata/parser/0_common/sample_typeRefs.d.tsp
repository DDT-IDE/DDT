Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

#@TYPE_REFS__REFID《#?STRUCTURE_EXPECTED!【Foo #TYPEREF_CanParseAsId(flag) ●RefIdentifier】》

#@_FOO_PTR《#?STRUCTURE_EXPECTED!【foo* ●RefTypePointer (RefIdentifier)】》
#@_FOO_ARR《#?STRUCTURE_EXPECTED!【foo[]●RefTypeDynArray(RefIdentifier)】》
#@_INT_PTR《#?STRUCTURE_EXPECTED!【int* ●RefTypePointer (RefPrimitive)】》
#@_INT_ARR《#?STRUCTURE_EXPECTED!【int[]●RefTypeDynArray(RefPrimitive)】》

// These type refs can always be used as qualifiers followed by dot or template instance
#@TYPE_REFS__QUALIFIER_ForTplOrDot《
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
#@TYPE_REFS__QUALIFIER_ForDotOnly《
  ►#?STRUCTURE_EXPECTED!【int●RefPrimitive】●
  ►#?STRUCTURE_EXPECTED!【const(foo)●RefTypeModifier(RefIdentifier)】●
  ►#?STRUCTURE_EXPECTED!【typeof(Bar.foo)●RefTypeof(ExpReference(RefQualified(RefIdentifier RefIdentifier)))】●
  ►#?STRUCTURE_EXPECTED!【bar. foo!this #TYPE_REF_TPL_SIMPLE_ARG(flag)●RefTemplateInstance(RefQualified(RefIdentifier ?) ExpThis)】●
¤》
#@TYPE_REFS__QUALIFIER_ForDot《
  ►#@TYPE_REFS__QUALIFIER_ForTplOrDot●
  ►#@TYPE_REFS__QUALIFIER_ForDotOnly●
¤》

// In the future it may be that RefIndexing and RefSlice are allowed as qualifier, as this makes sense semantically
#@TYPE_REFS__NO_BROKEN__INVALID_QUALIFIER《
  ►#?STRUCTURE_EXPECTED!【int* #TYPE_REF_POINTER(flag)●RefTypePointer(RefPrimitive)】●
  ►#?STRUCTURE_EXPECTED!【foo*[]●RefTypeDynArray(RefTypePointer(RefIdentifier))】●
  ►#?STRUCTURE_EXPECTED!【arrayElem[int]●RefIndexing(RefIdentifier RefPrimitive)】●

  ►#?STRUCTURE_EXPECTED!【dai[foo*] *[bar][foo**bar]●
    RefIndexing(RefIndexing(RefTypePointer(*) ?) ExpInfix(* ExpPrefix(ExpReference(?))))】●
  ►#?STRUCTURE_EXPECTED!【dai[int*[]] *** #TYPE_REF_POINTER(flag)●RefTypePointer(?(RefTypePointer(RefIndexing(? *))))】●
  
  ►#?STRUCTURE_EXPECTED!【foo[foo*[2 : #@NO_EXP]/*EXP*/]●RefIndexing(RefIdentifier ExpInfix(* *))】●
  
  ►#?STRUCTURE_EXPECTED!【dai[bar .. 123]●RefSlice(RefIdentifier #@ExpIdentifier Integer)】●
  ►#?STRUCTURE_EXPECTED!【dai * [bar .. 123] ●RefSlice(#@_FOO_PTR #@ExpIdentifier Integer)】●

  ►#?STRUCTURE_EXPECTED!【foo #?PARSING_AS_EXP【#@IGNORE_REST】 #?PARSING_AS_NESTEDEXP【#@BREAK】 function(in int blah) nothrow● 
    RefTypeFunction(RefIdentifier FunctionParameter(? ?))   #?PARSING_AS_NESTEDEXP【#@BREAK】】 #CANT_FULLY_EXP_PARSE(flag)●
  ►#?STRUCTURE_EXPECTED!【foo* function(in int blah) nothrow #?PARSING_AS_EXP【#@BREAK】 ●
    RefTypeFunction(#@_FOO_PTR FunctionParameter(? ?))】●
¤》

#@TYPE_REFS__NO_BROKEN《
  ►#@TYPE_REFS__QUALIFIER_ForTplOrDot●
  ►#@TYPE_REFS__QUALIFIER_ForDotOnly●
  ►#@TYPE_REFS__NO_BROKEN__INVALID_QUALIFIER●
  
  ►#?STRUCTURE_EXPECTED!【const shared int* #NO_TYPE_APPEND(flag) #TM_ATTRIB_AMBIG(flag) ●
    #?TM_AMBIG_AS_RAW【RefTypePointer(RefPrimitive)●RefTypeModifier(RefTypeModifier( RefTypePointer(RefPrimitive) ))】
    】●
¤》
#@TYPE_REFS__NO_BROKEN__LITE《
  ►#@TYPE_REFS__REFID●
  ►#?STRUCTURE_EXPECTED!【int*#TYPE_REF_POINTER(flag)●RefTypePointer(RefPrimitive)】●
  ►#?STRUCTURE_EXPECTED!【.Bar.foo*[]●RefTypeDynArray(RefTypePointer(RefQualified(RefModuleQualified(?) RefIdentifier)))】●
¤》

  #@TYPEREFS__NB_APPENDABLE《#@TYPE_REFS__NO_BROKEN #?NO_TYPE_APPEND【#:DISCARD_CASE】》
#@TYPEREFS__NBNE_APPENDABLE《#@TYPE_REFS__NO_BROKEN #?NO_TYPE_APPEND【#:DISCARD_CASE】》


#@TYPE_REFS__BROKEN《
   ►#?NO_BROKEN_BRACKETS【#:DISCARD_CASE●
  ►#?STRUCTURE_EXPECTED!【arrayElem[foo #error(EXP_CLOSE_BRACKET) #MISSING_BRACKET(flag)#TYPE_IS_BROKEN(flag)●
  RefIndexing(RefIdentifier RefIdentifier)】】●

   ►#?NO_BROKEN_PARENS【#:DISCARD_CASE●
  ►#?STRUCTURE_EXPECTED!【typeof("abc"  #error(EXP_CLOSE_PARENS) #MISSING_PARENS(flag)#TYPE_IS_BROKEN(flag)●
   RefTypeof(String)】】●
¤》


#@TYPE_REFS《
  ►#@TYPE_REFS__NO_BROKEN●
  ►#@TYPE_REFS__BROKEN●
¤》
#@TYPE_REFS__LITE《
  ►#@TYPE_REFS__NO_BROKEN__LITE●
  ►#@TYPE_REFS__BROKEN●
¤》

/------------------------------/

#@TYPE_MODIFIERS《const●immutable●shared●inout》

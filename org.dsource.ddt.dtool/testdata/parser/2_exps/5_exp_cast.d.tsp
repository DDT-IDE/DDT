▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) cast(Foo) foo
#STRUCTURE_EXPECTED:
ExpCast(RefIdentifier #@ExpIdentifier)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) cast() foo
#STRUCTURE_EXPECTED:
ExpCast( #@NO_TYPEREF #@ExpIdentifier )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) cast(const shared) foo
#STRUCTURE_EXPECTED:
ExpCastQual( #@ExpIdentifier )

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@CAST_QUAL1《const●inout●immutable●shared》
#@CAST_QUAL2《const shared●shared const●inout shared●shared inout》
#@CAST_TYPEREFS《#NO_BROKEN_PARENS(flag)#@TYPE_REFS #?CANT_APPEND_TYPE{#:DISCARD_CASE}》

#@CAST_START《
  ►#?STRUCTURE_EXPECTED!【cast( )●    ExpCast( #@NO_TYPEREF 】● 
  ►#?STRUCTURE_EXPECTED!【cast( #@CAST_TYPEREFS )● ExpCast( #@CAST_TYPEREFS 】● 
  ►#?STRUCTURE_EXPECTED!【cast( #@CAST_QUAL1 )● ExpCastQual(】●
  ►#?STRUCTURE_EXPECTED!【cast( #@CAST_QUAL2 )● ExpCastQual(】●
¤》

#@UNEXP_OR_NO《#@EXP_UNARY●#@NO_EXP》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   #@CAST_START  #@UNEXP_OR_NO
#STRUCTURE_EXPECTED: #@CAST_START  #@UNEXP_OR_NO )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   cast #error(EXP_OPEN_PARENS)【】 #@BREAK
#STRUCTURE_EXPECTED: ExpCast( )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   cast (  #error(EXP_CLOSE_PARENS) #@BREAK / 7  
#STRUCTURE_EXPECTED: ExpCast( #@NO_TYPEREF ) 
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   cast ( #@CAST_TYPEREFS #error(EXP_CLOSE_PARENS) #@BREAK foo  
#STRUCTURE_EXPECTED: ExpCast( #@CAST_TYPEREFS )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   cast ( #@CAST_QUAL1 #@NO_TYPEREF #error(EXP_CLOSE_PARENS) #@BREAK
#STRUCTURE_EXPECTED: ExpCast( RefTypeModifier(#@NO_TYPEREF) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   cast ( #@CAST_QUAL2 #@NO_TYPEREF #error(EXP_CLOSE_PARENS) #@BREAK  
#STRUCTURE_EXPECTED: ExpCast( RefTypeModifier(RefTypeModifier(#@NO_TYPEREF)) )


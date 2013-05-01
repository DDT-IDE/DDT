▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) cast(Foo) foo
#STRUCTURE_EXPECTED:
ExpCast(RefIdentifier #@ExpIdentifier)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) cast() foo
#STRUCTURE_EXPECTED:
ExpCast( #@NO_REF #@ExpIdentifier )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) cast(const shared) foo
#STRUCTURE_EXPECTED:
ExpCastQual( #@ExpIdentifier )

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@UNEXP_OR_NO《#@EXP_UNARY●#@NO_EXP》

#@CAST_QUAL1《const●inout●immutable●shared》
#@CAST_QUAL2《const shared●shared const●inout shared●shared inout》
#@CAST_START《
  ►#?STRUCTURE_EXPECTED!【cast( #NO_BROKEN_PARENS(flag) #@TYPE_REFS )● ExpCast( #@TYPE_REFS 】● 
  ►#?STRUCTURE_EXPECTED!【cast( )●    ExpCast( #@NO_REF 】● 
  ►#?STRUCTURE_EXPECTED!【cast( #@CAST_QUAL1 )● ExpCastQual(】●
  ►#?STRUCTURE_EXPECTED!【cast( #@CAST_QUAL2 )● ExpCastQual(】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   #@CAST_START  #@UNEXP_OR_NO
#STRUCTURE_EXPECTED: #@CAST_START  #@UNEXP_OR_NO )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   cast #error(EXP_OPEN_PARENS)【】 #@BREAK
#STRUCTURE_EXPECTED: ExpCast( )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   cast (  #error(EXP_CLOSE_PARENS) #@BREAK / 7  
#STRUCTURE_EXPECTED: ExpCast( #@NO_REF ) 
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   cast ( #NO_BROKEN_PARENS(flag)#@TYPE_REFS #error(EXP_CLOSE_PARENS) #@BREAK foo  
#STRUCTURE_EXPECTED: ExpCast( #@TYPE_REFS )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   cast ( #@CAST_QUAL1 #error(EXP_OPEN_PARENS)【】 #error(EXP_CLOSE_PARENS) #@BREAK foo
#STRUCTURE_EXPECTED: ExpCast( RefTypeModifier() )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   cast ( #@CAST_QUAL2 #error(EXP_CLOSE_PARENS) #@BREAK foo  
#STRUCTURE_EXPECTED: ExpCastQual( )


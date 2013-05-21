▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)     const(blah)
#STRUCTURE_EXPECTED:  RefTypeModifier(RefIdentifier)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@ARG_CONTENT_OR_NO《
  ►#@TYPE_REFS●
  ►#@NO_TYPEREF●
¤》
#@ARG_CONTENT_CLEAN《
  ►#@TYPE_REFS● 
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)    #@TYPE_MODIFIERS #@NO_TYPEREF #@BREAK
#STRUCTURE_EXPECTED: RefTypeModifier( #@NO_TYPEREF )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)    #@TYPE_MODIFIERS #@TYPEREF_NBNE 
#STRUCTURE_EXPECTED: RefTypeModifier( #@TYPEREF_NBNE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)    #@TYPE_MODIFIERS #@TYPEREF_BROKEN #@BREAK
#STRUCTURE_EXPECTED: RefTypeModifier( #@TYPEREF_BROKEN )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)    #@TYPE_MODIFIERS•(#@ARG_CONTENT_OR_NO #@DISCARD_OnPARENS )
#STRUCTURE_EXPECTED:   RefTypeModifier(#@ARG_CONTENT_OR_NO) 
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)    #@TYPE_MODIFIERS ( #@ARG_CONTENT_OR_NO #error(EXP_CLOSE_PARENS) #@BREAK  
#STRUCTURE_EXPECTED:   RefTypeModifier( #@ARG_CONTENT_OR_NO )


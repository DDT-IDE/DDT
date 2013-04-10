▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case 
mixin foo!(bar);
#AST_STRUCTURE_EXPECTED:
DeclarationMixin(RefTemplateInstance(RefIdentifier RefIdentifier))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)       mixin #@NO_REF #@SEMICOLON_OR_NO
#AST_STRUCTURE_EXPECTED:  DeclarationMixin(#@NO_REF)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)       mixin #@TYPE_REFS #@SEMICOLON_OR_NO
#AST_STRUCTURE_EXPECTED:  DeclarationMixin(#@TYPE_REFS)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)       mixin #@TYPE_REFS__NO_BROKEN id #@SEMICOLON_OR_NO
#AST_STRUCTURE_EXPECTED:  NamedMixinDeclaration(#@TYPE_REFS__NO_BROKEN DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)       mixin #@TYPE_REFS__BROKEN #error(EXP_SEMICOLON) #@IGNORE_REST id ;
#AST_STRUCTURE_EXPECTED:  DeclarationMixin(#@TYPE_REFS__BROKEN)

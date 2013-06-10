▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic cases

static int foo;
extern(C) Foo foo;
align (1) int foo;

#STRUCTURE_EXPECTED:
DeclarationBasicAttrib(DefinitionVariable(RefPrimitive DefSymbol))
DeclarationLinkage(DefinitionVariable(RefIdentifier DefSymbol))
DeclarationAlign(DefinitionVariable(RefPrimitive DefSymbol))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
final {
	#@DECL_LIST_SAMPLE
}
static:
const:
#STRUCTURE_EXPECTED:
DeclarationBasicAttrib(DeclBlock( #@DECL_LIST_SAMPLE )) 
DeclarationBasicAttrib(DeclList(
	DeclarationBasicAttrib(DeclList())
))


Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@DECL_LIST_TESTER《
 ►#?STRUCTURE_EXPECTED!【/**/● 】●
 ►#@DECL_LIST_SAMPLE●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)  #@ATTRIBS_NoENUM : #@DECL_LIST_TESTER
#STRUCTURE_EXPECTED: #@ATTRIBS_NoENUM DeclList( #@DECL_LIST_TESTER ))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)  #@ATTRIBS_NoENUM { #@DECL_LIST_TESTER #@CLOSEBRACE_OR_BREAK
#STRUCTURE_EXPECTED: #@ATTRIBS_NoENUM DeclBlock( #@DECL_LIST_TESTER ))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)  #@ATTRIBS_NoENUM int foo #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED: #@ATTRIBS_NoENUM DefinitionVariable(* *) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)  #@ATTRIBS_NoENUM /*EMPTY DECLARATION Case */ #?PRAGMA!【#error:SE_decl】 ;
#STRUCTURE_EXPECTED: #@ATTRIBS_NoENUM  DeclarationEmpty )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)  #@ATTRIBS /*EOF case*/ #@NO_DECLorBLOCK #@BREAK
#STRUCTURE_EXPECTED: #@ATTRIBS  #@NO_DECLorBLOCK )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)  #@ATTRIBS /*Invalid Decl */ #@NO_DECLorBLOCK #@BREAK else ;
#STRUCTURE_EXPECTED: #@ATTRIBS  #@NO_DECLorBLOCK )


▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  Ensure this case is not broken:
#PARSE(DECLARATION)  #@TYPE_MODIFIERS (foo) fooB ;
#STRUCTURE_EXPECTED: DefVariable(RefTypeModifier(RefIdentifier) DefSymbol) 
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

#@BODY_TYPES《
►int foo1;●

► #error(SE_decl) : 
    int foo2;
    void bar;●

► #error:SE_decl : /* Zero decls */●
► #error:SE_decl { #error:SE_decl } // This will change in the future●
► #error:SE_decl { // This error happening will change in the future
	int fooX;
	void bar;
#error:SE_decl }●

►/*EMPTY DECLARATION*/ ;●
► ●
¤》

#@ATTRIBS_BROKEN《 
 ►#?STRUCTURE_EXPECTED!【extern(#error(BAD_LINKAGE_ID) #@PARENS_BREAK●DeclarationLinkage() 】●
 ►#?STRUCTURE_EXPECTED!【extern(C++ #@PARENS_BREAK●DeclarationLinkage() 】●
 ►#?STRUCTURE_EXPECTED!【align(#error(EXP_INTEGER_DECIMAL) #@PARENS_BREAK●DeclarationAlign() 】●
 ►#?STRUCTURE_EXPECTED!【align(16 #@PARENS_BREAK●DeclarationAlign() 】●
 
 ►#?STRUCTURE_EXPECTED!【pragma #error(EXP_OPEN_PARENS)¤【()】 #@BREAK●DeclarationPragma() 】●
 ►#?STRUCTURE_EXPECTED!【pragma ( #error(EXP_ID) #@PARENS_BREAK●DeclarationPragma(Symbol) 】●
 ►#?STRUCTURE_EXPECTED!【pragma ( foo2 #@PARENS_BREAK●DeclarationPragma(Symbol) 】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)  #@ATTRIBS_BROKEN  /*-----*/  #@BODY_TYPES
#STRUCTURE_EXPECTED: #@ATTRIBS_BROKEN

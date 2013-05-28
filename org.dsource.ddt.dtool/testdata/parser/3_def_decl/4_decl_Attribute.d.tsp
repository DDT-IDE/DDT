▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic cases

static int foo;
extern(C) Foo foo;
align (1) int foo;

#STRUCTURE_EXPECTED:
DeclarationBasicAttrib(DefinitionVariable(RefPrimitive DefSymbol))
DeclarationLinkage(DefinitionVariable(RefIdentifier DefSymbol))
DeclarationAlign(DefinitionVariable(RefPrimitive DefSymbol))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
public: 
#@DECL_LIST_SAMPLE
extern(C):
#@DECL_LIST_SAMPLE

#STRUCTURE_EXPECTED:
DeclarationProtection(DeclList(
	#@DECL_LIST_SAMPLE
	DeclarationLinkage(DeclList( #@DECL_LIST_SAMPLE ))
))
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
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
extern(#error:BAD_LINKAGE_ID) int bar;
align(#error:EXP_INTEGER_DECIMAL) int bar;
align(12 #error:EXP_CLOSE_PARENS int bar;

#STRUCTURE_EXPECTED:
DeclarationLinkage(DefinitionVariable(RefPrimitive DefSymbol))
DeclarationAlign( DefinitionVariable(RefPrimitive DefSymbol) )
DeclarationAlign() DefinitionVariable(RefPrimitive DefSymbol)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂


#@LINKAGE_TYPE{C,C++,D,Windows,Pascal,System}
#@EXTERN_ATTRIB《
 ►#?STRUCTURE_EXPECTED!【extern(#@LINKAGE_TYPE)● DeclarationLinkage( 】●
 ►#?STRUCTURE_EXPECTED!【extern (Xpto #error(BAD_LINKAGE_ID) )● DeclarationLinkage( 】●
 ►#?STRUCTURE_EXPECTED!【extern(#error(BAD_LINKAGE_ID))● DeclarationLinkage( 】●
¤》

#@ALIGN_ATTRIB《
 ►#?STRUCTURE_EXPECTED!【align● DeclarationAlign( 】●
 ►#?STRUCTURE_EXPECTED!【align(1)● DeclarationAlign( 】●
 ►#?STRUCTURE_EXPECTED!【align(12)● DeclarationAlign( 】●
 ►#?STRUCTURE_EXPECTED!【align(#error(EXP_INTEGER_DECIMAL)) ● DeclarationAlign( 】●
¤》

#@PRAGMA_ATTRIB《
 ►#?STRUCTURE_EXPECTED!【pragma(foo, 123)● DeclarationPragma(Symbol Integer】●
 ►#?STRUCTURE_EXPECTED!【pragma(foo)● DeclarationPragma(Symbol 】●
 ►#?STRUCTURE_EXPECTED!【pragma ( #error(EXP_ID) )● DeclarationPragma(Symbol 】●
¤》

#@SIMPLE_ATTRIB《
  ►#?STRUCTURE_EXPECTED!【#@SIMPLE_ATTRIBS●#@SIMPLE_ATTRIBS 】●
¤》
#@ENUM_ATTRIB《
  ►#?STRUCTURE_EXPECTED!【#@SIMPLE_ATTRIBS_ENUM●#@SIMPLE_ATTRIBS_ENUM 】●
¤》

#@ATTRIBS《#@EXTERN_ATTRIB●#@ALIGN_ATTRIB●#@PRAGMA_ATTRIB#PRAGMA(flag)●#@SIMPLE_ATTRIBS》
#@ATTRIBS_NOTM《#@EXTERN_ATTRIB●#@ALIGN_ATTRIB●#@PRAGMA_ATTRIB#PRAGMA(flag)●#@SIMPLE_ATTRIBS_NOTM》
#@ATTRIBS_WITH_ENUM《#@ATTRIBS_NOTM●#@ENUM_ATTRIB》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)  #@ATTRIBS /**/ : #@DECL_LIST_SAMPLE
#STRUCTURE_EXPECTED: #@ATTRIBS DeclList( #@DECL_LIST_SAMPLE ))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)  #@ATTRIBS /**/ : /* Zero decls */
#STRUCTURE_EXPECTED: #@ATTRIBS DeclList( ))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)  #@ATTRIBS /**/ { #@CLOSEBRACE_OR_BREAK 
#STRUCTURE_EXPECTED: #@ATTRIBS DeclBlock( ))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)  #@ATTRIBS { #@DECL_LIST_SAMPLE #@CLOSEBRACE_OR_BREAK
#STRUCTURE_EXPECTED: #@ATTRIBS DeclBlock( #@DECL_LIST_SAMPLE ))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)  #@ATTRIBS_WITH_ENUM int foo;
#STRUCTURE_EXPECTED: #@ATTRIBS_WITH_ENUM DefinitionVariable(* *) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)  #@ATTRIBS /*EMPTY DECLARATION Case */ #?PRAGMA!【#error:SE_decl】 ;
#STRUCTURE_EXPECTED: #@ATTRIBS  DeclarationEmpty )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)  #@ATTRIBS_NOTM /*Invalid Decl */ #@NO_DECLorBLOCK #@BREAK else ;
#STRUCTURE_EXPECTED: #@ATTRIBS_NOTM  #@NO_DECLorBLOCK )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)  #@ATTRIBS_WITH_ENUM /*EOF case*/ #@NO_DECLorBLOCK #@BREAK
#STRUCTURE_EXPECTED: #@ATTRIBS_WITH_ENUM  #@NO_DECLorBLOCK )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#@ATTRIBS_NOTM #@NO_DECLorBLOCK #error(SE_decl) ] int foo;
#STRUCTURE_EXPECTED: 
#@ATTRIBS_NOTM #@NO_DECLorBLOCK ) InvalidSyntaxElement DefinitionVariable(RefPrimitive DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  Ensure this case is not broken:
#PARSE(DECLARATION)  #@TYPE_MODIFIERS (foo) fooB ;
#STRUCTURE_EXPECTED: DefVariable(RefTypeModifier(RefIdentifier) DefSymbol) 
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@ATTRIBS_BAL_BROKEN《 
 ►extern(#error(BAD_LINKAGE_ID)#error(EXP_CLOSE_PARENS)●
 ►extern(C++#error(EXP_CLOSE_PARENS)●
 ►align(#error(EXP_INTEGER_DECIMAL)#error(EXP_CLOSE_PARENS)●
 ►align(16#error(EXP_CLOSE_PARENS) ●
 ►pragma #error(EXP_OPEN_PARENS)¤【()】●
 ►pragma ( #error(EXP_ID) #error(EXP_CLOSE_PARENS)  ●
 ►pragma ( foo2 #error(EXP_CLOSE_PARENS)  ● 
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@ATTRIBS_BAL_BROKEN #@BODY_TYPES《
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
¤》 /*-----*/

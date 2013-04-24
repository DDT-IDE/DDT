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
#@DECLS_SAMPLE
extern(C):
#@DECLS_SAMPLE

#STRUCTURE_EXPECTED:
DeclarationProtection(NodeList2(
	#@DECLS_SAMPLE
	DeclarationLinkage(NodeList2( #@DECLS_SAMPLE ))
))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
final {
	#@DECLS_SAMPLE
}
static:
const:
#STRUCTURE_EXPECTED:
DeclarationBasicAttrib(NodeList2( #@DECLS_SAMPLE )) 
DeclarationBasicAttrib(NodeList2(
	DeclarationBasicAttrib(NodeList2())
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

// TODO pragma expression list !
#@PRAGMA_ATTRIB《
 ►#?STRUCTURE_EXPECTED!【pragma(foo)● DeclarationPragma(Symbol 】●
 ►#?STRUCTURE_EXPECTED!【pragma ( #error(EXP_ID) )● DeclarationPragma(Symbol 】●
¤》

#@SIMPLE_ATTRIB《
  ►#?STRUCTURE_EXPECTED!【#@SIMPLE_ATTRIBS●#@SIMPLE_ATTRIBS (】●
¤》

#@ATTRIBS《#@EXTERN_ATTRIB●#@ALIGN_ATTRIB●#@PRAGMA_ATTRIB#PRAGMA(flag)●#@SIMPLE_ATTRIB》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#@ATTRIBS int foo1;
#STRUCTURE_EXPECTED: #@ATTRIBS DefinitionVariable(* *) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#@ATTRIBS /**/ :
#@DECLS_SAMPLE
#STRUCTURE_EXPECTED: #@ATTRIBS NodeList2( #@DECLS_SAMPLE ))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#@ATTRIBS /**/ : /* Zero decls */
#STRUCTURE_EXPECTED: #@ATTRIBS NodeList2( ))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#@ATTRIBS /**/ { } 
#STRUCTURE_EXPECTED: #@ATTRIBS NodeList2( ))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#@ATTRIBS { 
#@DECLS_SAMPLE
}
#STRUCTURE_EXPECTED: #@ATTRIBS NodeList2( #@DECLS_SAMPLE ))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#@ATTRIBS /*EMPTY DECLARATION Case */ #?PRAGMA!【#error:SE_decl】 ;
#STRUCTURE_EXPECTED: #@ATTRIBS  DeclarationEmpty )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#@ATTRIBS /*EOF case*/ #error(EXPRULE_decl)
#STRUCTURE_EXPECTED: #@ATTRIBS  )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#@ATTRIBS #error(SE_decl) ] int foo;

#STRUCTURE_EXPECTED: #@ATTRIBS InvalidSyntaxElement)
DefinitionVariable(RefPrimitive DefSymbol)

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

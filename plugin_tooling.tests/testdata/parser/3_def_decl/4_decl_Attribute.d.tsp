▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic cases
extern(C) Foo foo;
#STRUCTURE_EXPECTED:
DeclarationAttrib(AttribLinkage DefinitionVariable(RefIdentifier DefSymbol) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
final {
	#@DECL_LIST_SAMPLE
}
static:
const:
#STRUCTURE_EXPECTED:
DeclarationAttrib(AttribBasic DeclBlock(#@DECL_LIST_SAMPLE) ) 
DeclarationAttrib(AttribBasic DeclList(
	DeclarationAttrib(AttribBasic DeclList())
))


Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@DECL_LIST_TESTER《
 ►#?STRUCTURE_EXPECTED!【/**/● 】●
 ►#@DECL_LIST_SAMPLE●
¤》

#@DECL_ATTRIB_BODY_TESTER《
 ►#?STRUCTURE_EXPECTED!【#@NO_DECLorBLOCK /*EOF case*/ #@IGNORE_REST● #@NO_DECLorBLOCK】●
 ►#?STRUCTURE_EXPECTED!【#@NO_DECLorBLOCK /*Invalid Decl */ #@IGNORE_REST else ;● #@NO_DECLorBLOCK】●
 ►#?STRUCTURE_EXPECTED!【int foo #@SEMICOLON_OR_BREAK ● DefinitionVariable(RefPrimitive DefSymbol)】●
 ►#?STRUCTURE_EXPECTED!【/*Empty Decl*/ #?PRAGMA_ATTR!【#error(SE_decl)】 ; #?ENUM_ATTRIB{#:DISCARD_CASE}●DeclarationEmpty】●

 ►#?STRUCTURE_EXPECTED!【invariant() { }● DeclarationInvariant(?)】●
 ►#?STRUCTURE_EXPECTED!【static assert(true);● DeclarationStaticAssert(?)】●
 ►#?STRUCTURE_EXPECTED!【static import foo;● DeclarationImport(*) 】●
 ►#?STRUCTURE_EXPECTED!【static if(true) {} ● DeclarationStaticIf(? ?)】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)  #@ATTRIBS_NoENUM : #@DECL_LIST_TESTER
#STRUCTURE_EXPECTED: DeclarationAttrib( #@ATTRIBS_NoENUM DeclList(#@DECL_LIST_TESTER) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)  #@ATTRIBS_NoENUM { #@DECL_LIST_TESTER #@CLOSEBRACE_OR_BREAK
#STRUCTURE_EXPECTED: DeclarationAttrib( #@ATTRIBS_NoENUM DeclBlock(#@DECL_LIST_TESTER) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)  #@ATTRIBS #@DECL_ATTRIB_BODY_TESTER
#STRUCTURE_EXPECTED: DeclarationAttrib( #@ATTRIBS  #@DECL_ATTRIB_BODY_TESTER )


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
 ►#?STRUCTURE_EXPECTED!【extern(#error(BAD_LINKAGE_ID) #@PARENS_BREAK● AttribLinkage】●
 ►#?STRUCTURE_EXPECTED!【extern(C++, foo #@PARENS_BREAK● AttribCppLinkage(RefIdentifier)】●
 ►#?STRUCTURE_EXPECTED!【align(#error(EXP_INTEGER_DECIMAL) #@PARENS_BREAK● AttribAlign】●
 ►#?STRUCTURE_EXPECTED!【align(16 #@PARENS_BREAK● AttribAlign】●
 
 ►#?STRUCTURE_EXPECTED!【pragma #error(EXP_OPEN_PARENS)¤【()】 #@BREAK● AttribPragma】●
 ►#?STRUCTURE_EXPECTED!【pragma ( #error(EXP_ID) #@PARENS_BREAK● AttribPragma(Symbol)】●
 ►#?STRUCTURE_EXPECTED!【pragma ( foo2 #@PARENS_BREAK● AttribPragma(Symbol)】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)  #@ATTRIBS_BROKEN  /*-----*/  #@BODY_TYPES
#STRUCTURE_EXPECTED: DeclarationAttrib( #@ATTRIBS_BROKEN )

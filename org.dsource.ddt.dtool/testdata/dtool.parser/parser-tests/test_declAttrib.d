▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic samples

extern(C) Foo foo;
align (1) int foo;

#AST_STRUCTURE_EXPECTED:
DeclarationLinkage(DefinitionVariable(RefIdentifier DefSymbol))
DeclarationAlign(DefinitionVariable(RefPrimitive DefSymbol))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
public: 
Foo foo;
int bar;
extern(C):
Foo foo;
int bar;

#AST_STRUCTURE_EXPECTED:
DeclarationProtection(
	DefinitionVariable(RefIdentifier DefSymbol) DefinitionVariable(RefPrimitive DefSymbol)
	DeclarationLinkage(
		DefinitionVariable(RefIdentifier DefSymbol) DefinitionVariable(RefPrimitive DefSymbol)
	)
)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
final {
	int foo;
}
static:
const:
#AST_STRUCTURE_EXPECTED:
DeclarationBasicAttrib( DefinitionVariable(RefPrimitive DefSymbol) )
DeclarationBasicAttrib(
	DeclarationBasicAttrib
)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
extern(#error:BAD_LINKAGE_ID) int bar;
align(#error:EXP_INTEGER_DECIMAL) int bar;
align(12 #error:EXP_CLOSE_PARENS int bar;

#AST_STRUCTURE_EXPECTED:
DeclarationLinkage(DefinitionVariable(RefPrimitive DefSymbol))
DeclarationAlign( DefinitionVariable(RefPrimitive DefSymbol) )
DeclarationAlign() DefinitionVariable(RefPrimitive DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ EOF case
extern(C) #error(EXPRULE_decl)

#AST_STRUCTURE_EXPECTED:
DeclarationLinkage()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Empty Declaration case
extern(C) #error(SE_decl) ;

#AST_STRUCTURE_EXPECTED:
DeclarationLinkage(InvalidSyntaxElement)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
extern(C) #error(SE_decl) ] int foo;

#AST_STRUCTURE_EXPECTED:
DeclarationLinkage(InvalidSyntaxElement)
DefinitionVariable(RefPrimitive DefSymbol)
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@LINKAGE_TYPE{C,C++,D,Windows,Pascal,System}
#@EXTERN_ATTRIB《
 ►extern(#@LINKAGE_TYPE)●
 ►extern (Xpto #error(BAD_LINKAGE_ID) )●
 ►extern(#error(BAD_LINKAGE_ID))●
 ►extern(#error(BAD_LINKAGE_ID)#error(EXP_CLOSE_PARENS)#DECL_BROKEN(flag)●
 ►extern(C++#error(EXP_CLOSE_PARENS)#DECL_BROKEN(flag)●
¤》

#@ALIGN_ATTRIB《
 ►align●
 ►align(1)●
 ►align(12)●
 ►align(#error(EXP_INTEGER_DECIMAL)) ●
 ►align(#error(EXP_INTEGER_DECIMAL)#error(EXP_CLOSE_PARENS)#DECL_BROKEN(flag)●
 ►align(16#error(EXP_CLOSE_PARENS) #DECL_BROKEN(flag)●
¤》

// TODO pragma expression list !
#@PRAGMA_ATTRIB《
 ►pragma(foo)●
 ►pragma #error(EXP_OPEN_PARENS) #error(!)【)】 #DECL_BROKEN(flag)●
 ►pragma ( #error(EXP_ID) #error(EXP_CLOSE_PARENS) #DECL_BROKEN(flag) ●
 ►pragma ( #error(EXP_ID) ) ●
 ►pragma ( foo2 #error(EXP_CLOSE_PARENS) #DECL_BROKEN(flag) ● 
¤》

#@SIMPLE_ATTRIBS《
 ►#@PROT_ATTRIB{private,package,protected,public,export}●
 ►deprecated●
 ►static●
 ►extern●
 ►final●
 ►synchronized●
 ►override●
 ►abstract●
 ►const●
 ►scope●
 ►__gshared●
 ►shared●
 ►immutable●
 ►inout●
¤》

TODO:
auto ??
@disable


#@ATTRIBS{#@EXTERN_ATTRIB,#@ALIGN_ATTRIB,#@PRAGMA_ATTRIB#PRAGMA(flag),#@SIMPLE_ATTRIBS}

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@ATTRIBS #@BODY_TYPES【
►int foo1;●

►#?DECL_BROKEN{#error:SE_decl} :
    int foo2;
    void bar;●

►#?DECL_BROKEN{#error:SE_decl} : /* Zero decls */●

►#?DECL_BROKEN{#error:SE_decl} { #?DECL_BROKEN{#error:SE_decl} } // This will change in the future●

►#?DECL_BROKEN{#error:SE_decl} { // This error happening will change in the future
	int fooX;
	void bar;
#?DECL_BROKEN{#error:SE_decl}}●

►/*EMPTY DECLARATION*/ #?DECL_BROKEN!{#?PRAGMA!{#error:SE_decl}} ;●
¤】

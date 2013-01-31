▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
import foo;
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
import pack.foo;
import pack.bar.foo;
static import pack.bar.foo;
import foo, pack.foo, pack.bar.foo;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
import#error(EXP_ID)#error(EXP_SEMICOLON)#AST_SOURCE_EXPECTED:
import ;
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
import #error(EXP_ID)#error(EXP_SEMICOLON) import foo;
#AST_SOURCE_EXPECTED:
import ; import foo;
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
import foo #error(EXP_SEMICOLON) Bar bar;
import pack. #error(EXP_ID) , foo;
#AST_SOURCE_EXPECTED:
import foo; Bar bar;
import pack. , foo;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ TODO: we could dup case with add of first import in import list
import #error(EXP_ID) ;
import #error(EXP_ID) , foo;
import #error(EXP_ID)#error(EXP_SEMICOLON) .pack #error(EXP_IDENTIFIER);
import pack. #error(EXP_ID) ;
import pack. #error(EXP_ID) , foo;
import foo, #error(EXP_ID) ;
import foo, foo2, #error(EXP_ID) ;

#AST_EXPECTED:
import ; 
import , foo;
import ; .pack;
import pack. ;
import pack. , foo;
import foo, ;
import foo, foo2, ;


▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  Import Alias ▂▂▂▂▂▂▂▂▂▂▂▂▂
import bar1 = foo, bar2 = pack.foo,        pack.fooX, bar3 = pack.bar.foo;
import        foo, bar2 = pack.foo, bar3 = pack.fooX,        pack.bar.foo;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

import #error(EXP_ID)  = pack.foo;
static import foo, #error(EXP_ID)  = pack.foo;
import #error(EXP_ID)#error(EXP_SEMICOLON) int #error(EXP_ID);
import pack.foo #error(EXP_SEMICOLON) #error(SE_decl)= pack.foo#error(EXP_ID) ;

import foo = #error(EXP_ID) ;
import foo = #error(EXP_ID)#error(EXP_SEMICOLON) import foo;
import foo = #error(EXP_ID) , #error(EXP_ID) ;

#AST_EXPECTED:
import  = pack.foo;
static import foo,  = pack.foo;
import ; int;
import pack.foo; = pack.foo;

import foo = ;
import foo = ; import foo;
import foo = , ;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  Import Selection ▂▂▂▂▂▂▂▂▂▂▂▂▂▂

import foo : elem1;
import myfoo = foo : elem1;
static import foo : elem1, elem2, elem3;
import foo, myPackFoo = pack.foo : elem1, elem2, elem3;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

import #error(EXP_ID) : selA ;
import pack. #error(EXP_ID) : selA ;
import foo, #error(EXP_ID) : selA ; 

import foo = #error(EXP_ID) : selA;

import pack.foo : #error(EXP_ID) ;
import pack.foo : #error(EXP_ID)#error(EXP_SEMICOLON)
import pack.foo : selA #error(EXP_SEMICOLON) import foo;
static import pack.foo : selA, #error(EXP_ID) ;
import foo = pack.foo : selA, selB, #error(EXP_ID) ;
import pack.foo : selA, #error(EXP_ID) , #error(EXP_ID) #error(EXP_SEMICOLON) import foo;

#AST_EXPECTED:

import : selA;
import pack. : selA;
import foo, : selA;

import foo =  : selA;

import pack.foo : ;
import pack.foo : ;
import pack.foo : selA; import foo;
static import pack.foo : selA, ;
import foo = pack.foo : selA, selB, ;
import pack.foo : selA, , ; import foo;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Import Selection Alias ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
import foo : ren1 = elem1;
import myfoo = foo : elem1;
import foo : ren1 = elem1, ren2 = elem2, ren3 = elem3;
static import foo : elem1, ren2 = elem2, elem3;
import myfoo = foo : ren1 = elem1, elem2, ren3 = elem3;
import foo, pack.foo : ren2 = elem2, elem3;

static import bar1 = pack.foo : ren1 = elem1;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

import      #error(EXP_ID) : 		#error(EXP_ID) = elem1;
import my = #error(EXP_ID) : 		#error(EXP_ID) = elem1;

import foo : 		#error(EXP_ID) = elem1;
import foo : 		#error(EXP_ID) = #error(EXP_ID);
import foo : elem1, #error(EXP_ID) = elem2;

import foo : elAlias = #error(EXP_ID) ;
import foo : elAlias = #error(EXP_ID)#error(EXP_SEMICOLON) import foo;
import foo : elAlias = #error(EXP_ID) , #error(EXP_ID);
import p.f : elAlias = #error(EXP_ID), sel2;

#AST_EXPECTED:
import      : 		= elem1;
import my = : 		= elem1;

import foo : = elem1;
import foo : = ;
import foo : elem1, = elem2;

import foo : elAlias = ;
import foo : elAlias = ; import foo;
import foo : elAlias = , ;
import p.f : elAlias = , sel2;


▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ attempt all error combinations ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
import #{,foo,bar =, foo:}#{:,=,foo :,foo: elem = }#{,;}
#parser(AllowAnyErrors)
#parser(DontCheckSourceEquality)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
enum Enum : basetype { MemberA , MemberB }

#REFSEARCH:
Enum▪
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ From inside node

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
int outside;
enum Enum : basetype #REFSEARCH¤【Enum▪outside】 { 
	MemberA #REFSEARCH¤【Enum▪outside▪MemberA▪MemberB】 ,  MemberB 
#@《}●/*MISSING*/》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ as qualifed ref
int outside; 
enum Enum : basetype { MemberA , MemberB }
Enum.#REFSEARCH¤【MemberA▪MemberB】
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ syntax error
int outside; 
Enum.#REFSEARCH¤【MemberA】;
enum Enum : basetype { MemberA , 
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ syntax error
int outside; 
Enum#FIND¤【Enum】.#REFSEARCH¤【】;
enum Enum #@《/**/● : basetype》

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

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
int outside;
enum Enum = #REFSEARCH¤【Enum▪VarA▪VarB▪outside】 , VarA = 123, VarB  #@《;●/*MISSING*/》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ as qualifed ref
int outside; 
enum Enum : basetype { MemberA , MemberB }
Enum.#REFSEARCH¤【MemberA▪MemberB▪#@TYPE_PROPERTIES】
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ syntax error
int outside; 
Enum.#REFSEARCH¤【MemberA▪#@TYPE_PROPERTIES】;
enum Enum : basetype { MemberA , 
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ syntax error
int outside; 
Enum#FIND¤【Enum】.#REFSEARCH¤【▪#@TYPE_PROPERTIES】;
enum Enum : basetype
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ syntax error
int outside; 
Enum#FIND¤【Enum】.#REFSEARCH¤【】;
enum Enum 

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
int outside;
Enum#FIND¤【Enum】.#REFSEARCH¤【】;
VarA#FIND¤【VarA】.#REFSEARCH¤【#@INTEGRAL_PROPERTIES】;
enum Enum = , VarA = 123, VarB  #@《;●/*MISSING*/》

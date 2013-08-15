▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
enum : int[] { EnumA = 2 , float EnumB }

#REFSEARCH:
EnumA▪
EnumB▪
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ From inside node
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
int outside;
enum : basetype #REFSEARCH¤【outside▪EnumA▪EnumB】 { EnumA = 2 , float EnumB #@《}●/*MISSING*/》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#@__CONTEXT_START__
	int outside;
	enum : basetype {
		EnumA = 2 #REFSEARCH¤【outside▪EnumA▪EnumB】, // This is not strictly correct, EnumB should not be visible 
		float EnumB #REFSEARCH¤【outside▪EnumA▪EnumB】
	#@《}●/*MISSING*/》
#@__CONTEXT_END__
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ syntax error
#@__CONTEXT_START__
	int outside; 
	#REFSEARCH¤【outside】;
	enum : basetype { , 
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ syntax error
#@__CONTEXT_START__
	int outside; 
	#REFSEARCH¤【outside】;
	enum #@《/**/● : basetype》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ as qualifed ref
int outside; 
enum : int[] { EnumA = 2 , float EnumB }
EnumA.#REFSEARCH¤【】 // TODO int[] members
EnumB.#REFSEARCH¤【】 // TODO int[] members
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ syntax error
int outside; 
enum : /*MISSING*/ { EnumA = 2 , float EnumB } 
EnumA.#REFSEARCH¤【】

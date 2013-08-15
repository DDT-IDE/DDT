▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

struct Struct #@TPLPARAMS_OPT { static int member1; int member2; }
union Union#@TPLPARAMS_OPT { static int member1; int member2; }
class Class #@TPLPARAMS_OPT { static int member1; int member2; }
interface Interface #@TPLPARAMS_OPT { static int member1; int member2; }

template Template (T, U : T[]) { static int member1; int member2; }

#REFSEARCH:
Struct▪
Union▪
Class▪
Interface▪
Template▪
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@AGGREGATE_KIND《struct●union●class●interface》
#@AGGREGATE_KIND_ALL《struct●union●class●interface●template》
#@AGGREGATE_START《#@AGGREGATE_KIND Aggregate#@TPLPARAMS_OPT》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
int other;
#REFSEARCH¤【other▪Aggregate】
#@AGGREGATE_START { static int member1; int member2;
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ From inside aggregate

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ tpl parameters
int outside;
#@AGGREGATE_KIND_ALL Aggregate
(T, #REFSEARCH¤【T▪Aggregate▪outside】 U : T[], alias ALIAS #REFSEARCH¤【T▪U▪ALIAS▪Aggregate▪outside】)
#REFSEARCH¤【T▪U▪ALIAS▪Aggregate▪outside】
#@《{ int bodyVar; }●/*MISSING*/》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  from body 
int outside;
#@AGGREGATE_KIND Aggregate#@TPLPARAMS_OPT {
	int bodyVar;
	#REFSEARCH¤【bodyVar▪#@TPLPARAMS_OPT_result▪Aggregate▪outside】
#@《}●/*MISSING*/》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ from body - template 
int outside;
template Aggregate(T, U : T[]) {
	int bodyVar;
	#REFSEARCH¤【bodyVar▪T▪U▪Aggregate▪outside】
#@《}●/*MISSING*/》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ as qualifed ref
int outside; 
#@AGGREGATE_KIND_ALL Aggregate(T, alias A = int) {
	int bodyVar;
	int bodyVar2;
}
Aggregate.#REFSEARCH¤【bodyVar▪bodyVar2】
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ syntax error
int outside; 
Aggregate.#REFSEARCH¤【】
#@AGGREGATE_KIND_ALL Aggregate(T, alias A = int /*MISSING BODY*/
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ super scopes
#@OBJ_AGGREGATE_KIND《class●interface》

#@COMMON_CLASSES《
►int outside; 
class Super1(TYPE, alias A = int) : SuperX { int superMember; int occluded } 
class Super2(TYPE, alias A = int) : NonExistant, ISuperX;
class SuperX { int fooX }
interface ISuper  { int barA; }
interface ISuperX { int barX; }
》
#@COMMON_CLASSES_result《outside▪Super1▪Super2▪SuperX▪ISuper▪ISuperX》(COMMON_CLASSES)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ super references
int outside;
#@OBJ_AGGREGATE_KIND Aggregate#@TPLPARAMS_OPT : #REFSEARCH¤【#@TPLPARAMS_OPT_result▪Aggregate▪outside】 
{ int bodyVar; }
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#@COMMON_CLASSES
#@OBJ_AGGREGATE_KIND Aggregate#@TPLPARAMS_OPT : Super1, Super2, ISuper {
	int bodyVar;
	#REFSEARCH¤【bodyVar▪#@TPLPARAMS_OPT_result▪superMember▪occluded▪fooX▪barA▪barX▪Aggregate▪#@COMMON_CLASSES_result】
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test occlusion TODO (needs changes in test code)
#@COMMON_CLASSES
#@OBJ_AGGREGATE_KIND Aggregate#@TPLPARAMS_OPT : Super1, Super2, ISuper {
	int bodyVar;
	int occluded;
	#REFSEARCH¤【bodyVar▪#@TPLPARAMS_OPT_result▪superMember▪occluded▪fooX▪barA▪barX▪Aggregate▪#@COMMON_CLASSES_result】

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ as qualifed ref
#@COMMON_CLASSES
#@OBJ_AGGREGATE_KIND Aggregate#@TPLPARAMS_OPT : Super1, Super2, ISuper {
	int bodyVar;
}
Aggregate.#REFSEARCH¤【bodyVar▪superMember▪occluded▪fooX▪barA▪barX】
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@COMMON_CLASSES
Aggregate.#REFSEARCH¤【superMember▪occluded▪fooX▪barA▪barX】
#@OBJ_AGGREGATE_KIND Aggregate#@TPLPARAMS_OPT : Super1, Super2, ISuper

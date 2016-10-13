Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
auto _dummy = (cast(int) foo).#REFSEARCH¤【#@INTEGRAL_PROPERTIES】;
auto _dummy = (cast() foo).#REFSEARCH¤【】;
auto xxx = cast(;
auto xxx2 = cast;
xxx.#REFSEARCH¤【】;
xxx2.#REFSEARCH¤【】;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ cast qualified
int foo;

auto _dummy = (cast(const) foo).#REFSEARCH¤【#@INTEGRAL_PROPERTIES】;
auto _dummy = (cast(const) 123).#REFSEARCH¤【#@INTEGRAL_PROPERTIES】;
auto _dummy = (cast(const) notFound).#REFSEARCH¤【】;

auto xxx = cast(immutable);  xxx.#REFSEARCH¤【】;
auto xxx2 = cast(immutable;  xxx2.#REFSEARCH¤【】;

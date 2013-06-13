▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ keywords
abstract alias align asm assert auto body bool break byte 
case cast catch cdouble cent cfloat char class const continue creal 
dchar debug default delegate delete deprecated do double else enum export extern 
false final finally float for foreach foreach_reverse function goto 
idouble if ifloat immutable import in inout int interface invariant ireal is lazy long 
macro mixin module new nothrow null out override package pragma private protected public pure 
real ref return scope shared short static struct super switch synchronized 
template this throw true try typedef typeid typeof ubyte ucent uint ulong union unittest ushort 
version void volatile wchar while with __FILE__ __LINE__ __gshared __thread __traits 
#LEXERTEST:
KW_ABSTRACT,WS, KW_ALIAS,WS, KW_ALIGN,WS, KW_ASM,WS, KW_ASSERT,WS, KW_AUTO,WS, 
KW_BODY,WS, KW_BOOL,WS, KW_BREAK,WS, KW_BYTE,WS,	EOL,
KW_CASE,WS, KW_CAST,WS, KW_CATCH,WS, KW_CDOUBLE,WS, KW_CENT,WS, KW_CFLOAT,WS, 
KW_CHAR,WS, KW_CLASS,WS, KW_CONST,WS, KW_CONTINUE,WS, KW_CREAL,WS,	EOL,
KW_DCHAR,WS, KW_DEBUG,WS, KW_DEFAULT,WS, KW_DELEGATE,WS, KW_DELETE,WS, KW_DEPRECATED,WS, KW_DO,WS, KW_DOUBLE,WS, 
KW_ELSE,WS, KW_ENUM,WS, KW_EXPORT,WS, KW_EXTERN,WS,		EOL,
KW_FALSE,WS, KW_FINAL,WS, KW_FINALLY,WS, KW_FLOAT,WS, KW_FOR,WS, 
KW_FOREACH,WS, KW_FOREACH_REVERSE,WS, KW_FUNCTION,WS, KW_GOTO,WS,	EOL,
KW_IDOUBLE,WS, KW_IF,WS, KW_IFLOAT,WS, KW_IMMUTABLE,WS, KW_IMPORT,WS, KW_IN,WS, KW_INOUT,WS, 
KW_INT,WS, KW_INTERFACE,WS, KW_INVARIANT,WS, KW_IREAL,WS, KW_IS,WS, KW_LAZY,WS, KW_LONG,WS,		EOL,
KW_MACRO,WS, KW_MIXIN,WS, KW_MODULE,WS, KW_NEW,WS, KW_NOTHROW,WS, KW_NULL,WS, KW_OUT,WS, KW_OVERRIDE,WS, 
KW_PACKAGE,WS, KW_PRAGMA,WS, KW_PRIVATE,WS, KW_PROTECTED,WS, KW_PUBLIC,WS, KW_PURE,WS,	EOL,
KW_REAL,WS, KW_REF,WS, KW_RETURN,WS, KW_SCOPE,WS, KW_SHARED,WS, KW_SHORT,WS, KW_STATIC,WS, 
KW_STRUCT,WS, KW_SUPER,WS, KW_SWITCH,WS, KW_SYNCHRONIZED,WS,	EOL,
KW_TEMPLATE,WS, KW_THIS,WS, KW_THROW,WS, KW_TRUE,WS, KW_TRY,WS, KW_TYPEDEF,WS, KW_TYPEID,WS, KW_TYPEOF,WS, 
KW_UBYTE,WS, KW_UCENT,WS, KW_UINT,WS, KW_ULONG,WS, KW_UNION,WS, KW_UNITTEST,WS, KW_USHORT,WS,	EOL,
KW_VERSION,WS, KW_VOID,WS, KW_VOLATILE,WS, KW_WCHAR,WS, KW_WHILE,WS, KW_WITH,WS, 
KW___FILE__,WS, KW___LINE__,WS, KW___GSHARED,WS, KW___THREAD,WS, KW___TRAITS,WS,	EOL

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
final finall finally finallyy 
#LEXERTEST:
KW_FINAL,WS, ID,WS, KW_FINALLY,WS, ID,WS, EOL

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
__FILE__ __MODULE__ __LINE__ __FUNCTION__ __PRETTY_FUNCTION__ 
__DATE__ __TIME__ __TIMESTAMP__ __VENDOR__ __VERSION__
__EOF__ 
#LEXERTEST:
KW___FILE__,WS, KW___MODULE__,WS, KW___LINE__,WS, KW___FUNCTION__,WS,  KW___PRETTY_FUNCTION__,WS, EOL,
KW___DATE__,WS, KW___TIME__,WS, KW___TIMESTAMP__,WS, KW___VENDOR__,WS, KW___VERSION__, EOL,
EOF,
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
!is
!in
#LEXERTEST:
NOT, KW_IS, EOL,
NOT, KW_IN, EOL,
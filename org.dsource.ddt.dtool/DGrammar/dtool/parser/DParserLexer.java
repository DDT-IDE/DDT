package dtool.parser;
// $ANTLR 3.0b4 dee.g 2006-11-03 18:03:27

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;

public class DParserLexer extends Lexer {
    public static final int MINUS=5;
    public static final int StringPostfix=22;
    public static final int HexDigit=18;
    public static final int DecimalDigit=31;
    public static final int T70=70;
    public static final int T74=74;
    public static final int T85=85;
    public static final int Hexadecimal=30;
    public static final int INTLITERAL=26;
    public static final int T81=81;
    public static final int Integer=24;
    public static final int CHARLITERAL=15;
    public static final int DecimalExponent=38;
    public static final int PLUS=4;
    public static final int T41=41;
    public static final int FloatTypeSuffix=34;
    public static final int T62=62;
    public static final int IDENT=13;
    public static final int T68=68;
    public static final int T73=73;
    public static final int T84=84;
    public static final int T78=78;
    public static final int STRING=23;
    public static final int T42=42;
    public static final int T96=96;
    public static final int T71=71;
    public static final int LINE_COMMENT=9;
    public static final int T72=72;
    public static final int T94=94;
    public static final int RAW_STRING_ALT=20;
    public static final int T76=76;
    public static final int T75=75;
    public static final int Binary=28;
    public static final int T89=89;
    public static final int T67=67;
    public static final int T60=60;
    public static final int T82=82;
    public static final int DIV=7;
    public static final int T100=100;
    public static final int T49=49;
    public static final int Octal=29;
    public static final int T79=79;
    public static final int NESTING_COMMENT=11;
    public static final int T58=58;
    public static final int T93=93;
    public static final int FLOATLITERAL=36;
    public static final int T83=83;
    public static final int IntSuffix=25;
    public static final int T61=61;
    public static final int Decimal=27;
    public static final int T45=45;
    public static final int T101=101;
    public static final int T64=64;
    public static final int T91=91;
    public static final int T86=86;
    public static final int ImaginarySuffix=35;
    public static final int IdStartChar=12;
    public static final int T51=51;
    public static final int T46=46;
    public static final int T77=77;
    public static final int MULT=6;
    public static final int DecimalDigits=37;
    public static final int T69=69;
    public static final int T39=39;
    public static final int EscapeChar=14;
    public static final int T44=44;
    public static final int T55=55;
    public static final int T95=95;
    public static final int T50=50;
    public static final int T92=92;
    public static final int T43=43;
    public static final int MULTILINE_COMMENT=10;
    public static final int OctalDigit=32;
    public static final int T40=40;
    public static final int T66=66;
    public static final int T88=88;
    public static final int T63=63;
    public static final int T57=57;
    public static final int T65=65;
    public static final int T98=98;
    public static final int WHITESPACE=8;
    public static final int T56=56;
    public static final int T87=87;
    public static final int HexEscape=17;
    public static final int T80=80;
    public static final int T59=59;
    public static final int T97=97;
    public static final int T48=48;
    public static final int T54=54;
    public static final int EOF=-1;
    public static final int T47=47;
    public static final int Float=33;
    public static final int Tokens=102;
    public static final int RAW_STRING=19;
    public static final int T53=53;
    public static final int OctalEscape=16;
    public static final int T99=99;
    public static final int T52=52;
    public static final int T90=90;
    public static final int DQ_STRING=21;
    public DParserLexer() {;} 
    public DParserLexer(CharStream input) {
        super(input);
    }
    public String getGrammarFileName() { return "dee.g"; }

    // $ANTLR start PLUS
    public void mPLUS() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = PLUS;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:7:8: ( '+' )
            // dee.g:7:8: '+'
            {
            match('+'); 

            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end PLUS

    // $ANTLR start MINUS
    public void mMINUS() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = MINUS;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:8:9: ( '-' )
            // dee.g:8:9: '-'
            {
            match('-'); 

            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end MINUS

    // $ANTLR start MULT
    public void mMULT() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = MULT;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:9:8: ( '*' )
            // dee.g:9:8: '*'
            {
            match('*'); 

            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end MULT

    // $ANTLR start DIV
    public void mDIV() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = DIV;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:10:7: ( '/' )
            // dee.g:10:7: '/'
            {
            match('/'); 

            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end DIV

    // $ANTLR start T39
    public void mT39() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T39;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:11:7: ( 'module' )
            // dee.g:11:7: 'module'
            {
            match("module"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T39

    // $ANTLR start T40
    public void mT40() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T40;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:12:7: ( ';' )
            // dee.g:12:7: ';'
            {
            match(';'); 

            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T40

    // $ANTLR start T41
    public void mT41() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T41;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:13:7: ( 'static' )
            // dee.g:13:7: 'static'
            {
            match("static"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T41

    // $ANTLR start T42
    public void mT42() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T42;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:14:7: ( 'import' )
            // dee.g:14:7: 'import'
            {
            match("import"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T42

    // $ANTLR start T43
    public void mT43() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T43;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:15:7: ( ',' )
            // dee.g:15:7: ','
            {
            match(','); 

            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T43

    // $ANTLR start T44
    public void mT44() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T44;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:16:7: ( '=' )
            // dee.g:16:7: '='
            {
            match('='); 

            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T44

    // $ANTLR start T45
    public void mT45() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T45;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:17:7: ( ':' )
            // dee.g:17:7: ':'
            {
            match(':'); 

            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T45

    // $ANTLR start T46
    public void mT46() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T46;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:18:7: ( 'private' )
            // dee.g:18:7: 'private'
            {
            match("private"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T46

    // $ANTLR start T47
    public void mT47() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T47;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:19:7: ( 'package' )
            // dee.g:19:7: 'package'
            {
            match("package"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T47

    // $ANTLR start T48
    public void mT48() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T48;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:20:7: ( 'protected' )
            // dee.g:20:7: 'protected'
            {
            match("protected"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T48

    // $ANTLR start T49
    public void mT49() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T49;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:21:7: ( 'public' )
            // dee.g:21:7: 'public'
            {
            match("public"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T49

    // $ANTLR start T50
    public void mT50() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T50;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:22:7: ( 'export' )
            // dee.g:22:7: 'export'
            {
            match("export"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T50

    // $ANTLR start T51
    public void mT51() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T51;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:23:7: ( '{' )
            // dee.g:23:7: '{'
            {
            match('{'); 

            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T51

    // $ANTLR start T52
    public void mT52() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T52;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:24:7: ( '}' )
            // dee.g:24:7: '}'
            {
            match('}'); 

            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T52

    // $ANTLR start T53
    public void mT53() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T53;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:25:7: ( '.' )
            // dee.g:25:7: '.'
            {
            match('.'); 

            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T53

    // $ANTLR start T54
    public void mT54() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T54;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:26:7: ( 'TEMPLATE INSTANCE' )
            // dee.g:26:7: 'TEMPLATE INSTANCE'
            {
            match("TEMPLATE INSTANCE"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T54

    // $ANTLR start T55
    public void mT55() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T55;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:27:7: ( 'typeof' )
            // dee.g:27:7: 'typeof'
            {
            match("typeof"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T55

    // $ANTLR start T56
    public void mT56() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T56;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:28:7: ( '(' )
            // dee.g:28:7: '('
            {
            match('('); 

            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T56

    // $ANTLR start T57
    public void mT57() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T57;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:29:7: ( ')' )
            // dee.g:29:7: ')'
            {
            match(')'); 

            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T57

    // $ANTLR start T58
    public void mT58() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T58;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:30:7: ( 'void' )
            // dee.g:30:7: 'void'
            {
            match("void"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T58

    // $ANTLR start T59
    public void mT59() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T59;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:31:7: ( 'bool' )
            // dee.g:31:7: 'bool'
            {
            match("bool"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T59

    // $ANTLR start T60
    public void mT60() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T60;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:32:7: ( 'byte' )
            // dee.g:32:7: 'byte'
            {
            match("byte"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T60

    // $ANTLR start T61
    public void mT61() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T61;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:33:7: ( 'ubyte' )
            // dee.g:33:7: 'ubyte'
            {
            match("ubyte"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T61

    // $ANTLR start T62
    public void mT62() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T62;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:34:7: ( 'short' )
            // dee.g:34:7: 'short'
            {
            match("short"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T62

    // $ANTLR start T63
    public void mT63() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T63;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:35:7: ( 'ushort' )
            // dee.g:35:7: 'ushort'
            {
            match("ushort"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T63

    // $ANTLR start T64
    public void mT64() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T64;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:36:7: ( 'int' )
            // dee.g:36:7: 'int'
            {
            match("int"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T64

    // $ANTLR start T65
    public void mT65() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T65;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:37:7: ( 'uint' )
            // dee.g:37:7: 'uint'
            {
            match("uint"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T65

    // $ANTLR start T66
    public void mT66() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T66;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:38:7: ( 'long' )
            // dee.g:38:7: 'long'
            {
            match("long"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T66

    // $ANTLR start T67
    public void mT67() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T67;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:39:7: ( 'ulong' )
            // dee.g:39:7: 'ulong'
            {
            match("ulong"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T67

    // $ANTLR start T68
    public void mT68() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T68;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:40:7: ( 'float' )
            // dee.g:40:7: 'float'
            {
            match("float"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T68

    // $ANTLR start T69
    public void mT69() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T69;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:41:7: ( 'double' )
            // dee.g:41:7: 'double'
            {
            match("double"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T69

    // $ANTLR start T70
    public void mT70() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T70;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:42:7: ( 'real' )
            // dee.g:42:7: 'real'
            {
            match("real"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T70

    // $ANTLR start T71
    public void mT71() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T71;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:43:7: ( 'ifloat' )
            // dee.g:43:7: 'ifloat'
            {
            match("ifloat"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T71

    // $ANTLR start T72
    public void mT72() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T72;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:44:7: ( 'idouble' )
            // dee.g:44:7: 'idouble'
            {
            match("idouble"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T72

    // $ANTLR start T73
    public void mT73() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T73;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:45:7: ( 'ireal' )
            // dee.g:45:7: 'ireal'
            {
            match("ireal"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T73

    // $ANTLR start T74
    public void mT74() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T74;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:46:7: ( 'cfloat' )
            // dee.g:46:7: 'cfloat'
            {
            match("cfloat"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T74

    // $ANTLR start T75
    public void mT75() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T75;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:47:7: ( 'cdouble' )
            // dee.g:47:7: 'cdouble'
            {
            match("cdouble"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T75

    // $ANTLR start T76
    public void mT76() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T76;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:48:7: ( 'creal' )
            // dee.g:48:7: 'creal'
            {
            match("creal"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T76

    // $ANTLR start T77
    public void mT77() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T77;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:49:7: ( 'char' )
            // dee.g:49:7: 'char'
            {
            match("char"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T77

    // $ANTLR start T78
    public void mT78() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T78;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:50:7: ( 'wchar' )
            // dee.g:50:7: 'wchar'
            {
            match("wchar"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T78

    // $ANTLR start T79
    public void mT79() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T79;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:51:7: ( 'dchar' )
            // dee.g:51:7: 'dchar'
            {
            match("dchar"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T79

    // $ANTLR start T80
    public void mT80() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T80;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:52:7: ( '[]' )
            // dee.g:52:7: '[]'
            {
            match("[]"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T80

    // $ANTLR start T81
    public void mT81() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T81;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:53:7: ( '[' )
            // dee.g:53:7: '['
            {
            match('['); 

            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T81

    // $ANTLR start T82
    public void mT82() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T82;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:54:7: ( ']' )
            // dee.g:54:7: ']'
            {
            match(']'); 

            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T82

    // $ANTLR start T83
    public void mT83() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T83;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:55:7: ( 'delegate' )
            // dee.g:55:7: 'delegate'
            {
            match("delegate"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T83

    // $ANTLR start T84
    public void mT84() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T84;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:56:7: ( 'function' )
            // dee.g:56:7: 'function'
            {
            match("function"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T84

    // $ANTLR start T85
    public void mT85() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T85;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:57:7: ( 'typedef' )
            // dee.g:57:7: 'typedef'
            {
            match("typedef"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T85

    // $ANTLR start T86
    public void mT86() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T86;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:58:7: ( 'alias' )
            // dee.g:58:7: 'alias'
            {
            match("alias"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T86

    // $ANTLR start T87
    public void mT87() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T87;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:59:7: ( 'auto' )
            // dee.g:59:7: 'auto'
            {
            match("auto"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T87

    // $ANTLR start T88
    public void mT88() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T88;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:60:7: ( 'abstract' )
            // dee.g:60:7: 'abstract'
            {
            match("abstract"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T88

    // $ANTLR start T89
    public void mT89() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T89;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:61:7: ( 'const' )
            // dee.g:61:7: 'const'
            {
            match("const"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T89

    // $ANTLR start T90
    public void mT90() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T90;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:62:7: ( 'deprecated' )
            // dee.g:62:7: 'deprecated'
            {
            match("deprecated"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T90

    // $ANTLR start T91
    public void mT91() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T91;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:63:7: ( 'extern' )
            // dee.g:63:7: 'extern'
            {
            match("extern"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T91

    // $ANTLR start T92
    public void mT92() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T92;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:64:7: ( 'final' )
            // dee.g:64:7: 'final'
            {
            match("final"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T92

    // $ANTLR start T93
    public void mT93() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T93;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:65:7: ( 'override' )
            // dee.g:65:7: 'override'
            {
            match("override"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T93

    // $ANTLR start T94
    public void mT94() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T94;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:66:7: ( 'synchronized' )
            // dee.g:66:7: 'synchronized'
            {
            match("synchronized"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T94

    // $ANTLR start T95
    public void mT95() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T95;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:67:7: ( 'asdfagad' )
            // dee.g:67:7: 'asdfagad'
            {
            match("asdfagad"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T95

    // $ANTLR start T96
    public void mT96() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T96;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:68:7: ( 'in' )
            // dee.g:68:7: 'in'
            {
            match("in"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T96

    // $ANTLR start T97
    public void mT97() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T97;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:69:7: ( 'out' )
            // dee.g:69:7: 'out'
            {
            match("out"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T97

    // $ANTLR start T98
    public void mT98() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T98;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:70:7: ( 'inout' )
            // dee.g:70:7: 'inout'
            {
            match("inout"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T98

    // $ANTLR start T99
    public void mT99() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T99;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:71:7: ( 'lazy' )
            // dee.g:71:7: 'lazy'
            {
            match("lazy"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T99

    // $ANTLR start T100
    public void mT100() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T100;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:72:8: ( 'EXPRESSION' )
            // dee.g:72:8: 'EXPRESSION'
            {
            match("EXPRESSION"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T100

    // $ANTLR start T101
    public void mT101() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = T101;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:73:8: ( 'ASSIGN EXPRESSION' )
            // dee.g:73:8: 'ASSIGN EXPRESSION'
            {
            match("ASSIGN  EXPRESSION"); 


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T101

    // $ANTLR start WHITESPACE
    public void mWHITESPACE() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = WHITESPACE;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:26:14: ( ( (' '|'\\t'|'\\u000B'|'\\u000C'|'\\r'|'\\n'))+ )
            // dee.g:26:14: ( (' '|'\\t'|'\\u000B'|'\\u000C'|'\\r'|'\\n'))+
            {
            // dee.g:26:14: ( (' '|'\\t'|'\\u000B'|'\\u000C'|'\\r'|'\\n'))+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);
                if ( ((LA1_0>='\t' && LA1_0<='\r')||LA1_0==' ') ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // dee.g:26:16: (' '|'\\t'|'\\u000B'|'\\u000C'|'\\r'|'\\n')
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\r')||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);

             channel = 99; 

            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end WHITESPACE

    // $ANTLR start LINE_COMMENT
    public void mLINE_COMMENT() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = LINE_COMMENT;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:29:16: ( '//' (~ ('\\n'|'\\r'))* ( '\\r' )? '\\n' )
            // dee.g:29:16: '//' (~ ('\\n'|'\\r'))* ( '\\r' )? '\\n'
            {
            match("//"); 

            // dee.g:29:21: (~ ('\\n'|'\\r'))*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);
                if ( ((LA2_0>='\u0000' && LA2_0<='\t')||(LA2_0>='\u000B' && LA2_0<='\f')||(LA2_0>='\u000E' && LA2_0<='\uFFFE')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // dee.g:29:22: ~ ('\\n'|'\\r')
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            // dee.g:29:37: ( '\\r' )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( (LA3_0=='\r') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // dee.g:29:37: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }

            match('\n'); 
             channel = 99; 

            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end LINE_COMMENT

    // $ANTLR start MULTILINE_COMMENT
    public void mMULTILINE_COMMENT() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = MULTILINE_COMMENT;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:30:21: ( ( '/*' ( options {greedy=false; } : . )* '*/' ) )
            // dee.g:30:21: ( '/*' ( options {greedy=false; } : . )* '*/' )
            {
            // dee.g:30:21: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // dee.g:30:22: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // dee.g:30:28: ( options {greedy=false; } : . )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);
                if ( (LA4_0=='*') ) {
                    int LA4_1 = input.LA(2);
                    if ( (LA4_1=='/') ) {
                        alt4=2;
                    }
                    else if ( ((LA4_1>='\u0000' && LA4_1<='.')||(LA4_1>='0' && LA4_1<='\uFFFE')) ) {
                        alt4=1;
                    }


                }
                else if ( ((LA4_0>='\u0000' && LA4_0<=')')||(LA4_0>='+' && LA4_0<='\uFFFE')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // dee.g:30:56: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            match("*/"); 


            }

             channel = 99; 

            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end MULTILINE_COMMENT

    // $ANTLR start NESTING_COMMENT
    public void mNESTING_COMMENT() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = NESTING_COMMENT;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:32:4: ( ( '/+' ( options {greedy=false; } : NESTING_COMMENT | . )* '+/' ) )
            // dee.g:32:4: ( '/+' ( options {greedy=false; } : NESTING_COMMENT | . )* '+/' )
            {
            // dee.g:32:4: ( '/+' ( options {greedy=false; } : NESTING_COMMENT | . )* '+/' )
            // dee.g:32:5: '/+' ( options {greedy=false; } : NESTING_COMMENT | . )* '+/'
            {
            match("/+"); 

            // dee.g:32:11: ( options {greedy=false; } : NESTING_COMMENT | . )*
            loop5:
            do {
                int alt5=3;
                int LA5_0 = input.LA(1);
                if ( (LA5_0=='+') ) {
                    int LA5_1 = input.LA(2);
                    if ( (LA5_1=='/') ) {
                        alt5=3;
                    }
                    else if ( ((LA5_1>='\u0000' && LA5_1<='.')||(LA5_1>='0' && LA5_1<='\uFFFE')) ) {
                        alt5=2;
                    }


                }
                else if ( (LA5_0=='/') ) {
                    int LA5_2 = input.LA(2);
                    if ( (LA5_2=='+') ) {
                        alt5=1;
                    }
                    else if ( ((LA5_2>='\u0000' && LA5_2<='*')||(LA5_2>=',' && LA5_2<='\uFFFE')) ) {
                        alt5=2;
                    }


                }
                else if ( ((LA5_0>='\u0000' && LA5_0<='*')||(LA5_0>=',' && LA5_0<='.')||(LA5_0>='0' && LA5_0<='\uFFFE')) ) {
                    alt5=2;
                }


                switch (alt5) {
            	case 1 :
            	    // dee.g:32:39: NESTING_COMMENT
            	    {
            	    mNESTING_COMMENT(); 

            	    }
            	    break;
            	case 2 :
            	    // dee.g:32:57: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            match("+/"); 


            }

             channel = 99; 

            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end NESTING_COMMENT

    // $ANTLR start IDENT
    public void mIDENT() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = IDENT;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:37:10: ( IdStartChar ( IdStartChar | '0' .. '9' )* )
            // dee.g:37:10: IdStartChar ( IdStartChar | '0' .. '9' )*
            {
            mIdStartChar(); 
            // dee.g:37:22: ( IdStartChar | '0' .. '9' )*
            loop6:
            do {
                int alt6=3;
                int LA6_0 = input.LA(1);
                if ( ((LA6_0>='A' && LA6_0<='Z')||LA6_0=='_'||(LA6_0>='a' && LA6_0<='z')) ) {
                    alt6=1;
                }
                else if ( ((LA6_0>='0' && LA6_0<='9')) ) {
                    alt6=2;
                }


                switch (alt6) {
            	case 1 :
            	    // dee.g:37:23: IdStartChar
            	    {
            	    mIdStartChar(); 

            	    }
            	    break;
            	case 2 :
            	    // dee.g:37:37: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end IDENT

    // $ANTLR start IdStartChar
    public void mIdStartChar() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // dee.g:38:22: ( ('_'|'a'..'z'|'A'..'Z'))
            // dee.g:38:25: ('_'|'a'..'z'|'A'..'Z')
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end IdStartChar

    // $ANTLR start CHARLITERAL
    public void mCHARLITERAL() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = CHARLITERAL;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:42:15: ( '\\'' (~ ('\\''|'\\\\') | EscapeChar ) '\\'' )
            // dee.g:42:15: '\\'' (~ ('\\''|'\\\\') | EscapeChar ) '\\''
            {
            match('\''); 
            // dee.g:42:20: (~ ('\\''|'\\\\') | EscapeChar )
            int alt7=2;
            int LA7_0 = input.LA(1);
            if ( ((LA7_0>='\u0000' && LA7_0<='&')||(LA7_0>='(' && LA7_0<='[')||(LA7_0>=']' && LA7_0<='\uFFFE')) ) {
                alt7=1;
            }
            else if ( (LA7_0=='\\') ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("42:20: (~ ('\\''|'\\\\') | EscapeChar )", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // dee.g:42:21: ~ ('\\''|'\\\\')
                    {
                    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFE') ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }


                    }
                    break;
                case 2 :
                    // dee.g:42:36: EscapeChar
                    {
                    mEscapeChar(); 

                    }
                    break;

            }

            match('\''); 

            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end CHARLITERAL

    // $ANTLR start EscapeChar
    public void mEscapeChar() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // dee.g:46:9: ( '\\\\' ('\\''|'\\\"'|'?'|'\\\\'|'a'|'b'|'f'|'n'|'r'|'t'|'v') | OctalEscape | HexEscape | '\\\\' '&' )
            int alt8=4;
            int LA8_0 = input.LA(1);
            if ( (LA8_0=='\\') ) {
                switch ( input.LA(2) ) {
                case 'U':
                case 'u':
                case 'x':
                    alt8=3;
                    break;
                case '&':
                    alt8=4;
                    break;
                case '\"':
                case '\'':
                case '?':
                case '\\':
                case 'a':
                case 'b':
                case 'f':
                case 'n':
                case 'r':
                case 't':
                case 'v':
                    alt8=1;
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                    alt8=2;
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("45:10: fragment EscapeChar : ( '\\\\' ('\\''|'\\\"'|'?'|'\\\\'|'a'|'b'|'f'|'n'|'r'|'t'|'v') | OctalEscape | HexEscape | '\\\\' '&' );", 8, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("45:10: fragment EscapeChar : ( '\\\\' ('\\''|'\\\"'|'?'|'\\\\'|'a'|'b'|'f'|'n'|'r'|'t'|'v') | OctalEscape | HexEscape | '\\\\' '&' );", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // dee.g:46:9: '\\\\' ('\\''|'\\\"'|'?'|'\\\\'|'a'|'b'|'f'|'n'|'r'|'t'|'v')
                    {
                    match('\\'); 
                    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='?'||input.LA(1)=='\\'||(input.LA(1)>='a' && input.LA(1)<='b')||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t'||input.LA(1)=='v' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }


                    }
                    break;
                case 2 :
                    // dee.g:47:9: OctalEscape
                    {
                    mOctalEscape(); 

                    }
                    break;
                case 3 :
                    // dee.g:48:9: HexEscape
                    {
                    mHexEscape(); 

                    }
                    break;
                case 4 :
                    // dee.g:49:7: '\\\\' '&'
                    {
                    match('\\'); 
                    match('&'); 

                    }
                    break;

            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end EscapeChar

    // $ANTLR start OctalEscape
    public void mOctalEscape() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // dee.g:54:9: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
            int alt9=3;
            int LA9_0 = input.LA(1);
            if ( (LA9_0=='\\') ) {
                int LA9_1 = input.LA(2);
                if ( ((LA9_1>='0' && LA9_1<='3')) ) {
                    int LA9_2 = input.LA(3);
                    if ( ((LA9_2>='0' && LA9_2<='7')) ) {
                        int LA9_4 = input.LA(4);
                        if ( ((LA9_4>='0' && LA9_4<='7')) ) {
                            alt9=1;
                        }
                        else {
                            alt9=2;}
                    }
                    else {
                        alt9=3;}
                }
                else if ( ((LA9_1>='4' && LA9_1<='7')) ) {
                    int LA9_3 = input.LA(3);
                    if ( ((LA9_3>='0' && LA9_3<='7')) ) {
                        alt9=2;
                    }
                    else {
                        alt9=3;}
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("53:10: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 9, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("53:10: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // dee.g:54:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // dee.g:54:14: ( '0' .. '3' )
                    // dee.g:54:15: '0' .. '3'
                    {
                    matchRange('0','3'); 

                    }

                    // dee.g:54:25: ( '0' .. '7' )
                    // dee.g:54:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // dee.g:54:36: ( '0' .. '7' )
                    // dee.g:54:37: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 2 :
                    // dee.g:55:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // dee.g:55:14: ( '0' .. '7' )
                    // dee.g:55:15: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // dee.g:55:25: ( '0' .. '7' )
                    // dee.g:55:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 3 :
                    // dee.g:56:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); 
                    // dee.g:56:14: ( '0' .. '7' )
                    // dee.g:56:15: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;

            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end OctalEscape

    // $ANTLR start HexEscape
    public void mHexEscape() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // dee.g:59:9: ( '\\\\U' HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit | '\\\\u' HexDigit HexDigit HexDigit HexDigit | '\\\\x' HexDigit HexDigit )
            int alt10=3;
            int LA10_0 = input.LA(1);
            if ( (LA10_0=='\\') ) {
                switch ( input.LA(2) ) {
                case 'U':
                    alt10=1;
                    break;
                case 'x':
                    alt10=3;
                    break;
                case 'u':
                    alt10=2;
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("58:10: fragment HexEscape : ( '\\\\U' HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit | '\\\\u' HexDigit HexDigit HexDigit HexDigit | '\\\\x' HexDigit HexDigit );", 10, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("58:10: fragment HexEscape : ( '\\\\U' HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit | '\\\\u' HexDigit HexDigit HexDigit HexDigit | '\\\\x' HexDigit HexDigit );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // dee.g:59:9: '\\\\U' HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit
                    {
                    match("\\U"); 

                    mHexDigit(); 
                    mHexDigit(); 
                    mHexDigit(); 
                    mHexDigit(); 
                    mHexDigit(); 
                    mHexDigit(); 
                    mHexDigit(); 
                    mHexDigit(); 

                    }
                    break;
                case 2 :
                    // dee.g:60:9: '\\\\u' HexDigit HexDigit HexDigit HexDigit
                    {
                    match("\\u"); 

                    mHexDigit(); 
                    mHexDigit(); 
                    mHexDigit(); 
                    mHexDigit(); 

                    }
                    break;
                case 3 :
                    // dee.g:61:9: '\\\\x' HexDigit HexDigit
                    {
                    match("\\x"); 

                    mHexDigit(); 
                    mHexDigit(); 

                    }
                    break;

            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end HexEscape

    // $ANTLR start RAW_STRING
    public void mRAW_STRING() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // dee.g:64:25: ( ('r'|'x') '\"' (~ '\"' )* '\"' )
            // dee.g:64:25: ('r'|'x') '\"' (~ '\"' )* '\"'
            {
            if ( input.LA(1)=='r'||input.LA(1)=='x' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            match('\"'); 
            // dee.g:64:42: (~ '\"' )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);
                if ( ((LA11_0>='\u0000' && LA11_0<='!')||(LA11_0>='#' && LA11_0<='\uFFFE')) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // dee.g:64:43: ~ '\"'
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);

            match('\"'); 

            }

        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end RAW_STRING

    // $ANTLR start RAW_STRING_ALT
    public void mRAW_STRING_ALT() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // dee.g:65:29: ( '`' (~ '`' )* '`' )
            // dee.g:65:29: '`' (~ '`' )* '`'
            {
            match('`'); 
            // dee.g:65:33: (~ '`' )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);
                if ( ((LA12_0>='\u0000' && LA12_0<='_')||(LA12_0>='a' && LA12_0<='\uFFFE')) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // dee.g:65:34: ~ '`'
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='_')||(input.LA(1)>='a' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);

            match('`'); 

            }

        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end RAW_STRING_ALT

    // $ANTLR start DQ_STRING
    public void mDQ_STRING() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // dee.g:66:25: ( '\"' (~ ('\"'|'\\\\') | EscapeChar )* '\"' )
            // dee.g:66:25: '\"' (~ ('\"'|'\\\\') | EscapeChar )* '\"'
            {
            match('\"'); 
            // dee.g:66:29: (~ ('\"'|'\\\\') | EscapeChar )*
            loop13:
            do {
                int alt13=3;
                int LA13_0 = input.LA(1);
                if ( ((LA13_0>='\u0000' && LA13_0<='!')||(LA13_0>='#' && LA13_0<='[')||(LA13_0>=']' && LA13_0<='\uFFFE')) ) {
                    alt13=1;
                }
                else if ( (LA13_0=='\\') ) {
                    alt13=2;
                }


                switch (alt13) {
            	case 1 :
            	    // dee.g:66:30: ~ ('\"'|'\\\\')
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // dee.g:66:44: EscapeChar
            	    {
            	    mEscapeChar(); 

            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);

            match('\"'); 

            }

        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end DQ_STRING

    // $ANTLR start StringPostfix
    public void mStringPostfix() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // dee.g:68:25: ( ('c'|'w'|'d'))
            // dee.g:68:27: ('c'|'w'|'d')
            {
            if ( (input.LA(1)>='c' && input.LA(1)<='d')||input.LA(1)=='w' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end StringPostfix

    // $ANTLR start STRING
    public void mSTRING() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = STRING;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:69:10: ( ( RAW_STRING | RAW_STRING_ALT | DQ_STRING ) ( StringPostfix )? )
            // dee.g:69:10: ( RAW_STRING | RAW_STRING_ALT | DQ_STRING ) ( StringPostfix )?
            {
            // dee.g:69:10: ( RAW_STRING | RAW_STRING_ALT | DQ_STRING )
            int alt14=3;
            switch ( input.LA(1) ) {
            case 'r':
            case 'x':
                alt14=1;
                break;
            case '`':
                alt14=2;
                break;
            case '\"':
                alt14=3;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("69:10: ( RAW_STRING | RAW_STRING_ALT | DQ_STRING )", 14, 0, input);

                throw nvae;
            }

            switch (alt14) {
                case 1 :
                    // dee.g:69:11: RAW_STRING
                    {
                    mRAW_STRING(); 

                    }
                    break;
                case 2 :
                    // dee.g:69:24: RAW_STRING_ALT
                    {
                    mRAW_STRING_ALT(); 

                    }
                    break;
                case 3 :
                    // dee.g:69:41: DQ_STRING
                    {
                    mDQ_STRING(); 

                    }
                    break;

            }

            // dee.g:69:53: ( StringPostfix )?
            int alt15=2;
            int LA15_0 = input.LA(1);
            if ( ((LA15_0>='c' && LA15_0<='d')||LA15_0=='w') ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // dee.g:69:53: StringPostfix
                    {
                    mStringPostfix(); 

                    }
                    break;

            }


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end STRING

    // $ANTLR start INTLITERAL
    public void mINTLITERAL() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = INTLITERAL;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:74:15: ( Integer ( IntSuffix )? )
            // dee.g:74:15: Integer ( IntSuffix )?
            {
            mInteger(); 
            // dee.g:74:23: ( IntSuffix )?
            int alt16=2;
            int LA16_0 = input.LA(1);
            if ( (LA16_0=='L'||LA16_0=='U'||LA16_0=='u') ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // dee.g:74:23: IntSuffix
                    {
                    mIntSuffix(); 

                    }
                    break;

            }


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end INTLITERAL

    // $ANTLR start IntSuffix
    public void mIntSuffix() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // dee.g:75:23: ( 'L' | 'u' | 'U' | 'Lu' | 'LU' | 'uL' | 'UL' )
            int alt17=7;
            switch ( input.LA(1) ) {
            case 'L':
                switch ( input.LA(2) ) {
                case 'u':
                    alt17=4;
                    break;
                case 'U':
                    alt17=5;
                    break;
                default:
                    alt17=1;}

                break;
            case 'u':
                int LA17_2 = input.LA(2);
                if ( (LA17_2=='L') ) {
                    alt17=6;
                }
                else {
                    alt17=2;}
                break;
            case 'U':
                int LA17_3 = input.LA(2);
                if ( (LA17_3=='L') ) {
                    alt17=7;
                }
                else {
                    alt17=3;}
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("75:10: fragment IntSuffix : ( 'L' | 'u' | 'U' | 'Lu' | 'LU' | 'uL' | 'UL' );", 17, 0, input);

                throw nvae;
            }

            switch (alt17) {
                case 1 :
                    // dee.g:75:23: 'L'
                    {
                    match('L'); 

                    }
                    break;
                case 2 :
                    // dee.g:75:27: 'u'
                    {
                    match('u'); 

                    }
                    break;
                case 3 :
                    // dee.g:75:31: 'U'
                    {
                    match('U'); 

                    }
                    break;
                case 4 :
                    // dee.g:75:35: 'Lu'
                    {
                    match("Lu"); 


                    }
                    break;
                case 5 :
                    // dee.g:75:40: 'LU'
                    {
                    match("LU"); 


                    }
                    break;
                case 6 :
                    // dee.g:75:45: 'uL'
                    {
                    match("uL"); 


                    }
                    break;
                case 7 :
                    // dee.g:75:50: 'UL'
                    {
                    match("UL"); 


                    }
                    break;

            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end IntSuffix

    // $ANTLR start Integer
    public void mInteger() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // dee.g:76:20: ( Decimal | Binary | Octal | Hexadecimal )
            int alt18=4;
            int LA18_0 = input.LA(1);
            if ( (LA18_0=='0') ) {
                switch ( input.LA(2) ) {
                case 'X':
                case 'x':
                    alt18=4;
                    break;
                case 'B':
                case 'b':
                    alt18=2;
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '_':
                    alt18=3;
                    break;
                default:
                    alt18=1;}

            }
            else if ( ((LA18_0>='1' && LA18_0<='9')) ) {
                alt18=1;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("76:10: fragment Integer : ( Decimal | Binary | Octal | Hexadecimal );", 18, 0, input);

                throw nvae;
            }
            switch (alt18) {
                case 1 :
                    // dee.g:76:20: Decimal
                    {
                    mDecimal(); 

                    }
                    break;
                case 2 :
                    // dee.g:76:29: Binary
                    {
                    mBinary(); 

                    }
                    break;
                case 3 :
                    // dee.g:76:37: Octal
                    {
                    mOctal(); 

                    }
                    break;
                case 4 :
                    // dee.g:76:44: Hexadecimal
                    {
                    mHexadecimal(); 

                    }
                    break;

            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end Integer

    // $ANTLR start Decimal
    public void mDecimal() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // dee.g:78:21: ( '0' | '1' .. '9' ( DecimalDigit | '_' )* )
            int alt20=2;
            int LA20_0 = input.LA(1);
            if ( (LA20_0=='0') ) {
                alt20=1;
            }
            else if ( ((LA20_0>='1' && LA20_0<='9')) ) {
                alt20=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("78:10: fragment Decimal : ( '0' | '1' .. '9' ( DecimalDigit | '_' )* );", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // dee.g:78:21: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // dee.g:78:27: '1' .. '9' ( DecimalDigit | '_' )*
                    {
                    matchRange('1','9'); 
                    // dee.g:78:36: ( DecimalDigit | '_' )*
                    loop19:
                    do {
                        int alt19=3;
                        int LA19_0 = input.LA(1);
                        if ( ((LA19_0>='0' && LA19_0<='9')) ) {
                            alt19=1;
                        }
                        else if ( (LA19_0=='_') ) {
                            alt19=2;
                        }


                        switch (alt19) {
                    	case 1 :
                    	    // dee.g:78:37: DecimalDigit
                    	    {
                    	    mDecimalDigit(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // dee.g:78:52: '_'
                    	    {
                    	    match('_'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop19;
                        }
                    } while (true);


                    }
                    break;

            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end Decimal

    // $ANTLR start Binary
    public void mBinary() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // dee.g:79:20: ( ( '0b' | '0B' ) ( ('0'|'1'|'_'))+ )
            // dee.g:79:20: ( '0b' | '0B' ) ( ('0'|'1'|'_'))+
            {
            // dee.g:79:20: ( '0b' | '0B' )
            int alt21=2;
            int LA21_0 = input.LA(1);
            if ( (LA21_0=='0') ) {
                int LA21_1 = input.LA(2);
                if ( (LA21_1=='B') ) {
                    alt21=2;
                }
                else if ( (LA21_1=='b') ) {
                    alt21=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("79:20: ( '0b' | '0B' )", 21, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("79:20: ( '0b' | '0B' )", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // dee.g:79:21: '0b'
                    {
                    match("0b"); 


                    }
                    break;
                case 2 :
                    // dee.g:79:28: '0B'
                    {
                    match("0B"); 


                    }
                    break;

            }

            // dee.g:79:34: ( ('0'|'1'|'_'))+
            int cnt22=0;
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);
                if ( ((LA22_0>='0' && LA22_0<='1')||LA22_0=='_') ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // dee.g:79:35: ('0'|'1'|'_')
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='1')||input.LA(1)=='_' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt22 >= 1 ) break loop22;
                        EarlyExitException eee =
                            new EarlyExitException(22, input);
                        throw eee;
                }
                cnt22++;
            } while (true);


            }

        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end Binary

    // $ANTLR start Octal
    public void mOctal() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // dee.g:80:19: ( '0' ( OctalDigit | '_' )+ )
            // dee.g:80:19: '0' ( OctalDigit | '_' )+
            {
            match('0'); 
            // dee.g:80:23: ( OctalDigit | '_' )+
            int cnt23=0;
            loop23:
            do {
                int alt23=3;
                int LA23_0 = input.LA(1);
                if ( ((LA23_0>='0' && LA23_0<='7')) ) {
                    alt23=1;
                }
                else if ( (LA23_0=='_') ) {
                    alt23=2;
                }


                switch (alt23) {
            	case 1 :
            	    // dee.g:80:24: OctalDigit
            	    {
            	    mOctalDigit(); 

            	    }
            	    break;
            	case 2 :
            	    // dee.g:80:37: '_'
            	    {
            	    match('_'); 

            	    }
            	    break;

            	default :
            	    if ( cnt23 >= 1 ) break loop23;
                        EarlyExitException eee =
                            new EarlyExitException(23, input);
                        throw eee;
                }
                cnt23++;
            } while (true);


            }

        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end Octal

    // $ANTLR start Hexadecimal
    public void mHexadecimal() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // dee.g:81:25: ( ( '0x' | '0X' ) ( HexDigit | '_' )+ )
            // dee.g:81:25: ( '0x' | '0X' ) ( HexDigit | '_' )+
            {
            // dee.g:81:25: ( '0x' | '0X' )
            int alt24=2;
            int LA24_0 = input.LA(1);
            if ( (LA24_0=='0') ) {
                int LA24_1 = input.LA(2);
                if ( (LA24_1=='X') ) {
                    alt24=2;
                }
                else if ( (LA24_1=='x') ) {
                    alt24=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("81:25: ( '0x' | '0X' )", 24, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("81:25: ( '0x' | '0X' )", 24, 0, input);

                throw nvae;
            }
            switch (alt24) {
                case 1 :
                    // dee.g:81:26: '0x'
                    {
                    match("0x"); 


                    }
                    break;
                case 2 :
                    // dee.g:81:33: '0X'
                    {
                    match("0X"); 


                    }
                    break;

            }

            // dee.g:81:39: ( HexDigit | '_' )+
            int cnt25=0;
            loop25:
            do {
                int alt25=3;
                int LA25_0 = input.LA(1);
                if ( ((LA25_0>='0' && LA25_0<='9')||(LA25_0>='A' && LA25_0<='F')||(LA25_0>='a' && LA25_0<='f')) ) {
                    alt25=1;
                }
                else if ( (LA25_0=='_') ) {
                    alt25=2;
                }


                switch (alt25) {
            	case 1 :
            	    // dee.g:81:40: HexDigit
            	    {
            	    mHexDigit(); 

            	    }
            	    break;
            	case 2 :
            	    // dee.g:81:51: '_'
            	    {
            	    match('_'); 

            	    }
            	    break;

            	default :
            	    if ( cnt25 >= 1 ) break loop25;
                        EarlyExitException eee =
                            new EarlyExitException(25, input);
                        throw eee;
                }
                cnt25++;
            } while (true);


            }

        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end Hexadecimal

    // $ANTLR start DecimalDigit
    public void mDecimalDigit() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // dee.g:83:26: ( '0' .. '9' )
            // dee.g:83:26: '0' .. '9'
            {
            matchRange('0','9'); 

            }

        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end DecimalDigit

    // $ANTLR start OctalDigit
    public void mOctalDigit() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // dee.g:84:24: ( '0' .. '7' )
            // dee.g:84:24: '0' .. '7'
            {
            matchRange('0','7'); 

            }

        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end OctalDigit

    // $ANTLR start HexDigit
    public void mHexDigit() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // dee.g:85:22: ( ('0'..'9'|'a'..'f'|'A'..'F'))
            // dee.g:85:22: ('0'..'9'|'a'..'f'|'A'..'F')
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end HexDigit

    // $ANTLR start FLOATLITERAL
    public void mFLOATLITERAL() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int type = FLOATLITERAL;
            int start = getCharIndex();
            int line = getLine();
            int charPosition = getCharPositionInLine();
            int channel = Token.DEFAULT_CHANNEL;
            // dee.g:89:17: ( Float ( FloatTypeSuffix )? ( ImaginarySuffix )? )
            // dee.g:89:17: Float ( FloatTypeSuffix )? ( ImaginarySuffix )?
            {
            mFloat(); 
            // dee.g:89:23: ( FloatTypeSuffix )?
            int alt26=2;
            int LA26_0 = input.LA(1);
            if ( (LA26_0=='F'||LA26_0=='L'||LA26_0=='f') ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // dee.g:89:23: FloatTypeSuffix
                    {
                    mFloatTypeSuffix(); 

                    }
                    break;

            }

            // dee.g:89:40: ( ImaginarySuffix )?
            int alt27=2;
            int LA27_0 = input.LA(1);
            if ( (LA27_0=='i') ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // dee.g:89:40: ImaginarySuffix
                    {
                    mImaginarySuffix(); 

                    }
                    break;

            }


            }



                    if ( token==null && ruleNestingLevel==1 ) {
                        emit(type,line,charPosition,channel,start,getCharIndex()-1);
                    }

                        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end FLOATLITERAL

    // $ANTLR start Float
    public void mFloat() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // dee.g:92:9: ( DecimalDigits '.' ( DecimalDigits ( DecimalExponent )? )? | '.' DecimalDigits ( DecimalExponent )? | DecimalDigits ( DecimalExponent )? )
            int alt32=3;
            alt32 = dfa32.predict(input);
            switch (alt32) {
                case 1 :
                    // dee.g:92:9: DecimalDigits '.' ( DecimalDigits ( DecimalExponent )? )?
                    {
                    mDecimalDigits(); 
                    match('.'); 
                    // dee.g:92:27: ( DecimalDigits ( DecimalExponent )? )?
                    int alt29=2;
                    int LA29_0 = input.LA(1);
                    if ( ((LA29_0>='0' && LA29_0<='9')||LA29_0=='_') ) {
                        alt29=1;
                    }
                    switch (alt29) {
                        case 1 :
                            // dee.g:92:28: DecimalDigits ( DecimalExponent )?
                            {
                            mDecimalDigits(); 
                            // dee.g:92:42: ( DecimalExponent )?
                            int alt28=2;
                            int LA28_0 = input.LA(1);
                            if ( (LA28_0=='E'||LA28_0=='e') ) {
                                alt28=1;
                            }
                            switch (alt28) {
                                case 1 :
                                    // dee.g:92:42: DecimalExponent
                                    {
                                    mDecimalExponent(); 

                                    }
                                    break;

                            }


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // dee.g:93:9: '.' DecimalDigits ( DecimalExponent )?
                    {
                    match('.'); 
                    mDecimalDigits(); 
                    // dee.g:93:27: ( DecimalExponent )?
                    int alt30=2;
                    int LA30_0 = input.LA(1);
                    if ( (LA30_0=='E'||LA30_0=='e') ) {
                        alt30=1;
                    }
                    switch (alt30) {
                        case 1 :
                            // dee.g:93:27: DecimalExponent
                            {
                            mDecimalExponent(); 

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // dee.g:94:9: DecimalDigits ( DecimalExponent )?
                    {
                    mDecimalDigits(); 
                    // dee.g:94:23: ( DecimalExponent )?
                    int alt31=2;
                    int LA31_0 = input.LA(1);
                    if ( (LA31_0=='E'||LA31_0=='e') ) {
                        alt31=1;
                    }
                    switch (alt31) {
                        case 1 :
                            // dee.g:94:23: DecimalExponent
                            {
                            mDecimalExponent(); 

                            }
                            break;

                    }


                    }
                    break;

            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end Float

    // $ANTLR start DecimalExponent
    public void mDecimalExponent() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // dee.g:97:29: ( 'e' | 'E' | 'e+' | 'E+' | 'e-' | 'E-' DecimalDigits )
            int alt33=6;
            int LA33_0 = input.LA(1);
            if ( (LA33_0=='e') ) {
                switch ( input.LA(2) ) {
                case '-':
                    alt33=5;
                    break;
                case '+':
                    alt33=3;
                    break;
                default:
                    alt33=1;}

            }
            else if ( (LA33_0=='E') ) {
                switch ( input.LA(2) ) {
                case '-':
                    alt33=6;
                    break;
                case '+':
                    alt33=4;
                    break;
                default:
                    alt33=2;}

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("97:10: fragment DecimalExponent : ( 'e' | 'E' | 'e+' | 'E+' | 'e-' | 'E-' DecimalDigits );", 33, 0, input);

                throw nvae;
            }
            switch (alt33) {
                case 1 :
                    // dee.g:97:29: 'e'
                    {
                    match('e'); 

                    }
                    break;
                case 2 :
                    // dee.g:97:35: 'E'
                    {
                    match('E'); 

                    }
                    break;
                case 3 :
                    // dee.g:97:41: 'e+'
                    {
                    match("e+"); 


                    }
                    break;
                case 4 :
                    // dee.g:97:48: 'E+'
                    {
                    match("E+"); 


                    }
                    break;
                case 5 :
                    // dee.g:97:55: 'e-'
                    {
                    match("e-"); 


                    }
                    break;
                case 6 :
                    // dee.g:97:62: 'E-' DecimalDigits
                    {
                    match("E-"); 

                    mDecimalDigits(); 

                    }
                    break;

            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end DecimalExponent

    // $ANTLR start DecimalDigits
    public void mDecimalDigits() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // dee.g:98:27: ( ( ('0'..'9'|'_'))+ )
            // dee.g:98:27: ( ('0'..'9'|'_'))+
            {
            // dee.g:98:27: ( ('0'..'9'|'_'))+
            int cnt34=0;
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);
                if ( ((LA34_0>='0' && LA34_0<='9')||LA34_0=='_') ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // dee.g:98:28: ('0'..'9'|'_')
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||input.LA(1)=='_' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt34 >= 1 ) break loop34;
                        EarlyExitException eee =
                            new EarlyExitException(34, input);
                        throw eee;
                }
                cnt34++;
            } while (true);


            }

        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end DecimalDigits

    // $ANTLR start FloatTypeSuffix
    public void mFloatTypeSuffix() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // dee.g:99:26: ( ('f'|'F'|'L'))
            // dee.g:99:30: ('f'|'F'|'L')
            {
            if ( input.LA(1)=='F'||input.LA(1)=='L'||input.LA(1)=='f' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end FloatTypeSuffix

    // $ANTLR start ImaginarySuffix
    public void mImaginarySuffix() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // dee.g:100:30: ( 'i' )
            // dee.g:100:30: 'i'
            {
            match('i'); 

            }

        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end ImaginarySuffix

    public void mTokens() throws RecognitionException {
        // dee.g:1:10: ( PLUS | MINUS | MULT | DIV | T39 | T40 | T41 | T42 | T43 | T44 | T45 | T46 | T47 | T48 | T49 | T50 | T51 | T52 | T53 | T54 | T55 | T56 | T57 | T58 | T59 | T60 | T61 | T62 | T63 | T64 | T65 | T66 | T67 | T68 | T69 | T70 | T71 | T72 | T73 | T74 | T75 | T76 | T77 | T78 | T79 | T80 | T81 | T82 | T83 | T84 | T85 | T86 | T87 | T88 | T89 | T90 | T91 | T92 | T93 | T94 | T95 | T96 | T97 | T98 | T99 | T100 | T101 | WHITESPACE | LINE_COMMENT | MULTILINE_COMMENT | NESTING_COMMENT | IDENT | CHARLITERAL | STRING | INTLITERAL | FLOATLITERAL )
        int alt35=76;
        alt35 = dfa35.predict(input);
        switch (alt35) {
            case 1 :
                // dee.g:1:10: PLUS
                {
                mPLUS(); 

                }
                break;
            case 2 :
                // dee.g:1:15: MINUS
                {
                mMINUS(); 

                }
                break;
            case 3 :
                // dee.g:1:21: MULT
                {
                mMULT(); 

                }
                break;
            case 4 :
                // dee.g:1:26: DIV
                {
                mDIV(); 

                }
                break;
            case 5 :
                // dee.g:1:30: T39
                {
                mT39(); 

                }
                break;
            case 6 :
                // dee.g:1:34: T40
                {
                mT40(); 

                }
                break;
            case 7 :
                // dee.g:1:38: T41
                {
                mT41(); 

                }
                break;
            case 8 :
                // dee.g:1:42: T42
                {
                mT42(); 

                }
                break;
            case 9 :
                // dee.g:1:46: T43
                {
                mT43(); 

                }
                break;
            case 10 :
                // dee.g:1:50: T44
                {
                mT44(); 

                }
                break;
            case 11 :
                // dee.g:1:54: T45
                {
                mT45(); 

                }
                break;
            case 12 :
                // dee.g:1:58: T46
                {
                mT46(); 

                }
                break;
            case 13 :
                // dee.g:1:62: T47
                {
                mT47(); 

                }
                break;
            case 14 :
                // dee.g:1:66: T48
                {
                mT48(); 

                }
                break;
            case 15 :
                // dee.g:1:70: T49
                {
                mT49(); 

                }
                break;
            case 16 :
                // dee.g:1:74: T50
                {
                mT50(); 

                }
                break;
            case 17 :
                // dee.g:1:78: T51
                {
                mT51(); 

                }
                break;
            case 18 :
                // dee.g:1:82: T52
                {
                mT52(); 

                }
                break;
            case 19 :
                // dee.g:1:86: T53
                {
                mT53(); 

                }
                break;
            case 20 :
                // dee.g:1:90: T54
                {
                mT54(); 

                }
                break;
            case 21 :
                // dee.g:1:94: T55
                {
                mT55(); 

                }
                break;
            case 22 :
                // dee.g:1:98: T56
                {
                mT56(); 

                }
                break;
            case 23 :
                // dee.g:1:102: T57
                {
                mT57(); 

                }
                break;
            case 24 :
                // dee.g:1:106: T58
                {
                mT58(); 

                }
                break;
            case 25 :
                // dee.g:1:110: T59
                {
                mT59(); 

                }
                break;
            case 26 :
                // dee.g:1:114: T60
                {
                mT60(); 

                }
                break;
            case 27 :
                // dee.g:1:118: T61
                {
                mT61(); 

                }
                break;
            case 28 :
                // dee.g:1:122: T62
                {
                mT62(); 

                }
                break;
            case 29 :
                // dee.g:1:126: T63
                {
                mT63(); 

                }
                break;
            case 30 :
                // dee.g:1:130: T64
                {
                mT64(); 

                }
                break;
            case 31 :
                // dee.g:1:134: T65
                {
                mT65(); 

                }
                break;
            case 32 :
                // dee.g:1:138: T66
                {
                mT66(); 

                }
                break;
            case 33 :
                // dee.g:1:142: T67
                {
                mT67(); 

                }
                break;
            case 34 :
                // dee.g:1:146: T68
                {
                mT68(); 

                }
                break;
            case 35 :
                // dee.g:1:150: T69
                {
                mT69(); 

                }
                break;
            case 36 :
                // dee.g:1:154: T70
                {
                mT70(); 

                }
                break;
            case 37 :
                // dee.g:1:158: T71
                {
                mT71(); 

                }
                break;
            case 38 :
                // dee.g:1:162: T72
                {
                mT72(); 

                }
                break;
            case 39 :
                // dee.g:1:166: T73
                {
                mT73(); 

                }
                break;
            case 40 :
                // dee.g:1:170: T74
                {
                mT74(); 

                }
                break;
            case 41 :
                // dee.g:1:174: T75
                {
                mT75(); 

                }
                break;
            case 42 :
                // dee.g:1:178: T76
                {
                mT76(); 

                }
                break;
            case 43 :
                // dee.g:1:182: T77
                {
                mT77(); 

                }
                break;
            case 44 :
                // dee.g:1:186: T78
                {
                mT78(); 

                }
                break;
            case 45 :
                // dee.g:1:190: T79
                {
                mT79(); 

                }
                break;
            case 46 :
                // dee.g:1:194: T80
                {
                mT80(); 

                }
                break;
            case 47 :
                // dee.g:1:198: T81
                {
                mT81(); 

                }
                break;
            case 48 :
                // dee.g:1:202: T82
                {
                mT82(); 

                }
                break;
            case 49 :
                // dee.g:1:206: T83
                {
                mT83(); 

                }
                break;
            case 50 :
                // dee.g:1:210: T84
                {
                mT84(); 

                }
                break;
            case 51 :
                // dee.g:1:214: T85
                {
                mT85(); 

                }
                break;
            case 52 :
                // dee.g:1:218: T86
                {
                mT86(); 

                }
                break;
            case 53 :
                // dee.g:1:222: T87
                {
                mT87(); 

                }
                break;
            case 54 :
                // dee.g:1:226: T88
                {
                mT88(); 

                }
                break;
            case 55 :
                // dee.g:1:230: T89
                {
                mT89(); 

                }
                break;
            case 56 :
                // dee.g:1:234: T90
                {
                mT90(); 

                }
                break;
            case 57 :
                // dee.g:1:238: T91
                {
                mT91(); 

                }
                break;
            case 58 :
                // dee.g:1:242: T92
                {
                mT92(); 

                }
                break;
            case 59 :
                // dee.g:1:246: T93
                {
                mT93(); 

                }
                break;
            case 60 :
                // dee.g:1:250: T94
                {
                mT94(); 

                }
                break;
            case 61 :
                // dee.g:1:254: T95
                {
                mT95(); 

                }
                break;
            case 62 :
                // dee.g:1:258: T96
                {
                mT96(); 

                }
                break;
            case 63 :
                // dee.g:1:262: T97
                {
                mT97(); 

                }
                break;
            case 64 :
                // dee.g:1:266: T98
                {
                mT98(); 

                }
                break;
            case 65 :
                // dee.g:1:270: T99
                {
                mT99(); 

                }
                break;
            case 66 :
                // dee.g:1:274: T100
                {
                mT100(); 

                }
                break;
            case 67 :
                // dee.g:1:279: T101
                {
                mT101(); 

                }
                break;
            case 68 :
                // dee.g:1:284: WHITESPACE
                {
                mWHITESPACE(); 

                }
                break;
            case 69 :
                // dee.g:1:295: LINE_COMMENT
                {
                mLINE_COMMENT(); 

                }
                break;
            case 70 :
                // dee.g:1:308: MULTILINE_COMMENT
                {
                mMULTILINE_COMMENT(); 

                }
                break;
            case 71 :
                // dee.g:1:326: NESTING_COMMENT
                {
                mNESTING_COMMENT(); 

                }
                break;
            case 72 :
                // dee.g:1:342: IDENT
                {
                mIDENT(); 

                }
                break;
            case 73 :
                // dee.g:1:348: CHARLITERAL
                {
                mCHARLITERAL(); 

                }
                break;
            case 74 :
                // dee.g:1:360: STRING
                {
                mSTRING(); 

                }
                break;
            case 75 :
                // dee.g:1:367: INTLITERAL
                {
                mINTLITERAL(); 

                }
                break;
            case 76 :
                // dee.g:1:378: FLOATLITERAL
                {
                mFLOATLITERAL(); 

                }
                break;

        }

    }


    protected DFA32 dfa32 = new DFA32(this);
    protected DFA35 dfa35 = new DFA35(this);
    public static final String DFA32_eotS =
        "\1\uffff\1\3\3\uffff";
    public static final String DFA32_eofS =
        "\5\uffff";
    public static final String DFA32_minS =
        "\2\56\3\uffff";
    public static final String DFA32_maxS =
        "\2\137\3\uffff";
    public static final String DFA32_acceptS =
        "\2\uffff\1\2\1\3\1\1";
    public static final String DFA32_specialS =
        "\5\uffff}>";
    public static final String[] DFA32_transition = {
        "\1\2\1\uffff\12\1\45\uffff\1\1",
        "\1\4\1\uffff\12\1\45\uffff\1\1",
        "",
        "",
        ""
    };

    class DFA32 extends DFA {
        public DFA32(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 32;
            this.eot = DFA.unpackEncodedString(DFA32_eotS);
            this.eof = DFA.unpackEncodedString(DFA32_eofS);
            this.min = DFA.unpackEncodedStringToUnsignedChars(DFA32_minS);
            this.max = DFA.unpackEncodedStringToUnsignedChars(DFA32_maxS);
            this.accept = DFA.unpackEncodedString(DFA32_acceptS);
            this.special = DFA.unpackEncodedString(DFA32_specialS);
            int numStates = DFA32_transition.length;
            this.transition = new short[numStates][];
            for (int i=0; i<numStates; i++) {
                transition[i] = DFA.unpackEncodedString(DFA32_transition[i]);
            }
        }
        public String getDescription() {
            return "91:10: fragment Float : ( DecimalDigits '.' ( DecimalDigits ( DecimalExponent )? )? | '.' DecimalDigits ( DecimalExponent )? | DecimalDigits ( DecimalExponent )? );";
        }
    }
    public static final String DFA35_eotS =
        "\4\uffff\1\57\1\53\1\uffff\2\53\3\uffff\2\53\2\uffff\1\75\2\53\2"+
        "\uffff\11\53\1\130\1\uffff\4\53\1\uffff\1\53\1\uffff\1\53\1\uffff"+
        "\2\147\5\uffff\10\53\1\167\4\53\2\uffff\30\53\2\uffff\15\53\2\uffff"+
        "\5\147\11\53\1\u00a8\1\uffff\43\53\1\u00cc\14\53\1\uffff\10\53\1"+
        "\u00e2\1\u00e3\1\u00e4\1\u00e5\3\53\1\u00e9\1\u00ea\7\53\1\u00f2"+
        "\2\53\1\u00f5\5\53\1\u00fb\1\53\1\uffff\6\53\1\u0103\3\53\1\u0107"+
        "\1\u0108\11\53\4\uffff\1\53\1\u0113\1\u0114\2\uffff\1\53\1\u0116"+
        "\1\u0117\3\53\1\u011b\1\uffff\1\u011c\1\53\1\uffff\1\u011e\1\53"+
        "\1\u0120\2\53\1\uffff\1\u0123\3\53\1\u0127\1\u0128\1\53\1\uffff"+
        "\1\u012a\1\53\1\u012c\2\uffff\3\53\1\u0130\1\u0131\1\u0132\2\53"+
        "\1\u0135\1\u0136\2\uffff\1\53\2\uffff\1\u0138\2\53\2\uffff\1\53"+
        "\1\uffff\1\u013c\1\uffff\2\53\1\uffff\3\53\2\uffff\1\53\1\uffff"+
        "\1\u0143\1\uffff\1\u0144\1\u0145\1\53\3\uffff\1\53\1\u0148\2\uffff"+
        "\1\53\1\uffff\2\53\1\u014c\1\uffff\4\53\1\uffff\1\53\3\uffff\2\53"+
        "\1\uffff\1\u0154\1\u0155\1\53\1\uffff\1\u0157\1\u0158\1\u0159\2"+
        "\53\1\u015c\3\uffff\1\53\3\uffff\2\53\1\uffff\1\u0160\1\u0161\1"+
        "\53\2\uffff\1\u0163\1\uffff";
    public static final String DFA35_eofS =
        "\u0164\uffff";
    public static final String DFA35_minS =
        "\1\11\3\uffff\1\52\1\157\1\uffff\1\150\1\144\3\uffff\1\141\1\170"+
        "\2\uffff\1\60\1\105\1\171\2\uffff\2\157\1\142\1\141\1\151\1\143"+
        "\1\42\1\144\1\143\1\135\1\uffff\1\142\1\165\1\130\1\123\1\uffff"+
        "\1\42\1\uffff\1\56\1\uffff\2\56\5\uffff\1\144\1\141\1\156\1\157"+
        "\1\160\1\157\1\154\1\145\1\60\1\143\1\151\1\142\1\160\2\uffff\1"+
        "\115\1\160\1\151\1\157\1\164\1\156\1\150\1\171\1\157\1\156\1\172"+
        "\2\156\1\157\1\165\1\154\1\150\1\141\1\156\1\157\1\141\1\145\1\154"+
        "\1\150\2\uffff\1\163\1\144\1\164\1\151\1\164\1\145\1\120\1\123\2"+
        "\56\2\53\1\151\2\uffff\1\56\1\151\3\56\1\165\1\164\1\143\1\162\1"+
        "\157\1\165\1\157\1\141\1\165\1\60\1\uffff\1\153\1\166\1\164\1\154"+
        "\1\157\1\145\1\120\1\145\1\144\1\154\1\145\1\164\1\157\1\164\1\156"+
        "\1\147\1\171\1\143\2\141\1\142\1\145\1\162\1\141\1\154\1\163\1\165"+
        "\1\162\1\141\1\157\1\141\1\164\1\146\1\157\1\141\1\60\1\162\1\122"+
        "\1\111\1\154\1\151\1\150\1\164\1\162\1\142\1\141\1\154\1\164\1\uffff"+
        "\2\141\1\145\1\151\2\162\1\114\1\144\4\60\1\162\1\145\1\147\2\60"+
        "\1\164\1\154\1\164\1\154\1\147\1\145\1\162\1\60\1\164\1\142\1\60"+
        "\1\154\1\141\2\162\1\141\1\60\1\163\1\uffff\1\162\1\105\1\107\1"+
        "\145\1\143\1\162\1\60\1\164\1\154\1\164\2\60\1\147\1\164\2\143\1"+
        "\164\1\156\1\101\1\145\1\146\4\uffff\1\164\2\60\2\uffff\1\151\2"+
        "\60\1\145\1\141\1\143\1\60\1\uffff\1\60\1\154\1\uffff\1\60\1\164"+
        "\1\60\1\141\1\147\1\uffff\1\60\1\151\1\123\1\116\2\60\1\157\1\uffff"+
        "\1\60\1\145\1\60\2\uffff\2\145\1\164\3\60\1\124\1\146\2\60\2\uffff"+
        "\1\157\2\uffff\1\60\1\164\1\141\2\uffff\1\145\1\uffff\1\60\1\uffff"+
        "\1\143\1\141\1\uffff\1\144\1\123\1\40\2\uffff\1\156\1\uffff\1\60"+
        "\1\uffff\2\60\1\145\3\uffff\1\105\1\60\2\uffff\1\156\1\uffff\1\145"+
        "\1\164\1\60\1\uffff\1\164\1\144\1\145\1\111\1\uffff\1\151\3\uffff"+
        "\1\144\1\40\1\uffff\2\60\1\145\1\uffff\3\60\1\117\1\172\1\60\3\uffff"+
        "\1\144\3\uffff\1\116\1\145\1\uffff\2\60\1\144\2\uffff\1\60\1\uffff";
    public static final String DFA35_maxS =
        "\1\175\3\uffff\1\57\1\157\1\uffff\1\171\1\162\3\uffff\1\165\1\170"+
        "\2\uffff\1\137\1\105\1\171\2\uffff\1\157\1\171\1\163\1\157\1\165"+
        "\1\157\1\145\1\162\1\143\1\135\1\uffff\1\165\1\166\1\130\1\123\1"+
        "\uffff\1\42\1\uffff\1\151\1\uffff\2\151\5\uffff\1\144\1\141\1\156"+
        "\1\157\1\160\1\157\1\154\1\145\1\172\1\143\1\157\1\142\1\164\2\uffff"+
        "\1\115\1\160\1\151\1\157\1\164\1\156\1\150\1\171\1\157\1\156\1\172"+
        "\2\156\1\157\1\165\1\160\1\150\1\141\1\156\1\157\1\141\1\145\1\154"+
        "\1\150\2\uffff\1\163\1\144\1\164\1\151\1\164\1\145\1\120\1\123\5"+
        "\151\2\uffff\5\151\1\165\1\164\1\143\1\162\1\157\1\165\1\157\1\141"+
        "\1\165\1\172\1\uffff\1\153\1\166\1\164\1\154\1\157\1\145\1\120\1"+
        "\145\1\144\1\154\1\145\1\164\1\157\1\164\1\156\1\147\1\171\1\143"+
        "\2\141\1\142\1\145\1\162\1\141\1\154\1\163\1\165\1\162\1\141\1\157"+
        "\1\141\1\164\1\146\1\157\1\141\1\172\1\162\1\122\1\111\1\154\1\151"+
        "\1\150\1\164\1\162\1\142\1\141\1\154\1\164\1\uffff\2\141\1\145\1"+
        "\151\2\162\1\114\1\157\4\172\1\162\1\145\1\147\2\172\1\164\1\154"+
        "\1\164\1\154\1\147\1\145\1\162\1\172\1\164\1\142\1\172\1\154\1\141"+
        "\2\162\1\141\1\172\1\163\1\uffff\1\162\1\105\1\107\1\145\1\143\1"+
        "\162\1\172\1\164\1\154\1\164\2\172\1\147\1\164\2\143\1\164\1\156"+
        "\1\101\1\145\1\146\4\uffff\1\164\2\172\2\uffff\1\151\2\172\1\145"+
        "\1\141\1\143\1\172\1\uffff\1\172\1\154\1\uffff\1\172\1\164\1\172"+
        "\1\141\1\147\1\uffff\1\172\1\151\1\123\1\116\2\172\1\157\1\uffff"+
        "\1\172\1\145\1\172\2\uffff\2\145\1\164\3\172\1\124\1\146\2\172\2"+
        "\uffff\1\157\2\uffff\1\172\1\164\1\141\2\uffff\1\145\1\uffff\1\172"+
        "\1\uffff\1\143\1\141\1\uffff\1\144\1\123\1\40\2\uffff\1\156\1\uffff"+
        "\1\172\1\uffff\2\172\1\145\3\uffff\1\105\1\172\2\uffff\1\156\1\uffff"+
        "\1\145\1\164\1\172\1\uffff\1\164\1\144\1\145\1\111\1\uffff\1\151"+
        "\3\uffff\1\144\1\40\1\uffff\2\172\1\145\1\uffff\3\172\1\117\2\172"+
        "\3\uffff\1\144\3\uffff\1\116\1\145\1\uffff\2\172\1\144\2\uffff\1"+
        "\172\1\uffff";
    public static final String DFA35_acceptS =
        "\1\uffff\1\1\1\2\1\3\2\uffff\1\6\2\uffff\1\11\1\12\1\13\2\uffff"+
        "\1\21\1\22\3\uffff\1\26\1\27\12\uffff\1\60\4\uffff\1\104\1\uffff"+
        "\1\111\1\uffff\1\112\2\uffff\1\110\1\106\1\107\1\105\1\4\15\uffff"+
        "\1\23\1\114\30\uffff\1\56\1\57\15\uffff\1\110\1\113\17\uffff\1\76"+
        "\60\uffff\1\36\43\uffff\1\77\25\uffff\1\30\1\31\1\32\1\37\3\uffff"+
        "\1\40\1\101\7\uffff\1\44\2\uffff\1\53\5\uffff\1\65\7\uffff\1\34"+
        "\3\uffff\1\47\1\100\12\uffff\1\33\1\41\1\uffff\1\72\1\42\3\uffff"+
        "\1\55\1\67\1\uffff\1\52\1\uffff\1\54\2\uffff\1\64\3\uffff\1\5\1"+
        "\7\1\uffff\1\10\1\uffff\1\45\3\uffff\1\17\1\20\1\71\2\uffff\1\25"+
        "\1\35\1\uffff\1\43\3\uffff\1\50\4\uffff\1\103\1\uffff\1\46\1\15"+
        "\1\14\2\uffff\1\63\3\uffff\1\51\6\uffff\1\24\1\62\1\61\1\uffff\1"+
        "\66\1\75\1\73\2\uffff\1\16\3\uffff\1\70\1\102\1\uffff\1\74";
    public static final String DFA35_specialS =
        "\u0164\uffff}>";
    public static final String[] DFA35_transition = {
        "\5\44\22\uffff\1\44\1\uffff\1\50\4\uffff\1\46\1\23\1\24\1\3\1\1"+
        "\1\11\1\2\1\20\1\4\1\51\11\52\1\13\1\6\1\uffff\1\12\3\uffff\1\43"+
        "\3\53\1\42\16\53\1\21\6\53\1\36\1\uffff\1\37\1\uffff\1\47\1\50\1"+
        "\40\1\26\1\34\1\32\1\15\1\31\2\53\1\10\2\53\1\30\1\5\1\53\1\41\1"+
        "\14\1\53\1\33\1\7\1\22\1\27\1\25\1\35\1\45\2\53\1\16\1\uffff\1\17",
        "",
        "",
        "",
        "\1\54\1\55\3\uffff\1\56",
        "\1\60",
        "",
        "\1\63\13\uffff\1\61\4\uffff\1\62",
        "\1\65\1\uffff\1\66\6\uffff\1\64\1\70\3\uffff\1\67",
        "",
        "",
        "",
        "\1\71\20\uffff\1\72\2\uffff\1\73",
        "\1\74",
        "",
        "",
        "\12\76\45\uffff\1\76",
        "\1\77",
        "\1\100",
        "",
        "",
        "\1\101",
        "\1\102\11\uffff\1\103",
        "\1\106\6\uffff\1\104\2\uffff\1\107\6\uffff\1\105",
        "\1\111\15\uffff\1\110",
        "\1\113\2\uffff\1\114\10\uffff\1\112",
        "\1\117\1\uffff\1\116\11\uffff\1\115",
        "\1\50\102\uffff\1\120",
        "\1\122\1\uffff\1\125\1\uffff\1\123\6\uffff\1\121\2\uffff\1\124",
        "\1\126",
        "\1\127",
        "",
        "\1\131\11\uffff\1\134\6\uffff\1\132\1\uffff\1\133",
        "\1\135\1\136",
        "\1\137",
        "\1\140",
        "",
        "\1\50",
        "",
        "\1\76\1\uffff\12\142\13\uffff\1\144\1\145\5\uffff\1\145\22\uffff"+
        "\1\141\5\uffff\1\143\1\145\2\uffff\1\146",
        "",
        "\1\76\1\uffff\10\150\2\76\13\uffff\2\76\5\uffff\1\151\22\uffff\1"+
        "\152\5\uffff\2\76\2\uffff\1\76",
        "\1\76\1\uffff\12\153\13\uffff\2\76\5\uffff\1\151\22\uffff\1\154"+
        "\5\uffff\2\76\2\uffff\1\76",
        "",
        "",
        "",
        "",
        "",
        "\1\155",
        "\1\156",
        "\1\157",
        "\1\160",
        "\1\161",
        "\1\162",
        "\1\163",
        "\1\164",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\16\53\1\165\4\53\1\166"+
        "\6\53",
        "\1\170",
        "\1\171\5\uffff\1\172",
        "\1\173",
        "\1\174\3\uffff\1\175",
        "",
        "",
        "\1\176",
        "\1\177",
        "\1\u0080",
        "\1\u0081",
        "\1\u0082",
        "\1\u0083",
        "\1\u0084",
        "\1\u0085",
        "\1\u0086",
        "\1\u0087",
        "\1\u0088",
        "\1\u0089",
        "\1\u008a",
        "\1\u008b",
        "\1\u008c",
        "\1\u008d\3\uffff\1\u008e",
        "\1\u008f",
        "\1\u0090",
        "\1\u0091",
        "\1\u0092",
        "\1\u0093",
        "\1\u0094",
        "\1\u0095",
        "\1\u0096",
        "",
        "",
        "\1\u0097",
        "\1\u0098",
        "\1\u0099",
        "\1\u009a",
        "\1\u009b",
        "\1\u009c",
        "\1\u009d",
        "\1\u009e",
        "\1\76\1\uffff\12\142\13\uffff\1\144\1\145\5\uffff\1\145\22\uffff"+
        "\1\141\5\uffff\1\143\1\145\2\uffff\1\146",
        "\1\76\1\uffff\12\142\13\uffff\1\144\1\145\5\uffff\1\145\22\uffff"+
        "\1\141\5\uffff\1\143\1\145\2\uffff\1\146",
        "\1\76\1\uffff\1\76\30\uffff\1\145\5\uffff\1\145\31\uffff\1\145\2"+
        "\uffff\1\146",
        "\1\76\1\uffff\1\76\30\uffff\1\145\5\uffff\1\145\31\uffff\1\145\2"+
        "\uffff\1\146",
        "\1\146",
        "",
        "",
        "\1\76\1\uffff\10\150\2\76\13\uffff\2\76\5\uffff\1\151\22\uffff\1"+
        "\152\5\uffff\2\76\2\uffff\1\76",
        "\1\76",
        "\1\76\1\uffff\10\150\2\76\13\uffff\2\76\5\uffff\1\151\22\uffff\1"+
        "\152\5\uffff\2\76\2\uffff\1\76",
        "\1\76\1\uffff\12\153\13\uffff\2\76\5\uffff\1\151\22\uffff\1\154"+
        "\5\uffff\2\76\2\uffff\1\76",
        "\1\76\1\uffff\12\153\13\uffff\2\76\5\uffff\1\151\22\uffff\1\154"+
        "\5\uffff\2\76\2\uffff\1\76",
        "\1\u009f",
        "\1\u00a0",
        "\1\u00a1",
        "\1\u00a2",
        "\1\u00a3",
        "\1\u00a4",
        "\1\u00a5",
        "\1\u00a6",
        "\1\u00a7",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "",
        "\1\u00a9",
        "\1\u00aa",
        "\1\u00ab",
        "\1\u00ac",
        "\1\u00ad",
        "\1\u00ae",
        "\1\u00af",
        "\1\u00b0",
        "\1\u00b1",
        "\1\u00b2",
        "\1\u00b3",
        "\1\u00b4",
        "\1\u00b5",
        "\1\u00b6",
        "\1\u00b7",
        "\1\u00b8",
        "\1\u00b9",
        "\1\u00ba",
        "\1\u00bb",
        "\1\u00bc",
        "\1\u00bd",
        "\1\u00be",
        "\1\u00bf",
        "\1\u00c0",
        "\1\u00c1",
        "\1\u00c2",
        "\1\u00c3",
        "\1\u00c4",
        "\1\u00c5",
        "\1\u00c6",
        "\1\u00c7",
        "\1\u00c8",
        "\1\u00c9",
        "\1\u00ca",
        "\1\u00cb",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\1\u00cd",
        "\1\u00ce",
        "\1\u00cf",
        "\1\u00d0",
        "\1\u00d1",
        "\1\u00d2",
        "\1\u00d3",
        "\1\u00d4",
        "\1\u00d5",
        "\1\u00d6",
        "\1\u00d7",
        "\1\u00d8",
        "",
        "\1\u00d9",
        "\1\u00da",
        "\1\u00db",
        "\1\u00dc",
        "\1\u00dd",
        "\1\u00de",
        "\1\u00df",
        "\1\u00e0\12\uffff\1\u00e1",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\1\u00e6",
        "\1\u00e7",
        "\1\u00e8",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\1\u00eb",
        "\1\u00ec",
        "\1\u00ed",
        "\1\u00ee",
        "\1\u00ef",
        "\1\u00f0",
        "\1\u00f1",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\1\u00f3",
        "\1\u00f4",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\1\u00f6",
        "\1\u00f7",
        "\1\u00f8",
        "\1\u00f9",
        "\1\u00fa",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\1\u00fc",
        "",
        "\1\u00fd",
        "\1\u00fe",
        "\1\u00ff",
        "\1\u0100",
        "\1\u0101",
        "\1\u0102",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\1\u0104",
        "\1\u0105",
        "\1\u0106",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\1\u0109",
        "\1\u010a",
        "\1\u010b",
        "\1\u010c",
        "\1\u010d",
        "\1\u010e",
        "\1\u010f",
        "\1\u0110",
        "\1\u0111",
        "",
        "",
        "",
        "",
        "\1\u0112",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "",
        "",
        "\1\u0115",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\1\u0118",
        "\1\u0119",
        "\1\u011a",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\1\u011d",
        "",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\1\u011f",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\1\u0121",
        "\1\u0122",
        "",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\1\u0124",
        "\1\u0125",
        "\1\u0126",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\1\u0129",
        "",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\1\u012b",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "",
        "",
        "\1\u012d",
        "\1\u012e",
        "\1\u012f",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\1\u0133",
        "\1\u0134",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "",
        "",
        "\1\u0137",
        "",
        "",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\1\u0139",
        "\1\u013a",
        "",
        "",
        "\1\u013b",
        "",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "",
        "\1\u013d",
        "\1\u013e",
        "",
        "\1\u013f",
        "\1\u0140",
        "\1\u0141",
        "",
        "",
        "\1\u0142",
        "",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\1\u0146",
        "",
        "",
        "",
        "\1\u0147",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "",
        "",
        "\1\u0149",
        "",
        "\1\u014a",
        "\1\u014b",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "",
        "\1\u014d",
        "\1\u014e",
        "\1\u014f",
        "\1\u0150",
        "",
        "\1\u0151",
        "",
        "",
        "",
        "\1\u0152",
        "\1\u0153",
        "",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\1\u0156",
        "",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\1\u015a",
        "\1\u015b",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "",
        "",
        "",
        "\1\u015d",
        "",
        "",
        "",
        "\1\u015e",
        "\1\u015f",
        "",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        "\1\u0162",
        "",
        "",
        "\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
        ""
    };

    class DFA35 extends DFA {
        public DFA35(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 35;
            this.eot = DFA.unpackEncodedString(DFA35_eotS);
            this.eof = DFA.unpackEncodedString(DFA35_eofS);
            this.min = DFA.unpackEncodedStringToUnsignedChars(DFA35_minS);
            this.max = DFA.unpackEncodedStringToUnsignedChars(DFA35_maxS);
            this.accept = DFA.unpackEncodedString(DFA35_acceptS);
            this.special = DFA.unpackEncodedString(DFA35_specialS);
            int numStates = DFA35_transition.length;
            this.transition = new short[numStates][];
            for (int i=0; i<numStates; i++) {
                transition[i] = DFA.unpackEncodedString(DFA35_transition[i]);
            }
        }
        public String getDescription() {
            return "1:1: Tokens : ( PLUS | MINUS | MULT | DIV | T39 | T40 | T41 | T42 | T43 | T44 | T45 | T46 | T47 | T48 | T49 | T50 | T51 | T52 | T53 | T54 | T55 | T56 | T57 | T58 | T59 | T60 | T61 | T62 | T63 | T64 | T65 | T66 | T67 | T68 | T69 | T70 | T71 | T72 | T73 | T74 | T75 | T76 | T77 | T78 | T79 | T80 | T81 | T82 | T83 | T84 | T85 | T86 | T87 | T88 | T89 | T90 | T91 | T92 | T93 | T94 | T95 | T96 | T97 | T98 | T99 | T100 | T101 | WHITESPACE | LINE_COMMENT | MULTILINE_COMMENT | NESTING_COMMENT | IDENT | CHARLITERAL | STRING | INTLITERAL | FLOATLITERAL );";
        }
    }
 

}
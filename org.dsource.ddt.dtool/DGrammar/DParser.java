// $ANTLR 3.0b4 dee.g 2006-12-20 12:39:38
 
/* PHOENIX test code 7  */


import org.antlr.runtime.BitSet;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.TreeAdaptor;

public class DParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PLUS", "MINUS", "MULT", "DIV", "WHITESPACE", "LINE_COMMENT", "MULTILINE_COMMENT", "NESTING_COMMENT", "IdStartChar", "IDENT", "EscapeChar", "CHARLITERAL", "OctalEscape", "HexEscape", "HexDigit", "RAW_STRING", "RAW_STRING_ALT", "DQ_STRING", "StringPostfix", "STRING", "Integer", "IntSuffix", "INTLITERAL", "Decimal", "Binary", "Octal", "Hexadecimal", "DecimalDigit", "OctalDigit", "Float", "FloatTypeSuffix", "ImaginarySuffix", "FLOATLITERAL", "DecimalDigits", "DecimalExponent", "'module'", "';'", "'static'", "'import'", "','", "'='", "':'", "'private'", "'package'", "'protected'", "'public'", "'export'", "'{'", "'}'", "'.'", "'TEMPLATE INSTANCE'", "'typeof'", "'('", "')'", "'void'", "'bool'", "'byte'", "'ubyte'", "'short'", "'ushort'", "'int'", "'uint'", "'long'", "'ulong'", "'float'", "'double'", "'real'", "'ifloat'", "'idouble'", "'ireal'", "'cfloat'", "'cdouble'", "'creal'", "'char'", "'wchar'", "'dchar'", "'[]'", "'['", "']'", "'delegate'", "'function'", "'typedef'", "'alias'", "'auto'", "'abstract'", "'const'", "'deprecated'", "'extern'", "'final'", "'override'", "'synchronized'", "'asdfagad'", "'in'", "'out'", "'inout'", "'lazy'", "'EXPRESSION'", "'ASSIGN  EXPRESSION'"
    };
    public static final int Octal=29;
    public static final int MINUS=5;
    public static final int StringPostfix=22;
    public static final int IDENT=13;
    public static final int HexDigit=18;
    public static final int NESTING_COMMENT=11;
    public static final int DecimalDigit=31;
    public static final int STRING=23;
    public static final int MULTILINE_COMMENT=10;
    public static final int OctalDigit=32;
    public static final int FLOATLITERAL=36;
    public static final int Hexadecimal=30;
    public static final int LINE_COMMENT=9;
    public static final int IntSuffix=25;
    public static final int Decimal=27;
    public static final int RAW_STRING_ALT=20;
    public static final int WHITESPACE=8;
    public static final int HexEscape=17;
    public static final int INTLITERAL=26;
    public static final int ImaginarySuffix=35;
    public static final int Binary=28;
    public static final int EOF=-1;
    public static final int Float=33;
    public static final int IdStartChar=12;
    public static final int Integer=24;
    public static final int RAW_STRING=19;
    public static final int CHARLITERAL=15;
    public static final int OctalEscape=16;
    public static final int DIV=7;
    public static final int DecimalExponent=38;
    public static final int PLUS=4;
    public static final int MULT=6;
    public static final int DecimalDigits=37;
    public static final int FloatTypeSuffix=34;
    public static final int DQ_STRING=21;
    public static final int EscapeChar=14;

        public DParser(TokenStream input) {
            super(input);
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "dee.g"; }


    public static class dmodule_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start dmodule
    // dee.g:111:1: dmodule : ( moduledeclaration )? decldefs EOF ;
    public dmodule_return dmodule() throws RecognitionException {   
        dmodule_return retval = new dmodule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOF3=null;
        moduledeclaration_return moduledeclaration1 = null;

        decldefs_return decldefs2 = null;


        Object EOF3_tree=null;

        try {
            // dee.g:111:11: ( ( moduledeclaration )? decldefs EOF )
            // dee.g:111:11: ( moduledeclaration )? decldefs EOF
            {
            root_0 = (Object)adaptor.nil();

            // dee.g:111:11: ( moduledeclaration )?
            int alt1=2;
            int LA1_0 = input.LA(1);
            if ( (LA1_0==39) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // dee.g:111:11: moduledeclaration
                    {
                    Object root_1 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_moduledeclaration_in_dmodule1014);
                    moduledeclaration1=moduledeclaration();
                    _fsp--;

                    adaptor.addChild(root_1, moduledeclaration1.tree);

                    adaptor.addChild(root_0, root_1);

                    }
                    break;

            }

            pushFollow(FOLLOW_decldefs_in_dmodule1017);
            decldefs2=decldefs();
            _fsp--;

            adaptor.addChild(root_0, decldefs2.tree);
            EOF3=(Token)input.LT(1);
            match(input,EOF,FOLLOW_EOF_in_dmodule1019); 
            EOF3_tree = (Object)adaptor.create(EOF3);
            adaptor.addChild(root_0, EOF3_tree);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end dmodule

    public static class moduledeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start moduledeclaration
    // dee.g:113:1: moduledeclaration : 'module' modulename ';' ;
    public moduledeclaration_return moduledeclaration() throws RecognitionException {   
        moduledeclaration_return retval = new moduledeclaration_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal4=null;
        Token char_literal6=null;
        modulename_return modulename5 = null;


        Object string_literal4_tree=null;
        Object char_literal6_tree=null;

        try {
            // dee.g:113:21: ( 'module' modulename ';' )
            // dee.g:113:21: 'module' modulename ';'
            {
            root_0 = (Object)adaptor.nil();

            string_literal4=(Token)input.LT(1);
            match(input,39,FOLLOW_39_in_moduledeclaration1027); 
            string_literal4_tree = (Object)adaptor.create(string_literal4);
            adaptor.addChild(root_0, string_literal4_tree);

            pushFollow(FOLLOW_modulename_in_moduledeclaration1029);
            modulename5=modulename();
            _fsp--;

            adaptor.addChild(root_0, modulename5.tree);
            char_literal6=(Token)input.LT(1);
            match(input,40,FOLLOW_40_in_moduledeclaration1031); 
            char_literal6_tree = (Object)adaptor.create(char_literal6);
            adaptor.addChild(root_0, char_literal6_tree);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end moduledeclaration

    public static class modulename_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start modulename
    // dee.g:114:1: modulename : IDENT ;
    public modulename_return modulename() throws RecognitionException {   
        modulename_return retval = new modulename_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IDENT7=null;

        Object IDENT7_tree=null;

        try {
            // dee.g:114:14: ( IDENT )
            // dee.g:114:14: IDENT
            {
            root_0 = (Object)adaptor.nil();

            IDENT7=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_modulename1039); 
            IDENT7_tree = (Object)adaptor.create(IDENT7);
            adaptor.addChild(root_0, IDENT7_tree);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end modulename

    public static class decldefs_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start decldefs
    // dee.g:117:1: decldefs : ( decldef )* ;
    public decldefs_return decldefs() throws RecognitionException {   
        decldefs_return retval = new decldefs_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        decldef_return decldef8 = null;



        try {
            // dee.g:117:13: ( ( decldef )* )
            // dee.g:117:13: ( decldef )*
            {
            root_0 = (Object)adaptor.nil();

            // dee.g:117:13: ( decldef )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);
                if ( (LA2_0==IDENT||(LA2_0>=41 && LA2_0<=42)||(LA2_0>=53 && LA2_0<=55)||(LA2_0>=58 && LA2_0<=79)||(LA2_0>=85 && LA2_0<=95)) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // dee.g:117:13: decldef
            	    {
            	    Object root_1 = (Object)adaptor.nil();

            	    pushFollow(FOLLOW_decldef_in_decldefs1051);
            	    decldef8=decldef();
            	    _fsp--;

            	    adaptor.addChild(root_1, decldef8.tree);

            	    adaptor.addChild(root_0, root_1);

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end decldefs

    public static class decldef_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start decldef
    // dee.g:119:1: decldef : ( importdeclaration | declaration );
    public decldef_return decldef() throws RecognitionException {   
        decldef_return retval = new decldef_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        importdeclaration_return importdeclaration9 = null;

        declaration_return declaration10 = null;



        try {
            // dee.g:120:5: ( importdeclaration | declaration )
            int alt3=2;
            switch ( input.LA(1) ) {
            case 41:
                int LA3_1 = input.LA(2);
                if ( (LA3_1==IDENT||LA3_1==41||(LA3_1>=53 && LA3_1<=55)||(LA3_1>=58 && LA3_1<=79)||(LA3_1>=87 && LA3_1<=95)) ) {
                    alt3=2;
                }
                else if ( (LA3_1==42) ) {
                    alt3=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("119:1: decldef : ( importdeclaration | declaration );", 3, 1, input);

                    throw nvae;
                }
                break;
            case 42:
                alt3=1;
                break;
            case IDENT:
            case 53:
            case 54:
            case 55:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
                alt3=2;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("119:1: decldef : ( importdeclaration | declaration );", 3, 0, input);

                throw nvae;
            }

            switch (alt3) {
                case 1 :
                    // dee.g:120:5: importdeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_importdeclaration_in_decldef1063);
                    importdeclaration9=importdeclaration();
                    _fsp--;

                    adaptor.addChild(root_0, importdeclaration9.tree);

                    }
                    break;
                case 2 :
                    // dee.g:121:4: declaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_declaration_in_decldef1068);
                    declaration10=declaration();
                    _fsp--;

                    adaptor.addChild(root_0, declaration10.tree);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end decldef

    public static class importdeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start importdeclaration
    // dee.g:140:1: importdeclaration : ( 'static' )? 'import' importlist ;
    public importdeclaration_return importdeclaration() throws RecognitionException {   
        importdeclaration_return retval = new importdeclaration_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal11=null;
        Token string_literal12=null;
        importlist_return importlist13 = null;


        Object string_literal11_tree=null;
        Object string_literal12_tree=null;

        try {
            // dee.g:140:21: ( ( 'static' )? 'import' importlist )
            // dee.g:140:21: ( 'static' )? 'import' importlist
            {
            root_0 = (Object)adaptor.nil();

            // dee.g:140:21: ( 'static' )?
            int alt4=2;
            int LA4_0 = input.LA(1);
            if ( (LA4_0==41) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // dee.g:140:21: 'static'
                    {
                    Object root_1 = (Object)adaptor.nil();

                    string_literal11=(Token)input.LT(1);
                    match(input,41,FOLLOW_41_in_importdeclaration1082); 
                    string_literal11_tree = (Object)adaptor.create(string_literal11);
                    adaptor.addChild(root_1, string_literal11_tree);


                    adaptor.addChild(root_0, root_1);

                    }
                    break;

            }

            string_literal12=(Token)input.LT(1);
            match(input,42,FOLLOW_42_in_importdeclaration1085); 
            string_literal12_tree = (Object)adaptor.create(string_literal12);
            adaptor.addChild(root_0, string_literal12_tree);

            pushFollow(FOLLOW_importlist_in_importdeclaration1087);
            importlist13=importlist();
            _fsp--;

            adaptor.addChild(root_0, importlist13.tree);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end importdeclaration

    public static class importlist_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start importlist
    // dee.g:142:1: importlist : ( singleimport ( ',' importlist )? | importbindings );
    public importlist_return importlist() throws RecognitionException {   
        importlist_return retval = new importlist_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal15=null;
        singleimport_return singleimport14 = null;

        importlist_return importlist16 = null;

        importbindings_return importbindings17 = null;


        Object char_literal15_tree=null;

        try {
            // dee.g:143:4: ( singleimport ( ',' importlist )? | importbindings )
            int alt6=2;
            int LA6_0 = input.LA(1);
            if ( (LA6_0==IDENT) ) {
                switch ( input.LA(2) ) {
                case 44:
                    int LA6_2 = input.LA(3);
                    if ( (LA6_2==IDENT) ) {
                        int LA6_5 = input.LA(4);
                        if ( (LA6_5==EOF||LA6_5==IDENT||(LA6_5>=41 && LA6_5<=43)||(LA6_5>=52 && LA6_5<=55)||(LA6_5>=58 && LA6_5<=79)||(LA6_5>=85 && LA6_5<=95)) ) {
                            alt6=1;
                        }
                        else if ( (LA6_5==45) ) {
                            alt6=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("142:1: importlist : ( singleimport ( ',' importlist )? | importbindings );", 6, 5, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("142:1: importlist : ( singleimport ( ',' importlist )? | importbindings );", 6, 2, input);

                        throw nvae;
                    }
                    break;
                case EOF:
                case IDENT:
                case 41:
                case 42:
                case 43:
                case 52:
                case 53:
                case 54:
                case 55:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                case 72:
                case 73:
                case 74:
                case 75:
                case 76:
                case 77:
                case 78:
                case 79:
                case 85:
                case 86:
                case 87:
                case 88:
                case 89:
                case 90:
                case 91:
                case 92:
                case 93:
                case 94:
                case 95:
                    alt6=1;
                    break;
                case 45:
                    alt6=2;
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("142:1: importlist : ( singleimport ( ',' importlist )? | importbindings );", 6, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("142:1: importlist : ( singleimport ( ',' importlist )? | importbindings );", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // dee.g:143:4: singleimport ( ',' importlist )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_singleimport_in_importlist1097);
                    singleimport14=singleimport();
                    _fsp--;

                    adaptor.addChild(root_0, singleimport14.tree);
                    // dee.g:143:17: ( ',' importlist )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);
                    if ( (LA5_0==43) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // dee.g:143:18: ',' importlist
                            {
                            Object root_1 = (Object)adaptor.nil();

                            char_literal15=(Token)input.LT(1);
                            match(input,43,FOLLOW_43_in_importlist1100); 
                            char_literal15_tree = (Object)adaptor.create(char_literal15);
                            adaptor.addChild(root_1, char_literal15_tree);

                            pushFollow(FOLLOW_importlist_in_importlist1102);
                            importlist16=importlist();
                            _fsp--;

                            adaptor.addChild(root_1, importlist16.tree);

                            adaptor.addChild(root_0, root_1);

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // dee.g:144:4: importbindings
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_importbindings_in_importlist1109);
                    importbindings17=importbindings();
                    _fsp--;

                    adaptor.addChild(root_0, importbindings17.tree);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end importlist

    public static class singleimport_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start singleimport
    // dee.g:146:1: singleimport : ( modulename | IDENT '=' modulename );
    public singleimport_return singleimport() throws RecognitionException {   
        singleimport_return retval = new singleimport_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IDENT19=null;
        Token char_literal20=null;
        modulename_return modulename18 = null;

        modulename_return modulename21 = null;


        Object IDENT19_tree=null;
        Object char_literal20_tree=null;

        try {
            // dee.g:147:4: ( modulename | IDENT '=' modulename )
            int alt7=2;
            int LA7_0 = input.LA(1);
            if ( (LA7_0==IDENT) ) {
                int LA7_1 = input.LA(2);
                if ( (LA7_1==44) ) {
                    alt7=2;
                }
                else if ( (LA7_1==EOF||LA7_1==IDENT||(LA7_1>=41 && LA7_1<=43)||LA7_1==45||(LA7_1>=52 && LA7_1<=55)||(LA7_1>=58 && LA7_1<=79)||(LA7_1>=85 && LA7_1<=95)) ) {
                    alt7=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("146:1: singleimport : ( modulename | IDENT '=' modulename );", 7, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("146:1: singleimport : ( modulename | IDENT '=' modulename );", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // dee.g:147:4: modulename
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_modulename_in_singleimport1120);
                    modulename18=modulename();
                    _fsp--;

                    adaptor.addChild(root_0, modulename18.tree);

                    }
                    break;
                case 2 :
                    // dee.g:148:4: IDENT '=' modulename
                    {
                    root_0 = (Object)adaptor.nil();

                    IDENT19=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_singleimport1125); 
                    IDENT19_tree = (Object)adaptor.create(IDENT19);
                    adaptor.addChild(root_0, IDENT19_tree);

                    char_literal20=(Token)input.LT(1);
                    match(input,44,FOLLOW_44_in_singleimport1127); 
                    char_literal20_tree = (Object)adaptor.create(char_literal20);
                    adaptor.addChild(root_0, char_literal20_tree);

                    pushFollow(FOLLOW_modulename_in_singleimport1129);
                    modulename21=modulename();
                    _fsp--;

                    adaptor.addChild(root_0, modulename21.tree);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end singleimport

    public static class importbindings_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start importbindings
    // dee.g:150:1: importbindings : singleimport ':' importbind ( ',' importbind )* ;
    public importbindings_return importbindings() throws RecognitionException {   
        importbindings_return retval = new importbindings_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal23=null;
        Token char_literal25=null;
        singleimport_return singleimport22 = null;

        importbind_return importbind24 = null;

        importbind_return importbind26 = null;


        Object char_literal23_tree=null;
        Object char_literal25_tree=null;

        try {
            // dee.g:151:2: ( singleimport ':' importbind ( ',' importbind )* )
            // dee.g:151:2: singleimport ':' importbind ( ',' importbind )*
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_singleimport_in_importbindings1138);
            singleimport22=singleimport();
            _fsp--;

            adaptor.addChild(root_0, singleimport22.tree);
            char_literal23=(Token)input.LT(1);
            match(input,45,FOLLOW_45_in_importbindings1140); 
            char_literal23_tree = (Object)adaptor.create(char_literal23);
            adaptor.addChild(root_0, char_literal23_tree);

            pushFollow(FOLLOW_importbind_in_importbindings1142);
            importbind24=importbind();
            _fsp--;

            adaptor.addChild(root_0, importbind24.tree);
            // dee.g:151:30: ( ',' importbind )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);
                if ( (LA8_0==43) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // dee.g:151:31: ',' importbind
            	    {
            	    Object root_1 = (Object)adaptor.nil();

            	    char_literal25=(Token)input.LT(1);
            	    match(input,43,FOLLOW_43_in_importbindings1145); 
            	    char_literal25_tree = (Object)adaptor.create(char_literal25);
            	    adaptor.addChild(root_1, char_literal25_tree);

            	    pushFollow(FOLLOW_importbind_in_importbindings1147);
            	    importbind26=importbind();
            	    _fsp--;

            	    adaptor.addChild(root_1, importbind26.tree);

            	    adaptor.addChild(root_0, root_1);

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end importbindings

    public static class importbind_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start importbind
    // dee.g:153:1: importbind : ( IDENT | IDENT '=' IDENT );
    public importbind_return importbind() throws RecognitionException {   
        importbind_return retval = new importbind_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IDENT27=null;
        Token IDENT28=null;
        Token char_literal29=null;
        Token IDENT30=null;

        Object IDENT27_tree=null;
        Object IDENT28_tree=null;
        Object char_literal29_tree=null;
        Object IDENT30_tree=null;

        try {
            // dee.g:154:4: ( IDENT | IDENT '=' IDENT )
            int alt9=2;
            int LA9_0 = input.LA(1);
            if ( (LA9_0==IDENT) ) {
                int LA9_1 = input.LA(2);
                if ( (LA9_1==44) ) {
                    alt9=2;
                }
                else if ( (LA9_1==EOF||LA9_1==IDENT||(LA9_1>=41 && LA9_1<=43)||(LA9_1>=52 && LA9_1<=55)||(LA9_1>=58 && LA9_1<=79)||(LA9_1>=85 && LA9_1<=95)) ) {
                    alt9=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("153:1: importbind : ( IDENT | IDENT '=' IDENT );", 9, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("153:1: importbind : ( IDENT | IDENT '=' IDENT );", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // dee.g:154:4: IDENT
                    {
                    root_0 = (Object)adaptor.nil();

                    IDENT27=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_importbind1160); 
                    IDENT27_tree = (Object)adaptor.create(IDENT27);
                    adaptor.addChild(root_0, IDENT27_tree);


                    }
                    break;
                case 2 :
                    // dee.g:155:4: IDENT '=' IDENT
                    {
                    root_0 = (Object)adaptor.nil();

                    IDENT28=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_importbind1165); 
                    IDENT28_tree = (Object)adaptor.create(IDENT28);
                    adaptor.addChild(root_0, IDENT28_tree);

                    char_literal29=(Token)input.LT(1);
                    match(input,44,FOLLOW_44_in_importbind1167); 
                    char_literal29_tree = (Object)adaptor.create(char_literal29);
                    adaptor.addChild(root_0, char_literal29_tree);

                    IDENT30=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_importbind1169); 
                    IDENT30_tree = (Object)adaptor.create(IDENT30);
                    adaptor.addChild(root_0, IDENT30_tree);


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end importbind

    public static class attributeSpecifier_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start attributeSpecifier
    // dee.g:160:1: attributeSpecifier : ( attribute | attribute declarationBlock );
    public attributeSpecifier_return attributeSpecifier() throws RecognitionException {   
        attributeSpecifier_return retval = new attributeSpecifier_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        attribute_return attribute31 = null;

        attribute_return attribute32 = null;

        declarationBlock_return declarationBlock33 = null;



        try {
            // dee.g:161:7: ( attribute | attribute declarationBlock )
            int alt10=2;
            switch ( input.LA(1) ) {
            case EOF:
                alt10=1;
                break;
            case 46:
                int LA10_2 = input.LA(2);
                if ( (LA10_2==IDENT||(LA10_2>=41 && LA10_2<=42)||LA10_2==51||(LA10_2>=53 && LA10_2<=55)||(LA10_2>=58 && LA10_2<=79)||(LA10_2>=85 && LA10_2<=95)) ) {
                    alt10=2;
                }
                else if ( (LA10_2==EOF) ) {
                    alt10=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("160:1: attributeSpecifier : ( attribute | attribute declarationBlock );", 10, 2, input);

                    throw nvae;
                }
                break;
            case 47:
                int LA10_3 = input.LA(2);
                if ( (LA10_3==IDENT||(LA10_3>=41 && LA10_3<=42)||LA10_3==51||(LA10_3>=53 && LA10_3<=55)||(LA10_3>=58 && LA10_3<=79)||(LA10_3>=85 && LA10_3<=95)) ) {
                    alt10=2;
                }
                else if ( (LA10_3==EOF) ) {
                    alt10=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("160:1: attributeSpecifier : ( attribute | attribute declarationBlock );", 10, 3, input);

                    throw nvae;
                }
                break;
            case 48:
                int LA10_4 = input.LA(2);
                if ( (LA10_4==EOF) ) {
                    alt10=1;
                }
                else if ( (LA10_4==IDENT||(LA10_4>=41 && LA10_4<=42)||LA10_4==51||(LA10_4>=53 && LA10_4<=55)||(LA10_4>=58 && LA10_4<=79)||(LA10_4>=85 && LA10_4<=95)) ) {
                    alt10=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("160:1: attributeSpecifier : ( attribute | attribute declarationBlock );", 10, 4, input);

                    throw nvae;
                }
                break;
            case 49:
                int LA10_5 = input.LA(2);
                if ( (LA10_5==EOF) ) {
                    alt10=1;
                }
                else if ( (LA10_5==IDENT||(LA10_5>=41 && LA10_5<=42)||LA10_5==51||(LA10_5>=53 && LA10_5<=55)||(LA10_5>=58 && LA10_5<=79)||(LA10_5>=85 && LA10_5<=95)) ) {
                    alt10=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("160:1: attributeSpecifier : ( attribute | attribute declarationBlock );", 10, 5, input);

                    throw nvae;
                }
                break;
            case 50:
                int LA10_6 = input.LA(2);
                if ( (LA10_6==EOF) ) {
                    alt10=1;
                }
                else if ( (LA10_6==IDENT||(LA10_6>=41 && LA10_6<=42)||LA10_6==51||(LA10_6>=53 && LA10_6<=55)||(LA10_6>=58 && LA10_6<=79)||(LA10_6>=85 && LA10_6<=95)) ) {
                    alt10=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("160:1: attributeSpecifier : ( attribute | attribute declarationBlock );", 10, 6, input);

                    throw nvae;
                }
                break;
            case IDENT:
            case 41:
            case 42:
            case 51:
            case 53:
            case 54:
            case 55:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
                alt10=2;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("160:1: attributeSpecifier : ( attribute | attribute declarationBlock );", 10, 0, input);

                throw nvae;
            }

            switch (alt10) {
                case 1 :
                    // dee.g:161:7: attribute
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_attribute_in_attributeSpecifier1187);
                    attribute31=attribute();
                    _fsp--;

                    adaptor.addChild(root_0, attribute31.tree);

                    }
                    break;
                case 2 :
                    // dee.g:162:7: attribute declarationBlock
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_attribute_in_attributeSpecifier1196);
                    attribute32=attribute();
                    _fsp--;

                    adaptor.addChild(root_0, attribute32.tree);
                    pushFollow(FOLLOW_declarationBlock_in_attributeSpecifier1198);
                    declarationBlock33=declarationBlock();
                    _fsp--;

                    adaptor.addChild(root_0, declarationBlock33.tree);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end attributeSpecifier

    public static class attribute_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start attribute
    // dee.g:164:1: attribute : ( | 'private' | 'package' | 'protected' | 'public' | 'export' );
    public attribute_return attribute() throws RecognitionException {   
        attribute_return retval = new attribute_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal34=null;
        Token string_literal35=null;
        Token string_literal36=null;
        Token string_literal37=null;
        Token string_literal38=null;

        Object string_literal34_tree=null;
        Object string_literal35_tree=null;
        Object string_literal36_tree=null;
        Object string_literal37_tree=null;
        Object string_literal38_tree=null;

        try {
            // dee.g:169:5: ( | 'private' | 'package' | 'protected' | 'public' | 'export' )
            int alt11=6;
            switch ( input.LA(1) ) {
            case EOF:
            case IDENT:
            case 41:
            case 42:
            case 51:
            case 53:
            case 54:
            case 55:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
                alt11=1;
                break;
            case 46:
                alt11=2;
                break;
            case 47:
                alt11=3;
                break;
            case 48:
                alt11=4;
                break;
            case 49:
                alt11=5;
                break;
            case 50:
                alt11=6;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("164:1: attribute : ( | 'private' | 'package' | 'protected' | 'public' | 'export' );", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // dee.g:169:5: 
                    {
                    root_0 = (Object)adaptor.nil();

                    }
                    break;
                case 2 :
                    // dee.g:169:7: 'private'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal34=(Token)input.LT(1);
                    match(input,46,FOLLOW_46_in_attribute1233); 
                    string_literal34_tree = (Object)adaptor.create(string_literal34);
                    adaptor.addChild(root_0, string_literal34_tree);


                    }
                    break;
                case 3 :
                    // dee.g:169:19: 'package'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal35=(Token)input.LT(1);
                    match(input,47,FOLLOW_47_in_attribute1237); 
                    string_literal35_tree = (Object)adaptor.create(string_literal35);
                    adaptor.addChild(root_0, string_literal35_tree);


                    }
                    break;
                case 4 :
                    // dee.g:169:31: 'protected'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal36=(Token)input.LT(1);
                    match(input,48,FOLLOW_48_in_attribute1241); 
                    string_literal36_tree = (Object)adaptor.create(string_literal36);
                    adaptor.addChild(root_0, string_literal36_tree);


                    }
                    break;
                case 5 :
                    // dee.g:169:45: 'public'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal37=(Token)input.LT(1);
                    match(input,49,FOLLOW_49_in_attribute1245); 
                    string_literal37_tree = (Object)adaptor.create(string_literal37);
                    adaptor.addChild(root_0, string_literal37_tree);


                    }
                    break;
                case 6 :
                    // dee.g:169:56: 'export'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal38=(Token)input.LT(1);
                    match(input,50,FOLLOW_50_in_attribute1249); 
                    string_literal38_tree = (Object)adaptor.create(string_literal38);
                    adaptor.addChild(root_0, string_literal38_tree);


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end attribute

    public static class declarationBlock_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start declarationBlock
    // dee.g:178:1: declarationBlock : ( decldef | '{' decldefs '}' );
    public declarationBlock_return declarationBlock() throws RecognitionException {   
        declarationBlock_return retval = new declarationBlock_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal40=null;
        Token char_literal42=null;
        decldef_return decldef39 = null;

        decldefs_return decldefs41 = null;


        Object char_literal40_tree=null;
        Object char_literal42_tree=null;

        try {
            // dee.g:179:4: ( decldef | '{' decldefs '}' )
            int alt12=2;
            int LA12_0 = input.LA(1);
            if ( (LA12_0==IDENT||(LA12_0>=41 && LA12_0<=42)||(LA12_0>=53 && LA12_0<=55)||(LA12_0>=58 && LA12_0<=79)||(LA12_0>=85 && LA12_0<=95)) ) {
                alt12=1;
            }
            else if ( (LA12_0==51) ) {
                alt12=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("178:1: declarationBlock : ( decldef | '{' decldefs '}' );", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // dee.g:179:4: decldef
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_decldef_in_declarationBlock1294);
                    decldef39=decldef();
                    _fsp--;

                    adaptor.addChild(root_0, decldef39.tree);

                    }
                    break;
                case 2 :
                    // dee.g:180:7: '{' decldefs '}'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal40=(Token)input.LT(1);
                    match(input,51,FOLLOW_51_in_declarationBlock1302); 
                    char_literal40_tree = (Object)adaptor.create(char_literal40);
                    adaptor.addChild(root_0, char_literal40_tree);

                    pushFollow(FOLLOW_decldefs_in_declarationBlock1304);
                    decldefs41=decldefs();
                    _fsp--;

                    adaptor.addChild(root_0, decldefs41.tree);
                    char_literal42=(Token)input.LT(1);
                    match(input,52,FOLLOW_52_in_declarationBlock1306); 
                    char_literal42_tree = (Object)adaptor.create(char_literal42);
                    adaptor.addChild(root_0, char_literal42_tree);


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end declarationBlock

    public static class qname_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start qname
    // dee.g:184:1: qname : ( IDENT | templateInstance ) ( '.' qname )? ;
    public qname_return qname() throws RecognitionException {   
        qname_return retval = new qname_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IDENT43=null;
        Token char_literal45=null;
        templateInstance_return templateInstance44 = null;

        qname_return qname46 = null;


        Object IDENT43_tree=null;
        Object char_literal45_tree=null;

        try {
            // dee.g:186:8: ( ( IDENT | templateInstance ) ( '.' qname )? )
            // dee.g:186:8: ( IDENT | templateInstance ) ( '.' qname )?
            {
            root_0 = (Object)adaptor.nil();

            // dee.g:186:8: ( IDENT | templateInstance )
            int alt13=2;
            int LA13_0 = input.LA(1);
            if ( (LA13_0==IDENT) ) {
                alt13=1;
            }
            else if ( (LA13_0==54) ) {
                alt13=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("186:8: ( IDENT | templateInstance )", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // dee.g:186:9: IDENT
                    {
                    Object root_1 = (Object)adaptor.nil();

                    IDENT43=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_qname1321); 
                    IDENT43_tree = (Object)adaptor.create(IDENT43);
                    adaptor.addChild(root_1, IDENT43_tree);


                    adaptor.addChild(root_0, root_1);

                    }
                    break;
                case 2 :
                    // dee.g:186:17: templateInstance
                    {
                    Object root_1 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_templateInstance_in_qname1325);
                    templateInstance44=templateInstance();
                    _fsp--;

                    adaptor.addChild(root_1, templateInstance44.tree);

                    adaptor.addChild(root_0, root_1);

                    }
                    break;

            }

            // dee.g:186:35: ( '.' qname )?
            int alt14=2;
            int LA14_0 = input.LA(1);
            if ( (LA14_0==53) ) {
                int LA14_1 = input.LA(2);
                if ( (LA14_1==IDENT) ) {
                    alt14=1;
                }
                else if ( (LA14_1==54) ) {
                    alt14=1;
                }
            }
            switch (alt14) {
                case 1 :
                    // dee.g:186:36: '.' qname
                    {
                    Object root_1 = (Object)adaptor.nil();

                    char_literal45=(Token)input.LT(1);
                    match(input,53,FOLLOW_53_in_qname1329); 
                    char_literal45_tree = (Object)adaptor.create(char_literal45);
                    adaptor.addChild(root_1, char_literal45_tree);

                    pushFollow(FOLLOW_qname_in_qname1331);
                    qname46=qname();
                    _fsp--;

                    adaptor.addChild(root_1, qname46.tree);

                    adaptor.addChild(root_0, root_1);

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end qname

    public static class templateInstance_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start templateInstance
    // dee.g:188:1: templateInstance : 'TEMPLATE INSTANCE' ;
    public templateInstance_return templateInstance() throws RecognitionException {   
        templateInstance_return retval = new templateInstance_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal47=null;

        Object string_literal47_tree=null;

        try {
            // dee.g:188:20: ( 'TEMPLATE INSTANCE' )
            // dee.g:188:20: 'TEMPLATE INSTANCE'
            {
            root_0 = (Object)adaptor.nil();

            string_literal47=(Token)input.LT(1);
            match(input,54,FOLLOW_54_in_templateInstance1342); 
            string_literal47_tree = (Object)adaptor.create(string_literal47);
            adaptor.addChild(root_0, string_literal47_tree);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end templateInstance

    public static class entityRef_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start entityRef
    // dee.g:191:1: entityRef : ( typeRef | rootNameRef );
    public entityRef_return entityRef() throws RecognitionException {   
        entityRef_return retval = new entityRef_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        typeRef_return typeRef48 = null;

        rootNameRef_return rootNameRef49 = null;



        try {
            // dee.g:192:4: ( typeRef | rootNameRef )
            int alt15=2;
            switch ( input.LA(1) ) {
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
                alt15=1;
                break;
            case 55:
                int LA15_2 = input.LA(2);
                if ( (LA15_2==56) ) {
                    int LA15_6 = input.LA(3);
                    if ( (LA15_6==100) ) {
                        int LA15_9 = input.LA(4);
                        if ( (LA15_9==57) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("191:1: entityRef : ( typeRef | rootNameRef );", 15, 9, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("191:1: entityRef : ( typeRef | rootNameRef );", 15, 6, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("191:1: entityRef : ( typeRef | rootNameRef );", 15, 2, input);

                    throw nvae;
                }
                break;
            case 53:
                int LA15_3 = input.LA(2);
                if ( (LA15_3==IDENT) ) {
                    alt15=1;
                }
                else if ( (LA15_3==54) ) {
                    alt15=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("191:1: entityRef : ( typeRef | rootNameRef );", 15, 3, input);

                    throw nvae;
                }
                break;
            case IDENT:
                alt15=1;
                break;
            case 54:
                alt15=1;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("191:1: entityRef : ( typeRef | rootNameRef );", 15, 0, input);

                throw nvae;
            }

            switch (alt15) {
                case 1 :
                    // dee.g:192:4: typeRef
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_typeRef_in_entityRef1354);
                    typeRef48=typeRef();
                    _fsp--;

                    adaptor.addChild(root_0, typeRef48.tree);

                    }
                    break;
                case 2 :
                    // dee.g:193:4: rootNameRef
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rootNameRef_in_entityRef1359);
                    rootNameRef49=rootNameRef();
                    _fsp--;

                    adaptor.addChild(root_0, rootNameRef49.tree);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end entityRef

    public static class rootNameRef_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start rootNameRef
    // dee.g:195:1: rootNameRef : ( 'typeof' '(' expression ')' ( '.' qname )? | '.' qname | qname );
    public rootNameRef_return rootNameRef() throws RecognitionException {   
        rootNameRef_return retval = new rootNameRef_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal50=null;
        Token char_literal51=null;
        Token char_literal53=null;
        Token char_literal54=null;
        Token char_literal56=null;
        expression_return expression52 = null;

        qname_return qname55 = null;

        qname_return qname57 = null;

        qname_return qname58 = null;


        Object string_literal50_tree=null;
        Object char_literal51_tree=null;
        Object char_literal53_tree=null;
        Object char_literal54_tree=null;
        Object char_literal56_tree=null;

        try {
            // dee.g:196:5: ( 'typeof' '(' expression ')' ( '.' qname )? | '.' qname | qname )
            int alt17=3;
            switch ( input.LA(1) ) {
            case 55:
                alt17=1;
                break;
            case 53:
                alt17=2;
                break;
            case IDENT:
            case 54:
                alt17=3;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("195:1: rootNameRef : ( 'typeof' '(' expression ')' ( '.' qname )? | '.' qname | qname );", 17, 0, input);

                throw nvae;
            }

            switch (alt17) {
                case 1 :
                    // dee.g:196:5: 'typeof' '(' expression ')' ( '.' qname )?
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal50=(Token)input.LT(1);
                    match(input,55,FOLLOW_55_in_rootNameRef1369); 
                    string_literal50_tree = (Object)adaptor.create(string_literal50);
                    adaptor.addChild(root_0, string_literal50_tree);

                    char_literal51=(Token)input.LT(1);
                    match(input,56,FOLLOW_56_in_rootNameRef1371); 
                    char_literal51_tree = (Object)adaptor.create(char_literal51);
                    adaptor.addChild(root_0, char_literal51_tree);

                    pushFollow(FOLLOW_expression_in_rootNameRef1373);
                    expression52=expression();
                    _fsp--;

                    adaptor.addChild(root_0, expression52.tree);
                    char_literal53=(Token)input.LT(1);
                    match(input,57,FOLLOW_57_in_rootNameRef1375); 
                    char_literal53_tree = (Object)adaptor.create(char_literal53);
                    adaptor.addChild(root_0, char_literal53_tree);

                    // dee.g:196:33: ( '.' qname )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);
                    if ( (LA16_0==53) ) {
                        int LA16_1 = input.LA(2);
                        if ( (LA16_1==IDENT) ) {
                            alt16=1;
                        }
                        else if ( (LA16_1==54) ) {
                            alt16=1;
                        }
                    }
                    switch (alt16) {
                        case 1 :
                            // dee.g:196:34: '.' qname
                            {
                            Object root_1 = (Object)adaptor.nil();

                            char_literal54=(Token)input.LT(1);
                            match(input,53,FOLLOW_53_in_rootNameRef1378); 
                            char_literal54_tree = (Object)adaptor.create(char_literal54);
                            adaptor.addChild(root_1, char_literal54_tree);

                            pushFollow(FOLLOW_qname_in_rootNameRef1380);
                            qname55=qname();
                            _fsp--;

                            adaptor.addChild(root_1, qname55.tree);

                            adaptor.addChild(root_0, root_1);

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // dee.g:197:4: '.' qname
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal56=(Token)input.LT(1);
                    match(input,53,FOLLOW_53_in_rootNameRef1388); 
                    char_literal56_tree = (Object)adaptor.create(char_literal56);
                    adaptor.addChild(root_0, char_literal56_tree);

                    pushFollow(FOLLOW_qname_in_rootNameRef1390);
                    qname57=qname();
                    _fsp--;

                    adaptor.addChild(root_0, qname57.tree);

                    }
                    break;
                case 3 :
                    // dee.g:198:5: qname
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_qname_in_rootNameRef1396);
                    qname58=qname();
                    _fsp--;

                    adaptor.addChild(root_0, qname58.tree);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end rootNameRef

    public static class typeRef_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start typeRef
    // dee.g:201:1: typeRef : basicType ( modType )? ;
    public typeRef_return typeRef() throws RecognitionException {   
        typeRef_return retval = new typeRef_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        basicType_return basicType59 = null;

        modType_return modType60 = null;



        try {
            // dee.g:201:12: ( basicType ( modType )? )
            // dee.g:201:12: basicType ( modType )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_basicType_in_typeRef1407);
            basicType59=basicType();
            _fsp--;

            adaptor.addChild(root_0, basicType59.tree);
            // dee.g:201:22: ( modType )?
            int alt18=2;
            int LA18_0 = input.LA(1);
            if ( (LA18_0==MULT||(LA18_0>=80 && LA18_0<=81)||(LA18_0>=83 && LA18_0<=84)) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // dee.g:201:22: modType
                    {
                    Object root_1 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_modType_in_typeRef1409);
                    modType60=modType();
                    _fsp--;

                    adaptor.addChild(root_1, modType60.tree);

                    adaptor.addChild(root_0, root_1);

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end typeRef

    public static class basicType_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start basicType
    // dee.g:205:1: basicType : ( primitiveType | rootNameRef );
    public basicType_return basicType() throws RecognitionException {   
        basicType_return retval = new basicType_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        primitiveType_return primitiveType61 = null;

        rootNameRef_return rootNameRef62 = null;



        try {
            // dee.g:205:14: ( primitiveType | rootNameRef )
            int alt19=2;
            int LA19_0 = input.LA(1);
            if ( ((LA19_0>=58 && LA19_0<=79)) ) {
                alt19=1;
            }
            else if ( (LA19_0==IDENT||(LA19_0>=53 && LA19_0<=55)) ) {
                alt19=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("205:1: basicType : ( primitiveType | rootNameRef );", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // dee.g:205:14: primitiveType
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_primitiveType_in_basicType1422);
                    primitiveType61=primitiveType();
                    _fsp--;

                    adaptor.addChild(root_0, primitiveType61.tree);

                    }
                    break;
                case 2 :
                    // dee.g:205:30: rootNameRef
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rootNameRef_in_basicType1426);
                    rootNameRef62=rootNameRef();
                    _fsp--;

                    adaptor.addChild(root_0, rootNameRef62.tree);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end basicType

    public static class primitiveType_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start primitiveType
    // dee.g:207:1: primitiveType : ('void'|'bool'|'byte'|'ubyte'|'short'|'ushort'|'int'|'uint'|'long'|'ulong'|'float'|'double'|'real'|'ifloat'|'idouble'|'ireal'|'cfloat'|'cdouble'|'creal'|'char'|'wchar'|'dchar');
    public primitiveType_return primitiveType() throws RecognitionException {   
        primitiveType_return retval = new primitiveType_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set63=null;

        Object set63_tree=null;

        try {
            // dee.g:207:15: ( ('void'|'bool'|'byte'|'ubyte'|'short'|'ushort'|'int'|'uint'|'long'|'ulong'|'float'|'double'|'real'|'ifloat'|'idouble'|'ireal'|'cfloat'|'cdouble'|'creal'|'char'|'wchar'|'dchar'))
            // dee.g:207:18: ('void'|'bool'|'byte'|'ubyte'|'short'|'ushort'|'int'|'uint'|'long'|'ulong'|'float'|'double'|'real'|'ifloat'|'idouble'|'ireal'|'cfloat'|'cdouble'|'creal'|'char'|'wchar'|'dchar')
            {
            root_0 = (Object)adaptor.nil();

            set63=(Token)input.LT(1);
            if ( (input.LA(1)>=58 && input.LA(1)<=79) ) {
                adaptor.addChild(root_0, adaptor.create(set63));
                input.consume();
                errorRecovery=false;
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_primitiveType1436);    throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end primitiveType

    public static class modType_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start modType
    // dee.g:212:1: modType : ( '*' ( modType )? | '[]' ( modType )? | '[' expression ']' | '[' typeRef ']' | 'delegate' parameters | 'function' parameters );
    public modType_return modType() throws RecognitionException {   
        modType_return retval = new modType_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal64=null;
        Token string_literal66=null;
        Token char_literal68=null;
        Token char_literal70=null;
        Token char_literal71=null;
        Token char_literal73=null;
        Token string_literal74=null;
        Token string_literal76=null;
        modType_return modType65 = null;

        modType_return modType67 = null;

        expression_return expression69 = null;

        typeRef_return typeRef72 = null;

        parameters_return parameters75 = null;

        parameters_return parameters77 = null;


        Object char_literal64_tree=null;
        Object string_literal66_tree=null;
        Object char_literal68_tree=null;
        Object char_literal70_tree=null;
        Object char_literal71_tree=null;
        Object char_literal73_tree=null;
        Object string_literal74_tree=null;
        Object string_literal76_tree=null;

        try {
            // dee.g:213:4: ( '*' ( modType )? | '[]' ( modType )? | '[' expression ']' | '[' typeRef ']' | 'delegate' parameters | 'function' parameters )
            int alt22=6;
            switch ( input.LA(1) ) {
            case MULT:
                alt22=1;
                break;
            case 80:
                alt22=2;
                break;
            case 81:
                int LA22_3 = input.LA(2);
                if ( (LA22_3==100) ) {
                    alt22=3;
                }
                else if ( (LA22_3==IDENT||(LA22_3>=53 && LA22_3<=55)||(LA22_3>=58 && LA22_3<=79)) ) {
                    alt22=4;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("212:1: modType : ( '*' ( modType )? | '[]' ( modType )? | '[' expression ']' | '[' typeRef ']' | 'delegate' parameters | 'function' parameters );", 22, 3, input);

                    throw nvae;
                }
                break;
            case 83:
                alt22=5;
                break;
            case 84:
                alt22=6;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("212:1: modType : ( '*' ( modType )? | '[]' ( modType )? | '[' expression ']' | '[' typeRef ']' | 'delegate' parameters | 'function' parameters );", 22, 0, input);

                throw nvae;
            }

            switch (alt22) {
                case 1 :
                    // dee.g:213:4: '*' ( modType )?
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal64=(Token)input.LT(1);
                    match(input,MULT,FOLLOW_MULT_in_modType1499); 
                    char_literal64_tree = (Object)adaptor.create(char_literal64);
                    adaptor.addChild(root_0, char_literal64_tree);

                    // dee.g:213:8: ( modType )?
                    int alt20=2;
                    int LA20_0 = input.LA(1);
                    if ( (LA20_0==MULT||(LA20_0>=80 && LA20_0<=81)||(LA20_0>=83 && LA20_0<=84)) ) {
                        alt20=1;
                    }
                    switch (alt20) {
                        case 1 :
                            // dee.g:213:8: modType
                            {
                            Object root_1 = (Object)adaptor.nil();

                            pushFollow(FOLLOW_modType_in_modType1501);
                            modType65=modType();
                            _fsp--;

                            adaptor.addChild(root_1, modType65.tree);

                            adaptor.addChild(root_0, root_1);

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // dee.g:214:4: '[]' ( modType )?
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal66=(Token)input.LT(1);
                    match(input,80,FOLLOW_80_in_modType1507); 
                    string_literal66_tree = (Object)adaptor.create(string_literal66);
                    adaptor.addChild(root_0, string_literal66_tree);

                    // dee.g:214:9: ( modType )?
                    int alt21=2;
                    int LA21_0 = input.LA(1);
                    if ( (LA21_0==MULT||(LA21_0>=80 && LA21_0<=81)||(LA21_0>=83 && LA21_0<=84)) ) {
                        alt21=1;
                    }
                    switch (alt21) {
                        case 1 :
                            // dee.g:214:9: modType
                            {
                            Object root_1 = (Object)adaptor.nil();

                            pushFollow(FOLLOW_modType_in_modType1509);
                            modType67=modType();
                            _fsp--;

                            adaptor.addChild(root_1, modType67.tree);

                            adaptor.addChild(root_0, root_1);

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // dee.g:215:5: '[' expression ']'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal68=(Token)input.LT(1);
                    match(input,81,FOLLOW_81_in_modType1516); 
                    char_literal68_tree = (Object)adaptor.create(char_literal68);
                    adaptor.addChild(root_0, char_literal68_tree);

                    pushFollow(FOLLOW_expression_in_modType1518);
                    expression69=expression();
                    _fsp--;

                    adaptor.addChild(root_0, expression69.tree);
                    char_literal70=(Token)input.LT(1);
                    match(input,82,FOLLOW_82_in_modType1520); 
                    char_literal70_tree = (Object)adaptor.create(char_literal70);
                    adaptor.addChild(root_0, char_literal70_tree);


                    }
                    break;
                case 4 :
                    // dee.g:216:8: '[' typeRef ']'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal71=(Token)input.LT(1);
                    match(input,81,FOLLOW_81_in_modType1529); 
                    char_literal71_tree = (Object)adaptor.create(char_literal71);
                    adaptor.addChild(root_0, char_literal71_tree);

                    pushFollow(FOLLOW_typeRef_in_modType1531);
                    typeRef72=typeRef();
                    _fsp--;

                    adaptor.addChild(root_0, typeRef72.tree);
                    char_literal73=(Token)input.LT(1);
                    match(input,82,FOLLOW_82_in_modType1533); 
                    char_literal73_tree = (Object)adaptor.create(char_literal73);
                    adaptor.addChild(root_0, char_literal73_tree);


                    }
                    break;
                case 5 :
                    // dee.g:217:8: 'delegate' parameters
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal74=(Token)input.LT(1);
                    match(input,83,FOLLOW_83_in_modType1542); 
                    string_literal74_tree = (Object)adaptor.create(string_literal74);
                    adaptor.addChild(root_0, string_literal74_tree);

                    pushFollow(FOLLOW_parameters_in_modType1544);
                    parameters75=parameters();
                    _fsp--;

                    adaptor.addChild(root_0, parameters75.tree);

                    }
                    break;
                case 6 :
                    // dee.g:218:8: 'function' parameters
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal76=(Token)input.LT(1);
                    match(input,84,FOLLOW_84_in_modType1553); 
                    string_literal76_tree = (Object)adaptor.create(string_literal76);
                    adaptor.addChild(root_0, string_literal76_tree);

                    pushFollow(FOLLOW_parameters_in_modType1555);
                    parameters77=parameters();
                    _fsp--;

                    adaptor.addChild(root_0, parameters77.tree);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end modType

    public static class exprEnt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start exprEnt
    // dee.g:222:1: exprEnt : ( basicType '.' qname | rootNameRef );
    public exprEnt_return exprEnt() throws RecognitionException {   
        exprEnt_return retval = new exprEnt_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal79=null;
        basicType_return basicType78 = null;

        qname_return qname80 = null;

        rootNameRef_return rootNameRef81 = null;


        Object char_literal79_tree=null;

        try {
            // dee.g:223:4: ( basicType '.' qname | rootNameRef )
            int alt23=2;
            switch ( input.LA(1) ) {
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
                alt23=1;
                break;
            case 55:
                int LA23_2 = input.LA(2);
                if ( (LA23_2==56) ) {
                    int LA23_6 = input.LA(3);
                    if ( (LA23_6==100) ) {
                        int LA23_11 = input.LA(4);
                        if ( (LA23_11==57) ) {
                            int LA23_15 = input.LA(5);
                            if ( (LA23_15==53) ) {
                                int LA23_18 = input.LA(6);
                                if ( (LA23_18==IDENT) ) {
                                    alt23=1;
                                }
                                else if ( (LA23_18==54) ) {
                                    alt23=1;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("222:1: exprEnt : ( basicType '.' qname | rootNameRef );", 23, 18, input);

                                    throw nvae;
                                }
                            }
                            else if ( (LA23_15==EOF) ) {
                                alt23=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("222:1: exprEnt : ( basicType '.' qname | rootNameRef );", 23, 15, input);

                                throw nvae;
                            }
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("222:1: exprEnt : ( basicType '.' qname | rootNameRef );", 23, 11, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("222:1: exprEnt : ( basicType '.' qname | rootNameRef );", 23, 6, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("222:1: exprEnt : ( basicType '.' qname | rootNameRef );", 23, 2, input);

                    throw nvae;
                }
                break;
            case 53:
                int LA23_3 = input.LA(2);
                if ( (LA23_3==IDENT) ) {
                    int LA23_7 = input.LA(3);
                    if ( (LA23_7==53) ) {
                        int LA23_12 = input.LA(4);
                        if ( (LA23_12==IDENT) ) {
                            alt23=1;
                        }
                        else if ( (LA23_12==54) ) {
                            alt23=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("222:1: exprEnt : ( basicType '.' qname | rootNameRef );", 23, 12, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA23_7==EOF) ) {
                        alt23=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("222:1: exprEnt : ( basicType '.' qname | rootNameRef );", 23, 7, input);

                        throw nvae;
                    }
                }
                else if ( (LA23_3==54) ) {
                    int LA23_8 = input.LA(3);
                    if ( (LA23_8==53) ) {
                        int LA23_12 = input.LA(4);
                        if ( (LA23_12==IDENT) ) {
                            alt23=1;
                        }
                        else if ( (LA23_12==54) ) {
                            alt23=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("222:1: exprEnt : ( basicType '.' qname | rootNameRef );", 23, 12, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA23_8==EOF) ) {
                        alt23=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("222:1: exprEnt : ( basicType '.' qname | rootNameRef );", 23, 8, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("222:1: exprEnt : ( basicType '.' qname | rootNameRef );", 23, 3, input);

                    throw nvae;
                }
                break;
            case IDENT:
                int LA23_4 = input.LA(2);
                if ( (LA23_4==53) ) {
                    int LA23_9 = input.LA(3);
                    if ( (LA23_9==IDENT) ) {
                        alt23=1;
                    }
                    else if ( (LA23_9==54) ) {
                        alt23=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("222:1: exprEnt : ( basicType '.' qname | rootNameRef );", 23, 9, input);

                        throw nvae;
                    }
                }
                else if ( (LA23_4==EOF) ) {
                    alt23=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("222:1: exprEnt : ( basicType '.' qname | rootNameRef );", 23, 4, input);

                    throw nvae;
                }
                break;
            case 54:
                int LA23_5 = input.LA(2);
                if ( (LA23_5==53) ) {
                    int LA23_9 = input.LA(3);
                    if ( (LA23_9==IDENT) ) {
                        alt23=1;
                    }
                    else if ( (LA23_9==54) ) {
                        alt23=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("222:1: exprEnt : ( basicType '.' qname | rootNameRef );", 23, 9, input);

                        throw nvae;
                    }
                }
                else if ( (LA23_5==EOF) ) {
                    alt23=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("222:1: exprEnt : ( basicType '.' qname | rootNameRef );", 23, 5, input);

                    throw nvae;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("222:1: exprEnt : ( basicType '.' qname | rootNameRef );", 23, 0, input);

                throw nvae;
            }

            switch (alt23) {
                case 1 :
                    // dee.g:223:4: basicType '.' qname
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_basicType_in_exprEnt1567);
                    basicType78=basicType();
                    _fsp--;

                    adaptor.addChild(root_0, basicType78.tree);
                    char_literal79=(Token)input.LT(1);
                    match(input,53,FOLLOW_53_in_exprEnt1569); 
                    char_literal79_tree = (Object)adaptor.create(char_literal79);
                    adaptor.addChild(root_0, char_literal79_tree);

                    pushFollow(FOLLOW_qname_in_exprEnt1571);
                    qname80=qname();
                    _fsp--;

                    adaptor.addChild(root_0, qname80.tree);

                    }
                    break;
                case 2 :
                    // dee.g:224:4: rootNameRef
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rootNameRef_in_exprEnt1577);
                    rootNameRef81=rootNameRef();
                    _fsp--;

                    adaptor.addChild(root_0, rootNameRef81.tree);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end exprEnt

    public static class declaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start declaration
    // dee.g:227:1: declaration : ( 'typedef' typeRef IDENT ';' | 'alias' rootNameRef IDENT ';' | varDeclaration );
    public declaration_return declaration() throws RecognitionException {   
        declaration_return retval = new declaration_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal82=null;
        Token IDENT84=null;
        Token char_literal85=null;
        Token string_literal86=null;
        Token IDENT88=null;
        Token char_literal89=null;
        typeRef_return typeRef83 = null;

        rootNameRef_return rootNameRef87 = null;

        varDeclaration_return varDeclaration90 = null;


        Object string_literal82_tree=null;
        Object IDENT84_tree=null;
        Object char_literal85_tree=null;
        Object string_literal86_tree=null;
        Object IDENT88_tree=null;
        Object char_literal89_tree=null;

        try {
            // dee.g:230:4: ( 'typedef' typeRef IDENT ';' | 'alias' rootNameRef IDENT ';' | varDeclaration )
            int alt24=3;
            switch ( input.LA(1) ) {
            case 85:
                alt24=1;
                break;
            case 86:
                alt24=2;
                break;
            case IDENT:
            case 41:
            case 53:
            case 54:
            case 55:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
                alt24=3;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("227:1: declaration : ( 'typedef' typeRef IDENT ';' | 'alias' rootNameRef IDENT ';' | varDeclaration );", 24, 0, input);

                throw nvae;
            }

            switch (alt24) {
                case 1 :
                    // dee.g:230:4: 'typedef' typeRef IDENT ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal82=(Token)input.LT(1);
                    match(input,85,FOLLOW_85_in_declaration1591); 
                    string_literal82_tree = (Object)adaptor.create(string_literal82);
                    adaptor.addChild(root_0, string_literal82_tree);

                    pushFollow(FOLLOW_typeRef_in_declaration1593);
                    typeRef83=typeRef();
                    _fsp--;

                    adaptor.addChild(root_0, typeRef83.tree);
                    IDENT84=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_declaration1595); 
                    IDENT84_tree = (Object)adaptor.create(IDENT84);
                    adaptor.addChild(root_0, IDENT84_tree);

                    char_literal85=(Token)input.LT(1);
                    match(input,40,FOLLOW_40_in_declaration1597); 
                    char_literal85_tree = (Object)adaptor.create(char_literal85);
                    adaptor.addChild(root_0, char_literal85_tree);


                    }
                    break;
                case 2 :
                    // dee.g:231:4: 'alias' rootNameRef IDENT ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal86=(Token)input.LT(1);
                    match(input,86,FOLLOW_86_in_declaration1602); 
                    string_literal86_tree = (Object)adaptor.create(string_literal86);
                    adaptor.addChild(root_0, string_literal86_tree);

                    pushFollow(FOLLOW_rootNameRef_in_declaration1604);
                    rootNameRef87=rootNameRef();
                    _fsp--;

                    adaptor.addChild(root_0, rootNameRef87.tree);
                    IDENT88=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_declaration1606); 
                    IDENT88_tree = (Object)adaptor.create(IDENT88);
                    adaptor.addChild(root_0, IDENT88_tree);

                    char_literal89=(Token)input.LT(1);
                    match(input,40,FOLLOW_40_in_declaration1608); 
                    char_literal89_tree = (Object)adaptor.create(char_literal89);
                    adaptor.addChild(root_0, char_literal89_tree);


                    }
                    break;
                case 3 :
                    // dee.g:232:4: varDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_varDeclaration_in_declaration1613);
                    varDeclaration90=varDeclaration();
                    _fsp--;

                    adaptor.addChild(root_0, varDeclaration90.tree);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end declaration

    public static class autoDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start autoDeclaration
    // dee.g:234:1: autoDeclaration : 'auto' identifierInitializerList ';' ;
    public autoDeclaration_return autoDeclaration() throws RecognitionException {   
        autoDeclaration_return retval = new autoDeclaration_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal91=null;
        Token char_literal93=null;
        identifierInitializerList_return identifierInitializerList92 = null;


        Object string_literal91_tree=null;
        Object char_literal93_tree=null;

        try {
            // dee.g:236:4: ( 'auto' identifierInitializerList ';' )
            // dee.g:236:4: 'auto' identifierInitializerList ';'
            {
            root_0 = (Object)adaptor.nil();

            string_literal91=(Token)input.LT(1);
            match(input,87,FOLLOW_87_in_autoDeclaration1625); 
            string_literal91_tree = (Object)adaptor.create(string_literal91);
            adaptor.addChild(root_0, string_literal91_tree);

            pushFollow(FOLLOW_identifierInitializerList_in_autoDeclaration1627);
            identifierInitializerList92=identifierInitializerList();
            _fsp--;

            adaptor.addChild(root_0, identifierInitializerList92.tree);
            char_literal93=(Token)input.LT(1);
            match(input,40,FOLLOW_40_in_autoDeclaration1629); 
            char_literal93_tree = (Object)adaptor.create(char_literal93);
            adaptor.addChild(root_0, char_literal93_tree);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end autoDeclaration

    public static class storageclass_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start storageclass
    // dee.g:239:1: storageclass : ('abstract'|'auto'|'const'|'deprecated'|'extern'|'final'|'override'|'static'|'synchronized');
    public storageclass_return storageclass() throws RecognitionException {   
        storageclass_return retval = new storageclass_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set94=null;

        Object set94_tree=null;

        try {
            // dee.g:239:13: ( ('abstract'|'auto'|'const'|'deprecated'|'extern'|'final'|'override'|'static'|'synchronized'))
            // dee.g:239:15: ('abstract'|'auto'|'const'|'deprecated'|'extern'|'final'|'override'|'static'|'synchronized')
            {
            root_0 = (Object)adaptor.nil();

            set94=(Token)input.LT(1);
            if ( input.LA(1)==41||(input.LA(1)>=87 && input.LA(1)<=94) ) {
                adaptor.addChild(root_0, adaptor.create(set94));
                input.consume();
                errorRecovery=false;
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_storageclass1640);    throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end storageclass

    public static class varDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start varDeclaration
    // dee.g:241:1: varDeclaration : ( storageclass )* actualVarDeclaration ;
    public varDeclaration_return varDeclaration() throws RecognitionException {   
        varDeclaration_return retval = new varDeclaration_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        storageclass_return storageclass95 = null;

        actualVarDeclaration_return actualVarDeclaration96 = null;



        try {
            // dee.g:241:20: ( ( storageclass )* actualVarDeclaration )
            // dee.g:241:20: ( storageclass )* actualVarDeclaration
            {
            root_0 = (Object)adaptor.nil();

            // dee.g:241:20: ( storageclass )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);
                if ( (LA25_0==87) ) {
                    int LA25_2 = input.LA(2);
                    if ( (LA25_2==41||(LA25_2>=53 && LA25_2<=55)||(LA25_2>=58 && LA25_2<=79)||(LA25_2>=87 && LA25_2<=95)) ) {
                        alt25=1;
                    }
                    else if ( (LA25_2==IDENT) ) {
                        int LA25_4 = input.LA(3);
                        if ( (LA25_4==MULT||LA25_4==IDENT||LA25_4==53||(LA25_4>=80 && LA25_4<=81)||(LA25_4>=83 && LA25_4<=84)) ) {
                            alt25=1;
                        }


                    }


                }
                else if ( (LA25_0==41||(LA25_0>=88 && LA25_0<=94)) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // dee.g:241:20: storageclass
            	    {
            	    Object root_1 = (Object)adaptor.nil();

            	    pushFollow(FOLLOW_storageclass_in_varDeclaration1666);
            	    storageclass95=storageclass();
            	    _fsp--;

            	    adaptor.addChild(root_1, storageclass95.tree);

            	    adaptor.addChild(root_0, root_1);

            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);

            pushFollow(FOLLOW_actualVarDeclaration_in_varDeclaration1669);
            actualVarDeclaration96=actualVarDeclaration();
            _fsp--;

            adaptor.addChild(root_0, actualVarDeclaration96.tree);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end varDeclaration

    public static class actualVarDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start actualVarDeclaration
    // dee.g:243:1: actualVarDeclaration options {k=2; } : ( ( typeRef identifierInitializerList ';' ) | ( mytype functionDeclarator functionBody ) | autoDeclaration );
    public actualVarDeclaration_return actualVarDeclaration() throws RecognitionException {   
        actualVarDeclaration_return retval = new actualVarDeclaration_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal99=null;
        typeRef_return typeRef97 = null;

        identifierInitializerList_return identifierInitializerList98 = null;

        mytype_return mytype100 = null;

        functionDeclarator_return functionDeclarator101 = null;

        functionBody_return functionBody102 = null;

        autoDeclaration_return autoDeclaration103 = null;


        Object char_literal99_tree=null;

        try {
            // dee.g:245:6: ( ( typeRef identifierInitializerList ';' ) | ( mytype functionDeclarator functionBody ) | autoDeclaration )
            int alt26=3;
            switch ( input.LA(1) ) {
            case IDENT:
            case 53:
            case 54:
            case 55:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
                alt26=1;
                break;
            case 95:
                alt26=2;
                break;
            case 87:
                alt26=3;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("243:1: actualVarDeclaration options {k=2; } : ( ( typeRef identifierInitializerList ';' ) | ( mytype functionDeclarator functionBody ) | autoDeclaration );", 26, 0, input);

                throw nvae;
            }

            switch (alt26) {
                case 1 :
                    // dee.g:245:6: ( typeRef identifierInitializerList ';' )
                    {
                    root_0 = (Object)adaptor.nil();

                    // dee.g:245:6: ( typeRef identifierInitializerList ';' )
                    // dee.g:245:8: typeRef identifierInitializerList ';'
                    {
                    Object root_1 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_typeRef_in_actualVarDeclaration1696);
                    typeRef97=typeRef();
                    _fsp--;

                    adaptor.addChild(root_1, typeRef97.tree);
                    pushFollow(FOLLOW_identifierInitializerList_in_actualVarDeclaration1698);
                    identifierInitializerList98=identifierInitializerList();
                    _fsp--;

                    adaptor.addChild(root_1, identifierInitializerList98.tree);
                    char_literal99=(Token)input.LT(1);
                    match(input,40,FOLLOW_40_in_actualVarDeclaration1700); 
                    char_literal99_tree = (Object)adaptor.create(char_literal99);
                    adaptor.addChild(root_1, char_literal99_tree);


                    adaptor.addChild(root_0, root_1);

                    }


                    }
                    break;
                case 2 :
                    // dee.g:246:7: ( mytype functionDeclarator functionBody )
                    {
                    root_0 = (Object)adaptor.nil();

                    // dee.g:246:7: ( mytype functionDeclarator functionBody )
                    // dee.g:246:9: mytype functionDeclarator functionBody
                    {
                    Object root_1 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_mytype_in_actualVarDeclaration1712);
                    mytype100=mytype();
                    _fsp--;

                    adaptor.addChild(root_1, mytype100.tree);
                    pushFollow(FOLLOW_functionDeclarator_in_actualVarDeclaration1714);
                    functionDeclarator101=functionDeclarator();
                    _fsp--;

                    adaptor.addChild(root_1, functionDeclarator101.tree);
                    pushFollow(FOLLOW_functionBody_in_actualVarDeclaration1716);
                    functionBody102=functionBody();
                    _fsp--;

                    adaptor.addChild(root_1, functionBody102.tree);

                    adaptor.addChild(root_0, root_1);

                    }


                    }
                    break;
                case 3 :
                    // dee.g:248:4: autoDeclaration
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_autoDeclaration_in_actualVarDeclaration1724);
                    autoDeclaration103=autoDeclaration();
                    _fsp--;

                    adaptor.addChild(root_0, autoDeclaration103.tree);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end actualVarDeclaration

    public static class mytype_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start mytype
    // dee.g:251:1: mytype : 'asdfagad' ;
    public mytype_return mytype() throws RecognitionException {   
        mytype_return retval = new mytype_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal104=null;

        Object string_literal104_tree=null;

        try {
            // dee.g:252:4: ( 'asdfagad' )
            // dee.g:252:4: 'asdfagad'
            {
            root_0 = (Object)adaptor.nil();

            string_literal104=(Token)input.LT(1);
            match(input,95,FOLLOW_95_in_mytype1735); 
            string_literal104_tree = (Object)adaptor.create(string_literal104);
            adaptor.addChild(root_0, string_literal104_tree);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end mytype

    public static class identifierInitializerList_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start identifierInitializerList
    // dee.g:257:1: identifierInitializerList : IDENT ( '=' initializer )? ( ',' identifierInitializerList )? ;
    public identifierInitializerList_return identifierInitializerList() throws RecognitionException {   
        identifierInitializerList_return retval = new identifierInitializerList_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IDENT105=null;
        Token char_literal106=null;
        Token char_literal108=null;
        initializer_return initializer107 = null;

        identifierInitializerList_return identifierInitializerList109 = null;


        Object IDENT105_tree=null;
        Object char_literal106_tree=null;
        Object char_literal108_tree=null;

        try {
            // dee.g:258:4: ( IDENT ( '=' initializer )? ( ',' identifierInitializerList )? )
            // dee.g:258:4: IDENT ( '=' initializer )? ( ',' identifierInitializerList )?
            {
            root_0 = (Object)adaptor.nil();

            IDENT105=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_identifierInitializerList1749); 
            IDENT105_tree = (Object)adaptor.create(IDENT105);
            adaptor.addChild(root_0, IDENT105_tree);

            // dee.g:258:10: ( '=' initializer )?
            int alt27=2;
            int LA27_0 = input.LA(1);
            if ( (LA27_0==44) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // dee.g:258:11: '=' initializer
                    {
                    Object root_1 = (Object)adaptor.nil();

                    char_literal106=(Token)input.LT(1);
                    match(input,44,FOLLOW_44_in_identifierInitializerList1752); 
                    char_literal106_tree = (Object)adaptor.create(char_literal106);
                    adaptor.addChild(root_1, char_literal106_tree);

                    pushFollow(FOLLOW_initializer_in_identifierInitializerList1754);
                    initializer107=initializer();
                    _fsp--;

                    adaptor.addChild(root_1, initializer107.tree);

                    adaptor.addChild(root_0, root_1);

                    }
                    break;

            }

            // dee.g:258:29: ( ',' identifierInitializerList )?
            int alt28=2;
            int LA28_0 = input.LA(1);
            if ( (LA28_0==43) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // dee.g:258:30: ',' identifierInitializerList
                    {
                    Object root_1 = (Object)adaptor.nil();

                    char_literal108=(Token)input.LT(1);
                    match(input,43,FOLLOW_43_in_identifierInitializerList1759); 
                    char_literal108_tree = (Object)adaptor.create(char_literal108);
                    adaptor.addChild(root_1, char_literal108_tree);

                    pushFollow(FOLLOW_identifierInitializerList_in_identifierInitializerList1761);
                    identifierInitializerList109=identifierInitializerList();
                    _fsp--;

                    adaptor.addChild(root_1, identifierInitializerList109.tree);

                    adaptor.addChild(root_0, root_1);

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end identifierInitializerList

    public static class singleDeclaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start singleDeclaration
    // dee.g:261:1: singleDeclaration : typeRef IDENT ;
    public singleDeclaration_return singleDeclaration() throws RecognitionException {   
        singleDeclaration_return retval = new singleDeclaration_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IDENT111=null;
        typeRef_return typeRef110 = null;


        Object IDENT111_tree=null;

        try {
            // dee.g:261:22: ( typeRef IDENT )
            // dee.g:261:22: typeRef IDENT
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_typeRef_in_singleDeclaration1774);
            typeRef110=typeRef();
            _fsp--;

            adaptor.addChild(root_0, typeRef110.tree);
            IDENT111=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_singleDeclaration1776); 
            IDENT111_tree = (Object)adaptor.create(IDENT111);
            adaptor.addChild(root_0, IDENT111_tree);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end singleDeclaration

    public static class functionDeclarator_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start functionDeclarator
    // dee.g:264:1: functionDeclarator : IDENT parameters ;
    public functionDeclarator_return functionDeclarator() throws RecognitionException {   
        functionDeclarator_return retval = new functionDeclarator_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IDENT112=null;
        parameters_return parameters113 = null;


        Object IDENT112_tree=null;

        try {
            // dee.g:266:22: ( IDENT parameters )
            // dee.g:266:22: IDENT parameters
            {
            root_0 = (Object)adaptor.nil();

            IDENT112=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_functionDeclarator1788); 
            IDENT112_tree = (Object)adaptor.create(IDENT112);
            adaptor.addChild(root_0, IDENT112_tree);

            pushFollow(FOLLOW_parameters_in_functionDeclarator1790);
            parameters113=parameters();
            _fsp--;

            adaptor.addChild(root_0, parameters113.tree);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end functionDeclarator

    public static class parameters_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start parameters
    // dee.g:268:1: parameters : '(' parameterList ')' ;
    public parameters_return parameters() throws RecognitionException {   
        parameters_return retval = new parameters_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal114=null;
        Token char_literal116=null;
        parameterList_return parameterList115 = null;


        Object char_literal114_tree=null;
        Object char_literal116_tree=null;

        try {
            // dee.g:268:15: ( '(' parameterList ')' )
            // dee.g:268:15: '(' parameterList ')'
            {
            root_0 = (Object)adaptor.nil();

            char_literal114=(Token)input.LT(1);
            match(input,56,FOLLOW_56_in_parameters1799); 
            char_literal114_tree = (Object)adaptor.create(char_literal114);
            adaptor.addChild(root_0, char_literal114_tree);

            pushFollow(FOLLOW_parameterList_in_parameters1801);
            parameterList115=parameterList();
            _fsp--;

            adaptor.addChild(root_0, parameterList115.tree);
            char_literal116=(Token)input.LT(1);
            match(input,57,FOLLOW_57_in_parameters1803); 
            char_literal116_tree = (Object)adaptor.create(char_literal116);
            adaptor.addChild(root_0, char_literal116_tree);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end parameters

    public static class parameterList_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start parameterList
    // dee.g:270:1: parameterList : parameter ( ',' parameterList )? ;
    public parameterList_return parameterList() throws RecognitionException {   
        parameterList_return retval = new parameterList_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal118=null;
        parameter_return parameter117 = null;

        parameterList_return parameterList119 = null;


        Object char_literal118_tree=null;

        try {
            // dee.g:270:17: ( parameter ( ',' parameterList )? )
            // dee.g:270:17: parameter ( ',' parameterList )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_parameter_in_parameterList1812);
            parameter117=parameter();
            _fsp--;

            adaptor.addChild(root_0, parameter117.tree);
            // dee.g:270:27: ( ',' parameterList )?
            int alt29=2;
            int LA29_0 = input.LA(1);
            if ( (LA29_0==43) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // dee.g:270:28: ',' parameterList
                    {
                    Object root_1 = (Object)adaptor.nil();

                    char_literal118=(Token)input.LT(1);
                    match(input,43,FOLLOW_43_in_parameterList1815); 
                    char_literal118_tree = (Object)adaptor.create(char_literal118);
                    adaptor.addChild(root_1, char_literal118_tree);

                    pushFollow(FOLLOW_parameterList_in_parameterList1817);
                    parameterList119=parameterList();
                    _fsp--;

                    adaptor.addChild(root_1, parameterList119.tree);

                    adaptor.addChild(root_0, root_1);

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end parameterList

    public static class parameter_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start parameter
    // dee.g:272:1: parameter : ( singleDeclaration ( '=' assignExpression )? | inout singleDeclaration ( '=' assignExpression )? );
    public parameter_return parameter() throws RecognitionException {   
        parameter_return retval = new parameter_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal121=null;
        Token char_literal125=null;
        singleDeclaration_return singleDeclaration120 = null;

        assignExpression_return assignExpression122 = null;

        inout_return inout123 = null;

        singleDeclaration_return singleDeclaration124 = null;

        assignExpression_return assignExpression126 = null;


        Object char_literal121_tree=null;
        Object char_literal125_tree=null;

        try {
            // dee.g:273:5: ( singleDeclaration ( '=' assignExpression )? | inout singleDeclaration ( '=' assignExpression )? )
            int alt32=2;
            int LA32_0 = input.LA(1);
            if ( (LA32_0==IDENT||(LA32_0>=53 && LA32_0<=55)||(LA32_0>=58 && LA32_0<=79)) ) {
                alt32=1;
            }
            else if ( ((LA32_0>=96 && LA32_0<=99)) ) {
                alt32=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("272:1: parameter : ( singleDeclaration ( '=' assignExpression )? | inout singleDeclaration ( '=' assignExpression )? );", 32, 0, input);

                throw nvae;
            }
            switch (alt32) {
                case 1 :
                    // dee.g:273:5: singleDeclaration ( '=' assignExpression )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_singleDeclaration_in_parameter1831);
                    singleDeclaration120=singleDeclaration();
                    _fsp--;

                    adaptor.addChild(root_0, singleDeclaration120.tree);
                    // dee.g:273:23: ( '=' assignExpression )?
                    int alt30=2;
                    int LA30_0 = input.LA(1);
                    if ( (LA30_0==44) ) {
                        alt30=1;
                    }
                    switch (alt30) {
                        case 1 :
                            // dee.g:273:24: '=' assignExpression
                            {
                            Object root_1 = (Object)adaptor.nil();

                            char_literal121=(Token)input.LT(1);
                            match(input,44,FOLLOW_44_in_parameter1834); 
                            char_literal121_tree = (Object)adaptor.create(char_literal121);
                            adaptor.addChild(root_1, char_literal121_tree);

                            pushFollow(FOLLOW_assignExpression_in_parameter1836);
                            assignExpression122=assignExpression();
                            _fsp--;

                            adaptor.addChild(root_1, assignExpression122.tree);

                            adaptor.addChild(root_0, root_1);

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // dee.g:274:5: inout singleDeclaration ( '=' assignExpression )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_inout_in_parameter1844);
                    inout123=inout();
                    _fsp--;

                    adaptor.addChild(root_0, inout123.tree);
                    pushFollow(FOLLOW_singleDeclaration_in_parameter1846);
                    singleDeclaration124=singleDeclaration();
                    _fsp--;

                    adaptor.addChild(root_0, singleDeclaration124.tree);
                    // dee.g:274:29: ( '=' assignExpression )?
                    int alt31=2;
                    int LA31_0 = input.LA(1);
                    if ( (LA31_0==44) ) {
                        alt31=1;
                    }
                    switch (alt31) {
                        case 1 :
                            // dee.g:274:30: '=' assignExpression
                            {
                            Object root_1 = (Object)adaptor.nil();

                            char_literal125=(Token)input.LT(1);
                            match(input,44,FOLLOW_44_in_parameter1849); 
                            char_literal125_tree = (Object)adaptor.create(char_literal125);
                            adaptor.addChild(root_1, char_literal125_tree);

                            pushFollow(FOLLOW_assignExpression_in_parameter1851);
                            assignExpression126=assignExpression();
                            _fsp--;

                            adaptor.addChild(root_1, assignExpression126.tree);

                            adaptor.addChild(root_0, root_1);

                            }
                            break;

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end parameter

    public static class inout_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start inout
    // dee.g:277:1: inout : ('in'|'out'|'inout'|'lazy');
    public inout_return inout() throws RecognitionException {   
        inout_return retval = new inout_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set127=null;

        Object set127_tree=null;

        try {
            // dee.g:277:6: ( ('in'|'out'|'inout'|'lazy'))
            // dee.g:277:8: ('in'|'out'|'inout'|'lazy')
            {
            root_0 = (Object)adaptor.nil();

            set127=(Token)input.LT(1);
            if ( (input.LA(1)>=96 && input.LA(1)<=99) ) {
                adaptor.addChild(root_0, adaptor.create(set127));
                input.consume();
                errorRecovery=false;
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_inout1862);    throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end inout

    public static class functionBody_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start functionBody
    // dee.g:279:1: functionBody : '{' '}' ;
    public functionBody_return functionBody() throws RecognitionException {   
        functionBody_return retval = new functionBody_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal128=null;
        Token char_literal129=null;

        Object char_literal128_tree=null;
        Object char_literal129_tree=null;

        try {
            // dee.g:280:4: ( '{' '}' )
            // dee.g:280:4: '{' '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal128=(Token)input.LT(1);
            match(input,51,FOLLOW_51_in_functionBody1878); 
            char_literal128_tree = (Object)adaptor.create(char_literal128);
            adaptor.addChild(root_0, char_literal128_tree);

            char_literal129=(Token)input.LT(1);
            match(input,52,FOLLOW_52_in_functionBody1880); 
            char_literal129_tree = (Object)adaptor.create(char_literal129);
            adaptor.addChild(root_0, char_literal129_tree);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end functionBody

    public static class initializer_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start initializer
    // dee.g:285:1: initializer : ( 'void' | '=' ('*'|'+'|'-') assignExpression );
    public initializer_return initializer() throws RecognitionException {   
        initializer_return retval = new initializer_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal130=null;
        Token char_literal131=null;
        Token set132=null;
        assignExpression_return assignExpression133 = null;


        Object string_literal130_tree=null;
        Object char_literal131_tree=null;
        Object set132_tree=null;

        try {
            // dee.g:288:4: ( 'void' | '=' ('*'|'+'|'-') assignExpression )
            int alt33=2;
            int LA33_0 = input.LA(1);
            if ( (LA33_0==58) ) {
                alt33=1;
            }
            else if ( (LA33_0==44) ) {
                alt33=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("285:1: initializer : ( 'void' | '=' ('*'|'+'|'-') assignExpression );", 33, 0, input);

                throw nvae;
            }
            switch (alt33) {
                case 1 :
                    // dee.g:288:4: 'void'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal130=(Token)input.LT(1);
                    match(input,58,FOLLOW_58_in_initializer1897); 
                    string_literal130_tree = (Object)adaptor.create(string_literal130);
                    adaptor.addChild(root_0, string_literal130_tree);


                    }
                    break;
                case 2 :
                    // dee.g:289:7: '=' ('*'|'+'|'-') assignExpression
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal131=(Token)input.LT(1);
                    match(input,44,FOLLOW_44_in_initializer1906); 
                    char_literal131_tree = (Object)adaptor.create(char_literal131);
                    adaptor.addChild(root_0, char_literal131_tree);

                    set132=(Token)input.LT(1);
                    if ( (input.LA(1)>=PLUS && input.LA(1)<=MULT) ) {
                        adaptor.addChild(root_0, adaptor.create(set132));
                        input.consume();
                        errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_initializer1909);    throw mse;
                    }

                    pushFollow(FOLLOW_assignExpression_in_initializer1916);
                    assignExpression133=assignExpression();
                    _fsp--;

                    adaptor.addChild(root_0, assignExpression133.tree);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end initializer

    public static class expression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start expression
    // dee.g:327:1: expression : 'EXPRESSION' ;
    public expression_return expression() throws RecognitionException {   
        expression_return retval = new expression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal134=null;

        Object string_literal134_tree=null;

        try {
            // dee.g:328:4: ( 'EXPRESSION' )
            // dee.g:328:4: 'EXPRESSION'
            {
            root_0 = (Object)adaptor.nil();

            string_literal134=(Token)input.LT(1);
            match(input,100,FOLLOW_100_in_expression1936); 
            string_literal134_tree = (Object)adaptor.create(string_literal134);
            adaptor.addChild(root_0, string_literal134_tree);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end expression

    public static class assignExpression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start assignExpression
    // dee.g:332:1: assignExpression : 'ASSIGN EXPRESSION' ;
    public assignExpression_return assignExpression() throws RecognitionException {   
        assignExpression_return retval = new assignExpression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal135=null;

        Object string_literal135_tree=null;

        try {
            // dee.g:333:4: ( 'ASSIGN EXPRESSION' )
            // dee.g:333:4: 'ASSIGN EXPRESSION'
            {
            root_0 = (Object)adaptor.nil();

            string_literal135=(Token)input.LT(1);
            match(input,101,FOLLOW_101_in_assignExpression1949); 
            string_literal135_tree = (Object)adaptor.create(string_literal135);
            adaptor.addChild(root_0, string_literal135_tree);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        return retval;
    }
    // $ANTLR end assignExpression


 

    public static final BitSet FOLLOW_moduledeclaration_in_dmodule1014 = new BitSet(new long[]{0xFCE0060000002000L,0x00000000FFE0FFFFL});
    public static final BitSet FOLLOW_decldefs_in_dmodule1017 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_dmodule1019 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_moduledeclaration1027 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_modulename_in_moduledeclaration1029 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_moduledeclaration1031 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_modulename1039 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_decldef_in_decldefs1051 = new BitSet(new long[]{0xFCE0060000002002L,0x00000000FFE0FFFFL});
    public static final BitSet FOLLOW_importdeclaration_in_decldef1063 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_declaration_in_decldef1068 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_importdeclaration1082 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_importdeclaration1085 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_importlist_in_importdeclaration1087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleimport_in_importlist1097 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_43_in_importlist1100 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_importlist_in_importlist1102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_importbindings_in_importlist1109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modulename_in_singleimport1120 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_singleimport1125 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_singleimport1127 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_modulename_in_singleimport1129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleimport_in_importbindings1138 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_importbindings1140 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_importbind_in_importbindings1142 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_43_in_importbindings1145 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_importbind_in_importbindings1147 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_IDENT_in_importbind1160 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_importbind1165 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_importbind1167 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_IDENT_in_importbind1169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attribute_in_attributeSpecifier1187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attribute_in_attributeSpecifier1196 = new BitSet(new long[]{0xFCE8060000002000L,0x00000000FFE0FFFFL});
    public static final BitSet FOLLOW_declarationBlock_in_attributeSpecifier1198 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_attribute1233 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_attribute1237 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_attribute1241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_attribute1245 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_attribute1249 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_decldef_in_declarationBlock1294 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_declarationBlock1302 = new BitSet(new long[]{0xFCF0060000002000L,0x00000000FFE0FFFFL});
    public static final BitSet FOLLOW_decldefs_in_declarationBlock1304 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_52_in_declarationBlock1306 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_qname1321 = new BitSet(new long[]{0x0020000000000002L});
    public static final BitSet FOLLOW_templateInstance_in_qname1325 = new BitSet(new long[]{0x0020000000000002L});
    public static final BitSet FOLLOW_53_in_qname1329 = new BitSet(new long[]{0x0040000000002000L});
    public static final BitSet FOLLOW_qname_in_qname1331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_templateInstance1342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeRef_in_entityRef1354 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rootNameRef_in_entityRef1359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_55_in_rootNameRef1369 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_56_in_rootNameRef1371 = new BitSet(new long[]{0x0000000000000000L,0x0000001000000000L});
    public static final BitSet FOLLOW_expression_in_rootNameRef1373 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_57_in_rootNameRef1375 = new BitSet(new long[]{0x0020000000000002L});
    public static final BitSet FOLLOW_53_in_rootNameRef1378 = new BitSet(new long[]{0x0040000000002000L});
    public static final BitSet FOLLOW_qname_in_rootNameRef1380 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_rootNameRef1388 = new BitSet(new long[]{0x0040000000002000L});
    public static final BitSet FOLLOW_qname_in_rootNameRef1390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qname_in_rootNameRef1396 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_basicType_in_typeRef1407 = new BitSet(new long[]{0x0000000000000042L,0x00000000001B0000L});
    public static final BitSet FOLLOW_modType_in_typeRef1409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_basicType1422 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rootNameRef_in_basicType1426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_primitiveType1436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MULT_in_modType1499 = new BitSet(new long[]{0x0000000000000042L,0x00000000001B0000L});
    public static final BitSet FOLLOW_modType_in_modType1501 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_80_in_modType1507 = new BitSet(new long[]{0x0000000000000042L,0x00000000001B0000L});
    public static final BitSet FOLLOW_modType_in_modType1509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_81_in_modType1516 = new BitSet(new long[]{0x0000000000000000L,0x0000001000000000L});
    public static final BitSet FOLLOW_expression_in_modType1518 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_82_in_modType1520 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_81_in_modType1529 = new BitSet(new long[]{0xFCE0000000002000L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_typeRef_in_modType1531 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_82_in_modType1533 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_83_in_modType1542 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_parameters_in_modType1544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_84_in_modType1553 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_parameters_in_modType1555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_basicType_in_exprEnt1567 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_53_in_exprEnt1569 = new BitSet(new long[]{0x0040000000002000L});
    public static final BitSet FOLLOW_qname_in_exprEnt1571 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rootNameRef_in_exprEnt1577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_85_in_declaration1591 = new BitSet(new long[]{0xFCE0000000002000L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_typeRef_in_declaration1593 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_IDENT_in_declaration1595 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_declaration1597 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_86_in_declaration1602 = new BitSet(new long[]{0x00E0000000002000L});
    public static final BitSet FOLLOW_rootNameRef_in_declaration1604 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_IDENT_in_declaration1606 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_declaration1608 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_varDeclaration_in_declaration1613 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_87_in_autoDeclaration1625 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_identifierInitializerList_in_autoDeclaration1627 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_autoDeclaration1629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_storageclass1640 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_storageclass_in_varDeclaration1666 = new BitSet(new long[]{0xFCE0020000002000L,0x00000000FF80FFFFL});
    public static final BitSet FOLLOW_actualVarDeclaration_in_varDeclaration1669 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeRef_in_actualVarDeclaration1696 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_identifierInitializerList_in_actualVarDeclaration1698 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_actualVarDeclaration1700 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mytype_in_actualVarDeclaration1712 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_functionDeclarator_in_actualVarDeclaration1714 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_functionBody_in_actualVarDeclaration1716 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_autoDeclaration_in_actualVarDeclaration1724 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_95_in_mytype1735 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_identifierInitializerList1749 = new BitSet(new long[]{0x0000180000000002L});
    public static final BitSet FOLLOW_44_in_identifierInitializerList1752 = new BitSet(new long[]{0x0400100000000000L});
    public static final BitSet FOLLOW_initializer_in_identifierInitializerList1754 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_43_in_identifierInitializerList1759 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_identifierInitializerList_in_identifierInitializerList1761 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeRef_in_singleDeclaration1774 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_IDENT_in_singleDeclaration1776 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_functionDeclarator1788 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_parameters_in_functionDeclarator1790 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_56_in_parameters1799 = new BitSet(new long[]{0xFCE0000000002000L,0x0000000F0000FFFFL});
    public static final BitSet FOLLOW_parameterList_in_parameters1801 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_57_in_parameters1803 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parameter_in_parameterList1812 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_43_in_parameterList1815 = new BitSet(new long[]{0xFCE0000000002000L,0x0000000F0000FFFFL});
    public static final BitSet FOLLOW_parameterList_in_parameterList1817 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleDeclaration_in_parameter1831 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_44_in_parameter1834 = new BitSet(new long[]{0x0000000000000000L,0x0000002000000000L});
    public static final BitSet FOLLOW_assignExpression_in_parameter1836 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inout_in_parameter1844 = new BitSet(new long[]{0xFCE0000000002000L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_singleDeclaration_in_parameter1846 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_44_in_parameter1849 = new BitSet(new long[]{0x0000000000000000L,0x0000002000000000L});
    public static final BitSet FOLLOW_assignExpression_in_parameter1851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_inout1862 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_functionBody1878 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_52_in_functionBody1880 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_58_in_initializer1897 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_initializer1906 = new BitSet(new long[]{0x0000000000000070L});
    public static final BitSet FOLLOW_set_in_initializer1909 = new BitSet(new long[]{0x0000000000000000L,0x0000002000000000L});
    public static final BitSet FOLLOW_assignExpression_in_initializer1916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_100_in_expression1936 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_101_in_assignExpression1949 = new BitSet(new long[]{0x0000000000000002L});

}
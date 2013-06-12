package dtool.ddoc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import descent.core.ParserToolFactory;
import descent.core.compiler.IScanner;
import descent.core.compiler.ITerminalSymbols;
import descent.core.ddoc.Ddoc;
import descent.core.ddoc.DdocMacros;
import descent.core.ddoc.DdocParser;
import descent.core.ddoc.DdocSection;
import descent.core.ddoc.DdocSection.Parameter;
import descent.core.ddoc.HTMLPrinterUtils;
import dtool.ast.definitions.DefUnit;
import dtool.parser.Token;

public class DeeDocAccessor {
	
	private static Set<String> redSections;
	static {
		redSections = new TreeSet<String>();
		redSections.add("Bugs"); //$NON-NLS-1$
		redSections.add("Deprecated"); //$NON-NLS-1$
	}
	
	public static Ddoc getDdoc(DefUnit def) {
		Ddoc ddoc = null;
		Token[] preComments = def.comments;
		if (preComments != null && preComments.length > 0) {
			for(Token comment : preComments) {
				DdocParser parser = new DdocParser(comment.getSourceValue());
				Ddoc newddoc = parser.parse();
				if (ddoc == null) {
					ddoc = newddoc;
				} else {
					ddoc.merge(newddoc);
				}
			}
			return ddoc;
		}
		
		return null;
	}
	
	public static StringBuffer transform(Ddoc ddoc, Map<String, String> parameters) {
		Map<String, String> defaultMacros = DdocMacros.getDefaultMacros();
		Map<String, String> macros = mergeMacros(ddoc, defaultMacros);

		StringBuffer buffer = new StringBuffer();
		
		for(DdocSection section : ddoc.getSections()) {
			switch(section.getKind()) {
			case DdocSection.NORMAL_SECTION:
				String text = DdocMacros.replaceMacros(section.getText(), macros);
				
				if (section.getName() != null) {
					buffer.append("<dl>"); //$NON-NLS-1$
					buffer.append("<dt>"); //$NON-NLS-1$
					
					boolean red = redSections.contains(section.getName());
					if (red) {
						buffer.append("<span style=\"color:red\">"); //$NON-NLS-1$
					}
					
					buffer.append(section.getName().replace('_', ' '));
					buffer.append(":"); //$NON-NLS-1$
					
					if (red) {
						buffer.append("</span>"); //$NON-NLS-1$
					}
					
					buffer.append("</dt>"); //$NON-NLS-1$
					buffer.append("<dd>"); //$NON-NLS-1$
					buffer.append(text);
					buffer.append("</dd>"); //$NON-NLS-1$					
					buffer.append("</dl>"); //$NON-NLS-1$
				} else {
					buffer.append(text);
				}
				break;
			case DdocSection.PARAMS_SECTION:
				buffer.append("<dl>"); //$NON-NLS-1$
				buffer.append("<dt>"); //$NON-NLS-1$
				buffer.append("Parameters:"); //$NON-NLS-1$
				buffer.append("</dt>"); //$NON-NLS-1$
				for(Parameter parameter : section.getParameters()) {
					buffer.append("<dd>"); //$NON-NLS-1$
					
					String type = parameters.get(parameter.getName());
					if (type != null) {
						buffer.append(type);
						buffer.append(" "); //$NON-NLS-1$
					}
					
					buffer.append("<b>"); //$NON-NLS-1$
					buffer.append(parameter.getName());
					buffer.append("</b>"); //$NON-NLS-1$
					buffer.append(" "); //$NON-NLS-1$
					buffer.append(DdocMacros.replaceMacros(parameter.getText(), macros));
					buffer.append("</dd>"); //$NON-NLS-1$
					buffer.append("<br/>"); //$NON-NLS-1$
				}
				buffer.append("</dl>"); //$NON-NLS-1$
				break;
			case DdocSection.CODE_SECTION:
				buffer.append("<dl>"); //$NON-NLS-1$
				buffer.append("<dd class=\"code\">"); //$NON-NLS-1$
				try {
					appendCode(buffer, section.getText());
				} catch (Exception e) {
					buffer.append(section.getText());
				}
				buffer.append("</dd>"); //$NON-NLS-1$
				buffer.append("</dl>"); //$NON-NLS-1$				
				break;
			}
			
			HTMLPrinterUtils.addParagraph(buffer, ""); //$NON-NLS-1$
		}
		return buffer;
	}
	
	private static Map<String, String> mergeMacros(Ddoc ddoc, Map<String, String> macros) {
		macros = new HashMap<String, String>(macros);
		if (ddoc.getMacrosSection() != null) {
			for(Parameter param : ddoc.getMacrosSection().getParameters()) {
				macros.put(param.getName(), param.getText());
			}
		}
		return macros;
	}
	
	private static void appendCode(StringBuffer buffer, String text) throws Exception {
		// Don't format the code
		
		/*CodeFormatter formatter = ToolFactory.createCodeFormatter(null);
		try {
			// The most common example is something inside a function 
			TextEdit edit = formatter.format(CodeFormatter.K_STATEMENTS, text, 0, text.length(), 0, "\n"); //$NON-NLS-1$
			if (edit == null) {
				// If not, try parsing a whole compilation unit
				edit = formatter.format(CodeFormatter.K_COMPILATION_UNIT, text, 0, text.length(), 0, "\n"); //$NON-NLS-1$
			}
			if (edit != null) {
				Document doc = new Document(text);
				edit.apply(doc);
				text = doc.get();
			}
		} catch (Exception e) {
		}*/
		
		IScanner scanner = ParserToolFactory.createScanner(true, true, true, false);
		scanner.setSource(text.toCharArray());
		
		int token;
		while((token = scanner.getNextToken()) != ITerminalSymbols.TokenNameEOF) {
			String raw = scanner.getRawTokenSourceAsString();
			String styleClassName = null;
			switch(token) {
			case ITerminalSymbols.TokenNameabstract:
			case ITerminalSymbols.TokenNamealias:
			case ITerminalSymbols.TokenNamealign:
			case ITerminalSymbols.TokenNameasm:
			case ITerminalSymbols.TokenNameassert:
			case ITerminalSymbols.TokenNameauto:
			case ITerminalSymbols.TokenNamebody:
			case ITerminalSymbols.TokenNamebreak:
			case ITerminalSymbols.TokenNamecase:
			case ITerminalSymbols.TokenNamecast:
			case ITerminalSymbols.TokenNamecatch:
			case ITerminalSymbols.TokenNameclass:
			case ITerminalSymbols.TokenNameconst:
			case ITerminalSymbols.TokenNamecontinue:
			case ITerminalSymbols.TokenNamedebug:
			case ITerminalSymbols.TokenNamedefault:
			case ITerminalSymbols.TokenNamedelegate:
			case ITerminalSymbols.TokenNamedelete:
			case ITerminalSymbols.TokenNamedeprecated:
			case ITerminalSymbols.TokenNamedo:
			case ITerminalSymbols.TokenNameelse:
			case ITerminalSymbols.TokenNameenum:
			case ITerminalSymbols.TokenNameexport:
			case ITerminalSymbols.TokenNameextern:
			case ITerminalSymbols.TokenNamefinal:
			case ITerminalSymbols.TokenNamefinally:
			case ITerminalSymbols.TokenNamefor:
			case ITerminalSymbols.TokenNameforeach:
			case ITerminalSymbols.TokenNameforeach_reverse:
			case ITerminalSymbols.TokenNamefunction:
			case ITerminalSymbols.TokenNamegoto:
			case ITerminalSymbols.TokenNameif:
			case ITerminalSymbols.TokenNameiftype:
			case ITerminalSymbols.TokenNameimport:
			case ITerminalSymbols.TokenNamein:
			case ITerminalSymbols.TokenNameinout:
			case ITerminalSymbols.TokenNameinterface:
			case ITerminalSymbols.TokenNameinvariant:
			case ITerminalSymbols.TokenNameis:
			case ITerminalSymbols.TokenNamelazy:
			case ITerminalSymbols.TokenNamemacro:
			case ITerminalSymbols.TokenNamemixin:
			case ITerminalSymbols.TokenNamemodule:
			case ITerminalSymbols.TokenNamenew:
			case ITerminalSymbols.TokenNameout:
			case ITerminalSymbols.TokenNameoverride:
			case ITerminalSymbols.TokenNamepackage:
			case ITerminalSymbols.TokenNamepragma:
			case ITerminalSymbols.TokenNameprivate:
			case ITerminalSymbols.TokenNameprotected:
			case ITerminalSymbols.TokenNamepublic:
			case ITerminalSymbols.TokenNameref:
			case ITerminalSymbols.TokenNamescope:
			case ITerminalSymbols.TokenNamestatic:
			case ITerminalSymbols.TokenNamestruct:
			case ITerminalSymbols.TokenNamesuper:
			case ITerminalSymbols.TokenNameswitch:
			case ITerminalSymbols.TokenNamesynchronized:
			case ITerminalSymbols.TokenNametemplate:
			case ITerminalSymbols.TokenNamethis:
			case ITerminalSymbols.TokenNamethrow:
			case ITerminalSymbols.TokenNametry:
			case ITerminalSymbols.TokenNametypedef:
			case ITerminalSymbols.TokenNametypeid:
			case ITerminalSymbols.TokenNametypeof:
			case ITerminalSymbols.TokenNameunion:
			case ITerminalSymbols.TokenNameunittest:
			case ITerminalSymbols.TokenNameversion:
			case ITerminalSymbols.TokenNamevolatile:
			case ITerminalSymbols.TokenNamewhile:
			case ITerminalSymbols.TokenNamewith:
				
			case ITerminalSymbols.TokenNamebool:
			case ITerminalSymbols.TokenNamebyte:
			case ITerminalSymbols.TokenNamecdouble:
			case ITerminalSymbols.TokenNamecent:
			case ITerminalSymbols.TokenNamecfloat:
			case ITerminalSymbols.TokenNamechar:
			case ITerminalSymbols.TokenNamecreal:
			case ITerminalSymbols.TokenNamedchar:
			case ITerminalSymbols.TokenNamedouble:
			case ITerminalSymbols.TokenNamefloat:
			case ITerminalSymbols.TokenNameidouble:
			case ITerminalSymbols.TokenNameifloat:
			case ITerminalSymbols.TokenNameint:
			case ITerminalSymbols.TokenNameireal:
			case ITerminalSymbols.TokenNamelong:
			case ITerminalSymbols.TokenNamereal:
			case ITerminalSymbols.TokenNameshort:
			case ITerminalSymbols.TokenNameubyte:
			case ITerminalSymbols.TokenNameucent:
			case ITerminalSymbols.TokenNameuint:
			case ITerminalSymbols.TokenNameulong:
			case ITerminalSymbols.TokenNameushort:
			case ITerminalSymbols.TokenNamevoid:
			case ITerminalSymbols.TokenNamewchar:
				
			case ITerminalSymbols.TokenName__traits:
				styleClassName = IDeeDocColorConstants.JAVA_KEYWORD;
				break;
			case ITerminalSymbols.TokenNamereturn:
				styleClassName = IDeeDocColorConstants.JAVA_KEYWORD_RETURN;
				break;
			case ITerminalSymbols.TokenNameAND:
			case ITerminalSymbols.TokenNameAND_AND:
			case ITerminalSymbols.TokenNameAND_EQUAL:
			case ITerminalSymbols.TokenNameCOLON:
			case ITerminalSymbols.TokenNameCOMMA:
			case ITerminalSymbols.TokenNameDIVIDE:
			case ITerminalSymbols.TokenNameDIVIDE_EQUAL:
			case ITerminalSymbols.TokenNameDOLLAR:
			case ITerminalSymbols.TokenNameDOT:
			case ITerminalSymbols.TokenNameDOT_DOT:
			case ITerminalSymbols.TokenNameDOT_DOT_DOT:
			case ITerminalSymbols.TokenNameEQUAL:
			case ITerminalSymbols.TokenNameEQUAL_EQUAL:
			case ITerminalSymbols.TokenNameEQUAL_EQUAL_EQUAL:
			case ITerminalSymbols.TokenNameGREATER:
			case ITerminalSymbols.TokenNameGREATER_EQUAL:
			case ITerminalSymbols.TokenNameLBRACE:
			case ITerminalSymbols.TokenNameLBRACKET:
			case ITerminalSymbols.TokenNameLEFT_SHIFT:
			case ITerminalSymbols.TokenNameLEFT_SHIFT_EQUAL:
			case ITerminalSymbols.TokenNameLESS:
			case ITerminalSymbols.TokenNameLESS_EQUAL:
			case ITerminalSymbols.TokenNameLESS_GREATER:
			case ITerminalSymbols.TokenNameLESS_GREATER_EQUAL:
			case ITerminalSymbols.TokenNameLPAREN:
			case ITerminalSymbols.TokenNameMINUS:
			case ITerminalSymbols.TokenNameMINUS_EQUAL:
			case ITerminalSymbols.TokenNameMINUS_MINUS:
			case ITerminalSymbols.TokenNameMULTIPLY:
			case ITerminalSymbols.TokenNameMULTIPLY_EQUAL:
			case ITerminalSymbols.TokenNameNOT:
			case ITerminalSymbols.TokenNameNOT_EQUAL:
			case ITerminalSymbols.TokenNameNOT_EQUAL_EQUAL:
			case ITerminalSymbols.TokenNameNOT_GREATER:
			case ITerminalSymbols.TokenNameNOT_GREATER_EQUAL:
			case ITerminalSymbols.TokenNameNOT_LESS:
			case ITerminalSymbols.TokenNameNOT_LESS_EQUAL:
			case ITerminalSymbols.TokenNameNOT_LESS_GREATER:
			case ITerminalSymbols.TokenNameNOT_LESS_GREATER_EQUAL:
			case ITerminalSymbols.TokenNameOR:
			case ITerminalSymbols.TokenNameOR_EQUAL:
			case ITerminalSymbols.TokenNameOR_OR:
			case ITerminalSymbols.TokenNamePLUS:
			case ITerminalSymbols.TokenNamePLUS_EQUAL:
			case ITerminalSymbols.TokenNamePLUS_PLUS:
			case ITerminalSymbols.TokenNameQUESTION:
			case ITerminalSymbols.TokenNameRBRACE:
			case ITerminalSymbols.TokenNameRBRACKET:
			case ITerminalSymbols.TokenNameREMAINDER:
			case ITerminalSymbols.TokenNameREMAINDER_EQUAL:
			case ITerminalSymbols.TokenNameRIGHT_SHIFT:
			case ITerminalSymbols.TokenNameRIGHT_SHIFT_EQUAL:
			case ITerminalSymbols.TokenNameRPAREN:
			case ITerminalSymbols.TokenNameSEMICOLON:
			case ITerminalSymbols.TokenNameTILDE:
			case ITerminalSymbols.TokenNameTILDE_EQUAL:
			case ITerminalSymbols.TokenNameUNSIGNED_RIGHT_SHIFT:
			case ITerminalSymbols.TokenNameUNSIGNED_RIGHT_SHIFT_EQUAL:
			case ITerminalSymbols.TokenNameXOR:
			case ITerminalSymbols.TokenNameXOR_EQUAL:
				styleClassName = IDeeDocColorConstants.JAVA_OPERATOR;				
				break;
			case ITerminalSymbols.TokenNamePRAGMA:
				styleClassName = IDeeDocColorConstants.JAVA_PRAGMA;
				break;
			case ITerminalSymbols.TokenNameCharacterLiteral:
			case ITerminalSymbols.TokenNameStringLiteral:
				styleClassName = IDeeDocColorConstants.JAVA_STRING;
				break;
			case ITerminalSymbols.TokenNameCOMMENT_BLOCK:
				styleClassName = IDeeDocColorConstants.JAVA_MULTI_LINE_COMMENT;
				break;
			case ITerminalSymbols.TokenNameCOMMENT_DOC_BLOCK:
				styleClassName = IDeeDocColorConstants.JAVADOC_DEFAULT;
				break;
			case ITerminalSymbols.TokenNameCOMMENT_DOC_LINE:
				styleClassName = IDeeDocColorConstants.JAVA_SINGLE_LINE_DOC_COMMENT;
				raw += "<br/>"; //$NON-NLS-1$
				break;
			case ITerminalSymbols.TokenNameCOMMENT_DOC_PLUS:
				styleClassName = IDeeDocColorConstants.JAVA_MULTI_LINE_PLUS_DOC_COMMENT;
				break;
			case ITerminalSymbols.TokenNameCOMMENT_LINE:
				styleClassName = IDeeDocColorConstants.JAVA_SINGLE_LINE_COMMENT;
				raw += "<br/>"; //$NON-NLS-1$
				break;
			case ITerminalSymbols.TokenNameCOMMENT_PLUS:
				styleClassName = IDeeDocColorConstants.JAVA_MULTI_LINE_PLUS_COMMENT;
				break;
			case ITerminalSymbols.TokenNameWHITESPACE:
				styleClassName = null;
			default:
				styleClassName = IDeeDocColorConstants.JAVA_DEFAULT;
			}
			if (styleClassName != null) {
				buffer.append("<span class=\"" + styleClassName + "\">"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			if (token == ITerminalSymbols.TokenNameWHITESPACE) {
				raw = raw.replace(" ", "&nbsp;"); //$NON-NLS-1$ //$NON-NLS-2$
				raw = raw.replace("\n", "<br/>"); //$NON-NLS-1$ //$NON-NLS-2$
				raw = raw.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			buffer.append(raw);
			if (styleClassName != null) {
				buffer.append("</span>"); //$NON-NLS-1$
			}
		}
	}



}

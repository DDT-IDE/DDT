package dtool.ddoc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import descent.core.ddoc.Ddoc;
import descent.core.ddoc.DdocMacros;
import descent.core.ddoc.DdocParser;
import descent.core.ddoc.DdocSection;
import descent.core.ddoc.DdocSection.Parameter;
import descent.core.ddoc.HTMLPrinterUtils;
import dtool.ast.definitions.DefUnit;
import dtool.parser.DeeLexer;
import dtool.parser.DeeTokenHelper;
import dtool.parser.DeeTokens;
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
		Token[] preComments = def.getDocComments();
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
		
		DeeLexer scanner = new DeeLexer(text);
		
		Token token;
		while((token = scanner.next()).type != DeeTokens.EOF) {
			String raw = token.getSourceValue();
			String styleClassName = null;
			
			switch(token.type) {
			case KW_RETURN:
				styleClassName = IDeeDocColorConstants.JAVA_KEYWORD_RETURN;
				break;
			case SPECIAL_TOKEN_LINE:
				styleClassName = IDeeDocColorConstants.JAVA_PRAGMA;
				break;
			case COMMENT_LINE:
				styleClassName = IDeeDocColorConstants.JAVA_SINGLE_LINE_COMMENT;
				raw += "<br/>"; //$NON-NLS-1$
				break;
			case COMMENT_MULTI:
				styleClassName = IDeeDocColorConstants.JAVA_MULTI_LINE_COMMENT;
				break;
			case COMMENT_NESTED:
				styleClassName = IDeeDocColorConstants.JAVA_MULTI_LINE_PLUS_COMMENT;
				break;
			case DOCCOMMENT_MULTI:
				styleClassName = IDeeDocColorConstants.JAVADOC_DEFAULT;
				break;
			case DOCCOMMENT_LINE:
				styleClassName = IDeeDocColorConstants.JAVA_SINGLE_LINE_DOC_COMMENT;
				raw += "<br/>"; //$NON-NLS-1$
				break;
			case DOCCOMMENT_NESTED:
				styleClassName = IDeeDocColorConstants.JAVA_MULTI_LINE_PLUS_DOC_COMMENT;
				break;
			case EOL:
			case WHITESPACE:
				styleClassName = null;
				raw = raw.replace(" ", "&nbsp;");
				raw = raw.replace("\n", "<br/>");
				raw = raw.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
				break;
			default:
				
				DeeTokens tokenTypeGroup = token.type.getGroupingToken();
				
				if(DeeTokenHelper.isKeyword(token.type)) {
					styleClassName = IDeeDocColorConstants.JAVA_KEYWORD;
				} else {
					if(tokenTypeGroup == DeeTokens.STRING || token.type.getGroupingToken() == DeeTokens.CHARACTER) {
						styleClassName = IDeeDocColorConstants.JAVA_STRING;
					}  else {
						styleClassName = IDeeDocColorConstants.JAVA_DEFAULT;
					}
				}
			}
			
			if (styleClassName != null) {
				buffer.append("<span class=\"" + styleClassName + "\">");
			}
			
			buffer.append(raw);
			if (styleClassName != null) {
				buffer.append("</span>");
			}
		}
	}
	
}

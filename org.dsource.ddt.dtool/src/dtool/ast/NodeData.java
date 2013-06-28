package dtool.ast;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import dtool.parser.ParserError;

public abstract class NodeData {
	
	public abstract boolean isParsedStatus();
	
	public abstract Collection<ParserError> getNodeErrors();
	
	public boolean hasErrors() {
		return getNodeErrors().size() > 0;
	}
	
	@SuppressWarnings("unused")
	public void attachedToNode( ASTNode node) {
	}
	
	public static NodeData CREATED_STATUS = new PreParseNodeData() {
		
		@Override
		public String toString() {
			return "(CREATED)";
		}
		
	};
	
	public void setPostParseStatus() {
		// TODO 
	}
	
	public static class PreParseNodeData extends NodeData {

		@Override
		public boolean isParsedStatus() {
			return false;
		}
		
		@Override
		public Collection<ParserError> getNodeErrors() {
			throw assertFail();
		};
		
	}
	
	public static NodeData DEFAULT_PARSED_STATUS = new ParsedNodeData();
	
	public static class ParsedNodeData extends NodeData {
		
		protected static final Collection<ParserError> NO_ERRORS = Collections.emptyList(); 
		
		@Override
		public boolean isParsedStatus() {
			return true;
		}
		
		@Override
		public void attachedToNode(final ASTNode node) {
			// Ensure children are also in parsed status
			node.accept(new ASTDirectChildrenVisitor() {
				@Override
				protected void geneticChildrenVisit(ASTNode child) {
					assertTrue(child.getParent() == node);
					assertTrue(child.isParsedStatus());
				}
			});
		}
		
		@Override
		public Collection<ParserError> getNodeErrors() {
			return NO_ERRORS;
		};
		
		@Override
		public String toString() {
			return "(PARSED)";
		}
		
	}
	
	public static class ParsedNodeDataWithErrors extends ParsedNodeData {
		
		protected Collection<ParserError> errors;
		
		public ParsedNodeDataWithErrors(ParserError... errors) {
			for (ParserError parserError : errors) {
				assertNotNull(parserError);
			}
			this.errors = Arrays.asList(errors); 
		}
		
		@Override
		public Collection<ParserError> getNodeErrors() {
			return errors;
		};
		
	}
	
}
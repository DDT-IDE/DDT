package dtool.ast;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.downCast;


public final class SourceRange {
	
	private final int offset;
	private final int length;
	
	public SourceRange(int offset, int length) {
		this(offset, length, true);
	}
	
	public SourceRange(int offset, int length, boolean mustBeValid) {
		if(mustBeValid) {
			assertTrue(offset >= 0);
			assertTrue(length > 0);
		}
		this.offset = offset;
		this.length = length;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public int getLength() {
		return length;
	}
	
	public int getStartPos() {
		return getOffset();
	}
	
	public int getEndPos() {
		return getOffset() + getLength();
	}
	
	@Override
	public String toString() {
		return "[" + getStartPos() + "-" + getEndPos() + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof SourceRange))
			return false;
		
		SourceRange other = downCast(obj);
		return this.offset == other.offset && this.length == other.length;
	}
}
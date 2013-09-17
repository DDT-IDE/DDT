package dtool.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import melnorme.utilbox.misc.ArrayUtil;
import descent.core.ddoc.Ddoc;
import descent.core.ddoc.DdocParser;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.IntrinsicDefUnit;
import dtool.util.ArrayView;

/** 
 * A module like class, contained all primitive defunits. 
 */
public class NativesScope implements IScopeProvider {
	
	public static final NativesScope nativesScope = new NativesScope();
	
	public final ArrayView<IntrinsicDefUnit> intrinsics;
	
	public NativesScope() {
		ArrayList<IntrinsicDefUnit> intrincsList = new ArrayList<>();
	    
		intrincsList.add(new PrimitiveDefUnit("bool"));
		intrincsList.add(new PrimitiveDefUnit("byte"));
		intrincsList.add(new PrimitiveDefUnit("ubyte"));
		intrincsList.add(new PrimitiveDefUnit("short"));
		intrincsList.add(new PrimitiveDefUnit("ushort"));
		intrincsList.add(new PrimitiveDefUnit("int"));
		intrincsList.add(new PrimitiveDefUnit("uint"));
		intrincsList.add(new PrimitiveDefUnit("long"));
		intrincsList.add(new PrimitiveDefUnit("ulong"));
		intrincsList.add(new PrimitiveDefUnit("char"));
		intrincsList.add(new PrimitiveDefUnit("wchar"));
		intrincsList.add(new PrimitiveDefUnit("dchar"));
		intrincsList.add(new PrimitiveDefUnit("float"));
		intrincsList.add(new PrimitiveDefUnit("double"));
		intrincsList.add(new PrimitiveDefUnit("real"));
		
		intrincsList.add(new PrimitiveDefUnit("void"));
		
		intrincsList.add(new PrimitiveDefUnit("ifloat"));
		intrincsList.add(new PrimitiveDefUnit("idouble"));
		intrincsList.add(new PrimitiveDefUnit("ireal"));
		intrincsList.add(new PrimitiveDefUnit("cfloat"));
		intrincsList.add(new PrimitiveDefUnit("cdouble"));
		intrincsList.add(new PrimitiveDefUnit("creal"));
		
		IntrinsicDefUnit[] createFrom = ArrayUtil.createFrom(intrincsList, IntrinsicDefUnit.class);
		intrinsics = ArrayView.create(createFrom);
	}
	
	@Override
	public void resolveSearchInScope(CommonDefUnitSearch search) {
		ReferenceResolver.findInNamedElementList(search, intrinsics);
	}
	
	public static interface IPrimitiveDefUnit {
		
	}
	
	public static class PrimitiveDefUnit extends IntrinsicDefUnit implements IPrimitiveDefUnit {
		
		public PrimitiveDefUnit(String name) {
			super(name);
		}
		
		@Override
		public Ddoc resolveDDoc() {
			return null;
		}
		
		@Override
		public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
			// TODO
		}
	}
	
	public static class IntrinsicProperty extends IntrinsicDefUnit {
		
		public final Ddoc ddoc;
		
		public IntrinsicProperty(String name, Ddoc ddoc) {
			super(name);
			this.ddoc = ddoc;
		}
		
		@Override
		public EArcheType getArcheType() {
			return EArcheType.Variable;
		}
		
		@Override
		public Ddoc resolveDDoc() {
			return ddoc;
		}
		
		@Override
		public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
			// TODO
		}
	}
	
	protected static List<IntrinsicDefUnit> typeProperties = Arrays.<IntrinsicDefUnit>asList(
		new IntrinsicProperty("init", parseDDoc("initializer")),
		new IntrinsicProperty("sizeof", parseDDoc("size in bytes (equivalent to C's $(D sizeof(type)))")),
		new IntrinsicProperty("alignof", parseDDoc("alignment size")),
		new IntrinsicProperty("mangleof", parseDDoc("string representing the ‘mangled’ representation of the type")),
		new IntrinsicProperty("stringof", parseDDoc("string representing the source representation of the type"))
	);
	
	public static Ddoc parseDDoc(String ddocSource) {
		return new DdocParser("/**" + ddocSource + "*/").parse();
	}
	
	public static void resolveSearchInTypePropertiesScope(CommonDefUnitSearch search) {
		ReferenceResolver.findInNamedElementList(search, typeProperties);
	}
	
}
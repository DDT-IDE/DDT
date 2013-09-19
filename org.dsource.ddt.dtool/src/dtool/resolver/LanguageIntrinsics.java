package dtool.resolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import descent.core.ddoc.Ddoc;
import descent.core.ddoc.DdocParser;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.IntrinsicDefUnit;
import dtool.util.ArrayView;

/** 
 * Aggregator for all language intrinsics 
 */
public class LanguageIntrinsics {
	
	public static interface IPrimitiveDefUnit { }
	
	public static class PrimitiveDefUnit extends IntrinsicDefUnit implements IPrimitiveDefUnit {
		
		protected final Collection<IntrinsicDefUnit> members;
		
		public PrimitiveDefUnit(String name, List<IntrinsicDefUnit> members) {
			super(name);
			this.members = members;
		}
		
		public PrimitiveDefUnit(String name) {
			this(name, null);
		}
		
		@Override
		public Ddoc resolveDDoc() {
			return null;
		}
		
		@Override
		public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
			ReferenceResolver.findInNamedElementList(search, members);
		}
	}
	
	public static class IntrinsicScope implements IScopeProvider {
		
		public final ArrayView<IntrinsicDefUnit> intrinsics;
		
		public IntrinsicScope(IntrinsicDefUnit... intrinsics) {
			this.intrinsics = ArrayView.create(intrinsics);
		}
		
		@Override
		public void resolveSearchInScope(CommonDefUnitSearch search) {
			ReferenceResolver.findInNamedElementList(search, intrinsics);
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
	
	public static LanguageIntrinsics d_2_063_intrinsics = new LanguageIntrinsics();
	
	public final IntrinsicScope primitivesScope;
	public final IntrinsicScope typePropertiesScope;
	public final IntrinsicScope objectPropertiesScope;
	
	public LanguageIntrinsics() {
		typePropertiesScope = new IntrinsicScope(
			new IntrinsicProperty("init", parseDDoc("initializer")),
			new IntrinsicProperty("sizeof", parseDDoc("size in bytes (equivalent to C's $(D sizeof(type)))")),
			new IntrinsicProperty("alignof", parseDDoc("alignment size")),
			new IntrinsicProperty("mangleof", 
				parseDDoc("string representing the ‘mangled’ representation of the type")),
			new IntrinsicProperty("stringof", parseDDoc("string representing the source representation of the type"))
		);
		
		List<IntrinsicDefUnit> integralProperties = Arrays.<IntrinsicDefUnit>asList(
			new IntrinsicProperty("max", parseDDoc("maximum value")),
			new IntrinsicProperty("min", parseDDoc("minimum value"))
		);
		
		List<IntrinsicDefUnit> floatProperties = Arrays.<IntrinsicDefUnit>asList(
			new IntrinsicProperty("max", parseDDoc("maximum value")),
			new IntrinsicProperty("infinity", parseDDoc("infinity value")),
			new IntrinsicProperty("nan", parseDDoc("NaN value")),
			new IntrinsicProperty("dig", parseDDoc("number of decimal digits of precision")),
			new IntrinsicProperty("epsilon", parseDDoc("smallest increment to the value 1")),
			new IntrinsicProperty("mant_dig", parseDDoc("number of bits in mantissa")),
			new IntrinsicProperty("max_10_exp", parseDDoc("maximum int value such that 10⊃ is representable")),
			new IntrinsicProperty("max_exp", parseDDoc("maximum int value such that 2⊃ is representable")),
			new IntrinsicProperty("min_10_exp", 
				parseDDoc("minimum int value such that 10⊃ is representable as a normalized value")),
			new IntrinsicProperty("min_exp", 
				parseDDoc("minimum int value such that 2⊃ is representable as a normalized value")),
			new IntrinsicProperty("max", parseDDoc("largest representable value that's not infinity")),
			new IntrinsicProperty("min_normal", parseDDoc("smallest representable normalized value that's not 0")),
			new IntrinsicProperty("re", parseDDoc("real part")),
			new IntrinsicProperty("im", parseDDoc("imaginary part"))
		);
		
		objectPropertiesScope = new IntrinsicScope(
			new IntrinsicProperty("classinfo", parseDDoc("Information about the dynamic type of the class"))
		);
		
		primitivesScope = new IntrinsicScope(
			new PrimitiveDefUnit("void"),
			
			new PrimitiveDefUnit("bool"),
			
			new PrimitiveDefUnit("byte", integralProperties),
			new PrimitiveDefUnit("ubyte", integralProperties),
			new PrimitiveDefUnit("short", integralProperties),
			new PrimitiveDefUnit("ushort", integralProperties),
			new PrimitiveDefUnit("int", integralProperties),
			new PrimitiveDefUnit("uint", integralProperties),
			new PrimitiveDefUnit("long", integralProperties),
			new PrimitiveDefUnit("ulong", integralProperties),
			new PrimitiveDefUnit("char", integralProperties),
			new PrimitiveDefUnit("wchar", integralProperties),
			new PrimitiveDefUnit("dchar", integralProperties),
			
			new PrimitiveDefUnit("float", floatProperties),
			new PrimitiveDefUnit("double", floatProperties),
			new PrimitiveDefUnit("real", floatProperties),
			
			new PrimitiveDefUnit("ifloat", floatProperties),
			new PrimitiveDefUnit("idouble", floatProperties),
			new PrimitiveDefUnit("ireal", floatProperties),
			new PrimitiveDefUnit("cfloat", floatProperties),
			new PrimitiveDefUnit("cdouble", floatProperties),
			new PrimitiveDefUnit("creal", floatProperties)
		);
	}
	
	// helper class
	protected static Ddoc parseDDoc(String ddocSource) {
		return new DdocParser("/**" + ddocSource + "*/").parse();
	}
	
}
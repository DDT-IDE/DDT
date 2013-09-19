package dtool.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import descent.core.ddoc.Ddoc;
import descent.core.ddoc.DdocParser;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.INamedElement;
import dtool.ast.definitions.IntrinsicDefUnit;
import dtool.ast.definitions.Module;
import dtool.resolver.api.IModuleResolver;
import dtool.util.ArrayView;

/** 
 * Aggregator for all language intrinsics 
 */
public class LanguageIntrinsics {
	
	public static interface IPrimitiveDefUnit { }
	
	public static class PrimitiveDefUnit extends IntrinsicDefUnit implements IPrimitiveDefUnit, IResolvable {
		
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
		
		Collection<INamedElement> singletonList = Collections.<INamedElement>singletonList(this);
		
		@Override
		public Collection<INamedElement> findTargetDefElements(IModuleResolver mr, boolean findFirstOnly) {
			return singletonList;
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
		public final IResolvable type;
		
		public IntrinsicProperty(String name, IResolvable type, Ddoc ddoc) {
			super(name);
			this.type = type;
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
			DefUnit.resolveSearchInReferredContainer(search, type);
		}
	}
	
	public static class FullyQualifiedReference implements IResolvable {
		
		public final String moduleFullName;
		public final String elementName;
		
		public FullyQualifiedReference(String moduleFullName, String elementName) {
			this.moduleFullName = moduleFullName;
			this.elementName = elementName;
		}
		
		@Override
		public Collection<INamedElement> findTargetDefElements(IModuleResolver mr, boolean findFirstOnly) {
			Module module = ResolverUtil.findModule_unchecked(mr, moduleFullName);
			if(module == null) 
				return null;
			
			DefUnitSearch search = new DefUnitSearch(elementName, null, -1, findFirstOnly, mr);
			module.resolveSearchInScope(search);
			return search.getMatchedElements();
		}
		
	}
	
	public static LanguageIntrinsics d_2_063_intrinsics = new LanguageIntrinsics();
	
	public final IntrinsicScope primitivesScope;
	public final IntrinsicScope typePropertiesScope;
	public final IntrinsicScope objectPropertiesScope;
	
	public LanguageIntrinsics() {
		
		ArrayList<IntrinsicDefUnit> integralProperties = new ArrayList<>();
		ArrayList<IntrinsicDefUnit> floatProperties = new ArrayList<>();
		
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
		
		PrimitiveDefUnit integral_type = new PrimitiveDefUnit("_integral_", integralProperties);
		PrimitiveDefUnit float_type = new PrimitiveDefUnit("_float_", floatProperties);
		FullyQualifiedReference string_type = new FullyQualifiedReference("object", "string");
		
		typePropertiesScope = new IntrinsicScope(
			new IntrinsicProperty("init", null, parseDDoc("initializer")),
			new IntrinsicProperty("sizeof", integral_type, 
				parseDDoc("size in bytes (equivalent to C's $(D sizeof(type)))")),
			new IntrinsicProperty("alignof", integral_type, parseDDoc("alignment size")),
			new IntrinsicProperty("mangleof", string_type,
				parseDDoc("string representing the ‘mangled’ representation of the type")),
			new IntrinsicProperty("stringof", string_type, 
				parseDDoc("string representing the source representation of the type"))
		);
		
		integralProperties.addAll(Arrays.<IntrinsicDefUnit>asList(
			new IntrinsicProperty("init", integral_type, parseDDoc("initializer")),
			
			new IntrinsicProperty("max", integral_type, parseDDoc("maximum value")),
			new IntrinsicProperty("min", integral_type, parseDDoc("minimum value"))
		));
		
		floatProperties.addAll(Arrays.<IntrinsicDefUnit>asList(
			new IntrinsicProperty("init", float_type, parseDDoc("initializer")),
			
			new IntrinsicProperty("max", float_type, parseDDoc("maximum value")),
			new IntrinsicProperty("infinity", float_type, parseDDoc("infinity value")),
			new IntrinsicProperty("nan", float_type, parseDDoc("NaN value")),
			new IntrinsicProperty("dig", integral_type, parseDDoc("number of decimal digits of precision")),
			new IntrinsicProperty("epsilon", float_type, parseDDoc("smallest increment to the value 1")),
			new IntrinsicProperty("mant_dig", integral_type, parseDDoc("number of bits in mantissa")),
			new IntrinsicProperty("max_10_exp", integral_type, 
				parseDDoc("maximum int value such that 10⊃ is representable")),
			new IntrinsicProperty("max_exp", integral_type, 
				parseDDoc("maximum int value such that 2⊃ is representable")),
			new IntrinsicProperty("min_10_exp", integral_type,  
				parseDDoc("minimum int value such that 10⊃ is representable as a normalized value")),
			new IntrinsicProperty("min_exp", integral_type, 
				parseDDoc("minimum int value such that 2⊃ is representable as a normalized value")),
			new IntrinsicProperty("max", float_type, 
				parseDDoc("largest representable value that's not infinity")),
			new IntrinsicProperty("min_normal", float_type, 
				parseDDoc("smallest representable normalized value that's not 0")),
			new IntrinsicProperty("re", float_type, parseDDoc("real part")),
			new IntrinsicProperty("im", float_type, parseDDoc("imaginary part"))
		));
		
		IResolvable classinfoRef = new FullyQualifiedReference("object", "TypeInfo_Class");
		objectPropertiesScope = new IntrinsicScope(
			new IntrinsicProperty("classinfo", classinfoRef, 
				parseDDoc("Information about the dynamic type of the class"))
		);
		
	}
	
	// helper class
	protected static Ddoc parseDDoc(String ddocSource) {
		return new DdocParser("/**" + ddocSource + "*/").parse();
	}
	
}
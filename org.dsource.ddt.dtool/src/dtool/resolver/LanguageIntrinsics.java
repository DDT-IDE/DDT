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
	
	public static abstract class IntrinsicTypeDefUnit extends IntrinsicDefUnit implements IResolvable {
		
		public IntrinsicTypeDefUnit(String name) {
			super(name);
		}
		
		Collection<INamedElement> singletonList = Collections.<INamedElement>singletonList(this);
		
		@Override
		public Collection<INamedElement> findTargetDefElements(IModuleResolver mr, boolean findFirstOnly) {
			return singletonList;
		}
		
	}
	
	public static interface IPrimitiveDefUnit { }
	
	public static class PrimitiveDefUnit extends IntrinsicTypeDefUnit implements IPrimitiveDefUnit {
		
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
	
	// helper class
	protected static Ddoc parseDDoc(String ddocSource) {
		return new DdocParser("/**" + ddocSource + "*/").parse();
	}
	
	public static LanguageIntrinsics d_2_063_intrinsics = new LanguageIntrinsics();
	
	public LanguageIntrinsics() {} 
	
	// To be filled later
	protected final ArrayList<IntrinsicDefUnit> integralProperties = new ArrayList<>();
	protected final ArrayList<IntrinsicDefUnit> floatProperties = new ArrayList<>();
	
	public final IntrinsicScope primitivesScope = new IntrinsicScope(
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
	
	public final PrimitiveDefUnit integral_type = new PrimitiveDefUnit("_integral_", integralProperties);
	public final PrimitiveDefUnit float_type  = new PrimitiveDefUnit("_float_", floatProperties);
	public final FullyQualifiedReference string_type = new FullyQualifiedReference("object", "string");
	
	public final IntrinsicScope typePropertiesScope = new IntrinsicScope(
		new IntrinsicProperty("init", null, parseDDoc("initializer")),
		new IntrinsicProperty("sizeof", integral_type, 
			parseDDoc("size in bytes (equivalent to C's $(D sizeof(type)))")),
		new IntrinsicProperty("alignof", integral_type, parseDDoc("alignment size")),
		new IntrinsicProperty("mangleof", string_type,
			parseDDoc("string representing the ‘mangled’ representation of the type")),
		new IntrinsicProperty("stringof", string_type, 
			parseDDoc("string representing the source representation of the type"))
	);
	
	public final IntrinsicScope objectPropertiesScope = new IntrinsicScope(
		new IntrinsicProperty("classinfo", new FullyQualifiedReference("object", "TypeInfo_Class"), 
			parseDDoc("Information about the dynamic type of the class"))
	);
	
	{
		
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
	}
	
	public final IntrinsicTypePointer pointerType = new IntrinsicTypePointer();
	
	public static class IntrinsicTypePointer extends IntrinsicTypeDefUnit {
		public IntrinsicTypePointer() {
			super("<pointer>");
		}
		
		@Override
		public Ddoc resolveDDoc() {
			return null;
		}
		
		@Override
		public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		}
	}
	
	public final IntrinsicStaticArray staticArrayType = new IntrinsicStaticArray();
	public final IntrinsicDynArray dynArrayType = new IntrinsicDynArray();
	
	public static class IntrinsicStaticArray extends IntrinsicTypeDefUnit {
		
		public final Ddoc ddoc = parseDDoc("see http://dlang.org/arrays.html#static-arrays");
		public IntrinsicScope membersScope;
		
		public IntrinsicStaticArray() {
			super("<static_array>");
		}
		
		@Override
		public Ddoc resolveDDoc() {
			return null;
		}
		
		@Override
		public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
			membersScope.resolveSearchInScope(search);
		}
		
	}
	
	{
		
		staticArrayType.membersScope = new IntrinsicScope(
			new IntrinsicProperty("init", staticArrayType, 
				parseDDoc("	Returns an array literal with each element of the literal being the .init property " +
					"of the array element type.")),
			new IntrinsicProperty("sizeof", integral_type, 
				parseDDoc("Returns the array length multiplied by the number of bytes per array element.")),
			new IntrinsicProperty("length", integral_type, 
				parseDDoc("Returns the number of elements in the array. This is a fixed quantity for static arrays. " +
					"It is of type $(D size_t).")),
			new IntrinsicProperty("ptr", pointerType, 
				parseDDoc("Returns a pointer to the first element of the array.")),
			new IntrinsicProperty("dup", dynArrayType, 
				parseDDoc("Create a dynamic array of the same size and copy the contents of the array into it.")),
			new IntrinsicProperty("idup", dynArrayType, 
				parseDDoc("Create a dynamic array of the same size and copy the contents of the array into it. " +
					"The copy is typed as being immutable.")),
			new IntrinsicProperty("reverse", staticArrayType, 
				parseDDoc("Reverses in place the order of the elements in the array. Returns the array.")),
			new IntrinsicProperty("sort", staticArrayType, 
				parseDDoc("Sorts in place the order of the elements in the array. Returns the array."))
		);
	}
	
	public class IntrinsicDynArray extends IntrinsicTypeDefUnit {
		
		public final Ddoc ddoc = parseDDoc("see http://dlang.org/arrays.html#dynamic-arrays");
		public IntrinsicScope membersScope;
		
		public IntrinsicDynArray() {
			super("<dynamic_array>");
		}
		
		@Override
		public Ddoc resolveDDoc() {
			return null;
		}
		
		@Override
		public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
			membersScope.resolveSearchInScope(search);
		}
		
	}
	
	{
		dynArrayType.membersScope = new IntrinsicScope(
			new IntrinsicProperty("init", dynArrayType, 
				parseDDoc("Returns null.")),
			new IntrinsicProperty("sizeof", integral_type, 
				parseDDoc("Returns the size of the dynamic array reference, " +
					"which is 8 in 32-bit builds and 16 on 64-bit builds.")),
			new IntrinsicProperty("length", integral_type, 
				parseDDoc("Get/set number of elements in the array. " +
					"It is of type $(D size_t).")),
			new IntrinsicProperty("ptr", pointerType, 
				parseDDoc("Returns a pointer to the first element of the array.")),
			new IntrinsicProperty("dup", dynArrayType, 
				parseDDoc("Create a dynamic array of the same size and copy the contents of the array into it.")),
			new IntrinsicProperty("idup", dynArrayType, 
				parseDDoc("Create a dynamic array of the same size and copy the contents of the array into it. " +
					"The copy is typed as being immutable.")),
			new IntrinsicProperty("reverse", dynArrayType, 
				parseDDoc("Reverses in place the order of the elements in the array. Returns the array.")),
			new IntrinsicProperty("sort", dynArrayType, 
				parseDDoc("Sorts in place the order of the elements in the array. Returns the array."))
		);
		
	}
	
}
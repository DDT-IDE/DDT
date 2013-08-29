package dtool.resolver;

import static dtool.resolver.CompareDefUnits.fnDefUnitToStringAsName;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.misc.CollectionUtil.createArrayList;
import static melnorme.utilbox.misc.StringUtil.collToString;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import melnorme.utilbox.misc.StringUtil;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.references.RefModule.LightweightModuleProxy;
import dtool.ast.util.NodeUtil;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.tests.CommonTestUtils;

public class DefUnitResultsChecker extends CommonTestUtils {
	
	protected final Collection<DefUnit> resultDefUnits;
	
	public DefUnitResultsChecker(Collection<DefUnit> resultDefUnits) {
		this.resultDefUnits = createArrayList(resultDefUnits);
	}
	
	public void removeIgnoredDefUnits(boolean ignoreDummyResults, boolean ignorePrimitives) {
		removeIgnoredDefUnits(resultDefUnits, ignoreDummyResults, ignorePrimitives);
	}
	
	public static void removeIgnoredDefUnits(Collection<DefUnit> resultDefUnits, 
		boolean ignoreDummyResults, boolean ignorePrimitives) {
		for (Iterator<DefUnit> iterator = resultDefUnits.iterator(); iterator.hasNext(); ) {
			DefUnit defUnit = iterator.next();
			
			if(ignoreDummyResults && 
				(defUnit.getName().equals("_dummy") || defUnit.getName().equals("_ignore"))) {
				iterator.remove();
			}
			
			if(ignorePrimitives && defUnit.isLanguageIntrinsic()) {
				iterator.remove();
			}
		}
	}
	
	public void checkResults(String[] expectedResults, Map<String, MetadataEntry> markers) {
		HashSet<String> expectedResultsDeduplicated = hashSet(expectedResults);
		
		for (String expectedTarget : expectedResultsDeduplicated) {
			if(expectedTarget.startsWith("@") ) {
				String markerName = expectedTarget.substring(1);
				MetadataEntry marker = assertNotNull(markers.get(markerName));
				DefUnitResultsChecker.removedDefUnitByMarker(resultDefUnits, marker);
			} else {
				removeDefUnit(expectedTarget);
			}
		}
		
		if(!resultDefUnits.isEmpty()) {
			String resultDefUnitsStr = collToString(strmap(resultDefUnits, fnDefUnitToStringAsName()), "\n");
			System.out.println("--- Unexpected elements ("+resultDefUnits.size()+") : ---\n" + resultDefUnitsStr);
		}
		assertTrue(resultDefUnits.isEmpty());
	}
	
	public void removeDefUnit(String expectedTarget) {
		String moduleName = StringUtil.segmentUntilMatch(expectedTarget, "/");
		String defUnitModuleQualifiedName = StringUtil.substringAfterMatch(expectedTarget, "/");
		
		boolean removed = false;
		if(moduleName == null ) {
			for (Iterator<DefUnit> iterator = resultDefUnits.iterator(); iterator.hasNext(); ) {
				DefUnit defUnit = iterator.next();
				
				if(defUnit.getName().equals(expectedTarget)) {
					iterator.remove();
					removed = true;
				}
			}
		} else {
			String expectedFullyTypedQualification = moduleName + 
				(defUnitModuleQualifiedName != null ? "/" + defUnitModuleQualifiedName : "");
			
			for (Iterator<DefUnit> iterator = resultDefUnits.iterator(); iterator.hasNext(); ) {
				DefUnit defUnit = iterator.next();
				
				String defUnitTypedQualification = getDefUnitTypedQualification(defUnit);
				if(defUnitTypedQualification.equals(expectedFullyTypedQualification)) {
					iterator.remove();
					removed = true;
				} else {
					continue; // Not a match
				}
			}
		}
		if(removed == false) {
			System.out.println(" > Not Found: " + expectedTarget);
			assertFail(); // Must find a matching result
		}
	}
	
	/**
	 * Return a name identifying this defUnit in the projects source code.
	 * It's similar to a fully qualified name, but has some more information on the name about
	 * the containing defunits.
	 * (the name is not enough to uniquely locate a defUnit in a project. That's the goal anyways)
	 */
	public static String getDefUnitTypedQualification(DefUnit defUnit) {
		String base = getDefUnitTypeQualificationBase(defUnit);
		switch(defUnit.getArcheType()) {
		case Package:
			base += "/";
			break;
		default:
		}
		return base;
	}
	
	public static String getDefUnitTypeQualificationBase(DefUnit defUnit) {
		if(defUnit instanceof LightweightModuleProxy) {
			return ((LightweightModuleProxy) defUnit).getFullyQualifiedName() + "/";
		}
		if(defUnit instanceof Module) {
			return ((Module) defUnit).getFullyQualifiedName() + "/";
		}
		
		DefUnit parentDefUnit = NodeUtil.getParentDefUnit(defUnit);
		
		String qualification;
		if(parentDefUnit == null) {
			assertTrue(defUnit.isSynthetic());
			if(defUnit.isLanguageIntrinsic()) { 
				qualification = NATIVES_ROOT;
			} else {
				qualification = "";
			}
		} else {
			String sep = parentDefUnit instanceof Module ? "" : ".";
			String parentQualifedName = getDefUnitTypeQualificationBase(parentDefUnit);
			qualification = parentQualifedName + sep;
		}
		
		return qualification + defUnit.getName();
	}
	
	public static String NATIVES_ROOT = "/";
	
	public static String getDefUnitModuleQualifedName(DefUnit defUnit) {
		if(defUnit instanceof Module) {
			return "";
		}
		DefUnit parentDefUnit = NodeUtil.getParentDefUnit(defUnit);
		String parentQualifedName = getDefUnitModuleQualifedName(parentDefUnit);
		if(parentQualifedName == "") {
			return defUnit.getName();
		}
		return parentQualifedName + "." + defUnit.getName();
	}
	
	
	
	/* ------ */
	
	public static void removedDefUnitByMarker(Collection<DefUnit> resolvedDefUnits, MetadataEntry marker) {
		for (Iterator<DefUnit> iterator = resolvedDefUnits.iterator(); iterator.hasNext(); ) {
			DefUnit defUnit = iterator.next();
			if(defUnit.defname.getEndPos() == marker.offset || defUnit.defname.getStartPos() == marker.offset) {
				iterator.remove();
				return;
			}
		}
		assertFail();
	}
	
}
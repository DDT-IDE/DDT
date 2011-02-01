/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.wizards;

import org.eclipse.osgi.util.NLS;

public final class DeeNewWizardMessages extends NLS {

	private DeeNewWizardMessages() {
		// Do not instantiate
	}
	
	public static String NewElementWizard_op_error_title =
		"New Element Wizard Error";
	public static String NewElementWizard_op_error_message = 
		"Creation of element failed.";

	/* -------- New Project -------- */ 
	public static String LangNewProject_wizardTitle = 
		"New D Project";
	
	public static String LangNewProject_Page1_pageTitle =
		"Create a D project.";
	public static String LangNewProject_Page1_pageDescription =
		"Create a D project in the workspace or in an external location.";

	
	public static String LangNewProject_Page1_NameGroup_label = 
		"&Project name:";
	public static String LangNewProject_Page1_LocationGroup_title = 
		"Contents";
	public static String LangNewProject_Page1_LocationGroup_workspace_desc =
		"Create new project in &workspace";
	public static String LangNewProject_Page1_LocationGroup_external_desc =
		"Create project from e&xisting source";
	public static String LangNewProject_Page1_LocationGroup_locationLabel_desc =
		"&Directory:";
	public static String LangNewProject_Page1_LocationGroup_browseButton_desc = 
		"B&rowse...";
	
	public static String LangNewProject_Page1_Message_enterProjectName = 
		"Enter a project name.";
	public static String LangNewProject_Page1_Message_projectAlreadyExists =
		"A project with this name already exists.";
	public static String LangNewProject_Page1_Message_enterLocation = 
		"Enter a location for the project.";
	public static String LangNewProject_Page1_Message_invalidDirectory = 
		"Invalid project contents directory";

	
	public static String LangNewProject_Page1_directory_message = 
		"Choose a directory for the project contents:";
	
	
	public static String LangNewProject_Page1_DetectGroup_message = 
		"The specified external location already exists. If a project is created in this location, the wizard will automatically try to detect existing sources and class files and configure the classpath appropriately.";

	
	public static String LangNewProject_Page1_DCEGroup_title = 
		"DCE";
	public static String LangNewProject_Page1_DCEGroup_default_compliance = 
		"Use def&ault DCE (Currently ''{0}'')";
	public static String LangNewProject_Page1_DCEGroup_link_description =
		"<a>C&onfigure DCEs... [TODO]</a>";;

}

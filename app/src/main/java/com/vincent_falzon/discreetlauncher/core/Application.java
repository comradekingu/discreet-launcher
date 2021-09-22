package com.vincent_falzon.discreetlauncher.core ;

// License
/*

	This file is part of Discreet Launcher.

	Copyright (C) 2019-2021 Vincent Falzon

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <https://www.gnu.org/licenses/>.

 */

// Imports
import android.content.Context ;
import android.content.Intent ;
import android.content.pm.PackageManager ;
import android.graphics.drawable.Drawable ;
import android.view.View ;

/**
 * Represent an Android application with its names (displayed, internal and package) and icon.
 */
public class Application
{
	// Attributes
	String display_name ;
	String name ;
	final String apk ;
	String component_info ;
	Drawable icon ;


	/**
	 * Constructor to represent an Android application
	 * @param display_name Displayed to the user
	 * @param name Application name used internally
	 * @param apk Package name used internally
	 * @param icon Displayed to the user
	 */
	public Application(String display_name, String name, String apk, Drawable icon)
	{
		this.display_name = display_name ;
		this.name = name ;
		this.apk = apk ;
		this.icon = icon ;
		component_info = "{" + apk + "/" + name + "}" ;
	}


	/**
	 * Get the disply name of the application.
	 * @return Name displayed in the menus
	 */
	public String getDisplayName()
	{
		return display_name ;
	}


	/**
	 * Set the display name of the application.
	 * @param new_name New display name
	 */
	public void setDisplayName(String new_name)
	{
		display_name = new_name ;
	}


	/**
	 * Get the internal name of the application.
	 * @return Application name used internally
	 */
	public String getName()
	{
		return name ;
	}


	/**
	 * Get the package name of the application.
	 * @return Package name used internally
	 */
	public String getApk()
	{
		return apk ;
	}


	/**
	 * Get the ComponentInfo{package/name} of the application.
	 * @return Used internally
	 */
	public String getComponentInfo()
	{
		return component_info ;
	}


	/**
	 * Get the icon of the application.
	 * @return Icon displayed in the menus
	 */
	public Drawable getIcon()
	{
		return icon ;
	}


	/**
	 * Set a new icon for the application.
	 * @param new_icon Icon displayed in the menus
	 */
	public void setIcon(Drawable new_icon)
	{
		icon = new_icon ;
	}


	/**
	 * Start the application as a new task.
	 * @param view Element from which the event originates
	 * @return <code>true</code> if the application was found, <code>false</code> otherwise
	 */
	public boolean start(View view)
	{
		// Check if the application still exists (not uninstalled or disabled)
		Context context = view.getContext() ;
		PackageManager apkManager = context.getPackageManager() ;
		Intent packageIntent = apkManager.getLaunchIntentForPackage(apk) ;
		if(packageIntent == null) return false ;

		// Try to launch the specific intent of the application
		Intent activityIntent = new Intent(Intent.ACTION_MAIN) ;
		activityIntent.addCategory(Intent.CATEGORY_LAUNCHER) ;
		activityIntent.setClassName(apk, name) ;
		activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
		if(activityIntent.resolveActivity(apkManager) != null) context.startActivity(activityIntent) ;
			else
			{
				// If it was not found, launch the default intent of the package
				packageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
				context.startActivity(packageIntent) ;
			}
		return true ;
	}
}

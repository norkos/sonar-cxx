/*
 * Sonar Cxx Plugin, open source software quality management tool.
 * Copyright (C) 2010 - 2011, Neticoa SAS France - Tous droits reserves.
 * Author(s) : Franck Bonin, Neticoa SAS France.
 *
 * Sonar Cxx Plugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar Cxx Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar Cxx Plugin; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.cxx.cppncss.dao;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FileData {
	private int noMethods = 0;
	private Map<String, ClassData> classes = new HashMap<String, ClassData>();
	private int complexity = 0;
	private File file;

	public FileData(String name) {
		this.file = new File(name);
	}

	public String getName() {
		return file.getAbsolutePath();
	}

	public File getFile() {
		return file;
	}

	public int getNoMethods() {
		return noMethods;
	}

	public int getComplexity() {
		return complexity;
	}

	/** @return data for classes contained in this file */
	public Collection<ClassData> getClasses() {
		return classes.values();
	}

	/**
	 * Adds complexity data for a method with given name in a given class
	 * 
	 * @param className
	 *            Name of method's class
	 * @param methodName
	 *            The name of the method to add data for
	 * @param complexity
	 *            The complexity number to store
	 * @param size
	 *            method size
	 */
	public void addMethod(String className, String methodName, int lineNumber,
			int complexity, int size) {
		noMethods++;
		this.complexity += complexity;

		ClassData classData = classes.get(className);
		if (classData == null) {
			classData = new ClassData();
			classes.put(className, classData);
		}
		classData.addMethod(methodName, lineNumber, complexity, size);
	}
}
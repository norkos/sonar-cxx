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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.sonar.plugins.cxx.utils.Pair;

public class ClassData {
	private Map<Pair<String, Integer>, FunctionData> methodComplexities = new HashMap<Pair<String, Integer>, FunctionData>();
	private int complexity = 0;

	/**
	 * Adds complexity data for a method with given name
	 * 
	 * @param name
	 *            The name of the method to add data for
	 * @param complexity
	 *            The complexity number to store
	 */
	public void addMethod(String name, int lineNumber, int complexity, int size) {
		this.complexity += complexity;
		methodComplexities.put(new Pair<String, Integer>(name, lineNumber),
				new FunctionData(lineNumber, complexity, size));
	}

	public Integer getComplexity() {
		return complexity;
	}

	/** @return complexity numbers for methods inside of this class */
	public Set<Entry<Pair<String, Integer>, FunctionData>> getMethodsWithNames() {
		return methodComplexities.entrySet();
	}

	/** @return complexity numbers for methods inside of this class */
	public Collection<FunctionData> getMethods() {
		return methodComplexities.values();
	}
}

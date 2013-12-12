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
package org.sonar.plugins.cxx.cppncss;

import org.sonar.plugins.cxx.cppncss.dao.ClassData;
import org.sonar.plugins.cxx.cppncss.dao.FileData;
import org.sonar.plugins.cxx.cppncss.dao.FunctionData;
import org.sonar.plugins.cxx.utils.Pair;

public final class DistanceCalculator {

	public static Pair<Integer /** compexity */
	, Integer /** size */
	> calculate(FileData file, int distanceForCompexity, int distanceForSize) {
		int resultComplexity = 0;
		int resultSize = 0;

		for (ClassData claz : file.getClasses()) {
			for (FunctionData function : claz.getMethods()) {
				int size = function.getSize() - distanceForSize;
				int compexity = function.getComplexity() - distanceForCompexity;

				if (size > 0) {
					resultSize += size;
				}

				if (compexity > 0) {
					resultComplexity += compexity;
				}

			}
		}

		return new Pair<Integer, Integer>(resultComplexity, resultSize);
	}
}

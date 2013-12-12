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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sonar.plugins.cxx.cppncss.dao.FileData;
import org.sonar.plugins.cxx.utils.Pair;

public class DistanceCalculatorTest {

	private final int complexity = 12;
	private final int size = 23;

	@Test
	public void testWhenNoMethods() {
		// given
		FileData file = new FileData("dummy");

		// when
		Pair<Integer, Integer> result = DistanceCalculator.calculate(file,
				complexity, size);

		// then
		assertEquals(0, result.getHead().intValue());
		assertEquals(0, result.getTail().intValue());
	}

	@Test
	public void testForMethodsBelow() {
		// given
		FileData file = new FileData("dummy");
		file.addMethod("dummyClass", "noViolation", 0, complexity, size);
		file.addMethod("dummyClass", "noViolation2", 0, complexity - 1,
				size - 1);

		// when
		Pair<Integer, Integer> result = DistanceCalculator.calculate(file,
				complexity, size);

		// then
		assertEquals(0, result.getHead().intValue());
		assertEquals(0, result.getTail().intValue());
	}

	@Test
	public void testForMethodsSameNames() {
		// given
		FileData file = new FileData("dummy");
		file.addMethod("dummyClass", "noViolation", 0, complexity, size);
		file.addMethod("dummyClass", "noViolation2", 0, complexity + 1,
				size + 2);
		file.addMethod("dummyClass", "noViolation2", 1, complexity + 3,
				size + 5);

		// when
		Pair<Integer, Integer> result = DistanceCalculator.calculate(file,
				complexity, size);

		// then
		assertEquals(4, result.getHead().intValue());
		assertEquals(7, result.getTail().intValue());
	}
	
	@Test
	public void testForMethodsAboveWithOverlapping() {
		// given
		FileData file = new FileData("dummy");
		file.addMethod("dummyClass", "noViolation", 0, complexity, size);
		file.addMethod("dummyClass", "noViolation2", 1, complexity + 1,
				size + 2);
		file.addMethod("dummyClass", "noViolation2", 1, complexity + 3,
				size + 5);

		// when
		Pair<Integer, Integer> result = DistanceCalculator.calculate(file,
				complexity, size);

		// then
		assertEquals(3, result.getHead().intValue());
		assertEquals(5, result.getTail().intValue());
	}
	
}

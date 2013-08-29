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
package org.sonar.plugins.cxx.coverage;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.cxx.TestUtils;

public class CoberturaExcluderTest {

	@Test
	public void testExcluded() {
		String directories = "var,log";
		Settings settings = new Settings();
		settings.appendProperty(
				CoberturaExcluder.COVERAGE_EXDLUDED_DIRECTORIES, directories);
		CoberturaExcluder excluder = new CoberturaExcluder(settings);

		File exluded = new File("/var/nk/file.txt");
		File exluded2 = new File("/nk/log/nk/file.txt");
		File exluded3 = new File("/nk/log/log/file.txt");

		assertTrue(excluder.isExcluded(exluded));
		assertTrue(excluder.isExcluded(exluded2));
		assertTrue(excluder.isExcluded(exluded3));
	}
	
	@Test
	public void testNoExcludedProperties() {
		Settings settings = new Settings();
		CoberturaExcluder excluder = new CoberturaExcluder(settings);

		File included = new File("/var/nk/file.txt");
		assertFalse(excluder.isExcluded(included));
	}

	@Test
	public void testNotExcluded() {
		String directories = "var,log";
		Settings settings = new Settings();
		settings.appendProperty(
				CoberturaExcluder.COVERAGE_EXDLUDED_DIRECTORIES, directories);
		CoberturaExcluder excluder = new CoberturaExcluder(settings);

		File included = new File("/var1/nk/file.txt");
		File included2 = new File("/nk/log1/nk/file.txt");

		assertFalse(excluder.isExcluded(included));
		assertFalse(excluder.isExcluded(included2));
	}
}

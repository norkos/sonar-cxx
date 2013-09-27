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
package org.sonar.plugins.cxx;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

/**
 * Exclusion from coverage on project level
 *
 */
public class CxxSampleProjectIT6 {

	private static Sonar sonar;
	private static final String PROJECT_SAMPLE = "CxxPlugin:Sample6";
	private static final String DIR_UTILS = "CxxPlugin:Sample6:lib";

	@BeforeClass
	public static void buildServer() {
		sonar = Sonar.create("http://localhost:9000");
	}

	@Test
	public void projectsMetrics() {

		String[] metricNames = { "coverage" };

		for (int i = 0; i < metricNames.length; ++i) {
			assertNull(getProjectMeasure(metricNames[i]));
		}

	}

	@Test
	public void directoryMetrics() {
		String[] metricNames = { "complexity", "function_complexity" };

		for (int i = 0; i < metricNames.length; ++i) {
			assertNotNull(getPackageMeasure(metricNames[i]));
		}

	}

	private Measure getProjectMeasure(String metricKey) {
		Resource resource = sonar.find(ResourceQuery.createForMetrics(
				PROJECT_SAMPLE, metricKey));
		return resource != null ? resource.getMeasure(metricKey) : null;
	}

	private Measure getPackageMeasure(String metricKey) {
		Resource resource = sonar.find(ResourceQuery.createForMetrics(
				DIR_UTILS, metricKey));
		return resource != null ? resource.getMeasure(metricKey) : null;
	}
}

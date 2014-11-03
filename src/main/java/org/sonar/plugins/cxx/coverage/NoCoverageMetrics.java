
/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.cxx.coverage;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;
import org.sonar.api.measures.SumChildValuesFormula;

public class NoCoverageMetrics implements Metrics {

	public final static Metric NOT_COVERED_COMPLEXITY = new Metric.Builder(
			"not-covered-complexity", "Complexity of UT tests",
			Metric.ValueType.INT)
			.setDescription("Complexity of tests")
			.setDirection(Metric.DIRECTION_NONE)
			.setDomain(CoreMetrics.DOMAIN_ISSUES).setQualitative(false)
			.setFormula(new SumChildValuesFormula(true)).create();
	

	public final static Metric TESTS_LINES = new Metric.Builder(
			"not-covered-lines", "Lines of UT tests",
			Metric.ValueType.INT)
			.setDescription("Test lines")
			.setDirection(Metric.DIRECTION_NONE)
			.setDomain(CoreMetrics.DOMAIN_ISSUES).setQualitative(false)
			.setFormula(new SumChildValuesFormula(true)).create();
	
	public List<Metric> getMetrics() {
		return Arrays
				.asList(NOT_COVERED_COMPLEXITY, TESTS_LINES);
	}
}


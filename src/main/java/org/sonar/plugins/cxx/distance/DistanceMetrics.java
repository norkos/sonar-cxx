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

package org.sonar.plugins.cxx.distance;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;
import org.sonar.api.measures.SumChildValuesFormula;

/**
 * {@inheritDoc}
 */
public final class DistanceMetrics implements Metrics {

	public final static Metric DISTANCE_COMPLEXITY_LENGTH = new Metric.Builder(
			"distance-complexity", "Cumulative complexity overrun",
			Metric.ValueType.INT)
			.setDescription("Cumulative complexity overrun")
			.setDirection(Metric.DIRECTION_WORST)
			.setDomain(CoreMetrics.DOMAIN_ISSUES).setQualitative(false)
			.setFormula(new SumChildValuesFormula(true)).create();
	
	public final static Metric DISTANCE_COMPLEXITY_RATIO = new Metric.Builder(
			"distance-complexity-ratio", "Cumulative complexity ratio",
			Metric.ValueType.PERCENT)
			.setDescription("Cumulative complexity overrun by complexity")
			.setDirection(Metric.DIRECTION_WORST)
			.setDomain(CoreMetrics.DOMAIN_ISSUES).setQualitative(false).create();

	
	public final static Metric DISTANCE_METHOD_LENGTH = new Metric.Builder(
			"distance-size", "Cumulative method length overrun",
			Metric.ValueType.INT)
			.setDescription("Cumulative method length overrun")
			.setDirection(Metric.DIRECTION_WORST)
			.setDomain(CoreMetrics.DOMAIN_ISSUES).setQualitative(false)
			.setFormula(new SumChildValuesFormula(true)).create();

	public final static Metric DISTANCE_LENGTH_RATIO = new Metric.Builder(
			"distance-size-ratio", "Cumulative method length ratio",
			Metric.ValueType.PERCENT)
			.setDescription("Cumulative method length overrun by size")
			.setDirection(Metric.DIRECTION_WORST)
			.setDomain(CoreMetrics.DOMAIN_ISSUES).setQualitative(false).create();
	
	public List<Metric> getMetrics() {
		return Arrays
				.asList(DISTANCE_COMPLEXITY_LENGTH, DISTANCE_METHOD_LENGTH,DISTANCE_LENGTH_RATIO, DISTANCE_COMPLEXITY_RATIO);
	}
}

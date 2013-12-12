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
package org.sonar.plugins.cxx.cppncss.metrics;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;
import org.sonar.api.measures.SumChildValuesFormula;

public final class CppNcssMetrics implements Metrics {

	public final static Metric DISTANCE_COMPLEXITY_LENGTH = new Metric.Builder(
			"distance-complexity", "Cumulative complexity overrun",
			Metric.ValueType.INT)
			.setDescription("Cumulative complexity overrun")
			.setDirection(Metric.DIRECTION_WORST)
			.setDomain(CoreMetrics.DOMAIN_ISSUES).setQualitative(false)
			.setFormula(new SumChildValuesFormula(true)).create();

	public final static Metric DISTANCE_METHOD_LENGTH = new Metric.Builder(
			"distance-size", "Cumulative method length overrun",
			Metric.ValueType.INT)
			.setDescription("Cumulative method length overrun")
			.setDirection(Metric.DIRECTION_WORST)
			.setDomain(CoreMetrics.DOMAIN_ISSUES).setQualitative(false)
			.setFormula(new SumChildValuesFormula(true)).create();

	public List<Metric> getMetrics() {
		return Arrays
				.asList(DISTANCE_COMPLEXITY_LENGTH, DISTANCE_METHOD_LENGTH);
	}
}

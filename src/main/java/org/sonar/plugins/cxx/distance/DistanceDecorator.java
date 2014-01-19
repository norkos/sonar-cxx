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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorBarriers;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.cxx.cppncss.CxxCppNcssRuleRepository;

import com.google.common.collect.ImmutableList;

@DependsUpon(DecoratorBarriers.ISSUES_TRACKED)
public class DistanceDecorator implements Decorator {

	private final static Map<String, Metric> sKeys = new HashMap<String, Metric>();
	static {
		sKeys.put(CxxCppNcssRuleRepository.FUNCTION_COMPLEXITY,
				DistanceMetrics.DISTANCE_COMPLEXITY_LENGTH);

		sKeys.put(CxxCppNcssRuleRepository.FUNCTION_SIZE,
				DistanceMetrics.DISTANCE_METHOD_LENGTH);
	}

	public boolean shouldExecuteOnProject(Project project) {
		return true;
	}

	@DependedUpon
	public List<Metric> generatesIssuesMetrics() {
		return ImmutableList.of(DistanceMetrics.DISTANCE_COMPLEXITY_LENGTH,
				DistanceMetrics.DISTANCE_METHOD_LENGTH);
	}

	public void decorate(Resource resource, DecoratorContext context) {
		if (resource == null || context == null) {
			return;
		}

		List<Violation> violations = context.getViolations();

		if (violations.isEmpty()) {
			return;
		}

		CummulataiveDistancePlugin.LOG.debug("Checking resource "
				+ resource.getLongName());
		
		Map<String, Double> values = new HashMap<String, Double>();
		for (Violation v : violations) {
			Double cost = v.getCost();
			String rule = v.getRule().getKey();
			CummulataiveDistancePlugin.LOG.debug("Cost for: " + rule + " is: "
					+ cost);

			if (cost == null) {
				CummulataiveDistancePlugin.LOG.error("No cost provided");
				return;
			}

			Double actualValue = values.get(rule);
			if (actualValue == null) {
				values.put(rule, cost);
			} else {
				values.put(
						rule,
						Double.valueOf(actualValue.doubleValue()
								+ cost.doubleValue()));
			}
		}

		for (Entry<String, Double> value : values.entrySet()) {
			Metric metric = sKeys.get(value.getKey());
			if (metric == null) {
				CummulataiveDistancePlugin.LOG
						.error("No metric provided for key "
								+ value.getKey());
				continue;
			}

			CummulataiveDistancePlugin.LOG.debug("Saving cummulative: "
					+ value.getKey() + " with value: " + value.getValue());
			context.saveMeasure(metric, value.getValue());
		}
	}
}

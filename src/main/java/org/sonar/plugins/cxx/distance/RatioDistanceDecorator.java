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

import java.util.List;

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;

import com.google.common.collect.ImmutableList;

public class RatioDistanceDecorator implements Decorator {

	@DependsUpon
	public List<Metric> dependsOnMetrics() {
		return ImmutableList.of(DistanceMetrics.DISTANCE_COMPLEXITY_LENGTH,
				DistanceMetrics.DISTANCE_METHOD_LENGTH);
	}

	@DependedUpon
	public List<Metric> generatesIssuesMetrics() {
		return ImmutableList.of(DistanceMetrics.DISTANCE_COMPLEXITY_RATIO,
				DistanceMetrics.DISTANCE_LENGTH_RATIO);
	}

	public boolean shouldExecuteOnProject(Project project) {
		return true;
	}

	public void decorate(Resource resource, DecoratorContext context) {
		if (resource == null) {
			return;
		}

		process(context, CoreMetrics.COMPLEXITY,
				DistanceMetrics.DISTANCE_COMPLEXITY_LENGTH,
				DistanceMetrics.DISTANCE_COMPLEXITY_RATIO);

		process(context, CoreMetrics.NCLOC,
				DistanceMetrics.DISTANCE_METHOD_LENGTH,
				DistanceMetrics.DISTANCE_LENGTH_RATIO);

	}

	private void process(DecoratorContext context, Metric all, Metric overrun,
			Metric result) {

		double allValue = MeasureUtils.getValue(context.getMeasure(all), 0.0);

		double overrunValue = MeasureUtils.getValue(
				context.getMeasure(overrun), 0.0);

		if (allValue > 0.0) {
			double ratio = 100.0 * overrunValue / allValue;

			context.saveMeasure(result, ratio);
			CummulataiveDistancePlugin.LOG.error("Saving cummulative factor: "
					+ result.getName() + " with value " + ratio + " from all "
					+ allValue + " and overrun" + overrunValue);
		}
	}

}

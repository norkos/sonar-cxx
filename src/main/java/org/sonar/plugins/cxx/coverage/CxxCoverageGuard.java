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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.cxx.CxxLanguage;
import org.sonar.plugins.cxx.utils.CxxReportSensor;
import org.sonar.plugins.cxx.utils.CxxUtils;

public class CxxCoverageGuard extends CxxReportSensor {

	/**
	 * {@inheritDoc}
	 */
	public CxxCoverageGuard(RuleFinder ruleFinder, Settings conf,
			RulesProfile profile) {
		super(ruleFinder, conf);
	}

	private interface GuardStrategy {
		public boolean isApplicable(org.sonar.api.resources.File cxxFile,
				SensorContext context);

		public void saveMeasure(org.sonar.api.resources.File cxxFile,
				SensorContext context);

	}

	private final class NoCoverage implements GuardStrategy {

		public boolean isApplicable(org.sonar.api.resources.File cxxFile,
				SensorContext context) {

			Measure measure = context
					.getMeasure(cxxFile, CoreMetrics.FUNCTIONS);
			int numberOfFunctions = measure != null ? measure.getIntValue() : 0;

			return numberOfFunctions > 0
					&& context.getMeasure(cxxFile, CoreMetrics.LINES_TO_COVER) == null;
		}

		public void saveMeasure(org.sonar.api.resources.File cxxFile,
				SensorContext context) {

			CxxUtils.LOG.debug("Saving zero coverage for '{}'",
					cxxFile.toString());

			context.saveMeasure(cxxFile, CoreMetrics.LINES_TO_COVER, context
					.getMeasure(cxxFile, CoreMetrics.NCLOC).getValue());

			context.saveMeasure(cxxFile, CoreMetrics.UNCOVERED_LINES, context
					.getMeasure(cxxFile, CoreMetrics.NCLOC).getValue());

			context.saveMeasure(cxxFile, CoreMetrics.COVERAGE_LINE_HITS_DATA,
					0d);

			context.saveMeasure(cxxFile, CoreMetrics.LINE_COVERAGE, 0d);

			/**
			 * branch coverage <= cyclomatic complexity <= number of paths.
			 */
			context.saveMeasure(cxxFile, CoreMetrics.UNCOVERED_CONDITIONS,
					context.getMeasure(cxxFile, CoreMetrics.COMPLEXITY)
							.getValue());
			context.saveMeasure(cxxFile, CoreMetrics.CONDITIONS_TO_COVER,
					context.getMeasure(cxxFile, CoreMetrics.COMPLEXITY)
							.getValue());

			context.saveMeasure(cxxFile, CoreMetrics.BRANCH_COVERAGE, 0d);
			context.saveMeasure(cxxFile, CoreMetrics.COVERAGE, 0d);
		}

	}

	public boolean shouldExecuteOnProject(Project project) {
		return CxxLanguage.KEY.equals(project.getLanguageKey());
	}

	public void analyse(Project project, SensorContext context) {
		final List<InputFile> sources = project.getFileSystem().mainFiles(
				CxxLanguage.KEY);

		GuardStrategy noCoverageProvided = new NoCoverage();
		CoverageExcluder excluder = new CoberturaExcluder(conf);

		for (InputFile inputFile : sources) {
			File file = inputFile.getFile();
			org.sonar.api.resources.File cxxFile = org.sonar.api.resources.File
					.fromIOFile(file, project);

			Reader reader = null;
			try {
				reader = new FileReader(file);

				if (!excluder.isExcluded(file)
						&& noCoverageProvided.isApplicable(cxxFile, context)) {
					noCoverageProvided.saveMeasure(cxxFile, context);
				}

			} catch (IOException exc) {
				String msg = new StringBuilder()
						.append("Cannot analyse the file: '")
						.append(file.getPath()).append("'").toString();
				throw new SonarException(msg, exc);
			} finally {
				IOUtils.closeQuietly(reader);
			}
		}
	}
}

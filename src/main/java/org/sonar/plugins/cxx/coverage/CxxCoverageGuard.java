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
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.cxx.CxxLanguage;
import org.sonar.plugins.cxx.utils.CxxUtils;

public class CxxCoverageGuard implements Sensor {

	public boolean shouldExecuteOnProject(Project project) {
		return CxxLanguage.KEY.equals(project.getLanguageKey());
	}

	public void analyse(Project project, SensorContext context) {
		final List<InputFile> sources = project.getFileSystem().mainFiles(
				CxxLanguage.KEY);

		for (InputFile inputFile : sources) {
			File file = inputFile.getFile();
			org.sonar.api.resources.File cxxFile = org.sonar.api.resources.File
					.fromIOFile(file, project);

			Reader reader = null;
			try {
				reader = new FileReader(file);

				double numberOfFunctions = 0;
				Measure measure = context.getMeasure(cxxFile,
						CoreMetrics.FUNCTIONS);
				if (measure != null) {
					numberOfFunctions = measure.getValue();
				}

				if (numberOfFunctions > 0) {

					measure = context.getMeasure(cxxFile,
							CoreMetrics.LINES_TO_COVER);
					if (measure == null) {

						CxxUtils.LOG.debug("Saving zero coverage for '{}'",
								file.getPath());

						context.saveMeasure(cxxFile,
								CoreMetrics.LINES_TO_COVER,
								context.getMeasure(cxxFile, CoreMetrics.NCLOC)
										.getValue());

						context.saveMeasure(cxxFile,
								CoreMetrics.UNCOVERED_LINES, context
										.getMeasure(cxxFile, CoreMetrics.NCLOC)
										.getValue());

						context.saveMeasure(cxxFile,
								CoreMetrics.COVERAGE_LINE_HITS_DATA, 0d);

						context.saveMeasure(cxxFile, CoreMetrics.LINE_COVERAGE,
								0d);

						context.saveMeasure(cxxFile,
								CoreMetrics.BRANCH_COVERAGE, 0d);

						context.saveMeasure(cxxFile, CoreMetrics.COVERAGE, 0d);
					}
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

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

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.cxx.TestUtils;

public class CxxCoverageSensorTest {
  private CxxCoverageSensor sensor;
  private SensorContext context;
  private Project project;

  @Before
  public void setUp() {
    project = TestUtils.mockProject();
    sensor = new CxxCoverageSensor(new Settings());
    context = mock(SensorContext.class);
    Resource resourceMock = mock(Resource.class);
    when(context.getResource((Resource)anyObject())).thenReturn(resourceMock);
  }

  @Test
  public void shouldReportCorrectCoverage() {
    sensor.analyse(project, context);
    verify(context, times(99)).saveMeasure((Resource) anyObject(), any(Measure.class));
  }

  @Test
  public void shouldReportNoCoverageSaved() {
    when(context.getResource((Resource)anyObject())).thenReturn(null);
    sensor.analyse(project, context);
    verify(context, times(0)).saveMeasure((Resource) anyObject(), any(Measure.class));
  }  
}

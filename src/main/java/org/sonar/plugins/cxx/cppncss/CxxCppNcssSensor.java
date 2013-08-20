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
package org.sonar.plugins.cxx.cppncss;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.codehaus.staxmate.in.SMInputCursor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.measures.RangeDistributionBuilder;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.StaxParser;
import org.sonar.plugins.cxx.utils.CxxReportSensor;
import org.sonar.plugins.cxx.utils.CxxUtils;

/**
 * {@inheritDoc}
 */
public class CxxCppNcssSensor extends CxxReportSensor {
	public static final String REPORT_PATH_KEY = "sonar.cxx.cppncss.reportPath";
	public static final String FUNCTION_COMPLEXITY = "sonar.cxx.cppncss.functionComplexity";
	public static final String FUNCTION_SIZE = "sonar.cxx.cppncss.functionSize";
	public static final int DEFAULT_MAX_COMPLEXITY = 15;
	public static final int DEFAULT_MAX_SIZE = 200;

	private static final String DEFAULT_REPORT_PATH = "cppncss-reports/cppncss-result-*.xml";
	private static final Number[] METHODS_DISTRIB_BOTTOM_LIMITS = { 1, 2, 4, 6,
			8, 10, 12 };
	private static final Number[] FILE_DISTRIB_BOTTOM_LIMITS = { 0, 5, 10, 20,
			30, 60, 90 };
	private static final Number[] CLASS_DISTRIB_BOTTOM_LIMITS = { 0, 5, 10, 20,
			30, 60, 90 };

	/**
	 * {@inheritDoc}
	 */
	public CxxCppNcssSensor(RuleFinder ruleFinder, Settings conf,
			RulesProfile profile) {
		super(ruleFinder, conf);
	}

	@Override
	protected String reportPathKey() {
		return REPORT_PATH_KEY;
	}

	@Override
	protected String defaultReportPath() {
		return DEFAULT_REPORT_PATH;
	}

	@Override
	protected void processReport(final Project project,
			final SensorContext context, File report)
			throws javax.xml.stream.XMLStreamException {

		final int maxComplexity = getParam(DEFAULT_MAX_COMPLEXITY, FUNCTION_COMPLEXITY);
		final int maxSize = getParam(DEFAULT_MAX_SIZE, FUNCTION_SIZE);

		try {
			StaxParser parser = new StaxParser(
					new StaxParser.XmlStreamHandler() {
						/**
						 * {@inheritDoc}
						 */
						public void stream(SMHierarchicCursor rootCursor)
								throws javax.xml.stream.XMLStreamException {
							Map<String, FileData> files = new HashMap<String, FileData>();
							rootCursor.advance(); // cppncss

							SMInputCursor measureCursor = rootCursor
									.childElementCursor("measure");
							while (measureCursor.getNext() != null) {
								collectMeasure(measureCursor, files);
							}

							for (FileData fileData : files.values()) {
								saveMetrics(project, context, fileData,
										maxComplexity, maxSize);
							}
						}
					});
			parser.parse(report);
		} catch (javax.xml.stream.XMLStreamException e) {
			CxxUtils.LOG.warn("Ignore XML stream exception for CppNccs '{}'",
					e.toString());
		}
	}

	private int getParam(int defaultValue, String param) {
		String value = getConf().getString(param);
		if (value == null) {
			return defaultValue;
		}

		return Integer.valueOf(value.trim());
	}

	private void collectMeasure(SMInputCursor measureCursor,
			Map<String, FileData> files)
			throws javax.xml.stream.XMLStreamException {
		// collect only function measures
		String type = measureCursor.getAttrValue("type");
		if (type.equalsIgnoreCase("function")) {
			collectFunctions(measureCursor, files);
		}
	}

	private final static class Pair<A, B> {
		private A a;
		private B b;

		public Pair(A a, B b) {
			this.a = a;
			this.b = b;
		}

		A getHead() {
			return a;
		}

		B getTail() {
			return b;
		}
	}

	private void collectFunctions(SMInputCursor measureCursor,
			Map<String, FileData> files)
			throws javax.xml.stream.XMLStreamException {
		// determine the position of ccn measure using 'labels' analysis
		SMInputCursor childCursor = measureCursor.childElementCursor();

		Pair<Integer /* ncss */, Integer /* ccn */> cursors = indexOfNCSS(childCursor
				.advance());
		int ncssIndex = cursors.getHead();
		int ccnIndex = cursors.getTail();

		// iterate over the function items and collect them
		while (childCursor.getNext() != null) {
			if ("item".equalsIgnoreCase(childCursor.getLocalName())) {
				collectFunction(ccnIndex, ncssIndex, childCursor, files);
			}
		}
	}

	private Pair<Integer /* ncss */, Integer /* ccn */> indexOfNCSS(
			SMInputCursor labelsCursor)
			throws javax.xml.stream.XMLStreamException {
		int index = 0;
		int ncss = 0;
		int ccn = 0;
		int toBeChecked = 2;

		SMInputCursor labelCursor = labelsCursor.childElementCursor();
		while (labelCursor.getNext() != null) {
			String name = labelCursor.getElemStringValue();
			if ("NCSS".equalsIgnoreCase(name)) {
				ncss = index;
				toBeChecked--;
			}
			if ("CCN".equalsIgnoreCase(name)) {
				ccn = index;
				toBeChecked--;
			}
			index++;
		}

		if (toBeChecked == 0) {
			return new Pair<Integer, Integer>(ncss, ccn);
		}
		throw labelCursor
				.constructStreamException("Cannot find the NCSS-label");
	}

	private void collectFunction(int ccnIndex, int ncssIndex,
			SMInputCursor itemCursor, Map<String, FileData> files)
			throws javax.xml.stream.XMLStreamException {
		String name = itemCursor.getAttrValue("name");
		String loc[] = name.split(" at ");
		String fullFuncName = loc[0];
		String fullFileName = loc[1];

		loc = fullFuncName.split("::");
		String className = (loc.length > 1) ? loc[0] : "GLOBAL";
		String funcName = (loc.length > 1) ? loc[1] : loc[0];

		CxxCppNcssFile file = new CxxCppNcssFile(fullFileName,
				org.sonar.plugins.cxx.utils.CxxOsValidator.getOSType());
		String fileName = file.getFileName();

		FileData fileData = files.get(fileName);
		if (fileData == null) {
			fileData = new FileData(fileName);
			files.put(fileName, fileData);
		}

		SMInputCursor valueCursor = itemCursor.childElementCursor("value");
		Pair<String /* ncss */, String /* cnn */> functionValues = stringValueOfChildWithIndex(
				valueCursor, ncssIndex, ccnIndex);

		fileData.addMethod(className, funcName,
				Integer.parseInt(functionValues.getTail().trim()),
				Integer.parseInt(functionValues.getHead().trim()));
	}

	private Pair<String /* ncss */, String /* cnn */> stringValueOfChildWithIndex(
			SMInputCursor cursor, int ncssIndex, int ccnIndex)
			throws javax.xml.stream.XMLStreamException {
		int index = 0;
		String ncss = null;
		String cnn = null;
		while (index <= ncssIndex || index <= ccnIndex) {
			cursor.advance();
			index++;

			if (ncss == null && index > ncssIndex) {
				ncss = cursor.getElemStringValue();
			}

			if (cnn == null && index > ccnIndex) {
				cnn = cursor.getElemStringValue();
			}
		}
		return new Pair<String, String>(ncss, cnn);
	}

	private void saveMetrics(Project project, SensorContext context,
			FileData fileData, int maxComplexity, int maxSize) {
		String filePath = fileData.getName();
		org.sonar.api.resources.File file = org.sonar.api.resources.File
				.fromIOFile(new File(filePath), project);

		if (context.getResource(file) != null) {
			CxxUtils.LOG.debug("Saving complexity measures for file '{}'",
					filePath);

			RangeDistributionBuilder complexityMethodsDistribution = new RangeDistributionBuilder(
					CoreMetrics.FUNCTION_COMPLEXITY_DISTRIBUTION,
					METHODS_DISTRIB_BOTTOM_LIMITS);
			RangeDistributionBuilder complexityFileDistribution = new RangeDistributionBuilder(
					CoreMetrics.FILE_COMPLEXITY_DISTRIBUTION,
					FILE_DISTRIB_BOTTOM_LIMITS);
			RangeDistributionBuilder complexityClassDistribution = new RangeDistributionBuilder(
					CoreMetrics.CLASS_COMPLEXITY_DISTRIBUTION,
					CLASS_DISTRIB_BOTTOM_LIMITS);

			complexityFileDistribution.add(fileData.getComplexity());
			for (ClassData classData : fileData.getClasses()) {

				complexityClassDistribution.add(classData.getComplexity());

				for (Entry<String, FunctionData> functions : classData
						.getMethods()) {

					FunctionData function = functions.getValue();
					String name = functions.getKey();

					int complexity = function.getComplexity();
					int size = function.getSize();

					if (complexity > maxComplexity) {
						CxxUtils.LOG.debug("Complexity to big '{}'", filePath);
						saveViolation(project, context,
								CxxCppNcssRuleRepository.KEY, filePath, 1,
								CxxCppNcssRuleRepository.FUNCTION_COMPLEXITY,
								"Complexity of method " + name + " was "
										+ complexity + " when limit is "
										+ maxComplexity);
					}

					if (size > maxSize) {
						CxxUtils.LOG.debug("Size of method too big '{}'",
								filePath);
						saveViolation(project, context,
								CxxCppNcssRuleRepository.KEY, filePath, 1,
								CxxCppNcssRuleRepository.FUNCTION_SIZE,
								"Size of method " + name + " was " + size
										+ " when limit is " + maxSize);
					}
					complexityMethodsDistribution.add(complexity);
				}
			}

			context.saveMeasure(file, CoreMetrics.FUNCTIONS,
					(double) fileData.getNoMethods());
			context.saveMeasure(file, CoreMetrics.COMPLEXITY,
					(double) fileData.getComplexity());
			context.saveMeasure(file, complexityMethodsDistribution.build()
					.setPersistenceMode(PersistenceMode.MEMORY));
			context.saveMeasure(file, complexityClassDistribution.build()
					.setPersistenceMode(PersistenceMode.MEMORY));
			context.saveMeasure(file, complexityFileDistribution.build()
					.setPersistenceMode(PersistenceMode.MEMORY));

		} else {
			CxxUtils.LOG.debug("Ignoring complexity measures for file '{}'",
					filePath);
		}
	}

	private static class FileData {
		private String name;
		private int noMethods = 0;
		private Map<String, ClassData> classes = new HashMap<String, ClassData>();
		private int complexity = 0;

		FileData(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public int getNoMethods() {
			return noMethods;
		}

		public int getComplexity() {
			return complexity;
		}

		/** @return data for classes contained in this file */
		public Collection<ClassData> getClasses() {
			return classes.values();
		}

		/**
		 * Adds complexity data for a method with given name in a given class
		 * 
		 * @param className
		 *            Name of method's class
		 * @param methodName
		 *            The name of the method to add data for
		 * @param complexity
		 *            The complexity number to store
		 * @param size
		 *            method size
		 */
		public void addMethod(String className, String methodName,
				int complexity, int size) {
			noMethods++;
			this.complexity += complexity;

			ClassData classData = classes.get(className);
			if (classData == null) {
				classData = new ClassData();
				classes.put(className, classData);
			}
			classData.addMethod(methodName, complexity, size);
		}
	}

	private static class FunctionData {
		public FunctionData(int complexity, int size) {
			this.complexity = complexity;
			this.size = size;
		}

		private int complexity = 0;
		private int size = 0;

		public int getComplexity() {
			return complexity;
		}

		public int getSize() {
			return size;
		}
	}

	private static class ClassData {
		private Map<String, FunctionData> methodComplexities = new HashMap<String, FunctionData>();
		private int complexity = 0;

		/**
		 * Adds complexity data for a method with given name
		 * 
		 * @param name
		 *            The name of the method to add data for
		 * @param complexity
		 *            The complexity number to store
		 */
		public void addMethod(String name, int complexity, int size) {
			this.complexity += complexity;
			methodComplexities.put(name, new FunctionData(complexity, size));
		}

		public Integer getComplexity() {
			return complexity;
		}

		/** @return complexity numbers for methods inside of this class */
		public Set<Entry<String, FunctionData>> getMethods() {
			return methodComplexities.entrySet();
		}
	}
}

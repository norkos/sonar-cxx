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

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.sonar.api.config.Settings;
import org.sonar.plugins.cxx.utils.CxxUtils;

public abstract class Excluder {

	private Set<String> directories;
	private String excluded;

	public Excluder(String excluded, Settings conf) {
		this.excluded = excluded;
		directories = getExcludedDirectories(conf);
	}

	public boolean isExcluded(File file) {
		if (directories.isEmpty()) {
			return false;
		}

		File parent = null;
		for (parent = file.getParentFile(); parent != null; parent = parent
				.getParentFile()) {

			if (directories.contains(parent.getName())) {
				return true;
			}
		}

		return false;
	}

	private Set<String> getExcludedDirectories(Settings conf) {
		Set<String> result = new HashSet<String>();

		String value = conf.getString(excluded);
		if (value == null) {
			return result;
		}
		for (String dir : value.trim().split(",")) {
			CxxUtils.LOG.debug("Ignore direcory while "
					+ this.getClass().getSimpleName() + "  calculations '{}'",
					dir);

			result.add(dir);
		}

		return result;
	}
}

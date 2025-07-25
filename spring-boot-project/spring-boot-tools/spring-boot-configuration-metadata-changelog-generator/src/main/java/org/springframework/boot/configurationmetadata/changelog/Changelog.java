/*
 * Copyright 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.configurationmetadata.changelog;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.configurationmetadata.ConfigurationMetadataProperty;
import org.springframework.boot.configurationmetadata.ConfigurationMetadataRepository;
import org.springframework.boot.configurationmetadata.Deprecation.Level;

/**
 * A changelog containing differences computed from two repositories of configuration
 * metadata.
 *
 * @param oldVersionNumber the name of the old version
 * @param newVersionNumber the name of the new version
 * @param differences the differences
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 * @author Phillip Webb
 * @author Yoobin Yoon
 */
record Changelog(String oldVersionNumber, String newVersionNumber, List<Difference> differences) {

	static Changelog of(String oldVersionNumber, ConfigurationMetadataRepository oldMetadata, String newVersionNumber,
			ConfigurationMetadataRepository newMetadata) {
		return new Changelog(oldVersionNumber, newVersionNumber, computeDifferences(oldMetadata, newMetadata));
	}

	static List<Difference> computeDifferences(ConfigurationMetadataRepository oldMetadata,
			ConfigurationMetadataRepository newMetadata) {
		List<String> seenIds = new ArrayList<>();
		List<Difference> differences = new ArrayList<>();
		for (ConfigurationMetadataProperty oldProperty : oldMetadata.getAllProperties().values()) {
			String id = oldProperty.getId();
			seenIds.add(id);
			ConfigurationMetadataProperty newProperty = newMetadata.getAllProperties().get(id);
			Difference difference = Difference.compute(oldProperty, newProperty);
			if (difference != null) {
				differences.add(difference);
			}
		}
		for (ConfigurationMetadataProperty newProperty : newMetadata.getAllProperties().values()) {
			if (!seenIds.contains(newProperty.getId())) {
				if (newProperty.isDeprecated() && newProperty.getDeprecation().getLevel() == Level.ERROR) {
					differences.add(new Difference(DifferenceType.DELETED, null, newProperty));
				}
				else if (!newProperty.isDeprecated()) {
					differences.add(new Difference(DifferenceType.ADDED, null, newProperty));
				}
			}
		}
		return List.copyOf(differences);
	}

}

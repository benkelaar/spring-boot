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

package org.springframework.boot.context.properties.source;

import java.util.function.Predicate;

import org.springframework.util.Assert;

/**
 * The state of content from a {@link ConfigurationPropertySource}.
 *
 * @author Phillip Webb
 * @since 2.0.0
 */
public enum ConfigurationPropertyState {

	/**
	 * The {@link ConfigurationPropertySource} has at least one matching
	 * {@link ConfigurationProperty}.
	 */
	PRESENT,

	/**
	 * The {@link ConfigurationPropertySource} has no matching
	 * {@link ConfigurationProperty ConfigurationProperties}.
	 */
	ABSENT,

	/**
	 * It's not possible to determine if {@link ConfigurationPropertySource} has matching
	 * {@link ConfigurationProperty ConfigurationProperties} or not.
	 */
	UNKNOWN;

	/**
	 * Search the given iterable using a predicate to determine if content is
	 * {@link #PRESENT} or {@link #ABSENT}.
	 * @param <T> the data type
	 * @param source the source iterable to search
	 * @param predicate the predicate used to test for presence
	 * @return {@link #PRESENT} if the iterable contains a matching item, otherwise
	 * {@link #ABSENT}.
	 */
	static <T> ConfigurationPropertyState search(Iterable<T> source, Predicate<T> predicate) {
		Assert.notNull(source, "'source' must not be null");
		Assert.notNull(predicate, "'predicate' must not be null");
		for (T item : source) {
			if (predicate.test(item)) {
				return PRESENT;
			}
		}
		return ABSENT;
	}

	/**
	 * Search the given iterable using a predicate to determine if content is
	 * {@link #PRESENT} or {@link #ABSENT}.
	 * @param <T> the data type
	 * @param source the source iterable to search
	 * @param startInclusive the first index to cover
	 * @param endExclusive index immediately past the last index to cover
	 * @param predicate the predicate used to test for presence
	 * @return {@link #PRESENT} if the iterable contains a matching item, otherwise
	 * {@link #ABSENT}.
	 */
	static <T> ConfigurationPropertyState search(T[] source, int startInclusive, int endExclusive,
			Predicate<T> predicate) {
		Assert.notNull(source, "'source' must not be null");
		Assert.notNull(predicate, "'predicate' must not be null");
		for (int i = startInclusive; i < endExclusive; i++) {
			if (predicate.test(source[i])) {
				return PRESENT;
			}
		}
		return ABSENT;
	}

}

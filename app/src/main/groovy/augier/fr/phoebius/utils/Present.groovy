package augier.fr.phoebius.utils


import groovy.transform.CompileStatic

/*
 * This file is taken from Guava libraries distributed under Apache license v2.0
 * https://github.com/google/guava/blob/master/guava/src/com/google/common/base/Present.java
 *
 * Copyright (C) 2011 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License") you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
@CompileStatic
final class Present<T> extends Optional<T>
{
    private static final long serialVersionUID = 0
	private final T reference

	Present(T reference){ this.reference = reference }

	@Override public boolean isPresent(){ true }
	@Override public T get(){ reference }

	@Override public boolean equals(Object object)
    {
        if (object instanceof Present)
        {
			def other = object as Present<T>
			return reference == other.reference
		}
		return false
	}

	@Override public int hashCode(){ 0x598df91c + reference.hashCode() }
	@Override public String toString(){ "Optional.of($reference)" }
}

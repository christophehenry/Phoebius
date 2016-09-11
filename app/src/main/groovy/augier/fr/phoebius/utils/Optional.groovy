package augier.fr.phoebius.utils


import groovy.transform.CompileStatic

/*
 * This file is taken from Guava libraries distributed under Apache license v2.0
 * https://github.com/google/guava/blob/master/guava/src/com/google/common/base/Optional.java
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
public abstract class Optional<T> implements Serializable {
    private static final long serialVersionUID = 0

    protected Optional() {}

    public static <T> Optional<T> absent(){ Absent.withType() }
    public static <T> Optional<T> of(T reference)
    {
        if(reference != null) return new Present<T>(reference)
        else throw new NullPointerException()
    }
    public abstract boolean isPresent()
    public abstract T get()
    @Override public abstract boolean equals(Object object)
    @Override public abstract int hashCode()
    @Override public abstract String toString()
}

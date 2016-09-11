package augier.fr.phoebius.utils


import groovy.transform.CompileStatic

/*
 * This file is taken from Guava libraries distributed under Apache license v2.0
 * https://github.com/google/guava/blob/master/guava/src/com/google/common/base/Absent.java  
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
final class Absent<T> extends Optional<T>
{
    private static final long serialVersionUID = 0
    static final Absent<Object> INSTANCE = new Absent<Object>()

    @SuppressWarnings("unchecked")
    static <T> Optional<T> withType() { INSTANCE as Optional<T> }

    private Absent() {}

    @Override public boolean isPresent() { false }
    @Override public T get() { throw new IllegalStateException("Absent value") }
    @Override public boolean equals(Object object) { object == this }
    @Override public int hashCode(){ 0x79a31aac }
    @Override public String toString() { "Optional.absent()" }
}

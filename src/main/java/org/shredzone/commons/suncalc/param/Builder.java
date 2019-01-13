/*
 * Shredzone Commons - suncalc
 *
 * Copyright (C) 2017 Richard "Shred" Körber
 *   http://commons.shredzone.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.shredzone.commons.suncalc.param;



/**
 * An interface for the method that eventually executes the calculation.
 *
 * @param <T>
 *            Result type
 */

public interface Builder<T> {

    /**
     * Executes the calculation and returns the desired result.
     * <p>
     * The resulting object is immutable. You can change parameters, and then invoke
     * {@link #execute()} again, to get a new object with new results.
     *
     * @return Result of the calculation.
     */
    T execute();

}

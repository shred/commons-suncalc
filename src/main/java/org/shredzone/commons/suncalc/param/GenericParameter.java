/*
 * Shredzone Commons - suncalc
 *
 * Copyright (C) 2020 Richard "Shred" Körber
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
 * Generic parameters and options.
 *
 * @param <T>
 *            Type of the final builder
 */
public interface GenericParameter<T> {

    /**
     * Creates a copy of the current parameters. The copy can be changed independently.
     *
     * @since 2.8
     */
    T copy();

}

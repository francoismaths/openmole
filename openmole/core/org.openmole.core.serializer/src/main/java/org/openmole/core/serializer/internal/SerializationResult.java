/*
 *  Copyright (C) 2010 reuillon
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.core.serializer.internal;

import java.io.File;
import org.openmole.core.serializer.ISerializationResult;

/**
 *
 * @author reuillon
 */
public class SerializationResult implements ISerializationResult {

    final Iterable<Class> classesFromPlugin;
    final Iterable<File> files;

    public SerializationResult(Iterable<Class> classesFromPlugin, Iterable<File> files) {
        this.classesFromPlugin = classesFromPlugin;
        this.files = files;
    }

    @Override
    public Iterable<Class> getClassesFromPlugin() {
        return classesFromPlugin;
    }

    @Override
    public Iterable<File> getFiles() {
        return files;
    }
    
    
}

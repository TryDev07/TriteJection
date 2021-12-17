/* ZipFileIterator.java
 *
 * Created: 2011-10-10 (Year-Month-Day)
 * Character encoding: UTF-8
 *
 ****************************************** LICENSE *******************************************
 *
 * Copyright (c) 2011 - 2013 XIAM Solutions B.V. (http://www.xiam.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.tritewolf.tritejection.utils.iterators;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

final class ZipFileIterator {

    private final ZipFile zipFile;
    private final String[] entryNameFilter;
    private final Enumeration<? extends ZipEntry> entries;

    private ZipEntry current;

    ZipFileIterator(final ZipFile zipFile, final String[] entryNameFilter) throws IOException {
        this.zipFile = zipFile;
        this.entryNameFilter = entryNameFilter;

        this.entries = zipFile.entries();
    }

    public ZipEntry getEntry() {
        return current;
    }

    @SuppressWarnings("emptyblock")
    public InputStream next() throws IOException {
        while (entries.hasMoreElements()) {
            current = entries.nextElement();
            if (accept(current)) {
                return zipFile.getInputStream(current);
            }
        }
        try {
            zipFile.close();
        } catch (IOException ex) {
            // suppress IOException, otherwise close() is called twice
        }
        return null;
    }

    private boolean accept(final ZipEntry entry) {
        if (entry.isDirectory()) {
            return false;
        }
        if (entryNameFilter == null) {
            return true;
        }
        for (final String filter : entryNameFilter) {
            if (entry.getName().startsWith(filter)) {
                return true;
            }
        }
        return false;
    }

}

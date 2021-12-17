/* FileIterator.java
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

import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;

final class FileIterator {

    private final Deque<File> stack = new LinkedList<File>();
    private int rootCount;
    private File current;

    FileIterator(final File... filesOrDirectories) {
        addReverse(filesOrDirectories);
        rootCount = stack.size();
    }

    public File getFile() {
        return current;
    }


    public boolean isRootFile() {
        if (current == null) {
            throw new NoSuchElementException();
        }
        return stack.size() < rootCount;
    }

    public File next() throws IOException {
        if (stack.isEmpty()) {
            current = null;
            return null;
        } else {
            current = stack.removeLast();
            if (current.isDirectory()) {
                if (stack.size() < rootCount) {
                    rootCount = stack.size();
                }
                addReverse(current.listFiles());
                return next();
            } else {
                return current;
            }
        }
    }

    private void addReverse(final File[] files) {
        for (int i = files.length - 1; i >= 0; --i) {
            stack.add(files[i]);
        }
    }

}

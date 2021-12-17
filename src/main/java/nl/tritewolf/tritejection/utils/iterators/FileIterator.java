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

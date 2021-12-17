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

package nl.tritewolf.tritejection.utils.iterators;


import java.io.*;
import java.util.Arrays;
import java.util.zip.ZipFile;

public final class ClassFileIterator extends ResourceIterator {

    private final FileIterator fileIterator;
    private final String[] pkgNameFilter;
    private ZipFileIterator zipIterator;


    public ClassFileIterator() {
        this(classPath(), null);
    }

    public ClassFileIterator(final File[] filesOrDirectories, final String[] pkgNameFilter) {
        this.fileIterator = new FileIterator(filesOrDirectories);
        this.pkgNameFilter = pkgNameFilter;
    }

    public String getName() {
        return zipIterator == null ?
            fileIterator.getFile().getPath() :
            zipIterator.getEntry().getName();
    }

    @Override
    public InputStream next() throws IOException {
        while (true) {
            if (zipIterator == null) {
                final File file = fileIterator.next();
                // not all specified Files exists!
                if (file == null || !file.isFile()) {
                    return null;
                } else {
                    final String name = file.getName();
                    if (name.endsWith(".class")) {
                        return new FileInputStream(file);
                    } else if (fileIterator.isRootFile() &&
                        (endsWithIgnoreCase(name, ".jar") || isZipFile(file))) {
                        zipIterator = new ZipFileIterator(new ZipFile(file), pkgNameFilter);
                    } // else just ignore
                }
            } else {
                final InputStream is = zipIterator.next();
                if (is == null) {
                    zipIterator = null;
                } else {
                    return is;
                }
            }
        }
    }

    private boolean isZipFile(final File file) {
        DataInputStream in = null;
        try {
            in = new DataInputStream(new FileInputStream(file));
            final int n = in.readInt();
            return n == 0x504b0304;
        } catch (IOException ex) {
            // silently ignore read exceptions
            return false;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        }
    }

    private static File[] classPath() {
        final String[] fileNames =
            System.getProperty("java.class.path").split(File.pathSeparator);
        final File[] files = new File[fileNames.length];
        for (int i = 0; i < files.length; ++i) {
            files[i] = new File(fileNames[i]);
        }
        return files;
    }

    private static boolean endsWithIgnoreCase(final String value, final String suffix) {
        final int n = suffix.length();
        return value.regionMatches(true, value.length() - n, suffix, 0, n);
    }

}

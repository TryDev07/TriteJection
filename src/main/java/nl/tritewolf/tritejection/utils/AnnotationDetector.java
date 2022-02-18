package nl.tritewolf.tritejection.utils;

import nl.tritewolf.tritejection.utils.iterators.ClassFileIterator;
import nl.tritewolf.tritejection.utils.iterators.ResourceIterator;
import nl.tritewolf.tritejection.utils.types.FieldReporter;
import nl.tritewolf.tritejection.utils.types.MethodReporter;
import nl.tritewolf.tritejection.utils.types.Reporter;
import nl.tritewolf.tritejection.utils.types.TypeReporter;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public final class AnnotationDetector {

    // Only used during development. If set to "true" debug messages are displayed.
    private static final boolean DEBUG = false;

    // Constant Pool type tags
    private static final int CP_UTF8 = 1;
    private static final int CP_INTEGER = 3;
    private static final int CP_FLOAT = 4;
    private static final int CP_LONG = 5;
    private static final int CP_DOUBLE = 6;
    private static final int CP_CLASS = 7;
    private static final int CP_STRING = 8;
    private static final int CP_REF_FIELD = 9;
    private static final int CP_REF_METHOD = 10;
    private static final int CP_REF_INTERFACE = 11;
    private static final int CP_NAME_AND_TYPE = 12;
    private static final int CP_METHOD_HANDLE = 15;
    private static final int CP_METHOD_TYPE = 16;
    private static final int CP_INVOKE_DYNAMIC = 18;

    // AnnotationElementValue
    private static final int BYTE = 'B';
    private static final int CHAR = 'C';
    private static final int DOUBLE = 'D';
    private static final int FLOAT = 'F';
    private static final int INT = 'I';
    private static final int LONG = 'J';
    private static final int SHORT = 'S';
    private static final int BOOLEAN = 'Z';
    // used for AnnotationElement only
    private static final int STRING = 's';
    private static final int ENUM = 'e';
    private static final int CLASS = 'c';
    private static final int ANNOTATION = '@';
    private static final int ARRAY = '[';

    private final ClassFileBuffer cpBuffer = new ClassFileBuffer();
    private final Map<String, Class<? extends Annotation>> annotations;

    private TypeReporter typeReporter;
    private FieldReporter fieldReporter;
    private MethodReporter methodReporter;

    private String typeName;
    private Object[] constantPool;
    private String memberName;

    public AnnotationDetector(final Reporter reporter) {
        final Class<? extends Annotation>[] a = reporter.annotations();
        annotations = new HashMap<>(a.length);
        for (int i = 0; i < a.length; ++i) {
            annotations.put("L" + a[i].getName().replace('.', '/') + ";", a[i]);
        }
        if (reporter instanceof TypeReporter) {
            typeReporter = (TypeReporter)reporter;
        }
        if (reporter instanceof FieldReporter) {
            fieldReporter = (FieldReporter)reporter;
        }
        if (reporter instanceof MethodReporter) {
            methodReporter = (MethodReporter)reporter;
        }
        if (typeReporter == null && fieldReporter == null && methodReporter == null) {
            throw new AssertionError("No reporter defined");
        }
    }

    public void detect() throws IOException {
        detect(new ClassFileIterator());
    }

    public void detect( ClassLoader loader, final String... packageNames) throws IOException {
        final String[] pkgNameFilter = new String[packageNames.length];
        for (int i = 0; i < pkgNameFilter.length; ++i) {
            pkgNameFilter[i] = packageNames[i].replace('.', '/');
            if (!pkgNameFilter[i].endsWith("/")) {
                pkgNameFilter[i] = pkgNameFilter[i].concat("/");
            }
        }
        final Set<File> files = new HashSet<File>();
        for (final String packageName : pkgNameFilter) {
            final Enumeration<URL> resourceEnum = loader.getResources(packageName);
            while (resourceEnum.hasMoreElements()) {
                final URL url = resourceEnum.nextElement();
                if ("file".equals(url.getProtocol())) {
                    final File dir = toFile(url);
                    if (dir.isDirectory()) {
                        files.add(dir);
                    } else {
                        throw new AssertionError("Not a recognized file URL: " + url);
                    }
                } else if (url.getProtocol().startsWith("vfs")) {
                    detect(new VfsResourceIterator(url));
                } else {
                    final File jarFile = toFile(openJarURLConnection(url).getJarFileURL());
                    if (jarFile.isFile()) {
                        files.add(jarFile);
                    } else {
                        throw new AssertionError("Not a File: " + jarFile);
                    }
                }
            }
        }
        if (DEBUG) {
            print("Files to scan: %s", files);
        }
        if (!files.isEmpty()) {
            detect(new ClassFileIterator(files.toArray(new File[0]), pkgNameFilter));
        }
    }

    public void detect(final File... filesOrDirectories) throws IOException {
        if (DEBUG) {
            print("detectFilesOrDirectories: %s", (Object)filesOrDirectories);
        }
        detect(new ClassFileIterator(filesOrDirectories, null));
    }


    private File toFile(final URL url) {
        try {
            return new File(url.toURI());
        } catch (URISyntaxException ex) {
            throw new AssertionError("Unable to convert URI to File: " + url);
        }
    }

    private JarURLConnection openJarURLConnection(final URL url) throws IOException {
        final URL checkedUrl;
        if ("zip".equals(url.getProtocol())) {
            checkedUrl = new URL(url.toExternalForm().replace("zip:/", "jar:file:/"));
        } else {
            checkedUrl = url;
        }
        URLConnection urlConnection = checkedUrl.openConnection();
        if (checkedUrl.getProtocol().startsWith("bundle")) {
            try {
                final Method m = urlConnection.getClass().getDeclaredMethod("getLocalURL");
                if (!m.isAccessible()) {
                    m.setAccessible(true);
                }
                final URL jarUrl = (URL)m.invoke(urlConnection);
                urlConnection = jarUrl.openConnection();
            } catch (Exception ex) {
                throw new AssertionError("Couldn't read jar file URL from bundle: " + ex);
            }
        }
        if (urlConnection instanceof JarURLConnection) {
            return (JarURLConnection)urlConnection;
        } else {
            throw new AssertionError(
                "Unknown URLConnection type: " + urlConnection.getClass().getName());
        }
    }

    @SuppressWarnings("illegalcatch")
    public void detect(final ResourceIterator iterator) throws IOException {
        InputStream stream;
        while ((stream = iterator.next()) != null) {
            try {
                cpBuffer.readFrom(stream);
                if (hasCafebabe(cpBuffer)) {
                    detect(cpBuffer);
                }
            } catch (Throwable t) {
                if (!(stream instanceof FileInputStream)) {
                    stream.close();
                }
            } finally {
                if (stream instanceof FileInputStream) {
                    stream.close();
                }
            }
        }
    }

    private boolean hasCafebabe(final ClassFileBuffer buffer) throws IOException {
        return buffer.size() > 4 &&  buffer.readInt() == 0xCAFEBABE;
    }

    private void detect(final DataInput di) throws IOException {
        readVersion(di);
        readConstantPoolEntries(di);
        readAccessFlags(di);
        readThisClass(di);
        readSuperClass(di);
        readInterfaces(di);
        readFields(di);
        readMethods(di);
        readAttributes(di, 'T', typeReporter == null);
    }

    private void readVersion(final DataInput di) throws IOException {
        if (DEBUG) {
            print("Java Class version %2$d.%1$d",
                di.readUnsignedShort(), di.readUnsignedShort());
        } else {
            di.skipBytes(4);
        }
    }

    private void readConstantPoolEntries(final DataInput di) throws IOException {
        final int count = di.readUnsignedShort();
        constantPool = new Object[count];
        for (int i = 1; i < count; ++i) {
            if (readConstantPoolEntry(di, i)) {
                ++i;
            }
        }
    }

    /**
     * Return {@code true} if a double slot is read (in case of Double or Long constant).
     */
    private boolean readConstantPoolEntry(final DataInput di, final int index)
        throws IOException {

        final int tag = di.readUnsignedByte();
        switch (tag) {
            case CP_METHOD_TYPE:
                di.skipBytes(2);  // readUnsignedShort()
                return false;
            case CP_METHOD_HANDLE:
                di.skipBytes(3);
                return false;
            case CP_INTEGER:
            case CP_FLOAT:
            case CP_REF_FIELD:
            case CP_REF_METHOD:
            case CP_REF_INTERFACE:
            case CP_NAME_AND_TYPE:
            case CP_INVOKE_DYNAMIC:
                di.skipBytes(4); // readInt() / readFloat() / readUnsignedShort() * 2
                return false;
            case CP_LONG:
            case CP_DOUBLE:
                di.skipBytes(8); // readLong() / readDouble()
                return true;
            case CP_UTF8:
                constantPool[index] = di.readUTF();
                return false;
            case CP_CLASS:
            case CP_STRING:
                // reference to CP_UTF8 entry. The referenced index can have a higher number!
                constantPool[index] = di.readUnsignedShort();
                return false;
            default:
                throw new ClassFormatError(
                    "Unkown tag value for constant pool entry: " + tag);
        }
    }

    private void readAccessFlags(final DataInput di) throws IOException {
        di.skipBytes(2); // u2
    }

    private void readThisClass(final DataInput di) throws IOException {
        typeName = resolveUtf8(di);
        if (DEBUG) {
            print("read type '%s'", typeName);
        }
    }

    private void readSuperClass(final DataInput di) throws IOException {
        di.skipBytes(2); // u2
    }

    private void readInterfaces(final DataInput di) throws IOException {
        final int count = di.readUnsignedShort();
        di.skipBytes(count * 2); // count * u2
    }

    private void readFields(final DataInput di) throws IOException {
        final int count = di.readUnsignedShort();
        if (DEBUG) {
            print("field count = %d", count);
        }
        for (int i = 0; i < count; ++i) {
            readAccessFlags(di);
            memberName = resolveUtf8(di);
            final String descriptor = resolveUtf8(di);
            readAttributes(di, 'F', fieldReporter == null);
            if (DEBUG) {
                print("Field: %s, descriptor: %s", memberName, descriptor);
            }
        }
    }

    private void readMethods(final DataInput di) throws IOException {
        final int count = di.readUnsignedShort();
        if (DEBUG) {
            print("method count = %d", count);
        }
        for (int i = 0; i < count; ++i) {
            readAccessFlags(di);
            memberName = resolveUtf8(di);
            final String descriptor = resolveUtf8(di);
            readAttributes(di, 'M', methodReporter == null);
            if (DEBUG) {
                print("Method: %s, descriptor: %s", memberName, descriptor);
            }
        }
    }

    private void readAttributes(final DataInput di, final char reporterType,
        final boolean skipReporting) throws IOException {

        final int count = di.readUnsignedShort();
        if (DEBUG) {
            print("attribute count (%s) = %d", reporterType, count);
        }
        for (int i = 0; i < count; ++i) {
            final String name = resolveUtf8(di);
            final int length = di.readInt();
            if (!skipReporting &&
                ("RuntimeVisibleAnnotations".equals(name) ||
                "RuntimeInvisibleAnnotations".equals(name))) {
                readAnnotations(di, reporterType);
            } else {
                if (DEBUG) {
                    print("skip attribute %s", name);
                }
                di.skipBytes(length);
            }
        }
    }

    private void readAnnotations(final DataInput di, final char reporterType)
        throws IOException {

        final int count = di.readUnsignedShort();
        if (DEBUG) {
            print("annotation count (%s) = %d", reporterType, count);
        }
        for (int i = 0; i < count; ++i) {
            final String rawTypeName = readAnnotation(di);
            final Class<? extends Annotation> type = annotations.get(rawTypeName);
            if (type == null) {
                continue;
            }
            final String externalTypeName = typeName.replace('/', '.');
            switch (reporterType) {
                case 'T':
                    typeReporter.reportTypeAnnotation(type, externalTypeName);
                    break;
                case 'F':
                    fieldReporter.reportFieldAnnotation(type, externalTypeName, memberName);
                    break;
                case 'M':
                    methodReporter.reportMethodAnnotation(type, externalTypeName, memberName);
                    break;
                default:
                    throw new AssertionError("reporterType=" + reporterType);
            }
        }
    }

    private String readAnnotation(final DataInput di) throws IOException {
        final String rawTypeName = resolveUtf8(di);
        // num_element_value_pairs
        final int count = di.readUnsignedShort();
        if (DEBUG) {
            print("annotation elements count: %d", count);
        }
        for (int i = 0; i < count; ++i) {
            if (DEBUG) {
                print("element '%s'", resolveUtf8(di));
            } else {
                di.skipBytes(2);
            }
            readAnnotationElementValue(di);
        }
        return rawTypeName;
    }


    private void readAnnotationElementValue(final DataInput di) throws IOException {
        final int tag = di.readUnsignedByte();
        if (DEBUG) {
            print("tag='%c'", (char)tag);
        }
        switch (tag) {
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
            case BOOLEAN:
            case STRING:
                di.skipBytes(2);
                break;
            case ENUM:
                di.skipBytes(4); // 2 * u2
                break;
            case CLASS:
                di.skipBytes(2);
                break;
            case ANNOTATION:
                readAnnotation(di);
                break;
            case ARRAY:
                final int count = di.readUnsignedShort();
                for (int i = 0; i < count; ++i) {
                    readAnnotationElementValue(di);
                }
                break;
            default:
                throw new ClassFormatError("Not a valid annotation element type tag: 0x" +
                    Integer.toHexString(tag));
        }
    }

    private String resolveUtf8(final DataInput di) throws IOException {
        final int index = di.readUnsignedShort();
        final Object value = constantPool[index];
        final String s;
        if (value instanceof Integer) {
            s = (String)constantPool[(Integer)value];
            if (DEBUG) {
                print("resolveUtf8(%d): %d --> %s", index, value, s);
            }
        } else {
            s = (String)value;
            if (DEBUG) {
                print("resolveUtf8(%d): %s", index, s);
            }
        }

        return s;
    }

    @SuppressWarnings("regexpsinglelinejava")
    private static void print(final String message, final Object... args) {
        if (DEBUG) {
            final String logMessage;
            if (args.length == 0) {
                logMessage = message;
            } else {
                for (int i = 0; i < args.length; ++i) {
                    if (args[i] == null) {
                        continue;
                    }
                    if (args[i].getClass().isArray()) {
                        args[i] = Arrays.toString((Object[])args[i]);
                    } else if (args[i] == Class.class) {
                        args[i] = ((Class<?>)args[i]).getName();
                    }
                }
                logMessage = String.format(message, args);
            }
            System.out.println(logMessage);
        }
    }

}

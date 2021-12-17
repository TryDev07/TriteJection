package nl.tritewolf.tritejection.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import nl.tritewolf.tritejection.TriteJection;
import nl.tritewolf.tritejection.annotations.TriteJect;
import nl.tritewolf.tritejection.utils.iterators.ResourceIterator;
import org.jboss.vfs.VirtualFile;

final class VfsResourceIterator extends ResourceIterator {

    private final List<VirtualFile> files;
    private int index = -1;

    VfsResourceIterator(final URL url) throws IOException {
        final VirtualFile vFile = (VirtualFile)url.getContent();
        files = vFile.getChildrenRecursively();
    }

    @Override
    public InputStream next() throws IOException {
        while (true) {
            if (++index >= files.size()) {
                // no files
                return null;
            }
            final VirtualFile f = files.get(index);
            if (f.isFile() && f.getName().endsWith(".class")) {
                return f.openStream();
            }
        }
    }

}

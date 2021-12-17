package nl.tritewolf.tritejection.utils.iterators;

import java.io.IOException;
import java.io.InputStream;

public abstract class ResourceIterator {

    public abstract InputStream next() throws IOException;

}

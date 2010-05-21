package org.jdna.url;

import java.io.IOException;

public interface IUrlFactory {
    public IUrl createUrl(String url) throws IOException;
}

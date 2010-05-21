package org.jdna.sage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import sage.MediaFileMetadataParser;

public class FailingDVDMetadataParser implements MediaFileMetadataParser {
    public FailingDVDMetadataParser() {
    }

    public Object extractMetadata(File file, String arg) {
        System.out.println("Handling: " + file.getAbsolutePath());
        Map<String,String> props = new HashMap<String, String>();
        props.put("Title", "Just a Title");
        props.put("Description", null);
        props.put("Actor", null);
        for (Map.Entry<String, String> me : props.entrySet()) {
            System.out.println("Prop: " + me.getKey() + " = " + me.getValue());
        }
        return props;
    }
}

package test;

import java.io.File;
import java.io.IOException;

import org.jdna.media.IMediaResource;
import org.jdna.media.MediaResourceFactory;

import sagex.api.ChannelAPI;

public class TestSageDVD {
    public static void main(String args[]) throws IOException {
        IMediaResource mr = MediaResourceFactory.getInstance().createResource(new File("testing/Movies/Queen/VIDEO_TS").toURI());
        System.out.println("Media Resource Name: " + mr.getName());
        System.out.println("Media Resource Type: " + mr.getClass().getName());
    }
}

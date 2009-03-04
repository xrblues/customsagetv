package test;

import java.io.File;
import java.io.IOException;

import org.jdna.media.IMediaResource;
import org.jdna.media.MediaResourceFactory;

import sagex.api.ChannelAPI;

public class TestSageDVD {
    public static void main(String args[]) throws IOException {
        IMediaResource mr = MediaResourceFactory.getInstance().createResource(new File("c:\\dvd\\casino_royale\\video_ts").toURI());
        System.out.println("Media Resource Name: " + mr.getName());
        System.out.println("Media Resource Type: " + mr.getClass().getName());
    }
}

package test;

import java.io.File;
import java.io.IOException;

import org.jdna.metadataupdater.MetadataUpdater;

import sagex.phoenix.fanart.FanartUtil;
import sagex.phoenix.fanart.FanartUtil.MediaArtifactType;
import sagex.phoenix.fanart.FanartUtil.MediaType;

public class TestDotDotDotBug {
    public static void main(String args[]) throws IOException {
        MetadataUpdater.initConfiguration();

        File posters[] = FanartUtil.getCentalFanartArtifacts(MediaType.MOVIE, MediaArtifactType.POSTER, "When Harry Met Sally...", "testing/fanart/", null);
        for (File poster: posters) {
            System.out.println("File: " + poster.getAbsolutePath());
            System.out.println("Exists: " + poster.exists());
        }
    }
}

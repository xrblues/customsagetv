package test;

import java.io.File;

import sagex.api.AiringAPI;
import sagex.api.MediaFileAPI;

public class TestMisc {
    public static void main(String args[]) {
        Object mediafile = MediaFileAPI.GetMediaFileForFilePath(new File("/var/media/tv/Futurama-SpacePilot3000-2007144-0.ts"));
        System.out.printf("     Title: %s\n", MediaFileAPI.GetMediaTitle(mediafile));
        System.out.printf("Is Watched: %s\n", AiringAPI.IsWatched(mediafile)); 
        
        System.out.println("Object Class Name: " + Object.class.getName());
    }
}

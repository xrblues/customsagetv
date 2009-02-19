package test;

import java.io.File;

import sagex.api.AiringAPI;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;

public class TestSageMediaFileAPI {
    public static void main(String args[]) {
        //Object mf = MediaFileAPI.GetMediaFileForFilePath(new File("/home/FileServer/Media/Videos/VideoCollection/Movies/New2/Changeling-cd1.avi"));
        Object mf = MediaFileAPI.GetMediaFileForFilePath(new File("/home/FileServer/Media/Videos/VideoCollection/TV/30 Rock/Season 2/30.Rock.S02E15 - Cooter.avi"));
        Object airing = MediaFileAPI.GetMediaFileAiring(mf);
        Object show = AiringAPI.GetShow(airing);
        System.out.println("Title: " + ShowAPI.GetShowTitle(show));
        System.out.println(" Desc: " + ShowAPI.GetShowDescription(show));

        //ShowAPI.AddShow(Title, IsFirstRun, Episode, Description, Duration, Category, SubCategory, PeopleList, RolesListForPeopleList, Rated, ExpandedRatingsList, Year, ParentalRating, MiscList, ExternalID, Language, OriginalAirDate)
    }
}

package org.jdna.metadataupdater;

import java.util.List;

import sagex.phoenix.metadata.ICastMember;
import sagex.phoenix.metadata.IMediaArt;
import sagex.phoenix.metadata.IMetadata;
import sagex.phoenix.progress.IProgressMonitor;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.IMediaResourceVisitor;
import sagex.phoenix.vfs.MediaResourceType;
import sagex.phoenix.vfs.util.PathUtils;

public class ListMovieVisitor implements IMediaResourceVisitor {
    private boolean verbose = false;

	public boolean visit(IMediaResource resource, IProgressMonitor monitor) {
        if (resource.isType(MediaResourceType.ANY_VIDEO.value())) {
            if (verbose) {
                showMetadata((IMediaFile) resource);
            } else {
                IMediaFile mediaFile = (IMediaFile) resource;
                String code = " ";
                System.out.printf("%s %-40s (%s)\n", code, mediaFile.getTitle(), PathUtils.getLocation(mediaFile));
            }
        }
        return true;
	}

	public ListMovieVisitor(boolean verbose) {
        this.verbose = verbose;
        System.out.printf("\nListing Movies\n- = Missing MetaData;\n");
    }

    public void showMetadata(IMediaFile mf) {
        printMetadata(mf.getMetadata(), PathUtils.getName(mf), PathUtils.getLocation(mf));
    }
    
    public static void printMetadata(IMetadata md, String name, String location) {
        if (md == null) {
            System.out.println("No Metadata for: " + location);
        } else {
            col2("--- BEGIN:", name);
            col2("Movie:", name);
            col2("Title:", md.getMediaTitle());
            col2("Plot:", md.getDescription());
            col2("Genres:", toGenreString(md.getGenres()));
            col2("MPAA Rating:", md.getRated());
            col2("MPAA Rating Full:", md.getExtendedRatings());
            col2("User Rating:", md.getUserRating());
            col2("Year:", md.getYear());
            col2("Release Date:", md.getOriginalAirDate());
            col2("Runtime:", md.getRunningTime());
            col2("Provider DataId:", md.getMediaProviderDataID());
            col2("Provider Id:", md.getMediaProviderID());
                List<IMediaArt> maArr = md.getFanart();
                for (int i=0;i<maArr.size();i++) {
                    col2(String.format("%s %s:", maArr.get(i).getType(), i+1), maArr.get(i).getDownloadUrl());
                }
            col2("Directors:", toSimpleCastString(md.getDirectors()));
            col2("Writers:", toSimpleCastString(md.getWriters()));
            List<ICastMember> actors = md.getActors();
            if (actors != null) {
                col2("Actors:", "-----------");
                for (ICastMember cm : actors) {
                    col2(cm.getName() + ":", cm.getRole());
                }
            } else {
                col2("Actors:", "-- NONE --");
            }
            col2("----- END:", location.toString());
            System.out.println("");
        }
    }

    private static String toSimpleCastString(List<ICastMember> cast) {
        if (cast == null) return "-- NONE --";
        StringBuffer sb = new StringBuffer();
        for (ICastMember cm : cast) {
            sb.append(cm.getName()).append(" / ");
        }
        return sb.toString();
    }

    private static String toGenreString(List<String> genres) {
        if (genres == null) return "-- NONE --";
        StringBuffer sb = new StringBuffer();
        for (String s : genres) {
            sb.append(s).append(" / ");
        }
        return sb.toString();
    }

    private static void col2(String c1, Object c2) {
        System.out.printf("%25s %s\n", c1, c2);
    }

}

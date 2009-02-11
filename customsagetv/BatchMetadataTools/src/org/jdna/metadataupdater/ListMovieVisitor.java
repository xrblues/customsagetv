package org.jdna.metadataupdater;

import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IMediaArt;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.MetadataKey;

public class ListMovieVisitor implements IMediaResourceVisitor {
    private boolean verbose = false;

    public ListMovieVisitor(boolean verbose) {
        this.verbose = verbose;
        System.out.printf("\nListing Movies\n- = Missing MetaData;\n");
    }

    public void visit(IMediaResource resource) {
        if (resource.getType() == IMediaFile.TYPE_FILE) {
            if (verbose) {
                showMetadata((IMediaFile) resource);
            } else {
                IMediaMetadata md = resource.getMetadata();
                IMediaFile mediaFile = (IMediaFile) resource;
                String code = ((md == null) ? "-" : " ");
                System.out.printf("%s %-40s (%s)\n", code, mediaFile.getTitle(), mediaFile.getLocationUri());
            }
        }
    }

    public void showMetadata(IMediaFile mf) {
        printMetadata(mf.getMetadata(), mf.getName(), mf.getLocationUri());
    }
    
    public static void printMetadata(IMediaMetadata md, String name, String location) {
        if (md == null) {
            System.out.println("No Metadata for: " + location);
        } else {
            col2("--- BEGIN:", name);
            col2("Movie:", name);
            col2("Title:", md.getTitle());
            col2("Plot:", md.getDescription());
            col2("Genres:", toGenreString(md.getGenres()));
            col2("MPAA Rating:", (String) md.get(MetadataKey.MPAA_RATING));
            col2("MPAA Rating Full:", (String) md.get(MetadataKey.MPAA_RATING_DESCRIPTION));
            col2("User Rating:", md.getUserRating());
            col2("Company:", (String) md.get(MetadataKey.COMPANY));
            col2("Year:", md.getYear());
            col2("Release Date:", md.getReleaseDate());
            col2("Runtime:", md.getRuntime());
            col2("Aspect Ratio:", (String) md.get(MetadataKey.ASPECT_RATIO));
            col2("Provider Url:", md.getProviderDataUrl());
            col2("Provider Id:", md.getProviderId());
            if (md.getMediaArt(IMediaArt.POSTER) != null) {
                IMediaArt maArr[] = md.getMediaArt(IMediaArt.POSTER);
                for (int i=0;i<maArr.length;i++) {
                    col2(String.format("Poster %s:", i+1), maArr[i].getDownloadUrl());
                }
            } else {
                col2("Poster:", "No Poster");
            }
            if (md.getMediaArt(IMediaArt.BACKGROUND) != null) {
                IMediaArt maArr[] = md.getMediaArt(IMediaArt.BACKGROUND);
                for (int i=0;i<maArr.length;i++) {
                    col2(String.format("Background %s:", i+1), maArr[i].getDownloadUrl());
                }
            } else {
                col2("Background:", "No Background");
            }
            col2("Directors:", toSimpleCastString(md.getCastMembers(ICastMember.DIRECTOR)));
            col2("Writers:", toSimpleCastString(md.getCastMembers(ICastMember.WRITER)));
            ICastMember actors[] = md.getCastMembers(ICastMember.ACTOR);
            if (actors != null) {
                col2("Actors:", "-----------");
                for (ICastMember cm : actors) {
                    col2(cm.getName() + ":", cm.getPart());
                }
            } else {
                col2("Actors:", "-- NONE --");
            }
            col2("----- END:", location);
            System.out.println("");
        }
    }

    private static String toSimpleCastString(ICastMember[] cast) {
        if (cast == null) return "-- NONE --";
        StringBuffer sb = new StringBuffer();
        for (ICastMember cm : cast) {
            sb.append(cm.getName()).append(" / ");
        }
        return sb.toString();
    }

    private static String toGenreString(String[] genres) {
        if (genres == null) return "-- NONE --";
        StringBuffer sb = new StringBuffer();
        for (String s : genres) {
            sb.append(s).append(" / ");
        }
        return sb.toString();
    }

    private static void col2(String c1, String c2) {
        System.out.printf("%25s %s\n", c1, c2);
    }

}

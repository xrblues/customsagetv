package org.jdna.metadataupdater;

import java.util.List;

import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.IPath;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IMediaArt;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.MetadataKey;

import sagex.phoenix.fanart.MediaArtifactType;

public class ListMovieVisitor implements IMediaResourceVisitor {
    private boolean verbose = false;
    private IMediaMetadataPersistence persistence;

    public ListMovieVisitor(IMediaMetadataPersistence persistence, boolean verbose) {
        this.verbose = verbose;
        this.persistence = persistence;
        System.out.printf("\nListing Movies\n- = Missing MetaData;\n");
    }

    public void visit(IMediaResource resource) {
        if (resource.getType() == IMediaResource.Type.File) {
            if (verbose) {
                showMetadata((IMediaFile) resource);
            } else {
                IMediaMetadata md = persistence.loadMetaData(resource);
                IMediaFile mediaFile = (IMediaFile) resource;
                String code = ((md == null) ? "-" : " ");
                System.out.printf("%s %-40s (%s)\n", code, mediaFile.getTitle(), mediaFile.getLocation());
            }
        }
    }

    public void showMetadata(IMediaFile mf) {
        printMetadata(persistence.loadMetaData(mf), mf.getName(), mf.getLocation());
    }
    
    public static void printMetadata(IMediaMetadata md, String name, IPath location) {
        if (md == null) {
            System.out.println("No Metadata for: " + location);
        } else {
            col2("--- BEGIN:", name);
            col2("Movie:", name);
            col2("Title:", MetadataAPI.getMediaTitle(md));
            col2("Plot:", MetadataAPI.getDescription(md));
            col2("Genres:", toGenreString(MetadataAPI.getGenres(md)));
            col2("MPAA Rating:", md.getString(MetadataKey.MPAA_RATING));
            col2("MPAA Rating Full:", md.getString(MetadataKey.MPAA_RATING_DESCRIPTION));
            col2("User Rating:", MetadataAPI.getUserRating(md));
            col2("Company:", md.getString(MetadataKey.COMPANY));
            col2("Year:", MetadataAPI.getYear(md));
            col2("Release Date:", MetadataAPI.getReleaseDate(md));
            col2("Runtime:", MetadataAPI.getRuntime(md));
            col2("Aspect Ratio:", md.getString(MetadataKey.ASPECT_RATIO));
            col2("Provider DataId:", (MetadataAPI.getProviderDataId(md)==null?"Not Set":MetadataAPI.getProviderDataId(md)));
            col2("Provider Url:", MetadataAPI.getProviderDataUrl(md));
            col2("Provider Id:", MetadataAPI.getProviderId(md));
            if (MetadataAPI.getMediaArt(md, MediaArtifactType.POSTER) != null) {
                List<IMediaArt> maArr = MetadataAPI.getMediaArt(md, MediaArtifactType.POSTER);
                for (int i=0;i<maArr.size();i++) {
                    col2(String.format("Poster %s:", i+1), maArr.get(i).getDownloadUrl());
                }
            } else {
                col2("Poster:", "No Poster");
            }
            if (MetadataAPI.getMediaArt(md, MediaArtifactType.BACKGROUND) != null) {
                List<IMediaArt> maArr = MetadataAPI.getMediaArt(md, MediaArtifactType.BACKGROUND);
                for (int i=0;i<maArr.size();i++) {
                    col2(String.format("Background %s:", i+1), maArr.get(i).getDownloadUrl());
                }
            } else {
                col2("Background:", "No Background");
            }
            if (MetadataAPI.getMediaArt(md, MediaArtifactType.BANNER) != null) {
                List<IMediaArt> maArr = MetadataAPI.getMediaArt(md, MediaArtifactType.BANNER);
                for (int i=0;i<maArr.size();i++) {
                    col2(String.format("Banner %s:", i+1), maArr.get(i).getDownloadUrl());
                }
            } else {
                col2("Banner:", "No Banner");
            }
            col2("Directors:", toSimpleCastString(MetadataAPI.getCastMembers(md, ICastMember.DIRECTOR)));
            col2("Writers:", toSimpleCastString(MetadataAPI.getCastMembers(md, ICastMember.WRITER)));
            List<ICastMember> actors = MetadataAPI.getCastMembers(md, ICastMember.ACTOR);
            if (actors != null) {
                col2("Actors:", "-----------");
                for (ICastMember cm : actors) {
                    col2(cm.getName() + ":", cm.getPart());
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

    private static void col2(String c1, String c2) {
        System.out.printf("%25s %s\n", c1, c2);
    }

}

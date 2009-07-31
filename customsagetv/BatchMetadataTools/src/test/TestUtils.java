package test;

import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.MetadataKey;

public class TestUtils {
    public static void dumpResults(java.util.List<IMediaSearchResult> results) {
        System.out.println("Begin Dumping Results");
        for (IMediaSearchResult r : results) {
            dumpResult(r);
        }
        System.out.println("End Dumping Results");
    }

    public static void dumpResult(IMediaSearchResult r) {
        System.out.printf("ResultClass: %s\n", r.getClass().getName());
        System.out.printf("      Title: %s\n", r.getTitle());
        System.out.printf("       Year: %s\n", r.getYear());
        System.out.printf("      Match: %02f\n------------------------------------\n", r.getScore());
    }

    public static void dumpMetaData(IMediaMetadata md) {
        for (MetadataKey k : MetadataKey.values()) {
            String o  =md.getString(k);
            if (o!=null) {
                System.out.printf("%20s: %s\n", k, o);
            }
        }
    }

    public static void dumpCastMember(ICastMember[] members) {
        for (ICastMember cm : members) {
            if (cm.getType() == ICastMember.ACTOR) {
                System.out.printf("Actor: %s as %s\n", cm.getName(), cm.getPart());
            } else if (cm.getType() == ICastMember.DIRECTOR) {
                System.out.printf("Director: %s\n", cm.getName());
            } else if (cm.getType() == ICastMember.WRITER) {
                System.out.printf("Writer: %s\n", cm.getName());
            } else {
                System.out.printf("Other: %s doing %s\n", cm.getName(), cm.getPart());
            }
        }
    }

}

package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdna.media.IMediaFile;
import org.jdna.sage.media.SageMediaFile;

import sagex.SageAPI;
import sagex.api.AiringAPI;
import sagex.stub.StubSageAPI;

public class TestAiringParser {
    public static void main(String args[]) {
        SageAPI.setProvider(SageAPI.getRemoteProvider());
        SageAPI.setProvider(new StubSageAPI());
        String file = "Futurama-BenderShouldNotBeAllowedonTelevision-2232522-0.ts";
        Pattern p = Pattern.compile("([0-9]+)-[0-9]{1,2}\\.");
        Matcher m = p.matcher(file);
        if (m.find()) {
            System.out.println("Airing ID: " + m.group(1));
            
            Object airing = AiringAPI.GetAiringForID(Integer.parseInt(m.group(1)));
            if (airing!=null) {
                System.out.println("Airing Title: " + AiringAPI.GetAiringTitle(airing));
            }
            IMediaFile mf = new SageMediaFile(airing);
            System.out.println("Media Title: " + mf.getTitle() + "; type: " + mf.getType() + "; content: " + mf.getContentType());
        }
    }
}

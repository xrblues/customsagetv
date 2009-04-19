package org.jdna.media.metadata;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.impl.CDStackingModel;

public class MediaMetadataUtils {

    private static final Logger log = Logger.getLogger(MediaMetadataUtils.class);

    public static void writeImageFromUrl(String url, File out) throws IOException {
        // make the directory, if it doesn't exist.
        out.getParentFile().mkdirs();
        
        URL u = new URL(url);
        URLConnection conn = u.openConnection();
        if (conn instanceof HttpURLConnection) {
            conn.setRequestProperty("User-Agent", ConfigurationManager.getInstance().getUrlConfiguration().getHttpUserAgent());
        }

        BufferedImage img = ImageIO.read(conn.getInputStream());
        ImageIO.write(img, "jpg", out);
        log.debug("Wrote standard image: " + out.getAbsolutePath());
    }

    public static void writeImageFromUrl(String url, File out, int scaleWidth) throws IOException {
        // make the directory, if it doesn't exist.
        out.getParentFile().mkdirs();
        
        URL u = new URL(url);
        URLConnection conn = u.openConnection();
        if (conn instanceof HttpURLConnection) {
            conn.setRequestProperty("User-Agent", ConfigurationManager.getInstance().getUrlConfiguration().getHttpUserAgent());
        }
        
        log.debug("Scaling was requested: " + scaleWidth);

        BufferedImage imageSrc = ImageIO.read(conn.getInputStream());
        if (scaleWidth>0 && imageSrc.getWidth()>scaleWidth) {
            // scale
            int width = imageSrc.getWidth();
            int height = imageSrc.getHeight();

            int thumbWidth = scaleWidth;
            float div = (float)width/thumbWidth;
            height = (int)(height/div);

            log.warn(String.format("Scaling Poster from %sx%s to %sx%s", imageSrc.getWidth(), imageSrc.getHeight(), thumbWidth, height));
            Image img = imageSrc.getScaledInstance(thumbWidth, height ,Image.SCALE_SMOOTH);

            BufferedImage bi = new BufferedImage(thumbWidth, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D biContext = bi.createGraphics();
            biContext.drawImage(img, 0, 0, null);
            
            imageSrc = bi;
        } else {
            log.debug("Scaling was not used because image was smaller than the scale width");
        }
        ImageIO.write(imageSrc, "jpg", out);
        log.debug("Wrote scaled image: " + out.getAbsolutePath());
    }
    
    /**
     * For the purposes of searching it will, keep only alpha numeric characters and '
     * 
     * @param s
     * @return
     */
    public static String removeNonSearchCharacters(String s) {
        if (s==null) return null;
        return (s.replaceAll("[^A-Za-z0-9']", " ")).replaceAll("[\\ ]+", " ");
    }

    /**
     * Utility method that can be used to clean the search criteria before
     * sending it to the seach function. eg, it removes any non alphanumeric
     * characters and replaces them with spaces. remove cd volumes, and some
     * common words like dvd, dvrip, etc.
     * 
     * You can control the cd and word tokens following keys in the
     * configuration manager.
     * 
     * <pre>
     * org.jdna.media.metadata.VideoMetaDataUtils.cleanSearchCriteria.cdTokens
     * org.jdna.media.metadata.VideoMetaDataUtils.cleanSearchCriteria.wordTokens
     * </pre>
     */
    public static String cleanSearchCriteria(String s, boolean removeYear) {
        String wordTokens[] = ConfigurationManager.getInstance().getMetadataConfiguration().getWordsToClean().split(",");

        s = CDStackingModel.INSTANCE.getStackedTitle(s);
        log.debug("Cleaning Search Criteria: " + s);

        String parts[] = removeNonSearchCharacters(s).split(" ");
        StringBuffer sb = new StringBuffer();
        int l = parts.length;
        for (int i = 0; i < l; i++) {
            String ss = parts[i].trim();

            // check all tokens for the word tokens to ignore
            boolean found = false;
            for (String t : wordTokens) {
                if (ss.equalsIgnoreCase(t)) {
                    log.debug("Word Token Matched: " + t);
                    found = true;
                    break;
                }
            }
            // stop processing if you find a word token
            if (found) break;

            sb.append(ss).append(" ");
        }

        String v = sb.toString().trim();
        if (v.length() == 0) {
            log.warn("After cleaning title we have no title... reseting title to the original: " + s);
            v = s;
        }

        if (removeYear) {
            v = v.replaceAll("19[0-9]+", "");
            v = v.replaceAll("20[0-9]+", "");
        }

        log.debug("Cleaned Title: " + v);
        return v;
    }

    public static String getBasename(File videoFile) {
        try {
            String name = videoFile.getName();
            name = name.substring(0, name.lastIndexOf("."));
            return name;
        } catch (Exception e) {
            return videoFile.getName();
        }
    }

    /**
     * Given a String in the format, ${KEY}..., it will replace all occurances
     * of ${KEY} with a lookup in the Map for KEY.
     * 
     * ie, ${TEST1} -- ${TEST2} would result in Hello -- There, provided that
     * the map contained 2 keys TEST1=Hello, TEST2=There
     * 
     * @param s
     *            Format String
     * @param map
     *            Map of Named Paramters
     * @return
     */
    public static String format(String s, Map props) {
        log.debug("Formatting: " + s);
        Pattern p = Pattern.compile("(\\$\\{[_\\.a-zA-Z]+\\})");
        Matcher m = p.matcher(s);
        StringBuffer sb = new StringBuffer();

        int lastStart = 0;
        while (m.find()) {
            String token = m.group(0);
            sb.append(s.substring(lastStart, m.start()));
            lastStart = m.end();
            String key = token.substring(2, token.length() - 1);
            String val = (String)props.get(key);
            if (val==null) val="";
            sb.append(val);
        }

        sb.append(s.substring(lastStart));

        return sb.toString();
    }
}
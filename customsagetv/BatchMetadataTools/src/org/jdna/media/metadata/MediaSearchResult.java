package org.jdna.media.metadata;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaSearchResult implements IMediaSearchResult, Serializable {
    private static final long serialVersionUID = 2L;

    private static final String                 EXTRA_ARGS_SEP       = ";;;;";

    
    private String            providerId, url, title, year;
    private MetadataID metadataId;
    private float			score;
    private Map<String,String> extraArgs = new HashMap<String, String>();
    
    public MediaSearchResult() {
    	
    }
    
    public MediaSearchResult(String providerId, float score) {
        this.providerId = providerId;
        this.score = score;
    }

    public MediaSearchResult(String providerId, String url, String title, String year, float score) {
        super();
        this.providerId = providerId;
        setUrlWithExtraArgs(url);
        this.title = title;
        this.year = year;
        this.score = score;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
    
    public float getScore(){
    	return score;
    }
    
    public void setScore(float score){
    	this.score = score;
    }

    public String getUrl() {
        return url;
    }
    
    public void setUrlWithExtraArgs(String url) {
        Map<String,String> extra = parseUrlArgs(url);
        if (extra==null) {
            setUrl(url);
        } else {
            this.extraArgs = extra;
            this.url = extra.get(MetadataKey.METADATA_PROVIDER_DATA_URL.getId());
            this.extraArgs.remove(MetadataKey.METADATA_PROVIDER_DATA_URL.getId());
        }
    }

    public void setUrl(String url) {
        this.url=url;
    }

    public MetadataID getMetadataId() {
        return metadataId;
    }

    public void setMetadataId(MetadataID id) {
        this.metadataId = id;
    }
    
    public void addExtraArg(String key, String value) {
        this.extraArgs.put(key, value);
    }
    
    public String getUrlWithExtraArgs() {
        String url = getUrl();
        if (extraArgs.size()>0) {
            url += EXTRA_ARGS_SEP;
            for (Iterator<Map.Entry<String, String>> i = extraArgs.entrySet().iterator(); i.hasNext();) {
                Map.Entry<String, String> me = i.next();
                url += (me.getKey() + "=" + URLEncoder.encode(me.getValue()));
                if (i.hasNext()) {
                    url += "&";
                }
            }
        }
        return url;
    }
    
    /**
     * given a provider data url, parse out the url and the extra provider args.
     * provider args are separated by ;;;;
     * 
     * @param providerDataUrl
     * @return
     */
    private Map<String, String> parseUrlArgs(String providerDataUrl) {
        if (providerDataUrl == null) return null;

        Map<String, String> map = new HashMap<String, String>();
        Pattern p = Pattern.compile("(.*)" + EXTRA_ARGS_SEP + "(.*)");
        Matcher m = p.matcher(providerDataUrl);
        if (m.find()) {
            map.put(MetadataKey.METADATA_PROVIDER_DATA_URL.getId(), m.group(1));

            Pattern argPattern = Pattern.compile("&?([^=]+)=([^&$]+)");
            Matcher argMatcher = argPattern.matcher(m.group(2));
            while (argMatcher.find()) {
                map.put(argMatcher.group(1), argMatcher.group(2));
            }
            return map;
        } else {
            return null;
        }
    }

    public Map<String, String> getExtraArgs() {
        return extraArgs;
    }
}

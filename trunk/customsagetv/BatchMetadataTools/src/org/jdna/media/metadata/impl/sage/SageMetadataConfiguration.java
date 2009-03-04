package org.jdna.media.metadata.impl.sage;

import org.jdna.persistence.annotations.Field;
import org.jdna.persistence.annotations.Table;

@Table(label="Sage Persistence", name = "sageMetadata", requiresKey = false, description = "Configuration for the SageTV Metadata Persistence")
public class SageMetadataConfiguration {
    @Field(label="Actor Mask", description = "How each Actor will be written.  This mask will be applied to each actor, and then appeneded into a single line for the properties file. {0} - Actor Name, {1} - Actor Role")
    private String actorMask        = "{0} -- {1};\n";

    @Field(label="Description Mask", description = "Description Mask (note ${PROP_FIELD_NAME} field names are looked up in the property file)")
    private String descriptionMask  = "${Description}\nUser Rating: ${_userRating}\n";

    @Field(label="Multi CD Title Mask", description = "Title to use for multi volume vidoes (_disc is disc # 1,2,3,etc)")
    private String multiCDTitleMask = "${Title} Disc ${_disc}";

    @Field(label="Single CD Title Mask", description = "Title to use for single volume vidoes")
    private String titleMask        = "${Title}";

    @Field(label="Genre Levels", description = "Number genre levels to write.  -1 means all levels.")
    private int    genreLevels      = 1;
    
    @Field(label="TV Title Mask", description="Title mask to use for TV Files")
    private String tvTitleMask = "${Title} - s${_season}e${_episode} - ${_showTitle}";
    
    @Field(label="TV DVD Title Mask", description="Title mask to use for TV on Dvd")
    private String tvDvdTitleMask = "${Title} - S${_season}D${_disc}";
    
    @Field(label="Rewrite Titles", description="Rewrite titles so that 'A Big Adventure' becomes 'Big Adventure, A'")
    private boolean rewriteTitle = false;
    
    @Field(label="Rewrite Title Regexp", description="A Search/Replace Regex containing 2 groups that will rewrite the title")
    private String rewriteTitleRegex = "^(in\\s+the|in\\s+a|i\\s+am|in|the|a|an|i|am)\\s+(.*)";
    
    @Field(label="Alternate Backdrop Location", description = "Location to save backdrops, if blank default location is used")
    private String alternateBackdropLocation = null;
    
    @Field(label="Safe Title Regex", description = "Regex used to replace characters from the title when writing backdrop")
    private String safeTitleReplaceRegex = ":|/|\\\\|\\*|\\?|<|>|\\|";
    
    public String getActorMask() {
        return actorMask;
    }

    public void setActorMask(String actorMask) {
        this.actorMask = actorMask;
    }

    public String getDescriptionMask() {
        return descriptionMask;
    }

    public void setDescriptionMask(String descriptionMask) {
        this.descriptionMask = descriptionMask;
    }

    public String getMultiCDTitleMask() {
        return multiCDTitleMask;
    }

    public void setMultiCDTitleMask(String multiCDTitleMask) {
        this.multiCDTitleMask = multiCDTitleMask;
    }

    public String getTitleMask() {
        return titleMask;
    }

    public void setTitleMask(String titleMask) {
        this.titleMask = titleMask;
    }

    public int getGenreLevels() {
        return genreLevels;
    }

    public void setGenreLevels(int genreLevels) {
        this.genreLevels = genreLevels;
    }

    public SageMetadataConfiguration() {
    }

    public String getTvTitleMask() {
        return tvTitleMask;
    }

    public void setTvTitleMask(String tvTitleMask) {
        this.tvTitleMask = tvTitleMask;
    }
    
    public String getTvDvdTitleMask() {
        return tvDvdTitleMask;
    }

    public void setTvDvdTitleMask(String tvDvdTitleMask) {
        this.tvDvdTitleMask = tvDvdTitleMask;
    }

    public boolean isRewriteTitle() {
        return rewriteTitle;
    }

    public void setRewriteTitle(boolean rewriteTitle) {
        this.rewriteTitle = rewriteTitle;
    }

    public String getRewriteTitleRegex() {
        return rewriteTitleRegex;
    }

    public void setRewriteTitleRegex(String rewriteTitleRegex) {
        this.rewriteTitleRegex = rewriteTitleRegex;
    }
    
    public String getAlternateBackdropLocation() {
        return alternateBackdropLocation;
    }

    public void setAlternateBackdropLocation(String alternateBackdropLocation) {
        this.alternateBackdropLocation = alternateBackdropLocation;
    }
    
    public String getSafeTitleReplaceRegex() {
        return safeTitleReplaceRegex;
    }

    public void setSafeTitleReplaceRegex(String alternateBackdropLocation) {
        this.safeTitleReplaceRegex = alternateBackdropLocation;
    }
}

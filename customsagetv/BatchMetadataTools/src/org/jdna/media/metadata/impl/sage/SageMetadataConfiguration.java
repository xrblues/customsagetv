package org.jdna.media.metadata.impl.sage;

import org.jdna.configuration.Field;
import org.jdna.configuration.FieldProxy;
import org.jdna.configuration.Group;
import org.jdna.configuration.GroupProxy;

@Group(label="Sage Persistence", path = "bmt/sageMetadata", description = "Configuration for the SageTV Metadata Persistence")
public class SageMetadataConfiguration extends GroupProxy {
    @Field(label="Actor Mask", description = "How each Actor will be written.  This mask will be applied to each actor, and then appeneded into a single line for the properties file. {0} - Actor Name, {1} - Actor Role")
    private FieldProxy<String> actorMask        = new FieldProxy<String>("{0} -- {1};\n");

    @Field(label="Description Mask", description = "Description Mask (note ${PROP_FIELD_NAME} field names are looked up in the property file)")
    private FieldProxy<String> descriptionMask  = new FieldProxy<String>("${"+SageProperty.DESCRIPTION.sageKey+"}\nUser Rating: ${"+SageProperty.USER_RATING.sageKey+"}\n");

    @Field(label="Multi CD Title Mask", description = "Title to use for multi volume vidoes (_disc is disc # 1,2,3,etc)")
    private FieldProxy<String> multiCDTitleMask = new FieldProxy<String>("${"+SageProperty.DISPLAY_TITLE.sageKey+"} Disc ${"+SageProperty.DISC.sageKey+"}");

    @Field(label="Single CD Title Mask", description = "Title to use for single volume vidoes")
    private FieldProxy<String> titleMask        = new FieldProxy<String>("${"+SageProperty.DISPLAY_TITLE.sageKey+"}");

    @Field(label="Genre Levels", description = "Number genre levels to write.  -1 means all levels.")
    private FieldProxy<Integer>    genreLevels      = new FieldProxy<Integer>(1);
    
    @Field(label="TV Title Mask", description="Title mask to use for TV Files")
    private FieldProxy<String> tvTitleMask = new FieldProxy<String>("${"+SageProperty.DISPLAY_TITLE.sageKey+"} - S${"+SageProperty.SEASON_NUMBER.sageKey+"}E${"+SageProperty.EPISODE_NUMBER.sageKey+"} - ${"+SageProperty.EPISODE_TITLE.sageKey+"}");
    
    @Field(label="TV DVD Title Mask", description="Title mask to use for TV on Dvd")
    private FieldProxy<String> tvDvdTitleMask = new FieldProxy<String>("${"+SageProperty.DISPLAY_TITLE.sageKey+"} - S${"+SageProperty.SEASON_NUMBER.sageKey+"}D${"+SageProperty.DISC.sageKey+"}");
    
    @Field(label="Rewrite Titles", description="Rewrite titles so that 'A Big Adventure' becomes 'Big Adventure, A'")
    private FieldProxy<Boolean> rewriteTitle = new FieldProxy<Boolean>(false);
    
    @Field(label="Rewrite Title Regexp", description="A Search/Replace Regex containing 2 groups that will rewrite the title")
    private FieldProxy<String> rewriteTitleRegex = new FieldProxy<String>("^(in\\s+the|in\\s+a|i\\s+am|in|the|a|an|i|am),?\\s+(.*)");

    public SageMetadataConfiguration() {
        super();
        init(this);
    }
    
    public String getActorMask() {
        return actorMask.getString();
    }

    public void setActorMask(String actorMask) {
        this.actorMask.set(actorMask);
    }

    public String getDescriptionMask() {
        return descriptionMask.getString();
    }

    public void setDescriptionMask(String descriptionMask) {
        this.descriptionMask.set(descriptionMask);
    }

    public String getMultiCDTitleMask() {
        return multiCDTitleMask.getString();
    }

    public void setMultiCDTitleMask(String multiCDTitleMask) {
        this.multiCDTitleMask.set(multiCDTitleMask);
    }

    public String getTitleMask() {
        return titleMask.getString();
    }

    public void setTitleMask(String titleMask) {
        this.titleMask.set(titleMask);
    }

    public int getGenreLevels() {
        return genreLevels.getInt();
    }

    public void setGenreLevels(int genreLevels) {
        this.genreLevels.set(genreLevels);
    }

    public String getTvTitleMask() {
        return tvTitleMask.getString();
    }

    public void setTvTitleMask(String tvTitleMask) {
        this.tvTitleMask.set(tvTitleMask);
    }
    
    public String getTvDvdTitleMask() {
        return tvDvdTitleMask.getString();
    }

    public void setTvDvdTitleMask(String tvDvdTitleMask) {
        this.tvDvdTitleMask.set(tvDvdTitleMask);
    }

    public boolean isRewriteTitle() {
        return rewriteTitle.getBoolean();
    }

    public void setRewriteTitle(boolean rewriteTitle) {
        this.rewriteTitle.set(rewriteTitle);
    }

    public String getRewriteTitleRegex() {
        return rewriteTitleRegex.getString();
    }

    public void setRewriteTitleRegex(String rewriteTitleRegex) {
        this.rewriteTitleRegex.set(rewriteTitleRegex);
    }
}

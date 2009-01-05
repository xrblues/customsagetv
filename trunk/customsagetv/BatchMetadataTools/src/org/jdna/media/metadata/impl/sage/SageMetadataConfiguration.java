package org.jdna.media.metadata.impl.sage;

import org.jdna.persistence.annotations.Field;
import org.jdna.persistence.annotations.Table;

@Table(name = "sageMetadata", requiresKey = false, description = "Configuration for the SageTV Metadata Persistence")
public class SageMetadataConfiguration {
    @Field(description = "How each Actor will be written.  This mask will be applied to each actor, and then appeneded into a single line for the properties file. {0} - Actor Name, {1} - Actor Role")
    private String actorMask        = "{0} -- {1};\n";

    @Field(description = "Description Mask (note ${PROP_FIELD_NAME} field names are looked up in the property file)")
    private String descriptionMask  = "${Description}\nUser Rating: ${_userRating}\n";

    @Field(description = "Title to use for multi volume vidoes (_disc is disc # 1,2,3,etc)")
    private String multiCDTitleMask = "${Title} Disc ${_disc}";

    @Field(description = "Title to use for single volume vidoes")
    private String titleMask        = "${Title}";

    @Field(description = "Number genre levels to write.  -1 means all levels.")
    private int    genreLevels      = 1;

    @Field(description = "Scale the poster to the specified Max width.  -1 no scaling.")
    private int posterScalingWidth=-1;
    
    @Field(description = "Set to true if you do not want to download backdrops.")
    private boolean ignoreBackdrop=false;
    
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

    public int getPosterScalingWidth() {
        return posterScalingWidth;
    }

    public void setPosterScalingWidth(int posterScalingWidth) {
        this.posterScalingWidth = posterScalingWidth;
    }

    public boolean isIgnoreBackdrop() {
        return ignoreBackdrop;
    }

    public void setIgnoreBackdrop(boolean ignoreBackdrop) {
        this.ignoreBackdrop = ignoreBackdrop;
    }
}

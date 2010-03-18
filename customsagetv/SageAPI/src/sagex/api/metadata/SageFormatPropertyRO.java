package sagex.api.metadata;

/**
 * <pre>
 * 7. Added support for the following properties which are automatically determined by the core if not defined when using the GetMediaFileMetadata API call: 
 * Format.Video.Height, 
 * Format.Video.Width, 
 * Format.Video.FPS, 
 * Format.Video.Interlaced, 
 * Format.Video.Progressive, 
 * Format.Audio.NumStreams, 
 * Format.Audio.[StreamNum].XXX (where StreamNum < NumStreams and XXX is one of the Format.Audio.XXX properties), 
 * Format.Subtitle.NumStreams, 
 * Format.Subtitle.[StreamNum].XXX (where StreamNum < NumStreams and XXX is one of the Format.Subtitle.XXX properties), 
 * Format.Subtitle.Codec and Format.Container.
 * 17. Added support for the following metadata values (case-insensitive) when using GetMediaFileMetadata if the values are not defined by an import plugin: 
 * Format.Video.Codec, 
 * Format.Video.Resolution, 
 * Format.Video.Aspect, 
 * Format.Audio.Codec, 
 * Format.Audio.Channels, 
 * Format.Audio.Language, 
 * Format.Audio.SampleRate, 
 * Format.Audio.BitsPerSample, 
 * Format.Audio.Bitrate (in kbps), 
 * Format.Subtitle.Language, 
 * Format.Video.Bitrate (in Mbps)
 * </pre>
 * @author seans
 *
 */
public enum SageFormatPropertyRO implements ISageProperty {
    FormatVideoHeight("Format.Video.Height"), 
    FormatVideoWidth("Format.Video.Width"), 
    FormatVideoFPS("Format.Video.FPS"), 
    FormatVideoInterlaced("Format.Video.Interlaced"), 
    FormatVideoProgressive("Format.Video.Progressive"), 
    FormatAudioNumStreams("Format.Audio.NumStreams"), 
    // (where StreamNum < NumStreams and XXX is one of the Format.Audio.XXX properties)
    FormatAudioStreamNumProperty("Format.Audio.[{0}].{1}"), 
    FormatSubtitleNumStreams("Format.Subtitle.NumStreams"), 
    //  (where StreamNum < NumStreams and XXX is one of the Format.Subtitle.XXX properties),
    FormatSubtitleStreamNumPropery("Format.Subtitle.[{0}].{1}"), 
    FormatSubtitleCodec("Format.Subtitle.Codec"),
    FormatContainer("Format.Container"),
    FormatVideoCodec("Format.Video.Codec"), 
    FormatVideoResolution("Format.Video.Resolution"), 
    FormatVideoAspect("Format.Video.Aspect"), 
    FormatAudioCodec("Format.Audio.Codec"), 
    FormatAudioChannels("Format.Audio.Channels"), 
    FormatAudioLanguage("Format.Audio.Language"), 
    FormatAudioSampleRate("Format.Audio.SampleRate"), 
    FormatAudioBitsPerSample("Format.Audio.BitsPerSample"), 
    FormatAudioBitrate("Format.Audio.Bitrate"), // (in kbps), 
    FormatSubtitleLanguage("Format.Subtitle.Language"), 
    FormatVideoBitrate("Format.Video.Bitrate") // (in Mbps)
    ;

    private String value;
    private ISageProperty.Type type;
    
    
    SageFormatPropertyRO() {
        this.value = name();
        this.type=Type.StringList;
    }

    SageFormatPropertyRO(Type type) {
        this.value = name();
        this.type=type;
    }

    SageFormatPropertyRO(String name) {
        this.value = name;
        this.type=Type.StringList;
    }

    SageFormatPropertyRO(String value, Type type) {
        this.value = value;
        this.type = type;
    }

    public Type type() {
        return type;
    }

    public String key() {
        return value;
    }
}

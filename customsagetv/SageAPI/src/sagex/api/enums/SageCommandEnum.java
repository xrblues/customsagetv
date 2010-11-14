package sagex.api.enums;

/**
 * Unofficial SageTV Generated File - Never Edit
 * Generated Date/Time: 14/11/10 6:25 PM
 * See Official Sage Documentation at <a href='http://download.sage.tv/api/sage/api/SageCommandEnum.html'>SageCommandEnum</a>
 * This Generated API is not Affiliated with SageTV.  It is user contributed.
 */
public enum SageCommandEnum {
Left("Left"),
Right("Right"),
Up("Up"),
Down("Down"),
Pause("Pause"),
Play("Play"),
SkipFwdOrPageRight("Skip Fwd/Page Right"),
SkipBkwdOrPageLeft("Skip Bkwd/Page Left"),
TimeScroll("Time Scroll"),
ChannelUpOrPageUp("Channel Up/Page Up"),
ChannelDownOrPageDown("Channel Down/Page Down"),
VolumeUp("Volume Up"),
VolumeDown("Volume Down"),
TV("TV"),
PlayFaster("Play Faster"),
PlaySlower("Play Slower"),
Guide("Guide"),
Power("Power"),
Select("Select"),
Watched("Watched"),
Favorite("Favorite"),
DontLike("Don't Like"),
Info("Info"),
Record("Record"),
Mute("Mute"),
FullScreen("Full Screen"),
Home("Home"),
Options("Options"),
Num0("Num 0"),
Num1("Num 1"),
Num2("Num 2"),
Num3("Num 3"),
Num4("Num 4"),
Num5("Num 5"),
Num6("Num 6"),
Num7("Num 7"),
Num8("Num 8"),
Num9("Num 9"),
Search("Search"),
Setup("Setup"),
Library("Library"),
PowerOn("Power On"),
PowerOff("Power Off"),
MuteOn("Mute On"),
MuteOff("Mute Off"),
AspectRatioFill("Aspect Ratio Fill"),
AspectRatio4x3("Aspect Ratio 4x3"),
AspectRatio16x9("Aspect Ratio 16x9"),
AspectRatioSource("Aspect Ratio Source"),
RightOrVolumeUp("Right/Volume Up"),
LeftOrVolumeDown("Left/Volume Down"),
UpOrChannelUp("Up/Channel Up"),
DownOrChannelDown("Down/Channel Down"),
PageUp("Page Up"),
PageDown("Page Down"),
PageRight("Page Right"),
PageLeft("Page Left"),
PlayOrPause("Play/Pause"),
PreviousChannel("Previous Channel"),
SkipFwd2("Skip Fwd #2"),
SkipBkwd2("Skip Bkwd #2"),
LiveTV("Live TV"),
DVDReversePlay("DVD Reverse Play"),
DVDNextChapter("DVD Next Chapter"),
DVDPrevChapter("DVD Prev Chapter"),
DVDMenu("DVD Menu"),
DVDTitleMenu("DVD Title Menu"),
DVDReturn("DVD Return"),
DVDSubtitleChange("DVD Subtitle Change"),
DVDSubtitleToggle("DVD Subtitle Toggle"),
DVDAudioChange("DVD Audio Change"),
DVDAngleChange("DVD Angle Change"),
DVD("DVD"),
Back("Back"),
Forward("Forward"),
Customize("Customize"),
Custom1("Custom1"),
Custom2("Custom2"),
Custom3("Custom3"),
Custom4("Custom4"),
Custom5("Custom5"),
Delete("Delete"),
MusicJukebox("Music Jukebox"),
RecordingSchedule("Recording Schedule"),
SageTVRecordings("SageTV Recordings"),
PictureLibrary("Picture Library"),
VideoLibrary("Video Library"),
Stop("Stop"),
Eject("Eject"),
StopOrEject("Stop/Eject"),
Input("Input"),
SmoothFF("Smooth FF"),
SmoothRew("Smooth Rew"),
Dash("-"),
AspectRatioToggle("Aspect Ratio Toggle"),
FullScreenOn("Full Screen On"),
FullScreenOff("Full Screen Off"),
RightOrSkipFwd("Right/Skip Fwd"),
LeftOrSkipBkwd("Left/Skip Bkwd"),
UpOrVolumeUp("Up/Volume Up"),
DownOrVolumeDown("Down/Volume Down"),
Online("Online"),
VideoOutput("Video Output"),
ScrollLeft("Scroll Left"),
ScrollRight("Scroll Right"),
ScrollUp("Scroll Up"),
ScrollDown("Scroll Down");


private String command;
private SageCommandEnum(String cmd) {
this.command =cmd;
}
public String command() { return command; }

}

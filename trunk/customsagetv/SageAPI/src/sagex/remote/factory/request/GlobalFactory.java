package sagex.remote.factory.request;

/**
 * Unofficial SageTV Generated File - Never Edit
 * Generated Date/Time: 1/1/10 10:04 AM
 * See Official Sage Documentation at <a href='http://download.sage.tv/api/sage/api/GlobalFactory.html'>GlobalFactory</a>
 * This Generated API is not Affiliated with SageTV.  It is user contributed.
 */

import java.util.Map;
import sagex.remote.RemoteRequest;
import sagex.remote.xmlrpc.RequestHelper;

public class GlobalFactory {
   public static RemoteRequest createRequest(String context, String command, String[] parameters) {
   if (command.equals("Refresh")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"Refresh",parameters,null);
   }
   if (command.equals("RefreshArea")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"RefreshArea",parameters,java.lang.String.class);
   }
   if (command.equals("RefreshAreaForVariable")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"RefreshAreaForVariable",parameters,java.lang.String.class,java.lang.Object.class);
   }
   if (command.equals("Repaint")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"Repaint",parameters,null);
   }
   if (command.equals("RepaintArea")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"RepaintArea",parameters,java.lang.String.class);
   }
   if (command.equals("AddStaticContext")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"AddStaticContext",parameters,java.lang.String.class,java.lang.Object.class);
   }
   if (command.equals("AddGlobalContext")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"AddGlobalContext",parameters,java.lang.String.class,java.lang.Object.class);
   }
   if (command.equals("AreThereUnresolvedConflicts")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"AreThereUnresolvedConflicts",parameters,null);
   }
   if (command.equals("IsAsleep")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"IsAsleep",parameters,null);
   }
   if (command.equals("GetTotalDiskspaceAvailable")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetTotalDiskspaceAvailable",parameters,null);
   }
   if (command.equals("GetTotalLibraryDuration")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetTotalLibraryDuration",parameters,null);
   }
   if (command.equals("GetTotalVideoDuration")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetTotalVideoDuration",parameters,null);
   }
   if (command.equals("GetUsedLibraryDiskspace")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetUsedLibraryDiskspace",parameters,null);
   }
   if (command.equals("GetUsedVideoDiskspace")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetUsedVideoDiskspace",parameters,null);
   }
   if (command.equals("AreAiringsSameShow")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"AreAiringsSameShow",parameters,Object.class,Object.class);
   }
   if (command.equals("GetLastEPGDownloadTime")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetLastEPGDownloadTime",parameters,null);
   }
   if (command.equals("GetLogo")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetLogo",parameters,java.lang.String.class);
   }
   if (command.equals("GetTimeUntilNextEPGDownload")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetTimeUntilNextEPGDownload",parameters,null);
   }
   if (command.equals("GetAllLineups")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetAllLineups",parameters,null);
   }
   if (command.equals("IsChannelDownloadComplete")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"IsChannelDownloadComplete",parameters,java.lang.String.class);
   }
   if (command.equals("GetLocalMarketsFromEPGServer")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetLocalMarketsFromEPGServer",parameters,null);
   }
   if (command.equals("GetLineupsForZipCodeFromEPGServer")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetLineupsForZipCodeFromEPGServer",parameters,java.lang.String.class);
   }
   if (command.equals("GetCurrentlyRecordingMediaFiles")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetCurrentlyRecordingMediaFiles",parameters,null);
   }
   if (command.equals("GetSuggestedIntelligentRecordings")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetSuggestedIntelligentRecordings",parameters,null);
   }
   if (command.equals("GetScheduledRecordings")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetScheduledRecordings",parameters,null);
   }
   if (command.equals("GetScheduledRecordingsForDevice")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetScheduledRecordingsForDevice",parameters,java.lang.String.class);
   }
   if (command.equals("GetScheduledRecordingsForTime")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetScheduledRecordingsForTime",parameters,long.class,long.class);
   }
   if (command.equals("GetScheduledRecordingsForDeviceForTime")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetScheduledRecordingsForDeviceForTime",parameters,java.lang.String.class,long.class,long.class);
   }
   if (command.equals("GetRecentlyWatched")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetRecentlyWatched",parameters,long.class);
   }
   if (command.equals("RunLibraryImportScan")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"RunLibraryImportScan",parameters,boolean.class);
   }
   if (command.equals("Exit")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"Exit",parameters,null);
   }
   if (command.equals("SageCommand")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"SageCommand",parameters,java.lang.String.class);
   }
   if (command.equals("RemoveUnusedLineups")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"RemoveUnusedLineups",parameters,null);
   }
   if (command.equals("GetApplicationLaunchTime")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetApplicationLaunchTime",parameters,null);
   }
   if (command.equals("GetFocusContext")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetFocusContext",parameters,null);
   }
   if (command.equals("Fork")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"Fork",parameters,null);
   }
   if (command.equals("TransmitCommandUsingInfraredTuningPlugin")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"TransmitCommandUsingInfraredTuningPlugin",parameters,java.lang.String.class,int.class,java.lang.String.class,java.lang.String.class,int.class);
   }
   if (command.equals("DebugLog")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"DebugLog",parameters,java.lang.String.class);
   }
   if (command.equals("CloseOptionsMenu")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"CloseOptionsMenu",parameters,null);
   }
   if (command.equals("GetSageCommandNames")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetSageCommandNames",parameters,null);
   }
   if (command.equals("ApplyServiceLevelToLineup")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"ApplyServiceLevelToLineup",parameters,java.lang.String.class,int.class);
   }
   if (command.equals("SetFocusForVariable")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"SetFocusForVariable",parameters,java.lang.String.class,java.lang.Object.class);
   }
   if (command.equals("EnsureVisibilityForVariable")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"EnsureVisibilityForVariable",parameters,java.lang.String.class,java.lang.Object.class,int.class);
   }
   if (command.equals("PassiveListen")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"PassiveListen",parameters,null);
   }
   if (command.equals("GetAiringsThatWontBeRecorded")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetAiringsThatWontBeRecorded",parameters,boolean.class);
   }
   if (command.equals("IsClient")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"IsClient",parameters,null);
   }
   if (command.equals("IsRemoteUI")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"IsRemoteUI",parameters,null);
   }
   if (command.equals("IsDesktopUI")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"IsDesktopUI",parameters,null);
   }
   if (command.equals("IsServerUI")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"IsServerUI",parameters,null);
   }
   if (command.equals("GetConnectedClients")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetConnectedClients",parameters,null);
   }
   if (command.equals("GetUIContextNames")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetUIContextNames",parameters,null);
   }
   if (command.equals("GetUIContextName")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetUIContextName",parameters,null);
   }
   if (command.equals("GetRemoteClientVersion")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetRemoteClientVersion",parameters,null);
   }
   if (command.equals("GetRemoteUIType")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetRemoteUIType",parameters,null);
   }
   if (command.equals("CreateTimedRecording")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"CreateTimedRecording",parameters,Object.class,long.class,long.class,java.lang.String.class);
   }
   if (command.equals("IsFullScreen")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"IsFullScreen",parameters,null);
   }
   if (command.equals("SetFullScreen")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"SetFullScreen",parameters,boolean.class);
   }
   if (command.equals("GetServerAddress")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetServerAddress",parameters,null);
   }
   if (command.equals("GetOS")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetOS",parameters,null);
   }
   if (command.equals("IsWindowsOS")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"IsWindowsOS",parameters,null);
   }
   if (command.equals("IsLinuxOS")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"IsLinuxOS",parameters,null);
   }
   if (command.equals("IsMacOS")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"IsMacOS",parameters,null);
   }
   if (command.equals("DVDBurnTheBurnList")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"DVDBurnTheBurnList",parameters,Object.class);
   }
   if (command.equals("DVDCancelBurn")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"DVDCancelBurn",parameters,null);
   }
   if (command.equals("DVDGetCurrentBurnStatus")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"DVDGetCurrentBurnStatus",parameters,null);
   }
   if (command.equals("CDBurnTheBurnList")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"CDBurnTheBurnList",parameters,Object.class);
   }
   if (command.equals("CDCancelBurn")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"CDCancelBurn",parameters,null);
   }
   if (command.equals("CDGetCurrentBurnStatus")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"CDGetCurrentBurnStatus",parameters,null);
   }
   if (command.equals("CDRipToLibrary")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"CDRipToLibrary",parameters,java.io.File.class,java.lang.String.class);
   }
   if (command.equals("CDCancelRip")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"CDCancelRip",parameters,null);
   }
   if (command.equals("CDGetCurrentRipStatus")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"CDGetCurrentRipStatus",parameters,null);
   }
   if (command.equals("StartFileCopy")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"StartFileCopy",parameters,java.lang.String.class,java.lang.String.class,java.io.File.class);
   }
   if (command.equals("CancelFileCopy")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"CancelFileCopy",parameters,null);
   }
   if (command.equals("GetFileCopyStatus")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetFileCopyStatus",parameters,null);
   }
   if (command.equals("StartFileDownload")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"StartFileDownload",parameters,java.lang.String.class,java.lang.String.class,java.io.File.class);
   }
   if (command.equals("StartCircularFileDownload")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"StartCircularFileDownload",parameters,java.lang.String.class,java.lang.String.class,java.io.File.class);
   }
   if (command.equals("CancelFileDownload")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"CancelFileDownload",parameters,null);
   }
   if (command.equals("GetFileDownloadStatus")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetFileDownloadStatus",parameters,null);
   }
   if (command.equals("GetFileDownloadStreamTime")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetFileDownloadStreamTime",parameters,null);
   }
   if (command.equals("IsFileDownloadProgressivePlay")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"IsFileDownloadProgressivePlay",parameters,null);
   }
   if (command.equals("SetRemoteEventEncryptionEnabled")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"SetRemoteEventEncryptionEnabled",parameters,boolean.class);
   }
   if (command.equals("CachePlaceshifterLogin")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"CachePlaceshifterLogin",parameters,null);
   }
   if (command.equals("CanCachePlaceshifterLogin")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"CanCachePlaceshifterLogin",parameters,null);
   }
   if (command.equals("ReloadSystemHooks")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"ReloadSystemHooks",parameters,null);
   }
   if (command.equals("UpdateLocatorServer")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"UpdateLocatorServer",parameters,null);
   }
   if (command.equals("GetFullUIWidth")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetFullUIWidth",parameters,null);
   }
   if (command.equals("GetFullUIHeight")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetFullUIHeight",parameters,null);
   }
   if (command.equals("GetDisplayResolutionWidth")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetDisplayResolutionWidth",parameters,null);
   }
   if (command.equals("GetDisplayResolutionHeight")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetDisplayResolutionHeight",parameters,null);
   }
   if (command.equals("GetDisplayResolution")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetDisplayResolution",parameters,null);
   }
   if (command.equals("GetDisplayResolutionOptions")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetDisplayResolutionOptions",parameters,null);
   }
   if (command.equals("GetPreferredDisplayResolutions")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetPreferredDisplayResolutions",parameters,null);
   }
   if (command.equals("SetDisplayResolution")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"SetDisplayResolution",parameters,java.lang.String.class);
   }
   if (command.equals("DiscoverSageTVServers")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"DiscoverSageTVServers",parameters,long.class);
   }
   if (command.equals("GetEmbeddedPanel")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetEmbeddedPanel",parameters,null);
   }
   if (command.equals("SetEmbeddedPanelBounds")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"SetEmbeddedPanelBounds",parameters,float.class,float.class,float.class,float.class);
   }
   if (command.equals("GetAvailableUpdate")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetAvailableUpdate",parameters,null);
   }
   if (command.equals("DeployAvailableUpdate")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"DeployAvailableUpdate",parameters,java.lang.String.class);
   }
   if (command.equals("GetLineupsForTvtv")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetLineupsForTvtv",parameters,java.lang.String.class);
   }
   if (command.equals("TvtvGetHost")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"TvtvGetHost",parameters,null);
   }
   if (command.equals("TvtvGetHostList")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"TvtvGetHostList",parameters,null);
   }
   if (command.equals("TvtvCreateNewAccount")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"TvtvCreateNewAccount",parameters,null);
   }
   if (command.equals("TvtvGetUser")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"TvtvGetUser",parameters,null);
   }
   if (command.equals("TvtvConfigureUser")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"TvtvConfigureUser",parameters,java.lang.String.class,java.lang.String.class,java.lang.String.class);
   }
   if (command.equals("TvtvGetAccountStatus")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"TvtvGetAccountStatus",parameters,null);
   }
   if (command.equals("TvtvGetAccountStatusMessage")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"TvtvGetAccountStatusMessage",parameters,null);
   }
   if (command.equals("TvtvMessage")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"TvtvMessage",parameters,java.lang.String.class);
   }
   if (command.equals("TvtvLogoLink")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"TvtvLogoLink",parameters,null);
   }
   if (command.equals("TvtvCreateSpecificAccount")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"TvtvCreateSpecificAccount",parameters,null);
   }
   if (command.equals("TvtvActivateTrial")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"TvtvActivateTrial",parameters,null);
   }
   if (command.equals("TvtvDownloadStations")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"TvtvDownloadStations",parameters,java.lang.String.class);
   }
   if (command.equals("TvtvConfigureInput")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"TvtvConfigureInput",parameters,java.lang.String.class,java.lang.String.class,java.lang.String.class);
   }
   if (command.equals("GetHotplugStorageMap")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetHotplugStorageMap",parameters,null);
   }
   if (command.equals("IsDoingLibraryImportScan")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"IsDoingLibraryImportScan",parameters,null);
   }
   if (command.equals("GetFailedNetworkMounts")) {
      return sagex.remote.xmlrpc.RequestHelper.createRequest(context,"GetFailedNetworkMounts",parameters,null);
   }
   throw new RuntimeException("Invalid GlobalFactory Command: "+command);
   }


}

println("Script Name: " + SCRIPT_NAME);
if (SCRIPT_ARGS || SCRIPT_ARGS.length>0) {
	for (i in SCRIPT_ARGS) {
		println("Arg: " + SCRIPT_ARGS[i]);
	}
}

println("");
Global = Packages.sagex.api.Global;
println("Sage Server Remote OS: " + Global.GetOS());

println("");
println("Listing Connected Clients...");
clients = Global.GetConnectedClients();
if (clients.length==0) {
	println("No Clients connected");
} else {
	for (client in clients) {
		println("Client: " + client);
	}
}

println("");
println("Listing Recorded Shows that are not watched...");
MediaFileAPI = Packages.sagex.api.MediaFileAPI;
AiringAPI = Packages.sagex.api.AiringAPI;
ShowAPI = Packages.sagex.api.ShowAPI;
files = MediaFileAPI.GetMediaFiles("T");
if (files) {
	for (i in files) {
		mf = files[i];
		if (!AiringAPI.IsWatched(mf)) {
			airing = MediaFileAPI.GetMediaFileAiring(mf);
			show = AiringAPI.GetShow(airing);
			println("       Show: " + MediaFileAPI.GetMediaTitle(mf));
			println("    Details: "  + ShowAPI.GetShowEpisode(show));
			println("");
		}
	}
}


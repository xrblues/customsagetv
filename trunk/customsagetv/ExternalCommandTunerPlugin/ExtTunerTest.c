/**
 * Copyright 2008 - Sean Stuckless
 */

#include <stdio.h>
#include <stdarg.h>
#include <unistd.h>
#include <stdlib.h>
#include <malloc.h>
#include "ExtTunerPlugin.h"

int main (int argc, char **argv) {
	printf("Loading Remotes....\n");
	remote *rems = LoadRemotes("testing 123");

	printf("Dumping Remotes...\n");
	DumpRemotes(rems);
	char *cmd = "215";
	printf("Playing Command: %s ...\n", cmd);
	PlayCommand(0, rems, cmd, 1);

	if(CanMacroTune()) {
		MacroTune(0, 567);
	}

	printf("Freeing Remotes....\n");
	FreeRemotes(&rems);

	printf("Done.\n");

	return 0;
}

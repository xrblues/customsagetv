/**
 * Simple External Command Tuner Plugin for SageTV
 *
 *
 */

#include <stdio.h>
#include <stdarg.h>
#include <unistd.h>
#include <stdlib.h>
#include <malloc.h>
#include <string.h>
#include "ExtTunerPlugin.h"

#ifndef DEVICE_NAME
#define DEVICE_NAME "External Command Tuner"
#endif

#ifndef LOG_FILE
#define LOG_FILE "ext-command-tuner.log"
#endif

#ifndef CONFIG_FILE
#define CONFIG_FILE "ext-command-tuner-remotes.cfg"
#endif

// used by the macro tune to hold the command of the last loaded remote
static char loadedDevName[256];
static int canMacroTune = 0;
static char macroTuneSepChar=NULL;

/**
 * Simply non-efficient logger.
 */
void _log(char *pref, char *fmt, ...) {
	va_list ap;
	FILE *f1;
	f1 = fopen(LOG_FILE, "a");
	va_start(ap, fmt);
	fprintf(f1, "%s: ", pref);
	vfprintf(f1, fmt, ap);
	fprintf(f1,"\n");
	va_end(ap);
	fflush(f1);
	fclose(f1);
}

#define INFO(s) _log("INFO", s)
#define INFO1(f,a) _log("INFO", f,a)
#define INFO2(f,a,b) _log("INFO", f,a,b)
#define INFO3(f,a,b,c) _log("INFO", f,a,b,c)
#define ERROR(s) _log("ERROR", s)
#define ERROR1(f,a) _log("ERROR", f,a)
#define ERROR2(f,a,b) _log("ERROR", f,a,b)

/**
 * simple method to allocate and copy string
 */
char *alloc_string(char *str) {
	if (str==NULL) return NULL;
	char *newStr = (char *)malloc(strlen(str)+1);
	strcpy(newStr, str);
	return newStr;
}

command* CreateCommand(unsigned char *Name) {
	struct command *Com; //pointer to new command structure

	Com = (struct command*)malloc(sizeof(struct command));//allocate space for a command structure
	if (Com == NULL) {
		return Com;
	}
	Com->name = Name; //copy values
	Com->next = NULL;
	Com->pattern = NULL;
	return Com; //return pointer to new command structure
}

int NeedBitrate() {
	return 0;
}
int NeedCarrierFrequency() {
	return 0;
}

const char* DeviceName() {
	return DEVICE_NAME;
}

int OpenDevice(int ComPort) {
	INFO1("OpenDevice %d", ComPort);
	return 1;
}

void CloseDevice(int devHandle) {
	INFO1("CloseDevice %d", devHandle);
}

unsigned long FindBitRate(int devHandle) {
	return 0;
}

unsigned long FindCarrierFrequency(int devHandle) {
	return 0;
}

void AddRemote(struct remote *Remote, struct remote **head) {
	struct remote *Temp; //Local remote structure

	if (!(*head)) { //if there are no structures in the list
		*head = Remote; //then assign this one to head.
		(*head)->next = NULL;
	} else //otherwise, add to end of list
	{
		Temp = *head;
		while (Temp->next) {
			Temp = Temp->next; //find the last structure in list
		}
		Temp->next=Remote; //assign the next field to the new structure
		Temp->next->next = NULL; //assign the next field of the new structure to NULL
	}
}

void AddCommand(struct command *Command, struct command **Command_List) {
	struct command *Temp; //temporary command structure pointer

	if (!(*Command_List)) { //if no commands in list, assign Command_List
		(*Command_List) = Command; //to the command structure
		(*Command_List)->next = NULL;
	} else {
		Temp = (*Command_List); //ELSE add to end of list of commands
		while (Temp->next) {
			Temp = Temp->next;
		}
		Temp->next=Command;
		Temp->next->next = NULL;
	}
}


void chomp (char* s) {
  int end = strlen(s) - 1;
  if (end >= 0 && s[end] == '\n')
    s[end] = '\0';
}

int LoadRemoteKeys(char *keyFile, struct remote *Remote) {
	char buf[1024];
	char key[40];

	INFO2("Loading Keys %s for Remote %s", keyFile, Remote->name);

	FILE *f1 = fopen(keyFile, "r");
	if (f1==NULL) {
		ERROR1("Failed to open remotes key file %s", keyFile);
		return 1;
	}
	command *head= NULL;
	command *newCmd;

	while (fgets(buf, 1024, f1)) {
		if (buf==NULL) break;
		chomp(buf);

		// ignore comments and empty lines
		if (buf[0]=='#' || strlen(buf)==0) continue;
		sscanf(buf,"%s",key);
		newCmd = CreateCommand(alloc_string(key));
		if (head==NULL) {
			head=Remote->command;
		}

		AddCommand(newCmd, &head);
	}

	fclose(f1);

	return 0;
}

remote* LoadRemotes(const char* pszPathName) {
	char buf[1024];

	if (pszPathName==NULL) {
		INFO("LoadRemotes Called");
	} else {
		INFO1("LoadRemote(%s)", pszPathName);
	}



	remote *head= NULL;
	INFO1("Loading Remotes from %s", CONFIG_FILE);

	FILE *f1 = fopen(CONFIG_FILE, "r");
	if (f1==NULL) {
		ERROR1("Failed to open remotes config file %s", CONFIG_FILE);
		return NULL;
	}

	char *name;
	char *cmd;
	char *remoteFile;

	remote *newRemote;
	command *newCmd;
	while (fgets(buf, 1024, f1)) {
		if (buf==NULL) break;

		// eat the newline
		chomp(buf);

		// ignore comments and empty lines
		if (buf[0]==' ' || buf[0]=='#' || buf[0]=='\t' || strlen(buf)==0) continue;

		// Remote name
		name=buf;

		// process set commands...
		if (strncasecmp(name,"set ",4)==0) {
			INFO1("Processing Set Command: %s", name);
			char setCmd[100], setValue[500];
			if (sscanf(name, "set %s %s",setCmd, setValue)==2) {
				INFO2("Processing Set Command: %s: %s", setCmd, setValue);
				if (strcasecmp(setCmd, "MacroTune")==0 && strcasecmp(setValue,"true")==0) {
					INFO("MacroTune is Enabled.  Macro Tune can only work when a Single Remote is defined.");
					canMacroTune = 1;
				} else if (strcasecmp(setCmd, "MacroTuneSepChar")==0) {
					sscanf(setValue, "%d", &macroTuneSepChar);
					INFO1("Using MacroTuneSepChar: '%c'", macroTuneSepChar);
					canMacroTune = 1;
				}
			} else {
				ERROR1("Invalid Set Command: %s", name);
			}
			continue;
		}

		// Remote command
		cmd = strchr(buf, ',');

		// invalid line
		if (cmd==NULL) continue;

		*cmd = 0;
		cmd++;
		while (*cmd == ' ') cmd++;

		// check for the a remote key file
		remoteFile = strchr(cmd,',');
		if (remoteFile!=NULL) {
			*remoteFile=0;
			remoteFile++;
			while (*remoteFile == ' ') remoteFile++;
		}

		newRemote = CreateRemote(alloc_string(name));

		// store the command to execute in the remote structure's command list...
		// this is not what it's supposed to be used for... but that's the only
		// place I could store it without creating a separate storage area
		newCmd = CreateCommand(alloc_string(cmd));

		// used by macro tune to hold the last loaded command
		strcpy(loadedDevName, cmd);

		newRemote->command = newCmd;

		// load the remote keys
		if (remoteFile!=NULL) {
			LoadRemoteKeys(remoteFile, newRemote);
		}

		if (head==NULL) {
			head = newRemote;
		} else {
			AddRemote(newRemote, &head);
		}
	}

	fclose(f1);

	return head;
}


remote* CreateRemote(unsigned char *Name) {
	INFO1("Creating/Adding Remote: %s", Name);
	remote *Remote;

	Remote = (struct remote*)malloc(sizeof(struct remote)); //allocate space for a remote structure
	if (Remote == NULL) {
		return Remote;
	}
	Remote->name = Name; //copy values
	Remote->carrier_freq = 0;
	Remote->bit_time = 0;
	Remote->command = NULL;
	Remote->next = NULL;
	return Remote; //return pointer to remote structure

}

void InitDevice() {
	INFO("InitDevice Called");
}

command* RecordCommand(int devHandle, unsigned char *Name) {
	return 0;
}

void PlayCommand(int devHandle, remote *remote, unsigned char *name, int tx_repeats) {
	INFO3("PlayCommand Called: Remote: %s; Command: %s; Repeats: %d", remote->name, name, tx_repeats);
    char cmd[1024];
    sprintf(cmd,remote->command->name, name);
    INFO1("SysCommand: %s" , cmd);
    if (system(cmd)!=0) {
    	ERROR1("Failed to execute Tuner Command: %s", cmd);
    } else {
    	INFO1("Executed Tuner Command without Error: %s", cmd);
    }
}

void FreeRemotes(remote **head) {
	INFO("FreeRemotes Called.");
	command *Temp_Com; //temporary command pointer
	remote *Temp_Rem; //temporary remote pointer
	pattern *temp_pat; //temporary pattern pointer

	while (*head) {
		Temp_Rem = *head;
		Temp_Com = (*head)->command;
		while (Temp_Com) {
			(*head)->command = (*head)->command->next;
			temp_pat = Temp_Com->pattern;
			while (temp_pat) {
				Temp_Com->pattern = Temp_Com->pattern->next;
				free(temp_pat->bytes);
				free(temp_pat);
				temp_pat = Temp_Com->pattern;
			}
			free(Temp_Com->name); //free command list
			free(Temp_Com);
			Temp_Com = (*head)->command;
		}
		(*head) = (*head)->next;
		free(Temp_Rem->name); //free remote data
		free(Temp_Rem);
	}
	*head = NULL;
}

void DumpRemotes(remote *head) {
	command *chead = head->command;
	while (head) {
		printf("RemoteName: %s\n", head->name);
		printf(" RemoteCmd: %s\n", head->command->name);
		while (chead) {
			printf("CmdKey: %s\n", chead->name);
			chead=chead->next;
		}
		printf("\n");
		head = head->next;
	}
}

/* MacroTune provided by TravisTodd http://forums.sagetv.com/forums/member.php?u=3792 */
int CanMacroTune(void) {
	// TODO: Set using property
	INFO("CanMacroTune");
	return canMacroTune;
}
void MacroTune(int devHandle, int channel) {
	INFO2("MacroTune: Handle: %d, Channel %d",devHandle,channel);

	char cmd[1024];
	char    channelStr[10];  // if more than 10 digits, we have problems
	char    channelBuffer[21]; // buffer holds twice the digits, because of the sep char
	sprintf(channelStr, "%d", channel);
	if (macroTuneSepChar!=NULL) {
		int s = strlen(channelStr);
		int i=0;
		for (i=0;i<s;i++) {
			channelBuffer[i*2]=channelStr[i];
			channelBuffer[(i*2)+1] = macroTuneSepChar;
			channelBuffer[(i*2)+2] = NULL;
		}
	} else {
		strcpy(channelBuffer, channelStr);
	}
    sprintf(cmd,loadedDevName, channelBuffer);
    if (system(cmd)!=0) {
    	ERROR1("Failed to execute MacroTune Command: %s", cmd);
    } else {
    	INFO1("Executed MacroTune Command without Error: %s", cmd);
    }
}

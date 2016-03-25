This plugin is an irtunerplugin and is allows you to control your STB channel changes using an external program.

# Installation #
Download and unzip the contents to the SageTV server location.  The ExtTunerPlugin.so should automatically be extracted to the irtunerplugins/ dir.

A file, ext-command-tuner-remotes.cfg, will be placed in the root of the SageTV server.  This configuration file is where you configure the remote for your STB.

The configuration file can have more than one remote configured.  A configuraration entry for a remote has 3 fields, separated by a comma.  For example,
```
Remote 1, /usr/bin/irsend SEND_ONCE dish1 %s, dish-301-remote-keys.txt
```

Field1 is the name, ie, Remote 1, and it can be any name you want.

Field2 is the command to be executed to change the channel. %s will be replaced by the channel #.  If MacroTune is enabled, then the channel # will the complete channel #, otherwise it is simply the channel digit.

Field3 is an optional file that contains the keys for the remote.

# Macro Tune (all digits at once) #
SageTV can tune channels by issuing a single digit at a time, or by sending the complete channel at once.  When the complete channel is sent it is called MacroTune.  Depending on the script you are using to change channels, you may want your script to be called once for each channel digit, or you may want to pass the complete channel digits to the script at once.

You can enable MacroTune in the configuration by setting the following in the configuration file.
```
set MacroTune true
```

When MacroTune is enabled, the entire channel will be sent to your external command.  ie, if you tune to channel 543, then 543 will be passed to your script.  If you want the digits passed to your script as single digits, ie, 5 4 3, then you can set the following in the configuration.
```
set MacroTuneSepChar 32
```

The MacroTuneSepChar is an ascii value of the character that you want to use to separate the digits.  32 is the ascii value for a space.

So with MacroTune enabled, and a MacroTuneSepChar of 32, then your command will recieve the channel as 5 4 3, ie,
/usr/bin/irsend SEND\_ONCE dish1 5 4 3

# External Command and Lirc #
I use the external command tuner plugin with lirc to control a STB.  I use the external command tuner because it's easier for me control the commands that i send to the STB using irsend.  For example, my remote line look like this,
```
6141, /usr/bin/irsend --count=2 SEND_ONCE dish1 select %s select, dish-301-remote-keys.txt
```

My Remote is called 6141, because that's what I named it since my STB is a Bell 6141 receiver.

My command, using irsend, sets the --count=2 because without, sometimes the receiver fails to catch all digits.  --count=2 seems to be working.

I also pass a "select" before and after the complete channel #.  I do this so that if the receiver is in the screen saver mode, then the select will force it out of the mode.  Passing select after the channel # simply forces the reciever to take the channel change immediately.

# Sample Configuration #
```
# enable macro tuning with space separated digits
set MacroTune true
set MacroTuneSepChar 32

# Sage Remote Display Name, Command With Args (%s will be remote code)
6141, /usr/bin/irsend --count=2 SEND_ONCE dish1 select %s select, dish-301-remote-keys.txt
#6141, /usr/bin/irsend SEND_ONCE dish1 %s, dish-301-remote-keys.txt
```

# Sample Keys File #

The keys file contains a list of keys that the remote can recognize.  The 0x00000 code is not important, it's simply included here, because I created my keys file from a lirc remote file.  By defining a keys file for your remote, you will enable sage to test your remote, or enable sage to send a "key" before channel changes, etc.

```
          info                     0x0000000000000000
          power                    0x0000000000000800
          play                     0x0000000000000C10
          1                        0x0000000000001000
          2                        0x0000000000001400
          3                        0x0000000000001800
          frwd                     0x0000000000001C10
          4                        0x0000000000002000
          5                        0x0000000000002400
          6                        0x0000000000002800
          menu                     0x0000000000002C00
          7                        0x0000000000003000
          8                        0x0000000000003400
          9                        0x0000000000003800
          ffwd                     0x0000000000003C10
          select                   0x0000000000004000
          0                        0x0000000000004400
          cancel                   0x0000000000004800
          guide                    0x0000000000005000
          mute                     0x0000000000005401
          view                     0x0000000000005800
          tv_video                 0x0000000000005C00
          right                    0x0000000000006000
          vol+                     0x0000000000006401
          up                       0x0000000000006800
          recall                   0x0000000000006C00
          left                     0x0000000000007000
          vol-                     0x0000000000007401
          down                     0x0000000000007800
          rec                      0x0000000000007C00
          pause                    0x0000000000008000
          stop                     0x0000000000008400
          sys_info                 0x0000000000009000
          */ptv_list               0x0000000000009400
          #/search                 0x0000000000009800
          sat                      0x000000000000A400
          tv                       0x000000000000A801
          rew                      0x000000000000C410
          fwd                      0x000000000000C810
          skip_back                0x000000000000D810
          skip_fwd                 0x000000000000DC10
```
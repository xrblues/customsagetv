CC=gcc
CFLAGS = -c -fPIC -I/usr/local/j2sdk1.4.2_06/include/ -I/usr/local/j2sdk1.4.2_06/include/linux -D_FILE_OFFSET_BITS=64

all: ExtTunerPlugin.so ExtTunerTest

ExtTunerPlugin.so: ExtTunerPlugin.o
	$(CC) -o ExtTunerPlugin.so -shared ExtTunerPlugin.o

ExtTunerPlugin.o: ExtTunerPlugin.c
	$(CC) -Wall -O2 -c ExtTunerPlugin.c

ExtTunerTest.o: ExtTunerTest.c
	$(CC) -Wall -O2 -c ExtTunerTest.c

ExtTunerTest: ExtTunerPlugin.o ExtTunerTest.o
	$(CC) -o ExtTunerTest ExtTunerPlugin.o ExtTunerTest.o

clean:
	rm -f *.o *.so *.c~ *.h~ ExtTunerTest

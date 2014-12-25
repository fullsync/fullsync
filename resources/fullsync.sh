#!/bin/sh

REAL_FULLSYNC_LOC=`readlink -f "$0"`

REAL_FULLSYNC_DIR=`dirname "${REAL_FULLSYNC_LOC}"`

JAVA_BIN=`which java`

if [ "x$JAVA_BIN" = "x" ]; then
	MESSAGE="Java could not be found! Please install a Java runtime first!"
	echo "$MESSAGE"
	xmessage -center "$MESSAGE"
	exit 1
else
	exec "${JAVA_BIN}" ${JAVA_OPTS} -jar "${REAL_FULLSYNC_DIR}/launcher.jar" "$@"
	STATUS=$?
	echo "Error running FullSync, please check your Java / FullSync installation!"
	exit $STATUS
fi

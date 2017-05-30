#!/bin/sh

REAL_FULLSYNC_LOC=`readlink -n "$0"`

REAL_FULLSYNC_DIR=`dirname "${REAL_FULLSYNC_LOC}"`
FULLSYNC="${REAL_FULLSYNC_DIR}/lib/net.sourceforge.fullsync-fullsync-core.jar"

JAVA_BIN=`which java`

if [ "x$JAVA_BIN" = "x" ]; then
	MESSAGE="Java could not be found! Please install a Java runtime first!"
	echo "$MESSAGE"
	xmessage -center --text "$MESSAGE"
	exit 1
else
	cd "${REAL_FULLSYNC_DIR}"
	exec "${JAVA_BIN}" ${JAVA_OPTS} -XstartOnFirstThread -jar "${FULLSYNC}" "$@"
	STATUS=$?
	echo "Error running FullSync, please check your Java / FullSync installation!"
	exit $STATUS
fi

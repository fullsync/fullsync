#!/bin/sh

FULLSYNC_DIR=`dirname $0`

REAL_FULLSYNC_DIR=`readlink -f ${FULLSYNC_DIR}`

JAVA_BIN=`which java`

if [ "x$JAVA_BIN" = "x" ]; then
	MESSAGE="Java could not be found! Please install a Java runtime first!"
	echo "$MESSAGE"
	xmessage -center --text "$MESSAGE"
	exit 1
else
	cd "${REAL_FULLSYNC_DIR}"
	exec "${JAVA_BIN}" ${JAVA_OPTS} -jar "${REAL_FULLSYNC_DIR}/launcher.jar" "$@"
	STATUS=$?
	echo "Error running FullSync, please check your Java / FullSync installation!"
	exit $STATUS
fi

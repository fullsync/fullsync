#!/bin/sh

dirname=`dirname $0`

cd `readlink -f ${dirname}`

exec `which java` -jar ./launcher.jar "$@"

exit $?

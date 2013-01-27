#!/bin/bash
#
# cleanup a few things before checkin
#


for msg in source/net/sourceforge/fullsync/ui/messages*.properties;
do
	echo "removing comments and empty messages of $msg..."
	#sed -i "s/^#.*//" "$msg"
	cat "$msg" | grep -v "^#" | grep -v "[A-Za-z0-9\.]=$" > "$msg._tmp"
	mv -f "$msg._tmp" "$msg"
done

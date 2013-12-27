#!/bin/bash -ex

DIR=${1:-.}

unzip -p swt-*-gtk-linux-x86_64.zip swt.jar > $DIR/swt-gtk-linux-x86_64.jar
unzip -p swt-*-gtk-linux-x86_64.zip swt-debug.jar > $DIR/swt-gtk-linux-x86_64-debug.jar
unzip -p swt-*-gtk-linux-x86.zip swt.jar > $DIR/swt-gtk-linux-x86.jar

unzip -p swt-*-cocoa-macosx-x86_64.zip swt.jar > $DIR/swt-cocoa-macosx-x86_64.jar
unzip -p swt-*-cocoa-macosx.zip swt.jar > $DIR/swt-cocoa-macosx-x86.jar

unzip -p swt-*-win32-win32-x86_64.zip swt.jar > $DIR/swt-win32-win32-x86_64.jar
unzip -p swt-*-win32-win32-x86.zip swt.jar > $DIR/swt-win32-win32-x86.jar

echo done

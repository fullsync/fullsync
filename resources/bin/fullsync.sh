#! /bin/sh

PRG="$0"
progname=`basename "$0"`
saveddir=`pwd`
dirname_prg=`dirname "$PRG"`

while [ -h "$PRG" ] ; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
      PRG="$link"
  else
      PRG=`dirname "$PRG"`"/$link"
  fi
done

FULLSYNC_HOME=`dirname "$PRG"`/..
FULLSYNC_HOME=`cd "$FULLSYNC_HOME" && pwd`

echo "FULLSYNC_HOME = $FULLSYNC_HOME"

cd $FULLSYNC_HOME

if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD=$JAVA_HOME/jre/sh/java
    else
      JAVACMD=$JAVA_HOME/bin/java
    fi
  else
    JAVACMD=java
  fi
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly."
  echo "  We cannot execute $JAVACMD"
  exit
fi

if [ -n "$CLASSPATH" ] ; then
  LOCALCLASSPATH=$CLASSPATH
fi

# add in the dependency .jar files
DIRLIBS=$FULLSYNC_HOME/lib/*.jar
for i in ${DIRLIBS}
do
    # if the directory is empty, then it will return the input string
    # this is stupid, so case for it
    if [ "$i" != "${DIRLIBS}" ] ; then
      if [ -z "$LOCALCLASSPATH" ] ; then
        LOCALCLASSPATH=$i
      else
        LOCALCLASSPATH="$i":$LOCALCLASSPATH
      fi
    fi
done

if [ -n "$JAVA_HOME" ] ; then
  if [ -f "$JAVA_HOME/lib/tools.jar" ] ; then
    LOCALCLASSPATH=$LOCALCLASSPATH:$JAVA_HOME/lib/tools.jar
  fi

  if [ -f "$JAVA_HOME/lib/classes.zip" ] ; then
    LOCALCLASSPATH=$LOCALCLASSPATH:$JAVA_HOME/lib/classes.zip
  fi

else
  echo "Warning: JAVA_HOME environment variable is not set."
  echo "  If build fails because sun.* classes could not be found"
  echo "  you will need to set the JAVA_HOME environment variable"
  echo "  to the installation directory of java."
fi

$JAVACMD -classpath "$LOCALCLASSPATH" -Djava.library.path="$FULLSYNC_HOME/lib" net.sourceforge.fullsync.cli.Main "$@"

#!/bin/sh
#
# TransformIO.sh - Transformation Input / Output tool.
#
# Example: TransformIO.sh mode=GUI batch=egs/batch01/batch01.tio
#
# mode := GUI | TUI | CLI
# batch := [tio script]
#
# Modes: 
#		GUI = graphical user interface
#		TUI = textual user interface
#		CLI = command line interface
#

# Set the base directory to the location of this script.
BASEDIR="$( cd -P "$( dirname "$0" )" && pwd )"

#
# We detect the java executable to use according to the following algorithm:
#
# 1. If it is located in JAVA_HOME, 
# 2. And if it is execuatable then we use that;
# 3. Or, Use the java that is in the command path.
# 
if [ -d "${JAVA_HOME}" -a -x "${JAVA_HOME}/bin/java" ]; then
	JAVA="${JAVA_HOME}/bin/java"
else
	JAVA=java
fi

# Uncomment JDWP to use Java Debug Wire Protocol (JDWP) to communicate with a debugger.
#	JDWP="-Xdebug -Xrunjdwp:transport=dt_socket,address=1044,server=y,suspend=y"

# Uncomment for additional JVM debugging
#	JVMDBG="-verbose:class"

#
# Start TransformIO
#
cd ${BASEDIR}
${JAVA} -client -Xmx256m ${JDWP} ${JVMDBG} \
	-D%{JVM_CFG_LOG} \
	-Djava.ext.dirs=%{EXT_DIR} \
	-Djava.library.path=%{NAT_DIR} \
	-jar %{FINAL_NAME}.jar $*
# vi:set tabstop=2 hardtabs=2 shiftwidth=2:

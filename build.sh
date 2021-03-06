#!/bin/bash

LOG_FILE="build.log"

COMMON_DIR="Common"
SERVER_DIR="Server"
PARSER_DIR="Parser"

PARSER_BUILD_DIR="build-parser"
SERVER_BUILD_DIR="build-server"

TARGET="/target"
LIB_FILES="${TARGET}/lib/"
CLASS_FILES="${TARGET}/classes/"

PARSER_SCRIPT_NAME="run-parser.sh"
SERVER_SCRIPT_NAME="run-server.sh"

# DO NOT EDIT ANYTHING BELOW THIS LINE

_log() {
  printf '[%s] %s\n' $(date '+%d_%m_%Y-%H_%M_%S') "$*";
}

_debug() {
  if [ $# -eq 0 ]
  then
    while read -r data; do
        _log "${data}" | tee -a ${LOG_FILE}
    done
  else
    _log "$*" | tee -a ${LOG_FILE}
  fi
}

rm "${LOG_FILE}"
_debug "CLEANING UP OLD TARGET"
rm -rfv "${BUILD_DIR}" "${COMMON_DIR}${TARGET}" "${SERVER_DIR}${TARGET}" "${PARSER_DIR}${TARGET}" | _debug
_debug "INSTALLING PROJECT"
mvn install | _debug
_debug "COMPILING PROJECT"
mvn compile | _debug
_debug "CREATING BUILD DIRECTORY"
mkdir -v {$SERVER_BUILD_DIR,$PARSER_BUILD_DIR} | _debug
_debug "SETTING SERVER LAUNCHER"
cp --verbose "${COMMON_DIR}${LIB_FILES}"* "${SERVER_DIR}${LIB_FILES}"* "${SERVER_BUILD_DIR}" | _debug
cp --verbose -R "${COMMON_DIR}${CLASS_FILES}"* "${SERVER_DIR}${CLASS_FILES}"* "${SERVER_BUILD_DIR}" | _debug
CLASSPATH=$(find ~+/${SERVER_BUILD_DIR} -name '*.jar' | tr '\n' ':')
echo "java -classpath '$(pwd)/${SERVER_BUILD_DIR}:${CLASSPATH:0:-1}' com.hawolt.WebServer \"\$@\"" > ${SERVER_SCRIPT_NAME}
_debug "SETTING PARSER LAUNCHER"
cp --verbose "${COMMON_DIR}${LIB_FILES}"* "${PARSER_DIR}${LIB_FILES}"* "${SERVER_BUILD_DIR}" | _debug
cp --verbose -R "${COMMON_DIR}${CLASS_FILES}"* "${PARSER_DIR}${CLASS_FILES}"* "${PARSER_BUILD_DIR}" | _debug
CLASSPATH=$(find ~+/${SERVER_BUILD_DIR} -name '*.jar' | tr '\n' ':')
echo "java -classpath '$(pwd)/${PARSER_BUILD_DIR}:${CLASSPATH:0:-1}' com.hawolt.Bootstrap \"\$@\"" > ${PARSER_SCRIPT_NAME}
_debug "LAUNCHERS SETUP"
chmod +x {${PARSER_SCRIPT_NAME},${SERVER_SCRIPT_NAME}}
_debug "COMPLETE"
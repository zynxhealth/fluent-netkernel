#!/bin/bash
SCRIPT_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
FLUNK_PATH=$SCRIPT_HOME

MODULE_DIRECTORY=$1
FLUNK_CODE_FILE=$MODULE_DIRECTORY/module.flunk
FLUNK_TEST_FILE=$MODULE_DIRECTORY/tests.flunk

MODULE_XML_FILE=$MODULE_DIRECTORY/module.xml
TESTS_XML_FILE=$MODULE_DIRECTORY/etc/system/Tests.xml
TESTLIST_XML_FILE=$MODULE_DIRECTORY/resources/test/testlist.xml

if [[ -e $FLUNK_CODE_FILE ]]; then
	echo "Path: $FLUNK_PATH, File:$FLUNK_CODE_FILE, XmlFile:$MODULE_XML_FILE"
	java -jar $FLUNK_PATH/flunk.jar $FLUNK_CODE_FILE > $MODULE_XML_FILE
	exit
else
	echo "$FLUNK_CODE_FILE does not exist. Checking for $FLUNK_TEST_FILE..."	
fi

if [[ -e $FLUNK_TEST_FILE ]]; then
	java -jar $FLUNK_PATH/flunk.jar $FLUNK_TEST_FILE module > $MODULE_XML_FILE
	java -jar $FLUNK_PATH/flunk.jar $FLUNK_TEST_FILE testlist >  $TESTLIST_XML_FILE
	java -jar $FLUNK_PATH/flunk.jar $FLUNK_TEST_FILE testreferences > $TESTS_XML_FILE
else
	echo "$FLUNK_TEST_FILE does not exist. No flunk file found, exiting."
fi

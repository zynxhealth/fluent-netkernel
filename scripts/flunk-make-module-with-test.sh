#!/bin/bash
SCRIPT_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

show_usage() {
	echo ${0##/*}" Usage: flunk-make-module -d MODULE_PARENT_DIRECTORY"
	echo "-u MODULE_NAMESPACE_URI [-n MODULE_NAME] [-v MODULE_VERSION] [-h]"
	exit
}

MODULE_PARENT_DIRECTORY=""
MODULE_NAMESPACE_URI=""
MODULE_NAME=""
MODULE_VERSION=""
EXPOSE_TO_HTTP=false

while getopts d:u:n:v:h opt
do 
	case "$opt" in
		d) MODULE_PARENT_DIRECTORY=$OPTARG;;
		u) MODULE_NAMESPACE_URI=$OPTARG;;
		n) MODULE_NAME=$OPTARG;;
		v) MODULE_VERSION=$OPTARG;;
		h) EXPOSE_TO_HTTP=true;;
		[?]) show_usage;;
	esac
done

echo "------------"
echo "Creating module $MODULE_NAME"
echo "------------"

if [[ $EXPOSE_TO_HTTP ]]; then
	$SCRIPT_HOME/flunk-make-module.sh -d $MODULE_PARENT_DIRECTORY -u $MODULE_NAMESPACE_URI -n $MODULE_NAME -v $MODULE_VERSION -h
else
	$SCRIPT_HOME/flunk-make-module.sh -d $MODULE_PARENT_DIRECTORY -u $MODULE_NAMESPACE_URI -n $MODULE_NAME -v $MODULE_VERSION
fi

if [[ $? -ne 0 ]]; then
	echo "Error creating module, exiting..."
	exit
fi

echo "------------"
echo "Creating test module for $MODULE_NAME"
echo "------------"

$SCRIPT_HOME/flunk-make-test-module.sh -d $MODULE_PARENT_DIRECTORY -u $MODULE_NAMESPACE_URI -n $MODULE_NAME -v $MODULE_VERSION
#!/bin/bash
SCRIPT_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

show_usage() {
	echo ${0##/*}" Usage: flunk-make-test-module -d MODULE_PARENT_DIRECTORY"
	echo "-u MUT_NAMESPACE_URI [-n MUT_NAME] [-v MODULE_VERSION] [-h]"
	exit
}

MODULE_PARENT_DIRECTORY=""
MUT_NAMESPACE_URI=""
MUT_NAME=""
MODULE_VERSION=""

while getopts d:u:n:v:h opt
do 
	case "$opt" in
		d) MODULE_PARENT_DIRECTORY=$OPTARG;;
		u) MUT_NAMESPACE_URI=$OPTARG;;
		n) MUT_NAME=$OPTARG;;
		v) MODULE_VERSION=$OPTARG;;
		[?]) show_usage;;
	esac
done

echo "MODULE_PARENT_DIRECTORY: $MODULE_PARENT_DIRECTORY" 
echo "MUT_NAMESPACE_URI: $MUT_NAMESPACE_URI" 
echo "MUT_NAME: $MUT_NAME" 
echo "MODULE_VERSION: $MODULE_VERSION" 

if [[ ! -e $MODULE_PARENT_DIRECTORY || $MUT_NAMESPACE_URI = "" ]]; then
	show_usage
fi

MODULE_DIRECTORY_NAME=$(echo "$MUT_NAMESPACE_URI" | sed "s/:/./g").test
echo "MODULE_DIRECTORY_NAME: $MODULE_DIRECTORY_NAME"
MODULE_DIRECTORY_PATH=$MODULE_PARENT_DIRECTORY/$MODULE_DIRECTORY_NAME
echo "MODULE_DIRECTORY_PATH: $MODULE_DIRECTORY_PATH"

mkdir -p "$MODULE_DIRECTORY_PATH/resources"

mkdir -p "$MODULE_DIRECTORY_PATH/etc/system"

mkdir -p "$MODULE_DIRECTORY_PATH/resources/test"

MODULE_FLUNK_TEMPLATE="tests.flunk.template"

cat "$SCRIPT_HOME/templates/$MODULE_FLUNK_TEMPLATE" | \
sed -e "s/{{MUT_NAME}}/$MUT_NAME/g" \
    -e "s/{{MUT_NAMESPACE_URI}}/$MUT_NAMESPACE_URI/g" \
    -e "s/{{MODULE_VERSION}}/$MODULE_VERSION/g" > "$MODULE_DIRECTORY_PATH/tests.flunk" 
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

echo "MODULE_PARENT_DIRECTORY: $MODULE_PARENT_DIRECTORY" 
echo "MODULE_NAMESPACE_URI: $MODULE_NAMESPACE_URI" 
echo "MODULE_NAME: $MODULE_NAME" 
echo "MODULE_VERSION: $MODULE_VERSION" 
echo "EXPOSE_TO_HTTP: $EXPOSE_TO_HTTP" 

if [[ ! -e $MODULE_PARENT_DIRECTORY || $MODULE_NAMESPACE_URI = "" ]]; then
	show_usage
fi

MODULE_DIRECTORY_NAME=$(echo "$MODULE_NAMESPACE_URI" | sed "s/:/./g")
echo "MODULE_DIRECTORY_NAME: $MODULE_DIRECTORY_NAME"
MODULE_DIRECTORY_PATH=$MODULE_PARENT_DIRECTORY/$MODULE_DIRECTORY_NAME
echo "MODULE_DIRECTORY_PATH: $MODULE_DIRECTORY_PATH"

mkdir -p "$MODULE_DIRECTORY_PATH/resources"

MODULE_FLUNK_TEMPLATE="module.flunk.template"

if [[ $EXPOSE_TO_HTTP = true ]]; then
	mkdir -p "$MODULE_DIRECTORY_PATH/etc/system"
	echo "<connection><type>HTTPFulcrum</type></connection>" > "$MODULE_DIRECTORY_PATH/etc/system/SimpleDynamicImportHook.xml"

	MODULE_FLUNK_TEMPLATE="module.flunk.template.exposetohttp"
fi

cat "$SCRIPT_HOME/templates/$MODULE_FLUNK_TEMPLATE" | \
sed -e "s/{{MODULE_NAME}}/$MODULE_NAME/g" \
    -e "s/{{MODULE_NAMESPACE_URI}}/$MODULE_NAMESPACE_URI/g" \
    -e "s/{{MODULE_VERSION}}/$MODULE_VERSION/g" > "$MODULE_DIRECTORY_PATH/module.flunk" 

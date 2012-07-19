#!/bin/bash

show_usage() {
echo ${0##/*}" Usage:"
echo "  Generate and Commission a Netkernel Module using the flunk DSL "
echo "${0##/} -f MODULE_DEFINITION_FILE [-r MODULE_FOLDERS_DIRECTORY] [-m MODULES_DIRECTORY]"
echo "[-n NETKERNEL_ROOT_DIRECTORY]"
exit
}

MINARGS=1

case $1 in
	""|"-h"|"--help") show_usage ;;
esac

ARGC=$#

if [[ $ARGC -lt $MINARGS ]] ; then
 echo "Too few arguments given (Minimum:$MINARGS)"
 echo
 show_usage
fi

MODULE_FOLDERS_DIRECTORY=""
FLUNK_PATH="./out/artifacts/flunk_jar"
MODULES_DIR="/home/pair/dev/galen/platform/netkernel-modules"
NETKERNEL_ROOT_DIRECTORY=""

while getopts f:r:m:n: opt 
do 
	case "$opt" in
		f) MODULE_FILE=$OPTARG;;
		r) MODULE_FOLDERS_DIRECTORY=$OPTARG;;
		m) MODULES_DIR=$OPTARG;;
		n) NETKERNEL_ROOT_DIRECTORY=$OPTARG;;
		[?]) show_usage;;
	esac
done

echo "Generating module XML"
java -jar $FLUNK_PATH/Fluent-netkernel.jar $MODULE_FILE > module_temp

MODULE_NAME=$(grep -m 1 "<uri>.*</uri>" module_temp | sed 's/.*<uri>//' | sed 's/<\/uri>.*//' | sed 's/:/./g')

if [[ -d $MODULES_DIR/$MODULE_NAME ]]; then
	echo "Directory $MODULES_DIR/$MODULE_NAME already exists"
	echo "Overwriting existing module definition"
else
	mkdir $MODULES_DIR/$MODULE_NAME
fi	

mv module_temp $MODULES_DIR/$MODULE_NAME/module.xml
rm -f module_temp

if [[ -n $MODULE_FOLDERS_DIRECTORY ]]; then
	echo "Adding module folders to $MODULE_FOLDERS_DIRECTORY"

	cp -r $MODULE_FOLDERS_DIRECTORY/* $MODULES_DIR/$MODULE_NAME/
fi

if [[ -n $NETKERNEL_ROOT_DIRECTORY ]]; then
	echo "Adding ${MODULE_NAME}.xml to ${NETKERNEL_ROOT_DIRECTORY}/etc/modules.d"
	echo "<modules><module>file:${MODULES_DIR}/${MODULE_NAME}/</module></modules>" > ${NETKERNEL_ROOT_DIRECTORY}/etc/modules.d/${MODULE_NAME}.xml
else
	echo "Skipping deployment"
fi

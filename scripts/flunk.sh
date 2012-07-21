#!/bin/bash
SCRIPT_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

show_usage() {
echo ${0##/*}" Usage:"
echo "  Generate and Commission a Netkernel Module using the flunk DSL "
echo "${0##/} -s MODULE_DEFINITION_FILE [-f MODULE_FOLDERS_DIRECTORY] [-m MODULES_DIRECTORY]"
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

MODULE_FILE=""
MODULE_FOLDERS_DIRECTORY=""
NETKERNEL_ROOT_DIRECTORY=""

FLUNK_PATH=$SCRIPT_HOME
MODULES_DIR=$SCRIPT_HOME

while getopts s:f:m:n: opt 
do 
	case "$opt" in
		s) MODULE_FILE=$OPTARG;;
		f) MODULE_FOLDERS_DIRECTORY=$OPTARG;;
		m) MODULES_DIR=$OPTARG;;
		n) NETKERNEL_ROOT_DIRECTORY=$OPTARG;;
		[?]) show_usage;;
	esac
done

if [[ -z $MODULE_FILE ]]; then
	echo "No module.groovy file given, exiting"
	exit
fi

echo "Generating module XML"
echo `pwd`
java -jar $FLUNK_PATH/flunk.jar $MODULE_FILE > module_temp

MODULE_NAME=$(grep -m 1 "<uri>.*</uri>" module_temp | sed 's/.*<uri>//' | sed 's/<\/uri>.*//' | sed 's/:/./g' | tr -d ' ')

MODULE_FILE=$(echo $MODULE_FILE | tr -d ' ')
MODULES_DIR=$(echo $MODULES_DIR | tr -d ' ')
MODULE_FOLDERS_DIRECTORY=$(echo $MODULE_FOLDERS_DIRECTORY | tr -d ' ')
NETKERNEL_ROOT_DIRECTORY=$(echo $NETKERNEL_ROOT_DIRECTORY | tr -d ' ')

if [[ -d $MODULES_DIR/$MODULE_NAME ]]; then
	echo "Directory $MODULES_DIR/$MODULE_NAME already exists"
	echo "Overwriting existing module definition"
else
	mkdir $MODULES_DIR/$MODULE_NAME
fi	

echo "Creating module XML at $MODULES_DIR/$MODULE_NAME/module.xml"
rm -f $MODULES_DIR/$MODULE_NAME/module.xml
mv module_temp $MODULES_DIR/$MODULE_NAME/module.xml

if [[ -n $MODULE_FOLDERS_DIRECTORY ]]; then
	echo "Adding module folders to $MODULE_FOLDERS_DIRECTORY"

	cp -vr $MODULE_FOLDERS_DIRECTORY/* $MODULES_DIR/$MODULE_NAME/
fi

if [[ -n $NETKERNEL_ROOT_DIRECTORY ]]; then
	echo "<modules><module>file:${MODULES_DIR}/${MODULE_NAME}/</module></modules>" > commission_temp

	if [[ -ne ${NETKERNEL_ROOT_DIRECTORY}/etc/modules.d/${MODULE_NAME}.xml ||
			-n $(diff commission_temp ${NETKERNEL_ROOT_DIRECTORY}/etc/modules.d/${MODULE_NAME}.xml) ]]; then
		echo "Adding ${MODULE_NAME}.xml to ${NETKERNEL_ROOT_DIRECTORY}/etc/modules.d"
		echo "<modules><module>file:${MODULES_DIR}/${MODULE_NAME}/</module></modules>" > ${NETKERNEL_ROOT_DIRECTORY}/etc/modules.d/${MODULE_NAME}.xml
	else
		echo "No changes detected in module location, skipping deployment configuration"
	fi

	rm -f commission_temp
	
else
	echo "Skipping deployment"
fi

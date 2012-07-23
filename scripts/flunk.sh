#!/bin/bash
SCRIPT_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

show_usage() {
echo ${0##/*}" Usage:"
echo "  Generate and Commission a Netkernel Module using the flunk DSL "
echo "${0##/} -s MODULE_SOURCE_DIRECTORY [-f MODULE_FOLDERS_DIRECTORY] [-m MODULES_DIRECTORY]"
echo "[-n NETKERNEL_ROOT_DIRECTORY] [-t (xml/groovy)]"
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

MODULE_SOURCE_DIRECTORY=""
MODULE_FILE=""
MODULE_FOLDERS_DIRECTORY=""
NETKERNEL_ROOT_DIRECTORY=""
MODULE_TYPE=""
FLUNK_PATH=$SCRIPT_HOME
MODULES_DIR=$SCRIPT_HOME

while getopts s:f:m:n:t: opt 
do 
	case "$opt" in
		s) MODULE_SOURCE_DIRECTORY=$OPTARG;;
		f) MODULE_FOLDERS_DIRECTORY=$OPTARG;;
		m) MODULES_DIR=$OPTARG;;
		n) NETKERNEL_ROOT_DIRECTORY=$OPTARG;;
		t) MODULE_TYPE=$OPTARG;;
		[?]) show_usage;;
	esac
done

echo "Generating module XML"

MODULE_TYPE=$(echo $MODULE_TYPE | tr -d ' ')

MODULE_FILE=$(echo $MODULE_SOURCE_DIRECTORY/module.$MODULE_TYPE | tr -d ' ')


if [[ ! -e $MODULE_FILE ]]; then
	echo "File $MODULE_FILE doesn't exist, exiting"
	exit
fi

if [[ $MODULE_TYPE = "xml" ]]; then
	cat $MODULE_FILE > module_temp
elif [[ $MODULE_TYPE = "groovy" ]]; then
	java -jar $FLUNK_PATH/flunk.jar $MODULE_FILE > module_temp
else
	echo "unknown file type $MODULE_TYPE provided, exiting"
	exit
fi

MODULE_NAME=$(grep -m 1 "<uri>.*</uri>" module_temp | sed 's/.*<uri>//' | sed 's/<\/uri>.*//' | sed 's/:/./g' | tr -d ' ')

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
	echo "Netkernel root directory found, beginning commissioning"
	echo "<modules><module>file:${MODULES_DIR}/${MODULE_NAME}/</module></modules>" > commission_temp
	echo "${NETKERNEL_ROOT_DIRECTORY}/etc/modules.d/${MODULE_NAME}.xml"

	DIFF_RESULT=$(diff commission_temp ${NETKERNEL_ROOT_DIRECTORY}/etc/modules.d/${MODULE_NAME}.xml)

	if [[ ! -e ${NETKERNEL_ROOT_DIRECTORY}/etc/modules.d/${MODULE_NAME}.xml || -n $DIFF_RESULT ]]; then
		echo "Adding ${MODULE_NAME}.xml to ${NETKERNEL_ROOT_DIRECTORY}/etc/modules.d"
		echo "<modules><module>file:${MODULES_DIR}/${MODULE_NAME}/</module></modules>" > ${NETKERNEL_ROOT_DIRECTORY}/etc/modules.d/${MODULE_NAME}.xml
	else
		echo "No changes detected in module location, skipping deployment configuration"
	fi

	rm -f commission_temp
	
else
	echo "Skipping deployment"
fi

#!/bin/sh

rproc_class_dir="/sys/class/remoteproc/remoteproc0/"
fw_dir="/lib/firmware"
fw_name="OpenAMP_TTY_echo.elf"
echo "fw_m4.sh: firmware name=${fw_name}"
rproc_state=`tr -d '\0' < $rproc_class_dir/state`

error() {
        echo -e "$1"
        exit 0
}


case $1 in
        start) ;;
        stop) ;;
        *) echo "`basename ${0}`:usage: start | stop"
           exit 1
           ;;
esac

#################
# Start example #
#################
if [ $1 == "start" ]
then

if [ $rproc_state == "running" ]
then
echo "Not possible to start fw, an M4 firmware is already running"

else
if [ ! -d $fw_dir ]
then
mkdir -p $fw_dir
fi
cp $fw_name $fw_dir/

# load and start firmware
echo $fw_name > $rproc_class_dir/firmware
echo start > $rproc_class_dir/state
fi

fi


################
# Stop example #
################
if [ $1 == "stop" ]
then

if [ $rproc_state == "offline" ]
then
echo "Nothing to do, no M4 fw is running"

else
echo stop > $rproc_class_dir/state

if [ -e $fw_dir/$fw_name ]
then
rm $fw_dir/$fw_name
fi
fi

fi

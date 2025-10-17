#!/bin/bash
# author: Lycan

# run with ./<FILE_NAME>.sh <MEM_USAGE_LIMIT_IN_MB> <PROCESS_HINT> <INTERVAL_IN_SEC>
# for example, ./mem_manager.sh 6000 "Vms" 120
# here the script will check the total memory usage at an interval of 120 sec and if the usage exceeds 6000 MB, it will kill the Vms Process if its running.
# the PROCESS_HINT is a phrase from the actual process name (like we use in grep)

# obtaining script name
script="$(basename "$(test -L "$0" && readlink "$0" || echo "$0")")"

# checking CLA
if [[ $# != 3 ]];
then
	echo "Error in syntax: ./$script <MEM_USAGE_LIMIT_IN_MB> <PROCESS_HINT> <INTERVAL_IN_SEC>"
	exit 1
fi
let MEM_USAGE_LIMIT=$1*1024
PROC_HINT=$2
INTERVAL=$3

# analyzing memory consumption at given interval
while true
do
	# getting RAM usage
	MEM_STAT=`free -m | grep Mem` 
	let VAL=`echo $MEM_STAT | cut -f3 -d' '`*1024
	echo "MemStat:: usage limit: $MEM_USAGE_LIMIT KB | current usage: $VAL KB"

	# if the RAM usage exceeds the given usage limit the mentioned process is killed forcebly, if it's running
	if [[ $VAL > $MEM_USAGE_LIMIT ]];
		then
			PROC_COUNT=`ps -ef | grep -i "$PROC_HINT" | grep -E -v "grep|$script" | wc -l`
			if [[ $PROC_COUNT > 0 ]];
			then
				PROCESS_ID=`/bin/ps -fu $USER| grep $PROC_HINT | grep -E -v "grep|$script" | awk '{print $2}'`
				kill -9 $PROCESS_ID
				echo "process '$PROC_HINT' is killed"
				echo
			else
				echo "no active process associated with '$PROC_HINT'"
				echo
			fi
		else
			echo
	fi
	sleep $INTERVAL

	# unsetting variables
	unset MEM_STAT
	unset VAL
	unset PROC_COUNT
	unset PROCESS_ID
done
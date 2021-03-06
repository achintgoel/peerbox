#!/bin/bash
export CLASSPATH="./bin/:./lib/google-gson-1.6/gson-1.6.jar:./lib/netty-3.2.4.Final/jar/netty-3.2.4.Final.jar:./lib/commons-codec-1.4/commons-codec-1.4.jar"

if [ $# -eq 0 ] ; then
	echo "Use -m or -f"
	exit
fi

args=""
i=1
inArgs=("$@")

while [ $i -lt $# ] ; do
	args="$args ${inArgs[$i]}"
	let i=$i+"1"
done

if [ $1 = "-m" ] ; then
	java org.peerbox.demo.MultiKadInstances $args
elif [ $1 = "-f" ] ; then
	java org.peerbox.demo.FileShareCLI $args
else
	echo "Use -m or -f"
fi

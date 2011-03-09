#!/bin/bash
export CLASSPATH="./bin/:./lib/google-gson-1.6/gson-1.6.jar:./lib/netty-3.2.4.Final/jar/netty-3.2.4.Final.jar"

args=""
i=1
inArgs=("$@")

while [ $i -lt $# ] ; do
	args="$args ${inArgs[$i]}"
	let i=$i+"1"
done

if [ $1 = "-m" ] ; then
	java org.peerbox.testlets.KadThisBetterWork $args
elif [ $1 = "-s" ] ; then
	java org.peerbox.testlets.DumbCLI $args
elif [ $1 = "-f" ] ; then
	java org.peerbox.testlets.FileShareTest $args
fi

#!/bin/sh

if [ -z "$DISPLAY" ]; then
	sbt run
else
	sbt "run gui"
fi
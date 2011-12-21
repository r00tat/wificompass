#!/bin/bash
echo "Generating ORMlite resources"
java -classpath lib/ormlite-core-4.31.jar:lib/ormlite-android-4.31.jar:bin/res:bin/classes com.j256.ormlite.android.apptools.OrmLiteConfigUtil ormlite_config.txt
echo "done."


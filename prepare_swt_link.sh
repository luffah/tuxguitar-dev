#!/bin/sh
mkdir -p ./lib
for i in "/usr/lib" "/usr/local/lib" "/opt/lib" "/opt/local/lib"
do
  echo "Search lib SWT in $i"
	SWT_LIB="`find $i -name "swt*.jar" | sort -r | head -n1`"
	if [ -n "$SWT_LIB" ]
	then
		echo "Found '$SWT_LIB'"
		if [ -L ./lib/swt.jar ]
		then
			unlink ./lib/swt.jar
		fi
		ln -s $SWT_LIB ./lib/swt.jar
		exit 0
	fi
done 
echo "lib SWT is not found"
echo "you shall install following libs: libswt-gtk-4-java libswt-cairo-gtk-4-jni libswt-webkit-gtk-4-jni"
exit 1

if [ "$(basename $(pwd))" = "Wildflower" ]
then
	cd wildflower-server/src/main/resources/public
	ln -s ../../../../../wildflower-web/build/compiled.js ./compiled.js
	cd ../../../../..
else
	echo "Run this from the project root"
fi

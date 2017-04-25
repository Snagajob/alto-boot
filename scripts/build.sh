export BASEDIR="/Users/$USER/ALTO-ACL-2016"
export TOMCAT="/Users/$USER/apache-tomcat-6.0.48/"
cd $BASEDIR
rm -r ${BASEDIR}/WebContent/WEB-INF/classes/
rm -r ${TOMCAT}/webapps/alto-release/
mkdir WebContent/WEB-INF/classes
javac -cp WebContent/WEB-INF/lib/*:$TOMCAT/* src/*/*.java -d WebContent/WEB-INF/classes
cp -r WebContent $TOMCAT/webapps/
mv $TOMCAT/webapps/WebContent $TOMCAT/webapps/alto-release
$TOMCAT/bin/catalina.sh start

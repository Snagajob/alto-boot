BASEDIR="${HOME}/ALTO-ACL-2016"
TOMCAT="${HOME}/apache-tomcat-6.0.53/"
cd $BASEDIR
rm -r ${BASEDIR}/WebContent/WEB-INF/classes/
rm -r ${TOMCAT}/webapps/alto-release/
mkdir WebContent/WEB-INF/classes
javac -cp WebContent/WEB-INF/lib/*:${TOMCAT}/* src/*/*.java -d WebContent/WEB-INF/classes
cp -r WebContent $TOMCAT/webapps/
mv ${TOMCAT}/webapps/WebContent ${TOMCAT}/webapps/alto-release
echo "${TOMCAT}/bin/catalina.sh start" > start_alto.sh
echo "${TOMCAT}/bin/catalina.sh stop" > stop_alto.sh

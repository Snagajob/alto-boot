BASEDIR="${HOME}/ALTO-ACL-2016"
TOMCAT="${HOME}/apache-tomcat-6.0.48/"
cd $BASEDIR

## copy over any annotation files 
mkdir ${BASEDIR}/existing_results/
rsync -ri ${TOMCAT}/webapps/alto-release/results/ ${BASEDIR}/existing_results/

rm -r ${TOMCAT}/webapps/alto-release/
rm -r ${BASEDIR}/WebContent/WEB-INF/classes/
mkdir WebContent/WEB-INF/classes
javac -cp WebContent/WEB-INF/lib/*:${TOMCAT}/* src/*/*.java -d WebContent/WEB-INF/classes
cp -r WebContent $TOMCAT/webapps/
mv ${TOMCAT}/webapps/WebContent ${TOMCAT}/webapps/alto-release
echo "${TOMCAT}/bin/catalina.sh start" > start_alto.sh
echo "${TOMCAT}/bin/catalina.sh stop" > stop_alto.sh

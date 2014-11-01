TEST_DIR=~/tmp
ART=sonar-cxx-plugin-0.3.jar

mvn clean install
cp target/$ART $TEST_DIR/sonar-*/extensions/plugins/
$TEST_DIR/sonar-*/bin/linux-x86-64/sonar.sh restart
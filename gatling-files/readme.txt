gatling instructions:

1.- Download gatling load test tool from:

http://repo1.maven.org/maven2/io/gatling/highcharts/gatling-charts-highcharts/2.0.3/gatling-charts-highcharts-2.0.3-bundle.zip

2.- Unzip gatling

3.- At least JDK7u6 is needed

4.- copy portfolio-service-simple folder inside of simulations to:

path_where_gatling_is_installed/gatling-charts-highcharts-2.0.3/user-files/simulations

5.- copy file inside of request-bodies to

path_where_gatling_is_installed/gatling-charts-highcharts-2.0.3/user-files/request-bodies

6.- From:

path_where_gatling_is_installed/gatling-charts-highcharts-2.0.3/bin/

run ./gatling.sh (linux) or batling.bat (windows)

and select RecordedSimulation from the menu and write an optional description

7.- Results of simulation are in a path similar to:

path_where_gatling_is_installed/gatling-charts-highcharts-2.0.3/results/RecordedSimulation-xxxxxxxxx/index.html
for f in BasicMetrics  DisableCoverage  ExclusionMetrics ViolationsMetrics
do
	cd $f
	make && make sonar && make clean
	cd -
done

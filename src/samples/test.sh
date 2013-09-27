for f in SampleProject2 SampleProject3 SampleProject5 SampleProject6
do
	cd $f
	make && make sonar && make clean
	cd -
done

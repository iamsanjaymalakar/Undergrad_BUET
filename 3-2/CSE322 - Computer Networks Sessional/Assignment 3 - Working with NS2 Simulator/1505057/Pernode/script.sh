i=0;



#####node_num variation
echo "                     Throughput per Node"
echo "" 
for((i=4; i<=20; i=i+4))
do
	sudo rm "gnuTest.dat"
	ns wirelessStatic_802_11_tcp.tcl $i 20 100 500
	echo "~~~~~~~~~~~~~~~~"
	awk -f wirelessStatic_802_11_tcp.awk staticOut.tr
	echo "~~~~~~~~~~~~~~~~"


gnuplot -p << EOF
set title "Node vs Throughput"
set xlabel "Node Number(Total 5*$i)"
set ylabel "Throughput"
set term wxt title "Varying number of nodes"
plot "gnuTest.dat" using 1:2 title 'Throughput' with linespoints

EOF

done

:'
sudo rm "gnuTest.dat"
#####flow variation
echo "                      Number of flows"
echo " "
for((i=10; i<=50; i=i+10))
do
	ns wirelessStatic_802_11_tcp.tcl 20 $i 100 500
	echo "~~~~~~~~~~~~~~~~"
	awk -f wirelessStatic_802_11_tcp.awk staticOut.tr
	echo "~~~~~~~~~~~~~~~~"
done

gnuplot -p << EOF
set title "Flow Number vs Throughput"
set xlabel "Flow Number"
set ylabel "Throughput"
set term wxt title "Varying number of flows"
plot "gnuTest.dat" using 2:5 title 'Throughput' with linespoints

EOF

gnuplot -p << EOF
set title "Flow Number vs Receive Ratio-Drop Ratio"
set xlabel "Flow Number"
set ylabel "Receive Ratio-Drop Ratio"
set term wxt title "Varying number of flows"
plot "gnuTest.dat" using 2:6 title 'Receive Ratio' with linespoints,"gnuTest.dat" using 2:7 title 'Drop Ratio' with linespoints
EOF



sudo rm "gnuTest.dat"
#####packets per second variation
echo "                    Number of packets per second"
echo " "
for((i=20; i<=100; i=i+20))
do
	
	ns wirelessStatic_802_11_tcp.tcl 12 20 $i 500
	echo "~~~~~~~~~~~~~~~~"
	awk -f wirelessStatic_802_11_tcp.awk staticOut.tr
	echo "~~~~~~~~~~~~~~~~"
done

gnuplot -p << EOF
set title "Packets per second vs Throughput"
set xlabel "Packets per second"
set ylabel "Throughput"
set term wxt title "Varying packets per second"
plot "gnuTest.dat" using 3:5 title 'Throughput' with linespoints

EOF

gnuplot -p << EOF
set title "Packets per second vs Receive Ratio-Drop Ratio"
set xlabel "Packets per second"
set ylabel "Receive Ratio-Drop Ratio"
set term wxt title "Varying packets per second"
plot "gnuTest.dat" using 3:6 title 'Receive Ratio' with linespoints,"gnuTest.dat" using 3:7 title 'Drop Ratio' with linespoints
EOF


sudo rm "gnuTest.dat"
#####Tx_range variation
echo "                    Tx Range variation"
echo " "
for((i=300; i<=500; i=i+50))
do
	
	ns wirelessStatic_802_11_tcp.tcl 8 10 100 $i 
	echo "~~~~~~~~~~~~~~~~"
	awk -f wirelessStatic_802_11_tcp.awk staticOut.tr
	echo "~~~~~~~~~~~~~~~~"
done
gnuplot -p << EOF
set title "Range vs Throughput"
set xlabel "Range in meter"
set ylabel "Throughput"
set term wxt title "Varying range"
plot "gnuTest.dat" using 4:5 title 'Throughput' with linespoints

EOF

gnuplot -p << EOF
set title "Range vs Receive Ratio-Drop Ratio"
set xlabel "Range"
set ylabel "Receive Ratio-Drop Ratio"
plot "gnuTest.dat" using 4:6 title 'Receive Ratio' with linespoints,"gnuTest.dat" using 4:7 title 'Drop Ratio' with linespoints
EOF

'

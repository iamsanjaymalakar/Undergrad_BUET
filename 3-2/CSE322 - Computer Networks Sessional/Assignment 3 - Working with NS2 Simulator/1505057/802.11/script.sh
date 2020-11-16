i=0;


sudo rm "gnuTest.dat"
#####node_num variation
echo "                     Number of nodes"
echo "" 
for((i=4; i<=20; i=i+4))
do
	ns wirelessStatic_802_11_tcp.tcl $i 20 100 400
	echo "~~~~~~~~~~~~~~~~"
	awk -f wirelessStatic_802_11_tcp.awk staticOut.tr
	echo "~~~~~~~~~~~~~~~~"
done

gnuplot -p << EOF
set title "Node Number vs Throughput"
set xlabel "Node Number"
set ylabel "Throughput"
set term wxt title 'Varying number of nodes'
plot "gnuTest.dat" using 1:5 title 'Throughput' with linespoints
set term png
set output "nodethrough.png"
replot
EOF

gnuplot -p << EOF
set title "Node Number vs Receive Ratio-Drop Ratio"
set xlabel "Node Number"
set ylabel "Receive Ratio-Drop Ratio"
set term wxt title 'Varying number of nodes'
plot "gnuTest.dat" using 1:6 title 'ReceivedRatio' with linespoints,"gnuTest.dat" using 1:7 title 'DropRatio' with linespoints
set term png
set output "nodepacket.png"
replot
EOF

gnuplot -p << EOF
set title "Node Number vs End-End time delay"
set xlabel "Node Number"
set ylabel "End-End time delay"
set term wxt title 'Varying number of nodes'
plot "gnuTest.dat" using 1:8 title 'End-End Time Delay' with linespoints
set term png
set output "nodetime.png"
replot
EOF

gnuplot -p << EOF
set title "Node Number vs Energy Consumption"
set xlabel "Node Number"
set ylabel "Energy Consumption"
set term wxt title 'Varying number of nodes'
plot "gnuTest.dat" using 1:9 title 'Energy Consumption' with linespoints
set term png
set output "nodeenergy.png"
replot
EOF



sudo rm "gnuTest.dat"
#####flow variation
echo "                      Number of flows"
echo " "
for((i=10; i<=50; i=i+10))
do
	ns wirelessStatic_802_11_tcp.tcl 20 $i 100 400
	echo "~~~~~~~~~~~~~~~~"
	awk -f wirelessStatic_802_11_tcp.awk staticOut.tr
	echo "~~~~~~~~~~~~~~~~"
done

gnuplot -p << EOF
set title "Flow Number vs Throughput"
set xlabel "Flow Number"
set ylabel "Throughput"
set term wxt title 'Varying number of flows'
plot "gnuTest.dat" using 2:5 title 'Throughput' with linespoints
set term png
set output "flowthro.png"
replot

EOF

gnuplot -p << EOF
set title "Flow Number vs Receive Ratio-Drop Ratio"
set xlabel "Flow Number"
set ylabel "Receive Ratio-Drop Ratio"
set term wxt title 'Varying number of flows'
plot "gnuTest.dat" using 2:6 title 'Receive Ratio' with linespoints,"gnuTest.dat" using 2:7 title 'Drop Ratio' with linespoints
set term png
set output "flowpacket.png"
replot
EOF

gnuplot -p << EOF
set title "Flow Number vs End-End time delay"
set xlabel "Flow Number"
set ylabel "End-End time delay"
set term wxt title 'Varying number of flows'
plot "gnuTest.dat" using 2:8 title 'End-End Time Delay' with linespoints
set term png
set output "flowtime.png"
replot
EOF

gnuplot -p << EOF
set title "Flow Number vs Energy Consumption"
set xlabel "Flow Number"
set ylabel "Energy Consumption"
set term wxt title 'Varying number of flows'
plot "gnuTest.dat" using 2:9 title 'Energy Consumption' with linespoints
set term png
set output "flowenergy.png"
replot
EOF



sudo rm "gnuTest.dat"
#####packets per second variation
echo "                    Number of packets per second"
echo " "
for((i=20; i<=100; i=i+20))
do
	
	ns wirelessStatic_802_11_tcp.tcl 12 20 $i 400
	echo "~~~~~~~~~~~~~~~~"
	awk -f wirelessStatic_802_11_tcp.awk staticOut.tr
	echo "~~~~~~~~~~~~~~~~"
done

gnuplot -p << EOF
set title "Packets per second vs Throughput"
set xlabel "Packets per second"
set ylabel "Throughput"
set term wxt title 'Varying packets per second'
plot "gnuTest.dat" using 3:5 title 'Throughput' with linespoints
set term png
set output "packetsthro.png"
replot

EOF

gnuplot -p << EOF
set title "Packets per second vs Receive Ratio-Drop Ratio"
set xlabel "Packets per second"
set ylabel "Receive Ratio-Drop Ratio"
set term wxt title 'Varying packets per second'
plot "gnuTest.dat" using 3:6 title 'Receive Ratio' with linespoints,"gnuTest.dat" using 3:7 title 'Drop Ratio' with linespoints
set term png
set output "packetspacket.png"
replot
EOF

gnuplot -p << EOF
set title "Packets per second vs End-End time delay"
set xlabel "Packets per second"
set ylabel "End-End time delay"
set term wxt title 'Varying number of nodes'
plot "gnuTest.dat" using 3:8 title 'End-End Time Delay' with linespoints
set term png
set output "packetstime.png"
replot
EOF

gnuplot -p << EOF
set title "Packets per second vs Energy Consumption"
set xlabel "Packets per second"
set ylabel "Energy Consumption"
set term wxt title 'Varying number of nodes'
plot "gnuTest.dat" using 3:9 title 'Energy Consumption' with linespoints
set term png
set output "packetsenergy.png"
replot
EOF


sudo rm "gnuTest.dat"
#####Tx_range variation
echo "                    Tx Range variation"
echo " "
for((i=200; i<=1000; i=i+200))
do
	
	ns wirelessStatic_802_11_tcp.tcl 20 30 100 $i 
	echo "~~~~~~~~~~~~~~~~"
	awk -f wirelessStatic_802_11_tcp.awk staticOut.tr
	echo "~~~~~~~~~~~~~~~~"
done
gnuplot -p << EOF
set title "Range vs Throughput"
set xlabel "Range in meter"
set ylabel "Throughput"
set term wxt title 'Varying range'
plot "gnuTest.dat" using 4:5 title 'Throughput' with linespoints
set term png
set output "txthrou.png"
replot

EOF

gnuplot -p << EOF
set title "Range vs Receive Ratio-Drop Ratio"
set xlabel "Range"
set ylabel "Receive Ratio-Drop Ratio"
plot "gnuTest.dat" using 4:6 title 'Receive Ratio' with linespoints,"gnuTest.dat" using 4:7 title 'Drop Ratio' with linespoints
set term png
set output "txpacket.png"
replot
EOF

gnuplot -p << EOF
set title "Range vs End-End time delay"
set xlabel "Range"
set ylabel "End-End time delay"
set term wxt title 'Varying number of nodes'
plot "gnuTest.dat" using 4:8 title 'End-End Time Delay' with linespoints
set term png
set output "txtime.png"
replot
EOF

gnuplot -p << EOF
set title "Range vs Energy Consumption"
set xlabel "Range"
set ylabel "Energy Consumption"
set term wxt title 'Varying number of nodes'
plot "gnuTest.dat" using 4:9 title 'Energy Consumption' with linespoints
set term png
set output "txenergy.png"
replot
EOF




set cbr_size 1000
set cbr_rate 11.0Mb
set cbr_interval 1;# ?????? 1 for 1 packets per second and 0.1 for 10 packets per second

set x_dim 1000
set y_dim 1000

set num_row 10 ;#number of row
set num_col 10 ;#number of column

set time_duration 25 ;#50
set start_time 50 ;#100
set parallel_start_gap 1.0
#set cross_start_gap 0.0

set num_parallel_flow 20
set num_cross_flow 10
set num_random_flow 0

set grid 1
set extra_time 2 ;#10

set tcp_src Agent/UDP ;# Agent/TCP or Agent/TCP/Reno or Agent/TCP/Newreno or Agent/TCP/FullTcp/Sack or Agent/TCP/Vegas
set tcp_sink Agent/Null ;# Agent/TCPSink or Agent/TCPSink/Sack1


#############################################################ENERGY PARAMETERS
set val(energymodel_11)    EnergyModel     ;
set val(initialenergy_11)  1000            ;# Initial energy in Joules
set val(idlepower_11) 900e-3			;#Stargate (802.11b) 
set val(rxpower_11) 925e-3			;#Stargate (802.11b)
set val(txpower_11) 1425e-3			;#Stargate (802.11b)
set val(sleeppower_11) 300e-3			;#Stargate (802.11b)
set val(transitionpower_11) 200e-3		;#Stargate (802.11b)	??????????????????????????????/
set val(transitiontime_11) 3			;#Stargate (802.11b)

#############################################################PROTOCOLS AND MODELS
set val(chan) Channel/WirelessChannel ;# channel type
set val(prop) Propagation/TwoRayGround ;# radio-propagation model
#set val(prop) Propagation/FreeSpace ;# radio-propagation model
set val(netif) Phy/WirelessPhy ;# network interface type
set val(mac) Mac/802_11 ;# MAC type
#set val(mac) SMac/802_15_4 ;# MAC type
set val(ifq) Queue/DropTail/PriQueue ;# interface queue type
set val(ll) LL ;# link layer type
set val(ant) Antenna/OmniAntenna ;# antenna model
set val(ifqlen) 50 ;# max packet in ifq
set val(rp) DSDV ;# routing protocol


#############################################################NS INITIALIZATION
set ns [new Simulator]

set tracefd [open staticOut.tr w]
$ns trace-all $tracefd

set namtrace [open staticOut.nam w]
$ns namtrace-all-wireless $namtrace $x_dim $y_dim

set topo_file "topo.txt"
set topofile [open $topo_file "w"]

set topo       [new Topography]
$topo load_flatgrid $x_dim $y_dim

create-god [expr $num_row * $num_col ]

#############################################################NODE CONFIGURATION
$ns node-config -adhocRouting $val(rp) -llType $val(ll) \
     -macType $val(mac)  -ifqType $val(ifq) \
     -ifqLen $val(ifqlen) -antType $val(ant) \
     -propType $val(prop) -phyType $val(netif) \
     -channel  [new $val(chan)] -topoInstance $topo \
     -agentTrace ON -routerTrace OFF\
     -macTrace ON \
     -movementTrace OFF \
			 -energyModel $val(energymodel_11) \
			 -idlePower $val(idlepower_11) \
			 -rxPower $val(rxpower_11) \
			 -txPower $val(txpower_11) \
          		 -sleepPower $val(sleeppower_11) \
          		 -transitionPower $val(transitionpower_11) \
			 -transitionTime $val(transitiontime_11) \
			 -initialEnergy $val(initialenergy_11)


#############################################################NODE CREATION
puts "start node creation"
for {set i 0} {$i < [expr $num_row*$num_col]} {incr i} {
	set node_($i) [$ns node]
	$node_($i) random-motion 0
}

#############################################################NODE POSITIONING
set x_start [expr $x_dim/($num_col*2)];
set y_start [expr $y_dim/($num_row*2)];
set i 0;
while {$i < $num_row } {
#in same column
    for {set j 0} {$j < $num_col } {incr j} {
#in same row
	set m [expr $i*$num_col+$j];
#	$node_($m) set X_ [expr $i*240];
#	$node_($m) set Y_ [expr $k*240+20.0];
#CHNG
	if {$grid == 1} {
		set x_pos [expr $x_start+$j*($x_dim/$num_col)];#grid settings
		set y_pos [expr $y_start+$i*($y_dim/$num_row)];#grid settings
	} else {
		set x_pos [expr int($x_dim*rand())] ;#random settings
		set y_pos [expr int($y_dim*rand())] ;#random settings
	}
	$node_($m) set X_ $x_pos;
	$node_($m) set Y_ $y_pos;
	$node_($m) set Z_ 0.0
#	puts "$m"
	puts -nonewline $topofile "$m x: [$node_($m) set X_] y: [$node_($m) set Y_] \n"
    }
    incr i;
}; 
if {$grid == 1} {
	puts "GRID topology"
} else {
	puts "RANDOM topology"
}
puts "node creation complete"

################################################FLOW CREATION AND ASSOCIATION WITH NODES
for {set i 0} {$i < [expr $num_parallel_flow]} {incr i} {
#    set udp_($i) [new Agent/UDP]
#    set null_($i) [new Agent/Null]

	set udp_($i) [new $tcp_src]
	$udp_($i) set class_ $i
	set null_($i) [new $tcp_sink]
	$udp_($i) set fid_ $i
	if { [expr $i%2] == 0} {
		$ns color $i Blue
	} else {
		$ns color $i Red
	}
} 

################################################PARALLEL FLOW
for {set i 0} {$i < $num_parallel_flow } {incr i} {
	set udp_node $i
	set null_node [expr (($num_col)*($num_row))-$i-1];#CHNG
	$ns attach-agent $node_($udp_node) $udp_($i)
  	$ns attach-agent $node_($null_node) $null_($i)
	puts -nonewline $topofile "PARALLEL: Src: $udp_node Dest: $null_node\n"
} 

#  $ns_ attach-agent $node_(0) $udp_(0)
#  $ns_ attach-agent $node_(6) $null_(0)

#CHNG
for {set i 0} {$i < $num_parallel_flow } {incr i} {
     $ns connect $udp_($i) $null_($i)
}
#CHNG
for {set i 0} {$i < $num_parallel_flow } {incr i} {
#	set cbr_($i) [new Application/FTP]
#	$cbr_($i) set type_ AGT
#	$cbr_($i) attach-agent $udp_($i)
	


	set cbr_($i) [new Application/Traffic/CBR]
	$cbr_($i) set packetSize_ $cbr_size
	$cbr_($i) set rate_ $cbr_rate
	$cbr_($i) set interval_ $cbr_interval
	$cbr_($i) attach-agent $udp_($i)
} 

#CHNG
for {set i 0} {$i < $num_parallel_flow } {incr i} {
     $ns at [expr $start_time+$i*$parallel_start_gap] "$cbr_($i) start"
}

puts "flow creation complete"
##########################################################################END OF FLOW GENERATION


############################################################SET TIMING OF EVENTS
# Tell nodes when the simulation ends
#
for {set i 0} {$i < [expr $num_row*$num_col] } {incr i} {
    $ns at [expr $start_time+$time_duration] "$node_($i) reset";
}
$ns at [expr $start_time+$time_duration +$extra_time] "finish"
#$ns at [expr $start_time+$time_duration +20] "puts \"NS Exiting...\"; $ns_ halt"
$ns at [expr $start_time+$time_duration +$extra_time] "$ns nam-end-wireless [$ns now]; puts \"NS Exiting...\"; $ns halt"

$ns at [expr $start_time+$time_duration/2] "puts \"half of the simulation is finished\""
$ns at [expr $start_time+$time_duration] "puts \"end of simulation duration\""


############################################################FINISH PROC AND RUN
proc finish {} {
	puts "finishing"
	global ns tracefd namtrace topofile 
	#global ns_ topofile
	$ns flush-trace
	close $tracefd
	close $namtrace
	close $topofile
    exec nam staticOut.nam &
    exit 0
}

for {set i 0} {$i < [expr $num_row*$num_col]  } { incr i} {
	$ns initial_node_pos $node_($i) 4
}

puts "Starting Simulation..."
$ns run 

#network size
set x_dim 100
set y_dim 100


set wired_node 3
set base_station_node 1
set wireless_node 1

set total_node [expr $wired_node + $base_station_node + $wireless_node]
set time_duration 20
set start_time .1


#protocols and models for different layers
set val(chan) 		Channel/WirelessChannel      ;# channel type
set val(prop) 		Propagation/TwoRayGround     ;# radio-propagation model
set val(netif) 		Phy/WirelessPhy              ;# network interface type
set val(mac) 		Mac/802_11                   ;# MAC type
set val(ifq) 		Queue/DropTail/PriQueue      ;# interface queue type
set val(ll) 		LL                           ;# link layer type
set val(ant) 		Antenna/OmniAntenna          ;# antenna model
set val(ifqlen) 	50                           ;# max packet in ifq - 50 is optimal
set val(rp) 		DSDV                         ;# routing protocol

set ns [new Simulator]


$ns node-config -addressType hierarchical
AddrParams set domain_num_ 2				;#number of domains
lappend cluster_num $wired_node 1						;#number of cluster in each of the domains
AddrParams set cluster_num_ $cluster_num
lappend eilastlevel 1 1 1 2			;#number of nodes in each cluster
AddrParams set nodes_num_ $eilastlevel		;#for each domain


$ns color 1 red
$ns color 2 blue
$ns color 3 green

set tracefile [open 802_11_Wired+Wireless.tr w]
$ns trace-all $tracefile

set namfile [open 802.11_Wired+Wireless.nam w]
$ns namtrace-all-wireless $namfile $x_dim $y_dim

set topo [new Topography]
$topo load_flatgrid $x_dim $y_dim	             ;#flatgrid for (x,y) -> 2D

create-god [expr $wireless_node + $base_station_node]

puts "start node creation"

set temp {0.0.0 0.1.0 0.2.0}           

for {set i 0} {$i < $wired_node} {incr i} {
	set n$i [$ns node [lindex $temp $i]]
}

for {set i 0} {$i < 50} {incr i} {
    set tcp_($i) [new Agent/TCP]
    set sink_($i) [new Agent/TCPSink]
    set ftp_($i) [new Application/FTP]

    $ftp_($i) attach-agent $tcp_($i)
}

$ns node-config -adhocRouting $val(rp) \
		-llType $val(ll) \
		-macType $val(mac) \
		-ifqType $val(ifq) \
		-ifqLen $val(ifqlen) \
		-antType $val(ant) \
		-propType $val(prop) \
		-phyType $val(netif) \
		-channel [new $val(chan)] \
		-topoInstance $topo \
		-agentTrace ON \
		-routerTrace OFF\
		-macTrace ON \
		-movementTrace OFF \
        -wiredRouting ON
#-------------------------------------------------------------------------

set temp {1.0.0 1.0.1}   

set bStation [$ns node [lindex $temp 0]]
$bStation set X_ 30.0
$bStation set Y_ 30.0
$bStation random-motion 0
$bStation color red

$ns node-config -wiredRouting OFF

set n7 [$ns node [lindex $temp 1]]
$n7 set X_ 50.0
$n7 set Y_ 40.0
$n7 random-motion 0
$n7 base-station [AddrParams addr2id [$bStation node-addr]]

#wired
$ns duplex-link $n0 $n1 2Mb 3ms DropTail  
$ns duplex-link-op $n0 $n1 orient 45deg

$ns duplex-link $n0 $n2 2Mb 3ms DropTail
$ns duplex-link-op $n0 $n2 orient 180deg

$ns duplex-link $n1 $n2 2Mb 3ms DropTail 
$ns duplex-link-op $n1 $n2 orient 90deg



#connect nodes with base station
$ns duplex-link $n2 $bStation 5Mb 2ms DropTail

#wired connection
$ns attach-agent $n0 $tcp_(0)
$ns attach-agent $n7 $sink_(0)

#start from a node that is in wired part
#the destination is one that is wireless
$ns connect $tcp_(0) $sink_(0)

#-------------------------------------------

puts "flow creation complete"

#init pos of wireless node
$ns initial_node_pos $n7 20

$ns at .1 "$ftp_(0) start"
$ns at [expr $start_time + $time_duration] "finish"


proc finish {} {
	puts "finishing"
	global ns tracefile namfile 
	$ns flush-trace
	close $tracefile
	close $namfile
	exec nam 802.11_Wired+Wireless.nam &
    exit 0
}


puts "running the simulation"
$ns run
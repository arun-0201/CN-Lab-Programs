set ns [new Simulator]

$ns color 0 blue
$ns color 1 black


set tr [open droptail-queue-out.tr w]
$ns trace-all $tr

set ftr [open droptail-queue-out.nam w]
$ns namtrace-all $ftr

set n0 [$ns node]
set n1 [$ns node]
set n2 [$ns node]
set n3 [$ns node]
set n4 [$ns node]
set n5 [$ns node]

$ns duplex-link $n0 $n2 5Mb 2ms DropTail
$ns duplex-link $n1 $n2 10Mb 5ms DropTail
$ns duplex-link $n2 $n3 4Mb 3ms DropTail
$ns duplex-link $n3 $n4 100Mb 2ms DropTail
$ns duplex-link $n3 $n5 15Mb 4ms DropTail

set udp [new Agent/UDP]

$udp set fid_ 1
set null [new Agent/Null]

$ns attach-agent $n0 $udp 
$ns attach-agent $n4 $null
$ns connect $udp $null

set tcp [new Agent/TCP]
$tcp set fid_ 0
set sink [new Agent/TCPSink]
$ns attach-agent $n1 $tcp
$ns attach-agent $n5 $sink
$ns connect $tcp $sink
$ns connect $tcp $sink

set cbr [new Application/Traffic/CBR]
$cbr attach-agent $udp
$cbr set interval 0.020

set ftp [new Application/FTP]
$ftp attach-agent $tcp

$ftp set interval 0.020
proc finish {} {
           global ns tr ftr
           $ns flush-trace
           close $tr
           close $ftr
           exec nam droptail-queue-out.nam &
           exec gawk -f analysis.awk droptail-queue-out.tr &
           exit
}

$ns at 0.1 "$cbr start"
$ns at 2.0 "$cbr stop"
$ns at 0.1 "$ftp start"
$ns at 2.0 "$ftp stop"
$ns at 2.1 "finish"

$ns run





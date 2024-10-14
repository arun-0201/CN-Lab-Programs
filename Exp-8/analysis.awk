BEGIN {
    recvdSize = 0
    startTime = 0.5
    stopTime = 0.0
}

{
    event = $1
    time = $2
    node_id = $3
    pkt_size = $6

    # Update startTime and stopTime based on packet reception
    if (event == "s") {
        if (time < startTime) {
            startTime = time
        }
    }

    if (event == "r") {
        if (time > stopTime) {
            stopTime = time
        }
        recvdSize += pkt_size
    }
}

END {
    if (stopTime > startTime) {
        avgThroughput = (recvdSize / (stopTime - startTime)) * (8 / 1000);  # Convert to kbps
        printf("Average Throughput [kbps] = %.2f\nStart Time = %.2f\nStop Time = %.2f\n", avgThroughput, startTime, stopTime);
    } else {
        printf("No packets received during the specified time interval.\n");
    }
}

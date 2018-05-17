SCRIPT_DIR="$( cd "$( dirname "$0" )" && pwd )"



if [ "$#" -ne 3 ]; then
    echo "usage: raft_script.sh <num servers> <init server port> <init rmi port>"
    echo "to shutdown the simulation: pkill -9 java"
    exit;
fi

NUM_SERVERS="$1"
echo "Simulating $NUM_SERVERS servers"

INIT_SERVER_PORT="$2"
echo "Server port start from $INIT_SERVER_PORT"

INIT_RMI_PORT="$3"
echo "Rmi port start from $INIT_RMI_PORT"

echo "Start Simulation"
java -jar DemoServer.jar 0 "$INIT_SERVER_PORT" "$INIT_RMI_PORT" & java -jar DemoServer.jar 1 "$INIT_SERVER_PORT" "$INIT_RMI_PORT" & java -jar DemoServer.jar 2 "$INIT_SERVER_PORT" "$INIT_RMI_PORT"

osascript -e 'tell app "Terminal"
    do script "java -jar DemoServer.jar 0 "$INIT_SERVER_PORT" "$INIT_RMI_PORT""
end tell'

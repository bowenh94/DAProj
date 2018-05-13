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

echo "$SCRIPT_DIR is the class path"
java -classpath "$SCRIPT_DIR/target/classes/" Server.newServer 0 "$INIT_SERVER_PORT" "$INIT_RMI_PORT" & java -classpath "$SCRIPT_DIR/target/classes/" Server.newServer 1 "$INIT_SERVER_PORT" "$INIT_RMI_PORT" & java -classpath "$SCRIPT_DIR/target/classes/" Server.newServer 2 "$INIT_SERVER_PORT" "$INIT_RMI_PORT"
echo "Starting server $id"




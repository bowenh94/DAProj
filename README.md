# Raft_Distributed_Algorithm
An JAVA implementation of Raft algoritm. Include a Snake game client for testing.
# Usage
## Following two files in src/configs/ provide the configurations for cluster.
init.config: provide the initial configuration of the cluster, include number of servers and initial term.
serverList.txt: contains servers' information in cluster, include id and address.
## Following two jar files are runnable jar in this project:
DemoServer.jar: Start a server. 
DemoClient.jar: Start a client with Snake game. 
## Following is a simple usage of cluster running on local machine.
raft_script.sh: provide a simple usage of servers. Execute the script will run 3 servers on local machine. 

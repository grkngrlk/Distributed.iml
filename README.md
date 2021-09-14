# distributedSystem
A distributed system can store,edit,query data on distributed servers. Multithreading and Socket Programming been used.

Setup Phase;

first you need to configure conf.txt
line-by-line
– The number of nodes q on the first line;
– One line per non-coordinator node (q − 1 lines total) listing each node’s IP address and the
  port number it is listening on;
– The path to an input file (say “input.txt”) that contains the strings (i.e., set S);
– The number of strings p in S.

Second input.txt, in this file there will be strings to store in described number on conf.txt file.

And finally after configuration you can start non-coordinatorswith: 
"java -cp . packageName.NonCoordinatorClassName 4444"

after that starting coordinator with: 
"java -cp . packageName.CoordinatorClassName conf.txt"

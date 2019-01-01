This dictionary includes 3 subdictionarys.Each includes java src and two city files.All of those can run from the command-line, taking a string as a command line parameter.
[1]Search is the basic A* implementation and it is unefficient.
[2]SearchOptimized is the optimized version of A* alogrthm, it deal with the question of looping becaues I set a condition. The nodes in closed list will never be searched again.
[3]SearchVeryOptimized is from your idears. You taught me this way to optimize code by comparing to the distance between parent and child , the distance between parent and destination.It can reduce many nodes which is not used. But my program is not more efficient than your test examples, maybe there are other optimizations I can use.
--Zeya Kong.
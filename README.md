# BudgetChecker Listener

JPF listener that extends the ListenerAdapter class. 

The BudgetChecker listener is designed to treat the resources of the 
local machine JPF is running on as a set of constraints with set values
(or a "budget") that shouldn't be exceeded during a JPF search.

Constraints for the BudgetChecker have to be set within the .jpf configuration
file. Options for these are as follows:
```
budget.max_time -- This sets the max amount of time in miliseconds that the search should run

budget.max_heap -- This is the upper limit on how large the search heap can be in bytes 

budget.max_depth -- This is the upper limit on how deep the search can go

budget.max_insn -- This is the upper limit on the number of instructions that the search will run

budget.max_state -- This is the upper limit on new states reached in the search

budget.max_new_states -- This is the upper limit on new states that are not a trace replay reached in the search

budget.check_interval -- This defines how often the checks within instructionExecuted are run. By default it is 10,000
```

Unless specified, the default value for the options listed are 0. Value of 0 indicates that the budget checker will not be checked.

# Example

```
public class BadCode {
    public static void main(String[] args) {
        badRecursion(true);
    }
    
    private static void badRecursion(boolean input) {
        badRecursion(!input);
    }
}
```

If we run JPF with the following application configuration file

```
target=BadCode
classpath=<folder that contains BadCode.class>
listener=gov.nasa.jpf.listener.BudgetChecker
budget.max_heap=1000
```

then JPF produces output similar to the following.

```
JavaPathfinder core system v8.0 (rev 26e11d1de726c19ba8ae10551e048ec0823aabc6) - (C) 2005-2014 United States Government. All rights reserved.


====================================================== system under test
BadCode.main()

====================================================== search started: 18/04/20 5:42 PM

====================================================== search constraint
max heap exceeded: 9MB >= 0MB

====================================================== snapshot 
initial program state

====================================================== search constraint
max heap exceeded: 10MB >= 0MB

====================================================== snapshot 
thread java.lang.Thread:{id:0,name:main,status:RUNNING,priority:5,isDaemon:false,lockCount:0,suspendCount:0}
  call stack:
	at BadCode.badRecursion(BadCode.java:7)
	at BadCode.badRecursion(BadCode.java:7)
	...


====================================================== results
no errors detected

====================================================== statistics
elapsed time:       00:00:00
states:             new=1,visited=0,backtracked=0,end=0
search:             maxDepth=1,constraints=2
choice generators:  thread=1 (signal=0,lock=1,sharedRef=0,threadApi=0,reschedule=0), data=0
heap:               new=350,released=2,maxLive=0,gcCycles=1
instructions:       10000
max memory:         119MB
loaded code:        classes=61,methods=1330

====================================================== search finished: 18/04/20 5:42 PM
```
As can be seen from the above output, the max heap was exceeded.
It says "10MB >= 0MB". Even though we put 1000 bytes, it still says 0MB, but thats an issue to solve another day.

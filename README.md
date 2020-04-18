# EECS4315_Project

JPF listener that extends the ListenerAdapter class. 

The BudgetChecker listener is designed to treat the resources of the 
local machine JPF is running on as a set of constraints with set values
(or a "budget") that shouldn't be exceeded during a JPF search.

Constraints for the BudgetChecker have to be set within the .jpf configuration
file. Options for these are as follows:

budget.max_time -- This sets the max amount of time in miliseconds that the search should run
budget.max_heap -- This is the upper limit on how large the search heap can be in bytes 
budget.max_depth -- This is the upper limit on how deep the search can go
budget.max_insn -- This is the upper limit on the number of instructions that the search will run
budget.max_state -- This is the upper limit on new states reached in the search
budget.max_new_states -- This is the upper limit on new states that are not a trace replay reached in the search
budget.check_interval -- This defines how often the checks within instructionExecuted are run. By default it is 10,000

Unless specified, the default value for the options listed are 0. Value of 0 indicates that the budget checker will not be checked.

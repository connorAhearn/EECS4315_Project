/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * The Java Pathfinder core (jpf-core) platform is licensed under the
 * Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package gov.nasa.jpf.listener;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.annotation.JPFOption;
import gov.nasa.jpf.annotation.JPFOptions;
import gov.nasa.jpf.report.Publisher;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * Listener that implements various budget constraints
 */
@JPFOptions({
  @JPFOption(type = "Long", key = "budget.max_time", defaultValue= "-1", comment = "stop search after specified duration [msec]"),
  @JPFOption(type = "Long", key = "budget.max_heap", defaultValue = "-1", comment= "stop search when VM heapsize reaches specified limit"),
  @JPFOption(type = "Int", key = "budget.max_depth", defaultValue = "-1", comment = "stop search at specified search depth"),
  @JPFOption(type = "long", key = "budget.max_insn", defaultValue = "-1", comment = "stop search after specified number of intstructions"),
  @JPFOption(type = "Int", key = "budget.max_state", defaultValue = "-1", comment = "stop search when reaching specified number of new states"),
  @JPFOption(type = "Int", key = "budget.max_new_states", defaultValue = "-1", comment= "stop search after specified number of non-replayed new states")
  @JPFOption(type = "Int", key = "budget.check_interval", defaultValue = "-1", comment= "decides after how many instructions to checks in instructionExecuted")
})

/**
 * JPF listener that extends the ListenerAdapter class. 
 * 
 * The BudgetChecker listener is designed to treat the resources of the 
 * local machine JPF is running on as a set of constraints with set values
 * (or a "budget") that shouldn't be exceeded during a JPF search.
 * 
 * Constraints for the BudgetChecker have to be set within the .jpf configuration
 * file. Options for these are as follows:
 * 
 * budget.max_time -- This sets the max amount of time in miliseconds that the search should run
 * budget.max_heap -- This is the upper limit on how large the search heap can be 
 * budget.max_depth -- This is the upper limit on how deep the search can go
 * budget.max_insn -- This is the upper limit on the number of instructions that the search will run
 * budget.max_state -- This is the upper limit on new states reached in the search
 * budget.max_new_states -- This is the upper limit on new states that are not a trace replay reached in the search
 * budget.check_interval -- This defines how often the checks within instructionExecuted are run. By default it is 10,000
 */
public class BudgetChecker extends ListenerAdapter {

  

  static final int MEGABYTE = 1048576;  // 1024 * 1024
    
  long tStart;
  MemoryUsage muStart;
  long mStart;
  MemoryMXBean mxb;
  
  VM vm;
  Search search;

  // counters
  long insnCount;
  int newStates;

  // the budget thresholds
  long maxTime;
  long maxHeap;
  int maxDepth;
  long maxInsn;
  int maxState;
  int maxNewStates;
  int checkInterval;
  
  // the message explaining the exceeded budget
  String message;
  
  /**
   * Initializes a new BudgetChecker Listener object for the 
   * corresponding JPF instance and configuration file
   * passed to it.
   * @param conf
   * @param jpf
   */
  public BudgetChecker (Config conf, JPF jpf) {
    // initialize counters
    newStates = 0;

    
    //--- get the configured budget limits (0 means not set)
    maxTime = conf.getDuration("budget.max_time", 0);
    maxHeap = conf.getMemorySize("budget.max_heap", 0);
    maxDepth = conf.getInt("budget.max_depth", 0);
    maxInsn = conf.getLong("budget.max_insn", 0);
    maxState = conf.getInt("budget.max_state", 0);
    maxNewStates = conf.getInt("budget.max_new_states", 0);
    checkInterval = conf.getInt("budget.check_interval", 10000)
    
    tStart = System.currentTimeMillis();
    
    if (maxHeap > 0) {
      mxb = ManagementFactory.getMemoryMXBean();
      muStart = mxb.getHeapMemoryUsage();
      mStart = muStart.getUsed();
    }

    search = jpf.getSearch();
    vm = jpf.getVM();
  }
  
  /**
   * Method that checks if the time that the search has ran has 
   * exceeded the max time specified in the configuration file.
   * 
   * @return true if the time has exceeded, false otherwise
   *    - If budget.max_time is not set, returns false
   */
  public boolean timeExceeded() {
    if (maxTime > 0) {
      long duration = System.currentTimeMillis() - tStart;
      if (duration > maxTime) {
        message = "max time exceeded: " + Publisher.formatHMS(duration)
               + " >= " + Publisher.formatHMS(maxTime);
        return true;
      }
    }
    
    return false;
  }
  
  /**
   * Method that checks if the amount of space taken by the heap
   * has exceeded the size specified in the configuration file.
   * 
   * @return true if the heap has exceeded, false otherwise
   *    - If budget.max_heap is not set, returns false
   */
  public boolean heapExceeded() {
    if (maxHeap > 0) {
      MemoryUsage memoryUsage = mxb.getHeapMemoryUsage();
      long used = memoryUsage.getUsed() - mStart;
      if (used > maxHeap) {
        message = "max heap exceeded: " + (used / MEGABYTE) + "MB" 
                      + " >= " + (maxHeap / MEGABYTE) + "MB" ;
        return true;
      }
    }
    
    return false;
  }
  
  /**
   * Method that checks if the depth of the search has exceeded
   * the limit specified in the configuration file
   * 
   * @return true if the depth has exceeded, false otherwise
   *    - If budget.max_depth is not set, returns false
   */
  public boolean depthExceeded () {
    if (maxDepth > 0) {
      int depth = search.getDepth();
      if (depth > maxDepth) {
        message = "max search depth exceeded: " + maxDepth;
        return true;
      }
    }
    
    return false;
  }
    
  /**
   * Method that checks if the number of instructions ran
   * has exceeded the limit specified in the configuration file
   * 
   * @return true if the instruction count has exceeded, false otherwise
   *    - If budget.max_insn is not set, returns false
   */
  public boolean insnExceeded () {
    if (maxInsn > 0) {
      if (insnCount > maxInsn) {
        message = "max instruction count exceeded: " + maxInsn;
        return true;
      }
    }
    return false;
  }

  /**
   * Method that checks if the number of states reached has
   * exceeded the limit specified in the configuration file
   * 
   * @return true if the state count has exceeded, false otherwise
   *    - If budget.max_state is not set, returns false
   */
  public boolean statesExceeded () {
    if (maxState > 0) {
      int stateId = vm.getStateId();
      if (stateId > maxState) {
        message = "max states exceeded: " + maxState;;
        return true;
      }
    }
    
    return false;
  }
  
  /**
   * Method that checks if the number of new states found has
   * exceeded the limit specified in the configuration file
   * 
   * @return true if the new states count has exceeded, false otherwise
   *    - If budget.max_new_states is not set, returns false
   */
  public boolean newStatesExceeded(){
    if (maxNewStates > 0){
      if (newStates > maxNewStates) {
        message = "max new state count exceeded: " + maxNewStates;
        return true;
      }
    }
    return false;
  }
  
  @Override
  /**
   * Overridden method inherited from ListenerAdapter
   * 
   * Anytime the state advances, this method checks if
   * the time, heap, state count, depth or new state count
   * have exceeded their limits. If they have, the search
   * terminates and a message describing why is passed on
   * to the JPF report
   * 
   * @param search 
   */
  public void stateAdvanced (Search search) {    
    if (timeExceeded() || heapExceeded()) {
      search.notifySearchConstraintHit(message);
      search.terminate();
    }
    
    if (search.isNewState()){
      if (!vm.isTraceReplay()){
        newStates++;
      }
      if (statesExceeded() || depthExceeded() || newStatesExceeded()){
        search.notifySearchConstraintHit(message);
        search.terminate();        
      }
    }
  }
      
  @Override
  /**
   * Overridden method inherited from ListenerAdapter
   * 
   * Anytime an instruction executes, this method checks if
   * the time, heap size or amount of instruction executed has
   * exceeded their limits. If they have, the search terminates
   * and a message why is passed on to the JPF report
   *
   * Only checks every 10000 instructions excecuted
   * 
   * @param vm
   * @param ti
   * @param nextInsn
   * @param executedInsn
   */
  public void instructionExecuted (VM vm, ThreadInfo ti, Instruction nextInsn, Instruction executedInsn) {
    // Checks every CHECK_INTERVAL instructions excecuted
    insnCount++;
    if ((insnCount % CHECK_INTERVAL) == 0) {

      if (timeExceeded() || heapExceeded() || insnExceeded()) {
        search.notifySearchConstraintHit(message);

        vm.getCurrentThread().breakTransition("budgetConstraint");
        search.terminate();
      }    
    }
  }

}

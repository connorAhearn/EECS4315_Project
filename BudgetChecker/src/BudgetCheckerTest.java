import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;

import org.junit.Test;

import gov.nasa.jpf.util.test.TestJPF;

/**
 * This BudgetCheckerTest is used to test different parameters for the BudgetChecker listener in JPF. 
 * @author Jeremy Winkler, Connor Ahearn
 *
 */
public class BudgetCheckerTest extends TestJPF {
	// List of properties. Many empty properties are needed when testing multiple properties at once.
	private static String[] PROPERTIES = { "+classpath=./bin",
										   "+native_classpath=./bin",
										   "+listener=BudgetChecker",
										   "+cg.enumerate_random = true",
										   "",
										   "",
										   "",
										   "",
										   "",
										   "",
										   "" };
	
	// Error message strings that are created by Budget Checker
	private static final String MAX_TIME_REACHED = "max time exceeded";
	private static final String MAX_HEAP_REACHED = "max heap exceeded";
	private static final String MAX_DEPTH_REACHED = "max search depth exceeded";
	private static final String MAX_INSTRUCTION_REACHED = "max instruction count exceeded";
	private static final String MAX_STATES_REACHED = "max states exceeded";
	private static final String MAX_NEW_STATES_REACHED = "max new state count exceeded";
	
	// Definitions used to run a certain number of instructions. 
	private static final int FEW_INSTRUCTIONS = 100;
	private static final int SOME_INSTRUCTIONS = 1000;
	private static final int MANY_INSTRUCTIONS = 2 * SOME_INSTRUCTIONS;
	private static final int MEGABYTE = 1048576;
	private static final int _90_MEGABYTES = 90 * MEGABYTE;
	
	
	// Values for properties to be used in multi property tests
	private static final int MAX_TIME = 10000;
	private static final int MAX_HEAP = 100000000;
	private static final int MAX_DEPTH = 3;
	private static final int MAX_INSN = 1000;
	private static final int MAX_STATE = 3;
	private static final int MAX_NEW_STATE = 3;
	
	/**
	 * Runs all test methods within this class
	 * @param methods List of methods to run
	 */
	public static void main(String[] methods) {
		runTestsOfThisClass(methods);
	}
	
	
	/**
	 * Makes a certain number of states
	 * @param maxStates Number of states to make
	 */
	private void makeStates(int maxStates) {
		maxStates--;
		int numberOfStates = 0;
		Random random = new Random();
		
		while (numberOfStates < maxStates && random.nextBoolean()) {
			numberOfStates++;
			System.out.println(numberOfStates);
		}
		System.out.println(numberOfStates);
	}

	/**
	 * A basic execution with two states.
	 */
	private void basicExecution() {
		makeStates(2);
	}
	
	private void concurrency() {
		
		for(int i = 0; i < 5; i++) {
			new PointlessThread().run();
		}
	}

	/**
	 * Resets all properties to their original values.
	 */
	private static void resetProperties() {
		PROPERTIES[0] = "+classpath=./bin";
		PROPERTIES[1] = "+native_classpath=./bin";
		PROPERTIES[2] = "+listener=BudgetChecker";
		PROPERTIES[3] = "+cg.enumerate_random = true";
		int startOfEmptyProperties = 4;
		int endOfEmptyProperties = 10;
		for(int i = startOfEmptyProperties; i <= endOfEmptyProperties; i++) PROPERTIES[i] = "";
	}
	
	/**
	 * Tests the BudgetChecker on an empty app with all properties set.
	 * Ensures no properties are violated
	 */
	@Test
	public void emptyTestWithConfig() {
		resetProperties();
		PROPERTIES[4] = "+budget.max_state=3";
		PROPERTIES[5] = "+budget.max_time=3000";
		PROPERTIES[6] = "+budget.max_heap=" + _90_MEGABYTES;
		PROPERTIES[7] = "+budget.max_depth=100";
		PROPERTIES[8] = "+budget.max_insn=100000";
		PROPERTIES[9] = "+budget.max_new_states=3";
		PROPERTIES[10] = "+budget.check_interval=10000";
		
		PrintStream out = System.out;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(stream));
		
		if(this.verifyNoPropertyViolation(PROPERTIES)) {
			
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_TIME_REACHED));
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_HEAP_REACHED));
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_DEPTH_REACHED));
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_INSTRUCTION_REACHED));
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_STATES_REACHED));
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_NEW_STATES_REACHED));
		}
	}
	
	/**
	 * Tests the BudgetChecker on an empty app with no properties set.
	 * Ensures no properties are violated
	 */
	@Test
	public void emptyTestNoConfig() {
		resetProperties();
		
		PrintStream out = System.out;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(stream));
		if(this.verifyNoPropertyViolation(PROPERTIES)) {
			
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_TIME_REACHED));
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_HEAP_REACHED));
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_DEPTH_REACHED));
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_INSTRUCTION_REACHED));
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_STATES_REACHED));
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_NEW_STATES_REACHED));
		}
	}
	
	/**
	 * Tests that no max state violation occurs when not exceeding max states
	 */
	@Test
	public void testMaxStateNoViolation() {
		resetProperties();
		int maxStates = 4;
		PROPERTIES[4] = "+budget.max_state=" + maxStates;
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			makeStates(maxStates);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_STATES_REACHED));
		}
	}
	
	/**
	 * Tests that no max time violation occurs with a simple execution
	 */
	@Test
	public void testMaxTimeNoViolation() {
		resetProperties();
		PROPERTIES[4] = "+budget.max_time=3000";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			basicExecution();
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_TIME_REACHED));
		}
	}
	
	/**
	 * Tests that no max state violation occurs when not exceeding max states
	 */
	@Test
	public void testMaxStateNoViolationWithConcurrency() {
		resetProperties();
		int maxStates = 1000000000;
		PROPERTIES[4] = "+budget.max_state=" + maxStates;
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			concurrency();
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_STATES_REACHED));
		}
	}
	
	/**
	 * Tests that no max time violation occurs with a simple execution
	 */
	@Test
	public void testMaxTimeNoViolationWithConcurrency() {
		resetProperties();
		PROPERTIES[4] = "+budget.max_time=3000";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			concurrency();
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_TIME_REACHED));
		}
	}
	
	/**
	 * Tests that no max heap violation occurs with a simple execution
	 */
	@Test
	public void testMaxHeapNoViolation() {	
		resetProperties();
		PROPERTIES[4] = "+budget.max_heap=" + _90_MEGABYTES;
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			basicExecution();
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_HEAP_REACHED));
		}
	}
	
	/**
	 * Tests that no max depth violation occurs with a simple execution
	 */
	@Test
	public void testMaxDepthNoViolation() {
		resetProperties();
		PROPERTIES[4] = "+budget.max_depth=100";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			basicExecution();
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_DEPTH_REACHED));
		}
	}
	
	/**
	 * Tests that no max heap violation occurs with a simple execution
	 */
	@Test
	public void testMaxHeapNoViolationWithConcurrency() {	
		resetProperties();
		PROPERTIES[4] = "+budget.max_heap=" + _90_MEGABYTES;
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			concurrency();
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_HEAP_REACHED));
		}
	}
	
	/**
	 * Tests that no max depth violation occurs with a simple execution
	 */
	@Test
	public void testMaxDepthNoViolationWithConcurrency() {
		resetProperties();
		PROPERTIES[4] = "+budget.max_depth=100";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			concurrency();
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_DEPTH_REACHED));
		}
	}
	
	/**
	 * Tests that the max time violation occurs with a longer execution
	 */
	@Test
	public void testMaxTimeViolation() {
		resetProperties();
		PROPERTIES[4] = "+budget.max_time=1";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			int manyExecutions = 1000;
			for(int i = 0; i < manyExecutions; i++) basicExecution();
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", stream.toString().contains(MAX_TIME_REACHED));
		}
	}
	
	/**
	 * Tests that the max time violation occurs with a longer execution
	 */
	@Test
	public void testMaxTimeViolationWithConcurrency() {
		resetProperties();
		PROPERTIES[4] = "+budget.max_time=1";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			concurrency();
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", stream.toString().contains(MAX_TIME_REACHED));
		}
	}
	
	/**
	 * An infinitely recursive call
	 * @param input Does nothing
	 */
	private void badRecursion(boolean input) {
		badRecursion(!input);
	}
	
	/**
	 * Tests that the max heap violation occurs on heap intensive program
	 */
	@Test
	public void testMaxHeapViolation() {
		resetProperties();
		PROPERTIES[4] = "+budget.max_heap=40000";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			badRecursion(true);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", stream.toString().contains(MAX_HEAP_REACHED));
		}
	}
	
	/**
	 * Tests that a max depth violation occurs when exceeding depth
	 */
	@Test
	public void testMaxDepthViolation() {
		resetProperties();
		PROPERTIES[4] = "+budget.max_depth=1";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			makeStates(10);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated: " + stream.toString(), stream.toString().contains(MAX_DEPTH_REACHED));
		}
	}
	
	/**
	 * Tests that a max state violation occurs when exceeding number of states
	 */
	@Test
	public void testMaxStateViolation() {
		resetProperties();
		int maxStates = 3;
		PROPERTIES[4] = "+budget.max_state=" + maxStates;
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			makeStates(maxStates + 1);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was not violated", stream.toString().contains(MAX_STATES_REACHED));
		}
	}
	
	/**
	 * Tests that no max state violation occurs when not exceeding number of states
	 */
	@Test
	public void testMaxNewStateNoViolation() {
		resetProperties();
		int maxStates = 4;
		PROPERTIES[4] = "+budget.max_new_states=" + maxStates;
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			makeStates(maxStates);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_NEW_STATES_REACHED));
		}
	}
	
	/**
	 * Tests that a max new state violation occurs when exceeding max new states
	 */
	@Test
	public void testMaxNewStateViolation() {
		resetProperties();
		int maxStates = 3;
		PROPERTIES[4] = "+budget.max_new_states=" + maxStates;
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			makeStates(maxStates + 1);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was not violated", stream.toString().contains(MAX_NEW_STATES_REACHED));
		}
	}
	
	/**
	 * Tests that no max instruction violation occurs when executing some instructions.
	 */
	@Test
	public void testMaxInstructionNoViolation() {
		resetProperties();
		PROPERTIES[4] = "+budget.max_insn=9999";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			for (int i = 0; i < SOME_INSTRUCTIONS; i++);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_INSTRUCTION_REACHED));
		}
	}
	
	/**
	 * Tests that a max instruction violation occurs when executing many instructions.
	 */
	@Test
	public void testMaxInstructionViolation() {
		resetProperties();
		PROPERTIES[4] = "+budget.max_insn=9999";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			for (int i = 0; i <= MANY_INSTRUCTIONS; i++);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", stream.toString().contains(MAX_INSTRUCTION_REACHED));
		}
	}
	
	/**
	 * Tests that check_interval changes how frequently the check within instructionExcecuted occurs.
	 * Should not have violation since check interval is its default of 10,000;
	 */
	@Test
	public void testCheckIntervalNoViolation() {
		resetProperties();
		PROPERTIES[4] = "+budget.max_insn=99";
		PROPERTIES[5] = "";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			for (int i = 0; i <= FEW_INSTRUCTIONS; i++);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_INSTRUCTION_REACHED));
		}
	}
	
	/**
	 * Tests that check_interval changes how frequently the check within instructionExcecuted occurs.
	 * Should have violation since check interval is low;
	 */
	@Test
	public void testCheckIntervalViolation() {
		resetProperties();
		PROPERTIES[4] = "+budget.max_insn=99";
		PROPERTIES[5] = "+budget.check_interval=100";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			for (int i = 0; i <= FEW_INSTRUCTIONS; i++);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", stream.toString().contains(MAX_INSTRUCTION_REACHED));
		}
	}
	
	/**
	 * Tests that the max heap violation occurs on heap intensive program
	 */
	@Test
	public void testMaxHeapViolationWithConcurrency() {
		resetProperties();
		PROPERTIES[4] = "+budget.max_heap=4000";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			concurrency();
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", stream.toString().contains(MAX_HEAP_REACHED));
		}
	}
	
	/**
	 * Tests that a max depth violation occurs when exceeding depth
	 */
	@Test
	public void testMaxDepthViolationWithConcurrency() {
		resetProperties();
		PROPERTIES[4] = "+budget.max_depth=1";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			concurrency();
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated: " + stream.toString(), stream.toString().contains(MAX_DEPTH_REACHED));
		}
	}
	
	/**
	 * Tests that a max state violation occurs when exceeding number of states
	 */
	@Test
	public void testMaxStateViolationWithConcurrency() {
		resetProperties();
		int maxStates = 3;
		PROPERTIES[4] = "+budget.max_state=" + maxStates;
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			concurrency();
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was not violated", stream.toString().contains(MAX_STATES_REACHED));
		}
	}
	
	/**
	 * Tests that no max state violation occurs when not exceeding number of states
	 */
	@Test
	public void testMaxNewStateNoViolationWithConcurrency() {
		resetProperties();
		int maxStates = 40000000;
		PROPERTIES[4] = "+budget.max_new_states=" + maxStates;
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			concurrency();
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_NEW_STATES_REACHED));
		}
	}
	
	/**
	 * Tests that a max new state violation occurs when exceeding max new states
	 */
	@Test
	public void testMaxNewStateViolationWithConcurrency() {
		resetProperties();
		int maxStates = 3;
		PROPERTIES[4] = "+budget.max_new_states=" + maxStates;
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			concurrency();
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was not violated", stream.toString().contains(MAX_NEW_STATES_REACHED));
		}
	}
	
	/**
	 * Tests that no max instruction violation occurs when executing some instructions.
	 */
	@Test
	public void testMaxInstructionNoViolationWithConcurrency() {
		resetProperties();
		PROPERTIES[4] = "+budget.max_insn=9999";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			concurrency();
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_INSTRUCTION_REACHED));
		}
	}
	
	/**
	 * Tests that check_interval changes how frequently the check within instructionExcecuted occurs.
	 * Should not have violation since check interval is its default of 10,000;
	 */
	@Test
	public void testCheckIntervalNoViolationWithConcurrency() {
		resetProperties();
		PROPERTIES[4] = "+budget.max_insn=99";
		PROPERTIES[5] = "";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			concurrency();
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_INSTRUCTION_REACHED));
		}
	}
	
	/**
	 * Tests that check_interval changes how frequently the check within instructionExcecuted occurs.
	 * Should have violation since check interval is low;
	 */
	@Test
	public void testCheckIntervalViolationWithConcurrency() {
		resetProperties();
		PROPERTIES[4] = "+budget.max_insn=99";
		PROPERTIES[5] = "+budget.check_interval=100";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			concurrency();
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", stream.toString().contains(MAX_INSTRUCTION_REACHED));
		}
	}
	
	/**
	 * Checks that time violation occurs first when a short time was set.
	 */
	@Test
	public void testMultipleProperties_TimeHeapDepth_TimeFails() {
		resetProperties();
		int shortTime = 100;
		PROPERTIES[4] = "+budget.max_time=" + shortTime;
		PROPERTIES[5] = "+budget.max_heap=" + MAX_HEAP;
		PROPERTIES[6] = "+budget.max_depth=" + MAX_DEPTH;
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			for (int i = 0; i <= MANY_INSTRUCTIONS; i++);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", stream.toString().contains(MAX_TIME_REACHED));
		}
	}
	
	/**
	 * Checks that heap violation occurs first when a small heap is used.
	 */
	@Test
	public void testMultipleProperties_TimeHeapDepth_HeapFails() {
		resetProperties();
		int smallHeap= 10;
		PROPERTIES[4] = "+budget.max_time=" + MAX_TIME;
		PROPERTIES[5] = "+budget.max_heap=" + smallHeap;
		PROPERTIES[6] = "+budget.max_depth=" + MAX_DEPTH;
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			badRecursion(true);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", stream.toString().contains(MAX_HEAP_REACHED));
		}
	}
	
	/**
	 * Checks that depth violation occurs first when a small depth is used.
	 */
	@Test
	public void testMultipleProperties_TimeHeapDepth_DepthFails() {
		resetProperties();
		int shortDepth = 1;
		PROPERTIES[4] = "+budget.max_time=" + MAX_TIME;
		PROPERTIES[5] = "+budget.max_heap=" + MAX_HEAP;
		PROPERTIES[6] = "+budget.max_depth=" + shortDepth;
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			int manyStates = 5;
			makeStates(manyStates);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", stream.toString().contains(MAX_DEPTH_REACHED));
		}
	}
	
	/**
	 * Checks that max instructions fails when a small check interval is used
	 */
	@Test
	public void testMultipleProperties_InsnStateNewStates_InsnFails() {
		resetProperties();
		int smallCheckIntrval = 1000;
		PROPERTIES[4] = "+budget.max_insn=" + MAX_INSN;
		PROPERTIES[5] = "+budget.max_state=" + MAX_STATE;
		PROPERTIES[6] = "+budget.max_new_states=" + MAX_NEW_STATE;
		PROPERTIES[7] = "+budget.check_interval=" + smallCheckIntrval; 
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			for (int i = 0; i <= MANY_INSTRUCTIONS; i++);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", stream.toString().contains(MAX_INSTRUCTION_REACHED));
		}
	}
	
	/**
	 * Checks that max states fails when a small number of states is used
	 */
	@Test
	public void testMultipleProperties_InsnStateNewStates_StatesFails() {
		resetProperties();
		int fewStates = 1;
		PROPERTIES[4] = "+budget.max_insn=" + MAX_INSN;
		PROPERTIES[5] = "+budget.max_state=" + fewStates;
		PROPERTIES[6] = "+budget.max_new_states=" + MAX_NEW_STATE;
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			int manyStates = 5;
			makeStates(manyStates);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", stream.toString().contains(MAX_STATES_REACHED));
		}
	}
	
	/**
	 * Checks that max states fails when a small number of new states is used
	 */
	@Test
	public void testMultipleProperties_InsnStateNewStates_NewStatesFails() {
		resetProperties();
		int fewStates = 1;
		PROPERTIES[4] = "+budget.max_insn=" + MAX_INSN;
		PROPERTIES[5] = "+budget.max_state=" + MAX_STATE;
		PROPERTIES[6] = "+budget.max_new_states=" + fewStates;
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			int manyStates = 5;
			makeStates(manyStates);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", stream.toString().contains(MAX_NEW_STATES_REACHED));
		}
	}
}

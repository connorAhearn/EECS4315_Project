import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;

import org.junit.Test;

import gov.nasa.jpf.util.test.TestJPF;

public class BudgetCheckerTest extends TestJPF {
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
										   ""};
	
	// Error message strings that are created by Budget Checker
	private static final String MAX_TIME_REACHED = "max time exceeded";
	private static final String MAX_HEAP_REACHED = "max heap exceeded";
	private static final String MAX_DEPTH_REACHED = "max search depth exceeded";
	private static final String MAX_INSTRUCTION_REACHED = "max instruction count exceeded";
	private static final String MAX_STATES_REACHED = "max states exceeded";
	private static final String MAX_NEW_STATES_REACHED = "max new state count exceeded";
	
	private static final int MEGABYTE = 1048576;
	private static final int _90_MEGABYTES = 90 * MEGABYTE;
	
	
	public static void main(String[] methods) {
		runTestsOfThisClass(methods);
	}
	
	private void basicExecution() {
		Random random = new Random();
		int a = 0;
		if (random.nextBoolean()) {
			a = 1;
		} else {
			a = 2;
		}
	}	
	
	/**
	 * Tests the BudgetChecker on an empty app
	 */
	@Test
	public void emptyTestWithConfig() {
		
		PROPERTIES[4] = "+budget.max_state=3";
		PROPERTIES[5] = "+budget.max_time=3000";
		PROPERTIES[6] = "+budget.max_heap=" + BudgetCheckerTest._90_MEGABYTES;
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
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(BudgetCheckerTest.MAX_TIME_REACHED));
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(BudgetCheckerTest.MAX_HEAP_REACHED));
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(BudgetCheckerTest.MAX_DEPTH_REACHED));
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(BudgetCheckerTest.MAX_INSTRUCTION_REACHED));
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(BudgetCheckerTest.MAX_STATES_REACHED));
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(BudgetCheckerTest.MAX_NEW_STATES_REACHED));
		}
	}
	
	/**
	 * Tests the BudgetChecker on an empty app
	 */
	@Test
	public void emptyTestNoConfig() {
//		PROPERTIES = { "+classpath=./bin",
//				   "+native_classpath=./bin",
//				   "+listener=BudgetChecker",
//				   "+cg.enumerate_random = true",
//				   "",
//				   "",
//				   "",
//				   "",
//				   "",
//				   "",
//				   ""};
		
		resetProperties();
		
		PrintStream out = System.out;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(stream));
		if(this.verifyNoPropertyViolation(PROPERTIES)) {
			
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(BudgetCheckerTest.MAX_TIME_REACHED));
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(BudgetCheckerTest.MAX_HEAP_REACHED));
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(BudgetCheckerTest.MAX_DEPTH_REACHED));
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(BudgetCheckerTest.MAX_INSTRUCTION_REACHED));
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(BudgetCheckerTest.MAX_STATES_REACHED));
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(BudgetCheckerTest.MAX_NEW_STATES_REACHED));
		}
	}

	private void resetProperties() {
		PROPERTIES[0] = "+classpath=./bin";
		PROPERTIES[1] = "+native_classpath=./bin";
		PROPERTIES[2] = "+listener=BudgetChecker";
		PROPERTIES[3] = "+cg.enumerate_random = true";
		for(int i = 4; i <= 10; i++) PROPERTIES[i] = "";
	}
	
	/**
	 * Tests that no max state violation occurs
	 */
	@Test
	public void testMaxStateNoViolation() {
		resetProperties();
		PROPERTIES[4] = "+budget.max_state=3";
		
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
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(BudgetCheckerTest.MAX_STATES_REACHED));
		}
	}
	
	/**
	 * Tests that no max time violation occurs
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
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(BudgetCheckerTest.MAX_TIME_REACHED));
		}
	}
	
	/**
	 * Tests that no max heap violation occurs
	 */
	@Test
	public void testMaxHeapNoViolation() {	
		resetProperties();
		PROPERTIES[4] = "+budget.max_heap=" + BudgetCheckerTest._90_MEGABYTES;
		
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
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(BudgetCheckerTest.MAX_HEAP_REACHED));
		}
	}
	
	/**
	 * Tests that no max depth violation occurs
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
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(BudgetCheckerTest.MAX_DEPTH_REACHED));
		}
	}
	
	/**
	 * Tests that the max time violation occurs
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
			for(int i = 0; i < 1000; i++) basicExecution();
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", stream.toString().contains(BudgetCheckerTest.MAX_TIME_REACHED));
		}
	}
	
	private void badRecursion(boolean input) {
		badRecursion(!input);
	}
	
	/**
	 * Tests that the max heap violation occurs
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
			TestJPF.assertTrue("Property was violated", stream.toString().contains(BudgetCheckerTest.MAX_HEAP_REACHED));
		}
	}
	
	/**
	 * Tests that a max depth violation occurs
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
			Random random = new Random();
			System.out.println("0");
			if (random.nextBoolean()) {
				System.out.println("1");
				if (random.nextBoolean()) {
					System.out.println("2");
					if (random.nextBoolean()) {
						System.out.println("3");
					} else {
						System.out.println("4");
					}
				} else {
					System.out.println("5");
				}
			} else {
				System.out.println("6");
			}
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated: " + stream.toString(), stream.toString().contains(BudgetCheckerTest.MAX_DEPTH_REACHED));
		}
	}
	
	/**
	 * Tests that a max state violation occurs
	 */
	@Test
	public void testMaxStateViolation() {
		resetProperties();
		PROPERTIES[4] = "+budget.max_state=2";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			Random random = new Random();
			System.out.println("0");
			if (random.nextBoolean()) {
				System.out.println("1");
				if (random.nextBoolean()) {
					System.out.println("2");
					if (random.nextBoolean()) {
						System.out.println("3");
					} else {
						System.out.println("4");
					}
				} else {
					System.out.println("5");
				}
			} else {
				System.out.println("6");
			}
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", stream.toString().contains(BudgetCheckerTest.MAX_STATES_REACHED));
		}
	}
}

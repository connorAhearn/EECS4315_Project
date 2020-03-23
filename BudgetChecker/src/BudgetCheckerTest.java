import org.junit.Test;
import gov.nasa.jpf.util.test.TestJPF;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;

public class BudgetCheckerTest extends TestJPF {
	private static String[] PROPERTIES = { "+classpath=./bin",
										   "+native_classpath=./bin",
										   "+listener=BudgetChecker",
										   "+cg.enumerate_random = true",
										   "",
										   "" };
	
	private static String MAX_STATES_REACHED = "max states exceeded";
	private static String MAX_NEW_STATES_REACHED = "max new state count exceeded";
	private static String MAX_INSTRUCTIONS_REACHED = "max instruction count exceeded";
	private static int ONE_HUNDERED_INSTRUCTIONS = 100;
	private static int TEN_THOUSAND_INSTRUCTIONS = 1675;
	private static int TWENTY_THOUSAND_INSTRUCTIONS = 4175;
	
	public static void main(String[] methods) {
		runTestsOfThisClass(methods);
	}
	
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
	 * Tests that no max state violation occurs
	 */
	@Test
	public void testMaxStateNoViolation() {
		PROPERTIES[4] = "+budget.max_state=4";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			makeStates(4);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_STATES_REACHED));
		}
	}
	
	/**
	 * Tests that a max state violation occurs
	 */
	@Test
	public void testMaxStateViolation() {
		PROPERTIES[4] = "+budget.max_state=3";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			makeStates(4);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was not violated", stream.toString().contains(MAX_STATES_REACHED));
		}
	}
	
	/**
	 * Tests that no max state violation occurs
	 */
	@Test
	public void testMaxNewStateNoViolation() {
		PROPERTIES[4] = "+budget.max_new_states=4";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			makeStates(4);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_NEW_STATES_REACHED));
		}
	}
	
	/**
	 * Tests that a max state violation occurs
	 */
	@Test
	public void testMaxNewStateViolation() {
		PROPERTIES[4] = "+budget.max_new_states=3";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			makeStates(4);
		} else {
			System.setOut(out);
			System.out.println(stream.toString());
			TestJPF.assertTrue("Property was not violated", stream.toString().contains(MAX_NEW_STATES_REACHED));
		}
	}
	
	/**
	 * Tests that no max instruction violation occurs
	 */
	@Test
	public void testMaxInstructionNoViolation() {
		PROPERTIES[4] = "+budget.max_insn=9999";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			for (int i = 0; i < TEN_THOUSAND_INSTRUCTIONS; i++);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_INSTRUCTIONS_REACHED));
		}
	}
	
	/**
	 * Tests that a max instruction violation occurs
	 */
	@Test
	public void testMaxInstructionViolation() {
		PROPERTIES[4] = "+budget.max_insn=9999";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			for (int i = 0; i <= TEN_THOUSAND_INSTRUCTIONS; i++);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", stream.toString().contains(MAX_INSTRUCTIONS_REACHED));
		}
	}
	
	/**
	 * Tests that no max instruction violation occurs
	 */
	@Test
	public void testMaxInstructionNoViolation2() {
		PROPERTIES[4] = "+budget.max_insn=15000";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			for (int i = 0; i < TWENTY_THOUSAND_INSTRUCTIONS; i++);
		} else {
			System.setOut(out);
			System.out.println(stream.toString());
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_INSTRUCTIONS_REACHED));
		}
	}
	
	/**
	 * Tests that a max instruction violation occurs at 20,000 when max_insn is 15,000 
	 * since the default value of check interval is 10,000 
	 */
	@Test
	public void testMaxInstructionViolation2() {
		PROPERTIES[4] = "+budget.max_insn=15000";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			for (int i = 0; i <= TWENTY_THOUSAND_INSTRUCTIONS; i++);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", stream.toString().contains(MAX_INSTRUCTIONS_REACHED));
		}
	}
	
	/**
	 * Tests that check_interval changes how frequently the check within instructionExcecuted occurs.
	 * Should not have violation since check interval is its default of 10,000;
	 */
	@Test
	public void testCheckIntervalNoViolation() {
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
			for (int i = 0; i <= ONE_HUNDERED_INSTRUCTIONS; i++);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(MAX_INSTRUCTIONS_REACHED));
		}
	}
	
	/**
	 * Tests that check_interval changes how frequently the check within instructionExcecuted occurs.
	 * Should have violation since check interval is low;
	 */
	@Test
	public void testCheckIntervalViolation() {
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
			for (int i = 0; i <= ONE_HUNDERED_INSTRUCTIONS; i++);
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", stream.toString().contains(MAX_INSTRUCTIONS_REACHED));
		}
	}
	
	
}

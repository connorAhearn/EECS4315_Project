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
										   "" };
	private static String MAX_STATES_REACHED = "max states exceeded";
	
	public static void main(String[] methods) {
		runTestsOfThisClass(methods);
	}
	
	/**
	 * Tests that no max state violation occurs
	 */
	@Test
	public void testMaxStateNoViolation() {
		PROPERTIES[4] = "+budget.max_state=3";
		
		PrintStream out = null;
		ByteArrayOutputStream stream = null;
		
		if (!TestJPF.isJPFRun()) {
			out = System.out;
			stream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stream));
		}
		
		if (this.verifyNoPropertyViolation(PROPERTIES)) {
			Random random = new Random();
			int a = 0;
			if (random.nextBoolean()) {
				a = 1;
			} else {
				a = 2;
			}
		} else {
			System.setOut(out);
			TestJPF.assertTrue("Property was violated", !stream.toString().contains(BudgetCheckerTest.MAX_STATES_REACHED));
		}
	}
	
	/**
	 * Tests that a max state violation occurs
	 */
	@Test
	public void testMaxStateViolation() {
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
			TestJPF.assertTrue("Property was not violated", stream.toString().contains(BudgetCheckerTest.MAX_STATES_REACHED));
		}
	}
}

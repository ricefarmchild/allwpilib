/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008-2014. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.wpi.first.wpilibj.fixtures.AnalogCrossConnectFixture;
import edu.wpi.first.wpilibj.test.AbstractComsSetup;
import edu.wpi.first.wpilibj.test.TestBench;

/**
 * @author jonathanleitschuh
 *
 */
public class AnalogCrossConnectTest extends AbstractComsSetup {
	private static final Logger logger = Logger.getLogger(AnalogCrossConnectTest.class.getName());

	private static AnalogCrossConnectFixture analogIO;

	static final double kDelayTime = 0.01;

	@Override
	protected Logger getClassLogger() {
		return logger;
	}


	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		analogIO = TestBench.getAnalogCrossConnectFixture();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		analogIO.teardown();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		analogIO.setup();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAnalogOuput() {
		for(int i = 0; i < 50; i++) {
			analogIO.getOutput().setVoltage(i / 10.0f);
			Timer.delay(kDelayTime);
			assertEquals(analogIO.getOutput().getVoltage(), analogIO.getInput().getVoltage(), 0.01);
		}
	}

	@Test
	public void testAnalogTriggerBelowWindow() {
		// Given
		AnalogTrigger trigger = new AnalogTrigger(analogIO.getInput());
		trigger.setLimitsVoltage(2.0f, 3.0f);

		// When the output voltage is than less the lower limit
		analogIO.getOutput().setVoltage(1.0f);
		Timer.delay(kDelayTime);

		// Then the analog trigger is not in the window and the trigger state is off
		assertFalse("Analog trigger is in the window (2V, 3V)", trigger.getInWindow());
		assertFalse("Analog trigger is on", trigger.getTriggerState());

		trigger.free();
	}

	@Test
	public void testAnalogTriggerInWindow() {
		// Given
		AnalogTrigger trigger = new AnalogTrigger(analogIO.getInput());
		trigger.setLimitsVoltage(2.0f, 3.0f);

		// When the output voltage is within the lower and upper limits
		analogIO.getOutput().setVoltage(2.5f);
		Timer.delay(kDelayTime);

		// Then the analog trigger is in the window and the trigger state is off
		assertTrue("Analog trigger is not in the window (2V, 3V)", trigger.getInWindow());
		assertFalse("Analog trigger is on", trigger.getTriggerState());

		trigger.free();
	}

	@Test
	public void testAnalogTriggerAboveWindow() {
		// Given
		AnalogTrigger trigger = new AnalogTrigger(analogIO.getInput());
		trigger.setLimitsVoltage(2.0f, 3.0f);

		// When the output voltage is greater than the upper limit
		analogIO.getOutput().setVoltage(4.0f);
		Timer.delay(kDelayTime);

		// Then the analog trigger is not in the window and the trigger state is on
		assertFalse("Analog trigger is in the window (2V, 3V)", trigger.getInWindow());
		assertTrue("Analog trigger is not on", trigger.getTriggerState());

		trigger.free();
	}

	@Test
	public void testAnalogTriggerCounter() {
		// Given
		AnalogTrigger trigger = new AnalogTrigger(analogIO.getInput());
		trigger.setLimitsVoltage(2.0f, 3.0f);

		Counter counter = new Counter(trigger);

		// When the analog output is turned low and high 50 times
		for(int i = 0; i < 50; i++) {
			analogIO.getOutput().setVoltage(1.0);
			Timer.delay(kDelayTime);
			analogIO.getOutput().setVoltage(4.0);
			Timer.delay(kDelayTime);
		}

		// Then the counter should be at 50
		assertEquals("Analog trigger counter did not count 50 ticks", 50, counter.get());
	}

	@Test(expected=RuntimeException.class)
	public void testRuntimeExceptionOnInvalidAccumulatorPort(){
		analogIO.getInput().getAccumulatorCount();
	}
}

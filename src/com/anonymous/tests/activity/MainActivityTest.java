package com.anonymous.tests.activity;

import com.anonymous.solar.android.*;

import com.anonymous.tests.runner.SampleTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(SampleTestRunner.class)
public class MainActivityTest {

	/**
	 * Basic test to ensure the testing harness works.
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldHaveHappySmiles() throws Exception {
		String hello = new MainActivity().getString(R.string.app_name);
		assertTrue("Main Activity Name", hello.compareTo("Solar Calculator for Android") == 0);
	}
}

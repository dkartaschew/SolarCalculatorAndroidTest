package com.anonymous.tests.activity;

import com.anonymous.solar.android.*;

import com.anonymous.tests.runner.SampleTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SampleTestRunner.class)
public class MainActivityTest {
	
	/**
	 * Basic test to ensure the testing harness works.
	 * @throws Exception
	 */
	@Test
    public void shouldHaveHappySmiles() throws Exception {
        String hello = new MainActivity().getResources().getString(R.string.app_name);
        assertThat(hello, equalTo("Solar Calculator for Android"));
    }
	
}

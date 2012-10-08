package com.anonymous.tests.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.anonymous.solar.android.*;

import com.anonymous.tests.runner.SampleTestRunner;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(SampleTestRunner.class)
public class MainActivityTest {
	
	private MainActivity main;
	private Button next;
	private Button back;
	
	@Before
	public void setUp() throws Exception {
		main = new MainActivity();
		main.onCreate(null);
		next = (Button) main.findViewById(R.id.buttonNext);
		back = (Button) main.findViewById(R.id.buttonBack);
	}

	/**
	 * Basic test to ensure the testing harness works.
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldHaveHappySmiles() throws Exception {
		String hello = main.getString(R.string.app_name);
		assertTrue("Main Activity Name", hello.compareTo("Solar Calculator for Android") == 0);
	}
	
	/**
	 * Test Basic navigation.
	 */
	@Test 
	public void basicNavigation() throws Exception {
		next.performClick();	// Move to setup description;
		View wizard = main.getWizard().getCurrentView();
		TextView title = (TextView) wizard.findViewById(R.id.textViewSetupTitle);
		assertTrue("On Setup Page", title != null);
		
		back.performClick();	// Move back to main page
		wizard = main.getWizard().getCurrentView();
		title = (TextView) wizard.findViewById(R.id.textViewTitle);
		assertTrue("On Setup Page", title != null);
	}
	
	/**
	 * Basic test of setup description page.
	 * @throws Exception
	 */
	@Test 
	public void setupDescriptionPage() throws Exception {
		next.performClick();	// Move to setup description;
		EditText setupName = (EditText) main.findViewById(R.id.editTextSetupName);
		setupName.setText("Dummy");
		next.performClick();
		View wizard = main.getWizard().getCurrentView();
		TextView title = (TextView) wizard.findViewById(R.id.textViewLocationTitle);
		assertTrue("On Location Page", title != null);
	}
}

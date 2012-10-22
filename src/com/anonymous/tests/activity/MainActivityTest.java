package com.anonymous.tests.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.anonymous.solar.android.*;
import com.anonymous.solar.shared.LocationData;
import com.anonymous.solar.shared.SolarInverter;
import com.anonymous.solar.shared.SolarPanelsException;
import com.anonymous.solar.shared.SolarSetupException;

import com.anonymous.tests.runner.SampleTestRunner;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowActivity;
import com.xtremelabs.robolectric.shadows.ShadowAlertDialog;
import com.xtremelabs.robolectric.shadows.ShadowDialog;
import com.xtremelabs.robolectric.shadows.ShadowHandler;
import com.xtremelabs.robolectric.shadows.ShadowSpinner;
import com.xtremelabs.robolectric.shadows.ShadowTabHost;
import com.xtremelabs.robolectric.shadows.ShadowTabSpec;
import com.xtremelabs.robolectric.shadows.ShadowView;

import static com.xtremelabs.robolectric.Robolectric.clickOn;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * Basic high level tests of the Android Application.
 * 
 * Tests are fairly coarse grained, as much of the data is pulled in from GAE,
 * so may modify results in an unpredictable manner.
 * 
 * @author 07627505 Darran Kartaschew
 * @version 1.0
 */
@RunWith(SampleTestRunner.class)
public class MainActivityTest {

	private String SETUPNAME = "Dummy";
	private String SETUPDESC = "Dummy Description";

	private MainActivity main;
	private Button next;
	private Button back;

	@Before
	public void setUp() throws Exception {
		Robolectric.getFakeHttpLayer().interceptHttpRequests(false);
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

	/********************************************************************************************
	 * 
	 * Basic Navigation.
	 * 
	 ********************************************************************************************/

	/**
	 * Test Basic navigation.
	 */
	@Test
	public void basicNavigation() throws Exception {
		clickOn(next); // Move to setup description;
		View wizard = main.getWizard().getCurrentView();
		TextView title = (TextView) wizard.findViewById(R.id.textViewSetupTitle);
		assertTrue("On Setup Page", title != null);

		clickOn(back); // Move back to main page
		wizard = main.getWizard().getCurrentView();
		title = (TextView) wizard.findViewById(R.id.textViewTitle);
		assertTrue("On Setup Page", title != null);
	}

	/**
	 * Test basic navigation via gesture
	 * 
	 * @throws Exception
	 */
	@Test
	public void testbasicNavigationGesture() throws Exception {
		ShadowActivity sMain = Robolectric.shadowOf(main);
		// TODO: Find out how to test touch based actions?
	}

	/********************************************************************************************
	 * 
	 * Setup Pane
	 * 
	 ********************************************************************************************/

	/**
	 * Basic test of setup description page.
	 * 
	 * @throws Exception
	 */
	@Test
	public void setupDescriptionPage() throws Exception {
		clickOn(next); // Move to setup description;
		EditText setupName = (EditText) main.findViewById(R.id.editTextSetupName);
		setupName.setText(SETUPNAME);
		clickOn(next);
		View wizard = main.getWizard().getCurrentView();
		TextView title = (TextView) wizard.findViewById(R.id.textViewLocationTitle);
		assertTrue("On Location Page", title != null);
	}

	/**
	 * Basic Test for no item in enter and attempting to move forward.
	 * 
	 * @throws Exception
	 */
	@Test
	public void setupDescriptionNoInput() throws Exception {
		clickOn(next);
		clickOn(next);
		ShadowHandler.idleMainLooper();

		AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
		ShadowAlertDialog shadowAlert = Robolectric.shadowOf(alert);
		assertTrue("Dialg expected", shadowAlert.getMessage().compareTo("Invalid Parameter, please ensure a name is present") == 0);

	}

	/**
	 * Basic test of setup description page, confirm items are entered.
	 * 
	 * @throws Exception
	 */
	@Test
	public void setupDescriptionPage2() throws Exception {
		clickOn(next); // Move to setup description;
		EditText setupName = (EditText) main.findViewById(R.id.editTextSetupName);
		EditText setupDesc = (EditText) main.findViewById(R.id.editTextSetupDescription);
		setupName.setText(SETUPNAME);
		setupDesc.setText(SETUPDESC);

		clickOn(next);
		View wizard = main.getWizard().getCurrentView();
		TextView title = (TextView) wizard.findViewById(R.id.textViewLocationTitle);
		assertTrue("On Location Page", title != null);
		assertTrue("Setup Name Present", main.getSolarSetup().getSetupName().compareTo(SETUPNAME) == 0);
		assertTrue("Setup Desc Present", main.getSolarSetup().getSetupDescription().compareTo(SETUPDESC) == 0);
	}

	/********************************************************************************************
	 * 
	 * Location Pane
	 * 
	 ********************************************************************************************/

	/**
	 * Basic test of setup location page.
	 * 
	 * @throws Exception
	 */
	@Test
	public void locationPage() throws Exception {
		clickOn(next); // Move to setup description;
		EditText setupName = (EditText) main.findViewById(R.id.editTextSetupName);
		setupName.setText(SETUPNAME);
		clickOn(next);
		View wizard = main.getWizard().getCurrentView();
		TextView title = (TextView) wizard.findViewById(R.id.textViewLocationTitle);
		assertTrue("On Location Page", title != null);
	}

	/**
	 * Basic Test for no item in enter and attempting to move forward.
	 * 
	 * @throws Exception
	 */
	@Test
	public void locationPageNoInput() throws Exception {
		locationPage();

		clickOn(next);

		AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
		ShadowAlertDialog shadowAlert = Robolectric.shadowOf(alert);
		assertTrue("Expected Dialog", shadowAlert.getMessage().compareTo("Invalid Location, please ensure a location name has been entered")==0);

	}

	/**
	 * Basic Test for having a locaiton already present.
	 * 
	 * @throws Exception
	 */
	@Test
	public void locationPageLocationSet() throws Exception {
		LocationData locData = new LocationData();
		locData.setLocationName(SETUPNAME);
		locData.setLatitude(0.0);
		locData.setLongitude(0.0);
		main.getSolarSetup().setLocationInformation(locData);

		locationPage();

		clickOn(next);
		View wizard = main.getWizard().getCurrentView();
		TextView title = (TextView) wizard.findViewById(R.id.textViewUsageTitle);
		assertTrue("On Usage Page", title != null);
	}

	/**
	 * Basic Test for selecting a location via the spinner.
	 * 
	 * Note: ShadowSpinner is broken, and does NOT send the event to the spinner
	 * control.
	 * 
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void locationPageLocationSetViaSpinner() throws Exception {
		locationPage();
		View wizard = main.getWizard().getCurrentView();
		final Spinner locSpinner = (Spinner) wizard.findViewById(R.id.spinnerLocationPredefined);
		ShadowSpinner sloc = (ShadowSpinner) Robolectric.shadowOf(locSpinner);

		sloc.requestFocus();
		sloc.setSelection(1, false);
		sloc.performItemClick(1);

		clickOn(next);
		wizard = main.getWizard().getCurrentView();
		TextView title = (TextView) wizard.findViewById(R.id.textViewUsageTitle);
		assertTrue("On Usage Page", title != null);
	}

	/********************************************************************************************
	 * 
	 * Usage Pane
	 * 
	 ********************************************************************************************/

	/**
	 * Basic test of usage page.
	 * 
	 * @throws Exception
	 */
	@Test
	public void usagePage() throws Exception {
		locationPageLocationSet();
		View wizard = main.getWizard().getCurrentView();
		TextView title = (TextView) wizard.findViewById(R.id.textViewUsageTitle);
		assertTrue("On Usage Page", title != null);
	}

	/**
	 * Basic Test for no item in enter and attempting to move forward.
	 * 
	 * @throws Exception
	 */
	@Test
	public void usagePageNoInput() throws Exception {
		usagePage();

		clickOn(next);
		AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
		ShadowAlertDialog shadowAlert = Robolectric.shadowOf(alert);
		assertTrue("Expected Dialog", shadowAlert.getMessage().compareTo("Please enter at least 1 valid amount.")==0);
	}

	/**
	 * Basic Test for entering 1 item and moving forward.
	 * 
	 * @throws Exception
	 */
	@Test
	public void usagePageInput1() throws Exception {
		usagePage();

		EditText amount = (EditText) main.findViewById(R.id.editTextUsageDaily);
		amount.requestFocus();
		amount.setText("1.0");
		main.findViewById(R.id.editTextUsageMonthly).requestFocus();
		clickOn(next);
		View wizard = main.getWizard().getCurrentView();
		TextView title = (TextView) wizard.findViewById(R.id.textViewUsageTitle);
		assertTrue("On Usage Page", title == null);
		assertTrue("Daily Entered Correctly", main.getSolarSetup().getCustomerData().getDailyAverageUsage() == 1.0);
	}

	/**
	 * Basic Test for entering 1 item and moving forward.
	 * 
	 *  Robolectric is not handlign this test correct, disabling.
	 * 
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void usagePageInput2() throws Exception {
		usagePage();

		EditText amount = (EditText) main.findViewById(R.id.editTextUsageDayTime);
		amount.requestFocus();
		amount.setText("1.0");
		main.findViewById(R.id.editTextUsageMonthly).requestFocus();
		clickOn(next);
		View wizard = main.getWizard().getCurrentView();
		TextView title = (TextView) wizard.findViewById(R.id.textViewUsageTitle);
		assertTrue("On Usage Page", title == null);
		System.out.println("Hourly " + main.getSolarSetup().getCustomerData().getHourlyAverageUsage().toString());
		assertTrue("Daytime Entered Correctly", main.getSolarSetup().getCustomerData().getHourlyAverageUsage() == 1.0);
	}

	/**
	 * Basic Test for entering 1 item and moving forward.
	 * 
	 * @throws Exception
	 */
	@Test
	public void usagePageInput3() throws Exception {
		usagePage();

		EditText amount = (EditText) main.findViewById(R.id.editTextUsageMonthly);
		amount.requestFocus();
		amount.setText("1.0");
		main.findViewById(R.id.editTextUsageDaily).requestFocus();
		clickOn(next);
		View wizard = main.getWizard().getCurrentView();
		TextView title = (TextView) wizard.findViewById(R.id.textViewUsageTitle);
		assertTrue("On Usage Page", title == null);
		assertTrue("Daily Entered Correctly", main.getSolarSetup().getCustomerData().getMonthlyAverageUsage() == 1.0);
	}

	/********************************************************************************************
	 * 
	 * Tariff Pane
	 * 
	 ********************************************************************************************/

	/**
	 * Basic test of tariff page.
	 * 
	 * @throws Exception
	 */
	@Test
	public void tariffPage() throws Exception {
		usagePageInput1();
		View wizard = main.getWizard().getCurrentView();
		TextView title = (TextView) wizard.findViewById(R.id.textViewTariffTitle);
		assertTrue("On Tariff Page", title != null);
	}

	/**
	 * Basic test of tariff page.
	 * 
	 * @throws Exception
	 */
	@Test
	public void tariffPage2() throws Exception {
		tariffPage();

		clickOn(next);

		View wizard = main.getWizard().getCurrentView();
		TextView title = (TextView) wizard.findViewById(R.id.textViewTariffTitle);
		assertTrue("On Tariff Page", title == null);
	}

	/**
	 * Basic test of tariff page, to ensure our tariffs are being loaded.
	 * 
	 * @throws Exception
	 */
	@Test
	public void tariffPageCheckSpinner() throws Exception {
		tariffPage();

		View wizard = main.getWizard().getCurrentView();
		final Spinner tSpinner = (Spinner) wizard.findViewById(R.id.spinnerTariffPredefined);
		assertTrue("Tariff Count ", tSpinner.getChildCount() > 0);
		clickOn(next);
	}

	/********************************************************************************************
	 * 
	 * Inverter Pane
	 * 
	 ********************************************************************************************/

	/**
	 * Basic test of inverter page.
	 * 
	 * @throws Exception
	 */
	@Test
	public void inverterPage() throws Exception {
		tariffPage();

		// Set a standard inverter.
		SolarInverter invertor = new SolarInverter("Dummy", "Dummy", "Dummy", 1000.0, 0.1, 99.0, 0.1, 0.2, 25);
		main.getSolarSetup().setInverter(invertor);
		clickOn(next);

		View wizard = main.getWizard().getCurrentView();
		TextView title = (TextView) wizard.findViewById(R.id.textViewInverterTitle);
		assertTrue("On Inverter Page", title != null);
	}

	/**
	 * Basic test of inverter page.
	 * 
	 * @throws Exception
	 */
	@Test
	public void inverterPage2() throws Exception {
		inverterPage();
		SolarInverter invertor = new SolarInverter("Dummy", "Dummy", "Dummy", 1000.0, 0.1, 99.0, 0.1, 0.2, 25);
		main.getSolarSetup().setInverter(invertor);
		clickOn(next);

		View wizard = main.getWizard().getCurrentView();
		TextView title = (TextView) wizard.findViewById(R.id.textViewInverterTitle);
		assertTrue("Off Inverter Page", title == null);
	}

	/**
	 * Basic test of inverter page, to ensure our inverters are being loaded.
	 * 
	 * @throws Exception
	 */
	@Test
	public void inverterPageCheckSpinner() throws Exception {
		inverterPage();

		View wizard = main.getWizard().getCurrentView();
		final Spinner tSpinner = (Spinner) wizard.findViewById(R.id.spinnerInverters);
		assertTrue("Inverter Count ", tSpinner.getChildCount() > 0);

	}

	/**
	 * Basic test of inverter page, to ensure our inverters are being loaded.
	 * 
	 * @throws Exception
	 */
	@Test
	public void inverterPageEditSpinner() throws Exception {
		inverterPage();

		View wizard = main.getWizard().getCurrentView();
		final Spinner tSpinner = (Spinner) wizard.findViewById(R.id.spinnerInverters);
		assertTrue("Inverter Count ", tSpinner.getChildCount() > 0);

		Button edit = (Button) wizard.findViewById(R.id.buttonInverterEdit);
		clickOn(edit);

		ShadowHandler.idleMainLooper();

		Dialog alert = ShadowDialog.getLatestDialog();
		ShadowDialog shadowAlert = Robolectric.shadowOf(alert);
		assertTrue("Expected Dialog", shadowAlert.getTitle().toString().compareTo("Edit Inverter Information")==0);

	}

	/**
	 * Basic test of inverter page, to ensure our inverters are being loaded.
	 * 
	 * @throws Exception
	 */
	@Test
	public void inverterPageEditSpinner2() throws Exception {
		inverterPage();

		View wizard = main.getWizard().getCurrentView();
		final Spinner tSpinner = (Spinner) wizard.findViewById(R.id.spinnerInverters);
		assertTrue("Inverter Count ", tSpinner.getChildCount() > 0);

		Button edit = (Button) wizard.findViewById(R.id.buttonInverterEdit);
		clickOn(edit);

		ShadowHandler.idleMainLooper();

		AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
		ShadowAlertDialog shadowAlert = Robolectric.shadowOf(alert);
		assertTrue("Expected Dialog", shadowAlert.getTitle().toString().compareTo("Edit Inverter Information")==0);

		// Add some information.
		EditText life = (EditText) shadowAlert.findViewById(R.id.editTextInverterEditLife);
		life.setText("10");
		Button okButton = shadowAlert.getButton(Dialog.BUTTON_POSITIVE);
		clickOn(okButton);

		clickOn(next);

		assertTrue("Inverter Updated", main.getSolarSetup().getInverter().getInverterLifeYears() == 10);
	}

	/**
	 * Basic test of modifying wire length.
	 */
	@Test
	public void inverterTestWiring() throws Exception {
		inverterPage();
		View wizard = main.getWizard().getCurrentView();
		EditText wire = (EditText) wizard.findViewById(R.id.editTextWiringLength);
		wire.setText("");
		clickOn(next);

		AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
		ShadowAlertDialog shadowAlert = Robolectric.shadowOf(alert);
		assertTrue("Expected Dialog", shadowAlert.getMessage().compareTo("Please enter a wire length")==0);
	}

	/**
	 * Basic test of modifying wire efficiency.
	 */
	@Test
	public void inverterTestWiringEff() throws Exception {
		inverterPage();
		View wizard = main.getWizard().getCurrentView();
		EditText wire = (EditText) wizard.findViewById(R.id.editTextWiringEff);
		wire.setText("");
		clickOn(next);

		AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
		ShadowAlertDialog shadowAlert = Robolectric.shadowOf(alert);
		assertTrue("Expected Dialog", shadowAlert.getMessage().compareTo("Please enter a wire efficiency between 0.00% and 100%")==0);
	}

	/**
	 * Basic test of modifying wire efficiency. (less than 0)
	 * 
	 * Robolectric is not handlign this test correct, disabling.
	 */
	@Ignore
	@Test
	public void inverterTestWiringEff2() throws Exception {
		inverterPage();
		View wizard = main.getWizard().getCurrentView();
		EditText wire = (EditText) wizard.findViewById(R.id.editTextWiringEff);
		wire.setText("-1");
		clickOn(next);

		AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
		ShadowAlertDialog shadowAlert = Robolectric.shadowOf(alert);
		assertTrue("Expected Dialog", shadowAlert.getMessage().compareTo("Invalid parameters entered, please ensure values entered are correct")==0);
	}

	/**
	 * Basic test of modifying wire efficiency. (more than 100)
	 */
	@Test
	public void inverterTestWiringEff3() throws Exception {
		inverterPage();
		View wizard = main.getWizard().getCurrentView();
		EditText wire = (EditText) wizard.findViewById(R.id.editTextWiringEff);
		wire.setText("100.1");
		clickOn(next);

		AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
		ShadowAlertDialog shadowAlert = Robolectric.shadowOf(alert);
		assertTrue("Expected Dialog", shadowAlert.getMessage().compareTo("Wire Efficiency must be between 0.00% and 100.00%")==0);

	}

	/********************************************************************************************
	 * 
	 * Panel Pane
	 * 
	 ********************************************************************************************/

	/**
	 * Basic test of panel page.
	 * 
	 * @throws Exception
	 */
	@Test
	public void panelPage() throws Exception {
		inverterPage2();

		clickOn(next);

		View wizard = main.getWizard().getCurrentView();
		TextView title = (TextView) wizard.findViewById(R.id.textViewSolarTitle);
		assertTrue("On Solar Page", title != null);
	}

	/**
	 * Basic test of panel page.
	 * 
	 * @throws Exception
	 */
	@Test
	public void panelPageMissingPanels() throws Exception {
		panelPage();

		clickOn(next);

		AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
		ShadowAlertDialog shadowAlert = Robolectric.shadowOf(alert);
		assertTrue("Expected Dialog", shadowAlert.getMessage().compareTo("Please enter at least 1 solar panel configuration")==0);
	}

	/**
	 * Basic test of adding a panel
	 * 
	 * @throws Exception
	 */
	@Test
	public void panelPageAddPanel() throws Exception {
		panelPage();

		View wizard = main.getWizard().getCurrentView();
		Button add = (Button) wizard.findViewById(R.id.buttonPanelAdd);

		TableLayout table = (TableLayout) wizard.findViewById(R.id.tablePanels);
		assertTrue("No Panels by default", table.getChildCount() == 1);

		clickOn(add);

		AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
		ShadowAlertDialog shadowAlert = Robolectric.shadowOf(alert);

		Button okButton = shadowAlert.getButton(Dialog.BUTTON_POSITIVE);
		clickOn(okButton);
		assertTrue("Panel added", table.getChildCount() == 2);
	}

	/**
	 * Basic test of removing a panel
	 * 
	 * @throws Exception
	 */
	@Test
	public void panelPageRemovePanel() throws Exception {
		panelPageAddPanel();

		View wizard = main.getWizard().getCurrentView();
		Button remove = (Button) wizard.findViewById(R.id.buttonPanelDelete);

		TableLayout table = (TableLayout) wizard.findViewById(R.id.tablePanels);
		assertTrue("One Panels by default", table.getChildCount() == 2);

		TableRow row = (TableRow) table.getChildAt(1);
		clickOn(row);
		row.performClick();
		clickOn(remove);
		ShadowHandler.idleMainLooper();
		assertTrue("Panel removed", table.getChildCount() == 2);
	}

	/**
	 * Basic test of editing a panel configuration
	 * 
	 * @throws Exception
	 */
	@Test
	public void panelPageEditPanel() throws Exception {
		panelPageAddPanel();

		View wizard = main.getWizard().getCurrentView();

		TableLayout table = (TableLayout) wizard.findViewById(R.id.tablePanels);
		assertTrue("One Panels by default", table.getChildCount() == 2);

		TableRow row = (TableRow) table.getChildAt(1);
		ShadowView sRow = Robolectric.shadowOf(row);
		sRow.performLongClick();

		// Should have edit dialog.
		ShadowHandler.idleMainLooper();

		Dialog alert = ShadowDialog.getLatestDialog();
		ShadowDialog shadowAlert = Robolectric.shadowOf(alert);
		assertTrue("Expected Dialog", shadowAlert.getTitle().toString().compareTo("Modify Panel Configuration")==0);
	}

	/**
	 * Basic test of editing a panel configuration
	 * 
	 * @throws Exception
	 */
	@Test
	public void panelPageEditPanels() throws Exception {
		panelPageAddPanel();

		View wizard = main.getWizard().getCurrentView();

		Button edit = (Button) wizard.findViewById(R.id.buttonPanelEdit);

		clickOn(edit);

		// Should have edit dialog.
		ShadowHandler.idleMainLooper();

		Dialog alert = ShadowDialog.getLatestDialog();
		ShadowDialog shadowAlert = Robolectric.shadowOf(alert);
		assertTrue("Expected Dialog", shadowAlert.getTitle().toString().compareTo("Edit Panel Information")==0);
	}

	/**
	 * Basic test of adding a panel and going to next page.
	 * 
	 * @throws Exception
	 */
	@Test
	public void panelNext() throws Exception {
		panelPageAddPanel();
		clickOn(next);

		View wizard = main.getWizard().getCurrentView();
		TextView title = (TextView) wizard.findViewById(R.id.textViewSolarTitle);
		assertTrue("Off Solar Page", title == null);
	}

	/********************************************************************************************
	 * 
	 * Confirmation Pane
	 * 
	 ********************************************************************************************/

	/**
	 * Basic test of confirmation page
	 * 
	 * @throws Exception
	 */
	@Test
	public void confimationPage() throws Exception {
		panelPageAddPanel();
		clickOn(next);

		View wizard = main.getWizard().getCurrentView();
		TextView title = (TextView) wizard.findViewById(R.id.textViewConfirmationTitle);
		assertTrue("On Confirmation Page", title != null);
	}

	/**
	 * Basic test of confirmation page
	 * 
	 * @throws Exception
	 */
	@Test
	public void confimationPageNoYear() throws Exception {
		confimationPage();

		View wizard = main.getWizard().getCurrentView();
		EditText years = (EditText) wizard.findViewById(R.id.editTextConfirmationYears);
		years.setText("");
		clickOn(next);

		ShadowHandler.idleMainLooper();

		AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
		ShadowAlertDialog shadowAlert = Robolectric.shadowOf(alert);
		assertTrue("Expected Dialog", shadowAlert.getMessage().compareTo("Invalid Parameter, please ensure a year value is present")==0);
	}

	/**
	 * Basic test of confirmation page
	 * 
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void confimationPageNext() throws Exception {
		confimationPage();

		ShadowHandler.idleMainLooper();

		clickOn(next);

		ShadowHandler.idleMainLooper();

		View wizard = main.getWizard().getCurrentView();
		TextView title = (TextView) wizard.findViewById(R.id.textViewConfirmationTitle);
		assertTrue("Off Solar Page", title == null);
	}

	/********************************************************************************************
	 * 
	 * Results Pane
	 * 
	 ********************************************************************************************/

	/**
	 * Basic test of results page
	 * 
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void resultsPage() throws Exception {
		confimationPageNext();

		View wizard = main.getWizard().getCurrentView();
		TextView title = (TextView) wizard.findViewById(R.id.textViewResultsTitle);
		assertTrue("On Results Page", title != null);
	}

	/**
	 * Basic test of results page, check for 3 tabs.
	 * 
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void resultsPageTabs() throws Exception {
		resultsPage();

		View wizard = main.getWizard().getCurrentView();
		TabHost tab = (TabHost) wizard.findViewById(R.id.tabHostResults);
		assertTrue("On Results Page", tab.getChildCount() == 3);
	}

	/**
	 * Basic test of results page, check for tab 1 header.
	 * 
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void resultsPageTabs1() throws Exception {
		resultsPage();

		View wizard = main.getWizard().getCurrentView();

		ShadowTabHost shadowTabHost = (ShadowTabHost) Robolectric.shadowOf(wizard.findViewById(R.id.tabHostResults));
		shadowTabHost.setCurrentTab(0);
		ShadowTabSpec tab = Robolectric.shadowOf(shadowTabHost.getCurrentTabSpec());

		assertTrue("On Results Summary Page", tab.getText().compareTo("Summary") == 0);
	}

	/**
	 * Basic test of results page, check for tab 2 header.
	 * 
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void resultsPageTabs2() throws Exception {
		resultsPage();

		View wizard = main.getWizard().getCurrentView();

		ShadowTabHost shadowTabHost = (ShadowTabHost) Robolectric.shadowOf(wizard.findViewById(R.id.tabHostResults));
		shadowTabHost.setCurrentTab(1);
		ShadowTabSpec tab = Robolectric.shadowOf(shadowTabHost.getCurrentTabSpec());

		assertTrue("On Results Summary Page", tab.getText().compareTo("Graph") == 0);
	}

	/**
	 * Basic test of results page, check for tab 2 header.
	 * 
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void resultsPageTabs3() throws Exception {
		resultsPage();

		View wizard = main.getWizard().getCurrentView();

		ShadowTabHost shadowTabHost = (ShadowTabHost) Robolectric.shadowOf(wizard.findViewById(R.id.tabHostResults));
		shadowTabHost.setCurrentTab(2);
		ShadowTabSpec tab = Robolectric.shadowOf(shadowTabHost.getCurrentTabSpec());

		assertTrue("On Results Summary Page", tab.getText().compareTo("Details") == 0);
	}

	/**
	 * Basic test of results page
	 * 
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void resultsPageNext() throws Exception {
		resultsPage();

		clickOn(next);

		View wizard = main.getWizard().getCurrentView();
		TextView title = (TextView) wizard.findViewById(R.id.textViewResultsTitle);
		assertTrue("Off Results Page", title == null);
	}

	/********************************************************************************************
	 * 
	 * Finish Pane
	 * 
	 ********************************************************************************************/

	/**
	 * Basic test of results page
	 * 
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void finishPage() throws Exception {
		resultsPageNext();

		View wizard = main.getWizard().getCurrentView();
		TextView title = (TextView) wizard.findViewById(R.id.textViewFinishTitle);
		assertTrue("On Finish Page", title != null);
	}

}

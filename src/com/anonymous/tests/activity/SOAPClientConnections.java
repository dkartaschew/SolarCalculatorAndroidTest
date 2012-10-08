package com.anonymous.tests.activity;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.anonymous.solar.client.*;
import com.anonymous.solar.shared.*;
import com.anonymous.tests.runner.SampleTestRunner;
import com.xtremelabs.robolectric.Robolectric;

/**
 * Basic high level tests of the SOAP Client services. Does not include the main
 * SolarCalculator test as this is done elsewhere.
 * 
 * @author 07627505 Darran Kartaschew
 * @version 1.0
 */
@RunWith(SampleTestRunner.class)
public class SOAPClientConnections {

	/**
	 * Turn off HTTP interception in library.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		Robolectric.getFakeHttpLayer().interceptHttpRequests(false);
	}

	/**
	 * Test Location Services.
	 */
	@Test
	public void TestLocations() {
		LocationInformationService locService = new LocationInformationService();
		List<LocationData> locData = locService.StoreLocationGetAll();
		assertTrue("Location Data Retrieved", locData != null);
		assertTrue("Location Data Retrieved", locData.size() > 0);
	}

	/**
	 * Test Inverter Services.
	 */
	@Test
	public void TestInvertors() {
		InverterInformationService iService = new InverterInformationService();
		List<SolarInverter> iData = iService.getInverters();
		assertTrue("Inverter Data Retrieved", iData != null);
		assertTrue("Inverter Data Retrieved", iData.size() > 0);
	}

	/**
	 * Test Panel Services.
	 */
	@Test
	public void TestPanels() {
		PanelInformationService iService = new PanelInformationService();
		List<SolarPanel> iData = iService.getPanels();
		assertTrue("Inverter Data Retrieved", iData != null);
		assertTrue("Inverter Data Retrieved", iData.size() > 0);
	}

	/**
	 * Test Tariff Services.
	 */
	@Test
	public void TestTariff() {
		TariffInformationService iService = new TariffInformationService();
		List<TariffRate> iData = iService.getTariffRates();
		assertTrue("Inverter Data Retrieved", iData != null);
		assertTrue("Inverter Data Retrieved", iData.size() > 0);
	}
}

// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.logxtool;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j._1_0.config.Configuration1;
import org.junit.Ignore;
import org.junit.Test;
import org.patrodyne.etl.logxtool.commands.CommandSupport;
import org.patrodyne.etl.logxtool.commands.Log4j1XmlToLog4j2Xml;
import org.patrodyne.etl.logxtool.commands.Log4j1XmlToLogbackXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit tests for LogXTool's Main application.
 *
 * @author Rick O'Sullivan
 */
public class MainTest extends CommandSupport
{
	private static final Logger log = LoggerFactory.getLogger(MainTest.class);
	private static final String TEST_FILENAME_LOG4J1 = "xml/log4j1.xml";
	private static final String TEST_FILENAME_STDOUT = "stdout";

	@Test
	public void testLog4j1Xml() throws JAXBException
	{
		JAXBContext jaxbLog4j1 = JAXBContext.newInstance(getLog4j1Factory().getClass());
		Unmarshaller log4j1Unmarshaller = jaxbLog4j1.createUnmarshaller();
		File fileLog4j1 = new File(TEST_FILENAME_LOG4J1);
		@SuppressWarnings("unchecked")
		JAXBElement<Configuration1> configuration1JAXB = (JAXBElement<Configuration1>) log4j1Unmarshaller.unmarshal(fileLog4j1);
		assertNotNull(configuration1JAXB);
		Configuration1 configuration1 = configuration1JAXB.getValue();
		assertNotNull(configuration1);

		Marshaller log4j1Marshaller = jaxbLog4j1.createMarshaller();
		log4j1Marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		log4j1Marshaller.marshal(configuration1JAXB, System.out);
	}

	@Test
	public void testMigrateLog4j1ToLogback() throws JAXBException
	{
		log.info("Migrate Log4j1ToLogback");
		Properties options = new Properties();
		options.put(Main.KEY_SOURCE, TEST_FILENAME_LOG4J1);
		options.put(Main.KEY_TARGET, TEST_FILENAME_STDOUT);
		Command command = new Log4j1XmlToLogbackXml();
		command.execute(options);
	}

	@Test
	public void testMigrateLog4j1ToLog4j2() throws JAXBException
	{
		log.info("Migrate Log4j1ToLog4j2");
		Properties options = new Properties();
		options.put(Main.KEY_SOURCE, TEST_FILENAME_LOG4J1);
		options.put(Main.KEY_TARGET, TEST_FILENAME_STDOUT);
		Command command = new Log4j1XmlToLog4j2Xml();
		command.execute(options);
	}

	/**
	 * Test method for {@link org.patrodyne.etl.logxtool.Main#validateOptions(java.util.Properties)}.
	 */
	@Test
	@Ignore
	public void testValidateOptions()
	{
		Properties options = new Properties();
		if ( options.isEmpty() )
			fail("Not implemented.");
	}
}

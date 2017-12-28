// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.logxtool.commands;

import java.io.File;
import java.io.Serializable;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apache.logging.log4j._1_0.config.Appender1;
import org.apache.logging.log4j._1_0.config.AppenderRef1;
import org.apache.logging.log4j._1_0.config.Category1;
import org.apache.logging.log4j._1_0.config.Configuration1;
import org.apache.logging.log4j._1_0.config.Logger1;
import org.apache.logging.log4j._1_0.config.Param1;
import org.apache.logging.log4j._1_0.config.Root1;
import org.apache.logging.log4j._2_0.config.Appender2;
import org.apache.logging.log4j._2_0.config.AppenderRef2;
import org.apache.logging.log4j._2_0.config.Appenders2;
import org.apache.logging.log4j._2_0.config.Configuration2;
import org.apache.logging.log4j._2_0.config.Filter2;
import org.apache.logging.log4j._2_0.config.Layout2;
import org.apache.logging.log4j._2_0.config.Logger2;
import org.apache.logging.log4j._2_0.config.Root2;
import org.patrodyne.etl.logxtool.Command;
import org.patrodyne.etl.logxtool.Main;

/**
 * Convert Log4j1 XML to Log4j2 XML.
 * @author Rick O'Sullivan
 */
public class Log4j1XmlToLog4j2Xml extends CommandSupport
	implements Command
{
	private static final QName QNAME_FILE = new QName("File");
	private static final QName QNAME_THRESHOLD = new QName("Threshold");
    private final static QName QNAME_LOGGER2 = new QName("http://logging.apache.org/log4j/2.0/config", "Logger");
    private final static QName QNAME_ROOT2 = new QName("http://logging.apache.org/log4j/2.0/config", "Root");

	private org.apache.logging.log4j._2_0.config.ObjectFactory log4j2Factory;
	public org.apache.logging.log4j._2_0.config.ObjectFactory getLog4j2Factory()
	{
		if ( log4j2Factory == null )
			log4j2Factory = new org.apache.logging.log4j._2_0.config.ObjectFactory();
		return log4j2Factory;
	}

	@Override
	public void execute(Properties options)
		throws JAXBException
	{
		File fileLog4j1 = new File(options.getProperty(Main.KEY_SOURCE));
		JAXBContext jaxbLog4j1 = JAXBContext.newInstance(getLog4j1Factory().getClass());

		Unmarshaller log4j1Unmarshaller = jaxbLog4j1.createUnmarshaller();
		@SuppressWarnings("unchecked")
		JAXBElement<Configuration1> configuration1JAXB = (JAXBElement<Configuration1>) log4j1Unmarshaller.unmarshal(fileLog4j1);
		Configuration1 configuration1 = configuration1JAXB.getValue();

		Configuration2 configuration2 = new Configuration2();
		configuration2.setStrict("true");

		if ( !configuration1.getAppender().isEmpty() )
		{
			configuration2.setAppenders(new Appenders2());
			for ( Appender1 appender1 : configuration1.getAppender() )
			{
				Appender2 appender2 = new Appender2();
				configuration2.getAppenders().getAppender().add(appender2);

				if ( !appender1.getParam().isEmpty() )
				{
					for ( Param1 param : appender1.getParam() )
						appender2.getOtherAttributes().put(new QName(param.getName()), param.getValue());
				}

				setLayout(appender1, appender2);
				setFilter(appender1, appender2);

				if ( "org.apache.log4j.ConsoleAppender".equals(appender1.getClazz()) )
				{
					appender2.setType("Console");
					appender2.setName(appender1.getName());
				}
				else if ( "org.apache.log4j.RollingFileAppender".equals(appender1.getClazz()) )
				{
					appender2.setType("RollingFile");
					appender2.setName(appender1.getName());

					String file = valueOf(appender1.getParam(),"File");
					if ( file != null )
					{
						appender2.setFileName(file);
						appender2.getOtherAttributes().remove(QNAME_FILE);
					}
				}
				else
				{
					String[] tokens = appender1.getClazz().split("\\.");
					String type = tokens[tokens.length-1].replace("Appender", "");
					appender2.setType(type);
					appender2.setName(appender1.getName());
				}
			}
		}

		for ( Serializable categoryOrLogger1 : configuration1.getCategoryOrLogger() )
		{
			if ( categoryOrLogger1 instanceof Category1 )
			{
				Category1 category1 = (Category1) categoryOrLogger1;
				Logger2 logger2 = getLog4j2Factory().createLogger2();
				configuration2.getLoggers().add(logger2);
				logger2.setName(category1.getName());
				logger2.setLevel(category1.getLevel().getValue());
				Boolean additivity = new Boolean(category1.getAdditivity());
				if ( !additivity )
					logger2.setAdditivity(additivity.toString());
				for ( AppenderRef1 appenderRef1 : category1.getAppenderRef() )
				{
					Appender1 ref = (Appender1) appenderRef1.getRef();
					logger2.getAppenderRef().add(new AppenderRef2(ref.getName(),null));
				}			}
			else if ( categoryOrLogger1 instanceof Logger1 )
			{
				Logger1 logger1 = (Logger1) categoryOrLogger1;
				Logger2 logger2 = getLog4j2Factory().createLogger2();
				configuration2.getLoggers().add(new JAXBElement<>(QNAME_LOGGER2, Logger2.class, logger2));
				logger2.setName(logger1.getName());
				logger2.setLevel(logger1.getLevel().getValue());
				Boolean additivity = new Boolean(logger1.getAdditivity());
				if ( !additivity )
					logger2.setAdditivity(additivity.toString());
				for ( AppenderRef1 appenderRef1 : logger1.getAppenderRef() )
				{
					Appender1 ref = (Appender1) appenderRef1.getRef();
					logger2.getAppenderRef().add(new AppenderRef2(ref.getName(),null));
				}
				logger2.setFilters(null);
			}
		}

		Root1 root1 = configuration1.getRoot();
		if ( root1 != null )
		{
			Root2 root2 = getLog4j2Factory().createRoot2();
			configuration2.getLoggers().add(new JAXBElement<>(QNAME_ROOT2, Root2.class, root2));
			root2.setLevel(root1.getLevel().getValue());
			for ( AppenderRef1 appenderRef1 : root1.getAppenderRef() )
			{
				Appender1 ref = (Appender1) appenderRef1.getRef();
				root2.getAppenderRef().add(new AppenderRef2(ref.getName(),null));
			}
		}

		if ( configuration2.getCustomLevels().isEmpty() )
			configuration2.setCustomLevels(null);
		if ( configuration2.getProperties().isEmpty() )
			configuration2.setProperties(null);
		if ( configuration2.getFilters().isEmpty() )
			configuration2.setFilters(null);
		if ( configuration2.getLoggers().isEmpty() )
			configuration2.setLoggers(null);

		JAXBElement<Configuration2> configuration2JAXB = getLog4j2Factory().createConfiguration(configuration2);
		JAXBContext jaxbLog4j2 = JAXBContext.newInstance(getLog4j2Factory().getClass());
		File fileLogback = new File(options.getProperty(Main.KEY_TARGET));
		Marshaller log4j2Marshaller = jaxbLog4j2.createMarshaller();
		log4j2Marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		if ( "stdout".equalsIgnoreCase(fileLogback.getName()) )
			log4j2Marshaller.marshal(configuration2JAXB, System.out);
		else
			log4j2Marshaller.marshal(configuration2JAXB, fileLogback);

		//Unmarshaller log4j2Unmarshaller = jaxbLog4j2.createUnmarshaller();
	}

	private void setLayout(Appender1 appender1, Appender2 appender2)
	{
		if ( appender1.getLayout() != null )
		{
			String pattern = valueOf(appender1.getLayout().getParam(),"ConversionPattern");
			if ( pattern != null )
			{
				Layout2 layout2 = new Layout2();
				layout2.setType("PatternLayout");
				layout2.setPattern(pattern);
				appender2.setLayout(layout2);
			}
		}
	}

	private void setFilter(Appender1 appender1, Appender2 appender2)
	{
		String threshold = valueOf(appender1.getParam(),"Threshold");
		if ( threshold != null )
		{
			Filter2 filter2 = new Filter2();
			filter2.setType("ThresholdFilter");
			filter2.setLevel(threshold);
			appender2.setFilter(filter2);
			appender2.setFilters(null);
			appender2.getOtherAttributes().remove(QNAME_THRESHOLD);
		}
	}
}

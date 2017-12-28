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

import org.apache.logging.log4j._1_0.config.Appender1;
import org.apache.logging.log4j._1_0.config.AppenderRef1;
import org.apache.logging.log4j._1_0.config.Category1;
import org.apache.logging.log4j._1_0.config.Configuration1;
import org.apache.logging.log4j._1_0.config.Logger1;
import org.apache.logging.log4j._1_0.config.Root1;
import org.patrodyne.etl.logxtool.Command;
import org.patrodyne.etl.logxtool.Main;

import logback.qos.ch.xml.ns.logback.Appender;
import logback.qos.ch.xml.ns.logback.AppenderRef;
import logback.qos.ch.xml.ns.logback.Configuration;
import logback.qos.ch.xml.ns.logback.Encoder;
import logback.qos.ch.xml.ns.logback.Filter;
import logback.qos.ch.xml.ns.logback.RollingPolicy;
import logback.qos.ch.xml.ns.logback.Root;

/**
 * Convert Log4j1 XML to Logback XML.
 * @author Rick O'Sullivan
 */
public class Log4j1XmlToLogbackXml extends CommandSupport
	implements Command
{
	private logback.qos.ch.xml.ns.logback.ObjectFactory logbackFactory;
	public logback.qos.ch.xml.ns.logback.ObjectFactory getLogbackFactory()
	{
		if ( logbackFactory == null )
			logbackFactory = new logback.qos.ch.xml.ns.logback.ObjectFactory();
		return logbackFactory;
	}

	@Override
	public void execute(Properties options) throws JAXBException
	{
		File fileLog4j1 = new File(options.getProperty(Main.KEY_SOURCE));
		JAXBContext jaxbLog4j1 = JAXBContext.newInstance(getLog4j1Factory().getClass());

		Unmarshaller log4j1Unmarshaller = jaxbLog4j1.createUnmarshaller();
		@SuppressWarnings("unchecked")
		JAXBElement<Configuration1> configuration1JAXB = (JAXBElement<Configuration1>) log4j1Unmarshaller.unmarshal(fileLog4j1);
		Configuration1 configuration1 = configuration1JAXB.getValue();

		Configuration configuration = new Configuration();
		configuration.setDebug(false);
		configuration.setScan(true);
		for ( Appender1 appender1 : configuration1.getAppender() )
		{
			if ( "org.apache.log4j.ConsoleAppender".equals(appender1.getClazz()) )
			{
				Appender appender = addAppender(configuration, appender1, "ch.qos.logback.core.ConsoleAppender");
				setEncoder(appender1, appender);
				setFilter(appender1, appender);
			}
			else if ( "org.apache.log4j.RollingFileAppender".equals(appender1.getClazz()) )
			{
				Appender appender = addAppender(configuration, appender1, "ch.qos.logback.core.rolling.RollingFileAppender");
				setEncoder(appender1, appender);
				setFilter(appender1, appender);
				String file = valueOf(appender1.getParam(),"File");
				if ( file != null )
				{
					appender.setFile(file);
					RollingPolicy rollingPolicy = new RollingPolicy();
					appender.setRollingPolicy(rollingPolicy);
					rollingPolicy.setClazz("ch.qos.logback.core.rolling.TimeBasedRollingPolicy");
					rollingPolicy.setFileNamePattern(file+"-%d{yyyy_MM_dd}");
					String maxHistory = valueOf(appender1.getParam(),"MaxBackupIndex");
					if ( maxHistory != null )
						rollingPolicy.setMaxHistory(new Integer(maxHistory));
					else
						rollingPolicy.setMaxHistory(new Integer(9));
				}
				String append = valueOf(appender1.getParam(),"Append");
				if ( append != null )
					appender.setAppend(new Boolean(append));
			}
			else if ( "org.apache.log4j.net.SocketHubAppender".equals(appender1.getClazz()) )
			{
				// ch.qos.logback.classic.net.server.ServerSocketAppender
				// ch.qos.logback.custom.SocketHubAppender
				Appender appender = addAppender(configuration, appender1, "ch.qos.logback.classic.net.server.ServerSocketAppender");
				setEncoder(appender1, appender);
				setFilter(appender1, appender);
				appender.setIncludeCallerData(true);
				String port = valueOf(appender1.getParam(),"Port");
				if ( port != null )
					appender.setPort(new Integer(port));
				else
					appender.setPort(4560);
			}
		}

		for ( Serializable categoryOrLogger1 : configuration1.getCategoryOrLogger() )
		{
			if ( categoryOrLogger1 instanceof Category1 )
			{
				Category1 category1 = (Category1) categoryOrLogger1;
				logback.qos.ch.xml.ns.logback.Logger logger = new logback.qos.ch.xml.ns.logback.Logger();
				configuration.getLogger().add(logger);
				logger.setName(category1.getName());
				logger.setLevel(category1.getLevel().getValue());
				Boolean additivity = new Boolean(category1.getAdditivity());
				if ( !additivity )
					logger.setAdditivity(additivity);
				for ( AppenderRef1 appenderRef1 : category1.getAppenderRef() )
				{
					Appender1 ref = (Appender1) appenderRef1.getRef();
					logger.getAppenderRef().add(new AppenderRef(ref.getName()));
				}
			}
			else if ( categoryOrLogger1 instanceof Logger1 )
			{
				Logger1 logger1 = (Logger1) categoryOrLogger1;
				logback.qos.ch.xml.ns.logback.Logger logger = new logback.qos.ch.xml.ns.logback.Logger();
				configuration.getLogger().add(logger);
				logger.setName(logger1.getName());
				logger.setLevel(logger1.getLevel().getValue());
				Boolean additivity = new Boolean(logger1.getAdditivity());
				if ( !additivity )
					logger.setAdditivity(additivity);
				for ( AppenderRef1 appenderRef1 : logger1.getAppenderRef() )
				{
					Appender1 ref = (Appender1) appenderRef1.getRef();
					logger.getAppenderRef().add(new AppenderRef(ref.getName()));
				}
			}
		}

		Root1 root1 = configuration1.getRoot();
		if ( root1 != null )
		{
			Root root = new Root();
			configuration.setRoot(root);
			root.setLevel(root1.getLevel().getValue());
			for ( AppenderRef1 appenderRef1 : root1.getAppenderRef() )
			{
				Appender1 ref = (Appender1) appenderRef1.getRef();
				root.getAppenderRef().add(new AppenderRef(ref.getName()));
			}
		}

		JAXBElement<Configuration> configurationJAXB = getLogbackFactory().createConfiguration(configuration);
		JAXBContext jaxbLogback = JAXBContext.newInstance(getLogbackFactory().getClass());
		File fileLogback = new File(options.getProperty(Main.KEY_TARGET));
		Marshaller logbackMarshaller = jaxbLogback.createMarshaller();
		logbackMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		if ( "stdout".equalsIgnoreCase(fileLogback.getName()) )
			logbackMarshaller.marshal(configurationJAXB, System.out);
		else
			logbackMarshaller.marshal(configurationJAXB, fileLogback);
	}

	private void setEncoder(Appender1 appender1, Appender appender)
	{
		Encoder encoder = new Encoder();
		String charset = valueOf(appender1.getParam(),"Encoding");
		if ( charset != null )
			encoder.setCharset(charset);
		if ( appender1.getLayout() != null )
		{
			if ( "org.apache.log4j.PatternLayout".equals(appender1.getLayout().getClazz()) )
			{
				String pattern = valueOf(appender1.getLayout().getParam(),"ConversionPattern");
				if ( pattern != null )
				{
					//Layout layout = new Layout();
					//layout.setClazz("ch.qos.logback.classic.PatternLayout");
					//layout.setPattern(pattern);
					//encoder.setLayout(layout);
					//encoder.setClazz("ch.qos.logback.core.encoder.LayoutWrappingEncoder");
					encoder.setPattern(pattern);
					appender.setEncoder(encoder);
				}
			}
		}
	}

	private void setFilter(Appender1 appender1, Appender appender)
	{
		String threshold = valueOf(appender1.getParam(),"Threshold");
		if ( threshold != null )
		{
			Filter filter = new Filter();
			filter.setClazz("ch.qos.logback.classic.filter.ThresholdFilter");
			filter.setLevel(threshold);
			appender.setFilter(filter);
		}
	}

	private Appender addAppender(Configuration configuration, Appender1 appender1, String clazz)
	{
		Appender appender = new Appender();
		configuration.getAppender().add(appender);
		appender.setName(appender1.getName());
		appender.setClazz(clazz);
		return appender;
	}
}

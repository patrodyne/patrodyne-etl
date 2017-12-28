package org.patrodyne.etl.logxtool.commands;

import java.util.List;

import org.apache.logging.log4j._1_0.config.Param1;

public class CommandSupport
{
	public CommandSupport()
	{
		super();
	}

	private org.apache.logging.log4j._1_0.config.ObjectFactory log4j1Factory;
	public org.apache.logging.log4j._1_0.config.ObjectFactory getLog4j1Factory()
	{
		if ( log4j1Factory == null )
			log4j1Factory = new org.apache.logging.log4j._1_0.config.ObjectFactory();
		return log4j1Factory;
	}

	protected String valueOf(List<Param1> paramList, String paramName)
	{
		String paramValue = null;
		for ( Param1 param : paramList )
		{
			if ( paramName.equalsIgnoreCase(param.getName()) )
			{
				paramValue = param.getValue();
				break;
			}
		}
		return paramValue;
	}
}
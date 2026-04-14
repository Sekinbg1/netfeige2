package org.teleal.common.androidfwk.logging;

import java.util.logging.Level;

/**
 * Stub class for Android compatibility
 * Original library not available
 */
public class LogCategory {
	private String name;
	private Group[] groups;
	
	public LogCategory(String name) {
		this.name = name;
	}
	
	public LogCategory(String name, Group[] groups) {
		this.name = name;
		this.groups = groups;
	}
	
	public static class Group {
		private String groupName;
		private LoggerLevel[] loggerLevels;
		
		public Group(String groupName, LoggerLevel[] loggerLevels) {
			this.groupName = groupName;
			this.loggerLevels = loggerLevels;
		}
	}
	
	public static class LoggerLevel {
		private String loggerName;
		private Level level;
		
		public LoggerLevel(String loggerName, Level level) {
			this.loggerName = loggerName;
			this.level = level;
		}
		
		public LoggerLevel(Class<?> clazz, Level level) {
			this.loggerName = clazz.getName();
			this.level = level;
		}
	}
}

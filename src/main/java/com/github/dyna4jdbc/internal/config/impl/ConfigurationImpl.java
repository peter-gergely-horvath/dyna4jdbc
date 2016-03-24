package com.github.dyna4jdbc.internal.config.impl;

import com.github.dyna4jdbc.internal.config.Configuration;

class ConfigurationImpl implements Configuration {

	private char cellSeparator;

	@Override
	public char getCellSeparator() {
		return this.cellSeparator;
	}
	
	void setCellSeparator(char c) {
		this.cellSeparator = c;
	}
	
}

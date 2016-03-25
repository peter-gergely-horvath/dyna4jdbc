package com.github.dyna4jdbc.internal.config.impl;

import com.github.dyna4jdbc.internal.config.Configuration;

class ConfigurationImpl implements Configuration {

	private char cellSeparator;
	private boolean skipFirstLine;

	@Override
	public char getCellSeparator() {
		return this.cellSeparator;
	}
	
	void setCellSeparator(char c) {
		this.cellSeparator = c;
	}

	@Override
	public boolean getSkipFirstLine() {
		return this.skipFirstLine;
	}
	
	public void setSkipFirstLine(boolean skipFirstLine) {
		this.skipFirstLine = skipFirstLine;
	}
	
}

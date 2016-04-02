package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;

class DefaultColumnMetadata implements ColumnMetadata {

	private boolean takesFirstRowValue;
	private boolean currency;
	private Nullability nullability;
	private boolean signed;
	private int columnDisplaySize;
	private String columnLabel;
	private String columnName;
	private int precision;
	private int scale;
	private SQLDataType columnType;
	private String columnTypeName;
	private Class<?> columnClass;
	private String formatString;



	@Override
	public boolean isTakesFirstRowValue() {
		return takesFirstRowValue;
	}

	@Override
	public boolean isCurrency() {
		return currency;
	}

	@Override
	public Nullability getNullability() {
		return nullability;
	}

	@Override
	public boolean isSigned() {
		return signed;
	}

	@Override
	public int getColumnDisplaySize() {
		return columnDisplaySize;
	}

	@Override
	public String getColumnLabel() {
		return columnLabel;
	}

	@Override
	public String getColumnName() {
		return columnName;
	}

	@Override
	public int getPrecision() {
		return precision;
	}

	@Override
	public int getScale() {
		return scale;
	}

	@Override
	public SQLDataType getColumnType() {
		return columnType;
	}

	@Override
	public String getColumnTypeName() {
		return columnTypeName;
	}

	@Override
	public Class<?> getColumnClass() {
		return columnClass;
	}
	
	@Override
	public String getFormatString() {
		return formatString;
	}

	public void setTakesFirstRowValue(boolean takesFirstRowValue) {
		this.takesFirstRowValue = takesFirstRowValue;

	}

	public void setCurrency(boolean currency) {
		this.currency = currency;

	}

	public void setNullability(Nullability nullability) {
		this.nullability = nullability;

	}

	public void setSigned(boolean signed) {
		this.signed = signed;

	}

	public void setColumnDisplaySize(int columnDisplaySize) {
		this.columnDisplaySize = columnDisplaySize;

	}

	public void setColumnLabel(String columnLabel) {
		this.columnLabel = columnLabel;

	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;

	}

	public void setPrecision(int precision) {
		this.precision = precision;

	}

	public void setScale(int scale) {
		this.scale = scale;

	}

	public void setColumnType(SQLDataType columnType) {
		this.columnType = columnType;

	}

	public void setColumnTypeName(String columnTypeName) {
		this.columnTypeName = columnTypeName;

	}

	public void setColumnClass(Class<?> columnClass) {
		this.columnClass = columnClass;
	}
	
	public void setFormatString(String formatString) {
		this.formatString = formatString;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Defaultthis[");
		sb.append("\ntakesFirstRowValue=");
		sb.append(takesFirstRowValue);
		sb.append(",\ncurrency=");
		sb.append(currency);
		sb.append(",\nnullability=");
		sb.append(nullability);
		sb.append(",\nsigned=");
		sb.append(signed);
		sb.append(",\ncolumnDisplaySize=");
		sb.append(columnDisplaySize);
		sb.append(",\ncolumnLabel=");
		sb.append(columnLabel);
		sb.append(",\ncolumnName=");
		sb.append(columnName);
		sb.append(",\nprecision=");
		sb.append(precision);
		sb.append(",\nscale=");
		sb.append(scale);
		sb.append(",\ncolumnType=");
		sb.append(columnType);
		sb.append(",\ncolumnTypeName=");
		sb.append(columnTypeName);
		sb.append(",\ncolumnClass=");
		sb.append(columnClass);
		sb.append(",\nformatString=");
		sb.append(formatString);
		sb.append("]");
		return sb.toString();
	}

}

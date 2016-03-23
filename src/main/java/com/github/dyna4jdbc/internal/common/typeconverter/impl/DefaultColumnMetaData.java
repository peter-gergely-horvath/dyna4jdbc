package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;

class DefaultColumnMetaData implements ColumnMetadata {

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
	
	private DefaultColumnMetaData() {
		// no instances allowed from outside 
	}
	
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


	static Builder builder() {
		return new Builder(new DefaultColumnMetaData());
	}

	static class Builder {
		
		private DefaultColumnMetaData columnMetaData;
		
		private Builder(DefaultColumnMetaData columnMetaData) {
			this.columnMetaData = columnMetaData;
		}
		
		DefaultColumnMetaData build() {
			if (columnMetaData == null) {
				throw new IllegalStateException("builder is not re-usable!");
			}

			// TODO: add validation
			
			DefaultColumnMetaData returnValue = columnMetaData;
			columnMetaData = null;

			return returnValue;
		}
		
		public Builder setTakesFirstRowValue(boolean takesFirstRowValue) {
			columnMetaData.takesFirstRowValue = takesFirstRowValue;
			return this;
		}

		public Builder setCurrency(boolean currency) {
			columnMetaData.currency = currency;
			return this;
		}

		public Builder setNullability(Nullability nullability) {
			columnMetaData.nullability = nullability;
			return this;
		}

		public Builder setSigned(boolean signed) {
			columnMetaData.signed = signed;
			return this;
		}

		public Builder setColumnDisplaySize(int columnDisplaySize) {
			columnMetaData.columnDisplaySize = columnDisplaySize;
			return this;
		}

		public Builder setColumnLabel(String columnLabel) {
			columnMetaData.columnLabel = columnLabel;
			return this;
		}

		public Builder setColumnName(String columnName) {
			columnMetaData.columnName = columnName;
			return this;
		}

		public Builder setPrecision(int precision) {
			columnMetaData.precision = precision;
			return this;
		}

		public Builder setScale(int scale) {
			columnMetaData.scale = scale;
			return this;
		}

		public Builder setColumnType(SQLDataType columnType) {
			columnMetaData.columnType = columnType;
			return this;
		}

		public Builder setColumnTypeName(String columnTypeName) {
			columnMetaData.columnTypeName = columnTypeName;
			return this;
		}

		public Builder setColumnClass(Class<?> columnClass) {
			columnMetaData.columnClass = columnClass;
			return this;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DefaultColumnMetaData[");
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
		sb.append("]");
		return sb.toString();
	}


	
	
}

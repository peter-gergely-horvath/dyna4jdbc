package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;

class DefaultColumnMetadata implements ColumnMetadata {

    private boolean consumesFirstRow;
    private boolean currency;
    private Nullability nullability;
    private boolean signed;
    private int columnDisplaySize;
    private String columnLabel;
    private String columnName;
    private int precision;
    private int scale;
    private SQLDataType columnType;
    private String formatString;

    public boolean isConsumesFirstRow() {
        return consumesFirstRow;
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
    public String getFormatString() {
        return formatString;
    }

    public void setConsumesFirstRow(boolean consumesFirstRow) {
        this.consumesFirstRow = consumesFirstRow;

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

    public void setFormatString(String formatString) {
        this.formatString = formatString;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DefaultColumnMetadata[");
        sb.append("\nconsumesFirstRow=");
        sb.append(consumesFirstRow);
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
        sb.append(",\nformatString=");
        sb.append(formatString);
        sb.append("]");
        return sb.toString();
    }

    //CHECKSTYLE.OFF: AvoidInlineConditionals, MagicNumber : generated hashCode / equals
    //CHECKSTYLE.OFF: MagicNumber : generated hashCode
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + columnDisplaySize;
        result = prime * result + ((columnLabel == null) ? 0 : columnLabel.hashCode());
        result = prime * result + ((columnName == null) ? 0 : columnName.hashCode());
        result = prime * result + ((columnType == null) ? 0 : columnType.hashCode());
        result = prime * result + (currency ? 1231 : 1237);
        result = prime * result + ((formatString == null) ? 0 : formatString.hashCode());
        result = prime * result + ((nullability == null) ? 0 : nullability.hashCode());
        result = prime * result + precision;
        result = prime * result + scale;
        result = prime * result + (signed ? 1231 : 1237);
        result = prime * result + (consumesFirstRow ? 1231 : 1237);
        return result;
    }
    //CHECKSTYLE.ON: MagicNumber : generated hashCode

    //CHECKSTYLE.OFF: NeedBraces : generated equals
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DefaultColumnMetadata other = (DefaultColumnMetadata) obj;
        if (columnDisplaySize != other.columnDisplaySize)
            return false;
        if (columnLabel == null) {
            if (other.columnLabel != null)
                return false;
        } else if (!columnLabel.equals(other.columnLabel))
            return false;
        if (columnName == null) {
            if (other.columnName != null)
                return false;
        } else if (!columnName.equals(other.columnName))
            return false;
        if (columnType != other.columnType)
            return false;
        if (currency != other.currency)
            return false;
        if (formatString == null) {
            if (other.formatString != null)
                return false;
        } else if (!formatString.equals(other.formatString))
            return false;
        if (nullability != other.nullability)
            return false;
        if (precision != other.precision)
            return false;
        if (scale != other.scale)
            return false;
        if (signed != other.signed)
            return false;
        if (consumesFirstRow != other.consumesFirstRow)
            return false;
        return true;
    }
    //CHECKSTYLE.ON: NeedBraces
    //CHECKSTYLE.ON: AvoidInlineConditionals, MagicNumber

}

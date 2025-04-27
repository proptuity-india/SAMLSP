package com.onek.ak.model;

public class AttributeMap {
    public String attributeName;
    public String mappedName;
    public boolean isList;

    public AttributeMap(String attributeName, String mappedName, boolean isList) {
        this.attributeName = attributeName;
        this.mappedName = mappedName;
        this.isList = isList;
    }
}

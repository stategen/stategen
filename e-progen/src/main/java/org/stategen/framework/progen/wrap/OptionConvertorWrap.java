package org.stategen.framework.progen.wrap;

public class OptionConvertorWrap {
    private String value;
    private String title;
    private String label;
    private String parentId;
    private String url;

    public OptionConvertorWrap(String value, String title, String label, String parentId, String url) {
        super();
        this.value = value;
        this.title = title;
        this.label = label;
        this.parentId = parentId;
        this.url = url;
    }

    public String getValue() {
        return value;
    }

    public String getTitle() {
        return title;
    }

    public String getParentId() {
        return parentId;
    }

    public String getUrl() {
        return url;
    }
    public String getLabel() {
        return label;
    }

}

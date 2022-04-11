package com.example.labfinal;

public class Article {

    protected String id;                // id (used when adding favorites)
    protected String sectionName;       // section name
    protected String webTitle;          // title
    protected String webUrl;            // web article url

    public Article(String id, String sectionName, String webTitle, String webUrl) {
        this.id = id;
        this.sectionName = sectionName;
        this.webTitle = webTitle;
        this.webUrl =webUrl;
    }

    public String getId() {
        return id;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public String getWebUrl() {
        return webUrl;
    }
}

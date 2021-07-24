package com.rspkumobile.model;

/**
 * Created by DK on 12/28/2017.
 */

public class ArticleModelBackUp {

    private String articleCover;
    private String articleTitle;
    private String articleContent;

    public ArticleModelBackUp(String articleCover, String articleTitle, String articleContnet) {
        this.articleCover = articleCover;
        this.articleTitle = articleTitle;
        this.articleContent = articleContnet;
    }

    public String getCover() {
        return articleCover;
    }

    public String getTitle() {
        return articleTitle;
    }

    public String getContent() {
        return articleContent;
    }
}

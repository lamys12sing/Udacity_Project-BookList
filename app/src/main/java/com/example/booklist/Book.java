package com.example.booklist;

public class Book {
    private String book, url;

    /**
     * @param book is the name of a book.
     * @param url is the link of a book.
     */
    public Book (String book, String url){
        this.book = book;
        this.url = url;
    }

    /**
     * @return the name of the book.
     */
    public String getBook (){
        return book;
    }

    /**
     * @return url of the book.
     */
    public String getUrl(){
        return url;
    }
}

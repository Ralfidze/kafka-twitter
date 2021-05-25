package com.ralfidze.kafka.tutorial2.helpers;

public class FakeTweetDto {
    private String author;
    private int quantity;

    public FakeTweetDto(String author, int quantity) {
        this.author = author;
        this.quantity = quantity;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "TweetDto {" +
                "symbol='" + author + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}

package com.ralfidze.kafka.tutorial2.helpers;

public class TweetGenerator {

    private TweetProvider orderProvider = new TweetProvider();

    public FakeTweetDto generate() {
        return orderProvider.getTweet();
    }

}

package com.ralfidze.kafka.tutorial2.helpers;

import java.util.Random;

public class TweetProvider {
    static final String [] SYMBOLS = { "APPL", "GOOG", "IBM", "YAH", "ALROSA" };
    static final int QUANTITY_SEED = 10;

    public FakeTweetDto getTweet() {
        return new FakeTweetDto(SYMBOLS[new Random().nextInt(SYMBOLS.length)],
                new Random().nextInt(QUANTITY_SEED));
    }
}

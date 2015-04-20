package com.darren.lucene50;

import org.junit.Test;

public class HelloLuceneTest {
    @Test
    public void testIndex() {
        HelloLucene helloLucene = new HelloLucene();
        helloLucene.index();
    }

    @Test
    public void testSearch() {
        HelloLucene helloLucene = new HelloLucene();
        helloLucene.search();
    }
}

package com.darren.lucene35;

import org.junit.Test;

public class HelloLuceneTest {
    
    @Test
    public void testIndex() {
        HelloLucene helloLucene = new HelloLucene();
        helloLucene.index();
    }
    
    @Test
    public void testSearcher(){
        HelloLucene helloLucene = new HelloLucene();
        helloLucene.searcher();
    }
}

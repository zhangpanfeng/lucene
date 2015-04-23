package com.darren.lucene50;

import org.junit.Test;

public class IndexUtilTest {
    @Test
    public void testIndex() {
        IndexUtil.index();
    }

    @Test
    public void testSearch() {
        IndexUtil.search();
    }

    @Test
    public void testCheck() {
        IndexUtil.check();
    }

    @Test
    public void testDelete() {
        IndexUtil.delete();
        IndexUtil.check();
    }

    @Test
    public void testUnDelete() {
        IndexUtil.unDelete();
        IndexUtil.check();
    }
}

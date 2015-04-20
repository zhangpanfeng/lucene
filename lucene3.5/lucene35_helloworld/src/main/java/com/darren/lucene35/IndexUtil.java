package com.darren.lucene35;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class IndexUtil {
    private static final String[] ids = { "1", "2", "3" };
    private static final String[] authors = { "Darren", "Tony", "Grylls" };
    private static final String[] titles = { "Hello World", "Hello Lucene", "Hello Java" };
    private static final String[] contents = { "Hello World, I am on my way", "Today is my first day to study Lucene",
            "I like Java" };

    /**
     * 建立索引
     */
    public static void index() {
        IndexWriter indexWriter = null;
        try {
            // 1、创建Directory
            Directory directory = FSDirectory.open(new File("F:/test/lucene/index"));

            // 2、创建IndexWriter
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, analyzer);
            indexWriter = new IndexWriter(directory, config);

            int size = ids.length;
            for (int i = 0; i < size; i++) {
                // 3、创建Document对象
                Document document = new Document();
                // 看看四个参数的意思
                /**
                 * Create a field by specifying its name, value and how it will be saved in the index. Term vectors will
                 * not be stored in the index.
                 * 
                 * @param name
                 *            The name of the field
                 * @param value
                 *            The string to process
                 * @param store
                 *            Whether <code>value</code> should be stored in the index
                 * @param index
                 *            Whether the field should be indexed, and if so, if it should be tokenized before indexing
                 * 
                 *            public Field(String name, String value, Store store, Index index) { this(name, value,
                 *            store, index, TermVector.NO); }
                 */

                // 4、为Document添加Field

                // 对ID存储，但是不分词也不存储norms信息,这个norms中包括了创建索引的时间和权值等信息
                document.add(new Field("id", ids[i], Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
                // 对Author存储，但是不分词
                document.add(new Field("author", authors[i], Field.Store.YES, Field.Index.NOT_ANALYZED));
                // 对Title存储，分词
                document.add(new Field("title", titles[i], Field.Store.YES, Field.Index.ANALYZED));
                // 对Content不存储，但是分词
                /**
                 * 注：添加内容或文件是默认是不存储的，这个查询时可以证明这个问题
                 * 
                 * new Field(name, reader)
                 * 
                 * 那么问题来了，如果想存文件内容怎么办呢？
                 * 
                 * 那就把文件读出来，比如读出字符串，然后不就能按字符串的方式存储啦
                 */
                document.add(new Field("content", contents[i], Field.Store.NO, Field.Index.ANALYZED));

                // 5、通过IndexWriter添加文档到索引中
                indexWriter.addDocument(document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (indexWriter != null) {
                    indexWriter.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 搜索
     */
    public static void search() {
        IndexReader indexReader = null;
        try {
            // 1、创建Directory
            Directory directory = FSDirectory.open(new File("F:/test/lucene/index"));
            // 2、创建IndexReader
            indexReader = IndexReader.open(directory);
            // 3、根据IndexReader创建IndexSearch
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            // 4、创建搜索的Query
            // 使用默认的标准分词器
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);

            // 在content中搜索Lucene
            // 创建parser来确定要搜索文件的内容，第二个参数为搜索的域
            QueryParser queryParser = new QueryParser(Version.LUCENE_35, "content", analyzer);
            // 创建Query表示搜索域为content包含Lucene的文档
            Query query = queryParser.parse("Lucene");

            // 5、根据searcher搜索并且返回TopDocs
            TopDocs topDocs = indexSearcher.search(query, 10);
            // 6、根据TopDocs获取ScoreDoc对象
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                // 7、根据searcher和ScoreDoc对象获取具体的Document对象
                Document document = indexSearcher.doc(scoreDoc.doc);
                // 8、根据Document对象获取需要的值
                System.out.println("id : " + document.get("id"));
                System.out.println("author : " + document.get("author"));
                System.out.println("title : " + document.get("title"));
                /**
                 * 看看content能不能打印出来，为什么？
                 */
                System.out.println("content : " + document.get("content"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (indexReader != null) {
                    indexReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}

package com.darren.lucene45;

import java.io.File;
import java.io.FileReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class HelloLucene {
    /**
     * 建立索引
     */
    public void index() {
        IndexWriter indexWriter = null;
        try {
            // 1、创建Directory
            Directory directory = FSDirectory.open(new File("F:/test/lucene/index"));

            // 2、创建IndexWriter
            /**
             * 注意StandardAnalyzer与3.5版本的不同：
             * 
             * StandardAnalyzer不在lucene-core包中而在lucene-analyzers-common包中 从4.0版本以后分离
             */
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_45);
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_45, analyzer);
            indexWriter = new IndexWriter(directory, indexWriterConfig);

            File dFile = new File("F:/test/lucene/document");
            File[] files = dFile.listFiles();
            for (File file : files) {
                // 3、创建Document对象
                Document document = new Document();

                // 4、为Document添加Field
                /**
                 * 注意Field与3.5版本的不同：两个参数的构造器已过时，使用如下构造器
                 */
                // 第三个参数是FieldType 但是定义在TextField中作为静态变量，看API也不好知道怎么写
                document.add(new Field("content", new FileReader(file), TextField.TYPE_NOT_STORED));
                document.add(new Field("filename", file.getName(), TextField.TYPE_STORED));
                document.add(new Field("filepath", file.getAbsolutePath(), TextField.TYPE_STORED));

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
    public void search() {
        DirectoryReader directoryReader = null;
        try {
            // 1、创建Directory
            Directory directory = FSDirectory.open(new File("F:/test/lucene/index"));
            // 2、创建IndexReader
            /**
             * 注意Reader与3.5版本不同：
             * 
             * 所以使用DirectoryReader
             * 
             * @Deprecated public static DirectoryReader open(final Directory directory) throws IOException { return
             *             DirectoryReader.open(directory); }
             */
            // 如下方法过时
            // IndexReader indexReader = IndexReader.open(directory);
            directoryReader = DirectoryReader.open(directory);
            // 3、根据IndexReader创建IndexSearch

            IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

            // 4、创建搜索的Query
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_45);
            /**
             * 注意与3.5版本不同：
             * 
             * 需要引入lucene-queryparser包，因为从4.0版本后lucene-queryparser包从核心包分离
             */
            // 创建parser来确定要搜索文件的内容，第二个参数为搜索的域
            QueryParser queryParser = new QueryParser(Version.LUCENE_45, "content", analyzer);

            // 创建Query表示搜索域为content包含Darren的文档
            Query query = queryParser.parse("Darren");

            // 5、根据searcher搜索并且返回TopDocs
            TopDocs topDocs = indexSearcher.search(query, 10);

            // 6、根据TopDocs获取ScoreDoc对象
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {

                // 7、根据searcher和ScoreDoc对象获取具体的Document对象
                Document document = directoryReader.document(scoreDoc.doc);

                // 8、根据Document对象获取需要的值
                System.out.println(document.get("filename") + " " + document.get("filepath"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (directoryReader != null) {
                    directoryReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

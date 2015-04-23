package com.darren.lucene50;

import java.nio.file.FileSystems;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

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
            Directory directory = FSDirectory.open(FileSystems.getDefault().getPath("F:/test/lucene/index"));

            // 2、创建IndexWriter
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
            indexWriter = new IndexWriter(directory, indexWriterConfig);

            int size = ids.length;
            for (int i = 0; i < size; i++) {
                // 3、创建Document对象
                Document document = new Document();
                // 看看四个参数的意思

                // 4、为Document添加Field
                /**
                 * Create field with String value.
                 * 
                 * @param name
                 *            field name
                 * @param value
                 *            string value
                 * @param type
                 *            field type
                 * @throws IllegalArgumentException
                 *             if either the name or value is null, or if the field's type is neither indexed() nor
                 *             stored(), or if indexed() is false but storeTermVectors() is true.
                 * @throws NullPointerException
                 *             if the type is null
                 * 
                 *             public Field(String name, String value, FieldType type)
                 */

                /**
                 * 注意：这里与3.5版本不同，原来的构造函数已过时
                 */

                /**
                 * 注：这里4.5版本类似使用FieldType代替了原来的Store和Index，不同的是Index变成IndexOptions
                 * 
                 */
                // 对ID存储，但是不分词也不存储norms信息
                FieldType idType = new FieldType();
                idType.setStored(true);
                idType.setIndexOptions(IndexOptions.DOCS);
                idType.setOmitNorms(false);
                document.add(new Field("id", ids[i], idType));

                // 对Author存储，但是不分词
                FieldType authorType = new FieldType();
                authorType.setStored(true);
                authorType.setIndexOptions(IndexOptions.DOCS);
                document.add(new Field("author", authors[i], authorType));

                // 对Title存储，分词
                document.add(new Field("title", titles[i], StringField.TYPE_STORED));

                // 对Content不存储，但是分词
                document.add(new Field("content", contents[i], TextField.TYPE_NOT_STORED));

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
        DirectoryReader indexReader = null;
        try {
            // 1、创建Directory
            Directory directory = FSDirectory.open(FileSystems.getDefault().getPath("F:/test/lucene/index"));
            // 2、创建IndexReader
            indexReader = DirectoryReader.open(directory);
            // 3、根据IndexReader创建IndexSearch
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            // 4、创建搜索的Query
            // 使用默认的标准分词器
            Analyzer analyzer = new StandardAnalyzer();

            // 在content中搜索Lucene
            // 创建parser来确定要搜索文件的内容，第二个参数为搜索的域
            QueryParser queryParser = new QueryParser("content", analyzer);
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

    /**
     * 删除索引
     */
    public static void delete() {
        IndexWriter indexWriter = null;
        try {
            Directory directory = FSDirectory.open(FileSystems.getDefault().getPath("F:/test/lucene/index"));
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
            indexWriter = new IndexWriter(directory, indexWriterConfig);
            /**
             * 参数是一个选项，可以是一个Query，也可以是一个term，term是一个精确查找的值
             * 
             * 此时删除的文档并不会被完全删除，而是存储在一个回收站中的，可以恢复
             */

            // 方式一：通过Term删除

            /**
             * 注意Term构造器的意思，第一个参数为Field，第二个参数为Field的值
             */
            indexWriter.deleteDocuments(new Term("id", "1"));

            // 方式二：通过Query删除

            /**
             * 这里就要造一个Query出来，删掉查处的索引
             */
            QueryParser queryParser = new QueryParser("content", analyzer);
            // 创建Query表示搜索域为content包含Lucene的文档
            Query query = queryParser.parse("Lucene");

            // indexWriter.deleteDocuments(query);
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
     * 更新索引
     */
    public static void update() {
        IndexWriter indexWriter = null;
        try {
            Directory directory = FSDirectory.open(FileSystems.getDefault().getPath("F:/test/lucene/index"));
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
            indexWriter = new IndexWriter(directory, indexWriterConfig);
            /**
             * Lucene并没有提供更新，这里的更新操作其实是如下两个操作的合集 先删除之后再添加
             */
            Document document = new Document();
            document.add(new Field("id", "33", StringField.TYPE_STORED));
            document.add(new Field("author", authors[0], StringField.TYPE_STORED));
            document.add(new Field("title", titles[0], StringField.TYPE_STORED));
            document.add(new Field("content", contents[1], TextField.TYPE_NOT_STORED));
            indexWriter.updateDocument(new Term("id", "1"), document);
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
     * 检查一下索引文件
     */
    public static void check() {
        DirectoryReader directoryReader = null;
        try {
            Directory directory = FSDirectory.open(FileSystems.getDefault().getPath("F:/test/lucene/index"));
            directoryReader = DirectoryReader.open(directory);
            // 通过reader可以有效的获取到文档的数量
            // 有效的索引文档
            System.out.println("有效的索引文档:" + directoryReader.numDocs());
            // 总共的索引文档
            System.out.println("总共的索引文档:" + directoryReader.maxDoc());
            // 删掉的索引文档，其实不恰当，应该是在回收站里的索引文档
            System.out.println("删掉的索引文档:" + directoryReader.numDeletedDocs());
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

    /**
     * 恢复删除的索引
     */
    public static void unDelete() {
        IndexWriter indexWriter = null;
        try {
            Directory directory = FSDirectory.open(FileSystems.getDefault().getPath("F:/test/lucene/index"));
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
            indexWriter = new IndexWriter(directory, indexWriterConfig);
            /**
             * 注意：和3.5版本不同，不再使用IndexReader恢复删除的索引，而是使用IndexWriter的rollback()方法
             */
            indexWriter.rollback();
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
     * 合并索引
     */
    public static void merge() {
        IndexWriter indexWriter = null;
        try {
            Directory directory = FSDirectory.open(FileSystems.getDefault().getPath("F:/test/lucene/index"));
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
            indexWriter = new IndexWriter(directory, indexWriterConfig);
            // 会将索引合并为2段，这两段中的被删除的数据会被清空
            /**
             * 特别注意：
             * 
             * 此处Lucene在3.5之后不建议使用，因为会消耗大量的开销，Lucene会根据情况自动处理的
             */

            // 把索引合并为两段
            indexWriter.forceMerge(2);
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
     * 强制删除
     */
    public static void forceDelete() {
        IndexWriter indexWriter = null;
        try {
            Directory directory = FSDirectory.open(FileSystems.getDefault().getPath("F:/test/lucene/index"));
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
            indexWriter = new IndexWriter(directory, indexWriterConfig);
            indexWriter.forceMergeDeletes();
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
}

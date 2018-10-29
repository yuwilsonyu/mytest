import com.itheima.dao.BookDao;
import com.itheima.dao.impl.BookDaoImpl;
import com.itheima.entity.Book;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.*;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IndexManager {

    String INDEX_PATH ="F:\\Index";

    @Test
    public void createIndex() throws Exception{
        //1.采集数据
        BookDao bookDao = new BookDaoImpl();
        List<Book> books = bookDao.findAllBook();
        //2.建立document对象
        List<Document> docList = new ArrayList<Document>();
        for (Book book : books) {
            Document doc = new Document();
            doc.add(new TextField("bookId",book.getId()+"", Field.Store.YES));
            // 图书名称
            doc.add(new TextField("bookName",book.getBookname(), Field.Store.YES));
            // 图书价格
            doc.add(new TextField("bookPrice",book.getPrice()+"", Field.Store.YES));
            // 图书图片
            doc.add(new TextField("bookPic",book.getPic(), Field.Store.YES));
            // 图书描述
            doc.add(new TextField("bookDesc",book.getBookdesc(), Field.Store.YES));
            docList.add(doc);
            //new DoubleField()
        }
        //3.建立分析器
        Analyzer analyzer = new IKAnalyzer();
        //4.建立索引库配置对象
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_3,analyzer);
        //5.建立索引库目录对象,确定索引库保存位置
        File file = new File(INDEX_PATH);
        Directory directory = FSDirectory.open(file);
        //6.建立索引库操作对象
        IndexWriter indexWriter = new IndexWriter(directory,iwc);
        //7.把文档对象写入索引库
        for (Document document : docList) {
            indexWriter.addDocument(document);
        }
        indexWriter.close();
    }

    @Test
    public void readIndex() throws Exception {
        //1.建立分析器
        Analyzer analyzer= new IKAnalyzer();
        //2.建立查询对象
        QueryParser queryParser = new QueryParser("bookName",analyzer);
        Query query = queryParser.parse("bookName:java");
        Directory directory = FSDirectory.open(new File(INDEX_PATH));
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs topDocs = searcher.search(query, 10);
        System.out.println("搜索到的数量"+topDocs.totalHits);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("---------------华丽丽分割线------------------------");
            // 取出文档id和分值
            int docId = scoreDoc.doc;
            float score = scoreDoc.score;
            System.out.println("当前文档的Id："+docId+",当前文档的分值："+score);
            Document doc = searcher.doc(docId);
            System.out.println("图书Id："+doc.get("bookId"));
            System.out.println("图书名称："+doc.get("bookName"));
            System.out.println("图书价格："+doc.get("bookPrice"));
            System.out.println("图书图片："+doc.get("bookPic"));
            System.out.println("图书图书描述："+doc.get("bookDesc"));
        }
        indexReader.close();
    }
}

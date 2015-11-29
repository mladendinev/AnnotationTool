package views;

import java.awt.EventQueue;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.basic.BasicArrowButton;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import auth.ConnectionMongo;

import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.ActionEvent;

import static com.mongodb.client.model.Filters.*;

public class ViewFrame extends JFrame {

    private JPanel contentPane;
    public int count;
    private static int j = 0;
    private static HashMap<Integer, ObjectId> indexDocuments = new HashMap<Integer, ObjectId>();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    char[] testPassword = {'f', 'i', 'n', 'a', 'l', 'Y', 'e', 'a', 'r', 'P', 'r', 'o', 'j', 'e', 'c', 't'};
                    MongoCredential credential = MongoCredential.createCredential("admin", "SearchApiResults", testPassword);
                    MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27017),
                            Collections.singletonList(credential));
                    MongoDatabase db = mongoClient.getDatabase("SearchApiResults");
                    MongoCollection<Document> collection = db.getCollection("testEncrypt");
                    ViewFrame frame = new ViewFrame(collection);

                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public ViewFrame(final MongoCollection<Document> collection) {
        final int limit = (int) collection.count();
        long files = collection.count();
        MongoCursor<Document> cursor = collection.find().iterator();
        Block<Document> storeArray = new Block<Document>() {
            public void apply(final Document document) {
                indexDocuments.put(j, (ObjectId) document.get("_id"));
                j++;
            }
        };
        collection.find().forEach(storeArray);

        final JFrame frame = new JFrame("TRY");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 588, 342);
        setTitle("Annotate data from Mongo");
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);


        EtchedBorder border = new EtchedBorder();
        final JTextArea textArea = new JTextArea();
        textArea.setBorder(border);
        Font arialBolditalic12 = new Font("Arial", Font.BOLD + Font.ITALIC, 12);
        textArea.setFont(arialBolditalic12);
        textArea.setBounds(99, 49, 388, 160);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        Document start = collection.find(eq("_id", indexDocuments.get(count))).first();
        textArea.setText("inital " + start.get("encrypt"));
        contentPane.add(textArea);

        final BasicArrowButton next = new BasicArrowButton(BasicArrowButton.EAST);
        next.setBounds(425, 12, 117, 25);
        contentPane.add(next);
        final BasicArrowButton previous = new BasicArrowButton(BasicArrowButton.WEST);
        previous.setBounds(35, 12, 117, 25);
        contentPane.add(previous);
        previous.setVisible(false);

        next.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                count++;
                if (count >= 0 && count < limit) {
                    System.out.println(count);
                    previous.setVisible(true);
                    previous.validate();
                    Document document = collection.find(eq("_id", indexDocuments.get(count))).first();
                    textArea.setText("next " + document.get("encrypt"));
                    if (count == limit - 1) {
                        next.setVisible(false);
                        next.invalidate();
                    }
                }
            }
        });

        previous.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                count--;
//				if (count<=0) 
//				{
//			        System.out.println("previous");
//					previous.setVisible(false);
//					previous.invalidate();
//		
//				}
                if (count >= 0) {
                    System.out.println(count);
                    Document document = collection.find(eq("_id", indexDocuments.get(count))).first();
                    textArea.setText("next " + document.get("encrypt"));
                    next.setVisible(true);
                    next.validate();
                    if (count == 0) {
                        previous.setVisible(false);
                        previous.invalidate();
                    }
                }

            }
        });

        contentPane.setLayout(null);

        JButton postiveButton = new JButton("Positive");
        postiveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//				System.out.println(indexDocuments.get(count)));
                collection.updateOne(eq("_id", indexDocuments.get(count)),
                        new Document("$set", new Document("sentiment", "positive")));
            }
        });
        postiveButton.setBounds(35, 233, 117, 25);
        contentPane.add(postiveButton);

        JButton negativeButton = new JButton("Negative");
        negativeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//				System.out.println(indexDocuments.get(count)));
                collection.updateOne(eq("_id", indexDocuments.get(count)),
                        new Document("$set", new Document("sentiment", "negative")));
            }
        });
        negativeButton.setBounds(440, 233, 117, 25);
        contentPane.add(negativeButton);

        JButton neutralButton = new JButton("Neutral");
        neutralButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//				System.out.println(indexDocuments.get(count)));
                collection.updateOne(eq("_id", indexDocuments.get(count)),
                        new Document("$set", new Document("sentiment", "neutral")));
            }
        });
        neutralButton.setBounds(231, 233, 117, 25);
        contentPane.add(neutralButton);
    }


}

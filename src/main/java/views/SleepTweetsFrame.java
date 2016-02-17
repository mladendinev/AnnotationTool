package views;

import auth.ConnectionMongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;

public class SleepTweetsFrame extends JFrame {

    private JPanel contentPane;
    private int count;
    private static int j = 0;
    private ConnectionMongo connection;

    private static ArrayList<ObjectId> ids = new ArrayList<ObjectId>();
    private static ArrayList<ObjectId> annotated = new ArrayList<ObjectId>();
    private JTextField extraText;
    private JButton positiveButton;
    private JButton negativeButton;
    private JButton neutralButton;
    /**
     * Launch the application.
     */
    //public static void main(String[] args) {
    // EventQueue.invokeLater(new Runnable() {
    // public void run() {
    //try {
    //char[] testPassword = {'f', 'i', 'n', 'a', 'l', 'Y', 'e', 'a', 'r', 'P', 'r', 'o', 'j', 'e', 'c', 't'};
    //MongoCredential credential = MongoCredential.createCredential("admin", "SearchApiResults", testPassword);
    //MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27017),
    //        Collections.singletonList(credential));
    //  MongoDatabase db = mongoClient.getDatabase("SearchApiResults");
    //MongoCollection<Document> collection = db.getCollection("testEncrypt");
    //    ViewFrame frame = new ViewFrame(collection);

    //        frame.setVisible(true);
    //  } catch (Exception e) {
    //          e.printStackTrace();
    // }
    //    }
    //  });
    //

    /**
     * Create the frame.
     */
    public SleepTweetsFrame(final MongoCollection<Document> sleepCollection, ConnectionMongo connectionFrame) {


        this.connection = connectionFrame;
        final String user = connection.username();
        String oppositeLock1;
        String oppositeLock2;
        switch (user) {
            case "rmoriss":
                oppositeLock1 = "nberry_lock";
                oppositeLock2 = "mladen_lock";
                break;
            case "nberry":
                oppositeLock1 = "rmorris_lock";
                oppositeLock2 = "mladen_lock";
                break;
            default:
                oppositeLock1 = "rmorris_lock";
                oppositeLock2 = "nberry_lock";
                break;
        }

        clearLocks(sleepCollection, user+"_lock");

        int numberTweetsForAnnot = 10;
        MongoCursor<Document> sleepTweetsCur = sleepCollection.find(and(exists(oppositeLock1, false), exists("user", false),
                                                                    exists(oppositeLock2, false))).iterator();

        while (sleepTweetsCur.hasNext() && numberTweetsForAnnot > 0) {
            ids.add((ObjectId) sleepTweetsCur.next().get("_id"));
            numberTweetsForAnnot--;
        }

        final int limit = ids.size();
        Collections.shuffle(ids);

        final Document lock = new Document(user + "_lock", "yes");

        for (ObjectId tweet : ids) {
            sleepCollection.updateOne(eq("_id", tweet),
                    new Document("$set", lock));
        }

       final MongoCollection<Document> extraInfoCollection = connection.database().getCollection("extraInformation");

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        setBounds(100, 100, 645, 350);
        setTitle("Annotate sleep related tweets");

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                for (ObjectId tweet : ids) {
                    sleepCollection.updateOne(eq("_id", tweet),
                            new Document("$unset", lock));
                }
                System.exit(0);
            }
        });


        EtchedBorder border = new EtchedBorder();
        final JTextArea textArea = new JTextArea();
        textArea.setBorder(border);
        Font arialBolditalic12 = new Font("Arial", Font.BOLD + Font.ITALIC, 12);
        textArea.setFont(arialBolditalic12);
        textArea.setBounds(99, 58, 388, 151);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        Document start = sleepCollection.find(eq("_id", ids.get(count))).first();
        textArea.setText(start.get("text").toString());
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
                    setBackGroundColor(negativeButton, positiveButton, neutralButton);
                    previous.setVisible(true);
                    previous.validate();
                    Document document = sleepCollection.find(eq("_id", ids.get(count))).first();
                    textArea.setText(document.get("text").toString());
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
                if (count >= 0) {
                    setBackGroundColor(negativeButton, positiveButton, neutralButton);
                    Document document = sleepCollection.find(eq("_id", ids.get(count))).first();
                    textArea.setText(document.get("text").toString());
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

        final JSlider slider = new JSlider();
        slider.setPreferredSize(new Dimension(300, 40));
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.setMaximum(10);
        slider.setValue(0);
        slider.setMajorTickSpacing(5);
        slider.setMinorTickSpacing(1);
        slider.setLabelTable(slider.createStandardLabels(5));
        slider.setBounds(88, 251, 229, 50);
        contentPane.add(slider);

        JLabel lblLevelOfConfidance = new JLabel("Level of confidence");
        lblLevelOfConfidance.setBounds(129, 231, 159, 15);
        contentPane.add(lblLevelOfConfidance);

        extraText = new JTextField();
        extraText.setBounds(383, 254, 185, 19);
        contentPane.add(extraText);
        extraText.setColumns(10);


        JLabel lblExtraInformation = new JLabel("Extra Information");
        lblExtraInformation.setBounds(413, 231, 141, 15);
        contentPane.add(lblExtraInformation);

        final String confidence = "user." + user + "." + "confidence";
        final String label = "user." + user + "." + "label";
        positiveButton = new JButton("Positive");
        neutralButton = new JButton("Neutral");
        negativeButton = new JButton("Negative");


        positiveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                annotated.add(ids.get(count));
                Document doc = new Document(label, "positive").append(confidence, slider.getValue());
                System.out.println(ids.get(count));
                setBackGroundColor(negativeButton, positiveButton, neutralButton);
                sleepCollection.updateOne(eq("_id", ids.get(count)),
                        new Document("$set", doc));


                if (isItEmpty(extraText)) {
                    extraInfoCollection.insertOne(new Document("hashtags", extraText.getText()));
                }

                Object source = e.getSource();
                if (source instanceof Component) {
                    ((Component) source).setBackground(Color.GREEN);
                }
            }
        });
        positiveButton.setBounds(499, 71, 117, 25);
        contentPane.add(positiveButton);


        neutralButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                annotated.add(ids.get(count));
                setBackGroundColor(negativeButton, positiveButton, neutralButton);
                Document doc = new Document(label, "neutral").append(confidence, slider.getValue());
                sleepCollection.updateOne(eq("_id", ids.get(count)),
                        new Document("$set", doc));

                if (isItEmpty(extraText)) {
                    extraInfoCollection.insertOne(new Document("hashtags", extraText.getText()));
                }

                Object source = e.getSource();
                if (source instanceof Component) {
                    ((Component) source).setBackground(Color.GREEN);
                }
            }
        });

        neutralButton.setBounds(499, 121, 117, 25);
        contentPane.add(neutralButton);


        negativeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                annotated.add(ids.get(count));
                setBackGroundColor(negativeButton, positiveButton, neutralButton);
                Document doc = new Document(label, "negative").append(confidence, slider.getValue());
                sleepCollection.updateOne(eq("_id", ids.get(count)),
                        new Document("$set", doc));


                if (isItEmpty(extraText)) {
                    extraInfoCollection.insertOne(new Document("hashtags", extraText.getText()));
                }

                Object source = e.getSource();
                if (source instanceof Component) {

                    ((Component) source).setBackground(Color.GREEN);
                }
            }
        });
        negativeButton.setBounds(499, 172, 117, 25);
        contentPane.add(negativeButton);


    }

    private boolean isItEmpty(JTextField text) {
        if (text.getText().equals(""))
            return false;
        else
            return true;
    }

    private static void setBackGroundColor(JButton button1, JButton button2, JButton button3) {
        button1.setBackground(null);
        button2.setBackground(null);
        button3.setBackground(null);
    }

    private static void clearLocks(MongoCollection<Document> sleepCollection, String userLock) {
        Document doc = new Document(userLock, "yes");
        sleepCollection.updateMany(doc,
                new Document("$unset", doc), new UpdateOptions().upsert(false));

    }

}	


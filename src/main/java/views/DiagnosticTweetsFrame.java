package views;

import auth.ConnectionMongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import static com.mongodb.client.model.Filters.*;

public class DiagnosticTweetsFrame extends JFrame {

    private JPanel contentPane;
    public int count;
    private static int count1 = 0;
    private static int count2 = 0;

    private static ArrayList<ObjectId> ids = new ArrayList<ObjectId>();
    private static ArrayList<ObjectId> annotated = new ArrayList<ObjectId>();
    private ConnectionMongo connection;
    private JTextField extraText;
    private JButton positiveButton;
    private JButton negativeButton;
    private JButton neutralButton;


    /**
     * Launch the application.
     */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					DiagnosticTweetsFrame frame = new DiagnosticTweetsFrame();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

    /**
     * Create the frame.
     */
    public DiagnosticTweetsFrame(final MongoCollection<Document> diagnosticCollection, ConnectionMongo connection) {

        this.connection = connection;
        String user = connection.username();

        int tweetsForAnnotation = 100;
        int numberOfPotentialTweets = ((int) (((double) 65 / 100) * tweetsForAnnotation));
        int numberRandomTweets = ((int) (((double) 35 / 100) * tweetsForAnnotation));
        MongoCursor<Document> potentialDiagCur = diagnosticCollection.find(and(eq("potentialDiagnostic", "yes"),
                                                                                         exists("user"+user, false))).iterator();
        while (potentialDiagCur.hasNext() && numberOfPotentialTweets > 0) {
            ids.add((ObjectId) potentialDiagCur.next().get("_id"));
            numberOfPotentialTweets--;
        }

        MongoCursor<Document> randomTweetsCursor = diagnosticCollection.find(and(exists("potentialDiagnostic", false),
                                                                                       exists("user"+user, false))).iterator();

        while (randomTweetsCursor.hasNext() && numberRandomTweets > 0 ){
            ids.add((ObjectId) potentialDiagCur.next().get("_id"));
            numberRandomTweets--;
        }
        
        final int limit = ids.size();
        Collections.shuffle(ids);

        Document lock = new Document("lock", "positive");

        for(ObjectId tweet : ids){
            diagnosticCollection.updateOne(eq("_id", tweet),
                    new Document("$set", lock));
        }

        final MongoCollection<Document> extraInfoCollection = connection.database().getCollection("extraInformation");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 645, 350);
        setTitle("Annotate diagnostic tweets");
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);


        EtchedBorder border = new EtchedBorder();
        final JTextArea textArea = new JTextArea();
        textArea.setBorder(border);
        Font arialBolditalic12 = new Font("Arial", Font.BOLD + Font.ITALIC, 12);
        textArea.setFont(arialBolditalic12);
        textArea.setBounds(99, 58, 388, 151);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        Document start = diagnosticCollection.find(eq("_id", ids.get(count))).first();
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
                	setBackGroundColor(negativeButton,positiveButton,neutralButton);
                    previous.setVisible(true);
                    previous.validate();
                    Document document = diagnosticCollection.find(eq("_id", ids.get(count))).first();
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
                	setBackGroundColor(negativeButton,positiveButton,neutralButton);

                    Document document = diagnosticCollection.find(eq("_id", ids.get(count))).first();
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
                Document doc = new Document(label, "positive").append(confidence, slider.getValue());
                setBackGroundColor(negativeButton, positiveButton, neutralButton);
                annotated.add(ids.get(count));
                diagnosticCollection.updateOne(eq("_id", ids.get(count)),
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
                setBackGroundColor(negativeButton, positiveButton, neutralButton);
                Document doc = new Document(label, "neutral").append(confidence, slider.getValue());
                annotated.add(ids.get(count));
                diagnosticCollection.updateOne(eq("_id", ids.get(count)),
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
                setBackGroundColor(negativeButton, positiveButton, neutralButton);
                annotated.add(ids.get(count));
                Document doc = new Document(label, "negative").append(confidence, slider.getValue());
                diagnosticCollection.updateOne(eq("_id", ids.get(count)),
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

}

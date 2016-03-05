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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

import static com.mongodb.client.model.Filters.*;

public class SleepTweetsFrame extends JFrame {

    private JPanel contentPane;
    private int count;
    private static int j = 0;
    private ConnectionMongo connection;

    private static ArrayList<ObjectId> ids = new ArrayList<ObjectId>();
    private JTextField extraText;
    private JButton submitButton;
    private JTextField labelAsValue;
    private final JTextField typeExtraInfo = new JTextField();

    /**
     * Launch the application.
     */
    //public static void main(String[] args) {
    // EventQueue.invokeLater(new Runnable() {
    // public void run() {
    //try {
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

        if (user.equals("rmorris")) {
            oppositeLock1 = "nberry_lock";
            oppositeLock2 = "mladen_lock";
        } else if (user.equals("nberry")) {
            oppositeLock1 = "rmorris_lock";
            oppositeLock2 = "mladen_lock";
        } else {
            oppositeLock1 = "rmorris_lock";
            oppositeLock2 = "nberry_lock";
        }

        clearLocks(sleepCollection, user + "_lock");

        int numberTweetsForAnnot = 100;
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

        setBounds(100, 100, 731, 350);
        setTitle("Annotate sleep related tweets");

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                clearLocks(sleepCollection, user + "_lock");
                System.exit(0);
            }
        });

        EtchedBorder border = new EtchedBorder();
        final JTextArea textArea = new JTextArea();
        textArea.setBorder(border);
        Font arialBolditalic12 = new Font("Arial", Font.BOLD + Font.ITALIC, 12);
        textArea.setFont(arialBolditalic12);
        textArea.setBounds(23, 62, 361, 209);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        Document start = sleepCollection.find(eq("_id", ids.get(count))).first();
        textArea.setText(start.get("text").toString());
        contentPane.add(textArea);

        final BasicArrowButton next = new BasicArrowButton(BasicArrowButton.EAST);
        next.setBounds(267, 12, 117, 25);

        contentPane.add(next);
        final BasicArrowButton previous = new BasicArrowButton(BasicArrowButton.WEST);
        previous.setBounds(22, 12, 117, 25);
        contentPane.add(previous);
        previous.setVisible(false);

        final JSlider slider = new JSlider();
        slider.setPreferredSize(new Dimension(300, 40));
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.setMaximum(10);
        slider.setMinimum(-10);
        slider.setValue(0);
        slider.setMajorTickSpacing(5);
        slider.setMinorTickSpacing(1);
        slider.setLabelTable(slider.createStandardLabels(5));
        slider.setBounds(401, 91, 199, 50);

        contentPane.add(slider);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                labelAsValue.setText("" + source.getValue());
            }
        });


        next.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                count++;
                if (count >= 0 && count < limit) {
                    reset(submitButton, extraText, typeExtraInfo, labelAsValue, slider);
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
                    reset(submitButton, extraText, typeExtraInfo, labelAsValue, slider);
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


        JLabel lblLevelOfConfidance = new JLabel("Numerical Label");
        lblLevelOfConfidance.setBounds(423, 61, 159, 15);
        contentPane.add(lblLevelOfConfidance);

        extraText = new JTextField();
        extraText.setBounds(397, 180, 185, 24);
        contentPane.add(extraText);
        extraText.setColumns(10);


        JLabel lblExtraInformation = new JLabel("Extra Information");
        lblExtraInformation.setBounds(441, 153, 141, 15);
        contentPane.add(lblExtraInformation);

        final String confidence = "user." + user + "." + "confidence";
        final String label = "user." + user + "." + "label";

        labelAsValue = new JTextField(15);
        labelAsValue.setDocument(new JTextFieldLimit(3));
        labelAsValue.setBounds(619, 91, 34, 24);
        contentPane.add(labelAsValue);
        labelAsValue.setColumns(10);


        labelAsValue.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = labelAsValue.getText();
                if (!text.equals("-") && !text.equals("")) {
                    int numb = Integer.parseInt(text);
                    slider.setValue(numb);
                }

//                if (!text.matches("[-+/\\*] \\d+") ) {
//                    return;
//                }

            }
        });

        submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tag = returnLabel(slider.getValue());
                Document doc = new Document(label, tag).append(confidence, slider.getValue());
                reset(submitButton, extraText, typeExtraInfo, labelAsValue, slider);
                sleepCollection.updateOne(eq("_id", ids.get(count)),
                        new Document("$set", doc));

                if (isItEmpty(extraText) || isItEmpty(typeExtraInfo)) {
                    if (validateField(extraText, typeExtraInfo)) {
                        System.out.println("update");
                        Document extraInfo = new Document(typeExtraInfo.getText(), extraText.getText());
                        sleepCollection.updateOne(eq("_id", ids.get(count)),
                                new Document("$set", extraInfo));
                    }
                }

                Object source = e.getSource();
                if (source instanceof Component) {
                    ((Component) source).setBackground(Color.GREEN);
                }
                reset(submitButton, extraText, typeExtraInfo, labelAsValue, slider);


            }
        });

        submitButton.setBounds(396, 236, 158, 35);
        contentPane.add(submitButton);
        typeExtraInfo.setBounds(605, 180, 109, 24);
        contentPane.add(typeExtraInfo);
        typeExtraInfo.setColumns(10);

        JLabel lblType = new JLabel("Type");
        lblType.setBounds(625, 153, 70, 15);
        contentPane.add(lblType);


    }

    private boolean isItEmpty(JTextField text) {
        if (text.getText().equals(""))
            return false;
        else
            return true;
    }

    private static void reset(JButton button1, JTextField field1, JTextField field3, JTextField field2, JSlider slider) {
        button1.setBackground(null);
        field1.setText(null);
        field3.setText(null);
        field2.setText(null);
        slider.setValue(0);
    }

    private static void clearLocks(MongoCollection<Document> sleepCollection, String userLock) {
        Document doc = new Document(userLock, "yes");
        sleepCollection.updateMany(doc,
                new Document("$unset", doc), new UpdateOptions().upsert(false));
    }

    private String returnLabel(int sliderValue) {
        if (sliderValue < 0)
            return "negative";
        else if (sliderValue == 0)
            return "neutral";
        else
            return "positive";
    }

    private boolean validateField(JTextField extraText, JTextField extraType) {
        if (!extraText.getText().trim().isEmpty() && extraType.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Type field empty");
            System.out.println("1");
            return false;
        } else if (extraText.getText().trim().isEmpty() && !extraType.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Extra information is empty");
            System.out.println("2");
            return false;
        } else
            return true;
    }
}	


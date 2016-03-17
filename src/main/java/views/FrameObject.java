package views;

import auth.ConnectionMongo;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by mladen on 3/17/16.
 */
public class FrameObject {
    private ArrayList<ObjectId> uniqueTweets;
    private MongoCollection<Document> collection;
    private ConnectionMongo connection;
    private String user;

    public FrameObject(ArrayList<ObjectId> Tweets,MongoCollection<Document> diagnosticCollection, ConnectionMongo connection,String user) {
        this.uniqueTweets = Tweets;

        this.collection = diagnosticCollection;
        this.connection = connection;
        this.user = user;
    }

    public static void generateGUI() {

        Document lock = new Document(user + "_lock", "yes");
        for (ObjectId tweet : ids) {
            diagnosticCollection.updateOne(eq("_id", tweet),
                    new Document("$set", lock));
        }


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 731, 350);
        setTitle("Annotate diagnostic tweets");
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                utilities.clearLocks(diagnosticCollection, user + "_lock");
                System.exit(0);
            }
        });

        EtchedBorder border = new EtchedBorder();
        final JTextArea textArea = new JTextArea();
        textArea.setBorder(border);
        Font arialBolditalic12 = new Font("Arial", Font.BOLD, 12);
        textArea.setFont(arialBolditalic12);
        textArea.setBounds(23, 62, 361, 209);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        Document start = diagnosticCollection.find(eq("_id", ids.get(count))).first();
        System.out.println(ids.get(count));
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
                    System.out.println(ids.get(count));
                    utilities.reset(submitButton, extraText, typeExtraInfo, labelAsValue, slider);
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
                    utilities.reset(submitButton, extraText, typeExtraInfo, labelAsValue, slider);
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

        JLabel lblLevelOfConfidance = new JLabel("Numerical Label");
        lblLevelOfConfidance.setBounds(423, 64, 159, 15);
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
        final String type = "user." + user + "." + "type";
        labelAsValue = new JTextField(15);
        labelAsValue.setDocument(new JTextFieldLimit(3));
        lblLevelOfConfidance.setBounds(423, 64, 159, 15);
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
                String tag = utilities.returnLabel(slider.getValue());
                Document doc = new Document(label, tag).append(confidence, slider.getValue());
                diagnosticCollection.updateOne(eq("_id", ids.get(count)),
                        new Document("$set", doc));
                if (utilities.isItEmpty(extraText) || utilities.isItEmpty(typeExtraInfo)) {
                    if (utilities.validateField(extraText, typeExtraInfo)) {
                        System.out.println("update");
                        Document extraInfo = new Document(typeExtraInfo.getText(), extraText.getText());
                        diagnosticCollection.updateOne(eq("_id", ids.get(count)),
                                new Document("$set", extraInfo));
                    }
                }

                Object source = e.getSource();
                if (source instanceof Component) {
                    ((Component) source).setBackground(Color.GREEN);
                }
                utilities.reset(submitButton, extraText, typeExtraInfo, labelAsValue, slider);

            }
        });
        submitButton.setBounds(396, 233, 158, 35);
        contentPane.add(submitButton);
        typeExtraInfo.setBounds(605, 180, 109, 24);
        contentPane.add(typeExtraInfo);
        typeExtraInfo.setColumns(10);

        JLabel lblType = new JLabel("Type");
        lblType.setBounds(625, 153, 70, 15);
        contentPane.add(lblType);
    }

}




}

package views;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import javax.swing.*;

/**
 * Created by mladen on 3/17/16.
 */
public final class Utilities {

    public static boolean isItEmpty(JTextField text) {
        return !text.getText().equals("");
    }

    public static String returnLabel(int sliderValue) {
        if (sliderValue < 0)
            return "negative";
        else if (sliderValue == 0)
            return "neutral";
        else
            return "positve";
    }

    public static void reset(JButton button1, JTextField field1, JTextField field3, JTextField field2, JSlider slider) {
        button1.setBackground(null);
        field1.setText(null);
        field3.setText(null);
        field2.setText(null);
        slider.setValue(0);
    }

    public static void clearLocks(MongoCollection<Document> sleepCollection, String userLock) {
        Document doc = new Document(userLock, "yes");
        sleepCollection.updateMany(doc,
                new Document("$unset", doc), new UpdateOptions().upsert(false));
    }

    public boolean validateField(JTextField extraText, JTextField extraType) {
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


    public static String[] assignUserLocks(String user){
        String oppositeLock1;
        String oppositeLock2;
        String [] locks;
        // Stick to if else because of java dependencies on my machine and researchers' machines
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
        locks = new String[]{oppositeLock1,oppositeLock2};
        return locks;
    }
}


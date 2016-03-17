package views;

import auth.ConnectionMongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.util.Util;
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
import java.util.HashMap;


import static com.mongodb.client.model.Filters.*;

public class DiagnosticTweetsFrame extends JFrame {
    private Utilities utilities = new Utilities();
    private JPanel contentPane;
    public int count;
    private static int count1 = 0;
    private static int count2 = 0;

    private static ArrayList<ObjectId> ids = new ArrayList<ObjectId>();
    private JTextField labelAsValue;
    private ConnectionMongo connection;
    private JTextField extraText;
    private JButton submitButton;
    private final JTextField typeExtraInfo = new JTextField();


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
        final String user = connection.username();
        String[] oppositeLocks;
        oppositeLocks = utilities.assignUserLocks(user);
        String oppositeLock2 = oppositeLocks[1];
        String oppositeLock1 = oppositeLocks[0];

        HashMap<Long, ObjectId> uniqueIds = new HashMap<Long, ObjectId>();
        int tweetsForAnnotation = 80;
//        int numberOfCommonTweets = ((int) (((double) 10 / 100) * tweetsForAnnotation));
        int numberOfCommonTweets = 20;
        int numberOfPotentialTweets = ((int) (((double) 70 / 100) * tweetsForAnnotation));
        int numberRandomTweets = ((int) (((double) 25 / 100) * tweetsForAnnotation));

        MongoCursor<Document> potentialDiagCur = diagnosticCollection.find(and(exists(oppositeLock1, false), exists("user", false),
                exists(oppositeLock2, false), eq("potentialDiagnostic", "yes"), exists("common", false))).iterator();

        MongoCursor<Document> randomTweetsCursor = diagnosticCollection.find(and(exists("potentialDiagnostic", false),
                exists("user.", false), exists(oppositeLock1, false), exists(oppositeLock2, false))).iterator();

        MongoCursor<Document> commonTweetsCursor = diagnosticCollection.find(and(eq("common", "yes"), eq("potentialDiagnostic", "yes"),
                exists("user." + user, false))).iterator();


        while (potentialDiagCur.hasNext() && numberOfPotentialTweets > 0) {
            uniqueIds.put((Long) potentialDiagCur.next().get("tweet_id"), (ObjectId) potentialDiagCur.next().get("_id"));
            numberOfPotentialTweets--;
        }
//        System.out.println(uniqueIds.size());
//
        if (commonTweetsCursor.hasNext()) {
            while (randomTweetsCursor.hasNext() && numberRandomTweets > 0) {
                Document das = randomTweetsCursor.next();
                Long tweet_id = (Long) das.get("tweet_id");
                ObjectId _id = (ObjectId) das.get("_id");
                uniqueIds.put(tweet_id, _id);
                numberRandomTweets--;
            }
            while (commonTweetsCursor.hasNext() && numberOfCommonTweets > 0) {
                Document das = commonTweetsCursor.next();
                Long tweet_id = (Long) das.get("tweet_id");
                ObjectId _id = (ObjectId) das.get("_id");
                uniqueIds.put(tweet_id, _id);
                numberOfCommonTweets--;
            }
        } else {
            int accumulator = numberOfCommonTweets + numberRandomTweets;
            while (randomTweetsCursor.hasNext() && accumulator > 0) {
                uniqueIds.put((Long) randomTweetsCursor.next().get("tweet_id"), (ObjectId) randomTweetsCursor.next().get("_id"));
                accumulator--;
            }
        }

        for (ObjectId value : uniqueIds.values()) {
            ids.add(value);
        }

        System.out.println(ids.size());
        Collections.shuffle(ids);
        final int limit = ids.size();
}
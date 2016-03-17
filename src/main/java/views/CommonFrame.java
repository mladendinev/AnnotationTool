package views;

import auth.ConnectionMongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.mongodb.client.model.Filters.*;

/**
 * Created by mladen on 3/17/16.
 */
public class CommonFrame {
    private auth.ConnectionMongo connection;
    private Utilities utilities = new Utilities();
    private ArrayList<ObjectId> ids = new ArrayList<ObjectId>();

    private String[] locks;

    private MongoCollection<Document> collection;
    private String user;

    public CommonFrame(MongoCollection<Document> collection, ConnectionMongo connection) {
        this.connection = connection;
        this.collection = collection;
        this.user = connection.username();

    }

    public ArrayList<ObjectId> sleepTweets() {
        int numberTweetsForAnnot = 100;


        locks = Utilities.assignUserLocks(user);
        final String oppositeLock1 = locks[0];
        final String oppositeLock2 = locks[1];

        MongoCursor<Document> sleepTweetsCur = collection.find(and(exists(oppositeLock1, false), exists("user", false),
                exists(oppositeLock2, false))).iterator();

        while (sleepTweetsCur.hasNext() && numberTweetsForAnnot > 0) {
            ids.add((ObjectId) sleepTweetsCur.next().get("_id"));
            numberTweetsForAnnot--;
        }

        final int limit = ids.size();
        Collections.shuffle(ids);

        final Document lock = new Document(user + "_lock", "yes");

        for (ObjectId tweet : ids) {
            collection.updateOne(eq("_id", tweet),
                    new Document("$set", lock));
        }
        return ids;
    }

    public ArrayList<ObjectId> diagnosticTweets() {
        HashMap<Long, ObjectId> uniqueIds = new HashMap<Long, ObjectId>();
        int tweetsForAnnotation = 80;
        //        int numberOfCommonTweets = ((int) (((double) 10 / 100) * tweetsForAnnotation));
        int numberOfCommonTweets = 20;
        int numberOfPotentialTweets = ((int) (((double) 70 / 100) * tweetsForAnnotation));
        int numberRandomTweets = ((int) (((double) 25 / 100) * tweetsForAnnotation));

        locks = Utilities.assignUserLocks(user);
        final String oppositeLock1 = locks[0];
        final String oppositeLock2 = locks[1];

        MongoCursor<Document> potentialDiagCur = collection.find(and(exists(oppositeLock1, false), exists("user", false),
                exists(oppositeLock2, false), eq("potentialDiagnostic", "yes"), exists("common", false))).iterator();

        MongoCursor<Document> randomTweetsCursor = collection.find(and(exists("potentialDiagnostic", false),
                exists("user.", false), exists(oppositeLock1, false), exists(oppositeLock2, false))).iterator();

        MongoCursor<Document> commonTweetsCursor = collection.find(and(eq("common", "yes"), eq("potentialDiagnostic", "yes"),
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
        return ids;
    }


    public void showDiagnosticFrame() {
        ArrayList<ObjectId> ids = diagnosticTweets();
        FrameObject diagnosticFrame = new FrameObject(ids, collection, connection, user, "diagnostic");
        diagnosticFrame.generateGUI();
    }

    public void showSleepFrame() {
        ArrayList<ObjectId> ids = sleepTweets();
        FrameObject diagnosticFrame = new FrameObject(ids, collection, connection, user, "sleep");
        diagnosticFrame.generateGUI();
    }

}

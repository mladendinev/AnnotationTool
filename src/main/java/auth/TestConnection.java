package auth;

import java.util.Collections;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class TestConnection {
	
	public static void main(String[] args){
		try{
			char [] testPassword =  {'f','i','n','a','l','Y','e','a','r','P','r','o','j','e','c','t'};
			String testUsername = "aaaa";
			MongoCredential credential = MongoCredential.createCredential(testUsername, "SearchApiResults", testPassword);
			MongoClientOptions mongoOptions=MongoClientOptions.builder().serverSelectionTimeout(1000).readPreference(ReadPreference.primaryPreferred()).build();
	        @SuppressWarnings("resource")
			MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27017), Collections.singletonList(credential),mongoOptions);
			MongoDatabase db = mongoClient.getDatabase("SearchApiResults");
			MongoCollection<Document> collection =  db.getCollection("testEncrypt");
			collection.count();
//			mongoClient.close();
		}


		catch (com.mongodb.MongoSecurityException ex)
		{
			throw new RuntimeException();
		}

		catch (MongoCommandException ex){
			System.err.println("errorrrr");
		}
	}

}

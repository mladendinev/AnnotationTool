package auth;

import java.util.Collections;

import com.mongodb.*;
import com.mongodb.client.MongoDatabase;

public class ConnectionMongo {
	
	private String user;
	private char[] pass;
	MongoClient mongoClient;
	MongoDatabase db;
	
	public ConnectionMongo(String username, char[] password){
		user = username;
		pass = password;
	}
	
    public void authenticate() throws Exception
    {
    	try 
        {	
            MongoCredential credential = MongoCredential.createCredential(user, "mbax2md2", pass);
            MongoClientOptions mongoOptions=MongoClientOptions.builder().serverSelectionTimeout(1000).readPreference(ReadPreference.primaryPreferred()).build();
            mongoClient = new MongoClient(new ServerAddress("130.88.192.221", 27018), Collections.singletonList(credential),mongoOptions);
            db = mongoClient.getDatabase("mbax2md2");
            
            // Test the connection - if this line fails the connection has not been established
            db.getCollection("streamDiagnostic").count();
        }
        catch (Exception e) 
        {
           System.out.println("Not authorised connection");
           throw new RuntimeException(e);
        }
   }

   public MongoClient client(){
	   return this.mongoClient;
   }
   
   public String username(){
	   return this.user;
   }
   
   public MongoDatabase database(){
	   return this.db;
   }
}

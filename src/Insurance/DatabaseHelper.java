package Insurance;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DatabaseHelper {
	private static MongoClient mongoClient;
	private static MongoDatabase database;
	
	private static String PACKAGES_TABLE = "insurance-package";
	private static String PLANS_TABLE = "insurance-plan";
	
	public DatabaseHelper(String databaseName) {
		mongoClient = new MongoClient(new MongoClientURI("mongodb+srv://Gehad:Aboarab97@cloud-computing-zqxty.mongodb.net/test?retryWrites=true"));
		database = mongoClient.getDatabase(databaseName);
	}
	
	// Returns all packages offered by the insurance company
	public static JSONObject getAllPackages(String insurance) {
		MongoCollection<Document> collection = database.getCollection(PACKAGES_TABLE);
		FindIterable<Document> documents = collection.find();

		JSONObject result = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		// Adds the id to each document
		for(Document document:documents) {
			if(document.getString("company").equals(insurance) || insurance.equals("all")){
				document.append("id", document.get("_id").toString());
				document.remove("_id");
				jsonArray.put(document);
			}
		}
		
		try {
			result.put("result", jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	// Returns the plan based on reference number
	public static JSONObject getPlan(String insurance, String reference_no) {
		MongoCollection<Document> collection = database.getCollection(PLANS_TABLE);
		FindIterable<Document> documents = collection.find();

		// Loop through the documents and return needed plan
		for(Document document : documents) {
			JSONObject result;
			
			try {
				if(document.get("company").equals(insurance) && document.get("reference-no").equals(reference_no)){
					document.append("id", document.get("_id").toString());
					document.remove("_id");
					
					result = new JSONObject(document);
					return result;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return new JSONObject();
	}
	
	// Registers user to a new plan
	public static JSONObject registerToPackage(String insurance, String name, String contact, String package_no) {
		MongoCollection<Document> collection = database.getCollection(PLANS_TABLE);
		
		// Delete customer from current plan
		Bson filter = new Document("name", name);
		collection.deleteOne(filter);
		
		// Find today's date
		String pattern = "dd/MM/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		
		// Insert new customer plan to plans table
		Document customer = new Document();
        customer.put("name", name);
        customer.put("contact", contact);
        customer.put("package-no", package_no);
        customer.put("expiry", date);
        customer.put("company", insurance);
        collection.insertOne(customer);
        
        // Add reference number (hacky way)
		FindIterable<Document> documents = collection.find();
		String id = null;
		for(Document document : documents) {
			if(document.get("name").equals(name) && document.get("contact").equals(contact)) {
				id = document.get("_id").toString();
				Bson condition = new Document("name", name);
				Bson newValue = new Document("reference-no", id);
				Bson updateOperationDocument = new Document("$set", newValue);
				collection.updateOne(condition, updateOperationDocument);
				document.remove("_id");
			}
		}
		
		for(Document document : documents) {
			if(document.get("name").equals(name) && document.get("contact").equals(contact)) {
				document.remove("_id");
				return new JSONObject(document);
			}
		}
		return new JSONObject();
	}
	
	// Renews the user's current plan using reference number
	public static JSONObject renewPlan(String insurance, String reference_no, String credit_card, double amount) {
		MongoCollection<Document> collection = database.getCollection(PLANS_TABLE);
		FindIterable<Document> documents = collection.find();
		try {
			for(Document document:documents) {
//				JSONObject object = new JSONObject(document.toJson());
				
				if(document.get("company").equals(insurance) && document.get("reference-no").equals(reference_no)){
					// Find today's date
					String pattern = "dd/MM/yyyy";
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
					String date = simpleDateFormat.format(new Date());

					// Update the expiry date
					Bson filter = new Document("reference-no", reference_no);
					Bson newValue = new Document("expiry", date);
					Bson updateOperationDocument = new Document("$set", newValue);
					collection.updateOne(filter, updateOperationDocument);
				}
			}
		
			// Return the modified object
			for(Document document : documents) {
				if(document.get("reference-no").equals(reference_no)) {
					document.remove("_id");
					return new JSONObject(document);
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return new JSONObject();
	}
	
	// For testing
	public static void main(String[] args) {
		mongoClient = new MongoClient(new MongoClientURI("mongodb+srv://Gehad:Aboarab97@cloud-computing-zqxty.mongodb.net/test?retryWrites=true"));
		database = mongoClient.getDatabase("cloud-computing");
		
//		System.out.println(getAllPackages("adamjee"));
//		System.out.println(getPlan("axa","5cbcc09c0654182584ddfec0"));
//		System.out.println(registerToPackage("Maryam","maryam@gmail.com","12"));
//		System.out.println(renewPlan("5cbcc09c0654182584ddfec0", "1111", 111));
	}
}

package Insurance;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

@Path("/insurance/{insurance}")
public class InsuranceWebServices {
	private static final DatabaseHelper database = new DatabaseHelper("cloud-computing");
	
	@GET
	@Path("get-packages")
	@Produces(MediaType.TEXT_PLAIN)
	public String getAllPackages(@PathParam("insurance") String insurance) {
		// Retrieve packages from the database
		return database.getAllPackages(insurance).toString();
	}
	
	@GET
	@Path("get-plan/{reference-no}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getPlan(@PathParam("insurance") String insurance,
			@PathParam("reference-no") String reference_no) {
		return database.getPlan(insurance, reference_no).toString();
	}
	
	@GET
	@Path("register-package/{name}/{contact}/{package-no}")
	@Produces(MediaType.TEXT_PLAIN)
	public String registerToPackage(@PathParam("insurance") String insurance,
			@PathParam("name") String name, 
			@PathParam("contact") String contact, 
			@PathParam("package-no") String package_no) {
		return database.registerToPackage(insurance, name, contact, package_no).toString();
	}
	
	@GET
	@Path("renew-plan/{reference-no}/{credit-card}/{amount}")
	@Produces(MediaType.TEXT_PLAIN)
	public String renewPlan(@PathParam("insurance") String insurance,
			@PathParam("reference-no") String reference_no,
			@PathParam("credit-card") String credit_card, 
			@PathParam("amount") double amount) {
		return database.renewPlan(insurance, reference_no, credit_card, amount).toString();
	}

}

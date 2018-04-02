package ai.talentify.wikidata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class InsertProperties {
	public static void main(String[] args) throws IOException {
		String url = "https://query.wikidata.org/sparql?format=json&query=SELECT%20%3Fproperty%20%3FpropertyLabel%20WHERE%20%7B%0A%20%20%20%20%3Fproperty%20a%20wikibase%3AProperty%20.%0A%20%20%20%20SERVICE%20wikibase%3Alabel%20%7B%0A%20%20%20%20%20%20bd%3AserviceParam%20wikibase%3Alanguage%20%22en%22%20.%0A%20%20%20%7D%0A%20%7D%0A%0A";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		int responseCode = con.getResponseCode();
		System.out.println("GET Response Code :: " + responseCode);
		StringBuffer response = new StringBuffer();
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result

		} else {
			System.out.println("GET request not worked");
		}

		/**
		 * { head: {}, results: { bindings: [ { property: { type: "uri", value:
		 * "http://www.wikidata.org/entity/P6" }, propertyLabel: { xml:lang: "en", type:
		 * "literal", value: "head of government" } },
		 */

		JsonElement parse = new JsonParser().parse(response.toString());
		JsonObject asJsonObject = parse.getAsJsonObject();
		JsonObject results = asJsonObject.get("results").getAsJsonObject();
		JsonArray bindings = results.get("bindings").getAsJsonArray();
		for (JsonElement binding : bindings) {
			JsonObject bindingObject = binding.getAsJsonObject();
			JsonObject property = bindingObject.get("property").getAsJsonObject();
			JsonObject propertyLabel = bindingObject.get("propertyLabel").getAsJsonObject();
			String propUrl = property.get("value").getAsString();
			String proplabel = propertyLabel.get("value").getAsString();
			String propID = propUrl.substring(propUrl.lastIndexOf("/") + 1, propUrl.length());
			String sql = " INSERT INTO \"public\".\"property\" (\"propID\", \"propLabel\") VALUES ('" + propID + "', '" + proplabel
					+ "');";
			Runnable myRunnable = new Runnable() {

				public void run() {
					Statement stmt;
					try {
						stmt = DBConnection.getInstance().createStatement();
						stmt.executeUpdate(sql);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					System.out.println("Added property: "+propID);
				}
			};

			Thread thread = new Thread(myRunnable);
			thread.start();

		}
	}
}

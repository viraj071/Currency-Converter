package net.virajshah.currencyconverter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import net.virajshah.currencyconverter.constants.ApiConstants;

public class CurrencyConverter{
	
	// this object is used for executing requests to the (REST) API
	static CloseableHttpClient httpClient = HttpClients.createDefault();
	
	public static Set<String> currencyCodes = new HashSet<String>(0);
	
	// Gets a valid list of currencies from a file.
	public static void getCurrencyList(){
		try (BufferedReader br = new BufferedReader(new FileReader("/Users/viraj/Documents/eclipseworkspace/CurrencyConverter/src/net/virajshah/currencyconverter/currencyList.txt"))){
		    String line;
			while ((line = br.readLine()) != null) {
		       // process the line.
		    	 currencyCodes.add(line);
		    }
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	public static void sendConvertRequest(Double amount, String source, String target){
		
				HttpGet get = new HttpGet(ApiConstants.BASE_URL + ApiConstants.END_POINT + "?base=" + source + "&symbols=" + target);
				try {
					CloseableHttpResponse response =  httpClient.execute(get);
					HttpEntity entity = response.getEntity();
					JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
					
					System.out.println("Currency Conversion");
					
//					// parsed JSON Objects are accessed according to the JSON resonse's hierarchy, output strings are built
					System.out.println("Last Updated Date : " +jsonObject.getString("date"));
					System.out.println("From : " + jsonObject.getString("base"));
					System.out.println("To : " + target);
					Double conversionRate = jsonObject.getJSONObject("rates").getDouble(target);
					System.out.println("Amount : " + conversionRate);
					System.out.println("Conversion Result : " + amount * conversionRate);
					System.out.println("\n");
					response.close();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	
	public static void main(String args[]) throws IOException{
		Double amount = Double.parseDouble(args[0]);
		String source = args[1];
		String target = args[2];
		getCurrencyList();
		if(args.length!=3){
			System.out.println("Invalid number of arguments.");
		}
		if(!currencyCodes.contains(source)){
			System.out.println("Invalid source currency");
			System.exit(1);
		}
		if(!currencyCodes.contains(target)){
			System.out.println("Invalid target currency");
			System.exit(1);
		}
		sendConvertRequest(amount,source,target);
		httpClient.close();
	}
	
}
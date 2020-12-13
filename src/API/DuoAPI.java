package API;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.gson.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import static API.Base64.encodeBytes;


public class DuoAPI {
    static String integrationKey = "DIU1DPD7XV84DO7DLNZE";
    static String secretKey = "QhVqjNlNrRV1XdyAuNuNcSBnO2QJqxTG9Pb02vrV";
    static String apiHostname = "https://api-50927531.duosecurity.com";


    // https://duo.com/docs/authapi#api-details
    // All methods must start with apiHostname
    // All responses are in JSON
    // {
    //  "stat": "OK",
    //  "response": {
    //    "key": "value"
    //  }
    //}
    // If fail, stat will equal FAIL


    public static String ping() throws MalformedURLException {
        URL pingUrl = new URL("https://api-50927531.duosecurity.com/auth/v2/ping");
        String response = "Not Online";
        try {
            // Missing URL
            HttpsURLConnection conn = (HttpsURLConnection) pingUrl.openConnection();
            conn.setDoOutput(true);
            if (conn.getResponseCode() == 200) {
                System.out.println("Ping response: ");
                returnResponse(conn.getInputStream());
                response = "Online";
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        return response;
    }


    public static String check() throws MalformedURLException {
        Date time = new Date();

        SimpleDateFormat newDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);

        String response = "Unauthorized";

        try {

            URL url = new URL("https://api-50927531.duosecurity.com/auth/v2/check");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Date", newDate.format(time));
            String auth = integrationKey + ":" + HMAC.calculateRFC2104HMAC(HMACSignature(time, "GET", "/auth/v2/check" ), secretKey);
            conn.setRequestProperty("Authorization", "Basic " + encodeBytes(auth.getBytes()));

            conn.setDoOutput(true);
            if(conn.getResponseMessage().equals("OK") && conn.getResponseCode() == 200){
                System.out.println("Check response: ");
                returnResponse(conn.getInputStream());
                response = "Authorized";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public static JSONObject enroll() throws MalformedURLException{
        SimpleDateFormat newDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
        URL url = new URL("https://api-50927531.duosecurity.com/auth/v2/enroll");
        Date time = new Date();
        JSONObject obj = new JSONObject();
        try{
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Date", newDate.format(time));
            String auth = integrationKey + ":" + HMAC.calculateRFC2104HMAC(HMACSignature(time, "POST", "/auth/v2/enroll"), secretKey);
            conn.setRequestProperty("Authorization", "Basic " + encodeBytes(auth.getBytes()));

            conn.setDoOutput(true);

            JSONParser parser = new JSONParser();
            obj = (JSONObject) parser.parse(returnResponse(conn.getInputStream()));

        }catch(Exception e){
            e.printStackTrace();
        }
        return obj;
    }

//    public static JSONObject enrollStatus(String userID, String activation) throws MalformedURLException{
//        SimpleDateFormat newDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
//        URL url = new URL("https://api-50927531.duosecurity.com/auth/v2/enroll_status");
//        Date time = new Date();
//        JSONObject obj = new JSONObject();
//
//        try{
//            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            conn.setRequestProperty("Date", newDate.format(time));
//            String auth = integrationKey + ":" + HMAC.calculateRFC2104HMAC(HMACSignature(time, "POST", "/auth/v2/enroll_status", "activation_code=" + activation + "&user_id=" + userID ), secretKey);
//            conn.setRequestProperty("Authorization", "Basic " + encodeBytes(auth.getBytes()));
//
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
//
//            OutputStream os = conn.getOutputStream();
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//            writer.write("activation_code=" + activation + "&user_id=" + userID);
//
//            writer.flush();
//            writer.close();
//            os.close();
//
//            JSONParser parser = new JSONParser();
//            obj = (JSONObject) parser.parse(returnResponse(conn.getInputStream()));
//
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return obj;
//    }
//
//    public static JSONObject preauth(String un) throws MalformedURLException{
//        SimpleDateFormat newDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
//        URL url = new URL("https://api-50927531.duosecurity.com/auth/v2/preauth?username=" + un);
//        Date time = new Date();
//        JSONObject obj = new JSONObject();
//
//        try{
//            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            conn.setRequestProperty("Date", newDate.format(time));
//            String auth = integrationKey + ":" + HMAC.calculateRFC2104HMAC(HMACSignature(time, "POST", "/auth/v2/preauth", "username=" + un ), secretKey);
//            conn.setRequestProperty("Authorization", "Basic " + encodeBytes(auth.getBytes()));
//
//            conn.setDoOutput(true);
//
//            JSONParser parser = new JSONParser();
//            obj = (JSONObject) parser.parse(returnResponse(conn.getInputStream()));
//
//            if(conn.getResponseCode() == 200){
//                returnResponse(conn.getInputStream());
//            }
//
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return obj;
//
//    }

    public static JSONObject auth(String username) throws MalformedURLException{
        SimpleDateFormat newDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
        URL url = new URL("https://api-50927531.duosecurity.com/auth/v2/auth");
        Date time = new Date();
        JSONObject obj = new JSONObject();


        try{
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Date", newDate.format(time));
            String auth = integrationKey + ":" + HMAC.calculateRFC2104HMAC(HMACSignature(time, "POST", "/auth/v2/auth", "device=auto&factor=auto&username=" + username ), secretKey);
            conn.setRequestProperty("Authorization", "Basic " + encodeBytes(auth.getBytes()));

            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write("device=auto&factor=auto&username=" + username);

            writer.flush();
            writer.close();
            os.close();

            JSONParser parser = new JSONParser();
            obj = (JSONObject) parser.parse(returnResponse(conn.getInputStream()));

            System.out.println(newDate.format(time));
            System.out.println("Basic " + encodeBytes(auth.getBytes()));
            System.out.println(conn.getResponseCode());
            System.out.println(conn.getResponseMessage());

            if(conn.getResponseCode() == 200){
                returnResponse(conn.getInputStream());
            }



        }catch(Exception e){
            e.printStackTrace();
        }
        return obj;
    }



    public static String HMACSignature(Date t, String m, String p) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
        String duoDate = formatter.format(t);
        String method = m.toUpperCase();
        String hostname = "api-50927531.duosecurity.com";
        String path = p;
        String params = "";
        String textToSign = duoDate + "\n" + method + "\n" + hostname + "\n" + path + "\n" + params;
        return textToSign;
    }

    public static String HMACSignature(Date t, String m, String p, String par) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
        String duoDate = formatter.format(t);
        String method = m.toUpperCase();
        String hostname = "api-50927531.duosecurity.com";
        String path = p;
        String params = par;
        String textToSign = duoDate + "\n" + method + "\n" + hostname + "\n" + path + "\n" + params;
        return textToSign;
    }

    public static String returnResponse(InputStream s){
        InputStream inputstream = s;
        BufferedReader in = new BufferedReader(new InputStreamReader(inputstream));
        StringBuilder res = new StringBuilder();
        String currentLine;

        try{
            while ((currentLine = in.readLine()) != null){
                res.append(currentLine);
            }
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }


        System.out.println(res);
        return res.toString();
    }
}


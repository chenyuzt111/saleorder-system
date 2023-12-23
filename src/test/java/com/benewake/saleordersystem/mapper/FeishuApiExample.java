package com.benewake.saleordersystem.mapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FeishuApiExample {

    public static void main(String[] args) {
        String appId = "cli_a5e56060a07ad00c";
        String redirectUri = "http://localhost:8080/benewake/callback";

        // Construct the URL
        String url = "https://open.feishu.cn/open-apis/authen/v1/authorize" +
                "?app_id=" + appId +
                "&redirect_uri=" + redirectUri;

        // Execute the method and print the response
        String response = executeGetMethod(url);
        System.out.println("Response: " + response);
    }

    public static String executeGetMethod(String url) {
        try {
            // Create URL object
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Set the request method to GET
            con.setRequestMethod("GET");

            // Get the response
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception as needed (e.g., logging, throwing a custom exception)
            return null;
        }
    }
}

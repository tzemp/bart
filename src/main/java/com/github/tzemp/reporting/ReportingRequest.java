package com.github.tzemp.reporting;

import com.github.tzemp.parser.ParserSummary;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.List;

/**
 * This Class performs a request to the remote reporting tool
 * via an simple HTTP Post
 */
public class ReportingRequest {
    private static final String POST_URL = "https://bart.tzemp.ch/report/new";
    private static final String USER_AGENT = "Mozilla/5.0";
    private Gson gson;

    public ReportingRequest() {
        this.gson = new Gson();
    }

    public void post(ParserSummary parserSummary, List<String> log, String project) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(ReportingRequest.POST_URL);

        // add header
        post.setHeader("User-Agent", USER_AGENT);
        Report report = new Report(project, parserSummary, log);
        StringEntity input = new StringEntity(gson.toJson(report));
        input.setContentType("application/json");

        post.setEntity(input);

        // send post request
        HttpResponse response = httpClient.execute(post);
        System.out.println("Post parameters : " + post.getEntity());
        System.out.println("Response Code : " +
                response.getStatusLine().

                        getStatusCode());
    }


}

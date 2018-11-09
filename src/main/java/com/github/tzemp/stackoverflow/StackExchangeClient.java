package com.github.tzemp.stackoverflow;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * StackExchangeClient
 * Allows to connect to StackExchange and offers
 * some basic function to query StackExchange
 */
public class StackExchangeClient {

    public static final String VERSION = "2.2/";
    public static final String SITE = "stackoverflow";
    public static final String BASE_URL = "https://api.stackexchange.com/";
    public static final String ADVANCED_SEARCH = "search/advanced";
    public static final String FILTER = "!b0OfNINZ1L6F(H";

    private String site;

    public StackExchangeClient(String site) {
        this.site = site;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    /**
     * Find all StackExchangeAnswers based on a Question
     * @return list of StackExchangeAnswers
     */
    public List<StackExchangeAnswer> getAnswersByIds(List<Long> ids) throws IOException, URISyntaxException {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("order", "desc");
        parameters.put("sort", "activity");
        parameters.put("filter","!9YdnSIaD(");
        Type type = new TypeToken<StackExchangeQuery<StackExchangeAnswer>>(){}.getType();
        StackExchangeQuery<StackExchangeAnswer> stackExchangeQuery = this.call("questions/"+ StringUtils.join(ids, ";")+"/answers", parameters, type);
        return stackExchangeQuery.getItems();
    }

    /**
     * Performs a search given the provided query string
     * @return List of StackExchangeQuestions
     */
    public List<StackExchangeQuestion> search(String query) throws IOException, URISyntaxException {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("q", query);
        parameters.put("order", "desc");
        parameters.put("sort", "relevance");
        //parameters.put("filter", "!-*f(6rc.(Xr5");
        parameters.put("filter", "!LUcFBE)OvU1pAOalIO3dXe");
        //parameters.put("filter","!9YdnSIaD(");
        Type type = new TypeToken<StackExchangeQuery<StackExchangeQuestion>>(){}.getType();
        StackExchangeQuery<StackExchangeQuestion> stackExchangeQuery = this.call("search/advanced", parameters, type);
        return stackExchangeQuery.getItems();
    }

    /**
     * Helper Function to read the content of the HTTPResponse
     */
    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    /**
     * Performs the actual call to StackExchange by opening a Apache HTTPClient
     * and performing a GET call providing the necessary parameters
     * Receives a JSON Response from StackExchange which is then deserialized
     * into the queried Java Objects (such as StackExchangeQuestion, ...)
     */
    private <T> StackExchangeQuery<T> call(String function, HashMap<String, String> parameters, Type type) throws URISyntaxException, IOException {

        HttpClient httpClient = HttpClientBuilder.create().build();

        URIBuilder uriBuilder = new URIBuilder(BASE_URL + VERSION + function);
        parameters.forEach(uriBuilder::addParameter);
        uriBuilder.addParameter("site", this.getSite());

        // Build and execute the query
        HttpGet httpGetRequest = new HttpGet(uriBuilder.build());
        HttpResponse httpResponse = httpClient.execute(httpGetRequest);
        HttpEntity entity = httpResponse.getEntity();

        if (entity != null) {
            InputStream inputStream = entity.getContent();
            try {
                int bytesRead = 0;

                // read json response
                // inputStream = new GZIPInputStream(inputStream);
                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);

                GsonBuilder builder = new GsonBuilder();
                        builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

                // deserialize
                builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return new Date(json.getAsJsonPrimitive().getAsLong());
                    }
                });

                Gson gson = builder.create();
                return gson.fromJson(jsonText, type);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (Exception ignore) {
                }
            }
        }

        return null;
    }
}


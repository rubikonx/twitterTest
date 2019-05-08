package org.sample.twitter;

import io.restassured.RestAssured;
import io.restassured.authentication.OAuthSignature;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class ApiTest {
    private String tweetId;
    private String createdTime;
    private String tweetText = "Sample from code #1";

    @BeforeClass
    public void setup() {
        // You need to set valid credentials here
        final String consumerKey = "%SET_VALID_CREDENTIAL_HERE%";
        final String consumerSecret = "%SET_VALID_CREDENTIAL_HERE%";
        final String accessToken = "%SET_VALID_CREDENTIAL_HERE%";
        final String accessSecret = "%SET_VALID_CREDENTIAL_HERE%";

        RestAssured.baseURI = "https://api.twitter.com";
        RestAssured.basePath = "/1.1/statuses";
        RestAssured.authentication = oauth(consumerKey, consumerSecret, accessToken, accessSecret, OAuthSignature.HEADER);
    }

    @Test(priority = 1)
    public void createTweetTest() {
        Response response = createTweet(tweetText);

        tweetId = response.getBody().path("id_str");
        createdTime = response.getBody().path("created_at");

        getStatus().body("text[0]", equalTo(tweetText));
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(createTweet(tweetText).getStatusCode(), 403);
    }

    @Test(priority = 2)
    public void getTweetsTest() {
        getStatus().
                body("id_str[0]", equalTo(tweetId)).
                body("created_at[0]", equalTo(createdTime)).
                body("retweet_count[0]", equalTo(0)).
                body("text[0]", equalTo(tweetText));
    }

    @Test(priority = 3)
    public void removeTweetTest() {
        removeTweet(tweetId);
        getStatus().body("id_str[0]", Matchers.nullValue());
    }

    private ValidatableResponse getStatus() {
        return when().
                get("/home_timeline.json").
                then().
                assertThat().
                statusCode(200);
    }

    private Response createTweet(final String text) {
        return given().
                queryParam("status", text).
                post("/update.json");
    }

    private void removeTweet(final String tweetId) {
        when().
                post(String.format("/destroy/%s.json", tweetId)).
                then().
                assertThat().
                statusCode(200);
    }
}

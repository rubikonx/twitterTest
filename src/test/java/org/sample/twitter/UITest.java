package org.sample.twitter;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class UITest {

    private       SelenideElement timelineHeader = $(".HomeEmptyTimeline-header");
    private       SelenideElement messageBar     = $(".message-text");
    private       SelenideElement statusBar      = $(".new-tweets-bar");
    private final String          tweetText      = "Sample tweet from UI tests";

    @BeforeClass
    public void setup() {
        Configuration.timeout = TimeUnit.SECONDS.toMillis(10);
        Configuration.baseUrl = "https://twitter.com";
        Configuration.startMaximized = true;

        open("/login");
        login("%SET_VALID_CREDENTIAL_HERE%", "%SET_VALID_CREDENTIAL_HERE%");  // You need to set valid login and password
    }

    @Test(priority = 1)
    public void createTweetTest() {
        timelineHeader.shouldHave(visible, text("What? No Tweets yet?"));
        sendTweet(tweetText);
        messageBar.shouldHave(visible, text("Your Tweet was sent."));
        messageBar.waitUntil(disappear, TimeUnit.SECONDS.toMillis(10));
        sendTweet(tweetText);
        messageBar.shouldHave(visible, text("You have already sent this Tweet."));
        closeSendTwitterForm();
    }

    @Test(priority = 2)
    public void removeTweetTest() {
        refresh();
        if (statusBar.isDisplayed()) { statusBar.click(); }
        $(".ProfileTweet-action--more button").click();
        $(".js-actionDelete > button").click();
        $(byId("delete-tweet-dialog-header")).shouldHave(visible, text("Are you sure you want to delete this Tweet?"));
        $(".delete-action").click();
        messageBar.shouldHave(visible, text("Your Tweet has been deleted."));
        refresh();
        timelineHeader.shouldHave(visible, text("What? No Tweets yet?"));
    }

    private void login(String name, String pass) {
        $(".js-username-field").val(name);
        $(".js-password-field").val(pass);
        $("button.submit").click();
    }

    private void sendTweet(final String text) {
        $(byId("global-new-tweet-button")).click();
        $(byText("Compose new Tweet")).shouldBe(visible);
        $(".is-fakeFocus .tweet-box[name=tweet]").val(text);
        $("button.SendTweetsButton").shouldBe(enabled).click();
    }

    private void closeSendTwitterForm() {
        $("div.message a").click();
        $(".modal-content + button.modal-btn").click();
        $("#confirm_dialog_submit_button").click();
    }
}
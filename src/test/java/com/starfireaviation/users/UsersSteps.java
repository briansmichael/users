/*
 *  Copyright (C) 2022 Starfire Aviation, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.starfireaviation.users;

import com.starfireaviation.model.CommonConstants;
import com.starfireaviation.model.User;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class UsersSteps {

    /**
     * URL.
     */
    protected static final String URL = "http://localhost:8080";

    /**
     * ORGANIZATION.
     */
    protected static final String ORGANIZATION = "TEST_ORG";

    /**
     * RestTemplate.
     */
    protected RestTemplate restTemplate = new RestTemplateBuilder()
            .errorHandler(new RestTemplateResponseErrorHandler()).build();

    @Autowired
    protected TestContext testContext;

    @Before
    public void init() {
        testContext.reset();
    }

    @Given("^I have a user$")
    public void iHaveAUser() throws Throwable {
        testContext.setUser(new User());
    }

    @Given("^I have a password$")
    public void iHaveAPassword() throws Throwable {
        // TODO
    }

    @Given("^I have a username")
    public void iHaveAUsername() throws Throwable {
        // TODO
    }

    @And("^The user has (.*) with (.*) characters$")
    public void theUserHasXWithYCharacters(final String fieldName, final int count) throws Throwable {
        // TODO
    }

    @And("^A user exists$")
    public void aUserExists() throws Throwable {
        // TODO
    }

    @And("^The user's (.*) is modified to be (.*) characters$")
    public void theUsersXIsModifiedToBeYCharacters(final String fieldName, final int count) throws Throwable {
        // TODO
    }

    @When("^I submit the user$")
    public void iAddTheUser() throws Throwable {
        log.info("I submit the user");
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (testContext.getOrganization() != null) {
            headers.add(CommonConstants.ORGANIZATION_HEADER_KEY, testContext.getOrganization());
        }
        if (testContext.getCorrelationId() != null) {
            headers.add(CommonConstants.CORRELATION_ID_HEADER_KEY, testContext.getCorrelationId());
        }
        //final HttpEntity<Question> httpEntity = new HttpEntity<>(testContext.getQuestion(), headers);
        //testContext.setResponse(restTemplate.postForEntity(URL, httpEntity, Void.class));
    }

    @When("^I submit the user for update$")
    public void iSubmitTheUserForUpdate() throws Throwable {
        // TODO
    }

    @When("^I get all users$")
    public void iGetAllUsers() throws Throwable {
        // TODO
    }

    @When("^I check a username to see if it has been used$")
    public void iCheckAUsernameToSeeIfItHasBeenUsed() throws Throwable {
        // TODO
    }

    @When("^I check a password to see if it has been compromised$")
    public void iCheckAPasswordToSeeIfItHasBeenCompromised() throws Throwable {
        // TODO
    }

    @When("^I delete the user$")
    public void iDeleteTheUser() throws Throwable {
        // TODO
    }

    @When("^I verify a user's notification settings for a given NotificationType$")
    public void iVerifyAUsersNotificationSettingsForAGivenNotificationType() throws Throwable {
        // TODO
    }

    @When("^I update a user's password$")
    public void iUpdateAUsersPassword() throws Throwable {
        // TODO
    }

    @When("^I start the user password reset process$")
    public void iStartTheUserPasswordResetProcess() throws Throwable {
        // TODO
    }

    @When("^I logout$")
    public void iLogout() throws Throwable {
        // TODO
    }

    @And("^I get the user$")
    public void iGetTheUser() throws Throwable {
        // TODO
    }

    @And("^A user should be received$")
    public void aUserShouldBeReceived() throws Throwable {
        // TODO
    }

    @And("^The user should be removed$")
    public void theUserShouldBeRemoved() throws Throwable {
        // TODO
    }

    @And("^A flag indicating whether or not the username has been used is received$")
    public void aFlagIndicatingWhetherOrNotTheUsernameHasBeenUsedIsReceived() throws Throwable {
        // TODO
    }

    @And("^A count of the number of times the password has been seen is received$")
    public void aCountOfTheNumberOfTimesThePasswordHasBeenSeenIsReceived() throws Throwable {
        // TODO
    }

    @And("^A list of users is received$")
    public void aListOfUsersIsReceived() throws Throwable {
        // TODO
    }

}

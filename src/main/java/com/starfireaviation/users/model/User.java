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

package com.starfireaviation.users.model;

import com.starfireaviation.model.NotificationPreference;
import com.starfireaviation.model.Role;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * User.
 */
@Data
@Entity
@Table(name = "GS_USER")
public class User extends BaseEntity {

    /**
     * Default SerialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    public User() {
        // Do nothing?
    }

    public User(final User other) {
        enabled = other.isEnabled();
        email = other.getEmail();
        emailVerified = other.isEmailVerified();
        emailEnabled = other.isEmailEnabled();
        sms = other.getSms();
        smsVerified = other.isSmsVerified();
        smsEnabled = other.isSmsEnabled();
        slack = other.getSlack();
        slackVerified = other.isSlackVerified();
        slackEnabled = other.isSlackEnabled();
        username = other.getUsername();
        password = other.getPassword();
        firstName = other.getFirstName();
        lastName = other.getLastName();
        certificateNumber = other.getCertificateNumber();
        code = other.getCode();
        role = other.getRole();
        notificationPreference = other.getNotificationPreference();
    }

    /**
     * Email.
     */
    @Column(name = "email")
    private String email;

    /**
     * Email verified.
     */
    @Column(name = "email_verified")
    private boolean emailVerified;

    /**
     * Email enabled.
     */
    @Column(name = "email_enabled")
    private boolean emailEnabled;

    /**
     * SMS.
     */
    @Column(name = "sms")
    private String sms;

    /**
     * SMS verified.
     */
    @Column(name = "sms_verified")
    private boolean smsVerified;

    /**
     * SMS enabled.
     */
    @Column(name = "sms_enabled")
    private boolean smsEnabled;

    /**
     * Slack.
     */
    @Column(name = "slack")
    private String slack;

    /**
     * Slack verified.
     */
    @Column(name = "slack_verified")
    private boolean slackVerified;

    /**
     * Slack enabled.
     */
    @Column(name = "slack_enabled")
    private boolean slackEnabled;

    /**
     * Username.
     */
    @Column(name = "username", nullable = false)
    private String username;

    /**
     * Password.
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * First name.
     */
    @Column(name = "first_name")
    private String firstName;

    /**
     * Last name.
     */
    @Column(name = "last_name")
    private String lastName;

    /**
     * Certificate Number.
     */
    @Column(name = "certificate_number")
    private String certificateNumber;

    /**
     * Code for verification purposes.
     */
    @Column(name = "code")
    private String code;

    /**
     * Role.
     */
    @Column(name = "role", nullable = false)
    private Role role;

    /**
     * QuestionPreference.
     */
    @Column(name = "notification_preference", nullable = false)
    private NotificationPreference notificationPreference = NotificationPreference.WEB;

    /**
     * Enabled.
     */
    private boolean enabled = false;

}

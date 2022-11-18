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

package com.starfireaviation.users.service;

import com.starfireaviation.common.CommonConstants;
import com.starfireaviation.common.exception.ResourceNotFoundException;
import com.starfireaviation.common.model.Role;
import com.starfireaviation.users.model.UserEntity;
import com.starfireaviation.users.model.UserModel;
import com.starfireaviation.users.model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * UserService.
 */
@Slf4j
public class UserService {

    /**
     * UserRepository.
     */
    private final UserRepository userRepository;

    /**
     * Synchronous rest template.
     */
    private final RestTemplate restTemplate;

    /**
     * VerificationTokenRepository.
     */
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    /**
     * PasswordResetTokenRepository.
     */
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    /**
     * PasswordEncoder.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * UserService.
     *
     * @param uRepository UserRepository
     * @param template    RestTemplate
     */
    public UserService(final UserRepository uRepository,
            final RestTemplate template) {
        userRepository = uRepository;
        restTemplate = template;
    }

    /**
     * Creates a user.
     *
     * @param user User
     * @return User
     * @throws ResourceNotFoundException when no user is found for the provided user
     *                                   ID
     */
    public UserEntity store(final UserEntity user) throws ResourceNotFoundException {
        if (user == null) {
            return user;
        }
        final Long userId = user.getId();
        if (userId != null) {
            final UserEntity existingUser = findByIdWithPassword(userId);
            if (existingUser == null) {
                final String msg = String.format("No user found for ID [%s]", userId);
                log.warn(msg);
                throw new ResourceNotFoundException(msg);
            }
            if (existingUser.getEmail() != null && !existingUser.getEmail().equals(user.getEmail())) {
                user.setEmailVerified(false);
            }
            if (existingUser.getSms() != null && !existingUser.getSms().equals(user.getSms())) {
                user.setSmsVerified(false);
            }
            if (existingUser.getSlack() != null && !existingUser.getSlack().equals(user.getSlack())) {
                user.setSlackVerified(false);
            }
            user.setPassword(existingUser.getPassword());
        }
        if (user.getRole() == null) {
            log.info(String.format("store() Setting role to %s", Role.STUDENT));
            user.setRole(Role.STUDENT);
        }
        return userRepository.save(user);
    }

    /**
     * Gets all users.
     *
     * @return list of Users
     * @throws ResourceNotFoundException when no user is found
     */
    public List<UserEntity> getAll() throws ResourceNotFoundException {
        final List<UserEntity> users = new ArrayList<>();
        final List<UserEntity> userEntities = userRepository.findAll();
        for (final UserEntity userEntity : userEntities) {
            final UserEntity user = get(userEntity.getId());
            users.add(user);
        }
        return users;
    }

    /**
     * Gets a user.
     *
     * @param id Long
     * @return User
     * @throws ResourceNotFoundException when no user is found for the provided user
     *                                   ID
     */
    public UserEntity get(final long id) throws ResourceNotFoundException {
        final UserEntity user = userRepository.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException();
        }
        user.setPassword(null);
        return user;
    }

    /**
     * Gets a user by username.
     *
     * @param username username
     * @return User
     */
    public UserEntity findByUsername(final String username) {
        final UserEntity user = userRepository.findByUsername(username);
        user.setPassword(null);
        return user;
    }

    /**
     * Gets a user by email.
     *
     * @param email User's email address
     * @return User
     */
    public UserEntity findByEmail(final String email) {
        final UserEntity user = userRepository.findByEmail(email);
        user.setPassword(null);
        return user;
    }

    /**
     * Checks database for compromised password.
     *
     * @param sha1Hash SHA-1 hashed password
     * @return number of times password has been found
     */
    public int checkIfPasswordIsCompromised(final String sha1Hash) {
        int count = 0;
        log.info(String.format("Checking password hash [%s]", sha1Hash));
        if (StringUtils.isEmpty(sha1Hash)) {
            return 0;
        }
        try {
            final String prefix = sha1Hash.substring(0, 5);
            final URI builtUri = UriComponentsBuilder
                    .fromHttpUrl("https://api.pwnedpasswords.com/range/" + prefix)
                    .build()
                    .encode()
                    .toUri();

            final ResponseEntity<String> responseResponseEntity = restTemplate.exchange(builtUri, HttpMethod.GET, null,
                    String.class);
            if (responseResponseEntity.getStatusCode() == HttpStatus.OK) {
                String[] rows = responseResponseEntity.getBody().split("\n");
                for (String row : rows) {
                    String[] record = row.split(":");
                    if (record.length == 2 && sha1Hash.equalsIgnoreCase(prefix + record[0])) {
                        count = Integer.parseInt(record[1].trim());
                        break;
                    }
                }
            } else if (responseResponseEntity.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                try {
                    int seconds = Integer
                            .parseInt(responseResponseEntity.getHeaders().get("Retry-After").get(0).trim());
                    Thread.sleep((long) seconds * CommonConstants.ONE_THOUSAND);
                    return checkIfPasswordIsCompromised(sha1Hash);
                } catch (InterruptedException | NumberFormatException ignored) {
                }
            }
        } catch (HttpClientErrorException e) {
            // A 404 response means the password has not been compromised
        }
        return count;
    }

    /**
     * Register user.
     *
     * @param userModel UserModel
     * @return User
     */
    public UserEntity registerUser(final UserModel userModel) {
        final UserEntity user = new UserEntity();
        user.setEmail(userModel.getEmail());
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setRole(Role.STUDENT);
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));

        userRepository.save(user);
        return user;
    }

    /**
     * Save verification token for user.
     *
     * @param token token
     * @param user User
     */
    public void saveVerificationTokenForUser(final String token, final User user) {
        VerificationToken verificationToken = new VerificationToken(user, token);

        verificationTokenRepository.save(verificationToken);
    }

    /**
     * Validate verification token.
     *
     * @param token token
     * @return success
     */
    public String validateVerificationToken(final String token) {
        final VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        if (verificationToken == null) {
            return "invalid";
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();

        if ((verificationToken.getExpirationTime().getTime()
                - cal.getTime().getTime()) <= 0) {
            verificationTokenRepository.delete(verificationToken);
            return "expired";
        }

        user.setEnabled(true);
        userRepository.save(user);
        return "valid";
    }

    /**
     * Generate new verification token.
     *
     * @param oldToken old token
     * @return VerificationToken
     */
    public VerificationToken generateNewVerificationToken(final String oldToken) {
        final VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    /**
     * Find user by email.
     *
     * @param email address
     * @return User
     */
    public UserEntity findUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Create password reset token for user.
     *
     * @param user User
     * @param token token
     */
    public void createPasswordResetTokenForUser(final UserEntity user, final String token) {
        final PasswordResetToken passwordResetToken = new PasswordResetToken(user, token);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    /**
     * Validate password reset token.
     *
     * @param token token
     * @return success
     */
    public String validatePasswordResetToken(final String token) {
        final PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if (passwordResetToken == null) {
            return "invalid";
        }

        final User user = passwordResetToken.getUser();
        final Calendar cal = Calendar.getInstance();

        if ((passwordResetToken.getExpirationTime().getTime()
                - cal.getTime().getTime()) <= 0) {
            passwordResetTokenRepository.delete(passwordResetToken);
            return "expired";
        }

        return "valid";
    }

    /**
     * Get user by password reset token.
     *
     * @param token token
     * @return User
     */
    public Optional<UserEntity> getUserByPasswordResetToken(final String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    }

    /**
     * Change password.
     *
     * @param user User
     * @param newPassword new password
     */
    public void changePassword(final UserEntity user, final String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Check if valid old password.
     *
     * @param user User
     * @param oldPassword old password
     * @return same
     */
    public boolean checkIfValidOldPassword(final UserEntity user, final String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    /**
     * Gets UserEntity including the user's password.
     *
     * @param userId User ID
     * @return UserEntity
     */
    private UserEntity findByIdWithPassword(final long userId) {
        return userRepository.findById(userId);
    }

}

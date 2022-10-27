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

package com.starfireaviation.users.validation;

import com.starfireaviation.groundschool.exception.ConflictException;
import com.starfireaviation.groundschool.exception.InvalidPayloadException;
import com.starfireaviation.groundschool.model.User;
import com.starfireaviation.groundschool.model.UserRepository;
import com.starfireaviation.groundschool.service.UserService;
import com.starfireaviation.model.Role;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;

/**
 * UserValidator.
 */
@Slf4j
public class UserValidator extends BaseValidator {

    /**
     * UserRepository.
     */
    private final UserRepository userRepository;

    /**
     * UserValidator.
     *
     * @param uService    UserService
     * @param uRepository UserRepository
     */
    public UserValidator(final UserService uService,
                         final UserRepository uRepository) {
        super(uService);
        userRepository = uRepository;
    }

    /**
     * User Validation.
     *
     * @param user User
     * @throws InvalidPayloadException when a username conflict occurs
     * @throws ConflictException       when user data conflict with another user
     */
    public void validate(final User user) throws ConflictException, InvalidPayloadException {
        empty(user);
        conflict(user);
    }

    /**
     * Ensures user object is not null.
     *
     * @param user User
     * @throws InvalidPayloadException when user is null
     */
    private static void empty(final User user) throws InvalidPayloadException {
        if (user == null) {
            String msg = "No user information was provided";
            log.warn(msg);
            throw new InvalidPayloadException(msg);
        }
    }

    /**
     * Ensures provided user information does not conflict with another user's
     * information.
     *
     * @param user User
     * @throws ConflictException       when user data conflict with another user
     * @throws InvalidPayloadException when user is null
     */
    private void conflict(final User user) throws ConflictException, InvalidPayloadException {
        username(user);
        sms(user);
        slack(user);
        email(user);
    }

    /**
     * Ensure username is unique.
     *
     * @param user User
     * @throws InvalidPayloadException when no username is provided
     * @throws ConflictException       when username conflicts with another user's
     *                                 username
     */
    private void username(final User user) throws InvalidPayloadException, ConflictException {
        if (user.getUsername() == null) {
            String msg = "Username is a required value";
            log.warn(msg);
            throw new InvalidPayloadException(msg);
        }
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser != null && existingUser.getId() != user.getId()) {
            String msg = String.format(
                    "Another user ["
                            + existingUser.getFirstName()
                            + " "
                            + existingUser.getLastName()
                            + "] has already taken username [%s]",
                    user.getUsername());
            log.warn(msg);
            throw new ConflictException(msg);
        }
    }

    /**
     * Ensure sms is unique.
     *
     * @param user User
     * @throws ConflictException when sms conflicts with another user's sms
     */
    private void sms(final User user) throws ConflictException {
        if (user.getSms() != null && !"".equals(user.getSms())) {
            User existingUser = userRepository.findBySms(user.getSms());
            if (existingUser != null && existingUser.getId() != user.getId()) {
                String msg = String.format(
                        "Another user ["
                                + existingUser.getFirstName()
                                + " "
                                + existingUser.getLastName()
                                + "] has already taken number [%s]",
                        user.getSms());
                log.warn(msg);
                throw new ConflictException(msg);
            }
        }
    }

    /**
     * Ensure slack is unique.
     *
     * @param user User
     * @throws ConflictException when slack conflicts with another user's slack
     */
    private void slack(final User user) throws ConflictException {
        if (user.getSlack() != null && !"".equals(user.getSlack())) {
            User existingUser = userRepository.findBySlack(user.getSlack());
            if (existingUser != null && existingUser.getId() != user.getId()) {
                String msg = String.format(
                        "Another user ["
                                + existingUser.getFirstName()
                                + " "
                                + existingUser.getLastName()
                                + "] has already taken slack name [%s]",
                        user.getSlack());
                log.warn(msg);
                throw new ConflictException(msg);
            }
        }
    }

    /**
     * Ensure email is unique.
     *
     * @param user User
     * @throws ConflictException when email conflicts with another user's email
     */
    private void email(final User user) throws ConflictException {
        if (user.getEmail() != null && !"".equals(user.getEmail())) {
            User existingUser = userRepository.findByEmail(user.getEmail());
            if (existingUser != null && existingUser.getId() != user.getId()) {
                String msg = String.format(
                        "Another user ["
                                + existingUser.getFirstName()
                                + " "
                                + existingUser.getLastName()
                                + "] has already taken email address [%s]",
                        user.getEmail());
                log.warn(msg);
                throw new ConflictException(msg);
            }
        }
    }

    /**
     * UserService.
     */
    private final UserService userService;

    /**
     * Validates access by an admin or instructor.
     *
     * @param principal Principal
     * @return Logged in user's ID
     * @throws AccessDeniedException when principal user is not permitted to access
     *                               user info
     */
    public Long accessAdminOrInstructor(final Principal principal) throws AccessDeniedException {
        empty(principal);
        final User loggedInUser = userService.findByUsername(principal.getName());
        final Role role = loggedInUser.getRole();
        if (role != Role.ADMIN && role != Role.INSTRUCTOR) {
            log.warn(
                    String.format(
                            "%s throwing AccessDeniedException because role is [%s]",
                            "accessAdminOrInstructor()",
                            role));
            throw new AccessDeniedException("Current user is not authorized");
        }
        return loggedInUser.getId();
    }

    /**
     * Validates access by an admin.
     *
     * @param principal Principal
     * @return Logged in user's ID
     * @throws ResourceNotFoundException when principal user is not found
     * @throws AccessDeniedException     when principal user is not permitted to
     *                                   access user info
     */
    public Long accessAdmin(final Principal principal) throws ResourceNotFoundException,
            AccessDeniedException {
        empty(principal);
        final User loggedInUser = userService.findByUsername(principal.getName());
        final Role role = loggedInUser.getRole();
        if (role != Role.ADMIN) {
            log.warn(
                    String.format(
                            "%s throwing AccessDeniedException because role is [%s]",
                            "accessAdmin()",
                            role));
            throw new AccessDeniedException("Current user is not authorized");
        }
        return loggedInUser.getId();
    }

    /**
     * Validates access by any authenticated user.
     *
     * @param principal Principal
     * @return Logged in user's ID
     * @throws AccessDeniedException when principal user is not permitted to access
     *                               user info
     */
    public Long accessAnyAuthenticated(final Principal principal) throws AccessDeniedException {
        empty(principal);
        final User loggedInUser = userService.findByUsername(principal.getName());
        final Role role = loggedInUser.getRole();
        if (role != Role.ADMIN && role != Role.INSTRUCTOR && role != Role.STUDENT) {
            log.warn(
                    String.format(
                            "%s throwing AccessDeniedException because role is [%s]",
                            "accessAnyAuthenticated()",
                            role));
            throw new AccessDeniedException("Current user is not authorized");
        }
        return loggedInUser.getId();
    }

    /**
     * Validates access by an admin, instructor, or the authenticated user.
     *
     * @param userId    User ID
     * @param principal Principal
     * @return Logged in user's ID
     * @throws AccessDeniedException when principal user is not permitted to access
     *                               user info
     */
    public Long accessAdminInstructorOrSpecificUser(final Long userId, final Principal principal)
            throws AccessDeniedException {
        empty(principal);
        final User loggedInUser = userService.findByUsername(principal.getName());
        final Role role = loggedInUser.getRole();
        if (role != Role.ADMIN && role != Role.INSTRUCTOR && userId.longValue() != loggedInUser.getId().longValue()) {
            log.warn(
                    String.format(
                            "%s throwing AccessDeniedException because role is [%s] and userId "
                                    + "is [%s] and loggedInUser ID is [%s]",
                            "accessAdminInstructorOrSpecificUser()",
                            role,
                            userId,
                            loggedInUser.getId()));
            throw new AccessDeniedException("Current user is not authorized");
        }
        return loggedInUser.getId();
    }

    /**
     * Determines if logged in user is an admin.
     *
     * @param principal Principal
     * @return admin user?
     */
    public boolean isAdmin(final Principal principal) {
        boolean admin = false;
        try {
            accessAdmin(principal);
            admin = true;
        } catch (AccessDeniedException | ResourceNotFoundException e) {
            admin = false;
        }
        return admin;
    }

    /**
     * Determines if logged in user is an authenticated user.
     *
     * @param userId    User ID
     * @param principal Principal
     * @return authenticated user?
     */
    public boolean isAuthenticatedUser(final Long userId, final Principal principal) {
        boolean authenticatedUser = false;
        try {
            empty(principal);
            final User loggedInUser = userService.findByUsername(principal.getName());
            if (userId == loggedInUser.getId()) {
                authenticatedUser = true;
            }
        } catch (AccessDeniedException ee) {
            authenticatedUser = false;
        }
        return authenticatedUser;
    }

    /**
     * Determines if logged in user is an admin or instructor.
     *
     * @param principal Principal
     * @return admin or instructor user
     */
    public boolean isAdminOrInstructor(final Principal principal) {
        boolean adminOrInstructor = false;
        if (principal == null) {
            return adminOrInstructor;
        }
        try {
            accessAdminOrInstructor(principal);
            adminOrInstructor = true;
        } catch (AccessDeniedException ade) {
            adminOrInstructor = false;
        }
        return adminOrInstructor;
    }

    /**
     * Ensures principal is not null.
     *
     * @param principal Principal
     * @throws AccessDeniedException when principal is null
     */
    private static void empty(final Principal principal) throws AccessDeniedException {
        if (principal == null) {
            log.warn(
                    String.format("%s throwing AccessDeniedException because principal is %s", "empty()", principal));
            throw new AccessDeniedException("No authorization provided");
        }
    }
}

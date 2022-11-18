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

package com.starfireaviation.users.controller;

import com.starfireaviation.common.model.User;
import com.starfireaviation.users.config.ApplicationProperties;
import com.starfireaviation.common.exception.AccessDeniedException;
import com.starfireaviation.common.exception.ConflictException;
import com.starfireaviation.common.exception.InvalidPayloadException;
import com.starfireaviation.common.exception.ResourceNotFoundException;
import com.starfireaviation.common.CommonConstants;
import com.starfireaviation.common.model.NotificationType;
import com.starfireaviation.common.model.Role;
import com.starfireaviation.users.service.UserService;
import com.starfireaviation.users.validation.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserController.
 */
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping({ "/api/users" })
public class UserController {

    /**
     * UserService.
     */
    private final UserService userService;

    /**
     * UserValidator.
     */
    private final UserValidator userValidator;

    /**
     * ApplicationProperties.
     */
    private final ApplicationProperties applicationProperties;

    /**
     * BCryptPasswordEncoder.
     */
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * UserController.
     *
     * @param uService   UserService
     * @param uValidator UserValidator
     * @param aProps     ApplicationProperties
     * @param encoder    BCryptPasswordEncoder
     */
    public UserController(final UserService uService,
                          final UserValidator uValidator,
                          final ApplicationProperties aProps,
                          final BCryptPasswordEncoder encoder) {
        userService = uService;
        userValidator = uValidator;
        applicationProperties = aProps;
        bCryptPasswordEncoder = encoder;
    }

    /**
     * Creates a user.
     *
     * @param user User
     * @return User
     * @throws InvalidPayloadException   when invalid data is provided
     * @throws ResourceNotFoundException when no user is found
     * @throws ConflictException         when user data conflict with another user
     */
    @PostMapping
    public User post(@RequestBody final User user) throws InvalidPayloadException, ResourceNotFoundException,
            ConflictException {
        userValidator.validate(user);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        if (user.getCertificateNumber() != null && user.getCertificateNumber().endsWith("CFI")) {
            user.setRole(Role.INSTRUCTOR);
        } else {
            user.setRole(Role.STUDENT);
        }
        return userService.store(user);
    }

    /**
     * Updates a user.
     *
     * @param user      User
     * @param principal Principal
     * @return User
     * @throws ResourceNotFoundException when no user is found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     * @throws InvalidPayloadException   when invalid data is provided
     * @throws ConflictException         when user data conflict with another user
     */
    @PutMapping
    public User put(@RequestBody final User user, final Principal principal) throws ResourceNotFoundException,
            AccessDeniedException, InvalidPayloadException, ConflictException {
        userValidator.validate(user);
        userValidator.accessAdminInstructorOrSpecificUser(user.getId(), principal);
        final User loggedInUser = userService.findByUsername(principal.getName());
        final Role role = loggedInUser.getRole();
        if (role != Role.ADMIN && role != Role.INSTRUCTOR && loggedInUser.getId() != user.getId()) {
            throw new AccessDeniedException("Current user is not authorized to update user information");
        }
        final User response = userService.store(user);
        return response;
    }

    /**
     * Gets a user.
     *
     * @param userId    Long
     * @param principal Principal
     * @return User
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     * @throws ResourceNotFoundException when user is not found
     */
    @GetMapping(path = { "/{userId}" })
    public User get(@PathVariable("userId") final long userId, final Principal principal) throws AccessDeniedException,
            ResourceNotFoundException {
        userValidator.accessAdminInstructorOrSpecificUser(userId, principal);
        return userService.get(userId);
    }

    /**
     * Checks password to see if it has been compromised.
     *
     * @param password password to check
     * @return count number of times password found in HIBP database
     */
    @GetMapping(path = { "/password/compromised" })
    public int checkIfPasswordIsCompromised(@RequestParam("p") final String password) {
        if (password == null) {
            return 0;
        }
        return userService.checkIfPasswordIsCompromised(CodeGenerator.sha1Hash(password));
    }

    /**
     * Checks to see if a username is available.
     *
     * @param username to verify
     * @return success
     */
    @GetMapping(path = { "/username/{username}/available" })
    public boolean checkUsername(@PathVariable("username") final String username) {
        return userService.findByUsername(username) == null;
    }

    /**
     * Get all users.
     *
     * @param username Optional username
     * @param username Optional slack
     * @param principal Principal
     * @return list of User IDs
     * @throws ResourceNotFoundException when user is not found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @GetMapping
    public List<Long> list(@RequestParam(value = "username", required = false) final String username,
                           @RequestParam(value = "slack", required = false) final String slack,
                           final Principal principal) throws ResourceNotFoundException, AccessDeniedException {
        userValidator.accessAdminOrInstructor(principal);
        return userService
                .getAll()
                .stream()
                .filter(u -> username == null || u.getUsername().equalsIgnoreCase(username))
                .filter(u -> slack == null || u.getSlack().equalsIgnoreCase(slack))
                .map(u -> u.getId())
                .collect(Collectors.toList());
    }

    /**
     * Verifies a user's notification settings for a given NotificationType.
     *
     * @param userId user ID
     * @param type   NotificationType
     * @return success
     * @throws ResourceNotFoundException when no user is found
     */
    @GetMapping(path = { "/{userId}/verify/{type}" })
    public RedirectView verify(@PathVariable("userId") final long userId,
            @PathVariable("type") final NotificationType type)
            throws ResourceNotFoundException {
        final User user = userService.get(userId);
        if (user != null) {
            switch (type) {
                case EMAIL:
                    user.setEmailVerified(true);
                    break;
                case SLACK:
                    user.setSlackVerified(true);
                    break;
                default:
            }
            userService.store(user);
        }
        return new RedirectView(applicationProperties.getUiHost());
    }

    /**
     * Updates a user's password.
     *
     * @param userId           User ID
     * @param password         new password
     * @param verificationCode to ensure request is not fraudulent
     * @param principal        Principal
     * @return success
     * @throws ResourceNotFoundException when no user is found
     *
     */
    @PostMapping(path = { "/{userId}/password/{verificationCode}" })
    public boolean updatePassword(
            @PathVariable("userId") final long userId,
            @PathVariable("verificationCode") final String verificationCode,
            @RequestBody final String password,
            final Principal principal) throws ResourceNotFoundException {
        final User user = userService.get(userId);
        if (user == null) {
            final String msg = String.format("No user found for ID [%s]", userId);
            log.warn(msg);
            throw new ResourceNotFoundException(msg);
        }
        if (userValidator.isAdmin(principal)
                || userValidator.isAuthenticatedUser(userId, principal)
                || (verificationCode != null && verificationCode.equals(user.getCode()))) {
            user.setPassword(bCryptPasswordEncoder.encode(password));
            user.setCode(null);
            return userService.store(user) != null;
        }
        return false;
    }

    /**
     * Start the user password reset process.
     *
     * @param email User's email address
     * @return success
     *
     */
    @PostMapping(path = { "/password/reset" })
    public boolean passwordReset(@RequestBody final String email) {
        boolean success = false;
        try {
            final User user = userService.findByEmail(email);
            if (user != null) {
                user.setCode(CodeGenerator.generateCode(CommonConstants.FOUR));
                userService.store(user);
                success = true;
            }
        } catch (ResourceNotFoundException rnfe) {
            log.warn(
                    String.format(
                            "A password reset was attempted for email [%s] but no "
                                    + "email address was found in the database.",
                            email));
        }
        return success;
    }

    /**
     * Logout.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @PostMapping(path = { "/logout" })
    public void logout(final HttpServletRequest request, final HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
    }

}

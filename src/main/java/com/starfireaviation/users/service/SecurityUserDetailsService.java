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

import com.starfireaviation.groundschool.model.SecurityUserDetails;
import com.starfireaviation.groundschool.model.User;
import com.starfireaviation.groundschool.model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * SecurityUserDetailsService.
 */
@Slf4j
public class SecurityUserDetailsService implements UserDetailsService {

    /**
     * UserRepository.
     */
    private final UserRepository userRepository;

    public SecurityUserDetailsService(final UserRepository uRepository) {
        userRepository = uRepository;
    }

    /**
     * {@inheritDoc} Required implementation.
     */
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user present with username [%s]", username));
        }
        log.info(String.format("loaded user [%s] with role [%s]", user.getUsername(), user.getRole()));
        final List<String> userRoles = new ArrayList<>();
        userRoles.add(user.getRole().toString());
        return new SecurityUserDetails(user, userRoles);
    }

}

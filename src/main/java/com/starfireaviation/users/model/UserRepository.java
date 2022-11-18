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

import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * UserRepository.
 */
public interface UserRepository extends Repository<UserEntity, Long> {

    /**
     * Deletes a user.
     *
     * @param user User
     */
    void delete(UserEntity user);

    /**
     * Gets all users.
     *
     * @return list of Users
     */
    List<UserEntity> findAll();

    /**
     * Gets a user by ID.
     *
     * @param id Long
     * @return User
     */
    UserEntity findById(long id);

    /**
     * Gets a user by Username.
     *
     * @param username String
     * @return User
     */
    UserEntity findByUsername(String username);

    /**
     * Gets a user by SMS Number.
     *
     * @param sms String
     * @return User
     */
    UserEntity findBySms(String sms);

    /**
     * Gets a user by Slack name.
     *
     * @param slack String
     * @return User
     */
    UserEntity findBySlack(String slack);

    /**
     * Gets a user by Email Address.
     *
     * @param email String
     * @return User
     */
    UserEntity findByEmail(String email);

    /**
     * Saves a user.
     *
     * @param user User
     * @return User
     */
    UserEntity save(UserEntity user);
}

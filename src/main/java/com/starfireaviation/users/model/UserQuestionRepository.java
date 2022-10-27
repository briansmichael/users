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
 * UserQuestionRepository.
 */
public interface UserQuestionRepository extends Repository<UserQuestion, Long> {

    /**
     * Deletes a UserQuestion.
     *
     * @param userQuestion UserQuestion
     */
    void delete(UserQuestion userQuestion);

    /**
     * Gets all UserQuestion.
     *
     * @return list of UserQuestion
     */
    List<UserQuestion> findAll();

    /**
     * Gets a UserQuestion.
     *
     * @param id Long
     * @return UserQuestion
     */
    UserQuestion findById(long id);

    /**
     * Gets all UserQuestions by User ID.
     *
     * @param id Long
     * @return UserQuestion
     */
    List<UserQuestion> findByUserId(long id);

    /**
     * Gets all UserQuestions by Event ID.
     *
     * @param id Long
     * @return UserQuestion
     */
    List<UserQuestion> findByEventId(long id);

    /**
     * Gets all UserQuestions by Question ID.
     *
     * @param id Long
     * @return UserQuestion
     */
    List<UserQuestion> findByQuestionId(long id);

    /**
     * Gets all UserQuestions by Quiz ID.
     *
     * @param id Long
     * @return UserQuestion
     */
    List<UserQuestion> findByQuizId(long id);

    /**
     * Saves a UserQuestion.
     *
     * @param userQuestion UserQuestion
     * @return Question
     */
    UserQuestion save(UserQuestion userQuestion);
}

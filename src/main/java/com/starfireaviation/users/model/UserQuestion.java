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

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * UserQuestion.
 */
@Data
@Entity
@Table(name = "USER_QUESTION")
public class UserQuestion extends BaseEntity {

    /**
     * QUESTION_ID.
     */
    public static final String QUESTION_ID = "QUESTION_ID";

    /**
     * QUIZ_ID.
     */
    public static final String QUIZ_ID = "QUIZ_ID";

    /**
     * EVENT_ID.
     */
    public static final String EVENT_ID = "EVENT_ID";

    /**
     * USER_ID.
     */
    public static final String USER_ID = "USER_ID";

    /**
     * TIME.
     */
    public static final String TIME = "TIME";

    /**
     * ANSWER_GIVEN.
     */
    public static final String ANSWER_GIVEN = "ANSWER_GIVEN";

    /**
     * ANSWERED_CORRECTLY.
     */
    public static final String ANSWERED_CORRECTLY = "ANSWERED_CORRECTLY";

    /**
     * Question ID.
     */
    @Column(name = "question_id", nullable = false)
    private Long questionId;

    /**
     * Quiz ID.
     */
    @Column(name = "quiz_id", nullable = true)
    private Long quizId;

    /**
     * Event ID.
     */
    @Column(name = "event_id", nullable = true)
    private Long eventId;

    /**
     * User ID.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * LocalDateTime - time.
     */
    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    /**
     * Answer given by user.
     */
    @Column(name = "answer_given", nullable = false)
    private String answerGiven;

    /**
     * Answer given was correct.
     */
    @Column(name = "answered_correctly", nullable = false)
    private boolean answeredCorrectly;

}

package com.example.dotheG.model;

import jakarta.persistence.*;

@Entity
public class MemberQuiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_QUIZ_ID")
    private Long userQuizId;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private Member userId;

    @ManyToOne
    @JoinColumn(name = "QUIZ_ID")
    private Quiz quizId;

    @Column(columnDefinition = "boolean default false")
    private boolean isSolved;
}

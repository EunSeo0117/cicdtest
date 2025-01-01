package com.example.dotheG.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
public class MemberInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_INFO_ID")
    private Long userInfoId;

    @OneToOne
    @JoinColumn(name = "USER_ID")
    private Member userId;

    @Column(columnDefinition = "integer default 20")
    private int userReward;

    private Long mainChar;

    public void addReward(int reward) {
        userReward += reward;
    }
}

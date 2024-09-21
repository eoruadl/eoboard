package com.eoboard.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(name = "id", length = 15, nullable = false, unique = true)
    private String memberId;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private String nickName;
    @Column(nullable = false)
    private String name;
    @Column
    private String email;
    @Column
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "member")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Comment> comments = new ArrayList<>();

    public Member(String memberId, String password, String nickName, String name, String email) {
        this.memberId = memberId;
        this.password = password;
        this.nickName = nickName;
        this.name = name;
        this.email = email;
    }

    public void updateMemberId(String memberId) {
        this.memberId = memberId;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateRole(Role role) {
        this.role = role;
    }

}

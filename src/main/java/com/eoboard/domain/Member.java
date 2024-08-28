package com.eoboard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
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
    private String eMail;

    @OneToMany(mappedBy = "member")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Comment> comments = new ArrayList<>();

    public static Member createUser(Member member, PasswordEncoder passwordEncoder) {
        Member member1 = new Member();
        member1.setMemberId(member.getMemberId());
        member1.setPassword(passwordEncoder.encode(member.getPassword()));
        member1.setEMail(member.getEMail());
        member1.setName(member.getName());
        member1.setNickName(member.getNickName());
        return member1;
    }

}

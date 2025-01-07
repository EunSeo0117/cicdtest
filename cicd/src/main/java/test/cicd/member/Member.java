package test.cicd.member;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class Member {
    @Id
    private Long id;
    private String name;
    private String password;

    public Member() {}
    public Member(Long id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }
}

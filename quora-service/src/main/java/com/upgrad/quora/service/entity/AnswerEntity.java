package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name = "answer")
@NamedQueries({@NamedQuery(name="answerById" , query="select a from AnswerEntity a where a.uuid = :id"),
        @NamedQuery(name="getAllAnswer", query = "select a from AnswerEntity a where a.question.uuid=:id")})
public class AnswerEntity {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "uuid")
  @Size(max = 200)
  private String uuid;

  @Column(name = "ans")
  @Size(max = 255)
  private String ans;

  @Column(name = "date")
  private ZonedDateTime date;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity userEntity;

  @ManyToOne
  @JoinColumn(name = "question_id")
  private QuestionEntity question;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getAns() {
    return ans;
  }

  public void setAns(String ans) {
    this.ans = ans;
  }

  public ZonedDateTime getDate() {
    return date;
  }

  public void setDate(ZonedDateTime date) {
    this.date = date;
  }

  public UserEntity getUser() {
    return userEntity;
  }

  public void setUser(UserEntity user) {
    this.userEntity = user;
  }

  public QuestionEntity getQuestion() {
    return question;
  }

  public void setQuestion(QuestionEntity question) {
    this.question = question;
  }
  public UserEntity getUserEntity() {
    return userEntity;
  }

  public void setUserEntity(UserEntity userEntity) {
    this.userEntity = userEntity;
  }



}

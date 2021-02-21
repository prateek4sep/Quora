package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class AnswerDao {

  @PersistenceContext
  private EntityManager entityManager;

  /**
   *
   * @param answerEntity- answer which need to be added
   * @return answer entity which was added with its id
   */
  public AnswerEntity createAnswer(AnswerEntity answerEntity){
      entityManager.persist(answerEntity);
      return answerEntity;
  }



}

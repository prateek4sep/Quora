package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

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

  /**
   *
   * @param answerEntity - answer that to be edited
   * @return Updated Answer
   */
  public AnswerEntity updateAnswer(final AnswerEntity answerEntity){
      entityManager.merge(answerEntity);
      return  answerEntity;
  }

    /**
     *
     * @param questionID - Question uuid for which neet to fetch all answers
     * @return - List of Answers
     */
  public List<AnswerEntity> getAllAnswer(String questionID){
      Query query= entityManager.createNamedQuery("getAllAnswer",AnswerEntity.class).setParameter("id",questionID);
      List <AnswerEntity>resultList = query.getResultList();
      return resultList;
  }


    /**
     *
     * @param answerUuid- answer uuid that to be fetched from DB
     * @return - Answer Entity
     */
  public AnswerEntity getAnswerByID(String answerUuid){
      try{
          return entityManager.createNamedQuery("answerById",AnswerEntity.class).setParameter("id",answerUuid).getSingleResult();
      }
      catch(NoResultException noResultException){
          return null;
      }
  }

    /**
     *
     * @param answerEntity - Answer Entity that to be deleted
     */
  public void deleteAnswer(AnswerEntity answerEntity){
      entityManager.remove(answerEntity);
  }




}

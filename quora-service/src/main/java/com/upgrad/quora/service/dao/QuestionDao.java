package com.upgrad.quora.service.dao;


import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     *
     * @param questionEntity questionEntity to be persisted
     * @return persisted questionEntity
     */
    public QuestionEntity createQuestion(QuestionEntity questionEntity){
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    /**
     *
     * @return returns List of all questions in the database
     * if no questions present in db returns null
     */
    public List<QuestionEntity> getAllQuestions(){
        try{
            return entityManager.createNamedQuery("getAllQuestions",QuestionEntity.class).getResultList();
        }
        catch (NoResultException nre){
            return null;
        }
    }

    /**
     *
     * @param uuid - uuiod of the question to be fetched
     * @return - if the question correspondent to the uuid is present in db return the questionEntity
     * or else return null
     */
    public QuestionEntity getQuestionByUuid(String uuid){
        try {
            return entityManager.createNamedQuery("getQuestionByUuid",QuestionEntity.class).setParameter("uuid",uuid).getSingleResult();
        }
        catch (NoResultException nre){
            return null;
        }
    }

    /**
     *
     * @param questionEntity - questionEntity with edited content
     * @return updated questionEntity
     */
    public QuestionEntity updateQuestion(QuestionEntity questionEntity){
        return entityManager.merge(questionEntity);
    }
}

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


    /**
     *
     * @param userId - user's unique id
     * @return all questions based on user id and return null if no question for the user
     */
    public List<QuestionEntity> getAllQuestionsByUser(final Integer userId){
        try {
            return entityManager.createNamedQuery("questionByUserId", QuestionEntity.class).setParameter("userId", userId).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     *
     * @param questionEntity which needs to be deleted
     */
    public void deleteQuestion(final QuestionEntity questionEntity){
        entityManager.remove(questionEntity);
    }

    /**
     *
     * @param questionUuid uuid of question which need to fetch
     * @return question entity and null if no question present for given uuid
     */
    public QuestionEntity getQuestion(final String questionUuid){
        try {
            return entityManager.createNamedQuery("questionByUuid", QuestionEntity.class).setParameter("uuid", questionUuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}

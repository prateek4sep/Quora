package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private QuestionDao questionDao;

    /**
     *
     * @param questionEntity - the questionEntity to be persisted in db
     * @return - persited question
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity){
        // call createQuestion of the questionDao
        QuestionEntity persistedQuestion = questionDao.createQuestion(questionEntity);
        return persistedQuestion;
    }

    /**
     *
     * @return - list of all questionEntities
     */
    public List<QuestionEntity> getAllQuestions(){
        // call getAllQuestions of questionDao
        List<QuestionEntity>questionEntities = questionDao.getAllQuestions();
        return questionEntities;
    }


    /**
     *
     * @param uuid- uuid of the question to be fetched from the database
     * @return - questionEntity with the corresponding uuid
     * @throws InvalidQuestionException - throwed if the question with the provided uuid not present
     */
    public QuestionEntity getQuestionByUuid(String uuid) throws InvalidQuestionException {
        QuestionEntity questionEntity = questionDao.getQuestionByUuid(uuid);

        if(questionEntity == null){
            throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
        }
        return questionEntity;
    }

    /**
     *
     * @param questionEntity - questionEntity to be updated
     * @return updated questionEntity
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity updateQuestion(QuestionEntity questionEntity){
        QuestionEntity updatedQuestion = questionDao.updateQuestion(questionEntity);
        return updatedQuestion;
    }

    public void authorizeEdit(UserEntity userToEdit,UserEntity questionOwner) throws AuthorizationFailedException {
        if(userToEdit.getId() != questionOwner.getId()){
            throw new AuthorizationFailedException("ATHR-003","Only the question owner can edit the question");
        }
    }
}

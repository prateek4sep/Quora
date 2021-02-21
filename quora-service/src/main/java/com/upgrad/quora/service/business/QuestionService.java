package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private QuestionDao questionDao;
    @Autowired
    private CommonService commonService;

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


  /**
     *
     * @param userUuid- uuid of user for which all question need to fetch
     * @return list of question for user
     *
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestionsByUser (final String userUuid) throws UserNotFoundException {

        UserEntity userEntity=commonService.getUserByUuid(userUuid);
        List<QuestionEntity> questionEntityList=questionDao.getAllQuestionsByUser(userEntity.getId());
        return questionEntityList;
    }

    /**
     *
     * @param userAuthTokenEntity - authorized user entity
     * @param questionUuid - uuid of question which need to be deleted
     * @return question entity which was deleted
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(final UserAuthEntity userAuthTokenEntity, final String questionUuid) throws AuthorizationFailedException, InvalidQuestionException {


        QuestionEntity questionEntity=questionDao.getQuestion(questionUuid);
        if(questionEntity==null){
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        if(userAuthTokenEntity.getUserEntity().getId()!=questionEntity.getUserEntity().getId() && questionEntity.getUserEntity().getRole()!="admin"){
            throw new AuthorizationFailedException("ATHR-003","Only the question owner or admin can delete the question");
        }
        questionDao.deleteQuestion(questionEntity);
        return  questionEntity;
    }


}

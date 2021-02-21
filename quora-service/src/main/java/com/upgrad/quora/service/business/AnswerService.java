package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class AnswerService {

  @Autowired
  private AnswerDao answerDao;

  @Autowired
  private  QuestionDao questionDao;

  /**
   *
   * @param userAuthTokenEntity - authorized user entity
   * @param answerEntity - New answer which need to be created
   * @param questionUuid - uuid of question for which answer need to be added
   * @return answer entity which was created
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public AnswerEntity createAnswer(final UserAuthEntity userAuthTokenEntity, final AnswerEntity answerEntity, final String questionUuid) throws AuthorizationFailedException, InvalidQuestionException {
    QuestionEntity questionEntity=questionDao.getQuestion(questionUuid);
    if(questionEntity==null){
      throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
    }
    answerEntity.setUser(userAuthTokenEntity.getUserEntity());
    answerEntity.setQuestion(questionEntity);
    return answerDao.createAnswer(answerEntity);
  }

  /**
   *
   * @param questionID - uuid of the question for which Fetch all Answers
   * @return - List of AnswerEntity
   * @throws AuthorizationFailedException - throwed if the user is not authorized to edit the question
   * @throws InvalidQuestionException - throws exception if the Provided QuestionID Not Present in DB
   */
  public List<AnswerEntity> getAllAnswer(String questionID) throws AuthorizationFailedException, InvalidQuestionException {
    QuestionEntity question = questionDao.getQuestion(questionID);
    if(question == null)
    {
      throw new InvalidQuestionException("QUES-001","The question with entered uuid whose details are to be seen does not exist");
    }
    return answerDao.getAllAnswer(questionID);
  }

  /**
   *
   * @param answerEntity - AnswerEntity that to be edited
   * @param userAuthToken - authorization header containing the accessToken
   * @return - Deleted AnswerEntity
   * @throws AuthorizationFailedException - throwed if the user is not authorized to edit the question
   * @throws AnswerNotFoundException - throwed if the answer requested for the edit is not present in DB
   */

  @Transactional(propagation = Propagation.REQUIRED)
  public AnswerEntity updateAnswer(AnswerEntity answerEntity,final UserAuthEntity userAuthToken) throws AuthorizationFailedException, AnswerNotFoundException {
    AnswerEntity answerByID = answerDao.getAnswerByID(answerEntity.getUuid());

    if(answerByID == null){
      throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
    }
    if(userAuthToken.getUserEntity().getId()!=answerByID.getUser().getId()){
      throw new AuthorizationFailedException("ATHR-003","Only the answer owner can edit the answer");
    }

    answerEntity.setQuestion(answerByID.getQuestion());
    answerEntity.setUser(answerByID.getUser());
    answerEntity.setUuid(answerByID.getUuid());
    answerEntity.setDate(answerByID.getDate());


    AnswerEntity updateAnswer = answerDao.updateAnswer(answerEntity);
    return updateAnswer;

  }

  /**
   *
   * @param answerID - uuid of Answer that to be deleted
   * @param userAuthToken - authorization header containing the accessToken
   * @throws AuthorizationFailedException - throwed if the user is not authorized to edit the question
   * @throws AnswerNotFoundException - throwed if the answer requested for the edit is not present in DB
   */

  @Transactional(propagation = Propagation.REQUIRED)
  public void deleteAnswer(String answerID,final UserAuthEntity userAuthToken) throws AuthorizationFailedException, AnswerNotFoundException {

    AnswerEntity answerByID = answerDao.getAnswerByID(answerID);

    if(answerByID == null){
      throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
    }

    if(userAuthToken.getUserEntity().getId()!=answerByID.getUser().getId() && ! userAuthToken.getUserEntity().getRole().equals("admin")){
      throw new AuthorizationFailedException("ATHR-003","Only the answer owner can edit the answer");
    }

    answerDao.deleteAnswer(answerByID);
  }


}

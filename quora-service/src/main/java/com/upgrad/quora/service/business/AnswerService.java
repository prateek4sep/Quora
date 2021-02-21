package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

}

package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.CommonService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

  @Autowired
  AnswerService answerService;


  @Autowired
  private CommonService commonService;


  /**
   * This method takes question uuid,  authorization string, and answer request object as parameter, validate the question id and user and then create an answer to a particular question.
   *
   * @param questionUuid "Uuid of question for which answer need to be created"
   * @param authorization "Basic <Base 64 Encoded username:password>"
   * @param answerRequest "Answer string is stored which will be added"
   * @return AnswerResponse containing answer uuid and status.
   * @throws AuthorizationFailedException Throws the error code ATH-001 if username doesn't exist,
   *      * ATH-002 in case of incorrect password
   * @throws InvalidQuestionException with the message code - 'QUES-001' and message - 'The question entered is invalid'.
   */
  @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerResponse> createAnswer (@PathVariable("questionId") final String questionUuid, @RequestHeader("authorization") final String authorization, final AnswerRequest answerRequest) throws AuthorizationFailedException,  InvalidQuestionException {

    UserAuthEntity userAuthTokenEntity = commonService.authorizeUser(authorization);

    final AnswerEntity answerEntity=new AnswerEntity();
    answerEntity.setAns(answerRequest.getAnswer());
    answerEntity.setUuid(UUID.randomUUID().toString());
    answerEntity.setDate(ZonedDateTime.now());
    AnswerEntity answer = answerService.createAnswer(userAuthTokenEntity, answerEntity, questionUuid);

    AnswerResponse answerResponse=new AnswerResponse().id(answerEntity.getUuid()).status("Answer Created");
    return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.OK);

  }

}

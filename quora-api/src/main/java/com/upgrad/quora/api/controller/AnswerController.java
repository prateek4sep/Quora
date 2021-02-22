package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.CommonService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

  @Autowired
  AnswerService answerService;

  @Autowired
  private CommonService commonService;

  /**
   * This method takes question uuid,  authorization string, and answer request object as parameter,
   * validates the question id and the user and then creates an answer for a particular question.
   *
   * @param questionUuid "Uuid of question for which answer needs to be created"
   * @param authorization "Basic <Base 64 Encoded username:password>"
   * @param answerRequest "Answer string is stored which will be added"
   * @return AnswerResponse containing answer uuid and status.
   * @throws AuthorizationFailedException Throws the error code ATH-001 if username doesn't exist,
   *      * ATH-002 in case of incorrect password
   * @throws InvalidQuestionException with the message code - 'QUES-001' and message - 'The question entered is invalid'.
   */
  @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", produces =
          MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerResponse> createAnswer(@PathVariable("questionId") final String questionUuid,
                                                     @RequestHeader("authorization") final String authorization, final AnswerRequest answerRequest)
          throws AuthorizationFailedException, InvalidQuestionException {

    UserAuthEntity userAuthTokenEntity = commonService.authorizeUser(authorization);

    final AnswerEntity answerEntity = new AnswerEntity();
    answerEntity.setAns(answerRequest.getAnswer());
    answerEntity.setUuid(UUID.randomUUID().toString());
    answerEntity.setDate(ZonedDateTime.now());
    AnswerEntity answer = answerService.createAnswer(userAuthTokenEntity, answerEntity, questionUuid);

    AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("Answer Created");
    return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.OK);

  }

  /**
   * This method takes the question ID and auth token as a request and returns the list of answers associated with the question.
   *
   * @param questionID - question ID for the question for which the answers are to be fetched
   * @param authorization - authorization header with the accessToken
   * @return - List of type AnswerDetailsResponse with all the answers
   * @throws AuthorizationFailedException - throws exception if the accessToken provided is not valid
   * @throws InvalidQuestionException - throws exception if the Provided QuestionID Not Present in DB
   */
  @RequestMapping(method = RequestMethod.GET, value = "/answer/all/{questionId}", produces =
          MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswer(@PathVariable("questionId") String questionID,
                                                                  @RequestHeader("authorization") final String authorization)
          throws AuthorizationFailedException, InvalidQuestionException {

    UserAuthEntity userAuthEntity = commonService.authorizeUser(authorization);
    List<AnswerEntity> allAnswer = answerService.getAllAnswer(questionID);
    StringBuilder answerContent = new StringBuilder();
    List <AnswerDetailsResponse> answerDetailsResponseList = new ArrayList<AnswerDetailsResponse>();
    for(AnswerEntity answerEntity:allAnswer){
      AnswerDetailsResponse answerDetailsResponse =
              new AnswerDetailsResponse().id(answerEntity.getUuid()).answerContent(answerEntity.getAns()).questionContent(answerEntity.getQuestion().getContent());
      answerDetailsResponseList.add(answerDetailsResponse);
    }
    return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponseList, HttpStatus.OK);

  }

  /**
   * This method takes the answer ID, content and auth token as a request, updates the answer and returns the status.
   *
   * @param answerEditRequest - AnswerEditRequest containing the content to be edited
   * @param answerID - uuid of the answer that is to be edited
   * @param authorization - authorization header containing the accessToken
   * @return - AnswerEditResponse
   * @throws AuthorizationFailedException - thrown if the user is not authorized to edit the question
   * @throws AnswerNotFoundException - thrown if the answer requested for the edit is not present in DB
   */
  @RequestMapping(method = RequestMethod.PUT, value = "/answer/edit/{answerId}",
          consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerEditResponse> editAnswer(final AnswerEditRequest answerEditRequest, @PathVariable("answerId") String answerID,
                                                       @RequestHeader("authorization") final String authorization)
          throws AuthorizationFailedException, AnswerNotFoundException {

    UserAuthEntity userAuthEntity = commonService.authorizeUser(authorization);
    AnswerEntity answerEntity = new AnswerEntity();
    answerEntity.setUuid(answerID);
    answerEntity.setAns(answerEditRequest.getContent());

    AnswerEntity updateAnswer = answerService.updateAnswer(answerEntity, userAuthEntity);
    AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(updateAnswer.getUuid()).status("ANSWER EDITED");

    return new ResponseEntity<AnswerEditResponse>(answerEditResponse,HttpStatus.CREATED);
  }

  /**
   * This method takes the answer ID, content and auth token as a request, deletes the answer and returns the status.
   *
   * @param answerID - uuid of the answer that is to be deleted
   * @param authorization - authorization header containing the accessToken
   * @return - AnswerDeleteResponse
   * @throws AuthorizationFailedException - thrown if the user is not authorized to edit the question
   * @throws AnswerNotFoundException - thrown if the answer requested for the edit is not present in DB
   */
  @RequestMapping(method = RequestMethod.DELETE,value = "/answer/delete/{answerId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") String answerID,
                                                           @RequestHeader("authorization") final String authorization)
          throws AuthorizationFailedException, AnswerNotFoundException {

    UserAuthEntity userAuthEntity = commonService.authorizeUser(authorization);
    answerService.deleteAnswer(answerID,userAuthEntity);
    AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(answerID).status("ANSWER DELETED");
    return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse,HttpStatus.OK);

  }



}

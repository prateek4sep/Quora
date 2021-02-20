package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.QuestionEditRequest;
import com.upgrad.quora.api.model.QuestionEditResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.CommonService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.*;

@RestController
@RequestMapping("/")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommonService commonService;

    /**
     *
     * @param request - QuestionRequest object
     * @param authHeader - authorization header containing the accessToken
     * @return QuestionResponse - with uuid and status
     * @throws AuthorizationFailedException - throwed when user is not authorized to create a question.
     */

    @RequestMapping(path = "/question/create",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest request, @RequestHeader("authorization")final String authHeader) throws AuthorizationFailedException {
        QuestionEntity questionEntity = new QuestionEntity();
//      check if the provided accessToken is valid and present in the db
//      if accessToken not valid throws AuthorizationFailedException
        UserAuthEntity userAuthTokenEntity = commonService.authorizeUser(authHeader);
//        get the user correspondent to the accessToken
        UserEntity userEntity = userAuthTokenEntity.getUserEntity();

        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(request.getContent());
        questionEntity.setDate(ZonedDateTime.now());
        questionEntity.setUserEntity(userEntity);

//      persist the question in db;
        QuestionEntity persistedQuestion = questionService.createQuestion(questionEntity);
//       create a QuestionResponse object
        QuestionResponse response = new QuestionResponse();
        response.id(persistedQuestion.getUuid());
        response.status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(response, HttpStatus.CREATED);
    }


    /**
     *
     * @param authHeader - authorization header with the accessToken
     * @return - List of type QuestionResponse with all the questions
     * @throws AuthorizationFailedException - throws exception if the accessToken provided is not valid
     */

    @RequestMapping(path = "/question/all",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<QuestionResponse>> getAllQuestions(@RequestHeader("authorization") final String authHeader) throws AuthorizationFailedException {
//      check if the accessToken is valid and present in db
        UserAuthEntity userAuthTokenEntity = commonService.authorizeUser(authHeader);

//      get all the questionEntities present in db
        List<QuestionEntity>questionEntities = questionService.getAllQuestions();
//      create a QuestionResponse list
        List <QuestionResponse> responseList = new ArrayList<>();

//      iterate through questionEntities and create a corresponding questionResponse and push it to the responseList
        for(QuestionEntity questionEntity:questionEntities){
            QuestionResponse response = new QuestionResponse();
            response.id(questionEntity.getUuid());
            response.status("QUESTION CREATED");
            responseList.add(response);
        }

        return new ResponseEntity<List<QuestionResponse>>(responseList,HttpStatus.OK);
    }

    /**
     *
     * @param authHeader - authorization header containing the accessToken
     * @param request - QuestionEditRequest containing the content to be edited
     * @param questionUuid - uuid of the question that is to be edited
     * @return - QuestionEditResponse
     * @throws AuthorizationFailedException - throwed if the user is not authorized to edit the question
     * @throws InvalidQuestionException - throwed if the question requested for the edit is not present in db
     */

    @RequestMapping(path = "/question/edit/{questionId}",produces = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.PUT)
    public ResponseEntity<QuestionEditResponse> editQuestion(@RequestHeader("authorization")final String authHeader, final QuestionEditRequest request,@PathVariable("questionId") final String questionUuid) throws AuthorizationFailedException, InvalidQuestionException {
        //check if the provided accessToken is valid and present in the db
        //if accessToken not valid throws AuthorizationFailedException
        UserAuthEntity userAuthTokenEntity = commonService.authorizeUser(authHeader);
//      check if the question correspondent to the provided uuid present in db
//      if question not present throws InvalidQuestionException
        QuestionEntity questionEntity = questionService.getQuestionByUuid(questionUuid);

//      get the user requesting for the edit
        UserEntity userToEdit = userAuthTokenEntity.getUserEntity();
//      get the actual question owner
        UserEntity questionOwner = questionEntity.getUserEntity();

//      checks if the user requesting the edit and the owner of the question are same
//      if not throws a AuthorizationFailedException
        questionService.authorizeEdit(userToEdit,questionOwner);

//      if user authorized edit the questionEntity with the provided new content
        questionEntity.setContent(request.getContent());

//      persist the updated questionEntity
        QuestionEntity updatedQuestion = questionService.updateQuestion(questionEntity);

//      create a QuestionEditResponse
        QuestionEditResponse response = new QuestionEditResponse();
        response.setId(updatedQuestion.getUuid());
        response.setStatus("QUESTION EDITED");

        return new ResponseEntity<QuestionEditResponse>(response,HttpStatus.OK);
    }
}

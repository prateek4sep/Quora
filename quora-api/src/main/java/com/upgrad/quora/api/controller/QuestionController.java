package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.CommonService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
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
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommonService commonService;

    /**
     * This method takes the question content and auth token as a request, creates the answer and returns the status.
     *
     * @param request - QuestionRequest object
     * @param authHeader - authorization header containing the accessToken
     * @return QuestionResponse - with uuid and status
     * @throws AuthorizationFailedException - thrown when user is not authorized to create a question.
     */
    @RequestMapping(path = "/question/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest request,
                                                           @RequestHeader("authorization") final String authHeader) throws AuthorizationFailedException {

        QuestionEntity questionEntity = new QuestionEntity();

        //Check if the provided accessToken is valid and present in the db
        //If accessToken is not valid throws AuthorizationFailedException
        UserAuthEntity userAuthTokenEntity = commonService.authorizeUser(authHeader);

        //Get the user corresponding to the accessToken
        UserEntity userEntity = userAuthTokenEntity.getUserEntity();

        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(request.getContent());
        questionEntity.setDate(ZonedDateTime.now());
        questionEntity.setUserEntity(userEntity);

        //Persist the question in db
        QuestionEntity persistedQuestion = questionService.createQuestion(questionEntity);

        //Create a QuestionResponse object
        QuestionResponse response = new QuestionResponse();
        response.id(persistedQuestion.getUuid());
        response.status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(response, HttpStatus.CREATED);
    }


    /**
     * This method takes the auth token as a request and returns the list of all the questions.
     *
     * @param authHeader - authorization header with the accessToken
     * @return - List of type QuestionResponse with all the questions
     * @throws AuthorizationFailedException - throws exception if the accessToken provided is not valid
     */

    @RequestMapping(path = "/question/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authHeader)
            throws AuthorizationFailedException {

        //Check if the accessToken is valid and present in db
        UserAuthEntity userAuthTokenEntity = commonService.authorizeUser(authHeader);

        //Get all the questionEntities present in db
        List<QuestionEntity>questionEntities = questionService.getAllQuestions();

        //Create a QuestionResponse list
        List <QuestionDetailsResponse> responseList = new ArrayList<>();

        //Iterate through questionEntities and create a corresponding questionResponse and push it to the responseList
        for(QuestionEntity questionEntity:questionEntities){
            QuestionDetailsResponse response = new QuestionDetailsResponse();
            response.id(questionEntity.getUuid());
            response.content(questionEntity.getContent());
            responseList.add(response);
        }

        return new ResponseEntity<List<QuestionDetailsResponse>>(responseList,HttpStatus.OK);
    }

    /**
     * This method takes the question ID, content and auth token as a request, edits the question and returns the status.
     *
     * @param authHeader - authorization header containing the accessToken
     * @param request - QuestionEditRequest containing the content to be edited
     * @param questionUuid - uuid of the question that is to be edited
     * @return - QuestionEditResponse
     * @throws AuthorizationFailedException - thrown if the user is not authorized to edit the question
     * @throws InvalidQuestionException - thrown if the question requested for the edit is not present in db
     */

    @RequestMapping(path = "/question/edit/{questionId}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PUT)
    public ResponseEntity<QuestionEditResponse> editQuestion(@RequestHeader("authorization") final String authHeader,
                                                             final QuestionEditRequest request, @PathVariable("questionId") final String questionUuid)
            throws AuthorizationFailedException, InvalidQuestionException {

        //Check if the provided accessToken is valid and present in the db
        //if accessToken is not valid throws AuthorizationFailedException
        UserAuthEntity userAuthTokenEntity = commonService.authorizeUser(authHeader);

        //Check if the question correspondent to the provided uuid present in db
        //If the question is not present throws InvalidQuestionException
        QuestionEntity questionEntity = questionService.getQuestionByUuid(questionUuid);

        //Get the user requesting for the edit
        UserEntity userToEdit = userAuthTokenEntity.getUserEntity();

        //Get the actual question owner
        UserEntity questionOwner = questionEntity.getUserEntity();

        //Checks if the user requesting the edit and the owner of the question are same
        //If not throws a AuthorizationFailedException
        questionService.authorizeEdit(userToEdit,questionOwner);

        //If user authorized edit the questionEntity with the provided new content
        questionEntity.setContent(request.getContent());

        //Persist the updated questionEntity
        QuestionEntity updatedQuestion = questionService.updateQuestion(questionEntity);

        //Create a QuestionEditResponse
        QuestionEditResponse response = new QuestionEditResponse();
        response.setId(updatedQuestion.getUuid());
        response.setStatus("QUESTION EDITED");

        return new ResponseEntity<QuestionEditResponse>(response,HttpStatus.OK);
    }



    /**
     * This method takes user uuid and authorization string as parameter,
     * validates the user and then fetch all the questions posted by a specific user.
     *
     * @param userUuid "Uuid of user who requested for all questions"
     * @param authorization Auth token of the user
     * @return List of QuestionDetailsResponse containing question uuid and content
     * @throws AuthorizationFailedException Throws the error code ATH-001 if username doesn't exist,
     *    ATH-002 in case of incorrect password
     * @throws UserNotFoundException Thrown with the message code -'USR-001' and
     * message -'User with entered uuid whose question details are to be seen does not exist'
     */
    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@PathVariable("userId") final String userUuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthEntity userAuthTokenEntity = commonService.authorizeUser(authorization);
        List<QuestionEntity> questionEntityList=questionService.getAllQuestionsByUser(userUuid);
        List<QuestionDetailsResponse> questionDetailsResponseList=new ArrayList<>();

        for(int i=0;i<questionEntityList.size();i++){
            QuestionDetailsResponse questionDetailsResponse=new QuestionDetailsResponse().id(questionEntityList.get(i).getUuid()).content(questionEntityList.get(i).getContent());
            questionDetailsResponseList.add(questionDetailsResponse);
        }

        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponseList, HttpStatus.OK);

    }

    /**
     * This method takes question uuid and authorization string as parameter,
     * validates the user & question id and then deletes the question that has been posted by a user.
     *
     * @param questionUuid "Uuid of question which need to be deleted"
     * @param authorization Auth Token of the user
     * @return QuestionDeleteResponse containing question uuid and status.
     * @throws UserNotFoundException with the message code -'USR-001' and
     * message -'User with entered uuid whose question details are to be seen does not exist'
     * @throws AuthorizationFailedException  Throws the error code ATH-001 if username doesn't exist,
     *    ATH-002 in case of incorrect password
     * @throws InvalidQuestionException with the message code-'QUES-001' and message-'Entered question uuid does not exist'.
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") final String questionUuid,
                                                                 @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuthTokenEntity = commonService.authorizeUser(authorization);
        QuestionEntity questionEntity=questionService.deleteQuestion(userAuthTokenEntity, questionUuid);
        QuestionDeleteResponse questionDeleteResponse=new QuestionDeleteResponse().id(questionEntity.getUuid()).status("Question Deleted");

        return  new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse,HttpStatus.OK);

    }


}

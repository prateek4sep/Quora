# Quora

REST API endpoints of various functionalities required for a website (similar to Quora). In order to observe the functionality of the endpoints, Swagger user interface has been used and PostgreSQL database has been used to store the data. Implemented using Java Persistence API (JPA).

## Contributors:
- Prateek Mehta
- Shubham Dhoot
- Bhushan Sharma
- Sai Darshan

## Pre-requisites:
Update the following files based on your database settings:
- Quora/quora-db/src/main/resources/config/localhost.properties
- Quora/quora-api/src/main/resources/application.yaml

## Description of Endpoints:
### signup - "/user/signup"
This endpoint is used to register a new user in the Quora Application.
- POST request
- This endpoint requests for all the attributes in 'SignupUserRequest' about the user.
- If the username provided already exists in the current database, ‘SignUpRestrictedException’ thrown with the message code -'SGR-001' and message - 'Try any other Username, this Username has already been taken'.
- If the email Id provided by the user already exists in the current database, ‘SignUpRestrictedException’ thrown with the message code -'SGR-002' and message -'This user has already been registered, try with any other emailId'.
- If the information is provided by a non-existing user, then user information is saved in the database and 'uuid' of the registered user is returned with message 'USER SUCCESSFULLY REGISTERED' in the JSON response with the corresponding HTTP status.
- Also, when a user signs up using this endpoint then the role of the person will be 'nonadmin' by default. You can add users with 'admin' role only by executing database queries or with pgAdmin.

### signin - "/user/signin"
This endpoint is used for user authentication. The user authenticates in the application and after successful authentication, JWT token is given to a user.
- POST request
- This endpoint requests for the User credentials to be passed in the authorization field of header as part of Basic authentication. You need to pass "Basic username:password" (where username:password of the String is encoded to Base64 format) in the authorization header.
- If the username provided by the user does not exist, "AuthenticationFailedException" thrown with the message code -'ATH-001' and message-'This username does not exist'.
- If the password provided by the user does not match the password in the existing database, 'AuthenticationFailedException' thrown with the message code -'ATH-002' and message -'Password failed'.
- If the credentials provided by the user match the details in the database, then user login information is saved in the database and 'uuid' of the authenticated user is returned from 'users' table with message 'SIGNED IN SUCCESSFULLY' in the JSON response with the corresponding HTTP status. 
- Also, in the access_token field of the Response Header access token is returned, which will be used by the user for any further operation in the Quora Application.

### signout - "/user/signout"
This endpoint is used to sign out from the Quora Application. The user cannot access any other endpoint once he is signed out of the application.
- POST request.
- This endpoint must request the access token of the signed in user in the authorization field of the Request Header.
- If the access token provided by the user does not exist in the database, 'SignOutRestrictedException' thrown with the message code -'SGR-001' and message - 'User is not Signed in'.
- If the access token provided by the user is valid, LogoutAt time of the user is updated in the database and 'uuid' of the signed out user is returned from 'users' table with message 'SIGNED OUT SUCCESSFULLY' in the JSON response with the corresponding HTTP status.

### userProfile - "/userprofile/{userId}"
This endpoint is used to get the details of any user in the Quora Application. This endpoint can be accessed by any user in the application.
- GET request
- This endpoint must request the path variable 'userId' as a string for the corresponding user profile to be retrieved and access token of the signed in user as a string in authorization Request Header.
- If the access token provided by the user does not exist in the database 'AuthorizationFailedException' thrown with the message code - 'ATHR-001' and message - 'User has not signed in'.
- If the user has signed out, "AuthorizationFailedException" thrown with the message code -'ATHR-002' and message -'User is signed out.Sign in first to get user details' .
- If the user with uuid whose profile is to be retrieved does not exist in the database, 'UserNotFoundException' thrown with the message code -'USR-001' and message -'User with entered uuid does not exist'.
- Else, all the details of the user from the database in the JSON response are returned with the corresponding HTTP status.

### userDelete - "/admin/user/{userId}"
This endpoint is used to delete a user from the Quora Application. Only an admin is authorized to access this endpoint.
- DELETE request.
- This endpoint requests the path variable 'userId' as a string for the corresponding user which is to be deleted from the database and access token of the signed in user as a string in authorization Request Header.
- If the access token provided by the user does not exist in the database 'AuthorizationFailedException' throw with the message code-'ATHR-001' and message -'User has not signed in'.
- If the user has signed out, 'AuthorizationFailedException' throw with the message code- 'ATHR-002' and message -'User is signed out'.
- If the role of the user is 'nonadmin',  'AuthorizationFailedException' thrown with the message code-'ATHR-003' with message -'Unauthorized Access, Entered user is not an admin'.
- If the user with uuid whose profile is to be deleted does not exist in the database, 'UserNotFoundException' thrown with the message code -'USR-001' with message -'User with entered uuid to be deleted does not exist'.
- Else, the records from all the tables related to that user are deleted and 'uuid' of the deleted user from 'users' table is returned with message 'USER SUCCESSFULLY DELETED' in the JSON response with the corresponding HTTP status.

### createQuestion - "/question/create"
This endpoint is used to create a question in the Quora Application which will be shown to all the users. Any user can access this endpoint.
- POST request.
- This endpoint requests for all the attributes in 'QuestionRequest' about the question and access token of the signed in user as a string in the authorization field of the Request Header.
- If the access token provided by the user does not exist in the database "AuthorizationFailedException" thrown with the message code - 'ATHR-001' and message - 'User has not signed in'.
- If the user has signed out, 'AuthorizationFailedException' thrown with the message code- 'ATHR-002' and message -'User is signed out.Sign in first to post a question'.
- Else, the question information is saved in the database and the 'uuid' of the question is returned with message 'QUESTION CREATED' in the JSON response with the corresponding HTTP status.

### getAllQuestions - "/question/all"
This endpoint is used to fetch all the questions that have been posted in the application by any user. Any user can access this endpoint.
- GET request.
- This endpoint requests for access token of the signed in user as a string in authorization Request Header.
- If the access token provided by the user does not exist in the database 'AuthorizationFailedException' thrown with the message code - 'ATHR-001' and message - 'User has not signed in'.
- If the user has signed out, 'AuthorizationFailedException' thrown with the message code-'ATHR-002' and message-'User is signed out.Sign in first to get all questions'.
- Else, 'uuid' and 'content' of all the questions from the database are returned in the JSON response with the corresponding HTTP status.

### editQuestionContent - "/question/edit/{questionId}"
This endpoint is used to edit a question that has been posted by a user. Note, only the owner of the question can edit the question.  
- PUT request.
- This endpoint requests for all the attributes in 'QuestionEditRequest', the path variable 'questionId' as a string for the corresponding question which is to be edited in the database and access token of the signed in user as a string in the authorization field of the Request Header.
- If the access token provided by the user does not exist in the database 'AuthorizationFailedException' thrown with the message code-'ATHR-001' and message-'User has not signed in'.
- If the user has signed out, 'AuthorizationFailedException' thrown with the message code-'ATHR-002' and message-'User is signed out.Sign in first to edit the question'.
- Only the question owner can edit the question. Therefore, if the user who is not the owner of the question tries to edit the question "AuthorizationFailedException" thrown with the message code-'ATHR-003' and message-'Only the question owner can edit the question'.
- If the question with uuid which is to be edited does not exist in the database, 'InvalidQuestionException' thrown with the message code - 'QUES-001' and message -'Entered question uuid does not exist'.
- Else, question in the database is edited and 'uuid' of the edited question is returned with message 'QUESTION EDITED' in the JSON response with the corresponding HTTP status.

### deleteQuestion - "/question/delete/{questionId}"
This endpoint is used to delete a question that has been posted by a user. Note, only the question owner of the question or admin can delete a question.
- DELETE request.
- This endpoint requests for the path variable 'questionId' as a string for the corresponding question which is to be deleted from the database and access token of the signed in user as a string in the authorization field of the Request Header.
- If the access token provided by the user does not exist in the database 'AuthorizationFailedException' thrown with the message code - 'ATHR-001' and message - 'User has not signed in'.
- If the user has signed out, 'AuthorizationFailedException' thrown with the message code- 'ATHR-002' and message -'User is signed out.Sign in first to delete a question'.
- Only the question owner or admin can delete the question. Therefore, if the user who is not the owner of the question or the role of the user is ‘nonadmin’ and tries to delete the question, 'AuthorizationFailedException' thrown with the message code-'ATHR-003' and message -'Only the question owner or admin can delete the question'.
- If the question with uuid which is to be deleted does not exist in the database, 'InvalidQuestionException' thrown with the message code-'QUES-001' and message-'Entered question uuid does not exist'.
- Else, the question from the database is deleted and return 'uuid' of the deleted question with message -'QUESTION DELETED' in the JSON response with the corresponding HTTP status.

### getAllQuestionsByUser - "question/all/{userId}"
This endpoint is used to fetch all the questions posed by a specific user. Any user can access this endpoint.
- GET request.
- This endpoint requests the path variable 'userId' as a string for the corresponding user whose questions are to be retrieved from the database and access token of the signed in user as a string in authorization Request Header.
- If the access token provided by the user does not exist in the database 'AuthorizationFailedException' thrown with the message code-'ATHR-001' and message -'User has not signed in'.
- If the user has signed out, 'AuthorizationFailedException' thrown with the message code-'ATHR-002' and message-'User is signed out.Sign in first to get all questions posted by a specific user'.
- If the user with uuid whose questions are to be retrieved from the database does not exist in the database, 'UserNotFoundException' thrown with the message code -'USR-001' and message -'User with entered uuid whose question details are to be seen does not exist'.
- Else, 'uuid' and 'content' of all the questions posed by the corresponding user returned from the database in the JSON response with the corresponding HTTP status.

### createAnswer - "/question/{questionId}/answer/create"
This endpoint is used to create an answer to a particular question. Any user can access this endpoint.
- POST request.
- This endpoint requests for the attribute in "Answer Request", the path variable 'questionId ' as a string for the corresponding question which is to be answered in the database and access token of the signed in user as a string in authorization Request Header.
- If the question uuid entered by the user whose answer is to be posted does not exist in the database, "InvalidQuestionException" thrown with the message code - 'QUES-001' and message - 'The question entered is invalid'.
- If the access token provided by the user does not exist in the database "AuthorizationFailedException" thrown with the message code - 'ATHR-001' and message - 'User has not signed in'.
- If the user has signed out, "AuthorizationFailedException" thrown with the message code - 'ATHR-002' and message - 'User is signed out.Sign in first to post an answer'.
- Else, the answer information is saved in the database and the "uuid" of the answer is returned with message "ANSWER CREATED" in the JSON response with the corresponding HTTP status.

### editAnswerContent - "/answer/edit/{answerId}"
This endpoint is used to edit an answer. Only the owner of the answer can edit the answer.  
- PUT request.
- This endpoint requests for all the attributes in "AnswerEditRequest", the path variable 'answerId' as a string for the corresponding answer which is to be edited in the database and access token of the signed in user as a string in authorization Request Header.
- If the access token provided by the user does not exist in the database "AuthorizationFailedException"  thrown with the message code - 'ATHR-001' and message - 'User has not signed in'.
- If the user has signed out, "AuthorizationFailedException" thrown with the message code - 'ATHR-002' and message 'User is signed out.Sign in first to edit an answer'.
- Only the answer owner can edit the answer. Therefore, if the user who is not the owner of the answer tries to edit the answer "AuthorizationFailedException" thrown with the message code - 'ATHR-003' and message - 'Only the answer owner can edit the answer'.
- If the answer with uuid which is to be edited does not exist in the database, "AnswerNotFoundException" thrown with the message code - 'ANS-001' and message - 'Entered answer uuid does not exist'.
- Else, the answer in the database is edited and "uuid" of the edited answer is returned and message "ANSWER EDITED" in the JSON response with the corresponding HTTP status.

### deleteAnswer - "/answer/delete/{answerId}"
This endpoint is used to delete an answer. Only the owner of the answer or admin can delete an answer.
- DELETE request.
- This endpoint requests for the path variable 'answerId' as a string for the corresponding answer which is to be deleted from the database and access token of the signed in user as a string in authorization Request Header.
- If the access token provided by the user does not exist in the database "AuthorizationFailedException" thrown with the message code - 'ATHR-001' and message - 'User has not signed in'.
- If the user has signed out, "AuthorizationFailedException" thrown with the message code - 'ATHR-002' and message - 'User is signed out.Sign in first to delete an answer'.
- Only the answer owner or admin can delete the answer. Therefore, if the user who is not the owner of the answer or the role of the user is ‘nonadmin’ and tries to delete the answer "AuthorizationFailedException" thrown with the message code - 'ATHR-003' and message - 'Only the answer owner or admin can delete the answer'.
- If the answer with uuid which is to be deleted does not exist in the database, "AnswerNotFoundException" thrown with the message code - 'ANS-001' and message - 'Entered answer uuid does not exist'.
- Else, the answer from the database is deleted and "uuid" of the deleted answer is returned with message "ANSWER DELETED" in the JSON response with the corresponding HTTP status.


### getAllAnswersToQuestion - "answer/all/{questionId}"
This endpoint is used to get all answers to a particular question. Any user can access this endpoint.
- GET request.
- This endpoint requests the path variable 'questionId' as a string for the corresponding question whose answers are to be retrieved from the database and access token of the signed in user as a string in authorization Request Header.
- If the access token provided by the user does not exist in the database "AuthorizationFailedException" thrown with the message code - 'ATHR-001' and message - 'User has not signed in'.
- If the user has signed out, "AuthorizationFailedException" thrown with the message code - 'ATHR-002' and message - 'User is signed out.Sign in first to get the answers'.
- If the question with uuid whose answers are to be retrieved from the database does not exist in the database, "InvalidQuestionException" thrown with the message code - 'QUES-001' and message - 'The question with entered uuid whose details are to be seen does not exist'.
- Else, "uuid" of the answer, "content" of the question and "content" of all the answers posted for that particular question are returned from the database in the JSON response with the corresponding HTTP status.

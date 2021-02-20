package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserAuthenticationService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired private UserAuthenticationService userAuthService;

    /**
     * Method for signing up a new user.
     * An object of 'SignupUserRequest' is received as an argument with corresponding fields.
     *
     * @return SignupUserResponse - UUID of the new user created.
     * @throws SignUpRestrictedException - Thrown if the username of email already exists in the DB.
     */
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/user/signup",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(SignupUserRequest signupUserRequest)
            throws SignUpRestrictedException {

        UserEntity userEntity = createNewUserEntity(signupUserRequest);
        UserEntity createdUserEntity = userAuthService.signup(userEntity);
        SignupUserResponse userResponse =
                new SignupUserResponse()
                        .id(createdUserEntity.getUuid())
                        .status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
    }

    /**
     * This method takes a authorization string as parameter, decodes and generates an access token.
     *
     * @param authorization "Basic <Base 64 Encoded username:password>"
     * @return SigninResponse containing user id and a access-token.
     * @throws AuthenticationFailedException Throws the error code ATH-001 if username doesn't exist,
     * ATH-002 in case of incorrect password
     */
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/user/signin",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(
            @RequestHeader("authorization") final String authorization)
            throws AuthenticationFailedException {

        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        UserAuthEntity userAuthEntity = userAuthService.signin(decodedArray[0], decodedArray[1]);

        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthEntity.getAccessToken());

        SigninResponse signinResponse = new SigninResponse();
        signinResponse.setId(userAuthEntity.getUserEntity().getUuid());
        signinResponse.setMessage("SIGNED IN SUCCESSFULLY");

        return new ResponseEntity<SigninResponse>(signinResponse, headers, HttpStatus.OK);
    }

    /**
     * This method takes an accessToken to validate and sign the user out.
     *
     * @param accessToken Token used for authenticating the user.
     * @return UUID of the user who is signed out.
     * @throws SignOutRestrictedException if the accessToken is invalid.
     */
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/user/signout",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signout(
            @RequestHeader("authorization") final String accessToken) throws SignOutRestrictedException {
        UserEntity userEntity = userAuthService.signout(accessToken);
        SignoutResponse signoutResponse =
                new SignoutResponse().id(userEntity.getUuid()).message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
    }

    /**
     * This method take the Sign up user request and creates a user entity.
     *
     * @param signupUserRequest
     * @return
     */
    public UserEntity createNewUserEntity(SignupUserRequest signupUserRequest){
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setRole("nonadmin");
        userEntity.setContactNumber(signupUserRequest.getContactNumber());
        return userEntity;
    }
}
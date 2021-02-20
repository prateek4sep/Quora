package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.CommonService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired private CommonService commonService;

    /**
     * Method to validate querying user using auth token.
     * Returns the desired user profile response after validation.
     *
     * @param uuid
     * @param authToken
     * @return User profile of the queried user
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/userprofile/{userId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUserProfile(
            @PathVariable("userId") final String uuid,
            @RequestHeader("authorization") final String authToken)
            throws AuthorizationFailedException, UserNotFoundException {

        // Authorizing the requesting user via authToken.
        UserAuthEntity userAuthEntity = commonService.authorizeUser(authToken);

        // Get Requested user details after authorization.
        UserEntity existingUser = commonService.getUserByUuid(uuid);

        UserDetailsResponse userDetailsResponse = getUserDetailsResponse(existingUser);
        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }

    /**
     * Creating new UserDetailsResponse with requested user profile details in response.
     *
     * @param existingUser user entity of the queried user.
     * @return UserDetailsResponse with user details of the queried user.
     */
    public UserDetailsResponse getUserDetailsResponse(UserEntity existingUser){
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
        userDetailsResponse
                .firstName(existingUser.getFirstName())
                .lastName(existingUser.getLastName())
                .userName(existingUser.getUserName())
                .emailAddress(existingUser.getEmail())
                .country(existingUser.getCountry())
                .aboutMe(existingUser.getAboutMe())
                .dob(existingUser.getDob())
                .contactNumber(existingUser.getContactNumber());
        return userDetailsResponse;
    }
}
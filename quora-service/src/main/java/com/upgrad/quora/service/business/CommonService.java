package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class CommonService {

    @Autowired private UserDao userDao;

    @Autowired private UserAuthDao userAuthDao;

    /**
     * Fetch the UserEntity based on passed userId.
     *
     * @param uuid
     * @return UserEntity
     * @throws UserNotFoundException If a null object is returned.
     */
    public UserEntity getUserByUuid(final String uuid) throws UserNotFoundException {
        UserEntity userEntity = userDao.getUserById(uuid);
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        } else {
            return userEntity;
        }
    }

    /**
     * Authentication for a user trying to access details.
     * This method also validates whether the user requesting details is signed in or not.
     * Throws exception if so, or returns the auth.
     *
     * @param authToken of the querying user.
     * @return UserAuthEntity
     * @throws AuthorizationFailedException
     */
    public UserAuthEntity authorizeUser(final String authToken)
            throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(authToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else {
            ZonedDateTime logoutAt = userAuthEntity.getLogoutAt();
            if (logoutAt != null) {
                throw new AuthorizationFailedException(
                        "ATHR-002", "User is signed out.Sign in first to get user details");
            } else {
                return userAuthEntity;
            }
        }
    }
}
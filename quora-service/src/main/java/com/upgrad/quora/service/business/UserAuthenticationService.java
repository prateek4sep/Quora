package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class UserAuthenticationService {

    @Autowired private UserDao userDao;

    @Autowired private UserAuthDao userAuthDao;

    @Autowired private PasswordCryptographyProvider passwordCryptographyProvider;
    /**
     * This service method assigns a UUID, sets an encrypted password and salt for the user signing up.
     * This method handle also exceptions in case of a duplicate username or if the user exists in the DB.
     *
     * @throws SignUpRestrictedException : Exception thrown if user/email already exists in the DB.
     * @return UserEntity with user details
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {
        if (userNameExists(userEntity.getUserName())) {
            throw new SignUpRestrictedException(
                    "SGR-001", "Try any other Username, this Username has already been taken");
        }

        if (emailExists(userEntity.getEmail())) {
            throw new SignUpRestrictedException(
                    "SGR-002", "This user has already been registered, try with any other emailId");
        }
        userEntity.setUuid(UUID.randomUUID().toString());

        //Encrypt the password and set salt
        String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        return userDao.createUser(userEntity);
    }
    /**
     * Sign in method takes username and password as argument, create and sets an auth token and opens a session.
     *
     * @param username : Username of the user
     * @param password : Password of the user
     * @throws AuthenticationFailedException : If the user is not found or password is invalid
     * @return UserAuthEntity access token and response.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity signin(final String username, final String password)
            throws AuthenticationFailedException {

        UserEntity userEntity = userDao.getUserByUserName(username);
        if (userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }
        final String encryptedPassword = passwordCryptographyProvider.encrypt(password, userEntity.getSalt());
        if (!encryptedPassword.equals(userEntity.getPassword())) {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }

        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
        UserAuthEntity userAuthEntity = new UserAuthEntity();
        userAuthEntity.setUuid(UUID.randomUUID().toString());
        userAuthEntity.setUserEntity(userEntity);

        //Generate a Aut Token with Expiration
        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime expiresAt = now.plusHours(8);
        userAuthEntity.setAccessToken(
                jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
        userAuthEntity.setLoginAt(now);
        userAuthEntity.setExpiresAt(expiresAt);

        userAuthDao.createAuthToken(userAuthEntity);
        userDao.updateUserEntity(userEntity);

        return userAuthEntity;
    }

    /**
     * This method takes the access Token for validation and signs the user out.
     *
     * @param accessToken : required for validation and sign out
     * @throws SignOutRestrictedException : Thrown if the access-token is not found in the DB.
     * @return UserEntity : Signed out user.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signout(final String accessToken) throws SignOutRestrictedException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
        if (userAuthEntity == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }
        userAuthEntity.setLogoutAt(ZonedDateTime.now());
        userAuthDao.updateUserAuth(userAuthEntity);
        return userAuthEntity.getUserEntity();
    }

    /**
     * Check if the Username already exists in the DB.
     * @param userName
     * @return true/false
     */
    private boolean userNameExists(final String userName) {
        return userDao.getUserByUserName(userName) != null;
    }

    /**
     * Checks if the email already exists in the DB.
     * @param email
     * @return true/false
     */
    private boolean emailExists(final String email) {
        return userDao.getUserByEmail(email) != null;
    }
}
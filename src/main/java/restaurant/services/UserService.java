package restaurant.services;

import restaurant.dto.request.SignInRequest;
import restaurant.dto.request.SignUpRequest;
import restaurant.dto.response.PaginationUser;
import restaurant.dto.response.SignResponse;
import restaurant.dto.response.SimpleResponse;

import java.security.Principal;

public interface UserService {
    SignResponse signIn(SignInRequest signInRequest);

    SimpleResponse signUp(Long resId, SignUpRequest signUpRequest);

    SignResponse saveUser(Long resId, SignUpRequest signUpRequest, Principal principal);

    PaginationUser findALlUsers(int page, int size, Principal principal);

    PaginationUser findALlApps(Long restId,int page, int size, Principal principal);
}

package restaurant.services;

import restaurant.dto.request.SignInRequest;
import restaurant.dto.request.SignUpRequest;
import restaurant.dto.request.UpdateRequest;
import restaurant.dto.response.*;

import java.security.Principal;

public interface UserService {
    SignResponse signIn(SignInRequest signInRequest);

    SimpleResponse signUp(Long resId, SignUpRequest signUpRequest);

    SignResponse saveUser(SignUpRequest signUpRequest, Principal principal);

    PaginationUser findALlUsers(int page, int size, Principal principal);

    PaginationUser findALlApps(int page, int size, Principal principal);

    HttpResponseForUser update(Principal principal, UpdateRequest updateRequest);

    HttpResponseForUser updateEmployees(Long userId, SignUpRequest signUpRequest, Principal principal);

    AllUsersResponse findEmployee(Long userId, Principal principal);

    SimpleResponse deleteEmployee(Long userId, Principal principal);
}

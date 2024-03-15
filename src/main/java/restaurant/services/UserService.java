package restaurant.services;

import restaurant.dto.request.SignInRequest;
import restaurant.dto.request.SignUpRequest;
import restaurant.dto.response.SignResponse;
import restaurant.dto.response.SimpleResponse;

public interface UserService {
    SignResponse signIn(SignInRequest signInRequest);

    SimpleResponse signUp(SignUpRequest signUpRequest);

    SignResponse saveUser(Long resId, SignUpRequest signUpRequest);
}

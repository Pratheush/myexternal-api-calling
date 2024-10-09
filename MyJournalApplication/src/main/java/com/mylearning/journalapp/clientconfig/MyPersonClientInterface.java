package com.mylearning.journalapp.clientconfig;

import com.mylearning.journalapp.clientresponse.JWTAuthResponse;
import com.mylearning.journalapp.clientresponse.LoginDto;

public interface MyPersonClientInterface {

    JWTAuthResponse login(LoginDto loginDto);
    String getJwtAccessToken();

}
